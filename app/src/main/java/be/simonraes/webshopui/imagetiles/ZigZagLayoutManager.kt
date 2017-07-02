package be.simonraes.webshopui.imagetiles

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup


/**
 * Moves items in a W shape.
 */
class ZigZagLayoutManager : RecyclerView.LayoutManager() {

    // todo proper padding and decorations support

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

        val extraOffsetToMakeFirstViewStartCentered = screenCenterX - tileSize / 2
        val oldLeft = oldLeftView?.left ?: paddingLeft + extraOffsetToMakeFirstViewStartCentered

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


    private fun shouldApplyLeftOffset(index: Int) = index > 0


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

    private fun getViewCenterX(view: View) = (view.left + view.right) / 2

    override fun scrollHorizontallyBy(dx: Int, recycler: RecyclerView.Recycler?, state: RecyclerView.State?): Int {

        if (childCount == 0) {
            return 0
        }

        var scrolled = 0
        if (dx < 0) {

            while (scrolled > dx) {
                val firstView = getChildAt(0)

                // If we're scrolling the first adapter position to the right, we add some extra scroll distance so that view can scroll to the center of the screen.
                // We don't need (or want to!) add this for other views
                val extraLeftOffset = if (firstPosition == 0) (screenCenterX - tileSize / 2) else 0
                val firstViewLeftSide = extraLeftOffset - getDecoratedLeft(firstView)

                val hangingLeft = Math.max(firstViewLeftSide, 0)
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

                val extraRightOffset = if (firstPosition + childCount == state!!.itemCount) (screenCenterX - tileSize / 2) else 0
                val lastViewRightSide = getDecoratedRight(lastView) + extraRightOffset

                val hangingRight = Math.max(lastViewRightSide - parentWidth, 0)
                val scrollBy = -Math.min(dx - scrolled, hangingRight)
                scrolled -= scrollBy
                offsetViews(-scrollBy)

                // We still have more to scroll and we can add a new child view
                if (state.getItemCount() > firstPosition + childCount && getViewCenterX(lastView) < width) {

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

        return scrolled
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

    /**
     * Works with values on the simplified values graph, for both x and y.
     * x is 0 at the view's highest y position, x is 1 at the view's lowest position, view is at the highest position again at 2, and so on
     * y 1 is the view's highest position, y 0 is its lowest position
     * */
    private fun getYPositionForXPosition(xPosition: Float): Float {
        val xPos = normaliseXPosition(xPosition)

        return xPos
    }

    private fun normaliseXPosition(xPosition: Float): Float {
        var mutableX = xPosition

        mutableX = Math.abs(mutableX)
        // Layoutmanager can be switched back to a VVVVV tile movement pattern by adding back this line here: mutableX = mutableX % 2
        // Removed it to make the pattern a large W
        if (mutableX > 1)
            mutableX = 2 - mutableX

        return mutableX
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
//        todo fix obviously
        firstPosition = position -2
        requestLayout()
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
//        todo implement
    }

    fun findFirstCompletelyVisibleItemPosition(): Int {
        if (childCount > 0) {
            for (i in 0..childCount - 1) {
                val v = getChildAt(i)
                if (getDecoratedLeft(v) >= paddingLeft && getDecoratedLeft(v) <= width - paddingBottom) {
                    return getPosition(v)
                }
            }
        }

        return 0
    }

    fun findFirstVisibleItemPosition(): Int {
        return firstPosition
    }

    fun findLastCompletelyVisibleItemPosition(): Int {
        if (childCount > 0) {
            for (i in childCount - 1..0) {
                val v = getChildAt(i)
                if (getDecoratedLeft(v) >= paddingLeft && getDecoratedLeft(v) <= width - paddingBottom) {
                    return getPosition(v)
                }
            }
        }

        return 0
    }

    fun findLastVisibleItemPosition(): Int {
        return firstPosition + childCount
    }


}