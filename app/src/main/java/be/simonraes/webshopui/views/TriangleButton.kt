package be.simonraes.webshopui.views

import android.content.Context
import android.util.AttributeSet
import android.widget.Button

/**
 * Created by SimonRaes on 01/07/17.
 */
class TriangleButton : Button {

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

    }
}