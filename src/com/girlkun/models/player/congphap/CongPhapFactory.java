/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;

public class CongPhapFactory {
    private static CongPhapFactory I;

    public static synchronized CongPhapFactory gI() {
        if (I == null) {
            I = new CongPhapFactory();
        }
        return I;
    }

    public CongPhapTuTien createCongPhapTuTien() {
        return new CongPhapTuTien(null);
    }

    public CongPhapTuMa createCongPhapTuMa() {
        return new CongPhapTuMa();
    }

    public CongPhapTuMa createCongPhapTuMaSoCap(byte heCongPhap, Player player) {
        CongPhapTuMa congPhapTuMa = CongPhapTemplate.getI().getCongPhapTuMa(Util.nextInt(1, CongPhapTemplate.getI().CONG_PHAP_TU_MA.size() - 1));
        congPhapTuMa.heCongPhap = heCongPhap;
        congPhapTuMa.tier = 0;
        congPhapTuMa.level = 0;
        congPhapTuMa.maxLevel = 5;
        congPhapTuMa.maxPham = 2;
        congPhapTuMa.restExp();
        congPhapTuMa.restExpPham();
        congPhapTuMa.player = player;
        return congPhapTuMa;
    }

    public CongPhapLuyenThe createCongPhapLuyenThe() {
        return new CongPhapLuyenThe();
    }

    public CongPhapTuTien createCongPhapTuTienSoCapByHe(int select, Player player) {
        CongPhapTuTien congPhapTuTien = CongPhapTemplate.getI().getCongPhapTuTien(Util.nextInt(0, CongPhapTemplate.getI().CONG_PHAP_TU_TIEN.size() - 1));
        congPhapTuTien.heCongPhap = select;
        congPhapTuTien.player = player;
        congPhapTuTien.tier = 0;
        congPhapTuTien.level = 0;
        congPhapTuTien.maxLevel = 5;
        congPhapTuTien.maxPham = 2;
        congPhapTuTien.restExp();
        congPhapTuTien.restExpPham();
        congPhapTuTien.ratioNewOption(2);
        return congPhapTuTien;
    }
}
