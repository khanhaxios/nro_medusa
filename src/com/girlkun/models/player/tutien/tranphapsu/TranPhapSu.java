package com.girlkun.models.player.tutien.tranphapsu;

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

public class TranPhapSu extends BasePoint implements IBaseAction {
    private final short[] idsChanMenh = new short[]{1300, 1301, 1302, 1303, 1304, 1305, 1306, 1307, 1308};
    private final byte MAX_LEVEL = 7;

    public TranPhapSu(Player player) {
        super(player);
    }

    public void calcPoint() {
        player.nPoint.hpAdd += player.nPoint.hpg * getHPMPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpg * getHPMPBuff() / 100;
        player.nPoint.dameAdd += player.nPoint.dameg * getDameBuff() / 100;
        player.nPoint.defAdd += player.nPoint.defg * getDefBuff() / 100;
        player.nPoint.tlchinhxac += getChinhXacBuff();
        player.nPoint.tlNeDon += getNeBuff();
    }

    public void update() {
        if (isTranPhap()) {
            // find item chan menh
            Item chanMenh = InventoryServiceNew.gI().findItemBody(player, idsChanMenh);
            if (chanMenh != null) {
                addExp(getExpCanGain(null));
            }
            // auto dot pha
            if (exp == maxExp) {
                float tyle = getLevelUpPercent();
                if (Util.isTrue(tyle, 300)) {
                    levelUp();
                    Service.gI().sendThongBaoOK(player, "Bạn đã đột phá trận pháp sư");
                } else {
                    Service.gI().sendThongBao(player, "Tự động đột phá trận pháp sư thất bại");
                }
                restExp();
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
        Item item = ItemService.gI().createNewItem((short) 1318, 99);
        item.itemOptions.add(new Item.ItemOption(30, 1));
        InventoryServiceNew.gI().addItemBag(player, item);
        InventoryServiceNew.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn nhận được x99" + item.template.name);
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
        String menuText = "|7|Thông tin trận pháp sư\n" + "|5|Cấp bậc :" + getName() + "\n" + "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" + "|7|Tỷ lệ đột phá : " + getLevelUpPercent() + "%\n" + "|1|Cấp càng cao tỷ lệ đột phá càng thấp\n" + "|2|Tổng buff : " + getHPMPBuff() + "% HP,MP |" + getDameBuff() + "% DAME\n" + "|7|Cấp càng cao buff cành mạnh,mỗi cấp tăng 5%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TRAN_PHAP_SU, -1, menuText, "Vẽ\nchân mệnh", "Nâng\ncấp CM", "Đóng");
    }

    public void showMenuCheBua() {
        String menuText = "|7|Vẽ chân mệnh\n" + "|5|Cần 1 đá ngũ sắc và một đá cầu vồng\n" + "|2|Đặt chúng ở trong hành trang và chọn vẽ chân mệnh\n" + "|1|Tỷ lệ thành công phụ thuộc vào vận khí của bạn";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_VE_CHAN_MENH, -1, menuText, "Vẽ\nChân Mệnh", "Đóng");
    }

    public void showMenuNangCapChanMenh() {
        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_CHAN_MENH);
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

    public boolean isTranPhap() {
        return level > 0;
    }

    public void veChanMenh() {
        if (!canHandleWithLinhKhiPoint(1)) {
            Service.gI().sendThongBao(player, "Không đủ linh khí");
            return;
        }
        Item it1 = InventoryServiceNew.gI().findItemBag(player, 674);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, 1083);
        if (it1 == null || it2 == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy nguyên liệu.Cần x1 đá ngũ sắc và x1 đá cầu vồng");
        }
        InventoryServiceNew.gI().subQuantityItemsBag(player, it1, 1);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it2, 1);
        subLinhKhiPercent(1);
        // tao chan menh 1
        Item chanmenh = ItemService.gI().createNewItem((short) 1300);
        chanmenh.itemOptions.add(new Item.ItemOption(50, 5));
        chanmenh.itemOptions.add(new Item.ItemOption(77, 7));
        chanmenh.itemOptions.add(new Item.ItemOption(103, 7));
        chanmenh.itemOptions.add(new Item.ItemOption(30, 1));
        InventoryServiceNew.gI().addItemBag(player, chanmenh);
        InventoryServiceNew.gI().sendItemBags(player);
        Service.gI().sendThongBao(player, "Bạn nhận được chân mệnh cấp 1");
        this.addExp(getExpCanGain(null));
    }
}
