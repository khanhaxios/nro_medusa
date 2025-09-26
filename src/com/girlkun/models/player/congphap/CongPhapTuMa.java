/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tuma.TuMa;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;

import static com.girlkun.models.player.congphap.CongPhapLuyenThe.getPhamNam;

public class CongPhapTuMa extends BaseCongPhap {
    public int level;
    public byte heCongPhap;
    public long exp;
    public long maxExp;
    public long expPham;
    public long maxExpPham;
    public byte tier;
    public long lastTimeHoiExp;
    public boolean isKichHoat;
    public double totalDameBuff;
    public double totalHpMpBuff;

    public double totalBuffDameHuyetDan;
    public double totalBuffHpHuyetDan;
    public double totalBuffMpHuyetDan;
    public int totalHuyetDan;

    public double hpBuff; // buff for thon phe
    public double dameBuff;
    public double mpBuff;


    public Player player;
    private static final long[] BASE_MAX_EXP = new long[]{10_000, 50_000, 1_00_000, 200_000, 500_000, 1_000_000, 10_000_000, 20_000_000, 50_000_000, 70_000_000, 100_000_000, 120_000_000, 150_000_000, 250_000_000, 350_000_000, 500_000_000, 700_000_000, 950_000_000};
    public int dlThonPhe;
    public double tongDameThonPhe;
    public double tongHpThonPhe;
    public double tongMpThonPhe;
    public int deTuThonPhe;

    public void addExp(long exp) {
        this.exp += exp;
    }

    public int getMaxHuyetDan() {
        return Math.max(1, tier) * 1_000_000;
    }

    public long calcMaxExp() {
        if (level < 0 || level >= BASE_MAX_EXP.length) {
            return BASE_MAX_EXP[BASE_MAX_EXP.length - 1];
        }
        return BASE_MAX_EXP[level] * 10_000 * Math.max(1, tier);
    }

    public void addExpPham(long exp) {
        this.expPham += exp;
    }

    public long calcMaxExpPham() {
        if (level < 0 || level >= BASE_MAX_EXP.length) {
            return BASE_MAX_EXP[BASE_MAX_EXP.length - 1] * 10;
        }
        return BASE_MAX_EXP[level] * 1000 * Math.max(1, tier);
    }

    public void kickHoat() {
        if (isKichHoat) {
            Service.gI().sendThongBao(player, String.format("Công pháp %s đã được kích hoạt", tenCongPhap));
            return;
        }

        //kich hoat yeu cau ma khi

        if (!player.tuMa.canHandleWithMaKhiPoint(getMaxKhiPointByLevel())) {
            Service.gI().sendThongBao(player, String.format("Bạn cần %s ma khí để kích hoạt công pháp %s", Util.format(getMaxKhiPointByLevel()), tenCongPhap));
            return;
        }
        player.tuMa.subMaKhi(getMaxKhiPointByLevel());
        isKichHoat = true;
        totalDameBuff += getDameBuffByLevel();
        totalHpMpBuff += getHpMpBuffByLevel();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, String.format("Bạn đã kích hoạt công pháp %s thành công", tenCongPhap));
    }

    private long getMaxKhiPointByLevel() {
        return 100_000L * Math.max(1, level);
    }

    private long getHpMpBuffByLevel() {
        return Math.max(1, level) * 200_000L;
    }

    private long getDameBuffByLevel() {
        return Math.max(1, level) * 100_000L;
    }

    public String getPhamName() {
        return getPhamNam(tier);
    }


    public String getTenCongPhap() {
        return String.format("[%s]%s[%s]", getPhamName(), tenCongPhap, TuMa.CANH_GIOI[level]);
    }

    public void levelUp() {
        if (this.exp < maxExp) {
            Service.gI().sendThongBao(player, "Bạn cần %s kinh nghiệm để thăng cấp" + (maxExp - this.exp));
            return;
        }
        if (level + 1 > maxLevel) {
            Service.gI().sendThongBao(player, String.format("Bạn đã đạt đến cấp tối đa của %s không thể thăng cấp", level));
            return;
        }

        if (Util.isTrue(getLevelUpPercent(), 100)) {
            level += 1;
            Service.gI().point(player);
            Service.gI().sendThongBao(player, String.format("Chúc mừng bạn đã thăng cấp %s lên cấp %s", this.tenCongPhap, level));
        } else {
            Service.gI().sendThongBao(player, String.format("Rất tiếc bạn đã thăng cấp %s thất bại, hãy cố gắng hơn nữa", this.tenCongPhap));
        }
        isKichHoat = false;
        restExp();
    }

    public void showMenuThonPhe() {
        String text = "|7|Thôn phệ\n" + "|5|Khi bạn thôn phệ đệ tử hoặc đạo lữ sẽ nhận được 20% chỉ số của họ";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CHON_THON_PHE, -1, text, "Đệ tử", "Đạo lữ");
    }

    public void thonPheDaoLu() {
        // thon phe de tu
        if (player.petDaoLu == null) {
            Service.gI().sendThongBao(player, "Bạn làm đéo gì có đạo lữ mà đòi thôn phệ");
            return;
        }
        tongDameThonPhe += player.petDaoLu.nPoint.dame * 50 / 100;
        tongHpThonPhe += player.petDaoLu.nPoint.hpMax * 50 / 100;
        tongMpThonPhe += player.petDaoLu.nPoint.mpMax * 50 / 100;
        // remove pet

        Service.gI().point(player);
        Service.gI().chatJustForMe(player, player.petDaoLu, "Sao phu quân lại aa...aa...aaaa....");
        EffectSkillService.gI().sendEffectBienhinh(player);
        new Thread(() -> {
            ChangeMapService.gI().exitMap(player.petDaoLu);
            player.petDaoLu.dispose();
            player.petDaoLu = null;
            Service.gI().sendHavePet(player);
        }).start();
        dlThonPhe++;
    }

    public void thonPheDeTu() {
        // thon phe de tu
        if (player.pet == null) {
            Service.gI().sendThongBao(player, "Bạn làm đéo gì có đệ tử mà đòi thôn phệ");
            return;
        }
        double dameAdd = player.pet.nPoint.dame * 20 / 100;
        double hpAdd = player.pet.nPoint.hpMax * 20 / 100;
        double mpAdd = player.pet.nPoint.mpMax * 20 / 100;
        // remove pet
        addDameBuff(dameAdd);
        addHpBuff(hpAdd);
        addMPBuff(mpAdd);
        Service.gI().point(player);
        Service.gI().chatJustForMe(player, player.pet, "Sao sư phụ lại aa...aa...aaaa....");
        EffectSkillService.gI().sendEffectBienhinh(player);
        new Thread(() -> {
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.dispose();
            player.pet = null;
            Service.gI().sendHavePet(player);
        }).start();
        deTuThonPhe++;
    }

    public void addDameBuff(double dameB) {
        this.totalDameBuff += dameB;
        if (dameBuff > totalDameBuff) {
            dameBuff = totalDameBuff;
        }
    }

    public void addHpBuff(double dameB) {
        this.hpBuff += dameB;
    }

    public void addMPBuff(double dameB) {
        this.mpBuff += dameB;
    }


    public void handleHutMaKhi(Mob mob) {
        double baseHut = Math.max(mob.point.maxHp / 1_000_000_0, Util.nextInt(1, 3));
        baseHut *= (tier + 1 + player.tuMa.luyenHon.getMaKhiBuff());
        if (player.nPoint.xHoiLinhKhi > 0) {
            baseHut += baseHut * Math.max(player.nPoint.xLinhKhi, 1) / 100;
        }
        player.tuMa.addMaKhi(Math.max(100, (long) baseHut));
    }

    public void calcPoint() {
        // calc thuoc tinh dac biet
        player.nPoint.dameAdd += totalBuffDameHuyetDan + dameBuff + totalDameBuff + (totalDameBuff * (Math.max(1, level))) * getDameBuff() / 100;
        player.nPoint.hpAdd += totalBuffHpHuyetDan + hpBuff + totalHpMpBuff + (totalHpMpBuff * (Math.max(1, level))) * getHpMpBuff() / 100;
        player.nPoint.mpAdd += totalBuffMpHuyetDan + mpBuff + totalHpMpBuff + (totalHpMpBuff * (Math.max(1, level))) * getHpMpBuff() / 100;
    }

    public void update() {
        if (tenCongPhap != null && Util.canDoWithTime(lastTimeHoiExp, getTimeHoiExp())) {
            addExpPham(getExpCanGain());
            lastTimeHoiExp = System.currentTimeMillis();
        }
    }

    public void doiCongPhap(CongPhapTuMa congPhapTuMa) {
        player.tuMa.congPhapTuMa.dispose();
        player.tuMa.congPhapTuMa = congPhapTuMa;
        player.tuMa.resetLevel();
        Service.gI().sendThongBao(player, String.format("Bạn đã đổi thành công công pháp %s", congPhapTuMa.tenCongPhap));
        Service.gI().point(player);
    }

    private void dispose() {
        this.player = null;
    }

    public void canNuotCongPhap(CongPhapTuMa congPhapTuMa) {
        long expGained = (Math.max(1, congPhapTuMa.level) * BASE_MAX_EXP[congPhapTuMa.level] / 1000) * Math.max(1, tier);
        addExpPham(expGained);
        Service.gI().sendThongBao(player, String.format("Bạn đã nuốt thành công %s và nhận được %s kinh nghiệm", congPhapTuMa.tenCongPhap, Util.format(expGained)));
    }

    public void tangPham() {
        if (expPham < maxExp) {
            Service.gI().sendThongBao(player, "Bạn cần %s kinh nghiệm phẩm để thăng cấp phẩm" + (maxExpPham - expPham));
            return;
        }
        if (tier + 1 > maxPham) {
            Service.gI().sendThongBao(player, String.format("Bạn đã đạt đến phẩm tối đa của %s không thể thăng cấp phẩm", level));
            return;
        }
        if (Util.isTrue(getLevelUpPercent() / 5, 100)) {
            tier += 1;
            Service.gI().point(player);
        }
        restExpPham();
    }

    public float getDameBuff() {
        return (Math.max(1, level) * 12) * Math.max(1, tier);
    }

    public float getHpMpBuff() {
        return (Math.max(1, level) * 20) * Math.max(1, tier);
    }

    private long getTimeHoiExp() {
        long timeHoi = 6_000;
        return timeHoi;
    }

    private long getExpCanGain() {
        long expCanGain = BASE_MAX_EXP[level] / 1000;
        expCanGain += expCanGain * player.nPoint.xHoiLinhKhi / 100;
        expCanGain += expCanGain * player.nPoint.xTuVi / 100;
        return expCanGain;
    }

    public void restExp() {
        this.exp -= maxExp;
        if (this.exp < 0) {
            this.exp = 0;
        }
        this.maxExp = calcMaxExp();
    }

    public void restExpPham() {
        this.expPham -= maxExpPham;
        if (this.expPham < 0) {
            this.expPham = 0;
        }
        this.maxExpPham = calcMaxExpPham();
    }

    public float getLevelUpPercent() {
        return switch (level) {
            case 0 -> 100f;
            case 1 -> 90f;
            case 2 -> 80f;
            case 3 -> 70f;
            case 4 -> 60f;
            case 5 -> 50f;
            case 6 -> 40f;
            case 7 -> 30f;
            case 8 -> 20f;
            case 9 -> 10f;
            case 10 -> 8f;
            case 11 -> 6f;
            case 12 -> 4f;
            case 13 -> 3f;
            case 14 -> 2f;
            case 15 -> 1f;
            case 16 -> 0.5f;
            case 17 -> 0.2f;
            default -> 5;
        };
    }

    public CongPhapTuMa() {
    }

    public CongPhapTuMa(Player player) {
        this.player = player;
    }

    public CongPhapTuMa(int id, String tenCongPhap, String mota, String thuoctinh, int maxLevel, int maxPham) {
        super(id, tenCongPhap, mota, thuoctinh, maxLevel, maxPham);
    }

    public void showBaseMenu() {
        if (tenCongPhap == null) {
            Service.gI().sendThongBao(player, "Bạn chưa học công pháp");
            return;
        }
        StringBuilder menu = new StringBuilder();
        menu.append(String.format("|7|Thông Tin Công Pháp\n"));
        menu.append(String.format("|5|%s", getTenCongPhap()));
        if (isKichHoat) {
            menu.append(String.format("\n|5|Công pháp đã được kích hoạt"));
        } else {
            menu.append(String.format("\n|5|Công pháp chưa được kích hoạt"));
        }
        menu.append(String.format("\n|5|Cấp độ : %s/%s", level, maxLevel));
        menu.append(String.format("\n|5|Phẩm : %s/%s", getPhamName(), maxPham));
        menu.append(String.format("\n|5|Kinh nghiệm : %s/%s", Util.format(exp), Util.format(maxExp)));
        menu.append(String.format("\n|5|Kinh nghiệm phẩm : %s/%s", Util.format(expPham), Util.format(maxExpPham)));
        menu.append(String.format("\n|5|Tổng sát thương cộng dồn : %s", Util.format(dameBuff + totalBuffDameHuyetDan + totalDameBuff + (totalDameBuff * (Math.max(1, level))) * getDameBuff() / 100)));
        menu.append(String.format("\n|5|Tổng HP cộng dồn : %s", Util.format(hpBuff + totalBuffHpHuyetDan + totalHpMpBuff + (totalHpMpBuff * (Math.max(1, level))) * getHpMpBuff() / 100)));
        menu.append(String.format("\n|5|Tổng MP cộng dồn : %s", Util.format(mpBuff + totalBuffMpHuyetDan + totalHpMpBuff + (totalHpMpBuff * (Math.max(1, level))) * getHpMpBuff() / 100)));
        menu.append(String.format("\n|5|Tỷ lệ thăng cấp : %s%%", getLevelUpPercent()));
        menu.append(String.format("\n|5|%s", mota));
        String[] options = new String[]{
                "Kích hoạt công pháp",
                "Thăng cấp",
                "Thăng phẩm",
                "Thôn phệ",
                "Huyết đan",
        };
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONG_PHAP_TU_MA, -1, menu.toString(), options);
    }

    public void showMenuKichHoat() {
        String text = "|7|Kích hoạt công pháp\n" +
                "|5|Khi kích hoạt công pháp sẽ tiêu hao một lượng ma khí nhất định và nhận được chỉ số cộng dồn từ công pháp";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_KICH_HOAT_CONG_PHAP_TU_MA, -1, text, "Kích hoạt", "Đóng");
    }

    public void showMenuThangCap() {
        String text = "|7|Thăng cấp công pháp\n" +
                "|5|Khi thăng cấp công pháp sẽ tăng chỉ số cộng dồn từ công pháp\n" +
                "|5|Tỷ lệ thăng cấp : " + getLevelUpPercent() + "%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_THANG_CAP_CONG_PHAP_TU_MA, -1, text, "Thăng cấp", "Đóng");
    }

    public void showMenuThangPham() {
        String text = "|7|Thăng phẩm công pháp\n" +
                "|5|Khi thăng phẩm công pháp sẽ tăng chỉ số cộng dồn từ công pháp\n" +
                "|5|Tỷ lệ thăng phẩm : " + getLevelUpPercent() / 5 + "%";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_THANG_PHAM_CONG_PHAP_TU_MA, -1, text, "Thăng phẩm", "Đóng");
    }

    public void showMenuDoiCongPhap() {
        String text = "|7|Đổi công pháp\n" +
                "|5|Bạn có thể đổi công pháp khác nhưng sẽ mất toàn bộ kinh nghiệm và cấp độ hiện tại";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_DOI_CONG_PHAP_TU_MA, -1, text, "Đổi công pháp", "Đóng");
    }

    public void showMenuCanNuotCongPhap() {
        String text = "|7|Nuốt công pháp\n" +
                "|5|Bạn có thể nuốt công pháp khác để nhận được kinh nghiệm phẩm";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CAN_NUOT_CONG_PHAP_TU_MA, -1, text, "Nuốt công pháp", "Đóng");
    }

    public void useHuyetDan(Item item) {
        if (totalHuyetDan + 1 > getMaxHuyetDan()) {
            Service.gI().sendThongBao(player, "Bạn đã dùng quá số lượng huyết đan, tiếp tục dùng không có tác dụng");
            return;
        }
        // + chi so
        int rad = Util.nextInt(0, 4);
        switch (rad) {
            case 0 -> totalBuffDameHuyetDan += Util.nextInt(1, 5);
            case 1 -> totalBuffHpHuyetDan += Util.nextInt(1, 10);
            case 2 -> totalBuffMpHuyetDan += Util.nextInt(1, 10);
            case 3 -> player.tuMa.addMaKhi(Util.nextInt(1, 10));
            case 4 -> player.tuMa.addExp(100);
        }
        Service.gI().sendThongBao(player, "Dùng huyết đan thành công tăng lên một chút chỉ số");
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
        InventoryServiceNew.gI().sendItemBags(player);
    }

    public void showThongTinHuyetDan() {
        String text = "|7|❖════ THÔNG TIN HUYẾT ĐAN ════❖\n" +
                "|5|➤ Dame Buff : " + totalBuffDameHuyetDan + "\n" +
                "|5|➤ HP Buff   : " + totalBuffHpHuyetDan + "\n" +
                "|5|➤ MP Buff   : " + totalBuffMpHuyetDan + "\n" +
                "|7|❖════════════════════════════❖";
        NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, text, "Đóng");
    }
}
