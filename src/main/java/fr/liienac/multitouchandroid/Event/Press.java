/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Event;

import fr.liienac.multitouchandroid.Graphic.Item;
import fr.liienac.multitouchandroid.Geometry.Point;

public class Press extends PositionalEvent {
	public Press(int cursorid_, Point p_, Item s_, float angRad) { super(/*Type.Press,*/ cursorid_, p_, s_, angRad); }
}