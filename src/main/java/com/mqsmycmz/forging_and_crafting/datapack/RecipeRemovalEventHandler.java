package com.mqsmycmz.forging_and_crafting.datapack;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = "forging_and_crafting", bus = Mod.EventBusSubscriber.Bus.MOD)
public class RecipeRemovalEventHandler {
    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) return;

        RecipeRemovalConfig.load();

        Path packpath = RecipeRemovalPackGenerator.generate();
        if (packpath == null) return;

        event.addRepositorySource((consumer) -> {
            Pack pack = Pack.readMetaAndCreate(
                    "forging_and_crafting_dynamic",
                    Component.literal("ForgingAndCrafting Recipe Removals"),
                    true,
                    id -> new PathPackResources(id, packpath, false),
                    PackType.SERVER_DATA,
                    Pack.Position.TOP,
                    PackSource.DEFAULT
            );

            if (pack != null) {
                consumer.accept(pack);
                System.out.println("[ForgingAndCrafting] 动态数据包注册成功");
            } else {
                System.err.println("[ForgingAndCrafting] 动态数据包注册失败（Pack.create 返回 null）");
            }
        });
    }
}
