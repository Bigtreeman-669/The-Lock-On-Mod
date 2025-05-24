package com.zeldatargeting.mod;

import com.zeldatargeting.mod.proxy.CommonProxy;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ZeldaTargetingMod.MODID, name = ZeldaTargetingMod.NAME, version = ZeldaTargetingMod.VERSION, clientSideOnly = false, guiFactory = "com.zeldatargeting.mod.client.gui.GuiFactory")
public class ZeldaTargetingMod {
    public static final String MODID = "zeldatargeting";
    public static final String NAME = "Zelda Targeting";
    public static final String VERSION = "1.1.0";
    
    @Mod.Instance(MODID)
    public static ZeldaTargetingMod instance;
    
    @SidedProxy(clientSide = "com.zeldatargeting.mod.proxy.ClientProxy", serverSide = "com.zeldatargeting.mod.proxy.ServerProxy")
    public static CommonProxy proxy;
    
    private static Logger logger;
    private static boolean betterThirdPersonLoaded = false;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("Zelda Targeting Mod - Pre-Initialization");
        
        // Check for Better Third Person compatibility
        betterThirdPersonLoaded = Loader.isModLoaded("betterthanperson");
        if (betterThirdPersonLoaded) {
            logger.info("Better Third Person detected - Camera lock-on will be disabled to prevent conflicts");
        }
        
        proxy.preInit(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Zelda Targeting Mod - Initialization");
        
        proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        logger.info("Zelda Targeting Mod - Post-Initialization");
        
        proxy.postInit(event);
    }
    
    public static Logger getLogger() {
        return logger;
    }
    
    public static boolean isBetterThirdPersonLoaded() {
        return betterThirdPersonLoaded;
    }
}