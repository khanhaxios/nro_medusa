package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BaseTuDuy;
import com.girlkun.server.Manager;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class LuyenThe extends BaseTuDuy {
    public short level;
    public long exp;
    public long maxExp;
    public long chanKhi;
    public boolean canLevelUp = true;
    public long maxChanKhi;
    public List<VoKy> voKyList = new ArrayList<>();
    Player player;
    public final byte MAX_LEVEL = 99;
    public final short MAX_LEVEL_FINAL = 9999;
    public CongPhapLuyenThe congPhapLuyenThe;
    public ToiThe toiThe;
    public byte timeThatBai = 0;

    public LuyenThe(Player player) {
        this.player = player;
        congPhapLuyenThe = new CongPhapLuyenThe(player);
        toiThe = new ToiThe(player);
    }

    public void calcPoint() {
        if (congPhapLuyenThe.isLearn()) {
            congPhapLuyenThe.calcPoint();
        }
        if (voKyList.size() > 0) {
            for (VoKy voKy : voKyList) {
                voKy.calcPoint();
            }
        }
        if (toiThe != null && toiThe.isOpen()) {
            toiThe.calcPoint();
        }
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHPMPBuff();
        player.nPoint.xDameLinhCan += tinhThan;
        player.nPoint.tlDameCrit.add(nhanhNhen * 100);
        if (player.nPoint.xDameLinhCan > 0) {
            player.nPoint.tlDameCrit.add(player.nPoint.xDameLinhCan * 2);
        }
    }

    public int getMaxSlVK() {
        if (congPhapLuyenThe.isLearn()) {
            return Math.max(congPhapLuyenThe.tang, 1);
        }
        return 0;
    }

    public long getExpCanGain(Mob targetMob) {
        long exp = ((long) level * Util.nextInt(1, 3)) * targetMob.level;
        if (player.luyenDanSu.isLuyenDan() && player.luyenDanSu.danDuocEffect.isBuffLt()) {
            exp *= player.luyenDanSu.danDuocEffect.xBuffLt;
        }
        if (congPhapLuyenThe != null && congPhapLuyenThe.isLearn() && congPhapLuyenThe.type == 0) {
            exp *= 20;
        }
        if (player.nPoint.xTuVi > 0) {
            exp += exp * player.nPoint.xTuVi / 100;
        }
        return exp;
    }

    public long calcMaxChanKhi() {
        long chanKhi = Math.max(level, 1) * 100_000L;
        if (congPhapLuyenThe.isLearn()) {
            if (congPhapLuyenThe.type == 1) {
                chanKhi *= 5;
            }
        }
        chanKhi += chanKhi * player.nPoint.xLinhKhi / 100;
        return chanKhi;
    }

    public void restChanKhi() {
        this.chanKhi = 0;
        this.maxChanKhi = calcMaxChanKhi();
    }

    public void addChanKhi(long ckk) {
        this.chanKhi += ckk;
        if (this.chanKhi > maxChanKhi) {
            this.chanKhi = maxChanKhi;
        }
    }

    public void levelUp() {
        if (canLevelUp()) {
            restExp();
            level += 1;
            timeThatBai = 0;
            if (level <= 10) {
                canLevelUp = false;
            } else {
                canLevelUp = level % 100 != 0;
            }
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
        return Math.max(level, 1) * 100_000;
    }

    public float getLevelUpPercent() {
        if (exp == 0) return 0;
        float percent = 0;

        if (!isLuyenTheReal()) {
            percent = ((exp / (maxExp * 1f) * 100) / (level / 3f)) + (timeThatBai * 3);
        } else {
            int levelInBlock = level % 100;
            float minRate = 0.3f;
            float maxRate = 25;
            float basePercent = (levelInBlock == 0)
                    ? maxRate
                    : maxRate - ((maxRate - minRate) / 99f) * (levelInBlock - 1);

            percent = (exp / (maxExp * 1f)) * basePercent + timeThatBai;
        }

        if (congPhapLuyenThe != null && congPhapLuyenThe.isLearn() && congPhapLuyenThe.type == 0) {
            percent *= 2.5f;
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
            return (level < MAX_LEVEL) && canLevelUp;
        }
        return (level < MAX_LEVEL_FINAL) && canLevelUp;
    }

    public String getName() {
        return "Luyện Thể Tầng " + level;
    }

    public String getCurrentExpAsString() {
        if (maxExp == 0) {
            maxExp = getNextLevelExp();
        }
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
            return;
        }

        StringBuilder text = new StringBuilder();

        text.append("|7|❖═════ LUYỆN THỂ ═════❖\n");
        text.append("|5|➤ ").append(getName()).append("\n");
        text.append("|5|➤ Tu vi: ").append(getCurrentExpAsString()).append("\n");
        text.append("|5|➤ Chân khí: ").append(getCurrentChanKhiAsString()).append("\n");

        // --- Buff chỉ số ---
        text.append("|5|➤ Dame Buff: ").append(getDameBuff()).append("%\n");
        text.append("|5|➤ HP/MP Buff: ").append(getHPMPBuff()).append("%\n");

        // --- Thuộc tính luyện thể ---
        text.append("|1|➤ Thể Chất: +").append(theChat).append(" Điểm\n");
        text.append("|1|➤ Sức Mạnh: +").append(sucManh).append(" Điểm\n");
        text.append("|1|➤ Tốc Độ: +").append(nhanhNhen).append(" Điểm\n");
        text.append("|1|➤ Tinh Thần: +").append(tinhThan).append(" Điểm\n");

        // --- Tỷ lệ đột phá ---
        text.append("|5|➤ Tỷ lệ đột phá: ").append(String.format("%.2f%%", getLevelUpPercent())).append("\n");

        // --- Nhắc nhở ---
        text.append("|7|✪ Cấp càng cao, tỷ lệ đột phá càng thấp!");
        text.append("\n|7|❖════════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LUYEN_THE, -1, text.toString(),
                "Đột phá", "Công Pháp", "Võ Kỹ", "Tôi Thể", "Đóng");
    }


    private String getCurrentChanKhiAsString() {
        return Util.powerToString(chanKhi) + "/" + Util.powerToString(maxChanKhi);
    }

    public String getItemNeed(short[] idsItemNeed) {
        StringBuilder needStr = new StringBuilder();
        for (short i : idsItemNeed) {
            needStr.append("x").append((level + 1) * 20).append(Manager.ITEM_TEMPLATES.get(i).name);
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

    public boolean canHandleWithChanKhi(int i) {
        return (this.chanKhi - (maxChanKhi * i / 100)) >= 0;
    }

    public String getAllVoKy() {
        StringBuilder vks = new StringBuilder();
        if (voKyList.size() == 0) {
            vks = new StringBuilder("|1|Không có võ kỹ nào");
            return vks.toString();
        }
        for (VoKy voKy : voKyList) {
            vks.append("|5|").append(voKy.tenVoKy).append("[").append(voKy.getDoThuanThucVoKy()).append("]").append("[").append(voKy.getCurrentPercent()).append("]").append("\n");
        }
        return vks.toString();
    }

    public String[] getAllVoKySelect() {
        String[] strings = new String[voKyList.size() + 1];
        if (voKyList.size() == 0) {
            return new String[]{"Đóng"};
        }
        for (int i = 0; i < voKyList.size(); i++) {
            strings[i] = getFirstLetters(voKyList.get(i).tenVoKy);
        }
        strings[voKyList.size()] = "Đóng";
        return strings;
    }

    public String[] getAllVoKySelectt() {
        String[] strings = new String[voKyList.size()];
        for (int i = 0; i < voKyList.size(); i++) {
            strings[i] = getFirstLetters(voKyList.get(i).tenVoKy);
        }
        return strings;
    }

    private String getFirstLetters(String input) {
        String[] words = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(word.charAt(0));
            }
        }
        return sb.toString().toUpperCase(); // Viết hoa cho ngầu
    }

    public void showVoKy() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Túi võ kỹ").append("\n");
        stringBuilder.append(getAllVoKy());
        stringBuilder.append("|7|Bạn muốn").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.VK_SHOW_BASE, -1, stringBuilder.toString(), getAllVoKySelect());
    }

    public void showMenuHocVoKy(VoKy voKy) {
        player.iDMark.vokytamthoi = voKy;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(voKy.getBaseMenuText());
        stringBuilder.append("|7|Bạn muốn");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_HOC_VK, -1, stringBuilder.toString(), "Học", "Bỏ");
    }

    public void hocVoKy(VoKy vokytamthoi) {
        if (voKyList.size() + 1 <= getMaxSlVK()) {
            voKyList.add(vokytamthoi);
            player.iDMark.vokytamthoi = null;
            Service.gI().point(player);
            Service.gI().sendThongBao(player, "Đã học võ kỹ " + vokytamthoi.tenVoKy);
            showInfo();
        } else {
            Service.gI().sendThongBao(player, "Bạn đã đạt số lượng võ kỹ tối đa");
            showMenuHocVoKy(vokytamthoi);
        }
    }

    public void showMenuTangKNVK() {
        StringBuilder stringBuilder = new StringBuilder();
        if (voKyList.size() <= 0) {
            Service.gI().sendThongBao(player, "Bạn không có võ kỹ nào");
            return;
        }
        stringBuilder.append("|7|Thông tin võ kỹ").append("\n");
        stringBuilder.append(getAllVoKy());
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TANG_KNVK, -1, stringBuilder.toString(), getAllVoKySelectt());
    }

    public void reset() {
        this.level = 0;
        restExp();
        restChanKhi();
        congPhapLuyenThe = new CongPhapLuyenThe(player);
        voKyList = new ArrayList<>();
        toiThe = new ToiThe(player);
        Service.gI().sendThongBao(player, "Bạn đã tán công luyện thể");
    }
}
