package fr.minemobs.renderingengine;

import java.awt.Color;
import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.minemobs.renderingengine.shapes.Cube;
import fr.minemobs.renderingengine.shapes.Square;
import fr.minemobs.renderingengine.shapes.Triangle;

public class Parser {

    public static Cube toCube(Element node) throws InvalidXMLException {
        return new Cube(
            getVertex(Optional.ofNullable(node.getElementsByTagName("beginning").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the beginning node."))),
            getVertex(Optional.ofNullable(node.getElementsByTagName("end").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the end node."))),
            getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
    }

    public static Square toSquare(Element node) throws InvalidXMLException {
        return new Square(
            getVertex(Optional.ofNullable(node.getElementsByTagName("beginning").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the beginning node."))),
            getVertex(Optional.ofNullable(node.getElementsByTagName("end").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the end node."))),
            getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
    }
    
    public static Triangle toTriangle(Element node) throws InvalidXMLException {
        return new Triangle(
            getVertex(Optional.ofNullable(node.getElementsByTagName("v1").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the first vertex node."))),
            getVertex(Optional.ofNullable(node.getElementsByTagName("v2").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the 2nd vertex node."))),
            getVertex(Optional.ofNullable(node.getElementsByTagName("v3").item(0)).map(Element.class::cast).orElseThrow(() -> new InvalidXMLException("The shape.xml file is missing the 3rd vertex node."))),
            getColor((Element) node.getElementsByTagName("color").item(0), Color.WHITE));
    }

    private static Vertex getVertex(Element node) throws InvalidXMLException {
        return new Vertex(getChildElementContent(node, "x"), getChildElementContent(node, "y"), getChildElementContent(node, "z"));
    }

    private static Color getColor(Element e, Color defaultColor) throws InvalidXMLException {
        int r = getChildElementContent(e, "r");
        int g = getChildElementContent(e, "g");
        int b = getChildElementContent(e, "b");
        if(r > 255 || r < 0) throw new InvalidXMLException("Red value in " + e.getNodeName() + " is higher than 255 or lower than 0");
        if(g > 255 || g < 0) throw new InvalidXMLException("Green value in " + e.getNodeName() + " is higher than 255 or lower than 0");
        if(b > 255 || b < 0) throw new InvalidXMLException("Blue value in " + e.getNodeName() + " is higher than 255 or lower than 0");
        return new Color(r, g, b, 255);
    }
    
    private static int getChildElementContent(Element e, String childName) throws InvalidXMLException {
        NodeList children = e.getElementsByTagName(childName);
        if(children.getLength() == 0) throw new InvalidXMLException(childName + " in element " + e.getNodeName() + " is not a valid integer");
        return parseInt(children.item(0).getTextContent()).map(i -> i.intValue()).orElseThrow(() -> new InvalidXMLException(childName + " in element " + e.getNodeName() + " is not a valid integer"));
    }

    private static Optional<Integer> parseInt(String integer) {
        try {
            return Optional.of(Integer.parseInt(integer));
        } catch(NumberFormatException ignored) {
            return Optional.empty();
        }
    }
}