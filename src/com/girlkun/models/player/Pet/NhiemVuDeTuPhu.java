/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.Pet;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;

public class NhiemVuDeTuPhu {
    public String mota;
    public int currentCount;
    public int totalCount;
    // type 0 is tim kiem item

    public int targetId;

    public TaskType type;
    public boolean isDone;

    public String tenNhiemVu;

    public NhiemVuDeTuPhu(String tenNhiemVu, String mota, int totalCount, TaskType type, int targetId) {
        this.mota = mota;
        this.tenNhiemVu = tenNhiemVu;
        this.targetId = targetId;
        this.totalCount = totalCount;
        this.type = type;
    }

    public void checkDoneTask() {
        currentCount += 1;
    }

    public boolean isDoneTask() {
        return currentCount >= totalCount;
    }

    public void showBaseMenu(Player player, Npc npc) {
        String stringBuilder = "|7|Nhiệm Vụ Hiện Tại" + "\n" +
                "|5|" + tenNhiemVu + "\n" +
                "|1|" + mota.replaceAll("#", String.valueOf(totalCount)) + "\n" +
                "|2|Tiến độ : " + Util.powerToString(currentCount) + "/" + Util.powerToString(totalCount) + "\n" +
                "|7|Trạng thái :" + (isDoneTask() ? "Hoàn thành" : "Chưa hoàn thành") + "\n";
        npc.createOtherMenu(player, ConstNpc.MENHU_CT_NHIEM_VU, stringBuilder, "Giao Nhiệm\nVụ", "Đóng");
    }
}
