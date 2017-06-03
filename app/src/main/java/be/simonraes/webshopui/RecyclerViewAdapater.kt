package be.simonraes.webshopui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Created by SimonRaes on 27/05/17.
 */
class RecyclerViewAdapater(context: Context) : RecyclerView.Adapter<RecyclerViewItem>() {

    private val layoutInflater: LayoutInflater

    init {
        layoutInflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(container: ViewGroup?, p1: Int): RecyclerViewItem {
        return RecyclerViewItem(layoutInflater.inflate(R.layout.listitem_item, container, false))
    }

    override fun onBindViewHolder(p0: RecyclerViewItem, p1: Int) {
        p0.bindData("yo boy")
    }

    override fun getItemCount() = 20
}