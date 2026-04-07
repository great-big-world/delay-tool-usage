package dev.creoii.delaytoolusage.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.creoii.delaytoolusage.DelayToolUsage;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemInHandRenderer.class)
public abstract class HeldItemRendererMixin {
    @WrapOperation(method = "renderArmWithItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;isUsingItem()Z", ordinal = 1))
    private boolean gbw$renderToolUseAction(AbstractClientPlayer instance, Operation<Boolean> original) {
        return original.call(instance) && instance.getUseItem().getUseAnimation() != DelayToolUsage.TOOL;
    }
}
