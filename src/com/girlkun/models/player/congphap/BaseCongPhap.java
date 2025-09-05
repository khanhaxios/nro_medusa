/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

public abstract class BaseCongPhap {
    public int id;
    public String tenCongPhap;
    public String mota;
    public String thuoctinh;
    public int maxLevel;
    public int maxPham;

    public BaseCongPhap() {
    }

    public BaseCongPhap(int id, String tenCongPhap, String mota, String thuoctinh, int maxLevel, int maxPham) {
        this.id = id;
        this.tenCongPhap = tenCongPhap;
        this.mota = mota;
        this.thuoctinh = thuoctinh;
        this.maxLevel = maxLevel;
        this.maxPham = maxPham;
    }
}
