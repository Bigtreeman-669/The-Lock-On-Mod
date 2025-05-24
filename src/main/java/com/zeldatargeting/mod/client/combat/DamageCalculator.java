package com.zeldatargeting.mod.client.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DamageCalculator {
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    
    /**
     * Calculate the damage that would be dealt to the target entity with the current weapon
     */
    public static float calculateDamage(Entity target) {
        EntityPlayer player = mc.player;
        if (player == null || !(target instanceof EntityLiving)) {
            return 0.0f;
        }
        
        ItemStack heldItem = player.getHeldItemMainhand();
        if (heldItem.isEmpty()) {
            // Base hand damage
            return calculateBaseDamage(player, (EntityLiving) target);
        }
        
        return calculateWeaponDamage(player, (EntityLiving) target, heldItem);
    }
    
    /**
     * Calculate how many hits it would take to kill the target
     */
    public static int calculateHitsToKill(Entity target) {
        if (!(target instanceof EntityLiving)) {
            return -1; // Unknown for non-living entities
        }
        
        EntityLiving living = (EntityLiving) target;
        float damage = calculateDamage(target);
        
        if (damage <= 0) {
            return -1; // Can't kill with 0 damage
        }
        
        float targetHealth = living.getHealth();
        return (int) Math.ceil(targetHealth / damage);
    }
    
    /**
     * Get damage prediction text for display
     */
    public static String getDamagePredictionText(Entity target) {
        if (!(target instanceof EntityLiving)) {
            return "";
        }
        
        float damage = calculateDamage(target);
        int hitsToKill = calculateHitsToKill(target);
        
        if (damage <= 0) {
            return "No damage";
        }
        
        if (hitsToKill == 1) {
            return String.format("%.1f dmg (LETHAL)", damage);
        } else if (hitsToKill > 0 && hitsToKill <= 99) {
            return String.format("%.1f dmg (%d hits)", damage, hitsToKill);
        } else {
            return String.format("%.1f dmg", damage);
        }
    }
    
    /**
     * Get color for damage prediction based on lethality
     */
    public static int getDamagePredictionColor(Entity target) {
        int hitsToKill = calculateHitsToKill(target);
        
        if (hitsToKill == 1) {
            return 0xFFFF4444; // Bright red for lethal
        } else if (hitsToKill <= 3) {
            return 0xFFFFAA44; // Orange for few hits
        } else if (hitsToKill <= 6) {
            return 0xFFFFFF44; // Yellow for moderate
        } else {
            return 0xFFAAAAAA; // Gray for many hits
        }
    }
    
    private static float calculateBaseDamage(EntityPlayer player, EntityLiving target) {
        // Base hand damage (usually 1.0)
        float baseDamage = 1.0f;
        
        // Apply strength effect
        if (player.isPotionActive(net.minecraft.init.MobEffects.STRENGTH)) {
            PotionEffect effect = player.getActivePotionEffect(net.minecraft.init.MobEffects.STRENGTH);
            if (effect != null) {
                int amplifier = effect.getAmplifier();
                baseDamage += (amplifier + 1) * 3.0f;
            }
        }
        
        // Apply weakness effect
        if (player.isPotionActive(net.minecraft.init.MobEffects.WEAKNESS)) {
            PotionEffect effect = player.getActivePotionEffect(net.minecraft.init.MobEffects.WEAKNESS);
            if (effect != null) {
                int amplifier = effect.getAmplifier();
                baseDamage -= (amplifier + 1) * 4.0f;
            }
        }
        
        return Math.max(0, baseDamage);
    }
    
    private static float calculateWeaponDamage(EntityPlayer player, EntityLiving target, ItemStack weapon) {
        float baseDamage = 1.0f; // Default base damage
        
        // Get weapon base damage using attribute modifiers (MC 1.12.2 method)
        for (AttributeModifier modifier : weapon.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
            if (modifier.getOperation() == 0) { // Addition operation
                baseDamage += modifier.getAmount();
            }
        }
        
        // Apply enchantments
        baseDamage += EnchantmentHelper.getModifierForCreature(weapon, target.getCreatureAttribute());
        
        // Apply strength effect
        if (player.isPotionActive(net.minecraft.init.MobEffects.STRENGTH)) {
            PotionEffect effect = player.getActivePotionEffect(net.minecraft.init.MobEffects.STRENGTH);
            if (effect != null) {
                int amplifier = effect.getAmplifier();
                baseDamage += (amplifier + 1) * 3.0f;
            }
        }
        
        // Apply weakness effect
        if (player.isPotionActive(net.minecraft.init.MobEffects.WEAKNESS)) {
            PotionEffect effect = player.getActivePotionEffect(net.minecraft.init.MobEffects.WEAKNESS);
            if (effect != null) {
                int amplifier = effect.getAmplifier();
                baseDamage -= (amplifier + 1) * 4.0f;
            }
        }
        
        // Apply critical hit multiplier (simplified - assumes full charge)
        if (canCriticalHit(player)) {
            baseDamage *= 1.5f;
        }
        
        return Math.max(0, baseDamage);
    }
    
    private static boolean canCriticalHit(EntityPlayer player) {
        // Simplified critical hit check
        return player.fallDistance > 0.0f && !player.onGround && !player.isOnLadder() && 
               !player.isInWater() && !player.isPotionActive(net.minecraft.init.MobEffects.BLINDNESS) && 
               !player.isRiding();
    }
    
    /**
     * Check if the target has specific vulnerabilities
     */
    public static boolean hasWeakness(EntityLiving target) {
        // Check for weakness potion effect
        return target.isPotionActive(net.minecraft.init.MobEffects.WEAKNESS);
    }
    
    /**
     * Check if the target has damage resistance
     */
    public static boolean hasResistance(EntityLiving target) {
        // Check for resistance potion effect
        return target.isPotionActive(net.minecraft.init.MobEffects.RESISTANCE);
    }
    
    /**
     * Get vulnerability indicator text
     */
    public static String getVulnerabilityText(EntityLiving target) {
        if (hasWeakness(target)) {
            return "WEAK";
        } else if (hasResistance(target)) {
            return "RESIST";
        }
        return "";
    }
    
    /**
     * Get vulnerability indicator color
     */
    public static int getVulnerabilityColor(EntityLiving target) {
        if (hasWeakness(target)) {
            return 0xFF44FF44; // Green for weakness
        } else if (hasResistance(target)) {
            return 0xFF4444FF; // Blue for resistance
        }
        return 0xFFFFFFFF; // White default
    }
}