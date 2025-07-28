package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.List;

public class ToiThe {
    // ====== TUNABLES ======
    Player player;
    public static final int BASE_COST_PER_MAT = 1000;
    public static final int MAX_TIER = 99;
    public static int BUFF_GROWTH = 4;

    public int tier;

    public ToiThe(Player player) {
        this.player = player;
    }

    public float getDameBuff() {
        tier = Math.max(tier, 1);
        int base = 20;
        return tier * (base + BUFF_GROWTH * tier);
    }

    public float getHPMPBuff() {
        tier = Math.max(tier, 1);
        int base = 20;
        return tier * (base + (BUFF_GROWTH + 1) * tier);
    }

    public void showBaseMenu() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|====== Thông tin tôi thể ======").append("\n");
        stringBuilder.append("|5|Cấp tôi thể  ").append(tier).append("\n");
        stringBuilder.append("|5|Dame Buff : ").append(getDameBuff()).append("%").append("\n");
        stringBuilder.append("|5|HPMP Buff : ").append(getHPMPBuff()).append("%").append("\n");
        stringBuilder.append("|5|Tỷ lệ thành công  ").append(getPercentLevelUp()).append("%").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TOI_THE, -1, stringBuilder.toString(), "Tôi thể", "Đóng");
    }

    public void showMenuToiThe() {
        int nextTier = Math.min(tier + 1, MAX_TIER);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|====== Tôi thể ======").append("\n");
        stringBuilder.append("|5|Cấp tôi thể tiếp theo ").append(nextTier).append("\n");
        stringBuilder.append("|7|Tỷ lệ thành công  ").append(getPercentLevelUp()).append("%").append("\n");
        stringBuilder.append(getNguyenLieuCan());
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_TT, -1, stringBuilder.toString(), "Tôi thể", "Đóng");
    }

    private String getNguyenLieuCan() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1260; i <= 1266; i++) {
            stringBuilder.append("|7|Cần x").append(Math.max(tier + 1, 1) * 1000).append(ItemService.gI().getTemplate(i).name).append("\n");
        }
        return stringBuilder.toString();
    }

    private float getPercentLevelUp() {
        return Math.max(0.3f, 100f - (tier - 1) * 5f);
    }

    public void toiLuyen() {
        // check item
        if (tier + 1 > MAX_TIER) {
            Service.gI().sendThongBao(player, "Bạn đã đạt tầng tối đa");
            return;
        }
        List<Item> items = InventoryServiceNew.gI().takeItemToiThe(player);
        if (items.size() < 7) {
            Service.gI().sendThongBao(player, "Không đủ đá");
            return;
        }
        int needyQuantity = Math.min(tier + 1, 99) * BASE_COST_PER_MAT;
        boolean canHandle = true;
        for (Item item : items) {
            if (item.quantity < needyQuantity) {
                canHandle = false;
                Service.gI().sendThongBao(player, "Cần x" + needyQuantity + " " + item.template.name);
                break;
            }
        }
        if (!canHandle) {
            return;
        }
        // sub quantity item
        InventoryServiceNew.gI().subQuantityItemsBag(player, items, needyQuantity);
        InventoryServiceNew.gI().sendItemBags(player);
        // handle
        if (!Util.isTrue(getPercentLevelUp(), 100)) {
            Service.gI().sendThongBao(player, "Tôi thể thất bại");
            return;
        }
        // toi the thanh cong
        tier += 1;
        Service.gI().point(player);
    }

    public void calcPoint() {
        player.nPoint.dameAdd += player.nPoint.dameAdd * getDameBuff() / 100;
        player.nPoint.hpAdd += player.nPoint.hpAdd * getHPMPBuff() / 100;
        player.nPoint.mpAdd += player.nPoint.mpAdd * getHPMPBuff() / 100;
    }

    public boolean isOpen() {
        return tier > 0;
    }

    public void openSystem() {
        this.tier = 1;
        Service.gI().sendThongBao(player, "Bạn kích hoạt tôi thể thành công");
        Service.gI().point(player);
    }
}
