# System Patterns - Architecture and Design Decisions

## Overall Architecture
**Client-Server Hybrid Pattern**
- Client-side: Camera manipulation, visual effects, input handling, entity detection
- Server-side: Entity validation, multiplayer synchronization (minimal for performance)
- Shared: Configuration system, targeting logic validation

## Implemented Components

### 1. Targeting System
```
TargetingManager (Client) - Core coordination
├── EntityDetector - Finds valid targets in range with line-of-sight
├── TargetSelector - Handles target prioritization (distance/health/threat)
├── CameraController - Manages smooth camera transitions
└── TargetTracker - Maintains current target state and validation
```

### 2. Visual System
```
RenderManager (Client)
├── TargetRenderer - Animated targeting reticles and HUD
│   ├── render3DReticle() - 3D world-space targeting rings
│   ├── drawReticleRing() - Circular reticles with animation
│   ├── drawCornerBrackets() - Corner bracket indicators
│   └── render2DHUD() - Target info, health bars, distance
└── Event Integration - Forge RenderGameOverlayEvent
```

### 3. Configuration System
```
TargetingConfig - Centralized configuration
├── Targeting Settings (range, angle, line-of-sight)
├── Visual Settings (reticle, HUD, colors, scale)
├── Camera Settings (smoothness, limits, enable/disable)
├── Entity Filtering (hostile, neutral, passive, players)
├── Audio Settings (sounds, volume)
└── Performance Settings (update frequency, validation interval)
```

### 4. Input System
```
KeyBindings - Customizable controls
├── lockOnToggle (default: R)
├── cycleTargetLeft (default: Q)
├── cycleTargetRight (default: E)
└── Forge ClientRegistry integration
```

### 5. GUI Configuration System
```
GuiTargetingConfig - Advanced multi-page configuration interface
├── Page 1: Targeting & Visual Settings (10 options)
│   ├── Targeting Range, Max Tracking Distance, Detection Angle
│   ├── Line of Sight Toggle, Visual Element Toggles
│   └── Reticle Scale Adjustment
├── Page 2: Camera Settings (7 options)
│   ├── Camera Lock-On Toggle, Smoothness, Pitch/Yaw Limits
│   ├── Auto Third Person Mode
│   └── Better Third Person Compatibility (3 modes)
├── Page 3: Entity Filtering, Audio & Performance (7 options)
│   ├── Entity Type Filtering (Hostile/Neutral/Passive)
│   ├── Audio Settings (Enable/Volume)
│   └── Performance Tuning (Update/Validation Frequency)
└── Advanced Features:
    ├── Left/Right Click for Increase/Decrease Values
    ├── Shift+Click for Fine Adjustment
    ├── Reset to Defaults Button
    └── Real-time Configuration Updates
```

## Key Design Patterns Implemented

### Observer Pattern
- **TargetingEvents**: Loose coupling between components
- **Forge Event System**: RenderGameOverlayEvent, KeyInputEvent, ClientTickEvent
- **Event-Driven Updates**: Efficient system coordination

### State Machine Pattern
- **Targeting States**: INACTIVE, SEARCHING, LOCKED, SWITCHING
- **Clean Transitions**: Proper state cleanup and validation
- **State Persistence**: Maintains targeting state across ticks

### Strategy Pattern
- **Target Selection**: Multiple algorithms (distance, health, threat, angle)
- **Entity Filtering**: Configurable targeting strategies
- **Priority Systems**: Flexible target prioritization

### Proxy Pattern
- **Client/Server Separation**: Clean architecture boundaries
- **CommonProxy**: Shared initialization logic
- **ClientProxy**: Client-specific rendering and input
- **ServerProxy**: Server-specific validation (minimal)

## Technical Implementation Details

### Camera Manipulation
- **Smooth Interpolation**: Custom interpolateAngle() and interpolateFloat() methods
- **Constraint System**: Configurable pitch/yaw limits to prevent jarring movements
- **State Preservation**: Stores and restores original camera rotation
- **Performance Optimized**: Efficient per-tick updates with configurable smoothing

### Entity Detection Algorithm
```java
1. Create bounding box search area (configurable range)
2. Get all entities within area using world.getEntitiesWithinAABB()
3. Filter by entity type (hostile/neutral/passive/players)
4. Validate distance constraints
5. Check angle from player look direction
6. Perform line-of-sight raytracing
7. Sort by priority (distance/health/threat)
8. Return best target
```

### Visual Rendering Pipeline
```java
1. RenderGameOverlayEvent.Post trigger
2. Check if targeting system is active
3. Get current target from TargetingManager
4. Calculate 3D world position with interpolation
5. Render 3D reticle with animation (pulsing scale)
6. Draw corner brackets for enhanced visibility
7. Render 2D HUD elements (name, health, distance)
8. Apply proper GL state management
```

### Performance Optimization Strategies
- **Configurable Update Frequencies**: Separate rates for detection vs validation
- **Spatial Queries**: Efficient chunk-based entity lookups
- **Client Prediction**: Reduces server load while maintaining responsiveness
- **Lazy Validation**: Periodic target validation instead of every tick
- **Memory Efficient**: Reuses objects, minimal garbage collection

### Multiplayer Architecture
- **Client Authority**: Camera and visual effects handled client-side
- **Server Validation**: Minimal validation to prevent cheating
- **Bandwidth Efficient**: No unnecessary network packets
- **Prediction System**: Client-side prediction with server reconciliation

## Integration Points

### Forge Integration
- **@Mod Annotation**: Proper mod registration and lifecycle
- **Event System**: MinecraftForge.EVENT_BUS registration
- **Proxy System**: @SidedProxy for client/server separation
- **Configuration**: Forge Configuration API integration

### Minecraft Integration
- **Entity System**: Works with all EntityLiving subclasses
- **Rendering Pipeline**: Integrates with Minecraft's render events
- **Input System**: Uses Minecraft's KeyBinding system
- **World Interaction**: Proper world.rayTraceBlocks() usage

### Mod Compatibility
- **Non-Intrusive**: Doesn't modify vanilla classes
- **Event-Based**: Uses standard Forge event system
- **Configurable**: All features can be disabled if needed
- **Clean Separation**: Client-only code properly isolated

## Error Handling & Edge Cases

### Target Validation
- **Entity Death**: Automatic target switching when target dies
- **Range Checking**: Continuous validation of target distance
- **Dimension Changes**: Handles player dimension switching
- **Line-of-Sight**: Dynamic obstacle detection

### Camera Safety
- **Constraint Limits**: Prevents extreme camera movements
- **State Recovery**: Always restores original camera state
- **Smooth Transitions**: Prevents jarring camera jumps
- **Performance Bounds**: Limits update frequency for stability

### Configuration Robustness
- **Default Values**: Sensible defaults for all settings
- **Range Validation**: Prevents invalid configuration values
- **Auto-Generation**: Creates config file if missing
- **Hot Reloading**: Configuration changes apply immediately

## GUI System Architecture

### Multi-Page Configuration Design
- **Page-Based Navigation**: 3 logical groupings of settings
- **Dynamic Button Generation**: Automatic layout based on setting types
- **Real-time Updates**: Changes apply immediately without restart
- **Input Validation**: Range checking and type validation for all settings

### Better Third Person Integration
- **Compatibility Detection**: Automatic BTP mod detection
- **Three Compatibility Modes**:
  - `disabled`: No BTP integration
  - `gentle`: Reduced camera movement intensity
  - `visual_only`: Visual targeting only, no camera control
- **Dynamic GUI**: Shows/hides BTP-specific options based on mode

### Advanced User Interaction
- **Mouse Controls**: Left-click increase, right-click decrease
- **Keyboard Modifiers**: Shift for fine adjustment increments
- **Visual Feedback**: Real-time display of current values
- **Persistence**: Automatic config file saving

## Scalability Considerations
- **Modular Design**: Easy to add new targeting modes or visual effects
- **Plugin Architecture**: New target selection strategies can be added
- **Configuration Expansion**: Easy to add new settings categories and pages
- **Performance Tuning**: All performance-critical values are configurable
- **GUI Extensibility**: Page system allows unlimited configuration expansion