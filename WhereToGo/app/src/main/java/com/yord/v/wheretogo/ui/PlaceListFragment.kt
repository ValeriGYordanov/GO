package com.yord.v.wheretogo.ui

import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yord.v.wheretogo.R
import com.yord.v.wheretogo.ui.recycler.PlaceAdapter

/**
 * Created by Valery on 3/27/2018.
 */
class PlaceListFragment: DialogFragment() {

    interface SelectedPlaceListener {
        fun onPlaceSelected(placeTitle: String)
    }

    var placeTitles: Array<String>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater?.inflate(R.layout.all_places_fragment, container, false)
        val act = activity as SelectedPlaceListener
        val recView = rootView?.findViewById(R.id.recycler_places) as RecyclerView
        recView.layoutManager = LinearLayoutManager(activity)

        placeTitles = arguments["allPlaces"] as Array<String>?

        val adapter = PlaceAdapter(this, placeTitles, act)
        recView.adapter = adapter

        this.dialog.setTitle("Places")

        return rootView!!
    }
}