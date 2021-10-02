package io.github.noeppi_noeppi.mods.torment;

import io.github.noeppi_noeppi.libx.annotation.registration.RegisterClass;
import io.github.noeppi_noeppi.mods.torment.item.GlowBerrySyrup;
import net.minecraft.world.item.Item;

@RegisterClass
public class ModItems {
    
    public static final Item glowBerrySyrup = new GlowBerrySyrup(Torment.getInstance());
}
