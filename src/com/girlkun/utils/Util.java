package com.girlkun.utils;

import com.girlkun.models.boss.BossManager;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;

import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.*;

import com.girlkun.models.matches.TOP;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.server.Client;
import com.girlkun.server.Manager;
import com.girlkun.services.ItemService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;

import org.apache.commons.lang.ArrayUtils;

public class Util {

    private static final Random rand;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Locale locale = new Locale("vi", "VN");
    private static final NumberFormat num = NumberFormat.getInstance(locale);

    static {
        rand = new Random();
    }

    public static boolean contains(String[] arr, String key) {
        return Arrays.toString(arr).contains(key);
    }

    public static void setTimeout(Runnable runnable, int delay) {
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            } catch (Exception e) {
                System.err.println(e);
            }
        }).start();
    }

    public static byte[] randomImg() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] array = null;
        try {
            BufferedImage img = new BufferedImage(nextInt(80, 250), nextInt(80, 250), BufferedImage.TYPE_INT_ARGB);
            IntStream.range(0, img.getWidth())
                    .forEach(x -> IntStream.range(0, img.getHeight())
                    .forEach(y -> {
                        int a = ThreadLocalRandom.current().nextInt(256);
                        int r = ThreadLocalRandom.current().nextInt(256);
                        int g = ThreadLocalRandom.current().nextInt(256);
                        int b = ThreadLocalRandom.current().nextInt(256);
                        int p = (a << 24) | (r << 16) | (g << 8) | b;
                        img.setRGB(x, y, p);
                    }));
            ImageIO.write(img, "png", baos);
            array = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static String getFormatNumber(double hp) {
        return Util.num.format(Math.floor(hp));
    }

    public static long GioiHannext(double from, double to) {
        //code by Việt
        return (long) (from + rand.nextInt((int) (to - from + 1)));
    }

    public static double GioiHannextdame(double from, double to) {
        //code by Việt
        int rdx = (int) (to - from + 1);
        return from + rand.nextInt(rdx > 0 ? rdx : 1);
    }

    public static String numberToMoney(long power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

    public static void log(String message) {
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String strDate = formatter.format(date);
        System.out.println("[" + strDate + "] " + message);
    }

    public static String msToTime(long ms) {
        ms = ms - System.currentTimeMillis();
        if (ms < 0) {
            ms = 0;
        }
        long mm = 0;
        long ss = 0;
        long hh = 0;
        ss = ms / 1000;
        mm = ss / 60;
        ss = ss % 60;
        hh = mm / 60;
        mm = mm % 60;
        String ssString = String.valueOf(ss);
        String mmString = String.valueOf(mm);
        String hhString = String.valueOf(hh);
        String time = null;
        if (hh != 0) {
            time = hhString + " giờ, " + mmString + "phút, " + ssString + "giây";
        } else if (mm != 0) {
            time = mmString + "phút, " + ssString + "giây";
        } else if (ss != 0) {
            time = ssString + "giây";
        } else {
            time = "Hết hạn";
        }
        return time;
    }

    public static String powerToString(double power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        num.setMaximumFractionDigits(1);
        if (power >= 1000000000) {
            return num.format((double) power / 1000000000) + " Tỷ";
        } else if (power >= 1000000) {
            return num.format((double) power / 1000000) + " Tr";
        } else if (power >= 1000) {
            return num.format((double) power / 1000) + " k";
        } else {
            return num.format(power);
        }
    }

//    public static String powerToStringnew(double power) {
//        Locale localee = new Locale("vi", "VN");
//        NumberFormat number = NumberFormat.getInstance(localee);
//        number.setMaximumFractionDigits(1);
//
//        String[] units = {"", "k", "Triệu", "Tỷ", "Nghìn Tỷ", "Triệu Tỷ", "Tỷ Tỷ", 
//            "Nghìn Tỷ Tỷ", "Triệu Tỷ Tỷ", "Tỷ Tỷ Tỷ", 
//            "Nghìn Tỷ Tỷ Tỷ", "Triệu Tỷ Tỷ Tỷ", "Tỷ Tỷ Tỷ Tỷ"};
//        int index = 0;
//        while (power >= 1000 && index < units.length - 1) {
//            power /= 1000;
//            index++;
//        }
//
//        return number.format(power) + " " + units[index];
//    }
    public static String powerToStringnew(double power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        String strConvert = "";
        num.setMaximumFractionDigits(1);

        while (power >= 1_000_000_000) {
            power /= 1000000000;
            strConvert = " Tỷ " + strConvert;
        }
        while (power >= 1_000_000) {
            power /= 1_000_000;
            strConvert = " Triệu " + strConvert;
        }
        while (power >= 1_000) {
            power /= 1_000;
            strConvert = " Nghìn " + strConvert;
        }
        strConvert = strConvert.replace("  ", " ");
        return num.format(power) + strConvert;
    }

    public static String powerToStringKMB(double power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        String strConvert = "";
        num.setMaximumFractionDigits(1);

        while (power >= 1_000_000_000) {
            power /= 1000000000;
            strConvert = " B " + strConvert;
        }
        while (power >= 1_000_000) {
            power /= 1_000_000;
            strConvert = " M " + strConvert;
        }
        while (power >= 1_000) {
            power /= 1_000;
            strConvert = " K " + strConvert;
        }
        strConvert = strConvert.replace("  ", " ");
        return num.format(power) + strConvert;
    }

    public static String powerToStringNDQ(double power) {
        Locale locale = new Locale("vi", "VN");
        NumberFormat num = NumberFormat.getInstance(locale);
        String strConvert = "";
        num.setMaximumFractionDigits(1);
        int conD = 0;

        while (power >= 1_000_000_000) {
            power /= 1000000000;
            strConvert = " Tỷ " + strConvert;
            conD = 1;
        }
        while (power >= 1_000_000) {
            power /= 1_000_000;
            strConvert = " Triệu " + strConvert;
            conD = 2;
        }
        while (power >= 1_000) {
            power /= 1_000;
            strConvert = " Nghìn " + strConvert;
            conD = 3;
        }
        switch (conD) {
            case 3:
                power *= 1000;
                strConvert = strConvert.substring(6, strConvert.length());
                break;
            case 2:
                power *= 1_000_000;
                strConvert = strConvert.substring(6, strConvert.length());
                break;
            default:
        }
        strConvert = strConvert.replace("  ", " ");
        return num.format(power) + strConvert;
    }

    public static String format(double power) {
        return num.format(power);
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public static int getDistance(Player pl1, Player pl2) {
        return getDistance(pl1.location.x, pl1.location.y, pl2.location.x, pl2.location.y);
    }

    public static int getDistance(Player pl, Npc npc) {
        return getDistance(pl.location.x, pl.location.y, npc.cx, npc.cy);
    }

    public static int DoubleGioihan(double a) {
        if (a > 2123456789) {
            a = 2123456789;
        }
        return (int) a;
    }

    public static int DoubleToInter(double a) {
        return (int) Math.floor(a);
    }

    public static int createIdDuongTank(int idPlayer) {
        return -idPlayer - 100_000_000;
    }

    public static int createIdBossClone(int idPlayer) {
        return -idPlayer - 120_000_000;
    }

    public static String toDateString(Date date) {
        try {
            String a = Util.dateFormat.format(date);
            return a;
        } catch (Exception e) {
            return "2021-01-01 01:01:00";
        }
    }

    public static int getDistance(Player pl, Mob mob) {
        return getDistance(pl.location.x, pl.location.y, mob.location.x, mob.location.y);
    }

    public static int getDistance(Mob mob1, Mob mob2) {
        return getDistance(mob1.location.x, mob1.location.y, mob2.location.x, mob2.location.y);
    }

    public static int nextInt(int from, int to) {
        return from + rand.nextInt(to - from + 1);
    }

    public static int nextInt(int max) {
        return rand.nextInt(max);
    }

    public static int nextInt(int[] percen) {
        int next = nextInt(1000), i;
        for (i = 0; i < percen.length; i++) {
            if (next < percen[i]) {
                return i;
            }
            next -= percen[i];
        }
        return i;
    }

    public static long nextLong(long from, long to) {
        return from + ThreadLocalRandom.current().nextLong(to - from + 1);
    }

    public static long nextLong(long max) {
        return rand.nextLong(max);
    }

    public static int getOne(int n1, int n2) {
        return rand.nextInt() % 2 == 0 ? n1 : n2;
    }

    public static int currentTimeSec() {
        return (int) System.currentTimeMillis() / 1000;
    }

    public static String replace(String text, String regex, String replacement) {
        return text.replace(regex, replacement);
    }

    public static boolean isTrue(int ratio, int typeRatio) {
        int num = Util.nextInt(typeRatio);
        if (num < ratio) {
            return true;
        }
        return false;
    }

    public static boolean isTrue(float ratio, int typeRatio) {
        if (ratio < 1) {
            ratio *= 10;
            typeRatio *= 10;
        }
        int num = Util.nextInt(typeRatio);
        if (num < ratio) {
            return true;
        }
        return false;
    }

    public static boolean haveSpecialCharacter(String text) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        boolean b = m.find();
        return b || text.contains(" ");
    }

    public static String clearKeywordVip(String name) {
        return name.replace("[SVIP] ", "")
                .replace("[VIP] ", "");
    }

    public static boolean kituvip(String text) {
        if (text.contains("[svip]") || text.contains("[vip]") || text.contains("[SVIP]")
                || text.contains("[VIP]") || text.contains("VIP") || text.contains("vip")
                || text.contains("SVIP") || text.contains("svip")) {
            return false;
        }
        return true;
    }

    public static boolean canDoWithTime(long lastTime, long miniTimeTarget) {
        return System.currentTimeMillis() - lastTime > miniTimeTarget;
    }

    private static final char[] SOURCE_CHARACTERS = {'À', 'Á', 'Â', 'Ã', 'È', 'É',
        'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â',
        'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý',
        'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ',
        'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ',
        'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ',
        'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ',
        'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ',
        'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ',
        'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ',
        'ữ', 'Ự', 'ự',};

    private static final char[] DESTINATION_CHARACTERS = {'A', 'A', 'A', 'A', 'E',
        'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a',
        'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u',
        'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u',
        'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A',
        'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e',
        'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E',
        'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o',
        'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O',
        'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u',
        'U', 'u', 'U', 'u',};

    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    public static String removeAccent(String str) {
        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public static String generateRandomText(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijk"
                + "lmnopqrstuvwxyz!@#$%&";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static Object[] addArray(Object[]... arrays) {
        if (arrays == null || arrays.length == 0) {
            return null;
        }
        if (arrays.length == 1) {
            return arrays[0];
        }
        Object[] arr0 = arrays[0];
        for (int i = 1; i < arrays.length; i++) {
            arr0 = ArrayUtils.addAll(arr0, arrays[i]);
        }
        return arr0;
    }

    public static ItemMap manhTS(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        return new ItemMap(zone, tempId, quantity, x, y, playerId);
    }

    public static ItemMap kogd(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        it.options.add(new Item.ItemOption(30, 1));
        return it;
    }

    public static Item ratiItemThanhTon(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        int ao = 1431;
        int quan = 1432;
        int giay = 1433;
        int gang = 1434;
        int nhan = 1435;
        if (ao == tempId) {
            it.itemOptions.add(new Item.ItemOption(47, highlightsItem(it.template.gender == 2, new Random().nextInt(2501) + 2000)));
        }
        if (quan == tempId) {
            it.itemOptions.add(new Item.ItemOption(22, highlightsItem(it.template.gender == 0, new Random().nextInt(90) + 120)));
        }
        if (gang == tempId) {
            it.itemOptions.add(new Item.ItemOption(0, highlightsItem(it.template.gender == 2, new Random().nextInt(15000) + 10000)));
        }
        if (giay == tempId) {
            it.itemOptions.add(new Item.ItemOption(23, highlightsItem(it.template.gender == 1, new Random().nextInt(60) + 120)));
        }
        if (nhan == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(12) + 30));
        }
        it.itemOptions.add(new Item.ItemOption(21, 200));
        return it;
    }

    public static Item ratiItemNguyenThuy(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        int ao = 1450;
        int quan = 1451;
        int giay = 1452;
        int gang = 1453;
        int nhan = 1454;
        if (ao == tempId) {
            it.itemOptions.add(new Item.ItemOption(47, highlightsItem(it.template.gender == 2, new Random().nextInt(4501) + 4000)));
        }
        if (quan == tempId) {
            it.itemOptions.add(new Item.ItemOption(22, highlightsItem(it.template.gender == 0, new Random().nextInt(150) + 220)));
        }
        if (gang == tempId) {
            it.itemOptions.add(new Item.ItemOption(0, highlightsItem(it.template.gender == 2, new Random().nextInt(45000) + 40000)));
        }
        if (giay == tempId) {
            it.itemOptions.add(new Item.ItemOption(23, highlightsItem(it.template.gender == 1, new Random().nextInt(100) + 220)));
        }
        if (nhan == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(15) + 40));
        }
        it.itemOptions.add(new Item.ItemOption(21, 500));
        return it;
    }

    //thongkho
    public static Item ratiItemThongKho(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        int ao = 1482;
        int quan = 1483;
        int giay = 1484;
        int gang = 1485;
        int nhan = 1486;
        if (ao == tempId) {
            it.itemOptions.add(new Item.ItemOption(47, highlightsItem(it.template.gender == 2, new Random().nextInt(4501) + 4000)));
        }
        if (quan == tempId) {
            it.itemOptions.add(new Item.ItemOption(22, highlightsItem(it.template.gender == 0, new Random().nextInt(150) + 220)));
        }
        if (gang == tempId) {
            it.itemOptions.add(new Item.ItemOption(0, highlightsItem(it.template.gender == 2, new Random().nextInt(5000000) + 1000000)));
        }
        if (giay == tempId) {
            it.itemOptions.add(new Item.ItemOption(23, highlightsItem(it.template.gender == 1, new Random().nextInt(100) + 220)));
        }
        if (nhan == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(15) + 40));
        }
        it.itemOptions.add(new Item.ItemOption(21, 500));
        return it;
    }
    //

    public static ItemMap ratiDTL(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, zone.map.yPhysicInTop(x, y - 24), playerId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.options.add(new Item.ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(501) + 1300)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new Item.ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new Item.ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new Item.ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(2) + 15));
        }
        it.options.add(new Item.ItemOption(209, 1)); // đồ rơi từ boss
        it.options.add(new Item.ItemOption(21, 15)); // ycsm 15 tỉ
        it.options.add(new Item.ItemOption(30, 1)); // ko thể gd
        if (Util.isTrue(90, 100)) {// tỉ lệ ra spl
            it.options.add(new Item.ItemOption(107, new Random().nextInt(3) + 1));
        } else if (Util.isTrue(4, 100)) {
            it.options.add(new Item.ItemOption(107, new Random().nextInt(3) + 5));
        } else {
            it.options.add(new Item.ItemOption(107, new Random().nextInt(5) + 1));
        }
        return it;
    }

    public static void ratiItemTS(ItemMap it, int tempId, int gender) {
        List<Integer> ao = Arrays.asList(1048, 1049, 1050);
        List<Integer> quan = Arrays.asList(1051, 1052, 1053);
        List<Integer> gang = Arrays.asList(1054, 1055, 1056);
        List<Integer> giay = Arrays.asList(1057, 1058, 1059);
        List<Integer> nhan = Arrays.asList(1060, 1061, 1062);
        if (ao.contains(tempId)) {
            it.options.add(new Item.ItemOption(47, highlightsItem(gender == 2, new Random().nextInt(501) + 2000)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new Item.ItemOption(22, highlightsItem(gender == 0, new Random().nextInt(11) + 90)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new Item.ItemOption(0, highlightsItem(gender == 2, new Random().nextInt(3001) + 7000)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new Item.ItemOption(23, highlightsItem(gender == 1, new Random().nextInt(11) + 70)));
        }
        if (nhan.contains(tempId)) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(3) + 25));
        }
        it.options.add(new Item.ItemOption(21, 15));
    }

    public static void ratiItemSen(ItemMap it, int tempId) {
        List<Integer> ao = Arrays.asList(1389);
        List<Integer> quan = Arrays.asList(1390);
        List<Integer> gang = Arrays.asList(1391);
        List<Integer> giay = Arrays.asList(1392);
        List<Integer> nhan = Arrays.asList(1393);
        it.options.add(new Item.ItemOption(230, 1));
        it.options.add(new Item.ItemOption(21, 120));
        if (ao.contains(tempId)) {
            it.options.add(new Item.ItemOption(47, Util.nextInt(2000, 2500)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new Item.ItemOption(22, Util.nextInt(150, 200)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new Item.ItemOption(0, Util.nextInt(18000, 20000)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new Item.ItemOption(23, Util.nextInt(150, 200)));
        }
        if (nhan.contains(tempId)) {
            it.options.add(new Item.ItemOption(14, Util.nextInt(20, 25)));
        }
        int[] options = {128, 129, 127, 130, 131, 132, 133, 135, 134};
        for (int skhId : options) {
            it.options.add(new Item.ItemOption(skhId, 1));
            it.options.add(new Item.ItemOption(ItemService.gI().optionIdSKH(skhId), 1));
        }
        it.options.add(new Item.ItemOption(30, 0));
    }

    public static ItemMap RaitiDoc12(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(233, 237, 241);
        List<Integer> quan = Arrays.asList(245, 249, 253);
        List<Integer> gang = Arrays.asList(257, 261, 265);
        List<Integer> giay = Arrays.asList(269, 273, 277);
        int rd12 = 281;
        if (ao.contains(tempId)) {
            it.options.add(new Item.ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(121) + 350)));//giáp 350-470
        }
        if (quan.contains(tempId)) {
            it.options.add(new Item.ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(5) + 20)));//hp 20-24k
        }
        if (gang.contains(tempId)) {
            it.options.add(new Item.ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(51) + 2200)));//2200-2250
        }
        if (giay.contains(tempId)) {
            it.options.add(new Item.ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(4) + 20)));//20-23k ki
        }
        if (rd12 == tempId) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(3) + 10));//10-12cm
        }
        it.options.add(new Item.ItemOption(209, 1));//đồ rơi từ boss
        if (Util.isTrue(70, 100)) {// tỉ lệ ra spl 1-3 sao 70%
            it.options.add(new Item.ItemOption(107, new Random().nextInt(1) + 3));
        } else if (Util.isTrue(4, 100)) {// tỉ lệ ra spl 5-7 sao 4%
            it.options.add(new Item.ItemOption(107, new Random().nextInt(3) + 5));
        } else {// tỉ lệ ra spl 1-5 sao 6%
            it.options.add(new Item.ItemOption(107, new Random().nextInt(2) + 3));
        }
        return it;
    }

    public static Item ratiItemTL(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.itemOptions.add(new Item.ItemOption(47, highlightsItem(it.template.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains(tempId)) {
            it.itemOptions.add(new Item.ItemOption(22, highlightsItem(it.template.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.itemOptions.add(new Item.ItemOption(0, highlightsItem(it.template.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.itemOptions.add(new Item.ItemOption(23, highlightsItem(it.template.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(3) + 15));
        }
        it.itemOptions.add(new Item.ItemOption(21, 15));
        return it;
    }

    public static ItemMap ratiItem(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.options.add(new Item.ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new Item.ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new Item.ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new Item.ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(3) + 15));
        }
        it.options.add(new Item.ItemOption(209, 1));
        it.options.add(new Item.ItemOption(21, 15));
        return it;
    }

    public static ItemMap ratiSpl(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> dnc = Arrays.asList(441, 442, 443, 444, 445, 446, 447);
        if (dnc.contains(tempId) && tempId == 441) {
            it.options.add(new Item.ItemOption(95, 5));
        }
        if (dnc.contains(tempId) && tempId == 442) {
            it.options.add(new Item.ItemOption(96, 5));
        }
        if (dnc.contains(tempId) && tempId == 443) {
            it.options.add(new Item.ItemOption(97, 5));
        }
        if (dnc.contains(tempId) && tempId == 444) {
            it.options.add(new Item.ItemOption(98, 5));
        }
        if (dnc.contains(tempId) && tempId == 445) {
            it.options.add(new Item.ItemOption(99, 5));
        }
        if (dnc.contains(tempId) && tempId == 446) {
            it.options.add(new Item.ItemOption(100, 5));
        }
        if (dnc.contains(tempId) && tempId == 447) {
            it.options.add(new Item.ItemOption(101, 5));
        }
        return it;
    }

    public static ItemMap ratiDa(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> spl = Arrays.asList(220, 221, 222, 223, 224);
        if (spl.contains(tempId) && tempId == 220) {
            it.options.add(new Item.ItemOption(71, 1));
        }
        if (spl.contains(tempId) && tempId == 221) {
            it.options.add(new Item.ItemOption(70, 1));
        }
        if (spl.contains(tempId) && tempId == 222) {
            it.options.add(new Item.ItemOption(69, 1));
        }
        if (spl.contains(tempId) && tempId == 223) {
            it.options.add(new Item.ItemOption(68, 1));
        }
        if (spl.contains(tempId) && tempId == 224) {
            it.options.add(new Item.ItemOption(67, 1));
        }
        return it;
    }

    public static ItemMap ratiItem1(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
        ItemMap it = new ItemMap(zone, tempId, quantity, x, y, playerId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains(tempId)) {
            it.options.add(new Item.ItemOption(47, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains(tempId)) {
            it.options.add(new Item.ItemOption(22, highlightsItem(it.itemTemplate.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains(tempId)) {
            it.options.add(new Item.ItemOption(0, highlightsItem(it.itemTemplate.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains(tempId)) {
            it.options.add(new Item.ItemOption(23, highlightsItem(it.itemTemplate.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(3) + 15));
        }
        it.options.add(new Item.ItemOption(21, 15));
        return it;
    }

    public static Item randomthanlinh(short tempId) {
        Item it = ItemService.gI().createNewItem((short) tempId);
        List<Integer> ao = Arrays.asList(555, 557, 559);
        List<Integer> quan = Arrays.asList(556, 558, 560);
        List<Integer> gang = Arrays.asList(562, 564, 566);
        List<Integer> giay = Arrays.asList(563, 565, 567);
        int ntl = 561;
        if (ao.contains((int) tempId)) {
            it.itemOptions.add(new Item.ItemOption(47, highlightsItem(it.template.gender == 2, new Random().nextInt(501) + 1000)));
        }
        if (quan.contains((int) tempId)) {
            it.itemOptions.add(new Item.ItemOption(22, highlightsItem(it.template.gender == 0, new Random().nextInt(11) + 45)));
        }
        if (gang.contains((int) tempId)) {
            it.itemOptions.add(new Item.ItemOption(0, highlightsItem(it.template.gender == 2, new Random().nextInt(1001) + 3500)));
        }
        if (giay.contains((int) tempId)) {
            it.itemOptions.add(new Item.ItemOption(23, highlightsItem(it.template.gender == 1, new Random().nextInt(11) + 35)));
        }
        if (ntl == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(3) + 15));
        }
        it.itemOptions.add(new Item.ItemOption(21, 15));
        it.itemOptions.add(new Item.ItemOption(30, 1));
        return it;
    }

    //Jiren
    public static Item ratiItemSKHJiren(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        int ao = 1519;
        int quan = 1520;
        int gang = 1521;
        int giay = 1522;
        int nhan = 1523;
        it.itemOptions.add(new Item.ItemOption(230, 1));
        if (ao == tempId) {
            it.itemOptions.add(new Item.ItemOption(47, Util.nextInt(8001) + 8000));
        }
        if (quan == tempId) {
            it.itemOptions.add(new Item.ItemOption(22, Util.nextInt(300) + 420));
        }
        if (gang == tempId) {
            it.itemOptions.add(new Item.ItemOption(0, Util.nextInt(300_000_000) + 100_000_000));
        }
        if (giay == tempId) {
            it.itemOptions.add(new Item.ItemOption(23, Util.nextInt(200) + 420));
        }
        if (nhan == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(15) + 50));
        }
        it.itemOptions.add(new Item.ItemOption(21, 1000));
        it.itemOptions.add(new Item.ItemOption(30, 0));
        return it;
    }

    //Jiren
    public static ItemMap ratiItemMapSKHJiren(ItemMap it, int tempId) {
        int ao = 1519;
        int quan = 1520;
        int gang = 1521;
        int giay = 1522;
        int nhan = 1523;
        it.options.add(new Item.ItemOption(230, 1));
        if (ao == tempId) {
            it.options.add(new Item.ItemOption(47, Util.nextInt(8001) + 8000));
        }
        if (quan == tempId) {
            it.options.add(new Item.ItemOption(22, Util.nextInt(300) + 420));
        }
        if (gang == tempId) {
            it.options.add(new Item.ItemOption(0, Util.nextInt(300_000_000) + 100_000_000));
        }
        if (giay == tempId) {
            it.options.add(new Item.ItemOption(23, Util.nextInt(200) + 420));
        }
        if (nhan == tempId) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(15) + 50));
        }
        it.options.add(new Item.ItemOption(21, 1000));
        it.options.add(new Item.ItemOption(30, 0));
        return it;
    }

    //Goku UI
    public static Item ratiItemSKHGokuUI(int tempId) {
        Item it = ItemService.gI().createItemSetKichHoat(tempId, 1);
        int ao = 1527;
        int quan = 1528;
        int gang = 1529;
        int giay = 1530;
        int nhan = 1531;
        it.itemOptions.add(new Item.ItemOption(230, 1));
        if (ao == tempId) {
            it.itemOptions.add(new Item.ItemOption(47, Util.nextInt(8001) + 16000));
        }
        if (quan == tempId) {
            it.itemOptions.add(new Item.ItemOption(22, Util.nextInt(600) + 820));
        }
        if (gang == tempId) {
            it.itemOptions.add(new Item.ItemOption(0, Util.nextInt(1_000_000_000) + 500_000_000));
        }
        if (giay == tempId) {
            it.itemOptions.add(new Item.ItemOption(23, Util.nextInt(300) + 820));
        }
        if (nhan == tempId) {
            it.itemOptions.add(new Item.ItemOption(14, new Random().nextInt(20) + 60));
        }
        it.itemOptions.add(new Item.ItemOption(21, 2000));
        it.itemOptions.add(new Item.ItemOption(30, 0));
        return it;
    }

    //Goku UI
    public static ItemMap ratiItemMapSKHGokuUI(ItemMap it, int tempId) {
        int ao = 1527;
        int quan = 1528;
        int gang = 1529;
        int giay = 1530;
        int nhan = 1531;
        it.options.add(new Item.ItemOption(230, 1));
        if (ao == tempId) {
            it.options.add(new Item.ItemOption(47, Util.nextInt(8001) + 16000));
        }
        if (quan == tempId) {
            it.options.add(new Item.ItemOption(22, Util.nextInt(600) + 820));
        }
        if (gang == tempId) {
            it.options.add(new Item.ItemOption(0, Util.nextInt(1_000_000_000) + 500_000_000));
        }
        if (giay == tempId) {
            it.options.add(new Item.ItemOption(23, Util.nextInt(300) + 820));
        }
        if (nhan == tempId) {
            it.options.add(new Item.ItemOption(14, new Random().nextInt(20) + 60));
        }
        it.options.add(new Item.ItemOption(21, 2000));
        it.options.add(new Item.ItemOption(30, 0));
        return it;
    }

    public static int highlightsItem(boolean highlights, int value) {
        double highlightsNumber = 1.1;
        return highlights ? (int) (value * highlightsNumber) : value;
    }

    public static Item sendDo(int itemId, int sql, List<Item.ItemOption> ios) {
//        InventoryServiceNew.gI().addItemBag(player, ItemService.gI().createItemFromItemShop(is));
//        InventoryServiceNew.gI().sendItemBags(player);
        Item item = ItemService.gI().createNewItem((short) itemId);
        item.itemOptions.addAll(ios);
        item.itemOptions.add(new Item.ItemOption(107, sql));
        return item;
    }

    public static boolean checkDo(Item.ItemOption itemOption) {
        switch (itemOption.optionTemplate.id) {
            case 0:// tấn công
                if (itemOption.param > 12000) {
                    return false;
                }
                break;
            case 14:// chí mạng
                if (itemOption.param > 30) {
                    return false;
                }
                break;
            case 107:// spl
            case 102:// spl
                if (itemOption.param > 16) {
                    return false;
                }
                break;
            case 77:
            case 103:
            case 95:
            case 96:
                if (itemOption.param > 41) {
                    return false;
                }
                break;
            case 50:// sd 3%
                if (itemOption.param > 24) {
                    return false;
                }
                break;
            case 6:// hp
            case 7:// ki
                if (itemOption.param > 120000) {
                    return false;
                }
                break;
            case 47:// giáp
                if (itemOption.param > 3500) {
                    return false;
                }
                break;
        }
        return true;
    }

    public static void useCheckDo(Player player, Item item, String position) {
        try {
            if (item.template != null) {
                if (item.template.id >= 381 && item.template.id <= 385) {
                    return;
                }
                if (item.template.id >= 66 && item.template.id <= 135) {
                    return;
                }
                if (item.template.id >= 474 && item.template.id <= 515) {
                    return;
                }
                item.itemOptions.forEach(itemOption -> {
                    if (!Util.checkDo(itemOption)) {
                        Logger.error(player.name + "-" + item.template.name + "-" + position + "\n");
                    }
                });
            }
        } catch (Exception e) {
            System.out.println("loi   useCheckDo");
        }
    }

//    public static String md5(String pass) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//            md.update(pass.getBytes());
//            byte[] digest = md.digest();
//            return DatatypeConverter.printHexBinary(digest).toUpperCase();
//        } catch (Exception e) {
//            Logger.error("Lỗi mã hóa password");
//        }
//        return "";
//    }
    public static String phanthuong(int i) {
        switch (i) {
            case 1:
                return "5tr";
            case 2:
                return "3tr";
            case 3:
                return "1tr";
            default:
                return "100k";
        }
    }

    public static byte getHead(byte gender) {
        switch (gender) {
            case 2:
                return 28;
            case 1:
                return 32;
            default:
                return 64;
        }
    }

    public static byte getLeg(byte gender) {
        switch (gender) {
            case 2:
                return 17;
            case 1:
                return 11;
            default:
                return 15;
        }
    }

    public static byte getBody(byte gender) {
        switch (gender) {
            case 2:
                return 16;
            case 1:
                return 10;
            default:
                return 14;
        }
    }

    public static int randomBossId() {
        int bossId = Util.nextInt(10000);
        while (BossManager.gI().getBossById(bossId) != null) {
            bossId = Util.nextInt(10000);
        }
        return bossId;
    }

    public static long tinhLuyThua(int coSo, int soMu) {
        long ketQua = 1;

        for (int i = 0; i < soMu; i++) {
            ketQua *= coSo;
        }
        return ketQua;
    }

    public static void checkPlayer(Player player) {
        new Thread(() -> {
            List<Player> list = Client.gI().getPlayers().stream().filter(
                    p -> !p.isPet && !p.isDaoLu && !p.isNewPet && !p.isTrieuhoipet
                    && p.getSession().userId == player.getSession().userId).collect(Collectors.toList());
            if (list.size() > 1) {
                list.forEach(pp -> Client.gI().kickSession(pp.getSession()));
                list.clear();
            }
        }).start();
    }

    public static int[] pickNRandInArr(int[] array, int n) {
        List<Integer> list = new ArrayList<Integer>(array.length);
        for (int i : array) {
            list.add(i);
        }
        Collections.shuffle(list);
        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            answer[i] = list.get(i);
        }
        Arrays.sort(answer);
        return answer;
    }
}

/**
 * Code được viết bởi Hoàng Việt Vui lòng không sao chép mã nguồn này dưới mọi
 * hình thức.
 */
