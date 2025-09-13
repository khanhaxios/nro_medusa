/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.BossCU;

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
public class BossCU7 extends Boss {

    public BossCU7() throws Exception {
        super(BossID.UCHIHA_SSS, BossesData.UCHIHA_SSS);
    }

    @Override
    public void reward(Player plKill) {
        
        int a = 0;
        for (int i = 0; i < 8; i++) {
            ItemMap it1 = new ItemMap(this.zone, 1340, 1, this.location.x + a, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it1);
            a += 10;
        }
        ItemMap it1 = new ItemMap(this.zone, 1341, 1, this.location.x - 10, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, it1);
        ItemMap daMedusa = new ItemMap(this.zone, 1079, Util.nextInt(1, 3), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        daMedusa.options.add(new Item.ItemOption(30, 0));
        Service.getInstance().dropItemMap(this.zone, daMedusa);
        Util.ratioManhPhapBao(zone, 1, this.location.x, this.location.y, plKill.id, 100);

        Util.ratioVeNangCap(zone, 1, this.location.x, this.location.y, plKill.id, 100);
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 2500000)) {
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
