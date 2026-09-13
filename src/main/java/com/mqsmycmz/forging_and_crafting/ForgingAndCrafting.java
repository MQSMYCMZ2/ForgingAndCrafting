package com.mqsmycmz.forging_and_crafting;

import com.mojang.logging.LogUtils;
import com.mqsmycmz.forging_and_crafting.block.ForgingAndCraftingBlocks;
import com.mqsmycmz.forging_and_crafting.block.entity.ForgingAndCraftingBlockEntities;
import com.mqsmycmz.forging_and_crafting.block.entity.renderer.GrindingTableBlockEntityRenderer;
import com.mqsmycmz.forging_and_crafting.block.entity.renderer.RockCrusherGeoBlockEntityRenderer;
import com.mqsmycmz.forging_and_crafting.item.ForgingAndCraftingItems;
import com.mqsmycmz.forging_and_crafting.recipe.ForgingAndCraftingRecipes;
import com.mqsmycmz.forging_and_crafting.tab.ForgingAndCraftingCreativeModeTabs;
import com.mqsmycmz.forging_and_crafting.world.menu.ForgingAndCraftingMenuTypes;
import com.mqsmycmz.forging_and_crafting.world.screen.RockCrusherScreen;
import com.mqsmycmz.forging_and_crafting.world.screen.ShapingTableScreen;
import com.mqsmycmz.forging_and_crafting.world.screen.packet.ShapingTableSelectRecipePacket;
import com.mqsmycmz.forging_and_crafting.world.screen.packet.ShapingTableStartCraftPacket;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod(ForgingAndCrafting.MOD_ID)
public class ForgingAndCrafting
{
    public static final String MOD_ID = "forging_and_crafting";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MOD_ID, "main"),
            () -> "1.0",
            s -> true,
            s -> true
    );
    private static int packetId = 0;

    private static <T> void registerPacket(Class<T> tClass,
                                           BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder,
                                           BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        CHANNEL.registerMessage(packetId ++, tClass, encoder, decoder, handler);
    }

    public ForgingAndCrafting(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        GeckoLib.initialize();

        ForgingAndCraftingCreativeModeTabs.register(modEventBus);
        ForgingAndCraftingBlocks.register(modEventBus);
        ForgingAndCraftingItems.register(modEventBus);

        ForgingAndCraftingBlockEntities.register(modEventBus);
        ForgingAndCraftingMenuTypes.register(modEventBus);
        ForgingAndCraftingRecipes.register(modEventBus);

        modEventBus.addListener(this::addCreative);

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        registerPacket(ShapingTableSelectRecipePacket.class,
                ShapingTableSelectRecipePacket::encode,
                ShapingTableSelectRecipePacket::decode,
                ShapingTableSelectRecipePacket::handle);
        registerPacket(ShapingTableStartCraftPacket.class,
                ShapingTableStartCraftPacket::encode,
                ShapingTableStartCraftPacket::decode,
                ShapingTableStartCraftPacket::handle);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            MenuScreens.register(ForgingAndCraftingMenuTypes.ROCK_CRUSHER_MENU.get(), RockCrusherScreen::new);
            MenuScreens.register(ForgingAndCraftingMenuTypes.SHAPING_TABLE.get(), ShapingTableScreen::new);
            BlockEntityRenderers.register(ForgingAndCraftingBlockEntities.ROCK_CRUSHER_BLOCK_ENTITY.get(), RockCrusherGeoBlockEntityRenderer::new);
            BlockEntityRenderers.register(ForgingAndCraftingBlockEntities.CARRIER_DISH.get(), GrindingTableBlockEntityRenderer::new);
//            ItemBlockRenderTypes.setRenderLayer(ForgingAndCraftingBlocks.SOLUTION_DELIVERY_PIPELINE.get(), RenderType.translucent());

            event.enqueueWork(() -> {
            ItemBlockRenderTypes.setRenderLayer(
                    ForgingAndCraftingBlocks.WATER_WOODEN_BUCKET.get(),
                    RenderType.translucent()
            );
        });
        }
    }
}
