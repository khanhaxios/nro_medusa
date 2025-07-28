package com.girlkun.models.player.huyet_mach;

import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.Template;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tuma.TuMa;
import com.girlkun.models.player.tutien.luyenkhi.TuTien;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Huyet {
    private static final String[] TEN_PHAM = new String[]{
            "Phàm", "Thanh", "Linh", "Quân", "Vương", "Hoàng", "Đế", "Tiên", "Thần"
    };

    public void openMenuTinhHuyet() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Tinh huyết huyết mạch\n");
        stringBuilder.append("|5|Tinh huyết cần huyết đan và mỗi huyết đan tăng 50 exp\n");
        stringBuilder.append("|5|Sau khi đầy exp ấn đột phá sẽ lên phẩm");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TINH_HUYET, -1, stringBuilder.toString(), "1 lần", "10 lần", "100 lần", "Đóng");
    }

    public void openMenuToihuyet() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Thông tin tôi huyết");
        stringBuilder.append(Util.getHonorialLine(12));
        stringBuilder.append("|5| DAME : ").append(getDoTinhKhietBuff()).append("%").append("\n");
        stringBuilder.append("|5| HP : ").append(getDoTinhKhietBuff()).append("%").append("\n");
        stringBuilder.append("|5| KI : ").append(getDoTinhKhietBuff()).append("%").append("\n");
        stringBuilder.append("|5|Độ tinh khiết [").append(doTinhKhiet).append("]\n");
        stringBuilder.append("|7|Tỷ lệ thành công [").append(getTyLeToiHuyetThanhCong()).append("]\n");
        stringBuilder.append(Util.getHonorialLine(12));
        stringBuilder.append("|2|Tinh huyết cần tốn huyết đan (cái này đi xin ma tu nhé!!!!)\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TOI_HUYET, -1, stringBuilder.toString(), "1 lần", "10 lần", "100 lần", "1000 lần", "Đóng");
    }

    public static class OptionForHuyet {

        private static Map<String, String> OPTION_STRING = new HashMap<String, String>();

        public double param;

        public Template.ItemOptionTemplate optionTemplate;

        public OptionForHuyet() {
        }

        public OptionForHuyet(OptionForHuyet io) {
            this.param = io.param;
            this.optionTemplate = io.optionTemplate;
        }

        public OptionForHuyet(int tempId, double param) {
            this.optionTemplate = ItemService.gI().getItemOptionTemplate(tempId);
            this.param = param;
        }

        public OptionForHuyet(Template.ItemOptionTemplate temp, double param) {
            this.optionTemplate = temp;
            this.param = param;
        }

        public String getOptionString() {
            return Util.replace(this.optionTemplate.name, "#", String.valueOf(this.param));
        }

        public void dispose() {
            this.optionTemplate = null;
        }

        @Override
        public String toString() {
            final String n = "\"";
            return "{"
                    + n + "id" + n + ":" + n + optionTemplate.id + n + ","
                    + n + "param" + n + ":" + n + param + n
                    + "}";
        }
    }

    public static class TinhHuyetEffect {
        public static int[][][] LEVEL_PARAM_TYPE = new int[][][]{
                {{50, 50}, {75, 75}, {160, 160}},
                {{100, 100}, {150, 150}, {200, 200}},
                {{20, 20}, {30, 30}, {40, 40}},
                {{20, 20}, {30, 30}, {40, 40}},
                {{10, 50}, {20, 75}, {30, 100}}
        };
        public static String[][] LEVEL_DESC_TYPE = new String[][]{
                {"Tăng 50% DAME,50% STCM", "Tăng 75% DAME,75% STCM", "Tăng 160% DAME,160% STCM"},
                {"Tăng 100% HP,100% KI", "Tăng 150% HP,150% KI", "Tăng 200% HP,200% HP"},
                {"Tăng 20% DAME SSS,20% DAME ÁNH SÁNG", "Tăng 30% DAME SSS,30% DAME ÁNH SÁNG", "Tăng 40% DAME SSS,50% DAME ÁNH SÁNG"},
                {"Tăng 20% DAME SSS,20% DAME BÓNG TỐI", "Tăng 30% DAME SSS,20% DAME BÓNG TỐI", "Tăng 40% DAME SSS,20% DAME BÓNG TỐI"},
                {"Tăng 10% KHÁNG SÁT THƯƠNG,50% HP,KI", "Tăng 20% KHÁNG SÁT THƯƠNG,75% HP,KI", "Tăng 30% KHÁNG SÁT THƯƠNG,160% HP,KI"}
        };

        public byte type;
        public byte levelRequired;
        public int xParam;

        public Player player;

        public TinhHuyetEffect(Player player) {
            this.player = player;
        }

        public TinhHuyetEffect(Player player, byte type, byte levelRequired, int xParam) {
            this.type = type;
            this.levelRequired = levelRequired;
            this.player = player;
            this.xParam = xParam;
        }

        public String getDescription(byte level) {
            return LEVEL_DESC_TYPE[type][level];
        }
    }

    public int slTinhHuyet;

    public boolean isOpen = false;
    public static int[] OPTION_VIP_CAN_ROLL = new int[]{0, 2, 5, 50, 77, 103, 251, 252};
    public String[] typeName = new String[]{"Thần huyết", "Long huyết", "Ma huyết", "Tiên huyết", "Đế Huyết"};
    public byte pham;
    public int doTinhKhiet;
    public long exp;
    public long maxExp;
    public byte type;
    public int[] chiSoBaseCongThem = new int[]{0, 0, 0};

    public int maxSlTinhHuyetCoTheNuot;

    public int slTinhHuyetDaNuot;

    public Player player;
    public byte MAX_PHAM = 8;
    public List<OptionForHuyet> options = new ArrayList<>();

    public List<OptionForHuyet> optionChiSo = new ArrayList<>();

    public Huyet(Player player) {
        this.player = player;
    }

    public int getDoTinhKhietBuff() {
        return slTinhHuyet * 20 / 100;
    }

    public void addExp(long exp) {
        this.exp += exp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }

    public int calcMaxSlTinhHuyetcoTheNuot() {
        return pham * 100;
    }

    public long calcMaxExp() {
        if (pham == 8) {
            return 50_000_000;
        }
        if (pham == 9) {
            return 500_000_000;
        }
        return Math.max(1, pham) * 1_000_000;
    }

    public int calcMaxSlTinhHuyet() {
        return Math.max(1, pham) * 100;
    }

    public void tinhHuyet(int slHuyetDan) {
        // can huyet dan
        Item item = InventoryServiceNew.gI().findItemBag(player, 2077);
        if (item == null || item.quantity - slHuyetDan < 0) {
            Service.gI().sendThongBao(player, "Không đủ huyết đan");
            return;
        }
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, slHuyetDan);
        InventoryServiceNew.gI().sendItemBags(player);
        long exp = slHuyetDan * 50L;
        addExp(exp);
        Service.gI().sendThongBaoOK(player, "Dùng huyết đan thành công nhận được x" + exp + " Kinh nghiệm huyết mạch");
    }

    public void nuotTinhHuyet() {
        if (slTinhHuyetDaNuot + 1 > maxSlTinhHuyetCoTheNuot) {
            Service.gI().sendThongBao(player, "Đã đạt giới hạn");
            return;
        }
        int ranDomChiSo = Util.nextInt(1, 3);
        chiSoBaseCongThem[Util.nextInt(0, 2)] += ranDomChiSo;
        slTinhHuyetDaNuot++;
        Service.gI().sendThongBao(player, "Nuốt tinh huyết thành công");
    }

    public int getLevelTinhHuyetCongDon() {
        if (pham == 8) return 3;

        if (pham >= 6) {
            return 2;
        }
        if (pham >= 3) {
            return 1;
        }
        return 0;
    }

    public boolean canNangPham() {
        return doTinhKhiet >= 100 && (exp == maxExp) && (pham + 1 <= MAX_PHAM);
    }

    public void nangPham() {
        // try to nang pham
        if (!canNangPham()) {
            Service.gI().sendThongBaoOK(player, "Bạn không thể nâng phẩm cần\n Đầy độ tinh khiết, Đầy kinh nghiệm và Phẩm chưa đạt tối đa");
            return;
        }
        pham += 1;
        slTinhHuyet += 1;
        // roll dong thuoc tinh
        rollDongThuocTinh();
        calcMaxSlTinhHuyetcoTheNuot();
        restExp();
        resDoTinhKhiet();
        Service.gI().sendThongBaoOK(player, "Đột phá phẩm thành công");
        Service.gI().point(player);
    }

    public void rollDongThuocTinh() {
        // lay ra dong random
        int randomParam = 0;
        int idOption = OPTION_VIP_CAN_ROLL[Util.nextInt(0, OPTION_VIP_CAN_ROLL.length)];
        if (idOption == 0) {
            randomParam = Util.nextInt(19999, 99999);
        } else if (idOption == 2) {
            randomParam = Util.nextInt(200000, 500000);
        } else {
            randomParam = Util.nextInt(1, 5);
        }
        randomParam *= pham;
        options.add(new OptionForHuyet(idOption, randomParam));
    }

    public void restExp() {
        this.exp = 0;
        this.maxExp = calcMaxExp();
    }

    public void resDoTinhKhiet() {
        this.doTinhKhiet = 0;
    }

    public String getTenPham() {
        return TEN_PHAM[pham];
    }

    public String getFullName() {
        return String.format("[%s]%s", getTenPham(), getNameByType());
    }

    public void showBaseMenu() {
        if (!player.huyet.isOpen) {
            Service.gI().sendThongBao(player, "Bạn chưa mở huyết mạch");
            return;
        }
        String menuText = "|7|Thông tin Huyết" +
                Util.getHonorialLine(12) +
                "|7|" + getFullName() + "\n"
                + getThongTinBuffBase()
                + "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" +
                "|5|Độ tinh khiết : " + doTinhKhiet + "%\n" +
                rateHuyetMach() + "\n" +
                "|7|Thông tin kích hoạt huyết mạch\n" +
                getThongTinBuff();
        NpcService.gI().createMenuConMeo(player, ConstNpc.BASE_MENU_HUYET, -1, menuText, "Tinh huyết", "Tôi huyết", "Nâng phẩm", "Đóng");
    }

    private String getThongTinBuffBase() {
        StringBuilder stringBuilder = new StringBuilder();
        for (OptionForHuyet option : options) {
            stringBuilder.append("|5|").append(option.getOptionString()).append("\n");
        }
        return stringBuilder.toString();
    }

    private String getThongTinBuff() {
        StringBuilder stringBuilder = new StringBuilder();
        int level = getLevelTinhHuyetCongDon();
        for (int i = 1; i <= 3; i++) {
            if (i <= level) {
                stringBuilder.append("\n|7|");
                stringBuilder.append(TinhHuyetEffect.LEVEL_DESC_TYPE[type][i - 1]);
                stringBuilder.append("\n");
            } else {
                stringBuilder.append("\n|5|");
                stringBuilder.append(TinhHuyetEffect.LEVEL_DESC_TYPE[type][i - 1]);
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    public void kichHoatHuyetMach() {
        // kich hoat huyet mach
        if (pham > 0 || isOpen) {
            Service.gI().sendThongBaoOK(player, "Bạn đã kích hoạt huyết mạch rồi mà!");
            return;
        }
        if (!player.tuTien.isTuTien() && !player.luyenThe.isLuyenTheReal() && !player.tuMa.isTuMa()) {
            Service.gI().sendThongBao(player, "Bạn cần chức nghiệp để có thể kích hoạt huyết mạch");
            return;
        }
        if (player.tuTien.isTuTien() || player.tuMa.isTuMa() || player.luyenThe.isLuyenTheReal()) {
            if (player.tuTien.isTuTien() && player.tuTien.level < 10) {
                Service.gI().sendThongBao(player, "Cần đạt " + TuTien.CANH_GIOI[10] + " Để kích hoạt huyết mạch");
                return;
            }
            if (player.tuMa.isTuMa() && player.tuMa.level < 100) {
                Service.gI().sendThongBao(player, "Cần đạt " + TuMa.CANH_GIOI[100 / 10] + " Để kích hoạt huyết mạch");
                return;
            }
            if (player.luyenThe.isLuyenTheReal() && player.luyenThe.level < 100) {
                Service.gI().sendThongBao(player, "Cần đạt luyện thể tầng " + 200 + " Để kích hoạt huyết mạch");
                return;
            }
        }
        if (player.session.vnd - 300_000 < 0) {
            Service.gI().sendThongBao(player, "Cần 300k điểm để mở");
            return;
        }
        PlayerDAO.subvnd(player, 300_000);
        // kich hoat ne
        ratioTypeHuyetMach();
        ratioChiSoHuyetMach();
        pham = 0;
        restExp();
        resDoTinhKhiet();
        isOpen = true;
        Service.gI().sendThongBao(player, "Kích hoạt huyết mạch thành công");
    }

    public boolean isKichHoat() {
        return isOpen;
    }

    public void ratioTypeHuyetMach() {
//        if (Util.isTrue(1, 100)) {
//            type = 0;
//        } else if (Util.isTrue(5, 100)) {
//            type = 1;
//        } else if (Util.isTrue(10, 100)) {
//            type = 2;
//        } else if (Util.isTrue(20, 100)) {
//            type = 3;
//        } else {
//            type = 4;
//        }
        type = (byte) Util.nextInt(0, 4);
    }

    public int getBaseOptionBuff(int type) {
        if (type == 0) {
            if (pham == 8) {
                return 850;
            }
            if (pham == 9) {
                return 1000;
            }
            return Math.max(1, pham) * 100;
        } else {
            if (pham == 8) {
                return 1000;
            }
            if (pham == 9) {
                return 1500;
            }
            return Math.max(1, pham) * 150;
        }
    }

    public String getNameByType() {
        return typeName[type];
    }

    public String rateHuyetMach() {
        int diem = 1;
        switch (type) {
            case 0:
                diem += 10;
                break;
            case 1:
                diem += 7;
                break;
            case 2:
                diem += 5;
                break;
            case 3:
                diem += 10;
                break;
            case 4:
                diem += 5;
                break;
        }
        diem *= Math.max(options.size(), 1);
        diem *= Math.max(optionChiSo.size(), 1);
        diem *= Math.max(chiSoBaseCongThem.length, 1);
        diem *= Math.max((doTinhKhiet + getDoTinhKhietBuff()), 1);
        diem *= Math.max(getLevelTinhHuyetCongDon(), 1);
        return "Chiến lực huyết mạch [" + diem + "]";
    }

    public void ratioChiSoHuyetMach() {
        List<OptionForHuyet> options1 = new ArrayList<>();
        switch (type) {
            case 0:
                options1.add(new OptionForHuyet(0, Util.nextInt(50_000, 250_000)));
                options1.add(new OptionForHuyet(5, Util.nextInt(5, 50)));
                break;
            case 1:
                options1.add(new OptionForHuyet(50, Util.nextInt(5, 20)));
                options1.add(new OptionForHuyet(77, Util.nextInt(20, 50)));
                options1.add(new OptionForHuyet(103, Util.nextInt(20, 50)));
                break;
            case 2:
                options1.add(new OptionForHuyet(77, Util.nextInt(50, 100)));
                options1.add(new OptionForHuyet(103, Util.nextInt(50, 100)));
                break;
            case 3:
                options1.add(new OptionForHuyet(50, Util.nextInt(5, 10)));
                options1.add(new OptionForHuyet(77, Util.nextInt(20, 50)));
                options1.add(new OptionForHuyet(103, Util.nextInt(20, 50)));
                break;
            case 4:
                options1.add(new OptionForHuyet(50, Util.nextInt(5, 100)));
                options1.add(new OptionForHuyet(103, Util.nextInt(5, 100)));
                break;
        }
        optionChiSo = options1;
    }

    public void calcPointWithOption(List<OptionForHuyet> options) {
        int dameAdd = 0;
        int dameSSSAdd = 0;
        for (OptionForHuyet io : options) {
            switch (io.optionTemplate.id) {
                case 41:
                    player.setClothes.tienKhi++;
                    break;
                case 42:
                    player.setClothes.thanhKhi++;
                    break;
                case 43:
                    player.setClothes.thanKhi++;
                    break;
                case 0: //Tấn công +#
                    player.nPoint.dameAdd += io.param;
                    break;
                case 2: //HP, KI+#000
                    player.nPoint.hpAdd += Math.abs(io.param * 1000);
                    player.nPoint.mpAdd += Math.abs(io.param * 1000);
                    break;
                case 108, 73:// fake
                    player.nPoint.tlNeDon += (short) Math.abs(io.param);
                    break;
                case 18: // #% chính xác
                    player.nPoint.tlchinhxac += (short) Math.abs(io.param);
                    break;
                case 5, 197, 220, 233: //+#% sức đánh chí mạng
                    player.nPoint.tlDameCrit.add((int) Math.abs(io.param));
                    break;
                case 6: //HP+#
                    player.nPoint.hpAdd += Math.abs(io.param);
                    break;
                case 7: //KI+#
                    player.nPoint.mpAdd += io.param;
                    break;
                case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                    player.nPoint.tlHutHpMpXQ += (short) io.param;
                    break;
                case 14: //Chí mạng+#%
                    player.nPoint.critAdd += io.param;
                    break;
                case 19: //Tấn công+#% khi đánh quái
                    player.nPoint.tlDameAttMob.add((int) io.param);
                    break;
                case 22: //HP+#K
                    player.nPoint.hpAdd += io.param * 1000;
                    break;
                case 23: //MP+#K
                    player.nPoint.mpAdd += io.param * 1000;
                    break;
                case 27: //+# HP/30s
                    player.nPoint.hpHoiAdd += io.param;
                    break;
                case 28: //+# KI/30s
                    player.nPoint.mpHoiAdd += io.param;
                    break;
                case 33: //dịch chuyển tức thời
                    player.nPoint.teleport = true;
                    break;
                case 44: // tien luc
                    player.tienLuc += (int) io.param;
                    break;
                case 45:
                    player.nPoint.dameAdd += io.param * 1_000_000L;
                    break;
                case 47: //Giáp+#
                    player.nPoint.defAdd += io.param;
                    break;
                case 48: //HP/KI+#
                    player.nPoint.hpAdd += io.param;
                    player.nPoint.mpAdd += io.param;
                    break;
                case 49: //Tấn công+#%
                    dameSSSAdd += (int) io.param;
                    break;
                case 50: //Sức đánh+#%
                    dameAdd += (int) io.param;
                    break;
                case 77, 194, 221: //HP+#%
                    player.nPoint.tlHp.add((int) io.param);
                    break;
                case 80: //HP+#%/30s
                    player.nPoint.tlHpHoi += (short) io.param;
                    break;
                case 81: //MP+#%/30s
                    player.nPoint.tlMpHoi += (short) io.param;
                    break;
                case 88, 101: //Cộng #% exp khi đánh quái
                    player.nPoint.tlTNSM.add((int) io.param);
                    break;
                case 94: //Giáp #%
                    player.nPoint.tlDef.add((int) io.param);
                    break;
                case 95: //Biến #% tấn công thành HP
                    player.nPoint.tlHutHp += (short) io.param;
                    break;
                case 96: //Biến #% tấn công thành MP
                    player.nPoint.tlHutMp += (short) io.param;
                    break;
                case 97: //Phản #% sát thương
                    player.nPoint.tlPST += (short) io.param;
                    break;
                case 100: //+#% vàng từ quái
                    player.nPoint.tlGold += (short) io.param;
                    break;
                case 103, 195, 222: //KI +#%
                    player.nPoint.tlMp.add((int) io.param);
                    break;
                case 104: //Biến #% tấn công quái thành HP
                    player.nPoint.tlHutHpMob += (short) io.param;
                    break;
                case 105: //Vô hình khi không đánh quái và boss
                    player.nPoint.wearingVoHinh = true;
                    break;
                case 106: //Không ảnh hưởng bởi cái lạnh
                    player.nPoint.isKhongLanh = true;
                    break;
                // đối nghịch
                case 109: //Hôi, giảm #% HP
                    player.nPoint.tlHpGiamODo += (short) io.param;
                    break;
                case 116: //Kháng thái dương hạ san
                    player.nPoint.khangTDHS = true;
                    break;
                case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                    player.nPoint.tlSDDep.add((int) io.param);
                    break;
                case 147, 196, 219, 232: //+#% sức đánh
                    player.nPoint.tlDame.add((int) io.param);
                    break;
                case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                    player.nPoint.tlSubSD += 50;
                    player.nPoint.tlTNSM.add((int) io.param);
                    player.nPoint.tlGold += (short) io.param;
                    break;
                case 162: //Cute hồi #% KI/s bản thân và xung quanh
                    player.nPoint.mpHoiCute += (long) io.param;
                    break;
                case 173: //Phục hồi #% HP và KI cho đồng đội
                    player.nPoint.tlHpHoiBanThanVaDongDoi += (short) io.param;
                    player.nPoint.tlMpHoiBanThanVaDongDoi += (short) io.param;
                    break;
            }
        }
        if (dameSSSAdd > 0) {
            int dameTurn1 = ((int) (dameSSSAdd * 0.81)) + ((int) (dameAdd * 0.19));
            int dameTurn2 = ((int) (dameAdd * 0.81)) + ((int) (dameSSSAdd * 0.19));
            player.nPoint.tlDame.add(dameTurn1);
            player.nPoint.tlDame.add(dameTurn2);
        } else if (dameAdd > 0) {
            player.nPoint.tlDame.add(dameAdd);
        }
    }

    public void calcPoint() {
        calcPointWithOption(options);
        calcPointWithOption(optionChiSo);
        // calc base point
        player.nPoint.tlDame.add((getBaseOptionBuff(0) + chiSoBaseCongThem[0] + (doTinhKhiet + getDoTinhKhietBuff())));
        player.nPoint.tlHp.add(getBaseOptionBuff(1) + chiSoBaseCongThem[1] + (doTinhKhiet + getDoTinhKhietBuff()));
        player.nPoint.tlMp.add(getBaseOptionBuff(2) + chiSoBaseCongThem[2] + (doTinhKhiet + getDoTinhKhietBuff()));
    }

    public float getTyLeToiHuyetThanhCong() {
        return 100 - (pham * 10) - (doTinhKhiet / 30f);
    }

    public void toiHuyet(int slTinhHuyet) {
        int slCong = 0;
        Item item = InventoryServiceNew.gI().findItemBag(player, 2077);
        if (item.quantity < (pham * 50)) {
            Service.gI().sendThongBao(player, "Cần x" + (pham * 50) + "Huyết đan cho 1 lần tôi huyết");
            return;
        }
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, pham * 50);
        InventoryServiceNew.gI().sendItemBags(player);

        while (slTinhHuyet > 0) {
            if (Util.isTrue(getTyLeToiHuyetThanhCong(), 100)) {
                slCong += 1;
            }
            slTinhHuyet--;
        }
        // toi huyet

        if (doTinhKhiet + slCong > 100) {
            doTinhKhiet = 100;
            Service.gI().sendThongBao(player, "Đã đạt độ tinh khiết tối đa hãy nâng phẩm để có thể tiếp tục");
        }
        Service.gI().sendThongBao(player, "Tôi huyết x" + slTinhHuyet + " Lần thành công x" + slCong + "Lần");
        Service.gI().point(player);
    }
}
