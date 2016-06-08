/*
 * Copyright (c) 2016 Stéphane Conversy - ENAC - All rights Reserved
 */

package com.example.arthurix.multitouche2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import fr.liienac.multitouchandroid.ColorPicking;
import fr.liienac.multitouchandroid.Event.Move;
import fr.liienac.multitouchandroid.Event.PositionalEvent;
import fr.liienac.multitouchandroid.Event.Press;
import fr.liienac.multitouchandroid.Event.Release;
import fr.liienac.multitouchandroid.Geometry.Point;
import fr.liienac.multitouchandroid.Graphic.Item;
import fr.liienac.multitouchandroid.StateMachineCanvas;
//import android.support.v4.view.MotionEventCompat;

public class T04_RRR extends Activity {

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
            int r, g, b;
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

            sceneGraph.add(createItem(0,0,300,300));

            sceneGraph.add(createItem(400,400,300,300));
        }

        @NonNull
        private Item createItem(float x, float y, float width, float height) {
            Item graphicItem;
            StateMachineCanvas machine;
            graphicItem = new Item(x,y,width,height);

            machine = new ResizeMachine(graphicItem);
            machines.add(machine);

            StateMachineCanvas machineRotate = new RotateMachine(graphicItem);
            machines.add(machineRotate);

            machine = new DeplacementMachine(graphicItem);
            machines.add(machine);
            return graphicItem;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            //colorPicking.onSizeChanged(w,h,oldw,oldh);
            colorPicking.onSizeChanged(w, h);
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
            for (Item graphicItem : sceneGraph) {
                // Display view
                canvas.save();
                paint.setARGB(255, graphicItem.style.r, graphicItem.style.g, graphicItem.style.b);
                graphicItem.draw(canvas, paint);
                canvas.restore();

                // Picking view
                colorPicking.canvas.save();
                colorPicking.newColor(graphicItem, pickingPaint);
                graphicItem.draw(colorPicking.canvas, pickingPaint);
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
                canvas.drawText("" + c.id, c.p.x + 30, c.p.y - 30, paint);
            }
        }

        class DeplacementMachine extends StateMachineCanvas {
            Item graphicItem = null;
            long cursor1 = -1;
            long cursor2 = -1;
            Point positionCursor1;
            Point positionCursor2;

            DeplacementMachine(Item graphicItem) {
                this.graphicItem = graphicItem;
            }

            public State start = new State() {
                Transition press = new Transition<Press>(Press.class) {
                    public boolean guard(Press evt) {
                        return cursor1 == -1 && evt.graphicItem == graphicItem;
                    }

                    public void action(Press evt) {
                        Log.d("T04_TouchSelect", "SELECT DOWN");
                        cursor1 = evt.cursorID;

                        graphicItem.style.r = 255;
                        positionCursor1 = evt.p;
                    }

                    public State goTo() {
                        Log.d("T04", "Touched");
                        return touched;
                    }
                };
            };

            public State touched = new State() {

                Transition press = new Transition<Press>(Press.class) {
                    public boolean guard(Press evt) {
                        return cursor2 == -1 && evt.graphicItem == graphicItem;
                    }

                    public void action(Press evt) {
                        Log.d("T04", "DoubleTouched");
                        cursor2 = evt.cursorID;
                        positionCursor2 = evt.p;

                    }

                    public State goTo() {
                        Log.d("T04", "DoubleTouched");
                        return doubleTouched;
                    }
                };

                Transition release = new Transition<Release>(Release.class) {

                    public boolean guard(Release evt) {
                        return evt.cursorID == cursor1;
                    }

                    public void action(Release evt) {
                        graphicItem.style.r = 0;
                        cursor1 = -1;

                    }

                    public State goTo() {
                        return start;
                    }
                };
                Transition move = new Transition<Move>(Move.class) {

                    public boolean guard(Move evt) {
                        return evt.cursorID == cursor1;
                    }

                    public void action(Move evt) {
                        //System.out.println("yep");
                        float dX = evt.p.x - positionCursor1.x;
                        float dY = evt.p.y - positionCursor1.y;
                        graphicItem.translateBy(new fr.liienac.multitouchandroid.Geometry.Vector(dX, dY));
                        positionCursor1 = evt.p;


                    }

                    public State goTo() {
                        return touched;
                    }
                };
            };

            public State doubleTouched = new State() {

                Transition release = new Transition<Release>(Release.class) {

                    public boolean guard(Release evt) {
                        return evt.cursorID == cursor1 || evt.cursorID == cursor2;
                    }

                    public void action(Release evt) {
                        if (evt.cursorID == cursor1) {
                            cursor1 = cursor2;
                            positionCursor1 = positionCursor2;
                        }
                        cursor2 = -1;
                    }

                    public State goTo() {
                        return touched;
                    }
                };
                Transition move = new Transition<Move>(Move.class) {

                    public boolean guard(Move evt) {
                        return evt.cursorID == cursor1 || evt.cursorID == cursor2;
                    }


                    public void action(Move evt) {
                        if (evt.cursorID == cursor1) {
                            positionCursor1 = evt.p;
                        } else if (evt.cursorID == cursor2) {
                            positionCursor2 = evt.p;
                        }
                        //dis
                        Log.d("T04", "Scale move");
                        //Log.d("T04", ""+newDistance);

                    }

                    public State goTo() {
                        return doubleTouched;
                    }
                };
            };
        }

        class ResizeMachine extends StateMachineCanvas {
            Item graphicItem = null;
            long cursor1 = -1;
            long cursor2 = -1;
            Point positionCursor1;
            Point positionCursor2;

            ResizeMachine(Item graphicItem) {
                this.graphicItem = graphicItem;
            }

            public State start = new State() {
                Transition press = new Transition<Press>(Press.class) {
                    public boolean guard(Press evt) {
                        return cursor1 == -1 && evt.graphicItem == graphicItem;
                    }

                    public void action(Press evt) {
                        Log.d("T02_TouchSelect", "SELECT DOWN");
                        cursor1 = evt.cursorID;
                        positionCursor1 = evt.p;
                    }

                    public State goTo() {
                        Log.d("T04", "Touched");
                        return touched;
                    }
                };
            };

            public State touched = new State() {

                Transition press = new Transition<Press>(Press.class) {
                    public boolean guard(Press evt) {
                        return cursor2 == -1 && evt.graphicItem == graphicItem;
                    }

                    public void action(Press evt) {
                        Log.d("T04", "DoubleTouched");
                        cursor2 = evt.cursorID;
                        positionCursor2 = evt.p;
                    }

                    public State goTo() {
                        Log.d("T04", "DoubleTouched");
                        return doubleTouched;
                    }
                };

                Transition release = new Transition<Release>(Release.class) {

                    public boolean guard(Release evt) {
                        return evt.cursorID == cursor1;
                    }

                    public void action(Release evt) {
                        cursor1 = -1;

                    }

                    public State goTo() {
                        return start;
                    }
                };
                Transition move = new Transition<Move>(Move.class) {

                    public boolean guard(Move evt) {
                        return evt.cursorID == cursor1;
                    }

                    public void action(Move evt) {
                        positionCursor1 = evt.p;


                    }

                    public State goTo() {
                        return touched;
                    }
                };
            };

            public State doubleTouched = new State() {

                Transition release = new Transition<Release>(Release.class) {

                    public boolean guard(Release evt) {
                        return evt.cursorID == cursor1 || evt.cursorID == cursor2;
                    }

                    public void action(Release evt) {
                        if (evt.cursorID == cursor1) {
                            cursor1 = cursor2;
                            positionCursor1 = positionCursor2;
                        }
                        cursor2 = -1;
                    }

                    public State goTo() {
                        return touched;
                    }
                };
                Transition move = new Transition<Move>(Move.class) {

                    public boolean guard(Move evt) {
                        return evt.cursorID == cursor1 || evt.cursorID == cursor2;
                    }

                    public void action(Move evt) {
                        float previousDistance = Point.distance(positionCursor1, positionCursor2);
                        if (evt.cursorID == cursor1) {
                            positionCursor1 = evt.p;

                            float newDistance = Point.distance(positionCursor1, positionCursor2);
                            graphicItem.scaleBy(newDistance / previousDistance, positionCursor2);
                        } else if (evt.cursorID == cursor2) {
                            positionCursor2 = evt.p;
                            float newDistance = Point.distance(positionCursor1, positionCursor2);


                            graphicItem.scaleBy(newDistance / previousDistance, positionCursor1);
                        }


                        Log.d("T04", "Scale move");
                        //Log.d("T04", ""+newDistance);

                    }

                    public State goTo() {
                        return doubleTouched;
                    }
                };
            };
        }
        
        private Point getCenter(Point p1, Point p2){
            float centerX = (Math.max(p1.x, p2.x) - Math.min(p1.x, p2.x)) / 2 + Math.min(p1.x, p2.x);
            float centerY = (Math.max(p1.y, p2.y) - Math.min(p1.y, p2.y)) / 2 + Math.min(p1.y, p2.y);
            return new Point(centerX, centerY);
        }

        class RotateMachine extends StateMachineCanvas {
            Item graphicItem = null;
            long cursor1 = -1;
            long cursor2 = -1;
            Point positionCursor1;
            Point positionCursor2;

            RotateMachine(Item graphicItem) {
                this.graphicItem = graphicItem;
            }

            public State start = new State() {
                Transition press = new Transition<Press>(Press.class) {
                    public boolean guard(Press evt) {
                        return cursor1 == -1 && evt.graphicItem == graphicItem;
                    }

                    public void action(Press evt) {
                        Log.d("T04_2", "SELECT DOWN");
                        cursor1 = evt.cursorID;
                        positionCursor1 = evt.p;
                    }

                    public State goTo() {
                        Log.d("T04_2", "Touched");
                        return touched;
                    }
                };
            };

            public State touched = new State() {

                Transition press = new Transition<Press>(Press.class) {
                    public boolean guard(Press evt) {
                        return cursor2 == -1 && evt.graphicItem == graphicItem;
                    }

                    public void action(Press evt) {
                        Log.d("T04_2", "DoubleTouched");
                        cursor2 = evt.cursorID;
                        positionCursor2 = evt.p;
                    }

                    public State goTo() {
                        Log.d("T04_2", "DoubleTouched");
                        return doubleTouched;
                    }
                };

                Transition release = new Transition<Release>(Release.class) {

                    public boolean guard(Release evt) {
                        return evt.cursorID == cursor1;
                    }

                    public void action(Release evt) {
                        cursor1 = -1;

                    }

                    public State goTo() {
                        return start;
                    }
                };
                Transition move = new Transition<Move>(Move.class) {

                    public boolean guard(Move evt) {
                        return evt.cursorID == cursor1;
                    }

                    public void action(Move evt) {
                        //System.out.println("yep");
                        positionCursor1 = evt.p;


                    }

                    public State goTo() {
                        return touched;
                    }
                };
            };

            public State doubleTouched = new State() {

                Transition release = new Transition<Release>(Release.class) {

                    public boolean guard(Release evt) {
                        return evt.cursorID == cursor1 || evt.cursorID == cursor2;
                    }

                    public void action(Release evt) {
                        if (evt.cursorID == cursor1) {
                            cursor1 = cursor2;
                            positionCursor1 = positionCursor2;
                        }
                        cursor2 = -1;

                    }

                    public State goTo() {
                        return touched;
                    }
                };
                Transition move = new Transition<Move>(Move.class) {

                    public boolean guard(Move evt) {
                        return evt.cursorID == cursor1 || evt.cursorID == cursor2;
                    }

                    public void action(Move evt) {
                        double angleRotate;
                        if (evt.cursorID == cursor1) {
                            angleRotate = computeAngle(changeOrigine(positionCursor2, positionCursor1), changeOrigine(positionCursor2, evt.p), positionCursor2);
                            graphicItem.rotateBy((float) angleRotate, positionCursor2);
                            Log.d("T04_02", "Angle rotate curseur 1:" + angleRotate);
                            positionCursor1 = evt.p;

                        } else if (evt.cursorID == cursor2) {

                            angleRotate = computeAngle(changeOrigine(positionCursor1, positionCursor2), changeOrigine(positionCursor1, evt.p), positionCursor1);
                            graphicItem.rotateBy((float) angleRotate, positionCursor1);
                            Log.d("T04_02", "Angle rotate curseur 2:" + angleRotate);

                            positionCursor2 = evt.p;
                        }


                        Log.d("T04_02", "move");

                    }

                    public State goTo() {
                        return doubleTouched;
                    }
                };
            };

            private Point changeOrigine(Point pOrigine, Point p) {
                Point temp;
                temp = new Point(p.x - pOrigine.x, -p.y + pOrigine.y);

                return temp;
            }

            private double computeAngle(Point positionCursor, Point p, Point origine) {
                Log.d("T04_02R", "Position Curseur : " + positionCursor.x + " ; " + positionCursor.y);
                Log.d("T04_02R", "Position initiale : " + p.x + " ; " + p.y);
                Log.d("T04_02R", "Position origin : " + origine.x + " ; " + origine.y);
                float oldDistance = Point.distance(new Point(0, 0), positionCursor);
                float newDistance = Point.distance(new Point(0, 0), p);
                Log.d("T04_02R", "Distance old : " + oldDistance);
                Log.d("T04_02R", "Distance new : " + newDistance);
                double oldAngle = Math.signum(Math.asin(positionCursor.y / oldDistance)) * Math.acos(positionCursor.x / oldDistance);
                double newAngle = Math.signum(Math.asin(p.y / newDistance)) * Math.acos(p.x / newDistance);
                return -newAngle + oldAngle;
            }
        }


//-----------------------------------------

        private void onTouchDown(Point p, int cursorid) {
            Item s = (Item) colorPicking.pick(p);
            //System.out.println("down "+cursorid + " " + p.x +" "+p.y+ " "+s);
            PositionalEvent evt = new Press(cursorid, p, s, 0);
            for (StateMachineCanvas m : machines) {
                m.handleEvent(Press.class, evt);
            }

            Cursor c = new Cursor();
            c.p = p;
            c.id = cursorid;
            c.r = (int) Math.floor(Math.random() * 100);
            c.g = (int) Math.floor(Math.random() * 100);
            c.b = (int) Math.floor(Math.random() * 100);
            cursors.put(Long.valueOf(c.id), c);
            invalidate();
        }

        private void onTouchMove(Point p, int cursorid) {
            //System.out.println("move "+cursorid+ " " +p.x+" "+p.y);
            Cursor c = cursors.get(Long.valueOf(cursorid));

            if (Point.distance(c.p, p) > 0) {
                Item s = (Item) colorPicking.pick(p);
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
            Item s = (Item) colorPicking.pick(p);
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
            float x, y;

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    x = event.getX(index); //MotionEventCompat.getX(event, index);
                    y = event.getY(index); //MotionEventCompat.getY(event, index);
                    onTouchDown(new Point(x, y), id);
                    break;
                case MotionEvent.ACTION_MOVE:
                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        x = event.getX(i); //MotionEventCompat.getX(event, i);
                        y = event.getY(i); //MotionEventCompat.getY(event, i);
                        id = event.getPointerId(i); //MotionEventCompat.getPointerId(event, i);
                        onTouchMove(new Point(x, y), id);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    x = event.getX(index); //MotionEventCompat.getX(event, index);
                    y = event.getY(index); //MotionEventCompat.getY(event, index);
                    onTouchUp(new Point(x, y), id);
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
            float x, y;

            switch (action) {
                case MotionEvent.ACTION_HOVER_ENTER:
                    x = event.getX(index); //MotionEventCompat.getX(event, index);
                    y = event.getY(index); //MotionEventCompat.getY(event, index);
                    onTouchDown(new Point(x, y), id);
                    break;
                case MotionEvent.ACTION_HOVER_MOVE:
                    for (int i = 0; i < event.getPointerCount(); ++i) {
                        x = event.getX(i); //MotionEventCompat.getX(event, i);
                        y = event.getY(i); //MotionEventCompat.getY(event, i);
                        id = event.getPointerId(i); //MotionEventCompat.getPointerId(event, i);
                        onTouchMove(new Point(x, y), id);
                    }
                    break;
                case MotionEvent.ACTION_HOVER_EXIT:
                    x = event.getX(index); //MotionEventCompat.getX(event, index);
                    y = event.getY(index); //MotionEventCompat.getY(event, index);
                    onTouchUp(new Point(x, y), id);
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


