package com.girlkun.models.player.tutien.linhthucsu;

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

public class LinhThucSu extends BasePoint implements IBaseAction {
    short[] idsItem1 = new short[]{465, 466, 663, 664, 665, 666, 667, 880, 881, 882};
    short[] idsItem2 = new short[]{472, 473, 1317, 1016, 1017};
    short[] idsItem3 = new short[]{579, 466, 1201};
    short[] idsItem4 = new short[]{381, 382, 383, 384};
    short[] idsItem5 = new short[]{1099, 1100, 1101, 1102};

    public LinhThucSu(Player player) {
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
            Service.gI().sendThongBao(player, "Đột phá linh thực sư thành công");
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
                return 50f;
            case 3:
                return 25f;
            case 4:
                return 10f;
            case 5:
                return 5f;
            case 6:
                return 3f;
            case 7:
                return 1f;
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
        if (player.tuTien.level < 2) {
            Service.gI().sendThongBao(player, "Bạn cần đạt trúc cơ để học phù chú");
            return;
        }
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
        String menuText = "|7|Thông tin linh thực sư\n" +
                "|5|Cấp bậc :" + getName() + "\n" +
                "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" +
                "|7|Tỷ lệ đột phá : " + getNextLevelExp() + "%\n" +
                "|1|Cấp càng cao tỷ lệ đột phá càng thấp\n" +
                "|2|Tổng buff : " + getHPMPBuff() + "% HP,MP |" + getDameBuff() + "% DAME\n" +
                "|7|Cấp càng cao buff cành mạnh,mỗi cấp tăng 5%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LINH_THUC, -1, menuText, "Nấu ăn", "Đóng");
    }

    public void showMenuCheBua() {
        String menuText = "|7|Nấu ăn\n" +
                "|5|Hãy chuẩn bị nguyên liệu như sau\n" +
                "|5|x1 Siêu thần thuỷ,x10 đùi gà\n" +
                "|2|Đặt chúng ở trong hành trang và chọn chế bùa\n" +
                "|1|Tỷ lệ thành công phụ thuộc vào vận khí của bạn";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CHE_LINH_THUC, -1, menuText, "Chế tạo", "Đóng");
    }

    @Override
    protected long getNextLevelExp() {
        switch (level + 1) {
            case 1:
                return 2000;
            case 2:
                return 10000;
            case 3:
                return 50000;
            case 4:
                return 100000;
            case 5:
                return 200000;
            case 6:
                return 300000;
            case 7:
                return 500000;
        }
        return 2000000000;
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

    public boolean isLinhThuc() {
        return level > 0;
    }

    public void nauAn() {
        Item it1 = InventoryServiceNew.gI().findItemBag(player, 2050);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, 2048);
        if (it1 == null || it2 == null || it2.quantity < 10) {
            Service.gI().sendThongBao(player, "Cần x1 Siêu thần thủy và x10 đùi gà");
            return;
        }
        short idTemp = 0;
        if (level > 0 && level < 3) {
            idTemp = idsItem1[Util.nextInt(0, idsItem1.length - 1)];
        } else if (level == 4) {
            idTemp = idsItem2[Util.nextInt(0, idsItem2.length - 1)];
        } else if (level == 5) {
            idTemp = idsItem3[Util.nextInt(0, idsItem3.length - 1)];
        } else if (level == 6) {
            idTemp = idsItem4[Util.nextInt(0, idsItem4.length - 1)];
        } else {
            idTemp = idsItem5[Util.nextInt(0, idsItem5.length - 1)];
        }
        if (Util.isTrue(getTyLeCheBuaThanhCong(), 100)) {
            Item item = ItemService.gI().createNewItem(idTemp);
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn nhận được x" + item.template.name);
        } else {
            Service.gI().sendThongBao(player, "Nấu ăn thất bại");
        }
        addExp(getExpCanGain(null));
    }
}
