package studios.devs.mobi.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import studios.devs.mobi.R
import studios.devs.mobi.model.SpotEntity
import studios.devs.mobi.ui.dialogs.AllSpotsDialog

class SpotAdapter(val dialog: AllSpotsDialog,
                  val spots: List<SpotEntity>?,
                  val selected: AllSpotsDialog.SelectedSpotListener) : RecyclerView.Adapter<SpotAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.cardview_frag, parent, false)
        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return spots?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title: String? = spots?.get(position)?.spotTitle
        holder.textViewSpotTitle.text = title
        holder.textViewSpotTitle.setOnClickListener { _ ->
            dialog.dismiss()
            selected.onSpotSelected(title!!)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textViewSpotTitle = itemView.findViewById(R.id.all_spots_txt) as TextView
    }
}