package com.trendyol.bubblescrollbarlib;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.trendyol.bubblescrollbarlib.databinding.ViewFastScrollerBinding;

public class FastScrollViewComponents {

    private final ViewFastScrollerBinding binding;
    private RecyclerView recyclerView;

    public FastScrollViewComponents(ViewFastScrollerBinding binding) {
        this.binding = binding;
    }

    @Nullable
    public final RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public final View getThumb() {
        return binding.thumb;
    }

    public final View getTrack() {
        return binding.track;
    }

    public final View getBubble() {
        return binding.bubble;
    }
}
