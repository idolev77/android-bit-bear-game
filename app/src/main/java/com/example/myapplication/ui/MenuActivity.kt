package com.example.myapplication.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMenuBinding

/**
 * MenuActivity - Main menu screen for the game
 * Part 2: Allows player to choose speed mode, control mode (buttons/sensors), or view high scores
 */
class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    companion object {
        const val EXTRA_IS_FAST_MODE = "is_fast_mode"
        const val EXTRA_USE_SENSORS = "use_sensors"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBackground()
        setupListeners()
    }

    /**
     * Load animated GIF background
     */
    private fun setupBackground() {
        Glide.with(this)
            .asGif()
            .load(R.drawable.big_bear_back)
            .into(binding.menuBackground)
    }

    /**
     * Setup button click listeners
     */
    private fun setupListeners() {
        // Start game with button controls
        binding.btnStartButtons.setOnClickListener {
            startGame(useSensors = false)
        }

        // Start game with sensor controls (accelerometer)
        binding.btnStartSensors.setOnClickListener {
            startGame(useSensors = true)
        }

        // Navigate to Top Ten high scores screen
        binding.btnTopTen.setOnClickListener {
            val intent = Intent(this, TopTenActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Start the main game activity with selected options
     */
    private fun startGame(useSensors: Boolean) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra(EXTRA_IS_FAST_MODE, binding.speedToggle.isChecked)
            putExtra(EXTRA_USE_SENSORS, useSensors)
        }
        startActivity(intent)
    }
}

