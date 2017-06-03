package be.simonraes.webshopui

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import be.simonraes.webshopui.R.id.viewpager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.SnapHelper
import be.simonraes.webshopui.imagetiles.ZigZagLayoutManager


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewpager: ViewPager
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview) as RecyclerView
//        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = ZigZagLayoutManager(this)

        recyclerView.adapter = RecyclerViewAdapater(this)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        viewpager = findViewById(R.id.viewpager) as ViewPager
        val adapter = ViewPagerAdapter (supportFragmentManager)
        viewpager.setAdapter(adapter)

        buttonLeft = findViewById(R.id.button_left) as Button
        buttonLeft.setOnClickListener({
//            viewpager.beginFakeDrag()
//            viewpager.fakeDragBy(-20f)
//            viewpager.endFakeDrag()
        })

        buttonRight = findViewById(R.id.button_right) as Button
        buttonRight.setOnClickListener({
//            viewpager.beginFakeDrag()
//            viewpager.fakeDragBy(20f)
//            viewpager.endFakeDrag()
        })
    }


}
