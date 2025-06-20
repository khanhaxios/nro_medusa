package com.girlkun.models.player.tuma;

import java.util.ArrayList;
import java.util.List;

public class TuMaTemplate {
    private static TuMaTemplate I;

    public static TuMaTemplate getI() {
        if (I == null) {
            I = new TuMaTemplate();
        }
        return I;
    }

    public static List<LinhCanTuMa> LINH_CAN = new ArrayList<>();
    public static List<CongPhapTuMa> CONG_PHAP = new ArrayList<>();

    public void initLinhCan() {
        LINH_CAN.add(new LinhCanTuMa("Huyết Linh Căn", "Linh căn khát máu hút #% HP của đối thủ chuyển thành của mình", 0, (byte) 0));
        LINH_CAN.add(new LinhCanTuMa("Sát Linh Căn", "Phóng thích sát khí gây sát thương chuẩn bằng #% máu của đối thủ", 0, (byte) 1));
        LINH_CAN.add(new LinhCanTuMa("Ma Linh Căn", "Đòn đánh thường gây thêm #% sát thương Ám ảnh", 0, (byte) 2));
        LINH_CAN.add(new LinhCanTuMa("Hắc Linh Căn", "Đòn đánh thường có #% gây mê muội và #% sát thương chuẩn", 0, (byte) 3));
        LINH_CAN.add(new LinhCanTuMa("Bạo Thực Linh Căn", "Linh căn cuồng bạo gây sát thương bằng #%Hp của đối thủ", 0, (byte) 4));
    }

    public void initCongPhap() {
        CONG_PHAP.add(new CongPhapTuMa("Hấp Huyết Tà Pháp"));
        CONG_PHAP.add(new CongPhapTuMa("Thiên Sát"));
        CONG_PHAP.add(new CongPhapTuMa("Ma Ảnh Quyết"));
        CONG_PHAP.add(new CongPhapTuMa("Hắc Ngọc Tịch"));
        CONG_PHAP.add(new CongPhapTuMa("Bạo Thực Lục"));
    }

    public void initTuMaTemplate() {
        initLinhCan();
        initCongPhap();
    }
}
