package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TuiNguyenLieu {
    Player player;

    public TuiNguyenLieu(Player player) {
        this.player = player;
    }

    int currentIndex = 0;
    int maxPage = 0;

    public List<NguyenLieu> nguyenLieus = new ArrayList<>();

    public void addNguyenLieu(NguyenLieu nguyenLieu) {
        for (NguyenLieu existing : nguyenLieus) {
            if (existing.id == nguyenLieu.id && existing.quality == nguyenLieu.quality) {
                existing.quantity += nguyenLieu.quantity;
                return;
            }
        }
        nguyenLieus.add(nguyenLieu);
    }

    public void removeNguyenLieu(NguyenLieu nguyenLieu) {
        this.nguyenLieus.remove(nguyenLieu);
    }

    public void subNguyenLieuQuantity(int id, int quantity, int quality) {
        for (NguyenLieu nl : nguyenLieus) {
            if (nl.id == id && nl.quality == quality) {
                nl.quantity = Math.max(0, nl.quantity - quantity); // không để âm
                break;
            }
        }
        removeIfNoQuantity(id, quality);
    }

    public void removeIfNoQuantity(int id, int quality) {
        Iterator<NguyenLieu> iterator = this.nguyenLieus.iterator();
        while (iterator.hasNext()) {
            NguyenLieu nguyenLieu = iterator.next();
            if (nguyenLieu.id == id && nguyenLieu.quantity == 0) {
                iterator.remove();  // Xóa phần tử hiện tại khỏi danh sách
                break;  // Nếu chỉ muốn xóa 1 phần tử, bạn có thể dừng lại sau khi xóa
            }
        }
    }

    public void showBaseNguyenLieu(int page, int perPage) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Túi Nguyên Liệu\n");

        int total = nguyenLieus.size();
        if (total == 0) {
            Service.gI().sendThongBao(player, "Không có nguyên liệu nào trong túi");
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
                NguyenLieu danPhuong = nguyenLieus.get(i);
                stringBuilder.append("|5|[").append(danPhuong.id).append("] ").append(danPhuong.tenNguyenLieu).append("[").append(danPhuong.getPhamChat()).append("]").append(" x").append(danPhuong.quantity).append("\n");
            }
        }

        String[] menuChonTrang = new String[maxPage];
        for (int i = 0; i < maxPage; i++) {
            menuChonTrang[i] = "Ngăn " + i;
        }

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_XEM_NGUYEN_LIEU, -1, stringBuilder.toString(), menuChonTrang);
    }

    public List<NguyenLieu> takeNguyenLieu(DanPhuong danPhuong) {
        List<NguyenLieu> nguyenLieuList = new ArrayList<>();
        for (NguyenLieu lieus : nguyenLieus) {
            NguyenLieu nl = lieus;
            for (NguyenLieu nguyenLieu : danPhuong.nguyenLieu) {
                if (lieus.id == nguyenLieu.id) {
                    if (nguyenLieu.quality > nl.quality) {
                        nl = nguyenLieu;
                    }
                }
            }
            if (nl != null) {
                nguyenLieuList.add(nl);
            }
        }
        return nguyenLieuList;
    }

    public void showMenuTui() {
    }
}
