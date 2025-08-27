/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss.HuyDiet;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

public class ThanHuyDiet extends Boss {

    private long lasttimehakai;
    private int timehakai;

    public ThanHuyDiet() throws Exception {
        super(Util.randomBossId(), BossesData.THAN_HUY_DIET);
    }

    @Override
    public void reward(Player plKill) {
        plKill.achievement.plusCount(3);
        plKill.inventory.event++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        ItemMap itemMap = null;
        if (Util.isTrue(5, 100)) {
            if (Util.isTrue(50, 20)) {
                itemMap = Util.ratiItem(zone, 561, 1, this.location.x, this.location.y, plKill.id);
                itemMap.options.add(new Item.ItemOption(30, 1));
                Service.getInstance().dropItemMap(this.zone, itemMap);
            } else {
                Util.ratioTrangBi(zone, 1, this.location.x, this.location.y, plKill.id, 100, 0);
            }
        } else {
            Service.getInstance().dropItemMap(this.zone, new ItemMap(zone, Manager.itemIds_NR_SB[randomNR], 1, this.location.x, this.location.y, plKill.id));
        }
    }

    @Override
    public void active() {

        if (this.typePk == ConstPlayer.NON_PK) {
            this.changeToTypePK();
        }
        this.huydiet();
        this.attack();
    }

    //    }
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    private void huydiet() {
        if (!Util.canDoWithTime(this.lasttimehakai, this.timehakai) || !Util.isTrue(1, 100)) {
            return;
        }

        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null) {
            return;
        }
        if (pl.isDie()) {
            return;
        }

        this.nPoint.dameg += (pl.nPoint.dame * 5 / 100);
        this.nPoint.hpg += (pl.nPoint.hp * 2 / 100);
        this.nPoint.critg++;
        this.nPoint.calPoint();
//        PlayerService.gI().hoiPhuc(this, pl.nPoint.hp, pl.nPoint.mp);
        pl.injured(null, Util.DoubleGioihan(pl.nPoint.hpMax), true, false, false);
        Service.getInstance().sendThongBao(pl, "Bạn vừa bị " + this.name + " cho bay màu");
        this.chat(2, "Hắn ta mạnh quá,coi chừng " + pl.name + ",tên " + this.name + " hắn không giống như những kẻ thù trước đây");
        this.chat("Thật là yếu ớt " + pl.name);
        this.lasttimehakai = System.currentTimeMillis();
        this.timehakai = Util.nextInt(20000, 30000);
    }

}
