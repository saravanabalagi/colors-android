package com.example.colors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.colors.custom.Color;
import com.example.colors.game.GlobalVariables;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Zeke on Jan 03, 2016.
 */
public class LevelActivity extends AppCompatActivity {

    private int colorChangeInterval = 700;
    @Bind(R.id.activity_layout) RelativeLayout activity_layout;
    @Bind(R.id.parent_layout) LinearLayout parentLayout;
    @Bind(R.id.chosenColor) FrameLayout chosenColor;
    @Bind(R.id.score) TextView score;
    @Bind(R.id.plus_one) TextView plusOne;
    @Bind(R.id.minus_one) TextView minusOne;
    @Bind(R.id.game_over_layout) LinearLayout gameOverLayout;
    @Bind(R.id.tap_to_play_again) TextView tapToPlayAgain;
    @Bind(R.id.replay) ImageView replay;

    int currentScore=0;
    Handler handler = new Handler();
    ConcurrentHashMap<Color, Integer> colorsHash = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        ButterKnife.bind(this);
        delayedHide(100);

        LinkedHashSet<Color> colorSet;
        GlobalVariables.setCurrentLevel(4);
        if(GlobalVariables.getCurrentLevel() < 5)
            colorSet = (GlobalVariables.getChosenColor()).getVisuallyDistinctColorSet();
        else colorSet = GlobalVariables.getChosenColor().getVisuallyDistinctExtendedColorSet();
        for (Color color : colorSet) colorsHash.put(color,2);

        System.out.println("Chosen color: "+GlobalVariables.getChosenColor());
        System.out.println("Color hash: "+colorsHash);
        chosenColor.setBackgroundColor(android.graphics.Color.parseColor(GlobalVariables.getChosenColor().toHexString()));
        gameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.FadeOut).duration(1200).playOn(activity_layout);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LevelActivity.this, GameActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                },1000);
            }
        });
        setLayout();
    }

    private void setLayout() {
        int currentLevel = GlobalVariables.getCurrentLevel();
        if(currentLevel==0) parentLayout.addView(getTile());
        else {
            int tilesPerRow; int l;
            for(tilesPerRow=1, l=0; l<currentLevel; l++) if(l%3==2) tilesPerRow++;
            for(int j=0; j<currentLevel; j++)
                parentLayout.addView(getTileLayout(tilesPerRow));
        }
    }

    private ImageView getTile(){
        final ImageView tile = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tile.setLayoutParams(layoutParams);
        final AtomicBoolean tileInitiated = new AtomicBoolean();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Color previousColor = new Color();
                if(tileInitiated.get()) {
                    previousColor = Color.getBackgroundColor(tile);
                    if(colorsHash.containsKey(previousColor))
                        colorsHash.put(previousColor, colorsHash.get(previousColor) + 1);
                }

                int colorAvailability;
                Color newColor;
                do {
                    int randomNumber = new Random().nextInt(colorsHash.size() - 1);
                    newColor = new ArrayList<>(colorsHash.keySet()).get(randomNumber);
                    colorAvailability = colorsHash.get(newColor);
                } while (colorAvailability == 0 || newColor.equals(previousColor));
                colorsHash.put(newColor, colorAvailability-1);

                tile.setBackgroundColor(android.graphics.Color.parseColor(newColor.toHexString()));
                tile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Color.getBackgroundColor(tile).equals(GlobalVariables.getChosenColor())) {
                            score.setText(String.valueOf(++currentScore));
                            YoYo.with(Techniques.FadeOutUp).duration(600).playOn(plusOne);
                            if(currentScore%50==0) YoYo.with(Techniques.Tada).duration(600).playOn(score);
                            else if(currentScore%10==0) YoYo.with(Techniques.Pulse).duration(600).playOn(score);
                        } else {
                            if(currentScore-5>=0) currentScore-=5;
                            else if(currentScore>0) currentScore-=1;
                            else currentScore=0;
                            score.setText(String.valueOf(currentScore));
                            YoYo.with(Techniques.FadeOutDown).duration(1500).playOn(minusOne);
                            YoYo.with(Techniques.RubberBand).duration(600).playOn(score);
                            if(currentScore==0) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        gameOverLayout.setAlpha(0f);
                                        gameOverLayout.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.FadeIn).duration(1000).playOn(gameOverLayout);
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                YoYo.with(Techniques.Flash).duration(1500).playOn(tapToPlayAgain);
                                                handler.postDelayed(this,5000);
                                            }
                                        },5000);
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                YoYo.with(Techniques.Bounce).duration(1500).playOn(replay);
                                                handler.postDelayed(this,1500);
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    }
                });

                tileInitiated.set(true);
                handler.postDelayed(this,colorChangeInterval);
            }
        },colorChangeInterval);
        return tile;
    }

    private LinearLayout getTileLayout(int tiles) {
        LinearLayout tileLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tileLayout.setLayoutParams(layoutParams);
        for(int i=0; i<tiles; i++)
            tileLayout.addView(getTile());
        return tileLayout;
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
            parentLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
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
