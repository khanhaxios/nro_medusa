/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tuma;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BaseTuDuy;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.models.player.tutien.khongthisu.KhongThiSu;
import com.girlkun.models.player.tutien.luyendansu.LuyenDanSu;
import com.girlkun.models.player.tutien.luyenkhi.TuTien;
import com.girlkun.models.player.tutien.tranphapsu.TranPhapSu;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class TuMa extends BaseTuDuy implements IBaseAction {
    public static final int MAX_LEVEL = 180;
    public boolean isAttackWithLinhCan = false;
    Player player;
    public LuyenHon luyenHon;
    public long maKhiPoint;
    public LuyenCot luyenCot;
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
        luyenHon = new LuyenHon(player);
        luyenCot = new LuyenCot(player);
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
        long exp = Math.min((long) (targetMob.point.maxHp / 1_000_000) * Math.max(player.tuMa.congPhapTuMa.phamChat, 1), Util.nextInt(1000, 50000));
        if (player.nPoint.xTuVi > 0) {
            exp += exp * player.nPoint.xTuVi / 100;
        }
        return exp;
    }

    @Override
    public void levelUp() {
        if (Util.isTrue(level, 180)) {
            Service.gI().sendThongBao(player, "Bạn đã nhập ma quá sâu dẫn đến đột phá thất bại");
            restExp();
            return;
        }
        this.level += 1;
        if (level / 10 != 0 && level % 10 == 0) {
            maTinh += 2;
        }
        if (player.nhiemVuDeTu != null) {
            player.nhiemVuDeTu.checkDoneTaskDotPha();
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
        m += m * player.nPoint.xLinhKhi / 100;
        m *= 3;
        return m;
    }

    public void restExp() {
        this.exp = 0;
        this.maxExp = getNextLevelExp();
    }

    private long getNextLevelExp() {
        return (LEVEL_EXP[level / 10] + SUB_LEVEL_EXP[level / 10]) * (congPhapTuMa.phamChat + 700);
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

    public String getMaTinhDanhGia() {
        if (maTinh >= 1 && maTinh <= 3) {
            return "Ma Nhân";
        }
        if (maTinh >= 4 && maTinh <= 6) {
            return "Ma Đầu";
        }
        if (maTinh >= 7 && maTinh <= 9) {
            return "Ma Sát";
        }
        if (maTinh >= 10 && maTinh <= 12) {
            return "Ma Quân";
        }
        if (maTinh >= 13 && maTinh <= 15) {
            return "Ma Tôn";
        }
        if (maTinh >= 16 && maTinh <= 18) {
            return "Ma Đế";
        }
        if (maTinh >= 19 && maTinh <= 20) {
            return "Ma Thần";
        }
        return "Vô Danh Ma"; // fallback cho giá trị không hợp lệ hoặc = 0
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
            linhCanTuMa1.ratioNewLinhCan();
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
        Service.gI().sendThongBao(player, "Bạn đã học được " + congPhapTuMa1.ten);
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
        float percentBuff = (getBaseBuffByLevel(1) + (getSubLevelOtherBuff()));
        percentBuff *= (congPhapTuMa.phamChat + 1 + maTinh);
        return percentBuff;
    }

    @Override
    public float getHPMPBuff() {
        float percentBuff = (getBaseBuffByLevel(3) + (getSubLevelHpMpBuff()));
        percentBuff *= (congPhapTuMa.phamChat + 1 + maTinh);
        return percentBuff;
    }

    private float getBaseBuffByLevel(float multiplier) {
        return (Math.max(this.level / 10, 1)) * multiplier;
    }

    private float getSubLevelOtherBuff() {
        return Math.max(1f, (this.level % 10) * 1f);
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
        String text = "|7|❖═════ THÔNG TIN MA TU ═════❖\n" +
//— Cấp độ & kinh nghiệm —
                "|5|➤" + getName() + "/" + "\n" +
                "|5|➤ Kinh nghiệm " + getCurrentExpAsString() + "\n" +
//— Ma Khí —
                "|5|➤ Ma Khí " + getMaKhiAsString() + "\n" +
                "|5|➤ Ma Tính " + getMaTinhDanhGia() + "\n" +
//— Thời gian tu luyện —
                "|5|➤ Đã tu luyện " + getYearOpened() + "\n" +
//— Dòng đặc biệt Ma Đạo —
                "|7|✪ Ma Tu không có bình cảnh – phá giới vô tận!\n" +
                "|7|❖══════════════════════════════❖";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BASE_TU_MA, -1, text, "Thông Tin\nTu Ma", "Thông Tin\nCông Pháp", "Thông Tin\nLinh Căn", "Thông tin\nLuyện Hồn", "Pháp bảo", "Luyện Cốt", "Đóng");
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
        player.nPoint.xDameLinhCan += tinhThan;
        player.nPoint.tlDameCrit.add(nhanhNhen * 100);
        if (congPhapTuMa != null && congPhapTuMa.ten != null) {
            congPhapTuMa.calcPoint();
            player.nPoint.tlHutHp += getHutHPBuff();
            player.nPoint.tlHutMp += getHutMPBuff();
        }
        if (luyenHon != null && luyenHon.isOpen) {
            luyenHon.calcPoint();
        }
        if (luyenCot.isOpen()) {
            luyenCot.calcPoint();
        }
    }

    public void showMenuTuMa() {

        String text = "|7|❖═════ THÔNG TIN MA TU ═════❖\n" +
                "|5|➤ Dame Buff: " + getDameBuff() + "%\n" +
                "|5|➤ HP/MP Buff: " + getHPMPBuff() + "%\n" +
                "|1|➤ Thể Chất: +" + theChat + " Điểm\n" +
                "|1|➤ Sức Mạnh: +" + sucManh + " Điểm\n" +
                "|1|➤ Tốc Độ: +" + nhanhNhen + " Điểm\n" +
                "|1|➤ Tinh Thần: +" + tinhThan + " Điểm\n" +
                "|5|➤ Ma Khí: " + getMaKhiAsString() + "\n" +
                "|5|➤ Tu vi: " + getCurrentExpAsString() + "\n" +
                "|7|✪ Ma Tu không có bình cảnh – phá giới vô hạn!\n" +
                "|7|❖══════════════════════════❖";

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MA_TU_DOT_PHA, -1, text, "Đột phá", "Đóng");
    }


    public void dotPha() {
        if (canLevelUp()) {
            levelUp();
        } else {
            Service.gI().sendThongBao(player, "Tu vi chưa đủ");
        }
    }

    public void nhapMa() {
        player.tuTien = new TuTien(player);
        player.luyenDanSu = new LuyenDanSu(player);
        player.tranPhapSu = new TranPhapSu(player);
        player.khongThiSu = new KhongThiSu(player);
        // remove all tu tien data
        player.tuMa = new TuMa(player);
        player.tuMa.openSystem();
        player.tuMa.ratioCongPhap();
        Service.gI().sendThongBaoOK(player, "Bạn đã nhập ma thành công");
    }

    public void openLuyenHon() {
        player.tuMa.luyenHon.isOpen = true;
        player.tuMa.luyenHon.open();
        player.tuMa.luyenHon.open();
        Service.gI().sendThongBao(player, "Đã mở luyện hồn");
    }

    public void subExp(long l) {
        exp -= l;
        if (exp < 0) exp = 0;
    }

    public void reset() {
        level = 0;
        congPhapTuMa = new CongPhapTuMa(player);
        linhCanTuMa = new LinhCanTuMa(player);
        luyenHon = new LuyenHon(player);
        luyenCot = new LuyenCot(player);
        exp = 0;
        maxExp = getNextLevelExp();
        maKhiPoint = 0;
        maKhiPoint = calcMaxMaKhi();
        maTinh = 0;
        timeTuMa = System.currentTimeMillis();
        Service.gI().sendThongBao(player, "Bạn đã tán công tu ma");
    }
}
