package com.performant.coremod;

import com.performant.coremod.config.Configuration;
import com.performant.coremod.entity.ai.CustomGoalTypeData;
import com.performant.coremod.event.EventHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MOD_ID)
public class Performant
{
    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * The config instance.
     */
    private static Configuration config;

    public static CustomGoalTypeData goalData;

    public Performant()
    {

        config = new Configuration();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        Mod.EventBusSubscriber.Bus.FORGE.bus().get().register(EventHandler.class);
    }

    public static Configuration getConfig()
    {
        return config;
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("Performant loaded.");
        goalData = new CustomGoalTypeData();
    }
}
