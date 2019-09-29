package org.gr1m.mc.mup.tweaks.ete.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.*;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.tweaks.ete.config.EteCustomConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(EntityTracker.class)
public abstract class MixinEntityTracker
{
    @Shadow
    @Final
    private Set<EntityTrackerEntry> entries;

    @Shadow
    public abstract void track(Entity entityIn, int trackingRange, final int updateFrequency, boolean sendVelocityUpdates);
    @Shadow
    public abstract void track(Entity entityIn, int trackingRange, int updateFrequency);

    // The first doesn't work for some reason...
    //@Inject(method = "track(Lnet/minecraft/entity/Entity;)V", at = @At(value = "RETURN", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
    @Inject(method = "track(Lnet/minecraft/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    public void customTrack(Entity entityIn, CallbackInfo ci)
    {
        final EteCustomConfig config = ((EteCustomConfig)(Mup.config.ete.customConfig));
        ci.cancel();

        if (net.minecraftforge.fml.common.registry.EntityRegistry.instance().tryTrackingEntity((EntityTracker)((Object)(this)), entityIn)) return;
        
        if (entityIn instanceof EntityPlayerMP)
        {
            this.track(entityIn, config.Player.range, (config.Player.frequency == 0) ? Integer.MAX_VALUE : config.Player.frequency);
            EntityPlayerMP entityplayermp = (EntityPlayerMP)entityIn;

            for (EntityTrackerEntry entitytrackerentry : this.entries)
            {
                if (entitytrackerentry.getTrackedEntity() != entityplayermp)
                {
                    entitytrackerentry.updatePlayerEntity(entityplayermp);
                }
            }
        }
        else if (entityIn instanceof EntityFishHook)
        {
            this.track(entityIn, config.FishHook.range, (config.FishHook.frequency == 0) ? Integer.MAX_VALUE : config.FishHook.frequency, true);
        }
        else if (entityIn instanceof EntityArrow)
        {
            this.track(entityIn, config.Arrow.range, (config.Arrow.frequency == 0) ? Integer.MAX_VALUE : config.Arrow.frequency, false);
        }
        else if (entityIn instanceof EntitySmallFireball)
        {
            this.track(entityIn, config.SmallFireball.range, (config.SmallFireball.frequency == 0) ? Integer.MAX_VALUE : config.SmallFireball.frequency, false);
        }
        else if (entityIn instanceof EntityFireball)
        {
            this.track(entityIn, config.Fireball.range, (config.Fireball.frequency == 0) ? Integer.MAX_VALUE : config.Fireball.frequency, true);
        }
        else if (entityIn instanceof EntitySnowball)
        {
            this.track(entityIn, config.Snowball.range, (config.Snowball.frequency == 0) ? Integer.MAX_VALUE : config.Snowball.frequency, true);
        }
        else if (entityIn instanceof EntityLlamaSpit)
        {
            this.track(entityIn, config.LlamaSpit.range, (config.LlamaSpit.frequency == 0) ? Integer.MAX_VALUE : config.LlamaSpit.frequency, false);
        }
        else if (entityIn instanceof EntityEnderPearl)
        {
            this.track(entityIn, config.EnderPearl.range, (config.EnderPearl.frequency == 0) ? Integer.MAX_VALUE : config.EnderPearl.frequency, true);
        }
        else if (entityIn instanceof EntityEnderEye)
        {
            this.track(entityIn, config.EnderEye.range, (config.EnderEye.frequency == 0) ? Integer.MAX_VALUE : config.EnderEye.frequency, true);
        }
        else if (entityIn instanceof EntityEgg)
        {
            this.track(entityIn, config.Egg.range, (config.Egg.frequency == 0) ? Integer.MAX_VALUE : config.Egg.frequency, true);
        }
        else if (entityIn instanceof EntityPotion)
        {
            this.track(entityIn, config.Potion.range, (config.Potion.frequency == 0) ? Integer.MAX_VALUE : config.Potion.frequency, true);
        }
        else if (entityIn instanceof EntityExpBottle)
        {
            this.track(entityIn, config.ExpBottle.range, (config.ExpBottle.frequency == 0) ? Integer.MAX_VALUE : config.ExpBottle.frequency, true);
        }
        else if (entityIn instanceof EntityFireworkRocket)
        {
            this.track(entityIn, config.FireworkRocket.range, (config.FireworkRocket.frequency == 0) ? Integer.MAX_VALUE : config.FireworkRocket.frequency, true);
        }
        else if (entityIn instanceof EntityItem)
        {
            this.track(entityIn, config.Item.range, (config.Item.frequency == 0) ? Integer.MAX_VALUE : config.Item.frequency, true);
        }
        else if (entityIn instanceof EntityMinecart)
        {
            this.track(entityIn, config.Minecart.range, (config.Minecart.frequency == 0) ? Integer.MAX_VALUE : config.Minecart.frequency, true);
        }
        else if (entityIn instanceof EntityBoat)
        {
            this.track(entityIn, config.Boat.range, (config.Boat.frequency == 0) ? Integer.MAX_VALUE : config.Boat.frequency, true);
        }
        else if (entityIn instanceof EntitySquid)
        {
            this.track(entityIn, config.Squid.range, (config.Squid.frequency == 0) ? Integer.MAX_VALUE : config.Squid.frequency, true);
        }
        else if (entityIn instanceof EntityWither)
        {
            this.track(entityIn, config.Wither.range, (config.Wither.frequency == 0) ? Integer.MAX_VALUE : config.Wither.frequency, false);
        }
        else if (entityIn instanceof EntityShulkerBullet)
        {
            this.track(entityIn, config.ShulkerBullet.range, (config.ShulkerBullet.frequency == 0) ? Integer.MAX_VALUE : config.ShulkerBullet.frequency, true);
        }
        else if (entityIn instanceof EntityBat)
        {
            this.track(entityIn, config.Bat.range, (config.Bat.frequency == 0) ? Integer.MAX_VALUE : config.Bat.frequency, false);
        }
        else if (entityIn instanceof EntityDragon)
        {
            this.track(entityIn, config.Dragon.range, (config.Dragon.frequency == 0) ? Integer.MAX_VALUE : config.Dragon.frequency, true);
        }
        else if (entityIn instanceof IAnimals)
        {
            this.track(entityIn, config.Mobs.range, (config.Mobs.frequency == 0) ? Integer.MAX_VALUE : config.Mobs.frequency, true);
        }
        else if (entityIn instanceof EntityTNTPrimed)
        {
            this.track(entityIn, config.PrimedTNT.range, (config.PrimedTNT.frequency == 0) ? Integer.MAX_VALUE : config.PrimedTNT.frequency, true);
        }
        else if (entityIn instanceof EntityFallingBlock)
        {
            this.track(entityIn, config.FallingBlock.range, (config.FallingBlock.frequency == 0) ? Integer.MAX_VALUE : config.FallingBlock.frequency, true);
        }
        else if (entityIn instanceof EntityHanging)
        {
            this.track(entityIn, config.Hanging.range, (config.Hanging.frequency == 0) ? Integer.MAX_VALUE : config.Hanging.frequency, false);
        }
        else if (entityIn instanceof EntityArmorStand)
        {
            this.track(entityIn, config.ArmorStand.range, (config.ArmorStand.frequency == 0) ? Integer.MAX_VALUE : config.ArmorStand.frequency, true);
        }
        else if (entityIn instanceof EntityXPOrb)
        {
            this.track(entityIn, config.XPOrb.range, (config.XPOrb.frequency == 0) ? Integer.MAX_VALUE : config.XPOrb.frequency, true);
        }
        else if (entityIn instanceof EntityAreaEffectCloud)
        {
            this.track(entityIn, config.AreaEffectCloud.range, (config.AreaEffectCloud.frequency == 0) ? Integer.MAX_VALUE : config.AreaEffectCloud.frequency, true);
        }
        else if (entityIn instanceof EntityEnderCrystal)
        {
            this.track(entityIn, config.EnderCrystal.range, (config.EnderCrystal.frequency == 0) ? Integer.MAX_VALUE : config.EnderCrystal.frequency, false);
        }
        else if (entityIn instanceof EntityEvokerFangs)
        {
            this.track(entityIn, config.EvokerFangs.range, (config.EvokerFangs.frequency == 0) ? Integer.MAX_VALUE : config.EvokerFangs.frequency, false);
        }

    }
}
