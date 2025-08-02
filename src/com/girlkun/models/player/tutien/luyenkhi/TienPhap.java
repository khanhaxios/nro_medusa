package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;
import lombok.Data;

@Data
public class TienPhap {
    TuTien tuTien;
    // 0 la buff dame thuoc tinh
    // 1 la hoi mau
    // 2 la buff dame sau khi dung chieu
    // 3 hoi mau trong khoang thoi gian
    // 4 la giam sat thuong
    public static byte[] PARAM_TO_BUFF = new byte[]{0, 1, 2, 3, 4};
    // dung de buff sat thuong
    private boolean hasEffect = false;

    private String ten;
    private byte thuoctinh;
    private String mota;
    private short xParam;
    private byte param;
    private long lastTimeUsed;
    private long timeDuration;
    private long coolDown;
    private int percentLinhKhiUse;
    private long COOL_DOWN_TIME = 30 * 1000;

    private byte id;

    public String getName() {
        return String.format("[%s] : %s", ten, getMota());
    }

    public String getMota() {
        return this.mota.replace("#", String.valueOf(xParam));
    }


    public void randomParam(byte baseXParam) {
        byte randomParam = TienPhap.PARAM_TO_BUFF[Util.nextInt(TienPhap.PARAM_TO_BUFF.length)];
        this.setParam(randomParam);
        byte xP = (byte) Util.nextInt(baseXParam);
        this.setPercentLinhKhiUse(baseXParam * 10000);
        this.setXParam(xP);
    }

    public TienPhap(byte id, String ten, String mota, long timeDuration, byte param, byte thuoctinh) {
        this.id = id;
        this.ten = ten;
        this.param = param;
        this.timeDuration = timeDuration;
        this.mota = mota;
        this.thuoctinh = thuoctinh;
    }

    public TienPhap() {

    }

    public TienPhap(TuTien tuTien) {
        this.tuTien = tuTien;
    }

    public void update() {
        if (Util.canDoWithTime(lastTimeUsed, 10_000)) {
            tuTien.subLinhKhi(getPercentLinhKhiUse());
        }
    }

    public void calcPoint(Player player) {
        switch (param) {
            case 0:
                player.nPoint.xDameLinhCan += xParam;
                break;
            case 1, 3:
                player.nPoint.hpAdd += player.nPoint.hpAdd * xParam / 100;
                break;
            case 2:
                player.nPoint.dameAfter += xParam;
                break;
            case 4:
                player.nPoint.tlSubSD += xParam;
                break;
        }
    }
}
