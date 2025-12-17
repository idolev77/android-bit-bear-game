package com.example.myapplication.ui

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.MediaPlayer
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
import android.content.Intent
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.data.HighScore
import com.example.myapplication.data.HighScoreManager
import com.example.myapplication.logic.GameManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity(), SensorEventListener {

    // ===== Grid Configuration - Part 2: 5 lanes =====
    private val GRID_ROWS = 16  // 16 rows - longer road
    private val GRID_COLS = 5   // Part 2: 5 columns - 5 lanes
    private val PLAYER_ROW = GRID_ROWS - 1  // Player is in last row

    // ===== UI Variables =====
    private lateinit var gameGrid: TableLayout  // Game grid
    private lateinit var btnLeft: ImageButton    // Left movement button
    private lateinit var btnRight: ImageButton   // Right movement button
    private lateinit var textScore: TextView     // Score display
    private lateinit var textOdometer: TextView  // Part 2: Distance display
    private lateinit var textCoins: TextView     // Part 2: Coins display
    private lateinit var heartViews: Array<ImageView>  // Hearts display (lives)
    private lateinit var buttonContainer: View   // Container for control buttons

    // ===== Grid Cells =====
    // 2D matrix containing all grid cells (ImageView)
    private val gridCells = Array(GRID_ROWS) {
        Array<ImageView?>(GRID_COLS) { null }
    }

    // ===== Game Logic =====
    private lateinit var gameManager: GameManager  // Game logic manager
    private val handler = Handler(Looper.getMainLooper())  // Game timer
    private var currentFrameRate: Long = 600 // Time between moves

    // ===== Part 2: Game Mode Settings =====
    private var isFastMode: Boolean = false
    private var useSensors: Boolean = false

    // ===== Part 2: Sensor Variables =====
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastSensorUpdate: Long = 0
    private val SENSOR_THRESHOLD = 2.5f  // Tilt sensitivity
    private val SENSOR_UPDATE_INTERVAL = 200L  // Min time between sensor updates (ms)

    // ===== Part 2: Sound =====
    private var crashSound: MediaPlayer? = null

    // ===== Part 2: Location =====
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null
    private val LOCATION_PERMISSION_REQUEST = 1001

    // ===== Part 2: High Score Manager =====
    private lateinit var highScoreManager: HighScoreManager

    // Obstacle tracking - multiple obstacles
    private data class Obstacle(var row: Int, var col: Int, var collisionDetected: Boolean = false)
    private val obstacles = mutableListOf<Obstacle>()  // List of active obstacles
    private var spawnCounter = 0  // Counter to control spawn frequency

    // Part 2: Coin tracking
    private data class Coin(var row: Int, var col: Int, var collected: Boolean = false)
    private val coins = mutableListOf<Coin>()
    private var coinSpawnCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Force LTR (Left-to-Right) layout direction
        window.decorView.layoutDirection = View.LAYOUT_DIRECTION_LTR

        // Part 2: Get game mode from intent
        isFastMode = intent.getBooleanExtra(MenuActivity.EXTRA_IS_FAST_MODE, false)
        useSensors = intent.getBooleanExtra(MenuActivity.EXTRA_USE_SENSORS, false)

        initViews()   // Initialize UI components
        initSensors() // Part 2: Initialize sensors
        initSound()   // Part 2: Initialize crash sound
        initLocation() // Part 2: Initialize location services
        buildGrid()   // Build game grid
        initGame()    // Start game

        // Part 2: Initialize high score manager
        highScoreManager = HighScoreManager(this)
    }

    // Initialize UI components
    private fun initViews() {
        gameGrid = findViewById(R.id.game_grid)
        btnLeft = findViewById(R.id.btn_left)
        btnRight = findViewById(R.id.btn_right)
        textScore = findViewById(R.id.text_score)
        buttonContainer = findViewById(R.id.button_container)

        // Part 2: Initialize new UI elements
        textOdometer = findViewById(R.id.text_odometer)
        textCoins = findViewById(R.id.text_coins)

        // Load animated GIF background
        val backgroundGif = findViewById<ImageView>(R.id.background_gif)
        Glide.with(this)
            .asGif()
            .load(R.drawable.big_bear_back)
            .into(backgroundGif)

        // Array of 3 hearts (lives)
        heartViews = arrayOf(
            findViewById(R.id.heart1),
            findViewById(R.id.heart2),
            findViewById(R.id.heart3)
        )

        // Part 2: Hide buttons if using sensors
        if (useSensors) {
            buttonContainer.visibility = View.GONE
        }
    }

    // Part 2: Initialize sensor manager and accelerometer
    private fun initSensors() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    // Part 2: Initialize crash sound
    private fun initSound() {
        try {
            // Direct reference to crash_sound in res/raw/
            crashSound = MediaPlayer.create(this, R.raw.crash_sound)
        } catch (e: Exception) {
            // Sound file not found - game will work without sound
            android.util.Log.w("MainActivity", "Crash sound not found: ${e.message}")
        }
    }

    // Part 2: Initialize location services
    private fun initLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()
    }

    // Part 2: Request location permission
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            getLastLocation()
        }
    }

    // Part 2: Get last known location
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }
    }

    // Build game grid - matrix of GRID_ROWS x GRID_COLS cells
    private fun buildGrid() {
        gameGrid.removeAllViews()

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
                    // Part 2: Increased padding for 5 lanes - smaller icons for better proportions
                    setPadding(20, 20, 20, 20)
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
        // Part 2: Create GameManager with lane count and speed mode
        gameManager = GameManager(
            lifeCount = 3,
            laneCount = GRID_COLS,
            isFastMode = isFastMode
        )
        currentFrameRate = gameManager.frameRate

        // Left button - moves player left
        btnLeft.setOnClickListener {
            gameManager.moveCarLeft()
            updatePlayerPosition()
            checkSideCollision()
            checkCoinCollection()
        }

        // Right button - moves player right
        btnRight.setOnClickListener {
            gameManager.moveCarRight()
            updatePlayerPosition()
            checkSideCollision()
            checkCoinCollection()
        }

        updatePlayerPosition()  // Place player in starting position
        updateLivesUI()         // Update hearts display
        updateScoreUI()         // Update score display
        updateOdometerUI()      // Part 2: Update odometer
        updateCoinsUI()         // Part 2: Update coins

        // Spawn initial obstacles
        spawnNewObstacle()
        spawnCounter = 0
        coinSpawnCounter = 0

        startTimer()            // Start game loop
    }

    // ===== Game Loop =====
    private val runnable = object : Runnable {
        override fun run() {
            if (gameManager.isGameRunning) {
                tick()
                handler.postDelayed(this, currentFrameRate)
            }
        }
    }

    // Start game timer
    private fun startTimer() {
        handler.postDelayed(runnable, currentFrameRate)
    }

    // Stop game timer
    private fun stopTimer() {
        handler.removeCallbacks(runnable)
    }

    // Game loop - executed every frameRate milliseconds
    private fun tick() {
        // Part 2: Increment odometer
        gameManager.incrementOdometer()

        // Spawn new obstacles periodically (every 3-5 ticks)
        spawnCounter++
        if (spawnCounter >= (3..5).random()) {
            spawnNewObstacle()
            spawnCounter = 0
        }

        // Part 2: Spawn coins periodically (every 5-8 ticks)
        coinSpawnCounter++
        if (coinSpawnCounter >= (5..8).random()) {
            spawnNewCoin()
            coinSpawnCounter = 0
        }

        // Move all obstacles
        moveAllObstacles()

        // Part 2: Move all coins
        moveAllCoins()

        updateScoreUI()
        updateLivesUI()
        updateOdometerUI()
        updateCoinsUI()
    }

    // Create new obstacle - starts at top row in random column
    private fun spawnNewObstacle() {
        val newObstacle = Obstacle(row = 0, col = (0 until GRID_COLS).random())
        obstacles.add(newObstacle)
        gridCells[newObstacle.row][newObstacle.col]?.setImageResource(R.drawable.bear_market)
    }

    // Part 2: Create new coin - starts at top row in random column
    private fun spawnNewCoin() {
        // Don't spawn coin in same column as obstacle
        val occupiedCols = obstacles.filter { it.row == 0 }.map { it.col }
        val availableCols = (0 until GRID_COLS).filter { it !in occupiedCols }
        if (availableCols.isNotEmpty()) {
            val newCoin = Coin(row = 0, col = availableCols.random())
            coins.add(newCoin)
            gridCells[newCoin.row][newCoin.col]?.setImageResource(R.drawable.coin)
        }
    }

    // Move all obstacles one row down
    private fun moveAllObstacles() {
        val obstaclesToRemove = mutableListOf<Obstacle>()

        for (obstacle in obstacles) {
            // Clear current position
            if (obstacle.row < GRID_ROWS && obstacle.row != PLAYER_ROW) {
                gridCells[obstacle.row][obstacle.col]?.setImageDrawable(null)
            }

            obstacle.row++

            // Check if obstacle reached bottom
            if (obstacle.row >= GRID_ROWS) {
                val lastRow = GRID_ROWS - 1
                if (obstacle.col != gameManager.currentCarIndex) {
                    gridCells[lastRow][obstacle.col]?.setImageDrawable(null)
                }

                // Obstacle passed successfully - add score
                if (!obstacle.collisionDetected) {
                    gameManager.incrementScore()
                }

                obstaclesToRemove.add(obstacle)
                continue
            }

            // Check collision
            if (obstacle.row == PLAYER_ROW && obstacle.col == gameManager.currentCarIndex && !obstacle.collisionDetected) {
                obstacle.collisionDetected = true
                onCrash()
                val isGameOver = gameManager.handleCollision()
                updateScoreUI()
                updateLivesUI()
                obstaclesToRemove.add(obstacle)

                // Check if game over
                if (isGameOver) {
                    showGameOverDialog()
                }
                continue
            }

            gridCells[obstacle.row][obstacle.col]?.setImageResource(R.drawable.bear_market)
        }

        obstacles.removeAll(obstaclesToRemove)
    }

    // Part 2: Move all coins one row down
    private fun moveAllCoins() {
        val coinsToRemove = mutableListOf<Coin>()

        for (coin in coins) {
            // Clear current position
            if (coin.row < GRID_ROWS && coin.row != PLAYER_ROW) {
                // Don't clear if there's an obstacle here
                val hasObstacle = obstacles.any { it.row == coin.row && it.col == coin.col }
                if (!hasObstacle) {
                    gridCells[coin.row][coin.col]?.setImageDrawable(null)
                }
            }

            coin.row++

            // Check if coin reached bottom
            if (coin.row >= GRID_ROWS) {
                coinsToRemove.add(coin)
                continue
            }

            // Check collection
            if (coin.row == PLAYER_ROW && coin.col == gameManager.currentCarIndex && !coin.collected) {
                coin.collected = true
                gameManager.collectCoin()
                coinsToRemove.add(coin)
                continue
            }

            // Don't draw coin if obstacle is in same position
            val hasObstacle = obstacles.any { it.row == coin.row && it.col == coin.col }
            if (!hasObstacle) {
                gridCells[coin.row][coin.col]?.setImageResource(R.drawable.coin)
            }
        }

        coins.removeAll(coinsToRemove)
    }

    // Update player position in grid
    private fun updatePlayerPosition() {
        // Clear old positions
        for (col in 0 until GRID_COLS) {
            val hasObstacle = obstacles.any { it.row == PLAYER_ROW && it.col == col }
            val hasCoin = coins.any { it.row == PLAYER_ROW && it.col == col }
            if (!hasObstacle && !hasCoin) {
                gridCells[PLAYER_ROW][col]?.setImageDrawable(null)
            }
        }

        // Place player
        val playerCol = gameManager.currentCarIndex
        gridCells[PLAYER_ROW][playerCol]?.setImageResource(R.drawable.bitcoin)
    }

    // Check collision when player moves sideways into obstacle
    private fun checkSideCollision() {
        val obstaclesToRemove = mutableListOf<Obstacle>()

        for (obstacle in obstacles) {
            if (obstacle.row == PLAYER_ROW && obstacle.col == gameManager.currentCarIndex && !obstacle.collisionDetected) {
                obstacle.collisionDetected = true
                onCrash()
                val isGameOver = gameManager.handleCollision()
                updateScoreUI()
                updateLivesUI()
                obstaclesToRemove.add(obstacle)

                // Check if game over
                if (isGameOver) {
                    showGameOverDialog()
                }
            }
        }

        obstacles.removeAll(obstaclesToRemove)
    }

    // Part 2: Check coin collection when player moves
    private fun checkCoinCollection() {
        val coinsToRemove = mutableListOf<Coin>()

        for (coin in coins) {
            if (coin.row == PLAYER_ROW && coin.col == gameManager.currentCarIndex && !coin.collected) {
                coin.collected = true
                gameManager.collectCoin()
                coinsToRemove.add(coin)
            }
        }

        coins.removeAll(coinsToRemove)
    }

    // Update hearts display (lives)
    private fun updateLivesUI() {
        for (i in heartViews.indices) {
            heartViews[i].visibility = if (i < gameManager.lives) View.VISIBLE else View.INVISIBLE
        }
    }

    // Update score display
    private fun updateScoreUI() {
        textScore.text = getString(R.string.score_label, gameManager.score)
    }

    // Part 2: Update odometer display
    private fun updateOdometerUI() {
        textOdometer.text = getString(R.string.odometer_label, gameManager.distanceTraveled)
    }

    // Part 2: Update coins display
    private fun updateCoinsUI() {
        textCoins.text = getString(R.string.coins_label, gameManager.coinsCollected)
    }

    // Show crash notification - Toast, vibration, and sound
    private fun onCrash() {
        Toast.makeText(this, getString(R.string.crash_toast), Toast.LENGTH_SHORT).show()

        // Play crash sound
        crashSound?.start()

        // Vibrate
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    // ===== Part 2: Sensor Event Handling =====
    override fun onSensorChanged(event: SensorEvent?) {
        if (!useSensors || event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val currentTime = System.currentTimeMillis()
        if (currentTime - lastSensorUpdate < SENSOR_UPDATE_INTERVAL) return
        lastSensorUpdate = currentTime

        val x = event.values[0]  // Left/Right tilt
        val y = event.values[1]  // Forward/Back tilt (bonus feature)

        // Part 2: Move player based on tilt (negative X = tilt right, positive X = tilt left)
        if (x < -SENSOR_THRESHOLD) {
            gameManager.moveCarRight()
            updatePlayerPosition()
            checkSideCollision()
            checkCoinCollection()
        } else if (x > SENSOR_THRESHOLD) {
            gameManager.moveCarLeft()
            updatePlayerPosition()
            checkSideCollision()
            checkCoinCollection()
        }

        // Part 2: Bonus - Adjust speed based on forward/back tilt
        currentFrameRate = when {
            y < -3 -> 300L  // Tilt forward = faster
            y > 3 -> 800L   // Tilt back = slower
            else -> gameManager.frameRate
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
        if (useSensors) {
            sensorManager.unregisterListener(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::gameManager.isInitialized && gameManager.isGameRunning) {
            startTimer()
        }
        if (useSensors) {
            accelerometer?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        crashSound?.release()
        crashSound = null
    }

    // ===== Game Over Dialog =====
    // Show dialog to enter player name and save score
    private fun showGameOverDialog() {
        // Stop the game
        stopTimer()

        // Create input field for player name
        val inputEditText = EditText(this).apply {
            hint = "Enter your name"
            setPadding(50, 30, 50, 30)
        }

        // Build and show dialog
        AlertDialog.Builder(this)
            .setTitle("🎮 Game Over!")
            .setMessage("Your Score: ${gameManager.score} points\n\nEnter your name:")
            .setView(inputEditText)
            .setCancelable(false)
            .setPositiveButton("SAVE") { _, _ ->
                val playerName = inputEditText.text.toString().ifEmpty { "Player" }
                saveHighScore(playerName)
                goToMenu()
            }
            .show()
    }

    // Save high score with current location
    private fun saveHighScore(playerName: String) {
        val latitude = currentLocation?.latitude ?: 31.7683  // Default to Jerusalem
        val longitude = currentLocation?.longitude ?: 35.2137

        val highScore = HighScore(
            score = gameManager.score,
            playerName = playerName,
            latitude = latitude,
            longitude = longitude
        )

        highScoreManager.saveHighScore(highScore)
        Toast.makeText(this, "Score saved!", Toast.LENGTH_SHORT).show()
    }

    // Return to main menu
    private fun goToMenu() {
        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}

