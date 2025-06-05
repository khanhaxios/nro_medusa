package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BasePoint;
import com.girlkun.models.player.tutien.base_tutien.CoDuyen;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;
import com.girlkun.models.player.tutien.base_tutien.TuTienTemplate;
import com.girlkun.services.NpcService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TuTien extends BasePoint implements IBaseAction {
    public byte xParam = 0;
    public long lastimeCoDuyen = System.currentTimeMillis();
    public CoDuyen currentCoDuyen;
    long lastTimeHoiPhuc = System.currentTimeMillis();
    long lastTimeAddExp = System.currentTimeMillis();
    long lastTimeAddDoTT = System.currentTimeMillis();
    private static final String[] CANH_GIOI = new String[]{"Luyện Khí", "Trúc Cơ", "Kim Đan", "Nguyên Anh", "Hóa Thần", "Phong Thánh", "Thần Chiếu", "Huyền Linh", "Quy Nguyên", "Du Tầm", "Không Luân", "Tam Thiên", "Tứ Trụ", "Dạ Ma Thiên Cảnh", "Tu Di Sơn Chủ", "Tinh Hà Thánh Nhân", "Thần Quỷ Mạt Trắc", "Đạo Lộ Chi Cảnh", "Thánh Tôn Chi Cảnh"};
    private static final long[] LEVEL_EXP = new long[]{100, 200, 500, 1000, 5000, 60000, 200000, 3000000, 5000000, 10000000, 12_000_000, 15_000_000, 18_000_000, 20_000_000, 25_000_000, 30_000_000, 40_000_000, 60_000_000, 80_000_000}; // 19
    private static final long[] SUB_LEVEL_EXP = new long[]{100, 150, 200, 250, 300, 320, 350, 360, 370, 399};
    private static final long[] BASE_LINH_KHI = new long[]{1000, 2000, 5000, 10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000, 20000000, 50000000, 100000000, 200000000, 500000000, 1000000000};
    private static final long[] BASE_SUB_LINH_KHI = new long[]{100, 200, 300, 400, 500, 600, 700, 800, 900, 1000};
    private static final long[] BASE_EXP_BUFF = new long[]{1, 2, 5, 10, 15, 20, 25, 50, 100, 120, 140, 200, 210, 230, 300, 350, 360, 400, 500};

    private static final long[] BASE_LINH_KHI_HOI_PHUC = new long[]{
            /* 0  */     20L,
            /* 1  */     40L,
            /* 2  */     70L,
            /* 3  */    100L,
            /* 4  */    200L,
            /* 5  */    500L,
            /* 6  */  1_000L,
            /* 7  */  2_000L,
            /* 8  */  5_000L,
            /* 9  */ 10_000L,
            /* 10 */ 20_000L,
            /* 11 */ 50_000L,
            /* 12 */100_000L,
            /* 13 */200_000L,
            /* 14 */500_000L,
            /* 15 */1_000_000L,
            /* 16 */2_000_000L,
            /* 17 */5_000_000L,
            /* 18 */10_000_000L};
    public long timeTuTien = 0;
    private static final float[] LEVEL_UP_PERCENT = new float[]{50, 30, 25, 20, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 1, .3f};
    public LinhCan linhCan;
    public List<TienPhap> tienPhaps;
    public byte MAX_SL_TIEN_PHAP = 1;
    public CongPhap congPhap;
    public List<TienPhap> tienPhapsUsed = new ArrayList<>();

    public boolean isAttackWithLinhCan = true;
    public boolean isKhongThi = true;

    public TuTien(Player player) {
        super(player);
        linhCan = new LinhCan(this);
        congPhap = new CongPhap(this);
        tienPhaps = new ArrayList<>();
        currentCoDuyen = null;
    }

    public byte getMaxSLTPByLV() {
        if (level > 4 && level <= 8) {
            return 2;
        }
        if (level > 8 && level <= 12) {
            return 4;
        }
        if (level > 12 && level <= 18) {
            return 8;
        }
        return 2;
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        float xDiem = getXDiemThienPhu();
        long baseDiem = 10;
        float tyLexDiemTuMob = Math.max(targetMob.buffTuTienLevel / level, 1);
        if (tyLexDiemTuMob > 10) {
            tyLexDiemTuMob /= 5;
        }

        long finalDiem = (long) ((baseDiem * tyLexDiemTuMob) * xDiem);
        finalDiem *= congPhap.xLinhKhiBuff;
        return finalDiem;
    }

    @Override
    public void levelUp() {
        if (subLevel == 10) {
            if (level < CANH_GIOI.length) {
                level++;
                subLevel = 1;
                if (player.iDMark.dotPhaThienDao) {
                    if (xParam <= 1) {
                        xParam = 2;
                    } else {
                        xParam++;
                    }
                }
                Service.gI().sendThongBao(player, "Chúc mừng bạn đã đột phá lên " + (player.iDMark.dotPhaThienDao ? "Thiên đạo " : "") + getFormatName());
            } else {
                Service.gI().sendThongBao(player, "Bạn đã đạt cấp tối đa.");
            }
        } else {
            subLevel++;
            Service.gI().sendThongBao(player, "Bạn đã đột phá lên " + getFormatName());
        }
        player.iDMark.dotPhaThienDao = false;
        restExp();
        restLinhKhi();
        Service.gI().point(player);
    }

    public void calcPoint() {
        // add buff vo day sau do se call lai point
        // buff dame goc o day
        player.nPoint.tlchinhxac += player.nPoint.tlchinhxac * getChinhXacBuff() / 100;
        player.nPoint.tlNeDon += player.nPoint.tlNeDon * getNeBuff() / 100;
        player.nPoint.tlHutHp += getHutHPBuff();
        player.nPoint.tlHutMp += getHutMPBuff();
        if (linhCan != null) {
            player.nPoint.tlDameCrit.add((int) linhCan.getThuocTinhLinhCan().getParam());
        }
        if (congPhap != null) {
            congPhap.calcPoint(player);
        }
    }

    @Override
    public void restExp() {
        exp = 0;
        this.maxExp = getNextLevelExp();
    }

    public void restLinhKhi() {
        maxLinhKhiPoint = calcMaxLinhKhiPoint();
        linhKhiPoint = maxLinhKhiPoint;
    }

    public void handleCoDuyen(CoDuyen coDuyen) {

    }

    public void randomizedCoDuyen() {
        if (Util.isTrue(0.005f, 100)) {
            if (currentCoDuyen == null) {
                currentCoDuyen = TuTienTemplate.CO_DUYEN.get(Util.nextInt(0, TuTienTemplate.CO_DUYEN.size() - 1));
                // tao bang co duyen
                String[] luaChonName = new String[currentCoDuyen.getLuaChons().size()];
                for (int i = 0; i < currentCoDuyen.getLuaChons().size(); i++) {
                    CoDuyen.LuaChon luaChon = currentCoDuyen.getLuaChons().get(i);
                    luaChonName[i] = luaChon.getTenLuaChon();
                }
                lastimeCoDuyen = System.currentTimeMillis();
                Service.gI().sendThongBao(player, "Khí vận đột xuất bạn gặp được cơ duyên");
                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_CO_DUYEN, -1, "|7|" + currentCoDuyen.getTenCoDuyen() + "\n" + "|5|" + currentCoDuyen.getMoTaCoDuyen(), luaChonName);
            }
        }
    }

    public void hoiPhucLinhKhi(long linhKhi) {
        if (linhKhiPoint < maxLinhKhiPoint) {
            addLinhKhi(linhKhi);
            PlayerService.gI().sendHoiPhucLinhKhi(player, linhKhi);
            PlayerService.gI().sendLinhKhiPoint(player);
        }
    }

    public void hoiPhucLinhKhi() {
        if (linhKhiPoint < maxLinhKhiPoint) {
            int lv = Math.min(level, BASE_LINH_KHI_HOI_PHUC.length - 1);
            long linhKhiCanHoiPhuc = ((BASE_LINH_KHI_HOI_PHUC[lv] * Math.max(1, congPhap.xTocDoKhoiPhucLinhKhi))) * Math.max(1, xParam);
            linhKhiCanHoiPhuc *= Util.nextInt(1, 2);
            addLinhKhi(linhKhiCanHoiPhuc);
            lastTimeHoiPhuc = System.currentTimeMillis();
            // send effect to server
            PlayerService.gI().sendHoiPhucLinhKhi(player, linhKhiCanHoiPhuc);
            PlayerService.gI().sendLinhKhiPoint(player);
        }
    }

    public long calcMaxLinhKhiPoint() {
        long la = BASE_LINH_KHI[level] + (BASE_LINH_KHI[level] * (Math.max(1, congPhap.xLinhKhiBuff))) + BASE_SUB_LINH_KHI[subLevel - 1];
        la += la * xParam;
        return (la + (la * congPhap.tlLinhKhiBuff / 100)) * Math.max(1, congPhap.xLinhKhiBuff);
    }

    @Override
    public void levelDown() {
        if (subLevel > 1) {
            subLevel--;
            Service.gI().sendThongBao(player, "Bạn đã lui bước xuống " + getFormatName());
        } else if (level > 0) {
            level--;
            subLevel = 10;
            Service.gI().sendThongBao(player, "Bạn đã lui bước xuống " + getFormatName());
        } else {
            Service.gI().sendThongBao(player, "Bạn đã bị phế");
        }
        restExp();
        Service.gI().point(player);
    }

    @Override
    public void resetLevel() {
        level = 0;
        subLevel = 1;
        restExp();
        Service.gI().point(player);
    }

    private float getBaseBuffByLevel(float multiplier) {
        return this.level * multiplier;
    }

    private float getSubLevelOtherBuff() {
        return Math.max(1f, this.subLevel * 1f);
    }

    private float getSubLevelOtherBuff(float pt) {
        return Math.max(pt, this.subLevel * pt);
    }

    private float getSubLevelHpMpBuff() {
        return Math.max(1.5f, this.subLevel * 1.5f);
    }

    @Override
    public float getLevelUpPercent() {
        if (level <= LEVEL_UP_PERCENT.length - 1) {
            return (getXDiemThienPhu() * 2) + LEVEL_UP_PERCENT[level];
        }
        return 1f;
    }

    @Override
    public void openSystem() {
        if (player.luyenThe.level < 10 && !player.isAdmin()) {
            Service.gI().sendThongBao(player, "Bạn cần đạt luyện thể cấp 10 để bắt đầu tu tiên");
            return;
        }
        level = 0;
        subLevel = 1;
        timeTuTien = System.currentTimeMillis();
        linhCan = ratioLinhCan();
        if (linhCan == null) {
            level = 0;
            subLevel = 0;
            restExp();
            return;
        }
        ratioThienPhu();
        restExp();
        Service.gI().point(player);
        Service.gI().sendThongBao(player, "Chúc mừng bạn đã mở hệ thống tu tiên");
    }

    public void ratioThienPhu() {
        if (Util.isTrue(10, 100)) {
            canCot = Util.nextInt(1, 500) + 500;
            ngoTinh = Util.nextInt(1, 250) + 250;
        } else if (Util.isTrue(15, 100)) {
            canCot = Util.nextInt(1, 200) + 200;
            ngoTinh = Util.nextInt(1, 100) + 100;
        } else if (Util.isTrue(50, 100)) {
            canCot = Util.nextInt(1, 30) + 100;
            ngoTinh = Util.nextInt(1, 15) + 50;
        } else {
            canCot = Util.nextInt(1, 10) + 50;
            ngoTinh = Util.nextInt(1, 5) + 25;
        }
    }

    public LinhCan ratioLinhCan() {
        if (Util.isTrue(10, 100)) {
            // thuoc tinh phong loi quang am
            int i = Util.nextInt(0, 3);
            String key = switch (i) {
                case 1 -> "L";
                case 2 -> "Q";
                case 3 -> "A";
                default -> "P";
            };
            ThuocTinhLinhCan thuocTinhLinhCan = getThuocTinhLinhCanByLinhCan(TuTienTemplate.LINH_CAN.get(key));
            thuocTinhLinhCan.setParam(thuocTinhLinhCan.ratioThuocTinhLinhCan());
            return new LinhCan(this, TuTienTemplate.LINH_CAN.get(key), thuocTinhLinhCan);
        } else if (Util.isTrue(20, 100)) {
            // 20% for kim hoac hoa
            int i = Util.nextInt(0, 1);
            String key = i == 0 ? "K" : "H";
            ThuocTinhLinhCan thuocTinhLinhCan = getThuocTinhLinhCanByLinhCan(TuTienTemplate.LINH_CAN.get(key));
            thuocTinhLinhCan.setParam(thuocTinhLinhCan.ratioThuocTinhLinhCan());
            return new LinhCan(this, TuTienTemplate.LINH_CAN.get(key), thuocTinhLinhCan);
        } else {
            // thuoc tinh moc thuy tho
            int i = Util.nextInt(0, 2);
            String key = switch (i) {
                case 0 -> "M";
                case 1 -> "TH";
                default -> "T";
            };
            // ratio thuoc tinh linh can
            ThuocTinhLinhCan thuocTinhLinhCan = getThuocTinhLinhCanByLinhCan(TuTienTemplate.LINH_CAN.get(key));
            if (thuocTinhLinhCan == null) {
                Service.gI().sendThongBao(player, "Trong quá trình mở sinh ra biến cố bạn đã mở tu tiên thất bại");
                return null;
            }
            thuocTinhLinhCan.setParam(thuocTinhLinhCan.ratioThuocTinhLinhCan());

            return new LinhCan(this, TuTienTemplate.LINH_CAN.get(key), thuocTinhLinhCan);
        }
    }

    public ThuocTinhLinhCan getThuocTinhLinhCanByLinhCan(byte id) {
        ThuocTinhLinhCan thuocTinhLinhCan = null;
        for (ThuocTinhLinhCan tinhLinhCan : TuTienTemplate.THUOC_TINH_BUFF_LINH_CAN) {
            if (tinhLinhCan.getLinhCanBatBuoc() == id) {
                thuocTinhLinhCan = tinhLinhCan;
            }
        }
        return thuocTinhLinhCan;
    }

    @Override
    public boolean canLevelUp() {
        return exp == maxExp;
    }

    public boolean hasLinhKhi() {
        return linhKhiPoint == maxLinhKhiPoint;
    }

    @Override
    public String getName() {
        return getFormatName();
    }

    @Override
    public String getCurrentExpAsString() {
        return Util.powerToString(exp) + "/" + Util.powerToString(maxExp);
    }

    @Override
    public float getDameBuff() {
        return (getBaseBuffByLevel(5) + (getSubLevelOtherBuff() * Math.max(1, level))) * Math.max(1, xParam);
    }

    @Override
    public float getHPMPBuff() {
        return (getBaseBuffByLevel(12) + (getSubLevelHpMpBuff() * Math.max(1, level))) * Math.max(1, xParam);
    }

    @Override
    public float getDefBuff() {
        return (getBaseBuffByLevel(5) + (getSubLevelOtherBuff() * Math.max(1, level))) * Math.max(1, xParam);
    }

    @Override
    public float getPSTBuff() {
        return getBaseBuffByLevel(2) + (getSubLevelOtherBuff(0.1f) * Math.max(1, level));
    }

    @Override
    public float getHutHPBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(0.1f) * Math.max(1, level));
    }

    @Override
    public float getHutMPBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(0.1f) * Math.max(1, level));
    }

    @Override
    public float getNeBuff() {
        return getBaseBuffByLevel(1) + (getSubLevelOtherBuff(0.1f) * Math.max(1, level));
    }

    @Override
    protected long getNextLevelExp() {
        int nextLv = Math.min(level, LEVEL_EXP.length - 1);
        int nextSubLv = Math.min(subLevel, SUB_LEVEL_EXP.length - 1);
        return (LEVEL_EXP[nextLv] + SUB_LEVEL_EXP[nextSubLv]) * Math.max(1, xParam);
    }

    @Override
    public float getChinhXacBuff() {
        return (getBaseBuffByLevel(1) + (getSubLevelOtherBuff() * Math.max(1, level))) * Math.max(1, xParam);
    }

    public void update() {
        if (isTuTien() && player.isPl()) {
            // dau tien la cong exp //
            if (exp < maxExp && !player.isDie() && Util.canDoWithTime(lastTimeAddExp, 3000)) {
                if (player.tuTien.congPhap != null && player.tuTien.congPhap.tenCongPhap != null) {
                    long expAdd = (long) (getXDiemThienPhu() * (BASE_EXP_BUFF[level] + (SUB_LEVEL_EXP[subLevel - 1] / 10)));
                    addExp(expAdd * Math.max(1, xParam));
                    PlayerService.gI().sendTuTienAddTuVi(player, expAdd);
                    PlayerService.gI().sendTuTienTuVi(player);
                    lastTimeAddExp = System.currentTimeMillis();
                }
            }
            if (congPhap.tenCongPhap != null && !player.isDie() && congPhap.doThuanThuc < congPhap.maxDoThuanThuc && Util.canDoWithTime(lastTimeAddDoTT, 3000)) {
                congPhap.autoAddDoTT();
            }
            if (maxLinhKhiPoint == 0) {
                maxLinhKhiPoint = calcMaxLinhKhiPoint();
            }
            if (congPhap.tenCongPhap != null && linhKhiPoint < maxLinhKhiPoint && !player.isDie() && Util.canDoWithTime(lastTimeHoiPhuc, 1000)) {
                hoiPhucLinhKhi();
            }
            if (!player.isDie()) {
                randomizedCoDuyen();
            }
            // tu dong use linh ky
            tienPhaps.forEach(TienPhap::useTienPhap);
            Iterator<TienPhap> iterator = tienPhaps.iterator();
            while (iterator.hasNext()) {
                Runnable r = iterator.next();
                new Thread(r).start();
                iterator.remove();
                break;
            }

        }
    }


    public void addExp(long pn) {
        this.exp += pn;
        if (exp > maxExp) {
            exp = maxExp;
        }
    }

    public String toJsonString() {
        return null;
    }

    public String getSubLevelName(byte lv) {
        if (lv > 0 && subLevel <= 3) {
            return "Sơ Kỳ[" + lv + "]";
        }
        if (lv > 3 && subLevel <= 6) {
            return "Trung Kỳ[" + lv + "]";
        }
        if (lv > 6 && subLevel <= 9) {
            return "Hậu Kỳ[" + lv + "]";
        }
        if (lv == 10) {
            return "Viên mãn[10]";
        }
        return "";
    }

    public String getNameByLevel(byte level) {
        if (level < 0 || level >= CANH_GIOI.length) return "Không xác định";
        return CANH_GIOI[level];
    }

    public String getCurrentLevelStr() {
        return CANH_GIOI[level] + " " + getSubLevelName(subLevel);
    }

    public String getFormatName() {
        return String.format("%s", getNameByLevel(level) + " " + getSubLevelName(subLevel));
    }

    @Override
    public String toString() {
        return String.format("TuTien{level=%d, subLevel=%d, exp=%d/%d, linhKhi=%d/%d}", level, subLevel, exp, maxExp, linhKhiPoint, maxLinhKhiPoint);
    }

    private CongPhap getCongPhapByLinhCan(int index) {
        List<CongPhap> congPhaps = new ArrayList<>();
        for (CongPhap phap : TuTienTemplate.CONG_PHAP) {
            if (phap.thuoctinh == index) {
                congPhaps.add(phap);
                break;
            }
        }
        return congPhaps.get(Util.nextInt(0, congPhaps.size() - 1));
    }

    public void hocCongPhap(int select) {
        if (!isTuTien()) {
            Service.gI().sendThongBao(player, "Bạn cần mở tu tiên trước");
            return;
        }
        if (congPhap.tenCongPhap != null) {
            Service.gI().sendThongBao(player, "Bạn đã học công pháp rồi mà.");
            return;
        }
        CongPhap cpTem = getCongPhapByLinhCan(select);
        if (cpTem.thuoctinh != linhCan.getLinhCanType()) {
            Service.gI().sendThongBao(player, "Công pháp không phù hợp với linh căn của bạn");
            return;
        }
        cpTem.tuTien = this;
        congPhap = cpTem;
        congPhap.ratioNewCongPhap();
        // random chi so
        Service.gI().sendThongBao(player, "Bạn đã học " + congPhap.getFullName());
    }

    public void ratioNewTienPhap() {
        MAX_SL_TIEN_PHAP = getMaxSLTPByLV();
        if (tienPhaps.size() + 1 > MAX_SL_TIEN_PHAP) {
            Service.gI().sendThongBao(player, "Bạn đã đạt giới hạn tiên pháp");
            return;
        }

        // add tien phap
        TienPhap tienPhap = null;
        List<TienPhap> canRandomTP = new ArrayList<>();
        // get tiem phap chua ton tai trong list tien phap
        for (TienPhap phap : TuTienTemplate.TIEN_PHAP) {
            boolean hasOwner = false;
            for (TienPhap tienPhap1 : tienPhaps) {
                if (phap.getId() == tienPhap1.getId()) {
                    hasOwner = true;
                }
            }
            if (!hasOwner) {
                canRandomTP.add(phap);
            }
        }
        if (canRandomTP.size() > 0) {
            tienPhap = canRandomTP.get(Util.nextInt(0, canRandomTP.size() - 1));
        }
        if (tienPhap == null) {
            Service.gI().sendThongBao(player, "Bạn đã học hết Tiên Pháp hiện có");
            return;
        }
        tienPhaps.add(tienPhap);
        Service.gI().sendThongBao(player, "Bạn đã học " + tienPhap.getName());
    }

    public void getInfoStr() {
        if (!player.tuTien.isTuTien()) {
            Service.gI().sendThongBaoOK(player, "Bạn cần mở tu tiên");
            return;
        }
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PLAYER_TU_TIEN, -1, "|7|Thông Tin Tu Tiên\n" + "|5|Cảnh Giới : " + getFormatName() + "\n" + "|5|Tu Vi : " + getCurrentExpAsString() + "\n" + "Linh Khí : " + Util.powerToString(linhKhiPoint) + "/" + Util.powerToString(maxLinhKhiPoint) + "\n|7|Căn Cốt : " + canCot + "\n" + "|7|Ngộ tính : " + ngoTinh + "\n" + "Thiên phú : " + getThienPhu() + "\nĐã tu luyện : " + getYearOpened() + "\n" + "|2|Cảnh giới tiếp theo : " + getNextLevelStr() + "\n" + "|1|Tỷ lệ đột phá : " + getLevelUpPercent() + "\n" + "|7|Cảnh giới càng cao tỷ lệ đột phá càng thấp" + "\n" + "|5|Đánh giá : " + pointForMe(), "Chức Năng\nTu Tiên", "Thông Tin\nCông Pháp", "Thông Tin\nTiên Pháp", "Thông Tin\nLinh Căn", "Cài đặt\nLinh Khí");
        // handle process string
    }

    public String pointForMe() {
        int diem = 0;
        diem += (level + (subLevel)) * level;
        congPhap.calcSlThuocTinh();
        diem += congPhap.slThuocTinh;
        diem += linhCan.getThuocTinhLinhCan().getParam();
        diem += (tienPhaps.size() * 5);
        String[] danhGia = new String[]{"Yếu Gà Một Cái", "Có Chút Thành Tựu", "Uy Trấn Một Phương", "Vang Danh Vạn Cổ", "Đạp Nát Tinh Không"};
        if (diem > 10000) {
            return danhGia[4];
        }
        if (diem > 5000) {
            return danhGia[3];
        }
        if (diem > 1000) {
            return danhGia[2];
        }
        if (diem > 500) {
            return danhGia[1];
        }
        if (diem > 100) {
            return danhGia[0];
        }
        return danhGia[0];
    }

    public String getNextLevelStr() {
        if (this.subLevel + 1 > 9) {
            if (level + 1 < CANH_GIOI.length) {
                return CANH_GIOI[level + 1] + " " + getSubLevelName((byte) 1);
            }
            return CANH_GIOI[CANH_GIOI.length - 1] + " " + getSubLevelName((byte) 10);
        }
        return CANH_GIOI[level] + " " + getSubLevelName((byte) (subLevel + 1));
    }

    private String getYearOpened() {
        long timeUsedMillis = System.currentTimeMillis() - timeTuTien;
        long minutesUsed = timeUsedMillis / (1000 * 60);
        long yearsOpened = minutesUsed / 10;

        return Util.powerToString(yearsOpened) + " năm";
    }

    public boolean isTuTien() {
        if (level == 0) {
            return subLevel > 0;
        } else if (level > 0) {
            return true;
        }
        return false;
    }

    public void showMenuTuTien() {
        String npcSay = "|7|Thông tin thuộc tính\n" + "|2|Hp,Mp : " + getHPMPBuff() + "%" + "\n" + "|2|Dame :" + getDameBuff() + "%" + "\n" + "|1|Def : " + getDefBuff() + "%" + "\n" + "|1|Né : " + getNeBuff() + "%" + "\n" + "|1|Chính Xác : " + getChinhXacBuff() + "%" + "\n" + "|7|Cảnh giới càng cao thuộc tính tăng càng mạnh";
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_PLAYER_TU_TIEN_F, -1, npcSay, "Đột Phá\nCảnh Giới", "Tán Công", "Đóng");
    }

    public void showMenuTienPhap() {
        // show thong tin tien phap buff
        StringBuilder npc = new StringBuilder();
        npc.append("|7|Thông Tin Tiên Pháp\n");
        for (TienPhap tienPhap : tienPhaps) {
            npc.append("|5|").append(tienPhap.getName()).append("\n");
        }
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TT_TIEN_PHAP, -1, npc.toString(), "Đóng");
    }

    public void tanCong() {
        if (level < 10) {
            Service.gI().sendThongBao(player, "Bạn cần đạt " + getNameByLevel((byte) 10) + " Để có thể tán công");
            return;
        }
        resetLevel();
        Service.gI().sendThongBao(player, "Đã Tán Công");
    }

    public void dispose() {
        tienPhapsUsed.clear();
        tienPhaps.clear();
        congPhap = null;
        linhCan = null;
        tienPhaps = null;
        tienPhapsUsed = null;
    }

    public void showCaiDatLinhKhi() {
        String text = "|7|Cài đặt linh khí\n" + "|5|Giúp mình điểu khiển cách dùng linh khí vào đâu";
        NpcService.gI().createMenuConMeo(player, ConstNpc.LINH_KHI_SETTING, -1, text, "STLC\n" + (isAttackWithLinhCan ? "Mở" : "Đóng"), "Khống Thi\n" + (isKhongThi ? "Mở" : "Đóng"), "Đóng");
    }

    public int getTyLeRoiDa() {
        // check luyen the level
        int percent = 0;
        if (player.luyenThe != null && player.luyenThe.isLuyenThe()) {
            percent += player.luyenThe.level / 4;
        }
        if (player.nguThuSu != null && player.nguThuSu.isNguThu()) {
            percent += player.nguThuSu.getLevel();
        }
        if (player.luyenKhiSu != null && player.luyenKhiSu.isLuyenKhiSu()) {
            percent += player.luyenKhiSu.getLevel();
        }
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            percent += player.tuTien.level;
        }
        if (percent <= 15) {
            return 15;
        }
        return percent;
    }

    public void handleHutChiSo() {
        if (congPhap != null) {
            if (congPhap.hutDame > 0) {
                congPhap.totalHutDame += congPhap.hutDame;
                if (congPhap.totalHutDame > congPhap.phamchat.maxHutDame) {
                    congPhap.totalHutDame = congPhap.phamchat.maxHutDame;
                } else {
                    Service.gI().sendThongBao(player, "Bạn được tăng cường " + congPhap.hutDame + " sức đánh từ việc tiêu diệt quái");
                }
            }
            if (congPhap.hutHp > 0) {
                congPhap.totalHutHp += congPhap.totalHutHp;
                if (congPhap.totalHutHp > congPhap.phamchat.maxHutHpMp) {
                    congPhap.totalHutHp = congPhap.phamchat.maxHutHpMp;
                }
            }
            if (congPhap.hutMp > 0) {
                congPhap.totalHutMp += congPhap.hutMp;
                if (congPhap.totalHutMp > congPhap.phamchat.maxHutHpMp) {
                    congPhap.totalHutMp = congPhap.phamchat.maxHutHpMp;
                }
            }
        }
    }

    public void rewnewLinhCanEffect() {
        if (linhCan != null) {
            if (Util.isTrue(1, 100)) {
                LinhCan oldLinhCan = linhCan;
                linhCan = ratioLinhCan();
                // renew cong phap
                if (oldLinhCan.getLinhCanType() != linhCan.getLinhCanType()) {
                    congPhap = new CongPhap(player.tuTien);
                }
            } else {
                linhCan.getThuocTinhLinhCan().setParam(linhCan.getThuocTinhLinhCan().ratioThuocTinhLinhCan());
            }
        }
    }
}
