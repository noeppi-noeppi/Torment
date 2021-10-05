package io.github.noeppi_noeppi.mods.torment;

import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import io.github.noeppi_noeppi.mods.torment.loot.CompressedLuckModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;

@RegisterClass
public class ModMisc {
    
    public static final GlobalLootModifierSerializer<CompressedLuckModifier> compressedLuck = CompressedLuckModifier.Serializer.INSTANCE;
}
