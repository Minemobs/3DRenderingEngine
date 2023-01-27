package fr.minemobs.renderingengine.shapes;

import fr.minemobs.renderingengine.Vertex;
import java.awt.Color;

public class Tetrahedron implements Shape {

    private final Triangle[] triangles;

    public Tetrahedron(Vertex a, Vertex b, Vertex c, Vertex d, Color color) {
        this.triangles = new Triangle[]{
            new Triangle(a, b, c, color),
            new Triangle(a, c, d, color),
            new Triangle(a, d, b, color),
            new Triangle(b, d, c, color)
        };
    }

    @Override
    public Triangle[] getTriangles() {
        return triangles;
    }
    
}
