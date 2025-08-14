package com.girlkun.server;

import com.girlkun.utils.Logger;

import java.time.LocalTime;

public class AutoMaintenance implements Runnable {
    public static int HOUR = 12;
    public static int MINUTE = 20;

    public static boolean isRunning = false;

    private static AutoMaintenance I;

    public static AutoMaintenance gI() {

        if (I == null) {
            I = new AutoMaintenance();
        }
        return I;
    }

    @Override
    public void run() {
        while (!Maintenance.isRuning && !isRunning) {
            try {
                LocalTime now = LocalTime.now();
                Logger.log("Time : " + now.getHour() + ":" + now.getMinute() + " time mt : " + HOUR + ":" + MINUTE);
                if (now.getHour() == HOUR && now.getMinute() == MINUTE) {
                    Logger.log("Start auto maintenance");
                    Maintenance.gI().start(60);
                    isRunning = true;
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                Logger.logException(AutoMaintenance.class, e);
            }
        }
    }

    public static void runBatchFile(String filePath) {
        try {
            ProcessBuilder builder = new ProcessBuilder("/bin/bash", filePath);
            builder.redirectErrorStream(true); // gộp stderr vào stdout
            builder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
