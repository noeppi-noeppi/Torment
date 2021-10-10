package io.github.noeppi_noeppi.mods.torment.effect;

public record EffectConfig(int duration, float strength) {
    
    public static EffectConfig instant(float strength) {
        return new EffectConfig(0, strength);
    }
}
