/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.BossTet;

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
 * @@Edit by ndq
 */
public class BossTET9 extends Boss {

    public BossTET9() throws Exception {
        super(BossID.BOSS_TET9, BossesData.BOSS_TET9);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        ItemMap it = new ItemMap(this.zone, 753, 4, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
      //  it.options.add(new Item.ItemOption(0, 55000));
        it.options.add(new Item.ItemOption(30, 0));
        Service.getInstance().dropItemMap(this.zone, it);
         int a=0;
                for (int i=0; i<8; i++)
                {
                      ItemMap it1 = new ItemMap(this.zone, 750, 50, this.location.x + a, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24),  plKill.id);
                       it.options.add(new Item.ItemOption(30, 0));
            Service.getInstance().dropItemMap(this.zone, it1);
            a+=10;
                }
                ItemMap it1 = new ItemMap(this.zone, 751, 55, this.location.x - 10, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24),  plKill.id);
                it.options.add(new Item.ItemOption(30, 0));
            Service.getInstance().dropItemMap(this.zone, it1);
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

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
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
