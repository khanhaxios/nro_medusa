/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstRatio;
import com.girlkun.models.card.Card;
import com.girlkun.models.card.OptionCard;
import com.girlkun.models.intrinsic.Intrinsic;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Pet.ConstPet;
import com.girlkun.models.player.Pet.DaoLu.DaoLu;
import com.girlkun.models.player.Pet.Pet;
import com.girlkun.models.player.huyet_mach.Huyet;
import com.girlkun.models.player.phapbao.PhapBao;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Manager;
import com.girlkun.services.*;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class NPoint {

    public static final byte MAX_LIMIT = 13;

    private Player player;

    public NPoint(Player player) {
        this.player = player;
        this.tlHp = new ArrayList<>();
        this.tlMp = new ArrayList<>();
        this.tlDef = new ArrayList<>();
        this.tlDame = new ArrayList<>();
        this.tlDameAttMob = new ArrayList<>();
        this.tlSDDep = new ArrayList<>();
        this.tlTNSM = new ArrayList<>();
        this.tlDameCrit = new ArrayList<>();
    }

    public int phamKhiLinh;
    public int khiLinhType;

    public int phamNguHanh;
    public int typeNguHanh;
    public int tlDameKim;
    public int tlDameMoc;
    public int tlDameThuy;
    public int tlDameHoa;
    public int tlDameTho;
    public int tlDamePhong;
    public int tlDameLoi;
    public int tlDameQuang;
    public int tlDameAm;

    public byte tyLeGiamDame = 0;
    public boolean isCrit;
    public boolean isCrit100;

    public int xLinhKhi;
    public int xDameLinhCan;
    public int xTuVi;

    public int xHoiLinhKhi;
    private Intrinsic intrinsic;
    private int percentDameIntrinsic;
    public int dameAfter;

    public double hutMauTamThoi;
    public double mauGoc;
    public long lastTimeHutMau;
    /*-----------------------Chỉ số cơ bản------------------------------------*/
    public byte numAttack;
    public long lastTimeNumAttackLinhCan = System.currentTimeMillis();
    public short numAttackLinhCan;
    public short stamina, maxStamina;

    public byte limitPower;
    public long power;
    public long tiemNang;

    public double hp, hpMax;
    public double mp, mpMax;
    public double hpg, mpg;
    public long dameg;
    public double dame;

    public double def;
    public long defg;
    public int crit, critg;
    public byte speed = 8;

    public boolean teleport;

    public boolean khangTDHS;

    /**
     * Chỉ số cộng thêm
     */
    public double hpAdd, mpAdd, defAdd, critAdd, hpHoiAdd, mpHoiAdd, dameAdd;
    public long hpGAdd, dameGAdd, mpGAdd, defGAdd, critGAdd;
    /**
     * //+#% sức đánh chí mạng
     */
    public List<Integer> tlDameCrit;

    /**
     * Tỉ lệ hp, mp cộng thêm
     */
    public List<Integer> tlHp, tlMp;

    /**
     * Tỉ lệ giáp cộng thêm
     */
    public List<Integer> tlDef;

    /**
     * Tỉ lệ sức đánh/ sức đánh khi đánh quái
     */
    public List<Integer> tlDame, tlDameAttMob;

    /**
     * Lượng hp, mp hồi mỗi 30s, mp hồi cho người khác
     */
    public long hpHoi, mpHoi, mpHoiCute;

    /**
     * Tỉ lệ hp, mp hồi cộng thêm
     */
    public short tlHpHoi, tlMpHoi;

    /**
     * Tỉ lệ hp, mp hồi bản thân và đồng đội cộng thêm
     */
    public short tlHpHoiBanThanVaDongDoi, tlMpHoiBanThanVaDongDoi;

    /**
     * Tỉ lệ hút hp, mp khi đánh, hp khi đánh quái
     */
    public short tlHutHp, tlHutMp, tlHutHpMob;

    /**
     * Tỉ lệ hút hp, mp xung quanh mỗi 5s
     */
    public short tlHutHpMpXQ;

    /**
     * Tỉ lệ phản sát thương
     */
    public short tlPST;

    /**
     * Tỉ lệ tiềm năng sức mạnh
     */
    public List<Integer> tlTNSM;

    /**
     * Tỉ lệ vàng cộng thêm
     */
    public short tlGold;

    /**
     * Tỉ lệ né đòn
     */
    public short tlNeDon, tlchinhxac;

    /**
     * Tỉ lệ sức đánh đẹp cộng thêm cho bản thân và người xung quanh
     */
    public List<Integer> tlSDDep;

    /**
     * Tỉ lệ giảm sức đánh
     */
    public short tlSubSD;

    public int voHieuChuong;

    /*------------------------Effect skin-------------------------------------*/
    public boolean wornTrainArmor;
    public boolean wearingTrainArmor;

    public boolean wearingVoHinh;
    public boolean isKhongLanh;

    public short tlHpGiamODo;

    public short multicationChuong;
    public long lastTimeMultiChuong;

    /*-------------------------------------------------------------------------*/

    /**
     * Tính toán mọi chỉ số sau khi có thay đổi
     */
    public void calPoint() {
        if (this.player.pet != null) {
            this.player.pet.nPoint.setPointWhenWearClothes();
        }
        if (this.player.petDaoLu != null) {
            this.player.petDaoLu.nPoint.setPointWhenWearClothes();
        }
        this.setPointWhenWearClothes();
    }

    private void setPointWhenWearClothes() {
        resetPoint();
        if (this.player.rewardBlackBall.timeOutOfDateReward[2] > System.currentTimeMillis()) {
            this.tlDameCrit.add(RewardBlackBall.R3S_2);
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            tlHutMp += RewardBlackBall.R2S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            tlDameAttMob.add(RewardBlackBall.R4S_2);
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[4] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R5S_1;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[5] > System.currentTimeMillis()) {
            tlPST += RewardBlackBall.R6S_1;
            tlNeDon += RewardBlackBall.R6S_2;
        }
        if (this.player.rewardBlackBall.timeOutOfDateReward[6] > System.currentTimeMillis()) {
            tlHpHoi += RewardBlackBall.R7S_1;
            tlHutHp += RewardBlackBall.R7S_2;
        }

        for (Card card : player.Cards) {
            if (card != null && card.Used == 1) {
                for (OptionCard io : card.Options) {
                    if (io.active == card.Level || (card.Level == -1 && io.active == 0)) {
                        switch (io.id) {
                            case 0: // Tấn công +#
                                this.dameAdd += io.param;
                                break;
                            case 2: // HP, KI+#000
                                this.hpAdd += io.param * 1000d;
                                this.mpAdd += io.param * 1000d;
                                break;
                            case 3:// fake
                                this.voHieuChuong += io.param;
                                break;
                            case 5: // +#% sức đánh chí mạng
                                this.tlDameCrit.add(io.param);
                                break;
                            case 6: // HP+#
                                this.hpAdd += io.param;
                                break;
                            case 7: // KI+#
                                this.mpAdd += io.param;
                                break;
                            case 8: // Hút #% HP, KI xung quanh mỗi 5 giây
                                this.tlHutHpMpXQ += io.param;
                                break;
                            case 14: // Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 19: // Tấn công+#% khi đánh quái
                                this.tlDameAttMob.add(io.param);
                                break;
                            case 22: // HP+#K
                                this.hpAdd += io.param * 1000d;
                                break;
                            case 23: // MP+#K
                                this.mpAdd += io.param * 1000d;
                                break;
                            case 27: // +# HP/30s
                                this.hpHoiAdd += io.param;
                                break;
                            case 28: // +# KI/30s
                                this.mpHoiAdd += io.param;
                                break;
                            case 47: // Giáp+#
                                this.defAdd += io.param;
                                break;
                            case 48: // HP/KI+#
                                this.hpAdd += io.param;
                                this.mpAdd += io.param;
                                break;
                            case 49: // Tấn công+#%
                            case 50: // Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: // HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 80: // HP+#%/30s
                                this.tlHpHoi += io.param;
                                break;
                            case 81: // MP+#%/30s
                                this.tlMpHoi += io.param;
                                break;
                            case 88: // Cộng #% exp khi đánh quái
                                this.tlTNSM.add(io.param);
                                break;
                            case 94: // Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 95: // Biến #% tấn công thành HP
                                this.tlHutHp += io.param;
                                break;
                            case 96: // Biến #% tấn công thành MP
                                this.tlHutMp += io.param;
                                break;
                            case 97: // Phản #% sát thương
                                this.tlPST += io.param;
                                break;
                            case 100: // +#% vàng từ quái
                                this.tlGold += io.param;
                                break;
                            case 101: // +#% TN,SM
                                this.tlTNSM.add(io.param);
                                break;
                            case 103: // KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 104: // Biến #% tấn công quái thành HP
                                this.tlHutHpMob += io.param;
                                break;
                            case 147: // +#% sức đánh
                                this.tlDame.add(io.param);
                                break;
                        }
                    }
                }
            }
        }

        this.player.setClothes.solomon = 0;
        for (Item item : this.player.inventory.itemsBody) {
            if (item.isNotNullItem()) {
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 193:
                            player.setClothes.solomon++;
                            break;
                        case 154:
                            player.setClothes.tienKhi++;
                            break;
                        case 155:
                            player.setClothes.thanhKhi++;
                            break;
                        case 156:
                            player.setClothes.thanKhi++;
                            break;
                    }
                }
                if (item.template.id >= 592 && item.template.id <= 594) {
                    teleport = true;
                }
                int dameAdd = 0;
                int dameSSSAdd = 0;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 0: //Tấn công +#
                            this.dameAdd += io.param;
                            break;
                        case 2: //HP, KI+#000
                            this.hpAdd += io.param * 1000;
                            this.mpAdd += io.param * 1000;
                            break;
                        case 108, 73:// fake
                            this.tlNeDon += io.param;
                            break;
                        case 18: // #% chính xác
                            this.tlchinhxac += io.param;
                            break;
                        case 5, 197, 220, 233: //+#% sức đánh chí mạng
                            this.tlDameCrit.add(io.param);
                            break;
                        case 6: //HP+#
                            this.hpAdd += io.param;
                            break;
                        case 7: //KI+#
                            this.mpAdd += io.param;
                            break;
                        case 8: //Hút #% HP, KI xung quanh mỗi 5 giây
                            this.tlHutHpMpXQ += io.param;
                            break;
                        case 14: //Chí mạng+#%
                            this.critAdd += io.param;
                            break;
                        case 19: //Tấn công+#% khi đánh quái
                            this.tlDameAttMob.add(io.param);
                            break;
                        case 22: //HP+#K
                            this.hpAdd += io.param * 1000;
                            break;
                        case 23: //MP+#K
                            this.mpAdd += io.param * 1000;
                            break;
                        case 27: //+# HP/30s
                            this.hpHoiAdd += io.param;
                            break;
                        case 28: //+# KI/30s
                            this.mpHoiAdd += io.param;
                            break;
                        case 33: //dịch chuyển tức thời
                            this.teleport = true;
                            break;
                        case 44: // tien luc
                            this.player.tienLuc += io.param;
                            break;
                        case 45:
                            this.dameAdd += io.param * 1_000_000L;
                            break;
                        case 47: //Giáp+#
                            this.defAdd += io.param;
                            break;
                        case 48: //HP/KI+#
                            this.hpAdd += io.param;
                            this.mpAdd += io.param;
                            break;
                        case 49: //Tấn công+#%
                            dameSSSAdd += io.param;
                            break;
                        case 50: //Sức đánh+#%
                            dameAdd += io.param;
                            break;
                        case 77, 194, 221: //HP+#%
                            this.tlHp.add(io.param);
                            break;
                        case 80: //HP+#%/30s
                            this.tlHpHoi += io.param;
                            break;
                        case 81: //MP+#%/30s
                            this.tlMpHoi += io.param;
                            break;
                        case 88, 101: //Cộng #% exp khi đánh quái
                            this.tlTNSM.add(io.param);
                            break;
                        case 94: //Giáp #%
                            this.tlDef.add(io.param);
                            break;
                        case 95: //Biến #% tấn công thành HP
                            this.tlHutHp += io.param;
                            break;
                        case 96: //Biến #% tấn công thành MP
                            this.tlHutMp += io.param;
                            break;
                        case 97: //Phản #% sát thương
                            this.tlPST += io.param;
                            break;
                        case 100: //+#% vàng từ quái
                            this.tlGold += io.param;
                            break;
                        case 103, 195, 222: //KI +#%
                            this.tlMp.add(io.param);
                            break;
                        case 104: //Biến #% tấn công quái thành HP
                            this.tlHutHpMob += io.param;
                            break;
                        case 105: //Vô hình khi không đánh quái và boss
                            this.wearingVoHinh = true;
                            break;
                        case 106: //Không ảnh hưởng bởi cái lạnh
                            this.isKhongLanh = true;
                            break;
                        // đối nghịch
                        case 109: //Hôi, giảm #% HP
                            this.tlHpGiamODo += io.param;
                            break;
                        case 116: //Kháng thái dương hạ san
                            this.khangTDHS = true;
                            break;
                        case 117: //Đẹp +#% SĐ cho mình và người xung quanh
                            this.tlSDDep.add(io.param);
                            break;
                        case 147, 196, 219, 232: //+#% sức đánh
                            this.tlDame.add(io.param);
                            break;
                        case 155: //Giảm 50% sức đánh, HP, KI và +#% SM, TN, vàng từ quái
                            this.tlSubSD += 50;
                            this.tlTNSM.add(io.param);
                            this.tlGold += io.param;
                            break;
                        case 162: //Cute hồi #% KI/s bản thân và xung quanh
                            this.mpHoiCute += io.param;
                            break;
                        case 173: //Phục hồi #% HP và KI cho đồng đội
                            this.tlHpHoiBanThanVaDongDoi += io.param;
                            this.tlMpHoiBanThanVaDongDoi += io.param;
                            break;
                        case 262:
                            xLinhKhi += io.param;
                            break;
                        case 263:
                            xDameLinhCan += io.param;
                            break;
                        case 264:
                            xHoiLinhKhi += io.param;
                            break;
                        case 265:
                            xTuVi += io.param;
                            break;
                        case 267:
                            tlDameKim += io.param;
                            break;
                        case 268:
                            tlDameMoc += io.param;
                            break;
                        case 269:
                            tlDameThuy += io.param;
                            break;
                        case 270:
                            tlDameHoa += io.param;
                            break;
                        case 271:
                            tlDameTho += io.param;
                            break;
                        case 272:
                            tlDamePhong += io.param;
                            break;
                        case 273:
                            tlDameLoi += io.param;
                            break;
                        case 274:
                            tlDameQuang += io.param;
                            break;
                        case 275:
                            tlDameAm += io.param;
                            break;
                        case 277:
                            phamNguHanh = io.param;
                            typeNguHanh = 0;
                            break;
                        case 278:
                            phamNguHanh = io.param;
                            typeNguHanh = 1;
                            break;
                        case 279:
                            phamNguHanh = io.param;
                            typeNguHanh = 2;
                            break;
                        case 280:
                            phamNguHanh = io.param;
                            typeNguHanh = 3;
                            break;
                        case 281:
                            phamNguHanh = io.param;
                            typeNguHanh = 4;
                            break;
                        case 282:
                            phamNguHanh = io.param;
                            typeNguHanh = 5;
                            break;
                        case 283:
                            phamNguHanh = io.param;
                            typeNguHanh = 6;
                            break;
                        case 284:
                            phamNguHanh = io.param;
                            typeNguHanh = 7;
                            break;
                        case 285:
                            phamNguHanh = io.param;
                            typeNguHanh = 8;
                            break;
                        case 286:
                            phamKhiLinh = io.param;
                            khiLinhType = 0;
                            break;
                        case 287:
                            phamKhiLinh = io.param;
                            khiLinhType = 1;
                            break;
                        case 288:
                            phamKhiLinh = io.param;
                            khiLinhType = 2;
                            break;
                        case 289:
                            phamKhiLinh = io.param;
                            khiLinhType = 3;
                            break;
                        case 290:
                            phamKhiLinh = io.param;
                            khiLinhType = 4;
                            break;
                        case 291:
                            phamKhiLinh = io.param;
                            khiLinhType = 5;
                            break;
                        case 292:
                            phamKhiLinh = io.param;
                            khiLinhType = 6;
                            break;
                        case 293:
                            phamKhiLinh = io.param;
                            khiLinhType = 7;
                            break;
                        case 294:
                            phamKhiLinh = io.param;
                            khiLinhType = 8;
                            break;

                    }
                }
                if (dameSSSAdd > 0) {
                    int dameTurn1 = ((int) (dameSSSAdd * 0.81)) + ((int) (dameAdd * 0.19));
                    int dameTurn2 = ((int) (dameAdd * 0.81)) + ((int) (dameSSSAdd * 0.19));
                    this.tlDame.add(dameTurn1);
                    this.tlDame.add(dameTurn2);
                } else if (dameAdd > 0) {
                    this.tlDame.add(dameAdd);
                }

            }
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenTheReal()) {
            player.luyenThe.calcPoint();
        }
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            player.tuTien.calcPoint();
        }
        if (player.huyet.isKichHoat()) {
            player.huyet.calcPoint();
        }
        if (player.mach != null && player.mach.isOpen) {
            player.mach.calcPoint();
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            player.tuMa.calcPoint();
        }
        if (!player.phapBaos.isEmpty()) {
            for (PhapBao phapBao : player.phapBaos) {
                if (phapBao != null) {
                    phapBao.calcPoint();
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA2) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 921) {
                    for (Item.ItemOption io : item.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 14: //Chí mạng+#%
                                this.critAdd += io.param;
                                break;
                            case 50: //Sức đánh+#%
                                this.tlDame.add(io.param);
                                break;
                            case 77: //HP+#%
                                this.tlHp.add(io.param);
                                break;
                            case 18: // #% chính xác
                                this.tlchinhxac += (short) io.param;// đối nghịch
                                break;
                            case 80: //HP+#%/30s
                                this.tlHpHoi += (short) io.param;
                                break;
                            case 81: //MP+#%/30s
                                this.tlMpHoi += (short) io.param;
                                break;
                            case 94: //Giáp #%
                                this.tlDef.add(io.param);
                                break;
                            case 103: //KI +#%
                                this.tlMp.add(io.param);
                                break;
                            case 108: //#% Né đòn
                                this.tlNeDon += (short) io.param;
                                break;
                        }
                    }
                    break;
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA3) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1165) {
                    calcFusionPoint(item);
                    break;
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA4) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1129) {
                    calcFusionPoint(item);
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA5) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1416) {
                    calcFusionPoint(item);
                }
            }
        }
        if (this.player.isPl() && this.player.fusion.typeFusion == ConstPlayer.HOP_THE_PORATA6) {
            for (Item item : this.player.inventory.itemsBag) {
                if (item.isNotNullItem() && item.template.id == 1417) {
                    calcFusionPoint(item);
                }
            }
        }

        setDameTrainArmor();
        setBasePoint();
    }

    private void calcFusionPoint(Item item) {
        for (Item.ItemOption io : item.itemOptions) {
            switch (io.optionTemplate.id) {
                case 14: //Chí mạng+#%
                    this.critAdd += io.param;
                    break;
                case 50: //Sức đánh+#%
                    this.tlDame.add(io.param);
                    break;
                case 18: // #% chính xác
                    this.tlchinhxac += (short) io.param;// đối nghịch
                    break;
                case 77: //HP+#%
                    this.tlHp.add(io.param);
                    break;
                case 80: //HP+#%/30s
                    this.tlHpHoi += (short) io.param;
                    break;
                case 81: //MP+#%/30s
                    this.tlMpHoi += (short) io.param;
                    break;
                case 94: //Giáp #%
                    this.tlDef.add(io.param);
                    break;
                case 103: //KI +#%
                    this.tlMp.add(io.param);
                    break;
                case 108: //#% Né đòn
                    this.tlNeDon += (short) io.param;
                    break;
            }
        }
    }

    private void setDameTrainArmor() {
        if (!this.player.isPet && !this.player.isDaoLu && !this.player.isBoss && !this.player.isTrieuhoipet) {
            if (this.player.inventory.itemsBody.size() < 7) {
                return;
            }
            try {
                Item gtl = this.player.inventory.itemsBody.get(6);
                if (gtl.isNotNullItem()) {
                    this.wearingTrainArmor = true;
                    this.wornTrainArmor = true;
                    this.player.inventory.trainArmor = gtl;
                    this.tlSubSD += ItemService.gI().getPercentTrainArmor(gtl);
                } else {
                    if (this.wornTrainArmor) {
                        this.wearingTrainArmor = false;
                        for (Item.ItemOption io : this.player.inventory.trainArmor.itemOptions) {
                            if (io.optionTemplate.id == 9 && io.param > 0) {
                                this.tlDame.add(ItemService.gI().getPercentTrainArmor(this.player.inventory.trainArmor));
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.error("Lỗi get giáp tập luyện " + this.player.name + "\n");
            }
        }
    }

    public void setBasePoint() {
        setHpMax();
        setHp();
        setMpMax();
        setMp();
        setDame();
        setDef();
        setCrit();
        setHpHoi();
        setMpHoi();
        setNeDon();
        setChisoGioiHan();
    }

    private void setChisoGioiHan() {
        if (this.hpHoi > 100) {
            this.hpHoi = 100;
        }
        if (this.mpHoi > 100) {
            this.mpHoi = 100;
        }
        if (this.mpHoiCute > 100) {
            this.mpHoiCute = 100;
        }
        if (this.tlHpHoi > 100) {
            this.tlHpHoi = 100;
        }
        if (this.tlMpHoi > 100) {
            this.tlMpHoi = 100;
        }
        if (this.tlHpHoiBanThanVaDongDoi > 100) {
            this.tlHpHoiBanThanVaDongDoi = 100;
        }
        if (this.tlMpHoiBanThanVaDongDoi > 100) {
            this.tlMpHoiBanThanVaDongDoi = 100;
        }
        if (this.tlHutHp > 100) {
            this.tlHutHp = 100;
        }
        if (this.tlHutMp > 100) {
            this.tlHutMp = 100;
        }
        if (this.tlHutHpMob > 100) {
            this.tlHutHpMob = 100;
        }
        if (this.tlHutHpMpXQ > 100) {
            this.tlHutHpMpXQ = 100;
        }
        if (this.tlPST > 100) {
            this.tlPST = 100;
        }
        if (this.tlGold > 10000) {
            this.tlGold = 10000;
        }
        if (this.tlNeDon > 80) {
            this.tlNeDon = 80;
        }
        if (this.tlchinhxac > 80) {
            this.tlchinhxac = 80;
        }
    }

    private void setNeDon() {
        if (player.setClothes.isSetGokuUI()) {
            tlNeDon += 60;
        }
    }

    private void setHpHoi() {
        this.hpHoi = Util.DoubleGioihan(this.hpMax / 100);
        this.hpHoi += this.hpHoiAdd;
        this.hpHoi += (this.hpMax * this.tlHpHoi / 100);
        this.hpHoi += (this.hpMax * this.tlHpHoiBanThanVaDongDoi / 100);
    }

    private void setMpHoi() {
        this.mpHoi = Util.DoubleGioihan(this.mpMax / 100);
        this.mpHoi += this.mpHoiAdd;
        this.mpHoi += (this.mpMax * this.tlMpHoi / 100);
        this.mpHoi += (this.mpMax * this.tlMpHoiBanThanVaDongDoi / 100);
    }

    private void setHpMax() {
        this.hpMax = this.hpg;
        if (player.huyet.isKichHoat()) {
            int sl = player.huyet.getLevelTinhHuyetCongDon();
            int cs1 = 0;
            switch (player.huyet.type) {
                case 1:
                    for (int i = 0; i < sl; i++) {
                        cs1 += Huyet.TinhHuyetEffect.LEVEL_PARAM_TYPE[player.huyet.type][i][0];
                    }
                    tlHp.add(cs1);
                    break;
                case 4:
                    for (int i = 0; i < sl; i++) {
                        cs1 += Huyet.TinhHuyetEffect.LEVEL_PARAM_TYPE[player.huyet.type][i][1];
                    }
                    tlHp.add(cs1);
                    break;
            }
        }

        this.hpMax += this.hpAdd;
        //item body
        for (Integer tl : this.tlHp) {
            this.hpMax += (this.hpMax * tl / 100);
        }
        //set nappa
        if (this.player.setClothes.nappa == 5) {
            this.hpMax += this.mpMax;
        }
        //set thanh ton ( thien tu xayda)
        if (this.player.setClothes.ThienTu == 5) {
            this.hpMax *= 3;
        }
        //set tinh ấn
        if (this.player.setClothes.tinhan == 5) {
            this.hpMax += (this.hpMax * 5 / 100);
        }
        //set ma hoa
        if (this.player.setClothes.mahoa == 5) {
            this.hpMax += (this.hpMax * 6 / 100);
        }
        if (this.player.setClothes.thanhoa == 5) {
            this.hpMax += (this.hpMax * 6 / 100);
        }
        //set solomon
        if (this.player.setClothes.solomon >= 2) {
            this.hpMax += (this.hpMax * 5 / 100);
        }
        if (this.player.setClothes.nguyenthuyxd == 5) {
            this.hpMax += this.hpMax * 40 / 100;
        }
        if (this.player.setClothes.setTienXayda == 5) {
            this.hpMax += this.hpMax * 250 / 100;
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
                int percent = SkillUtil.getPercentHpMonkey(player.effectSkill.levelMonkey);
                this.hpMax += (this.hpMax * percent / 100);
            }
        }
        //+hp đệ
        if (this.player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            this.hpMax += this.player.pet.nPoint.hpMax;
            if (this.player.khongThiSu.isKhongThi()) {
                this.hpMax += this.hpMax * player.khongThiSu.getHPMPBuff() / 100;
            }
        }
        // chi so  hop the
        if (this.player.isPet && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (((Pet) this.player).typePet == ConstPet.MABU) {
                this.hpMax += (this.hpMax * Math.max(20, player.khongThiSu.level * 20) / 100);
                this.tyLeGiamDame += Math.max(5, player.khongThiSu.level * 5);
            }
        }
        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 7 || this.player.TrieuHoiCapBac == 8 || this.player.TrieuHoiCapBac == 9 || this.player.TrieuHoiCapBac == 10)) {
                switch (this.player.TrieuHoiCapBac) {
                    case 7:
                    case 8:
                        this.hpMax += this.hpMax * 6 / 100;
                        break;
                    case 9:
                        this.hpMax += this.hpMax * 7 / 100;
                        break;
                    case 10:
                        this.hpMax += this.hpMax * 10 / 100;
                        break;
                    default:
                        this.hpMax += this.hpMax * 1 / 100;
                        break;
                }
            }
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 0 || this.player.TrieuHoiCapBac == 3 || this.player.TrieuHoiCapBac == 4)) {
                switch (this.player.TrieuHoiCapBac) {
                    case 0:
                        this.hpMax += 100_000;
                        break;
                    case 3:
                        this.hpMax += this.mpMax * 10 / 100;
                        break;
                    default:
                        this.hpMax += this.hpMax * 1 / 100;
                        break;
                }
            }
            // Đạo Lữ Song Tu
            // note  : he thong dao lu dang phat trien
            if (this.player.petDaoLu != null && this.player.petDaoLu.status != DaoLu.GOHOME) {
                this.hpMax += calPercent(this.hpMax, this.player.petDaoLu.getNPointAddByType());
            }
        }
        // he thong dao lu dang phat trien
        if (this.player.isDaoLu) {
            this.hpMax += calPercent(this.hpMax, ((DaoLu) this.player).getNPointAddByType());
            if (((DaoLu) this.player).pointCapCanhGioi == 10) {
                this.hpMax += calPercent(this.hpMax, DaoLu.POWER_DAU_THANH);
            }
            this.hpMax += calPercent(this.hpMax, ((DaoLu) this.player).getNPointAddCapBac());
            this.hpMax += calPercent(this.hpMax, ((DaoLu) this.player).getNPointAddCapTinh());
        }

        //đuôi khỉ
        // TODO : handle when create item buff system
        if (!this.player.isPet && this.player.itemTime.isDuoikhi || this.player.isPet && ((Pet) this.player).master.itemTime.isDuoikhi) {
            this.hpMax += (this.hpMax * 10 / 100);
        }
        if (!this.player.isPet && this.player.itemTime.isUseMayDo2 || this.player.isPet && ((Pet) this.player).master.itemTime.isUseMayDo2) {
            this.hpMax += (this.hpMax * 12 / 100);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.hpMax *= this.player.effectSkin.xHPKI;
        }

        //huýt sáo
        if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
            if (this.player.effectSkill.tiLeHPHuytSao != 0) {
                this.hpMax += (this.hpMax * this.player.effectSkill.tiLeHPHuytSao / 100);

            }
        }
        // notice : all items time is developing
        //bổ huyết
        if (this.player.itemTime != null && this.player.itemTime.isUseBoHuyet) {
            this.hpMax *= 2;
        }
        if (this.player.isPl() && this.player.isTitleUse3 && this.player.lastTimeTitle3 > 0) {
            this.hpMax += (this.hpMax * 10 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse2 && this.player.lastTimeTitle2 > 0) {
            this.hpMax += (this.hpMax * 10 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse1 && this.player.lastTimeTitle1 > 0) {
            this.hpMax += (this.hpMax * 10 / 100);
        }
        //bí ngô
        if (this.player.itemTime != null && this.player.itemTime.isBiNgo) {
            this.hpMax += (this.hpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh) {
            this.hpMax += (this.hpMax * 90 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh1) {
            this.hpMax += this.hpMax;
        }
        // item sieu cawsp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseBoHuyet3) {
            this.hpMax += (this.hpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseXiMuoi) {
            this.hpMax += (this.hpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4042) {
            this.hpMax += (this.hpMax * 10 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4043) {
            this.hpMax += (this.hpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4125) {
            this.hpMax += (this.hpMax * 30 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4126) {
            this.hpMax += (this.hpMax * 50 / 100);
        }
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map) && !this.isKhongLanh) {
            this.hpMax /= 2;
        }
        //mèo mun
        if (this.player.effectFlagBag.useMeoMun) {
            this.hpMax += (this.hpMax * 5 / 100);
        }

        //Kết hôn
        if (this.player.duockethon != 0) {
            this.hpMax += (this.hpMax * (this.player.duockethon * 5) / 100);
        }
        if (this.player.dakethon != 0) {
            this.hpMax += (this.hpMax * this.player.dakethon * 15 / 100);
        }

        //rồng siêu cấp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongSieuCap) {
            this.hpMax += (this.hpMax * 15 / 100);
        }


        // biến hình
        if (this.player.effectSkill.isBienHinh) {
            if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
                int percent = SkillUtil.getPercentHpMpBienHinh(player.effectSkill.levelBienHinh);
                this.hpMax += calPercent(this.hpMax, percent);
            }
        }

        //phu kien kich hoat
        switch (this.player.setClothes.pkkhMedusa) {
            case 2 -> this.hpMax += this.hpMax * 20 / 100;
            case 3 -> this.hpMax += this.hpMax * 30 / 100;
            case 5 -> this.hpMax += this.hpMax * 50 / 100;
        }
        // calc point tu tien o day
        double pointBase = this.hpMax;
        double pointAdd = 0;
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            pointAdd += pointBase * player.tuTien.getHPMPBuff() / 100;
        }
        if (player.tranPhapSu != null && player.tranPhapSu.isTranPhap()) {
            pointAdd += pointBase * player.tranPhapSu.getHPMPBuff() / 100;
        }
        if (player.nguThuSu != null && player.nguThuSu.isNguThu()) {
            pointAdd += pointBase * player.nguThuSu.getHPMPBuff() / 100;
        }
        if (player.linhThucSu != null && player.linhThucSu.isLinhThuc()) {
            pointAdd += pointBase * player.linhThucSu.getHPMPBuff() / 100;
        }
        if (player.phuChuSu != null && player.phuChuSu.isPhuChu()) {
            pointAdd += pointBase * player.phuChuSu.getHPMPBuff() / 100;
        }
        if (player.khongThiSu != null && player.khongThiSu.isKhongThi()) {
            pointAdd += pointBase * player.phuChuSu.getHPMPBuff() / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenThe()) {
            pointAdd += pointBase * player.luyenThe.getHPMPBuff() / 100;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            pointAdd += pointBase * player.tuMa.getHPMPBuff() / 100;
        }
        this.hpMax += pointAdd;
        // diem the chat
        if ((player.tuTien != null && player.tuTien.isTuTien())) {
            this.hpMax += this.hpMax * (player.tuTien.theChat * 20) / 100;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            this.hpMax += this.hpMax * (player.tuMa.theChat * 5) / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenTheReal()) {
            this.hpMax += this.hpMax * (player.luyenThe.sucManh * 30) / 100;
        }
        if (player.taixiu.chuyensinh > 0) {
            this.hpMax += this.hpMax * (3 * player.taixiu.chuyensinh) / 100;
        }
        mauGoc = this.hpMax;

    }

    private void setHp() {
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    private void setMpMax() {
        this.mpMax = this.mpg;
        if (player.huyet.isKichHoat()) {
            int sl = player.huyet.getLevelTinhHuyetCongDon();
            int cs1 = 0;
            switch (player.huyet.type) {
                case 1:
                    cs1 = 0;
                    for (int i = 0; i < sl; i++) {
                        cs1 += Huyet.TinhHuyetEffect.LEVEL_PARAM_TYPE[player.huyet.type][i][1];
                    }
                    tlMp.add(cs1);
                    break;
                case 4:
                    cs1 = 0;
                    for (int i = 0; i < sl; i++) {
                        cs1 += Huyet.TinhHuyetEffect.LEVEL_PARAM_TYPE[player.huyet.type][i][1];
                    }
                    tlMp.add(cs1);
                    break;
            }
        }
        this.mpMax += this.mpAdd;
        for (Integer tl : this.tlMp) {
            this.mpMax += (this.mpMax * tl / 100);
        }
        if (this.player.setClothes.picolo == 5) {
            this.mpMax *= 2;
        }
        if (this.player.setClothes.MaThan == 5) {
            this.mpMax *= 3;
        }
        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[1] > System.currentTimeMillis()) {
            this.mpMax += (this.mpMax * RewardBlackBall.R2S_2 / 100);
        }
        //set solomon
        if (this.player.setClothes.solomon >= 2) {
            this.mpMax += (this.mpMax * 5 / 100);
        }
        //set nhật ấn
        if (this.player.setClothes.nhatan == 5) {
            this.mpMax += (this.mpMax * 5 / 100);
        }
        if (this.player.fusion.typeFusion != 0) {
            this.mpMax += this.player.pet.nPoint.mpMax;
            if (this.player.khongThiSu.isKhongThi()) {
                this.mpMax += this.mpMax * player.khongThiSu.getHPMPBuff() / 100;
            }
        }

        if (this.player.isPet && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (((Pet) this.player).typePet == ConstPet.MABU) {
                this.mpMax += (this.mpMax * Math.max(20, player.khongThiSu.level * 20) / 100);
            }
        }
        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 7 || this.player.TrieuHoiCapBac == 8 || this.player.TrieuHoiCapBac == 9 || this.player.TrieuHoiCapBac == 10)) {
                switch (this.player.TrieuHoiCapBac) {
                    case 7:
                    case 8:
                        this.mpMax += this.mpMax * 6 / 100;
                        break;
                    case 9:
                        this.mpMax += this.mpMax * 7 / 100;
                        break;
                    case 10:
                        this.mpMax += this.mpMax * 10 / 100;
                        break;
                    default:
                        this.mpMax += this.mpMax * 1 / 100;
                        break;
                }
            }
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 1 || this.player.TrieuHoiCapBac == 3 || this.player.TrieuHoiCapBac == 4)) {
                switch (this.player.TrieuHoiCapBac) {
                    case 1:
                        this.mpMax += 100000;
                        break;
                    case 3:
                        this.mpMax += this.mpMax * 10 / 100;
                        break;
                    default:
                        this.mpMax += this.mpMax * 1 / 100;
                        break;
                }
            }
            // Đạo Lữ Song Tu
            if (this.player.petDaoLu != null && this.player.petDaoLu.status != DaoLu.GOHOME) {
                this.mpMax += calPercent(this.mpMax, this.player.petDaoLu.getNPointAddByType());
            }
        }
        if (this.player.isDaoLu) {
            this.mpMax += calPercent(this.mpMax, ((DaoLu) this.player).getNPointAddByType());
            if (((DaoLu) this.player).pointCapCanhGioi == 10) {
                this.mpMax += calPercent(this.mpMax, DaoLu.POWER_DAU_THANH);
            }
            this.mpMax += calPercent(this.mpMax, ((DaoLu) this.player).getNPointAddCapBac());
            this.mpMax += calPercent(this.mpMax, ((DaoLu) this.player).getNPointAddCapTinh());
        }
        //đuôi khỉ
        if (!this.player.isPet && this.player.itemTime.isDuoikhi || this.player.isPet && ((Pet) this.player).master.itemTime.isDuoikhi) {
            this.mpMax += (this.mpMax * 10 / 100);
        }
        if (!this.player.isPet && this.player.itemTime.isUseMayDo2 || this.player.isPet && ((Pet) this.player).master.itemTime.isUseMayDo2) {
            this.mpMax += (this.mpMax * 12 / 100);
        }
        //Kết hôn mp
        if (this.player.duockethon != 0) {
            this.mpMax += (this.mpMax * (this.player.duockethon * 5) / 100);
        }
        if (this.player.dakethon != 0) {
            this.mpMax += (this.mpMax * this.player.dakethon * 15 / 100);
        }
        //rồng siêu cấp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongSieuCap) {
            this.mpMax += (this.mpMax * 50 / 100);
        }
        //bổ khí
        if (this.player.itemTime != null && this.player.itemTime.isUseBoKhi) {
            this.mpMax *= 2;
        }
        if (this.player.isPl() && this.player.isTitleUse3 && this.player.lastTimeTitle3 > 0) {
            this.mpMax += (this.mpMax * 10 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse2 && this.player.lastTimeTitle2 > 0) {
            this.mpMax += (this.mpMax * 10 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse1 && this.player.lastTimeTitle1 > 0) {
            this.mpMax += (this.mpMax * 10 / 100);
        }
        //bí ngô
        if (this.player.itemTime != null && this.player.itemTime.isBiNgo) {
            this.mpMax += (this.mpMax * 12 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseBoKhi3) {
            this.mpMax += (this.mpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh) {
            this.mpMax += (this.mpMax * 90 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh1) {
            this.mpMax += this.mpMax;
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseXiMuoi) {
            this.mpMax += (this.mpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4042) {
            this.mpMax += (this.mpMax * 10 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4043) {
            this.mpMax += (this.mpMax * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4125) {
            this.mpMax += (this.mpMax * 30 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4126) {
            this.mpMax += (this.mpMax * 50 / 100);
        }
        //phù
        if (this.player.zone != null && MapService.gI().isMapBlackBallWar(this.player.zone.map.mapId)) {
            this.mpMax *= this.player.effectSkin.xHPKI;
        }
        //xiên cá
        if (this.player.effectFlagBag.useXienCa) {
            this.mpMax += (this.mpMax * 5 / 100);
        }


        // biến hình
        if (this.player.effectSkill.isBienHinh) {
            if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
                int percent = SkillUtil.getPercentHpMpBienHinh(player.effectSkill.levelBienHinh);
                this.mpMax += calPercent(this.mpMax, percent);
            }
        }

        //phu kien kich hoat
        switch (this.player.setClothes.pkkhMedusa) {
            case 2 -> this.mpMax += this.mpMax * 20 / 100;
            case 3 -> this.mpMax += this.mpMax * 30 / 100;
            case 5 -> this.mpMax += this.mpMax * 50 / 100;
        }
        double pointBase = this.mpMax;
        double pointAdd = 0;
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            pointAdd += pointBase * player.tuTien.getHPMPBuff() / 100;
        }
        if (player.tranPhapSu != null && player.tranPhapSu.isTranPhap()) {
            pointAdd += pointBase * player.tranPhapSu.getHPMPBuff() / 100;
        }
        if (player.nguThuSu != null && player.nguThuSu.isNguThu()) {
            pointAdd += pointBase * player.nguThuSu.getHPMPBuff() / 100;
        }
        if (player.linhThucSu != null && player.linhThucSu.isLinhThuc()) {
            pointAdd += pointBase * player.linhThucSu.getHPMPBuff() / 100;
        }
        if (player.phuChuSu != null && player.phuChuSu.isPhuChu()) {
            pointAdd += pointBase * player.phuChuSu.getHPMPBuff() / 100;
        }
        if (player.khongThiSu != null && player.khongThiSu.isKhongThi()) {
            pointAdd += pointBase * player.phuChuSu.getHPMPBuff() / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenThe()) {
            pointAdd += pointBase * player.luyenThe.getHPMPBuff() / 100;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            pointAdd += pointBase * player.tuMa.getHPMPBuff() / 100;
        }
        this.mpMax += pointAdd;
        if ((player.tuTien != null && player.tuTien.isTuTien())) {
            this.mpMax += this.mpMax * (player.tuTien.theChat * 20) / 100;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            this.mpMax += this.mpMax * (player.tuMa.theChat * 5) / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenTheReal()) {
            this.mpMax += this.mpMax * (player.luyenThe.theChat * 30) / 100;
        }
        if (player.taixiu.chuyensinh > 0) {
            this.mpMax += this.mpMax * (3 * player.taixiu.chuyensinh) / 100;
        }
    }

    private void setMp() {
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    private void setDame() {
        this.dame = this.dameg;
        if (player.huyet.isKichHoat()) {
            int slTrong = player.huyet.getLevelTinhHuyetCongDon();
            int cs1 = 0;
            int cs2 = 0;
            for (int i = 0; i < slTrong; i++) {
                cs1 += Huyet.TinhHuyetEffect.LEVEL_PARAM_TYPE[player.huyet.type][i][0];
                cs2 += Huyet.TinhHuyetEffect.LEVEL_PARAM_TYPE[player.huyet.type][i][1];
            }
            switch (player.huyet.type) {
                case 0:
                    tlDame.add(cs1);
                    tlDameCrit.add(cs2);
                    break;
                case 2, 3:
                    int dameTurn1 = ((int) (cs1 * 0.23));
                    int dameTurn2 = ((int) (cs2 * 0.23));
                    tlDame.add(dameTurn1);
                    tlDame.add(dameTurn2);
                    break;
            }
        }

        this.dame += this.dameAdd;
        // + point luyen the
        //đồ
        for (Integer tl : this.tlDame) {
            this.dame += (this.dame * tl / 100);
        }
        for (Integer tl : this.tlSDDep) {
            this.dame += (this.dame * tl / 100);
        }

        if (this.player.fusion.typeFusion != 0) {
            this.dame += this.player.pet.nPoint.dame;
            if (this.player.khongThiSu.isKhongThi()) {
                this.dame += this.dame * player.khongThiSu.getDameBuff() / 100;
            }
        }

        if (this.player.isPet && ((Pet) this.player).master.fusion.typeFusion != ConstPlayer.NON_FUSION) {
            if (((Pet) this.player).typePet == ConstPet.MABU) {
                this.dame += (this.dame * Math.max(5, player.khongThiSu.level * 5) / 100);
                this.tlDameCrit.add(Math.max(15, player.khongThiSu.level * 15));
            }
        }

        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 5 || this.player.TrieuHoiCapBac == 7 || this.player.TrieuHoiCapBac == 8 || this.player.TrieuHoiCapBac == 9 || this.player.TrieuHoiCapBac == 10)) {
                switch (this.player.TrieuHoiCapBac) {
                    case 5:
                    case 7:
                    case 8:
                        this.dame += this.dame * 6 / 100;
                        break;
                    case 9:
                        this.dame += this.dame * 7 / 100;
                        break;
                    case 10:
                        this.dame += this.dame * 10 / 100;
                        break;
                    default:
                        this.dame += this.dame * 1 / 100;
                        break;
                }
            }
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 2 || this.player.TrieuHoiCapBac == 3)) {
                this.dame += this.dame * 10 / 100;
            }
            if (this.player.petDaoLu != null && this.player.petDaoLu.status != DaoLu.GOHOME) {
                this.dame += calPercent(this.dame, this.player.petDaoLu.getNPointAddByType());
                if (this.player.petDaoLu.pointCapCanhGioi == 10) {
                    this.dame += calPercent(this.dame, DaoLu.POWER_DAU_THANH);
                }
                this.dame += calPercent(this.dame, this.player.petDaoLu.getNPointAddCapBac());
                this.dame += calPercent(this.dame, this.player.petDaoLu.getNPointAddCapTinh());
            }
        }
        if (this.player.isDaoLu) {
            this.dame += calPercent(this.dame, ((DaoLu) this.player).getNPointAddByType());
            if (((DaoLu) this.player).pointCapCanhGioi == 10) {
                this.dame += calPercent(this.dame, DaoLu.POWER_DAU_THANH);
            }
            this.dame += calPercent(this.dame, ((DaoLu) this.player).getNPointAddCapBac());
            this.dame += calPercent(this.dame, ((DaoLu) this.player).getNPointAddCapTinh());
        }

        //đuôi khỉ
        if (!this.player.isPet && this.player.itemTime.isDuoikhi || this.player.isPet && ((Pet) this.player).master.itemTime.isDuoikhi) {
            this.dame += (this.dame * 15 / 100);
        }

        //rồng siêu cấp
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isRongSieuCap) {
            this.dame += (this.dame * 10 / 100);
        }
        //Kết hôn
        if (this.player.duockethon != 0) {
            this.dame += (this.dame * (this.player.duockethon * 3) / 100);
        }
        if (this.player.dakethon != 0) {
            this.dame += (this.dame * this.player.dakethon * 10 / 100);
        }

        if (!this.player.isPet && this.player.itemTime.isUseMayDo2 || this.player.isPet && ((Pet) this.player).master.itemTime.isUseMayDo2) {
            this.dame += (this.dame * 12 / 100);
        }
        //hợp thể
        //cuồng nộ
        if (this.player.itemTime != null && this.player.itemTime.isUseCuongNo) {
            this.dame *= 2;
        }
        if (this.player.itemTime != null && this.player.itemTime.isBiNgo) {
            this.dame += (this.dame * 20 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse3 && this.player.lastTimeTitle3 > 0) {
            this.dame += (this.dame * 10 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse2 && this.player.lastTimeTitle2 > 0) {
            this.dame += (this.dame * 10 / 100);
        }
        if (this.player.isPl() && this.player.isTitleUse1 && this.player.lastTimeTitle1 > 0) {
            this.dame += (this.dame * 10 / 100);
        }

        //thức ăn
        if (!this.player.isPet && this.player.itemTimesieucap.isEatMeal || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isEatMeal) {
            this.dame += (this.dame * 10 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseCuongNo3) {
            this.dame += (this.dame * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh) {
            this.dame += (this.dame * 90 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh1) {
            this.dame += this.dame;
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseXiMuoi) {
            this.dame += (this.dame * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4042) {
            this.dame += (this.dame * 10 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4043) {
            this.dame += (this.dame * 20 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4125) {
            this.dame += (this.dame * 30 / 100);
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isUseTrungThu && this.player.itemTimesieucap.iconBanh == 4126) {
            this.dame += (this.dame * 50 / 100);
        }
        //giảm dame
        this.dame -= (this.dame * tlSubSD / 100);
        //map cold
        if (this.player.zone != null && MapService.gI().isMapCold(this.player.zone.map) && !this.isKhongLanh) {
            this.dame /= 2;
        }
        //ngọc rồng đen 1 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[0] > System.currentTimeMillis()) {
            this.dame += (this.dame * RewardBlackBall.R1S_2 / 100);
        }
        //set nhan hoang
        if (this.player.setClothes.NhanHoang == 5) {
            this.dame += (this.dame * 10 / 100);
            this.tlDameCrit.add(35);
        }
        //set ma than namec (thanh ton )
        if (this.player.setClothes.MaThan == 5) {
            this.dame += (this.dame * 5 / 100);
            this.tlDameCrit.add(75);
        }
//        ////set thien tu xay da ( set thanh ton )
        if (this.player.setClothes.ThienTu == 5) {
            this.dame += (this.dame * 5 / 100);
            this.tlDameCrit.add(75);
        }
//      nguyen thuy
        if (this.player.setClothes.nguyenthuytd == 5) {
            this.dame += (this.dame * 15 / 100);
            this.tlDameCrit.add(75);
        }

        if (this.player.setClothes.nguyenthuyxd == 5) {
            this.dame += (this.dame * 10 / 100);
            this.tlDameCrit.add(100);
        }
        ///set NGUYEN THUY  nm
        if (this.player.setClothes.nguyenthuynm == 5) {
            this.dame += (this.dame * 10 / 100);
            this.tlDameCrit.add(120);
        }

        if (this.player.setClothes.thongkhonm == 5) {
            this.tlDameCrit.add(175);
            this.dame += (this.dame * 15 / 100);
        }
        if (this.player.setClothes.thongkhotd == 5) {
            this.tlDameCrit.add(120);
            this.dame += (this.dame * 25 / 100);
        }

        if (this.player.setClothes.thongkhoxd == 5) {
            this.tlDameCrit.add(200);
            this.dame += (this.dame * 15 / 100);
        }
        //set solomon
        if (this.player.setClothes.solomon >= 2) {
            this.dame += (this.dame * 3 / 100);
            this.tlDameCrit.add(10);
        }
        // set tien
        if (this.player.setClothes.setTienTD == 5) {
            this.tlDameCrit.add(300);
            this.dame += this.dame * 50 / 100;
        }
        if (this.player.setClothes.setTienNM == 5) {
            this.tlDameCrit.add(350);
            this.dame += this.dame * 40 / 100;
        }
        if (this.player.setClothes.setTienXayda == 5) {
            this.tlDameCrit.add(400);
            this.dame += this.dame * 40 / 100;
        }

        //set nguyệt ấn
        if (this.player.setClothes.nguyetan == 5) {
            this.dame += (this.dame * 3 / 100);
        }
        // ma hóa trang bị
        if (this.player.setClothes.mahoa == 5) {
            this.dame += (this.dame * 3 / 100);
        }
        // thần hóa trang bị
        if (this.player.setClothes.thanhoa == 5) {
            this.dame += (this.dame * 3 / 100);
        }
        //nguyen thuy trang bị
        if (this.player.setClothes.nguyenthuy == 5) {
            this.dame += (this.dame * 12 / 100);
            this.tlDameCrit.add(60);
        }
        // hu vo trang bị
        if (this.player.setClothes.huvo == 5) {
            this.dame += (this.dame * 12 / 100);
            this.tlDameCrit.add(60);
        }
        //phóng heo
        if (this.player.effectFlagBag.usePhongHeo) {
            this.dame += (this.dame * 3 / 100);
        }
        // Set KH Jiren
        if (this.player.setClothes.isSetJiren()) {
            this.dame += this.dame * 25 / 100;
            this.tlDameCrit.add(200);
        }
        // Set KH Goku UI
        if (this.player.setClothes.isSetJirenTD()) {
            this.dame += this.dame * 30 / 100;
            this.tlDameCrit.add(200);
        }
        if (this.player.setClothes.isSetJirenNM()) {
            this.dame += this.dame * 25 / 100;
            this.tlDameCrit.add(250);
        }
        if (this.player.setClothes.isSetJirenXD()) {
            this.dame += this.dame * 25 / 100;
            this.tlDameCrit.add(300);
        }
        //khỉ
        if (this.player.effectSkill.isMonkey) {
            if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
                int percent = SkillUtil.getPercentDameMonkey(player.effectSkill.levelMonkey);
                this.dame += (this.dame * percent / 100);
            }
        }


        // biến hình
        if (this.player.effectSkill.isBienHinh) {
            if (!this.player.isPet || ((Pet) this.player).status != Pet.FUSION) {
                int percent = SkillUtil.getPercentHpMpBienHinh(player.effectSkill.levelBienHinh);
                this.dame += calPercent(this.dame, percent);
            }
        }

        //phu kien kich hoat
        switch (this.player.setClothes.pkkhMedusa) {
            case 2 -> this.dame += this.dame * 10 / 100;
            case 3 -> this.dame += this.dame * 20 / 100;
            case 5 -> this.dame += this.dame * 30 / 100;
        }
        double pointBase = this.dame;
        double pointAdd = 0;
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            pointAdd += pointBase * player.tuTien.getDameBuff() / 100;
        }
        if (player.tranPhapSu != null && player.tranPhapSu.isTranPhap()) {
            pointAdd += pointBase * player.tranPhapSu.getDameBuff() / 100;
        }
        if (player.nguThuSu != null && player.nguThuSu.isNguThu()) {
            pointAdd += pointBase * player.nguThuSu.getDameBuff() / 100;
        }
        if (player.linhThucSu != null && player.linhThucSu.isLinhThuc()) {
            pointAdd += pointBase * player.linhThucSu.getDameBuff() / 100;
        }
        if (player.phuChuSu != null && player.phuChuSu.isPhuChu()) {
            pointAdd += pointBase * player.phuChuSu.getDameBuff() / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenThe()) {
            pointAdd += pointBase * player.luyenThe.getDameBuff() / 100;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            pointAdd += pointBase * player.tuMa.getDameBuff() / 100;
        }
        this.dame += pointAdd;
        // diem the chat
        if ((player.tuTien != null && player.tuTien.isTuTien())) {
            this.dame += this.dame * (player.tuTien.sucManh * 15) / 100;
        }
        if (player.tuMa != null && player.tuMa.isTuMa()) {
            this.dame += this.dame * (player.tuMa.sucManh * 20) / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenTheReal()) {
            this.dame += this.dame * (player.luyenThe.sucManh * 5) / 100;
        }
        if (player.taixiu.chuyensinh > 0) {
            this.dame += this.dame * (3 * player.taixiu.chuyensinh) / 100;
        }
    }

    private void setDef() {
        this.def = this.defg * 4;
        this.def += this.defAdd;
        //đồ
        for (Integer tl : this.tlDef) {
            this.def += (this.def * tl / 100);
        }
        //ngọc rồng đen 2 sao
        if (this.player.rewardBlackBall.timeOutOfDateReward[3] > System.currentTimeMillis()) {
            this.def += ((long) this.def * RewardBlackBall.R4S_2 / 100f);
        }

        if (this.player.isPl()) {
            if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 4 || this.player.TrieuHoiCapBac == 7 || this.player.TrieuHoiCapBac == 8 || this.player.TrieuHoiCapBac == 9 || this.player.TrieuHoiCapBac == 10)) {
                switch (this.player.TrieuHoiCapBac) {
                    case 4:
                        this.def += 100000;
                        break;
                    case 7:
                    case 8:
                        this.def += this.def * 6 / 100;
                        break;
                    case 9:
                        this.def += this.def * 7 / 100;
                        break;
                    default:
                        this.def += this.def * 1 / 100;
                        break;
                }
            }
        }

        //phu kien kich hoat
        switch (this.player.setClothes.pkkhMedusa) {
            case 2 -> this.def += calPercent(this.def, 20);
            case 3 -> this.def += calPercent(this.def, 30);
            case 5 -> this.def += calPercent(this.def, 50);
        }
        double pointBase = this.def;
        double pointAdd = 0;
        if (player.tuTien != null && player.tuTien.isTuTien()) {
            pointAdd += pointBase * player.tuTien.getDefBuff() / 100;
        }
        if (player.tranPhapSu != null && player.tranPhapSu.isTranPhap()) {
            pointAdd += pointBase * player.tranPhapSu.getDefBuff() / 100;
        }
        if (player.nguThuSu != null && player.nguThuSu.isNguThu()) {
            pointAdd += pointBase * player.nguThuSu.getDefBuff() / 100;
        }
        if (player.linhThucSu != null && player.linhThucSu.isLinhThuc()) {
            pointAdd += pointBase * player.linhThucSu.getDefBuff() / 100;
        }
        if (player.phuChuSu != null && player.phuChuSu.isPhuChu()) {
            pointAdd += pointBase * player.phuChuSu.getDefBuff() / 100;
        }
        if (player.khongThiSu != null && player.khongThiSu.isKhongThi()) {
            pointAdd += pointBase * player.phuChuSu.getDefBuff() / 100;
        }
        if (player.luyenThe != null && player.luyenThe.isLuyenThe()) {
            pointAdd += pointBase * player.luyenThe.getDefBuff() / 100;
        }
        this.def += pointAdd;
    }

    private void setCrit() {
        this.crit = this.critg;
        this.crit += this.critAdd;
        if (this.player.effectSkill.isMonkey) {
            this.crit = 110;
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh) {
            this.crit = 110;
        }
        if (this.player.itemTimesieucap != null && this.player.itemTimesieucap.isBienhinh1) {
            this.crit = 110;
        }
    }

    private void resetPoint() {
        this.voHieuChuong = 0;
        this.hpAdd = 0;
        this.mpAdd = 0;
        this.dameAdd = 0;
        this.defAdd = 0;
        this.critAdd = 0;
        this.tlHp.clear();
        this.tlMp.clear();
        this.tlDef.clear();
        this.tlDame.clear();
        this.tlDameCrit.clear();
        this.tyLeGiamDame = 0;
        this.tlDameAttMob.clear();
        this.tlHpHoiBanThanVaDongDoi = 0;
        this.tlMpHoiBanThanVaDongDoi = 0;
        this.hpHoi = 0;
        this.mpHoi = 0;
        this.mpHoiCute = 0;
        this.tlHpHoi = 0;
        this.tlMpHoi = 0;
        this.tlHutHp = 0;
        this.tlHutMp = 0;
        this.tlHutHpMob = 0;
        this.tlHutHpMpXQ = 0;
        this.hpHoiAdd = 0;
        this.mpHoiAdd = 0;
        this.tlPST = 0;
        this.tlTNSM.clear();
        this.tlDameAttMob.clear();
        this.tlGold = 0;
        this.tlNeDon = 0;
        this.tlchinhxac = 0;
        this.tlSDDep.clear();
        this.tlSubSD = 0;
        this.tlHpGiamODo = 0;
        this.xLinhKhi = 0;
        this.xHoiLinhKhi = 0;
        this.xDameLinhCan = 0;
        this.xTuVi = 0;

        this.tlDameKim = 0;
        this.tlDameMoc = 0;
        this.tlDameThuy = 0;
        this.tlDameHoa = 0;
        this.tlDameTho = 0;

        this.tlDamePhong = 0;
        this.tlDameLoi = 0;
        this.tlDameQuang = 0;
        this.tlDameAm = 0;

        this.teleport = false;

        this.wearingVoHinh = false;
        this.isKhongLanh = false;
        this.khangTDHS = false;

        phamKhiLinh = 0;
        khiLinhType = -1;

        phamNguHanh = 0;
        typeNguHanh = -1;
    }

    public void addHp(double hp) {
        this.hp += hp;
        if (this.hp > this.hpMax) {
            this.hp = this.hpMax;
        }
    }

    public void addMp(double mp) {
        this.mp += mp;
        if (this.mp > this.mpMax) {
            this.mp = this.mpMax;
        }
    }

    public void setHp(double hp) {
        if (hp > this.hpMax) {
            this.hp = this.hpMax;
        } else {
            this.hp = hp;
        }
    }

    public void setMp(double mp) {
        if (mp > this.mpMax) {
            this.mp = this.mpMax;
        } else {
            this.mp = mp;
        }
    }

    private void setIsCrit() {
        if (intrinsic != null && intrinsic.id == 25 && this.getCurrPercentHP() <= intrinsic.param1) {
            isCrit = true;
        } else if (isCrit100) {
            isCrit100 = false;
            isCrit = true;
        } else {
            isCrit = Util.isTrue(this.crit, ConstRatio.PER100);
        }
    }

    public double getDameAttack(boolean isAttackMob) {
        setIsCrit();
        boolean autoCrit = false;
        double dameAttack = this.dame;
        intrinsic = this.player.playerIntrinsic.intrinsic;
        percentDameIntrinsic = 0;
        long percentDameSkill = 0;
        long percentXDame = 0;
        Skill skillSelect = player.playerSkill.skillSelect;
        switch (skillSelect.template.id) {
            case Skill.DRAGON:
                if (intrinsic.id == 1) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.KAMEJOKO:
                if (intrinsic.id == 2) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.songoku == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.NhanHoang == 5) {
                    percentXDame = 150;
                }
                if (this.player.setClothes.nguyenthuytd == 5) {
                    percentXDame = 220;
                }
                // thong kho
                if (this.player.setClothes.thongkhotd == 5) {
                    percentXDame = 275;
                }
                // Jiren
                if (this.player.setClothes.setJirenTD == 5) {
                    percentXDame = 350;
                }
                // Goku UI
                if (this.player.setClothes.setGokuUITD == 5) {
                    percentXDame = 750;
                }
                // set tien
                if (this.player.setClothes.setTienTD == 5) {
                    percentXDame = 1000;
                }
                if (player.tuTien.isTuTien()) {
                    percentXDame += percentDameSkill * (player.tuTien.xParam);
                }
                break;
            case Skill.GALICK:
                if (intrinsic.id == 16) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.kakarot == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.ThienTu == 5) {
                    percentXDame = 120;
                }
                if (this.player.setClothes.nguyenthuyxd == 5) {
                    percentXDame = 130;
                }
                if (this.player.setClothes.thongkhoxd == 5) {
                    percentXDame = 160;
                }
                // Jiren
                if (this.player.setClothes.setJirenXD == 5) {
                    percentXDame = 230;
                }
                // Goku UI
                if (this.player.setClothes.setGokuUIXD == 5) {
                    percentXDame = 250;
                }
                if (this.player.setClothes.setTienXayda == 5) {
                    percentXDame = 300;
                }
                dameAttack = this.hpMax / 5;
                if (player.tuMa.isTuMa()) {
                    percentXDame *= Math.max(1, player.tuMa.maTinh);
                }
                break;
            case Skill.ANTOMIC:
                percentDameSkill = skillSelect.damage;
                if (intrinsic.id == 17) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                dameAttack = this.hpMax / 2;
                if (player.tuMa.isTuMa()) {
                    percentDameSkill *= Math.max(1, player.tuMa.maTinh);
                }
                break;
            case Skill.DEMON:
                if (intrinsic.id == 8) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.MASENKO:
                if (intrinsic.id == 9) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.MaThan == 5) {
                    percentXDame = 110;
                }
                if (this.player.setClothes.nguyenthuynm == 5) {
                    percentXDame = 140;
                }
                break;
            case Skill.KAIOKEN:
                if (intrinsic.id == 26) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.thienXinHang == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.NhanHoang == 5) {
                    percentXDame = 150;
                }
                if (this.player.setClothes.nguyenthuytd == 5) {
                    percentXDame = 175;
                }
                if (this.player.setClothes.setTienTD == 5) {
                    percentXDame = 250;
                }
                break;
            case Skill.SUPER_KAME, Skill.LIEN_HOAN_CHUONG:
                percentDameSkill = skillSelect.damage;
                break;
            case Skill.LIEN_HOAN:
                if (intrinsic.id == 13) {
                    percentDameIntrinsic = intrinsic.param1;
                }
                percentDameSkill = skillSelect.damage;
                if (this.player.setClothes.ocTieu == 5) {
                    percentXDame = 100;
                }
                if (this.player.setClothes.MaThan == 5) {
                    percentXDame = 120;
                }
                if (this.player.setClothes.nguyenthuynm == 5) {
                    percentXDame = 140;
                }
                if (this.player.setClothes.thongkhonm == 5) {
                    percentXDame = 175;
                }
                // Jiren
                if (this.player.setClothes.setJirenNM == 5) {
                    percentXDame = 250;
                }
                // Goku UI
                if (this.player.setClothes.setGokuUINM == 5) {
                    percentXDame = 300;
                }
                if (this.player.setClothes.setTienNM == 5) {
                    percentXDame = 350;
                }
                if (player.luyenThe.isLuyenTheReal()) {
                    autoCrit = true;
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                dameAttack *= 2;
                dameAttack = Util.GioiHannextdame(dameAttack - (dameAttack * 5 / 100), dameAttack + (dameAttack * 5 / 100));
                return dameAttack;
            case Skill.MAKANKOSAPPO:
                percentDameSkill = skillSelect.damage;
                return this.mpMax * percentDameSkill / 100;
            case Skill.QUA_CAU_KENH_KHI:
                double dame = this.dame * 40;
                if (this.player.setClothes.kirin == 5) {
                    dame *= 2;
                }
                dame = dame + (Util.nextInt(-5, 5) * dame / 100);
                return dame;
        }
        if (player.setClothes.tienKhi >= 5) {
            percentXDame += 100;
        }
        if (player.setClothes.thanhKhi >= 5) {
            percentXDame += 300;
        }
        if (player.setClothes.thanKhi >= 5) {
            percentXDame += 500;
        }
        if (intrinsic.id == 18 && this.player.effectSkill.isMonkey) {
            percentDameIntrinsic = intrinsic.param1;
        }
        if (percentDameSkill != 0) {
            dameAttack = dameAttack * percentDameSkill / 100;
        }
        dameAttack += (dameAttack * percentDameIntrinsic / 100);
        dameAttack += (dameAttack * dameAfter / 100);

        if (isAttackMob) {
            for (Integer tl : this.tlDameAttMob) {
                dameAttack += (dameAttack * tl / 100);
            }
        }
        dameAfter = 0;
        if (this.player.isPet && ((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
            dameAttack *= 2;
        }
        if (isCrit || autoCrit) {
            if (SkillUtil.isUseSkillChuong(this.player) && this.player.chienthan.tasknow == 6) {
                this.player.chienthan.dalamduoc++;
            }
            dameAttack = dameAttack * 2;
            for (Integer integer : tlDameCrit) {
                dameAttack += dameAttack * integer / 150;
            }
            if (autoCrit) {
                dameAttack *= 1.5;
            }
        }
        dameAttack += dameAttack * percentXDame / 100;
        dameAttack = Util.GioiHannextdame(dameAttack - (dameAttack * 5 / 100), dameAttack + (dameAttack * 5 / 100));
        if (player.isPl()) {
            if (player.inventory.haveOption(player.inventory.itemsBody, 5, 159)) {
                if (Util.canDoWithTime(player.lastTimeUseOption, 60000) && SkillUtil.isSkillXDame(player.playerSkill.skillSelect.template.id)) {
                    dameAttack *= player.inventory.getParam(player.inventory.itemsBody.get(5), 159);
                    player.lastTimeUseOption = System.currentTimeMillis();
                    Service.getInstance().sendThongBao(player, "|1|Bạn vừa gây ra x" + player.inventory.getParam(player.inventory.itemsBody.get(5), 159) + " Sát thương chiêu thức cơ bản");
                }
            }
        }
        if (this.player.TrieuHoipet != null && this.player.TrieuHoipet.getStatus() != Thu_TrieuHoi.GOHOME && (this.player.TrieuHoiCapBac == 6 || this.player.TrieuHoiCapBac == 9 || this.player.TrieuHoiCapBac == 10)) {
            switch (this.player.TrieuHoiCapBac) {
                case 6:
                case 9:
                    dameAttack += dameAttack * ((this.player.TrieuHoiLevel + 1) / 3f) / 100;
                    break;
                default:
                    dameAttack += dameAttack * (this.player.TrieuHoiLevel + 1) / 100;
                    break;
            }
        }
        return dameAttack;
    }

    public double getCurrPercentHP() {
        if (this.hpMax == 0) {
            return 100;
        }
        return this.hp * 100 / this.hpMax;
    }

    public double getCurrPercentMP() {
        return this.mp * 100 / this.mpMax;
    }

    public void setFullHpMp() {
        this.hp = this.hpMax;
        this.mp = this.mpMax;
    }

    public void subHP(double sub) {
        this.hp -= sub;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void subMP(int sub) {
        this.mp -= sub;
        if (this.mp < 0) {
            this.mp = 0;
        }
    }

    public double calSucManhTiemNang(double tiemNang) {
        if (power < getPowerLimit()) {
            for (Integer tl : this.tlTNSM) {
                tiemNang += (tiemNang * tl / 100);
            }
            if (this.player.cFlag != 0) {
                if (this.player.cFlag == 8) {
                    tiemNang += (tiemNang * 10 / 100);
                } else {
                    tiemNang += (tiemNang * 5 / 100);
                }
            }
            double tn = tiemNang;
            if (this.player.charms.tdTriTue > System.currentTimeMillis()) {
                tiemNang += tn;
            }
            if (this.player.charms.tdTriTue3 > System.currentTimeMillis()) {
                tiemNang += tn * 2;
            }
            if (this.player.charms.tdTriTue4 > System.currentTimeMillis()) {
                tiemNang += tn * 3;
            }

            if (!this.player.isPet && this.player.itemTime.isDuoikhi || this.player.isPet && ((Pet) this.player).master.itemTime.isDuoikhi) {
                tiemNang += tn * 3;
            }
            if (!this.player.isPet && this.player.itemTimesieucap.isKeo || this.player.isPet && ((Pet) this.player).master.itemTimesieucap.isKeo) {
                tiemNang += tn * 2;
            }
            if (this.intrinsic != null && this.intrinsic.id == 24) {
                tiemNang += (tiemNang * this.intrinsic.param1 / 100);
            }
            if (this.power >= 60000000000L) {
                tiemNang -= (tiemNang * 80 / 100);
            }
            if (this.player.isPet) {
                if (((Pet) this.player).master.charms.tdDeTu > System.currentTimeMillis()) {
                    tiemNang += tn * 2;
                }
            }
            if (player.luyenThe.isLuyenTheReal()) {
                tiemNang *= 20;
            } else {
                tiemNang *= Manager.RATE_EXP_SERVER;
            }
            tiemNang = calSubTNSM(tiemNang);
            if (tiemNang <= 0) {
                tiemNang = 5;
            }
        } else {
            tiemNang = 10;
        }
        return tiemNang;
    }

    public double calSubTNSM(double tiemNang) {
        if (power >= 120000000000000L) {
            tiemNang /= 200000;
        } else if (power >= 50000000000000L) {
            tiemNang /= 100000;
        } else if (power >= 25000000000000L) {
            tiemNang /= 75000;
        } else if (power >= 10000000000000L) {
            tiemNang /= 50000;
        } else if (power >= 5000000000000L) {
            tiemNang /= 30000;
        } else if (power >= 2000000000000L) {
            tiemNang /= 10000;
        } else if (power >= 1300000000000L) {
            tiemNang /= 5000;
        } else if (power >= 1000000000000L) {
            tiemNang /= 2000;
        } else if (power >= 300000000000L) {
            tiemNang /= 1000;
        } else if (power >= 180000000000L) {
            tiemNang /= 700;
        } else if (power >= 110000000000L) {
            tiemNang /= 500;
        } else if (power >= 100000000000L) {
            tiemNang /= 300;
        } else if (power >= 90000000000L) {
            tiemNang -= (tiemNang * 95 / 100);
        } else if (power >= 80000000000L) {
            tiemNang -= (tiemNang * 90 / 100);
        }
        return tiemNang;
    }

    public short getTileHutHp(boolean isMob) {
        if (isMob) {
            return (short) (this.tlHutHp + this.tlHutHpMob);
        } else {
            return this.tlHutHp;
        }
    }

    public short getTiLeHutMp() {
        return this.tlHutMp;
    }

    public double subDameInjureWithDeff(double dame) {
        double def = this.def;
        if (player.tuTien.isTuTien() && player.tuTien.linhCan.getLinhCanType() == 4) {
            def += def * player.tuTien.linhCan.getThuocTinhLinhCan().getParam() / 100;
        }
        dame -= def;
        if (this.player.itemTime.isUseGiapXen) {
            dame /= 2;
        }
        if (this.player.itemTimesieucap.isUseGiapXen3) {
            dame /= 2.5;
        }
        if (player.tuTien.isTuTien() && player.tuTien.linhCan.getLinhCanType() == 4) {
            dame /= (player.tuTien.linhCan.getThuocTinhLinhCan().getParam() / 100f);
        }
        if (player.tuTien.isTuTien() && player.tuTien.linhCan.getLinhCanType() == 7) {
            dame /= (player.tuTien.linhCan.getThuocTinhLinhCan().getParam() / 100f);
        }
        if (dame < 0) {
            dame = 1;
        }
        return dame;
    }

    public long getPowerLimit() {
        switch (limitPower) {
            case 0:
                return 17999999999L;
            case 1:
                return 18999999999L;
            case 2:
                return 20999999999L;
            case 3:
                return 24999999999L;
            case 4:
                return 30999999999L;
            case 5:
                return 40999999999L;
            case 6:
                return 60999999999L;
            case 7:
                return 80999999999L;
            case 8:
                return 120999999999L;
            case 9:
                return 180999999999L;
            case 10:
                return 400999999999L;
            case 11:
                return 10000999999999L;
            case 12:
                return 100999999999999L;
            case 13:
                return 500999999999999L;
            default:
                return 0;
        }
    }

    public long getPowerNextLimit() {
        switch (limitPower + 1) {
            case 0:
                return 17999999999L;
            case 1:
                return 18999999999L;
            case 2:
                return 20999999999L;
            case 3:
                return 24999999999L;
            case 4:
                return 30999999999L;
            case 5:
                return 40999999999L;
            case 6:
                return 60999999999L;
            case 7:
                return 80999999999L;
            case 8:
                return 120999999999L;
            case 9:
                return 180999999999L;
            case 10:
                return 400999999999L;
            case 11:
                return 10000999999999L;
            case 12:
                return 100999999999999L;
            case 13:
                return 500999999999999L;

            default:
                return 0;
        }
    }

    public int getHpMpLimit(boolean isDaoLu) {
        return isDaoLu ? getHpMpLimit() / 10 : getHpMpLimit();
    }

    public int getHpMpLimit() {
        if (limitPower == 0) {
            return 220_000;
        }
        if (limitPower == 1) {
            return 240_000;
        }
        if (limitPower == 2) {
            return 280_000;
        }
        if (limitPower == 3) {
            return 320_000;
        }
        if (limitPower == 4) {
            return 360_000;
        }
        if (limitPower == 5) {
            return 400_000;
        }
        if (limitPower == 6) {
            return 450_000;
        }
        if (limitPower == 7) {
            return 500_000;
        }
        if (limitPower == 8) {
            return 560_000;
        }
        if (limitPower == 9) {
            return 600_000;
        }
        if (limitPower == 10) {
            return 650_000;
        }
        if (limitPower == 11) {
            return 700_000;
        }
        if (limitPower == 12) {
            return 725_000;
        }
        if (limitPower == 13) {
            return 750_000;
        }
        return 0;
    }

    public int getDameLimit(boolean isDaoLu) {
        return isDaoLu ? getDameLimit() / 10 : getDameLimit();
    }

    public int getDameLimit() {
        if (limitPower == 0) {
            return 11000;
        }
        if (limitPower == 1) {
            return 12000;
        }
        if (limitPower == 2) {
            return 15000;
        }
        if (limitPower == 3) {
            return 18000;
        }
        if (limitPower == 4) {
            return 20000;
        }
        if (limitPower == 5) {
            return 22000;
        }
        if (limitPower == 6) {
            return 25000;
        }
        if (limitPower == 7) {
            return 28888;
        }
        if (limitPower == 8) {
            return 29999;
        }
        if (limitPower == 9) {
            return 31666;
        }
        if (limitPower == 10) {
            return 33000;
        }
        if (limitPower == 11) {
            return 35000;
        }
        if (limitPower == 12) {
            return 36000;
        }
        if (limitPower == 13) {
            return 38888;
        }
        return 0;
    }

    public short getDefLimit(boolean isDaoLu) {
        return isDaoLu ? (short) (getDefLimit() / 10) : getDefLimit();
    }

    public short getDefLimit() {
        if (limitPower == 0) {
            return 550;
        }
        if (limitPower == 1) {
            return 600;
        }
        if (limitPower == 2) {
            return 700;
        }
        if (limitPower == 3) {
            return 800;
        }
        if (limitPower == 4) {
            return 1000;
        }
        if (limitPower == 5) {
            return 1200;
        }
        if (limitPower == 6) {
            return 1400;
        }
        if (limitPower == 7) {
            return 1600;
        }
        if (limitPower == 8) {
            return 1700;
        }
        if (limitPower == 9) {
            return 1800;
        }
        if (limitPower == 10) {
            return 2500;
        }
        if (limitPower == 11) {
            return 3500;
        }
        if (limitPower == 12) {
            return 5500;
        }
        if (limitPower == 13) {
            return 6000;
        }
        return 0;
    }

    public byte getCritLimit() {
        if (limitPower == 0) {
            return 5;
        }
        if (limitPower == 1) {
            return 6;
        }
        if (limitPower == 2) {
            return 7;
        }
        if (limitPower == 3) {
            return 8;
        }
        if (limitPower == 4) {
            return 9;
        }
        if (limitPower == 5) {
            return 10;
        }
        if (limitPower == 6) {
            return 10;
        }
        if (limitPower == 7) {
            return 10;
        }
        if (limitPower == 8) {
            return 10;
        }
        if (limitPower == 9) {
            return 10;
        }
        if (limitPower == 10) {
            return 15;
        }
        if (limitPower == 11) {
            return 20;
        }
        if (limitPower == 12) {
            return 21;
        }
        if (limitPower == 13) {
            return 22;
        }
        return 0;
    }

    //**************************************************************************
    //POWER - TIEM NANG
    public void powerUp(double power) {
        this.power += power;
        TaskService.gI().checkDoneTaskPower(player, this.power);
    }

    public void tiemNangUp(double tiemNang) {
        this.tiemNang += tiemNang;
    }

    public void increasePoint(byte type, short point) {
        if (point <= 0 || point > 100) {
            return;
        }
        double tiemNangUse = 0;
        if (type == 0) {
            int pointHp = point * 20;
            tiemNangUse = point * (2 * (this.hpg + 1000) + pointHp - 20) / 2;
            if ((this.hpg + pointHp) <= getHpMpLimit(player.isDaoLu)) {
                if (doUseTiemNang(tiemNangUse)) {
                    hpg += pointHp;
                }
            } else {
                clearAutoCs();
            }
        }
        if (type == 1) {
            int pointMp = point * 20;
            tiemNangUse = point * (2 * (this.mpg + 1000) + pointMp - 20) / 2;
            if ((this.mpg + pointMp) <= getHpMpLimit(player.isDaoLu)) {
                if (doUseTiemNang(tiemNangUse)) {
                    mpg += pointMp;
                }
            } else {
                clearAutoCs();
            }
        }
        if (type == 2) {
            tiemNangUse = point * (2 * this.dameg + point - 1) / 2f * 100;
            if ((this.dameg + point) <= getDameLimit(player.isDaoLu)) {
                if (doUseTiemNang(tiemNangUse)) {
                    dameg += point;
                }
            } else {
                clearAutoCs();
            }
        }
        if (type == 3) {
            tiemNangUse = point * 2 * (this.defg + 5) / 2f * 100000;
            if ((this.defg + point) <= getDefLimit(player.isDaoLu)) {
                if (doUseTiemNang(tiemNangUse)) {
                    defg += point;
                }
            } else {
                clearAutoCs();
                return;
            }
        }
        if (type == 4) {
            tiemNangUse = 50000000L;
            for (int i = 0; i < this.critg; i++) {
                tiemNangUse *= 5L;
            }
            if ((this.critg + point) <= getCritLimit()) {
                if (doUseTiemNang(tiemNangUse)) {
                    critg += point;
                }
            } else {
                Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
                return;
            }
        }
        if (type == 5) {
            if (player.nPoint.limitPower > NPoint.MAX_LIMIT) {
                player.nPoint.limitPower = NPoint.MAX_LIMIT;
            }
            Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
            return;
        }
        Service.getInstance().point(player);
    }

    private void clearAutoCs() {
        if (player.autocso) {
            Service.gI().sendThongBao(player, "|7|Đã dừng Auto cộng chỉ số nhanh");
            player.autocso = false;
            player.autoHP = false;
            player.autoKI = false;
            player.autoSD = false;
            player.autoGiap = false;
        }
        Service.getInstance().sendThongBaoOK(player, "Vui lòng mở giới hạn sức mạnh");
    }

    private boolean doUseTiemNang(double tiemNang) {
        if (this.tiemNang < tiemNang) {
            if (player.autocso) {
                Service.gI().sendThongBao(player, "|7|Đã dừng Auto cộng chỉ số nhanh");
                player.autocso = false;
                player.autoHP = false;
                player.autoKI = false;
                player.autoSD = false;
                player.autoGiap = false;
            }
            Service.getInstance().sendThongBaoOK(player, "Bạn không đủ tiềm năng");
            return false;
        }
        if (this.tiemNang >= tiemNang && this.tiemNang - tiemNang >= 0) {
            this.tiemNang -= tiemNang;
            TaskService.gI().checkDoneTaskUseTiemNang(player);
            return true;
        }
        return false;
    }

    public double calPercent(double param, int percent) {
        return param * percent / 100f;
    }

    //--------------------------------------------------------------------------
    private long lastTimeHoiPhuc;
    private long lastTimeHoiStamina;

    public void update() {
        //
        if (Util.canDoWithTime(lastTimeNumAttackLinhCan, 5000)) {
            numAttackLinhCan = 0;
        }
        if (player.isPl() && player.tuMa.isTuMa() && player.tuMa.linhCanTuMa.typeLinhCan == 0 && Util.canDoWithTime(lastTimeHutMau, 10_000)) {
            // reset mau ve binh thuong ne
            this.hpMax = mauGoc;
        }
        if (player != null && player.effectSkill != null) {
            if (player.effectSkill.isCharging && player.effectSkill.countCharging < 10) {
                long tiLeHoiPhuc = SkillUtil.getPercentCharge(player.playerSkill.skillSelect.point);
                if (player.effectSkill.isCharging && !player.isDie() && !player.effectSkill.isHaveEffectSkill() && (hp < hpMax || mp < mpMax)) {
                    PlayerService.gI().hoiPhuc(player, hpMax / 100 * tiLeHoiPhuc, mpMax / 100 * tiLeHoiPhuc);
                    if (player.effectSkill.countCharging % 3 == 0) {
                        Service.getInstance().chat(player, "Phục hồi năng lượng " + getCurrPercentHP() + "%");
                    }
                } else {
                    EffectSkillService.gI().stopCharge(player);
                }
                if (++player.effectSkill.countCharging >= 10) {
                    EffectSkillService.gI().stopCharge(player);
                }
            }
            if (Util.canDoWithTime(lastTimeHoiPhuc, 30000) && !player.isBoss) {
                PlayerService.gI().hoiPhuc(this.player, hpHoi, mpHoi);
                this.lastTimeHoiPhuc = System.currentTimeMillis();
            }
            if (Util.canDoWithTime(lastTimeHoiStamina, 60000) && this.stamina < this.maxStamina) {
                stamina++;
                this.lastTimeHoiStamina = System.currentTimeMillis();
                if (!this.player.isBoss && !this.player.isPet && !this.player.isDaoLu && !this.player.isTrieuhoipet) {
                    PlayerService.gI().sendCurrentStamina(this.player);
                }
            }
        }
    }

    public void dispose() {
        this.intrinsic = null;
        this.player = null;
        this.tlHp = null;
        this.tlMp = null;
        this.tlDef = null;
        this.tlDame = null;
        this.tlDameAttMob = null;
        this.tlSDDep = null;
        this.tlTNSM = null;
    }

    public double getHP() {
        return Math.min(this.hp, this.hpMax);
    }

    public double getMP() {
        return Math.min(this.mp, this.mpMax);
    }
}
