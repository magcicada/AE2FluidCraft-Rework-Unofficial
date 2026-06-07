package com.glodblock.github.coremod.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.data.IAEStack;
import appeng.me.storage.MEPassThrough;
import com.glodblock.github.util.FakeMonitor;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = MEPassThrough.class, remap = false)
public class MixinMEPassThrough {

    @WrapOperation(method = "injectItems", at = @At(value = "INVOKE", target = "Lappeng/api/storage/IMEInventory;injectItems(Lappeng/api/storage/data/IAEStack;Lappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)Lappeng/api/storage/data/IAEStack;"))
    public IAEStack<?> injectItems(IMEInventory<?> instance, IAEStack<?> t, Actionable actionable, IActionSource iActionSource, Operation<IAEStack<?>> original) {
        if (iActionSource instanceof FakeMonitor.FakeMonitorSource m) {
            return original.call(instance, t, actionable, m.getSource());
        }
        return original.call(instance, t, actionable, iActionSource);
    }

}
