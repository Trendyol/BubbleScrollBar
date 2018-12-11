package com.trendyol.bubblescrollbarlib.animation;

import android.animation.ValueAnimator;
import com.trendyol.bubblescrollbarlib.BubbleScrollBarViewComponents;

public class VerticalBubbleScrollBarAnimationManager implements BubbleScrollBarAnimationManager {

    public static final int HIDE_ANIMATION_START_DELAY = 350;
    private static final int VISIBLE_ALPHA = 1;
    private static final int HIDDEN_ALPHA = 0;
    private ValueAnimator.AnimatorUpdateListener updateListener = null;

    public VerticalBubbleScrollBarAnimationManager() {
    }

    @Override
    public ValueAnimator provideShowBubbleAnimation(BubbleScrollBarViewComponents viewComponents) {
        return ValueAnimator.ofFloat(HIDDEN_ALPHA, VISIBLE_ALPHA);
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener provideShowBubbleUpdateListener(final BubbleScrollBarViewComponents viewComponents) {
        if (updateListener == null) {
            updateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float animatedValue = (float) animation.getAnimatedValue();
                    viewComponents.getBubble().setAlpha(animatedValue);
                    viewComponents.getBubble().setScaleX(animatedValue);
                    viewComponents.getBubble().setScaleY(animatedValue);
                }
            };
        }
        return updateListener;
    }

    @Override
    public ValueAnimator.AnimatorUpdateListener provideHideBubbleUpdateListener(BubbleScrollBarViewComponents viewComponents) {
        return provideShowBubbleUpdateListener(viewComponents);
    }

    @Override
    public ValueAnimator provideHideBubbleAnimation(BubbleScrollBarViewComponents viewComponents) {
        ValueAnimator hideAnimation = ValueAnimator.ofFloat(VISIBLE_ALPHA, HIDDEN_ALPHA);
        hideAnimation.setStartDelay(HIDE_ANIMATION_START_DELAY);
        return hideAnimation;
    }

    @Override
    public boolean isBubbleVisible(BubbleScrollBarViewComponents viewComponents) {
        return viewComponents.getBubble().getAlpha() == VISIBLE_ALPHA;
    }

    @Override
    public boolean isBubbleHidden(BubbleScrollBarViewComponents viewComponents) {
        return viewComponents.getBubble().getAlpha() == HIDDEN_ALPHA;
    }
}
