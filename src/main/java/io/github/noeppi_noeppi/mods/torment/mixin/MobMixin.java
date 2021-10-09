package io.github.noeppi_noeppi.mods.torment.mixin;

import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobMixin {
    
    @Inject(
            method = "Lnet/minecraft/world/entity/Mob;setTarget(Lnet/minecraft/world/entity/LivingEntity;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    // Required because in forge event, there's no access to the old target
    public void setTarget(LivingEntity target, CallbackInfo ci) {
        if (target instanceof Player player) {
            TormentData data = TormentData.get(player);
            if (data.isPossessed((Mob) (Object) this)) {
                ci.cancel();
            }
        }
    }
}
