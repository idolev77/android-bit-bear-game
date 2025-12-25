# 🐻 Bit-Bear Game

An endless Android arcade game where you control a Bitcoin and dodge falling bears in a crypto-themed adventure!

## 📱 Game Overview

**Bit-Bear** is a fast-paced endless runner game built for Android. Navigate your Bitcoin character across **five lanes** while avoiding falling bear market obstacles and collecting coins. The game features:

- **Endless Gameplay** - The game never stops! When you lose all lives, it automatically resets
- **Grid-Based Movement** - Clean, discrete cell-to-cell movement (no Canvas rendering)
- **5-Lane Road** - Part 2 expanded from 3 to 5 lanes for more strategic gameplay
- **Dual Control Modes** - Button controls or Accelerometer (sensor) controls
- **Collectible Coins** - Earn bonus points by collecting coins on the road
- **Odometer** - Track distance traveled during gameplay
- **Animated Background** - Dynamic background image for visual appeal
- **Score Tracking** - Earn points for dodging obstacles and collecting coins
- **Lives System** - 3 hearts that reset when depleted
- **Haptic Feedback & Sound** - Device vibration and crash sound on collision
- **High Scores with Map** - Top 10 leaderboard with GPS locations displayed on Google Maps

---

## 🎮 How to Play

### Menu Screen
- **🎮 Buttons - Slow** - Play with on-screen arrow buttons at slow speed
- **🎮 Buttons - Fast** - Play with on-screen arrow buttons at fast speed
- **📱 Sensor Mode** - Play using phone tilt (accelerometer)
- **Top 10 Scores** - View the leaderboard with map locations

### Controls
**Button Mode:**
- **Left Arrow Button** - Move Bitcoin one lane to the left
- **Right Arrow Button** - Move Bitcoin one lane to the right

**Sensor Mode:**
- **Tilt Phone Left** - Move Bitcoin to the left
- **Tilt Phone Right** - Move Bitcoin to the right
- **Tilt Forward/Back** - Speed up or slow down the game (bonus feature)

### Objective
- Dodge the falling bears by moving between 5 lanes
- Collect coins for bonus points
- Each bear you successfully avoid awards you **+10 points**
- Each coin collected awards you **+5 points**
- If you collide with a bear, you lose **1 heart** (life)
- When all 3 hearts are lost, the game resets automatically

### Scoring
- **+10 points** - Successfully avoiding a bear obstacle
- **+5 points** - Collecting a coin
- **-1 life** - Colliding with a bear
- **Game Reset** - Score and lives reset when all hearts are lost

---

## 🛠️ Technical Details

### Built With
- **Language**: Kotlin
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 36
- **Architecture**: MVVM-like pattern with separate UI and Logic layers

### Key Technologies
- **RelativeLayout & TableLayout** - Grid-based UI (no Canvas)
- **Glide Library** - For animated background image rendering
- **Android Vibrator API** - Haptic feedback on collisions
- **SensorManager & Accelerometer** - Tilt-based controls
- **Google Maps SDK** - Display high score locations
- **FusedLocationProvider** - Get device GPS location
- **SharedPreferences + Gson** - Persist high scores locally
- **RecyclerView** - Display high score list
- **Fragments** - List and Map fragments for Top Ten screen
- **ViewBinding** - Type-safe view access
- **Handler/Runnable** - Game loop timing

### Project Structure
```
app/src/main/
├── java/com/example/myapplication/
│   ├── ui/
│   │   ├── MainActivity.kt              # Main game activity & UI logic
│   │   ├── MenuActivity.kt              # Main menu screen (Part 2)
│   │   ├── TopTenActivity.kt            # High scores activity (Part 2)
│   │   ├── HighScoreListFragment.kt     # RecyclerView fragment (Part 2)
│   │   ├── HighScoreMapFragment.kt      # Google Maps fragment (Part 2)
│   │   └── HighScoreAdapter.kt          # RecyclerView adapter (Part 2)
│   ├── logic/
│   │   └── GameManager.kt               # Game state & business logic
│   └── data/
│       ├── HighScore.kt                 # High score data class (Part 2)
│       └── HighScoreManager.kt          # SharedPreferences persistence (Part 2)
├── res/
│   ├── drawable/
│   │   ├── bitcoin.xml                  # Player character
│   │   ├── bear_market.png              # Obstacle (bear)
│   │   ├── coin.xml                     # Collectible coin (Part 2)
│   │   ├── big_bear_back.png            # Animated background
│   │   ├── heart.xml                    # Lives indicator
│   │   └── button_background.xml        # Button styling
│   └── layout/
│       ├── activity_main.xml            # Main game layout
│       ├── activity_menu.xml            # Menu screen layout (Part 2)
│       ├── activity_top_ten.xml         # High scores layout (Part 2)
│       ├── fragment_high_score_list.xml # List fragment layout (Part 2)
│       ├── fragment_high_score_map.xml  # Map fragment layout (Part 2)
│       └── item_high_score.xml          # RecyclerView item (Part 2)
└── AndroidManifest.xml
```

---

## 📋 Features

### ✅ Core Gameplay (Part 1 + Part 2)
- [x] 5-lane grid system (16 rows × 5 columns) - expanded from 3 lanes
- [x] Player movement (left/right)
- [x] Falling obstacles from top to bottom
- [x] Collectible coins on the road (Part 2)
- [x] Odometer - distance counter (Part 2)
- [x] Collision detection
- [x] Lives system (3 hearts)
- [x] Score tracking
- [x] Endless game mode with auto-reset

### ✅ Controls (Part 2)
- [x] Button-based controls with Slow mode
- [x] Button-based controls with Fast mode
- [x] Sensor-based controls (accelerometer tilt)
- [x] Tilt forward/back for speed control (bonus)

### ✅ Menu Screen (Part 2)
- [x] 🎮 Buttons - Slow button
- [x] 🎮 Buttons - Fast button
- [x] 📱 Sensor Mode button
- [x] Top 10 Scores button

### ✅ High Scores (Part 2)
- [x] Top 10 leaderboard with RecyclerView
- [x] Google Maps integration with markers
- [x] GPS location capture for each score
- [x] Click score to zoom map to location
- [x] SharedPreferences + Gson persistence
- [x] Multiple scores at same location shown with small offset

### ✅ Visual & Audio
- [x] Animated background image
- [x] Custom Bitcoin and Bear icons
- [x] Collectible coin icons
- [x] Heart icons for lives display
- [x] Toast notifications on collision
- [x] Vibration feedback
- [x] Crash sound effect (optional)

### ✅ Technical Requirements
- [x] No Canvas usage (pure ViewGroup-based rendering)
- [x] Grid-based discrete movement (no smooth X/Y coordinates)
- [x] Forced LTR layout (works in Hebrew/RTL languages)
- [x] Responsive design (adapts to different screen sizes)
- [x] ViewBinding enabled
- [x] Location permissions handling

---

## 🗺️ Google Maps Setup

To enable Google Maps functionality:

1. **Get an API Key**
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select existing one
   - Enable "Maps SDK for Android"
   - Create an API key

2. **Add API Key to Project**
   - Open `app/src/main/res/values/strings.xml`
   - Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key:
   ```xml
   <string name="google_maps_key">YOUR_ACTUAL_API_KEY</string>
   ```

3. **Restrict API Key (Recommended)**
   - In Google Cloud Console, restrict the key to your app's package name
   - Add SHA-1 certificate fingerprint for security

**Note:** If multiple scores are recorded at the same GPS location (e.g., playing from home), each score will be displayed on the map with a small offset (~11 meters) so all markers are visible and clickable.

---

## 🚀 Installation & Setup

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK 26 or higher
- Gradle 8.0+
- Google Maps API Key (for high scores map)

### Steps
1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd part1_new
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an existing project"
   - Navigate to the project folder

3. **Configure Google Maps API Key**
   - Follow the Google Maps Setup section above

4. **Sync Gradle**
   - Let Android Studio sync Gradle dependencies
   - Wait for build to complete

5. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button (or press Shift+F10)

---

## 🎯 Game Logic

### Grid System
- **16 Rows × 5 Columns** - Provides smooth falling motion with 5-lane gameplay
- **Player Position** - Fixed at row 15 (bottom row)
- **Obstacle Spawn** - Bears spawn at row 0 (top) in random columns
- **Coin Spawn** - Coins spawn randomly across all 5 lanes
- **Frame Rate** - 700ms per tick in slow mode, 250ms in fast mode

### Collision Detection
The game checks for collisions in two scenarios:
1. **Vertical Collision** - Bear falls onto the player's position
2. **Horizontal Collision** - Player moves sideways into a bear

### Spawn Logic
- Bears spawn every 4-6 ticks
- Coins spawn randomly on the road
- Random column selection (0, 1, 2, 3, or 4)
- Multiple bears and coins can exist simultaneously

---

## 📝 Development Notes

### Design Decisions
- **No Canvas** - Per assignment requirements, all rendering uses ViewGroups
- **Grid-based Movement** - Discrete cell jumps instead of smooth pixel movement
- **Endless Mode** - Game auto-resets for continuous play
- **LTR Enforcement** - Ensures consistent behavior regardless of device language
- **Map Marker Offset** - Multiple scores at the same GPS location are displayed with small offset (~11 meters) to make all markers visible instead of overlapping

### Performance Considerations
- Uses `Handler.postDelayed()` for efficient game loop
- Minimal object creation during gameplay
- Proper cleanup of off-screen obstacles

---

## 🎓 Assignment Requirements Met

### Part 1 - Core Game
✅ Three-lane road system (expanded to 5 lanes in Part 2)
✅ Player movement (left/right)  
✅ Falling obstacles  
✅ Constant obstacle speed  
✅ Collision detection with Toast notification  
✅ Vibration feedback  
✅ 3 lives system  
✅ Endless game mode  
✅ No Canvas usage  
✅ RelativeLayout-based UI  
✅ Responsive design  

### Part 2 - Enhanced Features
✅ 5-lane road system (16 rows × 5 columns)  
✅ Collectible coins with scoring  
✅ Odometer (distance tracking)  
✅ Menu screen with 3 game mode buttons  
✅ Button control modes (Slow/Fast)  
✅ Sensor control mode  
✅ Speed differentiation (Fast: 250ms, Slow: 700ms)  
✅ Top 10 high scores with RecyclerView  
✅ Google Maps integration with GPS locations  
✅ Fragments for list and map views  
✅ SharedPreferences data persistence  
✅ Location permissions and FusedLocationProvider  

---

**Enjoy dodging those bears! 🐻💰**

