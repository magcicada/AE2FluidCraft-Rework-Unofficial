package com.glodblock.github.coremod;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraftforge.fml.common.Loader;
import zone.rong.mixinbooter.ILateMixinLoader;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

@SuppressWarnings("unused")
public class LateMixinLoader implements ILateMixinLoader {

    private static final Map<String, BooleanSupplier> MIXIN_CONFIGS = new Object2ObjectLinkedOpenHashMap<>();

    static {
        addMixinCFG("mixins.ae2fc.json");
        addMixinCFG("mixins.ae2fc.packagedauto.json", () -> Loader.isModLoaded("packagedauto"));
        addMixinCFG("mixins.ae2fc.jei.json", () -> Loader.isModLoaded("jei"));
        addMixinCFG("mixins.ae2fc.wct.json", () -> Loader.isModLoaded("wct"));
    }

    private static void addMixinCFG(final String mixinConfig) {
        addMixinCFG(mixinConfig, () -> true);
    }

    private static void addMixinCFG(final String mixinConfig, @Nonnull final BooleanSupplier conditions) {
        MIXIN_CONFIGS.put(mixinConfig, conditions);
    }

    @Override
    public List<String> getMixinConfigs() {
        return new ObjectArrayList<>(MIXIN_CONFIGS.keySet());
    }

    @Override
    public boolean shouldMixinConfigQueue(final String mixinConfig) {
        return MIXIN_CONFIGS.get(mixinConfig).getAsBoolean();
    }

}
