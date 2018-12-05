package com.trendyol.bubblescrollbarlib;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.trendyol.bubblescrollbarlib.databinding.ViewFastScrollerBinding;
import com.trendyol.bubblescrollbarlib.vertical.VerticalFastScrollBubbleAnimationManager;
import com.trendyol.bubblescrollbarlib.vertical.VerticalFastScrollLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.trendyol.bubblescrollbarlib.FastScroller.ScrollbarState.*;

public class FastScroller extends FrameLayout {

    private static final int TOUCHABLE_AREA_PADDING = Utils.dpToPx(20);
    private final Point thumbPosition = new Point();
    private final Point bubblePosition = new Point();
    private final Rect thumbRect = new Rect();
    private final Rect trackRect = new Rect();

    private ViewFastScrollerBinding binding;
    @ScrollbarState
    private int currentScrollbarState = ScrollbarState.HIDDEN_BUBBLE;

    // Default BubbleAnimationManager
    private FastScrollBubbleAnimationManager bubbleAnimationManager = new VerticalFastScrollBubbleAnimationManager();
    private ValueAnimator showBubbleAnimation;
    private ValueAnimator hideBubbleAnimation;

    // Default LayoutManager
    private FastScrollLayoutManager layoutManager = new VerticalFastScrollLayoutManager();
    private FastScrollViewComponents fastScrollViewComponents;

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            onMove();
        }
    };

    public FastScroller(@NonNull Context context) {
        super(context);
        initializeView();
    }

    public FastScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public FastScroller(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FastScroller(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeView();
    }

    private void onMove() {
        layoutManager.calculateThumbPosition(fastScrollViewComponents, thumbPosition);
        moveThumb();
        layoutManager.calculateBubblePosition(fastScrollViewComponents, bubblePosition);
        moveBubble();
        setBubbleText(getBubbleText());
    }

    private void initializeView() {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(getContext()), R.layout.view_fast_scroller, this, true);
        fastScrollViewComponents = new FastScrollViewComponents(binding);
        initializeAnimations();
        post(() -> setScrollState(layoutManager.calculateScrollState(fastScrollViewComponents.getRecyclerView())));
        post(this::setInitialBubblePosition);
    }

    private void setInitialBubblePosition() {
        layoutManager.calculateBubblePosition(fastScrollViewComponents, bubblePosition);
        moveBubble();
    }

    private void initializeAnimations() {
        showBubbleAnimation = bubbleAnimationManager.provideShowBubbleAnimation(fastScrollViewComponents);
        hideBubbleAnimation = bubbleAnimationManager.provideHideBubbleAnimation(fastScrollViewComponents);

        ValueAnimator.AnimatorUpdateListener showBubbleUpdateListener =
                bubbleAnimationManager.provideShowBubbleUpdateListener(fastScrollViewComponents);
        ValueAnimator.AnimatorUpdateListener hideBubbleUpdateListener =
                bubbleAnimationManager.provideHideBubbleUpdateListener(fastScrollViewComponents);

        showBubbleAnimation.addUpdateListener(showBubbleUpdateListener);
        hideBubbleAnimation.addUpdateListener(hideBubbleUpdateListener);
    }

    private void setScrollState(@ScrollbarState int scrollState) {
        this.currentScrollbarState = scrollState;
        renderScrollState();
    }

    private void renderScrollState() {
        switch (currentScrollbarState) {
            case NO_SCROLLBAR:
                onNoScroll();
                break;
            case VISIBLE_BUBBLE:
                onVisibleBubble();
                break;
            case HIDDEN_BUBBLE:
                onHiddenBubble();
                break;
        }
    }

    private void onHiddenBubble() {
        playHideBubbleAnimation();
    }

    private void onVisibleBubble() {
        playShowBubbleAnimation();
    }

    private void onNoScroll() {
        setVisibility(GONE);
    }

    public void setViewState(FastScrollerViewState state) {
        binding.setViewState(state);
        binding.executePendingBindings();
    }

    private void moveBubble() {
        binding.bubble.setX(bubblePosition.x);
        binding.bubble.setY(bubblePosition.y);

    }

    private void moveThumb() {
        binding.thumb.setX(thumbPosition.x);
        binding.thumb.setY(thumbPosition.y);
    }

    private String getBubbleText() {
        int targetPosition = layoutManager.getScrolledItemPosition(fastScrollViewComponents);
        return targetPosition != RecyclerView.NO_POSITION ?
                binding.getViewState().getBubbleTextProvider().provideBubbleText(targetPosition) :
                "";
    }

    public void setBubbleText(String bubbleText) {
        if (bubbleText == null || bubbleText.isEmpty()) {
            binding.bubble.setVisibility(GONE);
        } else {
            binding.bubble.setVisibility(VISIBLE);
        }
        binding.bubble.setText(bubbleText);
    }

    public void attachToRecyclerView(RecyclerView recyclerView) {
        RecyclerView attachedRecyclerView = fastScrollViewComponents.getRecyclerView();
        if (attachedRecyclerView != null) {
            destroyCallbacks();
        }
        fastScrollViewComponents.setRecyclerView(recyclerView);
        setupCallbacks();
        post(() -> setScrollState(layoutManager.calculateScrollState(recyclerView)));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        if (shouldStartFastScrolling(event)) {
            handled = true;
            setScrollState(VISIBLE_BUBBLE);
        } else if (shouldContinueFastScrolling(event)) {
            fastScrollViewComponents.getRecyclerView().scrollBy(0, getScrollTarget(event));
            handled = true;
        } else if (shouldEndFastScrolling(event)) {
            handled = true;
            setScrollState(HIDDEN_BUBBLE);
        }
        return handled;
    }

    private boolean shouldContinueFastScrolling(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_MOVE && isEventInTrackPosition(event);
    }

    private boolean shouldStartFastScrolling(MotionEvent event) {
        return event.getAction() == MotionEvent.ACTION_DOWN && isEventInThumbPosition(event);
    }

    private boolean shouldEndFastScrolling(MotionEvent event) {
        return (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                && currentScrollbarState == VISIBLE_BUBBLE;
    }

    private boolean isEventInTrackPosition(MotionEvent event) {
        return Utils.isEventInViewRect(event, binding.track, TOUCHABLE_AREA_PADDING, trackRect);
    }

    private boolean isEventInThumbPosition(MotionEvent event) {
        return Utils.isEventInViewRect(event, binding.thumb, TOUCHABLE_AREA_PADDING, thumbRect);
    }

    private void playHideBubbleAnimation() {
        showBubbleAnimation.cancel();
        if (bubbleAnimationManager.isBubbleHidden(fastScrollViewComponents) || hideBubbleAnimation.isRunning()) {
            return;
        }
        hideBubbleAnimation = bubbleAnimationManager.provideHideBubbleAnimation(fastScrollViewComponents);
        hideBubbleAnimation.addUpdateListener(bubbleAnimationManager.provideHideBubbleUpdateListener(fastScrollViewComponents));
        hideBubbleAnimation.start();
    }

    private void playShowBubbleAnimation() {
        hideBubbleAnimation.cancel();
        if (bubbleAnimationManager.isBubbleVisible(fastScrollViewComponents) || showBubbleAnimation.isRunning()) {
            return;
        }
        showBubbleAnimation = bubbleAnimationManager.provideShowBubbleAnimation(fastScrollViewComponents);
        showBubbleAnimation.addUpdateListener(bubbleAnimationManager.provideShowBubbleUpdateListener(fastScrollViewComponents));
        showBubbleAnimation.start();
    }


    private void setupCallbacks() {
        fastScrollViewComponents.getRecyclerView().addOnScrollListener(onScrollListener);
    }

    private void destroyCallbacks() {
        fastScrollViewComponents.getRecyclerView().removeOnScrollListener(onScrollListener);
    }

    public int getScrollTarget(MotionEvent event) {
        return layoutManager.getScrollTarget(event, fastScrollViewComponents);
    }

    public void setBubbleAnimationManager(FastScrollBubbleAnimationManager fastScrollBubbleAnimationManager) {
        this.bubbleAnimationManager = bubbleAnimationManager;
    }

    public void setLayoutManager(FastScrollLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @IntDef({
            NO_SCROLLBAR,
            VISIBLE_BUBBLE,
            HIDDEN_BUBBLE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScrollbarState {
        int NO_SCROLLBAR = 0;
        int VISIBLE_BUBBLE = 1;
        int HIDDEN_BUBBLE = 2;
    }


    public static class Utils {
        public static void addPadding(int paddingDp, Rect outRect) {
            outRect.left -= paddingDp;
            outRect.top -= paddingDp;
            outRect.right += paddingDp;
            outRect.bottom += paddingDp;
        }

        public static boolean isEventInViewRect(MotionEvent event, View view, int padding, Rect outRect) {
            int touchX = (int) event.getRawX();
            int touchY = (int) event.getRawY();
            view.getGlobalVisibleRect(outRect);
            Utils.addPadding(padding, outRect);
            return outRect.contains(touchX, touchY);
        }

        public static int dpToPx(int dp) {
            return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
        }
    }
}
