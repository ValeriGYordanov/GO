package com.yord.v.wheretogo.ui.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yord.v.wheretogo.R
import com.yord.v.wheretogo.ui.SpotListFragment

/**
 * Created by Valery on 3/27/2018.
 */
class SpotAdapter(val dialog: SpotListFragment, val spots: Array<String>?, val selected: SpotListFragment.SelectedSpotListener) : RecyclerView.Adapter<SpotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.cardview_frag, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return spots?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val title: String? = spots?.get(position)
        holder?.textViewSpotTitle?.text = title
        holder?.textViewSpotTitle?.setOnClickListener { view ->
            dialog.dismiss()
            selected.onSpotSelected(title!!)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewSpotTitle = itemView.findViewById(R.id.all_spots_txt) as TextView
    }
}