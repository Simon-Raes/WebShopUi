package be.simonraes.webshopui.imagetiles

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup


/**
 * Created by SimonRaes on 03/06/17.
 */
class ZigZagLayoutManager(context: Context) : RecyclerView.LayoutManager() {

    private val TAG = "ZigZagLayoutManager"

    private var screenCenterX = 0
    private var tileSize = 0

    var firstPosition = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {

        screenCenterX = width / 2


        val parentRight = width - paddingRight
        val oldLeftView = if (childCount > 0) getChildAt(0) else null

        var oldLeft = oldLeftView?.left ?: paddingLeft

        detachAndScrapAttachedViews(recycler)

        var left = oldLeft
        var right: Int
        var top = paddingTop
        var bottom = (height - paddingBottom)

        val availableVerticalSpace = height - paddingTop - paddingBottom

        tileSize = availableVerticalSpace * 2 / 3

        val count = state?.itemCount ?: 0

        val leftOverlap = availableVerticalSpace * 1 / 3

        var i = 0
        while (firstPosition + i < count && left < parentRight) {
            val v = recycler?.getViewForPosition(firstPosition + i)
            if (v != null) {

                addView(v, i)

                measureChildWithMarginsAndDesiredWidthAndHeight(v, tileSize, tileSize)

                right = left + getDecoratedMeasuredWidth(v)

                val xCenter = left + tileSize / 2
                val yOffset = getYOffsetForXPosition(xCenter)

                layoutDecorated(v, left, top + yOffset, right, top + getDecoratedMeasuredHeight(v) + yOffset)

                i++

                left = right// - if (shouldApplyLeftOffset(i)) leftOverlap else 0
            }
        }
    }


    private fun getYOffsetForXPosition(xCenter: Int): Int {

        val simplifiedXCenter = simplifyXPosition(xCenter)
        val simplifiedYCenter = getYPositionForXPosition(simplifiedXCenter)
        val yOffset = (simplifiedYCenter * (tileSize / 2)).toInt()

//        Log.d(TAG, "getYOffsetForXPosition = xCenter:$xCenter -> simplifiedYCenter:$simplifiedYCenter -> yOffset:$yOffset")

        return yOffset
    }

    /**
     * Converts the on screen x position to an x position on the simplified graph where 0 is the center of the screen,
     * 1 is the point where it first reaches its lowest 1 value, 2 is where it's at the highest y again, and so on.
     * */
    private fun simplifyXPosition(xPosition: Int): Float {
//            Log.d(TAG, "simplifyXPosition = xPosition:$xPosition -> ${(xPosition - screenCenterX) / tileSize.toFloat()}")
        return (screenCenterX - xPosition) / (tileSize / 2).toFloat()
    }

    private fun getViewCenterX(view: View) = (view.left + view.right) / 2
    private fun getViewCenterY(view: View) = (view.top + view.bottom) / 2


    private fun shouldApplyLeftOffset(index: Int) = index > 0


    /**
     * Works with values on the simplified values graph, for both x and y.
     * x is 0 at the view's highest y position, x is 1 at the view's lowest position, view is at the highest position again at 2, and so on
     * y 1 is the view's highest position, y 0 is its lowest position
     * */
    private fun getYPositionForXPosition(xPosition: Float): Float {
        val xPos = normaliseXPosition(xPosition)

//        Log.d(TAG, "getYPositionForXPosition = input:$xPosition - normalised:$xPos - result:${1-xPos}")

        return xPos
    }

    private fun normaliseXPosition(xPosition: Float): Float {
        var mutableX = xPosition

        mutableX = Math.abs(mutableX)
        mutableX = mutableX % 2
        if (mutableX > 1)
            mutableX = 2 - mutableX


//        Log.d(TAG, "normaliseXPosition = $xPosition -> $mutableX")

        return mutableX
    }

    fun measureChildWithMarginsAndDesiredWidthAndHeight(child: View, desiredWidth: Int, desiredHeight: Int) {
        val lp = child.getLayoutParams() as RecyclerView.LayoutParams

        // The measureChildWithMargins method uses a private method to get the item decorations, we solve it like this:
        val decorations = Rect()
        calculateItemDecorationsForChild(child, decorations)
        val occupiedWidth = decorations.left + decorations.right
        val occupiedHeight = decorations.top + decorations.bottom


        val widthSpec = RecyclerView.LayoutManager.getChildMeasureSpec(width,
                widthMode,
                paddingLeft + paddingRight + lp.leftMargin + lp.rightMargin + occupiedWidth,
                desiredWidth - occupiedWidth,
                canScrollHorizontally())
        // The standard measureChildWithMargins method uses the height from the view's LayoutParams, but we want to be able to supply our own, calculated height.
        val heightSpec = RecyclerView.LayoutManager.getChildMeasureSpec(height,
                heightMode,
                paddingTop + paddingBottom + lp.topMargin + lp.bottomMargin + occupiedHeight,
                desiredHeight - occupiedHeight,
                canScrollVertically())

        child.measure(widthSpec, heightSpec)
    }

    override fun canScrollHorizontally() = true

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {

        val centerXBefore = getViewCenterX(getChildAt(0))
        val yOffsetBefore = getYOffsetForXPosition(centerXBefore)

        getChildAt(0).offsetLeftAndRight(-dx)


        val centerX = getViewCenterX(getChildAt(0))
        val yOffset = getYOffsetForXPosition(centerX)

        //TODO("calculate the distance we need to move them up or down, then do that")
//        getChildAt(0).top
        getChildAt(0).offsetTopAndBottom(yOffset - yOffsetBefore)

        return dx
    }

    private fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler?) {

    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }
}