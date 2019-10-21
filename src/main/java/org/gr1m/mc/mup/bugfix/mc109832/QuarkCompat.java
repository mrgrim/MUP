package org.gr1m.mc.mup.bugfix.mc109832;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public final class QuarkCompat
{
    public static final MethodHandle quarkHookMethod;

    static {
        MethodHandle check = null;
        
        try {
            check = MethodHandles.lookup().findStatic(Class.forName("vazkii.quark.base.asm.ASMHooks"), "setPistonBlock",
                                                      MethodType.methodType(boolean.class, World.class, BlockPos.class, IBlockState.class, int.class));
        }
        catch (Exception e)
        {
            // Eh..
        }
        
        quarkHookMethod = check;
    }
}
