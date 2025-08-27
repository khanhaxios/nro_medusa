/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.services.func;

import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class TaiXiu implements Runnable {

    public static final long TIME_TAI_XIU = (3) * (60 * 1000);
    public int goldTai;
    public int goldXiu;
    public boolean ketquaTai = false;
    public boolean ketquaXiu = false;
    public boolean ketquaTamhoa = false;

    public static int MIN_HN = 1000;
    public static int MAX_HN = 10_000_000;
    public static int TILE_AN_THUA = 60;

    public boolean baotri = false;
    public long lastTimeEnd;
    public List<Player> PlayersTai = new ArrayList<>();
    public List<Player> PlayersXiu = new ArrayList<>();
    private static TaiXiu instance;
    public int x, y, z;

    public static TaiXiu gI() {
        if (instance == null) {
            instance = new TaiXiu();
        }
        return instance;
    }

    public void addPlayerXiu(Player pl) {
        if (!PlayersXiu.equals(pl)) {
            PlayersXiu.add(pl);
        }
    }

    public void addPlayerTai(Player pl) {
        if (!PlayersTai.equals(pl)) {
            PlayersTai.add(pl);
        }
    }

    public void removePlayerXiu(Player pl) {
        if (PlayersXiu.equals(pl)) {
            PlayersXiu.remove(pl);
        }
    }

    public void removePlayerTai(Player pl) {
        if (PlayersTai.equals(pl)) {
            PlayersTai.remove(pl);
        }
    }

    public void resetTaiXiu() {
        ketquaXiu = false;
        ketquaTai = false;
        ketquaTamhoa = false;
        TaiXiu.gI().goldTai = 0;
        TaiXiu.gI().goldXiu = 0;
        TaiXiu.gI().PlayersTai.clear();
        TaiXiu.gI().PlayersXiu.clear();
        TaiXiu.gI().lastTimeEnd = System.currentTimeMillis() + TIME_TAI_XIU;
    }

    @Override
    public void run() {
        while (true) {
            try {
                long timeLeft = TaiXiu.gI().lastTimeEnd - System.currentTimeMillis();
                if (timeLeft <= 0) {
                    int x, y, z;
                    x = Util.nextInt(1, 6);
                    y = Util.nextInt(1, 6);
                    z = Util.nextInt(1, 6);

                    if (goldTai > 1_000_000 || goldXiu > 1_000_000) {
                        if (Util.isTrue(60, 100)) {
                            x = Util.nextInt(1, 6);
                            z = x;
                            y = z;
                        }
                    }
                    int tong = (x + y + z);
                    if (tong > 3 && tong < 11) {
                        ketquaTamhoa = false;
                        ketquaXiu = true;
                        ketquaTai = false;
                    }
                    if (tong > 10) {
                        ketquaTamhoa = false;
                        ketquaXiu = false;
                        ketquaTai = true;
                    }
                    if (y == z && z == x) {
                        ketquaTamhoa = true;
                        ketquaXiu = false;
                        ketquaTai = false;
                    }

                    if (ketquaTai) {
                        if (!TaiXiu.gI().PlayersTai.isEmpty()) {
                            for (Player pl : PlayersTai) {
                                if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                    int goldC = pl.goldTai + (pl.goldTai / 100 * TILE_AN_THUA);
                                    Service.getInstance().sendThongBao(pl, "Số hệ thống quay ra\n" + x + " : "
                                            + y + " : " + z + "\n|5|Tổng là : " + tong + "\n(TÀI)\n\n|1|Bạn đã chiến thắng!!");
                                    Service.getInstance().sendThongBao(pl, "Chúc mừng bạn đã dành chiến thắng và nhận được " + Util.format(goldC) + " Điểm nạp");
                                    Logger.logTaiXiu(pl, 0, pl.goldTai, goldC);
                                    pl.session.vnd += goldC;
                                    pl.taixiu.win += pl.goldTai * TILE_AN_THUA / 100;
                                    if (Util.isTrue(1, 500)) {
                                        InventoryServiceNew.gI().addItemBag(pl, ItemService.gI().createNewItem(752, 1));
                                        InventoryServiceNew.gI().sendItemBags(pl);
                                        Service.gI().sendThongBao(pl, "Bạn nhận được rương vàng");
                                    } else {
                                        InventoryServiceNew.gI().addItemBag(pl, ItemService.gI().createNewItem(570, 1));
                                        InventoryServiceNew.gI().sendItemBags(pl);
                                        Service.gI().sendThongBao(pl, "Bạn nhận được rương gỗ");
                                    }
                                    Service.getInstance().sendMoney(pl);
                                    InventoryServiceNew.gI().sendItemBags(pl);
                                }
                            }
                        }
                        for (Player pl : PlayersXiu) {
                            if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                Service.getInstance().sendThongBao(pl, "Số hệ thống quay ra\n" + x + " : "
                                        + y + " : " + z + "\n|5|Tổng là : " + tong + "\n(TÀI)\n\n|7|Trắng tay gòi, chơi lại đi!!!");
                                Logger.logTaiXiu(pl, 2, pl.goldXiu, 0);
                            }
                        }
                    } else if (ketquaXiu) {
                        if (!TaiXiu.gI().PlayersXiu.isEmpty()) {
                            for (Player pl : PlayersXiu) {
                                if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                    int goldC = pl.goldXiu + (pl.goldXiu / 100 * TILE_AN_THUA);
                                    Service.getInstance().sendThongBao(pl, "Số hệ thống quay ra\n" + x + " : "
                                            + y + " : " + z + "\n|5|Tổng là : " + tong + "\n(XỈU)\n\n|1|Bạn đã chiến thắng!!");
                                    Service.getInstance().sendThongBao(pl, "Chúc mừng bạn đã dành chiến thắng và nhận được " + Util.format(goldC) + " Điểm nạp");
                                    Logger.logTaiXiu(pl, 1, pl.goldXiu, goldC);
                                    pl.session.vnd += goldC;
                                    pl.taixiu.win += pl.goldXiu * TILE_AN_THUA / 100;
                                    if (Util.isTrue(1, 500)) {
                                        InventoryServiceNew.gI().addItemBag(pl, ItemService.gI().createNewItem(752, 1));
                                        InventoryServiceNew.gI().sendItemBags(pl);
                                        Service.gI().sendThongBao(pl, "Bạn nhận được rương vàng");
                                    } else {
                                        InventoryServiceNew.gI().addItemBag(pl, ItemService.gI().createNewItem(570, 1));
                                        InventoryServiceNew.gI().sendItemBags(pl);
                                        Service.gI().sendThongBao(pl, "Bạn nhận được rương gỗ");
                                    }
                                    Service.getInstance().sendMoney(pl);
                                    InventoryServiceNew.gI().sendItemBags(pl);
                                }
                            }
                        }
                        for (Player pl : PlayersTai) {
                            if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                Service.getInstance().sendThongBao(pl, "Số hệ thống quay ra\n" + x + " : "
                                        + y + " : " + z + "\n|5|Tổng là : " + tong + "\n(XỈU)\n\n|7|Trắng tay gòi, chơi lại đi!!!");
                                Logger.logTaiXiu(pl, 3, pl.goldTai, 0);
                            }
                        }
                    } else {
                        for (Player pl : PlayersTai) {
                            if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                Service.getInstance().sendThongBao(pl, "Số hệ thống quay ra\n" + x + " : "
                                        + y + " : " + z + "\n|5|Tổng là : " + tong + "\n(TAM HOA)\n\n|7|Hahaha Nhà cái lụm hết nha!!!");
                                Logger.logTaiXiu(pl, 4, pl.goldTai, 0);
                            }
                        }
                        for (Player pl : PlayersXiu) {
                            if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                Service.getInstance().sendThongBao(pl, "Số hệ thống quay ra\n" + x + " : "
                                        + y + " : " + z + "\n|5|Tổng là : " + tong + "\n(TAM HOA)\n\n|7|Hahaha Nhà cái lụm hết nha!!!");
                                Logger.logTaiXiu(pl, 5, pl.goldXiu, 0);
                            }
                        }
                    }
                    for (int i = 0; i < TaiXiu.gI().PlayersTai.size(); i++) {
                        Player pl = TaiXiu.gI().PlayersTai.get(i);
                        if (pl != null) {
                            pl.goldTai = 0;
                        }
                    }
                    for (int i = 0; i < TaiXiu.gI().PlayersXiu.size(); i++) {
                        Player pl = TaiXiu.gI().PlayersXiu.get(i);
                        if (pl != null) {
                            pl.goldXiu = 0;
                        }
                    }
                    resetTaiXiu();
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
