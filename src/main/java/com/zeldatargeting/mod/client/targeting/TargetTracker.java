package com.zeldatargeting.mod.client.targeting;

import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class TargetTracker {
    
    private Entity currentTarget;
    private int tickCounter = 0;
    private boolean isValid = false;
    
    private final Minecraft mc;
    
    public TargetTracker() {
        this.mc = Minecraft.getMinecraft();
    }
    
    public void setTarget(Entity target) {
        this.currentTarget = target;
        this.tickCounter = 0;
        this.isValid = validateTarget();
    }
    
    public void update(Entity target) {
        if (target != currentTarget) {
            setTarget(target);
            return;
        }
        
        tickCounter++;
        
        // Validate target periodically to avoid expensive checks every tick
        if (tickCounter >= TargetingConfig.validationInterval) {
            this.isValid = validateTarget();
            tickCounter = 0;
        }
    }
    
    public boolean isTargetValid() {
        return isValid && currentTarget != null;
    }
    
    public Entity getCurrentTarget() {
        return currentTarget;
    }
    
    private boolean validateTarget() {
        if (currentTarget == null) {
            return false;
        }
        
        EntityPlayer player = mc.player;
        if (player == null) {
            return false;
        }
        
        // Check if target is still alive
        if (!currentTarget.isEntityAlive()) {
            return false;
        }
        
        // Check if target is still in range
        double distance = player.getDistanceSq(currentTarget);
        double maxDistance = TargetingConfig.getMaxTrackingDistance();
        if (distance > maxDistance * maxDistance) {
            return false;
        }
        
        // Check if target is in the same dimension
        if (currentTarget.dimension != player.dimension) {
            return false;
        }
        
        return true;
    }
    
    public void clearTarget() {
        this.currentTarget = null;
        this.isValid = false;
        this.tickCounter = 0;
    }
}