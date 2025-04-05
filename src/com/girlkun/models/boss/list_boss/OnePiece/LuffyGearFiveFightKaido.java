package com.girlkun.models.boss.list_boss.OnePiece;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.services.SkillService;
import java.util.Random;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

/**
 * @@@@Code rewritten by ndqitVN (Zalo - 0372475179)
 */
public class LuffyGearFiveFightKaido extends Boss {

    public LuffyGearFiveFightKaido() throws Exception {
        super(BossID.LUFFY_GEAR_FIVE_FIGHT_KAIDO, BossesData.LUFFY_GEAR_FIVE_WITH_KAIDO);
    }

    @Override
    public void reward(Player plKill) {
        ItemMap item;
        int rand = Util.nextInt(1, 100);
        if (rand >= 96) {
            item = new ItemMap(zone, 1436, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            item.options.add(new Item.ItemOption(230, 1));
        } else if (rand >= 70) {
            short itemId;
            itemId = Manager.setSen[Util.nextInt(0, 4)];
            item = new ItemMap(zone, itemId, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            item.options.add(new Item.ItemOption(230, 1));
            Util.ratiItemSen(item, itemId);
            item.options.remove(item.options.stream().filter(itemOption -> itemOption.optionTemplate.id == 30).findFirst().get());
        } else {
            short itemId;
            int skhId;
            int genderItem = Util.nextInt(0, 2);
            itemId = Manager.doThienSuSKH[genderItem][Util.nextInt(0, 4)];
            skhId = ItemService.gI().randomSKHId();
            item = new ItemMap(zone, itemId, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
            item.options.add(new Item.ItemOption(230, 1));
            Util.ratiItemTS(item, itemId, genderItem);
            item.options.add(new Item.ItemOption(skhId, 1));
            item.options.add(new Item.ItemOption(ItemService.gI().optionIdSKH(skhId), 1));
            item.options.remove(item.options.stream().filter(itemOption -> itemOption.optionTemplate.id == 21).findFirst().get());
            item.options.add(new Item.ItemOption(21, 80));
        }
        Service.getInstance().dropItemMap(this.zone, item);
    }

    @Override
    public void active() {
        if (Util.canDoWithTime(st, 900000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        } else {
            if (this.typePk == ConstPlayer.NON_PK && !isWin) {
                this.changeToTypePK();
            }
            moveTo(Util.nextInt(280, 480), 312);
            this.attack();
        }

    }

    private Player findBossAttack() {
        int dis = 600;
        Player bossAtt = null;

        for (Player boss : zone.getBosses()) {
            if (boss.isDie()) {
                continue;
            }
            int d = Util.getDistance(this, boss);
            if (d <= dis) {
                dis = d;
                bossAtt = boss;
            }
        }
        return bossAtt;
    }

    @Override
    public void attack() {
        if (Util.canDoWithTime(this.lastTimeAttack, 100) && this.typePk == ConstPlayer.PK_ALL) {
            this.lastTimeAttack = System.currentTimeMillis();
            try {
                Player pl = findBossAttack();
                if (pl == null || pl.isDie()) {
                    return;
                }
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
                ex.printStackTrace();
//                System.out.println("loi ne    22    ClassCastException ");
            }
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
        this.location.x = 320;
        this.location.y = 312;
        this.isWin = false;
    }
    private long st;

    private boolean canAttackBoss(Player pl) {
        if (pl != null) {
            if (!pl.isPl()) {
                return true;
            } else {
                if (pl.fusion.typeFusion == ConstPlayer.NON_FUSION) {
                    if (pl.setClothes.ctDeKaiDo != -1) {
                        return true;
                    } else {
                        Service.gI().sendThongBao(pl, "Phải mang cải trang thuộc hạ Kaido mới có thể tấn công Luffy");
                    }
                } else {
                    Service.gI().sendThongBao(pl, "Vui lòng tách hợp thể và mang cải trang thuộc hạ Kaido mới có thể tấn công Luffy");
                }
            }
        }
        return false;
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!canAttackBoss(plAtt)) {
                return 0;
            }
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Haki Quan Sát");
                return 0;
            }
            damage = injuredLimitDame(plAtt, damage, piercing, DAME_10M);
            this.nPoint.subHP(damage);
            if (isDie()) {
                Service.gI().chat(this, "Ta sẽ còn quay lại...!");
                this.setDie(plAtt);
                if (plAtt == this) {
                    this.changeStatus(BossStatus.DIE);
                } else {
                    die(plAtt);
                }
                wakeupAnotherBossWhenDisappear();
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void wakeupAnotherBossWhenDisappear() {
        if (this.bossAppearTogether == null) {
            return;
        }
        try {
            for (Boss boss : this.bossAppearTogether[this.currentLevel]) {
                if (boss.id == BossID.KAIDO && !boss.isDie()) {
                    Service.gI().chat(boss, "Ngươi chưa thể đánh bại được ta!");
                    boss.isWin = true;
                    boss.changeToTypeNonPK();
                    boss.changeStatus(BossStatus.LEAVE_MAP);
                }
            }
        } catch (Exception e) {

        }

    }
}
