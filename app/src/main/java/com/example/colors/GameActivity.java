package com.example.colors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.colors.custom.Animations;
import com.example.colors.custom.Color;
import com.example.colors.game.GlobalVariables;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {

    private static final int COLOR_CHANGE_STEPS = 20;

    private Handler handler = new Handler();
    private Color currentColor;
    private Color startColor;
    private Color endColor;
    private Color[] colorSet;
    private int count = 0;

    @Bind(R.id.colors_layout) ImageView colorsLayout;
    @Bind(R.id.play) ImageView play;
    @Bind(R.id.tap_to_play) TextView tapToPlay;
    @Bind(R.id.your_color_is) TextView yourColorIs;
    @Bind(R.id.loading_layout) LinearLayout loadingLayout;
    @Bind(R.id.parent_layout) RelativeLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);

        startColor = new Color(this, R.color.colorPrimary);
        endColor = new Color(); endColor.newRandomRGB();
        colorSet = Color.getIntermediateColors(startColor, endColor, COLOR_CHANGE_STEPS);
        currentColor = startColor;

        handler.post(entryEffects);

        colorsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GlobalVariables.setChosenColor(currentColor);
                colorsLayout.setBackgroundColor(android.graphics.Color.parseColor(currentColor.toHexString()));

                handler.removeCallbacks(flashAnimationLoop);
                YoYo.with(Techniques.TakingOff).duration(500).playOn(play);
                YoYo.with(Techniques.FadeOutDown).duration(1500).playOn(tapToPlay);
                handler.removeCallbacks(changeColor);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Animations.leftPad(((View)colorsLayout.getParent()), 50, 300);
                        Animations.rightPad(((View)colorsLayout.getParent()), 50, 300);
                        Animations.topPad(((View)colorsLayout.getParent()), 80, 300);
                        Animations.bottomPad(((View)colorsLayout.getParent()), 100, 300);

                        YoYo.with(Techniques.FadeInDown).duration(500).playOn(yourColorIs);
                        YoYo.with(Techniques.FadeInUp).duration(500).playOn(loadingLayout);
                        YoYo.with(Techniques.FadeOut).duration(3500).playOn(parentLayout);
                    }
                },500);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(GameActivity.this, LevelActivity.class));
                    }
                },3000);
                return false;
            }
        });
    }

    private Runnable entryEffects = new Runnable() {
        @Override
        public void run() {
            ((View) colorsLayout.getParent()).setPadding(0,0,0,0);
            yourColorIs.setAlpha(0f);
            loadingLayout.setAlpha(0f);
            play.setAlpha(0f);
            delayedHide(100);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    YoYo.with(Techniques.Landing).duration(500).playOn(play);
                    YoYo.with(Techniques.FadeInDown).duration(1000).playOn(tapToPlay);
                    handler.postDelayed(flashAnimationLoop,1500);

                    handler.removeCallbacks(changeColor);
                    handler.postDelayed(changeColor,1000);
                }
            },1000);
        }
    };

    private Runnable flashAnimationLoop = new Runnable() {
        @Override
        public void run() {
            YoYo.with(Techniques.Flash).duration(1500).playOn(tapToPlay);
            handler.postDelayed(flashAnimationLoop,1000);
        }
    };

    @Override
    protected void onPostResume() {
        super.onPostResume();
        handler.post(entryEffects);
    }

    private Runnable changeColor = new Runnable() {
        @Override
        public void run() {
            currentColor.absorb(colorSet[count%COLOR_CHANGE_STEPS]);
            //System.out.println("Applying: "+colorSet[count]);
            colorsLayout.setBackgroundColor(android.graphics.Color.argb(255,currentColor.red,currentColor.green,currentColor.blue));
            count++;
            if(count % COLOR_CHANGE_STEPS == 0) {
                colorsLayout.setBackgroundColor(android.graphics.Color.argb(255,endColor.red,endColor.green,endColor.blue));
                startColor.absorb(endColor);
                endColor.newRandomRGB();
                colorSet = Color.getIntermediateColors(startColor, endColor, COLOR_CHANGE_STEPS);
                //System.out.println(startColor + "" + endColor);
            }
            handler.postDelayed(changeColor,25);
        }
    };

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, 300);
    }

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            colorsLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Handler mHideHandler = new Handler();
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
