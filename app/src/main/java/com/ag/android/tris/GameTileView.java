package com.ag.android.tris;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class GameTileView extends AppCompatImageView {
    public GameTileView(Context context) {
        super(context);
    }

    public GameTileView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameTileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
