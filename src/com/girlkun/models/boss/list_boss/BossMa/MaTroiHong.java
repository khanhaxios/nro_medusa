/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss.BossMa;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.services.SkillService;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;
import java.util.Random;

/**
 * @@Write By ndq
 */
public class MaTroiHong extends Boss {

    public MaTroiHong() throws Exception {
        super(BossID.MA_TROI_PINK, BossesData.MA_TROI_PINK);
    }

    @Override
    public void reward(Player plKill) {
        plKill.inventory.event++;
        plKill.inventory.eventSanMa++;
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm sự kiện săn Ma");
        Service.getInstance().sendThongBao(plKill, "Bạn đã nhận được 1 điểm săn Boss");

        int rand = Util.nextInt(1, 100);
        ItemMap itemReward;
        if (rand > 95) {
            int[] itemDos = new int[]{1476, 1474, 1477, 1475};
            int randomDo = new Random().nextInt(itemDos.length);
            itemReward = new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            switch (itemDos[randomDo]) {
                case 1474:
                    itemReward.options.add(new Item.ItemOption(103, 1000));
                    break;
                case 1475:
                    itemReward.options.add(new Item.ItemOption(77, 1000));
                    break;
                case 1476:
                    itemReward.options.add(new Item.ItemOption(50, 500));
                    break;
                case 1477:
                    itemReward.options.add(new Item.ItemOption(5, 500));
                    break;
            }
//            itemReward.options.add(new Item.ItemOption(30, 1));
        } else {
            int[] itemDos = new int[]{14, 15, 16, 17, 18, 19, 20};
            int randomDo = new Random().nextInt(itemDos.length);
            itemReward = new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            itemReward.options.add(new Item.ItemOption(30, 1));
        }
        Service.getInstance().dropItemMap(this.zone, itemReward);
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
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = getPlayerAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
                this.nPoint.dame = pl.nPoint.hpMax * 0.001;
                this.playerSkill.skillSelect = this.playerSkill.skills.get(Util.nextInt(0, this.playerSkill.skills.size() - 1));
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(5, 20)) {
                        if (SkillUtil.isUseSkillChuong(this)) {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 200)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 70));
                        } else {
                            this.moveTo(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(10, 40)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50));
                        }
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    if (Util.isTrue(1, 2)) {
                        this.moveToPlayer(pl);
                    }
                }
            } catch (Exception ex) {
//                System.out.println("loi ne    22    ClassCastException ");
            }
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = 20;
            if (plAtt != null) {
                if (plAtt.setClothes.isThuongLinhDietMa) {
                    damage = Util.nextInt(100, 400);
                } else if (plAtt.setClothes.isDaoYeuLinhPhucMa) {
                    damage = Util.nextInt(50, 200);
                } else {
                    damage = 20;
                }
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
