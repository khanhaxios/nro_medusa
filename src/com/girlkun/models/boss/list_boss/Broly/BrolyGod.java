package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.utils.Util;

public class BrolyGod extends Boss {
    public BrolyGod() throws Exception {
        super(BossID.BROLY_1, BossesData.BROLY_3);
    }

    public BrolyGod(Zone zone) throws Exception {
        super(BossID.BROLY_1, BossesData.BROLY_3);
        this.zone = zone;
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean isSTChuan) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = (int) this.nPoint.subDameInjureWithDeff(damage / 5);
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
                // call super broly 3
                try {
                    new BrolySuperGod(this.zone);
                } catch (Exception e) {

                }
            }
            return damage;
        } else {
            return 0;
        }
    }
}
