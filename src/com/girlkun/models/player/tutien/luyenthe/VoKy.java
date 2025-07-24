package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.utils.Util;

public class VoKy {
    public long doThuanThuc;
    public long maxDoThuanThuc;
    public int bac;
    public int type;

    public int[] TYPE_BUFF_0 = new int[]{20, 50, 80, 120, 150, 200}; //  buff sat thuong
    public int[] TYPE_BUFF_1 = new int[]{50, 100, 150, 200, 250, 300}; //  buff HP,MP
    public int[] TYPE_BUFF_2 = new int[]{20, 30, 50, 80, 100, 120}; //  buff phan sat thuong

    public long calcMaxDoThuanThuc() {
        return bac * 100_000L;
    }

    public long getExpCanGain() {
        return 2L * Util.nextInt(1, 2) * bac;
    }

    public void addDoThuanThuc(long doThuanThuc) {

    }

    public void tangBac() {

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

    public float getBuff() {
        switch (type) {
            case 0:
                return TYPE_BUFF_0[bac];
            case 1:
                return TYPE_BUFF_1[bac];
            case 2:
                return TYPE_BUFF_2[bac];
        }
        return 1;
    }
}
