/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.models.boss.list_boss;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @@Stole By NDQ
 */
public class ThoTrang extends Boss {

    public ThoTrang() throws Exception {
        super(BossID.BOSS_THOTRANG, BossesData.BOSS_THOTRANG);
    }

//    private long lateTimeCheckItem;
//    private long lateTimeCheckItem2;
    @Override
    public void reward(Player plKill) {
        ItemMap it = new ItemMap(this.zone, 1546, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                this.location.y - 24), plKill.id);
//        it.options.add(new Item.ItemOption(73, 1));
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
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }
    private long st;

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            byte damageLimit = DAME_5;
            if (plAtt != null) {
                if (plAtt.setClothes.isThuongLinhDietMa) {
                    damageLimit += 5;
                } else if (plAtt.setClothes.isDaoYeuLinhPhucMa) {
                    damageLimit += 3;
                }
                damageLimit += plAtt.setClothes.pkkhMedusa * 2;
                Player plMaster;
                plMaster = plAtt.getMaster();
                if (Util.isTrue(damageLimit, 100)) {
                    if (InventoryServiceNew.gI().getCountEmptyBag(plMaster) > 1) {
                        Item it = ItemService.gI().createNewItem((short) 1545);
                        it.itemOptions.add(new Item.ItemOption(230, 1));
                        it.itemOptions.add(new Item.ItemOption(30, 1));
                        it.quantity = Util.nextInt(1, 3);
                        InventoryServiceNew.gI().addItemBag(plMaster, it);
                        InventoryServiceNew.gI().sendItemBags(plMaster);
                        Service.gI().sendThongBao(plMaster, "Bạn vừa nhận được " + it.template.name);
                    } else {
                        ItemMap it = new ItemMap(this.zone, 1545, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                                this.location.y - 24), plMaster.id);
                        it.options.add(new Item.ItemOption(230, 1));
                        it.options.add(new Item.ItemOption(30, 1));
                        it.quantity = Util.nextInt(1, 3);
                        Service.getInstance().dropItemMap(this.zone, it);
                    }
                }
                if (Util.nextInt(1, 1000) > 999) {
                    if (InventoryServiceNew.gI().getCountEmptyBag(plMaster) > 1) {
                        Item it = ItemService.gI().createNewItem((short) 1546);
                        it.itemOptions.add(new Item.ItemOption(230, 1));
                        it.itemOptions.add(new Item.ItemOption(30, 1));
                        InventoryServiceNew.gI().addItemBag(plMaster, it);
                        InventoryServiceNew.gI().sendItemBags(plMaster);
                        Service.gI().sendThongBao(plMaster, "Bạn vừa nhận được " + it.template.name);
                    } else {
                        ItemMap it = new ItemMap(this.zone, 1546, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                                this.location.y - 24), plMaster.id);
                        it.options.add(new Item.ItemOption(230, 1));
                        it.options.add(new Item.ItemOption(30, 1));
                        Service.getInstance().dropItemMap(this.zone, it);
                    }
                }
            }
            this.nPoint.subHP(damageLimit);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
                wakeupAnotherBossWhenDisappear();
            }
            return damageLimit;

        } else {
            return 0;
        }
    }
}
