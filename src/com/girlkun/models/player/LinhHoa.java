package com.girlkun.models.player;

import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class LinhHoa {
    public static byte MAX_LEVEL = 12;

    private byte level;
    private long exp;
    private long maxExp;

    public String getCurrentExpStr() {
        return String.format("%s/%s", Util.format(exp), Util.format(maxExp));
    }

    public void levelUp(Player player) {
        if (level + 1 > MAX_LEVEL) {
            Service.gI().sendThongBao(player, String.format("%s Đã đạt đến cấp lớn nhất không thể đột phá", getName()));
        }
        level += 1;
        restExp();
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(long maxExp) {
        this.maxExp = maxExp;
    }

    public boolean levelUp() {
        level += 1;
        restExp();
        return true;
    }

    public void restExp() {
        this.exp = 0;
        this.maxExp = getNextLevelExp();
    }

    public long getNextLevelExp() {
        switch (level + 1) {
            case 1:
                return 99;
            case 2:
                return 222;
            case 3:
                return 6_666;
            case 4:
                return 99_999;
            case 5:
                return 222_222;
            case 6:
                return 6_666_666;
            case 7:
                return 99_999_999;
            case 8:
                return 222_222_222L;
            case 9:
                return 666_666_666L;
            case 10:
                return 1_000_000_000L;
            case 11:
                return 2_500_000_000L;
            case 12:
                return 9_999_999_999L;
            default:
                return 0;
        }
    }

    public String getName() {
        if (level > 0 && level <= 3) {
            return "Linh hỏa";
        }
        if (level > 3 && level <= 6) {
            return "Viêm Dương Linh Hỏa";
        }
        if (level > 6 && level < 9) {
            return "Cửu Đế Linh Hỏa";
        }
        if (level == 10) {
            return "Tiên Thương Hỏa";
        }
        if (level == 11) {
            return "Thánh Không Hỏa";

        }
        if (level == 12) {
            return "Thần Viêm";
        }
        return "Không thể xác định";
    }

    public void init() {
        this.level = (byte) 0;
        restExp();
    }

    public void addExp(long bounceExp) {
        this.exp += bounceExp;
        if (exp > maxExp) {
            exp = maxExp;
        }
    }

    public boolean canLevelUp() {
        return (this.exp == this.maxExp && this.level + 1 <= MAX_LEVEL);
    }

    public String getNextLevelName() {
        byte lvNext = (byte) (this.level + 1);
        if (lvNext > 0 && lvNext < 3) {
            return getName() + "(" + lvNext + ")";
        }
        if (lvNext > 3 && lvNext < 6) {
            return getName() + "(" + lvNext + ")";
        }
        if (lvNext > 6 && lvNext < 9) {
            return getName() + "(" + lvNext + ")";
        }
        if (lvNext == 10) {
            return getName() + "(" + lvNext + ")";
        }
        if (lvNext == 11) {
            return getName() + "(" + lvNext + ")";
        }
        if (lvNext == 12) {
            return getName() + "(" + lvNext + ")";
        }
        return "Không xác định";
    }

    public float getTyLeDotPha() {
        if (this.level == 0) {
            return 0f;
        }
        switch (this.level) {
            case 1:
                return 100f;
            case 2:
                return 50f;
            case 3:
                return 30f;
            case 4:
                return 25f;
            case 5:
                return 20f;
            case 6:
                return 12f;
            case 7:
                return 10f;
            case 8:
                return 5f;
            case 9:
                return 3f;
            case 10:
                return 1f;
            case 11:
                return .5f;
            case 12:
                return .3f;
            default:
                return 0f;

        }
    }
}
