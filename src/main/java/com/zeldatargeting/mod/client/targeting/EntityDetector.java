package com.zeldatargeting.mod.client.targeting;

import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityDetector {
    
    private final Minecraft mc;
    
    public EntityDetector() {
        this.mc = Minecraft.getMinecraft();
    }
    
    public Entity findNearestTarget() {
        EntityPlayer player = mc.player;
        if (player == null || mc.world == null) {
            return null;
        }
        
        List<Entity> validTargets = getValidTargetsInRange(player);
        if (validTargets.isEmpty()) {
            return null;
        }
        
        // Sort by distance and return the nearest
        validTargets.sort(Comparator.comparingDouble(entity -> 
            player.getDistanceSq(entity)));
        
        return validTargets.get(0);
    }
    
    public Entity findNextTarget(Entity currentTarget, boolean forward) {
        EntityPlayer player = mc.player;
        if (player == null || mc.world == null) {
            return null;
        }
        
        List<Entity> validTargets = getValidTargetsInRange(player);
        if (validTargets.isEmpty()) {
            return null;
        }
        
        // Sort by angle from player's look direction
        Vec3d playerLook = player.getLookVec();
        validTargets.sort((e1, e2) -> {
            double angle1 = getAngleToEntity(player, e1, playerLook);
            double angle2 = getAngleToEntity(player, e2, playerLook);
            return Double.compare(angle1, angle2);
        });
        
        if (currentTarget == null) {
            return validTargets.get(0);
        }
        
        int currentIndex = validTargets.indexOf(currentTarget);
        if (currentIndex == -1) {
            return validTargets.get(0);
        }
        
        if (forward) {
            return validTargets.get((currentIndex + 1) % validTargets.size());
        } else {
            return validTargets.get((currentIndex - 1 + validTargets.size()) % validTargets.size());
        }
    }
    
    private List<Entity> getValidTargetsInRange(EntityPlayer player) {
        List<Entity> validTargets = new ArrayList<>();
        World world = player.world;
        
        // Create bounding box for search area
        double range = TargetingConfig.getTargetingRange();
        AxisAlignedBB searchBox = new AxisAlignedBB(
            player.posX - range, player.posY - range, player.posZ - range,
            player.posX + range, player.posY + range, player.posZ + range
        );
        
        List<Entity> nearbyEntities = world.getEntitiesWithinAABB(Entity.class, searchBox);
        
        for (Entity entity : nearbyEntities) {
            if (isValidTarget(player, entity)) {
                validTargets.add(entity);
            }
        }
        
        return validTargets;
    }
    
    private boolean isValidTarget(EntityPlayer player, Entity entity) {
        // Don't target the player themselves
        if (entity == player) {
            return false;
        }
        
        // Don't target dead entities
        if (!entity.isEntityAlive()) {
            return false;
        }
        
        // Only target living entities for now
        if (!(entity instanceof EntityLiving)) {
            return false;
        }
        
        // Check distance
        double distance = player.getDistanceSq(entity);
        double maxRange = TargetingConfig.getTargetingRange();
        if (distance > maxRange * maxRange) {
            return false;
        }
        
        // Check angle (within field of view)
        Vec3d playerLook = player.getLookVec();
        double angle = getAngleToEntity(player, entity, playerLook);
        if (angle > TargetingConfig.getMaxAngle()) {
            return false;
        }
        
        // Check line of sight
        if (TargetingConfig.shouldRequireLineOfSight() && !hasLineOfSight(player, entity)) {
            return false;
        }
        
        // Additional filtering can be added here based on configuration
        return isTargetableEntityType(entity);
    }
    
    private double getAngleToEntity(EntityPlayer player, Entity entity, Vec3d playerLook) {
        Vec3d toEntity = new Vec3d(
            entity.posX - player.posX,
            entity.posY - player.posY,
            entity.posZ - player.posZ
        ).normalize();
        
        double dot = playerLook.dotProduct(toEntity);
        return Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, dot))));
    }
    
    private boolean hasLineOfSight(EntityPlayer player, Entity target) {
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d end = new Vec3d(target.posX, target.posY + target.height * 0.5, target.posZ);
        
        RayTraceResult result = player.world.rayTraceBlocks(start, end, false, true, false);
        return result == null || result.typeOfHit != RayTraceResult.Type.BLOCK;
    }
    
    private boolean isTargetableEntityType(Entity entity) {
        // Target hostile mobs
        if (entity instanceof IMob) {
            return TargetingConfig.shouldTargetHostileMobs();
        }
        
        // Target animals
        if (entity instanceof EntityAnimal) {
            return TargetingConfig.shouldTargetPassiveMobs();
        }
        
        // Target other players
        if (entity instanceof EntityPlayer) {
            return TargetingConfig.shouldTargetPlayers();
        }
        
        // Target other living entities (neutral mobs)
        if (entity instanceof EntityLiving) {
            return TargetingConfig.shouldTargetNeutralMobs();
        }
        
        return false;
    }
}