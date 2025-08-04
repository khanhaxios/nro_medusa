package com.girlkun.models.map.bdkb;

import com.girlkun.models.clan.Clan;
import com.girlkun.models.map.Zone;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BTH     public static final int MAX_AVAILABLE = 50;
 */
public class BanDoKhoBau {
    private static BanDoKhoBau i;

    public static BanDoKhoBau gI() {
        if (i == null) {
        }
        return i;
    }

    public static final long POWER_CAN_GO_TO_BDKB = 2000000000;

    public static final List<BanDoKhoBau> BAN_DO_KHO_BAU;
    public static final int MAX_AVAILABLE = 50;
    public static final int N_PLAYER_MAP = 2;
    public static final int TIME_KHI_BAN_DO_KHO_BAU = 1800000;
    private Player player;

    private long lastTimeBuffHP = System.currentTimeMillis();
    private static final long TIME_BUFF_HP = 7 * 60 * 1000;

    static {
        BAN_DO_KHO_BAU = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            BAN_DO_KHO_BAU.add(new BanDoKhoBau(i));
        }
    }

    public int id;
    public int level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;

    public BanDoKhoBau(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }

    public void update() {
        if (this.isOpened) {
            long now = System.currentTimeMillis();

            // Kiểm tra hết thời gian bản đồ
            if (Util.canDoWithTime(lastTimeOpen, TIME_KHI_BAN_DO_KHO_BAU)) {
                this.finish();
                return;
            }

            // Cứ mỗi 10 phút thì buff HP quái
            if (now - lastTimeBuffHP >= TIME_BUFF_HP) {
                buffQuaiX3HP();
                lastTimeBuffHP = now;
            }
        }
    }

    private void buffQuaiX3HP() {
        for (Zone zone : zones) {
            for (Mob mob : zone.mobs) {
                mob.point.maxHp *= 1.5;
                mob.point.hp = mob.point.maxHp;
            }
        }
        Service.gI().sendThongBao(player.clan.membersInGame, "Lũ quái đã trở nên mạnh hơn! HP x1.5!!");
    }

    public void openBanDoKhoBau(Player plOpen, Clan clan, int level) {
        this.level = level;
        plOpen.bdkb_countPerDay++;
        plOpen.bdkb_lastTimeJoin = System.currentTimeMillis();
        this.lastTimeOpen = System.currentTimeMillis();
        this.isOpened = true;
        plOpen.bdkb_isJoinBdkb = true;
        this.clan = clan;
        this.clan.timeOpenbdkb = this.lastTimeOpen;
        this.clan.playerOpenbdkb = plOpen;
        this.clan.banDoKhoBau = this;
        this.player = plOpen;
        resetBanDoKhoBau();
        ChangeMapService.gI().goToDBKB(plOpen);
        sendTextbdkb();
    }

    private void resetBanDoKhoBau() {
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) {
                Mob.initMopbbdkb(m, this.level);
                m.sendMobHoiSinh();
            }
        }
    }

    //kết thúc bản đồ kho báu
    public void finish() {
        List<Player> plOutDT = new ArrayList<>();
        for (Zone zone : zones) {
            List<Player> players = zone.getPlayers();
            plOutDT.addAll(players);

        }
        for (Player pl : plOutDT) {
            ChangeMapService.gI().changeMapBySpaceShip(pl, 0, -1, 384);
        }
        this.clan.banDoKhoBau = null;
        this.clan = null;
        this.isOpened = false;
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public static void addZone(int idbdkb, Zone zone) {
        BAN_DO_KHO_BAU.get(idbdkb).zones.add(zone);
    }

    private void sendTextbdkb() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }
}
