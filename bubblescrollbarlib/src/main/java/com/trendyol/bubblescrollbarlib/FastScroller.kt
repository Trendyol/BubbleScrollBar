package com.trendyol.bubblescrollbarlib


import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
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
import com.trendyol.common.getDimensionOrDefaultInPixelSize
import com.trendyol.bubblescrollbarlib.BubbleScrollbarState.*
import com.trendyol.common.addPadding
import com.trendyol.common.dpToPx
import com.trendyol.common.isInViewRect

class FastScroller : FrameLayout {
    private val thumbPosition = Point()
    private val bubblePosition = Point()
    private val thumbRect = Rect()
    private val trackRect = Rect()

    private var currentScrollbarState = BubbleScrollbarState.HIDDEN_BUBBLE

    // Default BubbleAnimationManager
    private var bubbleAnimationManager: FastScrollBubbleAnimationManager = VerticalFastScrollBubbleAnimationManager()
    private var showBubbleAnimation: ValueAnimator? = null
    private var hideBubbleAnimation: ValueAnimator? = null

    // Default LayoutManager
    private var layoutManager: FastScrollLayoutManager = VerticalFastScrollLayoutManager()
    private lateinit var fastScrollViewComponents: FastScrollViewComponents

    var bubbleTextProvider: BubbleTextProvider? = null

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
                bubbleTextProvider?.provideBubbleText(targetPosition)
            else
                ""
        }
        set(bubbleText) {
            val bubble = fastScrollViewComponents.bubble
            bubble.text = bubbleText
            onBubbleTextChange(bubbleText)
        }

    private fun onBubbleTextChange(newText: String?) {
        if (newText?.isNotEmpty() == true) {
            fastScrollViewComponents.bubble.visibility = View.VISIBLE
        } else {
            fastScrollViewComponents.bubble.visibility = View.GONE
        }
    }

    constructor(context: Context) : super(context) {
        initializeView()
        obtainStyledAttributes()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initializeView()
        obtainStyledAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initializeView()
        obtainStyledAttributes(attrs, defStyleAttr)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        initializeView()
        obtainStyledAttributes(attrs, defStyleAttr, defStyleRes)
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
        fastScrollViewComponents = FastScrollViewComponents(
            root.findViewById<View>(R.id.thumb) as ImageView,
            root.findViewById(R.id.track),
            root.findViewById<View>(R.id.bubble) as TextView
        )
        initializeAnimations()
        post { setScrollState(layoutManager.calculateScrollState(fastScrollViewComponents.recyclerView)) }
        post { this@FastScroller.setInitialBubblePosition() }
    }

    private var bubbleElevation = 0f
    private var bubbleMargin: Int = 0


    private var bubblePadding = 0
    private var bubbleTextSize = 0f

    private var bubbleTextColor: Int = 0

    private var bubbleMinWidth = 0
    private var bubbleHeight = 0

    private var trackBackground: Drawable? = null

    private var bubbleBackground: Drawable? = null

    private var thumbBackground: Drawable? = null

    private fun obtainStyledAttributes(attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) {
        with(context.theme.obtainStyledAttributes(attrs, R.styleable.FastScroller, defStyleAttr, defStyleRes)) {
            thumbBackground = getDrawable(R.styleable.FastScroller_thumbBackground)
            bubbleBackground = getDrawable(R.styleable.FastScroller_bubbleBackground)
            trackBackground = getDrawable(R.styleable.FastScroller_scrollbarBackground)
            bubbleElevation = getDimension(
                R.styleable.FastScroller_bubbleElevation,
                resources.getDimension(R.dimen.default_bubble_elevation)
            )
            bubbleTextSize = getDimension(
                R.styleable.FastScroller_bubbleTextSize,
                resources.getDimension(R.dimen.default_bubble_text_size)
            )
            bubbleTextColor = getColor(
                R.styleable.FastScroller_bubbleTextColor,
                ContextCompat.getColor(context, R.color.default_bubble_text_color)
            )
            bubbleMargin = getDimensionOrDefaultInPixelSize(
                R.styleable.FastScroller_bubbleMargin,
                R.dimen.default_bubble_margin
            )
            bubblePadding = getDimensionOrDefaultInPixelSize(
                R.styleable.FastScroller_bubblePadding,
                R.dimen.default_bubble_padding
            )
            bubbleMinWidth = getDimensionOrDefaultInPixelSize(
                R.styleable.FastScroller_bubbleMinWidth,
                R.dimen.default_bubble_min_width
            )
            bubbleHeight = getDimensionOrDefaultInPixelSize(
                R.styleable.FastScroller_bubbleHeight,
                R.dimen.default_bubble_height
            )
            return@with
        }

        with(fastScrollViewComponents) {
            bubble.setPadding(bubblePadding, bubblePadding, bubblePadding, bubblePadding)
            bubble.layoutParams.height = bubbleHeight
            thumb.layoutParams.height = bubbleHeight
            thumb.layoutParams.width = dpToPx(5)
            ((bubble.layoutParams) as? MarginLayoutParams)?.marginEnd = bubbleMargin
            bubble.minWidth = bubbleMinWidth
            bubble.setTextColor(bubbleTextColor)
            bubble.textSize = bubbleTextSize
            ViewCompat.setElevation(bubble, bubbleElevation)
            track.background = trackBackground
            thumb.setImageDrawable(thumbBackground)
            bubble.background = bubbleBackground
        }
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

    private fun setScrollState(scrollStateBubble: BubbleScrollbarState) {
        this.currentScrollbarState = scrollStateBubble
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

    private fun isEventInTrackPosition(event: MotionEvent): Boolean =
        event.isInViewRect(fastScrollViewComponents.track, TOUCHABLE_AREA_PADDING, trackRect)

    private fun isEventInThumbPosition(event: MotionEvent): Boolean =
        event.isInViewRect(fastScrollViewComponents.thumb, TOUCHABLE_AREA_PADDING, thumbRect)

    private fun playHideBubbleAnimation() {
        showBubbleAnimation?.cancel()
        if (bubbleAnimationManager.isBubbleHidden(fastScrollViewComponents) || hideBubbleAnimation?.isRunning == true) {
            return
        }
        hideBubbleAnimation = bubbleAnimationManager.provideHideBubbleAnimation(fastScrollViewComponents)
        hideBubbleAnimation?.addUpdateListener(
            bubbleAnimationManager.provideHideBubbleUpdateListener(
                fastScrollViewComponents
            )
        )
        hideBubbleAnimation?.start()
    }

    private fun playShowBubbleAnimation() {
        hideBubbleAnimation?.cancel()
        if (bubbleAnimationManager.isBubbleVisible(fastScrollViewComponents) || showBubbleAnimation?.isRunning == true) {
            return
        }
        showBubbleAnimation = bubbleAnimationManager.provideShowBubbleAnimation(fastScrollViewComponents)
        showBubbleAnimation?.addUpdateListener(
            bubbleAnimationManager.provideShowBubbleUpdateListener(
                fastScrollViewComponents
            )
        )
        showBubbleAnimation?.start()
    }

    private fun setupCallbacks() = fastScrollViewComponents.recyclerView?.addOnScrollListener(onScrollListener)

    private fun destroyCallbacks() = fastScrollViewComponents.recyclerView?.removeOnScrollListener(onScrollListener)

    private fun getScrollTarget(event: MotionEvent): Int =
        layoutManager.getScrollTarget(event, fastScrollViewComponents)

    fun setBubbleAnimationManager(bubbleAnimationManager: FastScrollBubbleAnimationManager) {
        this.bubbleAnimationManager = bubbleAnimationManager
    }

    fun setLayoutManager(layoutManager: FastScrollLayoutManager) {
        this.layoutManager = layoutManager
    }

    companion object {
        private val TOUCHABLE_AREA_PADDING = dpToPx(20)
    }
}
