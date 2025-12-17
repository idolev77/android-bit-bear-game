package com.example.myapplication.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * HighScoreManager - manages saving and loading high scores using SharedPreferences
 * Stores top 10 high scores with player name and GPS location
 */
class HighScoreManager(context: Context) {

    companion object {
        private const val PREFS_NAME = "high_scores_prefs"
        private const val KEY_HIGH_SCORES = "high_scores_list"
        private const val MAX_SCORES = 10
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    /**
     * Get all high scores sorted by score (highest first)
     */
    fun getHighScores(): List<HighScore> {
        val json = prefs.getString(KEY_HIGH_SCORES, null) ?: return emptyList()
        val type = object : TypeToken<List<HighScore>>() {}.type
        return try {
            gson.fromJson<List<HighScore>>(json, type).sortedByDescending { it.score }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Save a new high score
     * Returns true if the score made it into the top 10
     */
    fun saveHighScore(score: HighScore): Boolean {
        val currentScores = getHighScores().toMutableList()
        currentScores.add(score)

        // Sort by score descending and keep only top 10
        val topScores = currentScores.sortedByDescending { it.score }.take(MAX_SCORES)

        val json = gson.toJson(topScores)
        prefs.edit().putString(KEY_HIGH_SCORES, json).apply()

        // Check if the new score is in the top 10
        return topScores.any { it.timestamp == score.timestamp && it.score == score.score }
    }

    /**
     * Check if a score qualifies for the top 10
     */
    fun isHighScore(score: Int): Boolean {
        val currentScores = getHighScores()
        if (currentScores.size < MAX_SCORES) return true
        val lowestScore = currentScores.minByOrNull { it.score }?.score ?: 0
        return score > lowestScore
    }

    /**
     * Clear all high scores
     */
    fun clearHighScores() {
        prefs.edit().remove(KEY_HIGH_SCORES).apply()
    }
}

