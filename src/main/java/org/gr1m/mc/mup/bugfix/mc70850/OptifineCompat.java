package org.gr1m.mc.mup.bugfix.mc70850;

import net.minecraft.util.EnumFacing;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class OptifineCompat
{
    public static final MethodHandle optifineHookMethod;

    static {
        MethodHandle check = null;

        try {
            Method pm = Class.forName("net.minecraft.client.renderer.RenderGlobal$ContainerLocalRenderInformation").getDeclaredMethod("access$000", Class.forName("net.minecraft.client.renderer.RenderGlobal$ContainerLocalRenderInformation"), EnumFacing.class, int.class);

            pm.setAccessible(true);
            check = MethodHandles.lookup().unreflect(pm);
            pm.setAccessible(false);
        }
        catch (Exception e)
        {
            // Eh..
            throw new RuntimeException(e);
        }

        optifineHookMethod = check;
    }
}
