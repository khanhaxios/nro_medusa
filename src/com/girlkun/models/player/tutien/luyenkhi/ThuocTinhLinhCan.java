package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.utils.Util;
import lombok.*;

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

    public short ratioThuocTinhLinhCan() {
        if (Util.isTrue(5, 100)) {
            return (short) Util.nextInt(100, 250);
        } else if (Util.isTrue(20, 100)) {
            return (short) Util.nextInt(50, 100);
        } else if (Util.isTrue(50, 100)) {
            return (short) Util.nextInt(10, 50);
        } else {
            return (short) Util.nextInt(5, 25);
        }
    }
}
