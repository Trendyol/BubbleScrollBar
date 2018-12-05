package com.trendyol.bubblescrollbarlib;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FastScrollViewComponents {

    private final ImageView thumb;
    private final View track;
    private final TextView bubble;
    private RecyclerView recyclerView;

    public FastScrollViewComponents(ImageView thumb, View track, TextView bubble) {
        this.thumb = thumb;
        this.track = track;
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

    public final View getTrack() {
        return track;
    }

    public final TextView getBubble() {
        return bubble;
    }
}
