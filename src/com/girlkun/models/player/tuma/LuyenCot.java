/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tuma;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class LuyenCot {
    private static final float MAX_TANG = 9;
    public int giaiDoan;
    public boolean isOpen;

    public int soTang;

    public Player player;
    public long slManhCot;
    public long maxSlManhCot;

    public final int MAX_GIAI_DOAN = 360;
    public final int COST_MANH_COT = 100;

    public LuyenCot(Player player) {
        this.player = player;
    }

    public long calcMaxSlManhCot() {
        return (long) Math.max(soTang, 1) * 1000 * 360;
    }

    public float getDameBuff() {
        // Mỗi giai đoạn +1% dame, mỗi tầng reset tăng +360%
        return (Math.max(giaiDoan, 1) * Math.max(soTang, 1)) * 1.0f;
    }

    public float getHPMPBuff() {
        // Mỗi giai đoạn +2% HP/MP, mỗi tầng reset tăng +720%
        return (Math.max(giaiDoan, 1) * Math.max(soTang, 1)) * 2.0f;
    }

    public void calcPoint() {
        player.nPoint.dameAdd += player.nPoint.dameAdd * getDameBuff() / 100f;
        player.nPoint.hpAdd += player.nPoint.hpAdd * getHPMPBuff() / 100f;
        player.nPoint.mpAdd += player.nPoint.mpAdd * getHPMPBuff() / 100f;
    }

    public void addManhCot(long value) {
        slManhCot += value;
        if (slManhCot > maxSlManhCot) {
            slManhCot = maxSlManhCot;
        }
    }

    public void luyenCot() {
        if (giaiDoan >= MAX_GIAI_DOAN) {
            Service.gI().sendThongBao(player, "Đã đạt tối đa giai đoạn, hãy đột phá tầng!");
            return;
        }

        if (slManhCot < COST_MANH_COT) {
            Service.gI().sendThongBao(player, "Không đủ Mảnh Cốt (cần 100)");
            return;
        }
        slManhCot -= COST_MANH_COT;
        // Tỷ lệ thành công giảm theo giai đoạn: ví dụ từ 100% -> 1%
        int successRate = Math.max(1, 100 - giaiDoan / 4); // Tùy chỉnh logic bạn muốn

        if (!Util.isTrue(successRate, 100)) {
            Service.gI().sendThongBao(player, "Luyện Cốt thất bại!");
            return;
        }
        // Thành công

        giaiDoan++;
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Luyện Cốt thành công! Giai đoạn: " + giaiDoan + "/" + MAX_GIAI_DOAN);
    }

    public void dotphatang() {
        if (soTang + 1 > MAX_TANG) {
            Service.gI().sendThongBao(player, "Đã luyện đến tầng tối đa");
            return;
        }
        if (giaiDoan < MAX_GIAI_DOAN) {
            Service.gI().sendThongBao(player, "Chưa đủ giai đoạn để đột phá tầng");
            return;
        }
        if (slManhCot < maxSlManhCot) {
            Service.gI().sendThongBao(player, "Chưa đủ mảnh cốt");
            return;
        }

        float successRate = getTyLeThanhCongDotPha();
        if (!Util.isTrue(successRate, 100)) {
            Service.gI().sendThongBao(player, "Đột phá tầng thất bại!");
            slManhCot = 0;
            return;
        }
        soTang++;
        giaiDoan = 0;
        slManhCot = 0;
        maxSlManhCot = calcMaxSlManhCot();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Đột phá tầng thành công! Tầng mới: " + soTang);
    }

    public void openMenuLuyenCot() {
        if (!isOpen) {
            Service.gI().sendThongBaoOK(player, "Bạn chưa học luyện cốt hãy đến Vách Núi Đen tìm Npc Tu Ma để học");
            return;
        }
        String sb = "|7|❖═════ LUYỆN CỐT ═════❖\n" +
                "|2|➤ Giai đoạn: " + giaiDoan + "/" + MAX_GIAI_DOAN + "\n" +
                "|2|➤ Tầng hiện tại: " + soTang + "\n" +
                "|5|➤ Buff Dame: +" + getDameBuff() + "%\n" +
                "|5|➤ Buff HP/MP: +" + getHPMPBuff() + "%\n" +
                "|2|➤ Mảnh Cốt: " + slManhCot + " / " + maxSlManhCot + "\n" +
                "|7|✦ Bạn muốn...?";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_COT, -1, sb,
                "Luyện giai đoạn", "Đột phá tầng", "Hướng\ndẫn", "Đóng");
    }

    public void showHuongDanLuyenCot() {
        String hd = "|7|❖ HƯỚNG DẪN LUYỆN CỐT ❖\n" +
                "|5|- Mỗi lần luyện tiêu hao 100 Mảnh Cốt\n" +
                "|5|- Giai đoạn càng cao → tỉ lệ thành công càng thấp\n" +
                "|5|- Đạt 360 giai đoạn sẽ được Đột phá tầng\n" +
                "|5|- Đột phá sẽ tăng tầng, reset giai đoạn về 0\n" +
                "|5|- Mỗi tầng tăng mạnh chỉ số dame, HP, MP\n";
        NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, hd, "Đóng");
    }

    public void showMenuLuyenCot() {
        String sb = "|7|❖═════ LUYỆN CỐT ═════❖\n" +
                "|5|- Dùng |2|100 Mảnh Cốt để luyện 1 giai đoạn\n" +
                "|5|- Giai đoạn càng cao → tỉ lệ thành công càng thấp\n" +
                "|5|- Khi đạt |2|360 giai đoạn sẽ được đột phá tầng\n" +
                "|5|- Mỗi tầng giúp tăng chỉ số sức mạnh cực lớn\n" +
                "|7|➤ Cố gắng tu luyện, thành tựu sẽ đến!";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_LUYEN_COT, -1, sb, "Luyện", "Đóng");
    }

    public void showMenuDotPhaTang() {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DOT_PHA_TANG, -1,
                "|7|❖ Bạn thật sự muốn đột phá tầng?\n" +
                        "|5|- Điều kiện: đủ 360 giai đoạn\n" +
                        "|5|- Tỷ lệ thành công:" + getTyLeThanhCongDotPha() + " \n" +
                        "|5|- Nếu thất bại sẽ mất thời gian tu luyện!",
                "Đồng ý", "Từ chối");
    }

    private float getTyLeThanhCongDotPha() {
        return 100 - (soTang * 10);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void openSystem() {
        this.isOpen = true;
        this.giaiDoan = 0;
        this.soTang = 0;
        this.maxSlManhCot = calcMaxSlManhCot();
        this.slManhCot = 0;
        Service.gI().sendThongBaoOK(player, "Bạn đã học thành công luyện cốt");
    }
}
