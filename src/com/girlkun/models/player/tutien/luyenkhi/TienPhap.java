package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
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

    public void restTienCoolDown() {
        this.setCoolDown(30 * 1000);
        this.setLastTimeUsed(System.currentTimeMillis());
    }

    private void dispose() {
        lastTimeUsed = System.currentTimeMillis();
        timeDuration = 0;
    }


    public void randomParam(byte baseXParam) {
        byte randomParam = TienPhap.PARAM_TO_BUFF[Util.nextInt(TienPhap.PARAM_TO_BUFF.length)];
        this.setParam(randomParam);
        byte xP = (byte) Util.nextInt(baseXParam);
        this.setPercentLinhKhiUse(baseXParam * 100);
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

    public void useTienPhap() {
        int percent = (int) ((100.0 * tuTien.linhKhiPoint) / tuTien.maxLinhKhiPoint);
        if (percent - percentLinhKhiUse >= 0) {
            restTienCoolDown();
            tuTien.subLinhKhiPercent(percentLinhKhiUse);
            Service.gI().chat(tuTien.player, ten);
        }
    }

    public void update() {
        try {
            if (tuTien == null) {
                dispose();
            }
            switch (param) {
                case 1:
                    if (!hasEffect && isActive()) {
                        tuTien.player.nPoint.addHp(tuTien.player.nPoint.hpMax);
                        hasEffect = true;
                    }
                    break;
                case 3:
                    if (isActive()) {
                        tuTien.player.nPoint.addHp(tuTien.player.nPoint.hpMax / 100 * xParam);
                    }
                    break;
                case 4:
                    if (!hasEffect && isActive()) {
                        tuTien.player.nPoint.tyLeGiamDame += xParam;
                        hasEffect = true;
                    }
                    break;
            }
            if (coolDown - 1000 >= 0) {
                coolDown -= 1000;
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public boolean isActive() {
        return (System.currentTimeMillis() - this.lastTimeUsed + timeDuration) > 0;
    }

    public boolean isCoolDown() {
        return coolDown > 0;
    }
}
