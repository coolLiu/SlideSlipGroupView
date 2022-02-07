package com.lyh.slideslipgroupview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author lyh
 * @Date 2022/1/28
 **/
class ContentAdapter : RecyclerView.Adapter<ContentAdapter.ViewHolder>() {

    var list: ArrayList<AdapterBean> = arrayListOf()
    var slideSlipRecyclerHelper = SlideSlipRecyclerHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.adapter_content_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bean = list[position]
        holder.slideSlipGroupView.menuClickListenerList = { view, menuPosition, menuBean ->
            holder.slideSlipGroupView.closeSlideGroup(true)
            Toast.makeText(holder.itemView.context, "点击了${menuBean.content}  这是第${position + 1}条数据", Toast.LENGTH_SHORT).show()
        }
        if (bean.slideDataList != null) {
            slideSlipRecyclerHelper.bindView(
                bean.slideDataList!!,
                holder.slideSlipGroupView,
                position
            )
        }
        holder.tvContent.text = bean.content
    }

    override fun getItemCount(): Int = list.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slideSlipGroupView: SlideSlipGroupView = itemView.findViewById(R.id.slide_slip_group)
        val tvContent: TextView = itemView.findViewById(R.id.tv_content)
    }
}