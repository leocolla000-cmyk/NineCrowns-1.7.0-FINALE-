package it.lia.ninecrowns.mixin;

import it.lia.ninecrowns.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Inject(method="mayPickup", at=@At("HEAD"), cancellable=true)
    private void ninecrowns$blockStaleLegendaryResult(
        Player player,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!((Object)this instanceof ResultSlot)) return;
        if (NineCrowns.data == null) return;

        ItemStack stack = ((Slot)(Object)this).getItem();
        if (ModItems.isLegendary(stack) && NineCrowns.data.isActive(stack.getItem())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method="mayPlace", at=@At("HEAD"), cancellable=true)
    private void ninecrowns$noLegendaryInEnderChest(
        ItemStack stack,
        CallbackInfoReturnable<Boolean> cir
    ) {
        Slot self = (Slot)(Object)this;
        if (self.container instanceof PlayerEnderChestContainer && ModItems.isLegendary(stack)) {
            cir.setReturnValue(false);
        }
    }
}
