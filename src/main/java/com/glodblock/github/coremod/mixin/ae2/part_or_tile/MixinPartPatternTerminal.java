package com.glodblock.github.coremod.mixin.ae2.part_or_tile;

import appeng.parts.reporting.AbstractPartEncoder;
import appeng.parts.reporting.PartPatternTerminal;
import com.glodblock.github.interfaces.FCFluidPatternPart;
import com.glodblock.github.util.Util;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PartPatternTerminal.class, remap = false)
public abstract class MixinPartPatternTerminal extends AbstractPartEncoder implements FCFluidPatternPart {

    @Unique
    private boolean fc$combine = false;
    @Unique
    private boolean fc$fluidFirst = false;

    public MixinPartPatternTerminal(ItemStack is) {
        super(is);
    }

    public boolean getCombineMode() {
        return this.fc$combine;
    }

    public void setCombineMode(boolean value) {
        this.fc$combine = value;
    }

    public boolean getFluidPlaceMode() {
        return this.fc$fluidFirst;
    }

    public void setFluidPlaceMode(boolean value) {
        this.fc$fluidFirst = value;
    }

    @Override
    public void onChangeCrafting(Int2ObjectMap<ItemStack[]> inputs, List<ItemStack> outputs, boolean combine) {
        Util.onPatternTerminalChangeCrafting(this, !this.craftingMode, inputs, outputs, combine);
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    public void readFromNBT(NBTTagCompound data, CallbackInfo ci) {
        fc$combine = data.getBoolean("combineMode");
        fc$fluidFirst = data.getBoolean("fluidFirst");
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    public void writeToNBT(NBTTagCompound data, CallbackInfo ci) {
        data.setBoolean("combineMode", fc$combine);
        data.setBoolean("fluidFirst", fc$fluidFirst);
    }
}
