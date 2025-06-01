package com.zeldatargeting.mod.client.render;

import com.zeldatargeting.mod.client.TargetingManager;
import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@SideOnly(Side.CLIENT)
public class DamageNumbersRenderer {
    
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Random random = new Random();
    private static final ConcurrentLinkedQueue<DamageNumber> damageNumbers = new ConcurrentLinkedQueue<>();
    
    /**
     * Enhanced data class representing a floating damage number with advanced animations
     */
    public static class DamageNumber {
        public final Entity entity;
        public final double startX, startY, startZ;
        public double x, y, z;
        public final float damage;
        public final int color;
        public final boolean isCritical;
        public final boolean isLethal;
        public final boolean isHeal;
        public final DamageType damageType;
        public final int maxAge;
        public int age;
        public float scale;
        public final float velocityY;
        public final float velocityX;
        public final float velocityZ;
        public final float rotationSpeed;
        public float rotation;
        
        public enum DamageType {
            NORMAL, CRITICAL, LETHAL, HEAL, SHIELD_BLOCKED, ARMOR_REDUCED
        }
        
        public DamageNumber(Entity entity, float damage, boolean isCritical, boolean isLethal) {
            this(entity, damage, isCritical, isLethal, false, DamageType.NORMAL);
        }
        
        public DamageNumber(Entity entity, float damage, boolean isCritical, boolean isLethal, boolean isHeal, DamageType type) {
            this.entity = entity;
            this.damage = Math.abs(damage);
            this.isCritical = isCritical;
            this.isLethal = isLethal && !isHeal;
            this.isHeal = isHeal;
            this.damageType = type;
            this.maxAge = TargetingConfig.damageNumbersDuration + (isCritical ? 20 : 0);
            this.age = 0;
            this.scale = TargetingConfig.damageNumbersScale * (isCritical ? 1.3f : 1.0f);
            this.rotation = 0.0f;
            this.rotationSpeed = (random.nextFloat() - 0.5f) * 2.0f;
            
            // Enhanced positioning with slight random offset to prevent overlap
            double offsetX = (random.nextFloat() - 0.5f) * 0.3f;
            double offsetZ = (random.nextFloat() - 0.5f) * 0.3f;
            this.startX = entity.posX + offsetX;
            this.startY = entity.posY + entity.height + TargetingConfig.damageNumbersOffset + random.nextFloat() * 0.2f;
            this.startZ = entity.posZ + offsetZ;
            this.x = startX;
            this.y = startY;
            this.z = startZ;
            
            // Enhanced movement patterns based on damage type
            if (isLethal) {
                this.velocityY = 0.04f + random.nextFloat() * 0.02f; // Faster upward movement
                this.velocityX = (random.nextFloat() - 0.5f) * 0.02f;
                this.velocityZ = (random.nextFloat() - 0.5f) * 0.01f;
            } else if (isCritical) {
                this.velocityY = 0.03f + random.nextFloat() * 0.015f;
                this.velocityX = (random.nextFloat() - 0.5f) * 0.015f;
                this.velocityZ = (random.nextFloat() - 0.5f) * 0.008f;
            } else if (isHeal) {
                this.velocityY = 0.015f + random.nextFloat() * 0.01f; // Slower, gentler movement
                this.velocityX = (random.nextFloat() - 0.5f) * 0.005f;
                this.velocityZ = (random.nextFloat() - 0.5f) * 0.005f;
            } else {
                this.velocityY = 0.02f + random.nextFloat() * 0.01f;
                this.velocityX = (random.nextFloat() - 0.5f) * 0.01f;
                this.velocityZ = (random.nextFloat() - 0.5f) * 0.006f;
            }
            
            // Enhanced color system
            this.color = determineColor();
        }
        
        private int determineColor() {
            if (!TargetingConfig.damageNumbersColors) {
                return TargetingConfig.damageNumbersColor;
            }
            
            if (isHeal) {
                return 0xFF00FF88; // Bright green for healing
            } else if (isLethal) {
                return TargetingConfig.lethalDamageColor; // Bright red
            } else if (isCritical && TargetingConfig.damageNumbersCrits) {
                return TargetingConfig.criticalDamageColor; // Yellow/orange
            } else {
                // Color based on damage amount for better feedback
                if (damage >= 10.0f) {
                    return 0xFFFF6666; // High damage - bright red
                } else if (damage >= 5.0f) {
                    return 0xFFFFAA66; // Medium damage - orange
                } else if (damage >= 2.0f) {
                    return 0xFFFFDD66; // Low damage - yellow
                } else {
                    return 0xFFCCCCCC; // Very low damage - gray
                }
            }
        }
        
        public void update() {
            age++;
            
            // Move upward and slightly sideways
            y += velocityY;
            x += velocityX * Math.sin(age * 0.1);
            
            // Scale animation for critical hits
            if (isCritical && TargetingConfig.damageNumbersCrits && age < 10) {
                scale = TargetingConfig.damageNumbersScale * (1.0f + 0.5f * (float)Math.sin(age * 0.5));
            }
        }
        
        public boolean shouldRemove() {
            return age >= maxAge;
        }
        
        public float getAlpha() {
            if (!TargetingConfig.damageNumbersFadeOut) {
                return 1.0f;
            }
            
            if (age < maxAge * 0.7f) {
                return 1.0f; // Full opacity for most of the duration
            } else {
                // Fade out in the last 30% of duration
                float fadeProgress = (age - maxAge * 0.7f) / (maxAge * 0.3f);
                return 1.0f - fadeProgress;
            }
        }
        
        public String getText() {
            if (isLethal) {
                return "FATAL!";
            } else if (damage == (int)damage) {
                return String.valueOf((int)damage);
            } else {
                return String.format("%.1f", damage);
            }
        }
    }
    
    /**
     * Called when an entity takes damage to create damage numbers
     */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (!TargetingConfig.enableDamageNumbers || mc.world == null || mc.player == null) {
            return;
        }
        
        EntityLivingBase entity = event.getEntityLiving();
        float damage = event.getAmount();
        
        // Only show damage numbers for targeted entities or nearby entities
        TargetingManager manager = TargetingManager.getInstance();
        boolean isTargeted = manager != null && manager.getCurrentTarget() == entity;
        boolean isNearby = mc.player.getDistanceSq(entity) < 100; // Within 10 blocks
        
        if (!isTargeted && !isNearby) {
            return;
        }
        
        // Determine if this is a critical hit (simple heuristic)
        boolean isCritical = damage > entity.getMaxHealth() * 0.3f || random.nextFloat() < 0.1f;
        
        // Determine if this is a lethal hit
        boolean isLethal = entity.getHealth() - damage <= 0;
        
        // Create damage number
        DamageNumber damageNumber = new DamageNumber(entity, damage, isCritical, isLethal);
        damageNumbers.add(damageNumber);
        
        // Limit the number of damage numbers to prevent spam
        while (damageNumbers.size() > 50) {
            damageNumbers.poll(); // Remove first element safely
        }
    }
    
    /**
     * Render all damage numbers in the world
     */
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (!TargetingConfig.enableDamageNumbers || damageNumbers.isEmpty()) {
            return;
        }
        
        float partialTicks = event.getPartialTicks();
        
        // Update and render each damage number using thread-safe iteration
        Iterator<DamageNumber> iterator = damageNumbers.iterator();
        while (iterator.hasNext()) {
            DamageNumber damageNumber = iterator.next();
            
            // Update damage number
            damageNumber.update();
            
            // Remove if expired (thread-safe removal)
            if (damageNumber.shouldRemove()) {
                damageNumbers.remove(damageNumber);
                continue;
            }
            
            // Render damage number
            renderDamageNumber(damageNumber, partialTicks);
        }
    }
    
    /**
     * Render a single damage number in 3D space
     */
    private void renderDamageNumber(DamageNumber damageNumber, float partialTicks) {
        double x = damageNumber.x;
        double y = damageNumber.y;
        double z = damageNumber.z;
        
        // Offset for camera position
        x -= mc.getRenderManager().viewerPosX;
        y -= mc.getRenderManager().viewerPosY;
        z -= mc.getRenderManager().viewerPosZ;
        
        GlStateManager.pushMatrix();
        
        // Position in world
        GlStateManager.translate(x, y, z);
        
        // Make it face the player (billboard effect)
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        
        // Apply scaling
        float scale = damageNumber.scale * 0.025f; // Base scale
        GlStateManager.scale(-scale, -scale, scale);
        
        // Set up rendering state
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        FontRenderer fontRenderer = mc.fontRenderer;
        String text = damageNumber.getText();
        int textWidth = fontRenderer.getStringWidth(text);
        
        // Extract color components and apply alpha
        float alpha = damageNumber.getAlpha();
        int color = damageNumber.color;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int finalColor = ((int)(alpha * 255) << 24) | (red << 16) | (green << 8) | blue;
        
        // Draw text background for better visibility
        if (alpha > 0.1f) {
            int bgAlpha = (int)(alpha * 128); // Semi-transparent background
            drawRect(-textWidth / 2 - 2, -4, textWidth / 2 + 2, 8, (bgAlpha << 24));
        }
        
        // Draw the damage text
        fontRenderer.drawString(text, -textWidth / 2, 0, finalColor);
        
        // Additional effects for critical hits
        if (damageNumber.isCritical && TargetingConfig.damageNumbersCrits && damageNumber.age < 20) {
            // Draw exclamation marks around critical hits
            if (damageNumber.age % 4 < 2) { // Blinking effect
                fontRenderer.drawString("!", -textWidth / 2 - 10, -2, finalColor);
                fontRenderer.drawString("!", textWidth / 2 + 6, -2, finalColor);
            }
        }
        
        // Restore rendering state
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        
        GlStateManager.popMatrix();
    }
    
    /**
     * Draw a rectangle (helper method for background)
     */
    private void drawRect(int left, int top, int right, int bottom, int color) {
        if (left < right) {
            int temp = left;
            left = right;
            right = temp;
        }
        
        if (top < bottom) {
            int temp = top;
            top = bottom;
            bottom = temp;
        }
        
        float alpha = (float)(color >>> 24) / 255.0F;
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, 
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, 
            GlStateManager.SourceFactor.ONE, 
            GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        buffer.pos(left, bottom, 0.0D).endVertex();
        buffer.pos(right, bottom, 0.0D).endVertex();
        buffer.pos(right, top, 0.0D).endVertex();
        buffer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    /**
     * Clear all damage numbers (useful for cleanup)
     */
    public static void clearAll() {
        damageNumbers.clear();
    }
    
    /**
     * Get the current number of active damage numbers
     */
    public static int getActiveCount() {
        return damageNumbers.size();
    }
}