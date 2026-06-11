package com.example.playermanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.playermanager.R
import com.example.playermanager.model.Sport
import com.google.android.material.imageview.ShapeableImageView

class SportAdapter(
    private var sports: List<Sport>,
    private val onItemClick: (Sport) -> Unit,
    private val onItemLongClick: (Sport) -> Unit
) : RecyclerView.Adapter<SportAdapter.SportViewHolder>() {

    class SportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivSport: ShapeableImageView = view.findViewById(R.id.iv_sport)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvLocation: TextView = view.findViewById(R.id.tv_location)
        val tvHours: TextView = view.findViewById(R.id.tv_hours)
        val tvPrice: TextView = view.findViewById(R.id.tv_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sport, parent, false)
        return SportViewHolder(view)
    }

    override fun onBindViewHolder(holder: SportViewHolder, position: Int) {
        val sport = sports[position]
        holder.tvName.text = sport.name
        holder.tvLocation.text = sport.location
        holder.tvHours.text = sport.openingTime
        holder.tvPrice.text = "$${sport.price}/hr"
        
        // Display selected image or fallback to placeholder
        if (!sport.imageUri.isNullOrEmpty()) {
            try {
                holder.ivSport.setImageURI(android.net.Uri.parse(sport.imageUri))
            } catch (e: Exception) {
                holder.ivSport.setImageResource(R.drawable.ic_ball)
            }
        } else {
            holder.ivSport.setImageResource(R.drawable.ic_ball)
        } 

        holder.itemView.setOnClickListener { onItemClick(sport) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(sport)
            true
        }
    }

    override fun getItemCount() = sports.size

    fun updateData(newSports: List<Sport>) {
        sports = newSports
        notifyDataSetChanged()
    }
}
