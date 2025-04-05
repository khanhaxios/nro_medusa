package com.girlkun.models.player;

import com.girlkun.models.player.Pet.Pet;
import BoMong.BoMong;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.skill.PlayerSkill;

import java.util.List;

import com.girlkun.models.clan.Clan;
import com.girlkun.models.intrinsic.IntrinsicPlayer;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.ItemTime;
import com.girlkun.models.npc.specialnpc.MagicTree;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.npc.specialnpc.MabuEgg;
import com.girlkun.models.npc.specialnpc.Timedua;
import com.girlkun.models.mob.MobMe;
import com.girlkun.data.DataGame;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.card.Card;
import com.girlkun.models.clan.ClanMember;
import com.girlkun.models.item.ItemTimeSieuCap;
import com.girlkun.models.kygui.ItemKyGui;
import com.girlkun.models.kygui.ShopKyGuiManager;
//import com.girlkun.models.kygui.ShopKyGuiService;
import com.girlkun.models.map.TrapMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.bdkb.BanDoKhoBauService;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.map.gas.GasService;
import com.girlkun.models.matches.IPVP;
import com.girlkun.models.matches.TYPE_LOSE_PVP;
import com.girlkun.models.player.Pet.DaoLu.DaoLu;
import com.girlkun.models.skill.Skill;
//import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
//import com.girlkun.server.Manager;
import com.girlkun.services.Service;
import com.girlkun.server.io.MySession;
import com.girlkun.models.task.TaskPlayer;
import com.girlkun.network.io.Message;
import com.girlkun.server.Client;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.FriendAndEnemyService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.ChonAiDay;
import com.girlkun.services.func.CombineNew;
//import com.girlkun.services.func.TaiXiu;
//import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {

    public boolean tusat = false;
    public int pointPvpthuong;
    public int pointPvpVip;

    public boolean autoUse;
    public boolean autoUseNow;
    public boolean muanhieu;
    public boolean useCanCau;
    public long lasttimeCanCau;
    public int iconCancau;
    public long vangnhat = 0;
    public long hngocnhat = 0;
    public boolean resetdame = false;
    public long lastTimeDame;
    public double dametong = 0;
    public byte countBDKB;
    public boolean firstJoinBDKB;
    public long lastimeJoinBDKB;
    public int goldChallenge;
    public boolean receivedWoodChest;
    public List<String> textRuongGo = new ArrayList<>();
    public boolean isBot;

    public long timeupdateplayer;

    public MySession session;

    public boolean beforeDispose;
    public int soluongmuanhieu = 0;
    public int idmuanhieu = -1;

    public boolean autocso = false;
    public boolean autoHP = false;
    public boolean autoKI = false;
    public boolean autoSD = false;
    public boolean autoGiap = false;

    public boolean autodau = false;

    public boolean batco = false;
    public boolean haveBeQuynh;
    public int mapHoTong;
    public long lastTimeHoTong;

    public boolean titleitem;

    public boolean titlett;
    public boolean isTitleUse1;
    public long lastTimeTitle1;
    public boolean isTitleUse2;
    public long lastTimeTitle2;
    public boolean isTitleUse3;
    public long lastTimeTitle3;

    public int dk_bdkb;
    public int dk_gapvip;
    public int dakethon;
    public int duockethon;

    public int tt_cauca;
    public int tt_gapVIP;
    public int tt_pemgau;
    public int tt_dautruong;
    public boolean haveTuTien;
    public int CapTuTien;
    public long KinhNghiemTT;

    public boolean isPet;
    public boolean isNewPet;
    public boolean isTrieuhoipet;
    public boolean isDaoLu;
//    public boolean isNewPet1;
    public boolean isBoss;
    public int NguHanhSonPoint = 0;
    public IPVP pvp;
    public int pointPvp;
    public byte maxTime = 30;
    public byte type = 0;

    public int mapIdBeforeLogout;
    public List<Zone> mapBlackBall;
    public List<Zone> mapMaBu;

    public Zone zone;
    public Zone mapBeforeCapsule;
    public List<Zone> mapCapsule;
    public List<Card> Cards = new ArrayList<>();
    public Pet pet;
    public DaoLu petDaoLu;
    public NewPet newpet;
    public Thu_TrieuHoi TrieuHoipet;
    public NhiemvuChienthan chienthan;
//    public NewPet newpet1;
    public MobMe mobMe;
    public Location location;
    public SetClothes setClothes;
    public EffectSkill effectSkill;
    public MabuEgg mabuEgg;
    public Timedua timedua;
    public TaskPlayer playerTask;
    public ItemTime itemTime;
    public ItemTimeSieuCap itemTimesieucap;
    public Fusion fusion;
    public MagicTree magicTree;
    public IntrinsicPlayer playerIntrinsic;
    public Inventory inventory;
    public Taixiu taixiu;
    public PlayerSkill playerSkill;
    public CombineNew combineNew;
    public IDMark iDMark;
    public Charms charms;
    public EffectSkin effectSkin;
    public Gift gift;
    public NPoint nPoint;
    public RewardBlackBall rewardBlackBall;
    public EffectFlagBag effectFlagBag;
    public FightMabu fightMabu;

    public SkillSpecial skillSpecial;
    public BoMong achievement;

    public Clan clan;
    public ClanMember clanMember;

    public List<Friend> friends;
    public List<Enemy> enemies;

    public long id;
    public String name;
    public byte gender;
    public boolean isNewMember;
    public short head;

    public byte typePk;

    public byte cFlag;

    public boolean haveTennisSpaceShip;

    public boolean justRevived;
    public long lastTimeRevived;

    public boolean justRevived1;
    public long lastTimeRevived1;

    public boolean justRevived2;
    public long lastTimeRevived2;

    public long rankSieuHang;
    public long numKillSieuHang;

    public String TrieuHoiNamePlayer;
    public int TrieuHoiCapBac = -1;
    public String TenThuTrieuHoi;
    public int TrieuHoiThucAn;
    public long TrieuHoiDame;
    public long TrieuHoiHP;
    public long TrieuHoilastTimeThucan;
    public int TrieuHoiLevel;
    public long TrieuHoiExpThanThu;
    public Player TrieuHoiPlayerAttack;
    public double TrieuHoidamethanmeo;
    public long Autothucan;
    public boolean trangthai = false;

    public long diemdanh;
    public int vnd;
    public byte totalPlayerViolate;
    public long timeChangeZone;
    public long lastTimeUseOption;

    public int bdkb_countPerDay;
    public long bdkb_lastTimeJoin;
    public boolean bdkb_isJoinBdkb;

    public short idNRNM = -1;
    public short idGo = -1;
    public long lastTimePickNRNM;
    public int goldNormar;
    public int goldVIP;
    public int goldTai;
    public int goldXiu;
    public long lastTimeWin;
    public boolean isWin;
    public short idAura = -1;
    public String Hppl = "\n";
    public String HpQuai = "\n";
    public int levelWoodChest;
    // Tab Player
    public byte typeTabPet = 0;

    public long lastTimeSavePlayer;
    public static final int TIME_AUTO_SAVE = 1800000;
    public boolean dungCoSk2T9;

    public Player() {
        lastTimeSavePlayer = System.currentTimeMillis();
        lastTimeUseOption = System.currentTimeMillis();
        location = new Location();
        nPoint = new NPoint(this);
        inventory = new Inventory();
        taixiu = new Taixiu();
        playerSkill = new PlayerSkill(this);
        setClothes = new SetClothes(this);
        effectSkill = new EffectSkill(this);
        fusion = new Fusion(this);
        playerIntrinsic = new IntrinsicPlayer();
        rewardBlackBall = new RewardBlackBall(this);
        effectFlagBag = new EffectFlagBag();
        fightMabu = new FightMabu(this);
        //----------------------------------------------------------------------
        iDMark = new IDMark();
        combineNew = new CombineNew();
        playerTask = new TaskPlayer();
        friends = new ArrayList<>();
        enemies = new ArrayList<>();
        itemTime = new ItemTime(this);
        itemTimesieucap = new ItemTimeSieuCap(this);
        charms = new Charms();
        gift = new Gift(this);
        effectSkin = new EffectSkin(this);
        skillSpecial = new SkillSpecial(this);
        achievement = new BoMong(this);
        chienthan = new NhiemvuChienthan();
    }

    //--------------------------------------------------------------------------
    public boolean isDie() {
        if (this.nPoint != null) {
            return this.nPoint.hp <= 0;
        }
        return true;
    }

    //--------------------------------------------------------------------------
    public void setSession(MySession session) {
        this.session = session;
    }

    public void sendMessage(Message msg) {
        if (this.session != null) {
            session.sendMessage(msg);
        }
    }

    public MySession getSession() {
        return this.session;
    }

    public Player getMaster() {
        if (this.isTrieuhoipet) {
            return ((Thu_TrieuHoi) this).masterr;
        } else if (this.isDaoLu) {
            return ((DaoLu) this).master;
        } else if (this.isNewPet) {
            return ((NewPet) this).master;
        } else if (this.isPet) {
            return ((Pet) this).master;
        } else {
            return this;
        }
    }

    public static long getIdFromPet(long id) {
        if (id % 1111111 == 0) { // Chiến Thần
            return -id / 1111111;
        } else if (id % 11111111 == 0) { // Đạo Lữ
            return -id / 11111111;
        } else if (id % 111111 == 0) {
            return -id / 111111;
        } else if (id % 11111 == 0) { // Phan Than
            return -id / 11111;
        } else { //Main Hoặc Pet
            return id != -1 ? Math.abs(id) : id;
        }
    }

    public static long setIdForPet(Player player, long id) {
        if (player.isTrieuhoipet) {
            return -id * 1111111;
        } else if (player.isDaoLu) {
            return -id * 11111111;
        } else if (player.isPet) {
            return -id;
        } else {
            return id;
        }
    }

    public void CreatePet(String NamePet) {
        this.TenThuTrieuHoi = NamePet;
        this.TrieuHoilastTimeThucan = System.currentTimeMillis();
        this.TrieuHoiThucAn = 100;
        this.TrieuHoiLevel = 0;
        this.TrieuHoiExpThanThu = 0;
        this.TrieuHoiCapBac = 0; //Util.nextInt(0, Util.nextInt(3, 10));
        this.TrieuHoiDame = Util.GioiHannext(10000, 10000L + ((this.TrieuHoiCapBac + 1) * 10000L));
        this.TrieuHoiHP = Util.GioiHannext(100000, 100000L + ((this.TrieuHoiCapBac + 1) * 50000L));
    }

    public boolean isPl() {
        return !isPet && !isDaoLu && !isBoss && !isNewPet && !isTrieuhoipet;// && !isNewPet1
    }

    public void update() {
        if (!this.beforeDispose) {
            try {
                if (!iDMark.isBan()) {
                    if (nPoint != null) {
                        nPoint.update();
                    }
                    if (fusion != null) {
                        fusion.update();
                    }
                    if (effectSkin != null) {
                        effectSkill.update();
                    }
                    if (mobMe != null) {
                        mobMe.update();
                    }
                    if (effectSkin != null) {
                        effectSkin.update();
                    }
                    if (pet != null) {
                        pet.update();
                    }
                    if (newpet != null) {
                        newpet.update();
                    }
                    if (petDaoLu != null) {
                        petDaoLu.update();
                    }
                    if (TrieuHoipet != null) {
                        TrieuHoipet.update();
                    }
//                    if (newpet1 != null) {
//                        newpet1.update();
//                    }
                    if (magicTree != null) {
                        magicTree.update();
                    }
                    if (itemTime != null) {
                        itemTime.update();
                    }
                    if (itemTimesieucap != null) {
                        itemTimesieucap.update();
                    }
                    if (this.lastTimeTitle1 != 0 && Util.canDoWithTime(this.lastTimeTitle1, 6000)) {
                        lastTimeTitle1 = 0;
                        isTitleUse1 = false;
                    }
                    if (this.lastTimeTitle2 != 0 && Util.canDoWithTime(this.lastTimeTitle2, 6000)) {
                        lastTimeTitle2 = 0;
                        isTitleUse2 = false;
                    }

                    if (this.lastTimeTitle3 != 0 && Util.canDoWithTime(this.lastTimeTitle3, 6000)) {
                        lastTimeTitle3 = 0;
                        isTitleUse3 = false;
                    }
                    GasService.gI().update(this);
                    BanDoKhoBauService.gI().update(this);
                    DoanhTraiService.gI().update(this);
                    BlackBallWar.gI().update(this);
                    MapMaBu.gI().update(this);
                    TimeReset.gI().update(this); //time reset ngày
                    if (!isBoss && this.iDMark.isGoToGas() && Util.canDoWithTime(this.iDMark.getLastTimeGotoGas(), 6000)) {
//                        ChangeMapService.gI().changeMapBySpaceShip(this, 149, -1, 163);
                        ChangeMapService.gI().changeMapBySpaceShip(this, 149, -1, 163);
                        this.iDMark.setGoToGas(false);
                    }
                    if (!isBoss && this.iDMark.isGotoFuture() && Util.canDoWithTime(this.iDMark.getLastTimeGoToFuture(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 102, -1, Util.nextInt(60, 200));
                        this.iDMark.setGotoFuture(false);
                    }
                    if (!isBoss && this.iDMark.isGoToBDKB() && Util.canDoWithTime(this.iDMark.getLastTimeGoToBDKB(), 6000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 135, -1, 35);
                        this.iDMark.setGoToBDKB(false);
                    }
                    if (this.zone != null) {
                        TrapMap trap = this.zone.isInTrap(this);
                        if (trap != null) {
                            trap.doPlayer(this);
                        }
                        if (this.isPl()) {
                            if (this.zone.map.mapId == 183) { // && this.fusion.typeFusion == ConstPlayer.NON_FUSION
                                if (this.setClothes.ctDeKaiDo != -1 && this.cFlag != 1) {
                                    Service.getInstance().changeFlag(this, 1);
                                } else if (this.setClothes.ctHaiTac != -1 && this.cFlag != 2) {
                                    Service.getInstance().changeFlag(this, 2);
                                }
                                if ((this.setClothes.ctDeKaiDo == -1 && this.setClothes.ctHaiTac == -1) && this.cFlag != 0) {
                                    Service.getInstance().changeFlag(this, 0);
                                }
                            }
                        }
                    }
                    if (this.isPl() && this.inventory.itemsBody.get(7) != null) {
                        Item it = this.inventory.itemsBody.get(7);
                        if (it != null && it.isNotNullItem() && this.newpet == null) {// && this.newpet1 == null
                            switch (it.template.type) {
                                case 21:
                                    if (this.newpet != null) {
                                        ChangeMapService.gI().exitMap(this.newpet);
                                        this.newpet.dispose();
                                        this.newpet = null;
                                    }
                                    PetService.Pet2(this, it.template.head, it.template.body, it.template.leg, it.template.name);
                                    Service.getInstance().point(this);
                                    break;
                            }
                        }
                    } else if (this.isPl() && newpet != null && !this.inventory.itemsBody.get(7).isNotNullItem()) {// && newpet1 != null
                        ChangeMapService.gI().exitMap(this.newpet);
                        newpet.dispose();
                        newpet = null;
                    }
                    if (this.isPl() && this.TrieuHoipet == null && this.TrieuHoiCapBac >= 0
                            && this.TrieuHoiCapBac <= 10) {
                        PetService.Thu_TrieuHoi(this);
                    } else if (this.isPl() && this.TrieuHoipet != null && this.TrieuHoiCapBac < 0
                            && this.TrieuHoiCapBac > 10) {
                        ChangeMapService.gI().exitMap(this.TrieuHoipet);
                        TrieuHoipet.dispose();
                        TrieuHoipet = null;
                    }
                    if (this.isPl() && isWin && this.zone.map.mapId == 51 && Util.canDoWithTime(lastTimeWin, 2000)) {
                        ChangeMapService.gI().changeMapBySpaceShip(this, 52, 0, -1);
                        isWin = false;
                    }
                    if (!this.isBot && location.lastTimeplayerMove < System.currentTimeMillis() - 30 * 60 * 1000) {
                        Client.gI().kickSession(getSession());
                    }
                } else {
                    if (!this.isBot && Util.canDoWithTime(iDMark.getLastTimeBan(), 5000)) {
                        Client.gI().kickSession(session);
                    }
                }
                if (Client.gI().getPlayer(this.name) != null) {
                    this.achievement.plusCount(8);
                }
//                if((Client.gI().getPlayer(this.name) != null) && Util.canDoWithTime(this.timeupdateplayer, 900000)){
//                    updatePlayer(this);
//                    this.timeupdateplayer = System.currentTimeMillis();
//                }
                if (Util.canDoWithTime(this.lastTimeDame, 5000) && this.dametong != 0) {
                    this.dametong = 0;
                    this.resetdame = true;
                }

                //////////////////// HOÀN TRẢ KÝ GỬI SAU 2 NGÀY ////////////////////
                if (this.isPl()) {
                    Iterator<ItemKyGui> iterator1 = ShopKyGuiManager.gI().listItem.iterator();
                    int countit = 0;
                    while (iterator1.hasNext()) {
                        ItemKyGui it = iterator1.next();
                        if (it != null && it.isBuy == false && it.player_sell == this.id && this.session != null
                                && it.thoigian <= System.currentTimeMillis() - 172800000) {
                            countit++;
                        }
                    }

                    Iterator<ItemKyGui> iterator = ShopKyGuiManager.gI().listItem.iterator();
                    while (iterator.hasNext()) {
                        ItemKyGui it = iterator.next();
                        if (it != null && it.isBuy == false && it.player_sell == this.id && this.session != null
                                && it.thoigian <= System.currentTimeMillis() - 172800000) {

                            if (InventoryServiceNew.gI().getCountEmptyBag(this) < countit) {
                                Service.getInstance().sendThongBao(this, "Hành trang không đủ chỗ trống để hoàn trả vật phẩm kí gửi");
                            } else {
                                Item item = ItemService.gI().createNewItem(it.itemId);
                                item.quantity = it.quantity;
                                item.itemOptions.addAll(it.options);

                                // Remove the current item using the iterator
                                iterator.remove();

                                InventoryServiceNew.gI().addItemBag(this, item);
                                InventoryServiceNew.gI().sendItemBags(this);
                                Service.getInstance().sendMoney(this);
                                Service.getInstance().sendThongBao(this, "Vật phẩm kí đã quá 2 ngày. Vật phẩm đã được hoàn trả");
                            }
                        }
                    }
                }
                //////////////////////////////////////////////////////////////////////////////////////////

//                if (this.isPl() && !this.beforeDispose && Util.canDoWithTime(lastTimeSavePlayer, TIME_AUTO_SAVE)) {
//                    lastTimeSavePlayer = System.currentTimeMillis();
//                    if (this.iDMark == null) {
//                    } else {
//                        System.out.println("Tiến Hành Update Định Kỳ 30P 1 Lần Tại Người Chơi: " + this.name);
//                        PlayerDAO.updatePlayer(this);
//                    }
//                }
            } catch (Exception e) {
                e.getStackTrace();
                Logger.logException(Player.class, e, "Lỗi tại player: " + this.name);
            }
        }
    }

    //--------------------------------------------------------------------------
    /*
     * {380, 381, 382}: ht lưỡng long nhất thể xayda trái đất
     * {383, 384, 385}: ht porata xayda trái đất
     * {391, 392, 393}: ht namếc
     * {870, 871, 872}: ht c2 trái đất
     * {873, 874, 875}: ht c2 namếc
     * {867, 878, 869}: ht c2 xayda
     */
    private static final short[][] idOutfitFusion = {
        {380, 381, 382}, {383, 384, 385}, {391, 392, 393},// btc1
        //        {1080, 1081, 1082}, {1086, 1087, 1088}, {1083, 1084, 1085}, // btc2
        {1204, 1205, 1206}, {1207, 1208, 1209}, {1210, 1211, 1212}, //btc2
        {1375, 1376, 1377}, {1372, 1373, 1374}, {1369, 1370, 1371}, //btc3
        {1255, 1256, 1257}, {1249, 1250, 1251}, {1246, 1247, 1248}, //btc4
        {1252, 1253, 1254}, {793, 794, 795}, {390, 295, 296}, //btc5
        {1454, 1455, 1456}, {1457, 1458, 1459}, {1451, 1452, 1453} //btc6
    };

    public byte getAura() {
        /// Remake By ndq (Zalo - 0372475179) ====================
        // AURA BIẾN HÌNH Ở ĐÂY 
        if (this.isPl() && this.effectSkill != null && this.effectSkill.isBienHinh) {
            return ConstPlayer.AURA_BIEN_HINH[this.gender][this.effectSkill.levelBienHinh - 1];
        }
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(5);
        if (!item.isNotNullItem()) {
            return -1;
        }
        switch (item.template.id) {
            case 1204:
                return 11;
            case 1238:
                return 11;
            case 1444:
                return 16;
            case 1445:
                return 16;
            case 9501:
                return 11;
            case 9502:
                return 11;
            default:
                return -1;
        }

    }

    public byte getEffFront() {
        return -1;
    }

    public short getHead() {
        /// Remake By ndq (Zalo - 0372475179) ====================
        if (effectSkill != null && effectSkill.isBienHinh) {
            return (short) ConstPlayer.HEAD_BIEN_HINH[this.gender][effectSkill.levelBienHinh - 1];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (this.itemTimesieucap.isBienhinh) {
            return 1409;//head cuong hoa
        } else if (this.itemTimesieucap.isBienhinh1) {
            return 793;//head cuong hoa
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 412;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1321;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                return idOutfitFusion[9 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
                return idOutfitFusion[12 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6) {
                return idOutfitFusion[15 + this.gender][0];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][0];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int headd = inventory.itemsBody.get(5).template.head;
            if (headd != -1) {
                return (short) headd;
            }
        } else if (this.gender == 0 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 1231;
        } else if (this.gender == 1 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 913;
        } else if (this.gender == 2 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 1430;
        }
        if (inventory != null && inventory.itemsBody.get(4).isNotNullItem()) {
            int headd = inventory.itemsBody.get(4).template.part;
            if (headd != -1) {
                return (short) headd;
            }
        }
        return this.head;
    }

    public short getBody() {
        /// Remake By ndq (Zalo - 0372475179) ====================
        if (effectSkill != null && effectSkill.isBienHinh) {
            return (short) ConstPlayer.BODY_BIEN_HINH[this.gender][effectSkill.levelBienHinh - 1];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 193;
        } else if (this.itemTimesieucap.isBienhinh) {
            return 1410;// cuong hoa
        } else if (this.itemTimesieucap.isBienhinh1) {
            return 794;// cuong hoa
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 413;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1322;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                return idOutfitFusion[9 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
                return idOutfitFusion[12 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6) {
                return idOutfitFusion[15 + this.gender][1];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][1];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int body = inventory.itemsBody.get(5).template.body;
            if (body != -1) {
                return (short) body;
            }
        } else if (this.gender == 0 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 1232;
        } else if (this.gender == 1 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 914;
        } else if (this.gender == 2 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 1431;
        }
        if (inventory != null && inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
    }

    public short getLeg() {
        /// Remake By ndq (Zalo - 0372475179) ====================
        if (effectSkill != null && effectSkill.isBienHinh) {
            return (short) ConstPlayer.LEG_BIEN_HINH[this.gender][effectSkill.levelBienHinh - 1];
        }
        if (effectSkill != null && effectSkill.isMonkey) {
            return 194;
        } else if (this.itemTimesieucap.isBienhinh) {
            return 1411;//cuong hoa
        } else if (this.itemTimesieucap.isBienhinh1) {
            return 795;//cuong hoa
        } else if (effectSkill != null && effectSkill.isSocola) {
            return 414;
        } else if (effectSkill != null && effectSkill.isBinh) {
            return 1323;
        } else if (fusion != null && fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 0][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA) {
                return idOutfitFusion[this.gender == ConstPlayer.NAMEC ? 2 : 1][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
                return idOutfitFusion[3 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
                return idOutfitFusion[9 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
                return idOutfitFusion[12 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6) {
                return idOutfitFusion[15 + this.gender][2];
            } else if (fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
                return idOutfitFusion[6 + this.gender][2];
            }
        } else if (inventory != null && inventory.itemsBody.get(5).isNotNullItem()) {
            int leg = inventory.itemsBody.get(5).template.leg;
            if (leg != -1) {
                return (short) leg;
            }
        } else if (this.gender == 0 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 1233;
        } else if (this.gender == 1 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 915;
        } else if (this.gender == 2 && this.CapTuTien >= 19 && this.haveTuTien == true && !this.itemTimesieucap.isBienhinh1 && !this.itemTimesieucap.isBienhinh) {
            return 1432;
        }
        if (inventory != null && inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }
        return (short) (gender == 1 ? 60 : 58);
    }

    public short getFlagBag() {
        if (this.iDMark.isHoldBlackBall()) {
            return 31;
        } else if (this.idNRNM >= 353 && this.idNRNM <= 359) {
            return 30;
        }
        if (this.inventory.itemsBody.size() > 8) {
            if (this.inventory.itemsBody.get(8).isNotNullItem()) {
                return this.inventory.itemsBody.get(8).template.part;
            }
        }
        if (this.isPet) {
            if (this.inventory.itemsBody.get(7).isNotNullItem()) {
                return this.inventory.itemsBody.get(7).template.part;
            }
        }
        if (TaskService.gI().getIdTask(this) == ConstTask.TASK_3_2) {
            return 28;
        }
        if (this.clan != null) {
            return (short) this.clan.imgId;
        }
        return -1;
    }

    public short getMount() {
        if (this.inventory.itemsBody.isEmpty() || this.inventory.itemsBody.size() < 10) {
            return -1;
        }
        Item item = this.inventory.itemsBody.get(9);
        if (!item.isNotNullItem()) {
            return -1;
        }
        if (item.template.type == 24) {
            if (item.template.gender == 3 || item.template.gender == this.gender) {
                return item.template.id;
            } else {
                return -1;
            }
        } else {
            if (item.template.id < 500) {
                return item.template.id;
            } else {
                return (short) DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
            }
        }

//        for (Item item : inventory.itemsBag) {
//            if (item.isNotNullItem()) {
//                if (item.template.type == 24) {
//                    if (item.template.gender == 3 || item.template.gender == this.gender) {
//                        return item.template.id;
//                    } else {
//                        return -1;
//                    }
//                }
//                if (item.template.type == 23) {
//                    if (item.template.id < 500) {
//                        return item.template.id;
//                    } else {
//                        return (short) DataGame.MAP_MOUNT_NUM.get(String.valueOf(item.template.id));
//                    }
//                }
//            }
//        }
//        return -1;
    }

    public String tenskill9(int skill) {
        switch (skill) {
            case 2:
                return "Cadic Liên hoàn chưởng";
            case 1:
                return "Ma Phong Ba";
            case 0:
                return "Super Kamejoko";
            default:
                return "";
        }
    }

    public String nhiemvuchienthan(int nhiemvu) {
        switch (nhiemvu) {
            case 10:
                this.chienthan.maxcount = 100;
                return "Giết 100 Boss Thần Zeno";
            case 9:
                this.chienthan.maxcount = 10;
                return "Giết 10 Boss Đôremon";
            case 8:
                this.chienthan.maxcount = 10;
                return "Đi Bản đồ kho báu 10 lần";
            case 7:
                this.chienthan.maxcount = 10;
                return "Đi map Khí Gas 10 lần";
            case 6:
                this.chienthan.maxcount = 5000;
                return "Chưởng Chí mạng 5000 lần";
            case 5:
                this.chienthan.maxcount = 5000;
                return "Hạ 5.000 Quái bay";
            case 4:
                this.chienthan.maxcount = 2000;
                return "Nhặt 2000 Capsule Kì bí";
            case 3:
                this.chienthan.maxcount = 10;
                return "Hộ tống Thành công Bé Quỳnh 10 lần";
            case 2:
                this.chienthan.maxcount = 10;
                return "Tìm được 10 Món đồ Thần linh bên Cold";
            case 1:
                this.chienthan.maxcount = 2000;
                return "Giết 2.000 Sên 8";
            default:
                return "";
        }
    }

    public String NameThanthu(int CapBac) {
        switch (CapBac) {
            case 10:
                return "Đế Tiên";
            case 9:
                return "Vực Chủ Cảnh";
            case 8:
                return "Độ Kiếp Kỳ";
            case 7:
                return "Đại Thừa Kỳ";
            case 6:
                return "Hợp Thể Kỳ";
            case 5:
                return "Luyện Hư";
            case 4:
                return "Hóa Thần";
            case 3:
                return "Nguyên Anh";
            case 2:
                return "Kim Đan";
            case 1:
                return "Trúc Cơ";
            case 0:
                return "Luyện Khí";
            default:
                return "Phế Vật";
        }
    }

    public String DaDotpha(int CapBac) {
        switch (CapBac) {
            case 9:
                return "Đế Vương Thạch";
            case 8:
                return "Hỏa Hồn Thạch";
            case 7:
                return "Thiên Mệnh Thạch";
            case 6:
                return "Huyết Tinh Thạch";
            case 5:
                return "Linh Vân Thạch";
            case 4:
                return "Mịch Lâm Thạch";
            default:
                return "Thiên Nguyệt thạch";
        }
    }

    public String TrieuHoiKiNang(int CapBac) {
        switch (CapBac) {
            case 10:
                return "Tìm " + ((TrieuHoiLevel + 1) * 15) + " Hồng ngọc cho Chủ nhân\n"
                        + "Tăng " + (TrieuHoiLevel + 1) + "% HP, KI, Giáp, SD, SD chí mạng cho Chủ nhân\n";
            case 9:
                return "Tìm " + ((TrieuHoiLevel + 1) * 12) + " Hồng ngọc cho Chủ nhân\n"
                        + "Tăng " + ((TrieuHoiLevel + 1) / 2) + "% HP, KI, Giáp, SD, SD chí mạng cho Chủ nhân";
            case 8:
                return "Tìm " + ((TrieuHoiLevel + 1) * 10) + " Hồng ngọc cho Chủ nhân\n"
                        + "Tăng " + ((TrieuHoiLevel + 1) / 5) + "% HP, KI, Giáp, SD cho Chủ nhân";
            case 7:
                return "Tăng " + ((TrieuHoiLevel + 1) / 5) + "% HP, KI, Giáp, SD cho Chủ nhân";
            case 6:
                return "Tăng " + ((TrieuHoiLevel + 1) / 3) + "% SD Chí mạng cho Chủ nhân";
            case 5:
                return "Tăng " + ((TrieuHoiLevel + 1) / 5) + "% SD cho Chủ nhân";
            case 4:
                return "Tăng " + ((TrieuHoiLevel + 1) * 30) + " HP, KI, SD, Giáp cho Chủ nhân";
            case 3:
                return "Tăng " + ((TrieuHoiLevel + 1) * 20) + " HP, KI\n" + ((TrieuHoiLevel + 1) * 10) + " SD cho Chủ nhân";
            case 2:
                return "Tăng " + ((TrieuHoiLevel + 1) * 10) + " Sức đánh cho Chủ nhân";
            case 1:
                return "Tăng " + ((TrieuHoiLevel + 1) * 20) + " KI cho Chủ nhân";
            case 0:
                return "Tăng " + ((TrieuHoiLevel + 1) * 20) + " HP cho Chủ nhân";
            default:
                return "Phế vật mà làm được gì !!!";
        }
    }

    //-------------------------------------------------
    public String CapTuTien(int CapBac) {
        switch (CapBac) {
            case 19:
                return "Thiên Đạo";
            case 18:
                return "Đế Tiên";
            case 17:
                return "Chuẩn Đế Tiên";
            case 16:
                return "Vực Chủ Cảnh";
            case 15:
                return "Độ Kiếp Kì";
            case 14:
                return "Đại Thừa Kỳ Đỉnh phong";
            case 13:
                return "Đại Thừa Kỳ";
            case 12:
                return "Hợp Thể Kỳ";
            case 11:
                return "Luyện Hư";
            case 10:
                return "Hóa Thần";
            case 9:
                return "Bán Thần";
            case 8:
                return "Nguyên Anh";
            case 7:
                return "Kim Đan";
            case 6:
                return "Kết Đan";
            case 5:
                return "Trúc Cơ Kỳ Đỉnh phong";
            case 4:
                return "Trúc Cơ Kỳ";
            case 3:
                return "Luyện Khí Kỳ Đỉnh phong";
            case 2:
                return "Luyện Khí Kỳ";
            case 1:
                return "Phàm nhân";
            case 0:
                return "Phế Vật";
            default:
                return "";
        }
    }

    public String ChisoTuTien() {
        int CapBac = CapTuTien;
        switch (CapBac) {
            case 19:
                return "Tìm " + ((CapBac + 1) * 75) + " Hồng ngọc cho Bản thân và Đệ tử\n"
                        + "Tăng " + ((CapBac + 1) * 5) + "%HP, KI, SD cho Bản thân và Đệ tử\n";
            case 18:
            case 17:
            case 16:
            case 15:
            case 14:
            case 13:
            case 12:
            case 11:
            case 10:
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
            case 0:
                return "Tăng " + ((CapBac + 1) * 5) + "%HP, Ki, SD cho Bản thân và Đệ tử";
            default:
                return "Phế vật mà làm được gì !!!";
        }
    }

    public int tileDotPha(int CapBac) {
        switch (CapBac) {
            case 19:
                return 1;
            case 18:
                return 3;
            case 17:
                return 4;
            case 16:
                return 5;
            case 15:
                return 9;
            case 14:
                return 10;
            case 13:
                return 15;
            case 12:
                return 20;
            case 11:
                return 25;
            case 10:
                return 30;
            case 9:
                return 35;
            case 8:
                return 40;
            case 7:
                return 65;
            case 6:
                return 70;
            case 5:
                return 75;
            case 4:
                return 80;
            case 3:
                return 85;
            case 2:
                return 90;
            case 1:
                return 95;
            case 0:
                return 99;
            default:
                return 0;
        }
    }

    public int DaTuTien(int CapBac) {
        switch (CapBac) {
            case 19:
                return 200;
            case 18:
                return 99;
            case 17:
                return 99;
            case 16:
                return 99;
            case 15:
                return 99;
            case 14:
                return 99;
            case 13:
                return 99;
            case 12:
                return 99;
            case 11:
                return 99;
            case 10:
                return 99;
            case 9:
                return 75;
            case 8:
                return 75;
            case 7:
                return 50;
            case 6:
                return 45;
            case 5:
                return 45;
            case 4:
                return 45;
            case 3:
                return 30;
            case 2:
                return 25;
            case 1:
                return 20;
            case 0:
                return 20;
            default:
                return 0;
        }
    }

    //--------------------------------------------------------------------------
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            int TileChinhxac = 0;
            if (plAtt != null) {
                TileChinhxac = plAtt.nPoint.tlchinhxac;
                switch (plAtt.playerSkill.skillSelect.template.id) {
                    case Skill.KAMEJOKO:
                    case Skill.MASENKO:
                    case Skill.ANTOMIC:
                    case Skill.DRAGON:
                    case Skill.DEMON:
                    case Skill.GALICK:
                    case Skill.KAIOKEN:
                    case Skill.LIEN_HOAN:
                    case Skill.DE_TRUNG:
                        if (this.nPoint.voHieuChuong > 0) {
                            com.girlkun.services.PlayerService.gI().hoiPhuc(this, 0, Util.DoubleGioihan(damage * this.nPoint.voHieuChuong / 100));
                            return 0;
                        }
                }
            }
            if (this.setClothes.isSetGokuUI()) {
                int tl = 60 + (this.nPoint.tlNeDon - TileChinhxac);
                tl = tl > 95 ? 95 : tl;
                if (!piercing && Util.isTrue(tl, 100)) {
                    return 0;
                }
            } else {
                if (!piercing && Util.isTrue(this.nPoint.tlNeDon - TileChinhxac, 100)) {
                    return 0;
                }
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (isMobAttack && this.charms.tdBatTu > System.currentTimeMillis() && damage >= this.nPoint.hp) {
                damage = Util.DoubleGioihan(this.nPoint.hp - 1);
            }

            this.nPoint.subHP(damage);
            if (isDie()) {
                if (this.isPl()) {
                    if (plAtt != null && this.zone.map.mapId == 175) {
                        plAtt.pointPvpthuong++;
                        Service.gI().sendThongBao(plAtt, "Bạn vừa hạ đối thủ và nhận được 1 điểm PVP Thường");
                        if (plAtt.tusat == true) {
                            ChangeMapService.gI().changeMapNonSpaceship(plAtt, plAtt.gender + 21, -1, 250);
                            PlayerService.gI().hoiSinh(plAtt);
                            plAtt.tusat = false;
                        }
                        if (this != null && this.zone != null && this.zone.map.mapId == 175) {
                            ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, -1, 250);
                            PlayerService.gI().hoiSinh(this);
                        }
                    }
                    if (plAtt != null && this.zone.map.mapId == 176) {
                        plAtt.pointPvpVip++;
                        Service.gI().sendThongBao(plAtt, "Bạn vừa hạ đối thủ và nhận được 1 điểm PVP VIP");
                        if (plAtt.tusat == true) {
                            ChangeMapService.gI().changeMapNonSpaceship(plAtt, plAtt.gender + 21, -1, 250);
                            PlayerService.gI().hoiSinh(plAtt);
                            plAtt.tusat = false;
                        }
                        if (this != null && this.zone != null && this.zone.map.mapId == 176) {
                            ChangeMapService.gI().changeMapNonSpaceship(this, this.gender + 21, -1, 250);
                            PlayerService.gI().hoiSinh(this);
                        }
                    }
                    setDie(plAtt);
                }
            }
            return damage;
        } else {
            return 0;
        }
    }

    protected void setDie(Player plAtt) {
        //xóa phù
        if (this.effectSkin.xHPKI > 1) {
            this.effectSkin.xHPKI = 1;
            Service.getInstance().point(this);
        }
        //xóa tụ skill đặc biệt
        this.playerSkill.prepareQCKK = false;
        this.playerSkill.prepareLaze = false;
        this.playerSkill.prepareTuSat = false;
        //xóa hiệu ứng skill
        this.effectSkill.removeSkillEffectWhenDie();
        //
        nPoint.setHp(0);
        nPoint.setMp(0);
        //xóa trứng
        if (this.mobMe != null) {
            this.mobMe.mobMeDie();
        }
        Service.getInstance().charDie(this);
        //add kẻ thù
        if (!this.isPet && !this.isDaoLu && !this.isNewPet && !this.isTrieuhoipet && !this.isBoss
                && plAtt != null
                && !plAtt.isPet && !plAtt.isDaoLu && !plAtt.isNewPet && !plAtt.isTrieuhoipet && !plAtt.isBoss) {// && !this.isNewPet1 && !plAtt.isNewPet1
            if (!plAtt.itemTime.isUseAnDanh) {
                FriendAndEnemyService.gI().addEnemy(this, plAtt);
            }
        }
        //kết thúc pk
        if (this.pvp != null) {
            this.pvp.lose(this, TYPE_LOSE_PVP.DEAD);
        }
//        PVPServcice.gI().finishPVP(this, PVP.TYPE_DIE);
        BlackBallWar.gI().dropBlackBall(this);
    }

    //--------------------------------------------------------------------------
    public void setClanMember() {
        if (this.clanMember != null) {
            this.clanMember.powerPoint = this.nPoint.power;
            this.clanMember.head = this.getHead();
            this.clanMember.body = this.getBody();
            this.clanMember.leg = this.getLeg();
        }
    }

    public boolean isAdmin() {
        return this.session.isAdmin;
    }

    public boolean check99ThucAnHuyDiet() {
        for (Item item : this.inventory.itemsBag) {
            if (item != null && item.template != null && item.template.id >= 663 && item.template.id <= 667 && item.quantity >= 99) {
                return true;
            }
        }
        return false;
    }

    public void setJustRevivaled() {
        if (this.isPet) {
            this.justRevived = true;
            this.lastTimeRevived = System.currentTimeMillis();
        } else if (this.isTrieuhoipet) {
            this.justRevived1 = true;
            this.lastTimeRevived1 = System.currentTimeMillis();
        }
    }

    public void preparedToDispose() {

    }

    public void dispose() {
        if (pet != null) {
            pet.dispose();
            pet = null;
        }
        if (petDaoLu != null) {
            petDaoLu.dispose();
            pet = null;
        }
        if (newpet != null) {
            newpet.dispose();
            newpet = null;
        }
        if (TrieuHoipet != null) {
            TrieuHoipet.dispose();
            TrieuHoipet = null;
        }
//        if (newpet1 != null) {
//            newpet1.dispose();
//            newpet1 = null;
//        }
        if (mapBlackBall != null) {
            mapBlackBall.clear();
            mapBlackBall = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapMaBu != null) {
            mapMaBu.clear();
            mapMaBu = null;
        }
        zone = null;
        mapBeforeCapsule = null;
        if (mapCapsule != null) {
            mapCapsule.clear();
            mapCapsule = null;
        }
        if (mobMe != null) {
            mobMe.dispose();
            mobMe = null;
        }
        location = null;
        if (setClothes != null) {
            setClothes.dispose();
            setClothes = null;
        }
        if (effectSkill != null) {
            effectSkill.dispose();
            effectSkill = null;
        }
        if (mabuEgg != null) {
            mabuEgg.dispose();
            mabuEgg = null;
        }
        if (timedua != null) {
            timedua.dispose();
            timedua = null;
        }
        if (taixiu != null) {
            taixiu.dispose();
            taixiu = null;
        }
        if (chienthan != null) {
            chienthan.dispose();
            chienthan = null;
        }
        if (skillSpecial != null) {
            skillSpecial.dispose();
            skillSpecial = null;
        }
        if (playerTask != null) {
            playerTask.dispose();
            playerTask = null;
        }
        if (itemTime != null) {
            itemTime.dispose();
            itemTime = null;
        }
        if (itemTimesieucap != null) {
            itemTimesieucap.dispose();
            itemTimesieucap = null;
        }
        if (fusion != null) {
            fusion.dispose();
            fusion = null;
        }
        if (magicTree != null) {
            magicTree.dispose();
            magicTree = null;
        }
        if (playerIntrinsic != null) {
            playerIntrinsic.dispose();
            playerIntrinsic = null;
        }
        if (inventory != null) {
            inventory.dispose();
            inventory = null;
        }
        if (playerSkill != null) {
            playerSkill.dispose();
            playerSkill = null;
        }
        if (combineNew != null) {
            combineNew.dispose();
            combineNew = null;
        }
        if (iDMark != null) {
            iDMark.dispose();
            iDMark = null;
        }
        if (charms != null) {
            charms.dispose();
            charms = null;
        }
        if (effectSkin != null) {
            effectSkin.dispose();
            effectSkin = null;
        }
        if (gift != null) {
            gift.dispose();
            gift = null;
        }
        if (nPoint != null) {
            nPoint.dispose();
            nPoint = null;
        }
        if (rewardBlackBall != null) {
            rewardBlackBall.dispose();
            rewardBlackBall = null;
        }
        if (effectFlagBag != null) {
            effectFlagBag.dispose();
            effectFlagBag = null;
        }
        if (pvp != null) {
            pvp.dispose();
            pvp = null;
        }
        effectFlagBag = null;
        clan = null;
        clanMember = null;
        friends = null;
        enemies = null;
        session = null;
        name = null;
    }

    public String percentGold(int type) {
        try {
            if (type == 0) {
                double percent = ((double) this.goldNormar / ChonAiDay.gI().goldNormar) * 100;
                return String.valueOf(Math.ceil(percent));
            } else if (type == 1) {
                double percent = ((double) this.goldVIP / ChonAiDay.gI().goldVip) * 100;
                return String.valueOf(Math.ceil(percent));
            }
        } catch (ArithmeticException e) {
            return "0";
        }
        return "0";
    }
}
//nplayer
