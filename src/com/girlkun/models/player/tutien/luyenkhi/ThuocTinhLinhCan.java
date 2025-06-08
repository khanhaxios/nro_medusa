package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.utils.Util;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThuocTinhLinhCan {
    private byte id;
    private String tenThuocTinh;
    private short param;
    private byte linhCanBatBuoc;

    public String getTenThuocTinhReplace() {
        return tenThuocTinh.replace("#", String.valueOf(param));
    }

    public short ratioThuocTinhLinhCanAdmin() {
        return (short) Util.nextInt(1000, 10000);
    }

    public short ratioThuocTinhLinhCan() {
        if (Util.isTrue(2, 100)) {
            return (short) Util.nextInt(100, 250);
        } else if (Util.isTrue(10, 100)) {
            return (short) Util.nextInt(50, 100);
        } else if (Util.isTrue(20, 100)) {
            return (short) Util.nextInt(10, 50);
        } else {
            return (short) Util.nextInt(5, 25);
        }
    }
}
