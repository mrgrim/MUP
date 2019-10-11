package org.gr1m.mc.mup.modcompat.core.mixin;

/*
   This code was taken from the mod JustEnoughID's which shares this mods MIT license.  Copyright is owned by
   Dimensional Development. License and copyright info at time of writing can be found here:
   
   https://github.com/DimensionalDevelopment/JustEnoughIDs/blob/d75a9655bc302ca253ced105ffd90b9de8432669/LICENSE
 */

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModClassLoader;
import net.minecraftforge.fml.common.ModContainer;
import org.gr1m.mc.mup.core.MupCore;
import org.gr1m.mc.mup.core.MupCoreCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.transformer.MixinTransformer;
import org.spongepowered.asm.mixin.transformer.Proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.List;

@Mixin(Loader.class)
public class MixinLoader {
    @Shadow(remap = false)
    private List<ModContainer> mods;
    
    @Shadow(remap = false)
    private ModClassLoader modClassLoader;

    /**
     * @reason Load all mods now and load mod support mixin configs. This can't be done later
     * since constructing mods loads classes from them.
     */
    @Inject(method = "loadMods", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/LoadController;transition(Lnet/minecraftforge/fml/common/LoaderState;Z)V", ordinal = 1), remap = false)
    private void beforeConstructingMods(List<String> injectedModContainers, CallbackInfo ci) {
        // Add all mods to class loader
        for (ModContainer mod : mods) {
            try {
                modClassLoader.addFile(mod.getSource());
                MupCore.log.debug("Adding mod " + mod.getModId() + " version " + mod.getDisplayVersion());
                MupCoreCompat.modList.put(mod.getModId(), mod.getDisplayVersion());
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }

        // Add and reload mixin configs
        MupCore.loadMixins(true);

        Proxy mixinProxy = (Proxy) Launch.classLoader.getTransformers().stream().filter(transformer -> transformer instanceof Proxy).findFirst().get();
        try {
            Field transformerField = Proxy.class.getDeclaredField("transformer");
            transformerField.setAccessible(true);
            MixinTransformer transformer = (MixinTransformer) transformerField.get(mixinProxy);

            Method selectConfigsMethod = MixinTransformer.class.getDeclaredMethod("selectConfigs", MixinEnvironment.class);
            selectConfigsMethod.setAccessible(true);
            selectConfigsMethod.invoke(transformer, MixinEnvironment.getCurrentEnvironment());

            Method prepareConfigsMethod = MixinTransformer.class.getDeclaredMethod("prepareConfigs", MixinEnvironment.class);
            prepareConfigsMethod.setAccessible(true);
            prepareConfigsMethod.invoke(transformer, MixinEnvironment.getCurrentEnvironment());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}