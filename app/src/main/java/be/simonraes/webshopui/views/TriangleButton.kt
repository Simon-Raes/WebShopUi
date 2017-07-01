package be.simonraes.webshopui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.Button
import be.simonraes.webshopui.R


/**
 * Button with a left or right pointing triangle shape.
 */
class TriangleButton : Button {

    private val paintBackground = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var path: Path

    var viewWidth = 0f
    var viewHeight = 0f

    private var pointRight = true
    private var iconDrawable: Drawable? = null

    constructor(context: Context?) : super(context) {
        init(null)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        background = null

        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TriangleButton,
                0, 0)

        try {
            pointRight = a.getBoolean(R.styleable.TriangleButton_tb_direction, true)
            paintBackground.color = a.getColor(R.styleable.TriangleButton_tb_color, Color.RED)
            iconDrawable = a.getDrawable(R.styleable.TriangleButton_tb_icon)
        } finally {
            a.recycle()
        }

        if (iconDrawable != null) {
            println("kool")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        viewWidth = measuredWidth.toFloat()
        viewHeight = measuredHeight.toFloat()
        createBackgroundPath()
    }

    private fun createBackgroundPath() {
        path = Path()

        if (pointRight) {
            path.lineTo(0f, viewHeight)
            path.lineTo(viewWidth, viewHeight / 2);
        } else {
            path.moveTo(viewWidth, 0f)

            path.lineTo(0f, viewHeight / 2)
            path.lineTo(viewWidth, viewHeight);
        }



        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawPath(path, paintBackground)


//        todo clean up ugly code!!
        iconDrawable?.setBounds(0, 0, iconDrawable!!.intrinsicWidth, iconDrawable!!.intrinsicWidth);

        canvas.translate(width / 2f - iconDrawable!!.intrinsicWidth / 2, height / 2f - iconDrawable!!.intrinsicWidth / 2)
        iconDrawable?.draw(canvas)
    }
}