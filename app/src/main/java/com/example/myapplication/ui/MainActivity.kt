package com.example.myapplication.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.logic.GameManager

class MainActivity : AppCompatActivity() {

    // ===== Grid Configuration =====
    private val GRID_ROWS = 14  // 14 rows - obstacles fall slower
    private val GRID_COLS = 3   // 3 columns - 3 lanes
    private val PLAYER_ROW = GRID_ROWS - 1  // Player is in second-to-last row

    // ===== UI Variables =====
    private lateinit var gameGrid: TableLayout  // Game grid
    private lateinit var btnLeft: ImageButton    // Left movement button
    private lateinit var btnRight: ImageButton   // Right movement button
    private lateinit var textScore: TextView     // Score display
    private lateinit var heartViews: Array<ImageView>  // Hearts display (lives)

    // ===== Grid Cells =====
    // 2D matrix containing all grid cells (ImageView)
    private val gridCells = Array(GRID_ROWS) {
        Array<ImageView?>(GRID_COLS) { null }
    }

    // ===== Game Logic =====
    private lateinit var gameManager: GameManager  // Game logic manager
    private val handler = Handler(Looper.getMainLooper())  // Game timer
    private val frameRate: Long = 600 // Time between moves (600ms = 0.6 seconds)

    // Obstacle tracking
    private var obstacleRow = -1        // Current obstacle row
    private var obstacleCol = 1         // Current obstacle column
    private var collisionDetected = false  // Whether collision was detected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Force LTR (Left-to-Right) layout direction
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        initViews()   // Initialize UI components
        buildGrid()   // Build game grid
        initGame()    // Start game
    }

    // Initialize UI components
    private fun initViews() {
        gameGrid = findViewById(R.id.game_grid)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.btn_right)
        textScore = findViewById(R.id.text_score)

        // Load animated GIF background
        val backgroundGif = findViewById<ImageView>(R.id.background_gif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.tenor)
            .into(backgroundGif)

        // Array of 3 hearts (lives)
        heartViews = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )
    }

    // Build game grid - matrix of GRID_ROWS x GRID_COLS cells
    private fun buildGrid() {
        gameGrid.removeAllViews()

        // TableLayout distributes rows equally automatically
        // No need to manually calculate cell height

        for (row in 0 until GRID_ROWS) {
            val tableRow = TableRow(this).apply {
                layoutParams = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    0,  // Height 0 with weight
                    1f  // Equal weight for all rows
                )
                gravity = android.view.Gravity.CENTER
            }

            for (col in 0 until GRID_COLS) {
                val cell = ImageView(this).apply {
                    layoutParams = TableRow.LayoutParams(
                        0,  // Width 0 with weight
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1f  // Equal weight for all columns
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setPadding(24, 24, 24, 24)  // More padding = smaller icons
                    adjustViewBounds = true
                }

                gridCells[row][col] = cell
                tableRow.addView(cell)
            }

            gameGrid.addView(tableRow)
        }
    }

    // Initialize game - create GameManager and setup buttons
    private fun initGame() {
        gameManager = GameManager()

        // Left button - moves player left
        btnLeft.setOnClickListener {
            gameManager.moveCarLeft()
            updatePlayerPosition()
            checkSideCollision() // Check if player moved into obstacle from side
        }

        // Right button - moves player right
        btnRight.setOnClickListener {
            gameManager.moveCarRight()
            updatePlayerPosition()
            checkSideCollision() // Check if player moved into obstacle from side
        }

        updatePlayerPosition()  // Place player in starting position
        updateLivesUI()         // Update hearts display
        updateScoreUI()         // Update score display
        spawnNewObstacle()      // Create first obstacle
        startTimer()            // Start game loop
    }

    // ===== Game Loop =====
    // Runnable executed every frameRate milliseconds
    private val runnable = object : Runnable {
        override fun run() {
            if (gameManager.isGameRunning) {
                tick()  // Execute one game step
                handler.postDelayed(this, frameRate)  // Schedule next run
            }
        }
    }

    // Start game timer
    private fun startTimer() {
        handler.postDelayed(runnable, frameRate)
    }

    // Stop game timer
    private fun stopTimer() {
        handler.removeCallbacks(runnable)
    }

    // Game loop - executed every 600ms
    private fun tick() {
        android.util.Log.d("MainActivity", "tick() - isGameRunning=${gameManager.isGameRunning}, lives=${gameManager.lives}")

        // Endless game - no need to check isGameRunning, game always continues
        moveObstacle()
        updateScoreUI()
        updateLivesUI()
    }

    // Create new obstacle - starts at top row in random column
    private fun spawnNewObstacle() {
        obstacleRow = 0  // Top row
        obstacleCol = (0 until GRID_COLS).random()  // Random column (0-2)
        collisionDetected = false  // Reset collision flag

        // Place obstacle (bear) in grid
        gridCells[obstacleRow][obstacleCol]?.setImageResource(R.drawable.bear)
    }

    // Move obstacle one row down
    private fun moveObstacle() {
        if (obstacleRow < 0) return

        // Clear current position - but NEVER clear the player row
        if (obstacleRow < GRID_ROWS && obstacleRow != PLAYER_ROW) {
            gridCells[obstacleRow][obstacleCol]?.setImageDrawable(null)
        }

        // Move down one row
        obstacleRow++

        // Check if obstacle reached bottom (past the grid)
        if (obstacleRow >= GRID_ROWS) {
            // Clear the last position before spawning new obstacle
            // Check the actual last row where the bear was
            val lastRow = GRID_ROWS - 1
            if (lastRow != PLAYER_ROW) {
                // Clear if it's not player row
                gridCells[lastRow][obstacleCol]?.setImageDrawable(null)
            } else {
                // If last row IS player row, check if bear is in different column than player
                if (obstacleCol != gameManager.currentCarIndex) {
                    gridCells[lastRow][obstacleCol]?.setImageDrawable(null)
                }
            }

            // Obstacle passed successfully - add score
            if (!collisionDetected) {
                gameManager.incrementScore()
            }
            spawnNewObstacle()  // Create new obstacle
            return
        }

        // Check collision at new position
        if (obstacleRow == PLAYER_ROW && obstacleCol == gameManager.currentCarIndex) {
            // Collision! Bear hit the bitcoin
            android.util.Log.d("MainActivity", "COLLISION DETECTED! Lives before: ${gameManager.lives}")
            collisionDetected = true
            onCrash() // Show Toast and vibrate

            // Reduce lives (if lives run out, GameManager will reset automatically)
            gameManager.handleCollision()
            android.util.Log.d("MainActivity", "Lives after collision: ${gameManager.lives}")

            // Game continues - don't stop it (endless game)
            updateScoreUI()
            updateLivesUI()

            // Create new obstacle at top
            spawnNewObstacle()
            return
        }

        // Place obstacle in new position
        gridCells[obstacleRow][obstacleCol]?.setImageResource(R.drawable.bear)
    }

    // Update player position in grid
    private fun updatePlayerPosition() {
        // Clear ONLY old bitcoin positions, don't clear bear if it's at player row
        for (col in 0 until GRID_COLS) {
            // Only clear if this column doesn't have the bear at player row
            if (obstacleRow != PLAYER_ROW || col != obstacleCol) {
                gridCells[PLAYER_ROW][col]?.setImageDrawable(null)
            }
        }

        // Place player (bitcoin) in current column - ALWAYS visible
        val playerCol = gameManager.currentCarIndex
        gridCells[PLAYER_ROW][playerCol]?.setImageResource(R.drawable.bitcoin)
    }

    // Check collision when player moves sideways into obstacle
    private fun checkSideCollision() {
        // Check if player moved into same position as obstacle
        if (obstacleRow == PLAYER_ROW && obstacleCol == gameManager.currentCarIndex && !collisionDetected) {
            // Side collision! Player moved into the bear
            android.util.Log.d("MainActivity", "SIDE COLLISION! Player moved into bear. Lives before: ${gameManager.lives}")
            collisionDetected = true
            onCrash() // Show Toast and vibrate

            // Reduce lives (if lives run out, GameManager will reset automatically)
            gameManager.handleCollision()
            android.util.Log.d("MainActivity", "Lives after side collision: ${gameManager.lives}")

            // Game continues - don't stop it (endless game)
            updateScoreUI()
            updateLivesUI()

            // Create new obstacle at top
            spawnNewObstacle()
        }
    }

    // Update hearts display (lives)
    private fun updateLivesUI() {
        for (i in heartViews.indices) {
            // Show heart if lives remain, hide otherwise
            heartViews[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
    }

    // Update score display
    private fun updateScoreUI() {
        textScore.text = getString(R.string.score_label, gameManager.score)
    }

    // Show crash notification - Toast and vibration
    private fun onCrash() {
        Toast.makeText(this, "Crash!", Toast.LENGTH_SHORT).show()

        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    // Stop game when app goes to background
    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    // Resume game when app comes back to foreground
    override fun onResume() {
        super.onResume()
        android.util.Log.d("MainActivity", "onResume() - restarting timer")
        if (::gameManager.isInitialized) {
            startTimer() // Always start timer - endless game
        }
    }
}

