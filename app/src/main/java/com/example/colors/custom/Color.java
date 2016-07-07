package com.example.colors.custom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Random;

/**
 * Created by Zeke on Jan 04, 2016.
 */

public class Color {
    public int red;
    public int green;
    public int blue;
    public int alpha;

    public Color() { alpha = 255; red = 0; green = 0; blue = 0; }
    public Color(Context context, int colorResource) { absorbFromResource(context, colorResource); }

    static class ColorException extends Exception {
        public ColorException() {}
        public ColorException(String message) { super(message); }
        public ColorException(Throwable cause) { super(cause); }
        public ColorException(String message, Throwable cause) { super(message, cause); }
    }

    public static Color[] getIntermediateColors(Color fromColor, Color toColor, int intermediateColorsRequired) {
        try { if(intermediateColorsRequired<1) { throw new ColorException("Intermediate colors required needs to be at least one"); }
        } catch (ColorException e) { e.printStackTrace(); }
        Color[] color = new Color[intermediateColorsRequired];
        Float[] diff = new Float[3];
        for(int i=0; i<intermediateColorsRequired; i++) {
            color[i] = new Color();
            diff[0] = ((float) toColor.red - (float) fromColor.red) / intermediateColorsRequired * (i+1);
            diff[1] = ((float) toColor.green - (float) fromColor.green) / intermediateColorsRequired * (i+1);
            diff[2] = ((float) toColor.blue - (float) fromColor.blue) / intermediateColorsRequired * (i+1);
            color[i].alpha = 255;
            color[i].red = fromColor.red + diff[0].intValue();
            color[i].green = fromColor.green + diff[1].intValue();
            color[i].blue = fromColor.blue + diff[2].intValue();
        }
        return color;
    }

    public static Color getBackgroundColor(View view) {
        Color color = new Color();
        int backgroundColor = android.graphics.Color.TRANSPARENT;
        Drawable background = view.getBackground();
        try {
            if (background instanceof ColorDrawable) backgroundColor = ((ColorDrawable) background).getColor();
            else throw new ColorException("Background should be of type ColorDrawable");
        } catch (ColorException e) { e.printStackTrace(); return color; }
        color.absorbFromDecimalARGB(backgroundColor);
        return color;
    }

    public void newRandomRGB() {
        red = new Random().nextInt(255);
        green = new Random().nextInt(255);
        blue = new Random().nextInt(255);
        alpha = 255;
    }

    public void newRandomARGB() {
        newRandomRGB();
        alpha = new Random().nextInt(255);
    }

    public Color getRGBInverse() {
        Color color = new Color();
        color.red = 255 - this.red;
        color.green = 255 - this.green;
        color.blue = 255 - this.blue;
        return color;
    }

    public Color getRGBHalfInverse() {
        Color color = new Color();
        color.red = (this.red + 128) % 256;
        color.green = (this.green + 128) % 256;
        color.blue = (this.blue + 128) % 256;
        return color;
    }

    public Color getRGInverse() {
        Color color = new Color();
        color.red = 255 - this.red;
        color.green = 255 - this.green;
        return color;
    }

    public Color getGBInverse() {
        Color color = new Color();
        color.green = 255 - this.green;
        color.blue = 255 - this.blue;
        return color;
    }

    public Color getRBInverse() {
        Color color = new Color();
        color.red = 255 - this.red;
        color.blue = 255 - this.blue;
        return color;
    }

    public Color getRGHalfInverse() {
        Color color = new Color();
        color.red = (this.red + 128) % 256;
        color.green = (this.green + 128) % 256;
        return color;
    }

    public Color getGBHalfInverse() {
        Color color = new Color();
        color.green = (this.green + 128) % 256;
        color.blue = (this.blue + 128) % 256;
        return color;
    }

    public Color getRBHalfInverse() {
        Color color = new Color();
        color.red = (this.red + 128) % 256;
        color.blue = (this.blue + 128) % 256;
        return color;
    }

    public Color getRInverse() {
        Color color = new Color();
        color.red = 255 - this.red;
        return color;
    }

    public Color getGInverse() {
        Color color = new Color();
        color.green = 255 - this.green;
        return color;
    }

    public Color getBInverse() {
        Color color = new Color();
        color.blue = 255 - this.blue;
        return color;
    }

    public Color getRHalfInverse() {
        Color color = new Color();
        color.red = (this.red + 128) % 256;
        return color;
    }

    public Color getGHalfInverse() {
        Color color = new Color();
        color.green = (this.green + 128) % 256;
        return color;
    }

    public Color getBHalfInverse() {
        Color color = new Color();
        color.blue = (this.blue + 128) % 256;
        return color;
    }

    public LinkedHashSet<Color> getVisuallyDistinctColorSet() {
        LinkedHashSet<Color> colorSet = new LinkedHashSet<>();
        colorSet.add(this);
        colorSet.add(this.getRInverse());
        colorSet.add(this.getGInverse());
        colorSet.add(this.getBInverse());
        colorSet.add(this.getRGInverse());
        colorSet.add(this.getGBInverse());
        colorSet.add(this.getRBInverse());
        colorSet.add(this.getRGBInverse());
        return colorSet;
    }

    public LinkedHashSet<Color> getVisuallyDistinctExtendedColorSet() {
        LinkedHashSet<Color> colorSet = getVisuallyDistinctColorSet();
        colorSet.add(this.getRHalfInverse());
        colorSet.add(this.getGHalfInverse());
        colorSet.add(this.getBHalfInverse());
        colorSet.add(this.getRGHalfInverse());
        colorSet.add(this.getGBHalfInverse());
        colorSet.add(this.getRBHalfInverse());
        colorSet.add(this.getRGBHalfInverse());
        return colorSet;
    }

    public void absorb(Color color) { this.alpha = color.alpha; this.red = color.red; this.green = color.green; this.blue = color.blue; }
    public void absorbFromResource(Context context, int colorResource) {
        int color = android.graphics.Color.parseColor(context.getString(colorResource));
        alpha = (color >> 24) & 0xFF;
        red = (color >> 16) & 0xFF;
        green = (color >> 8) & 0xFF;
        blue = (color) & 0xFF;
    }
    public void absorbFromDecimalARGB(int ARGBColor) { this.absorbFromHexARGB("#"+Integer.toHexString(ARGBColor)); }
    public void absorbFromDecimalRGB(int RGBColor) { this.absorbFromHexRGB("#"+Integer.toHexString(RGBColor)); }
    public void absorbFromHexARGB(String hexARGBColor) {
        try {
            if (hexARGBColor.charAt(0) != '#') throw new ColorException("Could not parse " + hexARGBColor + ". Input color should have # at front");
            if (hexARGBColor.length() != 9) throw new ColorException("Could not parse " + hexARGBColor + ". Input color should be of format #AARRGGBB");
        } catch (ColorException e) { e.printStackTrace(); return; }
        alpha = Integer.valueOf(hexARGBColor.substring(1,3), 16);
        red = Integer.valueOf(hexARGBColor.substring(3,5), 16);
        green = Integer.valueOf(hexARGBColor.substring(5,7), 16);
        blue = Integer.valueOf(hexARGBColor.substring(7,9), 16);
    }
    public void absorbFromHexRGB(String hexRGBColor) {
        try {
            if(hexRGBColor.charAt(0)!='#') throw new ColorException("Could not parse "+hexRGBColor+". Input color should have # at front");
            if(hexRGBColor.length()!=7) throw new ColorException("Could not parse "+hexRGBColor+". Input color should be of format #AARRGGBB");
        } catch (ColorException e) { e.printStackTrace(); return; }
        alpha = 255;
        red = Integer.valueOf(hexRGBColor.substring(1,3), 16);
        green = Integer.valueOf(hexRGBColor.substring(3,5), 16);
        blue = Integer.valueOf(hexRGBColor.substring(5,7), 16);
    }

    public String toHexString() {
        return "#"+ String.format("%02X",this.alpha)
                + String.format("%02X",this.red)
                + String.format("%02X",this.green)
                + String.format("%02X",this.blue);
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Color)) return false;
        if (o == this) return true;
        Color c = (Color) o;
        return c.alpha == this.alpha
                && c.red == this.red
                && c.green == this.green
                && c.blue == this.blue;
    }

    @Override
    public int hashCode() {
        int hash = 11;
        hash += 2*this.alpha
                + 3*this.red
                + 5*this.green
                + 7*this.blue;
        return hash;
    }

    public String toString() {
        return "argb,hex("
                  + String.format("%03d",this.alpha) + ","
                  + String.format("%03d",this.red) + ","
                  + String.format("%03d",this.green) + ","
                  + String.format("%03d",this.blue) + ","
                + "#"+ String.format("%02X",this.alpha)
                    + String.format("%02X",this.red)
                    + String.format("%02X",this.green)
                    + String.format("%02X",this.blue) + ")";
    }
}
