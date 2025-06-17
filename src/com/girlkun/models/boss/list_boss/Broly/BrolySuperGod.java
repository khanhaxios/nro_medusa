package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class BrolySuperGod extends Boss {

    private long lastTimeHoiPhuc;

    public BrolySuperGod() throws Exception {
        super(BossID.BROLY_2, BossesData.BROLY_4);
    }

    public BrolySuperGod(Zone zone) throws Exception {
        super(BossID.BROLY_2, BossesData.BROLY_4);
        this.zone = zone;
    }

    @Override
    public void update() {
        hoiPhuc();
        super.update();
    }

    public void hoiPhuc() {
        if (!Util.canDoWithTime(lastTimeHoiPhuc, 30000)) {
            return;
        }
        if (this.nPoint.hp < this.nPoint.hpMax * 50 / 100) {
            this.nPoint.hpMax *= 2;
            this.nPoint.hp = this.nPoint.hpMax / 2;
            if (playerTarger != null) {
                Service.gI().sendThongBao(playerTarger, "Hắn lại mạnh lên rồi");
            }
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean isSTChuan)  {
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
            }
            return damage;
        } else {
            return 0;
        }
    }
}
