/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss.BLACK;

import com.girlkun.models.boss.*;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;


public class BlackGokuBase extends Boss {

    public BlackGokuBase() throws Exception {
        super(BossID.SUPER_BLACK_GOKU, BossesData.SUPER_BLACK_GOKU);
    }

    @Override
    public void reward(Player plKill) {
        plKill.achievement.plusCount(3);
        plKill.inventory.event++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        if (Util.isTrue(BossManager.ratioReward, 100)) {
            if (Util.isTrue(1, 20)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 865, 1, this.location.x, this.location.y, plKill.id));
            } else if (Util.isTrue(10, 20)) {
                Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 14, 1, this.location.x, this.location.y, plKill.id));
            } else {
                Service.getInstance().dropItemMap(this.zone, Util.ratiItem(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, plKill.id));
            }
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, 14, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
        }
        ItemMap it1 = new ItemMap(this.zone, 2030, 2, this.location.x - 10, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, it1);
        Util.ratioRoiBuaZeno(zone, 1, this.location.x, this.location.y, plKill.id);
        // ratio
        Util.ratioDropCaiTrang(zone, 1, this.location.x, this.location.y, plKill.id, 2f, 100, 150);

        Util.ratioDropHaoQuang(zone, 1, this.location.x, this.location.y, plKill.id, .5f, 100, 150);

        Util.ratioManhPhapBao(zone, 1, this.location.x, this.location.y, plKill.id, 100);

        Util.ratioVeNangCap(zone, 1, this.location.x, this.location.y, plKill.id, 100);
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 9000000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

//    @Override
//    public void moveTo(int x, int y) {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.moveTo(x, y);
//    }
//
//    @Override
//    public void reward(Player plKill) {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.reward(plKill);
//    }
//
//    @Override
//    protected void notifyJoinMap() {
//        if(this.currentLevel == 1){
//            return;
//        }
//        super.notifyJoinMap();
//    }
}






















