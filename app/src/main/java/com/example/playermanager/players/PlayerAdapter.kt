package com.example.playermanager.players

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.playermanager.R
import com.example.playermanager.model.Player
import de.hdodenhof.circleimageview.CircleImageView

class PlayerAdapter(
    private var players: List<Player>,
    private val onItemClick: (Player) -> Unit,
    private val onItemLongClick: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    class PlayerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivPhoto: CircleImageView = view.findViewById(R.id.iv_player_photo)
        val tvName: TextView = view.findViewById(R.id.tv_player_name)
        val tvStatusBadge: TextView = view.findViewById(R.id.tv_status_badge)
        val tvSport: TextView = view.findViewById(R.id.tv_sport_type)
        val tvCoach: TextView = view.findViewById(R.id.tv_coach_name)
        val tvJoinDate: TextView = view.findViewById(R.id.tv_join_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        val context = holder.itemView.context

        holder.tvName.text = player.fullName
        holder.tvSport.text = player.sportType.ifEmpty { "—" }
        holder.tvCoach.text = if (player.coachName.isNotEmpty()) {
            context.getString(R.string.coach_prefix, player.coachName)
        } else {
            context.getString(R.string.coach_prefix, "—")
        }
        holder.tvJoinDate.text = context.getString(R.string.joined_prefix, player.joinDate.ifEmpty { "—" })

        val isActive = SubscriptionHelper.isSubscriptionActive(player.expiryDate)
        if (isActive) {
            holder.tvStatusBadge.text = context.getString(R.string.badge_active)
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_active)
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        } else {
            holder.tvStatusBadge.text = context.getString(R.string.badge_expired)
            holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_expired)
            holder.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
        }

        PlayerPhotoLoader.load(holder.ivPhoto, player.photoUri)

        holder.itemView.setOnClickListener { onItemClick(player) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(player)
            true
        }
    }

    override fun getItemCount() = players.size

    fun updateData(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }
}
