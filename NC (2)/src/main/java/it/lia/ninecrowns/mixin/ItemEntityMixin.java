package it.lia.ninecrowns.mixin;

import it.lia.ninecrowns.*;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {
    @Inject(method="tick", at=@At("TAIL"))
    private void ninecrowns$releaseLegendaryOnDespawn(CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;

        // Vanilla despawns normal item entities at age 6000. Pickup also discards
        // the entity, but a picked-up entity does not reach a later tick; requiring
        // age >= 6000 therefore targets real despawn rather than pickup/transfer.
        if (self.isRemoved()
            && self.getAge() >= 6000
            && NineCrowns.data != null
            && ModItems.isLegendary(self.getItem())) {
            NineCrowns.data.release(self.getItem().getItem());
        }
    }
}
