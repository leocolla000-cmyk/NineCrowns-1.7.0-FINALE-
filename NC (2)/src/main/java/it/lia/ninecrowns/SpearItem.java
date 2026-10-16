package it.lia.ninecrowns;

import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

public final class SpearItem extends LegendaryItem {
    public SpearItem(Properties p) {
        super(p);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.isShiftKeyDown()) {
            if (player instanceof ServerPlayer sp) NineCrowns.lightning(sp);
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }
}
