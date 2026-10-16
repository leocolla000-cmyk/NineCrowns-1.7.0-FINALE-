package it.lia.ninecrowns.mixin;
import it.lia.ninecrowns.CrownEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LivingEntity.class)
public abstract class EquipmentMixin {
 @Inject(method="onEquipItem",at=@At("TAIL"))
 private void ninecrowns$equipment(EquipmentSlot slot,ItemStack oldStack,ItemStack newStack,CallbackInfo ci){
  if(slot==EquipmentSlot.HEAD&&(Object)this instanceof ServerPlayer player)CrownEffects.update(player);
 }
}
