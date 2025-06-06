package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import lombok.Data;

@Data
public class TienPhap implements Cloneable, Runnable {
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
    private byte xParam;
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

    public void update() {
        try {
            if (tuTien == null) {
                dispose();
            }
            if (System.currentTimeMillis() - (lastTimeUsed + timeDuration) > 0) {
                switch (param) {
                    case 0:
                        if (!hasEffect) {
                            tuTien.player.nPoint.tlDameCrit.add((int) xParam);
                            hasEffect = true;
                        }
                        break;
                    case 1:
                        if (!hasEffect) {
                            tuTien.player.nPoint.addHp(tuTien.player.nPoint.hpMax * xParam / 100);
                            hasEffect = true;
                            PlayerService.gI().sendInfoHp(tuTien.player);
                        }
                        break;
                    case 2:
                        if (!hasEffect) {
                            tuTien.player.nPoint.dameAfter += xParam;
                            hasEffect = true;
                        }
                        break;
                    case 3:
                        tuTien.player.nPoint.addHp(tuTien.player.nPoint.hpMax * xParam / 100);
                        PlayerService.gI().sendInfoHp(tuTien.player);
                        break;
                    case 4:
                        if (!hasEffect) {
                            tuTien.player.nPoint.tyLeGiamDame += xParam;
                            hasEffect = true;
                        }
                        break;
                }
            } else {
                // clear
                switch (param) {
                    case 0:
                        if (hasEffect) {
                            tuTien.player.nPoint.tlDameCrit.remove((Integer) Integer.parseInt(String.valueOf(xParam)));
                        }
                        break;
                    case 2:
                        if (hasEffect) {
                            tuTien.player.nPoint.dameAfter -= xParam;
                        }
                        break;
                    case 4:
                        if (hasEffect) {
                            tuTien.player.nPoint.tyLeGiamDame -= xParam;
                        }
                        break;
                }
            }
            if (this.coolDown <= 0) {
                this.coolDown = 0;
                return;
            }
            short time = 1000;
            this.setCoolDown(coolDown - time);
            Thread.sleep(1000);
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

    }

    private void dispose() {
        lastTimeUsed = System.currentTimeMillis();
        timeDuration = 0;
    }


    public void randomParam(byte baseXParam) {
        byte randomParam = TienPhap.PARAM_TO_BUFF[Util.nextInt(TienPhap.PARAM_TO_BUFF.length)];
        this.setParam(randomParam);
        byte xP = (byte) Util.nextInt(baseXParam);
        this.setPercentLinhKhiUse(20);
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
//        if (tuTien.tienPhapsUsed.stream().noneMatch(tp -> tp.id == id)) {
        int percent = (int) ((100.0 * tuTien.linhKhiPoint) / tuTien.maxLinhKhiPoint);
        if (percent - percentLinhKhiUse >= 0) {
            tuTien.tienPhapsUsed.add(clone());
            Service.gI().chat(tuTien.player, ten);
        }
//        }
    }

    @Override
    public TienPhap clone() {
        try {
            return (TienPhap) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void run() {
        restTienCoolDown();
        tuTien.subLinhKhiPercent(percentLinhKhiUse);
        while (coolDown > 0) {
            update();
        }
        tuTien.tienPhapsUsed.removeIf(tp -> tp.id == this.id);
    }
}
