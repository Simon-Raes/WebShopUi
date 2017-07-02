package be.simonraes.webshopui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Button
import be.simonraes.webshopui.R


/**
 * Button with a left or right pointing triangle shape.
 */
class TriangleButton : Button {

    // todo don't intercept touch events outside the triangle area
    // should be able to scroll the items there

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

        iconDrawable?.let {

            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicWidth);

            // Looks wrong when it's perfectly centered, so shift it a bit away from the point
            val extraXOffset = width / 10
            val extraXOffsetForDirection = if (pointRight) -extraXOffset else extraXOffset

            val xPos = width / 2f - it.intrinsicWidth / 2 + extraXOffsetForDirection
            val yPos = height / 2f - it.intrinsicWidth / 2

            canvas.translate(xPos, yPos)

            it.draw(canvas)
        }
    }


    /*
    * Make only the part with a background respond to touch events.
    * All other touch events should be ignored so the views below can handle them.
    */
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = if (pointRight) event.x else flipXTouchEventForPointLeft(event.x)
        val y = adjustYTouchEventForScale(event.y)

        if (event.y < viewHeight / 2) {
            if (x > y)
                return false
        } else {
            if (x > adjustYTouchEventForBottomHalf(y))
                return false
        }

        return super.onTouchEvent(event)
    }

    /*
    * Adjust the y value to that we can pretend the view is twice as tall as wide.
    * Simplifies the rest of the calculations.
    */
    private fun adjustYTouchEventForScale(yInput: Float) = yInput / height * width * 2

    /*
    * Invert the y value and make it start from 0 if it took place in the bottom half of the view.
    * Makes it easier to compare to the x value.
    */
    private fun adjustYTouchEventForBottomHalf(yInput: Float): Float {
        val viewMidPoint = height / 2
        return viewMidPoint - (yInput - viewMidPoint)
    }

    /*
    * Invert the x value if the triangle is pointing left.
    * Makes it easier to compare to the y value.
    */
    private fun flipXTouchEventForPointLeft(xInput: Float) = width - xInput
}