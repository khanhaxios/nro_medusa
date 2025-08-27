/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.card;

/**
 *
 */
public class OptionCard {

    public int id;
    public int param;
    public byte active;

    public OptionCard(int i, int p, byte a) {
        id = i;
        param = p;
        active = a;
    }

    @Override
    public String toString() {
        final String n = "\"";
        return "{"
                + n + "id" + n + ":" + n + id + n + ","
                + n + "active" + n + ":" + n + active + n + ","
                + n + "param" + n + ":" + n + param + n
                + "}";
    }
}
