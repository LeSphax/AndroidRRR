/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Graphic;

import fr.liienac.multitouchandroid.Geometry.Point;
import fr.liienac.multitouchandroid.Geometry.Vector;

// android
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.TextView;

// java2d
//import java.awt.Graphics2D;
//import java.awt.geom.*;


public class Item {

	public boolean blocked;
	public float x,y,width,height;
    public Style style;

	public Item(float x_, float y_, float w_, float h_) {
		x=x_; y=y_; width=w_; height=h_;
        style = new Style(0,0,0);
		blocked = false;
	} 

 	// android
    public Matrix transform = new Matrix();

	public void translateBy(Vector v) {
		transform.postTranslate(v.dx, v.dy);
	}

	public void rotateBy(float dangle, Point center) {
		Matrix t = transform;
		t.postTranslate(-center.x, -center.y);
		t.postRotate((float)(dangle*180/Math.PI));
		t.postTranslate(center.x, center.y);
	}

    public void rotateBy(Vector u, Vector v, Point center) {
        Matrix t = transform;
        float uvnorm = u.norm()*v.norm();
        float cosa = Vector.scalarProduct(u, v)/uvnorm; // cosa
        float sina = Vector.crossProduct(u, v)/uvnorm; // sina

		Matrix rot = new Matrix();
		rot.setSinCos(sina,cosa);
        t.postTranslate(-center.x, -center.y);
       // t.postScale(cosa, cosa);
       // t.postSkew(sina, -sina);
		t.postConcat(rot);
        t.postTranslate(center.x, center.y);
    }

	public void scaleBy(float ds, Point center) {
		Matrix t = transform;
		t.postTranslate(-center.x, -center.y);
		t.postScale((float)(ds), (float)(ds));
		t.postTranslate(center.x, center.y);
	}
	
	public void applyTransform(Canvas canvas) {
		canvas.concat(transform);		
	}
	
	public void draw(Canvas canvas, Paint paint) {
		applyTransform(canvas);
		canvas.drawRect(x, y, x+width, y+height, paint);
	}


/*
	// java2D

	public AffineTransform transform = new AffineTransform(); // java2d

	public void translateBy(Vector v) {
		AffineTransform t = new AffineTransform();
		t.translate(v.dx, v.dy);
		t.concatenate(transform);
		transform = t;
	}
	
	public void rotateBy(float dangle, Point center) {
		AffineTransform t = new AffineTransform();
		t.translate(center.x, center.y);
		t.rotate((float)(dangle));
		t.translate(-center.x, -center.y);
		t.concatenate(transform);
		transform=t;			
	}
	
	public void scaleBy(float ds, Point center) {		
		AffineTransform t = new AffineTransform();
		t.translate(center.x, center.y);
		t.scale((float)(ds), (float)(ds));
		t.translate(-center.x, -center.y);
		t.concatenate(transform);
		transform=t;
	}
		
	public void applyTransform(Graphics2D g) {
		g.transform(transform);		
	}
	
	public void draw(Graphics2D g) {
		applyTransform(g);
		g.fillRect((int)x, (int)y, (int)(width), (int)(height));
		//canvas.translate(x, y);
		//canvas.drawRect(0, 0, width, height, paint);
	}
*/
	
}
