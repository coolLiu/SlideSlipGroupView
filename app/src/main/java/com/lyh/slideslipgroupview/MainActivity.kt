package com.lyh.slideslipgroupview

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var slideSlipGroupView: SlideSlipGroupView
    private lateinit var tvContent: TextView
    private lateinit var btnClose: Button
    private lateinit var btnOpen: Button
    private lateinit var btnIntent: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        slideSlipGroupView = findViewById(R.id.slideSlipView)
        tvContent = findViewById(R.id.tv_content)
        btnClose = findViewById(R.id.btnClose)
        btnOpen = findViewById(R.id.btnOpen)
        btnIntent = findViewById(R.id.btnIntent)

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

        slideSlipGroupView.setSlideData(arrayListOf(likeBean, collectBean, followBean))

        slideSlipGroupView.menuClickListenerList = { view, menuPosition, menuBean ->
            Toast.makeText(this, "点击了${menuBean.content}", Toast.LENGTH_SHORT).show()
        }

        tvContent.setOnClickListener {
            Toast.makeText(this, "hahahahha", Toast.LENGTH_SHORT).show()
        }
        btnClose.setOnClickListener {
            slideSlipGroupView.closeSlideGroup()
        }
        btnOpen.setOnClickListener {
            slideSlipGroupView.openSlideGroup()
        }
        btnIntent.setOnClickListener {
            val intent = Intent(this, RecyclerListActivity::class.java)
            startActivity(intent)
        }
    }
}