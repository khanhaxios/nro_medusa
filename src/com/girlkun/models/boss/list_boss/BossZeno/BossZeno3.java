/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.BossZeno;

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
 * @@Edit by ndq
 */
public class BossZeno3 extends Boss {

    public BossZeno3() throws Exception {
        super(BossID.BOSS_ZENO3, BossesData.BOSS_NU_VUONG_MEDUSA_COLDER);
    }

    @Override
    public void reward(Player plKill) {
        if (plKill.chienthan.tasknow == 10) {
            plKill.chienthan.dalamduoc++;
        }
        
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        ItemMap it1 = new ItemMap(this.zone, 1517, 1, this.location.x - 10, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        it1.options.add(new Item.ItemOption(30, 1));
        Service.getInstance().dropItemMap(this.zone, it1);
        if (Util.isTrue(1, 200)) {
            ItemMap it2 = new ItemMap(this.zone, 1610, 1, this.location.x - 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            it1.options.add(new Item.ItemOption(30, 1));
            Service.getInstance().dropItemMap(this.zone, it2);
        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 3000000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

}
