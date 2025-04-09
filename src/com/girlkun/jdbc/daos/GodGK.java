package com.girlkun.jdbc.daos;

import com.girlkun.database.GirlkunDB;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.data.DataGame;
import com.girlkun.models.card.Card;
import com.girlkun.models.card.OptionCard;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.clan.ClanMember;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.ItemTime;
import com.girlkun.models.item.ItemTimeSieuCap;
import com.girlkun.models.npc.specialnpc.MabuEgg;
import com.girlkun.models.npc.specialnpc.Timedua;
import com.girlkun.models.npc.specialnpc.MagicTree;
import com.girlkun.models.player.Enemy;
import com.girlkun.models.player.Friend;
import com.girlkun.models.player.Fusion;
import com.girlkun.models.player.Pet.DaoLu.DaoLu;
import com.girlkun.models.player.Pet.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.models.task.TaskMain;
import com.girlkun.server.Client;
import com.girlkun.server.Manager;
import com.girlkun.server.io.MySession;
import com.girlkun.server.model.AntiLogin;
import com.girlkun.services.ClanService;
import com.girlkun.services.IntrinsicService;
import com.girlkun.services.ItemService;
import com.girlkun.services.MapService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.TimeUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.girlkun.utils.Util;

import java.time.LocalTime;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class GodGK {

    public static Boolean baotri = false;

    public static List<OptionCard> loadOptionCard(JSONArray json) {
        List<OptionCard> ops = new ArrayList<>();
        try {
            for (int i = 0; i < json.size(); i++) {
                JSONObject ob = (JSONObject) json.get(i);
                if (ob != null) {
                    ops.add(new OptionCard(Integer.parseInt(ob.get("id").toString()),
                            Integer.parseInt(ob.get("param").toString()), Byte.parseByte(ob.get("active").toString())));
                }
            }
        } catch (Exception e) {
            System.out.println("        loi 25");
        }
        return ops;
    }

    public static synchronized Player login(MySession session, AntiLogin al) {
        Player player = null;
        GirlkunResultSet rs = null;
        try {
            rs = GirlkunDB.executeQuery("select * from account where username = ? and password = ?", session.uu, session.pp);
            if (rs.first()) {
                session.userId = rs.getInt("account.id");
                session.isAdmin = rs.getBoolean("is_admin");
                session.lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                session.actived = rs.getBoolean("active");
                session.goldBar = rs.getInt("account.thoi_vang");
                session.bdPlayer = rs.getDouble("account.bd_player");
                session.vnd = rs.getInt("vnd");
                long lastTimeLogin = rs.getTimestamp("last_time_login").getTime();
                int secondsPass1 = (int) ((System.currentTimeMillis() - lastTimeLogin) / 1000);
                long lastTimeLogout = rs.getTimestamp("last_time_logout").getTime();
                int secondsPass = (int) ((System.currentTimeMillis() - lastTimeLogout) / 1000);

//                if (!session.isAdmin) {
//                    Service.getInstance().sendThongBaoOK(session, "Chi danh cho admin");
//                }else
                if (session.version < 200) {
                    Service.getInstance().sendThongBaoOK(session, "Vui lòng vào Game bằng Bản Version 225 Trở lên");
                    return null;
                }
                if (rs.getBoolean("ban")) {
                    Service.getInstance().sendThongBaoOK(session, "Tài khoản của bạn đã bị khóa. Lý do : Clone trên 5 acc !!!");
                } else if (baotri && !session.isAdmin) {
                    Service.getInstance().sendThongBaoOK(session, "Máy chủ đang bảo trì, vui lòng quay lại sau!");
                } else if (!session.isAdmin && secondsPass1 < Manager.SECOND_WAIT_LOGIN) {
                    if (secondsPass < secondsPass1) {
                        Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + "s");
                        return null;
                    }
                    Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass1) + "s");
                    return null;
                } else if (!session.isAdmin && rs.getTimestamp("last_time_login").getTime() > session.lastTimeLogout) {
                    Player plInGame = Client.gI().getPlayerByUser(session.userId);
                    if (plInGame != null) {
                        Client.gI().kickSession(plInGame.getSession());
                        Service.getInstance().sendThongBaoOK(session, "Ai đó đã vô acc bạn :3");
                    } else {
                        GirlkunDB.executeUpdate("update account set last_time_logout = ? where id = ?",
                                new Timestamp(System.currentTimeMillis()), session.userId);
                        Service.getInstance().sendThongBaoOK(session, "Cập nhật dữ liệu tài khoản hoàn tất, có thể tiếp tục đăng nhập");
                        return null;
                    }
//                    Service.getInstance().sendThongBaoOK(session, "Tài khoản đang được đăng nhập tại máy chủ khác");
                } else {
                    if (!session.isAdmin && secondsPass < Manager.SECOND_WAIT_LOGIN) {
                        Service.getInstance().sendThongBaoOK(session, "Vui lòng chờ " + (Manager.SECOND_WAIT_LOGIN - secondsPass) + "s");
                    } else {//set time logout trước rồi đọc data player
                        rs = GirlkunDB.executeQuery("select * from player where account_id = ? limit 1", session.userId);
                        if (!rs.first()) {
                            //-28 -4 version data game
                            DataGame.sendVersionGame(session);
                            //-31 data item background
                            DataGame.sendDataItemBG(session);
                            Service.getInstance().switchToCreateChar(session);
                        } else {
                            Player plInGame = Client.gI().getPlayerByUser(session.userId);
                            if (plInGame != null) {
                                Client.gI().kickSession(plInGame.getSession());
                            }
                            int plHp = 200000000;
                            int plMp = 200000000;
                            JSONValue jv = new JSONValue();
                            JSONArray dataArray = null;

                            player = new Player();

                            //base info
                            player.id = rs.getInt("id");
                            player.vnd = rs.getInt("vndd");
                            if (player.vnd >= 500000 && player.vnd < 1000000) {
                                player.name = "[VIP] " + rs.getString("name");
                            } else if (player.vnd >= 1000000) {
                                player.name = "[SVIP] " + rs.getString("name");
                            } else {
                                player.name = rs.getString("name");
                            }
                            player.head = rs.getShort("head");
                            player.gender = rs.getByte("gender");
                            player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                            player.diemdanh = rs.getLong("violate");
                            player.NguHanhSonPoint = rs.getInt("NguHanhSonPoint");
                            // data rada card
                            // data rada card
                            dataArray = (JSONArray) jv.parse(rs.getString("data_card"));
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.size(); i++) {
                                    JSONObject obj = (JSONObject) dataArray.get(i);
                                    player.Cards.add(new Card(Short.parseShort(obj.get("id").toString()), Byte.parseByte(obj.get("amount").toString()), Byte.parseByte(obj.get("max").toString()), Byte.parseByte(obj.get("level").toString()), loadOptionCard((JSONArray) JSONValue.parse(obj.get("option").toString())), Byte.parseByte(obj.get("used").toString())));
                                }
                                dataArray.clear();
                            }
                            player.totalPlayerViolate = 0;
                            int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
                            if (clanId != -1) {
                                try {
                                    Clan clan = ClanService.gI().getClanById(clanId);
                                    for (ClanMember cm : clan.getMembers()) {
                                        if (cm.id == player.id) {
                                            clan.addMemberOnline(player);
                                            player.clan = clan;
                                            player.clanMember = cm;
                                            break;
                                        }
                                    }
                                } catch (Exception e) {
                                    System.err.println("Phát hiện bang hội id '" + clanId + "' không tồn tại - loại bỏ bang của người chơi: " + player.name);
                                }
                            }

                            //data kim lượng
                            dataArray = (JSONArray) jv.parse(rs.getString("data_inventory"));

                            try {
                                player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
                                player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
                                player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));
                            } catch (Exception e) {
                                player.inventory.ruby = 2_000_000_000;
                                player.inventory.gem = 2_000_000_000;
                            }

                            try {
                                player.inventory.skMedusa = Integer.parseInt(String.valueOf(dataArray.get(3)));
                                player.inventory.event = Integer.parseInt(String.valueOf(dataArray.get(4)));
                                player.inventory.eventSanMa = Integer.parseInt(String.valueOf(dataArray.get(5)));
                                player.inventory.event2T9 = Integer.parseInt(String.valueOf(dataArray.get(6)));
                                player.inventory.eventTrungThu = Integer.parseInt(String.valueOf(dataArray.get(7)));
                                player.inventory.null4 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                                player.inventory.null5 = Integer.parseInt(String.valueOf(dataArray.get(9)));
                            } catch (Exception e) {
                            }
                            dataArray.clear();

                            //data danh hiệu
                            dataArray = (JSONArray) jv.parse(rs.getString("dhieu"));
                            player.titleitem = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                            player.titlett = Integer.parseInt(String.valueOf(dataArray.get(1))) == 1 ? true : false;
                            dataArray.clear();
                            dataArray = (JSONArray) jv.parse(rs.getString("dhtime"));
                            player.isTitleUse1 = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                            player.lastTimeTitle1 = Long.parseLong(String.valueOf(dataArray.get(1)));
                            dataArray.clear();
                            dataArray = (JSONArray) jv.parse(rs.getString("dhtime2"));
                            player.isTitleUse2 = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                            player.lastTimeTitle2 = Long.parseLong(String.valueOf(dataArray.get(1)));
                            dataArray.clear();
                            dataArray = (JSONArray) jv.parse(rs.getString("dhtime3"));
                            player.isTitleUse3 = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                            player.lastTimeTitle3 = Long.parseLong(String.valueOf(dataArray.get(1)));
                            dataArray.clear();

                            dataArray = (JSONArray) jv.parse(rs.getString("dk_tutien"));
                            player.tt_cauca = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            player.tt_gapVIP = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            player.tt_pemgau = Integer.parseInt(String.valueOf(dataArray.get(2)));
                            player.tt_dautruong = Integer.parseInt(String.valueOf(dataArray.get(3)));
                            dataArray.clear();

                            dataArray = (JSONArray) jv.parse(rs.getString("Tu_tien"));
                            player.haveTuTien = Integer.parseInt(String.valueOf(dataArray.get(0))) == 1 ? true : false;
                            player.CapTuTien = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            player.KinhNghiemTT = Long.parseLong(String.valueOf(dataArray.get(2)));
                            dataArray.clear();

                            dataArray = (JSONArray) jv.parse(rs.getString("dk_kethon"));
                            player.dk_bdkb = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            player.dk_gapvip = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            dataArray.clear();

                            dataArray = (JSONArray) jv.parse(rs.getString("ket_hon"));
                            player.dakethon = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            player.duockethon = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            dataArray.clear();

                            //data tọa độ
                            try {
                                dataArray = (JSONArray) jv.parse(rs.getString("data_location"));
                                int mapId = Integer.parseInt(String.valueOf(dataArray.get(0)));
                                player.location.x = Integer.parseInt(String.valueOf(dataArray.get(1)));
                                player.location.y = Integer.parseInt(String.valueOf(dataArray.get(2)));
                                player.location.lastTimeplayerMove = System.currentTimeMillis();
                                if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                                        || MapService.gI().isMapBanDoKhoBau(mapId) || MapService.gI().isMapKhiGas(mapId) || MapService.gI().isMapMaBu(mapId)) {
                                    mapId = player.gender + 21;
                                    player.location.x = 300;
                                    player.location.y = 336;
                                }
                                player.zone = MapService.gI().getMapCanJoin(player, mapId, -1);
                            } catch (Exception e) {
                                System.out.println("eee");
                            }
                            dataArray.clear();

                            //data chỉ số
                            dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                            player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                            player.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                            player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                            player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                            player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                            player.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                            player.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                            player.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                            player.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                            player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));
                            dataArray.get(10); //** Năng động
                            plHp = Integer.parseInt(String.valueOf(dataArray.get(11)));
                            plMp = Integer.parseInt(String.valueOf(dataArray.get(11)));
                            dataArray.clear();

                            //data đậu thần
                            dataArray = (JSONArray) jv.parse(rs.getString("data_magic_tree"));
                            byte level = Byte.parseByte(String.valueOf(dataArray.get(0)));
                            byte currPea = Byte.parseByte(String.valueOf(dataArray.get(1)));
                            boolean isUpgrade = Byte.parseByte(String.valueOf(dataArray.get(2))) == 1;
                            long lastTimeHarvest = Long.parseLong(String.valueOf(dataArray.get(3)));
                            long lastTimeUpgrade = Long.parseLong(String.valueOf(dataArray.get(4)));
                            player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
                            dataArray.clear();

                            //data phần thưởng sao đen
                            dataArray = (JSONArray) jv.parse(rs.getString("data_black_ball"));
                            JSONArray dataBlackBall = null;
                            for (int i = 0; i < dataArray.size(); i++) {
                                dataBlackBall = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(0)));
                                player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(1)));
                                try {
                                    player.rewardBlackBall.quantilyBlackBall[i] = dataBlackBall.get(2) != null ? Integer.parseInt(String.valueOf(dataBlackBall.get(2))) : 0;
                                } catch (Exception e) {
                                    player.rewardBlackBall.quantilyBlackBall[i] = player.rewardBlackBall.timeOutOfDateReward[i] != 0 ? 1 : 0;
                                    System.out.println("        loi 26");
                                }
                                dataBlackBall.clear();
                            }
                            dataArray.clear();

                            //data body
                            dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                            for (int i = 0; i < dataArray.size(); i++) {
                                Item item = null;
                                JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                if (tempId != -1) {
                                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                                    JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                                    for (int j = 0; j < options.size(); j++) {
                                        JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                                Integer.parseInt(String.valueOf(opt.get(1)))));
                                    }
                                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                                    if (ItemService.gI().isOutOfDateTime(item) || ItemService.gI().isOutOfDateTimeVV(item)) {
                                        item = ItemService.gI().createItemNull();
                                    }
                                } else {
                                    item = ItemService.gI().createItemNull();
                                }
                                player.inventory.itemsBody.add(item);
                            }
                            if (player.inventory.itemsBody.size() == 10) {
                                player.inventory.itemsBody.add(ItemService.gI().createItemNull());
                            }
                            dataArray.clear();

                            //data bag
                            dataArray = (JSONArray) jv.parse(rs.getString("items_bag"));
                            for (int i = 0; i < dataArray.size(); i++) {
                                Item item = null;
                                JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                if (tempId != -1) {
                                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                                    JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                                    for (int j = 0; j < options.size(); j++) {
                                        JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                                Integer.parseInt(String.valueOf(opt.get(1)))));
                                    }
                                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                                    if (ItemService.gI().isOutOfDateTime(item) || ItemService.gI().isOutOfDateTimeVV(item)) {
                                        item = ItemService.gI().createItemNull();
                                    }
                                } else {
                                    item = ItemService.gI().createItemNull();
                                }
                                player.inventory.itemsBag.add(item);
                            }
                            dataArray.clear();

                            //data box
                            dataArray = (JSONArray) jv.parse(rs.getString("items_box"));
                            for (int i = 0; i < dataArray.size(); i++) {
                                Item item = null;
                                JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                                short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                if (tempId != -1) {
                                    item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                                    JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                                    for (int j = 0; j < options.size(); j++) {
                                        JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                        item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                                Integer.parseInt(String.valueOf(opt.get(1)))));
                                    }
                                    item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                                    if (ItemService.gI().isOutOfDateTime(item) || ItemService.gI().isOutOfDateTimeVV(item)) {
                                        item = ItemService.gI().createItemNull();
                                    }
                                } else {
                                    item = ItemService.gI().createItemNull();
                                }
                                player.inventory.itemsBox.add(item);
                            }
                            dataArray.clear();

                            //data box lucky round
                            dataArray = (JSONArray) jv.parse(rs.getString("items_box_lucky_round"));
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.size(); i++) {
                                    Item item = null;
                                    JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                    if (tempId != -1) {
                                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                                        for (int j = 0; j < options.size(); j++) {
                                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                                        }
                                        player.inventory.itemsBoxCrackBall.add(item);
                                    }
                                }
                                dataArray.clear();
                            }

                            //data friends
                            dataArray = (JSONArray) jv.parse(rs.getString("friends"));
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.size(); i++) {
                                    JSONArray dataFE = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                    Friend friend = new Friend();
                                    friend.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                                    friend.name = String.valueOf(dataFE.get(1));
                                    friend.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                                    friend.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                                    friend.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                                    friend.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                                    friend.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                                    player.friends.add(friend);
                                    dataFE.clear();
                                }
                                dataArray.clear();
                            }

                            //data enemies
                            dataArray = (JSONArray) jv.parse(rs.getString("enemies"));
                            if (dataArray != null) {
                                for (int i = 0; i < dataArray.size(); i++) {
                                    JSONArray dataFE = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                    Enemy enemy = new Enemy();
                                    enemy.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                                    enemy.name = String.valueOf(dataFE.get(1));
                                    enemy.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                                    enemy.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                                    enemy.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                                    enemy.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                                    enemy.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                                    player.enemies.add(enemy);
                                    dataFE.clear();
                                }
                                dataArray.clear();
                            }

                            //data nội tại
                            dataArray = (JSONArray) jv.parse(rs.getString("data_intrinsic"));
                            byte intrinsicId = Byte.parseByte(String.valueOf(dataArray.get(0)));
                            player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
                            player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataArray.get(1)));
                            player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataArray.get(2)));
                            player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataArray.get(3)));
                            dataArray.clear();

                            //data item time
                            dataArray = (JSONArray) jv.parse(rs.getString("data_item_time"));
                            int timeBoHuyet = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            int timeBoKhi = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            int timeGiapXen = Integer.parseInt(String.valueOf(dataArray.get(2)));
                            int timeCuongNo = Integer.parseInt(String.valueOf(dataArray.get(3)));
                            int timeAnDanh = Integer.parseInt(String.valueOf(dataArray.get(4)));
                            int timeBiNgo = Integer.parseInt(String.valueOf(dataArray.get(5)));
                            int timeMayDo = Integer.parseInt(String.valueOf(dataArray.get(6)));
                            int timeDuoi = Integer.parseInt(String.valueOf(dataArray.get(7)));
                            int iconDuoi = Integer.parseInt(String.valueOf(dataArray.get(8)));
                            int timeUseTDLT = Integer.parseInt(String.valueOf(dataArray.get(9)));
                            int timeMayDo2 = Integer.parseInt(String.valueOf(dataArray.get(10)));

                            player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
                            player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
                            player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
                            player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
                            player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
                            player.itemTime.lastTimeBiNgo = System.currentTimeMillis() - (ItemTime.TIME_BI_NGO - timeBiNgo);
                            player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
                            player.itemTime.lastTimeDuoikhi = System.currentTimeMillis() - (ItemTime.TIME_DUOI_KHI - timeDuoi);
                            player.itemTime.timeTDLT = timeUseTDLT * 60 * 1000;
                            player.itemTime.lastTimeUseTDLT = System.currentTimeMillis();
                            player.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO2 - timeMayDo2);

                            player.itemTime.iconDuoi = iconDuoi;
                            player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
                            player.itemTime.isUseBoKhi = timeBoKhi != 0;
                            player.itemTime.isUseGiapXen = timeGiapXen != 0;
                            player.itemTime.isUseCuongNo = timeCuongNo != 0;
                            player.itemTime.isUseAnDanh = timeAnDanh != 0;
                            player.itemTime.isBiNgo = timeBiNgo != 0;
                            player.itemTime.isUseMayDo = timeMayDo != 0;
                            player.itemTime.isDuoikhi = timeDuoi != 0;
                            player.itemTime.isUseTDLT = timeUseTDLT != 0;
                            player.itemTime.isUseMayDo2 = timeMayDo2 != 0;
                            dataArray.clear();

                            //data item time
                            dataArray = (JSONArray) jv.parse(rs.getString("data_item_time_sieucap"));
                            int timeBoHuyet3 = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            int timeBoKhi3 = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            int timeGiapXen3 = Integer.parseInt(String.valueOf(dataArray.get(2)));
                            int timeCuongNo3 = Integer.parseInt(String.valueOf(dataArray.get(3)));
                            int timeAnDanh3 = Integer.parseInt(String.valueOf(dataArray.get(4)));
                            int timeKeo = Integer.parseInt(String.valueOf(dataArray.get(5)));
                            int timeXiMuoi = Integer.parseInt(String.valueOf(dataArray.get(6)));
                            int timeDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(7)));
                            int iconDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                            int iconBanh = Integer.parseInt(String.valueOf(dataArray.get(9)));
                            int timeBanh = Integer.parseInt(String.valueOf(dataArray.get(10)));
                            int timeBienhinh = Integer.parseInt(String.valueOf(dataArray.get(11)));
                            int timeBienhinh1 = Integer.parseInt(String.valueOf(dataArray.get(12)));
                            int timeSieuCap = Integer.parseInt(String.valueOf(dataArray.get(13)));

                            int timeUseTDLT3 = 0;
                            if (dataArray.size() == 10) {
                                timeUseTDLT3 = Integer.parseInt(String.valueOf(dataArray.get(9)));
//                            int timeMayDo2 = Integer.parseInt(String.valueOf(dataArray.get(10)));    
                            }

                            player.itemTimesieucap.lastTimeBoHuyet3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBoHuyet3);
                            player.itemTimesieucap.lastTimeBoKhi3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBoKhi3);
                            player.itemTimesieucap.lastTimeGiapXen3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeGiapXen3);
                            player.itemTimesieucap.lastTimeCuongNo3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeCuongNo3);
                            player.itemTimesieucap.lastTimeAnDanh3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeAnDanh3);
                            player.itemTimesieucap.lastTimeKeo = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_KEO - timeKeo);
                            player.itemTimesieucap.lastTimeUseXiMuoi = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_XI_MUOI - timeXiMuoi);
                            player.itemTimesieucap.lastTimeMeal = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_EAT_MEAL - timeDuoi3);
                            player.itemTimesieucap.lastTimeUseBanh = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_TRUNGTHU - timeBanh);
                            player.itemTimesieucap.lastTimeBienhinh = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBienhinh);
                            player.itemTimesieucap.lastTimeBienhinh1 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBienhinh1);
                            player.itemTimesieucap.lastTimeRongSieuCap = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeSieuCap);

                            player.itemTimesieucap.iconMeal = iconDuoi3;
                            player.itemTimesieucap.iconBanh = iconBanh;
                            player.itemTimesieucap.isUseBoHuyet3 = timeBoHuyet3 != 0;
                            player.itemTimesieucap.isUseBoKhi3 = timeBoKhi3 != 0;
                            player.itemTimesieucap.isUseGiapXen3 = timeGiapXen3 != 0;
                            player.itemTimesieucap.isUseCuongNo3 = timeCuongNo3 != 0;
                            player.itemTimesieucap.isUseAnDanh3 = timeAnDanh3 != 0;
                            player.itemTimesieucap.isKeo = timeKeo != 0;
                            player.itemTimesieucap.isUseXiMuoi = timeXiMuoi != 0;
                            player.itemTimesieucap.isEatMeal = timeDuoi3 != 0;
                            player.itemTimesieucap.isUseTrungThu = timeBanh != 0;
                            player.itemTimesieucap.isBienhinh = timeBienhinh != 0;
                            player.itemTimesieucap.isBienhinh1 = timeBienhinh1 != 0;
                            player.itemTimesieucap.isRongSieuCap = timeSieuCap != 0;
                            dataArray.clear();

                            //data nhiệm vụ
                            dataArray = (JSONArray) jv.parse(rs.getString("data_task"));
                            TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataArray.get(0))));
                            taskMain.index = Byte.parseByte(String.valueOf(dataArray.get(1)));
                            taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataArray.get(2)));
                            player.playerTask.taskMain = taskMain;
                            dataArray.clear();

                            //data nhiệm vụ hàng ngày
                            dataArray = (JSONArray) jv.parse(rs.getString("data_side_task"));
                            String format = "dd-MM-yyyy";
                            long receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
                            Date date = new Date(receivedTime);
                            if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                                player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                                player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                                player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                                player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                                player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                                player.playerTask.sideTask.receivedTime = receivedTime;
                            }

                            //data trứng bư
                            dataArray = (JSONArray) jv.parse(rs.getString("data_mabu_egg"));
                            if (dataArray.size() != 0) {
                                player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                                        Long.parseLong(String.valueOf(dataArray.get(1))));
                            }
                            dataArray.clear();
                            //data dưa
                            dataArray = (JSONArray) jv.parse(rs.getString("data_dua"));
                            if (dataArray.size() != 0) {
                                player.timedua = new Timedua(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                                        Long.parseLong(String.valueOf(dataArray.get(1))));
                            }
                            dataArray.clear();

                            //data tài xỉu
                            dataArray = (JSONArray) JSONValue.parse(rs.getString("Tai_xiu"));
                            player.taixiu.hotong = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            player.taixiu.chuyensinh = Byte.parseByte(String.valueOf(dataArray.get(1)));
                            player.taixiu.toptaixiu = Long.parseLong(String.valueOf(dataArray.get(2)));
                            player.taixiu.win = Integer.parseInt(String.valueOf(dataArray.get(3)));
                            player.taixiu.bongtai = Integer.parseInt(String.valueOf(dataArray.get(4)));
                            player.taixiu.MaxGoldTradeDay = Long.parseLong(String.valueOf(dataArray.get(5)));
                            dataArray.clear();

                            //data bùa
                            dataArray = (JSONArray) jv.parse(rs.getString("data_charm"));
                            player.charms.tdTriTue = Long.parseLong(String.valueOf(dataArray.get(0)));
                            player.charms.tdManhMe = Long.parseLong(String.valueOf(dataArray.get(1)));
                            player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataArray.get(2)));
                            player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataArray.get(3)));
                            player.charms.tdBatTu = Long.parseLong(String.valueOf(dataArray.get(4)));
                            player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataArray.get(5)));
                            player.charms.tdThuHut = Long.parseLong(String.valueOf(dataArray.get(6)));
                            player.charms.tdDeTu = Long.parseLong(String.valueOf(dataArray.get(7)));
                            player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataArray.get(8)));
                            player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataArray.get(9)));
                            dataArray.clear();

                            //data skill
                            int[] skillsArr = player.gender == 0 ? ConstPlayer.SKILL_TD
                                    : player.gender == 1 ? ConstPlayer.SKILL_NAMEC
                                    : ConstPlayer.SKILL_XAYDA;
                            dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                            for (int i = 0; i < dataArray.size(); i++) {
                                JSONArray dataSkill = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                int tempId = Integer.parseInt(String.valueOf(dataSkill.get(0)));
                                byte point = Byte.parseByte(String.valueOf(dataSkill.get(1)));
                                Skill skill = null;
                                if (point != 0) {
                                    skill = SkillUtil.createSkill(tempId, point);
                                } else {
                                    skill = SkillUtil.createSkillLevel0(tempId);
                                }
                                skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataSkill.get(2)));
                                if (dataSkill.size() > 3) {
                                    skill.currLevel = Short.parseShort(String.valueOf(dataSkill.get(3)));
                                }
                                player.playerSkill.skills.add(skill);
                            }
                            boolean hasSkill;
                            for (int i = 0; i < skillsArr.length; i++) {
                                hasSkill = false;
                                for (Skill skillPl : player.playerSkill.skills) {
                                    if (skillPl.template.id == skillsArr[i]) {
                                        hasSkill = true;
                                        break;
                                    }
                                }
                                if (!hasSkill) {
                                    Skill skill = SkillUtil.createSkillLevel0(skillsArr[i]);
                                    player.playerSkill.skills.add(skill);
                                }
                            }
                            dataArray.clear();

                            //data skill shortcut
                            dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                            for (int i = 0; i < dataArray.size(); i++) {
                                player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                            }
                            for (int i : player.playerSkill.skillShortCut) {
                                if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                                    player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                                    break;
                                }
                            }
                            if (player.playerSkill.skillSelect == null) {
                                player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                                        ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                            }
                            dataArray.clear();

                            //data pet
                            JSONArray petData = (JSONArray) jv.parse(rs.getString("pet"));
                            if (!petData.isEmpty()) {
                                dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(0)));
                                Pet pet = new Pet(player);
                                pet.id = -player.id;
                                pet.typePet = Byte.parseByte(String.valueOf(dataArray.get(0)));
                                pet.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                                pet.name = String.valueOf(dataArray.get(2));
                                player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                                player.fusion.lastTimeFusion = System.currentTimeMillis()
                                        - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                                pet.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

                                //data chỉ số
                                dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(1)));
                                pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                                pet.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                                pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                                pet.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                                pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                                pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                                pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                                pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                                pet.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                                pet.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                                int hp = Integer.parseInt(String.valueOf(dataArray.get(10)));
                                int mp = Integer.parseInt(String.valueOf(dataArray.get(11)));

                                //data body
                                dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(2)));
                                for (int i = 0; i < dataArray.size(); i++) {
                                    Item item = null;
                                    JSONArray dataItem = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                    if (tempId != -1) {
                                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                                        for (int j = 0; j < options.size(); j++) {
                                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                                        }
                                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                                        if (ItemService.gI().isOutOfDateTime(item) || ItemService.gI().isOutOfDateTimeVV(item)) {
                                            item = ItemService.gI().createItemNull();
                                        }
                                    } else {
                                        item = ItemService.gI().createItemNull();
                                    }
                                    pet.inventory.itemsBody.add(item);
                                }

                                //data skills
                                dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(3)));
                                for (int i = 0; i < dataArray.size(); i++) {
                                    JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                    int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                                    byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                                    Skill skill = null;
                                    if (point != 0) {
                                        skill = SkillUtil.createSkill(tempId, point);
                                    } else {
                                        skill = SkillUtil.createSkillLevel0(tempId);
                                    }
                                    switch (skill.template.id) {
                                        case Skill.KAMEJOKO:
                                        case Skill.MASENKO:
                                        case Skill.ANTOMIC:
                                            skill.coolDown = 1000;
                                            break;
                                    }
                                    pet.playerSkill.skills.add(skill);
                                }
                                if (pet.playerSkill.skills.size() < 5) {
                                    pet.playerSkill.skills.add(4, SkillUtil.createSkillLevel0(-1));
                                }
                                pet.nPoint.hp = hp;
                                pet.nPoint.mp = mp;
                                player.pet = pet;
                            }

                            //data pet dao nu
                            JSONArray petDaoLuData = (JSONArray) jv.parse(rs.getString("Dao_Lu"));
                            if (petDaoLuData != null && !petDaoLuData.isEmpty()) {
                                dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(0)));
                                DaoLu petDaoLu = new DaoLu(player);
                                petDaoLu.id = Player.setIdForPet(petDaoLu, player.id);
                                petDaoLu.typeDaoLu = Byte.parseByte(String.valueOf(dataArray.get(0)));
                                petDaoLu.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                                petDaoLu.nameDaoLu = String.valueOf(dataArray.get(2));
                                petDaoLu.name = "$[" + petDaoLu.getTypeString() + "] " + petDaoLu.nameDaoLu;
                                player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                                player.fusion.lastTimeFusion = System.currentTimeMillis()
                                        - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                                petDaoLu.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

                                //Ndq viết thêm mở rộng
                                try {
                                    petDaoLu.pointTuVi = Integer.parseInt(String.valueOf(dataArray.get(6)));
                                    petDaoLu.pointCapTinh = Integer.parseInt(String.valueOf(dataArray.get(7)));
                                    petDaoLu.pointCapCanhGioi = Integer.parseInt(String.valueOf(dataArray.get(8)));
                                    petDaoLu.timeThangCap = Long.parseLong(String.valueOf(dataArray.get(9)));
                                    petDaoLu.var2 = Integer.parseInt(String.valueOf(dataArray.get(10)));
                                } catch (Exception e) {
                                }

                                //data chỉ số
                                dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(1)));
//                                petDaoLu.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                                petDaoLu.nPoint.limitPower = player.nPoint.limitPower;
                                petDaoLu.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                                petDaoLu.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                                petDaoLu.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                                petDaoLu.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                                petDaoLu.nPoint.hpg = Double.parseDouble(String.valueOf(dataArray.get(5)));
                                petDaoLu.nPoint.mpg = Double.parseDouble(String.valueOf(dataArray.get(6)));
                                petDaoLu.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                                petDaoLu.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                                petDaoLu.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                                double hp = Double.parseDouble(String.valueOf(dataArray.get(10)));
                                double mp = Double.parseDouble(String.valueOf(dataArray.get(11)));

                                //data body
                                dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(2)));
                                for (int i = 0; i < dataArray.size(); i++) {
                                    Item item = null;
                                    JSONArray dataItem = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                                    if (tempId != -1) {
                                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                                        for (int j = 0; j < options.size(); j++) {
                                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                                        }
                                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                                        if (ItemService.gI().isOutOfDateTime(item)) {
                                            item = ItemService.gI().createItemNull();
                                        }
                                    } else {
                                        item = ItemService.gI().createItemNull();
                                    }
                                    petDaoLu.inventory.itemsBody.add(item);
                                }

                                //data skills
                                dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(3)));
                                for (int i = 0; i < dataArray.size(); i++) {
                                    JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                                    int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                                    byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                                    Skill skill = null;
                                    if (point != 0) {
                                        skill = SkillUtil.createSkill(tempId, point);
                                    } else {
                                        skill = SkillUtil.createSkillLevel0(tempId);
                                    }
                                    switch (skill.template.id) {
                                        case Skill.KAMEJOKO:
                                        case Skill.MASENKO:
                                        case Skill.ANTOMIC:
                                            skill.coolDown = 500;
                                            break;
                                    }
                                    petDaoLu.playerSkill.skills.add(skill);
                                }
                                for (int i = 0; i < skillsArr.length; i++) {
                                    hasSkill = false;
                                    for (Skill skillPl : player.playerSkill.skills) {
                                        if (skillPl.template.id == skillsArr[i]) {
                                            hasSkill = true;
                                            break;
                                        }
                                    }
                                    if (!hasSkill) {
                                        Skill skill = SkillUtil.createSkill(skillsArr[i], 1);
                                        petDaoLu.playerSkill.skills.add(skill);
                                    }
                                }
                                petDaoLu.nPoint.hp = hp;
                                petDaoLu.nPoint.mp = mp;
                                player.petDaoLu = petDaoLu;
                            }

                            dataArray = (JSONArray) JSONValue.parse(rs.getString("info_phoban"));
                            if (!dataArray.isEmpty()) {
                                player.bdkb_countPerDay = Integer.parseInt(String.valueOf(dataArray.get(1)));
                                player.bdkb_lastTimeJoin = Long.parseLong(String.valueOf(dataArray.get(0)));
                            }
                            dataArray.clear();

                            dataArray = (JSONArray) JSONValue.parse(rs.getString("pointPvp"));
                            if (!dataArray.isEmpty()) {
                                player.pointPvpthuong = Integer.parseInt(String.valueOf(dataArray.get(0)));
                                player.pointPvpVip = Integer.parseInt(String.valueOf(dataArray.get(1)));

                            }
                            dataArray.clear();

                            // nhiem vu bo mong 
                            JSONObject achievementObject = (JSONObject) JSONValue.parse(rs.getString("info_achievement"));
                            player.achievement.numPvpWin = Integer.parseInt(String.valueOf(achievementObject.get("numPvpWin")));
                            player.achievement.numSkillChuong = Integer.parseInt(String.valueOf(achievementObject.get("numSkillChuong")));
                            player.achievement.numFly = Integer.parseInt(String.valueOf(achievementObject.get("numFly")));
                            player.achievement.numKillMobFly = Integer.parseInt(String.valueOf(achievementObject.get("numKillMobFly")));
                            player.achievement.numKillNguoiRom = Integer.parseInt(String.valueOf(achievementObject.get("numKillNguoiRom")));
                            player.achievement.numHourOnline = Long.parseLong(String.valueOf(achievementObject.get("numHourOnline")));
                            player.achievement.numGivePea = Integer.parseInt(String.valueOf(achievementObject.get("numGivePea")));
                            player.achievement.numSellItem = Integer.parseInt(String.valueOf(achievementObject.get("numSellItem")));
                            player.achievement.numPayMoney = Integer.parseInt(String.valueOf(achievementObject.get("numPayMoney")));
                            player.achievement.numKillSieuQuai = Integer.parseInt(String.valueOf(achievementObject.get("numKillSieuQuai")));
                            player.achievement.numHoiSinh = Integer.parseInt(String.valueOf(achievementObject.get("numHoiSinh")));
                            player.achievement.numSkillDacBiet = Integer.parseInt(String.valueOf(achievementObject.get("numSkillDacBiet")));
                            player.achievement.numPickGem = Integer.parseInt(String.valueOf(achievementObject.get("numPickGem")));

                            dataArray = (JSONArray) JSONValue.parse(String.valueOf(achievementObject.get("listReceiveGem")));
                            for (Byte i = 0; i < dataArray.size(); i++) {
                                player.achievement.listReceiveGem.add(Boolean.valueOf(String.valueOf(dataArray.get(i))));
                            }
                            dataArray.clear();

                            dataArray = (JSONArray) JSONValue.parse(rs.getString("Thu_TrieuHoi"));
                            player.TrieuHoiCapBac = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            if (player.TrieuHoiCapBac >= 0 && player.TrieuHoiCapBac <= 10) {
                                player.TenThuTrieuHoi = String.valueOf(dataArray.get(1));
                                player.TrieuHoiThucAn = Integer.parseInt(String.valueOf(dataArray.get(2)));
                                player.TrieuHoiDame = Long.parseLong(String.valueOf(dataArray.get(3)));
                                player.TrieuHoilastTimeThucan = Long.parseLong(String.valueOf(dataArray.get(4)));
                                player.TrieuHoiLevel = Integer.parseInt(String.valueOf(dataArray.get(5)));
                                if (player.TrieuHoiLevel > 100) {
                                    player.TrieuHoiLevel = 100;
                                }
                                player.TrieuHoiExpThanThu = Long.parseLong(String.valueOf(dataArray.get(6)));
                                player.TrieuHoiHP = Long.parseLong(String.valueOf(dataArray.get(7)));
                            } else {
                                player.TrieuHoiCapBac = -1;
                            }
                            dataArray.clear();

                            //data Nhiệm vụ nhận Chiến Thần
                            dataArray = (JSONArray) JSONValue.parse(rs.getString("nhiemvu_chienthan"));
                            player.chienthan.tasknow = Integer.parseInt(String.valueOf(dataArray.get(0)));
                            player.chienthan.dalamduoc = Integer.parseInt(String.valueOf(dataArray.get(1)));
                            player.chienthan.maxcount = Integer.parseInt(String.valueOf(dataArray.get(2)));
                            player.chienthan.maxtask = Integer.parseInt(String.valueOf(dataArray.get(3)));
                            player.chienthan.donechienthan = Integer.parseInt(String.valueOf(dataArray.get(4)));
                            dataArray.clear();

                            player.bdkb_isJoinBdkb = false;
                            if ((new java.sql.Date(player.bdkb_lastTimeJoin)).getDay() != (new java.sql.Date(System.currentTimeMillis())).getDay()) {
                                player.bdkb_countPerDay = 0;
                            }
                            if ((new java.sql.Date(player.diemdanh)).getDay() != (new java.sql.Date(System.currentTimeMillis())).getDay()) {
                                player.diemdanh = 0;
                            }

                            player.nPoint.hp = plHp;
                            player.nPoint.mp = plMp;
                            player.iDMark.setLoadedAllDataPlayer(true);
                            GirlkunDB.executeUpdate("update account set last_time_login = '" + new Timestamp(System.currentTimeMillis()) + "', ip_address = '" + session.ipAddress + "' where id = " + session.userId);
                        }
                    }
                }
                al.reset();
            } else {
                Service.getInstance().sendThongBaoOK(session, "Thông tin tài khoản hoặc mật khẩu không chính xác");
                al.wrong();
            }
        } catch (Exception e) {
            System.out.println("qqq");
            Logger.error(session.uu);
            player.dispose();
            player = null;
            Logger.logException(GodGK.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }

    public static void checkDo() {
        long st = System.currentTimeMillis();
        JSONValue jv = new JSONValue();
        JSONArray dataArray = null;
        JSONObject dataObject = null;
        Player player;
        PreparedStatement ps = null;
        String name = "";
        ResultSet rs = null;
        try (Connection con = GirlkunDB.getConnection()) {
            ps = con.prepareStatement("select * from player");
            rs = ps.executeQuery();
            while (rs.next()) {
                int plHp = 200000000;
                int plMp = 200000000;
                player = new Player();
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                //data kim lượng
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_inventory"));
                player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
                player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));
                try {
                    player.inventory.skMedusa = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    player.inventory.event = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    player.inventory.eventSanMa = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.inventory.event2T9 = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    player.inventory.eventTrungThu = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    player.inventory.null4 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    player.inventory.null5 = Integer.parseInt(String.valueOf(dataArray.get(9)));
                } catch (Exception e) {
                }
                dataArray.clear();

                //data chỉ số
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_point"));
                player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                player.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                player.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                player.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                player.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                player.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));
                dataArray.get(10); //** Năng động
                plHp = Integer.parseInt(String.valueOf(dataArray.get(11)));
                plMp = Integer.parseInt(String.valueOf(dataArray.get(12)));
                dataArray.clear();

                //data body
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));

                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    Util.useCheckDo(player, item, "body");
                    player.inventory.itemsBody.add(item);
                }
                dataArray.clear();

                //data bag
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_bag"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    Util.useCheckDo(player, item, "bag");
                    player.inventory.itemsBag.add(item);
                }
                dataArray.clear();

                //data box
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    Util.useCheckDo(player, item, "box");
                    player.inventory.itemsBox.add(item);
                }
                dataArray.clear();

                //data box lucky round
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box_lucky_round"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        player.inventory.itemsBoxCrackBall.add(item);
                    }
                }
                dataArray.clear();

                //data pet
                JSONArray petData = (JSONArray) jv.parse(rs.getString("pet"));
                if (!petData.isEmpty()) {
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(0)));
                    Pet pet = new Pet(player);
                    pet.id = -player.id;
                    pet.typePet = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    pet.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                    pet.name = String.valueOf(dataArray.get(2));
                    player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                    player.fusion.lastTimeFusion = System.currentTimeMillis()
                            - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                    pet.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

                    //data chỉ số
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(1)));
                    pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    pet.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                    pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                    pet.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                    pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                    pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    pet.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    pet.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                    int hp = Integer.parseInt(String.valueOf(dataArray.get(10)));
                    int mp = Integer.parseInt(String.valueOf(dataArray.get(11)));

                    //data body
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(2)));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        JSONArray dataItem = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                            JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                        }
                        Util.useCheckDo(player, item, "pet");
                        pet.inventory.itemsBody.add(item);
                    }

                    //data kim lượng
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("Tai_xiu"));
                    player.taixiu.hotong = Integer.parseInt(String.valueOf(dataArray.get(0)));
                    player.taixiu.chuyensinh = Byte.parseByte(String.valueOf(dataArray.get(1)));
                    player.taixiu.toptaixiu = Long.parseLong(String.valueOf(dataArray.get(2)));
                    if (dataArray.size() == 4) {
                        player.taixiu.win = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    } else {
                        player.taixiu.win = 0;
                    }
                    if (dataArray.size() == 5) {
                        player.taixiu.bongtai = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    } else {
                        player.taixiu.bongtai = 0;
                    }
                    player.taixiu.MaxGoldTradeDay = Long.parseLong(String.valueOf(dataArray.get(5)));

                    ////data nhiệm vụ chiến thần
                    dataArray = (JSONArray) JSONValue.parse(rs.getString("nhiemvu_chienthan"));
                    player.chienthan.tasknow = Integer.parseInt(String.valueOf(dataArray.get(0)));
                    player.chienthan.dalamduoc = Integer.parseInt(String.valueOf(dataArray.get(1)));
                    player.chienthan.maxcount = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    if (dataArray.size() == 4) {
                        player.chienthan.maxtask = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    } else {
                        player.chienthan.maxtask = 0;
                    }
                    if (dataArray.size() == 5) {
                        player.chienthan.donechienthan = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    } else {
                        player.chienthan.donechienthan = 0;
                    }
                    dataArray.clear();

                }

            }
        } catch (Exception e) {
            System.out.println(name);
            System.out.println("www");
            Logger.logException(Manager.class, e, "Lỗi load database");
            System.exit(0);
        }
    }

    public static Boolean getAdmin(String uu, String pp) {
        GirlkunResultSet rs = null;
        try {
            rs = GirlkunDB.executeQuery("SELECT * FROM account WHERE username = ? AND password = ?", uu, pp);
            if (rs.first()) {
                return rs.getBoolean("is_admin");
            } else {
                return false;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void checkVang(int x) {
        int thoi_vang = 0;
        long st = System.currentTimeMillis();
        JSONValue jv = new JSONValue();
        JSONArray dataArray = null;
        JSONObject dataObject = null;
        Player player;
        PreparedStatement ps = null;
        String name = "";
        ResultSet rs = null;
        try (Connection con = GirlkunDB.getConnection()) {
            ps = con.prepareStatement("select * from player");
            rs = ps.executeQuery();
            while (rs.next()) {
                int plHp = 200000000;
                int plMp = 200000000;
                player = new Player();
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");
                //data kim lượng
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_inventory"));
                player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
                player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));
                try {
                    player.inventory.skMedusa = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    player.inventory.event = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    player.inventory.eventSanMa = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.inventory.event2T9 = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    player.inventory.eventTrungThu = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    player.inventory.null4 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    player.inventory.null5 = Integer.parseInt(String.valueOf(dataArray.get(9)));
                } catch (Exception e) {
                }
                dataArray.clear();

                //data chỉ số
                dataArray = (JSONArray) JSONValue.parse(rs.getString("data_point"));
                player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                player.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                player.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                player.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                player.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                player.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));
                dataArray.get(10); //** Năng động
                plHp = Integer.parseInt(String.valueOf(dataArray.get(11)));
                plMp = Integer.parseInt(String.valueOf(dataArray.get(12)));
                dataArray.clear();

                //data body
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));

                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    Util.useCheckDo(player, item, "body");
                    player.inventory.itemsBody.add(item);
                    if (item.template.id == 457) {
                        thoi_vang += item.quantity;
                    }
                }
                dataArray.clear();

                //data bag
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_bag"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    Util.useCheckDo(player, item, "bag");
                    if (item.template.id == 457) {
                        thoi_vang += item.quantity;
                    }
                    player.inventory.itemsBag.add(item);
                }
                dataArray.clear();

                //data box
                dataArray = (JSONArray) JSONValue.parse(rs.getString("items_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) JSONValue.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) JSONValue.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) JSONValue.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    Util.useCheckDo(player, item, "box");
                    if (item.template.id == 457) {
                        thoi_vang += item.quantity;
                    }
                    player.inventory.itemsBox.add(item);
                }
                dataArray.clear();

                //data tài xỉu
                dataArray = (JSONArray) JSONValue.parse(rs.getString("Tai_xiu"));
                player.taixiu.hotong = Integer.parseInt(String.valueOf(dataArray.get(0)));
                player.taixiu.chuyensinh = Byte.parseByte(String.valueOf(dataArray.get(1)));
                player.taixiu.toptaixiu = Long.parseLong(String.valueOf(dataArray.get(2)));
                if (dataArray.size() == 4) {
                    player.taixiu.win = Integer.parseInt(String.valueOf(dataArray.get(3)));
                } else {
                    player.taixiu.win = 0;
                }
                if (dataArray.size() == 5) {
                    player.taixiu.bongtai = Integer.parseInt(String.valueOf(dataArray.get(4)));
                } else {
                    player.taixiu.bongtai = 0;
                }
                player.taixiu.MaxGoldTradeDay = Long.parseLong(String.valueOf(dataArray.get(5)));
                dataArray.clear();

                if (thoi_vang > x) {
                    Logger.error("play:" + player.name);
                    Logger.error("thoi_vang:" + thoi_vang);
                }

                JSONObject achievementObject = (JSONObject) JSONValue.parse(rs.getString("info_achievement"));
                player.achievement.numPvpWin = Integer.parseInt(String.valueOf(achievementObject.get("numPvpWin")));
                player.achievement.numSkillChuong = Integer.parseInt(String.valueOf(achievementObject.get("numSkillChuong")));
                player.achievement.numFly = Integer.parseInt(String.valueOf(achievementObject.get("numFly")));
                player.achievement.numKillMobFly = Integer.parseInt(String.valueOf(achievementObject.get("numKillMobFly")));
                player.achievement.numKillNguoiRom = Integer.parseInt(String.valueOf(achievementObject.get("numKillNguoiRom")));
                player.achievement.numHourOnline = Long.parseLong(String.valueOf(achievementObject.get("numHourOnline")));
                player.achievement.numGivePea = Integer.parseInt(String.valueOf(achievementObject.get("numGivePea")));
                player.achievement.numSellItem = Integer.parseInt(String.valueOf(achievementObject.get("numSellItem")));
                player.achievement.numPayMoney = Integer.parseInt(String.valueOf(achievementObject.get("numPayMoney")));
                player.achievement.numKillSieuQuai = Integer.parseInt(String.valueOf(achievementObject.get("numKillSieuQuai")));
                player.achievement.numHoiSinh = Integer.parseInt(String.valueOf(achievementObject.get("numHoiSinh")));
                player.achievement.numSkillDacBiet = Integer.parseInt(String.valueOf(achievementObject.get("numSkillDacBiet")));
                player.achievement.numPickGem = Integer.parseInt(String.valueOf(achievementObject.get("numPickGem")));

                dataArray = (JSONArray) JSONValue.parse(String.valueOf(achievementObject.get("listReceiveGem")));
                for (Byte i = 0; i < dataArray.size(); i++) {
                    player.achievement.listReceiveGem.add((Boolean) dataArray.get(i));
                }

                ////data nhiệm vụ chiến thần
                dataArray = (JSONArray) JSONValue.parse(rs.getString("nhiemvu_chienthan"));
                player.chienthan.tasknow = Integer.parseInt(String.valueOf(dataArray.get(0)));
                player.chienthan.dalamduoc = Integer.parseInt(String.valueOf(dataArray.get(1)));
                player.chienthan.maxcount = Integer.parseInt(String.valueOf(dataArray.get(2)));
                if (dataArray.size() == 4) {
                    player.chienthan.maxtask = Integer.parseInt(String.valueOf(dataArray.get(3)));
                } else {
                    player.chienthan.maxtask = 0;
                }
                if (dataArray.size() == 5) {
                    player.chienthan.donechienthan = Integer.parseInt(String.valueOf(dataArray.get(4)));
                } else {
                    player.chienthan.donechienthan = 0;
                }
                dataArray.clear();

            }

        } catch (Exception e) {
            System.out.println(name);
            System.out.println("rrr");
            Logger.logException(Manager.class, e, "Lỗi load database");
        }
    }

    public static Player loadById(int id) {
        Player player = null;
        GirlkunResultSet rs = null;
        if (Client.gI().getPlayer(id) != null) {
            player = Client.gI().getPlayer(id);
            return player;
        }
        try {
            rs = GirlkunDB.executeQuery("select * from player where id = ? limit 1", id);
            if (rs.first()) {
                int plHp = 200000000;
                int plMp = 200000000;
                JSONValue jv = new JSONValue();
                JSONArray dataArray = null;

                player = new Player();

                //base info
                player.id = rs.getInt("id");
                player.name = rs.getString("name");
                player.head = rs.getShort("head");
                player.gender = rs.getByte("gender");
                player.haveTennisSpaceShip = rs.getBoolean("have_tennis_space_ship");

                int clanId = rs.getInt("clan_id_sv" + Manager.SERVER);
                if (clanId != -1) {
                    Clan clan = ClanService.gI().getClanById(clanId);
                    for (ClanMember cm : clan.getMembers()) {
                        if (cm.id == player.id) {
                            clan.addMemberOnline(player);
                            player.clan = clan;
                            player.clanMember = cm;
                            break;
                        }
                    }
                }

                //data kim lượng
                dataArray = (JSONArray) jv.parse(rs.getString("data_inventory"));
                player.inventory.gold = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.inventory.gem = Integer.parseInt(String.valueOf(dataArray.get(1)));
                player.inventory.ruby = Integer.parseInt(String.valueOf(dataArray.get(2)));
                dataArray.clear();

                //data tọa độ
                try {
                    dataArray = (JSONArray) jv.parse(rs.getString("data_location"));
                    int mapId = Integer.parseInt(String.valueOf(dataArray.get(0)));
                    player.location.x = Integer.parseInt(String.valueOf(dataArray.get(1)));
                    player.location.y = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    if (MapService.gI().isMapDoanhTrai(mapId) || MapService.gI().isMapBlackBallWar(mapId)
                            || MapService.gI().isMapBanDoKhoBau(mapId)
                            || MapService.gI().isMapKhiGas(mapId)) {
                        mapId = player.gender + 21;
                        player.location.x = 300;
                        player.location.y = 336;
                    }
                    player.zone = MapService.gI().getMapCanJoin(player, mapId, -1);
                } catch (Exception e) {
                    System.out.println("ttt");
                }
                dataArray.clear();

                //data chỉ số
                dataArray = (JSONArray) jv.parse(rs.getString("data_point"));
                player.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                player.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                player.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                player.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                player.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                player.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                player.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                player.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                player.nPoint.critg = Byte.parseByte(String.valueOf(dataArray.get(9)));
                dataArray.get(10); //** Năng động
                plHp = Integer.parseInt(String.valueOf(dataArray.get(11)));
                plMp = Integer.parseInt(String.valueOf(dataArray.get(12)));
                dataArray.clear();

                //data đậu thần
                dataArray = (JSONArray) jv.parse(rs.getString("data_magic_tree"));
                byte level = Byte.parseByte(String.valueOf(dataArray.get(0)));
                byte currPea = Byte.parseByte(String.valueOf(dataArray.get(1)));
                boolean isUpgrade = Byte.parseByte(String.valueOf(dataArray.get(2))) == 1;
                long lastTimeHarvest = Long.parseLong(String.valueOf(dataArray.get(3)));
                long lastTimeUpgrade = Long.parseLong(String.valueOf(dataArray.get(4)));
                player.magicTree = new MagicTree(player, level, currPea, lastTimeHarvest, isUpgrade, lastTimeUpgrade);
                dataArray.clear();

                //data phần thưởng sao đen
                dataArray = (JSONArray) jv.parse(rs.getString("data_black_ball"));
                JSONArray dataBlackBall = null;
                for (int i = 0; i < dataArray.size(); i++) {
                    dataBlackBall = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                    player.rewardBlackBall.timeOutOfDateReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(0)));
                    player.rewardBlackBall.lastTimeGetReward[i] = Long.parseLong(String.valueOf(dataBlackBall.get(1)));
                    dataBlackBall.clear();
                }
                dataArray.clear();

                //data body
                dataArray = (JSONArray) jv.parse(rs.getString("items_body"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBody.add(item);
                }
                dataArray.clear();

                //data bag
                dataArray = (JSONArray) jv.parse(rs.getString("items_bag"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBag.add(item);
                }
                dataArray.clear();

                //data box
                dataArray = (JSONArray) jv.parse(rs.getString("items_box"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                        if (ItemService.gI().isOutOfDateTime(item)) {
                            item = ItemService.gI().createItemNull();
                        }
                    } else {
                        item = ItemService.gI().createItemNull();
                    }
                    player.inventory.itemsBox.add(item);
                }
                dataArray.clear();

                //data box lucky round
                dataArray = (JSONArray) jv.parse(rs.getString("items_box_lucky_round"));
                for (int i = 0; i < dataArray.size(); i++) {
                    Item item = null;
                    JSONArray dataItem = (JSONArray) jv.parse(dataArray.get(i).toString());
                    short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                    if (tempId != -1) {
                        item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                        JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                        for (int j = 0; j < options.size(); j++) {
                            JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                            item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                    Integer.parseInt(String.valueOf(opt.get(1)))));
                        }
                        player.inventory.itemsBoxCrackBall.add(item);
                    }
                }
                dataArray.clear();

                //data friends
                dataArray = (JSONArray) jv.parse(rs.getString("friends"));
                if (dataArray != null) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray dataFE = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        Friend friend = new Friend();
                        friend.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                        friend.name = String.valueOf(dataFE.get(1));
                        friend.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                        friend.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                        friend.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                        friend.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                        friend.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                        player.friends.add(friend);
                        dataFE.clear();
                    }
                    dataArray.clear();
                }

                //data enemies
                dataArray = (JSONArray) jv.parse(rs.getString("enemies"));
                if (dataArray != null) {
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray dataFE = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        Enemy enemy = new Enemy();
                        enemy.id = Integer.parseInt(String.valueOf(dataFE.get(0)));
                        enemy.name = String.valueOf(dataFE.get(1));
                        enemy.head = Short.parseShort(String.valueOf(dataFE.get(2)));
                        enemy.body = Short.parseShort(String.valueOf(dataFE.get(3)));
                        enemy.leg = Short.parseShort(String.valueOf(dataFE.get(4)));
                        enemy.bag = Byte.parseByte(String.valueOf(dataFE.get(5)));
                        enemy.power = Long.parseLong(String.valueOf(dataFE.get(6)));
                        player.enemies.add(enemy);
                        dataFE.clear();
                    }
                    dataArray.clear();
                }

                //data nội tại
                dataArray = (JSONArray) jv.parse(rs.getString("data_intrinsic"));
                byte intrinsicId = Byte.parseByte(String.valueOf(dataArray.get(0)));
                player.playerIntrinsic.intrinsic = IntrinsicService.gI().getIntrinsicById(intrinsicId);
                player.playerIntrinsic.intrinsic.param1 = Short.parseShort(String.valueOf(dataArray.get(1)));
                player.playerIntrinsic.intrinsic.param2 = Short.parseShort(String.valueOf(dataArray.get(2)));
                player.playerIntrinsic.countOpen = Byte.parseByte(String.valueOf(dataArray.get(3)));
                dataArray.clear();

                //data item time
                dataArray = (JSONArray) jv.parse(rs.getString("data_item_time"));
                int timeBoHuyet = Integer.parseInt(String.valueOf(dataArray.get(0)));
                int timeBoKhi = Integer.parseInt(String.valueOf(dataArray.get(1)));
                int timeGiapXen = Integer.parseInt(String.valueOf(dataArray.get(2)));
                int timeCuongNo = Integer.parseInt(String.valueOf(dataArray.get(3)));
                int timeAnDanh = Integer.parseInt(String.valueOf(dataArray.get(4)));
                int timeBiNgo = Integer.parseInt(String.valueOf(dataArray.get(5)));
                int timeMayDo = Integer.parseInt(String.valueOf(dataArray.get(6)));
                int timeDuoi = Integer.parseInt(String.valueOf(dataArray.get(7)));
                int iconDuoi = Integer.parseInt(String.valueOf(dataArray.get(8)));
                int timeMayDo2 = Integer.parseInt(String.valueOf(dataArray.get(10)));

                player.itemTime.lastTimeBoHuyet = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoHuyet);
                player.itemTime.lastTimeBoKhi = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeBoKhi);
                player.itemTime.lastTimeGiapXen = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeGiapXen);
                player.itemTime.lastTimeCuongNo = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeCuongNo);
                player.itemTime.lastTimeAnDanh = System.currentTimeMillis() - (ItemTime.TIME_ITEM - timeAnDanh);
                player.itemTime.lastTimeBiNgo = System.currentTimeMillis() - (ItemTime.TIME_BI_NGO - timeBiNgo);
                player.itemTime.lastTimeUseMayDo = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO - timeMayDo);
                player.itemTime.lastTimeDuoikhi = System.currentTimeMillis() - (ItemTime.TIME_DUOI_KHI - timeDuoi);
                player.itemTime.iconDuoi = iconDuoi;
                player.itemTime.lastTimeUseMayDo2 = System.currentTimeMillis() - (ItemTime.TIME_MAY_DO2 - timeMayDo2);
                player.itemTime.isUseBoHuyet = timeBoHuyet != 0;
                player.itemTime.isUseBoKhi = timeBoKhi != 0;
                player.itemTime.isUseGiapXen = timeGiapXen != 0;
                player.itemTime.isUseCuongNo = timeCuongNo != 0;
                player.itemTime.isUseAnDanh = timeAnDanh != 0;
                player.itemTime.isBiNgo = timeBiNgo != 0;
                player.itemTime.isUseMayDo = timeMayDo != 0;
                player.itemTime.isDuoikhi = timeDuoi != 0;
                player.itemTime.isUseMayDo2 = timeMayDo2 != 0;
                dataArray.clear();

                //data item time
                dataArray = (JSONArray) jv.parse(rs.getString("data_item_time_sieucap"));
                int timeBoHuyet3 = Integer.parseInt(String.valueOf(dataArray.get(0)));
                int timeBoKhi3 = Integer.parseInt(String.valueOf(dataArray.get(1)));
                int timeGiapXen3 = Integer.parseInt(String.valueOf(dataArray.get(2)));
                int timeCuongNo3 = Integer.parseInt(String.valueOf(dataArray.get(3)));
                int timeAnDanh3 = Integer.parseInt(String.valueOf(dataArray.get(4)));
                int timeKeo = Integer.parseInt(String.valueOf(dataArray.get(5)));
                int timeXiMuoi = Integer.parseInt(String.valueOf(dataArray.get(6)));
                int timeDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(7)));
                int iconDuoi3 = Integer.parseInt(String.valueOf(dataArray.get(8)));
                int iconBanh = Integer.parseInt(String.valueOf(dataArray.get(9)));
                int timeBanh = Integer.parseInt(String.valueOf(dataArray.get(10)));
                int timeBienhinh = Integer.parseInt(String.valueOf(dataArray.get(11)));
                int timeBienhinh1 = Integer.parseInt(String.valueOf(dataArray.get(12)));
                int timeSieuCap = Integer.parseInt(String.valueOf(dataArray.get(13)));

                player.itemTimesieucap.lastTimeBoHuyet3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBoHuyet3);
                player.itemTimesieucap.lastTimeBoKhi3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBoKhi3);
                player.itemTimesieucap.lastTimeGiapXen3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeGiapXen3);
                player.itemTimesieucap.lastTimeCuongNo3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeCuongNo3);
                player.itemTimesieucap.lastTimeAnDanh3 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeAnDanh3);
                player.itemTimesieucap.lastTimeKeo = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_KEO - timeKeo);
                player.itemTimesieucap.lastTimeUseXiMuoi = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_XI_MUOI - timeXiMuoi);
                player.itemTimesieucap.lastTimeMeal = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_EAT_MEAL - timeDuoi3);
                player.itemTimesieucap.iconMeal = iconDuoi3;
                player.itemTimesieucap.iconBanh = iconBanh;
                player.itemTimesieucap.lastTimeUseBanh = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_TRUNGTHU - timeBanh);
                player.itemTimesieucap.lastTimeBienhinh = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBienhinh);
                player.itemTimesieucap.lastTimeBienhinh1 = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM3 - timeBienhinh1);
                player.itemTimesieucap.lastTimeRongSieuCap = System.currentTimeMillis() - (ItemTimeSieuCap.TIME_ITEM_SC_30P - timeSieuCap);
                player.itemTimesieucap.isUseBoHuyet3 = timeBoHuyet3 != 0;
                player.itemTimesieucap.isUseBoKhi3 = timeBoKhi3 != 0;
                player.itemTimesieucap.isUseGiapXen3 = timeGiapXen3 != 0;
                player.itemTimesieucap.isUseCuongNo3 = timeCuongNo3 != 0;
                player.itemTimesieucap.isUseAnDanh3 = timeAnDanh3 != 0;
                player.itemTimesieucap.isKeo = timeKeo != 0;
                player.itemTimesieucap.isUseXiMuoi = timeXiMuoi != 0;
                player.itemTimesieucap.isEatMeal = timeDuoi3 != 0;
                player.itemTimesieucap.isUseTrungThu = timeBanh != 0;
                player.itemTimesieucap.isBienhinh = timeBienhinh != 0;
                player.itemTimesieucap.isBienhinh1 = timeBienhinh1 != 0;
                player.itemTimesieucap.isRongSieuCap = timeSieuCap != 0;
                dataArray.clear();

                //data nhiệm vụ
                dataArray = (JSONArray) jv.parse(rs.getString("data_task"));
                TaskMain taskMain = TaskService.gI().getTaskMainById(player, Byte.parseByte(String.valueOf(dataArray.get(0))));
                taskMain.index = Byte.parseByte(String.valueOf(dataArray.get(1)));
                taskMain.subTasks.get(taskMain.index).count = Short.parseShort(String.valueOf(dataArray.get(2)));
                player.playerTask.taskMain = taskMain;
                dataArray.clear();

                //data nhiệm vụ hàng ngày
                dataArray = (JSONArray) jv.parse(rs.getString("data_side_task"));
                String format = "dd-MM-yyyy";
                long receivedTime = Long.parseLong(String.valueOf(dataArray.get(1)));
                Date date = new Date(receivedTime);
                if (TimeUtil.formatTime(date, format).equals(TimeUtil.formatTime(new Date(), format))) {
                    player.playerTask.sideTask.template = TaskService.gI().getSideTaskTemplateById(Integer.parseInt(String.valueOf(dataArray.get(0))));
                    player.playerTask.sideTask.count = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    player.playerTask.sideTask.maxCount = Integer.parseInt(String.valueOf(dataArray.get(3)));
                    player.playerTask.sideTask.leftTask = Integer.parseInt(String.valueOf(dataArray.get(4)));
                    player.playerTask.sideTask.level = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    player.playerTask.sideTask.receivedTime = receivedTime;
                }

                //data trứng bư
                dataArray = (JSONArray) jv.parse(rs.getString("data_mabu_egg"));
                if (dataArray.size() != 0) {
                    player.mabuEgg = new MabuEgg(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                            Long.parseLong(String.valueOf(dataArray.get(1))));
                }
                dataArray.clear();
                //data trứng bư
                dataArray = (JSONArray) jv.parse(rs.getString("data_dua"));
                if (dataArray.size() != 0) {
                    player.timedua = new Timedua(player, Long.parseLong(String.valueOf(dataArray.get(0))),
                            Long.parseLong(String.valueOf(dataArray.get(1))));
                }
                dataArray.clear();

                //data bùa
                dataArray = (JSONArray) jv.parse(rs.getString("data_charm"));
                player.charms.tdTriTue = Long.parseLong(String.valueOf(dataArray.get(0)));
                player.charms.tdManhMe = Long.parseLong(String.valueOf(dataArray.get(1)));
                player.charms.tdDaTrau = Long.parseLong(String.valueOf(dataArray.get(2)));
                player.charms.tdOaiHung = Long.parseLong(String.valueOf(dataArray.get(3)));
                player.charms.tdBatTu = Long.parseLong(String.valueOf(dataArray.get(4)));
                player.charms.tdDeoDai = Long.parseLong(String.valueOf(dataArray.get(5)));
                player.charms.tdThuHut = Long.parseLong(String.valueOf(dataArray.get(6)));
                player.charms.tdDeTu = Long.parseLong(String.valueOf(dataArray.get(7)));
                player.charms.tdTriTue3 = Long.parseLong(String.valueOf(dataArray.get(8)));
                player.charms.tdTriTue4 = Long.parseLong(String.valueOf(dataArray.get(9)));
                dataArray.clear();

                //data skill
                dataArray = (JSONArray) jv.parse(rs.getString("skills"));
                for (int i = 0; i < dataArray.size(); i++) {
                    JSONArray dataSkill = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                    int tempId = Integer.parseInt(String.valueOf(dataSkill.get(0)));
                    byte point = Byte.parseByte(String.valueOf(dataSkill.get(1)));
                    Skill skill = null;
                    if (point != 0) {
                        skill = SkillUtil.createSkill(tempId, point);
                    } else {
                        skill = SkillUtil.createSkillLevel0(tempId);
                    }
                    skill.lastTimeUseThisSkill = Long.parseLong(String.valueOf(dataSkill.get(2)));
                    player.playerSkill.skills.add(skill);
                }
                dataArray.clear();

                //data skill shortcut
                dataArray = (JSONArray) jv.parse(rs.getString("skills_shortcut"));
                for (int i = 0; i < dataArray.size(); i++) {
                    player.playerSkill.skillShortCut[i] = Byte.parseByte(String.valueOf(dataArray.get(i)));
                }
                for (int i : player.playerSkill.skillShortCut) {
                    if (player.playerSkill.getSkillbyId(i) != null && player.playerSkill.getSkillbyId(i).damage > 0) {
                        player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(i);
                        break;
                    }
                }
                if (player.playerSkill.skillSelect == null) {
                    player.playerSkill.skillSelect = player.playerSkill.getSkillbyId(player.gender == ConstPlayer.TRAI_DAT
                            ? Skill.DRAGON : (player.gender == ConstPlayer.NAMEC ? Skill.DEMON : Skill.GALICK));
                }
                dataArray.clear();

                //data pet
                JSONArray petData = (JSONArray) jv.parse(rs.getString("pet"));
                if (!petData.isEmpty()) {
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(0)));
                    Pet pet = new Pet(player);
                    pet.id = -player.id;
                    pet.typePet = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    pet.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                    pet.name = String.valueOf(dataArray.get(2));
                    player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                    player.fusion.lastTimeFusion = System.currentTimeMillis()
                            - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                    pet.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

                    //data chỉ số
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(1)));
                    pet.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    pet.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                    pet.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                    pet.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                    pet.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                    pet.nPoint.hpg = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    pet.nPoint.mpg = Integer.parseInt(String.valueOf(dataArray.get(6)));
                    pet.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    pet.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    pet.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                    int hp = Integer.parseInt(String.valueOf(dataArray.get(10)));
                    int mp = Integer.parseInt(String.valueOf(dataArray.get(11)));

                    //data body
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(2)));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        JSONArray dataItem = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                            JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();
                            ;
                        }
                        pet.inventory.itemsBody.add(item);
                    }

                    //data skills
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(3)));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                        byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        switch (skill.template.id) {
                            case Skill.KAMEJOKO:
                            case Skill.MASENKO:
                            case Skill.ANTOMIC:
                                skill.coolDown = 1000;
                                break;
                        }
                        pet.playerSkill.skills.add(skill);
                    }
                    pet.nPoint.hp = hp;
                    pet.nPoint.mp = mp;
                    player.pet = pet;
                }

                //data pet dao nu
                JSONArray petDaoLuData = (JSONArray) jv.parse(rs.getString("Dao_Lu"));
                if (petDaoLuData != null && !petDaoLuData.isEmpty()) {
                    dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(0)));
                    DaoLu petDaoLu = new DaoLu(player);
                    petDaoLu.id = -player.id * 111111;
                    petDaoLu.typeDaoLu = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    petDaoLu.gender = Byte.parseByte(String.valueOf(dataArray.get(1)));
                    petDaoLu.nameDaoLu = String.valueOf(dataArray.get(2));
                    petDaoLu.name = "$[" + petDaoLu.getTypeString() + "] " + petDaoLu.nameDaoLu;
                    player.fusion.typeFusion = Byte.parseByte(String.valueOf(dataArray.get(3)));
                    player.fusion.lastTimeFusion = System.currentTimeMillis()
                            - (Fusion.TIME_FUSION - Integer.parseInt(String.valueOf(dataArray.get(4))));
                    petDaoLu.status = Byte.parseByte(String.valueOf(dataArray.get(5)));

                    //Ndq viết thêm mở rộng
                    try {
                        petDaoLu.pointTuVi = Integer.parseInt(String.valueOf(dataArray.get(6)));
                        petDaoLu.pointCapTinh = Integer.parseInt(String.valueOf(dataArray.get(7)));
                        petDaoLu.pointCapCanhGioi = Integer.parseInt(String.valueOf(dataArray.get(8)));
                        petDaoLu.timeThangCap = Long.parseLong(String.valueOf(dataArray.get(9)));
                        petDaoLu.var2 = Integer.parseInt(String.valueOf(dataArray.get(10)));
                    } catch (Exception e) {
                    }

                    //data chỉ số
                    dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(1)));
                    petDaoLu.nPoint.limitPower = Byte.parseByte(String.valueOf(dataArray.get(0)));
                    petDaoLu.nPoint.power = Long.parseLong(String.valueOf(dataArray.get(1)));
                    petDaoLu.nPoint.tiemNang = Long.parseLong(String.valueOf(dataArray.get(2)));
                    petDaoLu.nPoint.stamina = Short.parseShort(String.valueOf(dataArray.get(3)));
                    petDaoLu.nPoint.maxStamina = Short.parseShort(String.valueOf(dataArray.get(4)));
                    petDaoLu.nPoint.hpg = Double.parseDouble(String.valueOf(dataArray.get(5)));
                    petDaoLu.nPoint.mpg = Double.parseDouble(String.valueOf(dataArray.get(6)));
                    petDaoLu.nPoint.dameg = Integer.parseInt(String.valueOf(dataArray.get(7)));
                    petDaoLu.nPoint.defg = Integer.parseInt(String.valueOf(dataArray.get(8)));
                    petDaoLu.nPoint.critg = Integer.parseInt(String.valueOf(dataArray.get(9)));
                    double hp = Double.parseDouble(String.valueOf(dataArray.get(10)));
                    double mp = Double.parseDouble(String.valueOf(dataArray.get(11)));

                    //data body
                    dataArray = (JSONArray) jv.parse(String.valueOf(petDaoLuData.get(2)));
                    for (int i = 0; i < dataArray.size(); i++) {
                        Item item = null;
                        JSONArray dataItem = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        short tempId = Short.parseShort(String.valueOf(dataItem.get(0)));
                        if (tempId != -1) {
                            item = ItemService.gI().createNewItem(tempId, Integer.parseInt(String.valueOf(dataItem.get(1))));
                            JSONArray options = (JSONArray) jv.parse(String.valueOf(dataItem.get(2)).replaceAll("\"", ""));
                            for (int j = 0; j < options.size(); j++) {
                                JSONArray opt = (JSONArray) jv.parse(String.valueOf(options.get(j)));
                                item.itemOptions.add(new Item.ItemOption(Integer.parseInt(String.valueOf(opt.get(0))),
                                        Integer.parseInt(String.valueOf(opt.get(1)))));
                            }
                            item.createTime = Long.parseLong(String.valueOf(dataItem.get(3)));
                            if (ItemService.gI().isOutOfDateTime(item)) {
                                item = ItemService.gI().createItemNull();
                            }
                        } else {
                            item = ItemService.gI().createItemNull();

                        }
                        petDaoLu.inventory.itemsBody.add(item);
                    }

                    //data skills
                    dataArray = (JSONArray) jv.parse(String.valueOf(petData.get(3)));
                    for (int i = 0; i < dataArray.size(); i++) {
                        JSONArray skillTemp = (JSONArray) jv.parse(String.valueOf(dataArray.get(i)));
                        int tempId = Integer.parseInt(String.valueOf(skillTemp.get(0)));
                        byte point = Byte.parseByte(String.valueOf(skillTemp.get(1)));
                        Skill skill = null;
                        if (point != 0) {
                            skill = SkillUtil.createSkill(tempId, point);
                        } else {
                            skill = SkillUtil.createSkillLevel0(tempId);
                        }
                        switch (skill.template.id) {
                            case Skill.KAMEJOKO:
                            case Skill.MASENKO:
                            case Skill.ANTOMIC:
                                skill.coolDown = 1000;
                                break;
                        }
                        petDaoLu.playerSkill.skills.add(skill);
                    }

                    petDaoLu.nPoint.hp = hp;
                    petDaoLu.nPoint.mp = mp;
                    player.petDaoLu = petDaoLu;
                }

                JSONObject achievementObject = (JSONObject) JSONValue.parse(rs.getString("info_achievement"));
                player.achievement.numPvpWin = Integer.parseInt(String.valueOf(achievementObject.get("numPvpWin")));
                player.achievement.numSkillChuong = Integer.parseInt(String.valueOf(achievementObject.get("numSkillChuong")));
                player.achievement.numFly = Integer.parseInt(String.valueOf(achievementObject.get("numFly")));
                player.achievement.numKillMobFly = Integer.parseInt(String.valueOf(achievementObject.get("numKillMobFly")));
                player.achievement.numKillNguoiRom = Integer.parseInt(String.valueOf(achievementObject.get("numKillNguoiRom")));
                player.achievement.numHourOnline = Long.parseLong(String.valueOf(achievementObject.get("numHourOnline")));
                player.achievement.numGivePea = Integer.parseInt(String.valueOf(achievementObject.get("numGivePea")));
                player.achievement.numSellItem = Integer.parseInt(String.valueOf(achievementObject.get("numSellItem")));
                player.achievement.numPayMoney = Integer.parseInt(String.valueOf(achievementObject.get("numPayMoney")));
                player.achievement.numKillSieuQuai = Integer.parseInt(String.valueOf(achievementObject.get("numKillSieuQuai")));
                player.achievement.numHoiSinh = Integer.parseInt(String.valueOf(achievementObject.get("numHoiSinh")));
                player.achievement.numSkillDacBiet = Integer.parseInt(String.valueOf(achievementObject.get("numSkillDacBiet")));
                player.achievement.numPickGem = Integer.parseInt(String.valueOf(achievementObject.get("numPickGem")));

                dataArray = (JSONArray) JSONValue.parse(String.valueOf(achievementObject.get("listReceiveGem")));
                for (Byte i = 0; i < dataArray.size(); i++) {
                    player.achievement.listReceiveGem.add((Boolean) dataArray.get(i));
                }

                dataArray = (JSONArray) JSONValue.parse(rs.getString("Thu_TrieuHoi"));
                player.TrieuHoiCapBac = Integer.parseInt(String.valueOf(dataArray.get(0)));
                if (player.TrieuHoiCapBac >= 0 && player.TrieuHoiCapBac <= 10) {
                    player.TenThuTrieuHoi = String.valueOf(dataArray.get(1));
                    player.TrieuHoiThucAn = Integer.parseInt(String.valueOf(dataArray.get(2)));
                    player.TrieuHoiDame = Long.parseLong(String.valueOf(dataArray.get(3)));
                    player.TrieuHoilastTimeThucan = Long.parseLong(String.valueOf(dataArray.get(4)));
                    player.TrieuHoiLevel = Integer.parseInt(String.valueOf(dataArray.get(5)));
                    if (player.TrieuHoiLevel > 100) {
                        player.TrieuHoiLevel = 100;
                    }
                    player.TrieuHoiExpThanThu = Long.parseLong(String.valueOf(dataArray.get(6)));
                    player.TrieuHoiHP = Long.parseLong(String.valueOf(dataArray.get(7)));
                } else {
                    player.TrieuHoiCapBac = -1;
                }
                dataArray.clear();

                player.nPoint.hp = plHp;
                player.nPoint.mp = plMp;
                player.iDMark.setLoadedAllDataPlayer(true);
            }
        } catch (Exception e) {
            System.out.println("yyy");
            player.dispose();
            player = null;
            Logger.logException(GodGK.class, e);
        } finally {
            if (rs != null) {
                rs.dispose();
            }
        }
        return player;
    }
}
