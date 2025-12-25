package com.example.myapplication.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.data.HighScore
import com.example.myapplication.databinding.ActivityTopTenBinding

/**
 * TopTenActivity - displays the top 10 high scores with a list and map
 * Part 2: Hosts two fragments - HighScoreListFragment and HighScoreMapFragment
 * Clicking on a score in the list zooms the map to that location
 */
class TopTenActivity : AppCompatActivity(), HighScoreListFragment.OnScoreSelectedListener {

    private lateinit var binding: ActivityTopTenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopTenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Called when a high score is selected in the list fragment
     * Zooms the map fragment to the selected score's location
     */
    override fun onScoreSelected(highScore: HighScore) {
        val mapFragment = supportFragmentManager.findFragmentById(binding.fragmentMap.id)
        if (mapFragment is HighScoreMapFragment) {
            mapFragment.zoomToLocation(
                highScore.latitude,
                highScore.longitude,
                highScore.timestamp,
                highScore.playerName
            )
        }
    }
}

