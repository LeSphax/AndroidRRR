package fr.liienac.multitouchandroid.Graphic;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import fr.liienac.multitouchandroid.Geometry.Point;
import fr.liienac.multitouchandroid.Geometry.Vector;

/**
 * Created by Arthurix on 12/02/2016.
 */
public class ItemWithText extends Item {

    private String name;
    private float scale;

    public ItemWithText(float x_, float y_, float w_, float h_, Context context, String name) {
        super(x_, y_, w_, h_);
        scale = 1;
        this.name = name;
        Log.d("T04", "ItemWithText " + name);
    }

    public void scaleBy(float ds, Point center) {
        super.scaleBy(ds, center);
        scale *= ds;
    }

    public void unApplyTransform(Canvas canvas) {
        Matrix inverse = new Matrix();
        if (transform.invert(inverse)) {
            canvas.concat(inverse);
        }
    }


    public void draw(Canvas canvas, Paint paint) {

        super.draw(canvas, paint);
        unApplyTransform(canvas);



        Paint paintText = new Paint();
        paintText.setColor(Color.GREEN);
        paintText.setTextSize(30);
        float widthText = paintText.measureText(name);

        float[] v = {x +width/2, y + height/2};
        transform.mapPoints(v);

        canvas.drawText(name, v[0] -widthText/2, v[1], paintText);
    }
}
