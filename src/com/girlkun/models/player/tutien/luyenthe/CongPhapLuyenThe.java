/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class CongPhapLuyenThe {
    public static String[] NAME_BY_TYPE = new String[]{"Hồng Mông Luyện Thể Quyết", "Bàn Cổ Luyện Thể Quyết"};
    private static final int MAX_TANG = 9;
    public byte tang;
    public byte giaiDoan;

    public long exp;
    public long maxExp;
    public byte type;
    public Player player;
    public String tenCongPhap;

    public long expGiaiDoan;
    public long maxExpGiaiDoan;

    public CongPhapLuyenThe(Player player) {
        this.player = player;
    }

    public long getExpCanGain() {
        long base = 10L * Util.nextInt(1, 2);
        if (type == 0) {
            base *= 5;
        }
        return base;
    }

    public boolean canLevelUp() {
        return tang + 1 < MAX_TANG && exp == maxExp && giaiDoan == 8;
    }

    public void levelUp() {
        if (!canLevelUp()) {
            if (tang + 1 > MAX_TANG) {
                Service.gI().sendThongBao(player, "Đã nâng tối đa");
                return;
            }
            if (exp != maxExp) {
                Service.gI().sendThongBaoOK(player, "Chưa đủ kinh nghiệm công pháp");
                return;
            }
            if (giaiDoan < 8) {
                Service.gI().sendThongBao(player, "Bạn đột phá đủ 9 giai đoạn thì mới có thể nâng tầng");
                return;
            }
        }
        tang += 1;
        restExp();
        Service.gI().sendThongBaoOK(player, "Đột phá tầng công pháp thành công");
    }

    public void hocCongPhap(byte type) {
        if (isLearn()) {
            Service.gI().sendThongBao(player, "Bạn đã học công pháp rồi");
            return;
        }
        tang = 0;
        giaiDoan = 0;
        restExp();
        restExpGiaiDoan();
        this.type = type;
        tenCongPhap = getGameByType();
        PlayerDAO.subvnd(player, 300_000);
        Service.gI().sendThongBao(player, "Bạn đã học thành công " + getGameByType());
    }

    private String getGameByType() {
        return NAME_BY_TYPE[type];
    }

    public void levelDown() {
        if (tang - 1 < 0) {
            return;
        }
        tang -= 1;
        restExp();
        Service.gI().sendThongBao(player, "Công pháp của bạn thụt lùi một tầng");
    }

    public void addExpGiaiDoan(long ex) {
        this.expGiaiDoan += ex;
        if (this.expGiaiDoan > maxExpGiaiDoan) {
            this.expGiaiDoan = maxExpGiaiDoan;
        }
    }

    public void restExpGiaiDoan() {
        this.expGiaiDoan = 0;
        this.maxExpGiaiDoan = calcMaxExpGiaiDoan();
    }

    private long calcMaxExpGiaiDoan() {
        long maxExp = 10_000 * Math.max(giaiDoan, 1);
        if (type == 1) {
            maxExp *= 5;
        }
        return maxExp;
    }

    public void addExp(long e) {
        this.exp += e;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    public long calcMaxExp() {
        if (type == 1) {
            return Math.max(tang, 1) * 100_000_000L;
        }
        return Math.max(tang, 1) * 20_000_000L;
    }

    public void restExp() {
        this.exp = 0;
        this.maxExp = calcMaxExp();
    }

    public float getDameBuff() {
        float tlDameBuff = Math.max(tang, 1) * 100;
        if (type == 1) {
            tlDameBuff *= 2;
        }
        tlDameBuff += getDameBuffGiaiDoan();
        return tlDameBuff;
    }

    private float getDameBuffGiaiDoan() {
        float gd = giaiDoan * .1f;
        return gd + (0.1f * 10) * tang;
    }

    public float getHPBuff() {
        float tlHpBuff = Math.max(tang, 1) * 120;
        if (type == 1) {
            tlHpBuff *= 2.5;
        }
        tlHpBuff += getHPMPBuffTheoGD();
        return tlHpBuff;
    }

    private float getHPMPBuffTheoGD() {
        float gd = giaiDoan * .1f;
        return gd + (0.3f * 10) * tang;
    }

    public float getMpBuff() {
        float tlHpBuff = Math.max(tang, 1) * 120;
        if (type == 1) {
            tlHpBuff *= 2.5;
        }
        tlHpBuff += getHPMPBuffTheoGD();
        return tlHpBuff;
    }

    public float getPercentLevelGiaiDoanUp() {
        return 100 - (tang * giaiDoan * 1.5f);
    }

    public void dotPhaGiaiDoan() {
        if (expGiaiDoan != maxExpGiaiDoan) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm để đột phá");
            return;
        }
        if (giaiDoan + 1 > 8) {
            Service.gI().sendThongBao(player, "Bạn đã đạt tới giai đoạn tối đa");
            return;
        }
        if (player.inventory.ruby - giaiDoan * 10_000 < 0) {
            Service.gI().sendThongBao(player, "Cần x" + giaiDoan * 10_000 + " hồng ngọc để phá giai");
            return;
        }
        player.inventory.ruby -= (giaiDoan * 10_000);
        Service.gI().sendMoney(player);
        if (Util.isTrue(getPercentLevelGiaiDoanUp(), 100)) {
            giaiDoan += 1;
            Service.gI().sendThongBao(player, "Phá giai thành công");
            Service.gI().point(player);
        } else {
            Service.gI().sendThongBao(player, "Phá giai thất bại");
        }
        restExpGiaiDoan();
    }

    public void showMenuPhaGiai() {

        String sb = "|7|❖════ PHÁ GIAI ĐOẠN ❖\n" +
                "|7|" + tenCongPhap + "\n" +
                "|5|➤ Giai đoạn hiện tại [" + giaiDoan + " / 8]\n" +
                "|5|➤ EXP Giai đoạn      : " + Util.numberToMoney(expGiaiDoan) + " / " + Util.numberToMoney(maxExpGiaiDoan) + "\n" +
                "|5|➤ Tỷ lệ thành công   : " + String.format("%.1f", getPercentLevelGiaiDoanUp()) + "%\n" +
                "|7|✦ Khi EXP đạt tối đa, bạn có thể phá giai đoạn để mạnh hơn!" +
                "\n|7|❖════════════════════❖";

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_PG, -1, sb, "Phá giai", "Đóng");
    }

    public void showBaseMenu() {
        if (!this.isLearn()) {
            Service.gI().sendThongBao(player, "Bạn chưa học công pháp");
            return;
        }

        String sb = "|7|❖════ THÔNG TIN CÔNG PHÁP LUYỆN THỂ ❖\n" +

// — Tên công pháp —
                "|7|" + tenCongPhap + "\n" +

// — Tầng + Giai đoạn —
                "|5|➤ Tầng hiện tại " + tang + " / " + MAX_TANG + "\n" +
                "|5|➤ Giai đoạn " + giaiDoan + " / 8\n" +

// — EXP các loại —
                "|5|➤ EXP Giai đoạn " + Util.numberToMoney(expGiaiDoan) + " / " + Util.numberToMoney(maxExpGiaiDoan) + "\n" +
                "|5|➤ EXP Công pháp " + Util.numberToMoney(exp) + " / " + Util.numberToMoney(maxExp) + "\n" +

// — Buff các chỉ số —
                "|5|➤ Buff Dame  +" + (int) getDameBuff() + "%\n" +
                "|5|➤ Buff HP  +" + (int) getHPBuff() + "%\n" +
                "|5|➤ Buff MP  +" + (int) getMpBuff() + "%\n" +

// — Tỷ lệ nâng giai đoạn —
                "|5|➤ Tỷ lệ nâng giai đoạn: " + String.format("%.1f", getPercentLevelGiaiDoanUp()) + "%\n" +
                "|7|❖══════════════════════════❖";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CP_LT, -1, sb, "Phá tầng", "Phá giai", "Đóng");
    }

    public void calcPoint() {
        player.nPoint.dameAdd += player.nPoint.dameAdd * getDameBuff() / 100;
        player.nPoint.hpAdd += player.nPoint.hpAdd * getHPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpAdd * getMpBuff() / 100;
    }

    public boolean isLearn() {
        return tang >= 0 && tenCongPhap != null;
    }
}
