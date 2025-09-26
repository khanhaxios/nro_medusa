package com.girlkun.models.player.the_chat;

import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;

public class HoangCoThanhThe extends BaseTheChat {

    public HoangCoThanhThe(Player player) {
        super(player);
        this.type = TheChatType.HOANG_CO_THANH_THE;
        tenTheChat = "Hoang Cổ Thánh Thể";
    }

    @Override
    public long calcMaxExp() {
        return super.calcMaxExp() * 100;
    }

    @Override
    public long calculateExpTayTuy() {
        return super.calculateExpTayTuy() * 10;
    }

    @Override
    public float getPercentGiaiDoan() {
        return 100f;
    }

    public int getBuffOption(int id, int phamChat) {
        switch (id) {
            case 0:
                return Math.max(phamChat, 1) * Util.nextInt(10_000, 200_000) * 2;
            case 1:
                return Math.max(phamChat, 1) * Util.nextInt(20_000, 400_000) * 2;
            case 2:
                return Math.max(phamChat, 1) * Util.nextInt(1, 3) * 2;
            case 3:
                return Math.max(phamChat, 1) * Util.nextInt(1, 2) * 2;
            case 4:
                return Math.max(phamChat, 1) * Util.nextInt(10, 20) * 2;
            case 5:
                return Math.max(phamChat, 1) * Util.nextInt(5, 10) * 2;
        }
        return 0;
    }

    @Override
    public void handleSpecial() {
        super.handleSpecial();

    }

    @Override
    public String getMoTaTheChat() {
        return "|5|Hoang cổ thánh thể đột phá không có bình cảnh tất cả các chỉ số x2 khi thăng giai nhưng đồng thời kinh nghiệm cũng tăng lên";
    }

    public double handleGiamDame(double damage) {
        if (giaiDoan == 2) {
            damage -= damage * (2 * (Math.max(phamChat, 1) + Math.max(giaiDoan, 1))) / 100;
        }
        if (damage <= 0) {
            damage = 1;
        }
        return damage;
    }

    public double handleBuffDame(double dameHit) {
        if (giaiDoan == 3) {
            dameHit += dameHit * (10 * (Math.max(phamChat, 1) + Math.max(giaiDoan, 1))) / 100;
        }
        return dameHit;
    }
}
