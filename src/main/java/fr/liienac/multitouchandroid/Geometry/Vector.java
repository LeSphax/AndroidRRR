/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Geometry;

public class Vector {
    public float dx,dy;

    public Vector(float dx_, float dy_) { dx=dx_;dy=dy_; }
    public Vector(Point p1, Point p2) { dx=p2.x-p1.x; dy=p2.y-p1.y; }

    public boolean equals(Vector p) { return (dx==p.dx) && (dy==p.dy); } // comparing floats [FIXME]

    static public Vector plus(Vector p1, Vector p2) {
        float dx = (p1.dx + p2.dx);
        float dy = (p1.dy + p2.dy);
        return new Vector(dx,dy);
    }

    static public Vector minus(Vector p1, Vector p2) {
        float dx = (p1.dx - p2.dx);
        float dy = (p1.dy - p2.dy);
        return new Vector(dx,dy);
    }

    static public Vector div(Vector p1, float d) {
        float dx = p1.dx/d;
        float dy = p1.dy/d;
        return new Vector(dx,dy);
    }

    public float normSq() {
        return dx*dx + dy*dy;
    }

    public float norm() {
        return (float)Math.sqrt(normSq());
    }

    static public float scalarProduct(Vector u, Vector v) {
        return u.dx*v.dx + u.dy*v.dy;
    }

    static public float crossProduct(Vector u, Vector v) {
        return u.dx*v.dy - u.dy*v.dx;
    }
}
