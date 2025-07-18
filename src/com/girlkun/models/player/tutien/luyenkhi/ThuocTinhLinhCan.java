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
        if (Util.isTrue(1, 100)) {
            return (short) Util.nextInt(50, 80);
        } else if (Util.isTrue(10, 100)) {
            return (short) Util.nextInt(20, 30);
        } else if (Util.isTrue(20, 100)) {
            return (short) Util.nextInt(10, 20);
        } else {
            return (short) Util.nextInt(1, 6);
        }
    }
}
