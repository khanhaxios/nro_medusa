package com.girlkun.models.player.tutien.luyendansu;

import java.util.ArrayList;
import java.util.List;

public class DanPhuong {
    public int id;
    public String tenDanPhuong;
    public int capYeuCauHoc;
    public String mota;
    public List<NguyenLieu> nguyenLieu;

    public DanPhuong() {
        nguyenLieu = new ArrayList<>();
    }

    public DanPhuong(int id, String tenDanPhuong, int capYeuCauHoc, List<NguyenLieu> nguyenLieu, String mota) {
        this.id = id;
        this.mota = mota;
        this.tenDanPhuong = tenDanPhuong;
        this.capYeuCauHoc = capYeuCauHoc;
        this.nguyenLieu = nguyenLieu;
    }
}
