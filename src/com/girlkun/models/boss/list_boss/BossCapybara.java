/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @@Rewrite By NDQ
 */
public class BossCapybara extends Boss {
    public BossCapybara() throws Exception {
        super(BossID.BOSS_CAPYBARA, BossesData.BOSS_CAPYPARA);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        if (Util.isTrue(10, 100)) {
            ItemMap capybara = new ItemMap(zone, 1481, 1, this.location.x, this.location.y, plKill.id);
            capybara.options.add(new Item.ItemOption(230, 1));
            capybara.options.add(new Item.ItemOption(247, 1));
            capybara.options.add(new Item.ItemOption(50, Util.nextInt(300, 500)));
            capybara.options.add(new Item.ItemOption(30, 1));
            Service.getInstance().dropItemMap(this.zone, capybara);
        } else {
            ItemMap nr1s = new ItemMap(zone, 14, 1, this.location.x, this.location.y, plKill.id);
            Service.getInstance().dropItemMap(this.zone, nr1s);
        }
    }

    @Override
    public void update() {
        super.update(); //To change body of generated methods, choose Tools | Templates.
        if ((this.bossStatus == BossStatus.CHAT_S || this.bossStatus == BossStatus.ACTIVE)
                && Util.canDoWithTime(st, 2700000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean isSTChuan) {
        if (damage > nPoint.getPercentHp(10)) {
            damage = nPoint.getPercentHp(10);
        }
        return super.injured(plAtt, damage, piercing, isMobAttack, isSTChuan);
    }
}
