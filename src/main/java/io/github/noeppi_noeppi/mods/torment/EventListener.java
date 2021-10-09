package io.github.noeppi_noeppi.mods.torment;

import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import io.github.noeppi_noeppi.mods.torment.network.PossessMobSerializer;
import io.github.noeppi_noeppi.mods.torment.ritual.LightningRitual;
import io.github.noeppi_noeppi.mods.torment.ritual.MineRitual;
import io.github.noeppi_noeppi.mods.torment.ritual.RitualHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
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
                TormentData data = TormentData.get(player);
                data.targetAggro(attacker);
                if (data.hasAbility(Ability.LIGHTNING_STRIKE) && player.getRandom().nextInt(5) == 0) {
                    LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, attacker.level);
                    lightning.setPos(attacker.getX(), attacker.getY() + attacker.getEyeHeight(), attacker.getZ());
                    attacker.level.addFreshEntity(lightning);
                }
            }
        }
        if (event.getSource().getEntity() instanceof Player attacker) {
            TormentData data = TormentData.get(attacker);
            data.targetAggro(event.getEntityLiving());
        }
    }
    
    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent event) {
        if (event.getState().getBlock() == Blocks.RAW_GOLD_BLOCK && event.getPlayer() != null) {
            RitualHelper.startRitual(event.getPlayer(), MineRitual.INSTANCE, event.getPos());
        }
    }
    
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void clientTick(TickEvent.ClientTickEvent event) {
        if (Keybinds.POSSESS_MOB.consumeClick()) {
            if (Minecraft.getInstance().player != null) {
                TormentData data = TormentData.get(Minecraft.getInstance().player);
                if (data.hasAbility(Ability.DEVIL_ALLIANCE)) {
                    Torment.getNetwork().channel.sendToServer(new PossessMobSerializer.PossessMobMessage());
                }
            }
        }
    }
}
