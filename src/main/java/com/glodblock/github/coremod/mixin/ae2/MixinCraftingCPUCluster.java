package com.glodblock.github.coremod.mixin.ae2;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.data.IAEItemStack;
import appeng.crafting.MECraftingInventory;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import com.glodblock.github.coremod.CoreModHooks;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CraftingCPUCluster.class, remap = false)
public class MixinCraftingCPUCluster {

    @Redirect(method = "executeCrafting", at = @At(value = "INVOKE", target = "Lappeng/api/storage/data/IAEItemStack;getStackSize()J", ordinal = 0))
    private long getFluidSize(IAEItemStack instance) {
        return CoreModHooks.getFluidSize(instance);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "NEW",
            target = "net/minecraft/inventory/InventoryCrafting",
            remap = true
        )
    )
    private InventoryCrafting wrapInventoryCrafting(Container container, int i, int ii, Operation<InventoryCrafting> original) {
        return CoreModHooks.wrapCraftingBuffer(container, i, ii);
    }

    @Redirect(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/InventoryCrafting;getStackInSlot(I)Lnet/minecraft/item/ItemStack;",
            remap = true
        )
    )
    private ItemStack redirectGetStackInSlot(InventoryCrafting inventory, int slot) {
        return CoreModHooks.removeFluidPackets(inventory, slot);
    }

    @WrapOperation(
        method = "executeCrafting",
        at = @At(
            value = "INVOKE",
            target = "Lappeng/crafting/MECraftingInventory;injectItems(Lappeng/api/storage/data/IAEItemStack;Lappeng/api/config/Actionable;Lappeng/api/networking/security/IActionSource;)Lappeng/api/storage/data/IAEItemStack;"
        )
    )
    private IAEItemStack wrapFromItemStack(MECraftingInventory instance, IAEItemStack input, Actionable mode, IActionSource src, Operation<IAEItemStack> original) {
        return original.call(instance, CoreModHooks.wrapFluidPacketStack(input), mode, src);
    }

    @Redirect(
        method = {"cancel", "updateCraftingLogic"},
        at = @At(
            value = "INVOKE",
            target = "Lappeng/me/cluster/implementations/CraftingCPUCluster;storeItems()V"
        )
    )
    private void redirectStoreItems(CraftingCPUCluster instance) {
        CoreModHooks.storeFluidItem(instance);
    }
}