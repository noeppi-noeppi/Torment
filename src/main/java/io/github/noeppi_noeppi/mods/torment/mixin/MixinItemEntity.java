package io.github.noeppi_noeppi.mods.torment.mixin;

import io.github.noeppi_noeppi.mods.torment.ritual.DevilRitual;
import io.github.noeppi_noeppi.mods.torment.ritual.RitualHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
    
    @Inject(
            method = "Lnet/minecraft/world/entity/item/ItemEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/ItemEntity;discard()V"
            )
    )
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isFire()) {
            UUID thrower = ((ItemEntity) (Object) this).getThrower();
            Player player = thrower == null ? null : ((ItemEntity) (Object) this).level.getPlayerByUUID(thrower);
            if (player != null) {
                RitualHelper.startRitual(player, DevilRitual.INSTANCE, ((ItemEntity) (Object) this).blockPosition());
            }
        }
    }
}
