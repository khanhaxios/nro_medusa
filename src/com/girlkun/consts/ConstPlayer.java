package com.girlkun.consts;

/**
 *
 * @Edit by ndq
 *
 */
public class ConstPlayer {
    public static final int[] HEADMONKEY = {192, 195, 196, 199, 197, 200, 198};

    public static final byte TRAI_DAT = 0;
    public static final byte NAMEC = 1;
    public static final byte XAYDA = 2;

    //type pk
    public static final byte NON_PK = 0;
    public static final byte PK_PVP = 3;
    public static final byte PK_ALL = 5;

    //type fushion
    public static final byte NON_FUSION = 0;
    public static final byte LUONG_LONG_NHAT_THE = 4;
    public static final byte HOP_THE_PORATA = 6;
    public static byte HOP_THE_PORATA2 = 8;
    public static byte HOP_THE_PORATA3 = 10;
    public static byte HOP_THE_PORATA4 = 12;
    public static byte HOP_THE_PORATA5 = 14;
    public static byte HOP_THE_PORATA6 = 16;
    
    /// Remake By ndq (Zalo - 0372475179) ====================
    // AURA BIẾN HÌNH Ở ĐÂY
    public static final byte[][] AURA_BIEN_HINH = {
        {7, 7, 13, 6, 12}, // 5 Aura TD Theo Lv Từ 1 -> 6
        {8, 8, 13, 4, 74}, // 5 Aura NM Theo Lv Từ 1 -> 5
        {7, 7, 13, 6, 15} // 5 Aura XD Theo Lv Từ 1 -> 5
    };
    // SỬA NGOẠI HÌNH TỪ LV 1-5 Ở ĐÂY
    public static final short[][] HEAD_BIEN_HINH = {
        {1496, 1497, 1498, 1499, 1500}, // 5 head TD 
        {1503, 1504, 1505, 1506, 1507},// 5 haed NM
         {1510, 1511, 1512, 1513, 1514}, // 5 head XD
    };
    // THÂN NGOẠI HÌNH LV 1-5
    public static final short[][] BODY_BIEN_HINH = {
        {1501, 1501, 1501, 1501, 1501},
        {1508, 1508, 1508, 1508, 1508},
         {1515, 1515, 1515, 1515, 1515},
    };
    // CHÂN NGOẠI HÌNH LV 1-5
    public static final short[][] LEG_BIEN_HINH = {
        {1502, 1502, 1502, 1502, 1502},
        {1509, 1509, 1509, 1509, 1509},
         {1516, 1516, 1516, 1516, 1516},
    };
    
    //const skill player
    public static final int[] SKILL_TD = new int[]{0, 1, 6, 9, 10, 20, 22, 24, 19, 27};
    public static final int[] SKILL_NAMEC = new int[]{2, 3, 7, 11, 12, 17, 18, 26, 19, 28};
    public static final int[] SKILL_XAYDA = new int[]{4, 5, 8, 13, 14, 21, 23, 25, 19, 29};
    
    public static final int QTY_MAX_ITEM_BODY_PLAYER = 12;
    public static final int QTY_MAX_ITEM_BODY_PET = 8;
}
