package com.girlkun.models.boss.list_boss;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;
import com.girlkun.models.player.Inventory;

/**
 * @author Administrator
 */
public class PetLan extends Boss {

    public PetLan() throws Exception {
        super(BossID.BOSS_LAN, BossesData.BOSS_THOTRANG);
    }

//    @Override
//    public void reward(Player plKill) {
//        ItemMap it = new ItemMap(this.zone, Util.nextInt(1099, 1103), Util.nextInt(3, 4), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
//                this.location.y - 24), plKill.id);
//        Service.getInstance().dropItemMap(this.zone, it);
//    }
    long lasttimemove;

    @Override
    public void reward(Player plKill) {
        int a = 0;
        for (int i = 0; i < 5; i++) {
            ItemMap it = new ItemMap(this.zone, 1546, 1, this.location.x + a, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it);
            a += 10;
            playerTarger.batco = false;
        }
        for (int i = 0; i < 5; i++) {
            ItemMap it = new ItemMap(this.zone, 1546, 1, this.location.x + a, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), playerTarger.id);
            Service.getInstance().dropItemMap(this.zone, it);
            a += 10;
            playerTarger.batco = false;
        }
    }

    @Override
    public void update() {
        try {
            super.update();
        } catch (Exception e) {
        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1800000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }
    private long st;
    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (plAtt != null) {
                if (plAtt.setClothes.setDTS == 5) {
                damage = 5;
                }
    
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            playerTarger.haveBeQuynh = false;
            return 0;
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
