package com.girlkun.models.player.tutien.khongthisu;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BasePoint;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class KhongThiSu extends BasePoint implements IBaseAction {
    private final byte MAX_LEVEL = 7;

    public KhongThiSu(Player player) {
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
        if (isKhongThi()) {
            if (exp == maxExp && level + 1 <= MAX_LEVEL && player.inventory.ruby - 5_000 >= 0 && player.tuTien.isAutoDotPhaKhongThi) {
                if (Util.isTrue(getLevelUpPercent(), 300)) {
                    this.levelUp();
                    Service.gI().sendThongBao(player, "Tự động đột phá khống thi sư thành công");
                } else {
                    Service.gI().sendThongBao(player, "Tự động đột phá khống thi sư thất bại");
                }
                restExp();
                player.inventory.ruby -= 5_000;
                Service.gI().sendMoney(player);
            }
        }
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        return ((level + targetMob.level) * 10);
    }

    @Override
    public void levelUp() {
        if (canLevelUp()) {
            level += 1;
            restExp();
            Service.gI().sendThongBao(player, "Đột phá phù chú sư thành công");
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
                return 20f;
            case 2:
                return 10f;
            case 3:
                return 5f;
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
        String menuText = "|7|Thông tin khống thi sư\n" +
                "|5|Cấp bậc :" + getName() + "\n" +
                "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" +
                "|7|Tỷ lệ đột phá : " + getLevelUpPercent() + "%\n" +
                "|1|Cấp càng cao tỷ lệ đột phá càng thấp\n" +
                "|2|Tổng buff : " + getHPMPBuff() + "% HP,MP |" + getDameBuff() + "% DAME\n" +
                "|7|Cấp càng cao buff cành mạnh,mỗi cấp tăng 5%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_KHONG_THI, -1, menuText, "Đóng");
    }

//    public void showMenuCheBua() {
//        String menuText = "|7|Chế bùa\n" +
//                "|5|Cần 1 giấy thếp và đạo cụ vẽ là bút chì\n" +
//                "|2|Đặt chúng ở trong hành trang và chọn chế bùa\n" +
//                "|1|Tỷ lệ thành công phụ thuộc vào vận khí của bạn";
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

    public boolean isKhongThi() {
        return level > 0;
    }
}
