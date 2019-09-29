package org.gr1m.mc.mup.bugfix.mc80032.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Teleporter;
import net.minecraft.block.state.pattern.BlockPattern;
import org.gr1m.mc.mup.Mup;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Teleporter.class)
public abstract class MixinTeleporter
{
    //TODO: Commented out local variable indexes as OptiFine mucks up the LVT for this method. I'd prefer to be explicit. When mod compatibility checks are added I'll make an alternative Mixin class/json to load.
    private double storedD2;
    private Entity storedEntity;
    private BlockPattern.PatternHelper storedBlockPatternHelper;
    
    @ModifyVariable(method = "placeInExistingPortal", name = "d2", /*index = 18,*/ at = @At(value = "LOAD", opcode = Opcodes.DLOAD, ordinal = 1))
    private double xAxisD2Capture(double d2)
    {
        this.storedD2 = d2;
        return 0.0D;
    }

    @ModifyVariable(method = "placeInExistingPortal", name = "d2", /*index = 18,*/ at = @At(value = "LOAD", opcode = Opcodes.DLOAD, ordinal = 2))
    private double zAxisD2Capture(double d2)
    {
        this.storedD2 = d2;
        return 0.0D;
    }
    
    @Redirect(method = "placeInExistingPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getLastPortalVec()Lnet/minecraft/util/math/Vec3d;", ordinal = 1))
    private Vec3d xAxisStoreEntity(Entity entityIn)
    {
        this.storedEntity = entityIn;
        return entityIn.getLastPortalVec();
    }

    @Redirect(method = "placeInExistingPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getLastPortalVec()Lnet/minecraft/util/math/Vec3d;", ordinal = 2))
    private Vec3d yAxisStoreEntity(Entity entityIn)
    {
        this.storedEntity = entityIn;
        return entityIn.getLastPortalVec();
    }

    @Redirect(method = "placeInExistingPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/state/pattern/BlockPattern$PatternHelper;getWidth()I"))
    private int storeBlockPatternHelper(BlockPattern.PatternHelper blockPatternHelper)
    {
        this.storedBlockPatternHelper = blockPatternHelper;
        return blockPatternHelper.getWidth();
    }

    @ModifyVariable(method = "placeInExistingPortal", name = "d7", /*index = 14,*/ at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 1))
    private double xAxisOffsetCalc(double d7)
    {
        double offset = d7;

        if (Mup.config.mc80032.enabled)
        {
            double entity_corrected_radius = 1.02D * this.storedEntity.width / 2.0D;

            if (entity_corrected_radius >= (double) this.storedBlockPatternHelper.getWidth() - entity_corrected_radius)
            {
                //entity is wider than portal, so will suffocate anyways, so place it directly in the middle
                entity_corrected_radius = (double) this.storedBlockPatternHelper.getWidth() / 2.0D - 0.001D;
            }

            if (offset >= 0)
            {
                offset = MathHelper.clamp(offset, entity_corrected_radius, (double) this.storedBlockPatternHelper.getWidth() - entity_corrected_radius);
            }
            else
            {
                offset = MathHelper.clamp(offset, -(double) this.storedBlockPatternHelper.getWidth() + entity_corrected_radius, -entity_corrected_radius);
            }
        }

        return this.storedD2 + offset;
    }

    @ModifyVariable(method = "placeInExistingPortal", name = "d5", /*index = 12,*/ at = @At(value = "STORE", opcode = Opcodes.DSTORE, ordinal = 1))
    private double yAxisOffsetCalc(double d5)
    {
        double offset = d5;

        if (Mup.config.mc80032.enabled)
        {
            double entity_corrected_radius = 1.02D * this.storedEntity.width / 2.0D;

            if (entity_corrected_radius >= (double) this.storedBlockPatternHelper.getWidth() - entity_corrected_radius)
            {
                //entity is wider than portal, so will suffocate anyways, so place it directly in the middle
                entity_corrected_radius = (double) this.storedBlockPatternHelper.getWidth() / 2.0D - 0.001D;
            }

            if (offset >= 0)
            {
                offset = MathHelper.clamp(offset, entity_corrected_radius, (double) this.storedBlockPatternHelper.getWidth() - entity_corrected_radius);
            }
            else
            {
                offset = MathHelper.clamp(offset, -(double) this.storedBlockPatternHelper.getWidth() + entity_corrected_radius, -entity_corrected_radius);
            }
        }
        
        return this.storedD2 + offset;
    }
}
