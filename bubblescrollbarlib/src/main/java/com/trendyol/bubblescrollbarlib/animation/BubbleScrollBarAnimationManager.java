package com.trendyol.bubblescrollbarlib.animation;

import android.animation.ValueAnimator;
import com.trendyol.bubblescrollbarlib.BubbleScrollBarViewComponents;

public interface BubbleScrollBarAnimationManager {
    ValueAnimator provideShowBubbleAnimation(BubbleScrollBarViewComponents viewComponents);

    ValueAnimator.AnimatorUpdateListener provideShowBubbleUpdateListener(BubbleScrollBarViewComponents viewComponents);

    ValueAnimator.AnimatorUpdateListener provideHideBubbleUpdateListener(BubbleScrollBarViewComponents viewComponents);

    ValueAnimator provideHideBubbleAnimation(BubbleScrollBarViewComponents viewComponents);

    boolean isBubbleVisible(BubbleScrollBarViewComponents viewComponents);

    boolean isBubbleHidden(BubbleScrollBarViewComponents viewComponents);
}
