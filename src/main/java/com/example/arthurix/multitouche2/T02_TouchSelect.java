/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package com.example.arthurix.multitouche2;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import fr.liienac.multitouchandroid.Event.Move;
import fr.liienac.multitouchandroid.Event.PositionalEvent;
import fr.liienac.multitouchandroid.ColorPicking;
import fr.liienac.multitouchandroid.Event.Press;
import fr.liienac.multitouchandroid.Event.Release;
import fr.liienac.multitouchandroid.Graphic.Item;
import fr.liienac.multitouchandroid.Geometry.Point;
import fr.liienac.multitouchandroid.StateMachineCanvas;


import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

//import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class T02_TouchSelect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setContentView(new MyView(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	public class MyView extends View {

		class Cursor {
			public Point p;
			public long id;
			int r,g,b;
		}
		
		Map<Long, Cursor> cursors = new HashMap<Long, Cursor>();
		Collection<Item> sceneGraph = new Vector<Item>();
		ColorPicking colorPicking = new ColorPicking();


		Vector<StateMachineCanvas> machines = new Vector<StateMachineCanvas>();

		public MyView(Context c) {
			super(c);
			
			// cache paints to avoid recreating them at each draw
			paint = new Paint();
			pickingPaint = new Paint();
			pickingPaint.setAntiAlias(false);

			StateMachineCanvas machine;
			Item graphicItem;

			graphicItem = new Item(0, 0, 300, 300);
			Item graphicItem2 = new Item(400, 400, 300, 300);
			sceneGraph.add(graphicItem);
			sceneGraph.add(graphicItem2);
			machine = new SelectMachine(graphicItem);
			machines.add(machine);
			StateMachineCanvas machine2 = new SelectMachine(graphicItem2);
			machines.add(machine2);

		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
			//colorPicking.onSizeChanged(w,h,oldw,oldh);
			colorPicking.onSizeChanged(w,h);
		}

		// cache paints to avoid recreating them at each draw
		Paint paint;
		Paint pickingPaint;
		
		@Override
		protected void onDraw(Canvas canvas) {

			// "erase" canvas (fill it with white)
			canvas.drawColor(0xFFAAAAAA);
			colorPicking.reset();
			
			// draw scene graph
			for(Item graphicItem : sceneGraph) {
				// Display view
				canvas.save();
				paint.setARGB(255, graphicItem.style.r, graphicItem.style.g, graphicItem.style.b);
				graphicItem.draw(canvas,  paint);
				canvas.restore();
				
				// Picking view
				colorPicking.canvas.save();
				colorPicking.newColor(graphicItem, pickingPaint);
				graphicItem.draw(colorPicking.canvas,  pickingPaint);
				colorPicking.canvas.restore();
			}

			if (false) { // debug: show picking view
				canvas.drawBitmap(colorPicking.bitmap, 0, 0, paint);
			}

			// draw cursors
			
			for (Map.Entry<Long, Cursor> entry : cursors.entrySet()) {
				Cursor c = entry.getValue();
				paint.setARGB(100, c.r, c.g, c.b);
				canvas.drawCircle(c.p.x, c.p.y, 50, paint);
				canvas.drawText(""+c.id, c.p.x+30, c.p.y-30, paint);
			}
		}

		class SelectMachine extends StateMachineCanvas {
			Item graphicItem = null;
			long cursor = -1;

			SelectMachine(Item graphicItem) {
				this.graphicItem = graphicItem;
			}

			public State start = new  State() {
				Transition press = new Transition<Press>(Press.class) {
					public boolean guard(Press evt) {
						return  cursor==-1 && evt.graphicItem== graphicItem;
					}
					public void action(Press evt) {
						Log.d("T02_TouchSelect", "SELECT DOWN");
							cursor = evt.cursorID;
							graphicItem.style.r = 255;

					}
					public State goTo() { return touched; }
				};
			};

			public State touched = new State() {		    	
				Transition release = new Transition<Release>(Release.class) {

					public boolean guard(Release evt) {
						return evt.cursorID == cursor;
					}

					public void action(Release evt) {
							Log.d("T02_TouchSelect", cursor+"    " + evt.cursorID);
							//System.out.println("yep");
							graphicItem.style.r = 0;
							cursor = -1;


					}
					public State goTo() { return start; }
				};
			};
		}

		
//-----------------------------------------
		
		private void onTouchDown(Point p, int cursorid) {
			Item s = (Item)colorPicking.pick(p);
            //System.out.println("down "+cursorid + " " + p.x +" "+p.y+ " "+s);
			PositionalEvent evt = new Press(cursorid, p, s, 0);
			for (StateMachineCanvas m : machines) {
				m.handleEvent(Press.class, evt);
			}
			
			Cursor c = new Cursor();
			c.p=p; c.id=cursorid;
			c.r=(int)Math.floor(Math.random()*100);
			c.g=(int)Math.floor(Math.random()*100);
			c.b=(int)Math.floor(Math.random()*100);
			cursors.put(Long.valueOf(c.id),c);
			invalidate();
		}

		private void onTouchMove(Point p, int cursorid) {
			//System.out.println("move "+cursorid+ " " +p.x+" "+p.y);
			Cursor c = cursors.get(Long.valueOf(cursorid));
			
			if(Point.distance(c.p, p)>0){
				Item s = (Item)colorPicking.pick(p);
				PositionalEvent evt = new Move(cursorid, p, s, 0);
				for (StateMachineCanvas m : machines) {
					m.handleEvent(Move.class, evt);
				}
				c.p = p;
			}
			
			invalidate();
		}

		private void onTouchUp(Point p, int cursorid) {
			//System.out.println("up "+cursorid);
			Item s = (Item)colorPicking.pick(p);
			PositionalEvent evt = new Release(cursorid, p, s, 0);
			for (StateMachineCanvas m : machines) {
				m.handleEvent(Release.class, evt);
			}

			cursors.remove(Long.valueOf(cursorid));
			invalidate();
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			//System.out.println("------");
			int action = event.getActionMasked(); //MotionEventCompat.getActionMasked(event);
			int index = event.getActionIndex(); //MotionEventCompat.getActionIndex(event);
			int id = event.getPointerId(index); //MotionEventCompat.getPointerId(event, index);
			float x,y;

			switch (action) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN:
					x = event.getX(index); //MotionEventCompat.getX(event, index);
					y = event.getY(index); //MotionEventCompat.getY(event, index);
					onTouchDown(new Point(x,y),id);
					break;
				case MotionEvent.ACTION_MOVE:
					for (int i=0; i<event.getPointerCount(); ++i) {
						x = event.getX(i); //MotionEventCompat.getX(event, i);
						y = event.getY(i); //MotionEventCompat.getY(event, i);
						id = event.getPointerId(i); //MotionEventCompat.getPointerId(event, i);
						onTouchMove(new Point(x,y),id);
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
					x = event.getX(index); //MotionEventCompat.getX(event, index);
					y = event.getY(index); //MotionEventCompat.getY(event, index);
					onTouchUp(new Point(x,y),id);
					break;
			}

			return true;
		}

		@Override
		public boolean onHoverEvent(MotionEvent event) {
			//System.out.println("------");
			int action = event.getActionMasked(); //MotionEventCompat.getActionMasked(event);
			int index = event.getActionIndex(); //MotionEventCompat.getActionIndex(event);
			int id = event.getPointerId(index); //MotionEventCompat.getPointerId(event, index);
			float x,y;

			switch (action) {
				case MotionEvent.ACTION_HOVER_ENTER:
					x = event.getX(index); //MotionEventCompat.getX(event, index);
					y = event.getY(index); //MotionEventCompat.getY(event, index);
					onTouchDown(new Point(x,y),id);
					break;
				case MotionEvent.ACTION_HOVER_MOVE:
					for (int i=0; i<event.getPointerCount(); ++i) {
						x = event.getX(i); //MotionEventCompat.getX(event, i);
						y = event.getY(i); //MotionEventCompat.getY(event, i);
						id = event.getPointerId(i); //MotionEventCompat.getPointerId(event, i);
						onTouchMove(new Point(x,y),id);
					}
					break;
				case MotionEvent.ACTION_HOVER_EXIT:
					x = event.getX(index); //MotionEventCompat.getX(event, index);
					y = event.getY(index); //MotionEventCompat.getY(event, index);
					onTouchUp(new Point(x,y),id);
					break;
			}

			return true;
		}
/*
		@Override
		public boolean onHoverEvent(MotionEvent event) {
			//System.out.println("=> TouchEvent");
			//System.out.println("---");
			int action = MotionEventCompat.getActionMasked(event);
			int index = MotionEventCompat.getActionIndex(event);
			int id = MotionEventCompat.getPointerId(event, index);
			float x,y;

			switch (action) {
			case MotionEvent.ACTION_HOVER_ENTER:
				x = MotionEventCompat.getX(event, index);
				y = MotionEventCompat.getY(event, index);
				onTouchDown(new Point(x,y),id);
				break;
			case MotionEvent.ACTION_HOVER_MOVE:
				//x = MotionEventCompat.getX(event, index);
				//y = MotionEventCompat.getY(event, index);
				//onTouchMove(new Point(x,y),id);
				for (int i=0; i<event.getPointerCount(); ++i) {
					x = MotionEventCompat.getX(event, i);
					y = MotionEventCompat.getY(event, i);
					id = MotionEventCompat.getPointerId(event, i);
					// event sent though there may be no move for that particular touch
					//System.out.println("------MoveEvent");
					//x=(float)Math.floor(x/15)*15;
					//y=(float)Math.floor(y/15)*15;
					onTouchMove(new Point(x,y),id);
				}
				break;
			case MotionEvent.ACTION_HOVER_EXIT:
				x = MotionEventCompat.getX(event, index);
				y = MotionEventCompat.getY(event, index);
				onTouchUp(new Point(x,y),id);
				break;
			}
			//System.out.println("<= TouchEvent");
			return true;
		}*/

	}	
}


