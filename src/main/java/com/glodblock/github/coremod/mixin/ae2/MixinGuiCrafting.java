package com.glodblock.github.coremod.mixin.ae2;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.client.gui.implementations.GuiCraftingCPU;
import com.glodblock.github.coremod.CoreModHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = {GuiCraftingCPU.class, GuiCraftConfirm.class}, remap = false)
public class MixinGuiCrafting {

    @WrapOperation(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/util/Platform;getItemDisplayName(Ljava/lang/Object;)Ljava/lang/String;"))
    public String getItemDisplayNameR(Object n, Operation<String> original) {
        return original.call(CoreModHooks.displayAEFluid((IAEItemStack) n));
    }

    @WrapOperation(method = "drawFG", at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IAEItemStack;asItemStackRepresentation()Lnet/minecraft/item/ItemStack;"))
    public ItemStack getItemDisplayNameR(IAEItemStack instance, Operation<ItemStack> original) {
        return CoreModHooks.displayFluid(original.call(instance));
    }
}