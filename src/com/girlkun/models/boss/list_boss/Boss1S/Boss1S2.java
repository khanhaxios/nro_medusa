/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.Boss1S;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @
 */
public class Boss1S2 extends Boss {

    public Boss1S2() throws Exception {
        super(BossID.BOSS_NRO1S2, BossesData.BOSS_NRO1S2);
    }

    @Override
    public void reward(Player plKill) {
        
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        ItemMap it = new ItemMap(this.zone, 1236, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        it.options.add(new Item.ItemOption(30, 1));
        Service.getInstance().dropItemMap(this.zone, it);
        int a = 20;
        for (int i = 0; i < 5; i++) {
            ItemMap it1 = new ItemMap(this.zone, 1235, 1, this.location.x + a, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it1);
            a += 10;
        }
        if (Util.isTrue(5, 100)) {
            ItemMap it1 = new ItemMap(this.zone, 1471, 1, this.location.x - a, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it1);
        }
        Util.ratioManhPhapBao(zone, 1, this.location.x, this.location.y, plKill.id, 100);

        Util.ratioVeNangCap(zone, 1, this.location.x, this.location.y, plKill.id, 100);
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 3500000)) {
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
