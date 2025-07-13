package com.girlkun.models.player.phapbao;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.girlkun.models.player.phapbao.PhapBaoFactory.phapBaoNames;
import static com.girlkun.models.player.phapbao.PhapBaoFactory.vuKhiNames;

public class PhapBao implements Cloneable {

    public List<Byte> dongKhoa = new ArrayList<>();
    private static final byte MAX_LEVEL = 18;
    private static final int MAX_PHAM = 12;
    public static int[] OPTION_SSS_VIP_CAN_ROLL = new int[]{41, 42, 43, 45, 49, 44};
    public static int[] OPTION_VIP_CAN_ROLL = new int[]{0, 2, 5, 22, 23, 50, 77, 103, 251, 252};
    public static int[] OPTION_CAN_ROLL = new int[]{6, 7, 14, 17, 18};

    public Player player;

    public List<Item.ItemOption> options;
    private String id;
    private String name;
    private byte level = 0;
    private long exp;
    private long maxExp;
    private byte pham;
    private byte type;
    private long expNangCap;
    private long maxExpNangCap;

    private byte subType;

    @Override
    public PhapBao clone() {
        try {
            return (PhapBao) super.clone();  // shallow copy
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }

    public PhapBao(Player player) {
        this.player = player;
        options = new ArrayList<>();
        this.id = UUID.randomUUID().toString();
    }

    public void addExp(long exp) {
        this.exp += exp;
    }

    public void lenPham() {
        // du exp là cho lên
        if (exp < maxExp) {
            Service.gI().sendThongBao(player, "Cần đầy kinh nghiệm để lên phẩm");
            return;
        }
        if (pham + 1 > MAX_PHAM) {
            Service.gI().sendThongBao(player, "Phẩm chất đã đạt tối đa");
            return;
        }
        pham += 1;
        // tang chi so dong
        buffAllChiSo();
        restExp();
        Service.gI().sendThongBao(player, "Lên phẩm thành công");
        showBaseMenu();     // gọi hàm sau khi đợi
    }

    public byte getColorPhapBaoByPhamChat() {
        if (pham >= 0 && pham < 4) {
            return 2;
        }
        if (pham >= 4 && pham < 8) {
            return 5;
        }
        if (pham >= 8 && pham <= 12) {
            return 7;
        }
        return 1;
    }

    public String getThuocTinhPhapBaoAsString() {
        StringBuilder text = new StringBuilder();
        for (Item.ItemOption option : options) {
            text.append("|5|").append(option.getOptionString()).append("\n");
        }
        return text.toString();
    }

    public void showBaseMenu() {
        String text = "|7|Thông tin pháp bảo\n|" + getColorPhapBaoByPhamChat() + "|" + "\n" + getFullName() + "\n" + getThuocTinhPhapBaoAsString() + "Exp : " + getCurrentExp() + "\n" + "Exp cường hóa : " + getCurrentExpNc() + "\n" + "|7|Tỷ lệ nâng cấp : " + getTyLeNangCap() + "%" + "\n" + "|7|Chiến lực : " + Util.powerToString(danhGiaLucChienPhapBao()) + "\n" + "|7|Mỗi lên phẩm tăng một chút thuộc tính\nPhẩm càng cao thuộc tính càng mạnh" + "\n" + "|2|Bạn muốn?";
        player.iDMark.typePhapBaoHandling = type;
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PHAP_BAO, -1, text, "Nâng phẩm", "Nâng cấp", "Tinh dòng", "Vứt bỏ", "Đóng");
    }

    private String getCurrentExpNc() {
        return Util.powerToString(expNangCap) + "/" + Util.powerToString(maxExpNangCap);
    }

    public String getCurrentExp() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    public void buffAllChiSo() {
        for (Item.ItemOption option : options) {
            long pointToAdd = getBuffByPham(option.optionTemplate.id, option.param);
            option.param += pointToAdd;
        }
    }

    public void restExpNangCap() {
        expNangCap -= maxExp;
        if (expNangCap < 0) {
            expNangCap = 0;
        }
        maxExpNangCap = getMaxExpNangCap();
    }

    public long getMaxExpNangCap() {
        return Math.max(1, 10_000);
    }

    public void addExpNangCap(long exp) {
        expNangCap += exp;
        if (expNangCap > maxExpNangCap) {
            expNangCap = maxExpNangCap;
        }
    }

    public void restExp() {
        exp -= maxExp;
        if (exp < 0) {
            exp = 0;
        }
        maxExp = getExpLenPham();
    }

    public long getExpLenPham() {
        return Math.max(1, pham) * Math.max(1, options.size()) * 12_000L;
    }

    public long getBuffByPham(int optionId, int originParam) {
        long pointToAdd = 0;
        if (pham >= 0 && pham < 4) {
            if (optionId == 0) {
                pointToAdd = originParam * 12L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 20L / 100;
            } else if (optionId == 14) {
                pointToAdd = Util.nextInt(4, 6);
            } else if (optionId == 17 || optionId == 18) {
                pointToAdd = originParam * 5L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, pham));
            }
        } else if (pham >= 4 && pham < 8) {
            if (optionId == 0) {
                pointToAdd = originParam * 20L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 30L / 100;
            } else if (optionId == 14) {
                pointToAdd = Util.nextInt(12, 20);
            } else if (optionId == 17 || optionId == 18) {
                pointToAdd = originParam * 7L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, pham));
            }
        } else if (pham >= 8 && pham < 10) {
            if (optionId == 0) {
                pointToAdd = originParam * 30L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 40L / 100;
            } else if (optionId == 14) {
                pointToAdd = Util.nextInt(25, 30);
            } else if (optionId == 17 || optionId == 18) {
                pointToAdd = originParam * 10L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, pham));
            }
        } else if (pham >= 10 && pham <= 12) {
            if (optionId == 0) {
                pointToAdd = originParam * 50L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 70L / 100;
            } else if (optionId == 14) {
                pointToAdd = Util.nextInt(10, 100);
            } else if (optionId == 17 || optionId == 18) {
                pointToAdd = originParam * 20L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, pham));
            }
        }
        return pointToAdd;
    }

    public void nuotPhapBao(PhapBao phapBao) {
        if (phapBao == null) {
            Service.gI().sendThongBao(player, "Pháp bảo không xác định");
            return;
        }
        if (phapBao.type != type) {
            Service.gI().sendThongBao(player, "Nuốt pháp bảo thất bại");
            return;
        }
        PhapBao phapBaoHienTai = player.phapBaos.get(type);
        if (phapBaoHienTai == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy pháp bảo");
            return;
        }
        addExp(phapBao.getExpPhanra());
        addExpNangCap(phapBao.getExpNangCapPhanra());
        Service.gI().sendThongBao(player, String.format("Đã nuốt pháp bảo %s thành công", phapBao.getFullName()));
    }

    public String getNameByPhamChat() {
        switch (pham) {
            case 0:
                return "Phàm khí";
            case 1:
                return "Linh khí";
            case 2:
                return "Pháp khí";
            case 3:
                return "Linh bảo";
            case 4:
                return "Pháp bảo";
            case 5:
                return "Tiên khí";
            case 6:
                return "Thánh khí";
            case 7:
                return "Thần khí";
            case 8:
                return "Tiên bảo";
            case 9:
                return "Thánh bảo";
            case 10:
                return "Thần bảo";
            case 11:
                return "Vương khí";
            case 12:
                return "Đế khí";
        }
        return "Vô phẩm";
    }

    public String getLevelName() {
        return "[" + level + "]";
    }

    public String getFullName() {
        return String.format("[%s]%s%s%s", getNameByPhamChat(), getPhapBaoNameByType(), name, getLevelName());
    }

    public String getPhapBaoNameByType() {
        if (type >= 0 && type < phapBaoNames.length) {
            return phapBaoNames[type];
        } else if (type == 4 && subType >= 0 && subType < vuKhiNames.length) {
            return vuKhiNames[subType];
        }
        return "[Vô Danh]";
    }


    public void dispose() {
        this.player = null;
    }

    private boolean isInArray(int[] arr, int value) {
        for (int i : arr) {
            if (i == value) return true;
        }
        return false;
    }

    public int danhGiaLucChienPhapBao() {
        int diem = 0;
        for (Item.ItemOption option : options) {
            int id = option.optionTemplate.id;

            if (isInArray(OPTION_SSS_VIP_CAN_ROLL, id)) {
                diem += 20;
            } else if (isInArray(OPTION_VIP_CAN_ROLL, id)) {
                diem += 10;
            } else if (isInArray(OPTION_CAN_ROLL, id)) {
                diem += 2;
            }
        }
        return diem;
    }

    public void rollDongPhapBao() {
        // can nguyen lieu la hong ngoc + da
        Item item = InventoryServiceNew.gI().findItemBag(player, 1263);
        int itemQuanNeed = getItemNeedQuantity();
        int rubyNeed = getRubyNeed();
        if (item == null || item.quantity < itemQuanNeed) {
            Service.gI().sendThongBao(player, "Cần x" + itemQuanNeed + " Huyết tinh thạch để luyện dòng");
            return;
        }
        if (player.inventory.ruby - rubyNeed < 0) {
            Service.gI().sendThongBao(player, "Cần x" + rubyNeed + " hồng ngọc để luyện dòng");
            return;
        }
        player.inventory.ruby -= rubyNeed;
        Service.gI().sendMoney(player);
        InventoryServiceNew.gI().subQuantityItemsBag(player, item, itemQuanNeed);
        InventoryServiceNew.gI().sendItemBags(player);
        // roll dong khong khoa
        rollDong();
        showBaseMenu();
    }

    private void rollDong() {
        // get dong ko khoa
        List<Byte> indexDongCanRoll = new ArrayList<>();
        options.sort((o1, o2) -> {
            String name1 = o1.optionTemplate.name != null ? o1.optionTemplate.name : "";
            String name2 = o2.optionTemplate.name != null ? o2.optionTemplate.name : "";
            return Integer.compare(name1.length(), name2.length());
        });
        for (byte i = 0; i < options.size(); i++) {
            boolean isLocked = false;
            for (byte b : dongKhoa) {
                if (i == b) {
                    isLocked = true;
                    break;
                }
            }
            if (!isLocked) {
                indexDongCanRoll.add(i);
            }
        }
        for (Byte aByte : indexDongCanRoll) {
            int index = aByte;
            if (index < 0 || index >= options.size()) continue;
            Item.ItemOption newOption = PhapBaoFactory.rollNewOption(options.size());
            options.set(index, newOption);
        }
        Service.gI().sendThongBao(player, "Tinh dòng thành công");
    }

    public void khoaDong(byte index) {
        if (dongKhoa.stream().filter(t -> t == index).toList().size() > 0) {
            dongKhoa.removeIf(f -> f == index);
            Service.gI().sendThongBao(player, "Đã mở khóa dòng " + index);
        } else {
            dongKhoa.add(index);
            Service.gI().sendThongBao(player, "Đã khóa dòng " + index);
        }
    }

    private int getRubyNeed() {
        int quantity = 1000;
        for (byte b : dongKhoa) {
            for (byte i = 0; i < options.size(); i++) {
                if (b == i) {
                    quantity *= 1.5;
                }
            }
        }
        return quantity;
    }

    public int getItemNeedQuantity() {
        byte quantity = 10;
        for (byte b : dongKhoa) {
            for (byte i = 0; i < options.size(); i++) {
                if (b == i) {
                    quantity *= 2;
                }
            }
        }
        return quantity;
    }

    public void calcPoint() {
        int dameAdd = 0;
        int dameSSSAdd = 0;
        for (Item.ItemOption io : options) {
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
                    player.nPoint.hpAdd += io.param * 1000;
                    player.nPoint.mpAdd += io.param * 1000;
                    break;
                case 108, 73:// fake
                    player.nPoint.tlNeDon += io.param;
                    break;
                case 18: // #% chính xác
                    player.nPoint.tlchinhxac += io.param;
                    break;
                case 5, 197, 220, 233: //+#% sức đánh chí mạng
                    player.nPoint.tlDameCrit.add(io.param);
                    break;
                case 6: //HP+#
                    player.nPoint.hpAdd += io.param;
                    break;
                case 7: //KI+#
                    player.nPoint.mpAdd += io.param;
                    break;
                case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                    player.nPoint.tlHutHpMpXQ += io.param;
                    break;
                case 14: //Chí mạng+#%
                    player.nPoint.critAdd += io.param;
                    break;
                case 19: //Tấn công+#% khi đánh quái
                    player.nPoint.tlDameAttMob.add(io.param);
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
                    player.tienLuc += io.param;
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
                    dameSSSAdd += io.param;
                    break;
                case 50: //Sức đánh+#%
                    dameAdd += io.param;
                    break;
                case 77, 194, 221: //HP+#%
                    player.nPoint.tlHp.add(io.param);
                    break;
                case 80: //HP+#%/30s
                    player.nPoint.tlHpHoi += io.param;
                    break;
                case 81: //MP+#%/30s
                    player.nPoint.tlMpHoi += io.param;
                    break;
                case 88, 101: //Cộng #% exp khi đánh quái
                    player.nPoint.tlTNSM.add(io.param);
                    break;
                case 94: //Giáp #%
                    player.nPoint.tlDef.add(io.param);
                    break;
                case 95: //Biến #% tấn công thành HP
                    player.nPoint.tlHutHp += io.param;
                    break;
                case 96: //Biến #% tấn công thành MP
                    player.nPoint.tlHutMp += io.param;
                    break;
                case 97: //Phản #% sát thương
                    player.nPoint.tlPST += io.param;
                    break;
                case 100: //+#% vàng từ quái
                    player.nPoint.tlGold += io.param;
                    break;
                case 103, 195, 222: //KI +#%
                    player.nPoint.tlMp.add(io.param);
                    break;
                case 104: //Biến #% tấn công quái thành HP
                    player.nPoint.tlHutHpMob += io.param;
                    break;
                case 105: //Vô hình khi không đánh quái và boss
                    player.nPoint.wearingVoHinh = true;
                    break;
                case 106: //Không ảnh hưởng bởi cái lạnh
                    player.nPoint.isKhongLanh = true;
                    break;
                // đối nghịch
                case 109: //Hôi, giảm #% HP
                    player.nPoint.tlHpGiamODo += io.param;
                    break;
                case 116: //Kháng thái dương hạ san
                    player.nPoint.khangTDHS = true;
                    break;
                case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                    player.nPoint.tlSDDep.add(io.param);
                    break;
                case 147, 196, 219, 232: //+#% sức đánh
                    player.nPoint.tlDame.add(io.param);
                    break;
                case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                    player.nPoint.tlSubSD += 50;
                    player.nPoint.tlTNSM.add(io.param);
                    player.nPoint.tlGold += io.param;
                    break;
                case 162: //Cute hồi #% KI/s bản thân và xung quanh
                    player.nPoint.mpHoiCute += io.param;
                    break;
                case 173: //Phục hồi #% HP và KI cho đồng đội
                    player.nPoint.tlHpHoiBanThanVaDongDoi += io.param;
                    player.nPoint.tlMpHoiBanThanVaDongDoi += io.param;
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

    public float getTyLeNangCap() {
        byte nextLevel = (byte) (level + 1);
        switch (nextLevel) {
            case 1:
                return 20;
            case 2:
                return 10;
            case 3:
                return 5;
            case 4:
                return 3;
            case 5:
                return 2.7f;
            case 6:
                return 2.5f;
            case 7:
                return 2.4f;
            case 8:
                return 2.3f;
            case 9:
                return 2.2f;
            case 10:
                return 2f;
            case 11:
                return 1.8f;
            case 12:
                return 1.5f;
            case 13:
                return 1.3f;
            case 14:
                return 1.2f;
            case 15:
                return 1.1f;
            case 16:
                return 1f;
            case 17:
                return .5f;
            case 18:
                return .3f;
        }
        return .3f;
    }

    public void nangCap() {
        if (expNangCap < maxExpNangCap) {
            Service.gI().sendThongBao(player, "Chưa đủ kinh nghiệm");
            return;
        }
        if (level + 1 > MAX_LEVEL) {
            Service.gI().sendThongBao(player, "Đã đạt cấp tối đa");
            return;
        }
        if (Util.isTrue(getTyLeNangCap(), 100)) {
            level += 1;
            restExpNangCap();
            rollBuff();
            Service.gI().sendThongBao(player, "Nâng cấp thành công");
        } else {
            Service.gI().sendThongBao(player, "Nâng cấp thất bại");
            restExpNangCap();
        }
        showBaseMenu();
    }

    public void rollBuff() {
        for (Item.ItemOption option : options) {
            option.param += getBuffAddNangCap(option.optionTemplate.id, option.param);
        }
    }

    private long getBuffAddNangCap(int optionId, int originParam) {
        long pointToAdd = 0;
        if (level >= 0 && level < 4) {
            if (optionId == 0) {
                pointToAdd = originParam * 3L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 6L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, level / 2));
            }
        } else if (level >= 4 && level < 8) {
            if (optionId == 0) {
                pointToAdd = originParam * 4L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 6L / 100;
            }
        } else if (level >= 8 && level < 10) {
            if (optionId == 0) {
                pointToAdd = originParam * 5L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 7L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, level / 2));
            }

        } else if (level >= 10 && level <= 18) {
            if (optionId == 0) {
                pointToAdd = originParam * 6L / 100;
            } else if (optionId == 2 || optionId == 6 || optionId == 7) {
                pointToAdd = originParam * 8L / 100;
            } else {
                pointToAdd = Math.max(1, Util.nextInt(1, level / 2));
            }
        }
        return pointToAdd;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Item.ItemOption> getOptions() {
        return options;
    }

    public void setOptions(List<Item.ItemOption> options) {
        this.options = options;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public byte getPham() {
        return pham;
    }

    public void setPham(byte pham) {
        this.pham = pham;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public long getExpNangCap() {
        return expNangCap;
    }

    public void setExpNangCap(long expNangCap) {
        this.expNangCap = expNangCap;
    }

    public void setMaxExpNangCap(long maxExpNangCap) {
        this.maxExpNangCap = maxExpNangCap;
    }

    public byte getSubType() {
        return subType;
    }

    public void setSubType(byte subType) {
        this.subType = subType;
    }

    public void initBase() {
        level = 0;
        pham = 0;
        restExpNangCap();
        restExp();
    }

    public String getBaseThuocTinh() {
        return "|7|Thông tin pháp bảo\n|" + getColorPhapBaoByPhamChat() + "|" + "\n" + getFullName() + "\n" + getThuocTinhPhapBaoAsString() + "|7|Chiến lực : " + Util.powerToString(danhGiaLucChienPhapBao()) + "\n" + "|2|Bạn muốn?";
    }

    public String getBaseThuocTinhHon() {
        return "|7|Thông tin pháp bảo\n|" + getColorPhapBaoByPhamChat() + "|" + "\n" + getFullName() + "\n" + getThuocTinhPhapBaoAsString() + "|7|Chiến lực : " + Util.powerToString(danhGiaLucChienPhapBao()) + "\n";
    }

    public String getBaseThuocTinhHonNoTitle() {
        return Util.getHonorialLine(12) +  "|" +  getColorPhapBaoByPhamChat() + "|" + "\n" + getFullName() + "\n" + getThuocTinhPhapBaoAsString() + "|7|Chiến lực : " + Util.powerToString(danhGiaLucChienPhapBao()) + "\n";
    }

    public String getBaseThuocTinhHon(PhapBao cu) {
        return "|7|Thông tin pháp bảo\n|" + getColorPhapBaoByPhamChat() + "|" + "\n" + getFullName() + "\n" + getThuocTinhPhapBaoAsString() + "|7|Chiến lực : " + Util.powerToString(danhGiaLucChienPhapBao()) + "\n" + cu.getBaseThuocTinhHonNoTitle();
    }

    public long getExpPhanra() {
        return Math.max(1, pham) * 12_000;
    }

    public long getExpNangCapPhanra() {
        return Math.max(1, level) * 10_000;
    }

    public void showMenuTinhDong() {
        player.iDMark.typePhapBaoHandling = type;
        String text = "|7|Tinh dòng pháp bảo\n|5|Tinh dòng pháp bảo giúp pháp bảo của bạn trở lên mạnh mẽ hơn";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TINH_DONG_PHAP_BAO, -1, text, "Tinh dòng", "Khóa đòng", "Đóng");
    }

    public List<String> getDong() {
        List<String> strings = new ArrayList<>();
        strings.add("Đóng");
        for (int i = 0; i < options.size(); i++) {
            String t = "Dòng " + (i + 1) + "\n";
            boolean isLocked = false;
            for (byte b : dongKhoa) {
                if (b == i) {
                    isLocked = true;
                    break;
                }
            }

            if (isLocked) {
                t += "Đã khóa";
            } else {
                t += "Mở";
            }
            strings.add(t);
        }
        return strings;
    }

    public void showMenuKhoaDong() {
        player.iDMark.typePhapBaoHandling = type;
        String text = "|7|Khóa dòng pháp bảo\n|5|Khóa dòng pháp bảo giúp bạn có dòng mong muốn\n" + getThuocTinhPhapBaoAsString();
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_KHOA_DONG_PHAP_BAO, -1, text, getDong().toArray(new String[]{}));
    }
}
