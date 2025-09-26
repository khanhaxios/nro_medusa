package com.girlkun.models.player.the_chat;

import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class BaseTheChat {
    public static final int MAX_PHAM = 5;
    public long exp;
    public long maxExp;
    public int giaiDoan;
    public int maxGiaiDoan;
    public float tyLePham;
    public int phamChat;
    public String tenTheChat;
    public long expTayTuy;
    public long maxExpTayTuy;
    public List<TheChatOption> theChatOptions = new ArrayList<>();
    public long[] MAX_EXP = new long[]{100_000, 1_000_000, 100_000_000, 1_000_000_000, 10_000_000_000L};
    public int type;
    public Player player;

    public BaseTheChat(Player player) {
        this.player = player;
    }

    public static BaseTheChat getTheChatByType(int type, Player player) {
        switch (type) {
            case 0:
                return new HoangCoThanhThe(player);
            case 1:
                return new AmDuongThanhThe(player);
        }
        return new BaseTheChat(player);
    }

    public void handleSpecial() {
    }

    public void handleTheChat() {
        // calc point
        for (TheChatOption theChatOption : theChatOptions) {
            switch (theChatOption.id) {
                case 0:
                    player.nPoint.dameAdd += theChatOption.param;
                    break;
                case 1:
                    player.nPoint.hpAdd += theChatOption.param;
                    player.nPoint.mpAdd += theChatOption.param;
                    break;
                case 2:
                    player.nPoint.tyLeGiamDame += theChatOption.param;
                    break;
                case 3:
                    player.nPoint.tlGiamDameChuan += theChatOption.param;
                    break;
                case 4:
                    player.nPoint.xTuVi += theChatOption.param;
                    break;
                case 5:
                    player.nPoint.dameAdd += player.nPoint.dameAdd * theChatOption.param / 100;
                    player.nPoint.mpAdd += player.nPoint.mpAdd * theChatOption.param / 100;
                    player.nPoint.hpAdd += player.nPoint.hpAdd * theChatOption.param / 100;
                    break;
            }
        }
    }

    public void updateTheChat() {
        // update the chat rieng biet thi se co nhung effect rieng biet cai nay override ben the chat rieng biet nhe
    }

    public void addExp(long exp) {
        this.exp += exp;
    }

    public void addExpTayTuy(long expTayTuy) {
        this.expTayTuy += expTayTuy;
    }

    public long calculateExpTayTuy() {
        if (giaiDoan > MAX_EXP.length - 1) {
            return MAX_EXP[MAX_EXP.length - 1] * (10L * giaiDoan);
        }
        return MAX_EXP[giaiDoan] / 10;
    }

    public long calcMaxExp() {
        if (giaiDoan > MAX_EXP.length - 1) {
            return MAX_EXP[MAX_EXP.length - 1] * (10L * giaiDoan);
        }
        return MAX_EXP[giaiDoan];
    }

    public void restExp() {
        this.exp -= maxExp;
        if (this.exp < 0) {
            this.exp = 0;
        }
        this.maxExp = calcMaxExp();
    }

    public boolean isKichHoat() {
        return tenTheChat != null && giaiDoan >= 0;
    }

    public void showBaseMenu() {
        if (!isKichHoat()) {
            Service.gI().sendThongBao(player, "Bạn chưa kich hoạt thể chất");
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Thông tin thể chất").append("\n");
        stringBuilder.append("|5|").append(getNameTheChat()).append("\n");
        stringBuilder.append(getBuff()).append("\n");
        stringBuilder.append(getMoTaTheChat()).append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.BASE_MENU_TC, -1, stringBuilder.toString(), "Phá Giai", "Tẩy Tủy", "Đóng");
    }

    public void showMenuPhaGiai() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Phá Giai Thể Chất").append("\n");
        stringBuilder.append("|5|").append(getNameTheChat()).append("\n");
        stringBuilder.append("|5|Giai hiện tại : ").append(getGiaiDoanName()).append("\n");
        stringBuilder.append("|7|Giai kế tiếp : ").append(getGiaiDoanName(giaiDoan + 1)).append("\n");
        stringBuilder.append("|7|Tỷ lệ thành công : ").append(getPercentGiaiDoan()).append("%").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PHA_GIAI, -1, stringBuilder.toString(), "Phá Giai", "Đóng");
    }

    public void showMenuTayTuy() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("|7|Tẩy Tủy Thể Chất").append("\n");
        stringBuilder.append("|5|").append(getNameTheChat()).append("\n");
        stringBuilder.append("|5|Kinh nghiệm : ").append(getCurrentTayTuyExpAsString()).append("\n");
        stringBuilder.append("|7|Tỷ lệ thành công : ").append(getTyLeTayTuy()).append("%").append("\n");
        stringBuilder.append("|7|Tiến độ hiện tại : ").append(tyLePham).append("%").append("\n");
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TAY_TUY, -1, stringBuilder.toString(), "Phá Giai", "Đóng");
    }

    public void phaGiai() {
        if (exp < maxExp) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm");
            return;
        }
        if (giaiDoan + 1 > maxGiaiDoan) {
            Service.gI().sendThongBao(player, "Đã đạt giai tối đa");
            return;
        }
        if (!Util.isTrue(getPercentGiaiDoan(), 100)) {
            Service.gI().sendThongBao(player, "Phá giai thất bại");
            restExp();
            return;
        }
        giaiDoan += 1;
        buffOptions();
        restExp();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Phá giai thành công");
    }

    public void buffOptions() {
        for (TheChatOption theChatOption : theChatOptions) {
            theChatOption.param += getBuffOption(theChatOption.id, phamChat);
        }
    }

    public int getBuffOption(int id, int phamChat) {
        switch (id) {
            case 0:
                return Math.max(phamChat, 1) * Util.nextInt(10_000, 100_000);
            case 1:
                return Math.max(phamChat, 1) * Util.nextInt(20_000, 200_000);
            case 2:
                return Math.max(phamChat, 1) * Util.nextInt(1, 3);
            case 3:
                return Math.max(phamChat, 1) * Util.nextInt(1, 2);
            case 4:
                return Math.max(phamChat, 1) * Util.nextInt(10, 20);
            case 5:
                return Math.max(phamChat, 1) * Util.nextInt(5, 10);
        }
        return 0;
    }

    public void tayTuy() {
        if (expTayTuy < maxExpTayTuy) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm để tẩy tủy");
            return;
        }
        if (phamChat + 1 > MAX_PHAM) {
            Service.gI().sendThongBao(player, "Đã đạt phẩm tối đa");
            return;
        }
        // get item
        tyLePham += Util.nextInt(1, 2);
        if (tyLePham >= 100) {
            phamChat += 1;
            tyLePham = 0;
            Service.gI().point(player);
        }
        restExpPham();
        Service.gI().sendThongBao(player, "Tẩy tủy thành công");
    }

    public float getTyLeTayTuy() {
        return (float) expTayTuy / maxExpTayTuy * 100f;
    }

    public String getCurrentTayTuyExpAsString() {
        return Util.powerToString(expTayTuy) + "/" + Util.powerToString(maxExpTayTuy);
    }

    public float getPercentGiaiDoan() {
        return switch (giaiDoan) {
            case 0 -> 50;
            case 1 -> 10;
            case 2 -> 1;
            case 4 -> .3f;
            default -> .1f;
        };
    }

    public String getMoTaTheChat() {
        return "";
    }

    public String getBuff() {
        StringBuilder opts = new StringBuilder();
        for (TheChatOption theChatOption : theChatOptions) {
            opts.append("|2|").append(theChatOption.getName()).append("\n");
        }
        return opts.toString();
    }

    public String getNameTheChat() {
        return String.format("[%s]%s[%s]", getPhamName(), tenTheChat, getGiaiDoanName());
    }

    public String getPhamName() {
        switch (phamChat) {
            case 0:
                return "Phàm";
            case 1:
                return "Linh";
            case 2:
                return "Vương";
            case 3:
                return "Tiên";
            case 4:
                return "Đế";
            case 5:
                return "Thánh";
        }
        return "Không xác định";
    }

    public String getGiaiDoanName() {
        switch (giaiDoan) {
            case 0:
                return "Nhập môn";
            case 1:
                return "Tiểu Thành";
            case 2:
                return "Đại Thành";
            case 3:
                return "Viên Mãn";
            case 4:
                return "Đăng Phong Tạo Cực";
        }
        return "Đăng Phong Tạo Cực";
    }

    public String getGiaiDoanName(int giaiDoan) {
        switch (giaiDoan) {
            case 0:
                return "Nhập môn";
            case 1:
                return "Tiểu Thành";
            case 2:
                return "Đại Thành";
            case 3:
                return "Viên Mãn";
            case 4:
                return "Đăng Phong Tạo Cực";
        }
        return "Đăng Phong Tạo Cực";
    }

    public BaseTheChat kichHoat() {
        boolean canActive = true;
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            canActive = player.tuTien.level > 2;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            canActive = player.tuMa.level >= 20;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenTheReal()) {
            canActive = player.luyenThe.level >= 100;
        }
        if (player.session.congduc - 100 < 0) {
            Service.gI().sendThongBao(player, "Cần 100 công đức để kích hoạt thể chất");
            return null;
        }
        if (!canActive) {
            Service.gI().sendThongBaoOK(player, "Bạn cần đạt nghề nghiệp cấp 2 ( Kim đan , Ma đan , tầng 200) để kích hoạt thể chất");
            return null;
        }
        PlayerDAO.subCongDuc(player, 100);
        int type = Util.nextInt(0, 1);
        BaseTheChat theChat = new BaseTheChat(player);
        switch (type) {
            case 0:
                return new HoangCoThanhThe(player);
            case 1:
                return new AmDuongThanhThe(player);
            case 2:
                return new BaseTheChat(player);
        }
        theChat.type = type;
        theChat.phamChat = 0;
        if (Util.isTrue(10, 100)) {
            theChat.phamChat = 1;
        }
        if (Util.isTrue(5, 100)) {
            theChat.phamChat = 2;
        }
        if (Util.isTrue(1, 100)) {
            theChat.phamChat = 3;
        }
        if (Util.isTrue(.3f, 100)) {
            theChat.phamChat = 4;
        }
        theChat.restExp();
        theChat.restExpPham();
        theChat.giaiDoan = 0;
        theChat.ratioNewBuff(Util.nextInt(1, 3), theChat.phamChat);
        return theChat;
    }

    private void ratioNewBuff(int i, int phamChat) {
        for (int i1 = 0; i1 < i; i1++) {
            int id = TheChatOption.TC_OPTIONS.stream().map(tc -> tc.id).toList().get(Util.nextInt(0, TheChatOption.TC_OPTIONS.size() - 1));
            theChatOptions.add(TheChatOption.init(i, getBuffOption(id, phamChat)));
        }
    }

    private void restExpPham() {
        this.expTayTuy -= maxExpTayTuy;
        if (expTayTuy < 0) {
            expTayTuy = 0;
        }
        maxExpTayTuy = calculateExpTayTuy();
    }
}
