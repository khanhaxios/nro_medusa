package com.girlkun.models.lucky_pool;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.server.Manager;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<Integer> luckyPoolSpecialItem = new ArrayList<>();
    public List<Short> luckyPoolLeagueItems = new ArrayList<>();

    public LuckyPool() {
        initLuckyItem();
    }

    private static LuckyPool I;

    public static LuckyPool getI() {
        if (I == null) {
            I = new LuckyPool();
        }
        return I;
    }

    public static void showBaseMenu(Player player) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|========= Vòng Quay May Mắn =========").append("\n");
        stringBuilder.append("|5|Điểm may mắn của bạn là : ").append(player.luckyPoolPlayer.totalLuckyPoint).append("\n");
        stringBuilder.append("|7|Tỷ lệ ra dòng Sử Thi : ").append((player.luckyPoolPlayer.totalLuckyPoint / 1000) * 100).append("%").append("\n");
        stringBuilder.append("|7|Tỷ lệ ra dòng Huyền Thoại : ").append((player.luckyPoolPlayer.totalLuckyPoint / 10_000) * 100).append("%").append("\n");
        stringBuilder.append("|5|Mỗi lần quay sẽ tăng tỷ lệ ra dòng hiếm,vật phẩm quay được sẽ nằm trong túi tạm thời").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_QSMM, -1, stringBuilder.toString(), "Quay Số", "Túi\nTạm Thời", "Xóa\nTúi");
    }

    public static void showMenuQuaySo(Player player) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|========= Vòng Quay May Mắn =========").append("\n");
        stringBuilder.append("|5|Điểm may mắn của bạn là : ").append(player.luckyPoolPlayer.totalLuckyPoint).append("\n");
        stringBuilder.append("|7|Tỷ lệ ra dòng Sử Thi : ").append((player.luckyPoolPlayer.totalLuckyPoint / 10000) * 100).append("%").append("\n");
        stringBuilder.append("|7|Tỷ lệ ra dòng Huyền Thoại : ").append((player.luckyPoolPlayer.totalLuckyPoint / 100000) * 100).append("%").append("\n");
        stringBuilder.append("|5|Mỗi lần quay sẽ tăng tỷ lệ ra dòng hiếm,vật phẩm quay được sẽ nằm trong túi tạm thời").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFRM_QSMM, -1, stringBuilder.toString(), "Quay\n 1 lần", "Quay\n 10 lần");
    }

    public static void showTuiTamThoi(Player player) {
        if (player.luckyPoolPlayer.itemBags.size() == 0) {
            Service.gI().sendThongBao(player, "Không có vật phẩm nào trong rương");
            return;
        }
        ShopServiceNew.gI().opendShop(player, "ITEM_LUCKY_POOL", true);
    }

    public static void removeAllItemInBag(Player player) {
        int size = player.luckyPoolPlayer.itemBags.size();
        for (int i = 0; i < size; i++) {
            player.luckyPoolPlayer.itemBags.set(i, ItemService.gI().createItemNull());
        }
        player.luckyPoolPlayer.itemBags.clear();
        long hn = size * 10000L;
        player.inventory.ruby += hn;
        Service.gI().sendMoney(player);
        Service.gI().sendThongBaoOK(player, "Đã xóa hết vật phẩm trong túi");
    }

    public static void prepeareForRoll(int time, Player player) {
        Item item = InventoryServiceNew.gI().findItemBag(player, 1378);
        if (item == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy bùa zeno trong túi");
            return;
        }
        if (item.quantity < time) {
            Service.gI().sendThongBao(player, "Không đủ số lượng bùa trong túi");
            return;
        }
        // auto add to ruong phu
        if (player.luckyPoolPlayer.itemBags.size() + time > LuckyPoolPlayer.MAX_COUNT_ITEM) {
            Service.gI().sendThongBao(player, "Rương phụ đã đầy hãy nhận thưởng trước đi nào");
            return;
        }
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, time);
        InventoryServiceNew.gI().sendItemBags(player);
        LuckyPool.getI().rollInPool(player, time);
    }

    public void initLuckyItem() {
        // add rare item
        // rare item includes linh thu ,
        luckyPoolRareItems = Manager.ITEM_TEMPLATES.stream().filter(it -> it.type == 13 || it.type == 33 || it.type == 29).map(it -> it.id) // only get id
                .collect(Collectors.toList());
        luckyPoolEpicItems = Manager.ITEM_TEMPLATES.stream().filter(it -> it.type == 5 || it.type == 72 || it.type == 23 || it.type == 24).map(it -> it.id) // only get id
                .collect(Collectors.toList());
        luckyPoolLeagueItems = Manager.ITEM_TEMPLATES.stream().filter(it -> it.type < 5 || it.type == 32).map(it -> it.id) // only get id
                .collect(Collectors.toList());
        luckyPoolSpecialItem = Arrays.asList(211, 380, 457, 542, 668, 573, 674, 1232, 1233, 1234, 2003, 2004, 2005);
    }

    public void ratioLeagueItemOption(Item item) {
        int slDong = 3;
        if (Util.isTrue(20, 100)) {
            slDong = 4;
        } else if (Util.isTrue(10, 100)) {
            slDong = 5;
        }
        int id;

        for (int i = 0; i < slDong; i++) {
            id = Util.nextInt(0, OPTION_LEAGUE.length - 1);
            Item.ItemOption itemOption = new Item.ItemOption(OPTION_LEAGUE[id], getParam(id, TYPE_LEAGUE));
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
                return 50 * type;
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
            if (Util.isTrue(tyLeRoll, 10000)) {
                // item league
                // khi roll ra league se tu dong reset point
                player.luckyPoolPlayer.totalLuckyPoint = 0;
                Item item = ItemService.gI().createNewItem(luckyPoolLeagueItems.get(Util.nextInt(0, luckyPoolLeagueItems.size() - 1)), 1);
                // ratio option for this
                ratioLeagueItemOption(item);
                // add to bag and continue
                player.luckyPoolPlayer.addItemToBag(item);
                Service.gI().sendThongBao(player, "Chúc mừng bạn nhận được x1" + item.template.name);
                continue;
            }
            if (Util.isTrue(tyLeRoll, 1000)) {
                Item item = ItemService.gI().createNewItem(luckyPoolEpicItems.get(Util.nextInt(0, luckyPoolEpicItems.size() - 1)), 1);
                // ratio option for this
                ratioEpicItemOption(item);
                // add to bag and continue
                player.luckyPoolPlayer.addItemToBag(item);
                player.luckyPoolPlayer.totalLuckyPoint += 1;
                if (player.luckyPoolPlayer.totalLuckyPoint > 1000) {
                    player.luckyPoolPlayer.totalLuckyPoint = 1000;
                }
                Service.gI().sendThongBao(player, "Chúc mừng bạn nhận được x1" + item.template.name);
                continue;
            }

            if (Util.isTrue(tyLeRoll, 500)) {
                Item item = ItemService.gI().createNewItem(luckyPoolSpecialItem.get(Util.nextInt(0, luckyPoolSpecialItem.size() - 1)), 1);
                // add to bag and continue
                item.itemOptions.add(new Item.ItemOption(30, 0));
                player.luckyPoolPlayer.addItemToBag(item);
                player.luckyPoolPlayer.totalLuckyPoint += 1;
                Service.gI().sendThongBao(player, "Chúc mừng bạn nhận được x1" + item.template.name);
                continue;
            }
            Item item = ItemService.gI().createNewItem(luckyPoolRareItems.get(Util.nextInt(0, luckyPoolRareItems.size() - 1)), 1);
            // add to bag and continue
            item.itemOptions.add(new Item.ItemOption(30, 0));
            player.luckyPoolPlayer.addItemToBag(item);
            player.luckyPoolPlayer.totalLuckyPoint += 1;
            if (player.luckyPoolPlayer.totalLuckyPoint > 1000) {
                player.luckyPoolPlayer.totalLuckyPoint = 1000;
            }
            Service.gI().sendThongBao(player, "Chúc mừng bạn nhận được x1" + item.template.name);
        }
        Service.gI().sendThongBao(player, "Quay số hoàn thành");
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
            Item.ItemOption itemOption = new Item.ItemOption(OPTION_EPIC[id], getParam(id, TYPE_EPIC));
            item.itemOptions.add(itemOption);
        }
    }
}
