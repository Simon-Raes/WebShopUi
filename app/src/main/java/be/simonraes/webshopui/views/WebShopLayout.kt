package be.simonraes.webshopui.views

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import be.simonraes.webshopui.R

/**
 * Lays out the left/right buttons in the right place. Rest of the layout is done through standard ConstraintLayout XML.
 */
class WebShopLayout : ConstraintLayout {


    private lateinit var recyclerView: View
    private lateinit var buttonLeft: View
    private lateinit var buttonRight: View

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

        LayoutInflater.from(context).inflate(R.layout.layout_main, this)

        recyclerView = findViewById(R.id.recyclerview)
        buttonLeft = findViewById(R.id.button_left)
        buttonRight = findViewById(R.id.button_right)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val buttonWidth = recyclerView.measuredWidth / 2 - recyclerView.measuredWidth / 3
        val buttonHeight = buttonWidth * 2

        val widthSpec = MeasureSpec.makeMeasureSpec(buttonWidth, MeasureSpec.EXACTLY)
        val heightSpec = MeasureSpec.makeMeasureSpec(buttonHeight, MeasureSpec.EXACTLY)
        buttonLeft.measure(widthSpec, heightSpec)
        buttonRight.measure(widthSpec, heightSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)


        buttonLeft.layout(0,
                recyclerView.bottom - buttonLeft.measuredHeight / 2,
                buttonLeft.measuredWidth,
                recyclerView.bottom + buttonLeft.measuredHeight / 2)

        buttonRight.layout(recyclerView.right - buttonRight.measuredWidth,
                recyclerView.bottom - buttonRight.measuredHeight / 2,
                recyclerView.right,
                recyclerView.bottom + buttonRight.measuredHeight / 2)
    }
}