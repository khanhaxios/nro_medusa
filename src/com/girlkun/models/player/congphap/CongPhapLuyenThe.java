/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CongPhapLuyenThe extends BaseCongPhap {

    private int level;
    private int type;
    private long exp;
    private long maxExp;

    private int tier;

    private long expTier;
    private long maxExpTier;
    private Player player;


    public List<CongPhapOption> congPhapOptions = new ArrayList<>();

    private static final long[] BASE_MAX_EXP = new long[]{10_000, 50_000, 1_00_000, 200_000, 500_000, 1_000_000, 10_000_000, 20_000_000, 50_000_000, 70_000_000, 100_000_000, 120_000_000, 150_000_000, 250_000_000, 350_000_000, 500_000_000, 700_000_000, 950_000_000};

    public CongPhapLuyenThe() {
    }

    public CongPhapLuyenThe(Player player) {
        this.player = player;
    }

    public CongPhapLuyenThe(int id, String tenCongPhap, String mota, String thuoctinh, int maxLevel, int maxPham) {
        super(id, tenCongPhap, mota, thuoctinh, maxLevel, maxPham);
    }

    public static int getParamBuff(int otpId, int level) {
        return getParam(otpId, level);
    }

    public static int getParam(int id, int tier) {
        switch (id) {
            case 0:
                return Util.nextInt(1, 30) * tier;
            case 1:
                return Util.nextInt(1, 5) * tier;
            case 2:
                return Util.nextInt(1, 10) * tier;
            case 3:
                return Util.nextInt(1, 20) * tier;
            case 4:
                return Util.nextInt(1, 10) * tier;
            case 5:
                return Util.nextInt(1, 20) * tier;
            case 6:
                return Util.nextInt(1, 10) * tier;
        }
        return 0;
    }


    public void addExp(long exp) {
        this.exp += exp;
    }

    public long calcMaxExp() {
        return BASE_MAX_EXP[level] * 100_000;
    }

    public void addExpTier(long expTier) {
        this.expTier += expTier;
    }

    public void doiCongPhap(CongPhapLuyenThe congPhapLuyenThe) {
        if (congPhapLuyenThe == null || congPhapLuyenThe.tenCongPhap == null) {
            Service.gI().sendThongBao(player, "Có lỗi xảy ra không thể hấp thụ công pháp này");
            return;
        }
        //
        resetLevel();
        player.luyenThe.reset();
        player.luyenThe.congPhapLuyenThe = congPhapLuyenThe;
        Service.gI().sendThongBao(player, "Đã đổi công pháp");
        Service.gI().point(player);
    }

    private void resetLevel() {
        this.level = 10;
        restExp();
    }

    public void nuotCongPhap(CongPhapLuyenThe congPhapLuyenThe) {
        long expAdd = getMaxExpGain(congPhapLuyenThe);
        this.addExpTier(expAdd);
        Service.gI().sendThongBao(player, "Bạn đã nuốt công pháp " + congPhapLuyenThe.tenCongPhap);
    }

    private long getMaxExpGain(CongPhapLuyenThe congPhapLuyenThe) {
        long ep = 0;
        if (congPhapLuyenThe != null) {
            if (congPhapLuyenThe.getTier() > this.tier) {
                ep = this.maxExpTier;
            } else {
                ep = (this.maxExpTier / 200) * Util.nextInt(1, 6);
            }
        }
        return ep;
    }

    public long calcMaxExpTier() {
        return BASE_MAX_EXP[tier] * 1000;
    }

    public void levelUp() {
        if (exp < maxExp) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm");
            return;
        }
        int nextLevel = level + 1;
        if (nextLevel > maxLevel) {
            Service.gI().sendThongBao(player, "Đã đạt cấp tối đa của công pháp");
            return;
        }
        if (!Util.isTrue(getBasePercent(), 100)) {
            Service.gI().sendThongBao(player, "Tăng cấp thất bại");
            restExp();
            return;
        }
        level = nextLevel;
        buffOption();
        Service.gI().point(player);
        restExp();
        Service.gI().sendThongBao(player, "Nâng cấp thành công");
    }

    public void buffOption() {
        for (CongPhapOption congPhapOption : congPhapOptions) {
            switch (congPhapOption.id) {
                case 0:
                    congPhapOption.param += Util.nextInt(1, 30);
                    break;
                case 1:
                    congPhapOption.param += Util.nextInt(1, 5);
                    break;
                case 2:
                    congPhapOption.param += Util.nextInt(1, 10);
                    break;
                case 3:
                    congPhapOption.param += Util.nextInt(1, 20);
                    break;
                case 4:
                    congPhapOption.param += Util.nextInt(1, 10);
                    break;
                case 5:
                    congPhapOption.param += Util.nextInt(1, 20);
                    break;
                case 6:
                    congPhapOption.param += Util.nextInt(1, 10);
                    break;
            }
        }
    }

    private void restExp() {
        exp -= maxExp;
        if (exp <= 0) {
            exp = 0;
        }
        maxExp = calcMaxExp();
    }

    public float getDameBuffByLevel() {
        int soube = level;
        return Math.max(1, soube) * 10;
    }

    public int getTimeHoiExp() {
        int timeHoi = 6_000;
        return timeHoi;
    }

    public float getHpMpBuffByLevel() {
        int soube = level;
        return Math.max(1, soube) * 20;
    }

    public double getDameBuff() {
        double baseBuff = 100_000;
        baseBuff += baseBuff * getDameBuffByTier() / 100;
        for (CongPhapOption congPhapOption : congPhapOptions) {
            if (congPhapOption.id == 2) {
                baseBuff += baseBuff * congPhapOption.param / 100;
            }
        }
        return baseBuff * getDameBuffByLevel() / 100;
    }

    public float getDameBuffByTier() {
        return Math.max(tier, 1) * 50;
    }

    public float getHpMPBuffByTier() {
        return Math.max(tier, 1) * 100;
    }

    public double getHpMpBuff() {
        double baseBuff = 300_000;
        baseBuff += baseBuff * getHpMPBuffByTier() / 100;
        for (CongPhapOption congPhapOption : congPhapOptions) {
            if (congPhapOption.id == 2) {
                baseBuff += baseBuff * congPhapOption.param / 100;
            }
        }
        return baseBuff * getHpMpBuffByLevel() / 100;
    }

    public long lastTimeHoiExp;
    public long lastTimeHoiTuVi;

    public long lastTimeHoiChanKhi;

    public void update() {
        // update cong phap exp
        if (level >= 0 && tenCongPhap != null) {
            if (Util.canDoWithTime(lastTimeHoiExp, getTimeHoiExp())) {
                addExp(getExpCanGain());
                lastTimeHoiExp = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiTuVi, getTimeHoiTuVi())) {
                player.luyenThe.addExp(player.luyenThe.getExpCanGain());
                lastTimeHoiTuVi = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiChanKhi, getTimeHoiChanKhi())) {
                player.luyenThe.addChanKhi(getChanKhiHoi());
                lastTimeHoiChanKhi = System.currentTimeMillis();
            }
            // hoi tu vi luyen the
        }
    }

    public void showBaseMenu() {
        if (tenCongPhap == null) {
            Service.gI().sendThongBao(player, "Bạn chưa học công pháp");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("|7|Thông tin công pháp").append("\n");
        sb.append("|5|").append(getFullName()).append("\n");
        sb.append("|5|").append(getCurrentExpAsString()).append("\n");
        sb.append("|2|").append("Dame +").append(getDameBuff()).append("\n");
        sb.append("|2|").append("HpMp +").append(getHpMpBuff()).append("\n");
        sb.append("|1|Cấp tiếp theo : ").append(level + 1).append("\n");
        sb.append("|2|Cấp tối đa : ").append(maxLevel).append("\n");
        sb.append("|7|Phẩm tối đa : ").append(maxPham).append("\n");
        sb.append("|5|").append(mota).append("\n");
        sb.append("|5|").append(thuoctinh).append("\n");
        String[] options = new String[]{"Tăng cấp", "Tăng phẩm", "Thuộc tính", "Đóng"};
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_LT, -1, sb.toString(), options);
    }

    public void showMenuTangCap() {
        StringBuilder sb = new StringBuilder();
        sb.append("|7|Tăng cấp công pháp").append("\n");
        sb.append("|5|").append(getFullName()).append("\n");
        sb.append("|5|").append(getCurrentExpAsString()).append("\n");
        sb.append("|7|Tỷ lệ thành công : ").append(getBasePercent()).append("%").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TC_CP_LT, -1, sb.toString(), "Tăng cấpa", "Đóng");
    }

    public float getPercentTangPham() {
        switch (tier) {
            case 0:
                return 100f;
            case 1:
                return 50f;
            case 2:
                return 20f;
            case 3:
                return 1f;
            case 4:
                return .5f;
            case 5:
                return .3f;
        }
        return .3f;
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


    private String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    private String getFullName() {
        return String.format("[%s]%s[%s]", getPhamName(), tenCongPhap, (double) (level * 555));
    }

    public String getPhamName() {
        return getPhamNam(tier);
    }

    public String getPhamName(int tier) {
        return getPhamNam(tier);
    }

    public static String getPhamNam(int tier) {
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

    public void nangPham() {
        if (expTier < maxExpTier) {
            Service.gI().sendThongBao(player, "Bạn chưa đủ kinh nghiệm");
            return;
        }
        int nextTier = tier + 1;
        if (nextTier > maxPham) {
            Service.gI().sendThongBao(player, "Công pháp đã đạt phẩm tối đa");
            return;
        }
        if (!Util.isTrue(getPercentTangPham(), 100)) {
            Service.gI().sendThongBao(player, "Tăng phẩm thất bại");
            restExpTier();
            return;
        }
        tier = nextTier;
        buffOption();
        Service.gI().point(player);
        restExpTier();
        Service.gI().sendThongBao(player, "Công pháp đã nâng phẩm thành công");
    }

    private void restExpTier() {
        this.expTier -= maxExpTier;
        if (expTier < 0) {
            expTier = 0;
        }
        maxExpTier = calcMaxExpTier();
    }


    private long getChanKhiHoi() {
        return (long) Math.max(1, level) * 10 * Util.nextInt(1, 5);
    }

    private int getTimeHoiChanKhi() {
        return 5_000;
    }

    private long getTimeHoiTuVi() {
        long timeHoiTuVi = 3_000;
        return timeHoiTuVi;
    }


    public long getExpCanGain() {
        long baseExp = BASE_MAX_EXP[level] / 100_000;
        if (player.luyenDanSu.danDuocEffect.xBuffCongPhap > 0) {
            baseExp += (long) (baseExp * player.luyenDanSu.danDuocEffect.xBuffCongPhap);
        }
        for (CongPhapOption congPhapOption : congPhapOptions) {
            if (congPhapOption.id == 1) {
                baseExp += baseExp * congPhapOption.param / 100;
            }
        }
        return baseExp;
    }

    public void calcPoint() {
        player.nPoint.dameAdd += getDameBuff();
        player.nPoint.hpAdd += getHpMpBuff();
        player.nPoint.mpAdd += getHpMpBuff();
    }

    public boolean isLearn() {
        return tenCongPhap != null && level >= 0;
    }

    public void showMenuTangPham() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Tăng phẩm công pháp").append("\n");
        stringBuilder.append("|5|Phẩm hiện tại : ").append(getPhamName()).append("\n");
        stringBuilder.append("|5|Phẩm tiếp theo : ").append(getPhamName(tier + 1)).append("\n");
        stringBuilder.append("|7|Tỷ lệ tăng phẩm : ").append(getPercentTangPham()).append("%").append("\n");
        stringBuilder.append("|7|Bạn muốn?");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_LT_TP, -1, stringBuilder.toString(), "Tăng phẩm", "Đóng");
    }

    public String getThuocTinh() {
        String str = "";
        for (CongPhapOption congPhapOption : congPhapOptions) {
            str += "|2|" + congPhapOption.getName() + "\n";
        }
        return str;
    }

    public void showMenuThuocTinh() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Thuộc tính công pháp").append("\n");
        stringBuilder.append(getThuocTinh()).append("\n");
        stringBuilder.append("Dame +").append(getDameBuff()).append("\n");
        stringBuilder.append("Hp,Mp + ").append(getHpMpBuff()).append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, stringBuilder.toString(), "Đóng");
    }

    public void hocCongPhap() {
        // hoc cong phap luyen the
        if (player.luyenThe.congPhapLuyenThe.isLearn()) {
            player.iDMark.congPhapLt = this;
            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DOI_CP_LT, -1, "Bạn đã học công pháp rồi bạn muốn?", "Đổi CP", "Nuốt CP");
            return;
        }
        player.luyenThe.congPhapLuyenThe = this;
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Bạn đã học công pháp " + tenCongPhap);
    }
}
