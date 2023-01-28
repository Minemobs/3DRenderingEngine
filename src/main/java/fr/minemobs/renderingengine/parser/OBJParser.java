package fr.minemobs.renderingengine.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.minemobs.renderingengine.Main;
import fr.minemobs.renderingengine.Vertex;
import fr.minemobs.renderingengine.shapes.Triangle;
import fr.minemobs.renderingengine.utils.ThrowableUtils;

public class OBJParser {
    private OBJParser() {}

    public static List<Triangle> parseOBJFile(Path path) {
        List<String> content = Collections.emptyList();
        try(var br = Files.newBufferedReader(path)) {
            content = br.lines().toList();
        } catch(IOException e) {
            Main.LOGGER.severe(() -> ThrowableUtils.toString(e));
            return Collections.emptyList();
        }
        List<Vertex> vertices = getVertices(content);
        return getTriangles(vertices, content);
    }

    private static List<Vertex> getVertices(List<String> content) {
        List<Vertex> vertices = new LinkedList<>();
        content.stream().filter(l -> l.startsWith("v ")).forEach(line -> {
            String[] coords = line.split(" ");
            vertices.add(new Vertex(Float.parseFloat(coords[1]), Float.parseFloat(coords[2]), Float.parseFloat(coords[3])));
        });
        return vertices;
    }

    private static List<Triangle> getTriangles(List<Vertex> vertices, List<String> content) {
        List<Triangle> triangles = new LinkedList<>();
        content.stream().filter(l -> l.startsWith("f ")).forEach(line -> {
            String[] split = line.split(" ");
            triangles.add(new Triangle(
                vertices.get(Integer.parseInt(split[1].split("/")[0]) - 1),
                vertices.get(Integer.parseInt(split[2].split("/")[0]) - 1),
                vertices.get(Integer.parseInt(split[3].split("/")[0]) - 1)
                ));
        });
        return triangles.stream().map(t -> t.multiply(70)).toList();
    }
}
