/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player;

import com.girlkun.models.item.Item;

import java.util.List;

public class Taixiu {

    public Player player;
    public int hotong;
    public int chuyensinh;
    public long toptaixiu;
    public int win;
    public int bongtai;
    public long MaxGoldTradeDay;

    public Taixiu() {
    }

    public Taixiu(Player player) {
        this.player = player;
    }


    public boolean haveOption(List<Item> l, int index, int id) {
        Item it = l.get(index);
        if (it != null && it.isNotNullItem()) {
            return it.itemOptions.stream().anyMatch(op -> op != null && op.optionTemplate.id == id);
        }
        return false;
    }

    public double addNPointChuyenSinh(double basePoint) {
        return basePoint * 3 / 100;
    }

    public double calcHpChuyenSinh() {
        return player.nPoint.hpg * player.taixiu.chuyensinh * 0.01;
    }

    public double calcMpChuyenSinh() {
        return player.nPoint.mpg * player.taixiu.chuyensinh * 0.01;
    }

    public double calcDameChuyenSinh() {
        return player.nPoint.dameg * player.taixiu.chuyensinh * 0.01;
    }


    public int priceNangChuyenSinh(int level) {
        return level * 5_000;
    }

    public int percentNangChuyenSinh() {
        return Math.abs(100 - (chuyensinh - 3));
    }

    public void dispose() {
    }

}
