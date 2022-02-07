package com.lyh.slideslipgroupview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.max
import kotlin.properties.Delegates

class SlideSlipGroupView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewGroup(context, attrs) {

    private val configuration by lazy { ViewConfiguration.get(context) }
    private var velocityTracker: VelocityTracker? = null

    private var isIntercept = false

    private var scrollType = -1//滑动方向  0为横向 做处理  1为竖向  -1为没有方向

    private var mStartX = 0
    private var mStartY = 0
    private var mLastX = 0
    private var mLastY = 0
    private var mContentWidth = 0
    private var mAnimator: ValueAnimator? = null
    private var animatorDuration = 200

    private var list: ArrayList<SlideDataBean>? = null
    var positionInRecycler: Int = -1

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount != 1) {
            throw RuntimeException("SlideSlipGroupView在布局文件中需要有且只有一个view用于展示内容区域")
        }
    }

    fun getSlideData(): ArrayList<SlideDataBean>? {
        return list
    }

    fun setSlideData(list: ArrayList<SlideDataBean>) {
        this.list = list
        for (i in childCount - 1 downTo 0) {
            if (i == 0) continue
            removeViewAt(i)
        }
        closeSlideGroup(false)
        for (i in list) {
            val textView = TextView(context)
            textView.text = i.content
            textView.setOnClickListener { i.listener.invoke(textView, this) }
            textView.gravity = Gravity.CENTER
            textView.setTextColor(i.textColor)
            textView.layoutParams =
                FrameLayout.LayoutParams(i.width, FrameLayout.LayoutParams.MATCH_PARENT)
            textView.setBackgroundColor(i.backGroundColor)
            addView(textView)
        }
    }

    fun isClosed(): Boolean {
        return scrollX == 0
    }

    fun isOpened(): Boolean {
        if (measuredWidth == 0) {
            return false
        }
        return scrollX == mContentWidth - measuredWidth
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var layoutRight = right
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (i == 0) {
                childView.layout(left, top, measuredWidth, measuredHeight)
            } else {
                childView.layout(layoutRight, top, layoutRight + childView.measuredWidth, bottom)
                layoutRight += childView.measuredWidth
            }
        }
        mContentWidth = layoutRight
    }

    override fun performClick(): Boolean {
//        Toast.makeText(context, "performClick", Toast.LENGTH_SHORT).show()
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.i("hahahahah", "onTouchEvent")
        if (event == null) return super.onTouchEvent(event)
        acquireVelocityTracker(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clearScrollMsg()
                mStartX = event.x.toInt()
                mStartY = event.y.toInt()
                mLastX = event.x.toInt()
                mLastY = event.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val slidX = mLastX - event.x.toInt()
                mLastX = event.x.toInt()
                mLastY = event.y.toInt()
                if (scrollType == -1) {
                    initScrollType(mStartX, mStartY, mLastX, mLastY)
                }
                if (scrollType == 0) {//横滑时通知父布局不拦截事件
                    parent.requestDisallowInterceptTouchEvent(true)
                    scrollDy(slidX)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (abs(mLastX - mStartX) <= configuration.scaledTouchSlop &&
                    abs(mLastY - mStartY) <= configuration.scaledTouchSlop
                ) {
                    performClick()
                    recyclerVelocityTracker()
                    return true
                }
                velocityTracker!!.computeCurrentVelocity(
                    1000,
                    configuration.scaledMaximumFlingVelocity.toFloat()
                )
                val xVelocity = velocityTracker!!.xVelocity
                if (abs(xVelocity) > 500) {
                    animatorDuration = 100
                    if (xVelocity < 0) {
                        openSlideGroup()
                    } else {
                        closeSlideGroup()
                    }
                } else {
                    val centerScrollX = (mContentWidth - measuredWidth) / 2
                    if (scrollX > centerScrollX) {
                        openSlideGroup()
                    } else {
                        closeSlideGroup()
                    }
                }
                recyclerVelocityTracker()
                clearScrollMsg()
            }
        }

        return true
    }

    private fun acquireVelocityTracker(event: MotionEvent) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
        velocityTracker?.addMovement(event)
    }

    private fun recyclerVelocityTracker() {
        velocityTracker?.clear()
        velocityTracker?.recycle()
        velocityTracker = null
    }

    private fun scrollDy(slidX: Int) {
        when {
            scrollX + slidX < 0 -> {
                scrollTo(0, 0)
            }
            scrollX + slidX > mContentWidth - measuredWidth -> {
                scrollTo(mContentWidth - measuredWidth, 0)
            }
            else -> {
                scrollBy(slidX, 0)
            }
        }
    }

    fun openSlideGroup(withAnimator: Boolean = true) {
        if (scrollX == mContentWidth - measuredWidth) {
            return
        }
        cancelAnimator()
        if (!withAnimator) {
            scrollTo(scrollX, 0)
            return
        }
        val startX = scrollX
        val endX = mContentWidth - measuredWidth
        mAnimator = ValueAnimator.ofInt(startX, endX)
        mAnimator?.duration = animatorDuration.toLong()
        mAnimator?.addUpdateListener {
            scrollTo(it.animatedValue as Int, 0)
        }
        mAnimator?.start()
    }

    fun closeSlideGroup(withAnimator: Boolean = true) {
        if (scrollX == 0) {
            return
        }
        cancelAnimator()
        if (!withAnimator) {
            scrollTo(0, 0)
            return
        }
        val startX = scrollX
        val endX = 0
        mAnimator = ValueAnimator.ofInt(startX, endX)
        mAnimator?.duration = animatorDuration.toLong()
        mAnimator?.addUpdateListener {
            scrollTo(it.animatedValue as Int, 0)
        }
        mAnimator?.start()
    }

    private fun cancelAnimator() {
        mAnimator?.cancel()
        mAnimator = null
    }

    private fun initScrollType(startX: Int, startY: Int, lastX: Int, lastY: Int) {
        if (scrollType != -1) return
        val deltaX = abs(lastX - startX)
        val deltaY = abs(lastY - startY)
        val scrollLength = max(deltaX, deltaY)
        if (scrollLength > configuration.scaledTouchSlop) {
            scrollType = if (deltaX == scrollLength) {
                0
            } else {
                1
            }
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.onInterceptTouchEvent(ev)
        isIntercept = false
        mLastX = ev.x.toInt()
        mLastY = ev.y.toInt()
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                clearScrollMsg()
                mStartX = ev.x.toInt()
                mStartY = ev.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                initScrollType(mStartX, mStartY, mLastX, mLastY)
            }
        }
        Log.i("hahahahah", "scrollType====${scrollType}")
        return scrollType == 0
    }

    private fun clearScrollMsg() {
        animatorDuration = 200
        scrollType = -1
    }

    /**
     *
     */
    class SlideDataBean {
        var content: String by Delegates.notNull()
        var listener: (clickView: View, slideView: SlideSlipGroupView) -> Unit by Delegates.notNull()
        var textColor: Int by Delegates.notNull()
        var backGroundColor: Int by Delegates.notNull()
        var width: Int by Delegates.notNull()
    }

    class SlideDataBuilder {

        private var content: String by Delegates.notNull()
        private var listener: (clickView: View, slideView: SlideSlipGroupView) -> Unit by Delegates.notNull()
        private var textColor: Int by Delegates.notNull()
        private var backGroundColor: Int by Delegates.notNull()
        private var width: Int by Delegates.notNull()

        fun buildContent(content: String): SlideDataBuilder {
            this.content = content
            return this
        }

        fun buildListener(listener: (clickView: View, slideView: SlideSlipGroupView) -> Unit): SlideDataBuilder {
            this.listener = listener
            return this
        }

        fun buildTextColor(color: Int): SlideDataBuilder {
            this.textColor = color
            return this
        }

        fun buildBackGroundColor(color: Int): SlideDataBuilder {
            this.backGroundColor = color
            return this
        }

        fun buildWidth(width: Int): SlideDataBuilder {
            this.width = width
            return this
        }

        fun build(): SlideDataBean {
            val slideDataBean = SlideDataBean()
            slideDataBean.width = width
            slideDataBean.textColor = textColor
            slideDataBean.listener = listener
            slideDataBean.content = content
            slideDataBean.backGroundColor = backGroundColor
            return slideDataBean
        }

    }

}