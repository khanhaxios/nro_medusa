/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.utils.Util;

public class DanDuocEffect {
    public long timeBuffLinhKhi;
    public long lastTimeUseDanBuffLinhKhi;
    public float xBuffLinhKhi;

    public long timeBuffLt;
    public long lastTimeUseDanLt;
    public float xBuffLt;

    public long timeBuffMayMan;
    public long lastTimeUseMayMan;
    public float pointMayMan;

    public long timeBuffSTLinhCan;
    public long lastTimeUseSTLinhCan;
    public float stLinhCanBuff;

    public long timeBuffCongPhap;
    public long lastTimeUseCongPhap;
    public float xBuffCongPhap;
    public boolean isUseDanTranhTamMa;
    public long lastTimeUseDanHoiLK;
    public int tranhTamMaPercent;
    public int percentDotPhaThienDao;
    public boolean isUseDanDotPhaThienDao;

    public void update() {
        // Reset buff Linh Khí nếu đã hết thời gian
        if (Util.canDoWithTime(lastTimeUseDanBuffLinhKhi, timeBuffLinhKhi)) {
            xBuffLinhKhi = 0;
            timeBuffLinhKhi = 0;
        }

        // Reset buff LT nếu đã hết thời gian
        if (Util.canDoWithTime(lastTimeUseDanLt, timeBuffLt)) {
            xBuffLt = 0;
            timeBuffLt = 0;
        }

        // Reset buff May Mắn nếu đã hết thời gian
        if (Util.canDoWithTime(lastTimeUseMayMan, timeBuffMayMan)) {
            pointMayMan = 0;
            timeBuffMayMan = 0;
        }

        // Reset buff Sự Tinh Linh Căn nếu đã hết thời gian
        if (Util.canDoWithTime(lastTimeUseSTLinhCan, timeBuffSTLinhCan)) {
            stLinhCanBuff = 0;
            timeBuffSTLinhCan = 0;
        }

        // Reset buff Công Pháp nếu đã hết thời gian
        if (Util.canDoWithTime(lastTimeUseCongPhap, timeBuffCongPhap)) {
            xBuffCongPhap = 0;
            timeBuffCongPhap = 0;
        }
    }

    public boolean isTranhTamMa() {
        return isUseDanTranhTamMa;
    }

    public boolean isBuffLinhKhi() {
        return !Util.canDoWithTime(lastTimeUseDanBuffLinhKhi, timeBuffLinhKhi);
    }

    public boolean isBuffLt() {
        return !Util.canDoWithTime(lastTimeUseDanLt, timeBuffLt);
    }

    // Kiểm tra xem buff May Mắn có còn hiệu lực hay không
    public boolean isBuffMayMan() {
        return !Util.canDoWithTime(lastTimeUseMayMan, timeBuffMayMan) && pointMayMan > 0;
    }

    // Kiểm tra xem buff Sự Tinh Linh Cần có còn hiệu lực hay không
    public boolean isBuffSTLinhCan() {
        return !Util.canDoWithTime(lastTimeUseSTLinhCan, timeBuffSTLinhCan) && stLinhCanBuff > 0;
    }

    // Kiểm tra xem buff Công Pháp có còn hiệu lực hay không
    public boolean isBuffCongPhap() {
        return !Util.canDoWithTime(lastTimeUseCongPhap, timeBuffCongPhap) && xBuffCongPhap > 0;
    }

    public void resetDanTranhTamMa() {
        isUseDanTranhTamMa = false;
        tranhTamMaPercent = 0;
    }

    public void resetDanDptd() {
        isUseDanDotPhaThienDao = false;
        percentDotPhaThienDao = 0;
    }
}

