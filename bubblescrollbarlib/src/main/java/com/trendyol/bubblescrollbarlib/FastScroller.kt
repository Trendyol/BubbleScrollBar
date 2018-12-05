package com.trendyol.bubblescrollbarlib

import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.trendyol.bubblescrollbarlib.vertical.VerticalFastScrollBubbleAnimationManager
import com.trendyol.bubblescrollbarlib.vertical.VerticalFastScrollLayoutManager


import com.trendyol.bubblescrollbarlib.FastScroller.ScrollbarState.HIDDEN_BUBBLE
import com.trendyol.bubblescrollbarlib.FastScroller.ScrollbarState.NO_SCROLLBAR
import com.trendyol.bubblescrollbarlib.FastScroller.ScrollbarState.VISIBLE_BUBBLE

class FastScroller : FrameLayout {
    private val thumbPosition = Point()
    private val bubblePosition = Point()
    private val thumbRect = Rect()
    private val trackRect = Rect()

    private var currentScrollbarState = ScrollbarState.HIDDEN_BUBBLE
    private var viewState: FastScrollerViewState? = null

    // Default BubbleAnimationManager
    private var bubbleAnimationManager: FastScrollBubbleAnimationManager = VerticalFastScrollBubbleAnimationManager()
    private var showBubbleAnimation: ValueAnimator? = null
    private var hideBubbleAnimation: ValueAnimator? = null

    // Default LayoutManager
    private var layoutManager: FastScrollLayoutManager = VerticalFastScrollLayoutManager()
    private lateinit var fastScrollViewComponents: FastScrollViewComponents

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onMove()
        }
    }

    private var bubbleText: String?
        get() {
            val targetPosition = layoutManager.getScrolledItemPosition(fastScrollViewComponents)
            return if (targetPosition != RecyclerView.NO_POSITION)
                viewState?.bubbleTextProvider?.provideBubbleText(targetPosition)
            else
                ""
        }
        set(bubbleText) {
            val bubble = fastScrollViewComponents.bubble
            bubble.text = bubbleText
            onBubbleTextChange(bubbleText)
        }

    private fun onBubbleTextChange(newText: String?) {
        if (newText?.isNotEmpty() == true){
            fastScrollViewComponents.bubble.visibility = View.VISIBLE
        }
        else {
            fastScrollViewComponents.bubble.visibility = View.GONE
        }
    }

    constructor(context: Context) : super(context) {
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initializeView()
    }

    private fun onMove() {
        layoutManager.calculateThumbPosition(fastScrollViewComponents, thumbPosition)
        moveThumb()
        layoutManager.calculateBubblePosition(fastScrollViewComponents, bubblePosition)
        moveBubble()
        bubbleText = bubbleText
    }

    private fun initializeView() {
        val root = LayoutInflater.from(context).inflate(R.layout.view_fast_scroller, this, true)
        fastScrollViewComponents = FastScrollViewComponents(root.findViewById<View>(R.id.thumb) as ImageView, root.findViewById(R.id.track), root.findViewById<View>(R.id.bubble) as TextView)
        initializeAnimations()
        post { setScrollState(layoutManager.calculateScrollState(fastScrollViewComponents.recyclerView)) }
        post { this@FastScroller.setInitialBubblePosition() }
    }

    private fun setInitialBubblePosition() {
        layoutManager.calculateBubblePosition(fastScrollViewComponents, bubblePosition)
        moveBubble()
    }

    private fun initializeAnimations() {
        showBubbleAnimation = bubbleAnimationManager.provideShowBubbleAnimation(fastScrollViewComponents)
        hideBubbleAnimation = bubbleAnimationManager.provideHideBubbleAnimation(fastScrollViewComponents)

        val showBubbleUpdateListener = bubbleAnimationManager.provideShowBubbleUpdateListener(fastScrollViewComponents)
        val hideBubbleUpdateListener = bubbleAnimationManager.provideHideBubbleUpdateListener(fastScrollViewComponents)

        showBubbleAnimation?.addUpdateListener(showBubbleUpdateListener)
        hideBubbleAnimation?.addUpdateListener(hideBubbleUpdateListener)
    }

    private fun setScrollState(scrollState: ScrollbarState) {
        this.currentScrollbarState = scrollState
        renderScrollState()
    }

    private fun renderScrollState() {
        when (currentScrollbarState) {
            NO_SCROLLBAR -> onNoScroll()
            VISIBLE_BUBBLE -> onVisibleBubble()
            HIDDEN_BUBBLE -> onHiddenBubble()
        }
    }

    private fun onHiddenBubble() {
        playHideBubbleAnimation()
    }

    private fun onVisibleBubble() {
        playShowBubbleAnimation()
    }

    private fun onNoScroll() {
        visibility = View.GONE
    }

    fun setViewState(viewState: FastScrollerViewState) {
        this.viewState = viewState
        fastScrollViewComponents.track.setBackgroundColor(viewState.trackColor)
        fastScrollViewComponents.thumb.setImageDrawable(viewState.thumbDrawable)
        fastScrollViewComponents.bubble.background = viewState.bubbleDrawable
    }

    private fun moveBubble() {
        val bubble = fastScrollViewComponents.bubble
        bubble.x = bubblePosition.x.toFloat()
        bubble.y = bubblePosition.y.toFloat()

    }

    private fun moveThumb() {
        val thumb = fastScrollViewComponents.thumb
        thumb.x = thumbPosition.x.toFloat()
        thumb.y = thumbPosition.y.toFloat()
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        val attachedRecyclerView = fastScrollViewComponents.recyclerView
        if (attachedRecyclerView != null) {
            destroyCallbacks()
        }
        fastScrollViewComponents.recyclerView = recyclerView
        setupCallbacks()
        post { setScrollState(layoutManager.calculateScrollState(recyclerView)) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var handled = false
        if (shouldStartFastScrolling(event)) {
            handled = true
            setScrollState(VISIBLE_BUBBLE)
        } else if (shouldContinueFastScrolling(event)) {
            fastScrollViewComponents.recyclerView?.scrollBy(0, getScrollTarget(event))
            handled = true
        } else if (shouldEndFastScrolling(event)) {
            handled = true
            setScrollState(HIDDEN_BUBBLE)
        }
        return handled
    }

    private fun shouldContinueFastScrolling(event: MotionEvent): Boolean {
        return event.action == MotionEvent.ACTION_MOVE && isEventInTrackPosition(event)
    }

    private fun shouldStartFastScrolling(event: MotionEvent): Boolean {
        return event.action == MotionEvent.ACTION_DOWN && isEventInThumbPosition(event)
    }

    private fun shouldEndFastScrolling(event: MotionEvent): Boolean {
        return (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) && currentScrollbarState == VISIBLE_BUBBLE
    }

    private fun isEventInTrackPosition(event: MotionEvent): Boolean {
        return Utils.isEventInViewRect(event, fastScrollViewComponents.track, TOUCHABLE_AREA_PADDING, trackRect)
    }

    private fun isEventInThumbPosition(event: MotionEvent): Boolean {
        return Utils.isEventInViewRect(event, fastScrollViewComponents.thumb, TOUCHABLE_AREA_PADDING, thumbRect)
    }

    private fun playHideBubbleAnimation() {
        showBubbleAnimation?.cancel()
        if (bubbleAnimationManager.isBubbleHidden(fastScrollViewComponents) || hideBubbleAnimation?.isRunning == true) {
            return
        }
        hideBubbleAnimation = bubbleAnimationManager.provideHideBubbleAnimation(fastScrollViewComponents)
        hideBubbleAnimation?.addUpdateListener(bubbleAnimationManager.provideHideBubbleUpdateListener(fastScrollViewComponents))
        hideBubbleAnimation?.start()
    }

    private fun playShowBubbleAnimation() {
        hideBubbleAnimation?.cancel()
        if (bubbleAnimationManager.isBubbleVisible(fastScrollViewComponents) || showBubbleAnimation?.isRunning == true) {
            return
        }
        showBubbleAnimation = bubbleAnimationManager.provideShowBubbleAnimation(fastScrollViewComponents)
        showBubbleAnimation?.addUpdateListener(bubbleAnimationManager.provideShowBubbleUpdateListener(fastScrollViewComponents))
        showBubbleAnimation?.start()
    }


    private fun setupCallbacks() {
        fastScrollViewComponents.recyclerView?.addOnScrollListener(onScrollListener)
    }

    private fun destroyCallbacks() {
        fastScrollViewComponents.recyclerView?.removeOnScrollListener(onScrollListener)
    }

    fun getScrollTarget(event: MotionEvent): Int {
        return layoutManager.getScrollTarget(event, fastScrollViewComponents)
    }

    fun setBubbleAnimationManager(bubbleAnimationManager: FastScrollBubbleAnimationManager) {
        this.bubbleAnimationManager = bubbleAnimationManager
    }

    fun setLayoutManager(layoutManager: FastScrollLayoutManager) {
        this.layoutManager = layoutManager
    }

    enum class ScrollbarState {
        NO_SCROLLBAR,
        VISIBLE_BUBBLE,
        HIDDEN_BUBBLE
    }


    object Utils {
        private fun addPadding(paddingDp: Int, outRect: Rect) {
            outRect.left -= paddingDp
            outRect.top -= paddingDp
            outRect.right += paddingDp
            outRect.bottom += paddingDp
        }

        internal fun isEventInViewRect(event: MotionEvent, view: View, padding: Int, outRect: Rect): Boolean {
            val touchX = event.rawX.toInt()
            val touchY = event.rawY.toInt()
            view.getGlobalVisibleRect(outRect)
            Utils.addPadding(padding, outRect)
            return outRect.contains(touchX, touchY)
        }

        internal fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().displayMetrics.density).toInt()
        }
    }

    companion object {

        private val TOUCHABLE_AREA_PADDING = Utils.dpToPx(20)
    }
}
