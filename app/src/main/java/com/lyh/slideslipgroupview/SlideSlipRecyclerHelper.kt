package com.lyh.slideslipgroupview

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author lyh
 * @Date 2022/1/28
 *
 * 由于recyclerview的服用机制，导致滑动到其他的条目会展示之前的状态
 * 创建一个helper解决这个问题
 **/
class SlideSlipRecyclerHelper {

    private val slideSlipGroupViewList = ArrayList<SlideSlipGroupView>()

    /**
     * 需要触摸屏幕时关闭已展开的菜单时使用，使用该方法的作用域仅为recyclerview布局内
     */
    fun attachToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (rv.scrollState == RecyclerView.SCROLL_STATE_DRAGGING) {//如果recyclerview处于滑动状态  不处理  防止recyclerview快速滑动时手指在其他view上导致开启状态下的view关闭
                    return false
                }
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


    /**
     * 在适配器中bind时调用
     * TODO 适配item不一样的情况
     */
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