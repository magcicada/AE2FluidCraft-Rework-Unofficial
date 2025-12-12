package com.glodblock.github.interfaces;

public interface FCFluidPatternContainer extends PatternConsumer {

    boolean getCombineMode();

    void setCombineMode(boolean mode);

    boolean getFluidPlaceMode();

    void setFluidPlaceMode(boolean mode);

    default void encodeFluidCraftPattern() {

    }
}
