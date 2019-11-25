package org.gr1m.mc.mup.config.cli;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.config.MupConfig;
import org.gr1m.mc.mup.config.PatchDef;
import org.gr1m.mc.mup.config.network.ConfigPacketHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigCommand extends CommandBase
{
    private Set<String> configsRequiringRestart = new HashSet<>();
    
    @Override
    @Nonnull
    public String getName()
    {
        return "config";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender)
    {
        return "";
    }
    
    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length >= 2)
        {
            if (MupConfig.parentCategories.contains(MupConfig.categoryCliMap.inverse().get(args[0])))
            {
                PatchDef patch = Mup.config.get(args[1]);
                
                if (patch != null && patch.getCategory().equals(MupConfig.categoryCliMap.inverse().get(args[0])) && patch.getFieldName().equals(args[1]) && args.length >= 3)
                {
                    boolean[] patchProperties = patch.getProperty().getBooleanList();

                    switch (args[2])
                    {
                        case "loaded":
                            if (args.length >= 4)
                            {
                                boolean loaded = Boolean.parseBoolean(args[3]);

                                if (loaded != patchProperties[0])
                                {
                                    patch.getProperty().set(new boolean[] {loaded, patchProperties[1]});
                                    MupConfig.config.save();

                                    if (patch.isLoaded() != loaded)
                                    {
                                        ITextComponent txt = new TextComponentTranslation(loaded ? "eup.commands.config.feature.loaded" : "eup.commands.config.feature.unloaded", patch.getFieldName());
                                        txt.getStyle().setItalic(true).setColor(TextFormatting.GRAY);

                                        sender.sendMessage(txt);

                                        this.configsRequiringRestart.add(patch.getFieldName());

                                        ITextComponent restartWarning = new TextComponentTranslation("eup.commands.config.restart.required");
                                        restartWarning.getStyle().setItalic(true).setColor(TextFormatting.RED);

                                        sender.sendMessage(restartWarning);
                                    }
                                    else
                                    {
                                        ITextComponent txt = new TextComponentTranslation("eup.commands.config.feature.loading.reset", patch.getFieldName());
                                        txt.getStyle().setItalic(true).setColor(TextFormatting.GRAY);

                                        sender.sendMessage(txt);

                                        this.configsRequiringRestart.remove(patch.getFieldName());

                                        if (this.configsRequiringRestart.isEmpty())
                                        {
                                            ITextComponent restartWarning = new TextComponentTranslation("eup.commands.config.restart.cancelled");
                                            restartWarning.getStyle().setItalic(true).setColor(TextFormatting.YELLOW);

                                            sender.sendMessage(restartWarning);
                                        }
                                        else
                                        {
                                            ITextComponent restartWarning = new TextComponentTranslation("eup.commands.config.restart.still.required", String.join(", ", this.configsRequiringRestart));
                                            restartWarning.getStyle().setItalic(true).setColor(TextFormatting.RED);

                                            sender.sendMessage(restartWarning);
                                        }
                                    }
                                }
                                else
                                {
                                    ITextComponent txt = new TextComponentTranslation(loaded ? "eup.commands.config.feature.already.loaded" : "eup.commands.config.feature.already.unloaded", patch.getFieldName());
                                    txt.getStyle().setItalic(true).setColor(TextFormatting.YELLOW);

                                    sender.sendMessage(txt);
                                }

                                return;
                            }
                            
                            break;
                        case "enabled":
                            if (patch.isToggleable())
                            {
                                if (args.length >= 4)
                                {
                                    boolean enabled = Boolean.parseBoolean(args[3]);

                                    if (enabled != patchProperties[1])
                                    {
                                        patch.getProperty().set(new boolean[] {patchProperties[0], enabled});
                                        patch.setEnabled(enabled);

                                        ConfigPacketHandler.sendServerConfigUpdate(patch);
                                        MupConfig.config.save();

                                        ITextComponent txt = new TextComponentTranslation(enabled ? "eup.commands.config.feature.enabled" : "eup.commands.config.feature.disabled", patch.getFieldName());
                                        txt.getStyle().setItalic(true).setColor(TextFormatting.GRAY);

                                        sender.sendMessage(txt);
                                    }
                                    else
                                    {
                                        ITextComponent txt = new TextComponentTranslation(enabled ? "eup.commands.config.feature.already.enabled" : "eup.commands.config.feature.already.disabled", patch.getFieldName());
                                        txt.getStyle().setItalic(true).setColor(TextFormatting.YELLOW);

                                        sender.sendMessage(txt);
                                    }

                                    return;
                                }
                            }
                            else
                            {
                                ITextComponent txt = new TextComponentTranslation("eup.commands.config.feature.not.toggleable", patch.getFieldName());
                                txt.getStyle().setItalic(true).setColor(TextFormatting.RED);

                                sender.sendMessage(txt);
                                return;
                            }
                            
                            break;
                        case "status":
                            TextComponentTranslation loadedStatus = new TextComponentTranslation(patchProperties[0] ? "eup.true" : "eup.false");
                            loadedStatus.getStyle().setColor(patchProperties[0] ? TextFormatting.GREEN : TextFormatting.RED);

                            TextComponentTranslation enabledStatus = new TextComponentTranslation(patch.enabled ? "eup.true" : "eup.false");
                            enabledStatus.getStyle().setColor(patch.enabled ? (patch.isToggleable() ? TextFormatting.GREEN : TextFormatting.BLUE) : TextFormatting.RED);

                            ITextComponent status = new TextComponentTranslation("eup.commands.config.feature.status", patch.getFieldName(), loadedStatus, enabledStatus);
                            status.getStyle().setItalic(true).setColor(TextFormatting.WHITE);

                            if (configsRequiringRestart.contains(patch.getFieldName()))
                            {
                                TextComponentTranslation restartRequired = new TextComponentTranslation("eup.commands.config.feature.status.restart.required");
                                restartRequired.getStyle().setColor(TextFormatting.RED);

                                status.appendText(" - ").appendSibling(restartRequired);
                            }
                            
                            if (patch.isCompatDisabled())
                            {
                                status.getStyle().setStrikethrough(true);
                                sender.sendMessage(status);
                                
                                TextComponentString reason = new TextComponentString(patch.compatReason);
                                reason.getStyle().setColor(TextFormatting.RED);
                                sender.sendMessage(reason);
                            }
                            else
                            {
                                sender.sendMessage(status);
                            }
                            
                            return;
                        case "info":
                            TextComponentString displayName = new TextComponentString(patch.getDisplayName());
                            displayName.getStyle().setColor(TextFormatting.GREEN);

                            TextComponentString description = new TextComponentString(String.join(", ", patch.getComment()));
                            description.getStyle().setColor(TextFormatting.YELLOW);

                            TextComponentString sideEffects = new TextComponentString("Side Effects: " + patch.getSideEffects());
                            sideEffects.getStyle().setColor(TextFormatting.RED);

                            TextComponentString credits = new TextComponentString("Credits: " + patch.getCredits());
                            credits.getStyle().setColor(TextFormatting.WHITE);

                            TextComponentString defaults = new TextComponentString("[defaults: " + (patch.getDefaults()[0] ? "Loaded" : "Not Loaded") + ", " + (patch.getDefaults()[0] ? "Enabled" : "Disabled") + "]");
                            defaults.getStyle().setColor(TextFormatting.AQUA);

                            sender.sendMessage(displayName);
                            sender.sendMessage(description);
                            sender.sendMessage(sideEffects);
                            sender.sendMessage(credits);
                            sender.sendMessage(defaults);

                            return;
                    }
                }
                else if (args[1].equals("status-all"))
                {
                    Mup.config.getAll().forEach(patchDef -> {
                        if (patchDef.getCategory().equals(MupConfig.categoryCliMap.inverse().get(args[0])))
                        {
                            boolean[] patchProperties = patchDef.getProperty().getBooleanList();
                            
                            TextComponentTranslation loadedStatus = new TextComponentTranslation(patchProperties[0] ? "eup.true" : "eup.false");
                            loadedStatus.getStyle().setColor(patchProperties[0] ? TextFormatting.GREEN : TextFormatting.RED);

                            TextComponentTranslation enabledStatus = new TextComponentTranslation(patchDef.enabled ? "eup.true" : "eup.false");
                            enabledStatus.getStyle().setColor(patchDef.enabled ? (patchDef.isToggleable() ? TextFormatting.GREEN : TextFormatting.BLUE) : TextFormatting.RED);

                            ITextComponent status = new TextComponentTranslation("eup.commands.config.feature.status", patchDef.getFieldName(), loadedStatus, enabledStatus);
                            status.getStyle().setItalic(true).setColor(TextFormatting.WHITE);

                            if (configsRequiringRestart.contains(patchDef.getFieldName()))
                            {
                                TextComponentTranslation restartRequired = new TextComponentTranslation("eup.commands.config.feature.status.restart.required");
                                restartRequired.getStyle().setColor(TextFormatting.RED);

                                status.appendText(" - ").appendSibling(restartRequired);
                            }

                            if (patchDef.isCompatDisabled())
                            {
                                status.getStyle().setStrikethrough(true);
                                sender.sendMessage(status);

                                TextComponentString reason = new TextComponentString(patchDef.compatReason);
                                reason.getStyle().setColor(TextFormatting.RED);
                                sender.sendMessage(reason);
                            }
                            else
                            {
                                sender.sendMessage(status);
                            }
                        }
                    });
                    
                    return;
                }
            }
        }

        throw new CommandException("eup.commands.config.subcommand.invalid");
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, MupConfig.parentCategories.stream().map(MupConfig.categoryCliMap::get).collect(Collectors.toList()));
        }
        else if (args.length == 2)
        {
            if (MupConfig.parentCategories.contains(MupConfig.categoryCliMap.inverse().get(args[0])))
            {
                List<String> patchList = Mup.config.getAll().stream()
                                                   .filter(p -> p.getCategory().equals(MupConfig.categoryCliMap.inverse().get(args[0])))
                                                   .map(PatchDef::getFieldName)
                                                   .collect(Collectors.toList());
                patchList.add("status-all");
                
                return getListOfStringsMatchingLastWord(args, patchList);
            }
        }
        else if (args.length == 3)
        {
            if (MupConfig.parentCategories.contains(MupConfig.categoryCliMap.inverse().get(args[0])))
            {
                PatchDef patch = Mup.config.get(args[1]);
                
                if (patch != null && MupConfig.categoryCliMap.inverse().get(args[0]).equals(patch.getCategory()))
                {
                    return getListOfStringsMatchingLastWord(args, "status", "info", "enabled", "loaded");
                }
            }
        }
        else if (args.length == 4)
        {
            if (MupConfig.parentCategories.contains(MupConfig.categoryCliMap.inverse().get(args[0])))
            {
                PatchDef patch = Mup.config.get(args[1]);

                if (patch != null && MupConfig.categoryCliMap.inverse().get(args[0]).equals(patch.getCategory()))
                {
                    if (Arrays.asList("loaded", "enabled").contains(args[2]))
                    {
                        return getListOfStringsMatchingLastWord(args, "true", "false");
                    }
                }
            }
        }
        
        return Collections.emptyList();
    }
}
