package com.girlkun.server;

import com.girlkun.database.GirlkunDB;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.server.io.MySession;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;


public class Maintenance extends Thread {

    public static boolean isRuning = false;

    private static Maintenance i;

    private int min;

    private Maintenance() {

    }

    public static Maintenance gI() {
        if (i == null) {
            i = new Maintenance();
        }
        return i;
    }

    public void start(int min) {
        if (!isRuning) {
            isRuning = true;
            this.min = min;
            this.start();
        }
    }

    public void maintenance30Second() {
        Logger.log(Logger.PURPLE, "Tiến Hành Bảo Trì Sau 30 Giây!\n");
        this.start(30);
    }

    @Override
    public void run() {
        while (min > 0) {
            if (min <= 10 || min % 5 == 0) { // thông báo mốc quan trọng
                Service.getInstance().sendThongBaoAllPlayer(
                        "Hệ thống sẽ bảo trì sau " + min + " giây nữa, vui lòng thoát game để tránh mất vật phẩm."
                );
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            min--;
        }
        Logger.error("BEGIN MAINTENANCE...............................\n");
        ServerManager.gI().close(2000);
    }

    public boolean canLogin(MySession mySession) {
        try {
            GirlkunResultSet rs;
            rs = GirlkunDB.executeQuery("select * from account where username = ? and password = ?", mySession.uu, mySession.pp);

            if (rs.first()) {
                boolean isAdmin = rs.getBoolean("is_admin");
                if (isRuning) {
                    return isAdmin;
                }
            }
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        return true;
    }
}
