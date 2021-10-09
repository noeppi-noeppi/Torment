package io.github.noeppi_noeppi.mods.torment.ability;

public enum Ability {
    
    LIGHTNING_STRIKE(4),
    COMPRESSED_LUCK(4),
    DEVIL_ALLIANCE(13);
    
    public final float targetCost;

    Ability(float targetCost) {
        this.targetCost = targetCost;
    }
}
