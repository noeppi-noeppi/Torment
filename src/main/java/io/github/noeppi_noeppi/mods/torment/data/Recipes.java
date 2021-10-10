package io.github.noeppi_noeppi.mods.torment.data;

import io.github.noeppi_noeppi.libx.annotation.data.Datagen;
import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import io.github.noeppi_noeppi.libx.data.provider.recipe.crafting.CraftingExtension;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.mods.torment.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Items;

@Datagen
public class Recipes extends RecipeProviderBase implements CraftingExtension {

    public Recipes(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    protected void setup() {
        this.shapeless(ModItems.glowBerrySyrup, Items.GLASS_BOTTLE, Items.GLOW_BERRIES, Items.SUGAR, Items.GLOW_INK_SAC);
    }
}
