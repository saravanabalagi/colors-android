package com.example.colors.game;

import com.example.colors.custom.Color;

/**
 * Created by Zeke on Jan 04, 2016.
 */
public class GlobalVariables {
    private static Color chosenColor;
    private static int currentLevel;

    public static Color getChosenColor() { return chosenColor; }
    public static int getCurrentLevel() { return currentLevel; }

    public static void setChosenColor(Color chosenColor) { GlobalVariables.chosenColor = chosenColor; }
    public static void setCurrentLevel(int currentLevel) { GlobalVariables.currentLevel = currentLevel; }
}
