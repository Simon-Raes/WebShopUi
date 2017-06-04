package be.simonraes.webshopui

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.widget.TextView

/**
 * Created by SimonRaes on 27/05/17.
 */
class RecyclerViewItem(itemView: View) : ViewHolder(itemView) {

    private val textView : TextView

    init {
        textView = itemView.findViewById(R.id.textview) as TextView
    }

    public fun bindData(text  :String) {
        textView.text = text
    }


}