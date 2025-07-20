package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class LuyenThe {
    public short level;
    public long exp;
    public long maxExp;
    Player player;
    public final byte MAX_LEVEL = 99;
    public final short MAX_LEVEL_FINAL = 9999;

    public byte timeThatBai = 0;

    public LuyenThe(Player player) {
        this.player = player;
    }

    public void calcPoint() {
        player.nPoint.mpAdd += (player.nPoint.mpg * getHPMPBuff() / 100f);
        player.nPoint.hpAdd += (player.nPoint.defg * getDefBuff() / 100f);
        player.nPoint.dameAdd += (player.nPoint.dameg * getDameBuff() / 100f);
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHPMPBuff();
    }

    public long getExpCanGain(Mob targetMob) {
        long exp = ((long) level * Util.nextInt(1, 3)) * targetMob.level;
        if (player.luyenDanSu.isLuyenDan() && player.luyenDanSu.danDuocEffect.isBuffLt()) {
            exp *= player.luyenDanSu.danDuocEffect.xBuffLt;
        }
        return exp;
    }

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

    public short getLevel() {
        return level;
    }

    public void restExp() {
        exp = 0;
        maxExp = getNextLevelExp();
    }

    public void levelDown() {
        if (level > 1) {
            level--;
            exp = 0;
            maxExp = getNextLevelExp();
            Service.gI().point(player);
        }
    }

    public void resetLevel() {
        level = 1;
        exp = 0;
        maxExp = getNextLevelExp();
        Service.gI().point(player);
    }

    protected long getNextLevelExp() {
        return level * 1000;
    }

    public float getLevelUpPercent() {
        if (exp == 0) return 0;
        if (isNotLuyenThe()) {
            return ((exp / (maxExp * 1f) * 100) / (level / 3f)) + (timeThatBai * 5);
        } else {
            return ((exp / (maxExp * 1f) * 100) / (level / 50f)) + (timeThatBai);
        }
    }

    public boolean isNotLuyenThe() {
        if (level <= 10) return true;
        return (player.tuTien.isTuTien() || player.tuMa.isTuMa()) && level >= 10;
    }

    public void openSystem() {
        levelUp();
        Service.gI().sendThongBao(player, "Đã học luyện thể");
    }

    public boolean canLevelUp() {
        if (isNotLuyenThe()) {
            return level < MAX_LEVEL;
        }
        return level < MAX_LEVEL_FINAL;
    }

    public String getName() {
        return "Luyện Thể Tầng " + level;
    }

    public String getCurrentExpAsString() {
        return exp + "/" + Util.powerToString(maxExp) + " (" + String.format("%s", exp / maxExp * 100) + "%)";
    }

    public float getDameBuff() {
        if (isNotLuyenThe()) {
            return Math.max(1, level) * 3f;
        } else {
            return Math.max(1, level) * 6;
        }
    }

    public float getHPMPBuff() {
        if (isNotLuyenThe()) {
            return Math.max(1, level) * 6f;
        } else {
            return Math.max(1, level) * 10;
        }
    }

    public float getDefBuff() {
        if (isNotLuyenThe()) {
            return Math.max(1, level) * 1f;
        } else {
            return Math.max(1, level) * 2f;
        }
    }

    public float getPSTBuff() {
        return Math.max(1, level) * .1f;
    }

    public float getHutHPBuff() {
        return Math.max(1, level) * .1f;
    }

    public float getHutMPBuff() {
        return Math.max(1, level) * .1f;
    }

    public float getNeBuff() {
        return Math.max(1, level) * .1f;
    }

    public float getChinhXacBuff() {
        return Math.max(1, level) * .1f;
    }

    public boolean isLuyenThe() {
        return level > 0;
    }

    public boolean isLuyenTheReal() {
        return level > 10 && (!player.tuMa.isTuMa() && !player.tuTien.isTuTien());
    }

    public void showInfo() {
        if (!isLuyenThe()) {
            Service.gI().sendThongBaoOK(player, "Bạn chưa mở luyện thể");
        }
        String text = "|7|Luyện Thể\n|5|Cấp bậc : " + getName() + "\n" + "Tu Vi : " + getCurrentExpAsString() + "\n" + "Dame : " + getDameBuff() + "%" + "\n" + "MPHP : " + getHPMPBuff() + "%" + "\n" + "Tỷ lệ đột phá : " + String.format("%.2f%%", getLevelUpPercent()) + "\n" + "|7|Cấp càng cao tỷ lệ đột phá càng thấp";
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

    public void subExp(long l) {
        exp -= l;
        if (exp < 0) exp = 0;
    }
}
