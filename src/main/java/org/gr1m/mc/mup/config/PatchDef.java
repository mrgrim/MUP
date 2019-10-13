package org.gr1m.mc.mup.config;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.text.*;
import org.gr1m.mc.mup.Mup;
import org.gr1m.mc.mup.core.MupCore;
import org.gr1m.mc.mup.core.MupCoreConfig;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PatchDef
{
    public enum Side { SERVER, CLIENT, BOTH };

    public boolean loaded;
    public boolean enabled;
    private boolean serverEnabled;
    
    private final String fieldName;
    private final Enum<Side> side;
    public final ICustomizablePatch customConfig;
    
    private String displayName;
    private String category;
    private String credits;
    private String sideEffects;
    private String[] comment;
    private boolean toggleable = true;
    private boolean clientToggleable = false;
    private boolean[] defaults;
    
    private boolean compatDisabled;
    public String compatReason;
    
    public PatchDef(String fieldNameIn, Enum<Side> sideIn)
    {
        this(fieldNameIn, sideIn, ServerSyncHandlers.ENFORCE, ClientSyncHandlers.IGNORE);
    }
    
    public PatchDef(String fieldNameIn, Enum<Side> sideIn, BiConsumer<PatchDef, Boolean> serverSyncHandler)
    {
        this(fieldNameIn, sideIn, serverSyncHandler, ClientSyncHandlers.IGNORE);
    }
    
    public PatchDef(String fieldNameIn, Enum<Side> sideIn, BiConsumer<PatchDef, Boolean> serverSyncHandler, TriFunction<PatchDef, Boolean, NetHandlerPlayServer, Boolean> clientSyncHandler)
    {
        this(fieldNameIn, sideIn, serverSyncHandler, clientSyncHandler, null);
    }
    
    public PatchDef(String fieldNameIn, Enum<Side> sideIn, BiConsumer<PatchDef, Boolean> serverSyncHandler, TriFunction<PatchDef, Boolean, NetHandlerPlayServer, Boolean> clientSyncHandler, ICustomizablePatch customConfigIn)
    {
        this.fieldName = fieldNameIn;
        this.processServerSync = serverSyncHandler;
        this.processClientSync = clientSyncHandler;
        this.side = sideIn;
        this.defaults = new boolean[] { true, true };
        this.customConfig = customConfigIn;
    }
    
    public final BiConsumer<PatchDef, Boolean> processServerSync;
    public final TriFunction<PatchDef, Boolean, NetHandlerPlayServer, Boolean> processClientSync;
    
    public boolean isLoaded()
    {
        return this.loaded;
    }
    public String getFieldName() { return this.fieldName; }
    public Enum<Side> getSide() { return side; }
    
    public void setLoaded()
    {
        // Can only set to true. Bug fixes can only be loaded at startup and cannot be unloaded
        this.loaded = true;
    }
    
    public boolean isClientToggleable() { return this.clientToggleable; }
    public PatchDef setClientToggleable(boolean clientToggleableIn) { this.clientToggleable = clientToggleableIn; return this; }
    
    public boolean isEnabled()
    {
        return this.enabled;
    }
    public PatchDef setEnabled(boolean isEnabled) { this.enabled = !this.toggleable || (isEnabled && this.loaded); return this; }

    public boolean isServerEnabled()
    {
        return this.serverEnabled;
    }
    public PatchDef setServerEnabled(boolean isServerEnabled) { this.serverEnabled = isServerEnabled; return this; }
    
    public boolean isCompatDisabled()
    {
        // Carry over core config compatibility data
        try
        {
            MupCoreConfig.Patch corePatch = (MupCoreConfig.Patch) MupCore.config.getClass().getDeclaredField(this.fieldName).get(MupCore.config);

            this.compatDisabled = corePatch.enabled && !corePatch.loaded;
            this.compatReason = corePatch.reason;
        }
        catch (Exception e)
        {
            Mup.logger.error("Failed to transfer compatibility data for patch " + this.fieldName + ".");
        }
        
        return this.compatDisabled;
    }

    public boolean isToggleable()
    {
        return this.toggleable;
    }
    public PatchDef setToggleable(boolean isToggleable) { this.toggleable = isToggleable; this.enabled = !isToggleable || this.enabled; return this; }

    public String getDisplayName() { return this.displayName; }
    public PatchDef setDisplayName(String displayNameIn) { this.displayName = displayNameIn; return this; }

    public String getCredits() { return this.credits; }
    public PatchDef setCredits(String creditsIn) { this.credits = creditsIn; return this; }

    public String getSideEffects() { return this.sideEffects; }
    public PatchDef setSideEffects(String sideEffectsIn) { this.sideEffects = sideEffectsIn; return this; }

    public String getCategory() { return this.category; }
    public PatchDef setCategory(String categoryIn) { this.category = categoryIn; return this; }

    public String[] getComment() { return this.comment; }
    public PatchDef setComment(String[] commentIn) { this.comment = commentIn; return this; }

    public boolean[] getDefaults() { return this.defaults; }
    public PatchDef setDefaults(boolean[] defaultsIn) { this.defaults = defaultsIn; return this; }
    
    public boolean wasLoaded()
    {
        Field coreField;
        
        try
        {
            coreField = MupCore.config.getClass().getDeclaredField(this.getFieldName());
            return ((MupCoreConfig.Patch)coreField.get(MupCore.config)).loaded;
        }
        catch (Exception e)
        {
            Mup.logger.error("Failure to fetch core config field for field name: " + this.getFieldName());
            return false;
        }
    }

    public static class ServerSyncHandlers {
        public static final BiConsumer<PatchDef, Boolean> IGNORE = (bug, enabled) -> { };

        public static final BiConsumer<PatchDef, Boolean> TOGGLE = (bug, enabled) -> {
            if (Mup.config.isServerLocked())
            {
                bug.setClientToggleable(enabled);
                if (!enabled) bug.setEnabled(false);
            }
            else
            {
                bug.setClientToggleable(true);
            }
        };

        public static final BiConsumer<PatchDef, Boolean> ENFORCE = (bug, enabled) -> {
            if (enabled && !bug.isLoaded())
            {
                Mup.logger.warn("Server requested to enable unloaded patch: " + bug.getDisplayName());
                return;
            }

            if (enabled && bug.isLoaded() && !bug.isEnabled())
            {
                Mup.logger.info("Server force enabled a disabled patch: " + bug.getDisplayName());
            }

            if (!enabled && bug.isLoaded() && bug.isEnabled())
            {
                Mup.logger.info("Server force disabled an enabled patch: " + bug.getDisplayName());
            }

            bug.setEnabled(enabled);
        };

        public static final BiConsumer<PatchDef, Boolean> ACCEPT = (bug, enabled) -> {
            TOGGLE.accept(bug, enabled);
            ENFORCE.accept(bug, enabled);
        };
    }

    @FunctionalInterface
    public interface TriFunction<A,B,C,R> {

        R apply(A a, B b, C c);

        default <V> TriFunction<A, B, C, V> andThen(
            Function<? super R, ? extends V> after) {
            Objects.requireNonNull(after);
            return (A a, B b, C c) -> after.apply(apply(a, b, c));
        }
    }

    public static class ClientSyncHandlers
    {
        public static final TriFunction<PatchDef, Boolean, NetHandlerPlayServer, Boolean> IGNORE = (bug, enabled, handler) -> false;
        
        public static final TriFunction<PatchDef, Boolean, NetHandlerPlayServer, Boolean> DISCONNECT = (bug, enabled, handler) -> {
            if (!enabled && bug.isLoaded() && bug.isEnabled())
            {
                ITextComponent disconnectMessage = new TextComponentString("");
                disconnectMessage.appendSibling(new TextComponentString("EigenCraft Unofficial Patch Configuration Conflict!\n").setStyle(new Style().setUnderlined(true)));
                disconnectMessage.appendSibling(new TextComponentString("\n"));
                disconnectMessage.appendSibling(new TextComponentString("The patch for \""));
                disconnectMessage.appendSibling(new TextComponentString(bug.displayName).setStyle(new Style().setColor(TextFormatting.YELLOW)));
                disconnectMessage.appendSibling(new TextComponentString("\" must be loaded to connect to this server."));

                handler.disconnect(disconnectMessage);

                return true;
            }
            else
            {
                return false;
            }
        };
    }
}
