package be.simonraes.webshopui.imagetiles

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import be.simonraes.webshopui.R

/**
 * Created by SimonRaes on 04/06/17.
 */

class RhombusItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    val margin : Int

    init {
        margin = context.resources.getDimensionPixelSize(R.dimen.margin_rhombus_item)
    }


    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect?.set(margin, margin, margin, margin)
    }
}