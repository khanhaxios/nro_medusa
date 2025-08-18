package com.girlkun.models.map.gas;

import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.list_boss.gas.DrLyChee;
import com.girlkun.models.boss.list_boss.gas.HaChiJack;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemTimeService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

import static com.girlkun.models.map.gas.Gas.TIME_KHI_GAS;

/**
 * @author BTH
 */
public class GasService {

    private static final long TIME_WAIT_KHI_GAS = (60 * 60 * 2) * 1000;
    private static GasService i;

    private GasService() {

    }

    public static GasService gI() {
        if (i == null) {
            i = new GasService();
        }
        return i;
    }

    public void update(Player player) {
        if (player.zone == null || !MapService.gI().isMapKhiGas(player.zone.map.mapId)) {
            return;
        }
        if (player.isPl() && player.clan.khiGas != null
                && player.clan.timeOpenKhiGas != 0) {
            if (Util.canDoWithTime(player.clan.timeOpenKhiGas, TIME_KHI_GAS)) {
                ketthucGas(player);
                return;
            }
            if (player.clan.khiGas.isOpened && player.clan.khiGas.timeWaitOut > 0) {
                player.clan.khiGas.timeWaitOut = -1;
                Service.gI().sendThongBao(player, "Khí gas sẽ kết thúc trong " + player.clan.khiGas.timeWaitOut + " giây");
                if (player.clan.khiGas.timeWaitOut == 0) {
                    ketthucGas(player);
                }
            }
        }
    }

    private void kickOutOfGas(Player player) {
        if (MapService.gI().isMapKhiGas(player.zone.map.mapId)) {
            Service.gI().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    public void ketthucGas(Player player) {
        List<Player> players = new ArrayList<>();
        List<Player> bosess = new ArrayList<>();
        int idGas = player.clan.khiGas.id;

        for (int i = 147; i <= 152; i++) {
            if (MapService.gI().isMapKhiGas(i)) {
                Map map = MapService.gI().getMapById(i);
                for (Zone zone : map.zones) {
                    if (zone.zoneId == idGas) {
                        players.addAll(zone.getPlayers());
                        bosess.addAll(zone.getBosses());
                    }
                }
            }
        }
        for (int i = players.size() - 1; i >= 0; i--) {
            Player pl = players.get(i);
            if (containInClan(pl, player)) {
                kickOutOfGas(pl);
                ItemTimeService.gI().removeTextKhiGas(player);
                pl.clan.khiGas.dispose();
                pl.clan.khiGas = null;
            }
        }
        for (Player bosess1 : bosess) {
            bosess1.injured(player, bosess1.nPoint.hpMax + 1000000, false, false, true);
        }
        // reset khi gas
        Gas.KHI_GAS.set(idGas, new Gas(idGas));
    }

    public void ketthucGasWithoutInside(Player player) {
        List<Player> players = player.clan.membersInGame;
        List<Player> bosess = new ArrayList<>();
        int idGas = player.clan.khiGas.id;

        for (int i = 147; i <= 152; i++) {
            if (MapService.gI().isMapKhiGas(i)) {
                Map map = MapService.gI().getMapById(i);
                for (Zone zone : map.zones) {
                    if (zone.zoneId == idGas) {
                        bosess.addAll(zone.getBosses());
                    }
                }
            }
        }

        for (int i = players.size() - 1; i >= 0; i--) {
            Player pl = players.get(i);
            kickOutOfGas(pl);
            ItemTimeService.gI().removeTextKhiGas(player);
            pl.clan.khiGas.dispose();
            pl.clan.khiGas = null;
        }
        for (Player bosess1 : bosess) {
            bosess1.injured(player, bosess1.nPoint.hpMax + 1000000, false, false, true);
        }
        // reset khi gas
        Service.gI().sendThongBao(player, "Đã xóa khí gas thành công");
        Gas.KHI_GAS.set(idGas, new Gas(idGas));
    }

    private boolean containInClan(Player pl, Player player) {
        for (Player player1 : player.clan.membersInGame) {
            if (pl.id == player1.id) {
                return true;
            }
        }
        return false;
    }


    public void openKhiGas(Player player, short level) {
        if (!Util.canDoWithTime(player.clan.timeOpenKhiGas, TIME_WAIT_KHI_GAS)) {
            Service.gI().sendThongBao(player, "Bạn vừa đi khí gas hãy đợi " + TimeUtil.getTimeLeft(System.currentTimeMillis() - player.clan.timeOpenKhiGas, (int) (TIME_WAIT_KHI_GAS / 1000)) + " Giây nữa để đi tiếp");
            return;
        }
        if (level >= 1 && level <= 500) {
            if (player.clan != null && player.clan.khiGas == null) {
                Item item = InventoryServiceNew.gI().findItemBag(player, 1309);
                if (item != null && item.quantity > 0) {
                    Gas gas = null;
                    for (Gas bdkb : Gas.KHI_GAS) {
                        if (!bdkb.isOpened) {
                            gas = bdkb;
                            break;
                        }
                    }
                    if (gas != null) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                        InventoryServiceNew.gI().sendItemBags(player);
                        gas.openKhiGas(player, player.clan, level);
                        try {
                            long totalDame = 0;
                            long totalHp = 0;
                            for (Player play : player.clan.membersInGame) {
                                totalDame += play.nPoint.dame;
                                totalHp += play.nPoint.hpMax;
                            }
                            long dame = (totalHp / 20) * (level);
                            long hp = (totalDame * 10) * (level);
                            if (dame >= 2000000000L) {
                                dame = 2000000000L;
                            }
                            if (hp >= 2000000000L) {
                                hp = 2000000000L;
                            }
                            new DrLyChee(player.clan.khiGas.getMapById(148), level, (int) dame, (int) hp, BossID.DR_LYCHEE);
                            new HaChiJack(player.clan.khiGas.getMapById(148), level, (int) dame, (int) hp, player);
                        } catch (Exception e) {
                            Logger.logException(GasService.class, e, "Lỗi init boss");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Khí Gas Destroy đã đầy, vui lòng quay lại sau");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Yêu cầu có Bình Khí gas");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }
}
