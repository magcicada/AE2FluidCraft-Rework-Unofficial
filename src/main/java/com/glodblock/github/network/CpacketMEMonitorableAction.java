package com.glodblock.github.network;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.storage.IStorageGrid;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.core.AELog;
import appeng.core.sync.network.NetworkHandler;
import appeng.core.sync.packets.PacketInventoryAction;
import appeng.fluids.util.AEFluidStack;
import appeng.helpers.InventoryAction;
import appeng.me.helpers.PlayerSource;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import com.glodblock.github.util.ModAndClassUtil;
import com.glodblock.github.util.Util;
import com.mekeng.github.common.me.data.impl.AEGasStack;
import io.netty.buffer.ByteBuf;
import mekanism.api.gas.GasStack;
import mekanism.api.gas.IGasItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.IOException;

public class CpacketMEMonitorableAction implements IMessage {

    public static final byte FLUID = 0;
    public static final byte GAS = 1;

    private byte type;
    private NBTTagCompound obj;

    public CpacketMEMonitorableAction() {
    }

    public CpacketMEMonitorableAction(byte b, NBTTagCompound s) {
        type = b;
        obj = s;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = buf.readByte();
        obj = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(type);
        ByteBufUtils.writeTag(buf, obj);
    }

    public static class Handler implements IMessageHandler<CpacketMEMonitorableAction, IMessage> {

        @Override
        public IMessage onMessage(CpacketMEMonitorableAction message, MessageContext ctx) {
            final var player = ctx.getServerHandler().player;
            final var c = player.openContainer;
            if (c instanceof ContainerMEMonitorable cme) {
                boolean drain = false;
                final IStorageGrid grid = cme.getNetworkNode().getGrid().getCache(IStorageGrid.class);
                final var source = new PlayerSource(player, (IActionHost) cme.getTarget());
                var h = player.inventory.getItemStack();
                if (h.isEmpty()) return null;
                if (message.type == FLUID) {
                    var ch = h.copy();
                    ch.setCount(1);
                    final IFluidHandlerItem fh = FluidUtil.getFluidHandler(ch);
                    if (fh == null) return null;
                    final var allFluid = fh.drain(Integer.MAX_VALUE, false);
                    FluidStack fluid = null;
                    if (!message.obj.isEmpty()) {
                        fluid = FluidStack.loadFluidStackFromNBT(message.obj);
                        if (fluid != null) {
                            if (allFluid != null && allFluid.amount > 0) {
                                if (!allFluid.isFluidEqual(fluid)) drain = true;
                            }
                        } else drain = true;
                    } else drain = true;
                    final var fluidStorage = grid.getInventory(Util.getFluidChannel());
                    final AEFluidStack allAEFluid;
                    if (drain) {
                        allAEFluid = AEFluidStack.fromFluidStack(allFluid);
                        if (allAEFluid == null) return null;
                        final var a = fluidStorage.injectItems(allAEFluid, Actionable.SIMULATE, source);
                        final var size = allAEFluid.getStackSize() - (a == null ? 0 : a.getStackSize());
                        fluidStorage.injectItems(allAEFluid.setStackSize(size), Actionable.MODULATE, source);
                        fh.drain((int) size, true);
                    } else {
                        allAEFluid = AEFluidStack.fromFluidStack(fluid);
                        final var a = fluidStorage.extractItems(allAEFluid, Actionable.SIMULATE, source);
                        final var size = fh.fill(a.getFluidStack(), false);
                        fluidStorage.extractItems(allAEFluid.setStackSize(size), Actionable.MODULATE, source);
                        fh.fill(a.getFluidStack(), true);
                    }
                    if (h.getCount() > 1) {
                        h.shrink(1);
                        final var cc = fh.getContainer();
                        cc.setCount(1);
                        player.inventory.placeItemBackInInventory(player.world, cc);
                    } else player.inventory.setItemStack(fh.getContainer());
                    updateHeld(player);
                } else if (ModAndClassUtil.GAS && message.type == GAS && h.getItem() instanceof IGasItem ig) {
                    final ItemStack gi = h.copy();
                    gi.setCount(1);
                    final var allGas = ig.getGas(gi);
                    final var allAmount = allGas == null ? 0 : allGas.amount;
                    GasStack gas = null;
                    if (!message.obj.isEmpty()) {
                        gas = GasStack.readFromNBT(message.obj);
                        if (gas != null) {
                            if (allGas != null && allGas.amount > 0) {
                                if (!allGas.isGasEqual(gas)) drain = true;
                            }
                        } else drain = true;
                    } else drain = true;
                    final var gasStorage = grid.getInventory(Util.getGasChannel());
                    AEGasStack allAEGas;
                    if (drain) {
                        allAEGas = AEGasStack.of(allGas);
                        if (allAEGas == null) return null;
                        final var a = gasStorage.injectItems(allAEGas, Actionable.SIMULATE, source);
                        final var size = allAEGas.getStackSize() - (a == null ? 0 : a.getStackSize());
                        gasStorage.injectItems(allAEGas.setStackSize(size), Actionable.MODULATE, source);
                        allGas.amount -= (int) size;
                        ig.setGas(gi, allGas);
                    } else {
                        allAEGas = AEGasStack.of(gas);
                        if (allAEGas == null) return null;
                        final var a = gasStorage.extractItems(allAEGas, Actionable.SIMULATE, source);
                        final var size = Math.min(ig.getMaxGas(gi) - allAmount, (int) a.getStackSize());
                        gasStorage.extractItems(allAEGas.setStackSize(size), Actionable.MODULATE, source);
                        gas.amount = size + allAmount;
                        ig.setGas(gi, gas);
                    }
                    if (h.getCount() > 1) {
                        h.shrink(1);
                        player.inventory.placeItemBackInInventory(player.world, gi);
                    } else player.inventory.setItemStack(gi);
                    updateHeld(player);
                }
            }
            return null;
        }

        private void updateHeld(EntityPlayerMP p) {
            if (Platform.isServer()) {
                try {
                    NetworkHandler.instance().sendTo(new PacketInventoryAction(InventoryAction.UPDATE_HAND, 0, AEItemStack.fromItemStack(p.inventory.getItemStack())), p);
                } catch (IOException e) {
                    AELog.debug(e);
                }
            }

        }
    }
}