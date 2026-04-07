package dev.creoii.delaytoolusage.mixin;

import dev.creoii.delaytoolusage.PickaxeItem;
import net.minecraft.world.item.ToolMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

@Mixin(Items.class)
public abstract class ItemsMixin {
    @Shadow
    public static Item registerItem(String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
        return null;
    }

    @Shadow
    public static Item registerItem(String id, Function<Item.Properties, Item> factory) {
        return null;
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 32))
    private static Item gbw$woodenPickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.WOOD, 1f, -2.8f, settings1));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 34))
    private static Item gbw$copperPickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.WOOD, 1f, -2.8f, settings1));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 36))
    private static Item gbw$stonePickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.STONE, 1f, -2.8f, settings1));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 38))
    private static Item gbw$goldenPickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.GOLD, 1f, -2.8f, settings1));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 40))
    private static Item gbw$ironPickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.IRON, 1f, -2.8f, settings1));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 42))
    private static Item gbw$diamondPickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.DIAMOND, 1f, -2.8f, settings1));
    }

    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Items;registerItem(Ljava/lang/String;Lnet/minecraft/world/item/Item$Properties;)Lnet/minecraft/world/item/Item;", ordinal = 44))
    private static Item gbw$netheritePickaxe(String id, Item.Properties properties) {
        return registerItem(id, settings1 -> new PickaxeItem(ToolMaterial.NETHERITE, 1f, -2.8f, settings1), new Item.Properties().fireResistant());
    }
}
