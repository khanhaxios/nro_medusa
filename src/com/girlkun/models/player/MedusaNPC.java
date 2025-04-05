package com.girlkun.models.player;

import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.services.MapService;
import com.girlkun.consts.ConstMap;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.MapService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class MedusaNPC extends Player {

    private long lastTimeChat;
    private Player playerTarget;

    private long lastTimeTargetPlayer;
    private long timeTargetPlayer = 10000;
    private long lastZoneSwitchTime;
    private long zoneSwitchInterval;
    private List<Zone> availableZones;
    private long lastTimeMove;

    String[] str = {"Kia là đường lên trên tháp karin đó, nơi đó có thần mèo Karin",
         "Nạp tại trang chủ nromedusa.win hoặc liên hệ trực tiếp Medusa!!!"};

    public void initMedusaNPC() {
        init();
    }

    @Override
    public short getHead() {
        return 1475;
    }

    @Override
    public short getBody() {
        return 1476;
    }

    @Override
    public short getLeg() {
        return 1477;
    }

    @Override
    public short getFlagBag() {
        return 132;
    }

    public void joinMap(Zone z, Player player) {
        MapService.gI().goToMap(player, z);
        z.load_Me_To_Another(player);
    }

    public void moveTo(int x, int y) {
        byte dir = (byte) (this.location.x - x < 0 ? 1 : -1);
        byte move = (byte) Util.nextInt(40, 60);
        PlayerService.gI().playerMove(this, this.location.x + (dir == 1 ? move : -move), y + (Util.isTrue(3, 10) ? -50 : 0));
    }

    @Override
    public void update() {
        if (Util.canDoWithTime(lastTimeMove, 1000)) {
            if (this.zone.map.mapId == 1 && this.name.equals("Medusa Linh Xinh Gái")) {
                this.moveTo(Util.nextInt(950, 1350), Util.nextInt(360, 360));
            }
            lastTimeMove = System.currentTimeMillis();
        }
        if (Util.canDoWithTime(lastTimeChat, 10000)) {
            Service.getInstance().chat(this, str[Util.nextInt(str.length)]);
            lastTimeChat = System.currentTimeMillis();

        }
    }

    private void init() {
        int id = -1000000;
        for (Map m : Manager.MAPS) {
            if (m.mapId == 1) {
                for (Zone z : m.zones) {
                    MedusaNPC pl = new MedusaNPC();
                    pl.name = "Medusa Linh Xinh Gái";
                    pl.gender = 0;
                    pl.id = id++;
                    pl.nPoint.hpMax = 110000;
                    pl.nPoint.hpg = 110000;
                    pl.nPoint.hp = 110000;
                    pl.nPoint.setFullHpMp();
                    pl.location.x = 296;
                    pl.location.y = 386;
                    joinMap(z, pl);
                    z.setMedusa(pl);
                }
            }
        }
    }
}
