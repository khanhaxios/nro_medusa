package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import lombok.Data;

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

    public void tangPham(long percent) {
        if (Util.isTrue(percent, 100)) {
            // success linh ngo
            this.phamchat = phamchat.getNext();
            this.maxThuocTinh = getMaxThuocTinhByPhamChat();
//            randomNewBuff();
            upOldBuff();
            restDoTT();
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
            randomNewBuff();
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
        randomNewBuff();
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

        if (tlHutHPBuff > 0) tlHutHPBuff += Math.min(tlHutHPBuff + Util.nextInt(3, 6), MAX_BUFF);

        if (tlHutMPBuff > 0) tlHutMPBuff += Math.min(tlHutMPBuff + Util.nextInt(3, 6), MAX_BUFF);

        if (tlLinhKhiBuff > 0) tlLinhKhiBuff += Math.min(tlLinhKhiBuff + Util.nextInt(1, 2), MAX_BUFF);

        if (hutDame > 0) hutDame += Math.min(hutDame + Util.nextInt(1, 3), MAX_BUFF);

        if (hutHp > 0) hutHp += Math.min(hutHp + Util.nextInt(1, 3), MAX_BUFF);

        if (hutMp > 0) hutMp += Math.min(hutMp + Util.nextInt(1, 3), MAX_BUFF);

        if (xDameThuocTinh > 0) xDameThuocTinh += (byte) Math.min(xDameThuocTinh + Util.nextInt(1, 2), 100);

        if (xLinhKhiBuff > 0) xLinhKhiBuff += (byte) Math.min(xLinhKhiBuff + Util.nextInt(1, 2), 100);

        if (xTocDoKhoiPhucLinhKhi > 0)
            xTocDoKhoiPhucLinhKhi += (byte) Math.min(xTocDoKhoiPhucLinhKhi + Util.nextInt(1, 2), 100);
    }

    public void randomNewBuff() {
        if (this.slThuocTinh >= maxThuocTinh) return;

        boolean isAdd = Util.isTrue(getBaseTyLeLinhNgo() + 10, 100);
        if (!isAdd) return;

        byte countNew = 0;

        if (tlHpBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlHpBuff = Util.nextInt(5, 12);
            countNew++;
        }
        if (tlMpBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlMpBuff = Util.nextInt(5, 12);
            countNew++;
        }
        if (tlDameBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlDameBuff = Util.nextInt(5, 12);
            countNew++;
        }
        if (tlLinhKhiBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlLinhKhiBuff = Util.nextInt(5, 12);
            countNew++;
        }
        if (tlHutHPBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlHutHPBuff = Util.nextInt(5, 12);
            countNew++;
        }
        if (tlHutMPBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlHutMPBuff = Util.nextInt(5, 12);
            countNew++;
        }
        if (tlAnCapVang == 0 && slThuocTinh + countNew < maxThuocTinh) {
            tlAnCapVang = Util.nextInt(5, 12);
            countNew++;
        }
        if (hutDame == 0 && slThuocTinh + countNew < maxThuocTinh) {
            hutDame = Util.nextInt(5, 12);
            countNew++;
        }
        if (hutHp == 0 && slThuocTinh + countNew < maxThuocTinh) {
            hutHp = Util.nextInt(5, 12);
            countNew++;
        }
        if (hutMp == 0 && slThuocTinh + countNew < maxThuocTinh) {
            hutMp = Util.nextInt(5, 12);
            countNew++;
        }
        if (xDameThuocTinh == 0 && slThuocTinh + countNew < maxThuocTinh) {
            xDameThuocTinh = (byte) Util.nextInt(1, 4);
            countNew++;
        }
        if (xLinhKhiBuff == 0 && slThuocTinh + countNew < maxThuocTinh) {
            xLinhKhiBuff = (byte) Util.nextInt(1, 4);
            countNew++;
        }
        if (xTocDoKhoiPhucLinhKhi == 0 && slThuocTinh + countNew < maxThuocTinh) {
            xTocDoKhoiPhucLinhKhi = (byte) Util.nextInt(1, 4);
            countNew++;
        }

        // Sau khi add xong thì cập nhật lại số lượgn thuộc tính hiện tại
        calcSlThuocTinh();
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

    public int getBaseTyLeLinhNgo() {
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
        return DO_TT[phamchat.id];
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
        player.nPoint.tlDameCrit.add(xDameThuocTinh * 5);
        player.nPoint.tlHutHp += tlHutHPBuff;
        player.nPoint.tlHutMp += tlHutMPBuff;
        player.nPoint.dameg += totalHutDame;
        player.nPoint.hpg += totalHutHp;
        player.nPoint.mpg += totalHutMp;
    }

    public void autoAddDoTT() {
        long dttAutoAdd = (long) (tuTien.getXDiemThienPhu() * (DO_TT[phamchat.id] / DO_TT[0]));
        addDoThuanThuc(dttAutoAdd);
    }

    public String getThuocTinhName() {
        return getLinhCanName(thuoctinh);
    }

    public void showMenuCongPhap() {
        String npcSay = "|7|Công Pháp+\n" + "|5|" + getFullName() + "\n" + "|5|Độ thuần thục : " + getCurrentExpStr() + "\n" + "|2|Số lượng thuộc tính : " + slThuocTinh + " thuộc tính\n" + "|1|Phẩm chất : " + phamchat.name + "\n" + "|5|Thuộc tính công pháp : " + getThuocTinhName() + "\n" + "|7|Bạn muốn ? ";
        NpcService.gI().createMenuConMeo(tuTien.player, ConstNpc.MENU_CONG_PHAP, -1, npcSay, "Tăng Phẩm", "Xem Thuộc\nTính", "Đóng");
    }

    public boolean canLevelUp() {
        return doThuanThuc == maxDoThuanThuc;
    }
}
