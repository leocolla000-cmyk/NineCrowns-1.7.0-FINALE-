package it.lia.ninecrowns;
import it.lia.ninecrowns.mixin.EffectAccessor;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.EquipmentSlot;

/** Applies only the temporary effects granted by the Emperor Crown. */
public final class CrownEffects {
 public static void update(ServerPlayer p){
  if(p.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.CROWN)&&p.isAlive()){
   grant(p,MobEffects.SPEED,1);          // Speed II
   grant(p,MobEffects.STRENGTH,1);       // Strength II
   grant(p,MobEffects.FIRE_RESISTANCE,0);// Fire Resistance I
  } else clear(p);
 }
 private static void grant(ServerPlayer p,Holder<MobEffect> effect,int amplifier){
  p.addEffect(new MobEffectInstance(effect,3,amplifier,true,false,true));
 }
 public static void clear(ServerPlayer p){
  remove(p,MobEffects.SPEED,1);
  remove(p,MobEffects.STRENGTH,1);
  remove(p,MobEffects.FIRE_RESISTANCE,0);
 }
 private static void remove(ServerPlayer p,Holder<MobEffect> effect,int amplifier){
  var current=p.getEffect(effect);
  // Only remove the tiny, ambient/invisible instance created by this crown.
  // If Minecraft was hiding a potion/beacon effect underneath it, restore that instance.
  if(current!=null&&current.getAmplifier()==amplifier&&current.getDuration()<=3&&current.isAmbient()&&!current.isVisible()){
   var hidden=((EffectAccessor)(Object)current).ninecrowns$hiddenEffect();
   p.removeEffect(effect);
   if(hidden!=null&&(hidden.isInfiniteDuration()||hidden.getDuration()>0))p.addEffect(new MobEffectInstance(hidden));
  }
 }
}
