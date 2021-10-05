package io.github.noeppi_noeppi.mods.torment.item;

import io.github.noeppi_noeppi.libx.base.ItemBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class GlowBerrySyrup extends ItemBase {

    public GlowBerrySyrup(ModX mod) {
        super(mod, new Item.Properties().stacksTo(4));
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity living) {
        if (living instanceof Player player) {
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            }
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!level.isClientSide) {
                TormentData.get(player).cure(18000, 2);
            }
            if (!player.isCreative()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    return new ItemStack(Items.GLASS_BOTTLE);
                } else {
                    player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return 32;
    }

    @Nonnull
    @Override
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        return ItemUtils.startUsingInstantly(level, player, hand);
    }
}
