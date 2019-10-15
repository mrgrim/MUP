package org.gr1m.mc.mup.tweaks.netseqcheck;

public interface INetSequenceHandler
{
    int getSendingSequenceNumber();
    void incrSendingSequenceNumber();

    int getCheckingSequenceNumber();
    void setCheckingSequenceNumber(int sequenceIn);
}
