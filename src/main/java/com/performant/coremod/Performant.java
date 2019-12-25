package com.performant.coremod;

import com.performant.coremod.config.Configuration;
import com.performant.coremod.entity.ai.CustomGoalTypeData;
import com.performant.coremod.event.EventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber
@Mod(modid = Constants.MOD_ID, acceptableRemoteVersions = "*", name = Constants.MOD_ID, version = Constants.VERSION, certificateFingerprint = "C0:AE:08:66:66:27:2A:00:C5:2D:79:38:4D:30:AF:84:B1:0D:8E:12")
@IFMLLoadingPlugin.SortingIndex(1002)
public class Performant
{
    @Mod.Instance(Constants.MOD_ID)
    public static Performant instance;
    static
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        goalData = new CustomGoalTypeData();
    }

    public static final Logger LOGGER = LogManager.getLogger();

    /**
     * The config instance.
     */
    private static Configuration config;

    public static CustomGoalTypeData goalData;

    @Mod.EventHandler
    public void preInit(@NotNull final FMLPreInitializationEvent event)
    {
        @NotNull final net.minecraftforge.common.config.Configuration configuration = new net.minecraftforge.common.config.Configuration(event.getSuggestedConfigurationFile());
        configuration.load();

        if (configuration.hasChanged())
        {
            configuration.save();
        }
    }
}
