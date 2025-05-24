package com.zeldatargeting.mod.client;

import com.zeldatargeting.mod.ZeldaTargetingMod;
import com.zeldatargeting.mod.client.targeting.EntityDetector;
import com.zeldatargeting.mod.client.targeting.CameraController;
import com.zeldatargeting.mod.client.targeting.TargetTracker;
import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TargetingManager {
    
    private static TargetingManager instance;
    
    private EntityDetector entityDetector;
    private CameraController cameraController;
    private TargetTracker targetTracker;
    
    private boolean isActive = false;
    private Entity currentTarget = null;
    private int previousPerspective = 0; // Store the perspective before lock-on
    
    private TargetingManager() {
        this.entityDetector = new EntityDetector();
        this.cameraController = new CameraController();
        this.targetTracker = new TargetTracker();
    }
    
    public static void init() {
        if (instance == null) {
            instance = new TargetingManager();
            MinecraftForge.EVENT_BUS.register(instance);
            ZeldaTargetingMod.getLogger().info("Targeting Manager initialized");
        }
    }
    
    public static TargetingManager getInstance() {
        return instance;
    }
    
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return; // Don't process input when GUI is open
        }
        
        if (KeyBindings.lockOnToggle.isPressed()) {
            toggleLockOn();
        } else if (KeyBindings.cycleTargetLeft.isPressed()) {
            cycleTarget(false);
        } else if (KeyBindings.cycleTargetRight.isPressed()) {
            cycleTarget(true);
        }
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        
        if (isActive && currentTarget != null) {
            // Update targeting system
            targetTracker.update(currentTarget);
            
            // Check if target is still valid
            if (!targetTracker.isTargetValid()) {
                // Try to find a new target or disable lock-on
                Entity newTarget = entityDetector.findNearestTarget();
                if (newTarget != null) {
                    setTarget(newTarget);
                } else {
                    disableLockOn();
                }
            } else {
                // Update camera to follow target
                cameraController.updateCamera(currentTarget);
            }
        }
    }
    
    private void toggleLockOn() {
        if (isActive) {
            disableLockOn();
        } else {
            enableLockOn();
        }
    }
    
    private void enableLockOn() {
        Entity target = entityDetector.findNearestTarget();
        if (target != null) {
            setTarget(target);
            isActive = true;
            
            // Handle auto third-person switching
            if (TargetingConfig.autoThirdPerson) {
                Minecraft mc = Minecraft.getMinecraft();
                previousPerspective = mc.gameSettings.thirdPersonView;
                if (mc.gameSettings.thirdPersonView == 0) { // First person
                    mc.gameSettings.thirdPersonView = 1; // Switch to third person
                }
            }
            
            ZeldaTargetingMod.getLogger().debug("Lock-on enabled on target: " + target.getName());
        }
    }
    
    private void disableLockOn() {
        if (isActive) {
            // Restore previous perspective if auto third-person was used
            if (TargetingConfig.autoThirdPerson) {
                Minecraft mc = Minecraft.getMinecraft();
                mc.gameSettings.thirdPersonView = previousPerspective;
            }
            
            isActive = false;
            currentTarget = null;
            cameraController.resetCamera();
            ZeldaTargetingMod.getLogger().debug("Lock-on disabled");
        }
    }
    
    private void cycleTarget(boolean forward) {
        if (!isActive) {
            enableLockOn();
            return;
        }
        
        Entity newTarget = entityDetector.findNextTarget(currentTarget, forward);
        if (newTarget != null && newTarget != currentTarget) {
            setTarget(newTarget);
            ZeldaTargetingMod.getLogger().debug("Cycled to new target: " + newTarget.getName());
        }
    }
    
    private void setTarget(Entity target) {
        this.currentTarget = target;
        targetTracker.setTarget(target);
        cameraController.setTarget(target);
    }
    
    // Getters
    public boolean isActive() {
        return isActive;
    }
    
    public Entity getCurrentTarget() {
        return currentTarget;
    }
}