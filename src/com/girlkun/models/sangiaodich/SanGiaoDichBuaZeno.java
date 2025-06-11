package com.girlkun.models.sangiaodich;

import com.girlkun.consts.ConstNpc;
import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import lombok.Data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Data
public class SanGiaoDichBuaZeno implements Runnable {
    private static boolean IS_OPEN = true;
    private long lastTimeUpdate;
    public BuaZenoHolder holder;
    private Random random;
    private long lastTimeAddBua = System.currentTimeMillis();

    public List<SanGiaoDichPlayer> players = new ArrayList<>();

    public void dangKyTaiKhoan(Player player) {
        if (getThongTinAccount(player) == null) {
            SanGiaoDichPlayer sanGiaoDichPlayer = new SanGiaoDichPlayer();
            sanGiaoDichPlayer.setPlayer(player);
            sanGiaoDichPlayer.setTotalHold(0);
            sanGiaoDichPlayer.setPlayerId((int) player.id);
            sanGiaoDichPlayer.setLastTimeUpdate(System.currentTimeMillis());
            players.add(sanGiaoDichPlayer);
            insertNewAccount(sanGiaoDichPlayer);
            Service.gI().sendThongBao(player, "Đã mở tài khoản trên sàn giao dịch");
        } else {
            Service.gI().sendThongBaoOK(player, "Bạn đã mở tài khoản trên hệ thống giao dịch của chúng tôi rồi");
        }
    }

    private void insertNewAccount(SanGiaoDichPlayer sanGiaoDichPlayer) {
        try {
            GirlkunDB.executeUpdate("insert into san_giao_dich_player(player_id,total_hold,last_time_update) values(?,?,?)", sanGiaoDichPlayer.getPlayerId(), sanGiaoDichPlayer.getTotalHold(), new Timestamp(sanGiaoDichPlayer.getLastTimeUpdate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllDataSanGiaoDichPlayer() {
        try {
            GirlkunResultSet rs = GirlkunDB.executeQuery("select * from san_giao_dich_player");
            while (rs.next()) {
                SanGiaoDichPlayer sanGiaoDichPlayer = new SanGiaoDichPlayer();
                sanGiaoDichPlayer.setPlayerId(rs.getInt("player_id"));
                sanGiaoDichPlayer.setTotalHold(rs.getInt("total_hold"));
                Timestamp timestamp = rs.getTimestamp("last_time_update");
                sanGiaoDichPlayer.setLastTimeUpdate(timestamp.getTime());
                players.add(sanGiaoDichPlayer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAllPlayer() {
        try {
            for (SanGiaoDichPlayer player : players) {
                GirlkunDB.executeUpdate("update san_giao_dich_player set total_hold=?,last_time_update=? where player_id = ?", player.getTotalHold(), new Timestamp(player.getLastTimeUpdate()), player.getPlayerId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public SanGiaoDichPlayer getThongTinAccount(Player player) {
        for (SanGiaoDichPlayer sanGiaoDichPlayer : players) {
            if (sanGiaoDichPlayer.getPlayerId() == player.id) {
                return sanGiaoDichPlayer;
            }
        }
        return null;
    }

    public void initDataFromDataBase() {
        // load data from db
        try {
            GirlkunResultSet girlkunResultSet = GirlkunDB.executeQuery("select * from san_giao_dich_bua_zeno where id = 1");
            while (girlkunResultSet.next()) {
                holder = new BuaZenoHolder();
                holder.setInStockBuaZeno(girlkunResultSet.getInt("in_stock"));
                holder.setTotalBuaZeno(girlkunResultSet.getInt("total"));
                holder.setPrice(girlkunResultSet.getInt("price"));
                holder.setFinalPrice(girlkunResultSet.getInt("final_price"));
                Timestamp timestamp = girlkunResultSet.getTimestamp("last_time_update");
                holder.setLastTimeUpdate(timestamp.getTime());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (holder == null) {
            initDataFromDataBase();
            loadAllDataSanGiaoDichPlayer();
            Logger.log("Load data sàn giao dịch thành công");
        }
        random = new Random();
        while (IS_OPEN) {
            update();
        }
    }

    public void calcPrice() {
        int basePrice = holder.price;
        float ratio = (float) holder.inStockBuaZeno / holder.totalBuaZeno;
        float finalPrice;
        if (ratio < 1) {
            float multiplier = 1 + ((1 - ratio) * 2); // Giá tăng theo % thiếu hụt x2
            finalPrice = basePrice * multiplier;
        } else {
            float multiplier = 1 - ((ratio - 1) * 1); // Giá giảm nhẹ nếu cung quá dư
            finalPrice = basePrice * Math.max(multiplier, 0.1f); // Không giảm dưới 10% giá gốc
        }
        float marketFluctuation = random.nextFloat(0.95f, 1.05f); // Giá dao động nhẹ
        finalPrice *= marketFluctuation;
        if (holder.finalPrice != Math.round(finalPrice)) {
            holder.finalPrice = Math.round(finalPrice);
            Logger.log("Đã cập nhật giá thị trường : " + holder.finalPrice + " , " + Math.round(finalPrice));
        }
    }

    public float getChangePercent() {
        if (holder != null) {
            return ((holder.finalPrice - holder.price * 1.f) / holder.price) * 100;
        }
        return 0f;
    }

    public void sendNotifyToAll(String notify) {
        for (SanGiaoDichPlayer player : players) {
            Service.gI().sendThongBao(player.getPlayer(), notify);
        }
    }

    public void canAddBuaZeno() {
        if (holder.inStockBuaZeno <= 100 && Util.canDoWithTime(lastTimeAddBua, 6 * 60 * 60 * 1000)) {
            holder.inStockBuaZeno += Util.nextInt(5, 10);
        }
    }

    public void update() {
        if (Util.canDoWithTime(lastTimeUpdate, 60 * 1000) && holder != null) {
            // update data from where
            canAddBuaZeno();
            calcPrice();
            autoSaveDataSanGiaoDich();
            lastTimeUpdate = System.currentTimeMillis();
        }
    }

    public void showInfo(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_INFO_SGD_ZENO, -1, "|7|Sàn giao dịch bùa zeno\n|5|Hiện còn : " + holder.inStockBuaZeno + "\n|5|Tổng đã phát ra :" + holder.totalBuaZeno + "\n|2|Giá hiện tại : " + holder.finalPrice + "\n|5|Giá gốc : " + holder.price + "\n|7|Tỷ lệ dao dộng : " + getChangePercent() + "%", "Tài khoản", "Đóng");
    }

    public void loginPlayer(Player player) {
        for (SanGiaoDichPlayer sanGiaoDichPlayer : players) {
            if (sanGiaoDichPlayer.getPlayerId() == player.id) {
                if (sanGiaoDichPlayer.getPlayer() == null) {
                    sanGiaoDichPlayer.setPlayer(player);
                }
            }
        }
    }

    public void showPlayerInfo(Player player) {
        SanGiaoDichPlayer sanGiaoDichPlayer = getThongTinAccount(player);
        NpcService.gI().createMenuConMeo(player, ConstNpc.MY_ACCOUNT, -1, "|7|Thông tin tài khoản\n|5|Số bùa đang có  : " + sanGiaoDichPlayer.getTotalHold() + " bùa" + "\n|5|Lần cuối cập nhật : " + new Date(sanGiaoDichPlayer.getLastTimeUpdate()).toLocaleString() + "\n" + "|7|Tổng giá trị tài sản của bạn : " + buaZenoToVND(sanGiaoDichPlayer) + "\nBạn muốn?", "Rút bùa", "Nạp bùa", "Lên bùa", "Mua bùa");
    }

    private String buaZenoToVND(SanGiaoDichPlayer sanGiaoDichPlayer) {
        return Util.powerToString(sanGiaoDichPlayer.getTotalHold() * holder.finalPrice) + " VND";
    }

    public void rutBuaPlayer(Player player, int soluong) {
        SanGiaoDichPlayer sanGiaoDichPlayer = getThongTinAccount(player);
        if (sanGiaoDichPlayer != null && sanGiaoDichPlayer.getTotalHold() - soluong >= 0) {
            sanGiaoDichPlayer.setTotalHold(sanGiaoDichPlayer.getTotalHold() - soluong);
            sanGiaoDichPlayer.setLastTimeUpdate(System.currentTimeMillis());
            Item item = ItemService.gI().createNewItem((short) 1378, soluong);
            item.itemOptions.add(new Item.ItemOption(30, 0));
            InventoryServiceNew.gI().addItemBag(player, item);
            InventoryServiceNew.gI().sendItemBags(player);
            Service.gI().sendThongBao(player, "Bạn đã rút x" + soluong + " bùa zeno về hành trang của mình");
        } else {
            Service.gI().sendThongBaoOK(player, "Bùa trong tài khoản không đủ");
        }
    }

    public void napBuaPlayer(Player player, int soluong2) {
        SanGiaoDichPlayer sanGiaoDichPlayer = getThongTinAccount(player);
        Item item = InventoryServiceNew.gI().findItemBag(player, 1378);
        if (item == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy bùa zeno trong hành trang");
            return;
        }
        if (item.quantity < soluong2) {
            Service.gI().sendThongBao(player, "Không đủ số lượng bùa");
            return;
        }
        if (sanGiaoDichPlayer == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy tài khoản");
            return;
        }
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, soluong2);
        sanGiaoDichPlayer.setTotalHold(sanGiaoDichPlayer.getTotalHold() + soluong2);
        sanGiaoDichPlayer.setLastTimeUpdate(System.currentTimeMillis());
        Service.gI().sendThongBao(player, "Bạn đã nạp x" + soluong2 + " bùa zeno vào tài khoản của mình");
    }

    public void lenBuaPlayer(Player player, int soluong3) {
        SanGiaoDichPlayer sanGiaoDichPlayer = getThongTinAccount(player);
        if (sanGiaoDichPlayer == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy tài khoản");
            return;
        }
        if (sanGiaoDichPlayer.getTotalHold() - soluong3 < 0) {
            Service.gI().sendThongBao(player, "Không đủ bùa");
            return;
        }
        sanGiaoDichPlayer.setTotalHold(sanGiaoDichPlayer.getTotalHold() - soluong3);
        holder.inStockBuaZeno += soluong3;
        holder.setLastTimeUpdate(System.currentTimeMillis());
        sendNotifyToAll("Đã có biến động trên sàn giao dịch hãy đi kiểm tra nào");
    }

    public void saveDataHolder() {
        try {
            GirlkunDB.executeUpdate("update san_giao_dich_bua_zeno set total=?,in_stock=?,price=?,final_price=?,last_time_update=? where id = 1", holder.getTotalBuaZeno(), holder.getInStockBuaZeno(), holder.getPrice(), holder.getFinalPrice(), new Timestamp(holder.getLastTimeUpdate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void autoSaveDataSanGiaoDich() {
        if (Util.canDoWithTime(lastTimeUpdate, 10 * 60 * 1000)) {
            saveAllPlayer();
            saveDataHolder();
        }
    }

    public void muaBuaPlayer(Player player, int soluong4) {
        SanGiaoDichPlayer sanGiaoDichPlayer = getThongTinAccount(player);
        if (sanGiaoDichPlayer == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy tài khoản");
            return;
        }
        if (sanGiaoDichPlayer.getTotalHold() / holder.totalBuaZeno * 100 > 2) {
            Service.gI().sendThongBao(player, "Tổng bùa của bạn vượt quá 2% tổng số bùa trên sàn bạn đã bị cấm lệnh mua");
            return;
        }
        if (holder.inStockBuaZeno - soluong4 < 0) {
            Service.gI().sendThongBao(player, "Trên sàn không đủ bùa");
            return;
        }
        holder.inStockBuaZeno -= soluong4;
        holder.setLastTimeUpdate(System.currentTimeMillis());
        sanGiaoDichPlayer.setTotalHold(sanGiaoDichPlayer.getTotalHold() + soluong4);
        Service.gI().sendThongBao(player, "Bạn đã mua x" + soluong4 + " bùa zeno từ sàn giao dịch");
        sendNotifyToAll("Đã có biến động trên sàn giao dịch hãy đi kiểm tra nào");
    }

    @Data
    public static class BuaZenoHolder {
        private int totalBuaZeno;
        private int inStockBuaZeno;
        private int finalPrice;
        private long lastTimeUpdate;
        private int price;
    }
}
