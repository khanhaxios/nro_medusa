/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

public class BossViet extends Boss {

    public BossViet() throws Exception {
        super(BossID.BOSS_VIET, BossesData.BOSS_VIET);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        int[] itemDos = new int[]{2030};
        int[] NRs = new int[]{722};
        int randomDo = new Random().nextInt(itemDos.length);
        int randomNR = new Random().nextInt(NRs.length);
        if (Util.isTrue(90, 100)) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.location.y, plKill.id));
            return;
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, NRs[randomNR], 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        if (Util.isTrue(5, 500)) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 1599, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 2076, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 900000)) {
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
        damage = 500;
        isSTChuan = true;
        return super.injured(plAtt, damage, piercing, isMobAttack, isSTChuan);
    }
}
