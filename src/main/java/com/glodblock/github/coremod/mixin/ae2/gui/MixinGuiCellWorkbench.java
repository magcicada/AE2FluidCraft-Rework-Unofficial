package com.glodblock.github.coremod.mixin.ae2.gui;

import appeng.api.implementations.IUpgradeableHost;
import appeng.client.gui.implementations.GuiCellWorkbench;
import appeng.client.gui.implementations.GuiUpgradeable;
import appeng.container.implementations.ContainerCellWorkbench;
import appeng.container.slot.SlotFake;
import appeng.items.storage.ItemViewCell;
import appeng.tile.misc.TileCellWorkbench;
import appeng.util.item.AEItemStack;
import com.glodblock.github.integration.jei.FluidPacketTarget;
import com.glodblock.github.util.ModAndClassUtil;
import com.glodblock.github.util.UtilClient;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import mezz.jei.api.gui.IGhostIngredientHandler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = GuiCellWorkbench.class, remap = false)
public abstract class MixinGuiCellWorkbench extends GuiUpgradeable {

    @Shadow
    @Final
    private ContainerCellWorkbench workbench;

    public MixinGuiCellWorkbench(final InventoryPlayer inventoryPlayer, final IUpgradeableHost te) {
        super(inventoryPlayer, te);
    }

    @Intrinsic
    protected void renderHoveredToolTip(final int mouseX, final int mouseY) {
        if (((TileCellWorkbench) this.workbench.getTileEntity()).getCell() instanceof ItemViewCell) {
            final var slot = this.getSlotUnderMouse();
            if (slot instanceof final SlotFake s) {
                if (UtilClient.renderPatternSlotTip(this, mouseX, mouseY)) return;
                final var i = AEItemStack.fromItemStack(s.getStack());
                if (UtilClient.rendererFluid(this, i, mouseX, mouseY)) return;
                if (ModAndClassUtil.GAS && UtilClient.rendererGas(this, i, mouseX, mouseY)) return;
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Intrinsic
    public List<IGhostIngredientHandler.Target<?>> getPhantomTargets(final Object ingredient) {
        if (((TileCellWorkbench) this.workbench.getTileEntity()).getCell() instanceof ItemViewCell) {
            final List<IGhostIngredientHandler.Target<?>> targets = new ObjectArrayList<>();
            for (final Slot slot : this.inventorySlots.inventorySlots) {
                if (slot instanceof SlotFake) {
                    final IGhostIngredientHandler.Target<?> target = new FluidPacketTarget(getGuiLeft(), getGuiTop(), slot);
                    targets.add(target);
                    mapTargetSlot.putIfAbsent(target, slot);
                }
            }
            return targets;
        } else return super.getPhantomTargets(ingredient);
    }
}
