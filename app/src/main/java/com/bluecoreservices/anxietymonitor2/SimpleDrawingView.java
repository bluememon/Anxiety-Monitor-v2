package com.bluecoreservices.anxietymonitor2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Guillermo Uribe on 12/01/2016.
 */
public class SimpleDrawingView extends View {
    public final static String PAGINA_DEBUG = "simpleDrawingView";
    // setup initial color
    public int paintColor = Color.parseColor("#CD0300");
    // defines paint and canvas
    private Paint drawPaint;

    public SimpleDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(5);
        drawPaint.setStyle(Paint.Style.FILL);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setShadowLayer(4.0f,0.0f,10.0f, Color.BLACK);
    }

    public void colorCircle (int color) {
        drawPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(this.getWidth()/2, this.getHeight()/2, this.getHeight()/2, drawPaint);
    }
}
