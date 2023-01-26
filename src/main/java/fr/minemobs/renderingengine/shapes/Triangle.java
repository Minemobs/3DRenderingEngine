package fr.minemobs.renderingengine.shapes;

import java.awt.Color;

import fr.minemobs.renderingengine.Vertex;

public class Triangle implements Shape {
    public final Vertex v1, v2, v3;
    public final Color color;

    public Triangle(Vertex v1, Vertex v2, Vertex v3, Color color) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    public Triangle(Vertex v1, Vertex v2, Vertex v3) {
        this(v1, v2, v3, Color.WHITE);
    }

    @Override
    public Triangle[] getTriangles() {
        return new Triangle[] { this };
    }

    @Override
    public String toString() {
        return "V1: " + v1.toString() + " | V2: " + v2.toString() + " | V3: " + v3.toString();
    }
}
