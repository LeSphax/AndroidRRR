/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid;

// android
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

// java2d
//import java.awt.Color;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.image.BufferedImage;

import java.util.HashMap;
import java.util.Map;

import fr.liienac.multitouchandroid.Geometry.Point;


public class ColorPicking {
	long seed=0;
	private Map<Long, Object> colorObjectMap = new HashMap<Long, Object>();
	
	// android
	public Bitmap bitmap = null;
	public Canvas canvas = null;
	// java2d
//	public BufferedImage bitmap;
//	public Graphics2D canvas;

	public ColorPicking() {
		resetSeed(0);
	}

	public void onSizeChanged(int w, int h) {
		// android
		bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		// java2d
//		bitmap = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
//		canvas = bitmap.createGraphics();
//	    canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public void reset() {
		// repeat with same seed to get repeatable randomized color
		// helpful for debugging by displaying the color buffer:
		// no color changed between frames
		resetSeed(1234);
		colorObjectMap.clear();
		
		if(canvas!=null) {
			// android
			canvas.drawColor(0xFFFFFFFF);
			//java2d
//			canvas.setBackground(Color.WHITE);
//			canvas.clearRect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		}
	}

	private void resetSeed(long s) {
		if (s==0)
			seed = System.currentTimeMillis();
		else
			seed=s;
	}

	private double random() {
		seed = (seed*9301+49297) % 233280;
		return seed/(233280.);
	}

	
	public void newColor(Object value, Paint paint) { // android
//	public void newColor(Object value) { // java2d
		// maxed out to 200 to discriminate the colors on a white bg
		// helpful for debugging by displaying the color buffer
		// does NOT guarantee unique identifiers but sufficient for debugging purpose
		// other algorithm: store the hash or objectid value into the pixels
		int
		r=(int) Math.floor(random()*100),
		g=(int) Math.floor(random()*100),
		b=(int) Math.floor(random()*100);
		int id=((((r << 8) + g) << 8) + b);
		colorObjectMap.put(Long.valueOf(id), value);
		paint.setARGB(255, r, g ,b); // android
//		canvas.setColor(new Color(r,g,b)); // java2d
	}

	public Object getValFromColor(int r, int g, int b) {
		int id=((((r << 8) + g) << 8) + b);
		return colorObjectMap.get(id);
	}

	public Object getValFromColor(int c) {
		return colorObjectMap.get(Long.valueOf(c & 0x00FFFFFF));
	}

	public Object pick(Point p) {
		Object o = null;
		if((p.x>=0) && (p.y>=0) && (p.x<bitmap.getWidth()) && (p.y<bitmap.getHeight())) {
			int color = bitmap.getPixel((int)p.x, (int)p.y); // android
//			int color = bitmap.getRGB((int)p.x, (int)p.y); // java2d
			o = getValFromColor(color);
		}
		return o;
	}

}
