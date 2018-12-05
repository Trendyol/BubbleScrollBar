package com.trendyol.bubblescrollbarlib;

import android.animation.ValueAnimator;

public interface FastScrollBubbleAnimationManager {
    ValueAnimator provideShowBubbleAnimation(FastScrollViewComponents viewComponents);

    ValueAnimator.AnimatorUpdateListener provideShowBubbleUpdateListener(FastScrollViewComponents viewComponents);

    ValueAnimator.AnimatorUpdateListener provideHideBubbleUpdateListener(FastScrollViewComponents viewComponents);

    ValueAnimator provideHideBubbleAnimation(FastScrollViewComponents viewComponents);

    boolean isBubbleVisible(FastScrollViewComponents viewComponents);

    boolean isBubbleHidden(FastScrollViewComponents viewComponents);
}
