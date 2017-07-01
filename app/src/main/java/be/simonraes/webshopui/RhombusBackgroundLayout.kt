package be.simonraes.webshopui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.FrameLayout


/**
 * ViewGroup that clips its content to a Rhombus shape.
 */
class RhombusBackgroundLayout : FrameLayout {

    private val paintClip = Paint(Paint.ANTI_ALIAS_FLAG)

    private val porterDuffXfermodeClear: PorterDuffXfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private lateinit var path: Path

    private var layoutWidth = 0f
    private var layoutHeight = 0f

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
        paintClip.xfermode = porterDuffXfermodeClear
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        layoutWidth = measuredWidth.toFloat()
        layoutHeight = measuredHeight.toFloat()
        createClipPath()
    }

    private fun createClipPath() {
        path = Path()

        val pathTopLeft = Path()
        pathTopLeft.lineTo(0f, layoutHeight / 2)
        pathTopLeft.lineTo(layoutWidth / 2, 0f);
        pathTopLeft.close()
        path.addPath(pathTopLeft)

        val pathBottomLeft = Path()
        pathBottomLeft.moveTo(0f, layoutHeight / 2)
        pathBottomLeft.lineTo(layoutWidth / 2, layoutHeight);
        pathBottomLeft.lineTo(0f, layoutHeight)
        pathBottomLeft.close()
        path.addPath(pathBottomLeft)

        val pathTopRight = Path()
        pathTopRight.moveTo(layoutWidth / 2, 0f)
        pathTopRight.lineTo(layoutWidth, layoutHeight / 2);
        pathTopRight.lineTo(layoutWidth, 0f)
        pathTopRight.close()
        path.addPath(pathTopRight)

        val pathBottomRight = Path()
        pathBottomRight.moveTo(layoutWidth / 2, layoutHeight)
        pathBottomRight.lineTo(layoutWidth, layoutHeight);
        pathBottomRight.lineTo(layoutWidth, layoutHeight / 2)
        pathBottomRight.close()
        path.addPath(pathBottomRight)
    }


    override fun dispatchDraw(canvas: Canvas) {

        val saveCount = canvas.saveLayer(0f, 0f, layoutWidth, layoutHeight, null, Canvas.ALL_SAVE_FLAG)

        super.dispatchDraw(canvas)

        canvas.drawPath(path, paintClip)
        canvas.restoreToCount(saveCount)
    }
}