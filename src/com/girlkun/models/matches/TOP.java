/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.matches;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TOP {
private int id_player;
    private long power;
    private long ki;
    private long hp;
    private long sd;
    private byte nv;
    private int sk;
    private int pvp;
    private String info1;
    private String info2;
//    private String name;
//    private byte gender;
//    private long power;
//    private long ki;
//    private long hp;
//    private long sd;
//    private byte nv;
//    private int sk;
//    private int pvp;
}
