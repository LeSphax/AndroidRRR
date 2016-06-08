/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Event;

import fr.liienac.multitouchandroid.Geometry.Point;
import fr.liienac.multitouchandroid.Graphic.Item;

public class Click extends PositionalEvent {
    public int num;
	public Click(int cursorid_, Point p_, Item s_, float angRad, int num_) { super(/*Type.Press,*/ cursorid_, p_, s_, angRad); num=num_; }
}