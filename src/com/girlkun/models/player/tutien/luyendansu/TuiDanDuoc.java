package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;

import java.util.ArrayList;
import java.util.List;

public class TuiDanDuoc {
    Player player;

    public TuiDanDuoc(Player player) {
        this.player = player;
    }

    public List<DanDuoc> danDuocs = new ArrayList<>();

    public void addDanDuoc(DanDuoc danDuoc) {
        for (DanDuoc existing : danDuocs) {
            if (existing.id == danDuoc.id && existing.capDanDuoc == danDuoc.capDanDuoc) {
                existing.quantity += danDuoc.quantity;
                return;
            }
        }
        danDuocs.add(danDuoc);
    }

    public void removeDanDuoc(DanDuoc danDuoc) {
        this.danDuocs.remove(danDuoc);
    }

    public void removeIfNoQuantity(int id, int quality) {
        for (int i = danDuocs.size() - 1; i >= 0; i--) {
            DanDuoc danDuoc = danDuocs.get(i);
            if (danDuoc.id == id && danDuoc.capDanDuoc == quality && danDuoc.quantity <= 0) {
                danDuocs.remove(i);
            }
        }
    }

    public void subDanDuocQuantity(int id, int quantity, int quality) {
        for (int i = 0; i < danDuocs.size(); i++) {
            DanDuoc danDuoc = danDuocs.get(i);
            if (danDuoc.id == id && danDuoc.capDanDuoc == quality) {
                danDuoc.quantity -= quantity;
                break; // chỉ giảm cho 1 item
            }
        }
        removeIfNoQuantity(id, quality); // sửa hàm remove
    }


    public void showBaseNguyenLieu(int page, int perPage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Túi Đan Dược\n");

        int total = danDuocs.size();
        if (total == 0) {
            Service.gI().sendThongBao(player, "Không có đan dược nào trong túi");
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
                DanDuoc lieu = danDuocs.get(i);
                stringBuilder.append("|5|[").append(lieu.id).append("] ").append("[").append(lieu.getNameByCap()).append("]")
                        .append(lieu.tenDanDuoc)
                        .append(" x").append(lieu.quantity)
                        .append("\n");
            }
        }

        String[] menuChonTrang = new String[maxPage];
        for (int i = 0; i < maxPage; i++) {
            menuChonTrang[i] = "Ngăn " + i;
        }

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_XEM_DAN_DUOC, -1, stringBuilder.toString(), menuChonTrang);
    }

    public void showMenuTui() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Túi Dan Dược\n");
        stringBuilder.append("|5|").append("Bạn có thể dùng đan dược bạn đang có ở đây");
        stringBuilder.append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DAN_DUOC, -1, stringBuilder.toString(), "Dùng Đan\nDược", "Xem Đan\nDược");
    }

    public DanDuoc takeDanDuoc(int idDanDuoc, int quantity) {
        return this.danDuocs.stream().filter(t -> t.id == idDanDuoc && t.quantity >= quantity).findFirst().orElse(null);
    }
}
