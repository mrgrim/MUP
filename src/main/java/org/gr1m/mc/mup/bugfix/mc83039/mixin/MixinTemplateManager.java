package org.gr1m.mc.mup.bugfix.mc83039.mixin;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.template.TemplateManager;
import org.apache.commons.io.IOUtils;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;

@Mixin(TemplateManager.class)
public abstract class MixinTemplateManager
{
    @Shadow
    protected abstract void readTemplateFromStream(String id, InputStream stream) throws IOException;

    // This is one ugly damn hack, but generalizing this would be overkill
    @Inject(method = "readTemplateFromJar", at = @At("HEAD"), cancellable = true)
    private void mc83039Override(ResourceLocation id, CallbackInfoReturnable<Boolean> cir)
    {
        if (id.getNamespace().equals("minecraft") && id.getPath().equals("endcity/tower_base"))
        {
            String s1 = id.getPath();
            InputStream inputstream = null;
            boolean flag;

            try
            {
                inputstream = Mup.class.getResourceAsStream("/assets/mup/structures/overrides/" + s1 + ".nbt");
                this.readTemplateFromStream(s1, inputstream);
                
                cir.setReturnValue(true);
                return;
            }
            catch (Throwable var10)
            {
                flag = false;
            }
            finally
            {
                IOUtils.closeQuietly(inputstream);
            }

            cir.setReturnValue(flag);
        }
    }
}
