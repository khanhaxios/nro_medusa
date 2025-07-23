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
    public CongPhapLuyenThe congPhapLuyenThe;
    public byte timeThatBai = 0;

    public LuyenThe(Player player) {
        this.player = player;
        congPhapLuyenThe = new CongPhapLuyenThe(player);
    }

    public void calcPoint() {
//        player.nPoint.mpAdd += (player.nPoint.mpg * getHPMPBuff() / 100f);
//        player.nPoint.hpAdd += (player.nPoint.defg * getDefBuff() / 100f);
//        player.nPoint.dameAdd += (player.nPoint.dameg * getDameBuff() / 100f);
        if (congPhapLuyenThe.isLearn()) {
            congPhapLuyenThe.calcPoint();
        }
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHPMPBuff();
    }

    public long getExpCanGain(Mob targetMob) {
        long exp = ((long) level * Util.nextInt(1, 3)) * targetMob.level;
        if (player.luyenDanSu.isLuyenDan() && player.luyenDanSu.danDuocEffect.isBuffLt()) {
            exp *= player.luyenDanSu.danDuocEffect.xBuffLt;
        }
        if (congPhapLuyenThe != null && congPhapLuyenThe.isLearn() && congPhapLuyenThe.type == 0) {
            exp *= 50;
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
        float percent = 0;
        if (!isLuyenTheReal()) {
            percent = ((exp / (maxExp * 1f) * 100) / (level / 5f)) + (timeThatBai * 3);
        } else {
            percent = ((exp / (maxExp * 1f) * 100) / (level / 50f)) + (timeThatBai);
        }
        if (congPhapLuyenThe != null && congPhapLuyenThe.isLearn() && congPhapLuyenThe.type == 0) {
            percent *= 5;
        }
        return percent;
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
        StringBuilder text = new StringBuilder();

        text.append("|7|❖═════ LUYỆN THỂ ═════❖\n");

// — Cấp bậc & Tu vi —
        text.append("|5|➤ Cấp bậc     : ").append(getName()).append("\n");
        text.append("|5|➤ Tu vi       : ").append(getCurrentExpAsString()).append("\n");

// — Buff chỉ số —
        text.append("|5|➤ Dame Buff   : ").append(getDameBuff()).append("%\n");
        text.append("|5|➤ HP/MP Buff  : ").append(getHPMPBuff()).append("%\n");

// — Tỷ lệ đột phá —
        text.append("|5|➤ Tỷ lệ đột phá: ").append(String.format("%.2f%%", getLevelUpPercent())).append("\n");

// — Nhắc nhở —
        text.append("|7|✪ Cấp càng cao, tỷ lệ đột phá càng thấp!");

        text.append("\n|7|❖════════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_THE, -1, text.toString(), "Đột phá", "Công Pháp", "Đóng");
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
