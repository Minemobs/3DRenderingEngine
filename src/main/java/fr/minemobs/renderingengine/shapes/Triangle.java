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

    public Triangle multiply(int x) {
        if(x < 2) return this;
        return new Triangle(new Vertex(this.v1.x * x, this.v1.y * x, this.v1.z * x), new Vertex(this.v2.x * x, this.v2.y * x, this.v2.z * x), new Vertex(this.v3.x * x, this.v3.y * x, this.v3.z * x), this.color);
    }

    @Override
    public String toString() {
        return "V1: " + v1.toString() + " | V2: " + v2.toString() + " | V3: " + v3.toString() + "\n";
    }
}
