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

/**
 * @@Stole By NDQ
 */
public class ThoTrang2 extends Boss {

    public ThoTrang2() throws Exception {
        super(BossID.BOSS_THOTRANG, BossesData.BOSS_THOTRANG);
    }

    //    private long lateTimeCheckItem;
//    private long lateTimeCheckItem2;
    @Override
    public void reward(Player plKill) {
        ItemMap it = new ItemMap(this.zone, 1318, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        int a = 0;
        for (int i = 0; i < 8; i++) {
            ItemMap it1 = new ItemMap(this.zone, 457, 1, this.location.x + a,
                    this.zone.map.yPhysicInTop(this.location.x,
                            this.location.y - 24),
                    plKill.id);
            Service.getInstance().dropItemMap(this.zone, it1);
            a += 10;
        }
//        it.options.add(new Item.ItemOption(73, 1));
        Service.getInstance().dropItemMap(this.zone, it);
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1800000)) {
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
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean a) {
        if (!this.isDie()) {
            byte damageLimit = 1;
            if (!a) {
                if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                    this.chat("Xí hụt");
                    return 0;
                }
                if (plAtt != null) {
                    if (plAtt.setClothes.isThuongLinhDietMa) {
                        damageLimit += 5;
                    } else if (plAtt.setClothes.isDaoYeuLinhPhucMa) {
                        damageLimit += 3;
                    }
                    damageLimit += plAtt.setClothes.pkkhMedusa * 2;
                }
            }

            this.nPoint.subHP(damageLimit);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
                wakeupAnotherBossWhenDisappear();
            }
            return damageLimit;

        } else {
            return 0;
        }
    }
}
