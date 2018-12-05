package com.trendyol.bubblescrollbarlib;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

public class FastScrollerViewState {
    private final Drawable bubbleDrawable;
    private int thumbWidth;
    private int thumbHeight;
    private int bubbleMargin;

    @ColorInt
    private int thumbColor;
    @ColorInt
    private int trackColor;
    private ColorDrawable thumbDrawable;
    private BubbleTextProvider bubbleTextProvider;

    private FastScrollerViewState(Builder builder) {
        thumbWidth = builder.thumbWidth;
        thumbHeight = builder.thumbHeight;
        thumbColor = builder.thumbColor;
        trackColor = builder.trackColor;
        thumbDrawable = new ColorDrawable(getThumbColor()) {
            @Override
            public int getIntrinsicHeight() {
                return getThumbHeight();
            }

            @Override
            public int getIntrinsicWidth() {
                return getThumbWidth();
            }
        };
        bubbleTextProvider = builder.bubbleTextProvider;
        bubbleDrawable = builder.bubbleDrawable;
        bubbleMargin = builder.bubbleMargin;
    }

    private int getThumbWidth() {
        return thumbWidth;
    }

    private int getThumbHeight() {
        return thumbHeight;
    }

    public ColorDrawable getThumbDrawable() {
        return thumbDrawable;
    }

    public int getThumbColor() {
        return thumbColor;
    }

    public int getBubbleMargin() {
        return bubbleMargin;
    }

    public int getTrackColor() {
        return trackColor;
    }

    public BubbleTextProvider getBubbleTextProvider() {
        return bubbleTextProvider;
    }

    public Drawable getBubbleDrawable() {
        return bubbleDrawable;
    }

    public static final class Builder {
        private int thumbWidth;
        private int thumbHeight;
        private int bubbleMargin;
        @ColorInt
        private int thumbColor;
        @ColorInt
        private int trackColor;
        private BubbleTextProvider bubbleTextProvider;

        private Drawable bubbleDrawable;

        public Builder() {
        }

        public Builder thumbWidth(int val) {
            thumbWidth = val;
            return this;
        }

        public Builder thumbHeight(int val) {
            thumbHeight = val;
            return this;
        }

        public Builder thumbColor(Context context, @ColorRes int val) {
            thumbColor = ContextCompat.getColor(context, val);
            return this;
        }

        public Builder trackColor(Context context, @ColorRes int val) {
            trackColor = ContextCompat.getColor(context, val);
            return this;
        }

        public Builder bubbleDrawable(Context context, @DrawableRes int val) {
            bubbleDrawable = ContextCompat.getDrawable(context, val);
            return this;
        }

        public Builder bubbleMargin(int val) {
            bubbleMargin = val;
            return this;
        }

        public Builder bubbleTextProvider(BubbleTextProvider bubbleTextProvider) {
            this.bubbleTextProvider = bubbleTextProvider;
            return this;
        }

        public FastScrollerViewState build() {
            return new FastScrollerViewState(this);
        }
    }
}
