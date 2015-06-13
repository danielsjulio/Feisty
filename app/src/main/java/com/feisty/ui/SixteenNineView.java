package com.feisty.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Gil on 13/06/15.
 */
public class SixteenNineView extends ImageView {

    private static final double RATIO = 0.56;

    public SixteenNineView(Context context) {
        super(context);
    }

    public SixteenNineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SixteenNineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, (int) (width * RATIO));
    }
}