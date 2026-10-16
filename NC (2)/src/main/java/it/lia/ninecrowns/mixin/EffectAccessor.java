package it.lia.ninecrowns.mixin;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(MobEffectInstance.class)
public interface EffectAccessor {
 @Accessor("hiddenEffect") MobEffectInstance ninecrowns$hiddenEffect();
}
