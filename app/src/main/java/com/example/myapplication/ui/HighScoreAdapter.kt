package com.example.myapplication.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.data.HighScore
import com.example.myapplication.databinding.ItemHighScoreBinding
import java.text.DecimalFormat

/**
 * HighScoreAdapter - RecyclerView adapter for displaying high scores
 * Part 2: Shows rank, player name, score, and location coordinates
 */
class HighScoreAdapter(
    private val onItemClick: (HighScore) -> Unit
) : ListAdapter<HighScore, HighScoreAdapter.ViewHolder>(HighScoreDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHighScoreBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position + 1)
    }

    inner class ViewHolder(
        private val binding: ItemHighScoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val coordFormat = DecimalFormat("#.###")

        fun bind(highScore: HighScore, rank: Int) {
            binding.apply {
                textRank.text = root.context.getString(R.string.rank_format, rank)
                textPlayerName.text = highScore.playerName.ifEmpty {
                    root.context.getString(R.string.unknown_player)
                }
                textScore.text = root.context.getString(R.string.score_format, highScore.score)

                // Show GPS coordinates
                val lat = coordFormat.format(highScore.latitude)
                val lng = coordFormat.format(highScore.longitude)
                textLocation.text = "📍 $lat, $lng"

                // Click listener - notify parent to zoom map
                root.setOnClickListener {
                    onItemClick(highScore)
                }
            }
        }
    }

    class HighScoreDiffCallback : DiffUtil.ItemCallback<HighScore>() {
        override fun areItemsTheSame(oldItem: HighScore, newItem: HighScore): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: HighScore, newItem: HighScore): Boolean {
            return oldItem == newItem
        }
    }
}

