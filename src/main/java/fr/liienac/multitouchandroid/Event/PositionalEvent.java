/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Event;

import fr.liienac.multitouchandroid.Graphic.Item;
import fr.liienac.multitouchandroid.Geometry.Point;

public class PositionalEvent extends Event {
	//static enum Type { Press, Move, Release };
	//Type type;
	public int cursorID;
	public Point p;
	public Item graphicItem;
	public float orientation;
	PositionalEvent(/*Type t_,*/ int cursorid_, Point p_, Item s_) { /*type=t_;*/ cursorID=cursorid_; p=p_; graphicItem =s_; orientation=0;}
	PositionalEvent(/*Type t_,*/ int cursorid_, Point p_, Item s_, float o_) { /*type=t_;*/ cursorID=cursorid_; p=p_; graphicItem =s_; orientation=o_;}
	
	/*// are they really necessary...
	public Point getPoint() { return p; }
	public Item getGraphicItem() { return graphicItem; }
	public int getCursorID() { return cursorid; }
	public float getOrientation() { return orientation; }*/
}

