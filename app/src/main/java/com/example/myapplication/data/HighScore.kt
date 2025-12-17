package com.example.myapplication.data

/**
 * HighScore - data class representing a high score entry
 * Contains score, player name, and GPS location where the score was achieved
 */
data class HighScore(
    val score: Int,
    val playerName: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

