package com.trendyol.bubblescrollbarlib.vertical;

import android.animation.ValueAnimator;
import com.trendyol.bubblescrollbarlib.FastScrollBubbleAnimationManager;
import com.trendyol.bubblescrollbarlib.FastScrollViewComponents;

public class VerticalFastScrollBubbleAnimationManager implements FastScrollBubbleAnimationManager {

    public static final int HIDE_ANIMATION_START_DELAY = 350;
    private static final int VISIBLE_ALPHA = 1;
    private static final int HIDDEN_ALPHA = 0;
    private ValueAnimator.AnimatorUpdateListener updateListener = null;

    public VerticalFastScrollBubbleAnimationManager() {
    }


    @Override
    public ValueAnimator provideShowBubbleAnimation(FastScrollViewComponents viewComponents) {
        return ValueAnimator.ofFloat(HIDDEN_ALPHA, VISIBLE_ALPHA);
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener provideShowBubbleUpdateListener(FastScrollViewComponents viewComponents) {
        if (updateListener == null) {
            updateListener = animation -> {
                float animatedValue = (float) animation.getAnimatedValue();
                viewComponents.getBubble().setAlpha(animatedValue);
                viewComponents.getBubble().setScaleX(animatedValue);
                viewComponents.getBubble().setScaleY(animatedValue);
            };
        }
        return updateListener;
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener provideHideBubbleUpdateListener(FastScrollViewComponents viewComponents) {
        return provideShowBubbleUpdateListener(viewComponents);
    }

    @Override
    public ValueAnimator provideHideBubbleAnimation(FastScrollViewComponents viewComponents) {
        ValueAnimator hideAnimation = ValueAnimator.ofFloat(VISIBLE_ALPHA, HIDDEN_ALPHA);
        hideAnimation.setStartDelay(HIDE_ANIMATION_START_DELAY);
        return hideAnimation;
    }

    @Override
    public boolean isBubbleVisible(FastScrollViewComponents viewComponents) {
        return viewComponents.getBubble().getAlpha() == VISIBLE_ALPHA;
    }

    @Override
    public boolean isBubbleHidden(FastScrollViewComponents viewComponents) {
        return viewComponents.getBubble().getAlpha() == HIDDEN_ALPHA;
    }
}
