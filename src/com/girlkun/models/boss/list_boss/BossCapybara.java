package com.girlkun.models.boss.list_boss;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import java.util.Random;
import com.girlkun.utils.Util;

/**
 * @@Rewrite By NDQ
 */
public class BossCapybara extends Boss {

    public BossCapybara() throws Exception {
        super(BossID.BOSS_CAPYBARA, BossesData.BOSS_CAPYPARA);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        if (Util.isTrue(10, 100)) {
            ItemMap capybara = new ItemMap(zone, 1481, 1, this.location.x, this.location.y, plKill.id);
            capybara.options.add(new Item.ItemOption(230, 1));
            capybara.options.add(new Item.ItemOption(247, 1));
            capybara.options.add(new Item.ItemOption(50, Util.nextInt(10000, 20000)));
            capybara.options.add(new Item.ItemOption(30, 1));
            Service.getInstance().dropItemMap(this.zone, capybara);
        } else {
            ItemMap nr1s = new ItemMap(zone, 14, 1, this.location.x, this.location.y, plKill.id);
            Service.getInstance().dropItemMap(this.zone, nr1s);
        }
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
            damage = this.nPoint.subDameInjureWithDeff(damage / 2);
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
