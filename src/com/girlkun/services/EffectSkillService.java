package com.girlkun.services;

import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.server.Manager;
import com.girlkun.services.func.RadaService;
import com.girlkun.utils.SkillUtil;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class EffectSkillService {

    public static final byte TURN_ON_EFFECT = 1;
    public static final byte TURN_OFF_EFFECT = 0;
    public static final byte TURN_OFF_ALL_EFFECT = 2;

    public static final byte HOLD_EFFECT = 32;
    public static final byte SHIELD_EFFECT = 33;
    public static final byte HUYT_SAO_EFFECT = 39;
    public static final byte BLIND_EFFECT = 40;
    public static final byte SLEEP_EFFECT = 41;
    public static final byte STONE_EFFECT = 42;

    private static EffectSkillService i;

    private EffectSkillService() {

    }

    public static EffectSkillService gI() {
        if (i == null) {
            i = new EffectSkillService();
        }
        return i;
    }

    //hiệu ứng player dùng skill
    public void sendEffectUseSkill(Player player, byte skillId) {
        Skill skill = SkillUtil.getSkillbyId(player, skillId);
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(8);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    public void sendEffectPlayer(Player plUseSkill, Player plTarget, byte toggle, byte effect) {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(toggle); //0: hủy hiệu ứng, 1: bắt đầu hiệu ứng
            msg.writer().writeByte(0); //0: vào phần phayer, 1: vào phần mob
            if (toggle == TURN_OFF_ALL_EFFECT) {
                msg.writer().writeInt((int) plTarget.id);
            } else {
                msg.writer().writeByte(effect); //loại hiệu ứng
                msg.writer().writeInt((int) plTarget.id); //id player dính effect
                msg.writer().writeInt((int) plUseSkill.id); //id player dùng skill
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    public void sendEffectMob(Player plUseSkill, Mob mobTarget, byte toggle, byte effect) {
        Message msg;
        try {
            msg = new Message(-124);
            msg.writer().writeByte(toggle); //0: hủy hiệu ứng, 1: bắt đầu hiệu ứng
            msg.writer().writeByte(1); //0: vào phần phayer, 1: vào phần mob
            msg.writer().writeByte(effect); //loại hiệu ứng
            msg.writer().writeByte(mobTarget.id); //id mob dính effect
            msg.writer().writeInt((int) plUseSkill.id); //id player dùng skill
            Service.getInstance().sendMessAllPlayerInMap(mobTarget.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //Trói *********************************************************************
    //dừng sử dụng trói
    public void removeUseTroi(Player player) {
        if (player.effectSkill.mobAnTroi != null) {
            player.effectSkill.mobAnTroi.effectSkill.removeAnTroi();
        }
        if (player.effectSkill.plAnTroi != null) {
            removeAnTroi(player.effectSkill.plAnTroi);
        }
        player.effectSkill.useTroi = false;
        player.effectSkill.mobAnTroi = null;
        player.effectSkill.plAnTroi = null;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, HOLD_EFFECT);
    }

    //hết thời gian bị trói
    public void removeAnTroi(Player player) {
        if (player != null && player.effectSkill != null) {
            player.effectSkill.anTroi = false;
            player.effectSkill.plTroi = null;
            sendEffectPlayer(player, player, TURN_OFF_EFFECT, HOLD_EFFECT);
        }
    }

    public void setAnTroi(Player player, Player plTroi, long lastTimeAnTroi, int timeAnTroi) {
        player.effectSkill.anTroi = true;
//        player.effectSkill.lastTimeAnTroi = lastTimeAnTroi;
//        player.effectSkill.timeAnTroi = timeAnTroi;
        player.effectSkill.plTroi = plTroi;
    }

    public void setUseTroi(Player player, long lastTimeTroi, int timeTroi) {
        player.effectSkill.useTroi = true;
        player.effectSkill.lastTimeTroi = lastTimeTroi;
        player.effectSkill.timeTroi = timeTroi;
    }
    //**************************************************************************

    //Thôi miên ****************************************************************
    //thiết lập thời gian bắt đầu bị thôi miên
    public void setThoiMien(Player player, long lastTimeThoiMien, int timeThoiMien) {
        player.effectSkill.isThoiMien = true;
        player.effectSkill.lastTimeThoiMien = lastTimeThoiMien;
        player.effectSkill.timeThoiMien = timeThoiMien;
    }

    //hết hiệu ứng thôi miên
    public void removeThoiMien(Player player) {
        player.effectSkill.isThoiMien = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, SLEEP_EFFECT);
    }

    //**************************************************************************
    //Thái dương hạ san &&&&****************************************************
    //player ăn choáng thái dương hạ san
    public void startStun(Player player, long lastTimeStartBlind, int timeBlind) {
        player.effectSkill.lastTimeStartStun = lastTimeStartBlind;
        player.effectSkill.timeStun = timeBlind;
        player.effectSkill.isStun = true;
        sendEffectPlayer(player, player, TURN_ON_EFFECT, BLIND_EFFECT);
    }

    //kết thúc choáng thái dương hạ san
    public void removeStun(Player player) {
        player.effectSkill.isStun = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, BLIND_EFFECT);
    }
    //**************************************************************************

    //Socola *******************************************************************
    //player biến thành socola
    public void setSocola(Player player, long lastTimeSocola, int timeSocola) {
        player.effectSkill.lastTimeSocola = lastTimeSocola;
        player.effectSkill.timeSocola = timeSocola;
        player.effectSkill.isSocola = true;
        player.effectSkill.countPem1hp = 0;
    }
    public void setBinh(Player player, long lastTimeBinh, int timeBinh) {
        player.effectSkill.lastTimeBinh = lastTimeBinh;
        player.effectSkill.timeBinh = timeBinh;
        player.effectSkill.isBinh = true;
    }

    //player trở lại thành người
    public void removeSocola(Player player) {
        player.effectSkill.isSocola = false;
        Service.getInstance().Send_Caitrang(player);
    }
    //player trở lại thành người
    public void removeBinh(Player player) {
        player.effectSkill.isBinh = false;
        Service.getInstance().Send_Caitrang(player);
    }

    //quái biến thành socola
    public void sendMobToSocola(Player player, Mob mob, int timeSocola) {
        Message msg;
        try {
            msg = new Message(-112);
            msg.writer().writeByte(1);
            msg.writer().writeByte(mob.id); //mob id
            msg.writer().writeShort(4133); //icon socola
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
            mob.effectSkill.setSocola(System.currentTimeMillis(), timeSocola);
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    public void sendMobToBinh(Player player, Mob mob, int timeBinh) {
        Message msg;
        try {
            msg = new Message(-112);
            msg.writer().writeByte(1);
            msg.writer().writeByte(mob.id); //mob id
            msg.writer().writeShort(14522); //icon socola
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
            mob.effectSkill.setBinh(System.currentTimeMillis(), timeBinh);
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    //**************************************************************************

    //Dịch chuyển tức thời *****************************************************
    public void setBlindDCTT(Player player, long lastTimeDCTT, int timeBlindDCTT) {
        player.effectSkill.isBlindDCTT = true;
        player.effectSkill.lastTimeBlindDCTT = lastTimeDCTT;
        player.effectSkill.timeBlindDCTT = timeBlindDCTT;
    }

    public void removeBlindDCTT(Player player) {
        player.effectSkill.isBlindDCTT = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, BLIND_EFFECT);
    }
    //**************************************************************************

    //Huýt sáo *****************************************************************
    //Hưởng huýt sáo
    public void setStartHuytSao(Player player, long tiLeHP) {
        player.effectSkill.tiLeHPHuytSao = tiLeHP;
        player.effectSkill.lastTimeHuytSao = System.currentTimeMillis();
    }

    //Hết hiệu ứng huýt sáo
    public void removeHuytSao(Player player) {
        player.effectSkill.tiLeHPHuytSao = 0;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, HUYT_SAO_EFFECT);
        Service.getInstance().point(player);
        Service.getInstance().Send_Info_NV(player);
    }

    //**************************************************************************
    //Biến khỉ *****************************************************************
    //Bắt đầu biến khỉ
    public void setIsMonkey(Player player) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(EffectSkillService.class.getName()).log(Level.SEVERE, null, ex);
        }
        int timeMonkey = SkillUtil.getTimeMonkey(player.playerSkill.skillSelect.point);
        if(player.setClothes.cadic == 5){
            timeMonkey *= 5;
        }
        player.effectSkill.isMonkey = true;
        player.effectSkill.timeMonkey = timeMonkey;
        player.effectSkill.lastTimeUpMonkey = System.currentTimeMillis();
        player.effectSkill.levelMonkey = (byte) player.playerSkill.skillSelect.point;
        player.nPoint.setHp(player.nPoint.hp * 5);
    }

    public void monkeyDown(Player player) {
        player.effectSkill.isMonkey = false;
        player.effectSkill.levelMonkey = 0;
        if (player.nPoint.hp > player.nPoint.hpMax) {
            player.nPoint.setHp(player.nPoint.hpMax);
        }

        sendEffectEndCharge(player);
        sendEffectMonkey(player);
        Service.getInstance().setNotMonkey(player);
        Service.getInstance().Send_Caitrang(player);
        Service.getInstance().point(player);
        PlayerService.gI().sendInfoHpMp(player);
        Service.getInstance().Send_Info_NV(player);
        Service.getInstance().sendInfoPlayerEatPea(player);
    }
    
    public void monkeyDown2(Player player) {
        player.itemTimesieucap.isBienhinh1 = false;
        if (player.nPoint.hp > player.nPoint.hpMax) {
            player.nPoint.setHp(player.nPoint.hpMax);
        }
        sendEffectEndCharge1(player);
        sendEffectBienhinh(player);
        Service.getInstance().setNotBienhinh(player);
        Service.getInstance().Send_Caitrang(player);
        Service.getInstance().point(player);
        PlayerService.gI().sendInfoHpMp(player);
        Service.getInstance().Send_Info_NV(player);
        Service.getInstance().sendInfoPlayerEatPea(player);
    }
    
    public void setIsBienhinh2(Player player) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(EffectSkillService.class.getName()).log(Level.SEVERE, null, ex);
        }
        player.itemTimesieucap.isBienhinh1 = true;
        player.itemTimesieucap.lastTimeBienhinh1 = System.currentTimeMillis();
    }
    //hiệu ứng nổ kết thúc gồng
    public void sendEffectEndCharge2(Player player) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(5);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(97);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    
    public void sendEffectBienhinh2(Player player) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(97);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    public void monkeyDown1(Player player) {
        player.itemTimesieucap.isBienhinh = false;
        if (player.nPoint.hp > player.nPoint.hpMax) {
            player.nPoint.setHp(player.nPoint.hpMax);
        }
        sendEffectEndCharge1(player);
        sendEffectBienhinh(player);
        Service.getInstance().setNotBienhinh(player);
        Service.getInstance().Send_Caitrang(player);
        Service.getInstance().point(player);
        PlayerService.gI().sendInfoHpMp(player);
        Service.getInstance().Send_Info_NV(player);
        Service.getInstance().sendInfoPlayerEatPea(player);
    }
    
    public void setIsBienhinh(Player player) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(EffectSkillService.class.getName()).log(Level.SEVERE, null, ex);
        }
        player.itemTimesieucap.isBienhinh = true;
        player.itemTimesieucap.lastTimeBienhinh = System.currentTimeMillis();
    }
    //hiệu ứng nổ kết thúc gồng
    public void sendEffectEndCharge1(Player player) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(5);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(97);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    
    public void sendEffectBienhinh(Player player) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(97);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    //**************************************************************************
    //Tái tạo năng lượng *******************************************************

    public void startCharge(Player player) {
        if (!player.effectSkill.isCharging) {
            player.effectSkill.isCharging = true;
            sendEffectCharge(player);
        }
    }

    public void stopCharge(Player player) {
        player.effectSkill.countCharging = 0;
        player.effectSkill.isCharging = false;;
        sendEffectStopCharge(player);

    }

    //**************************************************************************
    //Khiên năng lượng *********************************************************
    public void setStartShield(Player player) {
        player.effectSkill.isShielding = true;
        player.effectSkill.lastTimeShieldUp = System.currentTimeMillis();
        player.effectSkill.timeShield = SkillUtil.getTimeShield(player.playerSkill.skillSelect.point);
    }

    public void removeShield(Player player) {
        player.effectSkill.isShielding = false;
        sendEffectPlayer(player, player, TURN_OFF_EFFECT, SHIELD_EFFECT);
    }

    public void breakShield(Player player) {
        removeShield(player);
        Service.getInstance().sendThongBao(player, "Khiên năng lượng đã bị vỡ!");
        ItemTimeService.gI().removeItemTime(player, 3784);
    }

    //**************************************************************************
    public void sendEffectBlindThaiDuongHaSan(Player plUseSkill, List<Player> players, List<Mob> mobs, int timeStun) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(0);
            msg.writer().writeInt((int) plUseSkill.id);
            msg.writer().writeShort(plUseSkill.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mobs.size());
            for (Mob mob : mobs) {
                msg.writer().writeByte(mob.id);
                msg.writer().writeByte(timeStun / 1000);
            }
            msg.writer().writeByte(players.size());
            for (Player pl : players) {
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeByte(timeStun / 1000);
            }
            Service.getInstance().sendMessAllPlayerInMap(plUseSkill, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //hiệu ứng bắt đầu gồng
    public void sendEffectStartCharge(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.TAI_TAO_NANG_LUONG);
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //hiệu ứng đang gồng
    public void sendEffectCharge(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.TAI_TAO_NANG_LUONG);
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(1);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //dừng gồng
    public void sendEffectStopCharge(Player player) {
        try {
            Message msg = new Message(-45);
            msg.writer().writeByte(3);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(-1);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //hiệu ứng nổ kết thúc gồng
    public void sendEffectEndCharge(Player player) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(5);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }

    //hiệu ứng biến khỉ
    public void sendEffectMonkey(Player player) {
        Skill skill = SkillUtil.getSkillbyId(player, Skill.BIEN_KHI);
        if (skill == null) {
            return;
        }
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(6);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(skill.skillId);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
            com.girlkun.utils.Logger.logException(EffectSkillService.class, e);
        }
    }
    
    //Remake by ndq (Zalo - 0372475179) *************************************************************
    //SKILL BIẾN HÌNH*****************************************************************
    public void setSkillBienHinh(Player player) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

        }

        Skill template = Manager.NCLASS.get(player.gender)
                .getSkillTemplate(player.playerSkill.skillSelect.template.id).skillss.stream().filter(s -> s.point == player.playerSkill.skillSelect.point)
                .findFirst().orElse(null);

        if (template == null) {
            return;
        }

        int skillLevel = player.playerSkill.skillSelect.point;
        boolean lastLevel = player.effectSkill.levelBienHinh >= skillLevel - 1;
        player.effectSkill.levelBienHinh = Math.min(skillLevel, player.effectSkill.levelBienHinh + 1);

        if (player.effectSkill.levelBienHinh == 1) {
            player.effectSkill.timeBienHinh = SkillUtil.getTimeBienHinh(skillLevel, template.coolDown);
            player.effectSkill.lastTimeBienHinh = System.currentTimeMillis();
            player.effectSkill.isBienHinh = true;
        }

        if (!lastLevel && template.coolDown > 0) {
            player.playerSkill.skillSelect.coolDown = 1500;
        } else {
            int nowTime = (int) (System.currentTimeMillis() - player.effectSkill.lastTimeBienHinh);
            player.playerSkill.skillSelect.coolDown = template.coolDown - nowTime;
        }
    }

    public void downSkillBienHinh(Player player) {
        player.effectSkill.isBienHinh = false;
        int level = player.effectSkill.levelBienHinh;
        player.effectSkill.levelBienHinh = 0;
        if (player.nPoint.hp > player.nPoint.hpMax) {
            player.nPoint.setHp(player.nPoint.hpMax);
        }

        sendEffectEndCharge(player);
        sendEffectBienhinh(player);
        Service.getInstance().setNotBienhinh(player);
        Service.getInstance().Send_Caitrang(player);
        Service.getInstance().point(player);
        RadaService.gI().setIDAuraEff(player, player.getAura());
        PlayerService.gI().sendInfoHpMp(player);
        Service.getInstance().Send_Info_NV(player);
        Service.getInstance().sendInfoPlayerEatPea(player);
        
        Skill sBienHinh = player.playerSkill.getSkillbyId(27 + player.gender);
        int skillLevel = sBienHinh.point;
        boolean lastLevel = player.effectSkill.levelBienHinh >= skillLevel - 1;
        if (!lastLevel) {
            Skill template = Manager.NCLASS.get(player.gender)
                .getSkillTemplate(sBienHinh.template.id).skillss.stream().filter(s -> s.point == sBienHinh.point)
                .findFirst().orElse(null);
            
            int nowTime = (int) (System.currentTimeMillis() - player.effectSkill.lastTimeBienHinh);
            sBienHinh.coolDown = template.coolDown - nowTime;
            Service.getInstance().sendTimeSkill(player);
        }
        
        int iconLvNow = (player.gender == 0 ? 30007 : player.gender == 1 ? 30013 : 30001) + level - 1;
        ItemTimeService.gI().removeItemTime(player, iconLvNow);
        
    }
}
