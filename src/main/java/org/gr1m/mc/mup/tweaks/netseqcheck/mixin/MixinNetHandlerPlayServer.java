package org.gr1m.mc.mup.tweaks.netseqcheck.mixin;

import net.minecraft.network.NetHandlerPlayServer;
import org.gr1m.mc.mup.tweaks.netseqcheck.INetSequenceHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(NetHandlerPlayServer.class)
public abstract class MixinNetHandlerPlayServer implements INetSequenceHandler
{
    private int sendingSequenceNumber = 0;
    private int checkingSequenceNumber = 0;

    public int getSendingSequenceNumber()
    {
        return this.sendingSequenceNumber;
    }
    
    public void incrSendingSequenceNumber()
    {
        this.sendingSequenceNumber += 1;
    }

    public int getCheckingSequenceNumber()
    {
        return this.checkingSequenceNumber;
    }
    
    public void setCheckingSequenceNumber(int sequenceIn)
    {
        this.checkingSequenceNumber = sequenceIn;
    }
}
