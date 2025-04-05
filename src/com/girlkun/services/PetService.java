package com.girlkun.services;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.player.NewPet;
import com.girlkun.models.player.Pet.Pet;
import com.girlkun.models.player.Pet.ConstPet;
import com.girlkun.models.player.Pet.DaoLu.DaoLu;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.Thu_TrieuHoi;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.SkillUtil;
import com.girlkun.models.skill.Skill;
import com.girlkun.utils.Util;

public class PetService {

    private static PetService i;

    public static PetService gI() {
        if (i == null) {
            i = new PetService();
        }
        return i;
    }
//=====================Create Dao Lu V2 by NDQ (Zalo - 0372475179)========================

    public void createDaoLu(Player player, String name, byte typeDaoLu, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewDaoLu(player, name, typeDaoLu, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.petDaoLu.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                Service.getInstance().chatJustForMe(player, player.pet, "Ta nguyện đi theo chàng!!");
            } catch (Exception e) {
            }
        }).start();
    }

    public boolean changeDaoLu(Player player, String name, byte typeDaoLu, int gender) {
        if (InventoryServiceNew.gI().getCountEmptyBody(player.petDaoLu) == ConstPlayer.QTY_MAX_ITEM_BODY_PLAYER) {
            byte limitPower = player.petDaoLu.nPoint.limitPower;
            ChangeMapService.gI().exitMap(player.petDaoLu);
            player.petDaoLu.dispose();
            player.petDaoLu = null;
            createDaoLu(player, name, typeDaoLu, gender, limitPower);
            return true;
        } else {
            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đạo lữ đang mặc");
            return false;
        }
    }

    private void createNewDaoLu(Player player, String name, byte typeDaoLu, byte... gender) {
        int[] data = getDataDaoLus(typeDaoLu);
        DaoLu petDaoLu = new DaoLu(player);
        petDaoLu.typeDaoLu = (byte) typeDaoLu;
        petDaoLu.nameDaoLu = name;
        petDaoLu.name = "$[" + petDaoLu.getTypeString() + "] " + petDaoLu.nameDaoLu;
        //"$" + getNameDaoLus(typeDaoLu);
        petDaoLu.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        petDaoLu.id = Player.setIdForPet(petDaoLu, player.id);
        petDaoLu.nPoint.power = typeDaoLu != 1 ? 1500000 : 2000;
        petDaoLu.nPoint.tiemNang = typeDaoLu != 1 ? 1500000 : 2000;
        petDaoLu.nPoint.stamina = 1000;
        petDaoLu.nPoint.maxStamina = 1000;
        petDaoLu.nPoint.hpg = data[0];
        petDaoLu.nPoint.mpg = data[1];
        petDaoLu.nPoint.dameg = data[2];
        petDaoLu.nPoint.defg = data[3];
        petDaoLu.nPoint.critg = data[4];
        for (int i = 0; i < ConstPlayer.QTY_MAX_ITEM_BODY_PLAYER; i++) {
            petDaoLu.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        int[] skillsArr = player.gender == 0 ? ConstPlayer.SKILL_TD
                : petDaoLu.gender == 1 ? ConstPlayer.SKILL_NAMEC
                        : ConstPlayer.SKILL_XAYDA;
        for (int idSkill : skillsArr) {
            Skill skill = SkillUtil.createSkill(idSkill, 1);
            petDaoLu.playerSkill.skills.add(skill);
        }
        petDaoLu.nPoint.setFullHpMp();
        player.petDaoLu = petDaoLu;
    }

    private int[] getDataDaoLus(Byte typePet) {
        switch (typePet) {
            case ConstPet.DAO_LU_TYPE_1:
                return getDataDaoLuT1();
            case ConstPet.DAO_LU_TYPE_2:
                return getDataDaoLuT2();
            case ConstPet.DAO_LU_TYPE_3:
                return getDataDaoLuT3();
            default:
                return getDataDaoLuT1();
        }
    }

    private String getNameDaoLus(Byte typePet) {
        switch (typePet) {
            case ConstPet.DAO_LU_TYPE_1:
                return "Đạo Lữ - Hạng 1";
            case ConstPet.DAO_LU_TYPE_2:
                return "Đạo Lữ - Hạng 2";
            case ConstPet.DAO_LU_TYPE_3:
                return "Đạo Lữ - Hạng 3";
            default:
                return "Đạo Lữ";
        }
    }
    //=====================Data Pet Dao Lu V2 by NDQ (Zalo - 0372475179) ========================

    private int[] getDataDaoLuT1() {
        long[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] daoLuData = new int[5];
        daoLuData[0] = Util.nextInt(40, 105) * 20; //hp
        daoLuData[1] = Util.nextInt(40, 105) * 20; //mp
        daoLuData[2] = Util.nextInt(20, 45); //dame
        daoLuData[3] = Util.nextInt(9, 50); //def
        daoLuData[4] = Util.nextInt(0, 2); //crit
        return daoLuData;
    }

    private int[] getDataDaoLuT2() {
        long[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] daoLuData = new int[5];
        daoLuData[0] = Util.nextInt(40, 105) * 20; //hp
        daoLuData[1] = Util.nextInt(40, 105) * 20; //mp
        daoLuData[2] = Util.nextInt(50, 120); //dame
        daoLuData[3] = Util.nextInt(9, 50); //def
        daoLuData[4] = Util.nextInt(0, 2); //crit
        return daoLuData;
    }

    private int[] getDataDaoLuT3() {
        long[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] daoLuData = new int[5];
        daoLuData[0] = Util.nextInt(40, 110) * 20; //hp
        daoLuData[1] = Util.nextInt(40, 110) * 20; //mp
        daoLuData[2] = Util.nextInt(50, 130); //dame
        daoLuData[3] = Util.nextInt(9, 50); //def
        daoLuData[4] = Util.nextInt(0, 2); //crit
        return daoLuData;
    }
    //=====================End Code Create Dao Lu V2 by NDQ (Zalo - 0372475179) ========================

    // ======================== Create Pet ===========================
    public void createPet(Player player, byte typePet, int gender, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, typePet, (byte) gender);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                petTalkWhenCreatePet(player, typePet);
            } catch (Exception e) {
            }
        }).start();
    }

    public void createPet(Player player, byte typePet, byte... limitPower) {
        new Thread(() -> {
            try {
                createNewPet(player, typePet);
                if (limitPower != null && limitPower.length == 1) {
                    player.pet.nPoint.limitPower = limitPower[0];
                }
                Thread.sleep(1000);
                petTalkWhenCreatePet(player, typePet);
            } catch (Exception e) {
            }
        }).start();
    }
    // CreatePetV2 - Code rewritten by ndqitVN (Zalo 0372475179)

    // ======================== Change Pet ===========================
    public void changePet(Player player, byte typePet, int gender) {
        if (InventoryServiceNew.gI().getCountEmptyBody(player.pet) == ConstPlayer.QTY_MAX_ITEM_BODY_PET) {
            byte limitPower = player.pet.nPoint.limitPower;
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                player.pet.unFusion();
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.dispose();
            player.pet = null;
            createPet(player, typePet, gender, limitPower);
        } else {
            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
        }
    }

    public void changePet(Player player, byte typePet) {
        if (player.pet == null) {
            Service.gI().sendThongBao(player, "Không có đệ tử để đổi!");
        } else if (InventoryServiceNew.gI().getCountEmptyBody(player.pet) == ConstPlayer.QTY_MAX_ITEM_BODY_PET) {
            byte limitPower = player.pet.nPoint.limitPower;
            if (player.fusion.typeFusion != ConstPlayer.NON_FUSION) {
                player.pet.unFusion();
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.dispose();
            player.pet = null;
            createPet(player, typePet, limitPower);
        } else {
            Service.getInstance().sendThongBao(player, "Vui lòng tháo hết đồ đệ tử");
        }
    }
    // changePetV2 - Code rewritten by ndqitVN (Zalo 0372475179)    

    private void petTalkWhenCreatePet(Player player, byte typePet) {
        switch (typePet) {
            case ConstPet.MABU:
                Service.getInstance().chatJustForMe(player, player.pet, "Oa oa oa...");
                break;
            case ConstPet.BERUS:
                Service.getInstance().chatJustForMe(player, player.pet, "Thần hủy diệt hiện thân tất cả quỳ xuống...");
                break;
            case ConstPet.MASTER_BROLY:
                Service.getInstance().chatJustForMe(player, player.pet, "Sư Phụ Broly hiện thân tụi mày quỳ xuống...");
                break;
            case ConstPet.ZENO:
                Service.getInstance().chatJustForMe(player, player.pet, "Zeno hiện thân tụi mày quỳ xuống...");
                break;
            case ConstPet.GOKU:
                Service.getInstance().chatJustForMe(player, player.pet, "GOKU hiện thân KAMEKAME ..");
                break;
            case ConstPet.GOGETA:
                Service.getInstance().chatJustForMe(player, player.pet, "NOT VEGITO, I AM GOGETA... ..");
                break;
            case ConstPet.NAKROTH:
                Service.getInstance().chatJustForMe(player, player.pet, "HACK MAP,HÓA THÚ AE Ê ..");
                break;
            case ConstPet.THAN_LONG_TY_TY:
                Service.getInstance().chatJustForMe(player, player.pet, "THẦN LONG TỶ TỶ Giáng Trần");
                break;
            case ConstPet.FU:
                Service.getInstance().chatJustForMe(player, player.pet, "NGỤC TÙ VẠN NĂNG");
                break;
            default:
                Service.getInstance().chatJustForMe(player, player.pet, "Xin hãy thu nhận làm đệ tử");
                break;
        }
    }

    public void changeNamePet(Player player, String name) {
        try {
            if (!InventoryServiceNew.gI().KtraItemBag(player, 400)) {
                Service.getInstance().sendThongBao(player, "Bạn cần thẻ đặt tên đệ tử, mua tại Santa");
                return;
            } else if (Util.haveSpecialCharacter(name)) {
                Service.getInstance().sendThongBao(player, "Tên không được chứa ký tự đặc biệt");
                return;
            } else if (name.length() > 10) {
                Service.getInstance().sendThongBao(player, "Tên quá dài");
                return;
            }
            ChangeMapService.gI().exitMap(player.pet);
            player.pet.name = "$" + name.toLowerCase().trim();
            InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 400), 1);
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    Service.getInstance().chatJustForMe(player, player.pet, "Cảm ơn sư phụ đã đặt cho con tên " + name);
                } catch (Exception e) {
                }
            }).start();
        } catch (Exception ex) {

        }
    }

    private int[] getDataPetNormal() {
        int[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(20, 45); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }

    private int[] getDataPetMabu() {
        int[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 105) * 20; //hp
        petData[1] = Util.nextInt(40, 105) * 20; //mp
        petData[2] = Util.nextInt(50, 120); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }

    private int[] getDataPetBerus() {
        int[] hpmp = {1700, 1800, 1900, 2000, 2100, 2200};
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 110) * 20; //hp
        petData[1] = Util.nextInt(40, 110) * 20; //mp
        petData[2] = Util.nextInt(50, 130); //dame
        petData[3] = Util.nextInt(9, 50); //def
        petData[4] = Util.nextInt(0, 2); //crit
        return petData;
    }

    private int[] getDataPetPic() {
        int[] hpmp = {2000, 2100, 2200, 2300, 2400, 2500};
        int[] petData = new int[5];
        petData[0] = Util.nextInt(40, 125) * 20; //hp
        petData[1] = Util.nextInt(40, 125) * 20; //mp
        petData[2] = Util.nextInt(80, 160); //dame
        petData[3] = Util.nextInt(10, 60); //def
        petData[4] = Util.nextInt(2, 5); //crit
        return petData;
    }

    private int[] getDataPetVIP() { //PET ZENO
        int[] hpmp = {2000, 2100, 2200, 2300, 2400, 2500};//hpmp random
        int[] petData = new int[5];
        petData[0] = Util.nextInt(60, 250) * 20; //hp
        petData[1] = Util.nextInt(60, 250) * 20; //m
        petData[2] = Util.nextInt(50, 100); //dame
        petData[3] = Util.nextInt(50, 180); //def
        petData[4] = Util.nextInt(10, 15); //crit
        return petData;
    }

    private int[] getDataPetGOKU() { //PET GOKU
        int[] hpmp = {5000, 5100, 5200, 5300, 5400, 5500};//hpmp random
        int[] petData = new int[5];
        petData[0] = Util.nextInt(100, 250) * 20; //hp
        petData[1] = Util.nextInt(100, 250) * 20; //mp
        petData[2] = Util.nextInt(100, 150); //dame
        petData[3] = Util.nextInt(100, 180); //def
        petData[4] = Util.nextInt(5, 10); //crit
        return petData;
    }

    private int[] getDataPetGOGETA() { //PET GOGETA
        int[] hpmp = {5000, 5100, 5200, 5300, 5400, 5500};//hpmp random
        int[] petData = new int[5];
        petData[0] = Util.nextInt(100, 250) * 20; //hp
        petData[1] = Util.nextInt(100, 250) * 20; //mp
        petData[2] = Util.nextInt(100, 150); //dame
        petData[3] = Util.nextInt(100, 180); //def
        petData[4] = Util.nextInt(5, 10); //crit
        return petData;
    }

    private int[] getDataPetNA() { //PET GOGETA
        int[] hpmp = {5000, 5100, 5200, 5300, 5400, 5500};//hpmp random
        int[] petData = new int[5];
        petData[0] = Util.nextInt(100, 250) * 20; //hp
        petData[1] = Util.nextInt(100, 250) * 20; //mp
        petData[2] = Util.nextInt(100, 150); //dame
        petData[3] = Util.nextInt(100, 180); //def
        petData[4] = Util.nextInt(5, 10); //crit
        return petData;
    }

    private int[] getDataPetThanLongTyTy() { //PET Thần Long Tỷ Tỷ
        int[] hpmp = {5000, 5100, 5200, 5300, 5400, 5500};//hpmp random
        int[] petData = new int[5];
        petData[0] = Util.nextInt(100, 250) * 20; //hp
        petData[1] = Util.nextInt(100, 250) * 20; //mp
        petData[2] = Util.nextInt(100, 150); //dame
        petData[3] = Util.nextInt(100, 180); //def
        petData[4] = Util.nextInt(5, 10); //crit
        return petData;
    }

    private int[] getDataPetFU() { //PET Thần Long Tỷ Tỷ
        int[] hpmp = {5000, 5100, 5200, 5300, 5400, 5500};//hpmp random
        int[] petData = new int[5];
        petData[0] = Util.nextInt(100, 250) * 20; //hp
        petData[1] = Util.nextInt(100, 250) * 20; //mp
        petData[2] = Util.nextInt(100, 150); //dame
        petData[3] = Util.nextInt(100, 180); //def
        petData[4] = Util.nextInt(5, 10); //crit
        return petData;
    }

    //=========================== Pet - Ver 2 - by ndq ==========================
    private void createNewPet(Player player, byte typePet, byte... gender) {
        int[] data = getDataPets(typePet);
        Pet pet = new Pet(player);
        pet.name = "$" + getNamePets(typePet); //"[" + player.name + "]" + " - " +
        pet.gender = (gender != null && gender.length != 0) ? gender[0] : (byte) Util.nextInt(0, 2);
        pet.id = -player.id;
        pet.nPoint.power = typePet != 0 ? 1500000 : 2000;
        pet.nPoint.tiemNang = typePet != 0 ? 1500000 : 2000;
        pet.typePet = (byte) typePet;
        pet.nPoint.stamina = 1000;
        pet.nPoint.maxStamina = 1000;
        pet.nPoint.hpg = data[0];
        pet.nPoint.mpg = data[1];
        pet.nPoint.dameg = data[2];
        pet.nPoint.defg = data[3];
        pet.nPoint.critg = data[4];
        for (int i = 0; i < 8; i++) {
            pet.inventory.itemsBody.add(ItemService.gI().createItemNull());
        }
        if (pet.typePet >= 7) {
            pet.playerSkill.skills.add(SkillUtil.createSkill(17, 1));
        } else {
            pet.playerSkill.skills.add(SkillUtil.createSkill(Util.nextInt(0, 2) * 2, 1));
        }
        for (int i = 0; i < 4; i++) {
            pet.playerSkill.skills.add(SkillUtil.createEmptySkill());
        }
        pet.nPoint.setFullHpMp();
        player.pet = pet;
    }

    private int[] getDataPets(Byte typePet) {
        switch (typePet) {
            case ConstPet.MABU:
                return getDataPetMabu();
            case ConstPet.BERUS:
                return getDataPetBerus();
            case ConstPet.MASTER_BROLY:
                return getDataPetPic();
            case ConstPet.ZENO:
                return getDataPetVIP();
            case ConstPet.GOKU:
                return getDataPetGOKU();
            case ConstPet.GOGETA:
                return getDataPetGOGETA();
            case ConstPet.NAKROTH:
                return getDataPetNA();
            case ConstPet.THAN_LONG_TY_TY:
                return getDataPetThanLongTyTy();
            case ConstPet.FU:
                return getDataPetFU();
            default:
                return getDataPetNormal();
        }
    }

    private String getNamePets(Byte typePet) {
        switch (typePet) {
            case ConstPet.MABU:
                return "Mabư";
            case ConstPet.BERUS:
                return "Berus";
            case ConstPet.MASTER_BROLY:
                return "Super Broly";
            case ConstPet.ZENO:
                return "VIP Zeno";
            case ConstPet.GOKU:
                return "VIP GOKU SSJ4";
            case ConstPet.GOGETA:
                return "THE GOD GOGETA";
            case ConstPet.NAKROTH:
                return "NAKAROT VỆ THẦN";
            case ConstPet.THAN_LONG_TY_TY:
                return "THẦN LONG TỶ TỶ";
            case ConstPet.FU:
                return "FU Đại Ca";
            default:
                return "Đệ Tử";
        }
    }

    //==================================================================
    public static void Pet2(Player pl, int h, int b, int l, String name) {
        if (pl.newpet != null) {//&&pl.newpet1 != null
            pl.newpet.dispose();
            ChangeMapService.gI().exitMap(pl.newpet);
//            pl.newpet1.dispose();
        }
        pl.newpet = new NewPet(pl, (short) h, (short) b, (short) l);
//        pl.newpet1 = new NewPet(pl, (short) h, (short) b, (short) l);
        pl.newpet.name = "$" + name;
//        pl.newpet1.name = "$";
        pl.newpet.gender = pl.gender;
//          pl.newpet1.gender = pl.gender;
        pl.newpet.nPoint.tiemNang = 1;
//         pl.newpet1.nPoint.tiemNang = 1;
        pl.newpet.nPoint.power = 1;
//        pl.newpet1.nPoint.power = 1;
        pl.newpet.nPoint.limitPower = 1;
//         pl.newpet1.nPoint.limitPower = 1;
        pl.newpet.nPoint.hpg = 500;
//        pl.newpet1.nPoint.hpg = 500;
        pl.newpet.nPoint.mpg = 500;
//        pl.newpet1.nPoint.mpg = 500;
        pl.newpet.nPoint.hp = 500;
        pl.newpet.nPoint.mp = 500;
        pl.newpet.nPoint.dameg = 1;
        pl.newpet.nPoint.defg = 1;
        pl.newpet.nPoint.critg = 1;
        pl.newpet.nPoint.stamina = 1;
        pl.newpet.nPoint.setBasePoint();
        pl.newpet.nPoint.setFullHpMp();
//        pl.newpet1.nPoint.hp = 500000000;
//        pl.newpet1.nPoint.mp = 500000000;
//        pl.newpet1.nPoint.dameg = 1;
//        pl.newpet1.nPoint.defg = 1;
//        pl.newpet1.nPoint.critg = 1;
//        pl.newpet1.nPoint.stamina = 1;
//        pl.newpet1.nPoint.setBasePoint();
//        pl.newpet1.nPoint.setFullHpMp();
    }

    public static void Thu_TrieuHoi(Player pl) {
        if (pl.TrieuHoipet != null) {
            pl.TrieuHoipet.dispose();
        }
        pl.TrieuHoipet = new Thu_TrieuHoi(pl);
        pl.TrieuHoipet.name = "$" + "[" + pl.NameThanthu(pl.TrieuHoiCapBac) + "] " + pl.TenThuTrieuHoi;
        pl.TrieuHoipet.gender = pl.gender;
        pl.TrieuHoipet.nPoint.tiemNang = 1;
        pl.TrieuHoipet.nPoint.power = 1;
        pl.TrieuHoipet.nPoint.limitPower = 1;
        pl.TrieuHoipet.nPoint.hpg = pl.TrieuHoiHP;
        pl.TrieuHoipet.nPoint.mpg = 500000000;
        pl.TrieuHoipet.nPoint.hp = pl.TrieuHoiHP;
        pl.TrieuHoipet.nPoint.mp = 500000000;
        pl.TrieuHoipet.nPoint.dameg = pl.TrieuHoiDame;
        pl.TrieuHoipet.nPoint.defg = 1;
        pl.TrieuHoipet.nPoint.critg = 1;
        pl.TrieuHoipet.nPoint.stamina = 10000;
        pl.TrieuHoipet.nPoint.maxStamina = 10000;
        pl.TrieuHoipet.playerSkill.skills.add(SkillUtil.createSkill(17, 7));
        pl.TrieuHoipet.nPoint.setBasePoint();
        pl.TrieuHoipet.nPoint.setFullHpMp();
    }

    //--------------------------------------------------------------------------
}
