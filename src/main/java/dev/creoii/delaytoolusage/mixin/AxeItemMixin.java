package dev.creoii.delaytoolusage.mixin;

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
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
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
import java.util.Optional;

@Mixin(AxeItem.class)
public abstract class AxeItemMixin extends Item {
    @Shadow @Final protected static Map<Block, Block> STRIPPABLES;
    @Shadow protected abstract Optional<BlockState> evaluateNewBlockState(Level world, BlockPos pos, @Nullable Player player, BlockState state);

    public AxeItemMixin(ToolMaterial material, float attackDamage, float attackSpeed, Properties settings) {
        super(settings.axe(material, attackDamage, attackSpeed));
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
        if (canPlayerStrip(world, user) != null) {
            user.startUsingItem(hand);
        }
        return InteractionResult.PASS;
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void gbw$cancelDefaultBehavior(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(InteractionResult.PASS);
    }

    @Override
    public void onUseTick(Level world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int i = getUseDuration(stack, user) - remainingUseTicks;

        if (i >= 3 && i % 2 == 0) {
            BlockHitResult blockHitResult;
            if (user instanceof Player player && (blockHitResult = canPlayerStrip(world, player)) != null) {
                BlockPos pos = blockHitResult.getBlockPos();

                Optional<BlockState> optional = evaluateNewBlockState(world, pos, player, world.getBlockState(pos));
                if (optional.isPresent() && !world.isClientSide()) {
                    if (player instanceof ServerPlayer serverPlayer)
                        CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);

                    world.setBlock(pos, optional.get(), Block.UPDATE_ALL_IMMEDIATE);
                    world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, optional.get()));

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
    private BlockHitResult canPlayerStrip(Level world, Player player) {
        if (player.isSpectator())
            return null;

        HitResult hit = player.pick(player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE), 0f, false);
        if (hit instanceof BlockHitResult blockHitResult) {
            BlockState state = world.getBlockState(blockHitResult.getBlockPos());
            if (STRIPPABLES.containsKey(state.getBlock())) {
                return blockHitResult;
            } else if (WeatheringCopper.getPrevious(state).isPresent()) {
                return blockHitResult;
            } else if (Optional.ofNullable(HoneycombItem.WAX_OFF_BY_BLOCK.get().get(state.getBlock())).map(block -> block.withPropertiesOf(state)).isPresent()) {
                return blockHitResult;
            }
        }
        return null;
    }
}
