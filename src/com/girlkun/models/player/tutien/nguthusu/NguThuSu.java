package com.girlkun.models.player.tutien.nguthusu;

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
import com.girlkun.utils.Util;

public class NguThuSu extends BasePoint implements IBaseAction {
    public NguThuSu(Player player) {
        super(player);
    }

    private final byte MAX_LEVEL = 7;
    private long lastTimeGetExp = System.currentTimeMillis();

    public void calcPoint() {
        player.nPoint.hpAdd += player.nPoint.hpg * getHPMPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpg * getHPMPBuff() / 100;
        player.nPoint.dameAdd += player.nPoint.dameg * getDameBuff() / 100;
        player.nPoint.defAdd += player.nPoint.defg * getDefBuff() / 100;
        player.nPoint.tlchinhxac += getChinhXacBuff();
        player.nPoint.tlNeDon += getNeBuff();
    }

    public void update() {
        if (isNguThu() && Util.canDoWithTime(lastTimeGetExp, 2000)) {
            addExp(getExpCanGain(null));
            if (exp == maxExp && level + 1 <= MAX_LEVEL && player.inventory.ruby - 10_000 >= 0) {
                if (Util.isTrue(getLevelUpPercent(), 300)) {
                    this.levelUp();
                    Service.gI().sendThongBao(player, "Tự động đột phá ngự thú sư thành công");
                } else {
                    Service.gI().sendThongBao(player, "Tự động đột phá ngự thú sư thất bại");
                }
                restExp();
                player.inventory.ruby -= 10_000;
                Service.gI().sendMoney(player);
            }
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
                return 10f;
            case 2:
                return 5f;
            case 3:
                return 2f;
            case 4:
                return 1f;
            case 5:
                return .5f;
            case 6:
                return .3f;
            case 7:
                return .1f;
        }
        return 1f;
    }

    public float getTyLeCheBuaThanhCong() {
        float baseTyLe = 5;
        return level * baseTyLe;
    }

    public void addExp(long exp) {
        this.exp += exp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    @Override
    public void openSystem() {
        Item item = ItemService.gI().createNewItem((short) 2042, 1);
        item.itemOptions.add(new Item.ItemOption(0, 6000));
        item.itemOptions.add(new Item.ItemOption(50, 10));
        item.itemOptions.add(new Item.ItemOption(77, 10));
        item.itemOptions.add(new Item.ItemOption(103, 10));
        Item item1 = ItemService.gI().createNewItem((short) 1514, 1);
        item1.itemOptions.add(new Item.ItemOption(0, 6000));
        item1.itemOptions.add(new Item.ItemOption(50, 10));
        item1.itemOptions.add(new Item.ItemOption(77, 10));
        item1.itemOptions.add(new Item.ItemOption(103, 10));
        InventoryServiceNew.gI().addItemBag(player, item1);
        InventoryServiceNew.gI().addItemBag(player, item);
        InventoryServiceNew.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn nhận được " + item.template.name + " và " + item1.template.name);
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
        String menuText = "|7|Thông tin ngự thú sư\n" + "|5|Cấp bậc :" + getName() + "\n" + "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" + "|7|Tỷ lệ đột phá : " + getLevelUpPercent() + "%\n" + "|1|Cấp càng cao tỷ lệ đột phá càng thấp\n" + "|2|Tổng buff : " + getHPMPBuff() + "% HP,MP |" + getDameBuff() + "% DAME\n" + "|7|Cấp càng cao buff cành mạnh,mỗi cấp tăng 5%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_NGU_THU, -1, menuText, "Đóng");
    }

//    public void showMenuCheBua() {
//        String menuText = "|7|Chế bùa\n" + "|5|Cần 1 giấy thếp và đạo cụ vẽ là bút chì\n" + "|2|Đặt chúng ở trong hành trang và chọn chế bùa\n" + "|1|Tỷ lệ thành công phụ thuộc vào vận khí của bạn";
//        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PHU_CHU_SU_CHE_BUA, -1, menuText, "Bùa\nThường", "Bùa\nVIP", "Đóng");
//    }

    @Override
    protected long getNextLevelExp() {
        return (level + 1) * 1_000_000;
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

    public boolean isNguThu() {
        return level > 0;
    }

    public boolean canUseItem(int indexBag) {
        Item item = InventoryServiceNew.gI().findItemBag(player, indexBag);
        if (item == null) return false;

        int point = 0;

        for (Item.ItemOption option : item.itemOptions) {
            int id = option.optionTemplate.id;
            int param = option.param;
            // Các option chia 1000
            if (id == 0 || id == 2 || id == 5 || id == 6 || id == 7 || id == 22) {
                point += param / 1000;
            } else if (id == 14 || id == 23 || id == 50 || id == 77 || id == 103) {
                point += param;
            }
        }

        // Điều kiện dùng item theo level và point
        if (level == 7) return true;
        if (level >= 6 && point > 150) return true;
        if (level >= 4 && point > 100) return true;
        if (level >= 2 && point > 50) return true;
        return level >= 1 && point >= 0 && point <= 50;
    }
}
