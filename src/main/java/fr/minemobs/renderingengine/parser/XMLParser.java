package fr.minemobs.renderingengine.parser;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.minemobs.renderingengine.Main;
import fr.minemobs.renderingengine.Vertex;
import fr.minemobs.renderingengine.shapes.Cube;
import fr.minemobs.renderingengine.shapes.Shape;
import fr.minemobs.renderingengine.shapes.Shapes;
import fr.minemobs.renderingengine.shapes.Square;
import fr.minemobs.renderingengine.shapes.Tetrahedron;
import fr.minemobs.renderingengine.shapes.Triangle;
import fr.minemobs.renderingengine.utils.ThrowableUtils;
import fr.minemobs.renderingengine.utils.InvalidXMLException;

public class XMLParser {
    
    public static Cube toCube(Element node) {
        try {
            return new Cube(
                getVertex(node, "beginning"),
                getVertex(node, "end"),
                getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
        } catch (InvalidXMLException e) {
            Main.LOGGER.severe(() -> ThrowableUtils.toString(e));
            return null;
        }
    }

    public static Square toSquare(Element node) {
        try {
            return new Square(
                getVertex(node, "beginning"),
                getVertex(node, "end"),
                getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
        } catch (InvalidXMLException e) {
            Main.LOGGER.severe(() -> ThrowableUtils.toString(e));
            return null;
        }
    }
    
    public static Triangle toTriangle(Element node) {
        try {
            return new Triangle(
                getVertex(node, "v1"),
                getVertex(node, "v2"),
                getVertex(node, "v3"),
                getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
        } catch (InvalidXMLException e) {
            Main.LOGGER.severe(() -> ThrowableUtils.toString(e));
            return null;
        }
    }

    public static Shape toTetrahedron(Element node) {
        try {
            return new Tetrahedron(
                getVertex(node, "v1"),
                getVertex(node, "v2"),
                getVertex(node, "v3"),
                getVertex(node, "v4"),
                getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
        } catch (InvalidXMLException e) {
            Main.LOGGER.severe(() -> ThrowableUtils.toString(e));
            return null;
        }
    }

    private static Vertex getVertex(Element node, String tag) throws InvalidXMLException {
        return getVertex(Optional.ofNullable(node.getElementsByTagName(tag).item(0)).map(Element.class::cast)
            .orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the " + tag + " node.")));
    }

    private static Vertex getVertex(Element node) throws InvalidXMLException {
        return new Vertex(getChildElementContent(node, "x"), getChildElementContent(node, "y"), getChildElementContent(node, "z"));
    }

    private static Color getColor(Element e, Color defaultColor) throws InvalidXMLException {
        int r = getChildElementContent(e, "r", 255);
        int g = getChildElementContent(e, "g", 255);
        int b = getChildElementContent(e, "b", 255);
        if(r > 255 || r < 0) throw new InvalidXMLException("Red value in " + e.getNodeName() + " is higher than 255 or lower than 0");
        if(g > 255 || g < 0) throw new InvalidXMLException("Green value in " + e.getNodeName() + " is higher than 255 or lower than 0");
        if(b > 255 || b < 0) throw new InvalidXMLException("Blue value in " + e.getNodeName() + " is higher than 255 or lower than 0");
        return new Color(r, g, b);
    }

    private static int getChildElementContent(Element e, String childName, int defaultValue) {
        if(e == null) return defaultValue;
        NodeList children = e.getElementsByTagName(childName);
        if(children == null || children.getLength() == 0) return 255;
        return parseInt(children.item(0).getTextContent()).map(Integer::intValue).orElse(defaultValue);
    }
    
    private static int getChildElementContent(Element e, String childName) throws InvalidXMLException {
        NodeList children = e.getElementsByTagName(childName);
        if(children == null) throw new InvalidXMLException("The shape.xml file is missing the " + childName + " node.");
        return parseInt(children.item(0).getTextContent()).map(Integer::intValue)
            .orElseThrow(() -> new InvalidXMLException("\"" + childName + "\" in element " + e.getNodeName() + " is not a valid integer"));
    }

    private static Optional<Integer> parseInt(String integer) {
        try {
            return Optional.of(Integer.parseInt(integer));
        } catch(NumberFormatException ignored) {
            return Optional.empty();
        }
    }

    public static List<Triangle> parseXMLFile(Path path) {
        try(InputStream is = Files.newInputStream(path)) {
            List<Shape> shapes = new LinkedList<>();
            var doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            doc.getDocumentElement().normalize();
            addShapes(shapes, doc);
            return shapes.stream().filter(Objects::nonNull).map(Shape::getTriangles).flatMap(Arrays::stream).toList();
        } catch (IOException | ParserConfigurationException | InvalidXMLException | SAXException e) {
            Main.LOGGER.info(() -> ThrowableUtils.toString(e));
            return Collections.emptyList();
        }
    }

    private static void addShapes(List<Shape> shapes, Document doc) throws InvalidXMLException {
        for(Shapes shape : Shapes.values()) {
            NodeList nodes = doc.getElementsByTagName(shape.getTag());
            for(int i = 0; i < nodes.getLength(); i++) shapes.add(shape.getFunction().apply((Element) nodes.item(i)));
        }
    }
}