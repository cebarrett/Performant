package com.performant.coremod;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import sun.misc.URLClassPath;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
@IFMLLoadingPlugin.SortingIndex(1001)
public class PerformantLoadingPlugin implements IFMLLoadingPlugin
{
    static
    {
        fixMixinClasspathOrder();
        MixinBootstrap.init();
        Mixins.addConfiguration("performant_mixin.json");
    }
    /**
     * Fixes the loading order, thx vanillafix :)
     */
    private static void fixMixinClasspathOrder()
    {
        URL url = PerformantLoadingPlugin.class.getProtectionDomain().getCodeSource().getLocation();
        givePriorityInClasspath(url, Launch.classLoader);
        givePriorityInClasspath(url, (URLClassLoader) ClassLoader.getSystemClassLoader());
    }

    private static void givePriorityInClasspath(URL url, URLClassLoader classLoader)
    {
        try
        {
            Field ucpField = URLClassLoader.class.getDeclaredField("ucp");
            ucpField.setAccessible(true);

            List<URL> urls = new ArrayList<>(Arrays.asList(classLoader.getURLs()));
            urls.remove(url);
            urls.add(0, url);
            URLClassPath ucp = new URLClassPath(urls.toArray(new URL[0]));

            ucpField.set(classLoader, ucp);
        }
        catch (ReflectiveOperationException e)
        {
            throw new AssertionError(e);
        }
    }

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[0];
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data)
    {

    }

    @Override
    public String getAccessTransformerClass()
    {
        return null;
    }
}

