package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.models.player.Player;
import com.girlkun.models.player.tuma.TuMa;
import com.girlkun.models.player.tutien.luyenkhi.TuTien;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class DanDuocFactory {
    public static DanDuoc createDanDuoc(DanPhuong danPhuong, byte cap, int quanity) {
        DanDuoc danDuoc = new DanDuoc(danPhuong.id + cap + 1, danPhuong.tenDanPhuong, cap, 2 + cap);
        danDuoc.quantity = quanity;
        return danDuoc;
    }

    public static void useDanDuoc(Player player, DanDuoc danDuoc, int quantity) {
        if (!player.luyenDanSu.isLuyenDan()) {
            Service.gI().sendThongBao(player, "Bạn cần học luyện đan để có thể sử dụng đan dược");
            return;
        }
        if (!player.isAdmin()) {
            if (player.tuTien.isTuTien() && player.tuTien.level < danDuoc.capDoYeuCauDeSuDung) {
                Service.gI().sendThongBao(player, "Bạn cần đạt tu tiên cấp " + TuTien.CANH_GIOI[danDuoc.capDoYeuCauDeSuDung] + " để sử dụng");
                return;
            } else if (player.tuMa.isTuMa() && player.tuMa.level / 10 < danDuoc.capDoYeuCauDeSuDung) {
                Service.gI().sendThongBao(player, "Bạn cần đạt tu ma cấp " + TuMa.CANH_GIOI[danDuoc.capDoYeuCauDeSuDung] + " để sử dụng");
                return;
            } else if (player.luyenThe.isLuyenTheReal() && player.luyenThe.level < danDuoc.capDoYeuCauDeSuDung * 20) {
                Service.gI().sendThongBao(player, "Bạn cần đạt luyện thể tầng " + danDuoc.capDoYeuCauDeSuDung * 20 + " để sử dụng");
                return;
            }
        }
        if (danDuoc.id >= 456 && danDuoc.id <= 456 + 5) {
            //hoi khi dan
            player.luyenDanSu.danDuocEffect.xBuffLinhKhi = Math.max(danDuoc.capDanDuoc + 1, 2);
            player.luyenDanSu.danDuocEffect.timeBuffLinhKhi += (3 * (60 * 1000)) * Math.max(danDuoc.capDanDuoc + 1, 1);
            player.luyenDanSu.danDuocEffect.lastTimeUseDanBuffLinhKhi = System.currentTimeMillis();
        }
        if (danDuoc.id >= 789 && danDuoc.id <= 789 + 5) {
            player.luyenDanSu.danDuocEffect.xBuffLinhKhi = Math.max(danDuoc.capDanDuoc + 1, 2);
            player.luyenDanSu.danDuocEffect.timeBuffLt += (3 * (60 * 1000)) * Math.max(danDuoc.capDanDuoc + 1, 1);
            player.luyenDanSu.danDuocEffect.lastTimeUseDanLt = System.currentTimeMillis();
        }
        if (danDuoc.id >= 1010 && danDuoc.id <= 1010 + 5) {
            if (!Util.canDoWithTime(player.luyenDanSu.danDuocEffect.lastTimeUseDanHoiLK, 10 * (60 * 1000) * danDuoc.capDanDuoc)) {
                Service.gI().sendThongBao(player, "Không thể sử dụng liên tục");
                return;
            }
            // hoi linh khi ngay lap tuc
            int percentHoi = 20 * Math.max(danDuoc.capDanDuoc + 1, 2);
            long lkHoi = player.tuTien.maxLinhKhiPoint * percentHoi / 100;
            player.luyenDanSu.danDuocEffect.lastTimeUseDanHoiLK = System.currentTimeMillis();
            player.tuTien.addLinhKhi(lkHoi);
            PlayerService.gI().sendHoiPhucLinhKhi(player, lkHoi);
        }
        if (danDuoc.id > 2020 && danDuoc.id <= 2020 + 5) {
            int percentGiam = 10 * Math.max(2, danDuoc.capDanDuoc + 1);
            player.luyenDanSu.danDuocEffect.isUseDanTranhTamMa = true;
            player.luyenDanSu.danDuocEffect.tranhTamMaPercent = percentGiam;
            Service.gI().sendThongBao(player, "Bạn sẽ được giảm tỷ lệ gặp tâm ma trong lần đột phá tới");
        }
        if (danDuoc.id > 3030 && danDuoc.id <= 3030 + 5) {
            if (player.luyenDanSu.diemKhangTinh > 0) {
                // xoa diem khang tinh
                player.luyenDanSu.diemKhangTinh -= player.luyenDanSu.calcMaxDiemKhangTinh() * (5 * Math.max(danDuoc.capDanDuoc + 1, 1)) / 100;
            } else {
                int diemCong = Util.nextInt(3, 5);
                if (player.tuTien.getXDiemThienPhu() > danDuoc.capDanDuoc + 5) {
                    Service.gI().sendThongBao(player, "Do thiên phú của bạn quá cao nên đan được không có tác dụng");
                    return;
                }
                player.tuTien.addPoint(diemCong, 0);
                player.tuTien.addPoint(diemCong, 1);
            }
            Service.gI().sendThongBao(player, "Đã tẩy tủy");
        }
        if (danDuoc.id >= 4040 && danDuoc.id <= 4040 + 5) {
            int diemCong = Util.nextInt(3, 5);
            if (player.tuTien.getXDiemThienPhu() > danDuoc.capDanDuoc + 5) {
                Service.gI().sendThongBao(player, "Do thiên phú của bạn quá cao nên đan được không có tác dụng");
                return;
            }
            player.tuTien.addPoint(diemCong, 1);
        }
        if (danDuoc.id >= 5050 && danDuoc.id <= 5050 + 5) {
            // van khi
            int diemVanKhi = Math.max(danDuoc.capDanDuoc + 1, 1);
            player.luyenDanSu.danDuocEffect.pointMayMan += diemVanKhi;
            player.luyenDanSu.danDuocEffect.lastTimeUseMayMan = System.currentTimeMillis();
            player.luyenDanSu.danDuocEffect.timeBuffMayMan += 3 * (60 * 1000) * Math.max(danDuoc.capDanDuoc + 1, 1);
            Service.gI().sendThongBao(player, "Dùng vận khí đan thành công may mắn của bạn tăng lên một chút");
        }
        if (danDuoc.id >= 6060 && danDuoc.id <= 6060 + 5) {
            player.luyenDanSu.danDuocEffect.stLinhCanBuff = Math.max(danDuoc.capDanDuoc, 2) * 5;
            player.luyenDanSu.danDuocEffect.timeBuffSTLinhCan += 3 * (60 * 1000) * Math.max(danDuoc.capDanDuoc + 1, 1);
            player.luyenDanSu.danDuocEffect.lastTimeUseSTLinhCan = System.currentTimeMillis();
            Service.gI().sendThongBao(player, "Sát thương linh căn của bạn đã tăng mạnh");
        }
        if (danDuoc.id >= 7070 && danDuoc.id <= 7070 + 5) {
            player.luyenDanSu.danDuocEffect.xBuffCongPhap = Math.max(danDuoc.capDanDuoc, 2);
            player.luyenDanSu.danDuocEffect.timeBuffCongPhap += 5 * (60 * 1000) * Math.max(danDuoc.capDanDuoc + 1, 1);
            player.luyenDanSu.danDuocEffect.lastTimeUseCongPhap = System.currentTimeMillis();
            Service.gI().sendThongBao(player, "Kinh nghiệm công pháp nhận được sẽ tăng mạnh");
        }
        if (danDuoc.id >= 8080 && danDuoc.id <= 8080 + 5) {
            // van khi
            player.luyenDanSu.danDuocEffect.percentDotPhaThienDao = Math.max(danDuoc.capDanDuoc + 1, 1) * 3;
            player.luyenDanSu.danDuocEffect.isUseDanDotPhaThienDao = true;
            Service.gI().sendThongBao(player, "Tỷ lệ đột phá thiên đạo của bạn sẽ tăng một chút");
        }
        player.luyenDanSu.tuiDanDuoc.subDanDuocQuantity(danDuoc.id, quantity, danDuoc.capDanDuoc);
    }
}
