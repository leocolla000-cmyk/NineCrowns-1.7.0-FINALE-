package it.lia.ninecrowns.mixin;

import it.lia.ninecrowns.ModItems;
import it.lia.ninecrowns.NineCrowns;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public abstract class ResultSlotMixin {
    @Inject(method = "onTake", at = @At("HEAD"), cancellable = true)
    private void ninecrowns$lockLegendaryCraft(
        Player player,
        ItemStack carried,
        CallbackInfo ci
    ) {
        if (NineCrowns.data == null) return;
        if (!carried.is(ModItems.SWORD) && !carried.is(ModItems.SPEAR) && !carried.is(ModItems.CROWN)) return;

        // The JSON shaped recipes already consume all 9 ingredients normally.
        // This hook only reserves the ONE active world-wide copy of this legendary item.
        if (!NineCrowns.data.tryActivate(carried.getItem())) {
            // Normally SlotMixin prevents a stale result from being picked up at all.
            // This is a second server-side guard for safety.
            carried.setCount(0);
            ci.cancel();
        }
    }
}
