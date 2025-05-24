package com.zeldatargeting.mod.client.targeting;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class TargetSelector {
    
    public enum TargetPriority {
        DISTANCE,
        HEALTH,
        THREAT_LEVEL,
        ANGLE
    }
    
    private TargetPriority currentPriority = TargetPriority.DISTANCE;
    
    public Entity selectBestTarget(List<Entity> candidates, EntityPlayer player) {
        if (candidates.isEmpty() || player == null) {
            return null;
        }
        
        switch (currentPriority) {
            case DISTANCE:
                return selectByDistance(candidates, player);
            case HEALTH:
                return selectByHealth(candidates, player);
            case THREAT_LEVEL:
                return selectByThreatLevel(candidates, player);
            case ANGLE:
                return selectByAngle(candidates, player);
            default:
                return selectByDistance(candidates, player);
        }
    }
    
    private Entity selectByDistance(List<Entity> candidates, EntityPlayer player) {
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Entity entity : candidates) {
            double distance = player.getDistanceSq(entity);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = entity;
            }
        }
        
        return nearest;
    }
    
    private Entity selectByHealth(List<Entity> candidates, EntityPlayer player) {
        Entity lowestHealth = null;
        float lowestHealthValue = Float.MAX_VALUE;
        
        for (Entity entity : candidates) {
            if (entity instanceof EntityLiving) {
                EntityLiving living = (EntityLiving) entity;
                float health = living.getHealth();
                if (health < lowestHealthValue) {
                    lowestHealthValue = health;
                    lowestHealth = entity;
                }
            }
        }
        
        return lowestHealth != null ? lowestHealth : selectByDistance(candidates, player);
    }
    
    private Entity selectByThreatLevel(List<Entity> candidates, EntityPlayer player) {
        Entity highestThreat = null;
        int highestThreatLevel = -1;
        
        for (Entity entity : candidates) {
            int threatLevel = calculateThreatLevel(entity);
            if (threatLevel > highestThreatLevel) {
                highestThreatLevel = threatLevel;
                highestThreat = entity;
            }
        }
        
        return highestThreat != null ? highestThreat : selectByDistance(candidates, player);
    }
    
    private Entity selectByAngle(List<Entity> candidates, EntityPlayer player) {
        Entity closestToCenter = null;
        double smallestAngle = Double.MAX_VALUE;
        
        for (Entity entity : candidates) {
            double angle = calculateAngleFromCenter(entity, player);
            if (angle < smallestAngle) {
                smallestAngle = angle;
                closestToCenter = entity;
            }
        }
        
        return closestToCenter != null ? closestToCenter : selectByDistance(candidates, player);
    }
    
    private int calculateThreatLevel(Entity entity) {
        int threatLevel = 0;
        
        // Base threat for hostile mobs
        if (entity instanceof IMob) {
            threatLevel += 3;
        }
        
        // Additional threat based on entity type
        String entityName = entity.getClass().getSimpleName().toLowerCase();
        
        // High threat entities
        if (entityName.contains("creeper") || entityName.contains("witch") || 
            entityName.contains("skeleton") || entityName.contains("wither")) {
            threatLevel += 2;
        }
        
        // Medium threat entities
        if (entityName.contains("zombie") || entityName.contains("spider") || 
            entityName.contains("enderman")) {
            threatLevel += 1;
        }
        
        // Consider health as threat factor
        if (entity instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) entity;
            float healthRatio = living.getHealth() / living.getMaxHealth();
            if (healthRatio > 0.8f) {
                threatLevel += 1;
            }
        }
        
        return threatLevel;
    }
    
    private double calculateAngleFromCenter(Entity entity, EntityPlayer player) {
        // Calculate angle between player's look direction and direction to entity
        double deltaX = entity.posX - player.posX;
        double deltaZ = entity.posZ - player.posZ;
        
        double entityAngle = Math.atan2(-deltaX, deltaZ);
        double playerAngle = Math.toRadians(player.rotationYaw);
        
        double angleDiff = Math.abs(entityAngle - playerAngle);
        
        // Normalize to [0, PI]
        if (angleDiff > Math.PI) {
            angleDiff = 2 * Math.PI - angleDiff;
        }
        
        return angleDiff;
    }
    
    public void setPriority(TargetPriority priority) {
        this.currentPriority = priority;
    }
    
    public TargetPriority getPriority() {
        return currentPriority;
    }
    
    public void cyclePriority() {
        TargetPriority[] priorities = TargetPriority.values();
        int currentIndex = currentPriority.ordinal();
        currentPriority = priorities[(currentIndex + 1) % priorities.length];
    }
}