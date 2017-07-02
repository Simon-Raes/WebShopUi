package be.simonraes.webshopui

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.RecyclerView
import android.widget.Button
import be.simonraes.webshopui.imagetiles.RhombusItemDecoration
import be.simonraes.webshopui.imagetiles.ZigZagLayoutManager


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: ZigZagLayoutManager
    private lateinit var recyclerAdapter: RecyclerViewAdapater

    private lateinit var viewpager: ViewPager
    private lateinit var buttonLeft: Button
    private lateinit var buttonRight: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerview) as RecyclerView
        layoutManager = ZigZagLayoutManager()
        recyclerView.layoutManager = layoutManager
        recyclerAdapter = RecyclerViewAdapater(this)
        recyclerView.adapter = recyclerAdapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(RhombusItemDecoration(this))

        viewpager = findViewById(R.id.viewpager) as ViewPager
        val adapter = ViewPagerAdapter(supportFragmentManager)
        viewpager.setAdapter(adapter)

        buttonLeft = findViewById(R.id.button_left) as Button
        buttonLeft.setOnClickListener({
            val selectedItem = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (selectedItem > 0) {
                recyclerView.scrollToPosition(selectedItem - 1)
            }

//            viewpager.beginFakeDrag()
//            viewpager.fakeDragBy(-20f)
//            viewpager.endFakeDrag()
        })

        buttonRight = findViewById(R.id.button_right) as Button
        buttonRight.setOnClickListener({
            val selectedItem = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (selectedItem < recyclerAdapter.itemCount - 1) {
                recyclerView.scrollToPosition(selectedItem + 1)
            }
            //            viewpager.beginFakeDrag()
//            viewpager.fakeDragBy(20f)
//            viewpager.endFakeDrag()
        })
    }


}
