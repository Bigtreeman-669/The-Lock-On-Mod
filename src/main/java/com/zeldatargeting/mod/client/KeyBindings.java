package com.zeldatargeting.mod.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

public class KeyBindings {
    
    public static final String CATEGORY = "key.categories.zeldatargeting";
    
    public static KeyBinding lockOnToggle;
    public static KeyBinding cycleTargetLeft;
    public static KeyBinding cycleTargetRight;
    
    public static void init() {
        // Lock-on toggle key (default: R)
        lockOnToggle = new KeyBinding(
            "key.zeldatargeting.lockon_toggle",
            Keyboard.KEY_R,
            CATEGORY
        );
        
        // Cycle target left key (default: Q)
        cycleTargetLeft = new KeyBinding(
            "key.zeldatargeting.cycle_left",
            Keyboard.KEY_Q,
            CATEGORY
        );
        
        // Cycle target right key (default: E)
        cycleTargetRight = new KeyBinding(
            "key.zeldatargeting.cycle_right",
            Keyboard.KEY_E,
            CATEGORY
        );
        
        // Register key bindings
        ClientRegistry.registerKeyBinding(lockOnToggle);
        ClientRegistry.registerKeyBinding(cycleTargetLeft);
        ClientRegistry.registerKeyBinding(cycleTargetRight);
    }
}