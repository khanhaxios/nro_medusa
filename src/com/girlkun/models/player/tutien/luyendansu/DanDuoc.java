package com.girlkun.models.player.tutien.luyendansu;

public class DanDuoc {
    public String tenDanDuoc;
    public byte capDanDuoc;
    public int capDoYeuCauDeSuDung;
    public int id;

    public int quantity;

    public DanDuoc() {

    }

    public DanDuoc(int id, String tenDanDuoc, byte capDanDuoc, int capDoYeuCauDeSuDung) {
        this.id = id;
        this.tenDanDuoc = tenDanDuoc;
        this.capDanDuoc = capDanDuoc;
        this.capDoYeuCauDeSuDung = capDoYeuCauDeSuDung;
    }

    public String getNameByCap() {
        switch (capDanDuoc) {
            case 0:
                return "Sơ";
            case 1:
                return "Trung";
            case 2:
                return "Thượng";
            case 3:
                return "Cực";
            case 4:
                return "Tiên";
            default:
                return "Không xác định";
        }
    }
}
