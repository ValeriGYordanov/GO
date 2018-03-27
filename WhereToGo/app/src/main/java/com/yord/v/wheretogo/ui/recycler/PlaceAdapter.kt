package com.yord.v.wheretogo.ui.recycler

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yord.v.wheretogo.R
import com.yord.v.wheretogo.ui.PlaceListFragment

/**
 * Created by Valery on 3/27/2018.
 */
class PlaceAdapter(val dialog: PlaceListFragment, val places: Array<String>?, val selected: PlaceListFragment.SelectedPlaceListener) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.cardview_frag, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return places?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val title: String? = places?.get(position)
        holder?.textViewPlaceTitle?.text = title
        holder?.textViewPlaceTitle?.setOnClickListener { view ->
            dialog.dismiss()
            selected.onPlaceSelected(title!!)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewPlaceTitle = itemView.findViewById(R.id.all_places_txt) as TextView
    }
}