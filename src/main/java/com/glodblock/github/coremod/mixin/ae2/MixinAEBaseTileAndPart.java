package com.glodblock.github.coremod.mixin.ae2;

import appeng.parts.AEBasePart;
import appeng.tile.AEBaseTile;
import appeng.util.SettingsFrom;
import com.glodblock.github.coremod.CoreModHooks;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {AEBasePart.class, AEBaseTile.class}, remap = false)
public class MixinAEBaseTileAndPart {

    @Inject(method = "uploadSettings", at = @At("HEAD"))
    public void onUploadSettings(SettingsFrom from, NBTTagCompound compound, EntityPlayer player, CallbackInfo ci) {
        CoreModHooks.uploadExtraNBT(this, compound);
    }

    @Inject(method = "downloadSettings", at = @At(value = "RETURN"))
    public void onDownloadSettings(SettingsFrom from, CallbackInfoReturnable<NBTTagCompound> cir, @Local(name = "output") NBTTagCompound output) {
        CoreModHooks.downloadExtraNBT(this, output);
    }

}
