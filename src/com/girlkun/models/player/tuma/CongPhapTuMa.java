package com.girlkun.models.player.tuma;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;

public class CongPhapTuMa {
    public static final int MAX_PHAM_CHAT = 20;
    Player player;
    public long lastTimeAutoChiSo;

    public String ten;
    public int phamChat = 0;
    public int dlThonPhe;
    public int deTuThonPhe;
    public int totalHuyetDan;

    public boolean autoDame = false;
    public boolean autoHp = false;
    public boolean autoMp = false;
    public float tyLeLinhNgo;

    public double tongDameThonPhe;
    public double tongHpThonPhe;
    public double tongMpThonPhe;

    public double dameBuff;
    public double hpBuff;
    public double mpBuff;

    public double totalDameBuff;
    public double totalHpBuff;
    public double totalMpBuff;
    public double totalBuffDameHuyetDan;
    public double totalBuffHpHuyetDan;
    public double totalBuffMpHuyetDan;

    public CongPhapTuMa(String ten) {
        this.ten = ten;
    }

    public CongPhapTuMa(Player player) {
        this.player = player;
    }

    public double getMaxDameBuff() {
        double baseDame = 500_000;
        return baseDame * phamChat;
    }

    public double getMaxHpMpBuff() {
        double baseHpMp = 1_000_000;
        return baseHpMp * phamChat;
    }

    public int getMaxHuyetDan() {
        return Math.max(1, phamChat) * 2_000_000;
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

    public String getTenPhamChat() {
        switch (phamChat) {
            case 0:
                return "Tạp";
            case 1:
                return "Vô";
            case 2:
                return "Không";
            case 3:
                return "Luân";
            case 4:
                return "Thi";
            case 5:
                return "Huyết";
            case 6:
                return "Tà";
            case 7:
                return "Ma";
            case 8:
                return "U";
            case 9:
                return "Vong";
            case 10:
                return "Lệ";
            case 11:
                return "Ảnh";
            case 12:
                return "Sát";
            case 13:
                return "Linh";
            case 14:
                return "Ma Vương";
            case 15:
                return "Hắc Ám";
            case 16:
                return "Hủy Diệt";
            case 17:
                return "Chân Ma";
            case 18:
                return "Tuyệt Ma";
            case 19:
                return "Hư Vô";
            case 20:
                return "Ma Thần";
            default:
                return "Không xác định";
        }
    }

    public String getTenCongPhap() {
        return "[" + getTenPhamChat() + "]" + ten;
    }

    public float getTyLeLinhNgoPhamChat() {
        return tyLeLinhNgo;
    }

    public double getDameBuff() {
        return dameBuff;
    }

    public double getHpBuff() {
        return hpBuff;
    }

    public double getMpBuff() {
        return mpBuff;
    }

    public long getBaseMaKhiToChuMa() {
        int phamChat = Math.max(this.phamChat, 1);
        if (phamChat <= 5) {
            return phamChat * 1000L;
        } else if (phamChat <= 10) {
            return phamChat * 10000L;
        } else if (phamChat <= 15) {
            return phamChat * 100000;
        } else if (phamChat <= 20) {
            return phamChat * 1000000;
        }
        return 100;
    }

    public void update() {
        // auto chi so
        if (Util.canDoWithTime(lastTimeAutoChiSo, 1000) && (autoMp || autoDame || autoHp)) {
            if (autoDame) {
                congBuffKhongThongBao((byte) 0, 1);
            }
            if (autoMp) {
                congBuffKhongThongBao((byte) 2, 1);
            }
            if (autoHp) {
                congBuffKhongThongBao((byte) 1, 1);
            }
            lastTimeAutoChiSo = System.currentTimeMillis();
        }
    }

    public void chuMa(int time) {
        long maKhiChuMaCan = getBaseMaKhiToChuMa() * time;
        if (!player.tuMa.canHandleWithMaKhiPoint(maKhiChuMaCan)) {
            Service.gI().sendThongBao(player, "Bạn không đủ ma khí để chú ma cần " + maKhiChuMaCan + " ma khí");
            return;
        }
        // chu ma tang len ty le linh ngo
        tyLeLinhNgo += time;
        Service.gI().sendThongBao(player, "Chú ma thành công tăng tỷ lệ lĩnh ngộ lên " + tyLeLinhNgo + "%");
        player.tuMa.subMaKhi(maKhiChuMaCan);
    }

    public long getBaseDiemLinhNgoMax() {
        if (phamChat + 1 > MAX_PHAM_CHAT) return 0;
        if (phamChat + 1 <= 5) {
            return (phamChat + 1) * 100L;
        } else if (phamChat + 1 <= 10) {
            return (phamChat + 1) * 500;
        } else if (phamChat + 1 < 20) {
            return (phamChat + 1) * 1000;
        } else {
            return (phamChat + 1) * 10_000;
        }
    }

    public float getBaseTyLeLinhNgo() {
        return (player.tuMa.maTinh);
    }

    public void linhNgo() {
        float baseTyLe = getBaseTyLeLinhNgo() + tyLeLinhNgo;
        if (baseTyLe >= getBaseDiemLinhNgoMax()) {
            Service.gI().sendThongBao(player, "Lĩnh ngộ thành công");
            phamChat += 1;
            calcTotalBuff();
        } else {
            Service.gI().sendThongBao(player, "Lĩnh ngộ thất bại");
        }
        tyLeLinhNgo = 0;
    }

    public void calcTotalBuff() {
        this.totalDameBuff = getMaxDameBuff();
        this.totalHpBuff = getMaxHpMpBuff();
        this.totalMpBuff = getMaxHpMpBuff();
    }

    public long getMaKhiCanDeCongDiem(byte type) {
        if (type == 0) {
            // dua tren pham chat
            return phamChat * 2L;
        } else if (type == 1 || type == 2) {
            return phamChat;
        }
        return phamChat;
    }

    public void congBuff(byte type, long pris) {
        long maKhiCan = getMaKhiCanDeCongDiem(type) * pris;
        if (!player.tuMa.canHandleWithMaKhiPoint(maKhiCan)) {
            Service.gI().sendThongBaoOK(player, "Bạn không đủ ma khí để cộng điểm cần " + maKhiCan);
            return;
        }
        if (type == 0 && (dameBuff + (pris * 2) > totalDameBuff)) {
            Service.gI().sendThongBaoOK(player, "Bạn đang cộng quá giới hạn rồi");
            return;
        }
        if (type == 1 && (hpBuff + (pris * 10) > totalHpBuff)) {
            Service.gI().sendThongBaoOK(player, "Bạn đang cộng quá giới hạn rồi");
            return;
        }
        if (type == 2 && (mpBuff + (pris * 10) > totalMpBuff)) {
            Service.gI().sendThongBaoOK(player, "Bạn đang cộng quá giới hạn rồi");
            return;
        }
        switch (type) {
            case 0 -> dameBuff += pris * 2;
            case 1 -> hpBuff += pris * 10;
            case 2 -> mpBuff += pris * 10;
        }
        player.tuMa.subMaKhi(maKhiCan);
        Service.gI().point(player);
    }

    public void congBuffKhongThongBao(byte type, long pris) {
        long maKhiCan = getMaKhiCanDeCongDiem(type) * pris;
        if (!player.tuMa.canHandleWithMaKhiPoint(maKhiCan)) {
            return;
        }
        if (type == 0 && (dameBuff + (pris * 2) > totalDameBuff)) {
            return;
        }
        if (type == 1 && (hpBuff + (pris * 10) > totalHpBuff)) {
            return;
        }
        if (type == 2 && (mpBuff + (pris * 10) > totalMpBuff)) {
            return;
        }
        switch (type) {
            case 0 -> dameBuff += pris * 2;
            case 1 -> hpBuff += pris * 10;
            case 2 -> mpBuff += pris * 10;
        }
        player.tuMa.subMaKhi(maKhiCan);
        Service.gI().point(player);
    }

    // show info cong phap
    public void showBaseMenu() {
        if (ten == null) {
            Service.gI().sendThongBao(player, "Bạn chưa học công pháp");
            return;
        }
        String text = "|7|Thông tin công pháp\n" + "|5|" + getTenCongPhap() + "\n" + "|5|Dame Buff : " + Util.powerToString(dameBuff) + "\n" + "|5|HP Buff :" + Util.powerToString(hpBuff) + "\n" + "|5|Mp Buff :" + mpBuff + "\n" + "|1|Chú ma :" + tyLeLinhNgo + "%" + "/" + getBaseDiemLinhNgoMax() + "%" + "\n" + "|7|Phẩm cấp càng cao giới hạn buff càng cao";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONG_PHAP_TU_MA, -1, text, "Lĩnh ngộ", "Chú ma", "Tăng\nChỉ Số", "Thôn phệ", "Huyết Đan", "Đóng");
    }

    public void showCOngChiSoMenu() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖════ THÔNG TIN CÔNG PHÁP ════❖\n");

// — Tên công pháp —
        text.append("|5|").append(getTenCongPhap()).append("\n");

// — Buff chỉ số —
        text.append("|5|➤ Dame Buff : ").append(Util.powerToString(dameBuff)).append("\n");
        text.append("|5|➤ HP Buff   : ").append(Util.powerToString(hpBuff)).append("\n");
        text.append("|5|➤ MP Buff   : ").append(mpBuff).append("\n");

// — Tỷ lệ lĩnh ngộ (Chú ma) —
        text.append("|1|✦ Chú Ma     : ").append(tyLeLinhNgo).append("% / ").append(getBaseDiemLinhNgoMax()).append("%\n");

// — Gợi ý nâng cấp —
        text.append("|7|✪ Phẩm cấp càng cao, giới hạn buff càng lớn!");

        text.append("\n|7|❖══════════════════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONG_CHI_SO, -1, text.toString(), "Cộng\nChỉ Số", "Auto\nCộng CS");
    }

    public void calcPoint() {
        player.nPoint.hpAdd += getHpBuff() + totalBuffHpHuyetDan + tongDameThonPhe;
        player.nPoint.mpAdd += getMpBuff() + totalBuffMpHuyetDan + tongHpThonPhe;
        player.nPoint.dameAdd += getDameBuff() + totalBuffDameHuyetDan + tongMpThonPhe;
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
        this.dameBuff += dameB;
        if (dameBuff > totalDameBuff) {
            dameBuff = totalDameBuff;
        }
    }

    public void addHpBuff(double dameB) {
        this.hpBuff += dameB;
        if (hpBuff > totalHpBuff) {
            hpBuff = totalHpBuff;
        }
    }

    public void addMPBuff(double dameB) {
        this.mpBuff += dameB;
        if (mpBuff > totalMpBuff) {
            mpBuff = totalMpBuff;
        }
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

    public void showMenuThonPhe() {
        String text = "|7|Thôn phệ\n" + "|5|Khi bạn thôn phệ đệ tử hoặc đạo lữ sẽ nhận được 20% chỉ số của họ";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CHON_THON_PHE, -1, text, "Đệ tử", "Đạo lữ");
    }

    public void showMenuCongChiSo() {
        String text = "|7|Cộng chỉ số\n" + "|5|Bạn muốn cộng chỉ số nào";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONG_CHI_SO_MANUAL, -1, text, "Tấn công", "HP", "MP");
    }

    public void showMenuAutoCs() {
        String text = "|7|Cộng chỉ số tự động\n" + "|5|Bạn muốn tự động cộng chỉ số nào";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONG_CHI_SO_AUTO, -1, text, "Tấn công\n" + (autoDame ? "Mở" : "Đóng"), "HP\n" + (autoHp ? "Mở" : "Đóng"), "MP\n" + (autoMp ? "Mở" : "Đóng"));
    }

    public void showMenuChuMa() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖═════ CHÚ MA ═════❖\n");

        text.append("|5|➤ Chú ma giúp tăng tỷ lệ lĩnh ngộ công pháp.\n");
        text.append("|5|➤ Mỗi lần chú, điểm lĩnh ngộ tăng theo giới hạn phẩm cấp.\n");

        text.append("|7|✦ Tiến độ: [").append(tyLeLinhNgo).append("% / +").append(getBaseDiemLinhNgoMax()).append("%]\n");

        text.append("|7|❖══════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CHU_MA, -1, text.toString(), "1 lần", "10 lần", "100 lần", "Đóng");
    }

    public void showMemuLinhNgo() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖════ LĨNH NGỘ CÔNG PHÁP ════❖\n");

        text.append("|5|➤ Khi Chú Ma đạt giới hạn, bạn có thể lĩnh ngộ công pháp.\n");
        text.append("|7|✦ Chú Ma hiện tại: ").append("[").append(tyLeLinhNgo).append("%]\n");

        text.append("|7|❖═══════════════════════════❖");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_LINH_NGO_TU_MA, -1, text.toString(), "Lĩnh ngộ", "Đóng");
    }

    public void toggleAutoCs(int select) {
        if (select == 0) {
            autoDame = !autoDame;
        }
        if (select == 1) {
            autoHp = !autoHp;
        }
        if (select == 2) {
            autoMp = !autoMp;
        }
    }

    public void showMenuCongChiSoManual(int select) {
        String text = "|7|Chọn số lượng cần cộng\n|5|Bạn muốn cộng bao nhiêu nào?";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_CHI_SO, -1, text, "1 Lần", "10 Lần", "100 Lần", "1000 Lần", "Tất cả");
        player.iDMark.typePlusChiSoMaCong = select;
    }

    public void handleCongChiSo(int typePlusChiSoMaCong, int timeCong) {
        if (timeCong != -1) {
            congBuff((byte) typePlusChiSoMaCong, timeCong);
            return;
        }
        // tinh tong chi so co the dung bang ma khi hien tai
        long totalPris = (player.tuMa.maKhiPoint / getMaKhiCanDeCongDiem((byte) typePlusChiSoMaCong));
        if (totalPris == 0) {
            Service.gI().sendThongBao(player, "Không có ma khí");
            return;
        }
        switch (typePlusChiSoMaCong) {
            case 0:
                if (dameBuff + (totalPris * 2) > totalDameBuff) {
                    totalPris = (long) ((totalDameBuff - dameBuff) / 2);
                }
                break;
            case 1:
                if (hpBuff + (totalPris * 10) > totalHpBuff) {
                    totalPris = (long) ((totalHpBuff - hpBuff) / 10);
                }
                break;
            case 2:
                if (mpBuff + (totalPris * 10) > totalMpBuff) {
                    totalPris = (long) ((totalMpBuff - mpBuff) / 10);
                }
                break;
        }
        congBuff((byte) typePlusChiSoMaCong, totalPris);
    }

    public void handleHutMaKhi(Mob mob) {
        double baseHut = Math.max(mob.point.maxHp / 1_000_000_0, Util.nextInt(1, 3));
        baseHut *= (phamChat + 1 + player.tuMa.luyenHon.getMaKhiBuff());
        player.tuMa.addMaKhi(Math.max(100, (long) baseHut));
    }

    public void showThongTinHuyetDan() {
        StringBuilder text = new StringBuilder();

        text.append("|7|❖════ THÔNG TIN HUYẾT ĐAN ════❖\n");

        text.append("|5|➤ Dame Buff : ").append(totalBuffDameHuyetDan).append("\n");
        text.append("|5|➤ HP Buff   : ").append(totalBuffHpHuyetDan).append("\n");
        text.append("|5|➤ MP Buff   : ").append(totalBuffMpHuyetDan).append("\n");

        text.append("|7|❖════════════════════════════❖");

        NpcService.gI().createMenuConMeo(player, ConstNpc.IGNORE_MENU, -1, text.toString(), "Đóng");
    }
}
