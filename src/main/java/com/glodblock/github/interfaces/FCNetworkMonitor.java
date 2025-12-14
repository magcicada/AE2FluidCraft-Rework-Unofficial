package com.glodblock.github.interfaces;

import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEFluidStack;
import com.glodblock.github.util.FakeMonitor;

public interface FCNetworkMonitor<T> {

    void init();

    FakeMonitor<IAEFluidStack> getFluidMonitor();

    FakeMonitor<?> getGasMonitor();

    void fc$postChange(boolean add, Iterable<T> changes, IActionSource src);
}
