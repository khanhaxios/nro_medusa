package com.girlkun.models.player.tuma;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class LinhCanTuMa {
    public static float MAX_LINH_CAN_PARAM = 2;
    Player player;
    public String tenLinhCan;
    public String moTaLinhCan;
    public float xParam;
    public long maKhiDaNuot;
    public byte phamChat;
    public long maKhiCanNuot;

    public byte typeLinhCan;

    public LinhCanTuMa(Player player) {
        this.player = player;
    }

    public boolean canDuongLinh() {
        return phamChat + 1 <= MAX_LINH_CAN_PARAM;
    }

    public void tangPham() {
        // push chi so
        if (Util.isTrue(1, 100)) {
            xParam += .2f;
        } else if (Util.isTrue(2, 100)) {
            xParam += .1f;
        } else {
            xParam += .02f;
        }
        phamChat++;
        restMaKhi();
    }

    public void ratioNewLinhCan() {
        if (Util.isTrue(5, 100)) {
            xParam = .5f;
        } else if (Util.isTrue(10, 100)) {
            xParam = .3f;
        } else {
            xParam = .1f;
        }
    }

    public long calcMaKhiCanNuot() {
        return (phamChat + 1) * 10_000_000;
    }

    public void restMaKhi() {
        maKhiDaNuot = 0;
        maKhiCanNuot = calcMaKhiCanNuot();
    }

    public LinhCanTuMa(String tenLinhCan, String moTaLinhCan, short xParam, byte typeLinhCan) {
        this.tenLinhCan = tenLinhCan;
        this.moTaLinhCan = moTaLinhCan;
        this.xParam = xParam;
        this.typeLinhCan = typeLinhCan;
    }

    public String getMoTaLinhCan() {
        return moTaLinhCan.replace("#", String.valueOf(xParam));
    }

    public void showBaseMenu() {
        String text = "|7|Thông tin Ma Linh Căn\n|5|" + tenLinhCan + "\n|5|" + getMoTaLinhCan();
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MA_TU_LINH_CAN, -1, text, "Dưỡng\nLinh Căn", "Đóng");
    }

    public void duongLinhCan() {
        String text = "|7|Bồi dưỡng linh căn\n|5|" + tenLinhCan + "[" + getPercentMakhi() + "]\n" + "|5|Linh chú [ " + getMaKhiString() + "]" + "\n|1|Bạn muốn dưỡng mấy lần?";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DUONG_LINH_CAN, -1, text, "1 lần", "10 lần", "100 lần", "Đóng");
    }

    private String getMaKhiString() {
        return Util.powerToString(maKhiDaNuot) + "/" + Util.powerToString(maKhiCanNuot);
    }

    public void duongLinhCan(int time) {
        if (!canDuongLinh()) {
            Service.gI().sendThongBao(player, "Linh căn đã đạt phẩm tối đa không thể dưỡng thêm");
            return;
        }
        // tinh xem luong ma khi can co du hay ko
        long maKhiCanThiet = player.tuMa.maKhiPoint / time;
        if (!player.tuMa.canHandleWithMaKhiPoint(maKhiCanThiet)) {
            Service.gI().sendThongBao(player, "Bạn không đủ ma khí cần " + maKhiCanThiet);
            return;
        }
        // cong % ma khi
        maKhiDaNuot += maKhiCanThiet;
        player.tuMa.subMaKhi(maKhiCanThiet);
        if (maKhiDaNuot >= maKhiCanNuot) {
            tangPham();
            Service.gI().sendThongBao(player, "Bồi dưỡng thành công Linh căn đã tăng phẩm");
            return;
        }
        Service.gI().sendThongBao(player, "Bồi dưỡng thành công Ma Linh tăng lên một chút");
    }

    private String getPercentMakhi() {
        return (maKhiDaNuot / maKhiCanNuot * 100) + "%";
    }
}
