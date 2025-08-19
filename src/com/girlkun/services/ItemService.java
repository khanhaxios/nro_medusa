package com.girlkun.services;

import com.girlkun.models.Template;
import com.girlkun.models.Template.ItemOptionTemplate;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.models.shop.ItemShop;
import com.girlkun.server.Manager;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;

import java.util.*;
import java.util.stream.Collectors;

public class ItemService {

    int[] optionsRandom = new int[]{3, 4, 8, 24, 25, 26, 29, 82, 83, 88, 95, 96, 97, 101};
    int[] optionRandomVip = new int[]{5, 14, 22, 23, 50};
    int[] idsKichHoat = new int[]{127, 128, 129, 130, 131, 132, 133, 134, 135, 189, 190, 191, 213, 214, 215, 224, 225, 226, 235, 236, 237, 241, 242, 243};

    private static ItemService i;

    public static ItemService gI() {
        if (i == null) {
            i = new ItemService();
        }
        return i;
    }

    public short getItemIdByIcon(short IconID) {
        for (int i = 0; i < Manager.ITEM_TEMPLATES.size(); i++) {
            if (Manager.ITEM_TEMPLATES.get(i).iconID == IconID) {
                return Manager.ITEM_TEMPLATES.get(i).id;
            }
        }
        return -1;
    }

    public Item createItemNull() {
        Item item = new Item();
        return item;
    }

    public Item createItemFromItemShop(ItemShop itemShop) {
        if ("BILL".equals(itemShop.tabShop.shop.tagName) || "HUY_DIET".equals(itemShop.tabShop.shop.tagName)) {
            Item item = new Item();
            item.template = itemShop.temp;
            item.quantity = 1;
            item.content = item.getContent();
            item.info = item.getInfo();

            for (ItemOption io : itemShop.options) {
                item.itemOptions.add(new ItemOption(io));

                item.itemOptions.forEach(c -> {
                    if (c.optionTemplate.id != 21 && c.optionTemplate.id != 30) {
                        if (Util.nextInt(0, 500) < 300) {
                            c.param = c.param + ((c.param * Util.nextInt(1, 5)) / 100);
                        } else if (Util.nextInt(0, 500) < 450) {
                            c.param = c.param + ((c.param * Util.nextInt(1, 10)) / 100);
                        } else {
                            c.param = c.param + ((c.param * Util.nextInt(1, 15)) / 100);
                        }
                    }
                });

            }

            return item;
        } else {
            Item item = new Item();
            item.template = itemShop.temp;
            item.quantity = 1;
            item.content = item.getContent();
            item.info = item.getInfo();
            for (ItemOption io : itemShop.options) {
                item.itemOptions.add(new ItemOption(io));
            }
            return item;
        }
    }

    public Item copyItem(Item item) {
        Item it = new Item();
        it.itemOptions = new ArrayList<>();
        it.template = item.template;
        it.info = item.info;
        it.content = item.content;
        it.quantity = item.quantity;
        it.createTime = item.createTime;
        for (ItemOption io : item.itemOptions) {
            it.itemOptions.add(new ItemOption(io));
        }
        return it;
    }

    public Item createNewItem(short tempId) {
        return createNewItem(tempId, 1);
    }

    public Item otpts(short tempId) {
        return otpts(tempId, 1);
    }

    public Item createNewItem(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createNewItem(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createNewItem(short tempId, int quantity, boolean locked) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        if (locked) {
            item.itemOptions.add(new ItemOption(30, 0));
        }
        return item;
    }

    public Item otpts(short tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.createTime = System.currentTimeMillis();
        if (item.template.type == 0) {
            item.itemOptions.add(new ItemOption(21, 120));
            item.itemOptions.add(new ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (item.template.type == 1) {
            item.itemOptions.add(new ItemOption(21, 120));
            item.itemOptions.add(new ItemOption(22, Util.nextInt(150, 200)));
        }
        if (item.template.type == 2) {
            item.itemOptions.add(new ItemOption(21, 120));
            item.itemOptions.add(new ItemOption(0, Util.nextInt(18000, 20000)));
        }
        if (item.template.type == 3) {
            item.itemOptions.add(new ItemOption(21, 120));
            item.itemOptions.add(new ItemOption(23, Util.nextInt(150, 200)));
        }
        if (item.template.type == 4) {
            item.itemOptions.add(new ItemOption(21, 120));
            item.itemOptions.add(new ItemOption(14, Util.nextInt(20, 25)));
        }
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemSetKichHoat(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemDoHuyDiet(int tempId, int quantity) {
        Item item = new Item();
        item.template = getTemplate(tempId);
        item.quantity = quantity;
        item.itemOptions = createItemNull().itemOptions;
        item.createTime = System.currentTimeMillis();
        item.content = item.getContent();
        item.info = item.getInfo();
        return item;
    }

    public Item createItemFromItemMap(ItemMap itemMap) {
        Item item = createNewItem(itemMap.itemTemplate.id, itemMap.quantity);
        item.itemOptions = itemMap.options;
        return item;
    }

    public ItemOptionTemplate getItemOptionTemplate(int id) {
        return Manager.ITEM_OPTION_TEMPLATES.get(id);
    }

    public Template.ItemTemplate getTemplate(int id) {
        return Manager.ITEM_TEMPLATES.stream().filter(t -> t.id == id).findFirst().orElse(null);
    }

    public boolean isItemActivation(Item item) {
        return item.itemOptions.stream()
                .anyMatch(opt -> Arrays.stream(idsKichHoat).anyMatch(id -> id == opt.optionTemplate.id));
    }

    public int getPercentTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                    return 10;
                case 530:
                case 535:
                    return 20;
                case 531:
                case 536:
                    return 30;
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    public boolean isTrainArmor(Item item) {
        if (item != null) {
            switch (item.template.id) {
                case 529:
                case 534:
                case 530:
                case 535:
                case 531:
                case 536:
                    return true;
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean isOutOfDateTime(Item item) {
        if (item != null) {
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 93) {
                    int dayPass = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPass != 0) {
                        io.param -= dayPass;
                        if (io.param <= 0) {
                            return true;
                        } else {
                            item.createTime = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isOutOfDateTimeVV(Item item) {
        if (item != null) {
            for (ItemOption io : item.itemOptions) {
                if (io.optionTemplate.id == 234) {
                    int dayPass = (int) TimeUtil.diffDate(new Date(), new Date(item.createTime), TimeUtil.DAY);
                    if (dayPass != 0) {
                        io.param -= dayPass;
                        if (io.param <= 0) {
                            return true;
                        } else {
                            item.createTime = System.currentTimeMillis();
                        }
                    }
                }
            }
        }
        return false;
    }

    public void OpenSKH(Player player, int itemUseId, int select) throws Exception {
        if (select < 0 || select > 4) {
            return;
        }
        Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, itemUseId);
        int[][] items = {{0, 6, 21, 27, 12}, {1, 7, 22, 28, 12}, {2, 8, 23, 29, 12}};
        int[][] options = {{128, 129, 127}, {130, 131, 132}, {133, 135, 134}};
        int skhv1 = 25;// ti le
        int skhv2 = 35;//ti le
        int skhc = 40;//ti le
        int skhId = -1;

        int rd = Util.nextInt(1, 100);
        if (rd <= skhv1) {
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) {
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) {
            skhId = 2;
        }
        Item item = null;
        switch (itemUseId) {
            case 2000:
                item = itemSKH(items[0][select], options[0][skhId]);
                break;
            case 2001:
                item = itemSKH(items[1][select], options[1][skhId]);
                break;
            case 2002:
                item = itemSKH(items[2][select], options[2][skhId]);
                break;
        }
        if (item != null && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public int randomSKHId(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[][] options = {{128, 129, 127}, {130, 131, 132}, {133, 135, 134}};
        int skhv1 = 25;
        int skhv2 = 35;
        int skhc = 40;
        int skhId = -1;
        int rd = Util.nextInt(1, 100);
        if (rd <= skhv1) {
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) {
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) {
            skhId = 2;
        }
        return options[gender][skhId];
    }

    public int randomSKHId() {
        int gender = Util.nextInt(0, 2);
        int[][] options = {{128, 129, 127}, {130, 131, 132}, {133, 135, 134}};
        int skhv1 = 25;
        int skhv2 = 35;
        int skhc = 40;
        int skhId = -1;
        int rd = Util.nextInt(1, 100);
        if (rd <= skhv1) {
            skhId = 0;
        } else if (rd <= skhv1 + skhv2) {
            skhId = 1;
        } else if (rd <= skhv1 + skhv2 + skhc) {
            skhId = 2;
        }
        return options[gender][skhId];
    }

    public int randomSKHThanhTon(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[] options = {189, 190, 191};
        return options[gender];
    }

    public int randomSKHNguyenThuy(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[] options = {213, 214, 215};
        return options[gender];
    }

    public int randomSKHJiren(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[] options = {235, 236, 237};
        return options[gender];
    }

    public int randomSKHThongKho(byte gender) {
        if (gender == 3) {
            gender = 2;
        }
        int[] options = {224, 225, 226};
        return options[gender];
    }

    public int optionIdSKHThanhTon(int skhId) {
        switch (skhId) {
            case 189: //Set Thánh tôn trái dất
                return 199;
            case 190: //Set Thánh tôn namec
                return 200;
            case 191: //Set Thánh tôn xayda
                return 201;
        }
        return 0;
    }

    public int optionIdSKHJiren(int skhId) {
        switch (skhId) {
            case 235: //Set Thánh tôn trái dất
                return 238;
            case 236: //Set Thánh tôn namec
                return 239;
            case 237: //Set Thánh tôn xayda
                return 240;
        }
        return 0;
    }

    public int optionIdSKHNguyenThuy(int skhId) {
        switch (skhId) {
            case 213: //Set NguyenThuy trái dất
                return 216;
            case 214: //Set NguyenThuyn namec
                return 217;
            case 215: //Set NguyenThuy xayda
                return 218;
        }
        return 0;
    }

    public Item createRandomDoThongKho() {
        Item item = null;
        short itemId;
        itemId = Manager.DoThongKho[Util.nextInt(0, Manager.DoThongKho.length - 1)];
        item = Util.ratiItemThongKho(itemId);
        item.itemOptions.add(new ItemOption(260, 8));
        return randomOption(item);
    }

    public Item createRandomDoJiren() {
        Item item = null;
        short itemId;
        itemId = Manager.setJiren[Util.nextInt(0, 4)];
        item = Util.ratiItemSKHJiren(itemId);
        item.itemOptions.add(new ItemOption(260, 10));
        return randomOption(item);
    }

    public Item createRandomDoGoku() {
        Item item = null;
        short itemId;
        itemId = Manager.setGokuUI[Util.nextInt(0, 4)];
        item = Util.ratiItemSKHGokuUI(itemId);
        item.itemOptions.add(new ItemOption(260, 12));
        return randomOption(item);
    }

    private int optionSKHGK(int skhId) {
        switch (skhId) {
            case 241: //Set Thánh tôn trái dất
                return 244;
            case 242: //Set Thánh tôn namec
                return 245;
            case 243: //Set Thánh tôn xayda
                return 246;
        }
        return 0;
    }

    private int randomSKHGK(byte nextInt) {
        if (nextInt == 3) {
            nextInt = 2;
        }
        int[] options = {241, 242, 243};
        return options[nextInt];
    }

    public int optionIdSKHThongKho(int skhId) {
        switch (skhId) {
            case 224: //Set NguyenThuy trái dất
                return 227;
            case 225: //Set NguyenThuyn namec
                return 228;
            case 226: //Set NguyenThuy xayda
                return 229;
        }
        return 0;
    }

    public void OpenDHD(Player player, int itemUseId, int select) throws Exception {
        if (select < 0 || select > 4) {
            return;
        }
        Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, itemUseId);
        int gender = -1;
        switch (itemUseId) {
            case 2003: //td
                gender = 0;
                break;
            case 2004: //xd
                gender = 2;
                break;
            case 2005: //nm
                gender = 1;
                break;
        }
        int[][] items = {{650, 651, 657, 658, 656}, {652, 653, 659, 660, 656}, {654, 655, 661, 662, 656}}; //td, namec,xd
        Item item = randomCS_DHD(items[gender][select], gender);

        if (item != null && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public void OpenItem736(Player player, Item itemUse) {
        try {
            if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
                return;
            }
            short[] icon = new short[2];
            int rd = Util.nextInt(1, 100);
            int rac = 50;
            int ruby = 20;
            int dbv = 10;
            int vb = 10;
            int bh = 5;
            int ct = 5;
            Item item = randomRac();
            if (rd <= rac) {
                item = randomRac();
            } else if (rd <= rac + ruby) {
                item = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
            } else if (rd <= rac + ruby + dbv) {
                item = daBaoVe();
            } else if (rd <= rac + ruby + dbv + vb) {
                item = vanBay2011(true);
            } else if (rd <= rac + ruby + dbv + vb + bh) {
                item = phuKien2011(true);
            } else if (rd <= rac + ruby + dbv + vb + bh + ct) {
                item = caitrang2011(true);
            }
            if (item.template.id == 861) {
                item.quantity = Util.nextInt(10, 30);
            }
            icon[0] = itemUse.template.iconID;
            icon[1] = item.template.iconID;
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            player.inventory.event++;
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
            CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
        } catch (Exception e) {
            System.out.println("zxcv");
        }
    }

    public void settaiyoken(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new ItemOption(127, 1));
        quan.itemOptions.add(new ItemOption(127, 1));
        gang.itemOptions.add(new ItemOption(127, 1));
        giay.itemOptions.add(new ItemOption(127, 1));
        nhan.itemOptions.add(new ItemOption(127, 1));
        ao.itemOptions.add(new ItemOption(139, 1));
        quan.itemOptions.add(new ItemOption(139, 1));
        gang.itemOptions.add(new ItemOption(139, 1));
        giay.itemOptions.add(new ItemOption(139, 1));
        nhan.itemOptions.add(new ItemOption(139, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setgenki(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new ItemOption(128, 1));
        quan.itemOptions.add(new ItemOption(128, 1));
        gang.itemOptions.add(new ItemOption(128, 1));
        giay.itemOptions.add(new ItemOption(128, 1));
        nhan.itemOptions.add(new ItemOption(128, 1));
        ao.itemOptions.add(new ItemOption(140, 1));
        quan.itemOptions.add(new ItemOption(140, 1));
        gang.itemOptions.add(new ItemOption(140, 1));
        giay.itemOptions.add(new ItemOption(140, 1));
        nhan.itemOptions.add(new ItemOption(140, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setkamejoko(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1048);
        Item quan = ItemService.gI().otpts((short) 1051);
        Item gang = ItemService.gI().otpts((short) 1054);
        Item giay = ItemService.gI().otpts((short) 1057);
        Item nhan = ItemService.gI().otpts((short) 1060);
        ao.itemOptions.add(new ItemOption(129, 1));
        quan.itemOptions.add(new ItemOption(129, 1));
        gang.itemOptions.add(new ItemOption(129, 1));
        giay.itemOptions.add(new ItemOption(129, 1));
        nhan.itemOptions.add(new ItemOption(129, 1));
        ao.itemOptions.add(new ItemOption(141, 1));
        quan.itemOptions.add(new ItemOption(141, 1));
        gang.itemOptions.add(new ItemOption(141, 1));
        giay.itemOptions.add(new ItemOption(141, 1));
        nhan.itemOptions.add(new ItemOption(141, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setgodki(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new ItemOption(130, 1));
        quan.itemOptions.add(new ItemOption(130, 1));
        gang.itemOptions.add(new ItemOption(130, 1));
        giay.itemOptions.add(new ItemOption(130, 1));
        nhan.itemOptions.add(new ItemOption(130, 1));
        ao.itemOptions.add(new ItemOption(142, 1));
        quan.itemOptions.add(new ItemOption(142, 1));
        gang.itemOptions.add(new ItemOption(142, 1));
        giay.itemOptions.add(new ItemOption(142, 1));
        nhan.itemOptions.add(new ItemOption(142, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setgoddam(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new ItemOption(131, 1));
        quan.itemOptions.add(new ItemOption(131, 1));
        gang.itemOptions.add(new ItemOption(131, 1));
        giay.itemOptions.add(new ItemOption(131, 1));
        nhan.itemOptions.add(new ItemOption(131, 1));
        ao.itemOptions.add(new ItemOption(143, 1));
        quan.itemOptions.add(new ItemOption(143, 1));
        gang.itemOptions.add(new ItemOption(143, 1));
        giay.itemOptions.add(new ItemOption(143, 1));
        nhan.itemOptions.add(new ItemOption(143, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setsummon(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1049);
        Item quan = ItemService.gI().otpts((short) 1052);
        Item gang = ItemService.gI().otpts((short) 1055);
        Item giay = ItemService.gI().otpts((short) 1058);
        Item nhan = ItemService.gI().otpts((short) 1061);
        ao.itemOptions.add(new ItemOption(132, 1));
        quan.itemOptions.add(new ItemOption(132, 1));
        gang.itemOptions.add(new ItemOption(132, 1));
        giay.itemOptions.add(new ItemOption(132, 1));
        nhan.itemOptions.add(new ItemOption(132, 1));
        ao.itemOptions.add(new ItemOption(144, 1));
        quan.itemOptions.add(new ItemOption(144, 1));
        gang.itemOptions.add(new ItemOption(144, 1));
        giay.itemOptions.add(new ItemOption(144, 1));
        nhan.itemOptions.add(new ItemOption(144, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setgodgalick(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new ItemOption(133, 1));
        quan.itemOptions.add(new ItemOption(133, 1));
        gang.itemOptions.add(new ItemOption(133, 1));
        giay.itemOptions.add(new ItemOption(133, 1));
        nhan.itemOptions.add(new ItemOption(133, 1));
        ao.itemOptions.add(new ItemOption(136, 1));
        quan.itemOptions.add(new ItemOption(136, 1));
        gang.itemOptions.add(new ItemOption(136, 1));
        giay.itemOptions.add(new ItemOption(136, 1));
        nhan.itemOptions.add(new ItemOption(136, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setmonkey(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new ItemOption(134, 1));
        quan.itemOptions.add(new ItemOption(134, 1));
        gang.itemOptions.add(new ItemOption(134, 1));
        giay.itemOptions.add(new ItemOption(134, 1));
        nhan.itemOptions.add(new ItemOption(134, 1));
        ao.itemOptions.add(new ItemOption(137, 1));
        quan.itemOptions.add(new ItemOption(137, 1));
        gang.itemOptions.add(new ItemOption(137, 1));
        giay.itemOptions.add(new ItemOption(137, 1));
        nhan.itemOptions.add(new ItemOption(137, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public void setgodhp(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1105);
        Item ao = ItemService.gI().otpts((short) 1050);
        Item quan = ItemService.gI().otpts((short) 1053);
        Item gang = ItemService.gI().otpts((short) 1056);
        Item giay = ItemService.gI().otpts((short) 1059);
        Item nhan = ItemService.gI().otpts((short) 1062);
        ao.itemOptions.add(new ItemOption(135, 1));
        quan.itemOptions.add(new ItemOption(135, 1));
        gang.itemOptions.add(new ItemOption(135, 1));
        giay.itemOptions.add(new ItemOption(135, 1));
        nhan.itemOptions.add(new ItemOption(135, 1));
        ao.itemOptions.add(new ItemOption(138, 1));
        quan.itemOptions.add(new ItemOption(138, 1));
        gang.itemOptions.add(new ItemOption(138, 1));
        giay.itemOptions.add(new ItemOption(138, 1));
        nhan.itemOptions.add(new ItemOption(138, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set thiên sứ ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    //////////////
    public void setsencon(Player player) throws Exception {
        Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1394);
        Item ao = ItemService.gI().otpts((short) 1389);
        Item quan = ItemService.gI().otpts((short) 1390);
        Item gang = ItemService.gI().otpts((short) 1391);
        Item giay = ItemService.gI().otpts((short) 1392);
        Item nhan = ItemService.gI().otpts((short) 1393);
        ao.itemOptions.add(new ItemOption(135, 1));
        quan.itemOptions.add(new ItemOption(135, 1));
        gang.itemOptions.add(new ItemOption(135, 1));
        giay.itemOptions.add(new ItemOption(135, 1));
        nhan.itemOptions.add(new ItemOption(135, 1));
        ao.itemOptions.add(new ItemOption(138, 1));
        quan.itemOptions.add(new ItemOption(138, 1));
        gang.itemOptions.add(new ItemOption(138, 1));
        giay.itemOptions.add(new ItemOption(138, 1));
        nhan.itemOptions.add(new ItemOption(138, 1));
        //NOI TAI HOA KHI
        ao.itemOptions.add(new ItemOption(134, 1));
        quan.itemOptions.add(new ItemOption(134, 1));
        gang.itemOptions.add(new ItemOption(134, 1));
        giay.itemOptions.add(new ItemOption(134, 1));
        nhan.itemOptions.add(new ItemOption(134, 1));
        ao.itemOptions.add(new ItemOption(134, 1));
        quan.itemOptions.add(new ItemOption(137, 1));
        gang.itemOptions.add(new ItemOption(137, 1));
        giay.itemOptions.add(new ItemOption(137, 1));
        nhan.itemOptions.add(new ItemOption(13, 71));
        //kakarot
        ao.itemOptions.add(new ItemOption(133, 1));
        quan.itemOptions.add(new ItemOption(133, 1));
        gang.itemOptions.add(new ItemOption(133, 1));
        giay.itemOptions.add(new ItemOption(133, 1));
        nhan.itemOptions.add(new ItemOption(133, 1));
        ao.itemOptions.add(new ItemOption(136, 1));
        quan.itemOptions.add(new ItemOption(136, 1));
        gang.itemOptions.add(new ItemOption(136, 1));
        giay.itemOptions.add(new ItemOption(136, 1));
        nhan.itemOptions.add(new ItemOption(136, 1));
        //kaioken
        ao.itemOptions.add(new ItemOption(127, 1));
        quan.itemOptions.add(new ItemOption(127, 1));
        gang.itemOptions.add(new ItemOption(127, 1));
        giay.itemOptions.add(new ItemOption(127, 1));
        nhan.itemOptions.add(new ItemOption(127, 1));
        ao.itemOptions.add(new ItemOption(139, 1));
        quan.itemOptions.add(new ItemOption(139, 1));
        gang.itemOptions.add(new ItemOption(139, 1));
        giay.itemOptions.add(new ItemOption(139, 1));
        nhan.itemOptions.add(new ItemOption(139, 1));
        //lien hoan
        ao.itemOptions.add(new ItemOption(131, 1));
        quan.itemOptions.add(new ItemOption(131, 1));
        gang.itemOptions.add(new ItemOption(131, 1));
        giay.itemOptions.add(new ItemOption(131, 1));
        nhan.itemOptions.add(new ItemOption(131, 1));
        ao.itemOptions.add(new ItemOption(143, 1));
        quan.itemOptions.add(new ItemOption(143, 1));
        gang.itemOptions.add(new ItemOption(143, 1));
        giay.itemOptions.add(new ItemOption(143, 1));
        nhan.itemOptions.add(new ItemOption(143, 1));
        //
        //kame
        ao.itemOptions.add(new ItemOption(129, 1));
        quan.itemOptions.add(new ItemOption(129, 1));
        gang.itemOptions.add(new ItemOption(129, 1));
        giay.itemOptions.add(new ItemOption(129, 1));
        nhan.itemOptions.add(new ItemOption(129, 1));
        ao.itemOptions.add(new ItemOption(141, 1));
        quan.itemOptions.add(new ItemOption(141, 1));
        gang.itemOptions.add(new ItemOption(141, 1));
        giay.itemOptions.add(new ItemOption(141, 1));
        nhan.itemOptions.add(new ItemOption(141, 1));
        //
        //ki
        ao.itemOptions.add(new ItemOption(130, 1));
        quan.itemOptions.add(new ItemOption(130, 1));
        gang.itemOptions.add(new ItemOption(130, 1));
        giay.itemOptions.add(new ItemOption(130, 1));
        nhan.itemOptions.add(new ItemOption(130, 1));
        ao.itemOptions.add(new ItemOption(142, 1));
        quan.itemOptions.add(new ItemOption(142, 1));
        gang.itemOptions.add(new ItemOption(142, 1));
        giay.itemOptions.add(new ItemOption(142, 1));
        nhan.itemOptions.add(new ItemOption(142, 1));
        ao.itemOptions.add(new ItemOption(30, 0));
        quan.itemOptions.add(new ItemOption(30, 0));
        gang.itemOptions.add(new ItemOption(30, 0));
        giay.itemOptions.add(new ItemOption(30, 0));
        nhan.itemOptions.add(new ItemOption(30, 0));
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            InventoryServiceNew.gI().addItemBag(player, ao);
            InventoryServiceNew.gI().addItemBag(player, quan);
            InventoryServiceNew.gI().addItemBag(player, gang);
            InventoryServiceNew.gI().addItemBag(player, giay);
            InventoryServiceNew.gI().addItemBag(player, nhan);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được set sên con ");
            InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
            InventoryServiceNew.gI().sendItemBags(player);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }
    //////////////

    public Item itemSKH(int itemId, int skhId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.addAll(ItemService.gI().getListOptionItemShop((short) itemId));
            item.itemOptions.add(new ItemOption(skhId, 1));
            item.itemOptions.add(new ItemOption(optionIdSKH(skhId), 1));
            item.itemOptions.add(new ItemOption(30, 1));
        }
        return item;
    }

    public int optionItemSKH(int typeItem) {
        switch (typeItem) {
            case 0:
                return 47;
            case 1:
                return 6;
            case 2:
                return 0;
            case 3:
                return 7;
            default:
                return 14;
        }
    }

    public int pagramItemSKH(int typeItem) {
        switch (typeItem) {
            case 0:
            case 2:
                return Util.nextInt(5);
            case 1:
            case 3:
                return Util.nextInt(20, 30);
            default:
                return Util.nextInt(3);
        }
    }

    public int optionIdSKH(int skhId) {
        switch (skhId) {
            case 127: //Set Viet Taiyoken
                return 139;
            case 128: //Set Viet Genki
                return 140;
            case 129: //Set Viet Kamejoko
                return 141;
            case 130: //Set Viet KI
                return 142;
            case 131: //Set Viet Dame
                return 143;
            case 132: //Set Viet Summon
                return 144;
            case 133: //Set Viet Galick
                return 136;
            case 134: //Set Viet Monkey
                return 137;
            case 135: //Set Viet HP
                return 138;
            case 213:
                return 216;
            case 214:
                return 217;
            case 215:
                return 218;
            case 224:
                return 227;
            case 225:
                return 228;
            case 226:
                return 229;
            case 189:
                return 199;
            case 190:
                return 200;
            case 191:
                return 201;
            case 235: //Set Jiren TD
                return 238;
            case 236: //Set Jiren NM
                return 239;
            case 237: //Set Jiren XD
                return 240;
            case 241: //Set Goku UI TD
                return 244;
            case 242: //Set Goku UI NM
                return 245;
            case 243: //Set Goku UI XD
                return 246;
        }
        return 0;
    }

    public Item itemDHD(int itemId, int dhdId) {
        Item item = createItemSetKichHoat(itemId, 1);
        if (item != null) {
            item.itemOptions.add(new ItemOption(dhdId, 1));
            item.itemOptions.add(new ItemOption(optionIdDHD(dhdId), 1));
            item.itemOptions.add(new ItemOption(30, 1));
        }
        return item;
    }

    public int optionIdDHD(int skhId) {
        switch (skhId) {
            case 127: //Set Viet Taiyoken
                return 139;
            case 128: //Set Viet Genki
                return 140;
            case 129: //Set Viet Kamejoko
                return 141;
            case 130: //Set Viet KI
                return 142;
            case 131: //Set Viet Dame
                return 143;
            case 132: //Set Viet Summon
                return 144;
            case 133: //Set Viet Galick
                return 136;
            case 134: //Set Viet Monkey
                return 137;
            case 135: //Set Viet HP
                return 138;
        }
        return 0;
    }

    public Item randomCS_DHD(int itemId, int gender) {
        Item it = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(650, 652, 654);
        List<Integer> quan = Arrays.asList(651, 653, 655);
        List<Integer> gang = Arrays.asList(657, 659, 661);
        List<Integer> giay = Arrays.asList(658, 660, 662);
        int nhd = 656;
        if (ao.contains(itemId)) {
            it.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(1001) + 1800))); // áo từ 1800-2800 giáp
        }
        if (quan.contains(itemId)) {
            it.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(16) + 85))); // hp 85-100k
        }
        if (gang.contains(itemId)) {
            it.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(1500) + 8500))); // 8500-10000
        }
        if (giay.contains(itemId)) {
            it.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(11) + 80))); // ki 80-90k
        }
        if (nhd == itemId) {
            it.itemOptions.add(new ItemOption(14, new Random().nextInt(3) + 17)); //chí mạng 17-19%
        }
        it.itemOptions.add(new ItemOption(21, 80));// yêu cầu sm 80 tỉ
        it.itemOptions.add(new ItemOption(30, 1));// ko the gd
        return it;
    }

    //Cải trang sự kiện 20/11
    public Item caitrang2011(boolean rating) {
        Item item = createItemSetKichHoat(680, 1);
        item.itemOptions.add(new ItemOption(76, 1));//VIP
        item.itemOptions.add(new ItemOption(77, 28));//hp 28%
        item.itemOptions.add(new ItemOption(103, 25));//ki 25%
        item.itemOptions.add(new ItemOption(147, 24));//sd 26%
        item.itemOptions.add(new ItemOption(117, 18));//Đẹp + 18% sd
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new ItemOption(93, new Random().nextInt(3) + 1));//hsd
        }
        return item;
    }

    //610 - bong hoa
    //Phụ kiện bó hoa 20/11
    public Item phuKien2011(boolean rating) {
        Item item = createItemSetKichHoat(954, 1);
        item.itemOptions.add(new ItemOption(77, new Random().nextInt(5) + 5));
        item.itemOptions.add(new ItemOption(103, new Random().nextInt(5) + 5));
        item.itemOptions.add(new ItemOption(147, new Random().nextInt(5) + 5));
        if (Util.isTrue(1, 100)) {
            item.itemOptions.get(Util.nextInt(item.itemOptions.size() - 1)).param = 10;
        }
        item.itemOptions.add(new ItemOption(30, 1));//ko the gd
        if (Util.isTrue(995, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new ItemOption(93, new Random().nextInt(3) + 1));//hsd
        }
        return item;
    }

    public Item vanBay2011(boolean rating) {
        Item item = createItemSetKichHoat(795, 1);
        item.itemOptions.add(new ItemOption(89, 1));
        item.itemOptions.add(new ItemOption(30, 1));//ko the gd
        if (Util.isTrue(950, 1000) && rating) {// tỉ lệ ra hsd
            item.itemOptions.add(new ItemOption(93, new Random().nextInt(3) + 1));//hsd
        }
        return item;
    }

    public Item daBaoVe() {
        Item item = createItemSetKichHoat(987, 1);
        item.itemOptions.add(new ItemOption(30, 1));//ko the gd
        return item;
    }

    public Item randomRac() {
        short[] racs = {20, 19, 18, 17};
        Item item = createItemSetKichHoat(racs[Util.nextInt(racs.length - 1)], 1);
        if (optionRac(item.template.id) != 0) {
            item.itemOptions.add(new ItemOption(optionRac(item.template.id), 1));
        }
        return item;
    }

    public byte optionRac(short itemId) {
        switch (itemId) {
            case 220:
                return 71;
            case 221:
                return 70;
            case 222:
                return 69;
            case 224:
                return 67;
            case 223:
                return 68;
            default:
                return 0;
        }
    }

    public void openBoxVip(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 2 ô trống hành trang");
            return;
        }
        if (player.inventory.event < 3000) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ bông...");
            return;
        }
        Item item;
        if (Util.isTrue(45, 100)) {
            item = caitrang2011(false);
        } else {
            item = phuKien2011(false);
        }
        short[] icon = new short[2];
        icon[0] = 6983;
        icon[1] = item.template.iconID;
        InventoryServiceNew.gI().addItemBag(player, item);
        InventoryServiceNew.gI().sendItemBags(player);
        player.inventory.event -= 3000;
        Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
        CombineServiceNew.gI().sendEffectOpenItem(player, icon[0], icon[1]);
    }

    public void giaobong(Player player, int quantity) {
        if (quantity > 10000) {
            return;
        }
        try {
            Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 610);
            if (itemUse.quantity < quantity) {
                Service.getInstance().sendThongBao(player, "Bạn không đủ bông...");
                return;
            }
            InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, quantity);
            Item item = createItemSetKichHoat(736, (quantity / 100));
            item.itemOptions.add(new ItemOption(30, 1));//ko the gd
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được x" + (quantity / 100) + " " + item.template.name);
        } catch (Exception e) {
            Service.getInstance().sendThongBao(player, "Bạn không đủ bông...");
        }
    }

    public Item PK_WC(int itemId) {
        Item phukien = createItemSetKichHoat(itemId, 1);
        int co = 983;
        int cup = 982;
        int bong = 966;
        if (cup == itemId) {
            phukien.itemOptions.add(new ItemOption(77, new Random().nextInt(6) + 5)); // hp 5-10%
        }
        if (co == itemId) {
            phukien.itemOptions.add(new ItemOption(103, new Random().nextInt(6) + 5)); // ki 5-10%
        }
        if (bong == itemId) {
            phukien.itemOptions.add(new ItemOption(50, new Random().nextInt(6) + 5)); // sd 5- 10%
        }
        phukien.itemOptions.add(new ItemOption(192, 1));//solomon
        phukien.itemOptions.add(new ItemOption(193, 1));//(2 món kích hoạt ....)
        if (Util.isTrue(99, 100)) {// tỉ lệ ra hsd
            phukien.itemOptions.add(new ItemOption(93, new Random().nextInt(2) + 1));//hsd
        }
        return phukien;
    }

    //Cải trang Gohan WC
    public Item CT_WC(boolean rating) {
        Item caitrang = createItemSetKichHoat(883, 1);
        caitrang.itemOptions.add(new ItemOption(77, 30));// hp 30%
        caitrang.itemOptions.add(new ItemOption(103, 15));// ki 15%
        caitrang.itemOptions.add(new ItemOption(50, 20));// sd 20%
        caitrang.itemOptions.add(new ItemOption(192, 1));//solomon
        caitrang.itemOptions.add(new ItemOption(193, 1));//(2 món kích hoạt ....)
        if (Util.isTrue(99, 100) && rating) {// tỉ lệ ra hsd
            caitrang.itemOptions.add(new ItemOption(93, new Random().nextInt(2) + 1));//hsd
        }
        return caitrang;
    }

    public void openDTS(Player player) {
        //check sl đồ tl, đồ hd
        if (player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 555 && item.template.id <= 567).count() < 1) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ thần linh");
            return;
        }
        if (player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 650 && item.template.id <= 662).count() < 2) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ hủy diệt");
            return;
        }
        if (player.combineNew.itemsCombine.size() != 3) {
            Service.getInstance().sendThongBao(player, "Thiếu đồ");
            return;
        }
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            Item itemTL = player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 555 && item.template.id <= 567).findFirst().get();
            List<Item> itemHDs = player.combineNew.itemsCombine.stream().filter(item -> item.template.id >= 650 && item.template.id <= 662).collect(Collectors.toList());
            short[][] itemIds = {{1048, 1051, 1054, 1057, 1060}, {1049, 1052, 1055, 1058, 1061}, {1050, 1053, 1056, 1059, 1062}}; // thứ tự td - 0,nm - 1, xd - 2

            Item itemTS = DoThienSu(itemIds[player.gender][itemTL.template.type], player.gender);
            InventoryServiceNew.gI().addItemBag(player, itemTS);

            InventoryServiceNew.gI().subQuantityItemsBag(player, itemTL, 1);
            itemHDs.forEach(item -> InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1));

            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn đã nhận được " + itemTS.template.name);
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
        }
    }

    public Item DoThienSu(int itemId, int gender) {
        Item dots = createItemSetKichHoat(itemId, 1);
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        //áo
        if (ao.contains(itemId)) {
            dots.itemOptions.add(new ItemOption(47, Util.highlightsItem(gender == 2, new Random().nextInt(1201) + 2800))); // áo từ 2800-4000 giáp
            if (Util.isTrue(30, 100)) {
                dots.itemOptions.add(new ItemOption(108, Util.nextInt(3, 10)));
            }
        }
        //quần
        if (Util.isTrue(60, 100)) {
            if (quan.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(11) + 120))); // hp 120k-130k
            }
        } else {
            if (quan.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(22, Util.highlightsItem(gender == 0, new Random().nextInt(51) + 130))); // hp 130-180k 15%
                if (Util.isTrue(30, 100)) {
                    dots.itemOptions.add(new ItemOption(77, Util.nextInt(5, 15)));
                }
            }
        }
        //găng
        if (Util.isTrue(60, 100)) {
            if (gang.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(7651) + 11000))); // 11000-18600
            }
        } else {
            if (gang.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(0, Util.highlightsItem(gender == 2, new Random().nextInt(7001) + 12000))); // gang 15% 12-19k -xayda 12k1
                if (Util.isTrue(30, 100)) {
                    dots.itemOptions.add(new ItemOption(50, Util.nextInt(3, 10)));
                }
            }
        }
        //giày
        if (Util.isTrue(60, 100)) {
            if (giay.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 120))); // ki 90-110k
            }
        } else {
            if (giay.contains(itemId)) {
                dots.itemOptions.add(new ItemOption(23, Util.highlightsItem(gender == 1, new Random().nextInt(21) + 130))); // ki 110-130k
                if (Util.isTrue(30, 100)) {
                    dots.itemOptions.add(new ItemOption(103, Util.nextInt(5, 15)));
                }
            }
        }

        if (nhan.contains(itemId)) {
            dots.itemOptions.add(new ItemOption(14, Util.highlightsItem(gender == 1, new Random().nextInt(3) + 18))); // nhẫn 18-20%
            if (Util.isTrue(30, 100)) {
                dots.itemOptions.add(new ItemOption(117, Util.nextInt(3, 10)));
            }
        }
        dots.itemOptions.add(new ItemOption(21, 120));
        dots.itemOptions.add(new ItemOption(30, 1));
        return dots;
    }

    public List<ItemOption> getListOptionItemShop(short id) {
        List<ItemOption> list = new ArrayList<>();
        Manager.SHOPS.forEach(shop -> shop.tabShops.forEach(tabShop -> tabShop.itemShops.forEach(itemShop -> {
            if (itemShop.temp.id == id && list.size() == 0) {
                list.addAll(itemShop.options);
            }
        })));
        return list;
    }

    //Set Jiren ========================== Code by ndq
    public void openSKHJiren(Player player, int optionSKH) throws Exception {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1524);
            if (hq != null) {
                Item ao = Util.ratiItemSKHJiren(1519);
                ao.itemOptions.add(new ItemOption(optionSKH, 1));
                ao.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item quan = Util.ratiItemSKHJiren(1520);
                quan.itemOptions.add(new ItemOption(optionSKH, 1));
                quan.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));
                Item gang = Util.ratiItemSKHJiren(1521);
                gang.itemOptions.add(new ItemOption(optionSKH, 1));
                gang.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item giay = Util.ratiItemSKHJiren(1522);
                giay.itemOptions.add(new ItemOption(optionSKH, 1));
                giay.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item nhan = Util.ratiItemSKHJiren(1523);
                nhan.itemOptions.add(new ItemOption(optionSKH, 1));
                nhan.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                ao.itemOptions.add(new ItemOption(30, 0));
                quan.itemOptions.add(new ItemOption(30, 0));
                gang.itemOptions.add(new ItemOption(30, 0));
                giay.itemOptions.add(new ItemOption(30, 0));
                nhan.itemOptions.add(new ItemOption(30, 0));
                InventoryServiceNew.gI().addItemBag(player, ao);
                InventoryServiceNew.gI().addItemBag(player, quan);
                InventoryServiceNew.gI().addItemBag(player, gang);
                InventoryServiceNew.gI().addItemBag(player, giay);
                InventoryServiceNew.gI().addItemBag(player, nhan);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Bạn đã nhận được Set Jiren Cực VIP");
                InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Không tìm thấy hộp mở set Jiren");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    //Set Goku UI ========================== Code by ndq
    public void openSKHGokuUI(Player player, int optionSKH) throws Exception {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 4) {
            Item hq = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1532);
            if (hq != null) {
                Item ao = Util.ratiItemSKHGokuUI(1527);
                ao.itemOptions.add(new ItemOption(optionSKH, 1));
                ao.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item quan = Util.ratiItemSKHGokuUI(1528);
                quan.itemOptions.add(new ItemOption(optionSKH, 1));
                quan.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item gang = Util.ratiItemSKHGokuUI(1529);
                gang.itemOptions.add(new ItemOption(optionSKH, 1));
                gang.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item giay = Util.ratiItemSKHGokuUI(1530);
                giay.itemOptions.add(new ItemOption(optionSKH, 1));
                giay.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                Item nhan = Util.ratiItemSKHGokuUI(1531);
                nhan.itemOptions.add(new ItemOption(optionSKH, 1));
                nhan.itemOptions.add(new ItemOption(ItemService.gI().optionIdSKH(optionSKH), 1));

                ao.itemOptions.add(new ItemOption(30, 0));
                quan.itemOptions.add(new ItemOption(30, 0));
                gang.itemOptions.add(new ItemOption(30, 0));
                giay.itemOptions.add(new ItemOption(30, 0));
                nhan.itemOptions.add(new ItemOption(30, 0));
                InventoryServiceNew.gI().addItemBag(player, ao);
                InventoryServiceNew.gI().addItemBag(player, quan);
                InventoryServiceNew.gI().addItemBag(player, gang);
                InventoryServiceNew.gI().addItemBag(player, giay);
                InventoryServiceNew.gI().addItemBag(player, nhan);
                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendThongBao(player, "Bạn đã nhận được Set Goku UI Cực VIP");
                InventoryServiceNew.gI().subQuantityItemsBag(player, hq, 1);
                InventoryServiceNew.gI().sendItemBags(player);
            } else {
                Service.getInstance().sendThongBao(player, "Không tìm thấy hộp mở set Goku UI");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Bạn phải có ít nhất 5 ô trống hành trang");
        }
    }

    public Item createRanDomDoThanLinh() {
        Item item = null;
        short idItem = Manager.itemIds_TL[Util.nextInt(0, Manager.itemIds_TL.length - 1)];
        item = ItemService.gI().createNewItem(idItem);
        // add thuoc tinh cho do than linh
        int param = 10;
        int paramID = getParamIdByItemType(item.template.type);

        switch (paramID) {
            case 47:
                param = Util.nextInt(1500, 3000);
                break;
            case 6:
            case 7:
                param = Util.nextInt(45000, 65000);
                break;
            case 0:
                param = Util.nextInt(4000, 9000);
                break;
            case 14:
                Util.nextInt(5, 14);
                break;
        }
        item.itemOptions.add(new ItemOption(260, 2));
        return randommOptionForCheDo(item, param, paramID);
    }

    public int getParamIdByItemType(byte type) {
        int[] optionsType = new int[]{47, 6, 0, 7, 14};
        return optionsType[type];
    }

    public Item createRandomDoHuyDiet() {
        Item item = null;
        short idItem = Manager.itemIds_HD[Util.nextInt(0, Manager.itemIds_HD.length - 1)];
        item = ItemService.gI().createNewItem(idItem);
        // add thuoc tinh cho do than linh
        int param = 10;
        int paramID = getParamIdByItemType(item.template.type);

        switch (paramID) {
            case 47:
                param = Util.nextInt(3500, 6000);
                break;
            case 6:
            case 7:
                param = Util.nextInt(70000, 95000);
                break;
            case 0:
                param = Util.nextInt(10000, 12000);
                break;
            case 14:
                Util.nextInt(6, 18);
                break;
        }
        item.itemOptions.add(new ItemOption(260, 2));
        return randommOptionForCheDo(item, param, paramID);
    }

    private Item randommOptionForCheDo(Item item, int param, int paramID) {
        if (param <= 0) {
            param = 10;
        }
        ItemOption itemOption = new ItemOption(paramID, param);
        item.itemOptions.add(itemOption);
        return randomOption(item);
    }

    private Item randomOption(Item item) {
        if (Util.isTrue(80, 100)) {
            item.itemOptions.add(new ItemOption(optionsRandom[Util.nextInt(0, optionsRandom.length - 1)], Util.nextInt(5, 25)));
        }
        if (Util.isTrue(10, 100)) {
            item.itemOptions.add(new ItemOption(optionRandomVip[Util.nextInt(0, optionRandomVip.length - 1)], Util.nextInt(1, 25)));
        }
        return item;
    }

    public Item createRandomGoThienSu() {
        Item item = null;
        short idItem = (short) Util.nextInt(1048, 1062);
        item = ItemService.gI().createNewItem(idItem);
        // add thuoc tinh cho do than linh
        int param = 10;
        int paramID = getParamIdByItemType(item.template.type);

        switch (paramID) {
            case 47:
                param = Util.nextInt(6500, 9000);
                break;
            case 6:
            case 7:
                param = Util.nextInt(100000, 150000);
                break;
            case 0:
                param = Util.nextInt(12500, 16000);
                break;
            case 14:
                Util.nextInt(9, 25);
                break;
        }
        item.itemOptions.add(new ItemOption(260, 2));

        return randommOptionForCheDo(item, param, paramID);
    }

    public Item createRandomDoXen() {
        Item item = null;
        short idItem = Manager.setSen[Util.nextInt(0, Manager.setSen.length - 1)];
        item = ItemService.gI().createNewItem(idItem);
        // add thuoc tinh cho do than linh
        int param = 0;
        int paramID = getParamIdByItemType(item.template.type);

        switch (paramID) {
            case 47:
                param = Util.nextInt(9500, 12000);
                break;
            case 6:
            case 7:
                param = Util.nextInt(165000, 200000);
                break;
            case 0:
                param = Util.nextInt(16200, 21000);
                break;
            case 14:
                Util.nextInt(12, 20);
                break;
        }
        item.itemOptions.add(new ItemOption(260, 4));

        return randommOptionForCheDo(item, param, paramID);
    }

    public Item createRandomDoNguyenThuy() {
        Item item = null;
        short idItem = Manager.setNguyenThuy[Util.nextInt(0, Manager.setNguyenThuy.length - 1)];
        item = ItemService.gI().createNewItem(idItem);
        // add thuoc tinh cho do than linh
        int param = 0;
        int paramID = getParamIdByItemType(item.template.type);

        switch (paramID) {
            case 47:
                param = Util.nextInt(16000, 18000);
                break;
            case 6:
            case 7:
                param = Util.nextInt(275000, 300000);
                break;
            case 0:
                param = Util.nextInt(25500, 28000);
                break;
            case 14:
                Util.nextInt(20, 40);
                break;
        }
        item.itemOptions.add(new ItemOption(260, 4));

        return randommOptionForCheDo(item, param, paramID);
    }

    public Item createRandomDoThanhTon() {
        Item item = null;
        short idItem = Manager.setThanhTon[Util.nextInt(0, Manager.setThanhTon.length - 1)];
        item = ItemService.gI().createNewItem(idItem);
        // add thuoc tinh cho do than linh
        int param = 0;
        int paramID = getParamIdByItemType(item.template.type);

        switch (paramID) {
            case 47:
                param = Util.nextInt(20000, 25000);
                break;
            case 6:
            case 7:
                param = Util.nextInt(300000, 350000);
                break;
            case 0:
                param = Util.nextInt(28555, 32000);
                break;
            case 14:
                Util.nextInt(15, 18);
                break;
        }
        item.itemOptions.add(new ItemOption(260, 6));

        return randommOptionForCheDo(item, param, paramID);
    }

    public boolean hasOption(int i, Item it) {
        for (ItemOption itemOption : it.itemOptions) {
            if (itemOption.optionTemplate.id == i) {
                return true;
            }
        }
        return false;
    }

    public ItemOption getOptionById(Item item, int i) {
        for (ItemOption itemOption : item.itemOptions) {
            if (itemOption.optionTemplate.id == i) {
                return itemOption;
            }
        }
        return null;
    }

    public List<Item> createListItemLuyenThe() {
        List<Item> items = new ArrayList<>();
        for (int i = 1260; i <= 1266; i++) {
            items.add(ItemService.gI().createNewItem((short) i, 9999));
        }
        return items;
    }

    public Item createItemFromTemplate(Template.ItemTemplate itemTemplate, int quantity) {
        return createNewItem(itemTemplate.id, quantity);
    }

    public int getItemDuongLinh(byte type) {
        switch (type) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 2085 + type;
            case 7:
                return 2084;
            case 8:
                return 2092;
        }
        return -1;
    }
}
