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
    
    // Sound variety counter for cycling through different sounds
    private static int soundVarietyCounter = 0;
    
    // Default theme sounds
    private static final SoundEvent[] DEFAULT_TARGET_LOCK_SOUNDS = {
        SoundEvents.UI_BUTTON_CLICK,
        SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON,
        SoundEvents.BLOCK_LEVER_CLICK
    };
    
    private static final SoundEvent[] DEFAULT_TARGET_SWITCH_SOUNDS = {
        SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON,
        SoundEvents.UI_BUTTON_CLICK,
        SoundEvents.BLOCK_TRIPWIRE_CLICK_ON
    };
    
    private static final SoundEvent[] DEFAULT_LETHAL_TARGET_SOUNDS = {
        SoundEvents.BLOCK_ANVIL_LAND,
        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
        SoundEvents.BLOCK_NOTE_PLING
    };
    
    private static final SoundEvent[] DEFAULT_TARGET_LOST_SOUNDS = {
        SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF,
        SoundEvents.BLOCK_LEVER_CLICK,
        SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF
    };
    
    // Zelda-inspired theme sounds
    private static final SoundEvent[] ZELDA_TARGET_LOCK_SOUNDS = {
        SoundEvents.BLOCK_NOTE_PLING,
        SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
        SoundEvents.BLOCK_NOTE_BELL
    };
    
    private static final SoundEvent[] ZELDA_TARGET_SWITCH_SOUNDS = {
        SoundEvents.BLOCK_NOTE_HARP,
        SoundEvents.BLOCK_NOTE_CHIME,
        SoundEvents.ENTITY_ITEM_PICKUP
    };
    
    // Modern theme sounds
    private static final SoundEvent[] MODERN_TARGET_LOCK_SOUNDS = {
        SoundEvents.BLOCK_PISTON_EXTEND,
        SoundEvents.BLOCK_DISPENSER_LAUNCH,
        SoundEvents.ENTITY_BLAZE_HURT
    };
    
    // Subtle theme sounds
    private static final SoundEvent[] SUBTLE_TARGET_LOCK_SOUNDS = {
        SoundEvents.BLOCK_SAND_STEP,
        SoundEvents.BLOCK_CLOTH_STEP,
        SoundEvents.ENTITY_ITEM_PICKUP
    };
    
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
        
        // Get sound based on theme and variety settings
        SoundEvent sound = getTargetLockSound();
        float volume = TargetingConfig.soundVolume * TargetingConfig.targetLockVolume;
        float pitch = TargetingConfig.targetLockPitch;
        
        mc.world.playSound(mc.player, mc.player.getPosition(),
            sound, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play sound when switching between targets
     */
    public static void playTargetSwitchSound() {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableTargetSwitchSound) {
            return;
        }
        
        SoundEvent sound = getTargetSwitchSound();
        float volume = TargetingConfig.soundVolume * TargetingConfig.targetSwitchVolume;
        float pitch = TargetingConfig.targetSwitchPitch;
        
        mc.world.playSound(mc.player, mc.player.getPosition(),
            sound, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play special sound for lethal targets (one-hit kill)
     */
    public static void playLethalTargetSound() {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableLethalTargetSound) {
            return;
        }
        
        SoundEvent sound = getLethalTargetSound();
        float volume = TargetingConfig.soundVolume * TargetingConfig.lethalTargetVolume;
        float pitch = TargetingConfig.lethalTargetPitch;
        
        mc.world.playSound(mc.player, mc.player.getPosition(),
            sound, SoundCategory.PLAYERS, volume, pitch);
    }
    
    /**
     * Play sound when losing target lock
     */
    public static void playTargetLostSound() {
        if (!TargetingConfig.enableSounds || !TargetingConfig.enableTargetLostSound) {
            return;
        }
        
        SoundEvent sound = getTargetLostSound();
        float volume = TargetingConfig.soundVolume * TargetingConfig.targetLostVolume;
        float pitch = TargetingConfig.targetLostPitch;
        
        mc.world.playSound(mc.player, mc.player.getPosition(),
            sound, SoundCategory.PLAYERS, volume, pitch);
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
    
    /**
     * Get target lock sound based on theme and variety settings
     */
    private static SoundEvent getTargetLockSound() {
        SoundEvent[] sounds = getTargetLockSoundsForTheme();
        return selectSoundFromArray(sounds);
    }
    
    /**
     * Get target switch sound based on theme and variety settings
     */
    private static SoundEvent getTargetSwitchSound() {
        SoundEvent[] sounds = getTargetSwitchSoundsForTheme();
        return selectSoundFromArray(sounds);
    }
    
    /**
     * Get lethal target sound based on theme and variety settings
     */
    private static SoundEvent getLethalTargetSound() {
        SoundEvent[] sounds = getLethalTargetSoundsForTheme();
        return selectSoundFromArray(sounds);
    }
    
    /**
     * Get target lost sound based on theme and variety settings
     */
    private static SoundEvent getTargetLostSound() {
        SoundEvent[] sounds = getTargetLostSoundsForTheme();
        return selectSoundFromArray(sounds);
    }
    
    /**
     * Get sound array for current theme
     */
    private static SoundEvent[] getTargetLockSoundsForTheme() {
        switch (TargetingConfig.soundTheme.toLowerCase()) {
            case "zelda":
                return ZELDA_TARGET_LOCK_SOUNDS;
            case "modern":
                return MODERN_TARGET_LOCK_SOUNDS;
            case "subtle":
                return SUBTLE_TARGET_LOCK_SOUNDS;
            default:
                return DEFAULT_TARGET_LOCK_SOUNDS;
        }
    }
    
    private static SoundEvent[] getTargetSwitchSoundsForTheme() {
        switch (TargetingConfig.soundTheme.toLowerCase()) {
            case "zelda":
                return ZELDA_TARGET_SWITCH_SOUNDS;
            case "modern":
                return DEFAULT_TARGET_SWITCH_SOUNDS; // Use default for modern theme
            case "subtle":
                return DEFAULT_TARGET_SWITCH_SOUNDS; // Use default for subtle theme
            default:
                return DEFAULT_TARGET_SWITCH_SOUNDS;
        }
    }
    
    private static SoundEvent[] getLethalTargetSoundsForTheme() {
        switch (TargetingConfig.soundTheme.toLowerCase()) {
            case "zelda":
                return ZELDA_TARGET_LOCK_SOUNDS; // Use lock sounds for zelda theme
            case "modern":
                return DEFAULT_LETHAL_TARGET_SOUNDS;
            case "subtle":
                return DEFAULT_LETHAL_TARGET_SOUNDS;
            default:
                return DEFAULT_LETHAL_TARGET_SOUNDS;
        }
    }
    
    private static SoundEvent[] getTargetLostSoundsForTheme() {
        // All themes use default target lost sounds for consistency
        return DEFAULT_TARGET_LOST_SOUNDS;
    }
    
    /**
     * Select sound from array based on variety settings
     */
    private static SoundEvent selectSoundFromArray(SoundEvent[] sounds) {
        if (sounds.length == 0) {
            return SoundEvents.UI_BUTTON_CLICK; // Fallback sound
        }
        
        if (TargetingConfig.enableSoundVariety && sounds.length > 1) {
            // Cycle through sounds
            int index = soundVarietyCounter % sounds.length;
            soundVarietyCounter++;
            return sounds[index];
        } else {
            // Always use first sound in array
            return sounds[0];
        }
    }
}