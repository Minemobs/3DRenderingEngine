package fr.minemobs.renderingengine.shapes;

import java.util.function.Function;

import org.w3c.dom.Element;

import fr.minemobs.renderingengine.parser.XMLParser;

public enum Shapes {
    CUBE("cube", XMLParser::toCube),
    SQUARE("square", XMLParser::toSquare),
    TETRAHEDRON("tetrahedron", XMLParser::toTetrahedron),
    TRIANGLE("triangle", XMLParser::toTriangle);

    private final String tag;
    private final Function<Element, Shape> function;

    Shapes(String id, Function<Element, Shape> function) {
        this.tag = id;
        this.function = function;
    }

    public String getTag() {
        return tag;
    }

    public Function<Element, Shape> getFunction() {
        return function;
    }
}
