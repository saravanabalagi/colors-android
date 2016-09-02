package com.example.colors.game;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.colors.custom.Color;

/**
 * Created by Zeke on Jan 04, 2016.
 */
public class GlobalVariables {

    private static Color chosenColor;
    private static int currentLevel;
    private static int score = 0;

    public static Color getChosenColor() { return chosenColor; }
    public static int getCurrentLevel() { return currentLevel; }
    public static int getScore() { return score; }
    public static int getBest(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("scores",Context.MODE_PRIVATE);
        return sharedPreferences.getInt("best_score",0);
    }

    public static void setChosenColor(Color chosenColor) { GlobalVariables.chosenColor = chosenColor; }
    public static void setCurrentLevel(int currentLevel) { GlobalVariables.currentLevel = currentLevel; }

    public static void setScore(int score) { GlobalVariables.score = score; }
    public static void setBest(int best, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("scores",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("best_score",best);
        editor.apply();
    }

}
