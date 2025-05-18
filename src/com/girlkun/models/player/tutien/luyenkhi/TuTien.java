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
    private static final String[] CANH_GIOI = new String[]{"Luyện Khí",
            "Trúc Cơ", "Kim Đan", "Nguyên Anh", "Hóa Thần",
            "Phong Thánh", "Thần Chiếu", "Huyền Linh", "Quy Nguyên",
            "Du Tầm", "Không Luân", "Tam Thiên", "Tứ Trụ", "Dạ Ma Thiên Cảnh",
            "Tu Di Sơn Chủ", "Tinh Hà Thánh Nhân", "Thần Quỷ Mạt Trắc", "Đạo Lộ Chi Cảnh",
            "Thánh Tôn Chi Cảnh"};
    private static final long[] LEVEL_EXP = new long[]{100, 200, 500, 1000, 5000, 60000, 200000, 3000000, 5000000, 10000000, 12_000_000, 15_000_000, 18_000_000, 20_000_000, 25_000_000, 30_000_000, 40_000_000, 60_000_000, 80_000_000}; // 19
    private static final long[] SUB_LEVEL_EXP = new long[]{100, 150, 200, 250, 300, 320, 350, 360, 370, 399};
    private static final long[] BASE_LINH_KHI = new long[]{1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000, 20000000, 50000000, 100000000, 200000000, 500000000, 1000000000};
    private static final long[] BASE_EXP_BUFF = new long[]{1, 2, 5, 10, 15, 20, 25, 50, 100, 120, 140, 200, 210, 230, 300, 350, 360, 400, 500};

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
            /* 18 */10_000_000L};
    private static final float[] LEVEL_UP_PERCENT = new float[]{
            50, 30, 25, 20, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 1, .3f};
    public LinhCan linhCan;
    public List<TienPhap> tienPhaps;
    public byte MAX_SL_TIEN_PHAP = 1;
    public CongPhap congPhap;

    public TuTien(Player player) {
        super(player);
        linhCan = new LinhCan();
        congPhap = new CongPhap(this);
        tienPhaps = new ArrayList<>();
    }

    public byte getMaxSLTPByLV() {
        if (level > 4 && level <= 8) {
            return 2;
        }
        if (level > 8 && level <= 12) {
            return 4;
        }
        if (level > 12 && level <= 18) {
            return 8;
        }
        return 2;
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
        if (subLevel == 10) {
            if (level < CANH_GIOI.length) {
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
        restExp();
        restLinhKhi();
        Service.gI().point(player);
    }

    public void calcPoint() {
        // add buff vo day sau do se call lai point
        // buff dame goc o day
        player.nPoint.dameAdd += player.nPoint.dameg * getDameBuff() / 100;
        player.nPoint.hpAdd += player.nPoint.hpg * getHPMPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpg * getMaxExp() / 100;
        player.nPoint.defAdd += player.nPoint.defg * getDefBuff() / 100;
        player.nPoint.tlchinhxac += player.nPoint.tlchinhxac * getChinhXacBuff() / 100;
        player.nPoint.tlNeDon += player.nPoint.tlNeDon * getNeBuff() / 100;
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHutMPBuff();
        if (linhCan != null) {
            player.nPoint.tlDameCrit.add((int) linhCan.getThuocTinhLinhCan().getParam());
        }
        if (congPhap != null) {
            congPhap.calcPoint(player);
        }
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
        return Math.max(1f, this.subLevel * 1f);
    }

    private float getSubLevelOtherBuff(float pt) {
        return Math.max(pt, this.subLevel * pt);
    }

    private float getSubLevelHpMpBuff() {
        return Math.max(1.5f, this.subLevel * 1.5f);
    }

    @Override
    public float getLevelUpPercent() {
        if (level + 1 <= LEVEL_UP_PERCENT.length - 1) {
            return getXDiemThienPhu() + LEVEL_UP_PERCENT[level + 1];
        }
        return 1f;
    }

    @Override
    public void openSystem() {
        if (player.luyenThe.level < 10) {
            Service.gI().sendThongBao(player, "Bạn cần đạt luyện thể cấp 10 để bắt đầu tu tiên");
            return;
        }
        level = 0;
        subLevel = 1;
        linhCan = ratioLinhCan();
        ratioThienPhu();
        restExp();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã mở hệ thống tu tiên");
    }

    public void ratioThienPhu() {
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
        return getBaseBuffByLevel(10) + (getSubLevelOtherBuff() * level);
    }

    @Override
    public float getHPMPBuff() {
        return getBaseBuffByLevel(12) + (getSubLevelHpMpBuff() * level);
    }

    @Override
    public float getDefBuff() {
        return getBaseBuffByLevel(5) + (getSubLevelOtherBuff() * level);
    }

    @Override
    public float getPSTBuff() {
        return getBaseBuffByLevel(2) + (getSubLevelOtherBuff(0.1f) * level);
    }

    @Override
    public float getHutHPBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(0.1f) * level);
    }

    @Override
    public float getHutMPBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(0.1f) * level);
    }

    @Override
    public float getNeBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(0.1f) * level);
    }

    @Override
    protected long getNextLevelExp() {
        int nextLv = Math.min(level, LEVEL_EXP.length - 1);
        int nextSubLv = Math.min(subLevel, SUB_LEVEL_EXP.length - 1);
        return LEVEL_EXP[nextLv] + SUB_LEVEL_EXP[nextSubLv];
    }

    @Override
    public float getChinhXacBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff() * level);
    }

    public void update() {
        if (isTuTien()) {
            // dau tien la cong exp //
            if (exp < maxExp) {
                long expAdd = (long) (getXDiemThienPhu() * (BASE_EXP_BUFF[level] + (SUB_LEVEL_EXP[subLevel] / 10)));
                addExp(expAdd);
            }
            if (linhKhiPoint < maxLinhKhiPoint) {
                hoiPhucLinhKhi();
            }
            if (congPhap.doThuanThuc < congPhap.maxDoThuanThuc) {
                congPhap.autoAddDoTT();
            }
            // tu dong use linh ky
            for (TienPhap tienPhap : tienPhaps) {
                tienPhap.useTienPhap(player, null, null);
            }
        }
    }


    public void addExp(long pn) {
        this.exp += pn;
        if (exp > maxExp) {
            exp = maxExp;
        }
    }

    public String toJsonString() {
        return null;
    }

    public String getSubLevelName(byte lv) {
        if (lv > 0 && subLevel <= 3) {
            return "Sơ Kỳ[" + lv + "]";
        }
        if (lv > 3 && subLevel <= 6) {
            return "Trung Kỳ[" + lv + "]";
        }
        if (lv > 6 && subLevel <= 9) {
            return "Hậu Kỳ[" + lv + "]";
        }
        return "";
    }

    public String getNameByLevel(byte level) {
        if (level < 0 || level >= CANH_GIOI.length) return "Không xác định";
        return CANH_GIOI[level];
    }

    public String getFormatName() {
        return String.format("%s", getNameByLevel(level) + " " + getSubLevelName(subLevel));
    }

    @Override
    public String toString() {
        return String.format("TuTien{level=%d, subLevel=%d, exp=%d/%d, linhKhi=%d/%d}", level, subLevel, exp, maxExp, linhKhiPoint, maxLinhKhiPoint);
    }

    private CongPhap getCongPhapByLinhCan(int index) {
        List<CongPhap> congPhaps = new ArrayList<>();
        for (CongPhap phap : TuTienTemplate.CONG_PHAP) {
            if (phap.thuoctinh == index) {
                congPhaps.add(phap);
                break;
            }
        }
        return congPhaps.get(Util.nextInt(0, congPhaps.size() - 1));
    }

    public void hocCongPhap(int select) {
        if (congPhap != null) {
            Service.gI().sendThongBao(player, "Bạn đã học công pháp rồi mà.");
            return;
        }
        CongPhap cpTem = getCongPhapByLinhCan(select);
        if (cpTem.thuoctinh != linhCan.getLinhCanType()) {
            Service.gI().sendThongBao(player, "Công pháp không phù hợp với linh căn của bạn");
            return;
        }
        cpTem.tuTien = this;
        congPhap = cpTem;
        congPhap.ratioNewCongPhap();
        // random chi so
        Service.gI().sendThongBao(player, "Bạn đã học " + congPhap.getFullName());
    }

    public void ratioNewTienPhap() {
        MAX_SL_TIEN_PHAP = getMaxSLTPByLV();
        if (tienPhaps.size() + 1 > MAX_SL_TIEN_PHAP) {
            Service.gI().sendThongBao(player, "Bạn đã đạt giới hạn tiên pháp");
            return;
        }

        // add tien phap
        TienPhap tienPhap = null;
        List<TienPhap> canRandomTP = new ArrayList<>();
        // get tiem phap chua ton tai trong list tien phap
        for (TienPhap phap : TuTienTemplate.TIEN_PHAP) {
            boolean hasOwner = false;
            for (TienPhap tienPhap1 : tienPhaps) {
                if (phap.getId() == tienPhap1.getId()) {
                    hasOwner = true;
                }
            }
            if (!hasOwner) {
                canRandomTP.add(phap);
            }
        }
        if (canRandomTP.size() > 0) {
            tienPhap = canRandomTP.get(Util.nextInt(0, canRandomTP.size() - 1));
        }
        if (tienPhap == null) {
            Service.gI().sendThongBao(player, "Bạn đã học hết Tiên Pháp hiện có");
            return;
        }
        tienPhaps.add(tienPhap);
        Service.gI().sendThongBao(player, "Bạn đã học " + tienPhap.getName());
    }

    public String getInfoStr() {
        String str = "";
        // handle process string
        return str;
    }

    public boolean isTuTien() {
        if (level == 0) {
            return subLevel > 0;
        } else if (level > 0) {
            return true;
        }
        return false;
    }
}
