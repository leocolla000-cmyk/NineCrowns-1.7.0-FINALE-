package it.lia.ninecrowns.mixin;

import it.lia.ninecrowns.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method="onBelowWorld", at=@At("HEAD"))
    private void ninecrowns$releaseLegendaryInVoid(CallbackInfo ci) {
        if (!((Object)this instanceof ItemEntity itemEntity)) return;
        if (NineCrowns.data != null && ModItems.isLegendary(itemEntity.getItem())) {
            NineCrowns.data.release(itemEntity.getItem().getItem());
        }
    }
}
