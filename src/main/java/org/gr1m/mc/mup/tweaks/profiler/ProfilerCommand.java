package org.gr1m.mc.mup.tweaks.profiler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ProfilerCommand extends CommandBase
{
    static EntityPlayer CALLER = null;

    @Override
    public String getName()
    {
        return "tickhealth";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "tickhealth <global|entities> [tick count]";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0)
        {
            int ticks = 100;
            
            if (args.length > 1)
            {
                ticks = parseInt(args[1], 20, 72000);
            }
            
            if ("entities".equalsIgnoreCase(args[0]))
            {
                MupProfiler.prepareEntityReport(ticks);
            }
            else if ("global".equalsIgnoreCase(args[0]))
            {
                MupProfiler.prepareGlobalReport(ticks);
            }
            else
            {
                throw new CommandException("eup.commands.tickhealth.subcommand.invalid");
            }
            
            CALLER = (sender instanceof EntityPlayer) ? (EntityPlayer)(sender) : null;
        }
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "entities", "global");
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }
}
