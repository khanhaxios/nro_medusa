package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.player.tutien.luyendansu.DanPhuong;
import com.girlkun.models.player.tutien.luyenkhi.CongPhap;
import com.girlkun.models.player.tutien.luyenkhi.LinhCan;
import com.girlkun.models.player.tutien.luyenkhi.ThuocTinhLinhCan;
import com.girlkun.models.player.tutien.luyenkhi.TienPhap;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

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
    public static List<CoDuyen> CO_DUYEN = new ArrayList<>();

    public static List<CongPhap> CONG_PHAP = new ArrayList<>();

    public static List<TienPhap> TIEN_PHAP = new ArrayList<>();

    public static List<DanPhuong> DAN_PHUONG = new ArrayList<>();

    public void initBuffThuocTinhLinhCan() {
        // init thuoc tinh linh can
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 0, "Sát thương Kim +#%", (short) 0, (byte) 0));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 1, "Sát thương Mộc +#%", (short) 0, (byte) 1));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 2, "Sát thương Thủy +#%", (short) 0, (byte) 2));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 3, "Sát thương Hỏa +#%", (short) 0, (byte) 3));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 4, "Sát thương Thổ  +#%", (short) 0, (byte) 4));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 5, "Sát thương Phong  +#%", (short) 0, (byte) 5));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 6, "Sát thương lôi  +#%", (short) 0, (byte) 6));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 7, "Sát thương Quang  +#%", (short) 0, (byte) 7));
        THUOC_TINH_BUFF_LINH_CAN.add(new ThuocTinhLinhCan((byte) 8, "Sát thương Ám  +#%", (short) 0, (byte) 8));
    }

    public void initCoDuyen() {
        // add cơ duyen template
        List<CoDuyen> dsCoDuyen = new ArrayList<>();
        List<CoDuyen.LuaChon> luaChons = new ArrayList<>();
        luaChons.add(new CoDuyen.LuaChon("Đi khám\nphá",
                new String[]{
                        "Ngươi khám phá vào sâu trong gặp được có chí bảo đang tại hóa hình\nNgươi lòng tham nổi lên nhưng chí bảo đã nhanh chân chạy mất.\nNgươi hấp thu linh khí còn lại đột phá cảnh giới",
                        "Ngươi khám phá vào sâu trong gặp được có chí bảo đang tại hóa hình\nNó nhận thấy tham niệm của ngươi lên đã tấn công\nNgươi mạng lớn trốn thoát.Trong lòng thề sẽ trở lên mạnh hơn\nCảnh giới của ngươi sụp giảm"
                }, 0,
                (player, coDuyen, luaChon) -> {
                    if (Util.isTrue(50, 100)) {
                        player.tuTien.levelUp();
                        Service.gI().sendThongBaoOK(player, luaChon.getMoTaLuaChon()[0]);
                    } else {
                        player.tuTien.levelDown();
                        Service.gI().sendThongBaoOK(player, luaChon.getMoTaLuaChon()[1]);
                    }
                }));
        luaChons.add(new CoDuyen.LuaChon("Cẩu đạo\ntrung nhân", new String[]{"Ngươi quyết định không đi khám phá cái chỗ kia"}, 1, (player, coDuyen, luaChon) -> Service.gI().sendThongBao(player, "Ngươi đã bỏ lỡ cơ duyên")));
        dsCoDuyen.add(new CoDuyen(1, (byte) 1,
                "Linh Khí Dâng Trào",
                "Bạn ngẫu nhiên gặp được linh khí bạo động ,xét thấy đây là nguy hiểm cũng là cơ duyên bạn chọn?",
                1,
                new long[]{},
                60000L,
                luaChons
        )); // 1 phút tồn tại

        List<CoDuyen.LuaChon> lc2 = new ArrayList<>();
        lc2.add(new CoDuyen.LuaChon("Tiêu diệt\nTâm ma", new String[]{
                "Ngươi lựa chọn khiêu chiến bản thân tiêu diệt tâm ma.\nSau một trận khổ chiến ngươi thành công kéo về sắp nhập ma chính mình.\nThiên phú được tăng lên",
                "Ngươi lựa chọn khiêu chiến tâm ma.\nSau một hồi đại chiến ngươi mặc dù tiêu diệt tâm ma nhưng thực lực suy giảm"
        }, 0, (player, coDuyen, luaChon) -> {
            if (Util.isTrue(80, 100)) {
                player.tuTien.canCot += 2;
                player.tuTien.ngoTinh += 2;
                Service.gI().sendThongBaoOK(player, luaChon.getMoTaLuaChon()[0]);
            } else {
                player.tuTien.canCot -= 1;
                player.tuTien.ngoTinh -= 2;
                player.tuTien.levelDown();
                Service.gI().sendThongBaoOK(player, luaChon.getMoTaLuaChon()[1]);
            }
        }));
        dsCoDuyen.add(new CoDuyen(2, (byte) 2,
                "Đạo Tâm Bất Ổn",
                "Trong khoảng thời gian ngươi du lịch hồng trần đã khiến ngươi nhiễm phải hồng trần kiếp\nCảnh giới suy giảm\nNgươi Chọn?",
                5,
                new long[]{},
                0L, lc2)); // hiệu ứng tức thời

        dsCoDuyen.add(new CoDuyen(3, (byte) 3,
                "Khai Thiên Nhãn",
                "Linh quang lóe lên, ngươi đột phá cảnh giới nước chảy thành sông",
                5,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(4, (byte) 4,
                "Tâm Như Tịnh Thủy",
                "Tâm linh thanh tịnh, độ thuần thục công pháp tăng lên nhanh chóng.",
                1,
                new long[]{},
                120000L, luaChons)); // buff 2 phút

        dsCoDuyen.add(new CoDuyen(5, (byte) 5,
                "Thiên Ý Truyền Thừa",
                "Một tia linh ngộ đến từ trời xanh,Công pháp của ngươi đã tăng phẩm",
                0,
                new long[]{},
                0L, luaChons)); // hiệu ứng hiếm cực

        dsCoDuyen.add(new CoDuyen(6, (byte) 6,
                "Tài Nguyên Trời Ban",
                "Ngươi dựa vào khắc khổ không ngừng tu tiên được thiên đạo nhìn thấy\nban cho ngươi tố linh đan",
                2,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(7, (byte) 7,
                "Bá Thể Kim Cương",
                "Trong quá trình du lịch ngươi đã gặp một linh thảo kỳ lạ ngươi chọn?",
                3,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(1, (byte) 1,
                "Linh Khí Ngưng Tụ",
                "Trời đất cộng hưởng, linh khí xung quanh ngưng tụ, giúp người chơi hấp thu được lượng linh khí vượt trội.",
                1,
                new long[]{},
                60000L, luaChons));

        dsCoDuyen.add(new CoDuyen(2, (byte) 2,
                "Tâm Ma Xâm Nhập",
                "Tâm trí dao động, bị tâm ma quấy nhiễu, dẫn đến tụt 1 cảnh giới.",
                5,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(3, (byte) 3,
                "Thiên Đạo Thưởng Phạt",
                "Thiên đạo mở lòng, ban cho cơ duyên bất ngờ giúp người chơi đột phá 1 cảnh giới.",
                5,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(4, (byte) 4,
                "Linh Ngộ Đại Đạo",
                "Trong lúc tu luyện, lĩnh ngộ sâu sắc đạo lý thiên địa, tăng mạnh độ thuần thục công pháp.",
                1,
                new long[]{},
                120000L, luaChons));

        dsCoDuyen.add(new CoDuyen(5, (byte) 5,
                "Cơ Duyên Truyền Thừa",
                "Kế thừa lại tinh hoa của một vị tiền bối, phẩm chất công pháp được nâng cao rõ rệt.",
                1,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(6, (byte) 6,
                "Thiên Tài Địa Bảo",
                "Vô tình thu thập được thiên tài địa bảo, giúp tăng lượng tài nguyên thu hoạch.",
                2,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(7, (byte) 7,
                "Cốt Cách Kim Cang",
                "Thân thể được rèn luyện không ngừng, thể chất đạt ngưỡng đột phá, tăng cấp luyện thể.",
                3,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(0, (byte) 0,
                "Thiên Mệnh Chỉ Dẫn",
                "Một cảm giác khó hiểu dẫn lối bạn vào một vùng đất chưa từng biết đến – nơi định mệnh bắt đầu thay đổi.",
                1,
                new long[]{},
                60000L, luaChons));

        dsCoDuyen.add(new CoDuyen(1, (byte) 1,
                "Linh Khí Triều Dâng",
                "Linh khí nơi bạn đứng như có ý chí riêng, tuôn trào cuồn cuộn. Tốc độ hấp thu tăng vọt trong khoảnh khắc.",
                1,
                new long[]{},
                60000L, luaChons));

        dsCoDuyen.add(new CoDuyen(2, (byte) 2,
                "Tâm Ma Bộc Phát",
                "Trong lúc tham ngộ, nội tâm dao động dữ dội, bị tâm ma dẫn lối, khiến cảnh giới tụt một tầng.",
                5,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(3, (byte) 3,
                "Thiên Cơ Biến Động",
                "Một luồng khí tức huyền bí quét qua, bạn mơ hồ cảm nhận cảnh giới bản thân... đang âm thầm thăng hoa.",
                5,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(4, (byte) 4,
                "Ngộ Đạo Dưới Sao",
                "Khi đêm phủ xuống, ngồi thiền dưới trời sao, bạn chạm đến một tầng đạo lý mới – công pháp tiến bộ thần tốc.",
                1,
                new long[]{},
                120000L, luaChons));

        dsCoDuyen.add(new CoDuyen(5, (byte) 5,
                "Truyền Thừa Huyền Môn",
                "Linh hồn cổ nhân truyền dạy một bí kíp thất truyền – phẩm chất công pháp từ đó được nâng lên một tầng mới.",
                1,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(6, (byte) 6,
                "Thiên Tài Địa Bảo",
                "Khi bước chân vào vùng đất hoang vu, bạn vô tình thu nhặt được tài nguyên trời ban.",
                2,
                new long[]{},
                0L, luaChons));

        dsCoDuyen.add(new CoDuyen(7, (byte) 7,
                "Cốt Cách Kim Cang",
                "Thân thể ngưng luyện từng chút một, đến nay rốt cuộc đã đạt cảnh giới mới trong Luyện Thể Thuật.",
                3,
                new long[]{},
                0L, luaChons));

        CO_DUYEN = dsCoDuyen;
    }

    public void initLinhCanGoc() {
        LINH_CAN.put("K", (byte) 0);
        LINH_CAN.put("M", (byte) 1);
        LINH_CAN.put("T", (byte) 2);
        LINH_CAN.put("H", (byte) 3);
        LINH_CAN.put("TH", (byte) 4);
        LINH_CAN.put("P", (byte) 5);
        LINH_CAN.put("L", (byte) 6);
        LINH_CAN.put("Q", (byte) 7);
        LINH_CAN.put("A", (byte) 8);
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
            tp.randomParam((byte) 120);
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
            initCoDuyen();
            iniDanPhuong();
        }
    }

    private void iniDanPhuong() {

    }
}
