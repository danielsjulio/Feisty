package com.feisty.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Gil on 11/08/15.
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int spaceVertical;
    private int spaceHorizontal;

    public SpacesItemDecoration(int space) {
        this.spaceHorizontal = space;
        this.spaceVertical = space;
    }

    public SpacesItemDecoration(Context context, @DimenRes int horizontalId, @DimenRes int verticalId) {
        this.spaceVertical = (int) context.getResources().getDimension(horizontalId);
        this.spaceHorizontal = (int) context.getResources().getDimension(verticalId);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spaceHorizontal;
        outRect.right = spaceHorizontal;
        outRect.top = spaceVertical;

        // Add top margin only for the first item to avoid double space between items
        if(parent.getChildLayoutPosition(view) == parent.getChildCount() - 1)
            outRect.bottom = spaceVertical;
    }

}
