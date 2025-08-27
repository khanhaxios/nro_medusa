/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss.doanh_trai;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.*;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0
 */
public class TrungUyXanhLo extends Boss {

    protected Player playerAtt;

    public TrungUyXanhLo(Zone zone, double dame, double hp) throws Exception {
        super(BossID.TRUNG_UY_XANH_LO, new BossData(
                "Trung Uý Xanh Lơ", //name
                ConstPlayer.TRAI_DAT, //gender
                new short[]{135, 136, 137, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((hp / 8)), //dame
                new double[]{((dame * 250))}, //hp
                new int[]{62}, //map join
                new int[][]{
                        {Skill.LIEN_HOAN, 7, 500},
                        {Skill.BIEN_KHI, 7, 350000},
                        {Skill.THAI_DUONG_HA_SAN, 6, 30000},
                        {Skill.KAMEJOKO, 7, 1000},},
                new String[]{}, //text chat 1
                new String[]{"|-1|Nhóc con"}, //text chat 2
                new String[]{}, //text chat 3
                1,//respawn
                (byte) 2,
                (byte) 10
        ));

        this.zone = zone;
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(80, 100)) {
            ItemMap it = new ItemMap(this.zone, 16, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it);
        } else {
            ItemMap it = new ItemMap(this.zone, 14, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        ItemMap it = new ItemMap(this.zone, 2083, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, it);
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1800000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        if (Util.canDoWithTime(st, 1800000) || this.isDie()) {
            this.changeStatus(BossStatus.LEAVE_MAP);
            BossManager.gI().removeBoss(this);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean a) {
        damage /= 2;
        return super.injured(plAtt, damage, piercing, isMobAttack, true);
    }
}
