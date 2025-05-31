package com.girlkun.models.player.tutien.luyenkhisu;

import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class LuyenKhiSu {

    public LuyenKhiSu(Player player) {
        this.linhHoa = new LinhHoa();
        this.player = player;
    }

    private final Player player;
    public static byte MAX_LEVEL = 12;

    private byte level;
    private long exp;
    private long maxExp;
    private LinhHoa linhHoa;

    public String getCurrentExpStr() {
        return String.format("%s/%s", Util.format(exp), Util.format(maxExp));
    }

    public void levelUp(Player player) {
        if (level + 1 > MAX_LEVEL) {
            Service.gI().sendThongBao(player, String.format("Bạn Đã đạt đến %s không thể đột phá", getName()));
        }
        level += 1;
        restExp();
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

    public void init() {
        restExp();
        this.level = (byte) 0;
        this.linhHoa.init();
    }

    public float getPercentUpgradeEquipment(Item item) {
        // ty le thanh cong duoc cong them =
        // lay ra cap cua do
        float successRate = getPercentBounce();
        Item.ItemOption levelOption = null;
        for (Item.ItemOption itemOption : item.itemOptions) {
            if (itemOption.optionTemplate.id == 72) {
                levelOption = itemOption;
            }
        }
        if (levelOption == null) {
            return 100;
        }
        // bat dau tinh luyen khi
        // ty le tru tang theo cap
        int capDu = levelOption.param % 10;
        int capKhongDu = levelOption.param / 10;
        // vd 6
        successRate += getBasePercentOfItemOptionLevel(capDu);
        // tru di cap nua
        successRate += getBouncePercentOfLinhHoa();
        successRate -= getSubBasePercentOfItemOptionLevel(capKhongDu);
        // %cua cap luyen khi cong them
        if (successRate < 0.3f) {
            successRate = .03f;
        }
        return successRate;
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
                return 20f;
            case 4:
                return 15f;
            case 5:
                return 10f;
            case 6:
                return 5f;
            case 7:
                return 3f;
            case 8:
                return 2f;
            case 9:
                return 1f;
            case 10:
                return .5f;
            case 11:
                return .3f;
            case 12:
                return .1f;
            default:
                return 0f;

        }
    }

    public float getSubBasePercentOfItemOptionLevel(int capKhongDu) {
        switch (capKhongDu) {
            case 0:
                return 1f;
            case 1:
                return 3f;
            case 2:
                return 5f;
            case 3:
                return 7f;
            case 4:
                return 9f;
            case 5:
                return 12f;
            case 6:
                return 14f;
            case 7:
                return 16f;
            case 8:
                return 18f;
            case 9:
                return 20f;
            default:
                return 25f;
        }
    }

    public float getBouncePercentOfLinhHoa() {
        switch (linhHoa.getLevel()) {
            case 1:
                return .5f;
            case 2:
                return 1f;
            case 3:
                return 1.5f;
            case 4:
                return 2f;
            case 5:
                return 2.5f;
            case 6:
                return 3f;
            case 7:
                return 4f;
            case 8:
                return 8f;
            case 9:
                return 12f;
            case 10:
                return 15f;
            case 11:
                return 20f;
            case 12:
                return 25f;
            default:
                return 0;
        }
    }

    public float getBasePercentOfItemOptionLevel(int capdu) {
        switch (capdu) {
            case 0:
                return 15f;
            case 1:
                return 12f;
            case 2:
                return 10f;
            case 3:
                return 8f;
            case 4:
                return 6f;
            case 5:
                return 5f;
            case 6:
                return 4f;
            case 7:
                return 3f;
            case 8:
                return 2f;
            case 9:
                return 1f;
            default:
                return 0.5f;
        }
    }

    public float getPercentBounce(int lv) {
        switch (lv) {
            case 1:
                return .5f;
            case 2:
                return 1f;
            case 3:
                return 2f;
            case 4:
                return 3f;
            case 5:
                return 5f;
            case 6:
                return 7f;
            case 7:
                return 9f;
            case 8:
                return 10f;
            case 9:
                return 12f;
            case 10:
                return 15f;
            case 11:
                return 20f;
            case 12:
                return 25f;
            default:
                return 0;
        }
    }

    public float getPercentBounce() {
        switch (level) {
            case 1:
                return 3f;
            case 2:
                return 4f;
            case 3:
                return 6f;
            case 4:
                return 8f;
            case 5:
                return 10f;
            case 6:
                return 12f;
            case 7:
                return 14f;
            case 8:
                return 16f;
            case 9:
                return 18f;
            case 10:
                return 20f;
            case 11:
                return 23f;
            case 12:
                return 25f;
            default:
                return 0;
        }
    }

    public void openLuyenKhiSu() {
        if (!player.haveTuTien || player.taixiu.chuyensinh < 30) {
            Service.gI().sendThongBao(player, "Bạn chưa đủ điều kiện để học luyện khí");
            return;
        }
        this.level = 1;
        this.restExp();
    }

    public String getNextLevelName() {
        byte lvNext = (byte) (this.level + 1);
        if (lvNext > 0 && lvNext < 3) {
            return "Sơ Cấp Luyện Khí Sư (" + lvNext + ")";
        }
        if (lvNext > 3 && lvNext < 6) {
            return "Trung Cấp Luyện Khí Sư (" + lvNext + ")";
        }
        if (lvNext > 6 && lvNext < 9) {
            return "Cao Cấp Luyện Khí Sư (" + lvNext + ")";
        }
        if (lvNext == 10) {
            return "Tiên Cấp Luyện Khí Sư (" + lvNext + ")";
        }
        if (lvNext == 11) {
            return "Thánh Cấp Luyện Khí Sư (" + lvNext + ")";
        }
        if (lvNext == 12) {
            return "Thần Cấp Luyện Khí Sư (" + lvNext + ")";
        }
        return "Không xác định";
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
                return 6_666_66;
            case 7:
                return 99_999_99;
            case 8:
                return 222_222_22L;
            case 9:
                return 666_666_66L;
            case 10:
                return 1_000_000_00L;
            case 11:
                return 2_500_000_00L;
            case 12:
                return 9_999_999_99L;
            default:
                return 0;
        }
    }

    public String getName() {
        if (level > 0 && level <= 3) {
            return "Sơ cấp luyện khí sư";
        }
        if (level > 3 && level <= 6) {
            return "Trung cấp luyện khí sư";
        }
        if (level > 6 && level < 9) {
            return "Cao cấp luyện khí sư";
        }
        if (level == 10) {
            return "Luyện khí tông sư";
        }
        if (level == 11) {
            return "Luyện khí thánh sư";

        }
        if (level == 12) {
            return "Thần cấp luyện khí sư";
        }
        return "Không thể xác định";
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

    public LinhHoa getLinhHoa() {
        return linhHoa;
    }

    public void addExp(long bounceExp) {
        this.exp += bounceExp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    public void setLinhHoa(LinhHoa linhHoa) {
        this.linhHoa = linhHoa;
    }

    public boolean canLevelUp() {
        return (this.exp >= this.maxExp && this.level + 1 <= MAX_LEVEL);
    }

    public boolean isLuyenKhiSu() {
        return level > 0;
    }
}
