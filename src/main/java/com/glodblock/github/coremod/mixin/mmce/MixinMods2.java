package com.glodblock.github.coremod.mixin.mmce;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(targets = "hellfirepvp.modularmachinery.common.base.Mods$2", remap = false)
public class MixinMods2 {

    /**
     * @author circulation
     * @reason 确认为加载
     */
    @Overwrite
    public boolean isPresent() {
        return true;
    }
}