/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class BaseTuDuy {
    public int sucManh;
    public int tinhThan;
    public int nhanhNhen;
    public int theChat;

    public void increasePoint(Player pl, Item item) {
        if (Util.isTrue(1, 100)) {
            nhanhNhen += 1;
        } else if (Util.isTrue(5, 100)) {
            // tang suc manh
            sucManh += 1;
        } else if (Util.isTrue(10, 100)) {
            tinhThan += Util.nextInt(1, 2);
        } else {
            theChat += Util.nextInt(1, 2);
        }
        Service.gI().sendThongBao(pl, "Dùng tinh thạch thành công tứ duy thuộc tính của bạn tăng lên một chút");
        InventoryServiceNew.gI().subQuantityItemsBag(pl, item, 1);
        InventoryServiceNew.gI().sendItemBags(pl);
        Service.gI().point(pl);
    }
}
