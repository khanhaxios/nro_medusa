/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

/**
 * @
 */
public class BossChienThan extends Boss {

    public BossChienThan() throws Exception {
        super(BossID.BOSS_CHIENTHAN, BossesData.BOSS_CHIENTHAN);
    }

    @Override
    public void reward(Player plKill) {
        
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        int[] DaTienMon = new int[]{1260, 1261, 1262};
        int randomDA = new Random().nextInt(DaTienMon.length);
        if (Util.isTrue(50, 100)) {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, DaTienMon[randomDA], Util.nextInt(10, 100), this.location.x, this.location.y, plKill.id));
        }
    }
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;
}
