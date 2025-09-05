/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.models.player.Player;
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
    public List<CongPhapOption> optionCongPhaps = new ArrayList<>();
    private static final long[] BASE_MAX_EXP = new long[]{10_000, 50_000, 1_00_000, 200_000, 500_000, 1_000_000, 10_000_000, 20_000_000, 50_000_000, 70_000_000, 100_000_000, 120_000_000, 150_000_000, 250_000_000, 350_000_000, 500_000_000, 700_000_000, 950_000_000};

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

    private long calcMaxExpPham() {
        long maxExp = BASE_MAX_EXP[level] / 10;
        maxExp += maxExp * player.tuTien.xParam;
        maxExp += maxExp * player.tuTien.getXDiemThienPhu();
        maxExp += maxExp * tier;
        return maxExp;
    }

    public long calcMaxExp() {
        long maxExp = BASE_MAX_EXP[level];
        maxExp += maxExp * player.tuTien.xParam;
        maxExp += maxExp * player.tuTien.getXDiemThienPhu();
        maxExp += maxExp * tier;
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
        if (level + 1 > MAX_LEVEL) {
            Service.gI().sendThongBao(player, "Bạn đã đạt cấp tối đa");
            return;
        }
        if (!Util.isTrue(getBasePercent(), 100)) {
            restExp();
            Service.gI().sendThongBao(player, "Bạn đã lĩnh ngộ thất bại");
            return;
        }
        this.level += 1;
        Service.gI().sendThongBao(player, "Công pháp đã tăng lên cấp [" + level + "]");
        Service.gI().point(player);
        restExp();
    }

    // tang pham
    public void canNuot(CongPhapTuTien congPhapTuTien) {
        long exp = 0;
        if (congPhapTuTien.tier <= this.tier) {
            exp += getExpByTier(congPhapTuTien.tier);
        } else {
            exp += maxExpPham;
            exp += getExpByTier(congPhapTuTien.tier);
        }
        addExpPham(exp);
        Service.gI().sendThongBao(player, "Cắn nuốt thành công");
    }

    public void doiCongPhap(CongPhapTuTien congPhapTuTien) {

    }

    public void tangPham() {
        if (expPham < maxExpPham) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm");
            return;
        }
        if (tier + 1 > MAX_TIER) {
            Service.gI().sendThongBao(player, "Công pháp đã đạt phẩm tối đa");
            return;
        }
        if (Util.isTrue(getPercentTangPham(false), 100)) {
            tier += 1;
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
        return String.format("[%s]%s[%s]", getPhamName(), tenCongPhap, "Cấp" + level + 1);
    }

    public void calcPoint() {
        for (CongPhapOption optionCongPhap : optionCongPhaps) {
            // handle option cong phap right here
            switch (optionCongPhap.id) {
            }
        }
    }

    public int getTimeHoiExp() {
        int timeHoi = 12_000;
        return timeHoi;
    }

    public void update() {
        if (tenCongPhap != null) {
            if (Util.canDoWithTime(lastTimeAddExp, getTimeHoiExp())) {
                addExp(getExpCanGain());
            }
        }
    }

    private long getExpCanGain() {
        return BASE_MAX_EXP[level] / 1000;
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
}
