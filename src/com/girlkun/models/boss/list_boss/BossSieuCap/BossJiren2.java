/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.BossSieuCap;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @@Edit by ndq
 */
public class BossJiren2 extends Boss {

    private long lastTimeHoiSuc = System.currentTimeMillis();

    public BossJiren2() throws Exception {
        super(BossID.BOSS_JIREN2, BossesData.BOSS_JIREN2);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        Util.ratioTrangBi(zone, 1, this.location.x, this.location.y, plKill.id, 10, 4);
        ItemMap dns = new ItemMap(zone, 674, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, dns);
    }

    @Override
    public void update() {
        super.update(); //To change body of generated methods, choose Tools | Templates.
        if ((this.bossStatus == BossStatus.CHAT_S || this.bossStatus == BossStatus.ACTIVE)
                && Util.canDoWithTime(st, 2700000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
        if (this.bossStatus == BossStatus.ACTIVE) {
            if (Util.canDoWithTime(lastTimeHoiSuc, 20000) && !this.effectSkill.isStun && this.nPoint.hp < this.nPoint.hpMax) {
                this.nPoint.hp += this.nPoint.hpMax * 20 / 100;
                this.chat("Hồi sức aaaaa");
                lastTimeHoiSuc = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void joinMap() {

        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            // if (damage > this.nPoint.hpMax * 2 / 100) {
            //     damage = this.nPoint.hpMax * 1 / 100;
            // }
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
}
