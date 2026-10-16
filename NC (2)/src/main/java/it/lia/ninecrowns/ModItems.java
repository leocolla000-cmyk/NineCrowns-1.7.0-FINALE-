package it.lia.ninecrowns;

import net.minecraft.core.*;
import net.minecraft.core.component.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.util.Unit;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.*;
import net.minecraft.world.item.equipment.*;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import java.util.function.Function;

public final class ModItems {
    public static Item SWORD, SPEAR, EMPTY_CROWN, CROWN, ALL_HEADS;

    private static Item register(String name, Function<Item.Properties, Item> factory) {
        var id = NineCrowns.id(name);
        var properties = new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
        properties.rarity(Rarity.EPIC).stacksTo(1);
        return Registry.register(BuiltInRegistries.ITEM, id, factory.apply(properties));
    }

    public static void init() {
        ALL_HEADS = register("all_heads", Item::new);

        SWORD = register("op_sword", p -> new LegendaryItem(
            p.sword(ToolMaterial.NETHERITE, 6, -2.4f)
             .fireResistant()
             .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
             .attributes(weapon(10, -2.4))
        ));

        SPEAR = register("op_spear", p -> new SpearItem(
            p.spear(ToolMaterial.NETHERITE, 1.05f, 1.075f, .5f, 3f, 10f, 6.5f, 5.1f, 10f, 4.6f)
             .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
             .attributes(weapon(6, -3))
             .component(DataComponents.MINIMUM_ATTACK_CHARGE, 1f)
        ));

        EMPTY_CROWN = register("empty_crown", p -> new Item(
            p.humanoidArmor(ArmorMaterials.GOLD, ArmorType.HELMET)
             .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
             .rarity(Rarity.RARE)
        ));

        CROWN = register("emperor_crown", p -> new LegendaryItem(
            p.humanoidArmor(ArmorMaterials.NETHERITE, ArmorType.HELMET)
             .fireResistant()
             .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
             // No equipment asset: Minecraft renders the custom item model on the head.
             .component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.HEAD).build())
        ));
    }

    private static ItemAttributeModifiers weapon(double damage, double speed) {
        return ItemAttributeModifiers.builder()
            .add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, damage, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, speed, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
            )
            .build();
    }

    public static boolean isLegendary(ItemStack stack) {
        return !stack.isEmpty() && (stack.is(SWORD) || stack.is(SPEAR) || stack.is(CROWN));
    }

    public static boolean isLegendary(Item item) {
        return item == SWORD || item == SPEAR || item == CROWN;
    }

    /** Creates a finished special item with all mandatory properties already applied. */
    public static ItemStack enchanted(Item item, RegistryAccess access) {
        var stack = new ItemStack(item);
        applyRequiredProperties(stack, access);
        return stack;
    }

    /**
     * Keeps every Nine Crowns special item in the required state, including items
     * obtained through /give or the Creative inventory.
     */
    public static void applyRequiredProperties(ItemStack stack, RegistryAccess access) {
        if (stack.isEmpty()) return;
        if (!stack.is(SWORD) && !stack.is(SPEAR) && !stack.is(EMPTY_CROWN) && !stack.is(CROWN)) return;

        // Truly indestructible: durability never decreases.
        if (!stack.has(DataComponents.UNBREAKABLE)) {
            stack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        }

        if (stack.is(SWORD)) {
            stack.set(DataComponents.ATTRIBUTE_MODIFIERS, weapon(NineCrowns.config.swordDamage - 4, -2.4));
        } else if (stack.is(SPEAR)) {
            stack.set(
                DataComponents.ATTRIBUTE_MODIFIERS,
                weapon(NineCrowns.config.spearMeleeDamage - 4, 20.0 / NineCrowns.config.spearCooldownTicks - 4)
            );
        }

        // Empty Crown must only be unbreakable; it has no mandatory enchantment.
        if (stack.is(EMPTY_CROWN)) return;

        var enchantments = access.lookupOrThrow(Registries.ENCHANTMENT);
        var current = stack.getEnchantments();

        var sharpness = enchantments.getOrThrow(Enchantments.SHARPNESS);
        var lunge = enchantments.getOrThrow(Enchantments.LUNGE);
        var protection = enchantments.getOrThrow(Enchantments.PROTECTION);

        boolean needsUpdate =
            (stack.is(SWORD) && current.getLevel(sharpness) != 5) ||
            (stack.is(SPEAR) && (current.getLevel(lunge) != 3 || current.getLevel(sharpness) != 5)) ||
            (stack.is(CROWN) && current.getLevel(protection) != 10);

        if (!needsUpdate) return;

        // updateEnchantments writes the requested levels directly. This intentionally
        // allows Protection X, which is above vanilla survival limits.
        // The OP sword is forced to Sharpness V; the OP spear is Sharpness V + Lunge III.
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            if (stack.is(SWORD)) {
                mutable.set(sharpness, 5);
            } else if (stack.is(SPEAR)) {
                mutable.set(lunge, 3);
                mutable.set(sharpness, 5);
            } else if (stack.is(CROWN)) {
                mutable.set(protection, 10);
            }
        });
    }
}
