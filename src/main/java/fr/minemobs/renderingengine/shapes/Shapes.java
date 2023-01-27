package fr.minemobs.renderingengine.shapes;

import java.util.function.Function;

import org.w3c.dom.Element;

import fr.minemobs.renderingengine.Parser;

public enum Shapes {
    CUBE("cube", Parser::toCube),
    SQUARE("square", Parser::toSquare),
    TETRAHEDRON("tetrahedron", Parser::toTetrahedron),
    TRIANGLE("triangle", Parser::toTriangle);

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
