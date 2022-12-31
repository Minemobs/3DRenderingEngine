import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static class Matrix {
        
    }

    public static void main(String[] args) {
        final int width = 400, height = 400;
        final JFrame frame = new JFrame("Funny cube");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        // panel to display render results
        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                double heading = Math.toRadians(headingSlider.getValue());
                Matrix3 headingTransform = new Matrix3(new double[] {
                    //XZ rotation
                    Math.cos(heading), 0, -Math.sin(heading),
                    0, 1, 0,
                    Math.sin(heading), 0, Math.cos(heading)
                });
                double pitch = Math.toRadians(pitchSlider.getValue());
                Matrix3 pitchTransform = new Matrix3(new double[] {
                    //YZ Rotation
                    1, 0, 0,
                    0, Math.cos(pitch), Math.sin(pitch),
                    0, -Math.sin(pitch), Math.cos(pitch)
                });
                Matrix3 transform = headingTransform.multiply(pitchTransform);
                //g2.translate(getWidth() / 2, getHeight() / 2);
                
                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();

                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                Arrays.fill(zBuffer, Double.NEGATIVE_INFINITY);

                for (var t : getTriangles()) {                    
                    Vertex v1 = transform.transform(t.v1);
                    v1.x += getWidth() / 2;
                    v1.y += getHeight() / 2;
                    Vertex v2 = transform.transform(t.v2);
                    v2.x += getWidth() / 2;
                    v2.y += getHeight() / 2;
                    Vertex v3 = transform.transform(t.v3);
                    v3.x += getWidth() / 2;
                    v3.y += getHeight() / 2;
                
                    //Shadows
                    Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                    Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                    Vertex norm = new Vertex(
                         ab.y * ac.z - ab.z * ac.y,
                         ab.z * ac.x - ab.x * ac.z,
                         ab.x * ac.y - ab.y * ac.x
                    );
                    double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                    norm.x /= normalLength;
                    norm.y /= normalLength;
                    norm.z /= normalLength;
                    double angleCos = Math.abs(norm.z);

                    //Drawing stuff
                    int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                    int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                    int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                    int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                    double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);
                    for(int y = minY; y <= maxY; y++) {
                        for (int x = minX; x < maxX; x++) {
                            double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                            double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                            double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                            if(b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                //Avoiding z fighting
                                double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                int zIndex = y * img.getWidth() + x;
                                if(zBuffer[zIndex] < depth) {
                                    img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
                                    zBuffer[zIndex] = depth;
                                }
                            }
                        }
                    }
                }
                g2.drawImage(img, 0, 0, null);
            }
        };
        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setVisible(true);
        var t = new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path path = Path.of(".");
                path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    final WatchKey wk = watchService.take();
                    for (WatchEvent<?> event : wk.pollEvents()) {
                        final Path changed = (Path) event.context();
                        if (changed.endsWith("shape.xml")) {
                            System.out.println("Reloading");
                            renderPanel.repaint();
                        }
                    }
                    wk.reset();
                }
            } catch(IOException | InterruptedException ignored) {}
        });
        t.setDaemon(true);
        t.run();
    }

    private static BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
    
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
    
        return dimg;
    }

    public static Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;
    
        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);
    
        return new Color(red, green, blue);
    }
    
    private static Triangle[] getTriangles() {
        List<Triangle> triangles = new ArrayList<>();
        List<Square> squares = parseXMLFile();
        for (Square square : squares) {Collections.addAll(triangles, square.triangles);}
        return triangles.toArray(Triangle[]::new);
    }

    private static Triangle[] parseObjFile() {
        Path path = Path.of("model.obj");
        try {
            List<Triangle> triangles = new ArrayList<>();
            List<Vertex> vertices = new ArrayList<>();
            /*List<List<String>> listOfLines = new ArrayList<>(prepareChunks(Files.readAllLines(path).stream().filter(l -> l.startsWith("v ")).toList(), 2));
            int i = 0;
            for(List<String> lines : listOfLines) {
                List<Vertex> verticies = new ArrayList<>();
                for(String line : lines) {
                    i++;
                    var vertexs = line.split("\\s+");
                    verticies.add(new Vertex(Double.parseDouble(vertexs[1]), Double.parseDouble(vertexs[2]), Double.parseDouble(vertexs[3])));
                }
                try {
                    triangles.addAll(List.of(new Square(verticies.get(0), verticies.get(1), Color.BLUE).triangles));
                } catch(IndexOutOfBoundsException e) {
                    System.err.println("An IndexOutOfBoundsException occured at line : " + i + ". With array " + verticies.stream()
                    .map(v -> String.format("X: %f" + " Y: %f" + " Z: %f", v.x, v.y, v.z)).collect(Collectors.joining("\n")));
                }
            }*/
            List<String> lines = Files.readAllLines(path).stream().filter(l -> l.startsWith("v ") || l.startsWith("f")).toList();
            for(String line : lines) {
                var vertexs = line.split("\\s+");
                if(line.startsWith("v ")) {
                    vertices.add(new Vertex(Double.parseDouble(vertexs[1]) * 50, Double.parseDouble(vertexs[2]) * 50, Double.parseDouble(vertexs[3]) * 50));
                } else {
                    int[] v = Stream.of(vertexs).skip(1).mapToInt(s -> Integer.parseInt(s.split("/")[0])).toArray();
                    var t = new Triangle(vertices.get(v[0] - 1), vertices.get(v[1] - 1), vertices.get(v[2] - 1));
                    triangles.add(t);
                }
            }
            System.out.println(Arrays.deepToString(triangles.toArray(Triangle[]::new)));
            return triangles.toArray(Triangle[]::new);
        } catch(IOException e) {
            e.printStackTrace();
            return new Triangle[0];
        }
    }

    private static <T> Collection<List<T>> prepareChunks(List<T> inputList, int chunkSize) {
        AtomicInteger counter = new AtomicInteger();
        return inputList.stream().collect(Collectors.groupingBy(it -> counter.getAndIncrement() / chunkSize)).values();
    }

    private static List<Square> parseXMLFile() {
        Path path = Path.of("shape.xml");
        try(InputStream is = Files.newInputStream(path)) {
            List<Cube> cubes = new LinkedList<>();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            var doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            NodeList root = doc.getElementsByTagName("row");
            for(int i = 0; i < root.getLength(); i++) {
                Element node = (Element) root.item(i);
                cubes.add(Parser.toCube(node));
            }
            return cubes.stream().flatMap(cube -> Arrays.stream(cube.squares)).toList();
        } catch (IOException | ParserConfigurationException | InvalidXMLException | SAXException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<Square> getStairs() {
        return null;
    }

    private static List<Square> getAnvil() {
        List<Square> squares = new ArrayList<>();
        Collections.addAll(squares, new Cube(new Vertex(-100, 100, -100), new Vertex(100, 50, 100), Color.GRAY).squares);
        Collections.addAll(squares, new Cube(new Vertex(-75, 50, -50), new Vertex(75, 30, 50), Color.GRAY).squares);
        Collections.addAll(squares, new Cube(new Vertex(-60, 30, -40), new Vertex(60, -25, 40), Color.GRAY).squares);
        Collections.addAll(squares, new Cube(new Vertex(-75, -25, -125), new Vertex(75, -100, 125), Color.GRAY).squares);
        return squares;
    }

    private static List<Square> oldSquares() {
        List<Square> squares = new ArrayList<>();
        Collections.addAll(squares,new Square(new Vertex(-100, 100, -100), new Vertex(100, 100, 100), Color.RED));
        Collections.addAll(squares, new Square(new Vertex(-100, -100, -100), new Vertex(100, -100, 100), Color.RED));
        Collections.addAll(squares, new Square(new Vertex(100, -100, -100), new Vertex(100, 100, 100), Color.RED));
        Collections.addAll(squares, new Square(new Vertex(100, -100, -100), new Vertex(-100, 100, -100), Color.RED));
        Collections.addAll(squares, new Square(new Vertex(-100, -100, 100), new Vertex(100, 100, 100), Color.RED));
        Collections.addAll(squares, new Square(new Vertex(100, -100, -100), new Vertex(100, 100, 100), Color.RED));
        return squares;
    }

    private static Triangle[] oldTriangles() {
        return new Triangle[]{
            new Triangle(
                new Vertex(-100, 100, 100),
                new Vertex(-100, 100, -100),
                new Vertex(100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(100, 100, -100),
                new Vertex(-100, 100, -100),
                new Vertex(100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, 100, -100),
                new Vertex(-100, -100, -100),
                new Vertex(100, -100, -100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, 100, -100),
                new Vertex(100, 100, -100),
                new Vertex(100, -100, -100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(100, -100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, 100, 100),
                new Vertex(-100, 100, -100),
                new Vertex(100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(100, -100, 100),
                new Vertex(100, 100, 100),
                new Vertex(-100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, 100, -100),
                new Vertex(-100, -100, -100),
                new Vertex(-100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, -100, 100),
                new Vertex(-100, -100, -100),
                new Vertex(-100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(100, -100, 100),
                new Vertex(100, 100, 100),
                new Vertex(100, -100, -100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(100, 100, 100),
                Color.decode("#A52A2A")
            ),
            new Triangle(
                new Vertex(-100, -100, 100),
                new Vertex(-100, -100, -100),
                new Vertex(100, -100, -100),
                Color.GREEN
            ),
            new Triangle(
                new Vertex(100, -100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(100, -100, -100),
                Color.GREEN
            )
        };
    }

}