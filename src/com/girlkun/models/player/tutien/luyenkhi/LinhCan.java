package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.consts.ConstNpc;
import com.girlkun.services.NpcService;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LinhCan {
    TuTien tuTien;
    private byte linhCanType;
    private ThuocTinhLinhCan thuocTinhLinhCan;


    public LinhCan() {
        thuocTinhLinhCan = new ThuocTinhLinhCan();
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

    public void showMenuLinhCan() {
        String npc = String.format("|7|Thông Tin Linh Căn\n|5|Linh Căn  : %s\n|5|Hiệu Quả : %s", getThuocTinhLinhCan(), thuocTinhLinhCan.getTenThuocTinhReplace());
        NpcService.gI().createMenuConMeo(tuTien.player, ConstNpc.MENU_TT_LINH_CAN, -1, npc, "Đóng");
    }
}
