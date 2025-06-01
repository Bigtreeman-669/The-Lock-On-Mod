# System Patterns & Architecture

## Core Architectural Patterns

### Thread-Safe Collection Management Pattern
**Context**: Minecraft's multi-threaded environment requires careful handling of shared data structures.

**Problem**: Damage numbers were stored in ArrayList, causing ConcurrentModificationException when game logic thread added numbers while render thread displayed them.

**Solution**: 
```java
// Thread-safe collection for damage numbers
private static final ConcurrentLinkedQueue<DamageNumber> damageNumbers = new ConcurrentLinkedQueue<>();

// Safe iteration pattern
Iterator<DamageNumber> iterator = damageNumbers.iterator();
while (iterator.hasNext()) {
    DamageNumber number = iterator.next();
    // Process safely without concurrent modification issues
}
```

**Benefits**:
- Eliminates race conditions between threads
- Maintains performance with lock-free operations
- Automatic cleanup without manual synchronization

### Multi-Page GUI Configuration Pattern
**Context**: Complex mod configurations require organized, user-friendly interfaces.

**Implementation**:
```java
// Page-based navigation system
private int currentPage = 0;
private final int totalPages = 5;

// Dynamic page content generation
switch (currentPage) {
    case 0: // Targeting & Visual Settings
    case 1: // Camera Settings  
    case 2: // Entity Filtering & Basic Audio
    case 3: // Advanced Sound Tweaking
    case 4: // Damage Numbers Configuration
}
```

**Features**:
- Scalable page system for feature growth
- Consistent navigation patterns
- Dynamic button positioning and spacing
- Page-specific header display

### Configuration Persistence Pattern
**Context**: User settings must persist across game sessions with real-time updates.

**Implementation**:
```java
// Immediate persistence on configuration change
private void handleConfigButton(GuiButton button, boolean decrease) {
    // Modify configuration values
    TargetingConfig.someValue = newValue;
    
    // Update button display
    button.displayString = "Setting: " + newValue;
    
    // Immediate save - no user action required
    TargetingConfig.saveConfig();
}
```

**Benefits**:
- No configuration loss on game crashes
- Immediate feedback to user changes
- Simplified user experience (no manual save)

### Color Cycling System Pattern
**Context**: User-friendly color selection without complex color picker UI.

**Implementation**:
```java
private void cycleDamageNumberColor(String colorType) {
    // Predefined palette for consistent UX
    int[] colorOptions = {
        0xFFFFFF, // White
        0xFF0000, // Red
        // ... 16 total colors
    };
    
    // Find current index and cycle to next
    int currentIndex = findColorIndex(currentColor);
    int nextIndex = (currentIndex + 1) % colorOptions.length;
    
    // Apply new color with immediate persistence
    applyColorChange(colorType, colorOptions[nextIndex]);
}
```

**Benefits**:
- Simple left-click cycling interface
- Curated color palette for good visual results
- Hex display for technical users
- Consistent color options across all color settings

## Minecraft-Specific Patterns

### Dynamic GUI Scaling Pattern
**Context**: Minecraft GUIs must adapt to different screen sizes and GUI scale settings.

**Implementation**:
```java
// Calculate effective screen dimensions
int scaleFactor = this.mc.gameSettings.guiScale;
if (scaleFactor == 0) scaleFactor = 1000;
int scaledWidth = this.mc.displayWidth / scaleFactor;
int scaledHeight = this.mc.displayHeight / scaleFactor;

// Use safe dimensions for button placement
int effectiveWidth = Math.min(this.width, scaledWidth);
int effectiveHeight = Math.min(this.height, scaledHeight);

// Dynamic spacing based on available space
int spacing = Math.max(20, Math.min(25, (effectiveHeight - startY - 80) / 15));
```

**Benefits**:
- Works across all GUI scale settings
- Adapts to different screen resolutions
- Prevents button overflow and clipping
- Maintains usable spacing in all configurations

### Entity Rendering Integration Pattern
**Context**: Custom rendering must integrate with Minecraft's existing render pipeline.

**Implementation**:
```java
// Render during appropriate phase
@SubscribeEvent
public static void onRenderWorldLast(RenderWorldLastEvent event) {
    if (TargetingConfig.enableDamageNumbers) {
        renderDamageNumbers(event.getPartialTicks());
    }
}

// Thread-safe access to damage data
private static void renderDamageNumbers(float partialTicks) {
    Iterator<DamageNumber> iterator = damageNumbers.iterator();
    while (iterator.hasNext()) {
        // Safe iteration without locks
    }
}
```

**Benefits**:
- Integrates with Minecraft's render cycle
- Respects configuration toggles
- Thread-safe access to render data
- Proper cleanup and resource management

### Configuration Button Handler Pattern
**Context**: Complex configuration systems need consistent, maintainable button handling.

**Implementation**:
```java
private void handleConfigButton(GuiButton button, boolean decrease) {
    // Detect fine adjustment mode
    boolean isShiftPressed = org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_LSHIFT) ||
                           org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RSHIFT);
    
    switch (button.id) {
        case SOME_VALUE_BUTTON:
            float increment = isShiftPressed ? 0.05f : 0.1f;
            TargetingConfig.someValue = cycleValue(currentValue, minValue, maxValue, increment, decrease);
            button.displayString = "Label: " + String.format("%.2f", TargetingConfig.someValue);
            break;
        case SOME_TOGGLE_BUTTON:
            TargetingConfig.someToggle = !TargetingConfig.someToggle;
            button.displayString = "Label: " + (TargetingConfig.someToggle ? "ON" : "OFF");
            break;
    }
    
    // Consistent persistence
    TargetingConfig.saveConfig();
}
```

**Benefits**:
- Consistent behavior across all controls
- Fine adjustment support with shift key
- Immediate visual feedback
- Centralized persistence logic

## Performance Optimization Patterns

### Efficient Value Cycling Pattern
**Context**: Numeric configuration values need intuitive wrap-around behavior.

**Implementation**:
```java
private float cycleValue(float currentValue, float minValue, float maxValue, float increment, boolean decrease) {
    float newValue;
    if (decrease) {
        newValue = currentValue - increment;
        if (newValue < minValue) {
            newValue = maxValue; // Wrap to max
        }
    } else {
        newValue = currentValue + increment;
        if (newValue > maxValue) {
            newValue = minValue; // Wrap to min
        }
    }
    return newValue;
}
```

**Benefits**:
- Intuitive wrap-around behavior
- Consistent increment/decrement logic
- Bounds checking prevents invalid values
- Reusable across all numeric controls

### Helper Method Pattern for GUI Components
**Context**: Complex GUI components need reusable, consistent creation methods.

**Implementation**:
```java
// Specialized helper methods for different control types
private void addToggleButton(int id, int x, int y, int width, int height, String name, boolean currentValue) {
    String displayText = name + ": " + (currentValue ? "ON" : "OFF");
    GuiButton button = new GuiButton(id, x, y, width, height, displayText);
    this.buttonList.add(button);
}

private void addColorButton(int id, int x, int y, int width, int height, String name, int currentColor) {
    String colorHex = String.format("#%06X", currentColor & 0xFFFFFF);
    String displayText = name + ": " + colorHex;
    GuiButton button = new GuiButton(id, x, y, width, height, displayText);
    this.buttonList.add(button);
}
```

**Benefits**:
- Consistent visual styling across components
- Reduced code duplication
- Easier maintenance and updates
- Type-specific formatting logic

## Error Prevention Patterns

### Safe Configuration Access Pattern
**Context**: Configuration values must be validated and have safe defaults.

**Implementation**:
```java
// Safe bounds checking in value cycling
TargetingConfig.damageNumbersScale = cycleValue(
    TargetingConfig.damageNumbersScale, 
    0.5f,  // Minimum safe value
    3.0f,  // Maximum reasonable value
    increment, 
    decrease
);

// Configuration validation on load
public static void validateConfig() {
    if (damageNumbersScale < 0.5f || damageNumbersScale > 3.0f) {
        damageNumbersScale = 1.0f; // Safe default
    }
}
```

**Benefits**:
- Prevents invalid configuration states
- Provides reasonable defaults
- Handles edge cases gracefully
- Maintains system stability

### Thread Safety Documentation Pattern
**Context**: Complex threading relationships need clear documentation.

**Implementation**:
```java
/**
 * Thread-safe collection for damage numbers.
 * - Game logic thread: Adds new damage numbers via addDamageNumber()
 * - Render thread: Iterates and displays via renderDamageNumbers()
 * - Cleanup thread: Removes expired numbers automatically
 * 
 * Uses ConcurrentLinkedQueue to prevent ConcurrentModificationException
 * that occurred with previous ArrayList implementation.
 */
private static final ConcurrentLinkedQueue<DamageNumber> damageNumbers = new ConcurrentLinkedQueue<>();
```

**Benefits**:
- Clear threading contract documentation
- Prevents future threading issues
- Explains architectural decisions
- Guides future modifications

## Integration Patterns

### Mod Loader Compatibility Pattern
**Context**: Code must work across different mod loaders and Minecraft versions.

**Implementation**:
```java
// Event handling compatible with Forge
@SubscribeEvent
public static void onRenderWorldLast(RenderWorldLastEvent event) {
    // Implementation
}

// Configuration persistence using standard Java properties
public static void saveConfig() {
    // Standard file I/O that works across platforms
}
```

**Benefits**:
- Maximizes compatibility
- Uses standard APIs where possible
- Reduces platform-specific code
- Facilitates future porting

These patterns form the foundation of a maintainable, scalable, and user-friendly Minecraft mod architecture that handles complex configuration, threading, and user interface challenges effectively.