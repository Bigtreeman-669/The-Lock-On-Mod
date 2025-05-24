package com.zeldatargeting.mod.config;

import com.zeldatargeting.mod.ZeldaTargetingMod;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class TargetingConfig {
    
    private static Configuration config;
    
    // Targeting Settings
    public static double targetingRange = 16.0;
    public static double maxTrackingDistance = 20.0;
    public static double maxAngle = 60.0;
    public static boolean requireLineOfSight = true;
    
    // Visual Settings
    public static boolean showReticle = true;
    public static boolean showHealthBar = true;
    public static boolean showDistance = true;
    public static boolean showTargetName = true;
    public static float reticleScale = 1.0f;
    public static int reticleColor = 0xFF0000; // Red
    
    // Camera Settings
    public static float cameraSmoothness = 0.4f;
    public static float maxPitchAdjustment = 60.0f;
    public static float maxYawAdjustment = 90.0f;
    public static boolean enableCameraLockOn = true;
    public static boolean autoThirdPerson = false;
    public static String btpCompatibilityMode = "gentle"; // "disabled", "gentle", "visual_only"
    public static float btpCameraIntensity = 0.3f; // 0.0 to 1.0 for gentle mode
    
    // Entity Filtering
    public static boolean targetHostileMobs = true;
    public static boolean targetNeutralMobs = false;
    public static boolean targetPassiveMobs = false;
    public static boolean targetPlayers = false;
    
    // Audio Settings
    public static boolean enableSounds = true;
    public static float soundVolume = 1.0f;
    
    // Performance Settings
    public static int updateFrequency = 1; // ticks between updates
    public static int validationInterval = 10; // ticks between target validation
    
    public static void init(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "zeldatargeting.cfg");
        config = new Configuration(configFile);
        
        loadConfig();
    }
    
    public static void loadConfig() {
        try {
            config.load();
            
            // Targeting Settings
            targetingRange = config.getFloat("targetingRange", "targeting", (float) targetingRange, 5.0f, 50.0f,
                "Maximum range for target detection");
            maxTrackingDistance = config.getFloat("maxTrackingDistance", "targeting", (float) maxTrackingDistance, 5.0f, 100.0f,
                "Maximum distance to maintain target lock");
            maxAngle = config.getFloat("maxAngle", "targeting", (float) maxAngle, 15.0f, 180.0f,
                "Maximum angle from look direction to detect targets (degrees)");
            requireLineOfSight = config.getBoolean("requireLineOfSight", "targeting", requireLineOfSight,
                "Require line of sight to target entities");
            
            // Visual Settings
            showReticle = config.getBoolean("showReticle", "visual", showReticle,
                "Show targeting reticle around locked target");
            showHealthBar = config.getBoolean("showHealthBar", "visual", showHealthBar,
                "Show health bar for targeted entities");
            showDistance = config.getBoolean("showDistance", "visual", showDistance,
                "Show distance to target");
            showTargetName = config.getBoolean("showTargetName", "visual", showTargetName,
                "Show name of targeted entity");
            reticleScale = config.getFloat("reticleScale", "visual", reticleScale, 0.5f, 3.0f,
                "Scale of the targeting reticle");
            
            // Camera Settings
            cameraSmoothness = config.getFloat("cameraSmoothness", "camera", cameraSmoothness, 0.01f, 1.0f,
                "Smoothness of camera transitions (lower = smoother)");
            maxPitchAdjustment = config.getFloat("maxPitchAdjustment", "camera", maxPitchAdjustment, 0.0f, 90.0f,
                "Maximum pitch adjustment for camera lock-on (degrees)");
            maxYawAdjustment = config.getFloat("maxYawAdjustment", "camera", maxYawAdjustment, 0.0f, 180.0f,
                "Maximum yaw adjustment for camera lock-on (degrees)");
            enableCameraLockOn = config.getBoolean("enableCameraLockOn", "camera", enableCameraLockOn,
                "Enable camera lock-on to targets");
            autoThirdPerson = config.getBoolean("autoThirdPerson", "camera", autoThirdPerson,
                "Automatically switch to third person when locking on to targets");
            btpCompatibilityMode = config.getString("btpCompatibilityMode", "camera", btpCompatibilityMode,
                "Better Third Person compatibility mode: disabled, gentle, visual_only");
            btpCameraIntensity = config.getFloat("btpCameraIntensity", "camera", btpCameraIntensity, 0.0f, 1.0f,
                "Camera movement intensity when in BTP gentle mode (0.0 = no movement, 1.0 = full movement)");
            
            // Entity Filtering
            targetHostileMobs = config.getBoolean("targetHostileMobs", "entities", targetHostileMobs,
                "Allow targeting hostile mobs");
            targetNeutralMobs = config.getBoolean("targetNeutralMobs", "entities", targetNeutralMobs,
                "Allow targeting neutral mobs");
            targetPassiveMobs = config.getBoolean("targetPassiveMobs", "entities", targetPassiveMobs,
                "Allow targeting passive mobs");
            targetPlayers = config.getBoolean("targetPlayers", "entities", targetPlayers,
                "Allow targeting other players");
            
            // Audio Settings
            enableSounds = config.getBoolean("enableSounds", "audio", enableSounds,
                "Enable targeting sound effects");
            soundVolume = config.getFloat("soundVolume", "audio", soundVolume, 0.0f, 1.0f,
                "Volume of targeting sound effects");
            
            // Performance Settings
            updateFrequency = config.getInt("updateFrequency", "performance", updateFrequency, 1, 20,
                "Ticks between targeting system updates (higher = better performance, lower responsiveness)");
            validationInterval = config.getInt("validationInterval", "performance", validationInterval, 1, 60,
                "Ticks between target validation checks");
            
        } catch (Exception e) {
            ZeldaTargetingMod.getLogger().error("Error loading configuration", e);
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
    
    public static void saveConfig() {
        if (config != null) {
            try {
                // Update all config properties with current values
                config.get("targeting", "targetingRange", (float) targetingRange).set((float) targetingRange);
                config.get("targeting", "maxTrackingDistance", (float) maxTrackingDistance).set((float) maxTrackingDistance);
                config.get("targeting", "maxAngle", (float) maxAngle).set((float) maxAngle);
                config.get("targeting", "requireLineOfSight", requireLineOfSight).set(requireLineOfSight);
                
                config.get("visual", "showReticle", showReticle).set(showReticle);
                config.get("visual", "showHealthBar", showHealthBar).set(showHealthBar);
                config.get("visual", "showDistance", showDistance).set(showDistance);
                config.get("visual", "showTargetName", showTargetName).set(showTargetName);
                config.get("visual", "reticleScale", reticleScale).set(reticleScale);
                
                config.get("camera", "cameraSmoothness", cameraSmoothness).set(cameraSmoothness);
                config.get("camera", "maxPitchAdjustment", maxPitchAdjustment).set(maxPitchAdjustment);
                config.get("camera", "maxYawAdjustment", maxYawAdjustment).set(maxYawAdjustment);
                config.get("camera", "enableCameraLockOn", enableCameraLockOn).set(enableCameraLockOn);
                config.get("camera", "autoThirdPerson", autoThirdPerson).set(autoThirdPerson);
                config.get("camera", "btpCompatibilityMode", btpCompatibilityMode).set(btpCompatibilityMode);
                config.get("camera", "btpCameraIntensity", btpCameraIntensity).set(btpCameraIntensity);
                
                config.get("entities", "targetHostileMobs", targetHostileMobs).set(targetHostileMobs);
                config.get("entities", "targetNeutralMobs", targetNeutralMobs).set(targetNeutralMobs);
                config.get("entities", "targetPassiveMobs", targetPassiveMobs).set(targetPassiveMobs);
                config.get("entities", "targetPlayers", targetPlayers).set(targetPlayers);
                
                config.get("audio", "enableSounds", enableSounds).set(enableSounds);
                config.get("audio", "soundVolume", soundVolume).set(soundVolume);
                
                config.get("performance", "updateFrequency", updateFrequency).set(updateFrequency);
                config.get("performance", "validationInterval", validationInterval).set(validationInterval);
                
                // Force save
                config.save();
                
                ZeldaTargetingMod.getLogger().info("Configuration saved successfully");
            } catch (Exception e) {
                ZeldaTargetingMod.getLogger().error("Error saving configuration", e);
            }
        }
    }
    
    public static void resetToDefaults() {
        // Reset all values to their defaults
        targetingRange = 16.0;
        maxTrackingDistance = 20.0;
        maxAngle = 60.0;
        requireLineOfSight = true;
        
        showReticle = true;
        showHealthBar = true;
        showDistance = true;
        showTargetName = true;
        reticleScale = 1.0f;
        reticleColor = 0xFF0000;
        
        cameraSmoothness = 0.4f;
        maxPitchAdjustment = 60.0f;
        maxYawAdjustment = 90.0f;
        enableCameraLockOn = true;
        autoThirdPerson = false;
        btpCompatibilityMode = "gentle";
        btpCameraIntensity = 0.3f;
        
        targetHostileMobs = true;
        targetNeutralMobs = false;
        targetPassiveMobs = false;
        targetPlayers = false;
        
        enableSounds = true;
        soundVolume = 1.0f;
        
        updateFrequency = 1;
        validationInterval = 10;
        
        // Save the reset values
        saveConfig();
    }
    
    // Getters for commonly used values
    public static double getTargetingRange() {
        return targetingRange;
    }
    
    public static double getMaxTrackingDistance() {
        return maxTrackingDistance;
    }
    
    public static double getMaxAngle() {
        return maxAngle;
    }
    
    public static boolean shouldRequireLineOfSight() {
        return requireLineOfSight;
    }
    
    public static float getCameraSmoothness() {
        return cameraSmoothness;
    }
    
    public static boolean isCameraLockOnEnabled() {
        return enableCameraLockOn;
    }
    
    public static boolean shouldTargetHostileMobs() {
        return targetHostileMobs;
    }
    
    public static boolean shouldTargetNeutralMobs() {
        return targetNeutralMobs;
    }
    
    public static boolean shouldTargetPassiveMobs() {
        return targetPassiveMobs;
    }
    
    public static boolean shouldTargetPlayers() {
        return targetPlayers;
    }
}