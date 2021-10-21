package io.github.noeppi_noeppi.mods.torment.effect.instances;

import io.github.noeppi_noeppi.mods.torment.config.TormentConfig;
import io.github.noeppi_noeppi.mods.torment.effect.DefaultTormentEffect;
import io.github.noeppi_noeppi.mods.torment.effect.EffectConfig;
import io.github.noeppi_noeppi.mods.torment.effect.EffectManager;
import io.github.noeppi_noeppi.mods.torment.effect.TormentEffect;
import io.github.noeppi_noeppi.mods.torment.util.RegistryList;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class MobSwapEffect extends DefaultTormentEffect {

    public static final MobSwapEffect INSTANCE = new MobSwapEffect();
    
    // Don't put horse, donkey or mule on the list
    // These are handles specially, even if the effect is not running.
    private final RegistryList<EntityType<?>> friendly = new RegistryList<>(ForgeRegistries.ENTITIES)
            .add(EntityType.COW, EntityType.SHEEP, EntityType.CHICKEN, EntityType.PIG, EntityType.PANDA)
            .add(EntityType.DONKEY, EntityType.FOX, EntityType.GOAT);
    
    private final RegistryList<EntityType<?>> hostile = new RegistryList<>(ForgeRegistries.ENTITIES)
            .add(EntityType.ZOMBIE, EntityType.CREEPER, EntityType.SKELETON, EntityType.WITCH, EntityType.HUSK)
            .add(EntityType.STRAY, EntityType.ENDERMAN);
    
    private final Random random = new Random();
    
    private final Map<EntityType<?>, Entity> entityCache = new HashMap<>();
    private int uuidBit1 = -1;
    private int uuidBit2 = -1;

    private MobSwapEffect() {
        super(() -> TormentConfig.effects.mob_swap);
    }

    @Override
    public List<TormentEffect> cantRunWhile() {
        return List.of(StareEffect.INSTANCE);
    }

    @Nullable
    @Override
    public EffectConfig start(LocalPlayer player, Random random) {
        uuidBit1 = random.nextInt(128);
        boolean more = random.nextBoolean();
        if (more) {
            do { uuidBit2 = random.nextInt(128); } while (uuidBit1 == uuidBit2);
        }
        return new EffectConfig(3600 + random.nextInt(4800), 6 + (more ? 4 : 0));
    }

    @Override
    public void update(LocalPlayer player, Random random) {
        //
    }

    @Override
    public void stop(LocalPlayer player, Random random) {
        uuidBit1 = -1;
        uuidBit2 = -1;
    }
    
    // Patched by coremod
    public static Entity transformRenderEntity(Entity entity) {
        return INSTANCE.doTransformRenderEntity(entity);
    }
    
    private Entity doTransformRenderEntity(Entity entity) {
        if (entity instanceof Player) return entity;
        UUID uid = entity.getUUID();
        EntityType<?> type = entity.getType();
        if (type == EntityType.HORSE || type == EntityType.DONKEY || type == EntityType.MULE) {
            if (TormentConfig.misc.replace_horses.isPresent() && EffectManager.getCachedRawEffectLevel() >= TormentConfig.misc.replace_horses.get()) {
                return transformRaw(entity, (uid.getLeastSignificantBits() & (1 << 7)) == 0 ? EntityType.SKELETON_HORSE : EntityType.ZOMBIE_HORSE);
            }
        }
        if (EffectManager.isRunning(this) && (uuidBit1 >= 0 || uuidBit2 >= 0)) {
            if (friendly.contains(type)) {
                boolean has1 = uuidBit1 < 0 || (uuidBit1 < 64 && (uid.getLeastSignificantBits() & (1l << uuidBit1)) != 0) || (uuidBit1 >= 64 && (uid.getMostSignificantBits() & (1l << (uuidBit1 - 64))) != 0);
                boolean has2 = uuidBit2 < 0 || (uuidBit2 < 64 && (uid.getLeastSignificantBits() & (1l << uuidBit2)) != 0) || (uuidBit2 >= 64 && (uid.getMostSignificantBits() & (1l << (uuidBit2 - 64))) != 0);
                if (has1 && has2) {
                    random.setSeed(uid.getMostSignificantBits());
                    EntityType<?> newType = hostile.random(random);
                    if (newType != null) {
                        return transformRaw(entity, newType);
                    }
                }
            }
        }
        return entity;
    }
    
    private Entity transformRaw(Entity old, EntityType<?> newType) {
        if (newType == old.getType()) return old;
        if (!entityCache.containsKey(newType) || entityCache.get(newType).level != old.level) {
            entityCache.put(newType, newType.create(old.level));
        }
        Entity entity = entityCache.get(newType);
        entity.setPos(old.getX(), old.getY(), old.getZ());
        entity.xo = old.xo;
        entity.yo = old.yo;
        entity.zo = old.zo;
        entity.xOld = old.xOld;
        entity.yOld = old.yOld;
        entity.zOld = old.zOld;
        entity.setXRot(old.getXRot());
        entity.setYRot(old.getYRot());
        entity.setYHeadRot(old.getYHeadRot());
        entity.setYBodyRot(old.getYRot());
        entity.xRotO = old.xRotO;
        entity.yRotO = old.yRotO;
        entity.setDeltaMovement(old.getDeltaMovement());
        entity.setCustomName(old.getCustomName());
        entity.setCustomNameVisible(old.isCustomNameVisible());
        entity.setInvisible(old.isInvisible());
        entity.setRemainingFireTicks(old.isOnFire() ? 20 : 0);
        entity.isInPowderSnow = old.isInPowderSnow;
        entity.wasInPowderSnow = old.wasInPowderSnow;
        if (old instanceof LivingEntity oldLiving && entity instanceof LivingEntity living) {
            living.yBodyRot = oldLiving.yBodyRot;
            living.yHeadRotO = oldLiving.yHeadRotO;
            living.yBodyRotO = oldLiving.yBodyRotO;
            living.setHealth(oldLiving.getHealth());
            living.setAbsorptionAmount(oldLiving.getAbsorptionAmount());
            living.setArrowCount(oldLiving.getArrowCount());
            living.setItemSlot(EquipmentSlot.MAINHAND, oldLiving.getItemBySlot(EquipmentSlot.MAINHAND));
            living.setItemSlot(EquipmentSlot.OFFHAND, oldLiving.getItemBySlot(EquipmentSlot.OFFHAND));
            living.setItemSlot(EquipmentSlot.HEAD, oldLiving.getItemBySlot(EquipmentSlot.HEAD));
            living.setItemSlot(EquipmentSlot.CHEST, oldLiving.getItemBySlot(EquipmentSlot.CHEST));
            living.setItemSlot(EquipmentSlot.LEGS, oldLiving.getItemBySlot(EquipmentSlot.LEGS));
            living.setItemSlot(EquipmentSlot.FEET, oldLiving.getItemBySlot(EquipmentSlot.FEET));
            living.setOnGround(oldLiving.isOnGround());
            living.hurtMarked = oldLiving.hurtMarked;
            living.hurtDir = oldLiving.hurtDir;
            living.hurtDuration = oldLiving.hurtDuration;
            living.hurtTime = oldLiving.hurtTime;
            living.deathTime = oldLiving.deathTime;
            if (old instanceof AbstractHorse oldHorse && entity instanceof AbstractHorse horse) {
                horse.eatAnim = oldHorse.eatAnim;
                horse.eatAnimO = oldHorse.eatAnimO;
                horse.standAnim = oldHorse.standAnim;
                horse.standAnimO = oldHorse.standAnimO;
                horse.mouthAnim = oldHorse.mouthAnim;
                horse.mouthAnimO = oldHorse.mouthAnimO;
                
                int oldFlags = horse.getEntityData().get(AbstractHorse.DATA_ID_FLAGS);
                if (oldHorse.isSaddled()) {
                    horse.getEntityData().set(AbstractHorse.DATA_ID_FLAGS, (byte) (oldFlags | 4));
                } else {
                    horse.getEntityData().set(AbstractHorse.DATA_ID_FLAGS, (byte) (oldFlags & ~4));
                }
            }
        }
        return entity;
    }
}
