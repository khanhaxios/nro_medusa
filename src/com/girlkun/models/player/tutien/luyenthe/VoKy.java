/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Arrays;

public class VoKy {
    public int id;
    private static final int MAX_BAC = 5;
    public long doThuanThuc;
    public long maxDoThuanThuc;
    public String tenVoKy;
    public Player player;
    public String moTaVoKy;
    public int bac;
    public int type;
    public int[] buff = new int[]{};
    public long lastTimeCoolDown;

    public long timeCoolDown;

    public long calcMaxDoThuanThuc() {
        return Math.max(1, bac) * 100_000L;
    }

    public VoKy(Player player) {
        this.player = player;
    }

    public VoKy(int id, String tenVoKy, String moTaVoKy, int type) {
        this.id = id;
        this.tenVoKy = tenVoKy;
        this.moTaVoKy = moTaVoKy;
        this.type = type;
    }

    public long getExpCanGain() {
        return 2L * Util.nextInt(1, 2) * Math.max(bac, 1);
    }

    public void addDoThuanThuc(long doThuanThuc) {
        this.doThuanThuc += doThuanThuc;
        if (this.doThuanThuc > maxDoThuanThuc) {
            this.doThuanThuc = maxDoThuanThuc;
        }
    }

    public String getBaseMenuText() {
        String stringBuilder = "|7|Thông tin võ kỹ" + "\n" +
                "|5|" + tenVoKy + "[" + getDoThuanThucVoKy() + "\n" +
                "|5|" + moTaVoKy.replace("#", "?") + "\n";
        return stringBuilder;
    }

    public void addNewBuff() {
        int[] newBuff = Arrays.copyOf(buff, buff.length + 1);
        switch (type) {
            case 0:
                newBuff[newBuff.length - 1] = Util.nextInt(1, 10) * Math.max(bac, 1);
                break;
            case 1:
                newBuff[newBuff.length - 1] = Util.nextInt(1, 15) * Math.max(bac, 1);
                break;
            case 2:
                newBuff[newBuff.length - 1] = Util.nextInt(1, 6) * Math.max(bac, 1);
                break;
            case 3:
                newBuff[newBuff.length - 1] = Util.nextInt(1, 3) * Math.max(bac, 1);
                break;
        }
        this.buff = newBuff;
    }

    public void init() {
        bac = 0;
        restDoTT();
        addNewBuff();
    }

    public void tangBac() {
        if (!canNangBac()) {
            Service.gI().sendThongBaoOK(player, "Độ thuần thục chưa đủ hoặc bạn đã đạt bậc tối đa");
            return;
        }
        float ratio = getTyLeDotPha();
        if (Util.isTrue(ratio, 100)) {
            bac += 1;
            restDoTT();
            addNewBuff();
            Service.gI().point(player);
            Service.gI().sendThongBao(player, "Đột phá võ kỹ thành công");
        } else {
            subDoTTPercent(20);
            Service.gI().sendThongBao(player, "Đột phá võ kỹ thất bại bạn bị tổn thương căn cơ");
            this.lastTimeCoolDown = System.currentTimeMillis();
            this.timeCoolDown = 60 * 60 * 1000;
        }
    }

    private void subDoTTPercent(int i) {
        doThuanThuc -= maxDoThuanThuc * i / 100;
        if (doThuanThuc < 0) {
            doThuanThuc = 0;
        }
    }

    private void restDoTT() {
        doThuanThuc = 0;
        this.maxDoThuanThuc = calcMaxDoThuanThuc();
    }

    private boolean canNangBac() {
        return doThuanThuc == maxDoThuanThuc && bac + 1 <= MAX_BAC;
    }

    public void showBaseMenu() {
        player.iDMark.voKySelected = this;
        String stringBuilder = "|7|Thông tin võ kỹ" + "\n" +
                "|5|" + tenVoKy + "[" + getDoThuanThucVoKy() + "]" + "\n" +
                "|5|" + moTaVoKy.replace("#", "?") + "\n" +
                "|5|Độ thuần thục " + getCurrentExpAsString() + "\n" +
                "|7|Tỷ lệ đột phá " + getTyLeDotPha() + "%" + "\n" +
                "|7|Võ kỹ thuần thục càng cao thì càng buff nhiều" + "\n";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_VO_KY, -1, stringBuilder, "Đột phá", "Xem\nThông Tin");
    }

    public void showMenuDotPha() {
        if (bac + 1 > MAX_BAC) {
            Service.gI().sendThongBao(player, "Bạn đã đạt bậc tối đa");
            return;
        }
        player.iDMark.voKySelected = this;
        String stringBuilder = "|7|Đột phá võ kỹ" + "\n" +
                "|5|Hiện tại : " + tenVoKy + "[" + bac + "]" + "\n" +
                "|5|Bậc kế : " + tenVoKy + "[" + (bac + 1) + "]" + "\n" +
                "|7|Tỷ lệ thành công : " + getTyLeDotPha() + "\n" +
                "|7|Thất bại sẽ giảm 50% độ thuần thục , thành công sẽ reset độ thuần thục";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DOT_PHA_VK, -1, stringBuilder, "Đột phá");
    }

    public void calcPoint() {
        for (int i = 0; i < buff.length; i++) {
            switch (type) {
                case 0:
                    player.nPoint.tlDame.add(buff[i]);
                    // cong suc danh
                    break;
                case 1:
                    player.nPoint.tlDameCrit.add(buff[i]);
                    // cong sat thuong chi mang
                    break;
                case 2:
                    player.nPoint.tyLeGiamDame += buff[i];
                    // cong khang dame
                    break;
                case 3:
                    player.nPoint.tlNeDon += buff[i];
                    break;
            }
        }

    }

    private float getTyLeDotPha() {
        return switch (bac) {
            case 0 -> 100f;
            case 1 -> 50f;
            case 2 -> 10f;
            case 3 -> 1f;
            case 4 -> .5f;
            case 5 -> .3f;
            default -> .1f;
        };
    }

    public void showMenuThongTinBuff() {
        String stringBuilder = "|7|Thông tin tăng phúc võ kỹ" + "\n" +
                getBuff();
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TP_VK, -1, stringBuilder, "Đóng");
    }

    private String getCurrentExpAsString() {
        return Util.powerToString(doThuanThuc) + "/" + Util.powerToString(maxDoThuanThuc);
    }

    public String getDoThuanThucVoKy() {
        switch (bac) {
            case 0:
                return "Nhập môn";
            case 1:
                return "Tiểu thành";
            case 2:
                return "Đại thành";
            case 3:
                return "Sơ khuy môn kính";
            case 4:
                return "Xuất thần nhập hóa";
            case 5:
                return "Đăng phong tạo cực";
        }
        return "Chưa nhập môn";
    }

    public String getBuff() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < buff.length; i++) {
            stringBuilder.append("|5|").append(moTaVoKy.replace("#", String.valueOf(buff[i]))).append("\n");
        }
        return stringBuilder.toString();
    }

    public float getCurrentPercent() {
        return 100f * doThuanThuc / maxDoThuanThuc;
    }
}
