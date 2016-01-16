package com.bluecoreservices.anxietymonitor;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class breathingGame extends AppCompatActivity {
    public final static String PAGINA_DEBUG = "breathingGame";
    Toolbar toolbar;
    View inflatingButton;
    AnimatorSet scaleDown;
    AnimatorSet animQuote;
    SimpleDrawingView circleButton;
    TextView countDown;
    TextView quoteText;
    View quoteContainer;
    Long ballDuration;
    boolean isCancelled;
    Integer respirationCount;
    ArrayList<String> colorPallete;
    ArrayList<String> quoteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathing_game);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.anxiety_game_title);
        getSupportActionBar().setSubtitle(R.string.app_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scaleDown = new AnimatorSet();
        animQuote = new AnimatorSet();

        inflatingButton = findViewById(R.id.respButton);
        quoteContainer = findViewById(R.id.quoteContainer);
        quoteText = (TextView)findViewById(R.id.quoteText);
        countDown = (TextView)findViewById(R.id.countDownTF);
        respirationCount = 0;

        colorPallete = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.color_array)));
        quoteList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.anxiety_quotes)));

        quoteContainer.setAlpha(0.0f);
        quoteText.setText(quoteList.get(respirationCount));
        ballDuration = 1000l;


        circleButton = (SimpleDrawingView)findViewById(R.id.simpleDrawingView1);
        circleButton.colorCircle(Color.RED);
        //circleButton.colorCircle(Color.parseColor("#00796B"));

        inflatingButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i(PAGINA_DEBUG, "Hold!");
                    animateButton();

                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i(PAGINA_DEBUG, "Release!");
                    stopAnimation();

                    return true;
                }
                return false;
            }
        });
    }

    private void animateButton() {
        Log.i(PAGINA_DEBUG, "startAnimate!");
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(inflatingButton, "scaleX", 30.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(inflatingButton, "scaleY", 30.0f);

        scaleX.setDuration(ballDuration);
        scaleY.setDuration(ballDuration);

        scaleDown = new AnimatorSet();

        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ObjectAnimator showQuote = ObjectAnimator.ofFloat(quoteContainer, "alpha", 1f);
                showQuote.setDuration(500L);
                animQuote = new AnimatorSet();
                animQuote.play(showQuote);
                animQuote.start();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                countDown(countDown, 3);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });

        scaleDown.play(scaleX).with(scaleY);
        scaleDown.start();
    }

    private void stopAnimation() {
        isCancelled = false;
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(inflatingButton, "scaleX", 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(inflatingButton, "scaleY", 1.0f);

        if (scaleDown.isRunning()) {
            scaleDown.pause();

            isCancelled = true;

            scaleX.setDuration(1000l);
            scaleY.setDuration(1000l);
        }

        else {
            scaleX.setDuration(ballDuration);
            scaleY.setDuration(ballDuration);
        }

        scaleDown = new AnimatorSet();
        scaleDown.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (!isCancelled) {
                    if (respirationCount < 4) {
                        final float[] from = new float[3],
                                to = new float[3];

                        Color.colorToHSV(Color.parseColor(colorPallete.get(respirationCount)), from);   // from white
                        Color.colorToHSV(Color.parseColor(colorPallete.get(respirationCount + 1)), to);     // to red

                        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);   // animate from 0 to 1
                        anim.setDuration(ballDuration);                              // for 300 ms

                        final float[] hsv = new float[3];                  // transition color
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                // Transition along each axis of HSV (hue, saturation, value)
                                hsv[0] = from[0] + (to[0] - from[0]) * animation.getAnimatedFraction();
                                hsv[1] = from[1] + (to[1] - from[1]) * animation.getAnimatedFraction();
                                hsv[2] = from[2] + (to[2] - from[2]) * animation.getAnimatedFraction();

                                circleButton.colorCircle(Color.HSVToColor(hsv));
                            }
                        });

                        anim.start();
                    }
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator showQuote = ObjectAnimator.ofFloat(quoteContainer, "alpha", 0f);
                showQuote.setDuration(100L);
                animQuote = new AnimatorSet();
                animQuote.play(showQuote);

                animQuote.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {}

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!isCancelled) {
                            quoteText.setText(quoteList.get(respirationCount));
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {}

                    @Override
                    public void onAnimationRepeat(Animator animation) {}
                });
                animQuote.start();

                if (!isCancelled) {
                    if (ballDuration < 3000l) {
                        ballDuration += 600l;
                    }
                    if (respirationCount < 3) {
                        respirationCount++;
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        scaleDown.play(scaleX).with(scaleY);
        scaleDown.start();
    }

    private void countDown(final TextView tv, final int count) {
        if (count == 0) {
            tv.setText(""); //Note: the TextView will be visible again here.
            return;
        }
        tv.setText(Integer.toString(count));
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation anim) {
                countDown(tv, count - 1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        tv.startAnimation(animation);
    }
}
