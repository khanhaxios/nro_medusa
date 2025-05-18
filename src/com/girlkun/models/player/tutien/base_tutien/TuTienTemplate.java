package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.player.tutien.luyenkhi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TuTienTemplate {
    private static TuTienTemplate I;

    public static TuTienTemplate getI() {
        if (I == null) {
            I = new TuTienTemplate();
        }
        return I;
    }

    boolean isLoaded = false;
    public static List<ThuocTinhLinhCan> THUOC_TINH_BUFF_LINH_CAN = new ArrayList<>();
    public static Map<String, Byte> LINH_CAN = new HashMap<>();

    public static List<CongPhap> CONG_PHAP = new ArrayList<>();

    public static List<TienPhap> TIEN_PHAP = new ArrayList<>();

    public void initBuffThuocTinhLinhCan() {
        // init thuoc tinh linh can
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 0, "Sát thương Kim +#%", (short) 0, LINH_CAN.get("K")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 1, "Sát thương Mộc +#%", (short) 0, LINH_CAN.get("M")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 2, "Sát thương Thủy +#%", (short) 0, LINH_CAN.get("T")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 3, "Sát thương Hỏa +#%", (short) 0, LINH_CAN.get("H")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 4, "Sát thương Thổ  +#%", (short) 0, LINH_CAN.get("TH")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 5, "Sát thương Phong  +#%", (short) 0, LINH_CAN.get("P")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 6, "Sát thương lôi  +#%", (short) 0, LINH_CAN.get("L")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 7, "Sát thương Quang  +#%", (short) 0, LINH_CAN.get("Q")));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 8, "Sát thương Ám  +#%", (short) 0, LINH_CAN.get("A")));
    }

    public void initLinhCanGoc() {
        LINH_CAN.put("K", (byte) 0);
        LINH_CAN.put("M", (byte) 1);
        LINH_CAN.put("T", (byte) 2);
        LINH_CAN.put("H", (byte) 3);
        LINH_CAN.put("TH", (byte) 4);
        LINH_CAN.put("L", (byte) 5);
        LINH_CAN.put("Q", (byte) 6);
        LINH_CAN.put("A", (byte) 7);
        LINH_CAN.put("P", (byte) 8);
        LINH_CAN.put("HV", (byte) -1);
    }

    public void initCongPhap() {
        CONG_PHAP.add(new CongPhap("Bạch Kim Chân Quyết", LINH_CAN.get("K")));
        CONG_PHAP.add(new CongPhap("Thảo Tự Kiếm Quyết", LINH_CAN.get("M")));
        CONG_PHAP.add(new CongPhap("Hải Lãng Chân Kinh", LINH_CAN.get("T")));
        CONG_PHAP.add(new CongPhap("Hỏa Long Quyết", LINH_CAN.get("H")));
        CONG_PHAP.add(new CongPhap("Địa Thần Quyết", LINH_CAN.get("TH")));
        CONG_PHAP.add(new CongPhap("Phong Thần Quyết", LINH_CAN.get("P")));
        CONG_PHAP.add(new CongPhap("Lôi Thiên Kinh", LINH_CAN.get("L")));
        CONG_PHAP.add(new CongPhap("Đế Quang Chi Thư", LINH_CAN.get("Q")));
        CONG_PHAP.add(new CongPhap("Dạ Ngục Kinh", LINH_CAN.get("A")));
    }


    public void initTienPhap() {
        TIEN_PHAP.add(new TienPhap((byte) 0, "Kim Linh Kiếm Quyết", "Tăng sát thương thuộc tính Kim+#%", 0L, (byte) 0, LINH_CAN.get("K")));
        TIEN_PHAP.add(new TienPhap((byte) 1, "Thất Tinh Hồi Nguyên", "Hồi phục #% HP trong 5s", 5000L, (byte) 3, LINH_CAN.get("M")));
        TIEN_PHAP.add(new TienPhap((byte) 2, "Thái Cổ Cuồng Bạo", "Sau khi dùng chiêu, tăng sát thương thêm #%", 0L, (byte) 2, LINH_CAN.get("H")));
        TIEN_PHAP.add(new TienPhap((byte) 3, "Kim Cương Bất Hoại", "Giảm Sát Thương +#% Trong 5s", 0L, (byte) 4, LINH_CAN.get("TH")));
        TIEN_PHAP.add(new TienPhap((byte) 4, "Băng Hàn Chưởng", "Tăng sát thương Thủy +#%", 0L, (byte) 0, LINH_CAN.get("T")));
        TIEN_PHAP.add(new TienPhap((byte) 5, "Viêm Dương Hỏa Ấn", "Tăng sát thương Hỏa +#%", 0L, (byte) 0, LINH_CAN.get("H")));
        TIEN_PHAP.add(new TienPhap((byte) 6, "Lôi Đình Trảm", "Tăng sát thương Lôi +#%", 0L, (byte) 0, LINH_CAN.get("L")));

        TIEN_PHAP.add(new TienPhap((byte) 7, "Thiên Địa Dưỡng Sinh", "Hồi phục #% HP trong 5s", 5000L, (byte) 3, LINH_CAN.get("Q")));
        TIEN_PHAP.add(new TienPhap((byte) 9, "Tụ Linh Quy Nguyên", "Hồi #% HP lập tức", 5000L, (byte) 1, LINH_CAN.get("Q")));

        TIEN_PHAP.add(new TienPhap((byte) 8, "Huyễn Ảnh Tăng Pháp", "Tăng sát thương thêm #% sau khi dùng kỹ năng", 0L, (byte) 2, LINH_CAN.get("A")));
        TIEN_PHAP.add(new TienPhap((byte) 10, "Ma Thần Kích Hoạt", "Sau khi dùng chiêu, tăng sát thương lên #%", 0L, (byte) 2, LINH_CAN.get("P")));

        for (TienPhap tp : TIEN_PHAP) {
            tp.randomParam((byte) 20);
            tp.setCoolDown(0);
            tp.setLastTimeUsed(0);
        }
    }


    public void initTemplate() {
        if (!isLoaded) {
            initLinhCanGoc();
            initBuffThuocTinhLinhCan();
            initCongPhap();
            initTienPhap();
        }
    }
}
