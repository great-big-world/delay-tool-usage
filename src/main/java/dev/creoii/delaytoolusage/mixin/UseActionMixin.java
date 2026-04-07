package dev.creoii.delaytoolusage.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.ArrayList;
import java.util.Arrays;
import net.minecraft.world.item.ItemUseAnimation;

@Mixin(ItemUseAnimation.class)
public class UseActionMixin {
    @Invoker("<init>")
    private static ItemUseAnimation init(String internalName, int internalId, int id, final String name) {
        throw new AssertionError();
    }

    @Shadow @Final @Mutable private static ItemUseAnimation[] $VALUES;

    static {
        ArrayList<ItemUseAnimation> values = new ArrayList<>(Arrays.asList($VALUES));
        int last = values.size();

        values.add(init("TOOL", last, 100, "tool"));

        $VALUES = values.toArray(new ItemUseAnimation[0]);
    }
}
