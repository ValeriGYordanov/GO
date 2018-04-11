package com.yord.v.wheretogo.ui

import android.app.DialogFragment
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yord.v.wheretogo.R
import com.yord.v.wheretogo.ui.recycler.SpotAdapter

/**
 * Created by Valery on 3/27/2018.
 */
class SpotListFragment: DialogFragment() {

    interface SelectedSpotListener {
        fun onSpotSelected(spotTitle: String)
    }

    var spotTitles: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NoBackgroundThemeDialog)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater?.inflate(R.layout.all_spots_fragment, container, false)
        val act = activity as SelectedSpotListener
        val recView = rootView?.findViewById(R.id.recycler_spots) as RecyclerView
        recView.layoutManager = LinearLayoutManager(activity)

        spotTitles = arguments["allSpots"] as Array<String>?

        val adapter = SpotAdapter(this, spotTitles, act)
        recView.adapter = adapter

        return rootView!!
    }
}