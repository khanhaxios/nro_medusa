package com.girlkun.models.boss.list_boss.phoban;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.bdkb.BanDoKhoBauService;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0
 */
public class TrungUyXanhLoBdkb extends Boss {

    private long lastUpdate = System.currentTimeMillis();
    private int levell;
    private int initSuper = 0;
    protected Player playerAtt;
    private int timeLive = 200000000;

    public TrungUyXanhLoBdkb(Zone zone, int level, double dame, double hp, int id) throws Exception {
        super(id, new BossData(
                "Trung Úy Xanh Lơ", //name
                ConstPlayer.TRAI_DAT, //gender
                new short[]{135, 136, 137, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((long) ((10000L + dame) * level)), //dame
                new double[]{((double) ((100_000_000_000D + hp) * level))}, //hp
                new int[]{148}, //map join
                new int[][]{
                        {Skill.GALICK, 5, 5},
                        {Skill.KAMEJOKO, 7, 12},
                        {Skill.THAI_DUONG_HA_SAN, 7, 20000},
                        {Skill.KHIEN_NANG_LUONG, 6, 23},
                        {Skill.TAI_TAO_NANG_LUONG, 6, 23},
                        {Skill.DE_TRUNG, 6, 23},
                },
                new String[]{"|-1|Kho báu ở đây là của ta"}, //text chat 1
                new String[]{"|-1|Nhóc con"}, //text chat 2
                new String[]{"|-1|Ta sẽ tiêu diệt tất cả bang hội ngươi"}, //text chat 3
                60
        ));

        this.zone = zone;
        this.levell = level;
    }

    @Override
    public void reward(Player plKill) {
        if (levell == 110) {
            int a = 0;
            for (int i = 0; i < 40; i++) {
                ItemMap it = new ItemMap(this.zone, 861, 100, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
            }
        } else if (levell > 99 && levell < 110) {
            int a = 0;
            for (int i = 0; i < 40; i++) {
                ItemMap it = new ItemMap(this.zone, 861, 150, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
            }
        } else if (levell > 100) {
            int a = 0;
            for (int i = 0; i < 40; i++) {
                ItemMap it = new ItemMap(this.zone, 861, 250, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
            }
        }
        // chac chan roi ruong
        if (levell <= 50) {
            // roi ruong bac
            ItemMap it = new ItemMap(this.zone, 573, Util.nextInt(1, 5), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        if (levell > 50 && levell <= 100) {
            ItemMap it = new ItemMap(this.zone, 574, Util.nextInt(1, 5), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        if (levell > 100 && levell <= 250) {
            ItemMap it = new ItemMap(this.zone, 571, Util.nextInt(1, 5), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        if (levell > 250 && levell <= 500) {
            ItemMap it = new ItemMap(this.zone, 572, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        BanDoKhoBauService.gI().timeoutmap = 20;
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack, boolean a) {
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

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
