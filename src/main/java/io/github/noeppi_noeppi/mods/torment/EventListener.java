package io.github.noeppi_noeppi.mods.torment;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import io.github.noeppi_noeppi.mods.torment.ritual.LightningRitual;
import io.github.noeppi_noeppi.mods.torment.ritual.MineRitual;
import io.github.noeppi_noeppi.mods.torment.ritual.RitualHelper;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventListener {
    
    @SubscribeEvent
    public void usedItem(LivingEntityUseItemEvent.Finish event) {
        if (event.getItem().getItem() == Items.SPIDER_EYE && event.getEntityLiving() instanceof Player player) {
            RitualHelper.startRitual(player, LightningRitual.INSTANCE, null);
        }
    }
    
    @SubscribeEvent
    public void livingDamage(LivingAttackEvent event) {
        if (event.getSource() == DamageSource.LIGHTNING_BOLT && event.getEntityLiving() instanceof Player player) {
            if (TormentData.get(player).hasAbility(Ability.LIGHTNING_STRIKE)) {
                event.setCanceled(true);
            }
        }
    }
    
    @SubscribeEvent(priority = EventPriority.LOW)
    public void livingDamaged(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof Player player) {
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                if (player.getRandom().nextInt(5) == 0 && TormentData.get(player).hasAbility(Ability.LIGHTNING_STRIKE)) {
                    LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, attacker.level);
                    lightning.setPos(attacker.getX(), attacker.getY() + attacker.getEyeHeight(), attacker.getZ());
                    attacker.level.addFreshEntity(lightning);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() == Blocks.RAW_GOLD_BLOCK && event.getPlayer() != null) {
            RitualHelper.startRitual(event.getPlayer(), MineRitual.INSTANCE, event.getPos());
        }
    }
}
