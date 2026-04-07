package dev.creoii.delaytoolusage;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class UsageRegistry {
    public static final Map<Type, Map<Block, Supplier<Block>>> REGISTRY = new HashMap<>();

    public static void registerPickaxe(Block from, Supplier<Block> to) {
        register(Type.PICKAXE, from, to);
    }

    private static void register(Type type, Block from, Supplier<Block> to) {
        if (REGISTRY.containsKey(type)) {
            REGISTRY.get(type).put(from, to);
        } else {
            Map<Block, Supplier<Block>> map = new HashMap<>();
            map.put(from, to);
            REGISTRY.put(type, map);
        }
    }

    @Nullable
    public static Supplier<Block> getResult(Type type, Block key) {
        return REGISTRY.containsKey(type) ? REGISTRY.get(type).get(key) : null;
    }

    public enum Type {
        PICKAXE,
        AXE,
        SHOVEL,
        HOE
    }
}
