package com.example.colors;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.colors.custom.Color;
import com.example.colors.game.GlobalVariables;

/**
 * Created by Zeke on Jul 20, 2016.
 */
public class TileFragment extends Fragment {

    private RelativeLayout tile;
    private Color backgroundColor;

    public static TileFragment newInstance(Color backgroundColor) {
        TileFragment tileFragment = new TileFragment();
        tileFragment.backgroundColor = backgroundColor;
        return tileFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(backgroundColor == null) { Log.e("TileFragment", "newInstance() static method should be called"); return null; }
        View rootView = inflater.inflate(R.layout.fragment_tile, container, false);
        tile = (RelativeLayout) rootView.findViewById(R.id.tile);
        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius(((LevelActivity)getActivity()).pxToDp(0));
        shape.setColor(android.graphics.Color.parseColor(backgroundColor.toHexString()));
        tile.setBackground(shape);
        tile.setClickable(true);
        tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getBackgroundColor().equals(GlobalVariables.getChosenColor())) {
                    YoYo.with(Techniques.ZoomOut).duration(200).playOn(tile);
                    ((LevelActivity) getActivity()).incrementScore();
                }
                else {
                    YoYo.with(Techniques.Flash).duration(1200).playOn(tile);
                    ((LevelActivity) getActivity()).decrementScore();
                }
            }
        });
        return rootView;
    }

    public Color getBackgroundColor() { return backgroundColor; }
}

