package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DanPhuongFactory {
    private static DanPhuongFactory instance;

    public static DanPhuongFactory getInstance() {
        if (instance == null) {
            instance = new DanPhuongFactory();
        }
        return instance;
    }

    private static final Map<Integer, DanPhuong> DAN_PHUONG_TEMPLATE = new HashMap<>();

    public static String getNguyenLieuDetail(DanPhuong danPhuong) {
        StringBuilder stringBuilder = new StringBuilder();
        for (NguyenLieu nguyenLieu : danPhuong.nguyenLieu) {
            stringBuilder.append("|5|").append(nguyenLieu.tenNguyenLieu).append(" x").append(nguyenLieu.quantity).append("\n");
        }
        return stringBuilder.toString();
    }

    public static void prepareForLuyenDan(Player player, DanPhuong danPhuong) {
        String menuText = "|7|Thông tin đan phương" + "\n" +
                "|5|" + danPhuong.tenDanPhuong + "\n" +
                "|5|" + danPhuong.mota + "\n" +
                "|7|Nguyên Liệu\n" + getNguyenLieuDetail(danPhuong);
        player.iDMark.danPhuongChe = danPhuong;
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_CHE_DAN, -1, menuText, "Luyện đan", "Từ chối");
    }

    public static DanPhuong randomizeDanPhuong(int minCapYeuCau) {
        List<DanPhuong> filtered = DAN_PHUONG_TEMPLATE.values()
                .stream()
                .filter(dp -> dp.capYeuCauHoc <= minCapYeuCau)
                .toList();

        if (filtered.isEmpty()) return null;
        return filtered.get(Util.nextInt(0, filtered.size()));
    }

    public void initTemplate() {
        DAN_PHUONG_TEMPLATE.clear();
        JSONParser parser = new JSONParser();

        try (FileReader reader = new FileReader("data/girlkun/dan_phuong/dan_phuong.json")) {
            JSONArray danPhuongArray = (JSONArray) parser.parse(reader);

            for (Object obj : danPhuongArray) {
                JSONObject danPhuongJson = (JSONObject) obj;

                int id = ((Long) danPhuongJson.get("id")).intValue();
                String ten = (String) danPhuongJson.get("tenDanPhuong");
                int capYeuCau = ((Long) danPhuongJson.get("capYeuCauHoc")).intValue();
                String mota = danPhuongJson.get("moTa").toString();

                // Đọc nguyên liệu
                List<NguyenLieu> nguyenLieuList = new ArrayList<>();
                JSONArray nguyenLieuArray = (JSONArray) danPhuongJson.get("nguyenLieu");

                for (Object nlObj : nguyenLieuArray) {
                    JSONObject nlJson = (JSONObject) nlObj;
                    int nlId = ((Long) nlJson.get("id")).intValue();
                    int quantity = ((Long) nlJson.get("quantity")).intValue();
                    nguyenLieuList.add(NguyenLieuFactory.getByIdAndQuantity(nlId, quantity));
                }

                DanPhuong danPhuong = new DanPhuong(id, ten, capYeuCau, nguyenLieuList, mota);
                DAN_PHUONG_TEMPLATE.put(id, danPhuong);
            }

            System.out.println("Đã nạp " + DAN_PHUONG_TEMPLATE.size() + " đan phương từ JSON.");

        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public static DanPhuong getDanPhuongById(int id) {
        return DAN_PHUONG_TEMPLATE.get(id);
    }

    public static void addDanPhuong(DanPhuong danPhuong) {
        DAN_PHUONG_TEMPLATE.put(danPhuong.id, danPhuong);
    }

    //
    public static void luyenDan(Player player, DanPhuong danPhuong) {
        if (danPhuong == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy đan phương");
            return;
        }
        float nlPlus = 0f;

        // Lấy danh sách nguyên liệu cần thiết từ DanPhuong
        List<NguyenLieu> nguyenLieuList = player.luyenDanSu.tuiNguyenLieu.takeNguyenLieu(danPhuong);

        for (NguyenLieu nguyenLieu : nguyenLieuList) {
            nlPlus += nguyenLieu.quality;
        }
        // Kiểm tra xem người chơi có đủ nguyên liệu hay không
        if (!checkNguyenLieu(nguyenLieuList, danPhuong.nguyenLieu)) {
            Service.gI().sendThongBao(player, "Không đủ nguyên liệu");
            return;
        }

        // Trừ nguyên liệu sau khi luyện đan
        deductUsedNguyenLieu(danPhuong, player);

        // Tính tỷ lệ thành công của luyện đan
        float ratioThanhCong = player.luyenDanSu.getTyLeLuyenDan(danPhuong) + nlPlus;

        // Xác định cấp độ DanDuoc dựa trên tỷ lệ thành công
        byte levelDanDuoc = getDanDuocLevel(ratioThanhCong);
        long exp = 0;
        // Nếu tỷ lệ thành công hợp lệ (tức là có cấp độ DanDuoc hợp lệ)
        if (levelDanDuoc >= 0) {
            // Tạo DanDuoc mới dựa trên cấp độ
            DanDuoc danDuoc = DanDuocFactory.createDanDuoc(player, danPhuong, levelDanDuoc, Util.nextInt(1, 5));
            player.luyenDanSu.tuiDanDuoc.addDanDuoc(danDuoc);

            //handle exp
            exp = (long) Util.nextInt(50, 100) * Math.max(levelDanDuoc, 1);
            // Tạo thông báo cho người chơi về kết quả luyện đan
            String danDuocMessage = getDanDuocMessage(levelDanDuoc, danDuoc);
            Service.gI().sendThongBao(player, "Bạn đã luyện thành công " + danDuocMessage);

        } else {
            exp = (long) Util.nextInt(20, 50) * Math.max(levelDanDuoc, 1);
            Service.gI().sendThongBao(player, "Luyện đan thất bại");
        }
        // tang exp
        player.luyenDanSu.addExp(exp);
        Service.gI().sendThongBaoOK(player, "Lần luyện đan này bạn nhận được x" + Util.powerToString(exp) + " Kinh nghiệm luyện đan");
    }

    private static void deductUsedNguyenLieu(DanPhuong danPhuong, Player player) {
        for (NguyenLieu required : danPhuong.nguyenLieu) {
            player.luyenDanSu.tuiNguyenLieu.subNguyenLieuQuantity(required.id, required.quantity, required.quality);
        }
    }

    private static boolean checkNguyenLieu(List<NguyenLieu> nguyenLieuList, List<NguyenLieu> requiredNguyenLieu) {
        for (NguyenLieu required : requiredNguyenLieu) {
            boolean found = false;
            for (NguyenLieu nguyenLieu : nguyenLieuList) {
                if (nguyenLieu.id == required.id && nguyenLieu.quantity >= required.quantity) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public static void hocDanPhuong(Player player, DanPhuong danPhuong) {
        if (player.luyenDanSu.level < danPhuong.capYeuCauHoc) {
            Service.gI().sendThongBaoOK(player, "Bạn cần đạt luyện đan sư cấp " + danPhuong.capYeuCauHoc + " để học đan phương này");
            return;
        }
        if (player.luyenDanSu.danPhuongs.contains(danPhuong)) {
            Service.gI().sendThongBaoOK(player, "Bạn đã học đan phương này rồi mà");
            return;
        }
        player.luyenDanSu.danPhuongs.add(danPhuong);
        // remove dan phuong trong tui dan phuong
        player.luyenDanSu.tuiDanPhuong.removeDanPhuong(danPhuong);
        Service.gI().sendThongBao(player, "Bạn đã học được " + danPhuong.tenDanPhuong + " đan phương");
    }

    private static byte getDanDuocLevel(float ratioThanhCong) {
        if (Util.isTrue(ratioThanhCong, 500)) {
            return 4;  // Cấp Tiên
        } else if (Util.isTrue(ratioThanhCong, 300)) {
            return 3;  // Cấp Thượng
        } else if (Util.isTrue(ratioThanhCong, 200)) {
            return 2;  // Cấp Trung
        } else if (Util.isTrue(ratioThanhCong, 100)) {
            return 1;  // Cấp Hạ
        } else if (Util.isTrue(ratioThanhCong, 50)) {
            return 0;  // Cấp Thấp
        }
        return -1;  // Nếu tỉ lệ thành công quá thấp thì không tạo được DanDuoc
    }

    private static String getDanDuocMessage(byte level, DanDuoc danDuoc) {
        switch (level) {
            case 4:
                return "[Tiên] " + danDuoc.tenDanDuoc;
            case 3:
                return "[Cực] " + danDuoc.tenDanDuoc;
            case 2:
                return "[Thượng] " + danDuoc.tenDanDuoc;
            case 1:
                return "[Trung] " + danDuoc.tenDanDuoc;
            case 0:
                return "[Sơ] " + danDuoc.tenDanDuoc;
            default:
                return "[Không thành công]";
        }
    }
}
