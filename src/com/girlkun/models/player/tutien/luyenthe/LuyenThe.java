package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BasePoint;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.server.Manager;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class LuyenThe extends BasePoint implements IBaseAction {

    public final byte MAX_LEVEL = 99;

    public byte timeThatBai = 0;

    public LuyenThe(Player player) {
        super(player);
    }

    public void calcPoint() {
        player.nPoint.mpAdd += (player.nPoint.mpg * getHPMPBuff() / 100f);
        player.nPoint.hpAdd += (player.nPoint.defg * getDefBuff() / 100f);
        player.nPoint.dameAdd += (player.nPoint.dameg * getDameBuff() / 100f);
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHPMPBuff();
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        return ((long) level * Util.nextInt(1, 3)) * targetMob.level;
    }

    @Override
    public void levelUp() {
        if (canLevelUp()) {
            level += 1;
            exp = 0;
            maxExp = getNextLevelExp();
            timeThatBai = 0;
            Service.gI().point(player);
        }
    }

    public void addExp(long pp) {
        exp += pp;
        if (exp > maxExp) {
            exp = maxExp;
        }
    }

    @Override
    public void restExp() {
        exp = 0;
        maxExp = getNextLevelExp();
    }

    @Override
    public void levelDown() {
        if (level > 1) {
            level--;
            exp = 0;
            maxExp = getNextLevelExp();
            Service.gI().point(player);
        }
    }

    @Override
    public void resetLevel() {
        level = 1;
        exp = 0;
        maxExp = getNextLevelExp();
        Service.gI().point(player);
    }

    @Override
    protected long getNextLevelExp() {
        return level * 100;
    }

    @Override
    public float getLevelUpPercent() {
        if (exp == 0) return 0;
        return ((exp / (maxExp * 1f) * 100) / (level * 3)) + (timeThatBai * 3);
    }

    @Override
    public void openSystem() {
        levelUp();
        Service.gI().sendThongBao(player, "Đã học luyện thể");
    }

    @Override
    public boolean canLevelUp() {
        return level < MAX_LEVEL && exp == maxExp;
    }

    @Override
    public String getName() {
        return "Luyện Thể Tầng " + level;
    }

    @Override
    public String getCurrentExpAsString() {
        return exp + "/" + Util.powerToString(maxExp) + " (" + String.format("%s", exp / maxExp * 100) + "%)";
    }

    @Override
    public float getDameBuff() {
        return Math.max(1, level) * 2f;
    }

    @Override
    public float getHPMPBuff() {
        return Math.max(1, level) * 3f;
    }

    @Override
    public float getDefBuff() {
        return Math.max(1, level) * 1f;
    }

    @Override
    public float getPSTBuff() {
        return Math.max(1, level) * .1f;
    }

    @Override
    public float getHutHPBuff() {
        return Math.max(1, level) * .1f;
    }

    @Override
    public float getHutMPBuff() {
        return Math.max(1, level) * .1f;
    }

    @Override
    public float getNeBuff() {
        return Math.max(1, level) * .1f;
    }

    @Override
    public float getChinhXacBuff() {
        return Math.max(1, level) * .1f;
    }

    public boolean isLuyenThe() {
        return level > 0;
    }

    public void showInfo() {
        if (!isLuyenThe()) {
            Service.gI().sendThongBaoOK(player, "Bạn chưa mở luyện thể");
        }
        String text = "|7|Luyện Thể\n|5|Cấp bậc : " + getName() + "\n" + "Tu Vi : " + getCurrentExpAsString() + "\n" + "Tổng Thuộc tính buff : " + totalBuff() + "\n" + "Tỷ lệ đột phá : " + String.format("%.2f%%", getLevelUpPercent()) + "\n" + "|7|Cấp càng cao tỷ lệ đột phá càng thấp";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_THE, -1, text, "Đột phá", "Đóng");
    }

    private String totalBuff() {
        return String.format("%.2f%%", getHPMPBuff() + getDameBuff() + getNeBuff() + getChinhXacBuff() + getDefBuff());
    }

    public String getItemNeed(short[] idsItemNeed) {
        StringBuilder needStr = new StringBuilder();
        for (short i : idsItemNeed) {
            needStr.append("x").append((level + 1) * 10).append(Manager.ITEM_TEMPLATES.get(i).name);
            if (i != idsItemNeed[idsItemNeed.length - 1]) {
                needStr.append(",");
            }
        }
        return needStr.toString();
    }
}
