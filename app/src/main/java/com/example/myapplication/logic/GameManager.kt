package com.example.myapplication.logic

/**
 * GameManager - manages game logic: lives, collisions, car position, coins, and odometer
 * Updated for Part 2 - supports 5 lanes and new game features
 */
class GameManager(
    private val lifeCount: Int = 3,
    private val laneCount: Int = 5,  // Part 2: 5 lanes (was 3)
    private val isFastMode: Boolean = false  // Part 2: Speed mode toggle
) {

    // Player score - starts at 0 and increases by 10 points for each obstacle passed
    var score: Int = 0
        private set

    // Number of lives remaining - starts at 3
    var lives: Int = lifeCount
        private set

    // Current player position in grid: 0=leftmost, 2=center, 4=rightmost (5 lanes)
    var currentCarIndex: Int = laneCount / 2  // Start in center lane
        private set

    // Game status - always true in endless game
    var isGameRunning: Boolean = false
        private set

    // Part 2: Coins collected
    var coinsCollected: Int = 0
        private set

    // Part 2: Odometer (distance traveled in meters)
    var distanceTraveled: Int = 0
        private set

    // Part 2: Frame rate based on speed mode
    val frameRate: Long
        get() = if (isFastMode) 350L else 600L

    // Maximum lane index (0 to laneCount-1)
    val maxLaneIndex: Int
        get() = laneCount - 1

    // Initialization - starts the game
    init {
        resetGame()
    }

    // Reset game - returns lives and score to initial state
    fun resetGame() {
        score = 0
        lives = lifeCount
        currentCarIndex = laneCount / 2 // Start in center
        coinsCollected = 0
        distanceTraveled = 0
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
        if (currentCarIndex < maxLaneIndex) {
            currentCarIndex++
        }
    }

    // Part 2: Set car position directly (for sensor controls)
    fun setCarPosition(laneIndex: Int) {
        currentCarIndex = laneIndex.coerceIn(0, maxLaneIndex)
    }

    // ===== Score Management =====
    // Add 10 points - called when player successfully passes an obstacle
    fun incrementScore() {
        score += 10
    }

    // Part 2: Collect coin - adds 5 points and increments coin counter
    fun collectCoin() {
        coinsCollected++
        score += 5
    }

    // Part 2: Increment odometer - called each game tick
    fun incrementOdometer() {
        distanceTraveled += if (isFastMode) 15 else 10
    }

    // ===== Lives & Collision =====
    // Collision - reduces lives, returns true if game over
    fun handleCollision(): Boolean {
        android.util.Log.d("GameManager", "handleCollision() called - lives before: $lives")
        lives--
        android.util.Log.d("GameManager", "Lives after decrement: $lives")

        // Check if game over (no more lives)
        if (lives <= 0) {
            android.util.Log.d("GameManager", "All lives lost! Game Over!")
            isGameRunning = false
            return true // Game over
        }
        return false // Game continues
    }

    // Stop the game
    fun stopGame() {
        isGameRunning = false
    }
}