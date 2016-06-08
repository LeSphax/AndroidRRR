/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Geometry;

public class Point {
	public float x,y;
	
	public Point(float x_, float y_) {x=x_;y=y_;}

	public boolean equals(Point p) { return (x==p.x) && (y==p.y);} // comparing floats [FIXME]

	static public Vector minus(Point p1, Point p2) {
		float dx = (p1.x - p2.x);
		float dy = (p1.y - p2.y);
		return new Vector(dx,dy);
	}

	static public Point middle(Point p1, Point p2) {
		float x = (p1.x + p2.x)/2;
		float y = (p1.y + p2.y)/2;
		return new Point(x,y);
	}

	static public float distanceSq(Point p1, Point p2) {
        return new Vector(p1,p2).normSq();
	}

	static public float distance(Point p1, Point p2) {
		return (float)Math.sqrt(distanceSq(p1,p2));
	}

}
