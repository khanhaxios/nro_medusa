/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.server;

import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

public class AutoClear implements Runnable {
    private static final long TIME_WAIT_CLEAR = 10_800_000; // 3 tiếng
    private static AutoClear I;

    public boolean isRunning;
    public boolean AUTO_CLEAN_STATE;

    public long lastTimeClear;
    private boolean warned; // cờ đánh dấu đã thông báo trước 5 phút

    public static AutoClear getI() {
        if (I == null) {
            I = new AutoClear();
        }
        return I;
    }

    public AutoClear() {
        lastTimeClear = System.currentTimeMillis();
        isRunning = true;
        AUTO_CLEAN_STATE = true;
        warned = false;
    }

    public void setState(boolean state) {
        this.AUTO_CLEAN_STATE = state;
    }

    public void close() {
        AUTO_CLEAN_STATE = false;
        isRunning = false;
    }

    @Override
    public void run() {
        while (!Maintenance.isRuning && isRunning && AUTO_CLEAN_STATE) {
            try {
                long timePassed = System.currentTimeMillis() - lastTimeClear;
                long timeLeft = TIME_WAIT_CLEAR - timePassed;

                // Thông báo 1 lần khi còn 5 phút
                if (!warned && timeLeft <= 300000 && timeLeft > 0) {
                    long seconds = timeLeft / 1000;
                    long minutes = seconds / 60;
                    Service.gI().sendThongBaoAllPlayer(
                            "Hệ thống sắp dọn dẹp phó bản định kỳ sau "
                                    + minutes + " phút (" + seconds + " giây). Hãy chú ý thoát ra."
                    );
                    warned = true;
                }

                // Đủ thời gian thì clear + reset
                if (Util.canDoWithTime(lastTimeClear, TIME_WAIT_CLEAR)) {
                    try {
//                        BanDoKhoBauService.gI().clearAll();
//                        DoanhTraiService.gI().clearAll();
//                        BossManager.gI().clearAll();
                        Logger.log("AutoClear: dọn dẹp thành công!");
                    } catch (Exception e) {
                        Logger.error(e.getMessage());
                    }
                    lastTimeClear = System.currentTimeMillis();
                    warned = false;
                }
                Thread.sleep(800);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
