package fr.minemobs.renderingengine;

import java.awt.Color;

public class Square {
    
    public final Triangle[] triangles;

    public Square(Vertex v1, Vertex v2, Color color) {
        this(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z, color);
    }

    public Square(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        Vertex v1 = new Vertex(x1, y1, z1);
        Vertex v2 = new Vertex(x2, y2, z2);
        triangles = new Triangle[] {
            new Triangle(v2, new Vertex(x1, y2, z1), new Vertex(x2, y1, z1), color),
            new Triangle(v1, new Vertex(x2, y1, z2), new Vertex(x1, y2, z2), color),
        };
    }

    public Triangle[] getTriangles() {
        return triangles;
    }
}