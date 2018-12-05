package com.trendyol.bubblescrollbarlib;

import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

public interface FastScrollLayoutManager {
    void calculateThumbPosition(FastScrollViewComponents viewComponents, Point outThumbPosition);

    void calculateBubblePosition(FastScrollViewComponents viewComponents, Point outBubblePosition);

    FastScroller.ScrollbarState calculateScrollState(@Nullable RecyclerView attachedRecyclerView);

    boolean shouldShowScrollbar(@Nullable RecyclerView attachedRecyclerView);

    int getScrollTarget(MotionEvent event, FastScrollViewComponents viewComponents);

    int getScrolledItemPosition(FastScrollViewComponents viewComponents);

}
