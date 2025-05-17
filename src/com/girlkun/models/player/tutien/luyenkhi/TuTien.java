package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BasePoint;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.models.player.tutien.base_tutien.TuTienTemplate;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class TuTien extends BasePoint implements IBaseAction {
    private static final String[] CANH_GIOI = new String[]{"Luyện Khí", "Trúc Cơ", "Kim Đan", "Nguyên Anh", "Hóa Thần", "Phong Thánh", "Thần Chiếu", "Huyền Linh", "Quy Nguyên", "Du Tầm", "Không Luân", "Tam Thiên", "Tứ Trụ", "Dạ Ma Thiên Cảnh", "Tu Di Sơn Chủ", "Tinh Hà Thánh Nhân", "Thần Quỷ Mạt Trắc", "Đạo Lộ Chi Cảnh", "Thánh Tôn Chi Cảnh"};
    private static final long[] LEVEL_EXP = new long[]{100, 200, 500, 1000, 5000, 60000, 200000, 3000000, 5000000, 10000000, 12_000_000, 15_000_000, 18_000_000, 20_000_000, 25_000_000, 30_000_000, 40_000_000, 60_000_000, 80_000_000, 100_000_00}; // 20
    private static final long[] SUB_LEVEL_EXP = new long[]{100, 150, 200, 250, 300, 320, 350, 360, 370, 399};
    private static final long[] BASE_LINH_KHI = new long[]{
            1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000,
            1000000, 2000000, 5000000, 10000000, 20000000, 50000000, 100000000,
            200000000, 500000000, 1000000000
    };
    private static final long[] BASE_LINH_KHI_HOI_PHUC = new long[]{
            /* 0  */     10L,
            /* 1  */     20L,
            /* 2  */     50L,
            /* 3  */    100L,
            /* 4  */    200L,
            /* 5  */    500L,
            /* 6  */  1_000L,
            /* 7  */  2_000L,
            /* 8  */  5_000L,
            /* 9  */ 10_000L,
            /* 10 */ 20_000L,
            /* 11 */ 50_000L,
            /* 12 */100_000L,
            /* 13 */200_000L,
            /* 14 */500_000L,
            /* 15 */1_000_000L,
            /* 16 */2_000_000L,
            /* 17 */5_000_000L,
            /* 18 */10_000_000L
    };
    public LinhCan linhCan;
    public List<TienPhap> tienPhaps;
    public CongPhap congPhap;

    public TuTien(Player player) {
        super(player);
        linhCan = new LinhCan();
        congPhap = new CongPhap(this);
        tienPhaps = new ArrayList<>();
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        float xDiem = getXDiemThienPhu();
        long baseDiem = 10;
        float tyLexDiemTuMob = Math.max(targetMob.buffTuTienLevel / level, 1);
        if (tyLexDiemTuMob > 10) {
            tyLexDiemTuMob /= 5;
        }
        long finalDiem = (long) ((baseDiem * tyLexDiemTuMob) * xDiem);
        finalDiem *= congPhap.xLinhKhiBuff;
        return finalDiem;
    }

    @Override
    public void levelUp() {
        // check sub level
        if (subLevel == 9) {
            if (level < CANH_GIOI.length) { // cấp tối đa là 14
                level++;
                subLevel = 0;
                Service.gI().sendThongBao(player, "Chúc mừng bạn đã đột phá lên " + getFormatName());
            } else {
                Service.gI().sendThongBao(player, "Bạn đã đạt cấp tối đa.");
            }
        } else {
            subLevel++;
            Service.gI().sendThongBao(player, "Bạn đã đột phá lên " + getFormatName());
        }
        restExp(); // reset exp khi lên cấp
        restLinhKhi();
        Service.gI().point(player); // cập nhật lại chỉ số buff
    }

    @Override
    public void restExp() {
        exp = 0;
        this.maxExp = getNextLevelExp();
    }

    public void restLinhKhi() {
        maxLinhKhiPoint = calcMaxLinhKhiPoint();
        linhKhiPoint = maxLinhKhiPoint;
    }

    public void hoiPhucLinhKhi() {
        if (linhKhiPoint < maxLinhKhiPoint) {
            int lv = Math.min(level, BASE_LINH_KHI_HOI_PHUC.length - 1);
            long linhKhiCanHoiPhuc = BASE_LINH_KHI_HOI_PHUC[lv] * congPhap.xTocDoKhoiPhucLinhKhi;
            addLinhKhi(linhKhiCanHoiPhuc);
        }
    }

    public long calcMaxLinhKhiPoint() {
        return BASE_LINH_KHI[level] + (BASE_LINH_KHI[level] * congPhap.xLinhKhiBuff);
    }

    @Override
    public void levelDown() {
        if (subLevel > 0) {
            subLevel--;
            Service.gI().sendThongBao(player, "Bạn đã lui bước xuống " + getFormatName());
        } else if (level > 0) {
            level--;
            subLevel = 9;
            Service.gI().sendThongBao(player, "Bạn đã lui bước xuống " + getFormatName());
        } else {
            Service.gI().sendThongBao(player, "Bạn đã bị phế");
        }
        restExp();
        Service.gI().point(player);
    }

    @Override
    public void resetLevel() {
        level = 0;
        subLevel = 0;
        restExp();
        Service.gI().sendThongBao(player, "Bạn đã bị phế bỏ");
        Service.gI().point(player);
    }

    private float getBaseBuffByLevel(float multiplier) {
        return this.level * multiplier;
    }

    private float getSubLevelOtherBuff() {
        return this.subLevel * 2 / 100f;
    }

    private float getSubLevelHpMpBuff() {
        return this.subLevel * 3 / 100f;
    }

    @Override
    public float getLevelUpPercent() {
        return 0;
    }

    @Override
    public void openSystem() {
        // mo he thong tu tien
        // check luyen the dat cap 10
        if (player.luyenThe.level < 10) {
            Service.gI().sendThongBao(player, "Bạn cần đạt luyện thể cấp 10 để bắt đầu tu tiên");
            return;
        }

        this.ratioThienPhu();
        // random linh can
        this.linhCan = ratioLinhCan();
        // random congphap
        // set level
        level = 1; // luyen khi 1 tang
        subLevel = 0;
        restExp(); // reset exp
        // update lai chi so cua player vi se buff chi so
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã mở hệ thống tu tiên");
    }

    public void ratioThienPhu() {
        // open he thong tu tien
        // random thien phu va can cot
        if (Util.isTrue(1, 100)) {
            canCot = Util.nextInt(1, 500) + 500;
            ngoTinh = Util.nextInt(1, 250) + 250;
        } else if (Util.isTrue(15, 100)) {
            canCot = Util.nextInt(1, 200) + 200;
            ngoTinh = Util.nextInt(1, 100) + 100;
        } else if (Util.isTrue(50, 100)) {
            canCot = Util.nextInt(1, 30) + 100;
            ngoTinh = Util.nextInt(1, 15) + 50;
        } else {
            canCot = Util.nextInt(1, 10) + 50;
            ngoTinh = Util.nextInt(1, 5) + 25;
        }
    }

    public LinhCan ratioLinhCan() {
        if (Util.isTrue(10, 100)) {
            // thuoc tinh phong loi quang am
            int i = Util.nextInt(0, 3);
            String key = switch (i) {
                case 1 -> "L";
                case 2 -> "Q";
                case 3 -> "A";
                default -> "P";
            };
            ThuocTinhLinhCan thuocTinhLinhCan = getThuocTinhLinhCanByLinhCan(TuTienTemplate.LINH_CAN.get(key));
            thuocTinhLinhCan.setParam(thuocTinhLinhCan.ratioThuocTinhLinhCan());
            return new LinhCan(TuTienTemplate.LINH_CAN.get(key), thuocTinhLinhCan);
        } else if (Util.isTrue(20, 100)) {
            // 20% for kim hoac hoa
            int i = Util.nextInt(0, 1);
            String key = i == 0 ? "K" : "H";
            ThuocTinhLinhCan thuocTinhLinhCan = getThuocTinhLinhCanByLinhCan(TuTienTemplate.LINH_CAN.get(key));
            thuocTinhLinhCan.setParam(thuocTinhLinhCan.ratioThuocTinhLinhCan());
            return new LinhCan(TuTienTemplate.LINH_CAN.get(key), thuocTinhLinhCan);
        } else {
            // thuoc tinh moc thuy tho
            int i = Util.nextInt(0, 2);
            String key = switch (i) {
                case 0 -> "M";
                case 1 -> "TH";
                default -> "T";
            };
            // ratio thuoc tinh linh can
            ThuocTinhLinhCan thuocTinhLinhCan = getThuocTinhLinhCanByLinhCan(TuTienTemplate.LINH_CAN.get(key));
            thuocTinhLinhCan.setParam(thuocTinhLinhCan.ratioThuocTinhLinhCan());
            return new LinhCan(TuTienTemplate.LINH_CAN.get(key), thuocTinhLinhCan);
        }
    }

    public ThuocTinhLinhCan getThuocTinhLinhCanByLinhCan(byte id) {
        return TuTienTemplate.THUOC_TINH_BUFF_LINH_CAN.stream().filter(tt -> tt.getLinhCanBatBuoc() == id).findFirst().orElse(null);
    }

    @Override
    public boolean canLevelUp() {
        return exp == maxExp;
    }

    @Override
    public String getName() {
        return getFormatName();
    }

    @Override
    public String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    @Override
    public float getDameBuff() {
        return getBaseBuffByLevel(0.01f) + getSubLevelOtherBuff();
    }

    @Override
    public float getHPMPBuff() {
        return getBaseBuffByLevel(0.02f) + getSubLevelHpMpBuff();
    }

    @Override
    public float getDefBuff() {
        return getBaseBuffByLevel(0.01f) + getSubLevelOtherBuff();
    }

    @Override
    public float getPSTBuff() {
        return getBaseBuffByLevel(0.01f) + getSubLevelOtherBuff();
    }

    @Override
    public float getHutHPBuff() {
        return getBaseBuffByLevel(0.005f) + getSubLevelOtherBuff();
    }

    @Override
    public float getHutMPBuff() {
        return getBaseBuffByLevel(0.005f) + getSubLevelOtherBuff();
    }

    @Override
    public float getNeBuff() {
        return getBaseBuffByLevel(0.01f) + getSubLevelOtherBuff();
    }

    @Override
    protected long getNextLevelExp() {
        int nextLv = Math.min(level, LEVEL_EXP.length - 1);
        int nextSubLv = Math.min(subLevel, SUB_LEVEL_EXP.length - 1);
        return LEVEL_EXP[nextLv] + SUB_LEVEL_EXP[nextSubLv];
    }

    @Override
    public float getChinhXacBuff() {
        return getBaseBuffByLevel(0.01f) + getSubLevelOtherBuff();
    }

    public void update() {
        // update cac chi so tu tien
    }

    public String toJsonString() {
        return null;
    }

    public String getSubLevelName(byte lv) {
        if (lv > 0 && level <= 3) {
            return "Sơ Kỳ[" + lv + "]";
        }
        if (lv > 3 && level <= 6) {
            return "Trung Kỳ[" + lv + "]";
        }
        if (lv > 6 && level <= 9) {
            return "Hậu Kỳ[" + lv + "]";
        }
        return "";
    }

    public String getNameByLevel(byte level) {
        if (level < 0 || level >= CANH_GIOI.length) return "Không xác định";
        return CANH_GIOI[level];
    }

    public String getFormatName() {
        return String.format("%s", getNameByLevel(level) + getSubLevelName(subLevel));
    }

    @Override
    public String toString() {
        return String.format("TuTien{level=%d, subLevel=%d, exp=%d/%d, linhKhi=%d/%d}",
                level, subLevel, exp, maxExp, linhKhiPoint, maxLinhKhiPoint);
    }
}
