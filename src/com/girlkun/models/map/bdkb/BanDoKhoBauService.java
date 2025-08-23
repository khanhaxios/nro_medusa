package com.girlkun.models.map.bdkb;

import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.list_boss.phoban.TrungUyXanhLoBdkb;
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
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.girlkun.models.map.bdkb.BanDoKhoBau.TIME_KHI_BAN_DO_KHO_BAU;
import static com.girlkun.models.map.bdkb.BanDoKhoBau.TIME_WAIT_BDKB;

/**
 * @author BTH
 */
public class BanDoKhoBauService {
    private static BanDoKhoBauService i;

    private BanDoKhoBauService() {

    }

    public static BanDoKhoBauService gI() {
        if (i == null) {
            i = new BanDoKhoBauService();
        }
        return i;
    }

    public void update(Player player) {
        if (player.zone == null || !MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            return;
        }
        if (player.isPl() && player.clan.banDoKhoBau != null
                && player.clan.timeOpenbdkb != 0) {
            if (Util.canDoWithTime(player.clan.timeOpenbdkb, TIME_KHI_BAN_DO_KHO_BAU)) {
                BanDoKhoBauService.gI().ketthucbdkb(player);
                return;
            }
            if (player.isPl() && player.clan.banDoKhoBau != null && player.clan.banDoKhoBau.timeOutMap > 0
                    && player.clan.timeOpenbdkb != 0) {
                while (player.clan.banDoKhoBau.timeOutMap > 0) {
                    player.clan.banDoKhoBau.timeOutMap--;
                    Service.getInstance().sendThongBao(player, "Bản đồ kho báu sẽ kết thúc trong " + player.clan.banDoKhoBau.timeOutMap + " giây. Tàu vũ trụ sẽ đưa bạn về nhà");
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        System.out.println("loi ne bdkb 1 ");
                    }
                }
                BanDoKhoBauService.gI().ketthucbdkb(player);
            }
        }
    }

    public void joinBDKB(Player pl) {
        if (pl.clan == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (pl.clan.banDoKhoBau != null) {
            if (!pl.bdkb_isJoinBdkb) {
                pl.bdkb_countPerDay++;
                pl.bdkb_isJoinBdkb = true;
            }
            pl.bdkb_lastTimeJoin = System.currentTimeMillis();
            ChangeMapService.gI().goToDBKB(pl);
            ItemTimeService.gI().sendTextBanDoKhoBau(pl);
        }
    }

    private void kickOutOfBDKB(Player player) {
        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            Service.gI().sendThongBao(player, "Trận đại chiến đã kết thúc, tàu vận chuyển sẽ đưa bạn về nhà");
            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
        }
    }

    public void kethucbdkbwithoutinside(Player player) {
        List<Player> playersMap = player.clan.membersInGame;
        int id = player.clan.banDoKhoBau.id;
        Zone zone = player.clan.banDoKhoBau.getMapById(137);
        List<Player> bosses = zone.getBosses();
        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            kickOutOfBDKB(pl);
            ItemTimeService.gI().removeTextbdkb(player);
            pl.bdkb_isJoinBdkb = false;
            pl.clan.banDoKhoBau.dispose();
            pl.clan.banDoKhoBau = null;
        }
        player.clan.banDoKhoBau.dispose();
        player.clan.banDoKhoBau = null;
        player.bdkb_isJoinBdkb = false;

        for (Player boss : bosses) {
            if (boss != null && !boss.isDie) {
                boss.injured(player, boss.nPoint.hpMax + 10000, false, false, true);
            }
        }


        Service.gI().sendThongBao(player, "Đã xóa bản đồ kho báu thành công");
        BanDoKhoBau.BAN_DO_KHO_BAU.set(id, new BanDoKhoBau(id));
    }

    public void ketthucbdkb(Player player) {
        List<Player> playersMap = new ArrayList<>();
        for (int i = 135; i <= 138; i++) {
            // get player in this map
            Map map = MapService.gI().getMapById(i);
            for (Zone zone : map.zones) {
                if (zone.zoneId == player.clan.banDoKhoBau.id) {
                    playersMap.addAll(zone.getPlayers());
                }
            }
        }
        int id = player.clan.banDoKhoBau.id;
        Zone zone = player.clan.banDoKhoBau.getMapById(137);
        List<Player> bosses = zone.getBosses();
        for (int i = playersMap.size() - 1; i >= 0; i--) {
            Player pl = playersMap.get(i);
            if (containtInClan(pl, player)) {
                kickOutOfBDKB(pl);
                ItemTimeService.gI().removeTextbdkb(player);
                pl.bdkb_isJoinBdkb = false;
                pl.clan.banDoKhoBau.dispose();
                pl.clan.banDoKhoBau = null;
            }
        }
        for (Player boss : bosses) {
            if (boss != null && !boss.isDie) {
                boss.injured(player, boss.nPoint.hpMax + 10000, false, false, true);
            }
        }
        BanDoKhoBau.BAN_DO_KHO_BAU.set(id, new BanDoKhoBau(id));
    }

    private boolean containtInClan(Player pl, Player player) {
        for (Player player1 : pl.clan.membersInGame) {
            if (player.id == player1.id) {
                return true;
            }
        }
        return false;
    }


    public void openBanDoKhoBau(Player player, int level) {
        if (level >= 1 && level <= 500) {
            if (player.clan != null && player.clan.banDoKhoBau == null) {
                Item item = InventoryServiceNew.gI().findItemBag(player, 611);
                if (item != null && item.quantity > 0) {
                    BanDoKhoBau bdkb = null;
                    for (BanDoKhoBau bdkb1 : BanDoKhoBau.BAN_DO_KHO_BAU) {
                        if (!bdkb1.isOpened) {
                            bdkb = bdkb1;
                            break;
                        }
                    }
                    if (bdkb != null) {
                        if (!Util.canDoWithTime(player.clan.timeOpenbdkb, TIME_WAIT_BDKB)) {
                            Service.gI().sendThongBao(player, "Bang hội của bạn vừa đi bản đồ kho báu!Cần đợi " + (TIME_WAIT_BDKB - (System.currentTimeMillis() - player.clan.timeOpenbdkb)) + "Giây nữa để đi");
                            return;
                        }
                        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                        InventoryServiceNew.gI().sendItemBags(player);
                        bdkb.openBanDoKhoBau(player, player.clan, level);
                        try {
                            double totalDame = 0;
                            double totalHp = 0;
                            for (Player play : player.clan.membersInGame) {
                                totalDame += play.nPoint.dame;
                                totalHp += play.nPoint.hpMax;
                            }
                            double dame = (totalHp / 100) * (level);
                            double hp = (totalDame * 10) * (level);
                            new TrungUyXanhLoBdkb(player.clan.banDoKhoBau.getMapById(137), level, dame, hp, BossID.TRUNG_UY_XANH_LO_BDKB);
                        } catch (Exception e) {
                            Logger.logException(BanDoKhoBauService.class, e, "Lỗi init boss");
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Bản đồ kho báu đã đầy, vui lòng quay lại sau");
                    }
                } else {
                    Service.getInstance().sendThongBao(player, "Yêu cầu có bản đồ kho báu");
                }
            } else {
                Service.getInstance().sendThongBao(player, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
        }
    }

    public void setTimeOutMap(Player plKill, int i) {
        if (plKill.clan.banDoKhoBau != null && plKill.clan.timeOpenbdkb != 0) {
            plKill.clan.banDoKhoBau.timeOutMap = i;
        }
    }

    public void clearAll() {
        Iterator<BanDoKhoBau> iterator = BanDoKhoBau.BAN_DO_KHO_BAU.iterator();
        while (iterator.hasNext()) {
            BanDoKhoBau banDoKhoBau = iterator.next();
            if (banDoKhoBau != null) {
                if (banDoKhoBau.clan != null && (Util.canDoWithTime(banDoKhoBau.clan.timeOpenbdkb, BanDoKhoBau.TIME_KHI_BAN_DO_KHO_BAU)
                        || banDoKhoBau.timeOutMap > 0)) {
                    ketthucbdkb(banDoKhoBau.player);
                }
            }
        }
        //  init new
        BanDoKhoBau.BAN_DO_KHO_BAU.clear();
        for (int i = 0; i < BanDoKhoBau.MAX_AVAILABLE; i++) {
            BanDoKhoBau.BAN_DO_KHO_BAU.add(new BanDoKhoBau(i));
        }
        Logger.log("Reset bản đồ kho báu thành công");
    }
}
