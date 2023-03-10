package fr.minemobs.renderingengine.shapes;

import java.awt.Color;
import java.util.Arrays;

import fr.minemobs.renderingengine.Vertex;

public class Cube implements Shape {
    public final Square[] squares;

    public Cube(Vertex a, Vertex b, Color color) {
        squares = new Square[] {
            new Square(new Vertex(a.x, a.y, a.z), new Vertex(b.x, a.y, b.z), color),
            new Square(new Vertex(a.x, b.y, a.z), new Vertex(b.x, b.y, b.z), color),
            new Square(new Vertex(b.x, a.y, a.z), new Vertex(b.x, b.y, b.z), color),
            new Square(new Vertex(b.x, a.y, a.z), new Vertex(a.x, b.y, a.z), color),
            new Square(new Vertex(a.x, a.y, b.z), new Vertex(b.x, b.y, b.z), color),
            new Square(new Vertex(a.x, a.y, a.z), new Vertex(a.x, b.y, b.z), color),
        };   
    }
    
    public Square[] getSquares() {
        return squares;
    }

    @Override
    public Triangle[] getTriangles() {
        return Arrays.stream(squares).flatMap(t -> Arrays.stream(t.getTriangles())).toArray(Triangle[]::new);
    }
}
