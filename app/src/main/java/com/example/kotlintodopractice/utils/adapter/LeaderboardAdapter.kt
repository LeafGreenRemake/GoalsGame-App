package com.example.kotlintodopractice.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlintodopractice.R
import com.example.kotlintodopractice.utils.model.User

class LeaderboardAdapter(private val users: List<User>) :
    RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeTextView: TextView = itemView.findViewById(R.id.placeTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val scoreTextView: TextView = itemView.findViewById(R.id.scoreTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_leaderboard, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val user = users[position]
        holder.placeTextView.text = "${position + 1}."
        holder.nameTextView.text = user.nickname.toString()
        holder.scoreTextView.text = user.score.toString()
    }

    override fun getItemCount(): Int {
        return users.size
    }
}