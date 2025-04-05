package com.girlkun.services;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.network.io.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubMenuService {

    public static final int BAN = 500;
    public static final int BUFF_PET = 501;
    public static final int KET_HON = 502;

    private static SubMenuService i;

    private SubMenuService() {
    }

    public static SubMenuService gI() {
        if (i == null) {
            i = new SubMenuService();
        }
        return i;
    }

    public void controller(Player player, int playerTarget, int menuId) {
        Player plTarget = Client.gI().getPlayer(playerTarget);
        switch (menuId) {
            case BAN:
                if (plTarget != null) {
                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                            "Bạn có chắc chắn muốn ban " + plTarget.name, selects, plTarget);
                }
                break;
            case BUFF_PET:
                if (plTarget != null) {
                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                    NpcService.gI().createMenuConMeo(player, ConstNpc.BUFF_PET, -1,
                            "Bạn có chắc chắn muốn phát đệ tử cho " + plTarget.name, selects, plTarget);
                }
                break;
            case KET_HON:
                if (plTarget != null) {
                    Item ctrang = null;
                    try {
                        ctrang = InventoryServiceNew.gI().findItemBody(player, 1252);
                    } catch (Exception e) {
                    }
                    if (!player.inventory.itemsBody.get(5).isNotNullItem() || ctrang == null) {
                        Service.gI().sendThongBao(player, "Yêu cầu mang cải trang Xiao Genshin Impact");
                        return;
                    }
                    if (plTarget.duockethon >= 20) {
                        Service.gI().sendThongBao(player, "Đối phương đã đạt tối đa 20 lần nhận được kết hôn");
                        return;
                    }
                    if (player.dakethon >= 50) {
                        Service.gI().sendThongBao(player, "Bạn đã đạt tối đa 50 lần Cầu hôn!!!");
                    } else {
                        String[] selects = new String[]{"Đồng ý", "Hủy"};
                        NpcService.gI().createMenuConMeo(player, ConstNpc.KETHON_PLAYER, -1,
                                "Bạn có chắc chắn muốn Kết hôn với " + plTarget.name, selects, plTarget);
                    }
                }
                break;
        }
        Service.getInstance().hideWaitDialog(player);
    }

    public void showMenuForAdmin(Player player) {
        showSubMenu(player, new SubMenu(BAN, "Ban người chơi", ""));
    }

    public void showKetHon(Player player) {
        showSubMenu(player, new SubMenu(KET_HON, "Kết hôn", ""));
    }

    public void showSubMenu(Player player, SubMenu... subMenus) {
        Message msg;
        try {
            msg = Service.getInstance().messageSubCommand((byte) 63);
            msg.writer().writeByte(subMenus.length);
            for (SubMenu subMenu : subMenus) {
                msg.writer().writeUTF(subMenu.caption1);
                msg.writer().writeUTF(subMenu.caption2);
                msg.writer().writeShort((short) subMenu.id);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(SubMenuService.class, e);
        }
    }

    public static class SubMenu {

        private int id;
        private String caption1;
        private String caption2;

        public SubMenu(int id, String caption1, String caption2) {
            this.id = id;
            this.caption1 = caption1;
            this.caption2 = caption2;
        }
    }
}
