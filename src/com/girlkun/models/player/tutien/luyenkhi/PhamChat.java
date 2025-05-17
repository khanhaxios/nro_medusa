package com.girlkun.models.player.tutien.luyenkhi;

public enum PhamChat {
    HOANG(0, "Hoàng", 20000, 100),
    HUYEN(1, "Huyền", 30000, 1000),
    DIA(2, "Địa", 50000, 10000),
    THIEN(3, "Thiên", 60000, 20000),
    TIEN(4, "Tiên", 70000, 30000),
    QUAN(5, "Quân", 120000, 50000),
    VUONG(6, "Vương", 150000, 70000),
    DE(7, "Đế", 200000, 100000);

    public final int id;
    public final String name;
    public final int maxHutHpMp;
    public final int maxHutDame;

    PhamChat(int id, String name, int maxHutHpMp, int maxHutDame) {
        this.id = id;
        this.name = name;
        this.maxHutHpMp = maxHutHpMp;
        this.maxHutDame = maxHutDame;
    }
    public PhamChat getNext() {
        return fromId(this.id + 1);
    }

    public boolean isMaxLevel() {
        return this == DE;
    }

    public static PhamChat fromId(int id) {
        for (PhamChat pc : values()) {
            if (pc.id == id) return pc;
        }
        return HOANG; // default
    }
}