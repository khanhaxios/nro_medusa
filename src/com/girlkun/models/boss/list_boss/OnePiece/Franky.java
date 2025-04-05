/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.OnePiece;

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
import java.util.Random;

/**
 * @@Code rewritten by ndqitVN (Zalo - 0372475179)
 */
public class Franky extends Boss {

    public Franky() throws Exception {
        super(BossID.FRANKY, BossesData.FRANKY);
    }

    @Override
    public void reward(Player plKill) {
        int[] itemDos = new int[]{1198};
        int rand = Util.nextInt(1, 100);
        ItemMap itemReward;
        int randomDo = new Random().nextInt(itemDos.length);
        itemReward = new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        if (rand < 95) {
            itemReward.options.add(new Item.ItemOption(230, 1));
            itemReward.options.add(new Item.ItemOption(50, 200));
            itemReward.options.add(new Item.ItemOption(77, 300));
            itemReward.options.add(new Item.ItemOption(103, 300));
            itemReward.options.add(new Item.ItemOption(93, Util.nextInt(1, 3)));
        } else {
            itemReward.options.add(new Item.ItemOption(230, 1));
            itemReward.options.add(new Item.ItemOption(50, 500));
            itemReward.options.add(new Item.ItemOption(77, 1000));
            itemReward.options.add(new Item.ItemOption(103, 1000));

        }
        Service.getInstance().dropItemMap(this.zone, itemReward);
    }

    @Override
    public void active() {
        if (this.actFight) {
            super.active(); //To change body of generated methods, choose Tools | Templates.
        }
        if (Util.canDoWithTime(st, 1500000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
        this.actFight = false;
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
    
    @Override
    public void wakeupAnotherBossWhenDisappear() {
        if (this.parentBoss == null) {
            return;
        }
        int idx = 0;
        boolean zoroDie = false;
        boolean sanjiDie = false;
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.RORONOA_ZORO && boss.isDie()) {
                zoroDie = true;
                idx ++;
            }
            if (boss.id == BossID.VINSMOKE_SANJI && boss.isDie()) {
                sanjiDie = true;
                idx ++;
            }
            if (idx == 2) {
                break;
            }
        }
        if (zoroDie && sanjiDie) {
//            for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
//                if (boss.id == BossID.LUFFY && !boss.isDie()) {
//                    boss.actFight = true;
//                    boss.chat("Sao ngươi dám đụng đến đồng đội của taaaa.....");
//                    return;
//                }
//            }
            this.parentBoss.actFight = true;
            this.parentBoss.chat("Sao ngươi dám đụng đến đồng đội của taaaa.....");
        }

    }
}
