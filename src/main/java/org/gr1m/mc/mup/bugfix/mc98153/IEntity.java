package org.gr1m.mc.mup.bugfix.mc98153;

public interface IEntity
{
    void setDeferredDimensionChange(int dimensionIn);
    void clearDeferredDimensionChange();
    
    boolean hasDeferredDimensionChange();
    int getDeferredDimension();
}
