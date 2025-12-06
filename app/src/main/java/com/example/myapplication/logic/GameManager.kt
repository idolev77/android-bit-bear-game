package com.example.myapplication.logic

/**
 * GameManager - manages game logic: lives, collisions, and car position
 */
class GameManager(private val lifeCount: Int = 3) {

    // Player score - starts at 0 and increases by 10 points for each obstacle passed
    var score: Int = 0
        private set

    // Number of lives remaining - starts at 3
    var lives: Int = lifeCount
        private set

    // Current player position in grid: 0=left, 1=center, 2=right
    var currentCarIndex: Int = 1
        private set

    // Game status - always true in endless game
    var isGameRunning: Boolean = false
        private set

    // Initialization - starts the game
    init {
        resetGame()
    }

    // Reset game - returns lives and score to initial state
    fun resetGame() {
        score = 0
        lives = lifeCount
        currentCarIndex = 1 // Start in center
        isGameRunning = true
    }

    // ===== Player Movement =====
    // Move player left (if possible)
    fun moveCarLeft() {
        if (currentCarIndex > 0) {
            currentCarIndex--
        }
    }

    // Move player right (if possible)
    fun moveCarRight() {
        if (currentCarIndex < 2) {
            currentCarIndex++
        }
    }

    // ===== Score Management =====
    // Add 10 points - called when player successfully passes an obstacle
    fun incrementScore() {
        score += 10
    }

    // ===== Lives & Collision =====
    // Collision - reduces lives
    fun handleCollision() {
        android.util.Log.d("GameManager", "handleCollision() called - lives before: $lives")
        lives--
        android.util.Log.d("GameManager", "Lives after decrement: $lives")

        // Toast is shown in MainActivity.onCrash() to avoid duplicate messages

        // Endless game - when all 3 lives are lost, reset everything and start over
        if (lives <= 0) {
            android.util.Log.d("GameManager", "All lives lost! Resetting game (endless mode)")
            resetGame() // Reset lives and score and start over
        }
    }
}