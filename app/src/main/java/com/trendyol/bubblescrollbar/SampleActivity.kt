package com.trendyol.bubblescrollbar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.trendyol.bubblescrollbarlib.FastScrollerViewState
import com.trendyol.bubblescrollbarlib.vertical.VerticalFastScrollBubbleAnimationManager
import com.trendyol.bubblescrollbarlib.vertical.VerticalFastScrollLayoutManager
import kotlinx.android.synthetic.main.activity_sample.*

class SampleActivity : AppCompatActivity() {

    val sampleAdapter = SampleAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        recyclerView.apply {
            adapter = sampleAdapter
            layoutManager = LinearLayoutManager(this@SampleActivity)
        }

        fastscroll.apply {
            attachToRecyclerView(recyclerView)
            setBubbleAnimationManager(VerticalFastScrollBubbleAnimationManager())
            setLayoutManager(VerticalFastScrollLayoutManager())
            setViewState(getFastScrollViewState())
        }
    }

    private fun getFastScrollViewState(): FastScrollerViewState = FastScrollerViewState.Builder()
        .bubbleTextProvider(sampleAdapter)
        .thumbColor(this, R.color.colorAccent)
        .trackColor(this, android.R.color.darker_gray)
        .thumbHeight(resources.getDimension(R.dimen.thumb_height).toInt())
        .thumbWidth(resources.getDimension(R.dimen.thumb_width).toInt())
        .bubbleDrawable(this, R.drawable.fast_scroll_thumb)
        .bubbleMargin(resources.getDimension(R.dimen.bubble_margin).toInt())
        .build()
}