/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss.gas;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0
 */
public class HaChiJack extends Boss {
    private final int levell;
    private static final int[][] FULL_DEMON = new int[][]{{Skill.DEMON, 1}, {Skill.DEMON, 2}, {Skill.DEMON, 3}, {Skill.DEMON, 4}, {Skill.DEMON, 5}, {Skill.DEMON, 6}, {Skill.DEMON, 7}};
    private long lastTimeHapThu;
    private int timeHapThu;
    private final long lastUpdate = System.currentTimeMillis();
    private long timeJoinMap;
    private final int initSuper = 0;
    protected Player playerAtt;
    private final int timeLive = 200000000;

    public HaChiJack(Zone zone, int level, int dame, int hp, Player pl) throws Exception {
        super(BossID.HACHIYACK, new BossData(
                "HaChiYack", //name
                ConstPlayer.TRAI_DAT, //gender
                new short[]{639, 640, 641, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((10000 * level)), //dame
                new double[]{((10000000 * level))}, //hp
                new int[]{148}, //map join
                new int[][]{
                        {Skill.LIEN_HOAN, 7, 300},
                        {Skill.THOI_MIEN, 7, 30000},
                        {Skill.BIEN_KHI, 7, 150000},
                        {Skill.TROI, 7, 30000},
                        {Skill.KAMEJOKO, 7, 1000},},
                new String[]{}, //text chat 1
                new String[]{"|-1|Nhóc con"}, //text chat 2
                new String[]{}, //text chat 3
                60,
                (byte) 6, (byte) 10
        ));
        this.nPoint.setBasePoint();
        this.nPoint.setFullHpMp();
        this.zone = zone;
        this.levell = level;
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            if (levell == 110) {
                ItemMap it = new ItemMap(this.zone, 1201, 12, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), plKill.id);
                Service.getInstance().dropItemMap(this.zone, it);
            } else if (levell > 99 && levell < 110) {
                ItemMap it = new ItemMap(this.zone, 1201, 9, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), plKill.id);
                Service.getInstance().dropItemMap(this.zone, it);
            } else if (levell < 100) {
                ItemMap it = new ItemMap(this.zone, 1201, 3, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), plKill.id);
                Service.getInstance().dropItemMap(this.zone, it);
            }
        }
    }

    @Override
    public void active() {
        super.active();
    }

    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean a) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = (int) this.nPoint.subDameInjureWithDeff(damage / 2);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
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
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}







