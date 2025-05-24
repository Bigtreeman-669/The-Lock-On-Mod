package com.zeldatargeting.mod.client.render;

import com.zeldatargeting.mod.client.TargetingManager;
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
        
        // Set color (red for enemies, blue for neutrals)
        GlStateManager.color(1.0f, 0.2f, 0.2f, 0.8f);
        
        buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
        
        // Draw circular reticle
        int segments = 32;
        for (int i = 0; i < segments; i++) {
            double angle = 2.0 * Math.PI * i / segments;
            double offsetX = Math.cos(angle) * width;
            double offsetZ = Math.sin(angle) * width;
            buffer.pos(x + offsetX, y, z + offsetZ).endVertex();
        }
        
        tessellator.draw();
        
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
        
        // Draw target info in top-right corner
        String targetName = target.getName();
        int nameWidth = mc.fontRenderer.getStringWidth(targetName);
        int x = screenWidth - nameWidth - 10;
        int y = 10;
        
        // Background
        Gui.drawRect(x - 5, y - 2, x + nameWidth + 5, y + 12, 0x80000000);
        
        // Target name
        mc.fontRenderer.drawString(targetName, x, y, 0xFFFFFF);
        
        // Health bar for living entities
        if (target instanceof EntityLiving) {
            EntityLiving living = (EntityLiving) target;
            float health = living.getHealth();
            float maxHealth = living.getMaxHealth();
            float healthRatio = health / maxHealth;
            
            int barWidth = 100;
            int barHeight = 4;
            int barX = x;
            int barY = y + 15;
            
            // Health bar background
            Gui.drawRect(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);
            
            // Health bar
            int healthColor = healthRatio > 0.5f ? 0xFF00FF00 : (healthRatio > 0.25f ? 0xFFFFFF00 : 0xFFFF0000);
            Gui.drawRect(barX, barY, barX + (int)(barWidth * healthRatio), barY + barHeight, healthColor);
            
            // Health text
            String healthText = String.format("%.1f/%.1f", health, maxHealth);
            mc.fontRenderer.drawString(healthText, barX, barY + 8, 0xFFFFFF);
        }
        
        // Distance indicator
        double distance = mc.player.getDistanceSq(target);
        String distanceText = String.format("%.1fm", Math.sqrt(distance));
        mc.fontRenderer.drawString(distanceText, x, y + 35, 0xAAAAAA);
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