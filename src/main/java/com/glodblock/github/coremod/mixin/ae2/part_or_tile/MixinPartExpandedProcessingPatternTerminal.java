package com.glodblock.github.coremod.mixin.ae2.part_or_tile;

import appeng.parts.reporting.AbstractPartEncoder;
import appeng.parts.reporting.PartExpandedProcessingPatternTerminal;
import com.glodblock.github.interfaces.FCFluidPatternPart;
import com.glodblock.github.util.Util;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(value = PartExpandedProcessingPatternTerminal.class, remap = false)
public abstract class MixinPartExpandedProcessingPatternTerminal extends AbstractPartEncoder implements FCFluidPatternPart {

    @Unique
    private boolean fc$combine = false;
    @Unique
    private boolean fc$fluidFirst = false;

    public MixinPartExpandedProcessingPatternTerminal(ItemStack is) {
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

    public void onChangeCrafting(Int2ObjectMap<ItemStack[]> inputs, List<ItemStack> outputs, boolean combine) {
        Util.onPatternTerminalChangeCrafting(this, true, inputs, outputs, combine);
    }

    @Intrinsic
    public void readFromNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        fc$combine = data.getBoolean("combineMode");
        fc$fluidFirst = data.getBoolean("fluidFirst");
    }

    @Intrinsic
    public void writeToNBT(NBTTagCompound data) {
        super.writeToNBT(data);
        data.setBoolean("combineMode", fc$combine);
        data.setBoolean("fluidFirst", fc$fluidFirst);
    }
}