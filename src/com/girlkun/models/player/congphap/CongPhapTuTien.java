/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.luyenkhi.TuTien;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class CongPhapTuTien extends BaseCongPhap {
    private static final int MAX_TIER = 5;
    public Player player;
    public long exp;
    public long maxExp;
    public static int MAX_LEVEL = 17;
    public int heCongPhap;
    public long lastTimeAddExp = System.currentTimeMillis();
    public long expPham;
    public long maxExpPham;
    public int level;
    public int tier;

    public int tlHoiLinhKhi; // %
    public int tlCanCot; // %
    public int canCot;

    public int tlNgoTinh; // %
    public int ngoTinh;

    public int tlGiamHoiChieu; // %

    public long linhKhiHoiMoiLan; // +
    public int tlLinhKhiHoiMoiLan; // %

    public int satThuongLinhCan; // +
    public int tlSatThuongLinhCan; // %

    public int tlStKim;
    public int tlStMoc;
    public int tlStThuy;
    public int tlStHoa;
    public int tlStTho;
    public int tlStPhong;
    public int tlStLoi;
    public int tlStQuang;
    public int tlStAm;

    public int stKim;
    public int stMoc;
    public int stThuy;
    public int stHoa;
    public int stTho;
    public int stPhong;
    public int stLoi;
    public int stQuang;
    public int stAm;

    public long tuviNhanDuoc; // +
    public int tlTuViNhanDuoc; // %

    public int tlTheChat; // %
    public int tlTinhThan; // %
    public int tlNhanhNhen; // %
    public int tlSucManh; // %

    public int theChat;
    public int tinhThan;
    public int nhanhNhen;
    public int sucManh;

    public float tangTyLeDotPha; // %
    public float tangTyLeDotPhaThienDao; // %
    public int tlExpLucNghe; // %

    public int tlGiamHoiTuVi; // %

    public List<CongPhapOption> optionCongPhaps = new ArrayList<>();
    private static final long[] BASE_MAX_EXP = new long[]{10_000, 50_000, 1_00_000, 200_000, 500_000, 1_000_000, 10_000_000, 20_000_000, 50_000_000, 70_000_000, 100_000_000, 120_000_000, 150_000_000, 250_000_000, 350_000_000, 500_000_000, 700_000_000, 950_000_000};
    private long lastTimeHoiLinhKhi;

    public CongPhapTuTien(Player player) {
        this.player = player;
    }

    public CongPhapTuTien(int id, String tenCongPhap, String mota, String thuoctinh, int maxLevel, int maxPham) {
        super(id, tenCongPhap, mota, thuoctinh, maxLevel, maxPham);
    }

    public void addExp(long ex) {
        this.exp += ex;
    }

    public void addExpPham(long e) {
        this.expPham += e;
    }

    public void restExpPham() {
        this.expPham = 0;
        this.maxExpPham = calcMaxExpPham();
    }

    public void resetPoint() {
        tlHoiLinhKhi = 0;
        tlCanCot = 0;
        canCot = 0;
        tlNgoTinh = 0;
        ngoTinh = 0;
        tlGiamHoiChieu = 0;
        linhKhiHoiMoiLan = 0;
        tlLinhKhiHoiMoiLan = 0;
        satThuongLinhCan = 0;
        tlSatThuongLinhCan = 0;
        tlStKim = 0;
        tlStMoc = 0;
        tlStThuy = 0;
        tlStHoa = 0;
        tlStTho = 0;
        tlStPhong = 0;
        tlStLoi = 0;
        tlStQuang = 0;
        tlStAm = 0;
        stKim = 0;
        stMoc = 0;
        stThuy = 0;
        stHoa = 0;
        stTho = 0;
        stPhong = 0;
        stLoi = 0;
        stQuang = 0;
        stAm = 0;
        tuviNhanDuoc = 0;
        tlTuViNhanDuoc = 0;
        tlTheChat = 0;
        tlTinhThan = 0;
        tlNhanhNhen = 0;
        tlSucManh = 0;
        theChat = 0;
        tinhThan = 0;
        nhanhNhen = 0;
        sucManh = 0;
        tangTyLeDotPha = 0;
        tangTyLeDotPhaThienDao = 0;
        tlExpLucNghe = 0;
        tlGiamHoiTuVi = 0;
    }


    private long calcMaxExpPham() {
        long maxExp = BASE_MAX_EXP[level] / 10;
        maxExp += maxExp * player.tuTien.xParam;
        maxExp += (long) (maxExp * player.tuTien.getXDiemThienPhu());
        maxExp += maxExp * (tier + 1);
        return maxExp;
    }

    public long calcMaxExp() {
        long maxExp = BASE_MAX_EXP[level];
        maxExp += maxExp * player.tuTien.xParam;
        maxExp += (long) (maxExp * player.tuTien.getXDiemThienPhu());
        maxExp += maxExp * (tier + 1);
        return maxExp;
    }

    public void restExp() {
        this.exp -= maxExp;
        if (this.exp < 0) {
            this.exp = 0;
        }
        this.maxExp = calcMaxExp();
    }

    public float getPercentTangPham(boolean isNuotPhamCaoHon) {
        if (isNuotPhamCaoHon) {
            return 100f;
        }
        return getBasePercent();
    }

    private float getBasePercent() {
        switch (level) {
            case 0:
                return 100f;
            case 1:
                return 50f;
            case 2:
                return 30f;
            case 3:
                return 20f;
            case 4:
                return 10f;
            case 5:
                return 6f;
            case 7:
                return 4f;
            case 8:
                return 3f;
            case 9:
                return 2f;
            case 10:
                return 1f;
            case 11:
                return 0.9f;
            case 12:
                return .8f;
            case 13:
                return .7f;
            case 14:
                return .6f;
            case 15:
                return .5f;
            case 16:
                return .3f;
            case 17:
                return .1f;
        }
        return 0f;
    }

    public void levelUp() {
        // tang cap cong phap
        if (exp < maxExp) {
            Service.gI().sendThongBao(player, "Công pháp chưa đủ kinh nghiệm không thể tăng cấp");
            return;
        }
        if (level + 1 > maxLevel) {
            Service.gI().sendThongBao(player, "Bạn đã đạt cấp tối đa");
            return;
        }
        if (!Util.isTrue(getBasePercent(), 100)) {
            restExp();
            Service.gI().sendThongBao(player, "Bạn đã lĩnh ngộ thất bại");
            return;
        }
        this.level += 1;
        buffChiSoOption();
        Service.gI().sendThongBao(player, "Công pháp đã tăng lên cấp [" + level + "]");
        Service.gI().point(player);
        restExp();
    }

    // tang pham
    public void canNuot(CongPhapTuTien congPhapTuTien) {
        long exp = 0;
        if (congPhapTuTien.tier > this.tier) {
            exp += maxExpPham;
        }
        exp += getExpByTier(congPhapTuTien.tier);
        addExpPham(exp);
        Service.gI().sendThongBao(player, "Cắn nuốt thành công");
    }

    public void doiCongPhap(CongPhapTuTien congPhapTuTien) {
        player.tuTien.congPhap.dispose();
        player.tuTien.congPhap = congPhapTuTien;
        player.tuTien.resetCanhGioi();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Đổi công pháp thành công");
    }

    private void dispose() {
        player = null;
        optionCongPhaps.clear();
        optionCongPhaps = null;
    }

    public void tangPham() {
        if (expPham < maxExpPham) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm");
            return;
        }
        if (tier + 1 > maxPham) {
            Service.gI().sendThongBao(player, "Công pháp đã đạt phẩm tối đa");
            return;
        }
        if (Util.isTrue(getPercentTangPham(false), 100)) {
            tier += 1;
            buffChiSoOption();
            Service.gI().point(player);
            Service.gI().sendThongBao(player, "Tăng phẩm công pháp thành công");
        } else {
            Service.gI().sendThongBao(player, "Tăng phẩm công pháp thất bại");
        }
        restExpPham();
    }

    public String getPhamName() {
        switch (tier) {
            case 0:
                return "Phàm";
            case 1:
                return "Linh";
            case 2:
                return "Vương";
            case 3:
                return "Hoàng";
            case 4:
                return "Đế";
            case 5:
                return "Tiên";
        }
        return "Không xác định";
    }

    public String getTenCongPhap() {
        return String.format("[%s]%s[%s]", getPhamName(), tenCongPhap, TuTien.CANH_GIOI[level]);
    }

    public void calcPoint() {
        resetPoint();
        for (CongPhapOption optionCongPhap : optionCongPhaps) {
            // handle option cong phap right here
            switch (optionCongPhap.id) {
                case 0:
                    tlHoiLinhKhi += optionCongPhap.param;
                    break;
                case 1:
                    tlCanCot += optionCongPhap.param;
                    break;
                case 2:
                    tlGiamHoiChieu += optionCongPhap.param;
                    break;
                case 3:
                    tlNgoTinh += optionCongPhap.param;
                    break;
                case 4:
                    canCot += optionCongPhap.param;
                    break;
                case 5:
                    ngoTinh += optionCongPhap.param;
                    break;
                case 6:
                    linhKhiHoiMoiLan += optionCongPhap.param;
                    break;
                case 7:
                    tlLinhKhiHoiMoiLan += optionCongPhap.param;
                    break;
                case 8:
                    satThuongLinhCan += optionCongPhap.param;
                    break;
                case 9:
                    tlSatThuongLinhCan += optionCongPhap.param;
                    break;
                case 10:
                    stKim += optionCongPhap.param;
                    break;
                case 11:
                    stMoc += optionCongPhap.param;
                    break;
                case 12:
                    stThuy += optionCongPhap.param;
                    break;
                case 13:
                    stHoa += optionCongPhap.param;
                    break;
                case 14:
                    stTho += optionCongPhap.param;
                    break;
                case 15:
                    stPhong += optionCongPhap.param;
                    break;
                case 16:
                    stLoi += optionCongPhap.param;
                    break;
                case 17:
                    stQuang += optionCongPhap.param;
                    break;
                case 18:
                    stAm += optionCongPhap.param;
                    break;
                case 19:
                    tuviNhanDuoc += optionCongPhap.param;
                    break;
                case 20:
                    tlTuViNhanDuoc += optionCongPhap.param;
                    break;
                case 21:
                    theChat += optionCongPhap.param;
                    break;
                case 22:
                    tinhThan += optionCongPhap.param;
                    break;
                case 23:
                    nhanhNhen += optionCongPhap.param;
                    break;
                case 24:
                    sucManh += optionCongPhap.param;
                    break;
                case 25:
                    tlTheChat += optionCongPhap.param;
                    break;
                case 26:
                    tlTinhThan += optionCongPhap.param;
                    break;
                case 27:
                    tlNhanhNhen += optionCongPhap.param;
                    break;
                case 28:
                    tlSucManh += optionCongPhap.param;
                    break;
                case 29:
                    tlStKim += optionCongPhap.param;
                    break;
                case 30:
                    tlStMoc += optionCongPhap.param;
                    break;
                case 31:
                    tlStThuy += optionCongPhap.param;
                    break;
                case 32:
                    tlStHoa += optionCongPhap.param;
                    break;
                case 33:
                    tlStTho += optionCongPhap.param;
                    break;
                case 34:
                    tlStPhong += optionCongPhap.param;
                    break;
                case 35:
                    tlStLoi += optionCongPhap.param;
                    break;
                case 36:
                    tlStQuang += optionCongPhap.param;
                    break;
                case 37:
                    tlStAm += optionCongPhap.param;
                    break;
                case 38:
                    tangTyLeDotPha += optionCongPhap.param;
                    break;
                case 39:
                    tangTyLeDotPhaThienDao += optionCongPhap.param;
                    break;
                case 40:
                    tlExpLucNghe += optionCongPhap.param;
                    break;
                case 41:
                    tlGiamHoiTuVi += optionCongPhap.param;
                    break;
            }
        }
    }

    public void buffChiSoOption() {
        for (CongPhapOption optionCongPhap : optionCongPhaps) {
            optionCongPhap.param += getParam(optionCongPhap.id);
        }
    }

    private int getParam(int id) {
        switch (id) {
            case 0:
                return Math.max(1, tier);
            case 1, 3:
                return 2 * tier;
            case 2:
                return 2 * Math.max(tier - 2, 1);
            case 4, 5:
                return Util.nextInt(1, 10 * tier);
            case 6:
                return Util.nextInt(1, 1000 * tier);
            case 7:
                return 5 * tier;
            case 8:
                return Util.nextInt(1, 100000 * tier);
            case 9:
                return tier;
            case 10, 11, 12, 13, 14, 15, 16, 17, 18:
                return Util.nextInt(1, 100000 * tier);
            case 19:
                return Util.nextInt(1, 1000 * tier);
            case 20:
                return 2 * tier;
            case 21, 22, 23, 24:
                return Util.nextInt(1, 5 * tier);
            case 25, 26, 27, 28:
                return 5 * tier;
            case 29, 30, 31, 32, 33, 34, 35, 36, 37:
                return 2 * tier;
            case 38:
                return tier;
            case 39:
                return tier / 5;
            case 40:
                return 5 * tier;
            case 41:
                return Math.max(1, tier);
        }
        return 0;
    }

    public int getTimeHoiExp() {
        int timeHoi = 12_000;
        return timeHoi;
    }

    public void update() {
        if (tenCongPhap != null) {
            if (Util.canDoWithTime(lastTimeAddExp, getTimeHoiExp())) {
                addExp(getExpCanGain());
                lastTimeAddExp = System.currentTimeMillis();
            }
        }
    }

    public long getTimeHoiLinhKhi() {
        int time = 12_000;
        time -= time * tlHoiLinhKhi / 100;
        return time;
    }

    public long getTimeAddTuVi() {
        int time = 12_000;
        time -= time * tlGiamHoiTuVi / 100;
        return time;
    }

    public void showBaseMenu() {
        if (tenCongPhap == null) {
            Service.gI().sendThongBao(player, " Bạn chưa có công pháp nào");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Công pháp").append("\n");
        stringBuilder.append("|5|").append(getTenCongPhap()).append("\n");
        stringBuilder.append("|2|Kinh Nghiệm : ").append(getCurrentExpAsString()).append("\n");
        stringBuilder.append("|2|Phẩm : ").append(getPhamExpAsString()).append("\n");
        stringBuilder.append("|5|Hệ công pháp : ").append(getHeCongPhapAsString()).append("\n");
        stringBuilder.append("|2|").append(thuoctinh).append("\n");
        stringBuilder.append("|5|").append(mota).append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_TU_TIEN, -1, stringBuilder.toString(), "Tăng cấp", "Tăng phẩm", "Thuộc tính");
    }

    public void showThuocTinh() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Thuộc tính công pháp").append("\n");
        for (CongPhapOption optionCongPhap : optionCongPhaps) {
            stringBuilder.append("|2|").append(optionCongPhap.getName()).append("\n");
        }
        NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, stringBuilder.toString(), "Đóng");
    }

    public void showMenuTangCap() {
        String stringBuilder = "|7|Tăng cấp công pháp" + "\n" +
                "|2|Cấp hiện tại : " + level + "\n" +
                "|2|Kinh nghiệm hiện tại : " + Util.powerToString(exp) + "\n" +
                "|2|Kinh nghiệm cần để tăng cấp : " + Util.powerToString(maxExp) + "\n" +
                "|2|Tỷ lệ lĩnh ngộ thành công : " + getBasePercent() + "%\n";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_TU_TIEN_TANG_CAP, 1, stringBuilder, "Tăng cấp", "Đóng");
    }

    public void showMenuTangPham() {
        String stringBuilder = "|7|Tăng phẩm công pháp" + "\n" +
                "|2|Phẩm hiện tại : " + getPhamName() + "\n" +
                "|2|Kinh nghiệm hiện tại : " + Util.powerToString(expPham) + "\n" +
                "|2|Kinh nghiệm cần để tăng phẩm : " + Util.powerToString(maxExpPham) + "\n" +
                "|2|Tỷ lệ lĩnh ngộ thành công : " + getPercentTangPham(false) + "%\n";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_TU_TIEN_TANG_PHAM, 1, stringBuilder, "Tăng phẩm", "Đóng");
    }

    public String getHeCongPhapAsString() {
        return heCongPhap == 0 ? "Kim" : heCongPhap == 1 ? "Mộc" : heCongPhap == 2 ? "Thủy" : heCongPhap == 3 ? "Hỏa" : heCongPhap == 4 ? "Thổ" : heCongPhap == 5 ? "Phong" : heCongPhap == 6 ? "Lôi" : heCongPhap == 7 ? "Quang" : "Ám";
    }

    private String getPhamExpAsString() {
        return Util.powerToString(expPham) + "/" + Util.powerToString(maxExpPham);
    }

    private String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    private long getExpCanGain() {
        long exp = BASE_MAX_EXP[level] / 10000;
        long dtt = player.tuTien.congPhap.tier + 1 + Util.nextInt(10);
        if (player.luyenDanSu.isLuyenDan() && player.luyenDanSu.danDuocEffect.isBuffCongPhap()) {
            dtt *= (long) player.luyenDanSu.danDuocEffect.xBuffCongPhap;
        }
        exp += dtt;
        return exp;
    }

    private long getExpByTier(int tier) {
        return switch (tier) {
            case 0 -> 100;
            case 1 -> 200;
            case 2 -> 1000;
            case 3 -> 2000;
            case 4 -> 5000;
            case 5 -> 10000;
            default -> 0;
        };
    }

    public boolean canDotPha(int level) {
        return level + 1 <= this.level;
    }

    public boolean isLearn() {
        return tenCongPhap != null && id >= 0;
    }

    public void ratioNewOption(int sl) {
        for (int i = 0; i < sl; i++) {
            CongPhapOption congPhapOption = CongPhapOptionTemplate.getTienOption(Util.nextInt(CongPhapOptionTemplate.congPhapTienOptions.size() - 10));
            if (congPhapOption != null) {
                optionCongPhaps.add(congPhapOption);
            }
        }
    }
}
