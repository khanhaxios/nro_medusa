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
        if (soHonDaLuyen != tongSoHonCanLuyen) {
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
        player.nPoint.dameAdd *= ((double) getDameBuff() / 100);
        player.nPoint.hpAdd *= ((double) getHPMPBuff() / 100);
        player.nPoint.mpAdd *= ((double) getHPMPBuff() / 100);
    }

    public void restHon() {
        soHonDaLuyen -= tongSoHonCanLuyen;
        tongSoHonCanLuyen = getTongSoHonCanLuyen();
        if (soHonDaLuyen < 0) {
            soHonDaLuyen = 0;
        }
    }

    public void showBaseMenu() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖═════ LUYỆN HỒN ═════❖\n");

// — Tầng luyện hồn —
        text.append("|5|➤ Tầng đã luyện: ").append(soTangDaLuyen).append(" tầng\n");

// — Số lượng hồn —
        text.append("|5|➤ Hư Hồn : ").append(soHonChuaLuyen).append(" Hồn\n");
        text.append("|5|➤ Tinh Hồn: ").append(soHonDaLuyen).append(" / ").append(tongSoHonCanLuyen).append("\n");

// — Buff chỉ số —
        text.append("|5|➤ Dame Buff: ").append(getDameBuff()).append("%\n");
        text.append("|5|➤ HP/MP Buff: ").append(getHPMPBuff()).append("%\n");
        text.append("|5|➤ Ma Khí x: ").append(getMaKhiBuff()).append(" lần\n");

// — Lưu ý / khuyến khích —
        text.append("|7|✪ Luyện Hồn càng cao, chỉ số buff càng mạnh!");

        text.append("\n|7|❖══════════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_HON, -1, text.toString(), "Chú hồn", "Luyện hồn", "Đóng");
    }

    public void showMenuChuHon() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖═════ CHÚ HỒN ═════❖\n");

// — Hồn hiện tại —
        text.append("|5|➤ Hư Hồn   : ").append(soHonChuaLuyen).append(" Hồn\n");
        text.append("|5|➤ Tinh Hồn : ").append(soHonDaLuyen).append(" / ").append(tongSoHonCanLuyen).append("\n");

// — Gợi mở hành động —
        text.append("|7|✦ Bạn muốn Chú Hồn?");

        text.append("\n|7|❖════════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CHU_HON, -1, text.toString(), "1 Lần", "100 Lần", "1000 Lần", "Đóng");
    }

    public void showMenuLuyenHon() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖════ LUYỆN HỒN ════❖\n");

        text.append("|5|➤ Tinh Hồn : ").append(soHonDaLuyen).append(" / ").append(tongSoHonCanLuyen).append("\n");

        text.append("|7|✦ Bạn muốn tiếp tục luyện hồn?\n");

        text.append("|7|❖══════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_LH, -1, text.toString(), "Luyện hồn", "Đóng");
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
