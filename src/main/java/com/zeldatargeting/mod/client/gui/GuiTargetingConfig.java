package com.zeldatargeting.mod.client.gui;

import com.zeldatargeting.mod.ZeldaTargetingMod;
import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiTargetingConfig extends GuiScreen {
    private final GuiScreen parentScreen;
    private int currentPage = 0;
    private final int totalPages = 5; // Added sound tweaking + damage numbers pages
    
    // Button IDs
    private static final int DONE_BUTTON = 0;
    private static final int RESET_BUTTON = 1;
    private static final int NEXT_PAGE_BUTTON = 2;
    private static final int PREV_PAGE_BUTTON = 3;
    
    // Config button IDs
    private static final int TARGETING_RANGE_BUTTON = 100;
    private static final int MAX_TRACKING_DISTANCE_BUTTON = 101;
    private static final int DETECTION_ANGLE_BUTTON = 102;
    private static final int REQUIRE_LOS_TOGGLE = 103;
    private static final int SHOW_RETICLE_TOGGLE = 104;
    private static final int SHOW_HEALTH_TOGGLE = 105;
    private static final int SHOW_DISTANCE_TOGGLE = 106;
    private static final int SHOW_NAME_TOGGLE = 107;
    private static final int RETICLE_SCALE_BUTTON = 108;
    private static final int ENABLE_CAMERA_LOCKON_TOGGLE = 109;
    private static final int CAMERA_SMOOTHNESS_BUTTON = 110;
    private static final int MAX_PITCH_BUTTON = 111;
    private static final int MAX_YAW_BUTTON = 112;
    private static final int AUTO_THIRD_PERSON_TOGGLE = 113;
    private static final int BTP_MODE_TOGGLE = 114;
    private static final int BTP_INTENSITY_BUTTON = 115;
    private static final int TARGET_HOSTILES_TOGGLE = 116;
    private static final int TARGET_NEUTRALS_TOGGLE = 117;
    private static final int TARGET_PASSIVES_TOGGLE = 118;
    private static final int ENABLE_SOUNDS_TOGGLE = 119;
    private static final int SOUND_VOLUME_BUTTON = 120;
    private static final int UPDATE_FREQUENCY_BUTTON = 121;
    private static final int VALIDATION_INTERVAL_BUTTON = 122;
    
    // Enhanced Visual Feedback button IDs
    private static final int SHOW_DAMAGE_PREDICTION_TOGGLE = 123;
    private static final int SHOW_HITS_TO_KILL_TOGGLE = 124;
    private static final int SHOW_VULNERABILITIES_TOGGLE = 125;
    private static final int HIGHLIGHT_LETHAL_TARGETS_TOGGLE = 126;
    private static final int DAMAGE_PREDICTION_SCALE_BUTTON = 127;
    
    // Enhanced Audio Settings button IDs
    private static final int ENABLE_TARGET_LOCK_SOUND_TOGGLE = 128;
    private static final int ENABLE_TARGET_SWITCH_SOUND_TOGGLE = 129;
    private static final int ENABLE_LETHAL_TARGET_SOUND_TOGGLE = 130;
    private static final int ENABLE_TARGET_LOST_SOUND_TOGGLE = 131;
    private static final int TARGET_LOCK_VOLUME_BUTTON = 132;
    private static final int TARGET_SWITCH_VOLUME_BUTTON = 133;
    private static final int LETHAL_TARGET_VOLUME_BUTTON = 134;
    private static final int TARGET_LOST_VOLUME_BUTTON = 135;
    
    // Advanced Sound Tweaking button IDs
    private static final int SOUND_THEME_BUTTON = 136;
    private static final int TARGET_LOCK_PITCH_BUTTON = 137;
    private static final int TARGET_SWITCH_PITCH_BUTTON = 138;
    private static final int LETHAL_TARGET_PITCH_BUTTON = 139;
    private static final int TARGET_LOST_PITCH_BUTTON = 140;
    private static final int ENABLE_SOUND_VARIETY_TOGGLE = 141;
    
    // Damage Numbers Configuration button IDs
    private static final int ENABLE_DAMAGE_NUMBERS_TOGGLE = 142;
    private static final int DAMAGE_NUMBERS_SCALE_BUTTON = 143;
    private static final int DAMAGE_NUMBERS_DURATION_BUTTON = 144;
    private static final int DAMAGE_NUMBERS_CRITS_TOGGLE = 145;
    private static final int DAMAGE_NUMBERS_COLORS_TOGGLE = 146;
    private static final int DAMAGE_NUMBERS_COLOR_BUTTON = 147;
    private static final int CRITICAL_DAMAGE_COLOR_BUTTON = 148;
    private static final int LETHAL_DAMAGE_COLOR_BUTTON = 149;
    private static final int DAMAGE_NUMBERS_FADEOUT_TOGGLE = 150;
    private static final int DAMAGE_NUMBERS_OFFSET_BUTTON = 151;

    public GuiTargetingConfig(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        // Calculate GUI scale factor for proper scaling
        int scaleFactor = this.mc.gameSettings.guiScale;
        if (scaleFactor == 0) scaleFactor = 1000;
        int scaledWidth = this.mc.displayWidth / scaleFactor;
        int scaledHeight = this.mc.displayHeight / scaleFactor;
        
        // Use the smaller of actual screen dimensions or scaled dimensions for safety
        int effectiveWidth = Math.min(this.width, scaledWidth);
        int effectiveHeight = Math.min(this.height, scaledHeight);
        
        int centerX = effectiveWidth / 2;
        int startY = Math.max(45, effectiveHeight / 10); // Dynamic start position
        int buttonWidth = Math.min(200, effectiveWidth - 40); // Ensure buttons fit
        int buttonHeight = 18; // Slightly smaller for better fit
        int spacing = Math.max(20, Math.min(25, (effectiveHeight - startY - 80) / 15)); // Dynamic spacing

        // Page navigation buttons - positioned dynamically
        int buttonY = effectiveHeight - 25;
        if (currentPage > 0) {
            this.buttonList.add(new GuiButton(PREV_PAGE_BUTTON, centerX - 210, buttonY, 100, 20, "< Previous"));
        }
        if (currentPage < totalPages - 1) {
            this.buttonList.add(new GuiButton(NEXT_PAGE_BUTTON, centerX + 110, buttonY, 100, 20, "Next >"));
        }

        // Control buttons
        this.buttonList.add(new GuiButton(DONE_BUTTON, centerX - 100, buttonY, 95, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(RESET_BUTTON, centerX + 5, buttonY, 95, 20, "Reset"));

        int currentY = startY;

        switch (currentPage) {
            case 0: // Targeting & Visual Settings
                addValueButton(TARGETING_RANGE_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Targeting Range", (float)TargetingConfig.targetingRange, 5.0f, 50.0f, 1.0f);
                currentY += spacing;

                addValueButton(MAX_TRACKING_DISTANCE_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Max Tracking Distance", (float)TargetingConfig.maxTrackingDistance, 5.0f, 100.0f, 5.0f);
                currentY += spacing;

                addValueButton(DETECTION_ANGLE_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Detection Angle", (float)TargetingConfig.maxAngle, 15.0f, 180.0f, 5.0f);
                currentY += spacing;

                addToggleButton(REQUIRE_LOS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Require Line of Sight", TargetingConfig.requireLineOfSight);
                currentY += spacing + 10;

                addToggleButton(SHOW_RETICLE_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Reticle", TargetingConfig.showReticle);
                currentY += spacing;

                addToggleButton(SHOW_HEALTH_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Health Bar", TargetingConfig.showHealthBar);
                currentY += spacing;

                addToggleButton(SHOW_DISTANCE_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Distance", TargetingConfig.showDistance);
                currentY += spacing;

                addToggleButton(SHOW_NAME_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Target Name", TargetingConfig.showTargetName);
                currentY += spacing;

                addValueButton(RETICLE_SCALE_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Reticle Scale", TargetingConfig.reticleScale, 0.5f, 3.0f, 0.1f);
                currentY += spacing + 10;

                // Enhanced Visual Feedback section
                addToggleButton(SHOW_DAMAGE_PREDICTION_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Damage Prediction", TargetingConfig.showDamagePrediction);
                currentY += spacing;

                addToggleButton(SHOW_HITS_TO_KILL_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Hits to Kill", TargetingConfig.showHitsToKill);
                currentY += spacing;

                addToggleButton(SHOW_VULNERABILITIES_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Show Vulnerabilities", TargetingConfig.showVulnerabilities);
                currentY += spacing;

                addToggleButton(HIGHLIGHT_LETHAL_TARGETS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Highlight Lethal Targets", TargetingConfig.highlightLethalTargets);
                currentY += spacing;

                addValueButton(DAMAGE_PREDICTION_SCALE_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Damage Text Scale", TargetingConfig.damagePredictionScale, 0.5f, 2.0f, 0.1f);
                break;

            case 1: // Camera Settings
                addToggleButton(ENABLE_CAMERA_LOCKON_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Enable Camera Lock-On", TargetingConfig.enableCameraLockOn);
                currentY += spacing;

                addValueButton(CAMERA_SMOOTHNESS_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Camera Smoothness", TargetingConfig.cameraSmoothness, 0.01f, 1.0f, 0.05f);
                currentY += spacing;

                addValueButton(MAX_PITCH_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Max Pitch", TargetingConfig.maxPitchAdjustment, 0.0f, 90.0f, 5.0f);
                currentY += spacing;

                addValueButton(MAX_YAW_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Max Yaw", TargetingConfig.maxYawAdjustment, 0.0f, 180.0f, 10.0f);
                currentY += spacing;

                addToggleButton(AUTO_THIRD_PERSON_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Auto Third Person", TargetingConfig.autoThirdPerson);
                currentY += spacing + 10;

                addBtpModeButton(BTP_MODE_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "BTP Mode", TargetingConfig.btpCompatibilityMode);
                currentY += spacing;

                if ("gentle".equals(TargetingConfig.btpCompatibilityMode)) {
                    addValueButton(BTP_INTENSITY_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                            "BTP Camera Intensity", TargetingConfig.btpCameraIntensity, 0.0f, 1.0f, 0.05f);
                }
                break;

            case 2: // Entity Filtering & Basic Audio
                addToggleButton(TARGET_HOSTILES_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Hostile Mobs", TargetingConfig.targetHostileMobs);
                currentY += spacing;

                addToggleButton(TARGET_NEUTRALS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Neutral Mobs", TargetingConfig.targetNeutralMobs);
                currentY += spacing;

                addToggleButton(TARGET_PASSIVES_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Passive Mobs", TargetingConfig.targetPassiveMobs);
                currentY += spacing + 10;

                addToggleButton(ENABLE_SOUNDS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Enable Sounds", TargetingConfig.enableSounds);
                currentY += spacing;

                addValueButton(SOUND_VOLUME_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Master Sound Volume", TargetingConfig.soundVolume, 0.0f, 1.0f, 0.05f);
                currentY += spacing + 10;

                // Basic sound toggle controls
                addToggleButton(ENABLE_TARGET_LOCK_SOUND_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Lock Sound", TargetingConfig.enableTargetLockSound);
                currentY += spacing;

                addToggleButton(ENABLE_TARGET_SWITCH_SOUND_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Switch Sound", TargetingConfig.enableTargetSwitchSound);
                currentY += spacing;

                addToggleButton(ENABLE_LETHAL_TARGET_SOUND_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Lethal Target Sound", TargetingConfig.enableLethalTargetSound);
                currentY += spacing;

                addToggleButton(ENABLE_TARGET_LOST_SOUND_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Lost Sound", TargetingConfig.enableTargetLostSound);
                currentY += spacing + 10;

                addValueButton(UPDATE_FREQUENCY_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Update Frequency", (float)TargetingConfig.updateFrequency, 1.0f, 20.0f, 1.0f);
                currentY += spacing;

                addValueButton(VALIDATION_INTERVAL_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Validation Interval", (float)TargetingConfig.validationInterval, 1.0f, 60.0f, 1.0f);
                break;

            case 3: // Advanced Sound Tweaking
                // Sound Theme Selection
                addSoundThemeButton(SOUND_THEME_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Sound Theme", TargetingConfig.soundTheme);
                currentY += spacing;

                addToggleButton(ENABLE_SOUND_VARIETY_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Sound Variety", TargetingConfig.enableSoundVariety);
                currentY += spacing + 10;

                // Volume Controls
                addValueButton(TARGET_LOCK_VOLUME_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Lock Volume", TargetingConfig.targetLockVolume, 0.0f, 1.0f, 0.05f);
                currentY += spacing;

                addValueButton(TARGET_SWITCH_VOLUME_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Switch Volume", TargetingConfig.targetSwitchVolume, 0.0f, 1.0f, 0.05f);
                currentY += spacing;

                addValueButton(LETHAL_TARGET_VOLUME_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Lethal Target Volume", TargetingConfig.lethalTargetVolume, 0.0f, 1.0f, 0.05f);
                currentY += spacing;

                addValueButton(TARGET_LOST_VOLUME_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Lost Volume", TargetingConfig.targetLostVolume, 0.0f, 1.0f, 0.05f);
                currentY += spacing + 10;

                // Pitch Controls
                addValueButton(TARGET_LOCK_PITCH_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Lock Pitch", TargetingConfig.targetLockPitch, 0.5f, 2.0f, 0.1f);
                currentY += spacing;

                addValueButton(TARGET_SWITCH_PITCH_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Switch Pitch", TargetingConfig.targetSwitchPitch, 0.5f, 2.0f, 0.1f);
                currentY += spacing;

                addValueButton(LETHAL_TARGET_PITCH_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Lethal Target Pitch", TargetingConfig.lethalTargetPitch, 0.5f, 2.0f, 0.1f);
                currentY += spacing;

                addValueButton(TARGET_LOST_PITCH_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Target Lost Pitch", TargetingConfig.targetLostPitch, 0.5f, 2.0f, 0.1f);
                break;
                
            case 4: // Damage Numbers Configuration
                addToggleButton(ENABLE_DAMAGE_NUMBERS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Enable Damage Numbers", TargetingConfig.enableDamageNumbers);
                currentY += spacing;
                
                addValueButton(DAMAGE_NUMBERS_SCALE_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Damage Numbers Scale", TargetingConfig.damageNumbersScale, 0.5f, 3.0f, 0.1f);
                currentY += spacing;
                
                addValueButton(DAMAGE_NUMBERS_DURATION_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Duration (ticks)", (float)TargetingConfig.damageNumbersDuration, 20.0f, 200.0f, 10.0f);
                currentY += spacing;
                
                addValueButton(DAMAGE_NUMBERS_OFFSET_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Vertical Offset", TargetingConfig.damageNumbersOffset, 0.0f, 2.0f, 0.1f);
                currentY += spacing + 10;
                
                addToggleButton(DAMAGE_NUMBERS_CRITS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Critical Hit Effects", TargetingConfig.damageNumbersCrits);
                currentY += spacing;
                
                addToggleButton(DAMAGE_NUMBERS_COLORS_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Colored Damage Numbers", TargetingConfig.damageNumbersColors);
                currentY += spacing;
                
                addToggleButton(DAMAGE_NUMBERS_FADEOUT_TOGGLE, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Fade-Out Animation", TargetingConfig.damageNumbersFadeOut);
                currentY += spacing + 10;
                
                // Color configuration buttons (simplified for now)
                addColorButton(DAMAGE_NUMBERS_COLOR_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Default Color", TargetingConfig.damageNumbersColor);
                currentY += spacing;
                
                addColorButton(CRITICAL_DAMAGE_COLOR_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Critical Color", TargetingConfig.criticalDamageColor);
                currentY += spacing;
                
                addColorButton(LETHAL_DAMAGE_COLOR_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Lethal Color", TargetingConfig.lethalDamageColor);
                break;
        }
    }

    private void addToggleButton(int id, int x, int y, int width, int height, String name, boolean currentValue) {
        String displayText = name + ": " + (currentValue ? "ON" : "OFF");
        GuiButton button = new GuiButton(id, x, y, width, height, displayText);
        this.buttonList.add(button);
    }

    private void addValueButton(int id, int x, int y, int width, int height, String name, float currentValue, float minValue, float maxValue, float increment) {
        String displayText = name + ": " + String.format("%.2f", currentValue);
        GuiButton button = new GuiButton(id, x, y, width, height, displayText);
        this.buttonList.add(button);
    }
    
    private void addBtpModeButton(int id, int x, int y, int width, int height, String name, String currentMode) {
        String displayText = name + ": " + currentMode.toUpperCase();
        GuiButton button = new GuiButton(id, x, y, width, height, displayText);
        this.buttonList.add(button);
    }
    
    private void addSoundThemeButton(int id, int x, int y, int width, int height, String name, String currentTheme) {
        String displayText = name + ": " + currentTheme.toUpperCase();
        GuiButton button = new GuiButton(id, x, y, width, height, displayText);
        this.buttonList.add(button);
    }
    
    private void addColorButton(int id, int x, int y, int width, int height, String name, int currentColor) {
        String colorHex = String.format("#%06X", currentColor & 0xFFFFFF);
        String displayText = name + ": " + colorHex;
        GuiButton button = new GuiButton(id, x, y, width, height, displayText);
        this.buttonList.add(button);
    }

    private void cycleSoundTheme() {
        switch (TargetingConfig.soundTheme.toLowerCase()) {
            case "default":
                TargetingConfig.soundTheme = "zelda";
                break;
            case "zelda":
                TargetingConfig.soundTheme = "modern";
                break;
            case "modern":
                TargetingConfig.soundTheme = "subtle";
                break;
            case "subtle":
                TargetingConfig.soundTheme = "default";
                break;
            default:
                TargetingConfig.soundTheme = "default";
                break;
        }
        TargetingConfig.saveConfig();
    }

    @Override
    protected void actionPerformed(@Nonnull GuiButton button) throws IOException {
        if (button.id == DONE_BUTTON) {
            TargetingConfig.saveConfig();
            this.mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == RESET_BUTTON) {
            TargetingConfig.resetToDefaults();
            this.initGui();
        } else if (button.id == NEXT_PAGE_BUTTON) {
            currentPage++;
            this.initGui();
        } else if (button.id == PREV_PAGE_BUTTON) {
            currentPage--;
            this.initGui();
        } else {
            handleConfigButton(button, false);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        
        // Handle right-click for decreasing values
        if (mouseButton == 1) { // Right click
            for (GuiButton button : this.buttonList) {
                if (button.mousePressed(this.mc, mouseX, mouseY)) {
                    handleConfigButton(button, true);
                    break;
                }
            }
        }
    }

    private void handleConfigButton(GuiButton button, boolean decrease) {
        // Check for shift key for fine adjustment
        boolean isShiftPressed = org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_LSHIFT) ||
                                org.lwjgl.input.Keyboard.isKeyDown(org.lwjgl.input.Keyboard.KEY_RSHIFT);
        
        switch (button.id) {
            case TARGETING_RANGE_BUTTON:
                float targetingIncrement = isShiftPressed ? 0.5f : 1.0f;
                TargetingConfig.targetingRange = cycleValue((float)TargetingConfig.targetingRange, 5.0f, 50.0f, targetingIncrement, decrease);
                button.displayString = "Targeting Range: " + String.format("%.1f", TargetingConfig.targetingRange);
                break;
            case MAX_TRACKING_DISTANCE_BUTTON:
                float trackingIncrement = isShiftPressed ? 1.0f : 5.0f;
                TargetingConfig.maxTrackingDistance = cycleValue((float)TargetingConfig.maxTrackingDistance, 5.0f, 100.0f, trackingIncrement, decrease);
                button.displayString = "Max Tracking Distance: " + String.format("%.1f", TargetingConfig.maxTrackingDistance);
                break;
            case DETECTION_ANGLE_BUTTON:
                float angleIncrement = isShiftPressed ? 1.0f : 5.0f;
                TargetingConfig.maxAngle = cycleValue((float)TargetingConfig.maxAngle, 15.0f, 180.0f, angleIncrement, decrease);
                button.displayString = "Detection Angle: " + String.format("%.1f", TargetingConfig.maxAngle);
                break;
            case RETICLE_SCALE_BUTTON:
                float scaleIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.reticleScale = cycleValue(TargetingConfig.reticleScale, 0.5f, 3.0f, scaleIncrement, decrease);
                button.displayString = "Reticle Scale: " + String.format("%.2f", TargetingConfig.reticleScale);
                break;
            case CAMERA_SMOOTHNESS_BUTTON:
                float smoothnessIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.cameraSmoothness = cycleValue(TargetingConfig.cameraSmoothness, 0.01f, 1.0f, smoothnessIncrement, decrease);
                button.displayString = "Camera Smoothness: " + String.format("%.2f", TargetingConfig.cameraSmoothness);
                break;
            case MAX_PITCH_BUTTON:
                float pitchIncrement = isShiftPressed ? 1.0f : 5.0f;
                TargetingConfig.maxPitchAdjustment = cycleValue(TargetingConfig.maxPitchAdjustment, 0.0f, 90.0f, pitchIncrement, decrease);
                button.displayString = "Max Pitch: " + String.format("%.1f", TargetingConfig.maxPitchAdjustment);
                break;
            case MAX_YAW_BUTTON:
                float yawIncrement = isShiftPressed ? 5.0f : 10.0f;
                TargetingConfig.maxYawAdjustment = cycleValue(TargetingConfig.maxYawAdjustment, 0.0f, 180.0f, yawIncrement, decrease);
                button.displayString = "Max Yaw: " + String.format("%.1f", TargetingConfig.maxYawAdjustment);
                break;
            case BTP_INTENSITY_BUTTON:
                float intensityIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.btpCameraIntensity = cycleValue(TargetingConfig.btpCameraIntensity, 0.0f, 1.0f, intensityIncrement, decrease);
                button.displayString = "BTP Camera Intensity: " + String.format("%.2f", TargetingConfig.btpCameraIntensity);
                break;
            case SOUND_VOLUME_BUTTON:
                float volumeIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.soundVolume = cycleValue(TargetingConfig.soundVolume, 0.0f, 1.0f, volumeIncrement, decrease);
                button.displayString = "Sound Volume: " + String.format("%.2f", TargetingConfig.soundVolume);
                break;
            case UPDATE_FREQUENCY_BUTTON:
                TargetingConfig.updateFrequency = (int)cycleValue((float)TargetingConfig.updateFrequency, 1.0f, 20.0f, 1.0f, decrease);
                button.displayString = "Update Frequency: " + TargetingConfig.updateFrequency;
                break;
            case VALIDATION_INTERVAL_BUTTON:
                float intervalIncrement = isShiftPressed ? 1.0f : 5.0f;
                TargetingConfig.validationInterval = (int)cycleValue((float)TargetingConfig.validationInterval, 1.0f, 60.0f, intervalIncrement, decrease);
                button.displayString = "Validation Interval: " + TargetingConfig.validationInterval;
                break;
            
            // Toggle buttons
            case REQUIRE_LOS_TOGGLE:
                TargetingConfig.requireLineOfSight = !TargetingConfig.requireLineOfSight;
                button.displayString = "Require Line of Sight: " + (TargetingConfig.requireLineOfSight ? "ON" : "OFF");
                break;
            case SHOW_RETICLE_TOGGLE:
                TargetingConfig.showReticle = !TargetingConfig.showReticle;
                button.displayString = "Show Reticle: " + (TargetingConfig.showReticle ? "ON" : "OFF");
                break;
            case SHOW_HEALTH_TOGGLE:
                TargetingConfig.showHealthBar = !TargetingConfig.showHealthBar;
                button.displayString = "Show Health Bar: " + (TargetingConfig.showHealthBar ? "ON" : "OFF");
                break;
            case SHOW_DISTANCE_TOGGLE:
                TargetingConfig.showDistance = !TargetingConfig.showDistance;
                button.displayString = "Show Distance: " + (TargetingConfig.showDistance ? "ON" : "OFF");
                break;
            case SHOW_NAME_TOGGLE:
                TargetingConfig.showTargetName = !TargetingConfig.showTargetName;
                button.displayString = "Show Target Name: " + (TargetingConfig.showTargetName ? "ON" : "OFF");
                break;
            case ENABLE_CAMERA_LOCKON_TOGGLE:
                TargetingConfig.enableCameraLockOn = !TargetingConfig.enableCameraLockOn;
                button.displayString = "Enable Camera Lock-On: " + (TargetingConfig.enableCameraLockOn ? "ON" : "OFF");
                break;
            case AUTO_THIRD_PERSON_TOGGLE:
                TargetingConfig.autoThirdPerson = !TargetingConfig.autoThirdPerson;
                button.displayString = "Auto Third Person: " + (TargetingConfig.autoThirdPerson ? "ON" : "OFF");
                break;
            case BTP_MODE_TOGGLE:
                cycleBtpMode();
                button.displayString = "BTP Mode: " + TargetingConfig.btpCompatibilityMode.toUpperCase();
                this.initGui(); // Refresh GUI to show/hide intensity slider
                break;
            case TARGET_HOSTILES_TOGGLE:
                TargetingConfig.targetHostileMobs = !TargetingConfig.targetHostileMobs;
                button.displayString = "Target Hostile Mobs: " + (TargetingConfig.targetHostileMobs ? "ON" : "OFF");
                break;
            case TARGET_NEUTRALS_TOGGLE:
                TargetingConfig.targetNeutralMobs = !TargetingConfig.targetNeutralMobs;
                button.displayString = "Target Neutral Mobs: " + (TargetingConfig.targetNeutralMobs ? "ON" : "OFF");
                break;
            case TARGET_PASSIVES_TOGGLE:
                TargetingConfig.targetPassiveMobs = !TargetingConfig.targetPassiveMobs;
                button.displayString = "Target Passive Mobs: " + (TargetingConfig.targetPassiveMobs ? "ON" : "OFF");
                break;
            case ENABLE_SOUNDS_TOGGLE:
                TargetingConfig.enableSounds = !TargetingConfig.enableSounds;
                button.displayString = "Enable Sounds: " + (TargetingConfig.enableSounds ? "ON" : "OFF");
                break;
            
            // Enhanced Audio Settings toggles
            case ENABLE_TARGET_LOCK_SOUND_TOGGLE:
                TargetingConfig.enableTargetLockSound = !TargetingConfig.enableTargetLockSound;
                button.displayString = "Target Lock Sound: " + (TargetingConfig.enableTargetLockSound ? "ON" : "OFF");
                break;
            case ENABLE_TARGET_SWITCH_SOUND_TOGGLE:
                TargetingConfig.enableTargetSwitchSound = !TargetingConfig.enableTargetSwitchSound;
                button.displayString = "Target Switch Sound: " + (TargetingConfig.enableTargetSwitchSound ? "ON" : "OFF");
                break;
            case ENABLE_LETHAL_TARGET_SOUND_TOGGLE:
                TargetingConfig.enableLethalTargetSound = !TargetingConfig.enableLethalTargetSound;
                button.displayString = "Lethal Target Sound: " + (TargetingConfig.enableLethalTargetSound ? "ON" : "OFF");
                break;
            case ENABLE_TARGET_LOST_SOUND_TOGGLE:
                TargetingConfig.enableTargetLostSound = !TargetingConfig.enableTargetLostSound;
                button.displayString = "Target Lost Sound: " + (TargetingConfig.enableTargetLostSound ? "ON" : "OFF");
                break;
            
            // Enhanced Audio Volume buttons
            case TARGET_LOCK_VOLUME_BUTTON:
                float lockVolumeIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.targetLockVolume = cycleValue(TargetingConfig.targetLockVolume, 0.0f, 1.0f, lockVolumeIncrement, decrease);
                button.displayString = "Target Lock Volume: " + String.format("%.2f", TargetingConfig.targetLockVolume);
                break;
            case TARGET_SWITCH_VOLUME_BUTTON:
                float switchVolumeIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.targetSwitchVolume = cycleValue(TargetingConfig.targetSwitchVolume, 0.0f, 1.0f, switchVolumeIncrement, decrease);
                button.displayString = "Target Switch Volume: " + String.format("%.2f", TargetingConfig.targetSwitchVolume);
                break;
            case LETHAL_TARGET_VOLUME_BUTTON:
                float lethalVolumeIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.lethalTargetVolume = cycleValue(TargetingConfig.lethalTargetVolume, 0.0f, 1.0f, lethalVolumeIncrement, decrease);
                button.displayString = "Lethal Target Volume: " + String.format("%.2f", TargetingConfig.lethalTargetVolume);
                break;
            case TARGET_LOST_VOLUME_BUTTON:
                float lostVolumeIncrement = isShiftPressed ? 0.01f : 0.05f;
                TargetingConfig.targetLostVolume = cycleValue(TargetingConfig.targetLostVolume, 0.0f, 1.0f, lostVolumeIncrement, decrease);
                button.displayString = "Target Lost Volume: " + String.format("%.2f", TargetingConfig.targetLostVolume);
                break;
            
            // Enhanced Visual Feedback toggles
            case SHOW_DAMAGE_PREDICTION_TOGGLE:
                TargetingConfig.showDamagePrediction = !TargetingConfig.showDamagePrediction;
                button.displayString = "Show Damage Prediction: " + (TargetingConfig.showDamagePrediction ? "ON" : "OFF");
                break;
            case SHOW_HITS_TO_KILL_TOGGLE:
                TargetingConfig.showHitsToKill = !TargetingConfig.showHitsToKill;
                button.displayString = "Show Hits to Kill: " + (TargetingConfig.showHitsToKill ? "ON" : "OFF");
                break;
            case SHOW_VULNERABILITIES_TOGGLE:
                TargetingConfig.showVulnerabilities = !TargetingConfig.showVulnerabilities;
                button.displayString = "Show Vulnerabilities: " + (TargetingConfig.showVulnerabilities ? "ON" : "OFF");
                break;
            case HIGHLIGHT_LETHAL_TARGETS_TOGGLE:
                TargetingConfig.highlightLethalTargets = !TargetingConfig.highlightLethalTargets;
                button.displayString = "Highlight Lethal Targets: " + (TargetingConfig.highlightLethalTargets ? "ON" : "OFF");
                break;
            case DAMAGE_PREDICTION_SCALE_BUTTON:
                float scaleIncrement2 = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.damagePredictionScale = cycleValue(TargetingConfig.damagePredictionScale, 0.5f, 2.0f, scaleIncrement2, decrease);
                button.displayString = "Damage Text Scale: " + String.format("%.2f", TargetingConfig.damagePredictionScale);
                break;
            
            // Advanced Sound Tweaking controls
            case SOUND_THEME_BUTTON:
                cycleSoundTheme();
                button.displayString = "Sound Theme: " + TargetingConfig.soundTheme.toUpperCase();
                break;
            case ENABLE_SOUND_VARIETY_TOGGLE:
                TargetingConfig.enableSoundVariety = !TargetingConfig.enableSoundVariety;
                button.displayString = "Sound Variety: " + (TargetingConfig.enableSoundVariety ? "ON" : "OFF");
                break;
            
            // Pitch controls
            case TARGET_LOCK_PITCH_BUTTON:
                float lockPitchIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.targetLockPitch = cycleValue(TargetingConfig.targetLockPitch, 0.5f, 2.0f, lockPitchIncrement, decrease);
                button.displayString = "Target Lock Pitch: " + String.format("%.2f", TargetingConfig.targetLockPitch);
                break;
            case TARGET_SWITCH_PITCH_BUTTON:
                float switchPitchIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.targetSwitchPitch = cycleValue(TargetingConfig.targetSwitchPitch, 0.5f, 2.0f, switchPitchIncrement, decrease);
                button.displayString = "Target Switch Pitch: " + String.format("%.2f", TargetingConfig.targetSwitchPitch);
                break;
            case LETHAL_TARGET_PITCH_BUTTON:
                float lethalPitchIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.lethalTargetPitch = cycleValue(TargetingConfig.lethalTargetPitch, 0.5f, 2.0f, lethalPitchIncrement, decrease);
                button.displayString = "Lethal Target Pitch: " + String.format("%.2f", TargetingConfig.lethalTargetPitch);
                break;
            case TARGET_LOST_PITCH_BUTTON:
                float lostPitchIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.targetLostPitch = cycleValue(TargetingConfig.targetLostPitch, 0.5f, 2.0f, lostPitchIncrement, decrease);
                button.displayString = "Target Lost Pitch: " + String.format("%.2f", TargetingConfig.targetLostPitch);
                break;
            
            // Damage Numbers Configuration handlers
            case ENABLE_DAMAGE_NUMBERS_TOGGLE:
                TargetingConfig.enableDamageNumbers = !TargetingConfig.enableDamageNumbers;
                button.displayString = "Enable Damage Numbers: " + (TargetingConfig.enableDamageNumbers ? "ON" : "OFF");
                break;
            case DAMAGE_NUMBERS_SCALE_BUTTON:
                float damageScaleIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.damageNumbersScale = cycleValue(TargetingConfig.damageNumbersScale, 0.5f, 3.0f, damageScaleIncrement, decrease);
                button.displayString = "Damage Numbers Scale: " + String.format("%.2f", TargetingConfig.damageNumbersScale);
                break;
            case DAMAGE_NUMBERS_DURATION_BUTTON:
                float durationIncrement = isShiftPressed ? 5.0f : 10.0f;
                TargetingConfig.damageNumbersDuration = (int)cycleValue((float)TargetingConfig.damageNumbersDuration, 20.0f, 200.0f, durationIncrement, decrease);
                button.displayString = "Duration (ticks): " + TargetingConfig.damageNumbersDuration;
                break;
            case DAMAGE_NUMBERS_OFFSET_BUTTON:
                float offsetIncrement = isShiftPressed ? 0.05f : 0.1f;
                TargetingConfig.damageNumbersOffset = cycleValue(TargetingConfig.damageNumbersOffset, 0.0f, 2.0f, offsetIncrement, decrease);
                button.displayString = "Vertical Offset: " + String.format("%.2f", TargetingConfig.damageNumbersOffset);
                break;
            case DAMAGE_NUMBERS_CRITS_TOGGLE:
                TargetingConfig.damageNumbersCrits = !TargetingConfig.damageNumbersCrits;
                button.displayString = "Critical Hit Effects: " + (TargetingConfig.damageNumbersCrits ? "ON" : "OFF");
                break;
            case DAMAGE_NUMBERS_COLORS_TOGGLE:
                TargetingConfig.damageNumbersColors = !TargetingConfig.damageNumbersColors;
                button.displayString = "Colored Damage Numbers: " + (TargetingConfig.damageNumbersColors ? "ON" : "OFF");
                break;
            case DAMAGE_NUMBERS_FADEOUT_TOGGLE:
                TargetingConfig.damageNumbersFadeOut = !TargetingConfig.damageNumbersFadeOut;
                button.displayString = "Fade-Out Animation: " + (TargetingConfig.damageNumbersFadeOut ? "ON" : "OFF");
                break;
            case DAMAGE_NUMBERS_COLOR_BUTTON:
                cycleDamageNumberColor("default");
                String defaultColorHex = String.format("#%06X", TargetingConfig.damageNumbersColor & 0xFFFFFF);
                button.displayString = "Default Color: " + defaultColorHex;
                break;
            case CRITICAL_DAMAGE_COLOR_BUTTON:
                cycleDamageNumberColor("critical");
                String criticalColorHex = String.format("#%06X", TargetingConfig.criticalDamageColor & 0xFFFFFF);
                button.displayString = "Critical Color: " + criticalColorHex;
                break;
            case LETHAL_DAMAGE_COLOR_BUTTON:
                cycleDamageNumberColor("lethal");
                String lethalColorHex = String.format("#%06X", TargetingConfig.lethalDamageColor & 0xFFFFFF);
                button.displayString = "Lethal Color: " + lethalColorHex;
                break;
        }
        
        // Automatically save config after any change
        TargetingConfig.saveConfig();
    }

    private float cycleValue(float currentValue, float minValue, float maxValue, float increment, boolean decrease) {
        float newValue;
        if (decrease) {
            newValue = currentValue - increment;
            if (newValue < minValue) {
                newValue = maxValue;
            }
        } else {
            newValue = currentValue + increment;
            if (newValue > maxValue) {
                newValue = minValue;
            }
        }
        return newValue;
    }
    
    private void cycleBtpMode() {
        switch (TargetingConfig.btpCompatibilityMode) {
            case "disabled":
                TargetingConfig.btpCompatibilityMode = "gentle";
                break;
            case "gentle":
                TargetingConfig.btpCompatibilityMode = "visual_only";
                break;
            case "visual_only":
                TargetingConfig.btpCompatibilityMode = "disabled";
                break;
            default:
                TargetingConfig.btpCompatibilityMode = "gentle";
                break;
        }
        // Save config after BTP mode change
        TargetingConfig.saveConfig();
    }
    
    private void cycleDamageNumberColor(String colorType) {
        // Define common color options as RGB integers
        int[] colorOptions = {
            0xFFFFFF, // White
            0xFF0000, // Red
            0x00FF00, // Green
            0x0000FF, // Blue
            0xFFFF00, // Yellow
            0xFF8800, // Orange
            0xFF00FF, // Magenta
            0x00FFFF, // Cyan
            0x808080, // Gray
            0xFF6666, // Light Red
            0x66FF66, // Light Green
            0x6666FF, // Light Blue
            0xFFFFAA, // Light Yellow
            0xFFAA66, // Light Orange
            0xAA66FF, // Purple
            0x66FFAA  // Light Cyan
        };
        
        int currentColor;
        switch (colorType) {
            case "critical":
                currentColor = TargetingConfig.criticalDamageColor;
                break;
            case "lethal":
                currentColor = TargetingConfig.lethalDamageColor;
                break;
            default: // "default"
                currentColor = TargetingConfig.damageNumbersColor;
                break;
        }
        
        // Find current color index
        int currentIndex = 0;
        for (int i = 0; i < colorOptions.length; i++) {
            if (colorOptions[i] == currentColor) {
                currentIndex = i;
                break;
            }
        }
        
        // Cycle to next color
        int nextIndex = (currentIndex + 1) % colorOptions.length;
        int nextColor = colorOptions[nextIndex];
        
        // Apply the new color
        switch (colorType) {
            case "critical":
                TargetingConfig.criticalDamageColor = nextColor;
                break;
            case "lethal":
                TargetingConfig.lethalDamageColor = nextColor;
                break;
            default: // "default"
                TargetingConfig.damageNumbersColor = nextColor;
                break;
        }
        
        TargetingConfig.saveConfig();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        
        // Draw title
        String title = "Zelda Targeting Configuration";
        this.drawCenteredString(this.fontRenderer, title, this.width / 2, 20, 0xFFFFFF);
        
        // Draw page indicator
        String pageInfo = "Page " + (currentPage + 1) + " of " + totalPages;
        this.drawCenteredString(this.fontRenderer, pageInfo, this.width / 2, 35, 0xAAAAAA);
        
        // Draw Better Third Person compatibility info if detected
        if (ZeldaTargetingMod.isBetterThirdPersonLoaded()) {
            String btpMessage = "§eBetter Third Person detected - Mode: " + TargetingConfig.btpCompatibilityMode.toUpperCase();
            this.drawCenteredString(this.fontRenderer, btpMessage, this.width / 2, 45, 0xFFAA00);
        }
        
        // Draw section headers based on current page
        int centerX = this.width / 2;
        switch (currentPage) {
            case 0:
                this.drawCenteredString(this.fontRenderer, "§6Targeting & Visual Settings", centerX, 55, 0xFFAA00);
                break;
            case 1:
                this.drawCenteredString(this.fontRenderer, "§6Camera Settings", centerX, 55, 0xFFAA00);
                break;
            case 2:
                this.drawCenteredString(this.fontRenderer, "§6Entity Filtering & Basic Audio", centerX, 55, 0xFFAA00);
                break;
            case 3:
                this.drawCenteredString(this.fontRenderer, "§6Advanced Sound Tweaking", centerX, 55, 0xFFAA00);
                break;
            case 4:
                this.drawCenteredString(this.fontRenderer, "§6Damage Numbers Configuration", centerX, 55, 0xFFAA00);
                break;
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}