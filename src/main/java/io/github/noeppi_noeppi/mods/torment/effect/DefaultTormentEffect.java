package io.github.noeppi_noeppi.mods.torment.effect;

import io.github.noeppi_noeppi.mods.torment.config.EffectData;

import java.util.function.Supplier;

public abstract class DefaultTormentEffect implements TormentEffect {

    private final Supplier<EffectData> data;

    protected DefaultTormentEffect(Supplier<EffectData> data) {
        this.data = data;
    }

    @Override
    public final int minCoolDown() {
        return data.get().min_cooldown();
    }

    @Override
    public final float minLevel() {
        return data.get().min_level();
    }

    @Override
    public final int weight() {
        return data.get().weight();
    }
}
