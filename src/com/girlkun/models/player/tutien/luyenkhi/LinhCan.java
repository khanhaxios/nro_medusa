package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.consts.ConstNpc;
import com.girlkun.services.NpcService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LinhCan {
    TuTien tuTien;
    private byte linhCanType;
    private ThuocTinhLinhCan thuocTinhLinhCan;


    public LinhCan(TuTien tuTien) {
        thuocTinhLinhCan = new ThuocTinhLinhCan();
        this.tuTien = tuTien;
    }

    public String getThuocTinhName() {
        return getLinhCanName(linhCanType);
    }

    static String getLinhCanName(byte linhCanType) {
        switch (linhCanType) {
            case 0:
                return "Kim";
            case 1:
                return "Mộc";
            case 2:
                return "Thủy";
            case 3:
                return "Hỏa";
            case 4:
                return "Thổ";
            case 5:
                return "Phong";
            case 6:
                return "Lôi";
            case 7:
                return "Quang";
            case 8:
                return "Ám";
        }
        return "Vô thuộc tính";
    }

    public String getHieuQuaLinhCan() {
        String text = "";
        switch (linhCanType) {
            case 0:
                text = String.format("-Bỏ qua tất cả phòng thủ của đối phương\n-Dưới %s máu miễn mọi khống chế\n-Mỗi mất %s hp tăng %s sát thương linh căn(tối đa %s)", thuocTinhLinhCan.getParam() / 2 + "%", "1%", "1%", 100 + "%");
                break;
            case 1:
                text = String.format("-Tăng %s khả năng hút máu\n-Đòn đánh thường có %s tỷ lệ gây choáng\n-Hồi phục linh khí mạnh mẽ", thuocTinhLinhCan.getParam() * 3 + "%", (thuocTinhLinhCan.getParam() / 10) + "%");
                break;
            case 2:
                text = String.format("-Tăng %s né tránh và %s phản sát thương\n-Khi máu dưới %s miễn mọi khống chế\n-Hồi phục linh khí mạnh mẽ", thuocTinhLinhCan.getParam() / 5 + "%", thuocTinhLinhCan.getParam() + "%", "70%");
                break;
            case 3:
                text = String.format("-Sát thương cộng dồn mỗi đòn đánh(tối đa lên đến %s)", thuocTinhLinhCan.getParam() + "%");
                break;
            case 4:
                text = String.format("-Tăng %s Giáp và %s Giảm sát thương\n-Tỷ lệ phản sát thương %s", thuocTinhLinhCan.getParam() * 10 + "%", thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 5 + "%");
                break;
            case 5:
                text = String.format("-Đánh thường có %s tỷ lệ gây chí mạng và %s sát thương chí mạng\n-Tăng tỷ lệ chí mạng mỗi đòn đánh(tối đa lên đến %s)\nCó %s tỷ lệ né đòn", thuocTinhLinhCan.getParam() / 3 + "%", 100 + thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 3 + "%", thuocTinhLinhCan.getParam() / 10 + "%");
                break;
            case 6:
                text = String.format("-Đánh thường có %s tỷ lệ gây chí mạng và %s sát thương chí mạng\n-Tăng tỷ lệ chí mạng mỗi đòn đánh(tối đa lên đến %s)\nCó %s tỷ lệ gây choáng đối phương", thuocTinhLinhCan.getParam() / 2 + "%", 100 + thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 2 + "%", thuocTinhLinhCan.getParam() / 10 + "%");
                break;
            case 7:
                text = String.format("-Giảm %s sát thương nhận vào\n-Gây sát thương chuẩn bằng %s sát thương hiện có\nThần thánh thẩm phán", thuocTinhLinhCan.getParam() / 5 + "%", thuocTinhLinhCan.getParam() + "%");
                break;
            case 8:
                text = String.format("-Bất tử\n-Tăng %s né\nĐòn đánh thường có %s tỷ lệ gây choáng đối thủ\nCó %s tỷ lệ gây ra sát thương ám ảnh mỗi giây -0.2 phần trăm hp", thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 10 + "%", thuocTinhLinhCan.getParam() / 10 + "%");
                break;

        }
        return text;
    }

    public void showMenuLinhCan() {
        String npc = String.format("|7|Thông Tin Linh Căn\n|5|Linh Căn  : %s\n|5|Hiệu Quả\n%s",
                getLinhCanName(getThuocTinhLinhCan().getLinhCanBatBuoc()), getHieuQuaLinhCan());
        NpcService.gI().createMenuConMeo(tuTien.player, ConstNpc.MENU_TT_LINH_CAN, -1, npc, "Đóng");
    }
}
