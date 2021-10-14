package io.github.noeppi_noeppi.mods.torment;

import io.github.noeppi_noeppi.libx.mod.registration.ModXRegistration;
import io.github.noeppi_noeppi.libx.mod.registration.RegistrationBuilder;
import io.github.noeppi_noeppi.mods.torment.cap.CapabilityTorment;
import io.github.noeppi_noeppi.mods.torment.command.TormentCommands;
import io.github.noeppi_noeppi.mods.torment.effect.EffectManager;
import io.github.noeppi_noeppi.mods.torment.effect.instances.*;
import io.github.noeppi_noeppi.mods.torment.network.TormentNetwork;
import io.github.noeppi_noeppi.mods.torment.render.HeartOverlay;
import io.github.noeppi_noeppi.mods.torment.render.WorldAdditionsRender;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlclient.registry.ClientRegistry;

import javax.annotation.Nonnull;

@Mod("torment")
public final class Torment extends ModXRegistration {

    private static Torment instance;
    private static TormentNetwork network;
    
    public Torment() {
        super("torment", new CreativeModeTab("torment") {
            @Nonnull
            @Override
            public ItemStack makeIcon() {
                return new ItemStack(ModItems.glowBerrySyrup);
            }
        });
        
        instance = this;
        network = new TormentNetwork(this);

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CapabilityTorment::register);

        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityTorment::attachPlayerCaps);
        MinecraftForge.EVENT_BUS.addListener(CapabilityTorment::playerJoin);
        MinecraftForge.EVENT_BUS.addListener(CapabilityTorment::playerCopy);
        MinecraftForge.EVENT_BUS.addListener(CapabilityTorment::playerTick);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MinecraftForge.EVENT_BUS.addListener(WorldAdditionsRender::renderWorld));
        MinecraftForge.EVENT_BUS.addListener(TormentCommands::registerCommands);
        
        MinecraftForge.EVENT_BUS.register(new EventListener());
    }

    @Override
    protected void initRegistration(RegistrationBuilder builder) {
        builder.setVersion(1);
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.registerKeyBinding(Keybinds.POSSESS_MOB);
        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.PLAYER_HEALTH_ELEMENT, this.modid + "_hearts", HeartOverlay.INSTANCE);

        EffectManager.registerEffect(GoodbyeEffect.INSTANCE);
        EffectManager.registerEffect(MobSwapEffect.INSTANCE);
        EffectManager.registerEffect(MouseStutterEffect.INSTANCE);
        EffectManager.registerEffect(RandomBlocksEffect.INSTANCE);
        EffectManager.registerEffect(SoundEffect.INSTANCE);
        EffectManager.registerEffect(StareEffect.INSTANCE);
    }

    @Nonnull
    public static Torment getInstance() {
        return instance;
    }

    @Nonnull
    public static TormentNetwork getNetwork() {
        return network;
    }
}
