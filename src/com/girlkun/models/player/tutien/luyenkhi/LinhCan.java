/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
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
                text = String.format("Đòn đánh thường gây sát thương bùng nổ bằng\n %s Sát thương linh căn", Math.max(1, thuocTinhLinhCan.getParam() / 100) * (tuTien.congPhap.tier + 1 + tuTien.xParam) + "%");
                break;
            case 1:
                text = String.format("Tăng %s khả năng hút máu\n-Đòn đánh thường có %s tỷ lệ gây choáng\nĐòn đánh thường gây %s sát thương chuẩn", thuocTinhLinhCan.getParam() + "%", (thuocTinhLinhCan.getParam() / 20) + "%", thuocTinhLinhCan.getParam() / 20f * (tuTien.congPhap.tier + 1 + tuTien.xParam) + "%");
                break;
            case 2:
                text = String.format("Tăng %s né tránh và %s phản sát thương\nĐòn đánh thường gây Sát thương bằng %s KI", thuocTinhLinhCan.getParam() / 5 + "%", thuocTinhLinhCan.getParam() / 5 + "%", thuocTinhLinhCan.getParam() * (tuTien.congPhap.tier + 1 + tuTien.xParam) + "%");
                break;
            case 3:
                text = String.format("Sát thương cộng dồn mỗi đòn đánh(tối đa lên đến %s)", thuocTinhLinhCan.getParam() + "%");
                break;
            case 4:
                text = String.format("Tăng %s Giáp và %s Giảm sát thương\nTỷ lệ phản sát thương %s\nĐòn đánh thường gây Sát thương bằng %s HP", thuocTinhLinhCan.getParam() * 10 + "%", thuocTinhLinhCan.getParam() * 10 + "%", thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() * (tuTien.congPhap.tier + 1 + tuTien.xParam) + "%");
                break;
            case 5:
                text = String.format("Đánh thường có %s tỷ lệ gây chí mạng và %s sát thương chí mạng\nTăng tỷ lệ chí mạng mỗi đòn đánh(tối đa lên đến %s)\nCó %s tỷ lệ né đòn", thuocTinhLinhCan.getParam() / 3 + "%", 100 + thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 3 + "%", thuocTinhLinhCan.getParam() / 10 + "%");
                break;
            case 6:
                text = String.format("Đánh thường có %s tỷ lệ gây chí mạng và %s sát thương chí mạng\nTăng tỷ lệ chí mạng mỗi đòn đánh(tối đa lên đến %s)\nCó %s tỷ lệ gây choáng đối phương", thuocTinhLinhCan.getParam() / 2 + "%", 100 + thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 2 + "%", thuocTinhLinhCan.getParam() / 10 + "%");
                break;
            case 7:
                text = String.format("Giảm %s sát thương nhận vào\n-Gây sát thương chuẩn bằng %s sát thương hiện có\nThần thánh thẩm phán", thuocTinhLinhCan.getParam() / 5 + "%", thuocTinhLinhCan.getParam() + "%");
                break;
            case 8:
                text = String.format("Bất tử\n-Tăng %s né\nĐòn đánh thường có %s tỷ lệ gây choáng đối thủ\nCó %s tỷ lệ gây ra sát thương ám ảnh mỗi giây -0.2 phần trăm hp", thuocTinhLinhCan.getParam() + "%", thuocTinhLinhCan.getParam() / 10 + "%", thuocTinhLinhCan.getParam() / 10 + "%");
                break;

        }
        return text;
    }

    public void showMenuLinhCan() {
        String npc = String.format("|7|Thông Tin Linh Căn\n|5|Linh Căn  : %s(%s)\n|5|Hiệu Quả\n%s",
                getLinhCanName(getThuocTinhLinhCan().getLinhCanBatBuoc()), getThuocTinhLinhCan().getParam() + "%", getHieuQuaLinhCan());
        NpcService.gI().createMenuConMeo(tuTien.player, ConstNpc.MENU_TT_LINH_CAN, -1, npc, "Dưỡng Linh", "Đóng");
    }

    public void duongLinhCanMenu() {
        String menuText = "|7|Thông tin Linh Căn" + "\n" +
                "|5|Độ tinh khiết : " + thuocTinhLinhCan.getParam() + "%" + "\n" +
                "|5|Tỷ lệ thành công : " + getTyLeThanhCong() + "%" + "\n" +
                "|5|Cần x1" + getItemNeed().template.name + "\n" +
                "|7|Dùng thuộc tính linh thạch cùng loại với linh căn của bạn để bồi dưỡng linh căn" + "\n" +
                "|7|Sau khi thành công linh căn sẽ tăng lên một chút" + "\n";
        NpcService.gI().createMenuConMeo(tuTien.player, ConstNpc.MENU_DUONG_LINH, -1, menuText, "Dưỡng Linh", "Đóng");
    }

    private Item getItemNeed() {
        int id = ItemService.gI().getItemDuongLinh(getLinhCanType());
        return InventoryServiceNew.gI().findItemBag(tuTien.player, id);
    }

    private float getTyLeThanhCong() {
        float param = thuocTinhLinhCan.getParam();
        return 100f / (1f + (param / 60f));
    }

    public void duongLinh() {
        int itemId = ItemService.gI().getItemDuongLinh(getLinhCanType());
        if (itemId == -1) {
            Service.gI().sendThongBao(tuTien.player, "Có lỗi xảy ra hãy thử lại sau");
            return;
        }
        Item item = InventoryServiceNew.gI().findItemBag(tuTien.player, itemId);
        if (item == null || !item.isNotNullItem()) {
            Service.gI().sendThongBao(tuTien.player, "Không tìm thấy vật phẩm cần thiết");
            return;
        }
        float tyLe = getTyLeThanhCong();
        if (Util.isTrue(tyLe, 150)) {
            thuocTinhLinhCan.setParam((short) (thuocTinhLinhCan.getParam() + 1));
            Service.gI().sendThongBaoOK(tuTien.player, "Dưỡng linh thành công");
            InventoryServiceNew.gI().subQuantityItemsBag(tuTien.player, item, 1);
            InventoryServiceNew.gI().sendItemBags(tuTien.player);
            return;
        }
        Service.gI().sendThongBao(tuTien.player, "Dưỡng linh thất bại");
        InventoryServiceNew.gI().subQuantityItemsBag(tuTien.player, item, 1);
        InventoryServiceNew.gI().sendItemBags(tuTien.player);
    }
}
