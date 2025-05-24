# Technical Context - Technologies and Constraints

## Core Technologies
- **Minecraft Version**: 1.12.2
- **Forge Version**: 14.23.5.2859
- **Java Version**: 1.8 (required for MC 1.12.2)
- **Gradle**: ForgeGradle 3.x
- **MCP Mappings**: snapshot_20171003-1.12

## Development Environment
- **Build System**: Gradle with ForgeGradle plugin
- **IDE Compatibility**: Eclipse, IntelliJ IDEA, VSCode
- **Testing**: Client and server run configurations included
- **Debugging**: Forge logging system with configurable levels

## Key Dependencies
```gradle
minecraft 'net.minecraftforge:forge:1.12.2-14.23.5.2859'
```

## Technical Constraints

### Minecraft 1.12.2 Limitations
- No modern rendering pipeline (pre-1.14 rendering)
- Limited client-server communication APIs
- Older event system compared to modern Forge
- Java 8 language features only

### Forge 1.12.2 Specific APIs
- `@Mod` annotation system
- FML event handlers (`@EventHandler`)
- Client/Server proxy pattern required
- Manual registration of components

### Performance Requirements
- 60fps target on mid-range hardware
- Minimal server tick impact (<1ms per tick)
- Memory efficient entity tracking
- Smooth camera interpolation without stuttering

## Development Setup Commands
```bash
# Build the mod
./gradlew build

# Run client for testing
./gradlew runClient

# Run server for testing
./gradlew runServer

# Generate IDE files
./gradlew genEclipseRuns  # For Eclipse
./gradlew genIntellijRuns # For IntelliJ
```

## File Structure Requirements
```
src/main/java/
├── com/zeldatargeting/mod/
│   ├── ZeldaTargetingMod.java (main mod class)
│   ├── proxy/ (client/server separation)
│   │   ├── CommonProxy.java
│   │   ├── ClientProxy.java
│   │   └── ServerProxy.java
│   ├── client/ (client-only code)
│   │   ├── KeyBindings.java
│   │   ├── TargetingManager.java
│   │   ├── gui/ (configuration GUI)
│   │   │   ├── GuiFactory.java
│   │   │   └── GuiTargetingConfig.java (417 lines)
│   │   ├── render/
│   │   │   └── TargetRenderer.java
│   │   └── targeting/ (core targeting system)
│   │       ├── EntityDetector.java
│   │       ├── TargetSelector.java
│   │       ├── TargetTracker.java
│   │       └── CameraController.java
│   └── config/ (configuration system)
│       └── TargetingConfig.java (251 lines, 23 settings)
src/main/resources/
├── mcmod.info (mod metadata)
├── pack.mcmeta (resource pack info)
└── assets/zeldatargeting/
    └── lang/en_us.lang (localization)
```

## Compatibility Considerations
- Must not conflict with OptiFine
- Should work with common mods (JEI, Baubles, etc.)
- **Better Third Person Integration**: Full compatibility with 3 modes
- Multiplayer server compatibility essential
- Forge version compatibility within 1.12.2 range

## Advanced Configuration System
- **Multi-page GUI**: 3 pages with 23 total settings
- **Real-time Updates**: No restart required for configuration changes
- **File Persistence**: Automatic config file generation and saving
- **Input Validation**: Range checking and type safety
- **Better Third Person Detection**: Automatic mod detection and compatibility

## Security Constraints
- Client-side only camera manipulation
- Server validation for targeting ranges
- No direct player movement control
- Prevent targeting through walls (line-of-sight)
- Configuration bounds checking to prevent invalid values