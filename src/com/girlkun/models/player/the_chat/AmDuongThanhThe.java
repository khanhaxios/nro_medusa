package com.girlkun.models.player.the_chat;

import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;

public class AmDuongThanhThe extends BaseTheChat {
    public AmDuongThanhThe(Player player) {
        super(player);
        this.tenTheChat = "Âm Dương Thánh Thể";
        this.type = TheChatType.AM_DUONG_THANH_THE;
    }

    @Override
    public String getMoTaTheChat() {
        return "|5|Âm Dương Thánh Thể tăng mạnh chỉ số nhưng đồng thời khó đột phá hơn";
    }

    public long lastTimeAddExp;

    @Override
    public void updateTheChat() {
        super.updateTheChat();
        if (Util.canDoWithTime(lastTimeAddExp, 10_000)) {
            addExp((MAX_EXP[giaiDoan] / 10_000) * Math.max(phamChat, 1));
            lastTimeAddExp = System.currentTimeMillis();
        }
    }

    public int getBuffOption(int id, int phamChat) {
        switch (id) {
            case 0:
                return Math.max(phamChat, 1) * Util.nextInt(10_000, 200_000) * 3;
            case 1:
                return Math.max(phamChat, 1) * Util.nextInt(20_000, 400_000) * 3;
            case 2:
                return Math.max(phamChat, 1) * Util.nextInt(1, 3) * 3;
            case 3:
                return Math.max(phamChat, 1) * Util.nextInt(1, 2) * 3;
            case 4:
                return Math.max(phamChat, 1) * Util.nextInt(10, 20) * 3;
            case 5:
                return Math.max(phamChat, 1) * Util.nextInt(5, 10) * 3;
        }
        return 0;
    }

    public double handleSubDame(double damage) {
        if (damage > player.nPoint.hp) {
            damage = 0;
        }
        return damage;
    }
}
