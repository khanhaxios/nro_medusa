package com.girlkun.models.mob;

import com.girlkun.consts.ConstMap;
import com.girlkun.consts.ConstMob;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;

import java.util.List;

import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Location;
import com.girlkun.models.player.Pet.DaoLu.DaoLu;
import com.girlkun.models.player.Pet.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.Thu_TrieuHoi;
import com.girlkun.models.reward.ItemMobReward;
import com.girlkun.models.reward.MobReward;
import com.girlkun.models.skill.PlayerSkill;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.services.*;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;

    public boolean isMobMe;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public static void initMopbKhiGas(Mob mob, byte level) {
        mob.point.dame = (level * 3250 * mob.level * 4) * 5;
        mob.point.maxHp = (level * 12472 * mob.point.hp + level * 7263 * mob.tempId) / 2;
        //code by Việt Nguyễn
    }

    public static void initMopbbdkb(Mob mob, byte level) {
        mob.point.dame = (level * 3250 * mob.level * 4) * 5;
        mob.point.maxHp = (level * 12472 * mob.point.hp + level * 7263 * mob.tempId) / 2;
        //code by Việt Nguyễn
    }

    public void setTiemNang() {
        this.maxTiemNang = Util.DoubleGioihan(this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100);
    }

    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public boolean isSieuQuai() {
        return this.lvMob > 0;
    }

    public boolean isQuaiBay() {
        return this.tempId == 10 || this.tempId == 69 || this.tempId == 30 || this.tempId == 37
                || this.tempId == 21 || this.tempId == 49 || this.tempId == 25 || this.tempId == 33 || this.tempId == 32
                || this.tempId == 28 || this.tempId == 29 || this.tempId == 31 || this.tempId == 12 || this.tempId == 79
                || this.tempId == 75 || this.tempId == 11 || this.tempId == 50 || this.tempId == 43
                || this.tempId == 9 || this.tempId == 8 || this.tempId == 7;
    }

    public boolean isQuaiSen() {
        return this.tempId == 58 || this.tempId == 59 || this.tempId == 60 || this.tempId == 61
                || this.tempId == 62 || this.tempId == 63 || this.tempId == 64 || this.tempId == 65;
    }

    public synchronized void injured(Player plAtt, double damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            if (plAtt != null) {
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                        if (plAtt.nPoint.multicationChuong > 0 && Util.canDoWithTime(plAtt.nPoint.lastTimeMultiChuong, PlayerSkill.TIME_MUTIL_CHUONG)) {
                            damage *= plAtt.nPoint.multicationChuong;
                            plAtt.nPoint.lastTimeMultiChuong = System.currentTimeMillis();
                        }

                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                this.lvMob = 0;
                this.status = 0;
                this.sendMobDieAffterAttacked(plAtt, damage);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                this.lastTimeDie = System.currentTimeMillis();
                if (this.id == 13) {
                    this.zone.isbulon13Alive = false;
                }
                if (this.id == 14) {
                    this.zone.isbulon14Alive = false;
                }
                if (plAtt != null && this.isQuaiBay() == true) {
                    if (plAtt.chienthan.tasknow == 5) {
                        plAtt.chienthan.dalamduoc++;
                    }
                    plAtt.achievement.plusCount(6);
                }
                if (plAtt != null && this.tempId == 0) {
                    plAtt.achievement.plusCount(7);
                }
            } else {
                this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                Service.getInstance().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
        }
    }

    public boolean isMemInMap(Player me) {
        if (me.clan != null) {
            if (me.zone.players.stream().allMatch(pl -> pl != null && pl.zone != null && !pl.isBoss && !pl.isNewPet && pl.zone.zoneId == me.zone.zoneId && pl.clan != null && pl.clan.id == me.clan.id)) {
                return true;
            }
        }
        return false;
    }

    public long getTiemNangForPlayer(Player pl, double dame) {
        int levelPlayer = Service.getInstance().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = 0;
        if (point.getHpFull() >= 100000000) {
            pDameHit = Util.DoubleGioihan(dame) * 500 / Util.DoubleGioihan(point.getHpFull());
        } else {
            pDameHit = Util.DoubleGioihan(dame) * 100 / Util.DoubleGioihan(point.getHpFull());
        }

        long tiemNang = pDameHit * maxTiemNang / 100;
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 10 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 10 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = Util.DoubleGioihan(pl.nPoint.calSucManhTiemNang(tiemNang));
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124 || pl.zone.map.mapId == 141 || pl.zone.map.mapId == 142 || pl.zone.map.mapId == 146) {

            tiemNang *= 2;
        }
        if (pl.zone.items.stream().anyMatch(it -> it != null && (it.playerId == pl.id || isMemInMap(pl)) && it.itemTemplate.id == 343 && Util.getDistance(it.x, it.y, pl.location.x, pl.location.y) <= 200)) {
            tiemNang += tiemNang * 20 / 100;
        }
        return tiemNang;
    }

    public void update() {
        if (this.isDie() && !Maintenance.isRuning) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    if (this.zone.isTrungUyTrangAlive == true) {
                        if (this.tempId == 22 && this.zone.map.mapId == 59) {
                            if (Util.canDoWithTime(lastTimeDie, 5000)) {
                                if (this.id == 13) {
                                    this.zone.isbulon13Alive = true;
                                }
                                if (this.id == 14) {
                                    this.zone.isbulon14Alive = true;
                                }
                                this.hoiSinh();
                                this.sendMobHoiSinh();
                            }
                        }
                    }
                    break;
                case ConstMap.MAP_BAN_DO_KHO_BAU:
                    if (this.tempId == 72 || this.tempId == 71) {//ro bot bao ve
                        if (System.currentTimeMillis() - this.lastTimeDie > 3000) {
                            try {
                                Message t = new Message(102);
                                t.writer().writeByte((this.tempId == 71 ? 7 : 6));
                                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                                t.cleanup();
                            } catch (IOException e) {
                            }
                        }
                    }
                    break;
                case ConstMap.MAP_KHI_GAS:
                    break;
                default:
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.randomSieuQuai();
                        this.hoiSinh();
                        this.sendMobHoiSinh();
                    }
            }
        }
        effectSkill.update();
        attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0)) {

            if ((this.tempId == 72 || this.tempId == 71) && Util.canDoWithTime(lastTimeAttackPlayer, 300)) {
                List<Player> pl = getListPlayerCanAttack();
                if (!pl.isEmpty()) {
                    this.sendMobBossBdkbAttack(pl, Util.DoubleGioihan(this.point.getDameAttack()));
                } else {
                    if (this.tempId == 71) {
                        Player plA = getPlayerCanAttack();
                        if (plA != null) {
                            try {
                                Message t = new Message(102);
                                t.writer().writeByte(5);
                                t.writer().writeByte(plA.location.x);
                                this.location.x = plA.location.x;
                                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                                t.cleanup();
                            } catch (IOException e) {
                            }
                        }

                    }
                }
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            } else if (Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
                Player pl = getPlayerCanAttack();
                if (pl != null) {
                    this.mobAttackPlayer(pl);
                }
                this.lastTimeAttackPlayer = System.currentTimeMillis();
            }

        }
    }

    private void sendMobBossBdkbAttack(List<Player> players, long dame) {
        if (this.tempId == 72) {
            try {
                Message t = new Message(102);
                int action = Util.nextInt(0, 2);
                t.writer().writeByte(action);
                if (action != 1) {
                    this.location.x = players.get(Util.nextInt(0, players.size() - 1)).location.x;
                }
                t.writer().writeByte(players.size());
                for (Byte i = 0; i < players.size(); i++) {
                    t.writer().writeInt((int) players.get(i).id);
                    t.writer().writeInt((int) players.get(i).injured(null, (int) dame, false, true));
                }
                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                t.cleanup();
            } catch (IOException e) {
            }
        } else if (this.tempId == 71) {
            try {
                Message t = new Message(102);
                t.writer().writeByte(Util.getOne(3, 4));
                t.writer().writeByte(players.size());
                for (Byte i = 0; i < players.size(); i++) {
                    t.writer().writeInt((int) players.get(i).id);
                    t.writer().writeInt((int) players.get(i).injured(null, (int) dame, false, true));
                }
                Service.getInstance().sendMessAllPlayerInMap(this.zone, t);
                t.cleanup();
            } catch (IOException e) {
            }
        }
    }

    private List<Player> getListPlayerCanAttack() {
        List<Player> plAttack = new ArrayList<>();
        int distance = (this.tempId == 71 ? 250 : 600);
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack.add(pl);
                    }
                }
            }
        } catch (Exception e) {
        }

        return plAttack;
    }

    private Player getPlayerCanAttack() {
        int distance = 100;
        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {

        }
        return plAttack;
    }

    //**************************************************************************
    private void mobAttackPlayer(Player player) {
        double dameMob = this.point.getDameAttack();
        if (!player.isDie() && !player.isNewPet && !player.isBoss
                && !player.zone.items.stream().anyMatch(it -> it != null
                && (it.playerId == player.id || isMemInMap(player))
                && it.itemTemplate.id == 344
                && Util.getDistance(it.x, it.y, player.location.x, player.location.y) <= 200)) {
            if (player.charms.tdDaTrau > System.currentTimeMillis()) {
                dameMob /= 2;
            }
            if (this.isSieuQuai()) {
                dameMob = player.nPoint.hpMax / 10;
            }
            double dame = player.injured(null, dameMob, false, true);
            this.sendMobAttackMe(player, dame);
            this.sendMobAttackPlayer(player);
        }
    }

    private void sendMobAttackMe(Player player, double dame) {
        if (!player.isPet && !player.isNewPet && !player.isTrieuhoipet && !player.isDaoLu) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeInt(Util.DoubleGioihan(dame)); //dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (IOException e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeInt(Util.DoubleGioihan(player.nPoint.hp));
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void randomSieuQuai() {
        if (this.tempId != 0 && MapService.gI().isMapKhongCoSieuQuai(this.zone.map.mapId) && Util.nextInt(0, 150) < 1) {
            this.lvMob = 1;
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    public void sendMobHoiSinh() {
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob);
            msg.writer().writeInt(Util.DoubleGioihan(this.point.hp));
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    //**************************************************************************
    private void sendMobDieAffterAttacked(Player plKill, double dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(Util.DoubleGioihan(dameHit));
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (IOException e) {
        }
    }

    public void sendMobDieAfterMobMeAttacked(Player plKill, int dameHit) {
        this.status = 0;
        Message msg;
        try {
            if (this.id == 13) {
                this.zone.isbulon13Alive = false;
            }
            if (this.id == 14) {
                this.zone.isbulon14Alive = false;
            }
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(dameHit);
            msg.writer().writeBoolean(false); // crit

            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (IOException e) {
            Logger.logException(Mob.class, e);
        }
//        if (plKill.isPl()) {
//            if (TaskService.gI().IsTaskDoWithMemClan(plKill.playerTask.taskMain.id)) {
//                TaskService.gI().checkDoneTaskKillMob(plKill, this, true);
//            } else {
//                TaskService.gI().checkDoneTaskKillMob(plKill, this, false);
//            }
//
//        }
        this.lastTimeDie = System.currentTimeMillis();
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (player.isPl()) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player, item.itemMapId, true);
                }
            }
        } else if (player.isNewPet || player.isDaoLu || player.isTrieuhoipet) {
            if (player.getMaster().charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(player.getMaster(), item.itemMapId, true);
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
//        nplayer
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            if ((!player.isPet && player.setClothes.setDTS == 5)
                    || (player.isPet && ((Pet) player).setClothes.setDTS == 5)
                    || (player.isDaoLu && ((DaoLu) player).setClothes.setDTS == 5)) {
                if (Util.isTrue(30, 100)) {
                    byte random = 1;
                    if (Util.isTrue(2, 100)) {
                        random = 2;
                    }
                    Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
                    i.quantity = random;
                    InventoryServiceNew.gI().addItemBag(player, i);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng ngọc");
                }
            }

//            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
//                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.location.y);
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            msg.writer().writeByte(itemReward.size()); //sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (IOException e) {
            System.out.println("llllll");
        }
        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) { //quái rơi vật phẩm
        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = Manager.MOB_REWARDS.get(this.tempId);
        if (mobReward == null) {
            return list;
        }
        List<ItemMobReward> items = mobReward.getItemReward();
        List<ItemMobReward> golds = mobReward.getGoldReward();
        final Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(11);

        int tileVang = 0;
        if (!items.isEmpty()) {// chay nfile dât thi mo code nay ra crtl+/
            ItemMobReward item = items.get(Util.nextInt(0, items.size() - 1));
            ItemMap itemMap = item.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (!golds.isEmpty()) {//dât roi vàng
            ItemMobReward gold = golds.get(Util.nextInt(0, golds.size() - 1));
            ItemMap itemMap = gold.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }

        if (MapService.gI().isMapYardat(this.zone.map.mapId) && this.tempId == 0) {
            if (Util.isTrue(40, 100)) {
                list.add(new ItemMap(zone, 590, Util.nextInt(15, 30), this.location.x, this.location.y, player.id));
            }
            if (player.cFlag >= 1 && Util.isTrue(100, 100) && hour % 2 == 0) {    //up bí kíp
                list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));// cai nay sua sau nha
                if (Util.isTrue(50, 100) && this.tempId == 0) {    //up bí kíp
                    list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));
                    if (Util.isTrue(50, 100) && this.tempId == 0) {    //up bí kíp
                        list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));
                        if (Util.isTrue(50, 100) && this.tempId == 0) {    //up bí kíp
                            list.add(new ItemMap(zone, 590, 1, x, player.location.y, player.id));
                        }
                    }
                }
            }
        }

        if (MapService.gI().isMapDauLang(this.zone.map.mapId) && hour % 2 == 0 && Util.isTrue(3, 100)) {
            int bktkQty;
            if (player.gender == 1) {
                bktkQty = Util.nextInt(1, 4);
            } else {
                bktkQty = Util.nextInt(2, 6);
            }
            ItemMap it = new ItemMap(zone, 1215, bktkQty, this.location.x, this.location.y, player.id);
            it.options.add(new Item.ItemOption(30, 1));
            list.add(it);
        }

        if (player.itemTime.isUseMayDo && Util.isTrue(8, 100) && this.tempId > 57 && this.tempId < 66) {
            if (player.chienthan.tasknow == 4) {
                player.chienthan.dalamduoc++;
            }
            list.add(new ItemMap(zone, 380, 1, x, player.location.y, player.id));
            player.achievement.plusCount(5);
        }
//       vat phẩm rơi khi use máy dò
//        if (player.itemTime.isUseMayDo2) {
//            list.add(new ItemMap(zone, 570, 1, x, player.location.y, player.id));// cai nay sua sau nha
//        }
        if (Util.isTrue(1, 100)) {
            byte nroquai = (byte) new Random().nextInt(Manager.itemIds_NR.length);
            list.add(new ItemMap(zone, Manager.itemIds_NR[nroquai], 1, this.location.x, this.location.y, player.id));
        }
        tileVang = player.nPoint.tlGold / 100;
        if (Util.isTrue(5, 100)) {
            int vang = (Util.nextInt(30000, 50000) + Util.nextInt(30000, 50000) * tileVang);
            list.add(new ItemMap(zone, 190, vang, this.location.x, this.location.y, player.id));
        }
//        if (Util.isTrue(3, 100) && (this.zone.map.mapId == 1 || this.zone.map.mapId == 2
//                || this.zone.map.mapId == 15 || this.zone.map.mapId == 16
//                || this.zone.map.mapId == 8 || this.zone.map.mapId == 9)) {
//            switch (player.gender) {
//                case 0:
//                    list.add(Util.kogd(zone, 2000, 1, this.location.x, this.location.y, player.id));
//                    break;
//                case 1:
//                    list.add(Util.kogd(zone, 2001, 1, this.location.x, this.location.y, player.id));
//                    break;
//                default:
//                    list.add(Util.kogd(zone, 2002, 1, this.location.x, this.location.y, player.id));
//                    break;
//            }
//        }
        if (Util.isTrue(1, 1000) && this.zone.map.mapId >= 141 && this.zone.map.mapId <= 142) {
            byte radaThuong = (byte) new Random().nextInt(Manager.manhradaThuong.length);
            list.add(new ItemMap(zone, Manager.manhradaThuong[radaThuong], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(4, 10000) && this.zone.map.mapId == 171) {
            byte radaVIP = (byte) new Random().nextInt(Manager.manhradaVIP.length);
            list.add(new ItemMap(zone, Manager.manhradaVIP[radaVIP], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(1, 50) && this.zone.map.mapId == 171) {
            byte randomVp5 = (byte) new Random().nextInt(Manager.dachienthan.length);
            list.add(new ItemMap(zone, Manager.dachienthan[randomVp5], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(3, 100) && this.zone.map.mapId > 159 && this.zone.map.mapId < 164) {
            list.add(new ItemMap(zone, 2031, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(5, 100)) {
            byte randomVp4 = (byte) new Random().nextInt(Manager.danangcap.length);
            list.add(Util.ratiDa(zone, Manager.danangcap[randomVp4], 1, this.location.x, this.location.y, player.id));
        }
        if (player.isPl()) {
            if (Util.isTrue(2f, 100) && player.inventory.itemsBody.get(5).isNotNullItem()
                    && player.inventory.haveOption(player.inventory.itemsBody, 5, 110)) {
                byte randomVp2 = (byte) new Random().nextInt(Manager.spl.length);
                list.add(Util.ratiSpl(zone, Manager.spl[randomVp2], 1, this.location.x, this.location.y, player.id));
            }
        }
        if (Util.isTrue(5, 100) && player.setClothes.setDTL == 5 && this.tempId > 57 && this.tempId < 66 && player.isPl()) {
            byte randomVp3 = (byte) new Random().nextInt(Manager.thucan.length);
            list.add(new ItemMap(zone, Manager.thucan[randomVp3], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(40, 100) && this.zone.map.mapId > 155 && this.zone.map.mapId < 159) {
            list.add(new ItemMap(zone, 933, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(5, 100) && this.zone.map.mapId > 158 && this.zone.map.mapId < 160) {
            list.add(new ItemMap(zone, 934, 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(2, 100) && this.tempId > 64 && this.tempId < 66) {
            byte randomVp = (byte) new Random().nextInt(Manager.itemSkien.length);
            list.add(new ItemMap(zone, Manager.itemSkien[randomVp], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(5, 100) && this.zone.map.mapId == 155 && player.setClothes.setDHD == 5) {
            byte randomVp1 = (byte) new Random().nextInt(Manager.itemManh.length);
            list.add(new ItemMap(zone, Manager.itemManh[randomVp1], 1, this.location.x, this.location.y, player.id));
        }
        if (Util.isTrue(0.5f, 100) && this.tempId > 57 && this.tempId < 59) {
            list.add(new ItemMap(zone, 720, 1, this.location.x, this.location.y, player.id));
        }

        if (MapService.gI().isMapCold(this.zone.map)) {
            if (Util.isTrue(1, 250)) {// tile roi do than linh là 1/20000
                if (player.chienthan.tasknow == 2) {
                    player.chienthan.dalamduoc++;
                }
                byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length);
                list.add(Util.ratiItem1(zone, Manager.itemIds_TL[randomDo], 1, this.location.x, this.location.y, player.id));
            }
            if (Util.isTrue(1, 150)) {
                byte randomKey = (byte) new Random().nextInt(Manager.MEDUSA_KEYWORD.length);
                list.add(new ItemMap(zone, Manager.MEDUSA_KEYWORD[randomKey], 1, player.location.x, player.location.y, player.id));
                Service.gI().sendThongBao(player, "Bạn vừa nhận được 1 từ khóa đặc biệt từ Medusa");
            }
        }

        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            int levell = player.getMaster().clan.banDoKhoBau.level;
            int slhn = Util.nextInt(1, 3) * (levell / 10);
            if (Util.nextInt(0, 100) < 70) {
                list.add(new ItemMap(zone, 861, slhn, x, player.location.y, player.id));
                Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + slhn + " hồng ngọc");
            }
        }
        if (Util.isTrue(30, 100) && this.tempId > 76 && this.tempId < 78) {
            byte random = 1;
            if (Util.isTrue(2, 100)) {
                random = 2;
            }
            Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
            i.quantity = random;
            InventoryServiceNew.gI().addItemBag(player, i);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng ngọc");
        }
        if (Manager.SUKIEN == 1 && Util.isTrue(5, 100)) {
//        byte randomTrungthu = (byte) new Random().nextInt(Manager.SuKien_TrungThu.length);
            if (this.isQuaiBay()) {
                list.add(new ItemMap(zone, Manager.SuKien_TrungThu[2], 1, this.location.x, this.location.y, player.id));
            }
            if (!this.isQuaiBay() && !this.isQuaiSen()) {
                list.add(new ItemMap(zone, Manager.SuKien_TrungThu[1], 1, this.location.x, this.location.y, player.id));
            }
            if (this.isQuaiSen()) {
                list.add(new ItemMap(zone, Manager.SuKien_TrungThu[0], 1, this.location.x, this.location.y, player.id));
            }
        }
//        if (!player.isPet && player.getSession().actived && Util.isTrue(15, 100)) {
//            list.add(new ItemMap(zone, 610, 1, x, player.location.y, player.id));
//        }

        //Roi Do Map VIP 1
        if (this.zone.map.mapId == 177) {
            if (Util.isTrue(1, 500)) {
                int[] itemDos = new int[]{1474, 1475, 1476};
                int randomDo = new Random().nextInt(itemDos.length);
                ItemMap item = new ItemMap(zone, itemDos[randomDo], 1, this.location.x, this.location.y, player.id);
                switch (itemDos[randomDo]) {
                    case 1474:
                        item.options.add(new Item.ItemOption(50, 100));
                        break;
                    case 1475:
                        item.options.add(new Item.ItemOption(77, 100));
                        break;
                    case 1476:
                        item.options.add(new Item.ItemOption(103, 100));
                        break;
                }
                item.options.add(new Item.ItemOption(30, 1));
                list.add(item);
            }
            if (Util.isTrue(1, 1000)) {
                ItemMap itCaiTrang = new ItemMap(zone, 951, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), player.id);
                itCaiTrang.options.add(new Item.ItemOption(230, 1));
                itCaiTrang.options.add(new Item.ItemOption(50, Util.nextInt(200, 345)));
                itCaiTrang.options.add(new Item.ItemOption(77, Util.nextInt(200, 345)));
                itCaiTrang.options.add(new Item.ItemOption(103, Util.nextInt(200, 345)));
                itCaiTrang.options.add(new Item.ItemOption(93, Util.nextInt(1, 3)));
                itCaiTrang.options.add(new Item.ItemOption(30, 1));
                list.add(itCaiTrang);
            }
        }

        //Dao Lu rơi đan
        if (player.isDaoLu) {
            ItemMap itemDan = null;
            int randNhanPham = Util.nextInt(1, 100);
            int randVuong = 1;
            int randLinh = 1;
            int randDDS = 1;
            int randDS = 3;
            int randDGia = 5;
            int randDKhi = 10;
            if (randNhanPham > 100 - randVuong) {
                itemDan = new ItemMap(zone, 1605, 1, this.location.x, this.location.y, player.id);
            } else if (randNhanPham > 100 - randVuong - randLinh) {
                itemDan = new ItemMap(zone, 1604, 1, this.location.x, this.location.y, player.id);
            } else if (randNhanPham > 100 - randVuong - randLinh - randDDS) {
                itemDan = new ItemMap(zone, 1603, 1, this.location.x, this.location.y, player.id);
            } else if (randNhanPham > 100 - randVuong - randLinh - randDDS - randDS) {
                itemDan = new ItemMap(zone, 1602, 1, this.location.x, this.location.y, player.id);
            } else if (randNhanPham > 100 - randVuong - randLinh - randDDS - randDS - randDGia) {
                itemDan = new ItemMap(zone, 1601, 1, this.location.x, this.location.y, player.id);
            } else if (randNhanPham > 100 - randVuong - randLinh - randDDS - randDS - randDGia - randDKhi) {
                itemDan = new ItemMap(zone, 1600, 1, this.location.x, this.location.y, player.id);
            }
            if (itemDan != null) {
                itemDan.options.add(new Item.ItemOption(230, 1));
                itemDan.options.add(new Item.ItemOption(30, 1));
                list.add(itemDan);
            }
        }

        //SK 2T9
//        if (Util.isTrue(5, 100)) { //Util.isTrue(5, 100)
//            ItemMap nhanhTre = new ItemMap(zone, 1539, 1, this.location.x, this.location.y, player.id);
//            list.add(nhanhTre);
//        }

        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }
        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(double dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt(Util.DoubleGioihan(this.point.gethp()));
            msg.writer().writeInt(Util.DoubleGioihan(dameHit));
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.getInstance().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }
}

/**
 * Code được viết bởi Hoàng Việt Vui lòng không sao chép mã nguồn này dưới mọi
 * hình thức.
 */
