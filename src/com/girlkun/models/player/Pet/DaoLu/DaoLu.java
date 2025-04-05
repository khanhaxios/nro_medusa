package com.girlkun.models.player.Pet.DaoLu;

import com.girlkun.models.player.*;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.services.MapService;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Pet.ConstPet;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.services.PlayerService;
import com.girlkun.services.SkillService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.RadaService;

public class DaoLu extends Player {

    private static final short[][] DAO_LU_STYLE = {{285, 286, 287}, {288, 289, 290}, {282, 283, 284}, {304, 305, 303}};

    public static final byte FOLLOW = 0;
    public static final byte ATTACK = 2;
    public static final byte GOHOME = 3;
    private static final String[] chatPr = {"T vả chet di~ me m", "Dám đụng vào phu quân t này", "Chụy cho m thăng thiên"};
    public String nameDaoLu;

    private static final short ARANGE_CAN_ATTACK = 300;
    private static final short ARANGE_ATT_SKILL1 = 100;

    public Player master;
    public byte status = 0;
    private int numAttackMob = 0;
    private int NUM_TO_TUVI = 0;

    public byte typeDaoLu;
    public boolean isTransform;

    public long lastTimeDie;

    private boolean goingHome;
    private Mob mobAttack;

    //Mở rộng dự phòng
    public int pointTuVi;
    public int pointCapTinh;
    public int pointCapCanhGioi;
    public long timeThangCap;
    public int var2;

    public boolean isThangDauDe = false;
    public boolean isMacDo = false;

    public static int POWER_DAU_THANH = 50000;

    public byte getStatus() {
        return this.status;
    }

    public DaoLu(Player master) {
        this.master = master;
        this.isDaoLu = true;
    }

    public void changeStatus(byte status) {
        if (status == 1 || status == 4) {
            Service.getInstance().sendThongBao(master, "Đạo lữ không có trạng thái này");
            return;
        }
        if (goingHome) { // || (this.isDie() && status == FUSION) || master.fusion.typeFusion != 0
            Service.getInstance().sendThongBao(master, "Không thể thực hiện");
            return;
        }
        Service.getInstance().chatJustForMe(master, this, getTextStatus(status));
        if (status == GOHOME) {
            goHome();
        }
//        else if (status == FUSION) {
//            fusion(false);
//        }
        this.status = status;
    }

    public void joinMapMaster() {
        if (status != GOHOME && !isDie()) { // && status != FUSION  
            this.location.x = master.location.x + Util.nextInt(-10, 10);
            this.location.y = master.location.y;
            ChangeMapService.gI().goToMap(this, master.zone);
            this.zone.load_Me_To_Another(this);
        }
    }

    public void goHome() {
        if (this.status == GOHOME) {
            return;
        }
        goingHome = true;
        new Thread(() -> {
            try {
//                DaoLu.this.status = DaoLu.ATTACK;
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            ChangeMapService.gI().goToMap(this, MapService.gI().getMapCanJoin(this, master.gender + 21, -1));
            this.zone.load_Me_To_Another(this);
            DaoLu.this.status = DaoLu.GOHOME;
            goingHome = false;
        }).start();
    }

    private String getTextStatus(byte status) {
        return switch (status) {
            case FOLLOW ->
                "Phu quân, thiếp ở đây...";
//            case PROTECT -> "Á..À!!Dám Đánh Phu Quân Tao À, Mày no mày rồi";
            case ATTACK ->
                "Ok phu quân, thiếp sẽ chăm chỉ luyện tập";
            case GOHOME ->
                "Tạm biệt chàng, cần gì cứ gọi, ta sẽ đến ngay";
            default ->
                "";
        };
    }

    public long lastTimeMoveIdle;
    private int timeMoveIdle;
    public boolean idle;

    private void moveIdle() {
        if (status == GOHOME) { // || status == FUSION
            return;
        }
        if (idle && Util.canDoWithTime(lastTimeMoveIdle, timeMoveIdle)) {
            int dir = this.location.x - master.location.x <= 0 ? -1 : 1;
            PlayerService.gI().playerMove(this, master.location.x
                    + Util.nextInt(dir == -1 ? 30 : -50, dir == -1 ? 50 : 30), master.location.y);
            lastTimeMoveIdle = System.currentTimeMillis();
            timeMoveIdle = Util.nextInt(5000, 8000);
        }
    }

    private long lastTimeMoveAtHome;
    private byte directAtHome = -1;

    @Override
    public void update() {
        try {
            super.update();
            increasePoint(); //cộng chỉ số
            if (isDie()) {
                if (System.currentTimeMillis() - lastTimeDie > 5000) {
                    Service.getInstance().hsChar(this, nPoint.hpMax, nPoint.mpMax);
                } else {
                    return;
                }
            }

            if (justRevived2 && this.zone == master.zone) {
                Service.getInstance().chatJustForMe(master, this, "Phu quân, ta đã trở lại!");
                justRevived2 = false;
            }

            if (this.zone == null || this.zone != master.zone) {
                joinMapMaster();
            }
            if (master.isDie() || this.isDie() || effectSkill.isHaveEffectSkill()) {
                return;
            }

            moveIdle();
            switch (status) {
                case FOLLOW:
                    followMaster(30);
                    break;
//                case PROTECT:
//                    if (playerAttack != null && playerAttack.zone.equals(this.zone) && !playerAttack.isDie()) {
//                        if (Util.isTrue(1, 10)) {
//                            Service.gI().chat(this, chatPr[Util.nextInt(chatPr.length)]);
//                        }
//                        int disToMob = Util.getDistance(this, playerAttack);
//                        if (disToMob <= ARANGE_ATT_SKILL1) {
//                            //đấm
//                            this.playerSkill.skillSelect = getSkill(1);
//                            if (SkillService.gI().canUseSkillWithCooldown(this)) {
//                                if (SkillService.gI().canUseSkillWithMana(this)) {
//                                    PlayerService.gI().playerMove(this, playerAttack.location.x + Util.nextInt(-20, 20), playerAttack.location.y);
//                                    SkillService.gI().useSkill(this, playerAttack, null, null);
//                                } else {
//                                    askPea();
//                                }
//                            }
//                        } else {
//                            //chưởng
//                            this.playerSkill.skillSelect = getSkill(2);
//                            if (this.playerSkill.skillSelect.skillId != -1) {
//                                if (SkillService.gI().canUseSkillWithCooldown(this)) {
//                                    if (SkillService.gI().canUseSkillWithMana(this)) {
//                                        SkillService.gI().useSkill(this, playerAttack, null, null);
//                                    } else {
//                                        askPea();
//                                    }
//                                }
//                            }
//                        }
//                        return;
//                    } else {
//                        playerAttack = null;
//                    }
//                    if (useSkill3() || useSkill4()) {
//                        break;
//                    }
//                    mobAttack = findMobAttack();
//                    if (mobAttack != null) {
//                        int disToMob = Util.getDistance(this, mobAttack);
//                        if (disToMob <= ARANGE_ATT_SKILL1) {
//                            //đấm
//                            this.playerSkill.skillSelect = getSkill(1);
//                            if (SkillService.gI().canUseSkillWithCooldown(this)) {
//                                if (SkillService.gI().canUseSkillWithMana(this)) {
//                                    PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
//                                    SkillService.gI().useSkill(this, playerAttack, mobAttack, null);
//                                } else {
//                                    askPea();
//                                }
//                            }
//                        } else {
//                            //chưởng
//                            this.playerSkill.skillSelect = getSkill(2);
//                            if (this.playerSkill.skillSelect.skillId != -1) {
//                                if (SkillService.gI().canUseSkillWithCooldown(this)) {
//                                    if (SkillService.gI().canUseSkillWithMana(this)) {
//                                        SkillService.gI().useSkill(this, playerAttack, mobAttack, null);
//                                    } else {
//                                        askPea();
//                                    }
//                                }
//                            }
//                        }
//
//                    } else {
//                        idle = true;
//                    }
//
//                    break;
                case ATTACK:
                    mobAttack = findMobAttack();
                    if (mobAttack != null) {
                        if (Util.isTrue(1, 10)) {
                            Service.gI().chat(this, chatPr[Util.nextInt(chatPr.length)]);
                        }
                        int disToMob = Util.getDistance(this, mobAttack);
                        if (disToMob <= ARANGE_ATT_SKILL1) {
                            this.playerSkill.skillSelect = getSkill(1);
                            if (SkillService.gI().canUseSkillWithCooldown(this)) {
                                if (SkillService.gI().canUseSkillWithMana(this)) {
                                    PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                                    SkillService.gI().useSkill(this, null, mobAttack, null);
                                } else {
                                    askPea();
                                }
                            }
                        } else {
                            this.playerSkill.skillSelect = getSkill(2);
                            if (this.playerSkill.skillSelect.skillId != -1) {
                                if (SkillService.gI().canUseSkillWithMana(this)) {
                                    SkillService.gI().useSkill(this, null, mobAttack, null);
                                }
                            } else {
                                this.playerSkill.skillSelect = getSkill(1);
                                if (SkillService.gI().canUseSkillWithCooldown(this)) {
                                    if (SkillService.gI().canUseSkillWithMana(this)) {
                                        PlayerService.gI().playerMove(this, mobAttack.location.x + Util.nextInt(-20, 20), mobAttack.location.y);
                                        SkillService.gI().useSkill(this, null, mobAttack, null);
                                    } else {
                                        askPea();
                                    }
                                }
                            }
                        }

                    } else {
                        idle = true;
                    }
                    break;
                case GOHOME:
                    if (this.zone != null && (this.zone.map.mapId == 21 || this.zone.map.mapId == 22 || this.zone.map.mapId == 23)) {
                        if (System.currentTimeMillis() - lastTimeMoveAtHome <= 5000) {
                        } else {
                            switch (this.zone.map.mapId) {
                                case 21 -> {
                                    if (directAtHome == -1) {

                                        PlayerService.gI().playerMove(this, 250, 336);
                                        directAtHome = 1;
                                    } else {
                                        PlayerService.gI().playerMove(this, 200, 336);
                                        directAtHome = -1;
                                    }
                                }
                                case 22 -> {
                                    if (directAtHome == -1) {
                                        PlayerService.gI().playerMove(this, 500, 336);
                                        directAtHome = 1;
                                    } else {
                                        PlayerService.gI().playerMove(this, 452, 336);
                                        directAtHome = -1;
                                    }
                                }
                                case 23 -> {
                                    if (directAtHome == -1) {
                                        PlayerService.gI().playerMove(this, 250, 336);
                                        directAtHome = 1;
                                    } else {
                                        PlayerService.gI().playerMove(this, 200, 336);
                                        directAtHome = -1;
                                    }
                                }
                                default -> {
                                }
                            }
                            Service.getInstance().chatJustForMe(master, this, "Cơm đã nấu xong, nhà đã quét sạch, ta còn quên gì nữa không...!");
                            lastTimeMoveAtHome = System.currentTimeMillis();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
//            Logger.logException(DaoLu.class, e);
        }
    }

    private long lastTimeAskPea;

    public void askPea() {
        if (Util.canDoWithTime(lastTimeAskPea, 10000)) {
            Service.getInstance().chatJustForMe(master, this, "Chàng có thể cho ta 1 hạt đậu thần không..?");
            lastTimeAskPea = System.currentTimeMillis();
        }
    }

    private Mob findMobAttack() {
        int dis = ARANGE_CAN_ATTACK;
        Mob mobAtt = null;
        for (Mob mob : zone.mobs) {
            if (mob.isDie()) {
                continue;
            }
            int d = Util.getDistance(this, mob);
            if (d <= dis) {
                dis = d;
                mobAtt = mob;
            }
        }
        return mobAtt;
    }

    private long lastTimeIncreasePoint;

    private void increasePoint() {
        if (this.nPoint != null && Util.canDoWithTime(lastTimeIncreasePoint, 1)) {
            if (Util.isTrue(5, 100)) {
                this.nPoint.increasePoint((byte) 2, (short) 1);
            } else {
                byte index = (byte) Util.nextInt(0, 2);
                short point = (short) Util.nextInt(1, 101);
                this.nPoint.increasePoint(index, point);
            }
            lastTimeIncreasePoint = System.currentTimeMillis();
        }
    }

    public void followMaster() {
        if (this.isDie() || effectSkill.isHaveEffectSkill()) {
            return;
        }
        switch (this.status) {
//            case ATTACK:
//                if ((mobAttack != null && Util.getDistance(this, master) <= 1500)) {
//                    break;
//                }
            case FOLLOW:
//            case PROTECT:
                followMaster(40);
                break;
        }
    }

    private void followMaster(int dis) {
        int mX = master.location.x;
        int mY = master.location.y;
        int disX = this.location.x - mX;
        if (Math.sqrt(Math.pow(mX - this.location.x, 2) + Math.pow(mY - this.location.y, 2)) >= dis) {
            if (disX < 0) {
                this.location.x = mX - Util.nextInt(0, dis);
            } else {
                this.location.x = mX + Util.nextInt(0, dis);
            }
            this.location.y = mY;
            PlayerService.gI().playerMove(this, this.location.x, this.location.y);
        }
    }

    public void addPointTuViDaoLu() {
        numAttackMob += 1;
        NUM_TO_TUVI = pointCapTinh * (pointCapCanhGioi) + 1;
        if (numAttackMob > NUM_TO_TUVI) {
            numAttackMob -= NUM_TO_TUVI;
            this.pointTuVi += 1;
            if (this.pointTuVi > ConstPet.MAX_TU_VI_DAO_LU) {
                this.pointTuVi = ConstPet.MAX_TU_VI_DAO_LU;
            }
        }
    }

    @Override
    public byte getAura() {
        if (pointCapCanhGioi == ConstPet.MAX_CAP_BAC_DAO_LU) {
            return 22;
        } else {
            return master.getAura();
        }
    }

    public short getAvatar() {
        return switch (this.typeDaoLu) {
            case ConstPet.DAO_LU_TYPE_1 ->
                409;
            case ConstPet.DAO_LU_TYPE_2 ->
                1330;
            case ConstPet.DAO_LU_TYPE_3 ->
                1475;
            default ->
                DAO_LU_STYLE[3][this.gender];
        };
    }

    @Override
    public short getHead() {
        if (effectSkill.isMonkey) {
            return (short) ConstPlayer.HEADMONKEY[effectSkill.levelMonkey - 1];
        } else if (effectSkill.isSocola) {
            return 412;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_1) {
            return 409;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_2) {
            return 1330;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_3) {
            return 1475;
        } else if (inventory.itemsBody.get(5).isNotNullItem()) {
            int part = inventory.itemsBody.get(5).template.head;
            if (part != -1) {
                return (short) part;
            }
        }
        if (this.nPoint.power < 1500000) {
            return DAO_LU_STYLE[this.gender][0];
        } else {
            return DAO_LU_STYLE[3][this.gender];
        }
    }

    @Override
    public short getBody() {
        if (effectSkill.isMonkey) {
            return 193;
        } else if (effectSkill.isSocola) {
            return 413;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_1 && !this.isTransform) {
            return 410;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_2 && !this.isTransform) {
            return 1331;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_3 && !this.isTransform) {
            return 1476;
        } else if (inventory.itemsBody.get(5).isNotNullItem()) {
            int body = inventory.itemsBody.get(5).template.body;
            if (body != -1) {
                return (short) body;
            }
        }
        if (inventory.itemsBody.get(0).isNotNullItem()) {
            return inventory.itemsBody.get(0).template.part;
        }
        if (this.nPoint.power < 1500000) {
            return DAO_LU_STYLE[this.gender][1];
        } else {
            return (short) (gender == ConstPlayer.NAMEC ? 59 : 57);
        }
    }

    @Override
    public short getLeg() {
        if (effectSkill.isMonkey) {
            return 194;
        } else if (effectSkill.isSocola) {
            return 414;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_1 && !this.isTransform) {
            return 411;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_2 && !this.isTransform) {
            return 1332;
        } else if (this.typeDaoLu == ConstPet.DAO_LU_TYPE_3 && !this.isTransform) {
            return 1477;
        } else if (inventory.itemsBody.get(5).isNotNullItem()) {
            int leg = inventory.itemsBody.get(5).template.leg;
            if (leg != -1) {
                return (short) leg;
            }
        }
        if (inventory.itemsBody.get(1).isNotNullItem()) {
            return inventory.itemsBody.get(1).template.part;
        }

        if (this.nPoint.power < 1500000) {
            return DAO_LU_STYLE[this.gender][2];
        } else {
            return (short) (gender == ConstPlayer.NAMEC ? 60 : 58);
        }
    }

    public static String getTextTypeDaoLu(byte type) {
        switch (type) {
            case 3:
                return "|2|Nhân Phẩm Ngút Trời!!! Bạn Đã Chiêu Mộ Được Đạo Lữ "
                        + "\n|7|Tam Phẩm";
            case 2:
                return "|8|Nhân Phẩm Hơn Người!!! Bạn Đã Chiêu Mộ Được Đạo Lữ "
                        + "\n|5|Nhị Phẩm";
            case 1:
                return "Bạn Đã Chiêu Mộ Được Đạo Lữ\n|0|Nhất Phẩm";
            default:
                return "Vô Định";
        }
    }

    public String getTypeString() {
        return switch (this.typeDaoLu) {
            case 3 ->
                "Tam Phẩm";
            case 2 ->
                "Nhị Phẩm";
            case 1 ->
                "Nhất Phẩm";
            default ->
                "Vô Phẩm";
        };
    }

    public static String sGetTypeString(int type) {
        return switch (type) {
            case 3 ->
                "Tam Phẩm";
            case 2 ->
                "Nhị Phẩm";
            case 1 ->
                "Nhất Phẩm";
            default ->
                "Vô Phẩm";
        };
    }

    public static String sGetCapBacCapTinh(int CapBac, int CapTinh) {
        String intBac = CapBac + "-" + CapTinh;
        return switch (intBac) {
            case "9-10" ->
                "Đấu Thánh - Đỉnh Phong";
            case "8-11" ->
                "Đấu Tôn - Nữa Bước Đấu Thánh";
            case "8-10" ->
                "Đấu Tôn - Đỉnh Phong";
            case "7-10" ->
                "Đấu Tông - Nữa Bước Đấu Tôn";
            case "6-10" ->
                "Đấu Hoàng - Nữa Bước Đấu Tông";
            case "5-10" ->
                "Đấu Vương - Nữa Bước Đấu Hoàng";
            case "4-10" ->
                "Đấu Linh - Nữa Bước Đấu Vương";
            case "3-10" ->
                "Đại Đấu Sư - Nữa Bước Đấu Linh";
            default ->
                getCapBac(CapBac) + " - " + getCapTinhDaoLu(CapBac, CapTinh);
        };
    }

    public String getCapBacCapTinh() {
        return sGetCapBacCapTinh(pointCapCanhGioi, pointCapTinh);
    }

    public static String getCapBac(int capBac) {
        return switch (capBac) {
            case 10 ->
                "Đấu Đế";
            case 9 ->
                "Đấu Thánh";
            case 8 ->
                "Đấu Tôn";
            case 7 ->
                "Đấu Tông";
            case 6 ->
                "Đấu Hoàng";
            case 5 ->
                "Đấu Vương";
            case 4 ->
                "Đấu Linh";
            case 3 ->
                "Đại Đấu Sư";
            case 2 ->
                "Đấu Sư";
            case 1 ->
                "Đấu Giả";
            case 0 ->
                "Đấu Khí";
            default ->
                "Vô Danh";
        };
    }

    public static String getCapTinhDaoLu(int capBac, int capTinh) {
        switch (capBac) {
            case 10:
                return "Chí Tôn";
            case 9:
            case 8:
            case 7:
            case 6:
            case 5:
            case 4:
            case 3:
            case 2:
            case 1:
                switch (capTinh) {
                    case 9 -> {
                        return "Cửu Tinh Đỉnh Phong";
                    }
                    case 8 -> {
                        return "Cửu Tinh";
                    }
                    case 7 -> {
                        return "Bát Tinh";
                    }
                    case 6 -> {
                        return "Thất Tinh";
                    }
                    case 5 -> {
                        return "Lục Tinh";
                    }
                    case 4 -> {
                        return "Ngũ Tinh";
                    }
                    case 3 -> {
                        return "Tứ Tinh";
                    }
                    case 2 -> {
                        return "Tam Tinh";
                    }
                    case 1 -> {
                        return "Nhị Tinh";
                    }
                    case 0 -> {
                        return "Nhất Tinh";
                    }
                }
            case 0:
                switch (capTinh) {
                    case 9 -> {
                        return "Cửu Đoạn Đỉnh Phong";
                    }
                    case 8 -> {
                        return "Cửu Đoạn";
                    }
                    case 7 -> {
                        return "Bát Đoạn";
                    }
                    case 6 -> {
                        return "Thất Đoạn";
                    }
                    case 5 -> {
                        return "Lục Đoạn";
                    }
                    case 4 -> {
                        return "Ngũ Đoạn";
                    }
                    case 3 -> {
                        return "Tứ Đoạn";
                    }
                    case 2 -> {
                        return "Tam Đoạn";
                    }
                    case 1 -> {
                        return "Nhị Đoạn";
                    }
                    case 0 -> {
                        return "Nhất Đoạn";
                    }
                }
            default:
                return "Phế Vật";
        }
    }

    public static int getMaxTinh(int capBac) {
        return switch (capBac) {
            case 10 ->
                0;
            case 8 ->
                11;
            case 9, 7, 6, 5, 4, 3 ->
                10;
            case 2, 1, 0 ->
                9;
            default ->
                0;
        };
    }

    public int getMaxTinh() {
        return switch (pointCapCanhGioi) {
            case 10 ->
                0;
            case 8 ->
                11;
            case 9, 7, 6, 5, 4, 3 ->
                10;
            case 2, 1, 0 ->
                9;
            default ->
                0;
        };
    }

    public int getTiLeThangTinh() {
        if (this.pointTuVi < 600) {
            return 0;
        } else {
            float bSo = (90 * (this.pointTuVi - 600) / 400);
            int tile = 10 + ((int) bSo) - (this.pointCapTinh * 8);
            return tile > 100 ? 100 : tile;
        }
    }

    public int getTiLeThangCanhGioi() {
        return (ConstPet.MAX_CAP_BAC_DAO_LU - 1 - this.pointCapCanhGioi) * 10;
    }

    public String getTextStatus() {
        return switch (this.status) {
            case GOHOME ->
                "Về Nhà\nVề Chông Nhà, Rửa Chén, Nấu Cơm";
            case ATTACK ->
                "Tu Luyện\nTấn Công Quái Xung Quanh Luyện Sức Mạnh và Tu Vi\n"
                + "Số Đòn Tấn Công Tăng 1 Tu Vi: " + numAttackMob + "/" + NUM_TO_TUVI;
            case FOLLOW ->
                "Đi Theo\nTung Kỹ Năng Và Tấn Công Mục Tiêu Cùng Chủ Công";
            default ->
                "";
        };
    }

    public int getNPointAddByType() {
        switch (this.typeDaoLu) {
            case 3 -> {
                return 100000;
            }
            case 2 -> {
                return 10000;
            }
            case 1 -> {
                return 1000;
            }
        }
        return 0;
    }

    public int getNPointAddCapBac() {
        switch (this.pointCapCanhGioi) {
            case 10 -> {
                return 100000;
            }
            case 9 -> {
                return 50000;
            }
            case 8 -> {
                return 30000;
            }
            case 7 -> {
                return 20000;
            }
            case 6 -> {
                return 10000;
            }
            case 5 -> {
                return 5000;
            }
            case 4 -> {
                return 4000;
            }
            case 3 -> {
                return 3000;
            }
            case 2 -> {
                return 2000;
            }
            case 1 -> {
                return 1000;
            }
            case 0 -> {
                return 500;
            }
        }
        return 0;
    }

    public int getNPointAddCapTinh() {
        if (this.pointCapCanhGioi == 10) {
            return 900 + (this.pointCapTinh + 1) * 100;
        }
        return switch (this.pointCapTinh) {
            default ->
                (this.pointCapTinh + 1) * 100;
        };
    }

//    private Mob findMobAttack() {
//        int dis = ARANGE_CAN_ATTACK;
//        Mob mobAtt = null;
//        for (Mob mob : zone.mobs) {
//            if (mob.isDie()) {
//                continue;
//            }
//            int d = Util.getDistance(this, mob);
//            if (d <= dis) {
//                dis = d;
//                mobAtt = mob;
//            }
//        }
//        return mobAtt;
//    }
//    private void openSkill5() {
//        Skill skill = null;
//        int tiLeThoiMien = 10; //khi
//        int tiLeSoCoLa = 70; //detrung
//        int tiLeDCTT = 20; //khienNl
//
//        int rd = Util.nextInt(1, 100);
//        if (rd <= tiLeThoiMien) {
//            skill = SkillUtil.createSkill(Skill.SOCOLA, 1);
//        } else if (rd <= tiLeThoiMien + tiLeSoCoLa) {
//            skill = SkillUtil.createSkill(Skill.QUA_CAU_KENH_KHI, 1);
//        } else if (rd <= tiLeThoiMien + tiLeSoCoLa + tiLeDCTT) {
//            skill = SkillUtil.createSkill(Skill.DICH_CHUYEN_TUC_THOI, 1);
//        }
//        this.playerSkill.skills.set(4, skill);
//    }
//    private Skill getSkill(int i) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//    }
//    ========================================================
    private Skill getSkill(int indexSkill) {
        return this.playerSkill.skills.get(indexSkill - 1);
    }

    public void transform() {
        switch (this.typeDaoLu) {
            case ConstPet.DAO_LU_TYPE_1 -> {
                this.isTransform = !this.isTransform;
                Service.getInstance().Send_Caitrang(this);
                Service.getInstance().chat(this, "Đạo Lũ Type 1 Xuất Trận");
            }
            case ConstPet.DAO_LU_TYPE_2 -> {
                this.isTransform = !this.isTransform;
                Service.getInstance().Send_Caitrang(this);
                Service.getInstance().chat(this, "Đạo Lũ Type 2 Xuất Trận");
            }
            case ConstPet.DAO_LU_TYPE_3 -> {
                this.isTransform = !this.isTransform;
                Service.getInstance().Send_Caitrang(this);
                Service.getInstance().chat(this, "Đạo Lũ Type 3 Xuất Trận");
            }
            default -> {
            }
        }
    }

    public static void effDauDeXuatHien(Player player) {
        new Thread(() -> {
            if (!Manager.haveEffectNightSky) {
                Manager.haveEffectNightSky = true;
                try {
                    Thread.sleep(10000);
                    Service.gI().nightSky(player);
                    EffectSkillService.gI().sendEffectBienhinh(player.petDaoLu);
                    RadaService.gI().setIDAuraEff(player.petDaoLu, (byte) 22);
                    String[] str = {"Đấu đế, là đấu đế!!",
                        "Mauuu, mau quỳ xuống!"};
                    for (String s : str) {
                        Player plMap = player.zone.getRandomPlayerInMap();
                        if (plMap != null && !plMap.isDie()) { // && Util.getDistance(player, plMap) <= 600
                            Service.getInstance().chat(plMap, s);
                        }
                    }
                    Thread.sleep(5000);
                    Service.gI().lightSky();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Service.gI().lightSky();
                }
                Manager.haveEffectNightSky = false;
            }
        }, "Đấu Đế Xuất Hiện tại " + player.name).start();
    }

    @Override
    public void dispose() {
        if (zone != null) {
            ChangeMapService.gI().exitMap(this);
        }
        this.master = null;
        super.dispose();
    }
}
