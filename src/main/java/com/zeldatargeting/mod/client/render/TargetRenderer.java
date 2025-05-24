package com.zeldatargeting.mod.client.render;

import com.zeldatargeting.mod.client.TargetingManager;
import com.zeldatargeting.mod.client.combat.DamageCalculator;
import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;


public class TargetRenderer {
    
    private final Minecraft mc;
    private long animationTime = 0;
    
    public TargetRenderer() {
        this.mc = Minecraft.getMinecraft();
    }
    
    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        
        TargetingManager manager = TargetingManager.getInstance();
        if (manager == null || !manager.isActive()) {
            return;
        }
        
        Entity target = manager.getCurrentTarget();
        if (target == null) {
            return;
        }
        
        animationTime = System.currentTimeMillis();
        
        // Render 3D reticle around target
        render3DReticle(target);
        
        // Render 2D HUD elements
        render2DHUD(target, event.getResolution());
    }
    
    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        TargetingManager manager = TargetingManager.getInstance();
        if (manager == null) {
            return;
        }
        
        // Render red indicator above current target ONLY when locked on
        Entity currentTarget = manager.getCurrentTarget();
        if (currentTarget != null && manager.isActive()) {
            renderRedIndicator(currentTarget, event.getPartialTicks());
        }
    }
    
    private void render3DReticle(Entity target) {
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        
        // Calculate target position
        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.getRenderPartialTicks();
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.getRenderPartialTicks();
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.getRenderPartialTicks();
        
        // Offset for camera position
        x -= mc.getRenderManager().viewerPosX;
        y -= mc.getRenderManager().viewerPosY;
        z -= mc.getRenderManager().viewerPosZ;
        
        // Calculate reticle size based on target size and distance
        float targetHeight = target.height;
        float targetWidth = target.width;
        double distance = mc.player.getDistanceSq(target);
        float scale = (float) Math.max(0.5, Math.min(2.0, 10.0 / Math.sqrt(distance)));
        
        // Animation pulse
        float pulse = (float) (0.8 + 0.2 * Math.sin(animationTime * 0.01));
        scale *= pulse;
        
        // Draw reticle rings
        drawReticleRing(x, y + targetHeight * 0.5, z, targetWidth * scale, targetHeight * scale);
        
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
    
    private void drawReticleRing(double x, double y, double z, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        // Get current target for enhanced coloring
        TargetingManager manager = TargetingManager.getInstance();
        Entity target = manager != null ? manager.getCurrentTarget() : null;
        
        // Enhanced color system based on lethality and target type
        float red = 1.0f, green = 0.2f, blue = 0.2f, alpha = 0.8f;
        
        if (target instanceof EntityLiving && TargetingConfig.highlightLethalTargets) {
            int hitsToKill = DamageCalculator.calculateHitsToKill(target);
            if (hitsToKill == 1) {
                // Bright pulsing red for lethal targets
                float pulse = (float) (0.8 + 0.2 * Math.sin(System.currentTimeMillis() * 0.015));
                red = 1.0f;
                green = 0.1f * pulse;
                blue = 0.1f * pulse;
                alpha = 0.9f + 0.1f * pulse;
            } else if (hitsToKill <= 3) {
                // Orange for low-hit targets
                red = 1.0f;
                green = 0.6f;
                blue = 0.1f;
            } else {
                // Standard red for normal targets
                red = 1.0f;
                green = 0.2f;
                blue = 0.2f;
            }
        }
        
        GlStateManager.color(red, green, blue, alpha);
        
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        
        // Draw circular reticle with enhanced thickness for lethal targets
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            double offsetX = Math.cos(angle) * width;
            double offsetZ = Math.sin(angle) * width;
            buffer.pos(x + offsetX, y, z + offsetZ).endVertex();
        }
        
        tessellator.draw();
        
        // Draw additional inner ring for lethal targets
        if (target instanceof EntityLiving && TargetingConfig.highlightLethalTargets) {
            int hitsToKill = DamageCalculator.calculateHitsToKill(target);
            if (hitsToKill == 1) {
                GlStateManager.color(1.0f, 0.8f, 0.0f, 0.6f); // Golden inner ring
                buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
                
                float innerWidth = width * 0.7f;
                for (int i = 0; i < segments; i++) {
                    double angle = 2.0 * Math.PI * i / segments;
                    double offsetX = Math.cos(angle) * innerWidth;
                    double offsetZ = Math.sin(angle) * innerWidth;
                    buffer.pos(x + offsetX, y, z + offsetZ).endVertex();
                }
                
                tessellator.draw();
            }
        }
        
        // Draw corner brackets
        drawCornerBrackets(x, y, z, width, height);
    }
    
    private void drawCornerBrackets(double x, double y, double z, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        
        float bracketSize = width * 0.3f;
        float halfWidth = width * 0.7f;
        float halfHeight = height * 0.5f;
        
        // Top-left bracket
        buffer.pos(x - halfWidth, y + halfHeight, z).endVertex();
        buffer.pos(x - halfWidth + bracketSize, y + halfHeight, z).endVertex();
        buffer.pos(x - halfWidth, y + halfHeight, z).endVertex();
        buffer.pos(x - halfWidth, y + halfHeight - bracketSize, z).endVertex();
        
        // Top-right bracket
        buffer.pos(x + halfWidth, y + halfHeight, z).endVertex();
        buffer.pos(x + halfWidth - bracketSize, y + halfHeight, z).endVertex();
        buffer.pos(x + halfWidth, y + halfHeight, z).endVertex();
        buffer.pos(x + halfWidth, y + halfHeight - bracketSize, z).endVertex();
        
        // Bottom-left bracket
        buffer.pos(x - halfWidth, y - halfHeight, z).endVertex();
        buffer.pos(x - halfWidth + bracketSize, y - halfHeight, z).endVertex();
        buffer.pos(x - halfWidth, y - halfHeight, z).endVertex();
        buffer.pos(x - halfWidth, y - halfHeight + bracketSize, z).endVertex();
        
        // Bottom-right bracket
        buffer.pos(x + halfWidth, y - halfHeight, z).endVertex();
        buffer.pos(x + halfWidth - bracketSize, y - halfHeight, z).endVertex();
        buffer.pos(x + halfWidth, y - halfHeight, z).endVertex();
        buffer.pos(x + halfWidth, y - halfHeight + bracketSize, z).endVertex();
        
        tessellator.draw();
    }
    
    private void render2DHUD(Entity target, ScaledResolution resolution) {
        int screenWidth = resolution.getScaledWidth();
        int screenHeight = resolution.getScaledHeight();
        
        // Calculate all text widths to determine maximum HUD width
        String targetName = target.getName();
        int nameWidth = mc.fontRenderer.getStringWidth(targetName);
        int maxWidth = nameWidth;
        
        // Check damage prediction text width
        String damageText = "";
        if (target instanceof EntityLiving && TargetingConfig.showDamagePrediction) {
            damageText = DamageCalculator.getDamagePredictionText(target);
            if (!damageText.isEmpty()) {
                int damageWidth = (int)(mc.fontRenderer.getStringWidth(damageText) * TargetingConfig.damagePredictionScale);
                maxWidth = Math.max(maxWidth, damageWidth);
            }
        }
        
        // Check vulnerability text width
        String vulnText = "";
        if (target instanceof EntityLiving && TargetingConfig.showVulnerabilities) {
            vulnText = DamageCalculator.getVulnerabilityText((EntityLiving)target);
            if (!vulnText.isEmpty()) {
                maxWidth = Math.max(maxWidth, mc.fontRenderer.getStringWidth(vulnText));
            }
        }
        
        // Health bar is always 100 pixels wide
        if (target instanceof EntityLiving) {
            maxWidth = Math.max(maxWidth, 100);
        }
        
        // Distance text width
        if (TargetingConfig.showDistance) {
            double distance = mc.player.getDistanceSq(target);
            String distanceText = String.format("%.1fm", Math.sqrt(distance));
            maxWidth = Math.max(maxWidth, mc.fontRenderer.getStringWidth(distanceText));
        }
        
        // Smart positioning: ensure HUD stays on screen
        int padding = 16; // Total padding (8 on each side)
        int hudWidth = maxWidth + padding;
        int x = Math.min(screenWidth - hudWidth, screenWidth - nameWidth - 10);
        x = Math.max(10, x); // Ensure minimum distance from left edge
        int y = 10;
        
        // Calculate total HUD height for proper background sizing
        int hudHeight = 12; // Base height for name
        if (target instanceof EntityLiving) {
            hudHeight += 25; // Health bar and text
            if (TargetingConfig.showDamagePrediction && !damageText.isEmpty()) {
                hudHeight += 12; // Damage prediction
            }
            if (TargetingConfig.showVulnerabilities && !vulnText.isEmpty()) {
                hudHeight += 12; // Vulnerability info
            }
        }
        if (TargetingConfig.showDistance) {
            hudHeight += 12; // Distance
        }
        
        // Ensure HUD doesn't go off bottom of screen
        if (y + hudHeight > screenHeight - 10) {
            y = screenHeight - hudHeight - 10;
        }
        
        // Enhanced background with better visibility
        Gui.drawRect(x - 8, y - 2, x + hudWidth, y + hudHeight, 0x90000000);
        
        // Target name with enhanced lethal highlighting
        int nameColor = 0xFFFFFF;
        if (TargetingConfig.highlightLethalTargets && target instanceof EntityLiving) {
            int hitsToKill = DamageCalculator.calculateHitsToKill(target);
            if (hitsToKill == 1) {
                nameColor = 0xFFFF4444; // Bright red for lethal targets
                // Add pulsing effect for lethal targets
                float pulse = (float) (0.8 + 0.2 * Math.sin(System.currentTimeMillis() * 0.01));
                nameColor = (int)(255 * pulse) << 16 | 0xFF4444;
            }
        }
        mc.fontRenderer.drawString(targetName, x, y, nameColor);
        
        int currentY = y + 15;
        
        // Health bar for living entities
        if (target instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) target;
            float health = living.getHealth();
            float maxHealth = living.getMaxHealth();
            float healthRatio = health / maxHealth;
            
            int barWidth = 100;
            int barHeight = 4;
            int barX = x;
            int barY = currentY;
            
            // Health bar background
            Gui.drawRect(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);
            
            // Health bar with enhanced colors
            int healthColor = healthRatio > 0.75f ? 0xFF00FF00 :
                             (healthRatio > 0.5f ? 0xFF88FF00 :
                             (healthRatio > 0.25f ? 0xFFFFFF00 : 0xFFFF0000));
            Gui.drawRect(barX, barY, barX + (int)(barWidth * healthRatio), barY + barHeight, healthColor);
            
            // Health text
            String healthText = String.format("%.1f/%.1f", health, maxHealth);
            mc.fontRenderer.drawString(healthText, barX, barY + 8, 0xFFFFFF);
            currentY += 20;
            
            // Damage prediction display
            if (TargetingConfig.showDamagePrediction && !damageText.isEmpty()) {
                int damageColor = DamageCalculator.getDamagePredictionColor(target);
                
                // Scale the text if configured
                if (TargetingConfig.damagePredictionScale != 1.0f) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(TargetingConfig.damagePredictionScale, TargetingConfig.damagePredictionScale, 1.0f);
                    int scaledX = (int)(x / TargetingConfig.damagePredictionScale);
                    int scaledY = (int)(currentY / TargetingConfig.damagePredictionScale);
                    mc.fontRenderer.drawString(damageText, scaledX, scaledY, damageColor);
                    GlStateManager.popMatrix();
                } else {
                    mc.fontRenderer.drawString(damageText, x, currentY, damageColor);
                }
                currentY += 12;
            }
            
            // Vulnerability indicators
            if (TargetingConfig.showVulnerabilities && !vulnText.isEmpty()) {
                int vulnColor = DamageCalculator.getVulnerabilityColor(living);
                mc.fontRenderer.drawString(vulnText, x, currentY, vulnColor);
                currentY += 12;
            }
        }
        
        // Distance indicator
        if (TargetingConfig.showDistance) {
            double distance = mc.player.getDistanceSq(target);
            String distanceText = String.format("%.1fm", Math.sqrt(distance));
            mc.fontRenderer.drawString(distanceText, x, currentY, 0xAAAAAA);
        }
    }
    
    private void renderRedIndicator(Entity target, float partialTicks) {
        GlStateManager.pushMatrix();
        
        // Calculate target position
        double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTicks;
        double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTicks;
        double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTicks;
        
        // Offset for camera position
        x -= mc.getRenderManager().viewerPosX;
        y -= mc.getRenderManager().viewerPosY;
        z -= mc.getRenderManager().viewerPosZ;
        
        // Position indicator above target's head
        y += target.height + 0.8;
        
        // Apply billboard transformation to face the player (like name tags)
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        
        // Disable depth test and texture, enable blending
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        
        // Animation pulse
        float pulse = (float) (0.8 + 0.2 * Math.sin(System.currentTimeMillis() * 0.008));
        float size = 0.4f * pulse;
        
        // Set bright red color with good alpha
        GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
        
        // Draw animated red downward-pointing triangle (filled)
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION);
        
        // Triangle pointing DOWN (towards the target) - fixed coordinates
        buffer.pos(0, -size * 0.5, 0).endVertex();          // Bottom point (pointing down)
        buffer.pos(-size, size * 0.5, 0).endVertex();       // Top left
        buffer.pos(size, size * 0.5, 0).endVertex();        // Top right
        
        tessellator.draw();
        
        // Draw black outline for better visibility
        GlStateManager.color(0.0f, 0.0f, 0.0f, 1.0f);
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        buffer.pos(0, -size * 0.5, 0).endVertex();          // Bottom point (pointing down)
        buffer.pos(-size, size * 0.5, 0).endVertex();       // Top left
        buffer.pos(size, size * 0.5, 0).endVertex();        // Top right
        tessellator.draw();
        
        // Restore GL state
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }
    
}