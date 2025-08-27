/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.player.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoDuyen {
    @AllArgsConstructor
    @Data
    public static class LuaChon {
        private String tenLuaChon;
        private String[] moTaLuaChon;
        private int id;
        private ICoDuyenHandle handle;

        public interface ICoDuyenHandle {
            void onSelect(Player player, CoDuyen coDuyen, LuaChon luaChon);
        }
    }

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
    private List<LuaChon> luaChons = new ArrayList<>();


}
