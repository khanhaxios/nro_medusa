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
    int[] buffs = new int[]{
            (int) (player.nPoint.hpg * getHPMPBuff() / 100f),
            (int) (player.nPoint.mpg * getHPMPBuff() / 100f),
            (int) (player.nPoint.defg * getDefBuff() / 100f),
            (int) (player.nPoint.dameg * getDameBuff() / 100f),
    };
    public final byte MAX_LEVEL = 99;

    public LuyenThe(Player player) {
        super(player);
    }

    public int calcPoint(byte type) {
        return buffs[type];
//        player.nPoint.hpg += (int) (player.nPoint.hpg * getHPMPBuff() / 100f);
//        player.nPoint.mpg += (int) (player.nPoint.mpg * getHPMPBuff() / 100f);
//        player.nPoint.defg += (int) (player.nPoint.defg * getDefBuff() / 100f);
//        player.nPoint.dameg += (int) (player.nPoint.dameg * getDameBuff() / 100f);
//        player.nPoint.tlchinhxac += getChinhXacBuff();
//        player.nPoint.tlNeDon += getNeBuff();
//        player.nPoint.tlHutMp += getHutMPBuff();
//        player.nPoint.tlHutHp += getHutHPBuff();
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
        return (exp / (maxExp * 1f) * 100) / (level * 2);
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
        return level * 1f;
    }

    @Override
    public float getHPMPBuff() {
        return level * 1.5f;
    }

    @Override
    public float getDefBuff() {
        return level * 1f;
    }

    @Override
    public float getPSTBuff() {
        return level * .1f;
    }

    @Override
    public float getHutHPBuff() {
        return level * .1f;
    }

    @Override
    public float getHutMPBuff() {
        return level * .1f;
    }

    @Override
    public float getNeBuff() {
        return level * .1f;
    }

    @Override
    public float getChinhXacBuff() {
        return level * .1f;
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
        return String.format("%.2f%%",getHPMPBuff() + getDameBuff() + getNeBuff() + getChinhXacBuff() + getDefBuff());
    }

    public String getItemNeed(short[] idsItemNeed) {
        StringBuilder needStr = new StringBuilder();
        for (short i : idsItemNeed) {
            needStr.append("x").append(level * 10).append(Manager.ITEM_TEMPLATES.get(i).name);
            if (i != idsItemNeed[idsItemNeed.length - 1]) {
                needStr.append(",");
            }
        }
        return needStr.toString();
    }
}
