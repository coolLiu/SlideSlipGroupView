package com.lyh.slideslipgroupview

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author lyh
 * @Date 2022/1/28
 **/
class SlideSlipRecyclerHelper {

    private val slideSlipGroupViewList = ArrayList<SlideSlipGroupView>()

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (hasOpenSlideView()) {
                    val view = rv.findChildViewUnder(e.x, e.y)
                    if (view != null) {
                        val position = rv.getChildAdapterPosition(view)
                        if (position == getOpenedSlideViewPosition()) {
                            return false
                        }
                    }
                    closeSlideSlip(true)
                    return true
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })
    }

    fun bindView(
        slideDataList: ArrayList<SlideSlipGroupView.SlideDataBean>,
        slideSlipGroupView: SlideSlipGroupView,
        position: Int
    ) {
        if (!slideSlipGroupViewList.contains(slideSlipGroupView)) {
            slideSlipGroupViewList.add(slideSlipGroupView)
        }
        slideSlipGroupView.positionInRecycler = position
        if (slideSlipGroupView.getSlideData() != null) {
            slideSlipGroupView.closeSlideGroup(false)
        } else {
            slideSlipGroupView.setSlideData(slideDataList)
        }
    }

    fun removeView(slideSlipGroupView: SlideSlipGroupView) {
        slideSlipGroupViewList.remove(slideSlipGroupView)
    }

    /**
     * 是否有展开的view
     */
    fun hasOpenSlideView(): Boolean {
        for (i in slideSlipGroupViewList) {
            if (i.isOpened()) {
                return true
            }
        }
        return false
    }

    fun getOpenedSlideViewPosition(): Int {
        for (i in slideSlipGroupViewList) {
            if (i.isOpened()) {
                return i.positionInRecycler
            }
        }
        return -1
    }

    fun closeSlideSlip(widthAnimator: Boolean) {
        for (i in slideSlipGroupViewList) {
            i.closeSlideGroup(widthAnimator)
        }
    }

}