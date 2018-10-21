package studios.devs.mobi.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import studios.devs.mobi.R
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.ui.adapters.SpotAdapter

const val SPOTS_LIST = "spots_list"

class AllSpotsDialog: DialogFragment() {

    interface SelectedSpotListener {
        fun onSpotSelected(spotTitle: String)
    }

    var spotTitles: List<SpotEntity>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NoBackgroundThemeDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.all_spots_fragment, container, false)
        val act = activity as SelectedSpotListener
        val recView = rootView?.findViewById(R.id.recycler_spots) as RecyclerView
        recView.layoutManager = LinearLayoutManager(activity)

        arguments?.let {
            spotTitles = it[SPOTS_LIST] as List<SpotEntity>
        }
        val adapter = SpotAdapter(this, spotTitles, act)
        recView.adapter = adapter

        return rootView
    }

}