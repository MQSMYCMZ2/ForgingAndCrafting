package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.item.renderer.RockCrusherBlockItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.RenderUtils;

import java.util.function.Consumer;

public class RockCrusherBlockItem extends BlockItem implements GeoItem {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public RockCrusherBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RockCrusherBlockItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new RockCrusherBlockItemRenderer();
                }

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "gear1_controller",
                0, this::gear1Predicate));

        controllerRegistrar.add(new AnimationController<>(this, "gear2_controller",
                0, this::gear2Predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private <T extends GeoAnimatable> PlayState gear1Predicate(AnimationState<T> state) {
            state.getController().setAnimation(RawAnimation.begin().then("rock_crusher.working.1", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState gear2Predicate(AnimationState<T> state) {
            state.getController().setAnimation(RawAnimation.begin().then("rock_crusher.working.2", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }
}
