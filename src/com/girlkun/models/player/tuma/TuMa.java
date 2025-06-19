package com.girlkun.models.player.tuma;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class TuMa implements IBaseAction {
    public static final int MAX_LEVEL = 180;
    Player player;
    public long maKhiPoint;
    public long maxMaKhiPoint;
    public int level;
    public long exp;
    public long maxExp;
    public long timeTuMa = System.currentTimeMillis();
    public LinhCanTuMa linhCanTuMa;
    public int maTinh;

    public CongPhapTuMa congPhapTuMa;

    public static final String[] CANH_GIOI = new String[]{"Luyện Hồn", "Ngưng Ma", "Huyết Đan", "Ma Anh", "Hóa Ma", "Ma Tôn", "Hắc Ma", "U Linh", "Quỷ Hồn", "Ma Du", "Vô Ảnh", "Tam Huyết", "Tứ Hồn", "Dạ Ma Sát Cảnh", "Thống Ma Chi Chủ", "Tà Ma Đại Đế", "Vạn Tà Chi Thể", "Ma Lộ Vô Cực", "Chân Ma Thánh Tôn"};
    public static final long[] LEVEL_EXP = new long[]{100, 200, 500, 1000, 5000, 60000, 200000, 3000000, 5000000, 10000000, 12_000_000, 15_000_000, 20_000_000, 30_000_000, 50_000_000, 70_000_000, 100_000_000, 150_000_000, 500_000_000}; // 19
    public static final long[] SUB_LEVEL_EXP = new long[]{100, 150, 200, 250, 300, 320, 350, 360, 370, 399};
    public static final long[] BASE_LINH_KHI = new long[]{1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000, 20000000, 50000000, 100000000, 200000000, 500000000, 1000000000};
    public static final long[] BASE_SUB_LINH_KHI = new long[]{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
    public static final long[] BASE_EXP_BUFF = new long[]{1, 2, 5, 10, 15, 20, 25, 50, 100, 120, 140, 200, 210, 230, 300, 350, 360, 400, 500};


    public TuMa(Player player) {
        this.player = player;
        congPhapTuMa = new CongPhapTuMa(player);
        linhCanTuMa = new LinhCanTuMa(player);
    }

    public boolean canHandleWithMaKhiPoint(long maKhi) {
        return maKhiPoint - maKhi >= 0;
    }

    public boolean canHandleWithMaKhiPercent(int percent) {
        if (percent <= 0) return false;
        if (percent > 100) percent = 100; // giới hạn tối đa 100%
        long required = maxMaKhiPoint * percent / 100;
        return maKhiPoint >= required;
    }

    public void subMaKhi(long maKhiChuMaCan) {
        this.maKhiPoint -= maKhiChuMaCan;
        if (maKhiPoint < 0) maKhiPoint = 0;
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        return Math.max((long) (targetMob.point.maxHp / 100_000), Util.nextInt(10, 50));
    }

    @Override
    public void levelUp() {
        this.level += 1;
        if (level / 10 != 0 && level % 10 == 0) {
            maTinh += 1;
        }
        restExp();
        restMaKhi();
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã đột phá thành công " + getName());
    }

    public void restMaKhi() {
        maxMaKhiPoint = calcMaxMaKhi();
    }

    private long calcMaxMaKhi() {
        long m = (BASE_LINH_KHI[level / 10] + BASE_SUB_LINH_KHI[level % 10]) * (congPhapTuMa.phamChat + 1 + maTinh);
        return m;
    }

    public void restExp() {
        this.exp = 0;
        this.maxExp = getNextLevelExp();
    }

    private long getNextLevelExp() {
        return (LEVEL_EXP[level / 10] + SUB_LEVEL_EXP[level / 10]) * (congPhapTuMa.phamChat + 50);
    }

    @Override
    public void levelDown() {
        level -= 1;
        restExp();
        restMaKhi();
        Service.gI().sendThongBao(player, "Bạn đã bị tẩu hỏa nhập ma cảnh giới giảm xuống một cảnh");
    }

    @Override
    public void resetLevel() {

    }

    @Override
    public float getLevelUpPercent() {
        return 0;
    }

    @Override
    public void openSystem() {
        // mo system
        maTinh = 1;
        ratioLinhCan();
        ratioCongPhap();
        level += 1;
        restExp();
        restMaKhi();
        timeTuMa = System.currentTimeMillis();
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã mở tu ma");
    }

    public void ratioLinhCan() {
        int typeLinhCan = Util.nextInt(0, 4);
        LinhCanTuMa linhCanTuMa1 = null;
        for (LinhCanTuMa canTuMa : TuMaTemplate.LINH_CAN) {
            if (canTuMa.typeLinhCan == typeLinhCan) {
                linhCanTuMa1 = canTuMa;
            }
        }
        if (linhCanTuMa1 != null) {
            linhCanTuMa1.player = player;
            this.linhCanTuMa = linhCanTuMa1;
        }
    }

    public void update() {
        if (congPhapTuMa != null && congPhapTuMa.ten != null) {
            congPhapTuMa.update();
        }
    }

    public void ratioCongPhap() {
        int typeLinhCan = Util.nextInt(0, 4);
        CongPhapTuMa congPhapTuMa1 = TuMaTemplate.CONG_PHAP.get(typeLinhCan);
        congPhapTuMa1.player = player;
        this.congPhapTuMa = congPhapTuMa1;
    }

    public boolean canLevelUp() {
        return exp == maxExp;
    }

    public String getSubName() {
        int phanDu = level % 10;

        if (phanDu == 0) return "Sơ kỳ" + "[" + phanDu + "]";
        if (phanDu >= 1 && phanDu <= 3) return "Sơ kỳ" + "[" + phanDu + "]";
        if (phanDu >= 4 && phanDu <= 6) return "Trung kỳ" + "[" + phanDu + "]";
        if (phanDu >= 7) return "Hậu kỳ" + "[" + phanDu + "]";
        return "Sơ kỳ" + "[" + phanDu + "]"; // fallback an toàn
    }

    @Override
    public String getName() {
        int index = level / 10;
        if (index < 0 || index >= CANH_GIOI.length) {
            return "Vô Danh " + getSubName();
        }
        return CANH_GIOI[index] + " " + getSubName();
    }

    @Override
    public String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    @Override
    public float getDameBuff() {
        float percentBuff = (getBaseBuffByLevel(5) + (getSubLevelOtherBuff()));
        percentBuff *= (congPhapTuMa.phamChat + 1 + maTinh);
        return percentBuff;
    }

    @Override
    public float getHPMPBuff() {
        float percentBuff = (getBaseBuffByLevel(10) + (getSubLevelHpMpBuff()));
        percentBuff *= (congPhapTuMa.phamChat + 1 + maTinh);
        return percentBuff;
    }

    private float getBaseBuffByLevel(float multiplier) {
        return (Math.max(this.level / 10, 1)) * multiplier;
    }

    private float getSubLevelOtherBuff() {
        return Math.max(2f, (this.level % 10) * 2f);
    }

    private float getSubLevelOtherBuff(float pt) {
        return Math.max(pt, (this.level % 10) * pt);
    }

    private float getSubLevelHpMpBuff() {
        return Math.max(3f, (this.level % 10) * 3f);
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
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(5f) * Math.max(1, level));
    }


    @Override
    public float getHutMPBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(5f) * Math.max(1, level));
    }

    @Override
    public float getNeBuff() {
        return 0;
    }

    @Override
    public float getChinhXacBuff() {
        return 0;
    }

    public void addExp(long exp) {
        this.exp += exp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    private String getYearOpened() {
        long timeUsedMillis = System.currentTimeMillis() - timeTuMa;
        long minutesUsed = timeUsedMillis / (1000 * 60);
        long yearsOpened = minutesUsed / 10;

        return Util.powerToString(yearsOpened) + " năm";
    }

    public void showBaseMenu() {
        String text = "|7|Thông Tin Ma Tu\n" + "|5|" + getName() + "\n" + "|5|Tu vi : " + getCurrentExpAsString() + "\n" + "Ma khí : " + getMaKhiAsString() + "\n|5| Đã tu ma " + getYearOpened() + "\n" + "|1|Khi đầy exp ấn đột phá";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BASE_TU_MA, -1, text, "TT\nTu Ma", "TT\nCông Pháp", "TT\nLinh Căn", "Đóng");
    }

    private String getMaKhiAsString() {
        return Util.powerToString(maKhiPoint) + "/" + Util.powerToString(maxMaKhiPoint);
    }

    public void addMaKhi(long maKhiAdd) {
        this.maKhiPoint += maKhiAdd;
        if (maKhiPoint > maxMaKhiPoint) {
            maKhiPoint = maxMaKhiPoint;
        }
    }

    public boolean isTuMa() {
        return level >= 1;
    }

    public void calcPoint() {
        if (congPhapTuMa != null && congPhapTuMa.ten != null) {
            congPhapTuMa.calcPoint();
        }
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHutMPBuff();
    }

    public void showMenuTuMa() {
        String text = "|7|Thông Tin Ma Tu\n" + "|5|Dame Buff : " + getDameBuff() + "%\n" + "|5| HpMp Buff : " + getHPMPBuff() + "%\n|7|Ma tu không có bình cảnh";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MA_TU_DOT_PHA, -1, text, "Đột phá", "Đóng");
    }

    public void dotPha() {
        if (canLevelUp()) {
            levelUp();
        }
    }
}
