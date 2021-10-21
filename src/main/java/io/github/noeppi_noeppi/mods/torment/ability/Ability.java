package io.github.noeppi_noeppi.mods.torment.ability;

import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;

import java.util.function.Supplier;

public enum Ability {
    
    LIGHTNING_STRIKE(() -> TormentConfig.rituals.lightning_cost),
    COMPRESSED_LUCK(() -> TormentConfig.rituals.luck_cost),
    DEVIL_ALLIANCE(() -> TormentConfig.rituals.devil_cost);
    
    private final Supplier<Integer> targetCost;

    Ability(Supplier<Integer> targetCost) {
        this.targetCost = targetCost;
    }

    public int targetCost() {
        return targetCost.get();
    }
}
