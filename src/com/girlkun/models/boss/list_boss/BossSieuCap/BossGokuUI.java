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
public class BossGokuUI extends Boss {

    public BossGokuUI() throws Exception {
        super(BossID.BOSS_GOKU_UI, BossesData.BOSS_GOKU_UI);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        ItemMap item;
        short itemId;
        itemId = Manager.setGokuUI[Util.nextInt(0, 4)];
        item = new ItemMap(zone, itemId, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Util.ratiItemMapSKHGokuUI(item, itemId);
        item.options.remove(item.options.stream().filter(itemOption -> itemOption.optionTemplate.id == 30).findFirst().get());
        Service.getInstance().dropItemMap(this.zone, item);
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
