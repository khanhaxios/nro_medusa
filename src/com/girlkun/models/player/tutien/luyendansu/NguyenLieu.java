package com.girlkun.models.player.tutien.luyendansu;

public class NguyenLieu {
    public int id;
    public String tenNguyenLieu;
    public int quantity;
    public int quality;

    public NguyenLieu() {
    }

    public void copy(NguyenLieu nguyenLieu) {
        this.id = nguyenLieu.id;
        this.tenNguyenLieu = nguyenLieu.tenNguyenLieu;
        this.quantity = nguyenLieu.quantity;
        this.quantity = nguyenLieu.quantity;
    }

    public NguyenLieu(int id, String tenNguyenLieu) {
        this.id = id;
        this.tenNguyenLieu = tenNguyenLieu;
    }

    public NguyenLieu(int id, String tenNguyenLieu, int quantity) {
        this.id = id;
        this.tenNguyenLieu = tenNguyenLieu;
        this.quantity = quantity;
    }

    public NguyenLieu(int id, String tenNguyenLieu, int quantity, int quality) {
        this(id, tenNguyenLieu, quantity);
        this.quality = quality;
    }

    public String getPhamChat() {
        switch (quality) {
            case 0:
                return "Sơ Giai";
            case 1:
                return "Trung Giai";
            case 2:
                return "Thượng Giai";
            case 3:
                return "Cực Giai";
            case 4:
                return "Tiên Giai";
            default:
                return "Vô Giai";
        }
    }
}
