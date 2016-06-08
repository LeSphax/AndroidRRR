/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package com.example.arthurix.multitouche2;

import java.util.HashMap;
import java.util.Map;

import fr.liienac.multitouchandroid.Event.Timeout;
import fr.liienac.multitouchandroid.Geometry.Point;
import fr.liienac.multitouchandroid.StateMachineCanvas;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

//import android.support.v4.view.MotionEventCompat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

public class T01_MTCursor extends Activity {

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
		}

		Map<Long, Cursor> cursors = new HashMap<Long, Cursor>();
		Handler timeoutHandler = new Handler(Looper.getMainLooper()) {
			public void handleMessage(Message inputMessage) {
				if (inputMessage.what!=numTimer) return;
				Timeout evt = new Timeout();
				/*for (StateMachineCanvas m : machines) {
					m.handleEvent(Timeout.class, evt);
				}*/
			}
		};

		int numTimer=0;
		public void disarmTimer() {
			numTimer+=1;
		}
		public void armTimer(int ms) {
			//Message msg; // dummy msg
			timeoutHandler.sendEmptyMessageDelayed(numTimer, ms);
		}


		public MyView(Context c) {
			super(c);

			// cache paints to avoid recreating them at each draw
			paint = new Paint();
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			super.onSizeChanged(w, h, oldw, oldh);
		}

		// cache paints to avoid recreating them at each draw
		Paint paint;

		@Override
		protected void onDraw(Canvas canvas) {

			// "erase" canvas (fill it with white)
			canvas.drawColor(0xFFAAAAAA);
			// draw cursors
			for (Map.Entry<Long, Cursor> entry : cursors.entrySet()) {
				Cursor c = entry.getValue();
				canvas.drawCircle(c.p.x, c.p.y, 50, paint);
				canvas.drawText(""+c.id, c.p.x+30, c.p.y-30, paint);
			}
		}

		private void onTouchDown(Point p, int cursorid) {
			//System.out.println("down "+cursorid + " " + p.x +" "+p.y);
			Cursor c = new Cursor();
			c.p=p; c.id=cursorid;
			cursors.put(Long.valueOf(c.id),c);
			invalidate();
		}

		private void onTouchMove(Point p, int cursorid) {
			//System.out.println("move "+cursorid+ " " +p.x+" "+p.y);
			Cursor c = cursors.get(Long.valueOf(cursorid));
			c.p = p;
			invalidate();
		}

		private void onTouchUp(Point p, int cursorid) {
			//System.out.println("up "+cursorid);
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

	}
}


