package com.zeldatargeting.mod.client.targeting;

import com.zeldatargeting.mod.ZeldaTargetingMod;
import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class CameraController {
    
    private final Minecraft mc;
    private Entity currentTarget;
    
    // Store original camera settings
    private float originalYaw;
    private float originalPitch;
    private boolean hasStoredOriginal = false;
    
    // Smooth interpolation values
    private float targetYaw;
    private float targetPitch;
    private float currentYaw;
    private float currentPitch;
    
    public CameraController() {
        this.mc = Minecraft.getMinecraft();
    }
    
    public void setTarget(Entity target) {
        this.currentTarget = target;
        
        if (!hasStoredOriginal && mc.player != null) {
            // Store original camera rotation
            originalYaw = mc.player.rotationYaw;
            originalPitch = mc.player.rotationPitch;
            currentYaw = originalYaw;
            currentPitch = originalPitch;
            hasStoredOriginal = true;
        }
        
        if (target != null) {
            calculateTargetRotation();
        }
    }
    
    public void updateCamera(Entity target) {
        if (target == null || mc.player == null || !TargetingConfig.isCameraLockOnEnabled()) {
            return;
        }
        
        // Handle BTP compatibility modes
        if (ZeldaTargetingMod.isBetterThirdPersonLoaded()) {
            String btpMode = TargetingConfig.btpCompatibilityMode;
            if ("visual_only".equals(btpMode)) {
                return; // No camera movement at all
            } else if ("disabled".equals(btpMode)) {
                // Full camera lock-on (ignore BTP)
            } else if ("gentle".equals(btpMode)) {
                // Gentle camera assistance - handled below
            }
        }
        
        this.currentTarget = target;
        calculateTargetRotation();
        
        // Calculate smoothing based on BTP compatibility mode
        float smoothing = Math.max(TargetingConfig.getCameraSmoothness(), 0.3f);
        
        // Apply BTP gentle mode intensity reduction
        if (ZeldaTargetingMod.isBetterThirdPersonLoaded() && "gentle".equals(TargetingConfig.btpCompatibilityMode)) {
            smoothing *= TargetingConfig.btpCameraIntensity; // Reduce intensity for BTP compatibility
            smoothing = Math.max(smoothing, 0.05f); // Minimum smoothing to prevent jerkiness
        }
        
        // Calculate the difference between current and target rotation
        float yawDiff = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float pitchDiff = targetPitch - currentPitch;
        
        // Apply faster interpolation for larger differences
        float adaptiveSmoothing = smoothing;
        if (Math.abs(yawDiff) > 10.0f || Math.abs(pitchDiff) > 10.0f) {
            adaptiveSmoothing = Math.max(smoothing, 0.5f);
        }
        
        currentYaw = interpolateAngle(currentYaw, targetYaw, adaptiveSmoothing);
        currentPitch = MathHelper.clamp(
            interpolateFloat(currentPitch, targetPitch, adaptiveSmoothing),
            -90.0f, 90.0f
        );
        
        // Apply the camera rotation
        applyCameraRotation();
    }
    
    public void resetCamera() {
        if (hasStoredOriginal && mc.player != null) {
            // Smoothly return to original rotation
            targetYaw = originalYaw;
            targetPitch = originalPitch;
            
            // Quick interpolation back to original
            for (int i = 0; i < 10; i++) {
                currentYaw = interpolateAngle(currentYaw, targetYaw, 0.3f);
                currentPitch = interpolateFloat(currentPitch, targetPitch, 0.3f);
            }
            
            mc.player.rotationYaw = originalYaw;
            mc.player.rotationPitch = originalPitch;
            mc.player.prevRotationYaw = originalYaw;
            mc.player.prevRotationPitch = originalPitch;
        }
        
        currentTarget = null;
        hasStoredOriginal = false;
    }
    
    private void calculateTargetRotation() {
        if (currentTarget == null || mc.player == null) {
            return;
        }
        
        EntityPlayer player = mc.player;
        
        // Use interpolated positions for smoother tracking of moving targets
        float partialTicks = mc.getRenderPartialTicks();
        
        // Get interpolated player position
        double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks + player.getEyeHeight();
        double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
        
        // Get interpolated target position
        double targetX = currentTarget.lastTickPosX + (currentTarget.posX - currentTarget.lastTickPosX) * partialTicks;
        double targetY = currentTarget.lastTickPosY + (currentTarget.posY - currentTarget.lastTickPosY) * partialTicks + currentTarget.height * 0.5;
        double targetZ = currentTarget.lastTickPosZ + (currentTarget.posZ - currentTarget.lastTickPosZ) * partialTicks;
        
        // Calculate vector from player to target
        Vec3d playerPos = new Vec3d(playerX, playerY, playerZ);
        Vec3d targetPos = new Vec3d(targetX, targetY, targetZ);
        
        Vec3d direction = targetPos.subtract(playerPos).normalize();
        
        // Calculate yaw (horizontal rotation)
        float newYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
        
        // Calculate pitch (vertical rotation)
        double horizontalDistance = Math.sqrt(direction.x * direction.x + direction.z * direction.z);
        float newPitch = (float) -Math.toDegrees(Math.atan2(direction.y, horizontalDistance));
        
        // Get current player rotation for reference
        float currentPlayerYaw = player.rotationYaw;
        float currentPlayerPitch = player.rotationPitch;
        
        // Limit the camera adjustment to prevent jarring movements
        float yawDiff = MathHelper.wrapDegrees(newYaw - currentPlayerYaw);
        float pitchDiff = newPitch - currentPlayerPitch;
        
        float maxYawAdj = TargetingConfig.maxYawAdjustment;
        float maxPitchAdj = TargetingConfig.maxPitchAdjustment;
        
        yawDiff = MathHelper.clamp(yawDiff, -maxYawAdj, maxYawAdj);
        pitchDiff = MathHelper.clamp(pitchDiff, -maxPitchAdj, maxPitchAdj);
        
        targetYaw = currentPlayerYaw + yawDiff;
        targetPitch = MathHelper.clamp(currentPlayerPitch + pitchDiff, -90.0f, 90.0f);
    }
    
    private void applyCameraRotation() {
        if (mc.player == null) {
            return;
        }
        
        // Store previous rotation for smooth rendering
        mc.player.prevRotationYaw = mc.player.rotationYaw;
        mc.player.prevRotationPitch = mc.player.rotationPitch;
        
        // Apply new rotation
        mc.player.rotationYaw = currentYaw;
        mc.player.rotationPitch = currentPitch;
    }
    
    private float interpolateAngle(float current, float target, float factor) {
        float diff = MathHelper.wrapDegrees(target - current);
        return current + diff * factor;
    }
    
    private float interpolateFloat(float current, float target, float factor) {
        return current + (target - current) * factor;
    }
    
    public Entity getCurrentTarget() {
        return currentTarget;
    }
    
    public boolean isActive() {
        return currentTarget != null;
    }
}