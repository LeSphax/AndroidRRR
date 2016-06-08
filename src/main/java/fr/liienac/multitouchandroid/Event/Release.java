/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package fr.liienac.multitouchandroid.Event;

import fr.liienac.multitouchandroid.Graphic.Item;
import fr.liienac.multitouchandroid.Geometry.Point;

public class Release extends PositionalEvent {
	public Release(int cursorid_, Point p_, Item s_, float angRad) { super(/*Type.Release,*/ cursorid_, p_, s_, angRad); }
}