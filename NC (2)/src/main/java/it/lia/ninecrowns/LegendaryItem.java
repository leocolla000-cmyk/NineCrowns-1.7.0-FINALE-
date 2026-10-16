package it.lia.ninecrowns;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;

/** Base class for legendary items whose unique craft unlocks again when destroyed. */
public class LegendaryItem extends Item {
    public LegendaryItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        super.onDestroyed(itemEntity);
        if (NineCrowns.data != null) {
            NineCrowns.data.release(this);
        }
    }
}
