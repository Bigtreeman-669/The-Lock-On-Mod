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
    private final int totalPages = 3;
    
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

    public GuiTargetingConfig(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        int centerX = this.width / 2;
        int startY = 60;
        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 25;

        // Page navigation buttons
        if (currentPage > 0) {
            this.buttonList.add(new GuiButton(PREV_PAGE_BUTTON, centerX - 210, this.height - 30, 100, 20, "< Previous"));
        }
        if (currentPage < totalPages - 1) {
            this.buttonList.add(new GuiButton(NEXT_PAGE_BUTTON, centerX + 110, this.height - 30, 100, 20, "Next >"));
        }

        // Control buttons
        this.buttonList.add(new GuiButton(DONE_BUTTON, centerX - 100, this.height - 30, 95, 20, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(RESET_BUTTON, centerX + 5, this.height - 30, 95, 20, "Reset"));

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

            case 2: // Entity Filtering, Audio & Performance
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
                        "Sound Volume", TargetingConfig.soundVolume, 0.0f, 1.0f, 0.05f);
                currentY += spacing + 10;

                addValueButton(UPDATE_FREQUENCY_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Update Frequency", (float)TargetingConfig.updateFrequency, 1.0f, 20.0f, 1.0f);
                currentY += spacing;

                addValueButton(VALIDATION_INTERVAL_BUTTON, centerX - buttonWidth/2, currentY, buttonWidth, buttonHeight,
                        "Validation Interval", (float)TargetingConfig.validationInterval, 1.0f, 60.0f, 1.0f);
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
        }
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
                this.drawCenteredString(this.fontRenderer, "§6Entity Filtering, Audio & Performance", centerX, 55, 0xFFAA00);
                break;
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}