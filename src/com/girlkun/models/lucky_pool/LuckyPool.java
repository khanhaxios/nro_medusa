package com.girlkun.models.lucky_pool;

import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LuckyPool {
    public int TYPE_LEAGUE = 50;
    public int TYPE_EPIC = 15;
    public int TYPE_RARE = 5;
    public static int[] OPTION_LEAGUE = new int[]{45, 49, 77, 103, 5, 50};
    public static int[] OPTION_EPIC = new int[]{45, 49, 77, 103, 5, 50};
    public List<Short> luckyPoolEpicItems = new ArrayList<>();
    public List<Short> luckyPoolRareItems = new ArrayList<>();
    public List<Short> luckyPoolLeagueItems = new ArrayList<>();

    public LuckyPool() {
        initLuckyItem();
    }

    public void initLuckyItem() {
        // add rare item
        // rare item includes linh thu ,
        luckyPoolRareItems = Manager.ITEM_TEMPLATES.stream().filter(it -> it.type == 13 || it.type == 27 || it.type == 11 || it.type == 33 || it.type == 29).map(it -> it.id) // only get id
                .collect(Collectors.toList());
        luckyPoolEpicItems = Manager.ITEM_TEMPLATES.stream().filter(it -> it.type == 5 || it.type == 72 || it.type == 23 || it.type == 24).map(it -> it.id) // only get id
                .collect(Collectors.toList());
        luckyPoolLeagueItems = Manager.ITEM_TEMPLATES.stream().filter(it -> it.type < 5 || it.type == 32).map(it -> it.id) // only get id
                .collect(Collectors.toList());
    }

    public void ratioLeagueItemOption(Item item) {
        int slDong = 2;
        if (Util.isTrue(20, 100)) {
            slDong = 3;
        } else if (Util.isTrue(10, 100)) {
            slDong = 4;
        }
        int id;

        for (int i = 0; i < slDong; i++) {
            id = Util.nextInt(0, OPTION_LEAGUE.length - 1);
            Item.ItemOption itemOption = new Item.ItemOption(OPTION_LEAGUE[id], getParam(i, TYPE_LEAGUE));
            item.itemOptions.add(itemOption);
        }
    }

    public int getParam(int id, int type) {
        switch (id) {
            case 0: //Tấn công +#
                return 10_000 * type;
            case 2: //HP, KI+#000
                return 3000 * type;
            case 108, 73:// fake
                return type / 3;
            case 18, 94: // #% chính xác
                return type;
            case 5, 197, 220, 233, 77, 194, 221, 103, 195, 222, 100, 88, 101: //+#% sức đánh chí mạng
                return 10 * type;
            case 6, 7, 48: //HP+#
                return 30_000 * type;
            case 8, 14, 45, 80, 81, 104, 97, 162, 173: //Hút #% HP, KI xung quanh mỗi 5 giây
                return type / 5;
            case 19, 49, 147, 196, 219, 232: //Tấn công+#% khi đánh quái
                return 2 * type;
            case 22: //HP+#K
            case 23, 28, 27, 47: //MP+#K
                return 1000 * type;
            case 50: //Sức đánh+#%
                return 5 * type;
            case 95: //Biến #% tấn công thành HP
                return type;
            case 96: //Biến #% tấn công thành MP
                return type;
        }
        return 1;
    }

    public void rollInPool(Player player, int timeToRoll) {
        // auto add to ruong phu
        if (player.luckyPoolPlayer.itemBags.size() + timeToRoll > LuckyPoolPlayer.MAX_COUNT_ITEM) {
            Service.gI().sendThongBao(player, "Rương phụ đã đầy hãy nhận thưởng trước đi nào");
            return;
        }
        List<Item> itemsRolled = new ArrayList<>();
        float tyLeRoll = player.luckyPoolPlayer.totalLuckyPoint;
        // roll nè
        for (int i = 0; i < timeToRoll; i++) {
            // roll item
            // league item first
            if (tyLeRoll == 1000) {
                //  auto roll league item and pass this loop time
                player.luckyPoolPlayer.totalLuckyPoint = 0;
                // reset ty le rool
                // random item trong league pool
                Item item = ItemService.gI().createNewItem(luckyPoolLeagueItems.get(Util.nextInt(0, luckyPoolLeagueItems.size() - 1)), 1);
                // ratio option for this
                ratioLeagueItemOption(item);
                // add to bag and continue
                player.luckyPoolPlayer.addItemToBag(item);
                continue;
            }
            if (Util.isTrue(tyLeRoll, 1000)) {
                // item league
                // khi roll ra league se tu dong reset point
                player.luckyPoolPlayer.totalLuckyPoint = 0;
                Item item = ItemService.gI().createNewItem(luckyPoolLeagueItems.get(Util.nextInt(0, luckyPoolLeagueItems.size() - 1)), 1);
                // ratio option for this
                ratioLeagueItemOption(item);
                // add to bag and continue
                player.luckyPoolPlayer.addItemToBag(item);
                continue;
            }
            if (Util.isTrue(tyLeRoll, 500)) {
                Item item = ItemService.gI().createNewItem(luckyPoolEpicItems.get(Util.nextInt(0, luckyPoolEpicItems.size() - 1)), 1);
                // ratio option for this
                ratioEpicItemOption(item);
                // add to bag and continue
                player.luckyPoolPlayer.addItemToBag(item);
                player.luckyPoolPlayer.totalLuckyPoint += 1;
                if (player.luckyPoolPlayer.totalLuckyPoint > 1000) {
                    player.luckyPoolPlayer.totalLuckyPoint = 1000;
                }
                continue;
            }
            Item item = ItemService.gI().createNewItem(luckyPoolRareItems.get(Util.nextInt(0, luckyPoolRareItems.size() - 1)), 1);
            // add to bag and continue
            player.luckyPoolPlayer.addItemToBag(item);
            player.luckyPoolPlayer.totalLuckyPoint += 1;
            if (player.luckyPoolPlayer.totalLuckyPoint > 1000) {
                player.luckyPoolPlayer.totalLuckyPoint = 1000;
            }
        }
    }

    private void ratioEpicItemOption(Item item) {
        int slDong = 2;
        if (Util.isTrue(20, 100)) {
            slDong = 3;
        } else if (Util.isTrue(10, 100)) {
            slDong = 4;
        }
        int id;

        for (int i = 0; i < slDong; i++) {
            id = Util.nextInt(0, OPTION_LEAGUE.length - 1);
            Item.ItemOption itemOption = new Item.ItemOption(OPTION_EPIC[id], getParam(i, TYPE_EPIC));
            item.itemOptions.add(itemOption);
        }
    }
}
