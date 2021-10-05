package io.github.noeppi_noeppi.mods.torment.loot;

import com.google.gson.JsonObject;
import io.github.noeppi_noeppi.mods.torment.ability.Ability;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class CompressedLuckModifier extends LootModifier {
    
    public static final Map<Item, Item> ITEMS = Map.of(
            Items.COAL, Items.COAL_BLOCK,
            Items.REDSTONE, Items.REDSTONE_BLOCK,
            Items.LAPIS_LAZULI, Items.LAPIS_BLOCK,
            Items.RAW_COPPER, Items.RAW_COPPER_BLOCK,
            Items.RAW_IRON, Items.RAW_IRON_BLOCK,
            Items.RAW_GOLD, Items.RAW_GOLD_BLOCK,
            Items.DIAMOND, Items.DIAMOND_BLOCK,
            Items.EMERALD, Items.EMERALD_BLOCK
    );
    public static final Map<ResourceLocation, ResourceLocation> ITEM_IDS = Map.of();

    public CompressedLuckModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> loot, LootContext context) {
        if (context.hasParam(LootContextParams.BLOCK_STATE) && context.hasParam(LootContextParams.THIS_ENTITY)) {
            BlockState state = context.getParam(LootContextParams.BLOCK_STATE);
            if (Tags.Blocks.ORES.contains(state.getBlock())) {
                Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
                if (entity instanceof Player player && player.getRandom().nextInt(5) == 0 && TormentData.get(player).hasAbility(Ability.COMPRESSED_LUCK)) {
                    for (int i = 0; i < loot.size(); i++) {
                        ItemStack stack = loot.get(i);
                        if (ITEMS.containsKey(stack.getItem())) {
                            loot.set(i, new ItemStack(ITEMS.get(stack.getItem()), stack.getCount(), stack.getOrCreateTag().copy()));
                        } else if (ITEM_IDS.containsKey(stack.getItem().getRegistryName())) {
                            Item item = ForgeRegistries.ITEMS.getValue(ITEM_IDS.get(stack.getItem().getRegistryName()));
                            if (item != null) {
                                loot.set(i, new ItemStack(item, stack.getCount(), stack.getOrCreateTag().copy()));
                            }
                        }
                    }
                }
            }
        }
        return loot;
    }
    
    
    
    public static class Serializer extends GlobalLootModifierSerializer<CompressedLuckModifier> {

        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {

        }

        public CompressedLuckModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
            return new CompressedLuckModifier(conditions);
        }

        public JsonObject write(CompressedLuckModifier modifier) {
            return this.makeConditions(modifier.conditions);
        }
    }
}
