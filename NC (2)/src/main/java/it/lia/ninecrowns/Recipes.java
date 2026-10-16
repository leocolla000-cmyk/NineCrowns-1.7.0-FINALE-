package it.lia.ninecrowns;

import java.util.*;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.CraftingInput;

public final class Recipes {
    // All Heads:
    // H H H
    // H H H
    // H H H
    // H = each of the nine OTHER registered participants, all distinct.
    private static final int[] ALL_HEAD_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};

    // Legendary recipes:
    // D A D
    // D X D
    // D D D
    // A = All Heads, D = Diamond Block, X = base legendary item.
    private static final int ALL_HEADS_SLOT = 1;
    private static final int CENTER_SLOT = 4;
    private static final int[] DIAMOND_SLOTS = {0, 2, 3, 5, 6, 7, 8};

    public static Item resultForCenter(ItemStack stack) {
        if (stack.is(Items.NETHERITE_SWORD)) return ModItems.SWORD;
        if (stack.is(Items.NETHERITE_SPEAR)) return ModItems.SPEAR;
        if (stack.is(ModItems.EMPTY_CROWN)) return ModItems.CROWN;
        return null;
    }

    public static boolean allHeadsShape(CraftingInput input) {
        if (input.width() != 3 || input.height() != 3) return false;
        for (int slot : ALL_HEAD_SLOTS) {
            if (!input.getItem(slot).is(Items.PLAYER_HEAD)) return false;
        }
        return true;
    }

    public static boolean allHeadsShape(CraftingContainer grid) {
        if (grid.getWidth() != 3 || grid.getHeight() != 3) return false;
        for (int slot : ALL_HEAD_SLOTS) {
            if (!grid.getItem(slot).is(Items.PLAYER_HEAD)) return false;
        }
        return true;
    }

    /** Shape-only check for OP Sword, OP Spear and Emperor Crown. */
    public static boolean opShape(CraftingInput input) {
        if (input.width() != 3 || input.height() != 3) return false;
        if (!input.getItem(ALL_HEADS_SLOT).is(ModItems.ALL_HEADS)) return false;
        if (resultForCenter(input.getItem(CENTER_SLOT)) == null) return false;
        for (int slot : DIAMOND_SLOTS) {
            if (!input.getItem(slot).is(Items.DIAMOND_BLOCK)) return false;
        }
        return true;
    }

    public static boolean opShape(CraftingContainer grid) {
        if (grid.getWidth() != 3 || grid.getHeight() != 3) return false;
        if (!grid.getItem(ALL_HEADS_SLOT).is(ModItems.ALL_HEADS)) return false;
        if (resultForCenter(grid.getItem(CENTER_SLOT)) == null) return false;
        for (int slot : DIAMOND_SLOTS) {
            if (!grid.getItem(slot).is(Items.DIAMOND_BLOCK)) return false;
        }
        return true;
    }

    /** Validates that All Heads was made from exactly the other nine registered players. */
    public static ItemStack craftAllHeads(ServerPlayer player, CraftingContainer grid) {
        if (!allHeadsShape(grid) || NineCrowns.data == null) return ItemStack.EMPTY;

        List<UUID> heads = new ArrayList<>(Roster.SIZE - 1);
        for (int slot : ALL_HEAD_SLOTS) {
            ItemStack stack = grid.getItem(slot);
            var profile = stack.get(DataComponents.PROFILE);
            if (profile == null) return ItemStack.EMPTY;

            UUID id = profile.partialProfile().id();
            if (id == null) return ItemStack.EMPTY;
            heads.add(id);
        }

        if (!NineCrowns.data.roster.validHeads(player.getUUID(), heads)) {
            return ItemStack.EMPTY;
        }

        return new ItemStack(ModItems.ALL_HEADS);
    }

    /** Validates a legendary recipe and the global one-copy rule. */
    public static ItemStack craft(ServerPlayer player, CraftingContainer grid) {
        if (!opShape(grid)) return ItemStack.EMPTY;

        Item result = resultForCenter(grid.getItem(CENTER_SLOT));
        if (result == null) return ItemStack.EMPTY;

        if (NineCrowns.data == null || NineCrowns.data.isActive(result)) {
            return ItemStack.EMPTY;
        }

        return ModItems.enchanted(result, player.registryAccess());
    }
}
