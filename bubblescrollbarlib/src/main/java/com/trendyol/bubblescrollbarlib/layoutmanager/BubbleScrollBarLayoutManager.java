package com.trendyol.bubblescrollbarlib.layoutmanager;

import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import com.trendyol.bubblescrollbarlib.BubbleScrollbarState;
import com.trendyol.bubblescrollbarlib.BubbleScrollBarViewComponents;

public interface BubbleScrollBarLayoutManager {
    void calculateThumbPosition(BubbleScrollBarViewComponents viewComponents, Point outThumbPosition);

    void calculateBubblePosition(BubbleScrollBarViewComponents viewComponents, Point outBubblePosition);

    BubbleScrollbarState calculateScrollState(@Nullable RecyclerView attachedRecyclerView);

    boolean shouldShowScrollbar(@Nullable RecyclerView attachedRecyclerView);

    int getScrollTarget(MotionEvent event, BubbleScrollBarViewComponents viewComponents);

    int getScrolledItemPosition(BubbleScrollBarViewComponents viewComponents);

}
