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


    var firstPosition = 0

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)

        val parentRight = width - paddingRight
        val oldLeftView = if (childCount > 0) getChildAt(0) else null

        var oldLeft = oldLeftView?.left ?: paddingLeft

        detachAndScrapAttachedViews(recycler)

        var left = oldLeft
        var right: Int
        var top = paddingTop
        var bottom = (height - paddingBottom)

        val availableVerticalSpace = height - paddingTop - paddingBottom
        val tileHeight = availableVerticalSpace * 2 / 3

        val count = state?.itemCount ?: 0


        var i = 0
        while (firstPosition + i < count && left < parentRight) {
            val v = recycler?.getViewForPosition(firstPosition + i)
            if (v != null) {

                addView(v, i)

                measureChildWithMarginsAndDesiredWidthAndHeight(v, tileHeight, tileHeight)

                right = left + getDecoratedMeasuredWidth(v)

                layoutDecorated(v, left, top, right, top + getDecoratedMeasuredHeight(v))
                i++
                left = right
            }
        }
    }

    fun measureChildWithMarginsAndDesiredWidthAndHeight(child: View, desiredWidth: Int, desiredHeight: Int) {
        val lp = child.getLayoutParams() as RecyclerView.LayoutParams

        // The measureChildWithMargins method uses a private method to get the item decorations, we solve it like this:
        val decorations = Rect()
        calculateItemDecorationsForChild(child, decorations)
        val occupiedWidth= decorations.left + decorations.right
        val occupiedHeight= decorations.top + decorations.bottom


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
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    private fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler?) {

    }

    override fun scrollToPosition(position: Int) {
        super.scrollToPosition(position)
    }
}