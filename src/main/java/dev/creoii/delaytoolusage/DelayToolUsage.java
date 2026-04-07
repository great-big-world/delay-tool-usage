package dev.creoii.delaytoolusage;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.block.Blocks;

public class DelayToolUsage implements ModInitializer {
    public static final ItemUseAnimation TOOL = ItemUseAnimation.valueOf("TOOL");
    public static final SoundEvent ITEM_PICKAXE_CRACK = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("great_big_world", "item.pickaxe.crack"));

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, Identifier.fromNamespaceAndPath("great_big_world", "item.pickaxe.crack"), ITEM_PICKAXE_CRACK);

        UsageRegistry.registerPickaxe(Blocks.STONE_BRICKS, () -> Blocks.CRACKED_STONE_BRICKS);
        UsageRegistry.registerPickaxe(Blocks.INFESTED_STONE_BRICKS, () -> Blocks.INFESTED_CRACKED_STONE_BRICKS);
        UsageRegistry.registerPickaxe(Blocks.DEEPSLATE_BRICKS, () -> Blocks.CRACKED_DEEPSLATE_BRICKS);
        UsageRegistry.registerPickaxe(Blocks.DEEPSLATE_TILES, () -> Blocks.CRACKED_DEEPSLATE_TILES);
        UsageRegistry.registerPickaxe(Blocks.POLISHED_BLACKSTONE_BRICKS, () -> Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
        UsageRegistry.registerPickaxe(Blocks.NETHER_BRICKS, () -> Blocks.CRACKED_NETHER_BRICKS);
    }
}
