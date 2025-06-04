package com.girlkun.models.player.tutien.base_tutien;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoDuyen {
    private int id;
    private byte coDuyenType;
    // type 1 co duyen ngau nhien buff tong luong linh khi(1%)
    // type 2 co duyen nhau nhien giam canh gioi(5%)
    // type 3 co duyen ngau nhien tang canh gioi(5%)
    // type 4 co duyen ngau nhien linh ngo tang do thuan thuc cua cong phap(1%)
    // type 5 co duyen ngau nhien linh ngo tang pham cong phap(.2%)
    // type 6 co duyen loai dua tai nguyen(2%)
    // type 7 co duyen tang level luyen the

    private String tenCoDuyen;
    private String moTaCoDuyen;
    private int tiLeXuatHien;

    private long[] idsPlayerNhan = new long[]{};
    private long thoiGianTonTai;
}
