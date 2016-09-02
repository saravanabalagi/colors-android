package com.example.colors;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.TypedValue;
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
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Zeke on Jan 03, 2016.
 */
public class LevelActivity extends AppCompatActivity {

    static { AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); }
    public int pxToDp(int pixels) { return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, getResources().getDisplayMetrics()); }

    private int colorChangeIntervalLowerLimit = 2000;
    private int colorChangeIntervalUpperLimit = 3500;
    @Bind(R.id.activity_layout) RelativeLayout activity_layout;
    @Bind(R.id.parent_layout) LinearLayout parentLayout;
    @Bind(R.id.chosenColor) ImageView chosenColor;
    @Bind(R.id.score) TextView score;
    @Bind(R.id.best) TextView best;
    @Bind(R.id.plus_one) TextView plusOne;
    @Bind(R.id.minus_one) TextView minusOne;
    @Bind(R.id.game_over_layout) LinearLayout gameOverLayout;
    @Bind(R.id.tap_to_play_again) TextView tapToPlayAgain;
    @Bind(R.id.game_over) TextView gameOver;
    @Bind(R.id.replay) ImageView replay;
    @Bind(R.id.exit) TextView exit;
    @Bind(R.id.retry) TextView retry;

    Handler handler = new Handler();
    ConcurrentHashMap<Color, Integer> colorsHash = new ConcurrentHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        ButterKnife.bind(this);
        delayedHide(100);

        GlobalVariables.setCurrentLevel(3);
        for (Color color : Color.standardColors) colorsHash.put(color,2);

        System.out.println("Chosen color: "+GlobalVariables.getChosenColor());
        best.setText(String.valueOf(GlobalVariables.getBest(getApplicationContext())));
        score.setText(String.valueOf(GlobalVariables.getScore()));

//        System.out.println("Color hash: "+colorsHash);
//        for (Color color : Color.standardColors) System.out.println(color.toString());
//        printColorHash();

        chosenColor.setColorFilter(android.graphics.Color.parseColor(GlobalVariables.getChosenColor().toHexString()));
        gameOverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGame();
            }
        });
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { showGameOverLayout("You pressed Retry"); }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitGame();
            }
        });
        setLayout();
    }

    private void exitGame() {
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

    private RelativeLayout getTile(){
        final RelativeLayout parentLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams parentLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        parentLayout.setLayoutParams(parentLayoutParams);
        parentLayout.setBackgroundResource(R.drawable.tile_parent_background);

        final FrameLayout borderFrame = new FrameLayout(this);
//        borderFrame.setBackgroundColor(android.graphics.Color.BLUE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        borderFrame.setLayoutParams(layoutParams);
        borderFrame.setBackgroundResource(R.drawable.tile_parent_background);
        int requiredPadding = pxToDp(5);
        borderFrame.setPadding(requiredPadding, requiredPadding, requiredPadding, requiredPadding);

        final FrameLayout tileFrame = new FrameLayout(this);
        tileFrame.setBackgroundColor(android.graphics.Color.BLACK);
        LinearLayout.LayoutParams tileLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tileFrame.setLayoutParams(tileLayoutParams);
        final int tileId = View.generateViewId();
        tileFrame.setId(tileId);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(tileId, TileFragment.newInstance(getNextColor(null)));
        ft.commitAllowingStateLoss();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.setCustomAnimations(getRandomAnimation(), android.R.anim.fade_out);
                TileFragment currentTile = (TileFragment)getSupportFragmentManager().findFragmentById(tileId);
                if(currentTile==null) return;
                ft.replace(tileId,TileFragment.newInstance(getNextColor(currentTile.getBackgroundColor())));
                ft.commitAllowingStateLoss();
                handler.postDelayed(this, (new Random()).nextInt(colorChangeIntervalUpperLimit - colorChangeIntervalLowerLimit +1) + colorChangeIntervalLowerLimit);
            }
        },(new Random()).nextInt(colorChangeIntervalUpperLimit - colorChangeIntervalLowerLimit +1) + colorChangeIntervalLowerLimit);
        parentLayout.addView(tileFrame);
        parentLayout.addView(borderFrame);
        return parentLayout;
    }

    private Color getNextColor(Color previousColor) {
        int colorAvailability;
        Color newColor;
        do {
            int randomNumber = new Random().nextInt(colorsHash.size());
            newColor = new ArrayList<>(colorsHash.keySet()).get(randomNumber);
            colorAvailability = (colorsHash.get(newColor)==null)?0:colorsHash.get(newColor);
        } while (colorAvailability == 0 || (previousColor!=null && newColor.equals(previousColor)));
        colorsHash.put(newColor, colorAvailability-1);
        if(previousColor!=null)
            if(colorsHash.containsKey(previousColor))
                colorsHash.put(previousColor, colorsHash.get(previousColor) + 1);
//        printColorHash();
        return newColor;
    }

    private void printColorHash() {
        System.out.println("Printing Hashmap");
        for (Map.Entry<Color, Integer> entry: colorsHash.entrySet())
            System.out.println(entry.getKey().toString() +" "+ entry.getValue());
    }

    private LinearLayout getTileLayout(int tiles) {
        LinearLayout tileLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        tileLayout.setLayoutParams(layoutParams);
        for(int i=0; i<tiles; i++)
            tileLayout.addView(getTile());
        return tileLayout;
    }

    private int getRandomAnimation() {
        switch ((new Random()).nextInt(4)) {
            case 0: return R.anim.slide_in_from_left;
            case 1: return R.anim.slide_in_from_right;
            case 2: return R.anim.slide_in_from_top;
            case 3: return R.anim.slide_in_from_bottom;
            default: return R.anim.slide_in_from_left;
        }
    }

    public void incrementScore() {
        GlobalVariables.setScore(GlobalVariables.getScore() + 1);
        score.setText(String.valueOf(GlobalVariables.getScore()));
        if(GlobalVariables.getScore()>GlobalVariables.getBest(getApplicationContext())) {
            GlobalVariables.setBest(GlobalVariables.getScore(), getApplicationContext());
            best.setText(String.valueOf(GlobalVariables.getBest(getApplicationContext())));
        }
        YoYo.with(Techniques.FadeOutUp).duration(600).playOn(plusOne);
        if(GlobalVariables.getScore()%50==0) YoYo.with(Techniques.Tada).duration(600).playOn(score);
        else if(GlobalVariables.getScore()%10==0) YoYo.with(Techniques.Pulse).duration(600).playOn(score);
    }

    public void decrementScore() {
        if(GlobalVariables.getScore()-1>=0) GlobalVariables.setScore(GlobalVariables.getScore() - 1);
//        else if(GlobalVariables.getScore()>0) GlobalVariables.setScore(GlobalVariables.getScore() - 1);
        else GlobalVariables.setScore(0);
        score.setText(String.valueOf(GlobalVariables.getScore()));
        YoYo.with(Techniques.FadeOutDown).duration(1500).playOn(minusOne);
        YoYo.with(Techniques.RubberBand).duration(600).playOn(score);
        if(GlobalVariables.getScore()==0) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showGameOverLayout("Game Over");
                }
            });
        }
    }

    private void showGameOverLayout(String textToDisplay) {
        gameOverLayout.setAlpha(0f);
        gameOverLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn).duration(1000).playOn(gameOverLayout);
        gameOver.setText(textToDisplay);
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
