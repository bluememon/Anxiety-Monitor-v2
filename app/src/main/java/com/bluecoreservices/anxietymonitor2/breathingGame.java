package com.bluecoreservices.anxietymonitor2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class breathingGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_game);

        View bolite = findViewById(R.id.bc);
        setContentView(new MyView(this));
    }

    public class MyView extends View
    {
        public MyView(Context context)
        {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            int x = getWidth();
            int y = getHeight();
            int radius;
            radius = 100;
            Paint paint = new Paint();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawPaint(paint);
            // Use Color.parseColor to define HTML colors
            paint.setColor(Color.parseColor("#CD5C5C"));
            canvas.drawCircle(x / 2, y - 200, radius, paint);


        }
    }
}
