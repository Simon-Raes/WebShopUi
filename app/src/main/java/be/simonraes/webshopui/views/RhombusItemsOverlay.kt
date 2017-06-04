package be.simonraes.webshopui.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import be.simonraes.webshopui.R

/**
 * Created by SimonRaes on 04/06/17.
 */
class RhombusItemsOverlay : View {

    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintSlant = Paint(Paint.ANTI_ALIAS_FLAG)

    private var centerX = 0
    private var slantMiddleY = 0
    private var slantLeftBottomX = 0
    private var slantRightBottomX = 0
    private var slantBottomY = 0
    private var slantTopY = 0

    private var pathSlantLeft: Path = Path()
    private var pathSlantRight: Path = Path()

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
        val strokeWidth = resources.getDimension(R.dimen.rhombus_overlay_line_width)
        paintLine.strokeWidth = strokeWidth
        paintLine.color = ContextCompat.getColor(context, R.color.rhombusoverlay_line)

        paintSlant.color = ContextCompat.getColor(context, R.color.rhombusoverlay_slant)
        paintSlant.style = Paint.Style.FILL;
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        centerX = measuredWidth / 2
        // Ugly hardcoded value here. We know tiles are 2/3 the size of the full recycler, so just use the same for the slants.
        slantMiddleY = measuredHeight * 2 / 3
        val remainingMiddleY = measuredHeight - slantMiddleY
        slantLeftBottomX = centerX - remainingMiddleY
        slantRightBottomX = centerX + remainingMiddleY
        val remainingX = centerX - remainingMiddleY
        slantBottomY = measuredHeight - remainingX
        slantTopY = slantBottomY - slantMiddleY

        pathSlantLeft.reset()
        pathSlantLeft.moveTo(0F, slantTopY.toFloat())
        pathSlantLeft.lineTo(0F, slantBottomY.toFloat())
        pathSlantLeft.lineTo(slantLeftBottomX.toFloat(), measuredHeight.toFloat())
        pathSlantLeft.lineTo(centerX.toFloat(), slantMiddleY.toFloat())
        pathSlantLeft.close()

        pathSlantRight.reset()
        pathSlantRight.moveTo(measuredWidth.toFloat(), slantTopY.toFloat())
        pathSlantRight.lineTo(measuredWidth.toFloat(), slantBottomY.toFloat())
        pathSlantRight.lineTo(slantRightBottomX.toFloat(), measuredHeight.toFloat())
        pathSlantRight.lineTo(centerX.toFloat(), slantMiddleY.toFloat())
        pathSlantRight.close()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(pathSlantLeft, paintSlant)
        canvas?.drawPath(pathSlantRight, paintSlant)

        canvas?.drawLine(centerX.toFloat(), 0f, 0f, centerX.toFloat(), paintLine)
        canvas?.drawLine(centerX.toFloat(), 0f, measuredWidth.toFloat(), centerX.toFloat(), paintLine)
    }

}