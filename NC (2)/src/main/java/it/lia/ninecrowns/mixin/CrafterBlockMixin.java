package it.lia.ninecrowns.mixin;

import java.util.Optional;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrafterBlock.class)
public abstract class CrafterBlockMixin {
    @Inject(method="getPotentialResults", at=@At("RETURN"), cancellable=true)
    private static void ninecrowns$blockAutomatedSpecialCrafts(
        ServerLevel level,
        CraftingInput input,
        CallbackInfoReturnable<Optional<RecipeHolder<CraftingRecipe>>> cir
    ) {
        Optional<RecipeHolder<CraftingRecipe>> result = cir.getReturnValue();
        if (result.isEmpty()) return;

        var id = result.get().id().identifier();
        if (!id.getNamespace().equals("ninecrowns")) return;

        String path = id.getPath();
        if (path.equals("all_heads") || path.equals("op_sword") || path.equals("op_spear") || path.equals("emperor_crown")) {
            // All Heads needs the identity of the real crafter. Legendary recipes are
            // also kept player-only so the uniqueness lock cannot be bypassed by Crafter.
            cir.setReturnValue(Optional.empty());
        }
    }
}
