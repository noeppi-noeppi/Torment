package io.github.noeppi_noeppi.mods.torment.effect;

public interface TormentEffect {
    
    int minCoolDown();
    float minLevel();
    int weight();
    EffectConfig start();
    void update();
    void stop();
}
