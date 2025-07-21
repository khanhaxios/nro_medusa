package com.girlkun.models.player.huyet_mach;

import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tuma.TuMa;
import com.girlkun.models.player.tutien.luyenkhi.TuTien;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class Mach {
    private static final String[] BAC_NAME = new String[]{"Khí", "Lực", "Thần", "Ngự", "Hộ", "Linh", "Ma", "Thiên", "Ngọc"};

    public int tang;
    public byte bac;
    private int MAX_BASE_POINT_ATK = 1200;
    private int MAX_BASE_POINT_LINH_KHI_HOI = 1200;
    private int MAX_BASE_POINT_HP = 2600;
    private int MAX_BASE_POINT_MP = 2600;
    private int MAX_BASE_POINT_ATK_PERCENT = 2;
    private int MAX_BASE_POINT_HP_PERCENT = 2;
    private int MAX_BASE_POINT_MP_PERCENT = 2;
    private int MAX_BASE_POINT_SSS = 2;
    private int MAX_BASE_POINT_M = 10_000;

    public double dameBuff;
    public double hpBuff;
    public double mpBuff;
    public double linhKhiBuff;

    public int atkPercentBuff;
    public int hpPercentBuff;
    public int mpPercentBuff;
    public int sssPercentBuff;
    public int mAtkBuff;

    public long exp;
    public long maxExp;

    public Player player;
    public boolean isOpen = false;

    public Mach(Player player) {
        this.player = player;
    }

    public void addExp(long exp) {
        this.exp += exp;
        if (this.exp > maxExp) {
            this.exp = maxExp;
        }
    }


    public void restExp() {
        this.exp = 0;
        this.maxExp = calcMaxExp();
    }

    public long calcMaxExp() {
        return ((Math.max(1, tang)) * 1000L) + Math.max(1, bac) * 10;
    }

    public void kichHoatMach() {
        if (isOpen) {
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
            if (player.tuMa.isTuMa() && player.tuMa.level < 10) {
                Service.gI().sendThongBao(player, "Cần đạt " + TuMa.CANH_GIOI[10] + " Để kích hoạt huyết mạch");
                return;
            }
            if (player.luyenThe.isLuyenTheReal() && player.luyenThe.level < 100) {
                Service.gI().sendThongBao(player, "Cần đạt luyện thể tầng " + 100 + " Để kích hoạt huyết mạch");
                return;
            }
        }
        if (player.session.vnd - 100_000 < 0) {
            Service.gI().sendThongBao(player, "Cần 300k điểm để mở");
            return;
        }
        PlayerDAO.subvnd(player, 300_000);
        isOpen = true;
        bac += 1;
        tang = 0;
        restExp();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Kích hoạt mạch thành công");
    }

    public void nangBac() {
        // nang bac 100% thanh cong khi du exp
        if (!canLevelUpBac()) {
            if (bac + 1 > 8) {
                Service.gI().sendThongBao(player, "Bậc đạt tối đa hãy đột phá tầng");
                return;
            }
            if (exp != maxExp) {
                Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm");
                return;
            }
        }
        bac += 1;
        restExp();
        // add buff first
        buff();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Nâng bậc thành công");
    }

    public void buff() {
        switch (bac) {
            case 0:
                dameBuff += MAX_BASE_POINT_ATK + ((double) (MAX_BASE_POINT_ATK * (10 * tang)) / 100);
                break;
            case 1:
                hpBuff += MAX_BASE_POINT_HP + ((double) (MAX_BASE_POINT_HP * (10 * tang)) / 100);
                break;
            case 2:
                mpBuff += MAX_BASE_POINT_MP + ((double) (MAX_BASE_POINT_MP * (10 * tang)) / 100);
                break;
            case 3:
                linhKhiBuff += MAX_BASE_POINT_LINH_KHI_HOI + ((double) (MAX_BASE_POINT_LINH_KHI_HOI * (10 * tang)) / 100);
                break;
            case 4:
                atkPercentBuff += MAX_BASE_POINT_ATK_PERCENT * tang;
                break;
            case 5:
                hpPercentBuff += MAX_BASE_POINT_HP_PERCENT * tang;
                break;
            case 6:
                mpPercentBuff += MAX_BASE_POINT_MP_PERCENT * tang;
                break;
            case 7:
                sssPercentBuff += MAX_BASE_POINT_SSS * Math.max(tang / 3, 1);
                break;
            case 8:
                mAtkBuff += MAX_BASE_POINT_M * Math.max((tang / 3), 1);
                break;
        }
    }

    private boolean canLevelUpBac() {
        return tang + 1 <= 9 && exp == maxExp;
    }

    public void dotPhaTang() {
        if (bac != 8) {
            Service.gI().sendThongBao(player, "Cần đạt bậc 9 của tầng này trước đã");
            return;
        }
        if (tang + 1 > 99) {
            Service.gI().sendThongBao(player, "Đã đạt số tầng tối đa");
            return;
        }
        if (Util.isTrue(getTyLeTangBac(), 100)) {
            tang += 1;
            bac = 0;
            buff();
            Service.gI().sendThongBao(player, "Nâng bậc thành công");
            // calc buff 0 tang
        } else {
            Service.gI().sendThongBao(player, "Nâng bậc thất bại");
        }
        restExp();
    }

    public String getNameByTang() {
        if (tang >= 0 && tang <= 10) {
            return "Nhân[" + (tang) + "][" + bac + "]";
        }
        if (tang > 10 && tang <= 20) {
            return "Thông[" + (tang) + "][" + bac + "]";
        }
        if (tang > 20 && tang <= 30) {
            return "Linh[" + (tang) + "][" + bac + "]";
        }
        if (tang > 30 && tang <= 40) {
            return "Hoàng[" + (tang) + "][" + bac + "]";
        }
        if (tang > 40 && tang <= 50) {
            return "Thiên Tuyền[" + (tang) + "][" + bac + "]";
        }
        if (tang > 50 && tang <= 60) {
            return "Thông Thiên[" + (tang) + "][" + bac + "]";
        }
        if (tang > 60 && tang <= 70) {
            return "Thiên Hồn[" + (tang) + "][" + bac + "]";
        }
        if (tang > 70 && tang <= 80) {
            return "Lục Đạo[" + (tang) + "][" + bac + "]";
        }
        if (tang > 80 && tang <= 90) {
            return "Quy Nhất[" + (tang) + "][" + bac + "]";
        }
        if (tang > 90 && tang < 99) {
            return "Hư Vô[" + (tang) + "][" + bac + "]";
        }
        if (tang == 99) {
            return "Thần mạch[99]";
        }
        return "Không xác định";
    }

    public String getNameByTang(int tang, int bac) {
        if (tang >= 0 && tang <= 10) {
            return "Nhân[" + (tang) + "][" + bac + "]";
        }
        if (tang > 10 && tang <= 20) {
            return "Thông[" + (tang) + "][" + bac + "]";
        }
        if (tang > 20 && tang <= 30) {
            return "Linh[" + (tang) + "][" + bac + "]";
        }
        if (tang > 30 && tang <= 40) {
            return "Hoàng[" + (tang) + "][" + bac + "]";
        }
        if (tang > 40 && tang <= 50) {
            return "Thiên Tuyền[" + (tang) + "][" + bac + "]";
        }
        if (tang > 50 && tang <= 60) {
            return "Thông Thiên[" + (tang) + "][" + bac + "]";
        }
        if (tang > 60 && tang <= 70) {
            return "Thiên Hồn[" + (tang) + "][" + bac + "]";
        }
        if (tang > 70 && tang <= 80) {
            return "Lục Đạo[" + (tang) + "][" + bac + "]";
        }
        if (tang > 80 && tang <= 90) {
            return "Quy Nhất[" + (tang) + "][" + bac + "]";
        }
        if (tang > 90 && tang < 99) {
            return "Hư Vô[" + (tang) + "][" + bac + "]";
        }
        if (tang == 99) {
            return "Thần mạch[99]";
        }
        return "Không xác định";
    }

    public float getTyLeTangBac() {
        return 100f / (Math.max(tang, 2) * Math.max(2, bac));
    }

    public String getBuffByBac() {
        switch (bac) {
            case 0:
                return "+ Tấn công";
            case 1:
                return "+  HP";
            case 2:
                return "+ KI";
            case 3:
                return "+ Tấn công %";
            case 4:
                return "+ HP %";
            case 5:
                return "+ KI %";
            case 6:
                return "+ Linh khí hồi";
            case 7:
                return "+ SSS Dame";
            case 8:
                return "+ M Dame";
        }
        return "+ Không xác định";
    }

    public String getBuffByBac(byte bac) {
        switch (bac) {
            case 0:
                return "+ Tấn công";
            case 1:
                return "+  HP";
            case 2:
                return "+ KI";
            case 3:
                return "+ Tấn công %";
            case 4:
                return "+ HP %";
            case 5:
                return "+ KI %";
            case 6:
                return "+ Linh khí hồi";
            case 7:
                return "+ SSS Dame";
            case 8:
                return "+ M Dame";
        }
        return "+ Không xác định";
    }

    public void showBaseMenu() {
        if (!isOpen) {
            Service.gI().sendThongBao(player, "Bạn cần mở mạch để xem");
            return;
        }
        int nextBac = bac + 1 <= 8 ? bac + 1 : 0;
        String nameNext = getNameByBac(nextBac);
        String menuText = "|7|Thông tin mạch\n" + "|7|" + getNameByTang() + "\n" + "|5|Kinh nghiệm : " + getCurrentExpAsString() + "\n" + "|5|Tỷ lệ đột phá " + getTyLeTangBac() + "\n"
                + "|5|Bậc hiện tại : " + getNameByBac() + "\n"
                + "|5|Cấp bậc tiếp theo : " + nameNext + "[" + getBuffByBac() + "]" + "\n"
                + "|7|Khi đầy exp nhấn đột phá bậc , khi đạt bậc 9 hãy đột phá tầng";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MACH, -1, menuText, "Thông tin", "Phá tầng", "Thông mạch", "Đóng");
    }


    private String getNameByBac() {
        return BAC_NAME[bac] + "[" + bac + 1 + "]";
    }

    private String getNameByBac(int bac) {
        if (bac < 0 || bac > 8) {
            return "Không xác định";
        }
        return BAC_NAME[bac] + "[" + bac + 1 + "]";
    }

    public void showMenuThongTin() {
        StringBuilder menuText = new StringBuilder();
        menuText.append("|7|Thông tin Mạch\n");
        menuText.append(getNameByTang());
        menuText.append("|5|Tấn công +").append(dameBuff).append("\n");
        menuText.append("|5|Hp +").append(hpBuff).append("\n");
        menuText.append("|5|Ki +").append(mpBuff).append("\n");
        menuText.append("|5|Tấn công +").append(atkPercentBuff).append("%").append("\n");
        menuText.append("|5|Hp +").append(hpPercentBuff).append("%").append("\n");
        menuText.append("|5|Ki +").append(mpPercentBuff).append("%").append("\n");
        menuText.append("|5|Tốc độ hồi LK +").append(linhKhiBuff).append("\n");
        menuText.append("|5|Dame SSS +").append(sssPercentBuff).append("%").append("\n");
        menuText.append("|5|Dame M +").append(mAtkBuff).append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TT_MACH, -1, menuText.toString(), "Đóng");
    }

    private String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    public void showMenuTangBac() {
        int t = Math.min(bac + 1, 8);
        StringBuilder menuText = new StringBuilder();
        menuText.append("|7|Thông mạch").append("\n");
        menuText.append("|5|Bậc hiện tại : ").append(getNameByBac()).append("\n");
        menuText.append("|5|Bậc tiếp theo : ").append(getNameByBac(t)).append("[").append(getBuffByBac((byte) t)).append("]").append("\n");
        menuText.append("|5|Kinh nghiệm : ").append(getCurrentExpAsString()).append("\n");
        menuText.append("|7|Đột phá sẽ có 100% thành công").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_MACH_NANG_BAC, -1, menuText.toString(), "Thông mạch", "Đóng");
    }

    public void showMenuDotPhaTang() {
        int t = Math.min(tang + 1, 99);
        String menuText = "|7|Đột phá Tầng" + "\n" +
                "|5|Tầng hiện tại : " + getNameByTang() + "\n" +
                "|5|Tầng tiếp theo : " + getNameByTang(t, 0) + "\n" +
                "|7|Tỷ lệ đột phá :" + getTyLeTangBac() + "\n" +
                "|7|Đột phá thất bại sẽ mất hết kinh nghiệm" + "\n";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CONFIRM_DOT_PHA_MACH_TANG, -1, menuText, "Đột phá", "Đóng");
    }

    public void update() {
        if (isOpen) {
            if (exp < maxExp) {
                long expToAdd = (long) tang * Util.nextInt(1, 3) + (long) bac * Util.nextInt(1, 2);
                addExp(expToAdd);
            }
        }
    }

    public void calcPoint() {
        player.nPoint.dameAdd += dameBuff;
        player.nPoint.hpAdd += hpBuff;
        player.nPoint.mpAdd += mpBuff;
        player.nPoint.dameAdd += player.nPoint.dameAdd * atkPercentBuff / 100;
        player.nPoint.hpAdd += player.nPoint.hpAdd * hpPercentBuff / 100;
        player.nPoint.mpAdd += player.nPoint.mpAdd * mpPercentBuff / 100;
        player.nPoint.dameAdd += player.nPoint.dameAdd * sssPercentBuff / 80;
        player.nPoint.dameAdd += 10_000 * mAtkBuff;
    }
}
