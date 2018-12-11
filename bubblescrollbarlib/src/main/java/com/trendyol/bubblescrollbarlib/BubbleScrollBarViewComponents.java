package com.trendyol.bubblescrollbarlib;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BubbleScrollBarViewComponents {

    private final ImageView thumb;
    private final View scrollBar;
    private final TextView bubble;
    private RecyclerView recyclerView;

    public BubbleScrollBarViewComponents(ImageView thumb, View scrollBar, TextView bubble) {
        this.thumb = thumb;
        this.scrollBar = scrollBar;
        this.bubble = bubble;
    }

    @Nullable
    public final RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public final ImageView getThumb() {
        return thumb;
    }

    public final View getScrollBar() {
        return scrollBar;
    }

    public final TextView getBubble() {
        return bubble;
    }
}
