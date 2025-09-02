/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.phuchusu;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BasePoint;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.utils.Util;

public class PhuChuSu extends BasePoint implements IBaseAction {
    public static short[] idsBua = new short[]{213, 214, 215, 216, 217, 218, 219, 797, 798, 799};
    public static short[] idsBuaVIP = new short[]{671, 672, 522};

    public PhuChuSu(Player player) {
        super(player);
    }

    private final byte MAX_LEVEL = 7;

    public void calcPoint() {
        player.nPoint.hpAdd += player.nPoint.hpg * getHPMPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpg * getHPMPBuff() / 100;
        player.nPoint.dameAdd += player.nPoint.dameg * getDameBuff() / 100;
        player.nPoint.defAdd += player.nPoint.defg * getDefBuff() / 100;
        player.nPoint.tlchinhxac += getChinhXacBuff();
        player.nPoint.tlNeDon += getNeBuff();
    }

    public void update() {
        if (exp == maxExp && level + 1 <= MAX_LEVEL && player.inventory.ruby - 5_000 >= 0 && player.isAutoDotPhaPhuChu) {
            if (Util.isTrue(getLevelUpPercent(), 100)) {
                this.levelUp();
                Service.gI().sendThongBao(player, "Tự động đột phá phù chú sư thành công");
            } else {
                Service.gI().sendThongBao(player, "Tự động đột phá phù chứ sư thất bại");
            }
            player.inventory.ruby -= 5_000;
            Service.gI().sendMoney(player);
            restExp();
        }
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        return level * 10;
    }

    @Override
    public void levelUp() {
        if (canLevelUp()) {
            level += 1;
            restExp();
        }
    }

    @Override
    public void restExp() {
        exp = 0;
        maxExp = getNextLevelExp();

    }

    @Override
    public void levelDown() {
        if (level - 1 >= 0) {
            level -= 1;
            restExp();
            Service.gI().sendThongBao(player, "Bạn đã lùi bước");
        }
    }

    @Override
    public void resetLevel() {
        level = 0;
        restExp();
        Service.gI().sendThongBao(player, "Bạn đã phế");
    }

    @Override
    public float getLevelUpPercent() {
        switch (level) {
            case 1:
                return 100f;
            case 2:
                return 20;
            case 3:
                return 15f;
            case 4:
                return 5f;
            case 5:
                return 3f;
            case 6:
                return 2f;
            case 7:
                return 1f;
        }
        return 1f;
    }

    public float getTyLeCheBuaThanhCong() {
        float baseTyLe = 20;
        return level * baseTyLe;
    }

    public void cheBua() {
        if (!canCheBua()) {
            Service.gI().sendThongBao(player, "Bạn không đủ linh lực để chế bùa");
            return;
        }
        player.tuTien.subLinhKhiPercent(1);
        float baseTl = getTyLeCheBuaThanhCong();
        Item item = null;
        Item it1 = InventoryServiceNew.gI().findItemBag(player, (short) 2046);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, (short) 2047);
        Item.ItemOption itemOption = null;
        int exxxxp = 0;
        if (it1 == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy Giấy Thếp");
            return;
        }
        if (it2 == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy Bút Chì");
            return;
        }
        for (Item.ItemOption option : it2.itemOptions) {
            if (option.optionTemplate.id == 12) {
                itemOption = option;
            }
        }
        if (itemOption == null || itemOption.param <= 0) {
            Service.gI().sendThongBao(player, "Bút chì của bạn đã hết số lần sử dụng");
            return;
        }

        if (Util.isTrue(baseTl, 100)) {
            CombineServiceNew.gI().sendEffectSuccessCombine(player);
            // rand nhan pham
            short tempId;
            tempId = idsBua[Util.nextInt(0, idsBua.length - 1)];
            // find item option cua but chi
            itemOption.param -= 1;
            item = ItemService.gI().createNewItem(tempId);
            InventoryServiceNew.gI().addItemBag(player, item);
            Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name);
            exxxxp += getExpCanGain(null) * Util.nextInt(1, 5);
            if (player.chienthan.tasknow == 3) {
                player.chienthan.dalamduoc++;
            }
        } else {
            CombineServiceNew.gI().sendEffectFailCombine(player);
            Service.gI().sendThongBao(player, "Chế bùa thất bại");
            exxxxp += getExpCanGain(null);
        }
        InventoryServiceNew.gI().subQuantityItemsBag(player, it1, 1);
        InventoryServiceNew.gI().sendItemBags(player);
        addExp(exxxxp);
    }

    public void addExp(long exp) {
        this.exp += exp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    public boolean canCheBua() {
        return player.tuTien.canHandleWithLinhKhiPoint(1);
    }

    public void cheBuaVIP() {
        if (this.level < 4) {
            Service.gI().sendThongBao(player, "Bạn cần đạt phù sư cấp tông sư để chế bùa vip");
            return;
        }
        if (!canCheBua()) {
            Service.gI().sendThongBao(player, "Bạn không đủ linh lực để chế bùa");
            return;
        }
        player.tuTien.subLinhKhiPercent(1);
        float baseTl = getTyLeCheBuaThanhCong();
        Item item = null;
        Item it1 = InventoryServiceNew.gI().findItemBag(player, (short) 2046);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, (short) 2047);
        Item.ItemOption itemOption = null;
        int exxxxp = 0;
        if (it1 == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy Giấy Thếp");
            return;
        }
        if (it2 == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy Bút Chì");
            return;
        }
        for (Item.ItemOption option : it2.itemOptions) {
            if (option.optionTemplate.id == 12) {
                itemOption = option;
            }
        }
        if (itemOption == null || itemOption.param <= 0) {
            Service.gI().sendThongBao(player, "Bút chì của bạn đã hết số lần sử dụng");
            return;
        }

        if (Util.isTrue(baseTl / 2, 100)) {
            CombineServiceNew.gI().sendEffectSuccessCombine(player);
            // rand nhan pham
            short tempId = idsBuaVIP[Util.nextInt(0, idsBuaVIP.length - 1)];
            // find item option cua but chi
            itemOption.param -= 1;
            item = ItemService.gI().createNewItem(tempId);
            Service.gI().sendThongBao(player, "Chế bùa thành công,bạn nhận được " + item.template.name);
            InventoryServiceNew.gI().subQuantityItemsBag(player, it1, 1);
            InventoryServiceNew.gI().addItemBag(player, item);
            exxxxp += getExpCanGain(null) * Util.nextInt(1, 10);
            InventoryServiceNew.gI().sendItemBags(player);
            if (player.chienthan.tasknow == 3) {
                player.chienthan.dalamduoc++;
            }
        } else {
            CombineServiceNew.gI().sendEffectFailCombine(player);
            Service.gI().sendThongBao(player, "Chế bùa thất bại");
            exxxxp += getExpCanGain(null) * Util.nextInt(1, 5);
        }
        addExp(exxxxp);
    }

    @Override
    public void openSystem() {
        // add but chi
        Item item = ItemService.gI().createNewItem((short) 2047, 1);
        item.itemOptions.add(new Item.ItemOption(12, 99999));
        InventoryServiceNew.gI().addItemBag(player, item);
        // add thay thep
        Item item1 = ItemService.gI().createNewItem((short) 2046, 99);
        item1.itemOptions.add(new Item.ItemOption(30, 1));
        InventoryServiceNew.gI().addItemBag(player, item1);
        InventoryServiceNew.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn nhận được x" + item.template.name + ",x99" + item1.template.name);
        this.levelUp();
    }

    @Override
    public boolean canLevelUp() {
        return exp == maxExp && level + 1 <= MAX_LEVEL;
    }

    @Override
    public String getName() {
        switch (level) {
            case 1:
                return "Nhập Giai";
            case 2:
                return "Sơ Giai";
            case 3:
                return "Trung Giai";
            case 4:
                return "Cao Giai";
            case 5:
                return "Kỳ Tài";
            case 6:
                return "Tông Sư";
            case 7:
                return "Đại tông Sư";
        }
        return "Học đồ";
    }

    @Override
    public String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    public void showMenu() {
        String menuText = "|7|Thông tin phù chú sư\n" +
                "|5|Cấp bậc :" + getName() + "\n" +
                "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" +
                "|7|Tỷ lệ đột phá : " + getLevelUpPercent() + "%\n" +
                "|1|Cấp càng cao tỷ lệ đột phá càng thấp\n" +
                "|2|Tổng buff : " + getHPMPBuff() + "% HP,MP |" + getDameBuff() + "% DAME\n" +
                "|7|Cấp càng cao buff cành mạnh,mỗi cấp tăng 5%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PHU_CHU_SU, -1, menuText, "Chế bùa", "Xem thông\ntin bùa", "Đóng");
    }

    public void showMenuCheBua() {
        String menuText = "|7|Chế bùa\n" +
                "|5|Cần 1 giấy thếp và đạo cụ vẽ là bút chì\n" +
                "|2|Đặt chúng ở trong hành trang và chọn chế bùa\n" +
                "|1|Tỷ lệ thành công phụ thuộc vào vận khí của bạn";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PHU_CHU_SU_CHE_BUA, -1, menuText, "Bùa\nThường", "Bùa\nVIP", "Đóng");
    }

    @Override
    protected long getNextLevelExp() {
        return (level + 1) * 10_000;
    }

    @Override
    public float getDameBuff() {
        return level * 5;
    }

    @Override
    public float getHPMPBuff() {
        return level * 5;
    }

    @Override
    public float getDefBuff() {
        return level * 5;
    }

    @Override
    public float getPSTBuff() {
        return level;
    }

    @Override
    public float getHutHPBuff() {
        return level;
    }

    @Override
    public float getHutMPBuff() {
        return level;
    }

    @Override
    public float getNeBuff() {
        return level / 2f;
    }

    @Override
    public float getChinhXacBuff() {
        return level;
    }

    public boolean isPhuChu() {
        return level > 0;
    }
}
