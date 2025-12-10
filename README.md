# 🐻 Bit-Bear Game

An endless Android arcade game where you control a Bitcoin and dodge falling bears in a crypto-themed adventure!

## 📱 Game Overview

**Bit-Bear** is a fast-paced endless runner game built for Android. Navigate your Bitcoin character across three lanes while avoiding falling bear market obstacles. The game features:

- **Endless Gameplay** - The game never stops! When you lose all lives, it automatically resets
- **Grid-Based Movement** - Clean, discrete cell-to-cell movement (no Canvas rendering)
- **Animated Background** - Dynamic GIF background for visual appeal
- **Score Tracking** - Earn 10 points for every obstacle you successfully dodge
- **Lives System** - 3 hearts that reset when depleted
- **Haptic Feedback** - Device vibration on collision

---

## 🎮 How to Play

### Controls
- **Left Arrow Button** - Move Bitcoin one lane to the left
- **Right Arrow Button** - Move Bitcoin one lane to the right

### Objective
- Dodge the falling bears by moving between lanes
- Each bear you successfully avoid awards you **+10 points**
- If you collide with a bear, you lose **1 heart** (life)
- When all 3 hearts are lost, the game resets automatically

### Scoring
- **+10 points** - Successfully avoiding a bear obstacle
- **-1 life** - Colliding with a bear
- **Game Reset** - Score and lives reset to 0 when all hearts are lost

---

## 🛠️ Technical Details

### Built With
- **Language**: Kotlin
- **Min SDK**: 26 (Android 8.0 Oreo)
- **Target SDK**: 36
- **Architecture**: MVVM-like pattern with separate UI and Logic layers

### Key Technologies
- **RelativeLayout & TableLayout** - Grid-based UI (no Canvas)
- **Glide Library** - For animated GIF background rendering
- **Android Vibrator API** - Haptic feedback on collisions
- **Handler/Runnable** - Game loop timing

### Project Structure
```
app/src/main/
├── java/com/example/myapplication/
│   ├── ui/
│   │   └── MainActivity.kt          # Main game activity & UI logic
│   └── logic/
│       └── GameManager.kt           # Game state & business logic
├── res/
│   ├── drawable/
│   │   ├── bitcoin.png              # Player character
│   │   ├── bear_market.png          # Obstacle (bear)
│   │   ├── tenor.gif                # Animated background
│   │   ├── heart.png                # Lives indicator
│   │   ├── arrow_left.png           # Left button icon
│   │   └── arrow_right.png          # Right button icon
│   └── layout/
│       └── activity_main.xml        # Main game layout
└── AndroidManifest.xml
```

---

## 📋 Features

### ✅ Core Gameplay
- [x] 3-lane grid system (14 rows × 3 columns)
- [x] Player movement (left/right)
- [x] Falling obstacles from top to bottom
- [x] Collision detection
- [x] Lives system (3 hearts)
- [x] Score tracking
- [x] Endless game mode with auto-reset

### ✅ Visual & Audio
- [x] Animated GIF background
- [x] Custom Bitcoin and Bear icons
- [x] Heart icons for lives display
- [x] Toast notifications on collision
- [x] Vibration feedback

### ✅ Technical Requirements
- [x] No Canvas usage (pure ViewGroup-based rendering)
- [x] Grid-based discrete movement (no smooth X/Y coordinates)
- [x] Forced LTR layout (works in Hebrew/RTL languages)
- [x] Responsive design (adapts to different screen sizes)

---

## 🚀 Installation & Setup

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK 26 or higher
- Gradle 8.0+

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

3. **Sync Gradle**
   - Let Android Studio sync Gradle dependencies
   - Wait for build to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button (or press Shift+F10)

---

## 🎯 Game Logic

### Grid System
- **14 Rows × 3 Columns** - Provides smooth falling motion
- **Player Position** - Fixed at row 13 (bottom row)
- **Obstacle Spawn** - Bears spawn at row 0 (top) in random columns
- **Frame Rate** - 600ms per tick (0.6 seconds)

### Collision Detection
The game checks for collisions in two scenarios:
1. **Vertical Collision** - Bear falls onto the player's position
2. **Horizontal Collision** - Player moves sideways into a bear

### Spawn Logic
- Bears spawn every 4-6 ticks (2.4-3.6 seconds)
- Random column selection (0, 1, or 2)
- Multiple bears can exist simultaneously

---

## 🐛 Known Issues & Solutions

### Issue: Icons changing size during gameplay
**Solution**: Set `adjustViewBounds = false` in ImageView configuration to maintain consistent icon sizes.

### Issue: Bears stuck at bottom
**Fixed**: Proper cleanup logic ensures bears are removed when reaching the bottom or after collision.

### Issue: Bitcoin visibility when bear on same row
**Fixed**: Layering logic prioritizes Bitcoin rendering over bears at player row.

---

## 📝 Development Notes

### Design Decisions
- **No Canvas** - Per assignment requirements, all rendering uses ViewGroups
- **Grid-based Movement** - Discrete cell jumps instead of smooth pixel movement
- **Endless Mode** - Game auto-resets for continuous play
- **LTR Enforcement** - Ensures consistent behavior regardless of device language

### Performance Considerations
- Uses `Handler.postDelayed()` for efficient game loop
- Minimal object creation during gameplay
- Proper cleanup of off-screen obstacles

---

## 📜 License

This project is created for educational purposes as part of an Android development course.

---

## 👨‍💻 Author

Created as part of HW1 - Android Development Course

---

## 🎓 Assignment Requirements Met

✅ Three-lane road system  
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

---

## 📞 Support

For issues or questions, please check the code comments or contact the development team.

**Enjoy dodging those bears! 🐻💰**

