package org.gr1m.mc.mup;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class MupCommand extends CommandBase
{
    private static Map<String, CommandBase> COMMANDS = new HashMap<>();
    
    public static void registerCommand(@Nonnull String prefix, @Nonnull CommandBase handler)
    {
        COMMANDS.put(prefix, handler);
    }

    @Override
    public @Nonnull String getName()
    {
        return "eup";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "Base command for " + Mup.NAME + ". Use tab completion to discover subcommands.\nTesting";
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length > 0)
        {
            if (COMMANDS.containsKey(args[0]))
            {
                COMMANDS.get(args[0]).execute(server, sender, Arrays.copyOfRange(args, 1, args.length));
                return;
            }
        }

        throw new CommandException("eup.commands.subcommand.invalid");
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, COMMANDS.keySet());
        }
        else if (args.length > 1)
        {
            return COMMANDS.get(args[0]).getTabCompletions(server, sender, Arrays.copyOfRange(args, 1, args.length), targetPos);
        }

        return Collections.emptyList();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return false;
    }
}