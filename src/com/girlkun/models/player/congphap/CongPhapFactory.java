/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

public class CongPhapFactory {
    public CongPhapTuTien createCongPhapTuTien() {
        return new CongPhapTuTien(null);
    }

    public CongPhapTuMa createCongPhapTuMa() {
        return new CongPhapTuMa();
    }

    public CongPhapLuyenThe createCongPhapLuyenThe() {
        return new CongPhapLuyenThe();
    }
}
