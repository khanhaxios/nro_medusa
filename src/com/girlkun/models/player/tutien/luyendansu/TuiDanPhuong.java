package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;

import java.util.ArrayList;
import java.util.List;

public class TuiDanPhuong {
    Player player;

    public TuiDanPhuong(Player player) {
        this.player = player;
    }

    // Danh sách DanPhuong
    public List<DanPhuong> danPhuongs = new ArrayList<>();

    // Thêm DanPhuong vào túi
    public void addDanPhuong(DanPhuong danPhuong) {
        if (!this.danPhuongs.contains(danPhuong)) {
            danPhuongs.add(danPhuong);
        }
    }

    // Xóa DanPhuong khỏi túi
    public void removeDanPhuong(DanPhuong danPhuong) {
        this.danPhuongs.remove(danPhuong);
    }

    public void showMenuTui() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Túi Dan Phương\n");
        stringBuilder.append("|5|").append("Bạn có thể học đan phương hoặc xem đan phương bạn đang có ở đây");
        stringBuilder.append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DAN_PHUONG, -1, stringBuilder.toString(), "Học Đan\nPhương", "Xem Đan\nPhương");
    }

    // Hiển thị danh sách DanPhuong theo phân trang
    public void showBaseDanPhuong(int page, int perPage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Túi Dan Phương\n");

        int total = danPhuongs.size();
        if (total == 0) {
            Service.gI().sendThongBao(player, "Không có đan phương nào trong túi");
            return;
        }

        // Tính tổng số trang
        int maxPage = (total + perPage - 1) / perPage;

        // Tính index bắt đầu của trang hiện tại
        int currentIndex = page * perPage;

        // Giới hạn hiển thị
        int end = Math.min(currentIndex + perPage, total);

        if (currentIndex >= total || currentIndex < 0) {
            stringBuilder.append("Đã hết\n");
        } else {
            for (int i = currentIndex; i < end; i++) {
                DanPhuong danPhuong = danPhuongs.get(i);
                stringBuilder.append("|5|[").append(danPhuong.id).append("] ")
                        .append(danPhuong.tenDanPhuong)
                        .append(" - Cấp yêu cầu: ").append(danPhuong.capYeuCauHoc)
                        .append("\n");
            }
        }

        String[] menuChonTrang = new String[maxPage];
        for (int i = 0; i < maxPage; i++) {
            menuChonTrang[i] = "Ngăn " + i;
        }

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_XEM_DAN_PHUONG, -1, stringBuilder.toString(), menuChonTrang);
    }

    // Lấy DanPhuong từ túi theo tiêu chí nào đó (ví dụ theo id)
    public DanPhuong takeDanPhuong(int id) {
        return danPhuongs.stream().filter(t -> t.id == id).findFirst().orElse(null);
    }
}
