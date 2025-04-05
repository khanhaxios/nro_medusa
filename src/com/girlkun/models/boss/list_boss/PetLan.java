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

    public PetLan(int bossID, BossData bossData, Zone zone, int x, int y) throws Exception {
        super(bossID, bossData);
        this.zone = zone;
        this.location.x = x;
        this.location.y = y;
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
        if (playerTarger.haveBeQuynh == true && playerTarger.batco == false) {
            int co = Util.nextInt(1, 7);
            Service.getInstance().changeFlag(playerTarger, co);
            Service.getInstance().changeFlag(this, co);
            playerTarger.batco = true;
        }
//        if (this.typePk == ConstPlayer.NON_PK) {
//            this.typePk = ConstPlayer.NON_PK;
//        }
        if (this.playerTarger != null && Client.gI().getPlayer(this.playerTarger.id) == null) {
            playerTarger.haveBeQuynh = false;
            playerTarger.batco = true;
            this.leaveMap();
        }
        if (Util.getDistance(playerTarger, this) > 500 && this.zone == this.playerTarger.zone) {
            Service.gI().sendThongBao(this.playerTarger, "|7|Đi quá xa , Lân đã lạc mất bạn!");
            Service.getInstance().changeFlag(playerTarger, 0);
            playerTarger.haveBeQuynh = false;
            playerTarger.batco = false;
            this.leaveMap();
        }
        if (Util.getDistance(playerTarger, this) > 300 && this.zone == this.playerTarger.zone) {
            Service.gI().sendThongBao(this.playerTarger, "|7|Khoảng cách quá xa, Lân SẮP lạc mất bạn!! ");
        }
        if (this.playerTarger != null && Util.getDistance(playerTarger, this) <= 300) {
            int dir = this.location.x - this.playerTarger.location.x <= 0 ? -1 : 1;
            if (Util.canDoWithTime(lasttimemove, 1000)) {
                lasttimemove = System.currentTimeMillis();
                this.moveTo(this.playerTarger.location.x + Util.nextInt(dir == -1 ? 0 : -30, dir == -1 ? 10 : 0), this.playerTarger.location.y);
            }
        }
        if (this.playerTarger != null && playerTarger.haveBeQuynh && this.zone.map.mapId == this.mapHoTong) { // xử lý khi đến map muốn đến
            playerTarger.haveBeQuynh = false;
            playerTarger.batco = false;
            Item longDen = ItemService.gI().createNewItem((short) 1547);
            longDen.quantity = 1;
            longDen.itemOptions.add(new Item.ItemOption(230, 1));
            longDen.itemOptions.add(new Item.ItemOption(30, 1));
            InventoryServiceNew.gI().addItemBag(playerTarger, longDen);
            InventoryServiceNew.gI().sendItemBags(playerTarger);
            Service.getInstance().changeFlag(playerTarger, 0);
            Service.getInstance().sendThongBao(playerTarger, "|1|Bạn nhận được lồng đèn!");
            this.leaveMap();
        }
        if (this.playerTarger != null && this.zone != null && this.zone.map.mapId != this.playerTarger.zone.map.mapId) {
            ChangeMapService.gI().changeMap(this, this.playerTarger.zone, this.playerTarger.location.x, this.playerTarger.location.y);
        }
        if (Util.canDoWithTime(this.lastTimeAttack, 4000)) {
            Service.gI().chat(this, playerTarger.name + "\n|3|Hộ tống ta đến " + MapService.gI().getMapById(this.mapHoTong).mapName);
            this.lastTimeAttack = System.currentTimeMillis();
        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (plAtt != this.playerTarger) {
                damage = this.nPoint.hpMax / 120;
            } else {
                damage = 0;
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
        if (zoneFinal != null) {
            joinMapByZone(zoneFinal);
            this.notifyJoinMap();
            return;
        }
        if (this.zone == null) {
            if (this.parentBoss != null) {
                this.zone = parentBoss.zone;
            } else if (this.lastZone == null) {
                this.zone = getMapJoin();
            } else {
                this.zone = this.lastZone;
            }
        }
        if (this.zone != null) {
            if (this.currentLevel == 0) {
                if (this.parentBoss == null) {
                    ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
                } else {
                    ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);;
                }
//                this.wakeupAnotherBossWhenAppear();
            } else {
                ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            }
            Service.getInstance().sendFlagBag(this);
            this.notifyJoinMap();
        }
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        this.dispose();
    }
}
