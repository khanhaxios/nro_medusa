package com.girlkun.services.func;

import java.util.HashMap;
import java.util.Map;
import com.girlkun.models.item.Item;
import com.girlkun.consts.ConstNpc;
import com.girlkun.models.map.Zone;
import com.girlkun.services.NpcService;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.network.io.Message;
import com.girlkun.services.ItemService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemTimeService;
import com.girlkun.utils.Logger;
import java.util.List;

/**
 *
 * @Stole By Hoàng Việt💖
 *
 */
public class SummonSieuCap {

    public static final byte WISHED = 0;
    public static final byte TIME_UP = 1;

    public static final byte DRAGON_SUPER = 0;

    public static final short NGOC_RONG_SIEU_CAP = 1015;

    public static final String RONG_SUPER_TUTORIAL
            = "Gọi rồng từ Ngọc rồng Siêu cấp\n"
            + "Quá 5 phút nếu không ước Rồng Siêu cấp sẽ bay mất";
    public static final String SUPER_SAY
            = "Ta sẽ ban cho người 1 điều ước, ngươi có 5 phút, hãy suy nghĩ thật kỹ trước khi quyết định"
            + "\n 1) Cải trang Gohan Siêu Nhân (Chỉ số ngẫu nhiên 60 Ngày) "
            + "\n 2) Cải trang Biden Siêu Nhân (Chỉ số ngẫu nhiên 60 Ngày) "
            + "\n 3) Cải trang Cô nương Siêu Nhân (Chỉ số ngẫu nhiên 60 Ngày) "
            + "\n 4) Pet Thỏ Ốm (Chỉ số ngẫu nhiên 60 Ngày) "
            + "\n 5) Pet Thỏ Mập (Chỉ số ngẫu nhiên 60 Ngày) "
            + "\n 6) Tăng 50% HP,KI,SD trong 30 Phút ";

    public static final String[] SUPER_1_STAR_WISHES_1
            = new String[]{"Điều\nước 1", "Điều\nước 2", "Điều\nước 3", "Điều\nước 4", "Điều\nước 5", "Điều\nước 6"};
    //--------------------------------------------------------------------------
    private static SummonSieuCap instance;
    private final Map pl_dragonStar;
    public long lastTimeSieuCapAppeared;
    private long lastTimeSieuCapWait;
    public final int timeReSieuCap = 600000;
    public boolean isSieuCapAppear = false;
    private final int timeSieuCapWait = 150000;

    private final Thread update;
    private boolean active;

    public boolean isPlayerDisconnect;
    public Player playerSieuCap;
    private int playerSieuCapId;
    public Zone mapSieuCapAppear;
    private byte SieuCapStar;
    private int menuSieuCap;
    private byte select;

    private SummonSieuCap() {
        this.pl_dragonStar = new HashMap<>();
        this.update = new Thread(() -> {
            while (active) {
                try {
                    if (isSieuCapAppear == true) {
                        if (isPlayerDisconnect) {
                            List<Player> players = mapSieuCapAppear.getPlayers();
                            for (Player plMap : players) {
                                if (plMap.id == playerSieuCapId) {
                                    playerSieuCap = plMap;
                                    reSummonSieuCap();
                                    isPlayerDisconnect = false;
                                    break;
                                }
                            }
                        }
                        if (Util.canDoWithTime(lastTimeSieuCapWait, timeSieuCapWait)) {
                            SieuCapLeave(playerSieuCap, TIME_UP);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Logger.logException(SummonSieuCap.class, e);
                }
            }
        });
        this.active();
    }

    private void active() {
        if (!active) {
            active = true;
            this.update.start();
        }
    }

    public static SummonSieuCap gI() {
        if (instance == null) {
            instance = new SummonSieuCap();
        }
        return instance;
    }

    public void openMenuSieuCap(Player pl, byte dragonBallStar) {
        this.pl_dragonStar.put(pl, dragonBallStar);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.RONG_SUPER, -1, "Bạn muốn gọi Rồng Siêu Cấp ?",
                "Hướng\ndẫn thêm\n(mới)", "Gọi\nRồng Super");
    }

    public synchronized void summonSieuCap(Player pl) {
        if (pl.zone.map.mapId == 5) {
            if (isSieuCapAppear == true) {
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
                return;
            }
            Item ngocrong = null;
            try {
                ngocrong = InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_SIEU_CAP);
            } catch (Exception ex) {
                // Ignore exception
            }
            if (ngocrong == null || ngocrong.quantity < 1){
                Service.getInstance().sendThongBao(pl, "Cần 1 viên Ngọc rồng Siêu cấp");
                return;
            }
            long currentTimeMillis = System.currentTimeMillis();
            long timeSinceLastSieuCapAppeared = currentTimeMillis - lastTimeSieuCapAppeared;
            long timeLeftUntilResummon = timeReSieuCap - timeSinceLastSieuCapAppeared;

            if (timeSinceLastSieuCapAppeared < timeReSieuCap) {
                int timeLeftInSeconds = (int) (timeLeftUntilResummon / 1000);
                String timeLeftString = (timeLeftInSeconds < 7200) ? (timeLeftInSeconds + " giây") : ((timeLeftInSeconds / 60) + " phút");
                Service.getInstance().sendThongBao(pl, "Vui lòng đợi " + timeLeftString + " để gọi rồng");
                return;
            }

            // SummonSieuCap
            isSieuCapAppear = true;
            playerSieuCap = pl;
            playerSieuCapId = (int) pl.id;
            mapSieuCapAppear = pl.zone;
            try {
                InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, NGOC_RONG_SIEU_CAP), 1);//trừ 1v ngoc rồng
            } catch (Exception ex) {
                // Ignore exception
            }
            InventoryServiceNew.gI().sendItemBags(pl);
            sendNotifySieuCapAppear();
            activeSieuCap(pl, true);
            sendWhishesSieuCap(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Chỉ được gọi Rồng Siêu Cấp ở Đảo Kame");
        }
    }

    private void sendWhishesSieuCap(Player pl) {
        byte dragonStar;
        try {
            dragonStar = (byte) pl_dragonStar.get(pl);
            this.SieuCapStar = dragonStar;
        } catch (Exception e) {
            dragonStar = this.SieuCapStar;
        }
        switch (dragonStar) {
            case 1:
                NpcService.gI().createMenuSieuCap(pl, ConstNpc.SUPER_1, SUPER_SAY, SUPER_1_STAR_WISHES_1);
                break;
        }
    }

    private void activeSieuCap(Player pl, boolean appear) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(0);
            if (appear) {
                msg.writer().writeShort(pl.zone.map.mapId);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(pl.zone.zoneId);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                msg.writer().writeByte(1);
                lastTimeSieuCapWait = System.currentTimeMillis();
            }
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (Exception e) {
        }
    }

    private void reSummonSieuCap() {
        activeSieuCap(playerSieuCap, true);
        sendWhishesSieuCap(playerSieuCap);
    }

    private void sendNotifySieuCapAppear() {
        Message msg;
        try {
            msg = new Message(-25);
            msg.writer().writeUTF(playerSieuCap.name + " vừa gọi Rồng Siêu Cấp tại "
                    + playerSieuCap.zone.map.mapName + " khu vực " + playerSieuCap.zone.zoneId);
            Service.getInstance().sendMessAllPlayerIgnoreMe(playerSieuCap, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void confirmWish() {
        switch (this.menuSieuCap) {
            case ConstNpc.SUPER_1:
                switch (this.select) {
                    case 0:
                        if (InventoryServiceNew.gI().getCountEmptyBag(playerSieuCap) > 0) {
                            Item ctGohan = ItemService.gI().createNewItem((short) 989);
                            ctGohan.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 250)));
                            ctGohan.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 250)));
                            ctGohan.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 250)));
                            ctGohan.itemOptions.add(new Item.ItemOption(5, Util.nextInt(20, 30)));
                            ctGohan.itemOptions.add(new Item.ItemOption(47, Util.nextInt(5, 15)));

                            ctGohan.itemOptions.add(new Item.ItemOption(93, 60));
                            InventoryServiceNew.gI().addItemBag(playerSieuCap, ctGohan);
                            InventoryServiceNew.gI().sendItemBags(playerSieuCap);
                        } else {
                            Service.getInstance().sendThongBao(playerSieuCap, "Hành trang đã đầy");
                            reOpenSieuCapWishes(playerSieuCap);
                            return;
                        }
                        break;
                    case 1:
                        if (InventoryServiceNew.gI().getCountEmptyBag(playerSieuCap) > 0) {
                            Item ctBiden = ItemService.gI().createNewItem((short) 990);
                            ctBiden.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 250)));
                            ctBiden.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 250)));
                            ctBiden.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 250)));
                            ctBiden.itemOptions.add(new Item.ItemOption(5, Util.nextInt(20, 30)));
                            ctBiden.itemOptions.add(new Item.ItemOption(47, Util.nextInt(5, 15)));

                            ctBiden.itemOptions.add(new Item.ItemOption(93, 60));
                            InventoryServiceNew.gI().addItemBag(playerSieuCap, ctBiden);
                            InventoryServiceNew.gI().sendItemBags(playerSieuCap);
                        } else {
                            Service.getInstance().sendThongBao(playerSieuCap, "Hành trang đã đầy");
                            reOpenSieuCapWishes(playerSieuCap);
                            return;
                        }
                        break;
                    case 2:
                        if (InventoryServiceNew.gI().getCountEmptyBag(playerSieuCap) > 0) {
                            Item ctConuong = ItemService.gI().createNewItem((short) 991);
                            ctConuong.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 250)));
                            ctConuong.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 250)));
                            ctConuong.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 250)));
                            ctConuong.itemOptions.add(new Item.ItemOption(5, Util.nextInt(20, 30)));
                            ctConuong.itemOptions.add(new Item.ItemOption(47, Util.nextInt(5, 15)));

                            ctConuong.itemOptions.add(new Item.ItemOption(93, 60));
                            InventoryServiceNew.gI().addItemBag(playerSieuCap, ctConuong);
                            InventoryServiceNew.gI().sendItemBags(playerSieuCap);
                        } else {
                            Service.getInstance().sendThongBao(playerSieuCap, "Hành trang đã đầy");
                            reOpenSieuCapWishes(playerSieuCap);
                            return;
                        }
                        break;
                    case 3:
                        if (InventoryServiceNew.gI().getCountEmptyBag(playerSieuCap) > 0) {
                            Item PetThoOm = ItemService.gI().createNewItem((short) 1039);
                            PetThoOm.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 250)));
                            PetThoOm.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 250)));
                            PetThoOm.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 250)));
                            PetThoOm.itemOptions.add(new Item.ItemOption(93, 60));
                            InventoryServiceNew.gI().addItemBag(playerSieuCap, PetThoOm);
                            InventoryServiceNew.gI().sendItemBags(playerSieuCap);
                        } else {
                            Service.getInstance().sendThongBao(playerSieuCap, "Hành trang đã đầy");
                            reOpenSieuCapWishes(playerSieuCap);
                            return;
                        }
                        break;
                    case 4:
                        if (InventoryServiceNew.gI().getCountEmptyBag(playerSieuCap) > 0) {
                            Item PetThoMap = ItemService.gI().createNewItem((short) 1040);
                            PetThoMap.itemOptions.add(new Item.ItemOption(50, Util.nextInt(5, 250)));
                            PetThoMap.itemOptions.add(new Item.ItemOption(77, Util.nextInt(5, 250)));
                            PetThoMap.itemOptions.add(new Item.ItemOption(103, Util.nextInt(5, 250)));
                            PetThoMap.itemOptions.add(new Item.ItemOption(93, 60));
                            InventoryServiceNew.gI().addItemBag(playerSieuCap, PetThoMap);
                            InventoryServiceNew.gI().sendItemBags(playerSieuCap);
                        } else {
                            Service.getInstance().sendThongBao(playerSieuCap, "Hành trang đã đầy");
                            reOpenSieuCapWishes(playerSieuCap);
                            return;
                        }
                        break;
                    case 5: //50% HP,KI,SD 30 phút
                        this.playerSieuCap.itemTimesieucap.lastTimeRongSieuCap = System.currentTimeMillis();
                        this.playerSieuCap.itemTimesieucap.isRongSieuCap = true;
                        Service.getInstance().point(this.playerSieuCap);
                        ItemTimeService.gI().sendAllItemTime(this.playerSieuCap);
                        break;
                }
                break;
        }
        SieuCapLeave(this.playerSieuCap, WISHED);
    }

    public void showConfirmSieuCap(Player pl, int menu, byte select) {
        this.menuSieuCap = menu;
        this.select = select;
        String wish = null;
        switch (menu) {
            case ConstNpc.SUPER_1:
                wish = SUPER_1_STAR_WISHES_1[select];
                break;
        }
        NpcService.gI().createMenuSieuCap(pl, ConstNpc.SUPER_CONFIRM, "Ngươi có chắc muốn ước?", wish, "Từ chối");
    }

    public void reOpenSieuCapWishes(Player pl) {
        switch (menuSieuCap) {
            case ConstNpc.SUPER_1:
                NpcService.gI().createMenuSieuCap(pl, ConstNpc.SUPER_1, SUPER_SAY, SUPER_1_STAR_WISHES_1);
                break;
        }
    }

    public void SieuCapLeave(Player pl, byte type) {
        if (type == WISHED) {
            NpcService.gI().createTutorial(pl, -1, "Điều ước của ngươi đã trở thành sự thật\nHẹn gặp ngươi lần sau, ta đi ngủ đây, bái bai");
        } else {
            NpcService.gI().createMenuSieuCap(pl, ConstNpc.IGNORE_MENU, "Ta buồn ngủ quá rồi\nHẹn gặp ngươi lần sau, ta đi đây, bái bai");
        }
        SieuCapBienMat(pl, false);
        this.isSieuCapAppear = false;
        this.menuSieuCap = -1;
        this.select = -1;
        this.playerSieuCap = null;
        this.playerSieuCapId = -1;
        this.SieuCapStar = -1;
        this.mapSieuCapAppear = null;
        lastTimeSieuCapAppeared = System.currentTimeMillis();
    }

    public void SieuCapBienMat(Player pl, boolean appear) {
        Message msg;
        try {
            msg = new Message(-83);
            msg.writer().writeByte(1);
            if (appear) {
                msg.writer().writeShort(pl.zone.map.mapId);
                msg.writer().writeShort(pl.zone.map.bgId);
                msg.writer().writeByte(pl.zone.zoneId);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeUTF("");
                msg.writer().writeShort(pl.location.x);
                msg.writer().writeShort(pl.location.y);
                msg.writer().writeByte(0);
            }
            Service.getInstance().sendMessAllPlayer(msg);
        } catch (Exception e) {
        }
    }

    //--------------------------------------------------------------------------
}
