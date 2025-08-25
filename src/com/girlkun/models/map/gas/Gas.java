package com.girlkun.models.map.gas;
//import com.girlkun.models.boss.bdkb.TrungUyXanhLo;

import com.girlkun.models.clan.Clan;
import com.girlkun.models.map.Zone;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.func.ChangeMapService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BTH     public static final int MAX_AVAILABLE = 50;
 */
public class Gas {

    public static final long POWER_CAN_GO_TO_GAS = 2000000000;

    public static final List<Gas> KHI_GAS;
    public static final int MAX_AVAILABLE = 30;
    public int timeWaitOut;
    public static final int TIME_KHI_GAS = 1800000;

    private Player player;

    static {
        KHI_GAS = new ArrayList<>();
        for (int i = 0; i < MAX_AVAILABLE; i++) {
            KHI_GAS.add(new Gas(i));
        }
    }

    public int id;
    public short level;
    public final List<Zone> zones;

    public Clan clan;
    public boolean isOpened;
    private long lastTimeOpen;

    public Gas(int id) {
        this.id = id;
        this.zones = new ArrayList<>();
    }


    public void openKhiGas(Player plOpen, Clan clan, short level) {
        this.level = level;
        this.lastTimeOpen = System.currentTimeMillis();
        this.player = plOpen;
        this.isOpened = true;
        this.clan = clan;
        this.clan.timeOpenKhiGas = this.lastTimeOpen;
        this.clan.playerOpenKhiGas = plOpen;
        this.clan.khiGas = this;
        resetGas();
        ChangeMapService.gI().goToGas(plOpen);
        sendTextGas();
    }

    private void resetGas() {
        for (Zone zone : zones) {
            for (Mob m : zone.mobs) {
                Mob.initMopbKhiGas(m, this.level);
                m.hoiSinh();
                m.sendMobHoiSinh();
            }
        }
    }

    public Zone getMapById(int mapId) {
        for (Zone zone : zones) {
            if (zone.map.mapId == mapId) {
                return zone;
            }
        }
        return null;
    }

    public static void addZone(int idGas, Zone zone) {
        KHI_GAS.get(idGas).zones.add(zone);
    }

    private void sendTextGas() {
        for (Player pl : this.clan.membersInGame) {
            ItemTimeService.gI().sendTextGas(pl);
        }
    }

    public void dispose() {
        this.player = null;
        this.clan = null;
        this.isOpened = false;
    }
}
