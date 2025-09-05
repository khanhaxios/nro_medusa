/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

public class CongPhapOption {
    public int id;
    public String name;
    public String param;

    public String getName() {
        return name.replace("#", String.valueOf(param));
    }

    public CongPhapOption() {
    }

    public CongPhapOption(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public CongPhapOption(int id, String name, String param) {
        this.id = id;
        this.name = name;
        this.param = param;
    }
}
