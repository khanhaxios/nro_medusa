package com.girlkun.models.player;

import java.util.ArrayList;
import java.util.List;

import com.girlkun.models.item.Item;

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


    public int priceNangChuyenSinh() {
        return 50;
    }

    public int percentNangChuyenSinh() {
        return 100 - (chuyensinh % 100);
    }

    public void dispose() {
    }

}
