package io.github.noeppi_noeppi.mods.torment.config;

import io.github.noeppi_noeppi.libx.annotation.config.RegisterConfig;
import io.github.noeppi_noeppi.libx.config.Config;
import io.github.noeppi_noeppi.libx.config.Group;
import io.github.noeppi_noeppi.libx.config.validator.FloatRange;
import io.github.noeppi_noeppi.libx.config.validator.IntRange;

import java.util.Optional;

@RegisterConfig
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TormentConfig {
    
    @Group("Ritual values")
    public static class rituals {
        
        @Config("How many purple hearts will the ritual of the angered lightning give")
        @IntRange(min = 0, max = 20)
        public static int lightning_cost = 6;

        @Config("How many purple hearts will the ritual of the miners magic give")
        @IntRange(min = 0, max = 20)
        public static int luck_cost = 4;

        @Config("How many purple hearts will the ritual of the condemned evil give")
        @IntRange(min = 0, max = 20)
        public static int devil_cost = 13;
    }
    
    @Group("Miscellaneous stuff")
    public static class misc {
        
        @Config({
                "The minimum effect level at which horses should be visually replaced with",
                "their skeleton and zombie counterparts."
        })
        public static Optional<Float> replace_horses = Optional.of(30f);
        
        @Config("The time in ticks that the glowberry cure effect lasts")
        @IntRange(min = 0)
        public static int glowberry_ticks = 18000;
        
        @Config("How many half hearts does the glowberry syrup cure")
        @IntRange(min = 0, max = 20)
        public static int glowberry_amount = 2;
        
        @Config("How many mobs can a player possess at the same time")
        @IntRange(min = 1)
        public static int max_possess = 2;
    }
    
    @Group("Configuration for the effects by torment")
    public static class effects {
        
        @Config("Effect configuration for the random sounds effect")
        public static EffectData sounds = new EffectData(60, 1, 5);

        @Config("Effect configuration for mob stare effect")
        public static EffectData stare = new EffectData(7200, 40, 4);

        @Config("Effect configuration for the randomly placed blocks effect")
        public static EffectData random_blocks = new EffectData(6000, 10, 2);

        @Config("Effect configuration for the mob swap effect")
        public static EffectData mob_swap = new EffectData(7200, 35, 4);

        @Config("Effect configuration for the mouse stutter effect")
        public static EffectData mouse_stutter = new EffectData(4000, 20, 5);

        @Config("Effect configuration for the goodbye effect. This one kicks players out of the game.")
        public static ReducedEffectData goodbye = new ReducedEffectData(70, 2);
    }

    @Group("Configuration for ghosties")
    public static class ghosties {
        
        @Config("The minimum effect level required for ghosties to appear")
        @FloatRange(min = 0, max = 80)
        public static float min_level = 10;
        
        @Config({
                "A constant value that is added to the result of the function that computes",
                "the amount of ghosties. Higher values make more ghosties spawn, lower values less."
        })
        @FloatRange(min = -20, max = 20)
        public static float adjustment_y = 0;
        
        @Config({
                "A constant value that is added to the input of the function that computes",
                "the amount of ghosties. Higher values make ghosties spawn earlier."
        })
        @FloatRange(min = -80, max = 80)
        public static float adjustment_x = 0;
    }
}
