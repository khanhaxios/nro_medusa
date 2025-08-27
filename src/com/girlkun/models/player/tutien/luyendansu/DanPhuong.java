/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.luyendansu;

import java.util.ArrayList;
import java.util.List;

public class DanPhuong implements ITransaction {
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
