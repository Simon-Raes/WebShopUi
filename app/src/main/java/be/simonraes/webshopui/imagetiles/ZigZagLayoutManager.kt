package be.simonraes.webshopui.imagetiles

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
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
        val availableVerticalSpace = height - paddingTop - paddingBottom

        tileSize = availableVerticalSpace * 2 / 3

        val parentRight = width - paddingRight
        val oldLeftView = if (childCount > 0) getChildAt(0) else null

        val oldLeft = oldLeftView?.left ?: paddingLeft + getLeftToMakeFirstViewStartCentered()

        detachAndScrapAttachedViews(recycler)

        var left = oldLeft
        var right: Int

        val count = state?.itemCount ?: 0

        val leftOverlap = availableVerticalSpace * 1 / 3

        var i = 0
        while (firstPosition + i < count && left < parentRight) {
            val v = recycler?.getViewForPosition(firstPosition + i)
            if (v != null) {

                right = left + tileSize

                addNewView(v, left, right)

                i++

                left = right - if (shouldApplyLeftOffset(i)) leftOverlap else 0
            }
        }
    }

    private fun getLeftToMakeFirstViewStartCentered(): Int {
        return screenCenterX - tileSize / 2
    }


    private fun getYOffsetForXPosition(xCenter: Int): Int {

        val simplifiedXCenter = simplifyXPosition(xCenter)
        val simplifiedYCenter = getYPositionForXPosition(simplifiedXCenter)
        val yOffset = (simplifiedYCenter * (tileSize / 2)).toInt()

        return yOffset
    }

    /**
     * Converts the on screen x position to an x position on the simplified graph where 0 is the center of the screen,
     * 1 is the point where it first reaches its lowest 1 value, 2 is where it's at the highest y again, and so on.
     * */
    private fun simplifyXPosition(xPosition: Int): Float {
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

        if (childCount == 0) {
            return 0
        }


        // todo proper padding and decorations support


        // fixme let the list scroll a bit farther than normally for the very first and last views
        // we want to scroll them up to their selected position

        var scrolled = 0
        val top = paddingTop
        if (dx < 0) {

            while (scrolled > dx) {
                val firstView = getChildAt(0)
                val hangingLeft = Math.max(-getDecoratedLeft(firstView), 0)
                val scrollBy = Math.min(scrolled - dx, hangingLeft)
                scrolled -= scrollBy
                offsetViews(-scrollBy)

                if (firstPosition > 0 && getViewCenterX(firstView) > 0) {
                    firstPosition--

                    val v = recycler?.getViewForPosition(firstPosition)!!

                    val right = getDecoratedLeft(firstView) + tileSize / 2
                    val left = right - tileSize

                    addNewView(v, left, right, true)
                } else {
                    break
                }
            }
        } else if (dx > 0) {
            val parentWidth = width
            while (scrolled < dx) {
                val lastView = getChildAt(childCount - 1)
                val hangingRight = Math.max(getDecoratedRight(lastView) - parentWidth, 0)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -= scrollBy
                offsetViews(-scrollBy)

                // We still have more to scroll and we can add a new child view
                if (state!!.getItemCount() > firstPosition + childCount && getViewCenterX(lastView) < width) {

                    val v = recycler?.getViewForPosition(firstPosition + childCount)!!

                    val left = getDecoratedRight(lastView) - tileSize / 2
                    val right = left + tileSize

                    addNewView(v, left, right)
                } else {
                    break
                }
            }
        }

        recycleViewsOutOfBounds(recycler)

        return dx
    }

    private fun offsetViews(dx: Int) {
        for (i in 0..childCount - 1) {
            offsetView(getChildAt(i), dx)
        }
    }

    private fun offsetView(view: View, dx: Int) {
        val centerXBefore = getViewCenterX(view)
        val yOffsetBefore = getYOffsetForXPosition(centerXBefore)

        view.offsetLeftAndRight(-dx)

        val centerX = getViewCenterX(view)
        val yOffset = getYOffsetForXPosition(centerX)

        view.offsetTopAndBottom(yOffset - yOffsetBefore)
    }

    private fun addNewView(view: View, left: Int, right: Int, addInFront: Boolean = false) {
        if (addInFront) {
            addView(view, 0)
        } else {
            addView(view)
        }
        measureChildWithMarginsAndDesiredWidthAndHeight(view, tileSize, tileSize)

        val xCenter = left + tileSize / 2
        val yOffset = getYOffsetForXPosition(xCenter)
        layoutDecorated(view, left, paddingTop + yOffset, right, paddingTop + tileSize + yOffset)
    }

    private fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler?) {
        val childCount = childCount
        val parentWidth = width
        val parentHeight = height
        var foundFirst = false
        var first = 0
        var last = 0
        for (i in 0..childCount - 1) {
            val v = getChildAt(i)
            if (v.hasFocus() || getDecoratedRight(v) >= 0 &&
                    getDecoratedLeft(v) <= parentWidth &&
                    getDecoratedBottom(v) >= 0 &&
                    getDecoratedTop(v) <= parentHeight) {
                if (!foundFirst) {
                    first = i
                    foundFirst = true
                }
                last = i
            }
        }
        for (i in childCount - 1 downTo last + 1) {
            removeAndRecycleViewAt(i, recycler)
        }
        for (i in first - 1 downTo 0) {
            removeAndRecycleViewAt(i, recycler)
        }
        if (getChildCount() == 0) {
            firstPosition = 0
        } else {
            firstPosition += first
        }
    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }
}