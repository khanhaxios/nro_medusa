package com.girlkun.models.player.Pet;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.player.Player;

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
        isDone = currentCount >= totalCount;
    }

    public void showBaseMenu(Player player, Npc npc) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Nhiệm Vụ Hiện Tại").append("\n");
        stringBuilder.append("|5|").append(tenNhiemVu).append("\n");
        stringBuilder.append("|1|").append(mota.replaceAll("#", String.valueOf(totalCount))).append("\n");
        stringBuilder.append("|2|Tiến độ : ").append(currentCount).append("/").append(totalCount).append("\n");
        stringBuilder.append("|7|Trạng thái :").append(isDone ? "Hoàn thành" : "Chưa hoàn thành").append("\n");
        npc.createOtherMenu(player, ConstNpc.MENHU_CT_NHIEM_VU, stringBuilder.toString(), "Giao Nhiệm\nVụ", "Đóng");
    }
}
