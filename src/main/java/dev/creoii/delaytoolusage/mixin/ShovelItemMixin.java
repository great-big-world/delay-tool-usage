package dev.creoii.delaytoolusage.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.creoii.delaytoolusage.DelayToolUsage;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ShovelItem.class)
public abstract class ShovelItemMixin extends Item {
    @Shadow @Final protected static Map<Block, BlockState> FLATTENABLES;

    public ShovelItemMixin(ToolMaterial material, float attackDamage, float attackSpeed, Properties settings) {
        super(settings.shovel(material, attackDamage, attackSpeed));
    }

    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return DelayToolUsage.TOOL;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (canPlayerPath(world, user) != null) {
            user.startUsingItem(hand);
        }
        return InteractionResult.PASS;
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;", shift = At.Shift.BY, by = 2), cancellable = true)
    private void gbw$cancelDefaultBehavior(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local Level world, @Local BlockPos blockPos, @Local(ordinal = 0) BlockState blockState, @Local Player playerEntity) {
        if (blockState.getBlock() instanceof CampfireBlock && blockState.getValue(CampfireBlock.LIT)) {
            CampfireBlock.dowse(playerEntity, world, blockPos, blockState);
            if (!world.isClientSide()) {
                BlockState state = blockState.setValue(CampfireBlock.LIT, false);
                world.setBlock(blockPos, state, Block.UPDATE_ALL_IMMEDIATE);
                world.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(playerEntity, state));
                if (playerEntity != null) {
                    context.getItemInHand().hurtAndBreak(1, playerEntity, context.getHand());
                }
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        cir.setReturnValue(InteractionResult.PASS);
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = getUseDuration(stack, user) - remainingUseTicks;

        if (i >= 3 && i % 2 == 0) {
            BlockHitResult blockHitResult;
            if (user instanceof Player player && (blockHitResult = canPlayerPath(world, player)) != null) {
                if (!world.isClientSide()) {
                    BlockPos pos = blockHitResult.getBlockPos();
                    BlockState state = world.getBlockState(pos);

                    if (player instanceof ServerPlayer serverPlayer)
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);

                    world.setBlock(pos, FLATTENABLES.get(state.getBlock()), Block.UPDATE_ALL);
                    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));

                    if (!player.isCreative()) {
                        stack.hurtAndBreak(1, player, player.getUsedItemHand());
                    }

                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    player.swing(player.getUsedItemHand(), true);
                }
            }
        }
    }

    @Unique
    @Nullable
    private BlockHitResult canPlayerPath(Level world, Player player) {
        if (player.isSpectator())
            return null;

        HitResult hit = player.pick(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0f, false);
        if (hit instanceof BlockHitResult blockHitResult) {
            BlockState state = world.getBlockState(blockHitResult.getBlockPos());
            if (FLATTENABLES.containsKey(state.getBlock())) {
                return blockHitResult;
            }
        }
        return null;
    }
}
