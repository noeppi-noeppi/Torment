package io.github.noeppi_noeppi.mods.torment.mixin;

import io.github.noeppi_noeppi.mods.torment.effect.EffectManager;
import io.github.noeppi_noeppi.mods.torment.effect.instances.MouseStutterEffect;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Shadow private double accumulatedDX;
    @Shadow private double accumulatedDY;

    @Inject(
            method = "Lnet/minecraft/client/MouseHandler;turnPlayer()V",
            at = @At("HEAD")
    )
    public void turnPlayer(CallbackInfo ci) {
        if (EffectManager.isRunning(MouseStutterEffect.INSTANCE)) {
            // Same values for 50 millis.
            // Multiplication is required because pseudo random number generators
            // are pseudo random
            Random random = new Random((System.currentTimeMillis() / 50l) * "PumpkinPotions".hashCode());
            if (random.nextBoolean()) accumulatedDX = 0;
            if (random.nextBoolean()) accumulatedDY = 0;
        }
    }
}
