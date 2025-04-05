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
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.Random;

/**
 * @@Code rewritten by ndqitVN (Zalo - 0372475179)
 */
public class Chopper extends Boss {

    public Chopper() throws Exception {
        super(BossID.CHOPPER, BossesData.CHOPPER);
    }

    @Override
    public void reward(Player plKill) {
        int rand = Util.nextInt(1, 100);
        ItemMap itemReward;
        if (rand > 97) {
            int[] itemDos = new int[]{1461, 1462, 1463};
            int randomDo = new Random().nextInt(itemDos.length);
            itemReward = new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
        } else {
            int[] itemDos = new int[]{1474, 1475, 1476};
            int randomDo = new Random().nextInt(itemDos.length);
            itemReward = new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            switch (itemDos[randomDo]) {
                case 1474:
                    itemReward.options.add(new Item.ItemOption(50, Util.nextInt(50, 200)));
                    break;
                case 1475:
                    itemReward.options.add(new Item.ItemOption(77, Util.nextInt(50, 200)));
                    break;
                case 1476:
                    itemReward.options.add(new Item.ItemOption(103, Util.nextInt(50, 200)));
                    break;
            }
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
        this.actFight = true;
    }
    private long st;

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = injuredLimitDame(plAtt, damage, piercing, DAME_10M);
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
                wakeupAnotherBossWhenDisappear();
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
        boolean usoppDie = false;
        for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
            if (boss.id == BossID.USOPP && boss.isDie()) {
                usoppDie = true;
                break;
            }
        }
        if (usoppDie) {
            for (Boss boss : this.parentBoss.bossAppearTogether[this.parentBoss.currentLevel]) {
                if (boss.id == BossID.BROOK && !boss.isDie()) {
                    boss.actFight = true;
                    boss.chat("Tuần lộc nhỏ, cậu còn ổn không");
                    idx++;
                }
                if (boss.id == BossID.NAMI && !boss.isDie()) {
                    boss.actFight = true;
                    boss.chat("Chopper... cậu không sao chứ");
                    idx++;
                }
                if (boss.id == BossID.NICO_ROBIN && !boss.isDie()) {
                    boss.actFight = true;
                    boss.chat("Ta không thể tha thứ cho ngươi được!");
                    idx++;
                }
                if (idx == 3) {
                    break;
                }
            }
        }

    }
}
