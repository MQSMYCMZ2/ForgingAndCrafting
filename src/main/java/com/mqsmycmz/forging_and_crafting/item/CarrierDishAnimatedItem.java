package com.mqsmycmz.forging_and_crafting.item;

import com.mqsmycmz.forging_and_crafting.item.renderer.CarrierDishAnimatedItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class CarrierDishAnimatedItem extends Item implements GeoItem {

    // 动画定义：旋转+浮动动画
    private static final RawAnimation IDLE_ANIM = RawAnimation.begin()
            .thenLoop("carrier_dish_with_raw_iron_block");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CarrierDishAnimatedItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "idle", 0, state -> {
            state.getController().setAnimation(IDLE_ANIM);
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // 注册GeckoLib渲染器
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private CarrierDishAnimatedItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new CarrierDishAnimatedItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}