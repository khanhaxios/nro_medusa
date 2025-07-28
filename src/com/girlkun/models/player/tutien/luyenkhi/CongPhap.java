package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.girlkun.models.player.tutien.luyenkhi.LinhCan.getLinhCanName;

@Data
public class CongPhap {
    private static int MAX_BUFF;
    private static final int[] MAX_HUT_DAME = {100, 1000, 10000, 20000, 30000, 50000, 70000, 100000};
    private static final int[] MAX_HUT_HP_MP = {20000, 30000, 50000, 60000, 70000, 120000, 150000, 200000};

    private static final long[] DO_TT = new long[]{100000, 500_000, 1_000_000, 10_000_00, 50_000_000, 100_000_000, 500_000_000, 1_000_000_000};
    TuTien tuTien;
    public byte id;
    public int tlHpBuff = 0;
    public int tlMpBuff = 0;
    public int tlDameBuff = 0;
    public int tlLinhKhiBuff = 0;
    public int tlHutHPBuff = 0;
    public int tlHutMPBuff = 0;
    public int tlAnCapVang = 0;
    public int hutDame = 0;
    public int hutHp = 0;
    public int hutMp = 0;
    public int totalHutHp = 0;
    public int totalHutMp = 0;
    public int totalHutDame = 0;
    public byte thuoctinh;
    public byte xDameThuocTinh = 0;
    public PhamChat phamchat = PhamChat.HOANG;
    public byte xLinhKhiBuff = 0;
    public byte xTocDoKhoiPhucLinhKhi = 0;
    public long doThuanThuc = 0;
    public long maxDoThuanThuc = 0;
    List<Runnable> thuocTinhList = new ArrayList<>();

    public String tenCongPhap;
    byte maxThuocTinh;
    byte slThuocTinh;
    public static byte MAX_THUOC_TINH = 13;

    public CongPhap(TuTien tuTien) {
        this.tuTien = tuTien;
        this.maxThuocTinh = getMaxThuocTinhByPhamChat();
        this.slThuocTinh = getSlThuocTinhHienTai();
        MAX_BUFF = (int) tuTien.getXDiemThienPhu() + 5;
    }

    public CongPhap(String name, byte thuoctinh) {
        this.tenCongPhap = name;
        this.thuoctinh = thuoctinh;
    }

    public byte getSlThuocTinhHienTai() {
        byte quan = 0;
        if (tlHpBuff > 0) quan += 1;
        if (tlMpBuff > 0) quan += 1;
        if (tlAnCapVang > 0) quan += 1;
        if (tlDameBuff > 0) quan += 1;
        if (tlHutHPBuff > 0) quan += 1;
        if (tlHutMPBuff > 0) quan += 1;
        if (tlLinhKhiBuff > 0) quan += 1;
        if (hutDame > 0) quan += 1;
        if (hutHp > 0) quan += 1;
        if (hutMp > 0) quan += 1;
        if (xDameThuocTinh > 0) quan += 1;
        if (xLinhKhiBuff > 0) quan += 1;
        if (xTocDoKhoiPhucLinhKhi > 0) quan += 1;
        return quan;
    }

    public int getMaxHutDameByPhamChat() {
        if (phamchat.id >= 0 && phamchat.id < MAX_HUT_DAME.length) {
            return MAX_HUT_DAME[phamchat.id];
        }
        return 100;
    }


    public int getMaxHutHpMPByPhamChat() {
        if (phamchat.id >= 0 && phamchat.id < MAX_HUT_HP_MP.length) {
            return MAX_HUT_HP_MP[phamchat.id];
        }
        return 2000;
    }

    public void addTotalHutDame(int pint) {
        this.totalHutDame += pint;
        if (totalHutDame > getMaxHutDameByPhamChat()) {
            totalHutDame = getMaxHutDameByPhamChat();
        }
    }

    public void addTotalHutHp(int pint) {
        this.totalHutHp += pint;
        if (totalHutHp > getMaxHutHpMPByPhamChat()) {
            totalHutHp = getMaxHutHpMPByPhamChat();
        }
    }

    public void addTotalHutMP(int pint) {
        this.totalHutMp += pint;
        if (totalHutMp > getMaxHutHpMPByPhamChat()) {
            totalHutMp = getMaxHutHpMPByPhamChat();
        }
    }

    public byte getMaxThuocTinhByPhamChat() {
        switch (phamchat.id) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
            case 3:
                return 6;
            case 4:
                return 7;
            case 5:
                return 9;
            case 6:
                return 12;
            case 7:
                return MAX_THUOC_TINH;
        }
        return 0;
    }

    public String getNameByPhamChat() {
        return phamchat.name;
    }

    public void tangPham(int percent) {
        if (Util.isTrue(percent, 100)) {
            // success linh ngo
            this.phamchat = phamchat.getNext();
            this.maxThuocTinh = getMaxThuocTinhByPhamChat();
        } else {
            restDoTT();
            Service.gI().sendThongBao(tuTien.player, "Lĩnh ngộ thất bại bạn mất hết độ thuần thục");
        }
    }

    public void tangPham() {
        if (this.phamchat.isMaxLevel()) {
            Service.gI().sendThongBao(tuTien.player, "Công pháp đã đạt phẩm chất tối đa");
            return;
        }
        // tu dai thanh la tang pham duoc
        long percent = (doThuanThuc * 100) / maxDoThuanThuc;
        if (percent < 80) {
            Service.gI().sendThongBao(tuTien.player, "Đột phá công pháp cần độ thuần thục đạt tới Đại Thành");
            return;
        }
        float successPercent = getTyLeLinhNgo();
        if (Util.isTrue(successPercent, 100)) {
            // success linh ngo
            int nextId = this.phamchat.id + 1;
            this.phamchat = PhamChat.fromId(nextId);
            this.maxThuocTinh = getMaxThuocTinhByPhamChat();
            randomNewBuff(3);
            upOldBuff();
            restDoTT();
        } else {
            restDoTT();
            Service.gI().sendThongBao(tuTien.player, "Lĩnh ngộ thất bại bạn mất hết độ thuần thục");
        }
    }

    public void ratioNewCongPhap() {
        // ratio buff for new cong phap
        calcSlThuocTinh();
        randomNewBuff(3);
        phamchat = PhamChat.HOANG;
        thuoctinh = tuTien.linhCan.getLinhCanType();
        restDoTT();
    }

    public void restDoTT() {
        doThuanThuc = 0;
        maxDoThuanThuc = getDoThuanThucByPhamChat(phamchat);
    }

    public void calcSlThuocTinh() {
        this.maxThuocTinh = getMaxThuocTinhByPhamChat();
        this.slThuocTinh = getSlThuocTinh();
    }

    public void upOldBuff() {
        if (tlHpBuff > 0) tlHpBuff += Math.min(tlHpBuff + Util.nextInt(1, 5), MAX_BUFF);

        if (tlMpBuff > 0) tlMpBuff += Math.min(tlMpBuff + Util.nextInt(1, 5), MAX_BUFF);

        if (tlAnCapVang > 0) tlAnCapVang += Math.min(tlAnCapVang + Util.nextInt(1, 3), MAX_BUFF);

        if (tlDameBuff > 0) tlDameBuff += Math.min(tlDameBuff + Util.nextInt(1, 2), MAX_BUFF);

        if (tlLinhKhiBuff > 0) tlLinhKhiBuff += Math.min(tlLinhKhiBuff + Util.nextInt(1), MAX_BUFF);

        if (hutDame > 0) hutDame += Math.min(hutDame + Util.nextInt(1, 3), MAX_BUFF);

        if (hutHp > 0) hutHp += Math.min(hutHp + Util.nextInt(1, 3), MAX_BUFF);

        if (hutMp > 0) hutMp += Math.min(hutMp + Util.nextInt(1, 3), MAX_BUFF);

        if (Util.isTrue(1, 100)) {
            if (xDameThuocTinh > 0) xDameThuocTinh += (byte) Math.min(xDameThuocTinh + Util.nextInt(1, 2), 100);
        }

        if (xLinhKhiBuff > 0) xLinhKhiBuff += (byte) Math.min(xLinhKhiBuff + Util.nextInt(1), 100);

        if (xTocDoKhoiPhucLinhKhi > 0)
            xTocDoKhoiPhucLinhKhi += (byte) Math.min(xTocDoKhoiPhucLinhKhi + Util.nextInt(1, 2), 100);
    }

    public void randomNewBuff(int soLuongMuonRandom) {
        if (this.slThuocTinh >= maxThuocTinh) return;

        boolean isAdd = Util.isTrue(getBaseTyLeLinhNgo(), 100);
        if (!isAdd) return;
        thuocTinhList.clear();

        byte countNew = 0;

        List<Runnable> thuocTinhList = new ArrayList<>();

        if (tlHpBuff == 0) thuocTinhList.add(() -> tlHpBuff = Util.nextInt(5, 12));
        if (tlMpBuff == 0) thuocTinhList.add(() -> tlMpBuff = Util.nextInt(5, 12));
        if (tlDameBuff == 0) thuocTinhList.add(() -> tlDameBuff = Util.nextInt(5, 12));
        if (tlLinhKhiBuff == 0) thuocTinhList.add(() -> tlLinhKhiBuff = Util.nextInt(5, 12));
        if (tlAnCapVang == 0) thuocTinhList.add(() -> tlAnCapVang = Util.nextInt(5, 12));
        if (hutDame == 0) thuocTinhList.add(() -> hutDame = Util.nextInt(5, 12));
        if (hutHp == 0) thuocTinhList.add(() -> hutHp = Util.nextInt(5, 12));
        if (hutMp == 0) thuocTinhList.add(() -> hutMp = Util.nextInt(5, 12));
        if (xDameThuocTinh == 0) thuocTinhList.add(() -> xDameThuocTinh = (byte) Util.nextInt(1, 4));
        if (xLinhKhiBuff == 0) thuocTinhList.add(() -> xLinhKhiBuff = (byte) Util.nextInt(1, 4));
        if (xTocDoKhoiPhucLinhKhi == 0) thuocTinhList.add(() -> xTocDoKhoiPhucLinhKhi = (byte) Util.nextInt(1, 4));
        Collections.shuffle(thuocTinhList);
        for (Runnable r : thuocTinhList) {
            if (countNew >= soLuongMuonRandom || (slThuocTinh + countNew) >= maxThuocTinh) break;
            r.run();
            countNew++;
        }
        calcSlThuocTinh();
        calcPoint(tuTien.player);
    }

    public int randomNewBuffA(int soLuongMuonRandom) {
        if (this.slThuocTinh >= maxThuocTinh) return 0;

        boolean isAdd = Util.isTrue(getBaseTyLeLinhNgo(), 100);
        if (!isAdd) return -1;
        thuocTinhList.clear();
        if (tlHpBuff == 0) thuocTinhList.add(() -> tlHpBuff = Util.nextInt(5, 12));
        if (tlMpBuff == 0) thuocTinhList.add(() -> tlMpBuff = Util.nextInt(5, 12));
        if (tlDameBuff == 0) thuocTinhList.add(() -> tlDameBuff = Util.nextInt(5, 12));
        if (tlLinhKhiBuff == 0) thuocTinhList.add(() -> tlLinhKhiBuff = Util.nextInt(5, 12));
        if (tlAnCapVang == 0) thuocTinhList.add(() -> tlAnCapVang = Util.nextInt(5, 12));
        if (hutDame == 0) thuocTinhList.add(() -> hutDame = Util.nextInt(5, 12));
        if (hutHp == 0) thuocTinhList.add(() -> hutHp = Util.nextInt(5, 12));
        if (hutMp == 0) thuocTinhList.add(() -> hutMp = Util.nextInt(5, 12));
        if (xDameThuocTinh == 0) thuocTinhList.add(() -> xDameThuocTinh = (byte) Util.nextInt(1, 4));
        if (xLinhKhiBuff == 0) thuocTinhList.add(() -> xLinhKhiBuff = (byte) Util.nextInt(1, 4));
        if (xTocDoKhoiPhucLinhKhi == 0) thuocTinhList.add(() -> xTocDoKhoiPhucLinhKhi = (byte) Util.nextInt(1, 4));

        int soLuongBuffCoTheThem = Math.min(soLuongMuonRandom, thuocTinhList.size());
        int countNew = 0;

        Collections.shuffle(thuocTinhList);
        for (int i = 0; i < soLuongBuffCoTheThem; i++) {
            thuocTinhList.get(i).run();
            countNew++;
        }
        calcSlThuocTinh();
        calcPoint(tuTien.player);

        return countNew;
    }


    public float getTyLeLinhNgo() {
        float tyle = getTyLeThuanThuc();
        switch (phamchat.id) {
            case 1:
                tyle += 1f;
                break;
            case 2:
                tyle += .8f;
                break;
            case 3:
                tyle += .7f;
                break;
            case 4:
                tyle += .6f;
                break;
            case 5:
                tyle += .5f;
                break;
            case 6:
                tyle += .4f;
                break;
            case 7:
                tyle += .3f;
                break;
        }
        tyle *= getBaseTyLeLinhNgo();
        return tyle;
    }

    public float getTyLeThuanThuc() {
        long percent = doThuanThuc / maxDoThuanThuc * 100;
        if (percent > 0 && percent <= 20) {
            return 1f;
        }
        if (percent > 20 && percent <= 40) {
            return 2f;
        }
        if (percent > 40 && percent <= 80) {
            return 3f;
        }
        if (percent == 100) {
            return 5f;
        }
        return 1f;
    }

    public float getBaseTyLeLinhNgo() {
        return tuTien.getXDiemNgoTinh() + 1;
    }

    public String getFullName() {
        return String.format("[%s]%s", getNameByPhamChat(), tenCongPhap);
    }

    public String getPecentName() {
        long percent = doThuanThuc / maxDoThuanThuc * 100;
        if (percent > 0 && percent <= 20) {
            return "Nhập môn";
        }
        if (percent > 20 && percent <= 40) {
            return "Tiểu Thành";
        }
        if (percent > 40 && percent <= 80) {
            return "Đại Thành";
        }
        if (percent == 100) {
            return "Viên mãn";
        }
        return "Nhập môn";
    }

    public void addDoThuanThuc(long dtt) {
        doThuanThuc += dtt;
        if (doThuanThuc > maxDoThuanThuc) {
            doThuanThuc = maxDoThuanThuc;
        }
    }

    public long getDoThuanThucByPhamChat(PhamChat phamchat) {
        return DO_TT[phamchat.id] * 2;
    }

    public String getCurrentExpStr() {
        return String.format("%s/%s", Util.powerToString(doThuanThuc), Util.powerToString(maxDoThuanThuc));
    }

    public String getFullNameWithPercent() {
        return String.format("[%s]%s", getNameByPhamChat(), tenCongPhap);
    }

    public void calcPoint(Player player) {
        // calc buff cong phap here
        player.nPoint.dameAdd += (long) ((player.nPoint.dameg + player.nPoint.dameAdd) * tlDameBuff / 100f);
        player.nPoint.hpAdd += (long) ((player.nPoint.hpg + player.nPoint.hpAdd) * tlHpBuff / 100f);
        player.nPoint.mpAdd += (long) ((player.nPoint.mpg + player.nPoint.mpAdd) * tlMpBuff / 100f);
        player.nPoint.tlHutHp += tlHutHPBuff;
        player.nPoint.tlHutMp += tlHutMPBuff;
        player.nPoint.dameAdd += totalHutDame;
        player.nPoint.hpAdd += totalHutHp;
        player.nPoint.mpAdd += totalHutMp;
    }

    public void autoAddDoTT() {
        long dttAutoAdd = (long) (tuTien.getXDiemThienPhu() * (DO_TT[phamchat.id] / DO_TT[0]));
        if (dttAutoAdd <= 0) {
            dttAutoAdd = Util.nextInt(1, 20);
        }
        addDoThuanThuc(dttAutoAdd);
    }

    public String getThuocTinhName() {
        return getLinhCanName(thuoctinh);
    }

    public void showMenuCongPhap() {
        if (tenCongPhap == null) {
            Service.gI().sendThongBao(tuTien.player, "Bạn chưa học công pháp");
            return;
        }
        StringBuilder npcSay = new StringBuilder();

        npcSay.append("|7|❖═════ CÔNG PHÁP ═════❖\n");
        npcSay.append("|5|➤").append(getFullName()).append("\n");
        npcSay.append("|5|➤ Thuần thục:").append(getCurrentExpStr()).append("\n");

        npcSay.append("|2|➤ Số thuộc tính: ").append(slThuocTinh).append(" thuộc tính\n");
        npcSay.append("|1|➤ Phẩm chất:").append(phamchat.name).append("\n");

        npcSay.append("|5|➤ Thuộc tính:").append(getThuocTinhName()).append("\n");

        npcSay.append("|7|✦ Bạn muốn...?");
        NpcService.gI().createMenuConMeo(tuTien.player, ConstNpc.MENU_CONG_PHAP, -1, npcSay.toString(), "Tăng Phẩm", "Lĩnh ngộ", "Xem Thuộc\nTính", "Đóng");
    }

    public boolean canLevelUp() {
        return doThuanThuc == maxDoThuanThuc;
    }

    public boolean canLinhNgo() {
        return canLevelUp();
    }

    public boolean isLearn() {
        return phamchat.id >= 0 && tenCongPhap != null;
    }
}
