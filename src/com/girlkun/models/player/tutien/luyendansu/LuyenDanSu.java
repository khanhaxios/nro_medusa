package com.girlkun.models.player.tutien.luyendansu;

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

public class LuyenDanSu extends BasePoint implements IBaseAction {
    private static byte MAX_LEVEL = 9;

    public int tongDanDuocDaAn;
    public long lastTimeTruDiemKhangTinh = System.currentTimeMillis();

    public LuyenDanSu(Player player) {
        super(player);
    }

    public int diemKhangTinh = 0;
    public int totalDiemKhangTinh = 100;

    @Override
    public long getExpCanGain(Mob targetMob) {
        return level * 100;
    }

    @Override
    public void levelUp() {
        this.level++;
        restExp();
        diemKhangTinh = 0;
        Service.gI().sendThongBao(player, "Bạn đã đột phá luyện đan sư thành công");
    }


    @Override
    public void restExp() {
        this.exp = 0;
        this.maxExp = getNextLevelExp();
    }

    @Override
    protected long getNextLevelExp() {
        return (level + 1) * 10000;

    }

    @Override
    public void levelDown() {
        this.level--;
        restExp();
    }

    @Override
    public void resetLevel() {
        this.level = 0;
        restExp();
    }

    @Override
    public float getLevelUpPercent() {
        switch (level) {
            case 1:
                return 10f;
            case 2:
                return 5f;
            case 3:
                return 3f;
            case 4:
                return 1f;
            case 5:
                return .5f;
            case 6:
                return .3f;
            case 7:
                return .1f;
            case 8:
                return .05f;
            case 9:
                return .03f;
        }
        return .3f;
    }

    @Override
    public void openSystem() {
        if (player.tuTien.level < 3) {
            Service.gI().sendThongBao(player, "Cần đạt nguyên anh để học luyện đan");
            return;
        }
        levelUp();
        // cho it vat lieu
        InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createNewItem((short) 2069, 10));
        InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createNewItem((short) 2070, 10));
        InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createNewItem((short) 2071, 10));
        Service.gI().sendThongBao(player, "Bạn nhận được x10 Cam ngọc,Mầm đậu thần,Khúc liên");
        InventoryServiceNew.gI().sendItemBags(player);
    }

    @Override
    public boolean canLevelUp() {
        return exp == maxExp && level + 1 <= MAX_LEVEL;
    }

    @Override
    public String getName() {
        return "Luyện đan sư [" + level + "]";
    }

    @Override
    public String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    @Override
    public float getDameBuff() {
        return 0;
    }

    @Override
    public float getHPMPBuff() {
        return 0;
    }

    @Override
    public float getDefBuff() {
        return 0;
    }

    @Override
    public float getPSTBuff() {
        return 0;
    }

    @Override
    public float getHutHPBuff() {
        return 0;
    }

    @Override
    public float getHutMPBuff() {
        return 0;
    }

    @Override
    public float getNeBuff() {
        return 0;
    }

    @Override
    public float getChinhXacBuff() {
        return 0;
    }

    public void update() {
        if (isLuyenDan()) {
            //// handle diem khang tinh cho dan
            if (diemKhangTinh - 1 >= 0 && Util.canDoWithTime(lastTimeTruDiemKhangTinh, 2 * 60 * 60 * 1000)) {
                diemKhangTinh -= 1;
                lastTimeTruDiemKhangTinh = System.currentTimeMillis();
            }
            if (canLevelUp() && player.inventory.ruby - 5_000 >= 0 && player.tuTien.isAutoDotPhaLuyenDan) {
                // try dot pha
                if (Util.isTrue(getLevelUpPercent(), 100)) {
                    levelUp();
                    Service.gI().sendThongBao(player, "Tự động đột phá luyện đan sư thành công");
                } else {
                    Service.gI().sendThongBao(player, "Tự động đột phá luyện đan sư thất bại");
                }
                player.inventory.ruby -= 5_000;
                Service.gI().sendMoney(player);
            }
        }
    }

    public boolean isLuyenDan() {
        return level > 0;
    }

    public void showBaseMenu() {
        String menuText = "|7|Thông tin luyện đan sư\n" + "|5|Cấp bậc :" + getName() + "\n" + "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" + "|7|Tỷ lệ đột phá : " + getLevelUpPercent() + "%\n" + "|1|Cấp càng cao tỷ lệ đột phá càng thấp\n" + "|2|Số đan dược đã dùng  : " + tongDanDuocDaAn + " viên\n" + "|5|Đan dược kháng tính : " + diemKhangTinh + "%\n" + "|7|Khi đột phá , đan dược kháng tính sẽ được giảm bớt đi 1 xíu";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_DAN, -1, menuText, "Luyện đan", "Đóng");
    }

    public void luyenToiTheDan() {
        Item it1 = InventoryServiceNew.gI().findItemBag(player, 2069);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, 2070);
        Item it3 = InventoryServiceNew.gI().findItemBag(player, 2071);
        if (it1 == null || it3 == null || it2 == null || it1.quantity < 2 || it2.quantity < 2 || it3.quantity < 2) {
            Service.gI().sendThongBaoOK(player, "Cần x2 mầm đậu thần , khúc liên ,cam ngọc");
            return;
        }
        // tao dan theo ty le
        float ratio = getTyLeCheDanThanhCong();
        if (Util.isTrue(ratio, 110)) {
            InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createNewItem((short) 2072, 1));
            Service.gI().sendThongBao(player, "Bạn nhận được Tôi thể đan x1");
        } else {
            Service.gI().sendThongBao(player, "Luyện chế thất bại");
        }
        addExp(getExpCanGain(null));
        InventoryServiceNew.gI().subQuantityItemsBag(player, it1, 2);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it2, 2);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it3, 2);
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void addExp(long exp) {
        this.exp += exp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    private float getTyLeCheDanThanhCong() {
        return 1 + ((player.tuTien.ngoTinh / 100f) * level);
    }

    public void useDanDuoc(Item item) {
        // cong can cot
        boolean canUse = true;
        boolean canAddKhangTinh = true;
        if (diemKhangTinh >= totalDiemKhangTinh) {
            Service.gI().sendThongBao(player, "Bạn đã đạt giới hạn đan dược có thể sử dụng nếu ăn nữa cũng ko có tác dụng");
            canUse = false;
        }
        if (!Util.isTrue(50, (int) (100 * getXDiemThienPhu()))) {
            canUse = false;
            Service.gI().sendThongBao(player, "Thiên phú quá cao dùng đan thất bại");
        }
        if (canUse) {
            switch (item.template.id) {
                case 2072:
                    player.tuTien.addPoint(0, 2);
                    Service.gI().sendThongBao(player, "Bạn vừa dùng x1 " + item.template.name + " Căn cốt tăng lên " + 2);
                    if (!player.luyenThe.isNotLuyenThe() && player.luyenThe.level >= 100) {
                        //+ tu vi
                        player.luyenThe.addExp(Util.nextInt(1, 100));
                    }
                    break;
                case 2073:
                    player.tuTien.addPoint(1, 1);
                    Service.gI().sendThongBao(player, "Bạn vừa dùng x1 " + item.template.name + " Ngộ tính tăng lên " + 1);
                    break;
                case 2074:
                    if (diemKhangTinh > 0) {
                        diemKhangTinh -= 20;
                        canAddKhangTinh = false;
                    } else {
                        player.tuTien.addPoint(0, Util.nextInt(2, 6));
                        player.tuTien.addPoint(1, Util.nextInt(1, 3));
                    }
                    break;
                case 2075:
                    player.tuTien.ratioThienPhu();
                    Service.gI().sendThongBao(player, "Thiên phú của bạn đã được làm mới hãy kiểm tra");
                    break;
            }
            tongDanDuocDaAn++;
            if (canAddKhangTinh) {
                diemKhangTinh += 5;
                if (diemKhangTinh > 100) diemKhangTinh = 100;
            }
        }

        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void luyenNgungNguyenDan() {
        Item it1 = InventoryServiceNew.gI().findItemBag(player, 2069);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, 2070);
        Item it3 = InventoryServiceNew.gI().findItemBag(player, 2071);
        if (it1 == null || it3 == null || it2 == null || it1.quantity < 2 || it2.quantity < 2 || it3.quantity < 2) {
            Service.gI().sendThongBaoOK(player, "Cần x2 mầm đậu thần , khúc liên ,cam ngọc");
            return;
        }
        // tao dan theo ty le
        float ratio = getTyLeCheDanThanhCong();
        if (Util.isTrue(ratio, 110)) {
            InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createNewItem((short) 2073, 1));
            Service.gI().sendThongBao(player, "Bạn nhận được Ngưng nguyên đan x1");
        } else {
            Service.gI().sendThongBao(player, "Luyện chế thất bại");
        }
        addExp(getExpCanGain(null));
        InventoryServiceNew.gI().subQuantityItemsBag(player, it1, 2);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it2, 2);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it3, 2);
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void luyenTayTuyDan() {
        Item it1 = InventoryServiceNew.gI().findItemBag(player, 2069);
        Item it2 = InventoryServiceNew.gI().findItemBag(player, 2070);
        Item it3 = InventoryServiceNew.gI().findItemBag(player, 2071);
        if (it1 == null || it3 == null || it2 == null || it1.quantity < 20 || it2.quantity < 20 || it3.quantity < 20) {
            Service.gI().sendThongBaoOK(player, "Cần x20 mầm đậu thần , khúc liên ,cam ngọc");
            return;
        }
        // tao dan theo ty le
        float ratio = getTyLeCheDanThanhCong();
        if (Util.isTrue(ratio, 110)) {
            InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createNewItem((short) 2074, 1));
            Service.gI().sendThongBao(player, "Bạn nhận được Tẩy Tủy Đan x1");
        } else {
            Service.gI().sendThongBao(player, "Luyện chế thất bại");
        }
        addExp(getExpCanGain(null));
        InventoryServiceNew.gI().subQuantityItemsBag(player, it1, 20);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it2, 20);
        InventoryServiceNew.gI().subQuantityItemsBag(player, it3, 20);
        InventoryServiceNew.gI().sendItemBags(player);
    }
}
