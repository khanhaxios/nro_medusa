package com.girlkun.models.player.tutien.luyenkhi;

import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;
import lombok.Data;

@Data
public class TienPhap {
    // 0 la buff dame thuoc tinh
    // 1 la hoi mau
    // 2 la buff dame sau khi dung chieu
    // 3 hoi mau trong khoang thoi gian
    // 4 la giam sat thuong
    public static byte[] PARAM_TO_BUFF = new byte[]{0, 1, 2, 3, 4};
    // dung de buff sat thuong

    private String ten;
    private byte thuoctinh;
    private String mota;
    private byte xParam;
    private byte param;
    private long lastTimeUsed;
    private long timeDuration;
    private long coolDown;
    private long percentLinhKhiUse;

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
        if (this.coolDown <= 0) {
            this.coolDown = 0;
            return;
        }
        short time = 1000;
        this.setCoolDown(coolDown - time);
    }

    public void randomParam(byte baseXParam) {
        byte randomParam = TienPhap.PARAM_TO_BUFF[Util.nextInt(TienPhap.PARAM_TO_BUFF.length)];
        this.setParam(randomParam);
        byte xP = (byte) Util.nextInt(baseXParam);
        this.setPercentLinhKhiUse(xP + 5);
        this.setXParam(xP);
    }

    public TienPhap(byte id, String ten, String mota, long timeDuration, byte param, byte thuoctinh) {
        this.ten = ten;
        this.param = param;
        this.timeDuration = timeDuration;
        this.mota = mota;
        this.thuoctinh = thuoctinh;
    }

    public void useTienPhap(Player plAtt, Player plInjure, Mob mob) {
        // handle attack by tien phap here
    }
}
