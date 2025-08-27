/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.lucky_pool;

import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LuckyPoolPlayer {
    public static int MAX_COUNT_ITEM = 100;
    public Player player;
    public int totalLuckyPoint = 0;
    public List<Item> itemBags = new ArrayList<>();

    public void addItemToBag(Item item) {
        if (itemBags.contains(item)) {
            Optional<Item> item1 = itemBags.stream()
                    .filter(t -> t.template.id == item.template.id)
                    .findFirst();
            item1.ifPresent(value -> value.quantity += 1);
            return;
        }
        itemBags.add(item);
    }

    public void sendItemBags() {
        // reopen item bag

    }

    public void removeItem(Item item) {
        itemBags.remove(item);
    }
}
