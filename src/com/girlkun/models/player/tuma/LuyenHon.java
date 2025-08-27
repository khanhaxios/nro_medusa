/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tuma;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;

public class LuyenHon {
    public Player player;
    public int soHonDaLuyen;
    public boolean isOpen;

    public int soHonChuaLuyen;

    public int soTangDaLuyen;

    public int tongSoHonCanLuyen;

    public LuyenHon(Player player) {
        this.player = player;
    }

    public int getTongSoHonCanLuyen() {
        return Math.max(1, soTangDaLuyen) * 100_000;
    }

    public void tangBac() {
        if (soHonDaLuyen < tongSoHonCanLuyen) {
            Service.gI().sendThongBao(player, "Cần số hồn đã luyện đạt tối đa thì mới tăng bậc được");
            return;
        }
        soTangDaLuyen++;
        restHon();
        Service.gI().sendThongBao(player, "Luyện thành công " + soTangDaLuyen + " Tầng");
    }

    public int getDameBuff() {
        return Math.max(1, soTangDaLuyen) * 30;
    }

    public int getHPMPBuff() {
        return Math.max(1, soTangDaLuyen) * 30;
    }

    public int getMaKhiBuff() {
        return Math.max(1, soTangDaLuyen);
    }

    public void chuHon(int time) {
        // tang chu hon o day
        long maKhiCan = (100L * soTangDaLuyen) * time;
        if (!player.tuMa.canHandleWithMaKhiPoint(maKhiCan)) {
            Service.gI().sendThongBao(player, "Bạn không đủ ma khí cần " + maKhiCan);
            return;
        }
        if (soHonChuaLuyen - time < 0) {
            Service.gI().sendThongBao(player, "Bạn không đủ hư hồn cần" + time + " hồn");
            return;
        }
        soHonChuaLuyen -= time;
        soHonDaLuyen += time;
        Service.gI().sendThongBao(player, "Chú hồn thành công");
        player.tuMa.subMaKhi(maKhiCan);
    }

    public void calcPoint() {
        player.nPoint.dameAdd += player.nPoint.dameAdd * getDameBuff() / 100;
        player.nPoint.hpAdd += player.nPoint.hpAdd * getHPMPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpAdd * getHPMPBuff() / 100;
    }

    public void restHon() {
        soHonDaLuyen -= tongSoHonCanLuyen;
        tongSoHonCanLuyen = getTongSoHonCanLuyen();
        if (soHonDaLuyen < 0) {
            soHonDaLuyen = 0;
        }
    }

    public void showBaseMenu() {
        String text = "|7|❖═════ LUYỆN HỒN ═════❖\n" +
                "|5|➤ Tầng đã luyện: " + soTangDaLuyen + " tầng\n" +
                "|5|➤ Hư Hồn : " + soHonChuaLuyen + " Hồn\n" +
                "|5|➤ Tinh Hồn: " + soHonDaLuyen + " / " + tongSoHonCanLuyen + "\n" +
                "|5|➤ Dame Buff: " + getDameBuff() + "%\n" +
                "|5|➤ HP/MP Buff: " + getHPMPBuff() + "%\n" +
                "|5|➤ Ma Khí x: " + getMaKhiBuff() + " lần\n" +
                "|7|✪ Luyện Hồn càng cao, chỉ số buff càng mạnh!" +
                "\n|7|❖══════════════════════❖";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_HON, -1, text, "Chú hồn", "Luyện hồn", "Đóng");
    }

    public void showMenuChuHon() {

        String text = "|7|❖═════ CHÚ HỒN ═════❖\n" +

// — Hồn hiện tại —
                "|5|➤ Hư Hồn   : " + soHonChuaLuyen + " Hồn\n" +
                "|5|➤ Tinh Hồn : " + soHonDaLuyen + " / " + tongSoHonCanLuyen + "\n" +

// — Gợi mở hành động —
                "|7|✦ Bạn muốn Chú Hồn?" +
                "\n|7|❖════════════════════❖";

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CHU_HON, -1, text, "1 Lần", "100 Lần", "1000 Lần", "Đóng");
    }

    public void showMenuLuyenHon() {

        String text = "|7|❖════ LUYỆN HỒN ════❖\n" +
                "|5|➤ Tinh Hồn : " + soHonDaLuyen + " / " + tongSoHonCanLuyen + "\n" +
                "|7|✦ Bạn muốn tiếp tục luyện hồn?\n" +
                "|7|❖══════════════════❖";

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_LH, -1, text, "Luyện hồn", "Đóng");
    }

    public boolean isLuyenHon() {
        return isOpen;
    }

    public void addHon(int i) {
        soHonChuaLuyen += i;
    }

    public void open() {
        this.soHonChuaLuyen = 0;
        this.soHonDaLuyen = 0;
        this.tongSoHonCanLuyen = getTongSoHonCanLuyen();
    }
}
