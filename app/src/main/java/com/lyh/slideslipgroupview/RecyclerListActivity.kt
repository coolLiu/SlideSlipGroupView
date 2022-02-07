package com.lyh.slideslipgroupview

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RecyclerListActivity : AppCompatActivity() {

    private lateinit var mRecyclerContent: RecyclerView
    private val mAdapter = ContentAdapter()

    private val list: ArrayList<AdapterBean> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_list)

        mRecyclerContent = findViewById(R.id.recycler_content)
        mRecyclerContent.layoutManager = LinearLayoutManager(this)
        mRecyclerContent.adapter = mAdapter

        for (i in 0..20) {

            val likeBean = SlideSlipGroupView.SlideDataBuilder()
                .buildContent("点赞")
                .buildTextColor(Color.WHITE)
                .buildBackGroundColor(resources.getColor(R.color.purple_200))
                .buildWidth(200)
                .build()

            val collectBean = SlideSlipGroupView.SlideDataBuilder()
                .buildContent("收藏")
                .buildTextColor(Color.WHITE)
                .buildBackGroundColor(resources.getColor(R.color.teal_200))
                .buildWidth(300)
                .build()

            val followBean = SlideSlipGroupView.SlideDataBuilder()
                .buildContent("订阅")
                .buildTextColor(Color.WHITE)
                .buildBackGroundColor(resources.getColor(R.color.teal_700))
                .buildWidth(200)
                .build()

            val slideList = arrayListOf(likeBean, collectBean, followBean)

            val bean = AdapterBean()
            bean.slideDataList = slideList
            bean.content = "这是第${i + 1}条数据"
            list.add(bean)
        }
        mAdapter.list = list
        mAdapter.slideSlipRecyclerHelper.attachToRecyclerView(mRecyclerContent)
        mAdapter.notifyDataSetChanged()
    }

}