package com.girlkun.utils;

import com.girlkun.models.player.Player;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class Logger {

    //time Format
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    // Reset
    public static final String RESET = "\033[0m";  // Text Reset

    // Regular Colors
    public static final String BLACK = "\033[0;30m";   // BLACK
    public static final String RED = "\033[0;31m";     // RED
    public static final String GREEN = "\033[0;32m";   // GREEN
    public static final String YELLOW = "\033[0;33m";  // YELLOW
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String WHITE = "\033[0;37m";   // WHITE

    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    // Underline
    public static final String BLACK_UNDERLINED = "\033[4;30m";  // BLACK
    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String GREEN_UNDERLINED = "\033[4;32m";  // GREEN
    public static final String YELLOW_UNDERLINED = "\033[4;33m"; // YELLOW
    public static final String BLUE_UNDERLINED = "\033[4;34m";   // BLUE
    public static final String PURPLE_UNDERLINED = "\033[4;35m"; // PURPLE
    public static final String CYAN_UNDERLINED = "\033[4;36m";   // CYAN
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    // Background
    public static final String BLACK_BACKGROUND = "\033[40m";  // BLACK
    public static final String RED_BACKGROUND = "\033[41m";    // RED
    public static final String GREEN_BACKGROUND = "\033[42m";  // GREEN
    public static final String YELLOW_BACKGROUND = "\033[43m"; // YELLOW
    public static final String BLUE_BACKGROUND = "\033[44m";   // BLUE
    public static final String PURPLE_BACKGROUND = "\033[45m"; // PURPLE
    public static final String CYAN_BACKGROUND = "\033[46m";   // CYAN
    public static final String WHITE_BACKGROUND = "\033[47m";  // WHITE

    // High Intensity
    public static final String BLACK_BRIGHT = "\033[0;90m";  // BLACK
    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String BLUE_BRIGHT = "\033[0;94m";   // BLUE
    public static final String PURPLE_BRIGHT = "\033[0;95m"; // PURPLE
    public static final String CYAN_BRIGHT = "\033[0;96m";   // CYAN
    public static final String WHITE_BRIGHT = "\033[0;97m";  // WHITE

    // Bold High Intensity
    public static final String BLACK_BOLD_BRIGHT = "\033[1;90m"; // BLACK
    public static final String RED_BOLD_BRIGHT = "\033[1;91m";   // RED
    public static final String GREEN_BOLD_BRIGHT = "\033[1;92m"; // GREEN
    public static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";// YELLOW
    public static final String BLUE_BOLD_BRIGHT = "\033[1;94m";  // BLUE
    public static final String PURPLE_BOLD_BRIGHT = "\033[1;95m";// PURPLE
    public static final String CYAN_BOLD_BRIGHT = "\033[1;96m";  // CYAN
    public static final String WHITE_BOLD_BRIGHT = "\033[1;97m"; // WHITE

    // High Intensity backgrounds
    public static final String BLACK_BACKGROUND_BRIGHT = "\033[0;100m";// BLACK
    public static final String RED_BACKGROUND_BRIGHT = "\033[0;101m";// RED
    public static final String GREEN_BACKGROUND_BRIGHT = "\033[0;102m";// GREEN
    public static final String YELLOW_BACKGROUND_BRIGHT = "\033[0;103m";// YELLOW
    public static final String BLUE_BACKGROUND_BRIGHT = "\033[0;104m";// BLUE
    public static final String PURPLE_BACKGROUND_BRIGHT = "\033[0;105m"; // PURPLE
    public static final String CYAN_BACKGROUND_BRIGHT = "\033[0;106m";  // CYAN
    public static final String WHITE_BACKGROUND_BRIGHT = "\033[0;107m";   // WHITE

    /**
     * Note: System.out.print
     */
    public static void log(String text) {
        System.out.print(text);
    }

    public static void log(String color, String text) {
        System.out.print(color + text + RESET);
    }

    /**
     * Note: System.out.print
     */
    public static void success(String text) {
        System.out.print(GREEN + text + RESET);
    }

    /**
     * Note: System.out.print
     */
    public static void warning(String text) {
        System.out.print(BLUE + text + RESET);
    }

    /**
     * Note: System.out.print
     */
    public static void error(String text) {
        System.out.print(RED + text + RESET);
    }

    public static void logException(Class clazz, Exception ex, String... log) {
        try {
            String logE = "";
            if (log != null && log.length > 0) {
                log(PURPLE, log[0] + "\n");
                logE = "Chú thích lỗi gặp phải: " + log[0] + "\n";
            }
            StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
            String nameMethod = stackTraceElements[1].getMethodName();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            System.out.println("loi ne    kkkk    ClassCastException ");
            String detail = sw.toString();
            String[] arr = detail.split("\n");
            Logger.warning("Có lỗi tại class: ");
            Logger.error(clazz.getName());
            Logger.warning(" - tại phương thức: ");
            Logger.error(nameMethod + "\n");
            Logger.warning("Chi tiết lỗi:\n");
            logE += "Có lỗi tại class: " + clazz.getName()
                    + " - tại phương thức: " + nameMethod + "\n"
                    + "Chi tiết lỗi:\n";
            for (String str : arr) {
                Logger.error(str + "\n");
                logE += str + "\n";
            }
            logError(logE);
            Logger.log("--------------------------------------------------------\n");
        } catch (Exception e) {
        }
    }

    public static void logError(String logE) {
        try {
            if (logE.contains("ConcurrentModifier")) {
                return;
            }
            Calendar calender = Calendar.getInstance();
            Date date = calender.getTime();
            String str = toTimeString(Date.from(Instant.now()));
            String filename = "log/LogLoi_" + dateFormat.format(date) + ".txt";
            try (FileWriter fw = new FileWriter(filename, true); BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write("================Err in Time: " + str + " ========================\n"
                        + logE
                        + "\n\n\n================================================================\n");
            }
        } catch (Exception e) {
        }
    }

    public static void logTaiXiu(Player pl, int typeLog, int qtyCuoc, int qtyNhan) {
        try {
            Calendar calender = Calendar.getInstance();
            Date date = calender.getTime();
            String str = toTimeString(Date.from(Instant.now()));
            String filename = "log/LogTaiXiu_" + dateFormat.format(date) + ".txt";
            // 0 - WinTai
            // 1 - WinXiu
            // 2 - ThuaTai
            // 3 - ThuaXiu
            // 4 - ThuaHoaTai
            // 5 - ThuaHoaXiu

            //  6 - Đặt Cược Tài
            //  7 - Đặt Cược Xỉu
            String log;
            switch (typeLog) {
                case 0 ->
                    log = "Player " + pl.name + " win cửa Tài mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc từ " + Util.format(pl.inventory.ruby)
                            + " -> tăng " + Util.format((pl.inventory.ruby + qtyNhan));
                case 1 ->
                    log = "Player " + pl.name + " win cửa Xỉu mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc từ " + Util.format(pl.inventory.ruby)
                            + " -> tăng " + Util.format((pl.inventory.ruby + qtyNhan));
                case 2 ->
                    log = "Player " + pl.name + " thua cửa Tài vì đặt Xỉu mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc hiện còn " + Util.format(pl.inventory.ruby);
                case 3 ->
                    log = "Player " + pl.name + " thua cửa Xỉu vì đặt Tài mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc hiện còn " + Util.format(pl.inventory.ruby);
                case 4 ->
                    log = "Player " + pl.name + " thua của Hòa vì đặt Tài mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc hiện còn " + Util.format(pl.inventory.ruby);
                case 5 ->
                    log = "Player " + pl.name + " thua của Hòa vì đặt Xỉu mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc hiện còn " + Util.format(pl.inventory.ruby);
                case 6 ->
                    log = "Player " + pl.name + " đã đặt cược vào Tài mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc hiện còn " + Util.format(pl.inventory.ruby);
                case 7 ->
                    log = "Player " + pl.name + " đã đặt cược vào Xỉu mức cược: "
                            + Util.format(qtyCuoc)
                            + "\nHồng ngọc hiện còn " + Util.format(pl.inventory.ruby);
                default -> {
                    return;
                }
            }

            try (FileOutputStream fos = new FileOutputStream(filename, true);
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, StandardCharsets.UTF_8))) {
                if (typeLog == 6 || typeLog == 7) {
                    bw.write("Cược!!! - > Time: " + str + " =================================================\n"
                            + log
                            + "\nEnd Cược ========================================================\n\n");
                } else {
                    bw.write("Result!!! - > Time: " + str + " =================================================\n"
                            + log
                            + "\nEnd Kết Quả ========================================================\n\n");
                }
            }
        } catch (Exception e) {
        }
    }

    public static String toTimeString(Date date) {
        try {
            String a = timeFormat.format(date);
            return a;
        } catch (Exception e) {
            return "01:01:00";
        }
    }
}
