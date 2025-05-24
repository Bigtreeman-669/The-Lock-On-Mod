package com.zeldatargeting.mod.client.audio;

import com.zeldatargeting.mod.client.combat.DamageCalculator;
import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TargetingSounds {
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    // Carefully selected Minecraft sounds that fit targeting theme
    private static final SoundEvent TARGET_LOCK_SOUND = SoundEvents.UI_BUTTON_CLICK;
    private static final SoundEvent TARGET_SWITCH_SOUND = SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON;
    private static final SoundEvent LETHAL_TARGET_SOUND = SoundEvents.BLOCK_ANVIL_LAND;
    private static final SoundEvent TARGET_LOST_SOUND = SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF;
    
    /**
     * Play sound when locking onto a new target
     */
    public static void playTargetLockSound(Entity target) {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableTargetLockSound) {
            return;
        }
        
        // Check if this is a lethal target for special sound
        if (target instanceof EntityLiving && TargetingConfig.enableLethalTargetSound) {
            int hitsToKill = DamageCalculator.calculateHitsToKill(target);
            if (hitsToKill == 1) {
                playLethalTargetSound();
                return;
            }
        }
        
        // Play normal target lock sound
        float volume = TargetingConfig.soundVolume * TargetingConfig.targetLockVolume;
        float pitch = 1.2f; // Slightly higher pitch for crisp targeting feel
        
        mc.world.playSound(mc.player, mc.player.getPosition(), 
            TARGET_LOCK_SOUND, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play sound when switching between targets
     */
    public static void playTargetSwitchSound() {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableTargetSwitchSound) {
            return;
        }
        
        float volume = TargetingConfig.soundVolume * TargetingConfig.targetSwitchVolume;
        float pitch = 1.0f; // Normal pitch for subtle switching
        
        mc.world.playSound(mc.player, mc.player.getPosition(), 
            TARGET_SWITCH_SOUND, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play special sound for lethal targets (one-hit kill)
     */
    public static void playLethalTargetSound() {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableLethalTargetSound) {
            return;
        }
        
        float volume = TargetingConfig.soundVolume * TargetingConfig.lethalTargetVolume;
        float pitch = 1.5f; // Higher pitch for dramatic effect
        
        mc.world.playSound(mc.player, mc.player.getPosition(), 
            LETHAL_TARGET_SOUND, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play sound when losing target lock
     */
    public static void playTargetLostSound() {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableTargetLostSound) {
            return;
        }
        
        float volume = TargetingConfig.soundVolume * TargetingConfig.targetLostVolume;
        float pitch = 0.8f; // Lower pitch for "lost" feeling
        
        mc.world.playSound(mc.player, mc.player.getPosition(), 
            TARGET_LOST_SOUND, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play a subtle confirmation sound for successful targeting actions
     */
    public static void playConfirmationSound() {
        if (!TargetingConfig.enableSounds) {
            return;
        }
        
        float volume = TargetingConfig.soundVolume * 0.3f; // Very quiet
        float pitch = 1.8f; // High pitch for subtle confirmation
        
        mc.world.playSound(mc.player, mc.player.getPosition(), 
            SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Check if a target qualifies for lethal sound
     */
    public static boolean isLethalTarget(Entity target) {
        if (!(target instanceof EntityLiving)) {
            return false;
        }
        
        return DamageCalculator.calculateHitsToKill(target) == 1;
    }
    
    /**
     * Play appropriate sound based on target type and context
     */
    public static void playContextualTargetSound(Entity target, boolean isNewTarget) {
        if (isNewTarget) {
            playTargetLockSound(target);
        } else {
            playTargetSwitchSound();
        }
    }
}