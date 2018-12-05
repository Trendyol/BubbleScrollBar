package com.trendyol.bubblescrollbarlib.vertical;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import com.trendyol.bubblescrollbarlib.FastScrollLayoutManager;
import com.trendyol.bubblescrollbarlib.FastScrollViewComponents;
import com.trendyol.bubblescrollbarlib.FastScroller;

import static com.trendyol.bubblescrollbarlib.FastScroller.ScrollbarState.HIDDEN_BUBBLE;
import static com.trendyol.bubblescrollbarlib.FastScroller.ScrollbarState.NO_SCROLLBAR;

public class VerticalFastScrollLayoutManager implements FastScrollLayoutManager {

    public static final int STEP_RATIO = 10;

    public VerticalFastScrollLayoutManager() {
    }

    @Override
    public void calculateThumbPosition(FastScrollViewComponents viewComponents, Point outThumbPosition) {
        RecyclerView recyclerView = viewComponents.getRecyclerView();
        if (recyclerView == null) {
            return;
        }
        float verticalScrollRange = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();
        float height = recyclerView.getHeight() - viewComponents.getThumb().getHeight();
        outThumbPosition.set((int) viewComponents.getThumb().getX(), (int) (recyclerView.computeVerticalScrollOffset() * (height / verticalScrollRange)));
    }

    private int getScrollableHeight(@NonNull RecyclerView recyclerView) {
        return recyclerView.getHeight() - recyclerView.getPaddingTop() - recyclerView.getPaddingBottom();
    }

    @Override
    public void calculateBubblePosition(FastScrollViewComponents viewComponents, Point outBubblePosition) {
        outBubblePosition.set((int) viewComponents.getBubble().getX(), Math.max(getThumbCenterY(viewComponents.getThumb()), 0));
    }

    private int getThumbCenterY(View thumb) {
        return (int) thumb.getY() - (thumb.getHeight() / 2);
    }

    @Override
    @FastScroller.ScrollbarState
    public int calculateScrollState(RecyclerView attachedRecyclerView) {
        return shouldShowScrollbar(attachedRecyclerView) ? HIDDEN_BUBBLE : NO_SCROLLBAR;
    }

    @Override
    public boolean shouldShowScrollbar(RecyclerView attachedRecyclerView) {
        return attachedRecyclerView != null
                && attachedRecyclerView.computeVerticalScrollRange() > attachedRecyclerView.getHeight();
    }

    @Override
    public int getScrollTarget(MotionEvent event, FastScrollViewComponents viewComponents) {
        RecyclerView recyclerView = viewComponents.getRecyclerView();
        if (recyclerView == null) {
            return 0;
        }
        double percentage = event.getY() / (float) getScrollableHeight(viewComponents.getRecyclerView());
        percentage = Math.max(0, Math.min(percentage, 1));
        double absoluteScrollTarget = Math.rint((double) getTotalScrollableArea(recyclerView) * percentage);
        int relativeScrollTarget = (int) Math.rint(absoluteScrollTarget - recyclerView.computeVerticalScrollOffset());
        return relativeScrollTarget;
    }

    private int getTotalScrollableArea(RecyclerView recyclerView) {
        return (recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent());
    }

    private double getScrollProgress(FastScrollViewComponents viewComponents) {
        RecyclerView recyclerView = viewComponents.getRecyclerView();
        if (recyclerView == null) {
            return 0;
        }
        float scrolledPosition = viewComponents.getThumb().getY() + (viewComponents.getThumb().getHeight() / 2);
        float percentage = scrolledPosition / (float) getScrollableHeight(viewComponents.getRecyclerView());
        return Math.max(0, Math.min(percentage, 1));
    }

    @Override
    public int getScrolledItemPosition(FastScrollViewComponents viewComponents) {
        RecyclerView recyclerView = viewComponents.getRecyclerView();
        if (recyclerView == null) {
            return 0;
        }
        View itemView = findScrolledView(recyclerView, viewComponents.getThumb());
        return recyclerView.getChildAdapterPosition(itemView);
    }

    @Nullable
    private View findScrolledView(RecyclerView recyclerView, View thumb) {
        View foundView = null;
        if (thumb.getHeight() == 0) {
            return null;
        }
        float scrolledPosition = thumb.getY() + (thumb.getHeight() / 2);
        float searchStepSize = thumb.getHeight() / STEP_RATIO;
        for (int step = 0; scrolledPosition < recyclerView.getHeight()
                && foundView == null; step++) {
            scrolledPosition += step * searchStepSize;
            foundView = recyclerView.findChildViewUnder(recyclerView.getX(), scrolledPosition);
        }
        return foundView;
    }
}
