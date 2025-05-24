package com.zeldatargeting.mod.proxy;

import com.zeldatargeting.mod.config.TargetingConfig;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {
    
    public void preInit(FMLPreInitializationEvent event) {
        // Initialize configuration
        TargetingConfig.init(event);
    }
    
    public void init(FMLInitializationEvent event) {
        // Common initialization logic
    }
    
    public void postInit(FMLPostInitializationEvent event) {
        // Common post-initialization logic
    }
}