package com.girlkun.services;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.intrinsic.Intrinsic;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.mob.MobMe;
import com.girlkun.models.player.Pet.DaoLu.DaoLu;
import com.girlkun.models.player.Pet.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.SkillSpecial;
import com.girlkun.models.player.Thu_TrieuHoi;
import com.girlkun.models.player.tutien.luyenkhi.TuTien;
import com.girlkun.models.skill.Skill;
import com.girlkun.network.io.Message;
import com.girlkun.services.func.RadaService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SkillService {

    private static SkillService i;

    private SkillService() {

    }

    public static SkillService gI() {
        if (i == null) {
            i = new SkillService();
        }
        return i;
    }

    public boolean useSkill(Player player, Player plTarget, Mob mobTarget, Message message) {
        if (player.effectSkill.isHaveEffectSkill()) {
            return false;
        }
        if (player.playerSkill == null) {
            return false;
        }
        if (player.playerSkill.skillSelect.template.type == 2 && canUseSkillWithMana(player) && canUseSkillWithCooldown(player)) {
            useSkillBuffToPlayer(player, plTarget);
            return true;
        }
        if ((player.effectSkill.isHaveEffectSkill() && (player.playerSkill.skillSelect.template.id != Skill.TU_SAT && player.playerSkill.skillSelect.template.id != Skill.QUA_CAU_KENH_KHI && player.playerSkill.skillSelect.template.id != Skill.MAKANKOSAPPO)) || (plTarget != null && !canAttackPlayer(player, plTarget)) || (mobTarget != null && mobTarget.isDie()) || !canUseSkillWithMana(player) || !canUseSkillWithCooldown(player)) {
            return false;
        }
        if (player.effectSkill.useTroi) {
            EffectSkillService.gI().removeUseTroi(player);
        }
        if (player.effectSkill.isCharging) {
            EffectSkillService.gI().stopCharge(player);
        }
        if (player.isPet) {
//            ((Pet) player).lastTimeMoveIdle = System.currentTimeMillis();
        }

        //Fix Skill Special
        byte st = -1;
        byte skillId = -1;
        Short dx = -1;
        Short dy = -1;
        byte dir = -1;
        Short x = -1;
        Short y = -1;
        if (isUseSkill9(player.playerSkill.skillSelect.template.id)) {
            if ((player.isPl()) && message != null) {
                try {
                    st = message.reader().readByte();
                    skillId = message.reader().readByte();
                    dx = message.reader().readShort();
                    dy = message.reader().readShort();

                    dir = message.reader().readByte();
                    x = message.reader().readShort();
                    y = message.reader().readShort();
                    if (st == 20 && skillId != player.playerSkill.skillSelect.template.id) {
                        selectSkill(player, skillId);
                        return false;
                    }
                } catch (Exception e) {
                }
            }
        }
        switch (player.playerSkill.skillSelect.template.type) {
            case 1:
                useSkillAttack(player, plTarget, mobTarget);
                break;
            case 3:
                if (player.playerSkill.skillSelect.template.id > 26) {
                    useSkillNew(player, plTarget, mobTarget);
                } else {
                    useSkillAlone(player);
                }
                break;
            case 4:
                userSkillSpecial(player, st, skillId, dx, dy, dir, x, y);
                break;
            default:
                return false;
        }
        if (player.isPl() || player.isDaoLu) {
            int idSkill = player.playerSkill.skillSelect.template.id;
            if (player.petDaoLu != null) {
                if (player.petDaoLu.status == 3) {
//                    player.petDaoLu.changeStatus((byte) 0);
                }
                if (player.petDaoLu.status == 0) {
                    if (player.petDaoLu.zone == player.zone) {
                        selectSkill(player.petDaoLu, idSkill);
                        if (isUseSkill9(idSkill)) {
                            userSkillSpecial(player.petDaoLu, st, skillId, dx, dy, dir, x, y);
                        } else {
                            useSkill(player.petDaoLu, plTarget, mobTarget, null);
                        }
                    }
                }
            }
        }
        return true;
    }

    public boolean isUseSkill9(int idSkill) {
        return idSkill == Skill.SUPER_KAME || idSkill == Skill.LIEN_HOAN_CHUONG || idSkill == Skill.MA_PHONG_BA;
    }

    private void userSkillSpecial(Player player, byte st, byte skillId, Short dx, Short dy, byte dir, Short x, Short y) {
        try {
            switch (skillId) {
                case Skill.SUPER_KAME -> sendEffSkillSpecialID24(player, dir);
                case Skill.LIEN_HOAN_CHUONG -> sendEffSkillSpecialID25(player, dir);
                case Skill.MA_PHONG_BA -> sendEffSkillSpecialID26(player, dir);
            }
            affterUseSkill(player, player.playerSkill.skillSelect.template.id);
            player.skillSpecial.setSkillSpecial(dir, dx, dy, x, y);
        } catch (Exception e) {
        }
    }

    private void userSkillSpecial(Player player, Message message) {
        if (message == null) {
            return;
        }
        try {
            byte st = message.reader().readByte();
            byte skillId = message.reader().readByte();
            Short dx = message.reader().readShort();
            Short dy = message.reader().readShort();

            byte dir = message.reader().readByte();
            Short x = message.reader().readShort();
            Short y = message.reader().readShort();
            switch (skillId) {
                case Skill.SUPER_KAME:
//                    System.out.println("người chơi xài skill td " + player.id);
                    sendEffSkillSpecialID24(player, dir);
                    break;
                case Skill.LIEN_HOAN_CHUONG:
//                    System.out.println("người chơi xài skill xd " + player.id);
                    sendEffSkillSpecialID25(player, dir);
                    break;
                case Skill.MA_PHONG_BA:
//                    System.out.println("người chơi xài skill nm " + player.id);
                    sendEffSkillSpecialID26(player, dir);
                    break;
            }
            affterUseSkill(player, player.playerSkill.skillSelect.template.id);
            player.skillSpecial.setSkillSpecial(dir, dx, dy, x, y);
        } catch (Exception e) {
        }
    }

    public void updateSkillSpecial(Player player) {
        try {
            if (player.isDie() || player.effectSkill.isHaveEffectSkill()) {
                player.skillSpecial.closeSkillSpecial();
                return;
            }
            if (player.skillSpecial.skillSpecial.template.id == Skill.MA_PHONG_BA) {
                if (Util.canDoWithTime(player.skillSpecial.lastTimeSkillSpecial, SkillSpecial.TIME_GONG)) {
                    player.skillSpecial.lastTimeSkillSpecial = System.currentTimeMillis();
                    player.skillSpecial.closeSkillSpecial();
                    int timeBinh = SkillUtil.getTimeBinh();//thời gian biến thành bình

                    //hút người
                    for (Player playerMap : player.zone.getPlayers()) {
                        if (player.skillSpecial.dir == -1 && !playerMap.isDie() && Util.getDistance(player, playerMap) <= 500 && this.canAttackPlayer(player, playerMap)) {
                            player.skillSpecial.playersTaget.add(playerMap);

                        } else if (player.skillSpecial.dir == 1 && !playerMap.isDie() && Util.getDistance(player, playerMap) <= 500 && this.canAttackPlayer(player, playerMap)) {
                            player.skillSpecial.playersTaget.add(playerMap);
                        }
                        if (playerMap == null || playerMap.id == player.id) {
                            continue;
                        }
                    }
                    //hút quái
                    for (Mob mobMap : player.zone.mobs) {
                        if (player.skillSpecial.dir == -1 && !mobMap.isDie() && Util.getDistance(player, mobMap) <= 500) {
                            player.skillSpecial.mobsTaget.add(mobMap);

                        } else if (player.skillSpecial.dir == 1 && !mobMap.isDie() && Util.getDistance(player, mobMap) <= 500) {
                            player.skillSpecial.mobsTaget.add(mobMap);

                        }
                        if (mobMap == null) {
                            continue;
                        }
                    }

                    //bắt đầu hút
                    this.startSkillSpecialID26(player);
                    Thread.sleep(3000);//nghỉ 3s

                    //biến quái - bình
                    for (Mob mobMap : player.zone.mobs) {
                        if (player.skillSpecial.dir == -1 && !mobMap.isDie() && Util.getDistance(player, mobMap) <= 500) {
                            player.skillSpecial.mobsTaget.add(mobMap);

                        } else if (player.skillSpecial.dir == 1 && !mobMap.isDie() && Util.getDistance(player, mobMap) <= 500) {
                            player.skillSpecial.mobsTaget.add(mobMap);

                        }
                        if (mobMap == null) {
                            continue;
                        }
                        EffectSkillService.gI().sendMobToBinh(player, mobMap, timeBinh);//biến mob thành bình
                        this.playerAttackMob(player, mobMap, false, true); // trừ dame 
                    }

                    //biến người - bình
                    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

                    for (Player playerMap : player.zone.getPlayers()) {
                        if (player.skillSpecial.dir == -1 && !playerMap.isDie() && Util.getDistance(player, playerMap) <= 500 && this.canAttackPlayer(player, playerMap)) {
                            player.skillSpecial.playersTaget.add(playerMap);
                        } else if (player.skillSpecial.dir == 1 && !playerMap.isDie() && Util.getDistance(player, playerMap) <= 500 && this.canAttackPlayer(player, playerMap)) {
                            player.skillSpecial.playersTaget.add(playerMap);
                        }
                        if (playerMap == null || playerMap.id == player.id) {
                            continue;
                        }
                        if (this.canAttackPlayer(player, playerMap)) {
                            ItemTimeService.gI().sendItemTime(playerMap, 14523, timeBinh / 1000);
                            EffectSkillService.gI().setBinh(playerMap, System.currentTimeMillis(), timeBinh);
                            Service.getInstance().Send_Caitrang(playerMap);
                            Skill curSkill = SkillUtil.getSkillbyId(player, Skill.MA_PHONG_BA);
                            double ptdame = 0;
                            if (curSkill.point == 1) {
                                ptdame = 0.01;
                            } else if (curSkill.point == 2) {
                                ptdame = 0.01;
                            } else if (curSkill.point == 3) {
                                ptdame = 0.02;
                            } else if (curSkill.point == 4) {
                                ptdame = 0.02;
                            } else if (curSkill.point == 5) {
                                ptdame = 0.03;
                            } else if (curSkill.point == 6) {
                                ptdame = 0.03;
                            } else if (curSkill.point == 7) {
                                ptdame = 0.04;
                            } else if (curSkill.point == 8) {
                                ptdame = 0.04;
                            } else if (curSkill.point == 9) {
                                ptdame = 0.06;
                            }
                            double dameHit = playerMap.nPoint.hpMax * ptdame;
                            for (int i = 0; i < 10; i++) {
                                final int index = i;
                                executorService.schedule(() -> {
                                    playerMap.injured(playerMap, dameHit, false, false, false);
                                    PlayerService.gI().sendInfoHpMpMoney(playerMap); //gửi in4 hp cho player bị nhốt
                                    this.playerAttackPlayer(player, playerMap, true, false);
                                    if (index == 0) {
                                        this.playerAttackPlayer(player, playerMap, true, false);
                                    }
                                }, index, TimeUnit.SECONDS);
                            }
                        }
                    }

                    // Sau khi hoàn thành tất cả các tác vụ, hủy bỏ ScheduledExecutorService
                    executorService.shutdown();

                }
            } else if (player.skillSpecial.stepSkillSpecial == 0 && Util.canDoWithTime(player.skillSpecial.lastTimeSkillSpecial, SkillSpecial.TIME_GONG)) {
                player.skillSpecial.lastTimeSkillSpecial = System.currentTimeMillis();
                player.skillSpecial.stepSkillSpecial = 1;
                if (player.skillSpecial.skillSpecial.template.id == Skill.SUPER_KAME) {
                    this.startSkillSpecialID24(player);
                } else {
                    this.startSkillSpecialID25(player);
                }
            } else if (player.skillSpecial.stepSkillSpecial == 1 && !Util.canDoWithTime(player.skillSpecial.lastTimeSkillSpecial, SkillSpecial.TIME_END_24_25)) {
                for (Player playerMap : player.zone.getHumanoids()) {
                    if (player.skillSpecial.dir == -1 && !playerMap.isDie() && playerMap.location.x <= player.location.x - 15 && Math.abs(playerMap.location.x - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget && Math.abs(playerMap.location.y - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget && this.canAttackPlayer(player, playerMap)) {
                        this.playerAttackPlayer(player, playerMap, false, true);
                        PlayerService.gI().sendInfoHpMpMoney(playerMap);
                    }
                    if (player.skillSpecial.dir == 1 && !playerMap.isDie() && playerMap.location.x >= player.location.x + 15 && Math.abs(playerMap.location.x - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget && Math.abs(playerMap.location.y - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget && this.canAttackPlayer(player, playerMap)) {
                        this.playerAttackPlayer(player, playerMap, false, true);
                        PlayerService.gI().sendInfoHpMpMoney(playerMap);
                    }
                    if (playerMap == null) {
                        continue;
                    }
                }
                for (Mob mobMap : player.zone.mobs) {
                    if (player.skillSpecial.dir == -1 && !mobMap.isDie() && mobMap.location.x <= player.skillSpecial._xPlayer - 15 && Math.abs(mobMap.location.x - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget && Math.abs(mobMap.location.y - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget) {
                        this.playerAttackMob(player, mobMap, false, false);
                    }
                    if (player.skillSpecial.dir == 1 && !mobMap.isDie() && mobMap.location.x >= player.skillSpecial._xPlayer + 15 && Math.abs(mobMap.location.x - player.skillSpecial._xPlayer) <= player.skillSpecial._xObjTaget && Math.abs(mobMap.location.y - player.skillSpecial._yPlayer) <= player.skillSpecial._yObjTaget) {
                        this.playerAttackMob(player, mobMap, false, false);
                    }
                    if (mobMap == null) {
                        continue;
                    }
                }
            } else if (player.skillSpecial.stepSkillSpecial == 1) {
                player.skillSpecial.closeSkillSpecial();
            }
        } catch (Exception e) {
        }
    }

    public void sendCurrLevelSpecial(Player player, Skill skill) {
        Message message = null;
        try {
            message = Service.getInstance().messageSubCommand((byte) 62);
            message.writer().writeShort(skill.skillId);
            message.writer().writeByte(0);
            message.writer().writeShort(skill.currLevel);
            player.sendMessage(message);
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    //============================================================================
    // Skill SuperKame
    public void sendEffSkillSpecialID24(Player player, byte dir) {
        Message message = null;
        try {
            message = new Message(-45);// passt code k dc vcb 
            message.writer().writeByte(20);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(24);
            message.writer().writeByte(1);
            message.writer().writeByte(dir); // -1 trai | 1 phai
            message.writer().writeShort(2000);
            message.writer().writeByte(0);
            message.writer().writeByte(player.gender);
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (Exception e) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    // Skill liên hoàn chưởng
    public void sendEffSkillSpecialID25(Player player, byte dir) {
        Message message = null;
        try {
            message = new Message(-45);// passt code k dc vcb 
            message.writer().writeByte(20);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(25);
            message.writer().writeByte(2);
            message.writer().writeByte(dir); // -1 trai | 1 phai
            message.writer().writeShort(2000);
            message.writer().writeByte(0);
            message.writer().writeByte(player.gender);
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (Exception e) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    // Skill Ma phong ba
    public void sendEffSkillSpecialID26(Player player, byte dir) {
        Message message = null;
        try {
            message = new Message(-45);// passt code k dc vcb 
            message.writer().writeByte(20);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(26);
            message.writer().writeByte(3);
            message.writer().writeByte(dir); // -1 trai | 1 phai
            message.writer().writeShort(SkillSpecial.TIME_GONG);
            message.writer().writeByte(0);
            message.writer().writeByte(player.gender);
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (Exception e) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    public void startSkillSpecialID24(Player player) {
        Message message = null;
        try {
            message = new Message(-45);
            message.writer().writeByte(21);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(player.skillSpecial.skillSpecial.template.id);
            message.writer().writeShort(player.skillSpecial._xPlayer + ((player.skillSpecial.dir == -1) ? (-player.skillSpecial._xObjTaget) : player.skillSpecial._xObjTaget));
            message.writer().writeShort(player.skillSpecial._xPlayer);
            message.writer().writeShort(3000); // thời gian skill chưởng chưởng nè
            message.writer().writeShort(player.skillSpecial._yObjTaget);
            message.writer().writeByte(player.gender);
            message.writer().writeByte(0);
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    public void startSkillSpecialID25(Player player) {
        Message message = null;
        try {
            message = new Message(-45);
            message.writer().writeByte(21);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(player.skillSpecial.skillSpecial.template.id);
            message.writer().writeShort(player.skillSpecial._xPlayer + ((player.skillSpecial.dir == -1) ? (-player.skillSpecial._xObjTaget) : player.skillSpecial._xObjTaget));
            message.writer().writeShort(player.skillSpecial._yPlayer);
            message.writer().writeShort(3000); // thời gian skill chưởng chưởng nè
            message.writer().writeShort(25);
            message.writer().writeByte(player.gender);
            message.writer().writeByte(0);
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }
    }

    public void startSkillSpecialID26(Player player) {
        Message message = null;
        try {
            message = new Message(-45);
            message.writer().writeByte(21);
            message.writer().writeInt((int) player.id);
            message.writer().writeShort(26);
            message.writer().writeShort(player.skillSpecial._xPlayer + ((player.skillSpecial.dir == -1) ? (-75) : 75));
            message.writer().writeShort(player.skillSpecial._yPlayer);
            message.writer().writeShort(3000);
            message.writer().writeShort(player.skillSpecial._yObjTaget);
            message.writer().writeByte(player.gender);
            final byte size = (byte) (player.skillSpecial.playersTaget.size() + player.skillSpecial.mobsTaget.size());
            message.writer().writeByte(size);
            for (Player playerMap : player.skillSpecial.playersTaget) {
                message.writer().writeByte(1);
                message.writer().writeInt((int) playerMap.id);

            }
            for (Mob mobMap : player.skillSpecial.mobsTaget) {
                message.writer().writeByte(0);
                message.writer().writeByte(mobMap.id);
            }
            message.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, message);
            message.cleanup();
        } catch (final Exception ex) {
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }
        }

    }

    // này hoc5 skill nha
    public void learSkillSpecial(Player player, byte skillID) {
        Message message = null;
        try {
            Skill curSkill = SkillUtil.createSkill(skillID, 1);
            SkillUtil.setSkill(player, curSkill);
            message = Service.getInstance().messageSubCommand((byte) 23);
            message.writer().writeShort(curSkill.skillId);
            player.sendMessage(message);
            message.cleanup();
        } catch (Exception e) {
            System.out.println("88888");
        } finally {
            if (message != null) {
                message.cleanup();
                message = null;
            }

        }
    }

    private void useSkillNew(Player player, Player plTarget, Mob mobTarget) {
        try {
            switch (player.playerSkill.skillSelect.template.id) {
                case Skill.BIEN_HINH_TD:
                case Skill.BIEN_HINH_NM:
                case Skill.BIEN_HINH_XD:
                    EffectSkillService.gI().sendEffectBienhinh(player);
                    if (player.effectSkill.levelBienHinh < player.playerSkill.skillSelect.point) {
                        EffectSkillService.gI().setSkillBienHinh(player);
                        EffectSkillService.gI().sendEffectBienhinh(player);
                        Service.getInstance().sendSpeedPlayer(player, 0);
                        Service.getInstance().Send_Caitrang(player);
                        Service.getInstance().sendSpeedPlayer(player, -1);
                        Service.getInstance().point(player);
                        player.nPoint.setFullHpMp();
                        PlayerService.gI().sendInfoHpMp(player);
                        RadaService.gI().setIDAuraEff(player, player.getAura());
                        ItemTimeService.gI().sendItemTimeBienHinh(player, player.effectSkill.levelBienHinh);
                    } else {
                        Service.gI().sendThongBao(player, "Đây đã là giới hạn của ngươi rồi...!");
                    }
                    break;
            }
            affterUseSkill(player, player.playerSkill.skillSelect.template.id);
        } catch (Exception e) {
        }
    }

    private boolean canEffectOnTarget(Player playerTarget) {
        if (playerTarget.tuTien.isTuTien() && playerTarget.tuTien.linhCan.getLinhCanType() == 0 && playerTarget.nPoint.getCurrPercentHP() <= 30) {
            return false;
        }
        if (playerTarget.tuTien.isTuTien() && playerTarget.tuTien.linhCan.getLinhCanType() == 2 && playerTarget.nPoint.getCurrPercentHP() <= 70) {
            return false;
        }
        return true;
    }

    private void useSkillAttack(Player player, Player plTarget, Mob mobTarget) {
        if (!player.isBoss) {
            if (player.isTrieuhoipet && ((Thu_TrieuHoi) player).masterr.TrieuHoiCapBac != -1 && ((Thu_TrieuHoi) player).masterr.TrieuHoipet != null) {
                ((Thu_TrieuHoi) player).masterr.TrieuHoiExpThanThu += Util.nextInt(1, 50_000);
                if (((Thu_TrieuHoi) player).masterr.TrieuHoiExpThanThu - 3_000_000 >= 0 && ((Thu_TrieuHoi) player).masterr.TrieuHoiLevel + 1 <= 100) {
                    ((Thu_TrieuHoi) player).masterr.TrieuHoiLevel++;
                    ((Thu_TrieuHoi) player).masterr.TrieuHoiExpThanThu -= 3_000_000;
                }
            } else if (player.isPet) {
                if (player.nPoint.stamina > 0) {
                    player.nPoint.numAttack++;
                    boolean haveCharmPet = ((Pet) player).master.charms.tdDeTu > System.currentTimeMillis();
                    if (haveCharmPet ? player.nPoint.numAttack >= 5 : player.nPoint.numAttack >= 2) {
                        player.nPoint.numAttack = 0;
                        player.nPoint.stamina--;
                    }
                    if (player.getMaster().khongThiSu != null && player.getMaster().khongThiSu.isKhongThi() && player.getMaster().tuTien.isKhongThi) {
                        player.getMaster().khongThiSu.addExp(player.getMaster().khongThiSu.getExpCanGain(mobTarget));
                    }
                } else {
                    ((Pet) player).askPea();
                    return;
                }
            } else if (player.isDaoLu) {
                player.nPoint.numAttack++;
                if (player.nPoint.numAttack == 5) {
                    player.nPoint.numAttack = 0;
                    player.nPoint.stamina--;
                    PlayerService.gI().sendCurrentStamina(player);
                }
            } else if (player.nPoint.stamina > 0) {
                if (player.charms.tdDeoDai < System.currentTimeMillis()) {
                    player.nPoint.numAttack++;
                    if (player.luyenThe.isLuyenTheReal()) {
                        if (player.nPoint.numAttack == 20) {
                            player.nPoint.numAttack = 0;
                            player.nPoint.stamina--;
                            PlayerService.gI().sendCurrentStamina(player);
                        }
                    } else if (player.tuMa.isTuMa()) {
                        if (player.nPoint.numAttack == 10) {
                            player.nPoint.numAttack = 0;
                            player.nPoint.stamina--;
                            PlayerService.gI().sendCurrentStamina(player);
                        }
                    } else if (player.nPoint.numAttack == 5) {
                        player.nPoint.numAttack = 0;
                        player.nPoint.stamina--;
                        PlayerService.gI().sendCurrentStamina(player);
                    }
                }
            } else {
                Service.getInstance().sendThongBao(player, "Thể lực đã cạn kiệt, hãy nghỉ ngơi để lấy lại sức");
                return;
            }
        }
        List<Mob> mobs;
        boolean miss = false;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.KAIOKEN: //kaioken
                double hpUse = player.nPoint.hpMax / 100 * 1;
                if (player.nPoint.hp <= hpUse) {
                    break;
                } else {
                    player.nPoint.setHp(player.nPoint.hp - hpUse);
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    Service.getInstance().Send_Info_NV(player);
                }
            case Skill.DRAGON:
            case Skill.DEMON:
            case Skill.GALICK:
            case Skill.LIEN_HOAN:
                if (plTarget != null && Util.getDistance(player, plTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
                if (mobTarget != null && Util.getDistance(player, mobTarget) > Skill.RANGE_ATTACK_CHIEU_DAM) {
                    miss = true;
                }
            case Skill.KAMEJOKO:
            case Skill.MASENKO:
            case Skill.ANTOMIC:
                if (plTarget != null) {
                    playerAttackPlayer(player, plTarget, miss, true);
                }
                if (mobTarget != null) {
                    playerAttackMob(player, mobTarget, miss, false);
                }
                if (player.mobMe != null) {
                    player.mobMe.attack(plTarget, mobTarget);
                }
                if (player.isDaoLu && ((DaoLu) player).status == 2) {
                    ((DaoLu) player).addPointTuViDaoLu();
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            //******************************************************************
            case Skill.QUA_CAU_KENH_KHI:
                if (!player.playerSkill.prepareQCKK) {
                    //bắt đầu tụ quả cầu
                    player.playerSkill.prepareQCKK = !player.playerSkill.prepareQCKK;
                    player.playerSkill.lastTimePrepareQCKK = System.currentTimeMillis();
                    if (player.isPet || player.isTrieuhoipet) {
                        if (player.gender == 2) {
                            sendPlayerPrepareBom(player, 2000);
                        } else {
                            sendPlayerPrepareSkill(player, 2000);
                        }
                    } else {
                        sendPlayerPrepareSkill(player, 1000);
                    }
                } else {
                    //ném cầu
                    player.playerSkill.prepareQCKK = !player.playerSkill.prepareQCKK;
                    mobs = new ArrayList<>();
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false, false);
                        if (player.isBoss) {
                            for (Player pl : player.zone.notBosses) {
                                if (!pl.isDie() && Util.getDistance(plTarget, pl) <= SkillUtil.getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                    playerAttackPlayer(player, pl, false, true);
                                }
                            }
                        }
                        for (Mob mob : player.zone.mobs) {
                            if (!mob.isDie() && Util.getDistance(plTarget, mob) <= SkillUtil.getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                mobs.add(mob);
                            }
                        }
                    }
                    if (mobTarget != null) {
                        playerAttackMob(player, mobTarget, false, true);
                        for (Mob mob : player.zone.mobs) {
                            if (!mob.equals(mobTarget) && !mob.isDie() && Util.getDistance(mob, mobTarget) <= SkillUtil.getRangeQCKK(player.playerSkill.skillSelect.point)) {
                                mobs.add(mob);
                            }
                        }
                    }
                    for (Mob mob : mobs) {
                        mob.injured(player, player.nPoint.getDameAttack(true), true, (byte) 0); //mở thử
                    }
                    mobs.clear();
                    PlayerService.gI().sendInfoHpMpMoney(player);
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                break;
            case Skill.MAKANKOSAPPO:
                if (!player.playerSkill.prepareLaze) {
                    //bắt đầu nạp laze
                    player.playerSkill.prepareLaze = !player.playerSkill.prepareLaze;
                    player.playerSkill.lastTimePrepareLaze = System.currentTimeMillis();
                    sendPlayerPrepareSkill(player, 1000);
                } else {
                    //bắn laze
                    player.playerSkill.prepareLaze = !player.playerSkill.prepareLaze;
                    if (plTarget != null) {
                        playerAttackPlayer(player, plTarget, false, true);
                    }
                    if (mobTarget != null) {
                        playerAttackMob(player, mobTarget, false, true);
//                        mobTarget.attackMob(player, false, true);
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                }
                PlayerService.gI().sendInfoHpMpMoney(player);
                break;
            case Skill.SOCOLA:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.SOCOLA);
                int timeSocola = SkillUtil.getTimeSocola();
                if (plTarget != null) {
                    EffectSkillService.gI().setSocola(plTarget, System.currentTimeMillis(), timeSocola);
                    Service.getInstance().Send_Caitrang(plTarget);
                    ItemTimeService.gI().sendItemTime(plTarget, 3780, timeSocola / 1000);
                }
                if (mobTarget != null) {
                    EffectSkillService.gI().sendMobToSocola(player, mobTarget, timeSocola);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                int timeChoangDCTT = SkillUtil.getTimeDCTT(player.playerSkill.skillSelect.point);
                if (plTarget != null) {
                    Service.getInstance().setPos(player, plTarget.location.x, plTarget.location.y);
                    playerAttackPlayer(player, plTarget, miss, true);
                    if (canEffectOnTarget(plTarget)) {
                        EffectSkillService.gI().setBlindDCTT(plTarget, System.currentTimeMillis(), timeChoangDCTT);
                        EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.BLIND_EFFECT);
                        ItemTimeService.gI().sendItemTime(plTarget, 3779, timeChoangDCTT / 1000);
                    }
                    PlayerService.gI().sendInfoHpMpMoney(plTarget);
                }
                if (mobTarget != null) {
                    Service.getInstance().setPos(player, mobTarget.location.x, mobTarget.location.y);
//                    mobTarget.attackMob(player, false, false);
                    playerAttackMob(player, mobTarget, false, false);
                    mobTarget.effectSkill.setStartBlindDCTT(System.currentTimeMillis(), timeChoangDCTT);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.BLIND_EFFECT);
                }
                player.nPoint.isCrit100 = true;
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.THOI_MIEN:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.THOI_MIEN);
                int timeSleep = SkillUtil.getTimeThoiMien(player.playerSkill.skillSelect.point);
                if (plTarget != null && canEffectOnTarget(plTarget)) {
                    EffectSkillService.gI().setThoiMien(plTarget, System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SLEEP_EFFECT);
                    ItemTimeService.gI().sendItemTime(plTarget, 3782, timeSleep / 1000);
                }
                if (mobTarget != null) {
                    mobTarget.effectSkill.setThoiMien(System.currentTimeMillis(), timeSleep);
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SLEEP_EFFECT);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TROI:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.TROI);
                int timeHold = SkillUtil.getTimeTroi(player.playerSkill.skillSelect.point);
                EffectSkillService.gI().setUseTroi(player, System.currentTimeMillis(), timeHold);
                if (plTarget != null && canEffectOnTarget(plTarget) && (!plTarget.playerSkill.prepareQCKK && !plTarget.playerSkill.prepareLaze && !plTarget.playerSkill.prepareTuSat)) {
                    player.effectSkill.plAnTroi = plTarget;
                    EffectSkillService.gI().sendEffectPlayer(player, plTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HOLD_EFFECT);
                    EffectSkillService.gI().setAnTroi(plTarget, player, System.currentTimeMillis(), timeHold);
                }
                if (mobTarget != null) {
                    player.effectSkill.mobAnTroi = mobTarget;
                    EffectSkillService.gI().sendEffectMob(player, mobTarget, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HOLD_EFFECT);
                    mobTarget.effectSkill.setTroi(System.currentTimeMillis(), timeHold);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                if (plTarget != null && plTarget.isBoss && MapService.gI().isMapHuyDiet(player.zone.map.mapId) && Util.isTrue(10, 100)) {
                    EffectSkillService.gI().removeUseTroi(player);
                    EffectSkillService.gI().removeAnTroi(plTarget);
                    Service.getInstance().chat(plTarget, "Chiêu đó không có tác dụng đâu kaka");
                }
                if (plTarget != null && plTarget.isBoss && MapService.gI().isMap1sao(player.zone.map.mapId) && Util.isTrue(99, 100)) {
                    EffectSkillService.gI().removeUseTroi(player);
                    EffectSkillService.gI().removeAnTroi(plTarget);
                    Service.getInstance().chat(plTarget, "Chiêu đó không có tác dụng đâu kaka");
                }
                break;
        }
        // handle auto use linh ky tan cong
        // chose better tan cong linh ky
        if (player.tuTien.isAutoUseTienPhap && player.isPl()) {
            player.tuTien.useBestAttackTienPhap();
        }
        if (!player.isBoss) {
            player.effectSkin.lastTimeAttack = System.currentTimeMillis();
        }
    }

    private void useSkillAlone(Player player) {
        List<Mob> mobs;
        List<Player> players;
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.THAI_DUONG_HA_SAN:
                int timeStun = SkillUtil.getTimeStun(player.playerSkill.skillSelect.point);
                if (player.setClothes.thienXinHang == 5) {
                    timeStun *= 2;
                }
                mobs = new ArrayList<>();
                players = new ArrayList<>();
                if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                    List<Player> playersMap = player.zone.getHumanoids();
                    for (Player pl : playersMap) {
                        if (pl != null && !player.equals(pl) && !pl.nPoint.khangTDHS && pl.dakethon < 3) {
                            if (Util.getDistance(player, pl) <= SkillUtil.getRangeStun(player.playerSkill.skillSelect.point) && canAttackPlayer(player, pl) //                                        && (!pl.playerSkill.prepareQCKK && !pl.playerSkill.prepareLaze && !pl.playerSkill.prepareTuSat)
                            ) {
                                if (player.isPet && ((Pet) player).master.equals(pl)) {
                                    continue;
                                }
                                if (player.isDaoLu && ((DaoLu) player).master.equals(pl)) {
                                    continue;
                                }
                                if (!canEffectOnTarget(pl)) {
                                    continue;
                                }
                                EffectSkillService.gI().startStun(pl, System.currentTimeMillis(), timeStun);
                                players.add(pl);
                            }
                        }
                    }
                }
                for (Mob mob : player.zone.mobs) {
                    if (Util.getDistance(player, mob) <= SkillUtil.getRangeStun(player.playerSkill.skillSelect.point)) {
                        mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                        mobs.add(mob);
                    }
                }
                EffectSkillService.gI().sendEffectBlindThaiDuongHaSan(player, players, mobs, timeStun);
                if (player.isPl()) {
                    player.achievement.plusCount(14);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.DE_TRUNG:
                EffectSkillService.gI().sendEffectUseSkill(player, Skill.DE_TRUNG);
                if (player.mobMe != null) {
                    player.mobMe.mobMeDie();
                }
                player.mobMe = new MobMe(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.BIEN_KHI:
                EffectSkillService.gI().sendEffectMonkey(player);
                EffectSkillService.gI().setIsMonkey(player);
                EffectSkillService.gI().sendEffectMonkey(player);

                Service.getInstance().sendSpeedPlayer(player, 0);
                Service.getInstance().Send_Caitrang(player);
                Service.getInstance().sendSpeedPlayer(player, -1);
                if (!player.isPet) {
                    PlayerService.gI().sendInfoHpMp(player);
                }
                Service.getInstance().point(player);
                Service.getInstance().Send_Info_NV(player);
                Service.getInstance().sendInfoPlayerEatPea(player);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.KHIEN_NANG_LUONG:
                EffectSkillService.gI().setStartShield(player);
                EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.SHIELD_EFFECT);
                ItemTimeService.gI().sendItemTime(player, 3784, player.effectSkill.timeShield / 1000);
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.HUYT_SAO:
                long tileHP = SkillUtil.getPercentHPHuytSao(player.playerSkill.skillSelect.point);
                if (player.zone != null) {
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        List<Player> playersMap = player.zone.getHumanoids();
                        for (Player pl : playersMap) {
                            if (pl.effectSkill.useTroi) {
                                EffectSkillService.gI().removeUseTroi(pl);
                            }
                            if (!pl.isBoss && pl.gender != ConstPlayer.NAMEC && player.cFlag == pl.cFlag) {
                                EffectSkillService.gI().setStartHuytSao(pl, tileHP);
                                EffectSkillService.gI().sendEffectPlayer(pl, pl, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HUYT_SAO_EFFECT);
                                pl.nPoint.calPoint();
                                pl.nPoint.setHp(pl.nPoint.hp + (pl.nPoint.hp * tileHP / 100));
                                Service.getInstance().point(pl);
                                Service.getInstance().Send_Info_NV(pl);
                                ItemTimeService.gI().sendItemTime(pl, 3781, 30);
                                PlayerService.gI().sendInfoHpMp(pl);
                            }

                        }
                    } else {
                        EffectSkillService.gI().setStartHuytSao(player, tileHP);
                        EffectSkillService.gI().sendEffectPlayer(player, player, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.HUYT_SAO_EFFECT);
                        player.nPoint.calPoint();
                        player.nPoint.setHp(player.nPoint.hp + (player.nPoint.hp * tileHP / 100));
                        Service.getInstance().point(player);
                        Service.getInstance().Send_Info_NV(player);
                        ItemTimeService.gI().sendItemTime(player, 3781, 30);
                        PlayerService.gI().sendInfoHpMp(player);
                    }
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TAI_TAO_NANG_LUONG:
                EffectSkillService.gI().startCharge(player);
                if (player.isPl()) {
                    player.achievement.plusCount(14);
                }
                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                break;
            case Skill.TU_SAT:
                if (!player.playerSkill.prepareTuSat) {
                    //gồng tự sát
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    player.playerSkill.lastTimePrepareTuSat = System.currentTimeMillis();
                    sendPlayerPrepareBom(player, 2000);
                } else {
                    if (!player.isBoss && !Util.canDoWithTime(player.playerSkill.lastTimePrepareTuSat, 1500)) {
                        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis();
                        player.playerSkill.prepareTuSat = false;
                        return;
                    }
                    //nổ
                    player.playerSkill.prepareTuSat = !player.playerSkill.prepareTuSat;
                    double dame = player.nPoint.hpMax;
                    player.tusat = true;
                    for (Mob mob : player.zone.mobs) {
                        mob.injured(player, dame, true, (byte) 0);
                    }
                    List<Player> playersMap = null;
                    if (player.isBoss) {
                        playersMap = player.zone.getNotBosses();
                    } else {
                        playersMap = player.zone.getHumanoids();
                    }
                    if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                        for (Player pl : playersMap) {
                            if (!player.equals(pl) && canAttackPlayer(player, pl)) {
                                pl.injured(player, pl.isBoss ? dame / 2 : dame, false, false, false);
                                PlayerService.gI().sendInfoHpMpMoney(pl);
                                Service.getInstance().Send_Info_NV(pl);
                            }
                        }
                    }
                    affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                    player.injured(null, 2100000000, true, false, false);
                    if (player.effectSkill.tiLeHPHuytSao != 0) {
                        player.effectSkill.tiLeHPHuytSao = 0;
                        EffectSkillService.gI().removeHuytSao(player);
                    }
                }
                break;
        }
    }

    public void useSkillBuffToPlayer(Player player, Player plTarget) {
        switch (player.playerSkill.skillSelect.template.id) {
            case Skill.TRI_THUONG:
                List<Player> playersMap = null;
                playersMap = player.zone.getNotBosses();
                Message msg = null;
                Player plTriThuong = null;
                for (Player pl : playersMap) {
                    if (pl != null && pl.zone.zoneId == player.zone.zoneId && !pl.isNewPet && !pl.isBoss && !plTarget.isBoss && !plTarget.isNewPet && canHsPlayer(player, plTarget)) {
                        if (player.playerSkill.skillSelect.point > 1) {
                            plTriThuong = pl;
                        } else if (player.playerSkill.skillSelect.point == 1) {
                            plTriThuong = plTarget;
                        }
                        if (plTriThuong != null) {
                            int percentTriThuong = SkillUtil.getPercentTriThuong(player.playerSkill.skillSelect.point);
                            Service.gI().chat(plTriThuong, "Cảm ơn " + player.name + " đã cứu mình");
                            try {
                                msg = new Message(-60);
                                msg.writer().writeInt((int) player.id); //id pem
                                msg.writer().writeByte(player.playerSkill.skillSelect.skillId); //skill pem
                                msg.writer().writeByte(1); //số người pem
                                msg.writer().writeInt((int) plTriThuong.id); //id ăn pem
                                msg.writer().writeByte(0); //read continue
                                msg.writer().writeByte(player.playerSkill.skillSelect.template.type); //type skill
                                Service.gI().sendMessAllPlayerInMap(plTriThuong.zone, msg);
                                msg.cleanup();
                                boolean isDie = plTriThuong.isDie();
                                player.nPoint.addHp((player.nPoint.hpMax * percentTriThuong / 100));
                                plTriThuong.nPoint.addHp((plTriThuong.nPoint.hpMax * percentTriThuong / 100));
                                plTriThuong.nPoint.addMp((plTriThuong.nPoint.mpMax * percentTriThuong / 100));
                                if (isDie) {
                                    Service.gI().Send_Info_NV(player);
                                    Service.gI().hsChar(plTriThuong, plTriThuong.nPoint.getHP(), plTriThuong.nPoint.getMP());
                                    PlayerService.gI().sendInfoHpMpMoney(plTriThuong);
                                    PlayerService.gI().sendInfoHpMp(player);
                                } else {
                                    Service.gI().Send_Info_NV(player);
                                    PlayerService.gI().sendInfoHpMpMoney(plTriThuong);
                                    PlayerService.gI().sendInfoHpMp(player);
                                }
                                Service.gI().Send_Info_NV(plTriThuong);
                            } catch (IOException e) {
                                //                            e.printStackTrace();
                            }
                        }
                    }
                }
                if (player.isPl()) {
                    player.achievement.plusCount(14);
                }

                affterUseSkill(player, player.playerSkill.skillSelect.template.id);
                playersMap = null;
                break;
        }
    }

    private void phanSatThuong(Player plAtt, Player plTarget, double dame) {
        double damePST = 0;
        int percentPST = 0;
        percentPST = plTarget.nPoint.tlPST;
        if (plTarget.tuTien.isTuTien() && plTarget.tuTien.linhCan.getLinhCanType() == 2) {
            percentPST += plTarget.tuTien.linhCan.getThuocTinhLinhCan().getParam();
        }
        if (plTarget.tuTien.isTuTien() && plTarget.tuTien.linhCan.getLinhCanType() == 4) {
            percentPST += plTarget.tuTien.linhCan.getThuocTinhLinhCan().getParam() / 5;
        }
        if (percentPST != 0) {
            damePST = Util.DoubleGioihan(dame * percentPST / 100);
            Message msg;
            try {
                msg = new Message(56);
                msg.writer().writeInt((int) plAtt.id);
                if (damePST >= plAtt.nPoint.hp) {
                    damePST = Util.DoubleGioihan(plAtt.nPoint.hp) - 1;
                }
                damePST = (damePST >= plAtt.nPoint.hp || plAtt.nPoint.hp < 2) ? 0 : plAtt.injured(null, damePST, true, false, false);
                plAtt.nPoint.hp = (damePST >= plAtt.nPoint.hp) ? 1 : (plAtt.nPoint.hp - damePST);
                msg.writer().writeDouble(Util.DoubleGioihang(plAtt.nPoint.hp));
                msg.writer().writeDouble(Util.DoubleGioihang(damePST));
                msg.writer().writeBoolean(false);
                msg.writer().writeByte(36);
                Service.getInstance().sendMessAllPlayerInMap(plAtt, msg);
                msg.cleanup();
            } catch (Exception e) {
                Logger.logException(SkillService.class, e);
            }
        }
    }

    private void hutHPMP(Player player, double dame, boolean attackMob) {
        int tiLeHutHp = player.nPoint.getTileHutHp(attackMob);
        int tiLeHutMp = player.nPoint.getTiLeHutMp();
        long hpHoi = Util.DoubleGioihan(dame * tiLeHutHp / 100);
        long mpHoi = Util.DoubleGioihan(dame * tiLeHutMp / 100);
        if (player.tuTien.isTuTien()) {
            if (hpHoi <= 0) {
                hpHoi = 1000;
            }
            if (mpHoi <= 0) {
                mpHoi = 1000;
            }
            short xParam = player.tuTien.linhCan.getThuocTinhLinhCan().getParam();
            if (player.tuTien.linhCan.getLinhCanType() == 8) {
                xParam *= (player.tuTien.linhCan.getThuocTinhLinhCan().getParam() * 5);
            }
            hpHoi += hpHoi * xParam / 100;
            mpHoi += mpHoi * xParam / 100;
        }
        if (hpHoi > 0 || mpHoi > 0) {
            PlayerService.gI().hoiPhuc(player, Math.abs(hpHoi), Math.abs(mpHoi));
        }
    }

//    private double subDameWithCanhGioi(Player plAtt, Player plInjure) {
//        double damGoc = -1;
//
//        // --------- XỬ LÝ TU TIÊN ----------
//        if ((plInjure.tuTien != null && plInjure.tuTien.isTuTien()) || plInjure.isBoss) {
//            damGoc = plAtt.nPoint.getDameAttack(false);
//            if (plInjure.isBoss) {
//                Boss boss = (Boss) plInjure;
//                byte level = boss.level;
//                byte subLevel = boss.subLevel;
//                if (!plAtt.tuTien.isTuTien() || plAtt.tuTien.level < level) {
//                    int levelDiff = level - plAtt.tuTien.level;
//                    damGoc -= damGoc * (30 + (20 * levelDiff)) / 100;
//                } else {
//                    int levelDiff = plAtt.tuTien.level - level;
//                    int subLevelDiff = plAtt.tuTien.subLevel - subLevel;
//                    if (subLevelDiff > 0) {
//                        damGoc += damGoc * (5 * subLevelDiff) / 100;
//                    }
//                    if (levelDiff > 0) {
//                        damGoc += damGoc * (30 * levelDiff) / 100;
//                    }
//                }
//            } else if (plInjure.isPl()) {
//                byte level = plInjure.tuTien.level;
//                byte subLevel = plInjure.tuTien.subLevel;
//                if (!plAtt.tuTien.isTuTien() || plAtt.tuTien.level < level) {
//                    int levelDiff = level - plAtt.tuTien.level;
//                    damGoc -= damGoc * (30 + (20 * levelDiff)) / 100;
//                } else {
//                    int levelDiff = plAtt.tuTien.level - level;
//                    int subLevelDiff = plAtt.tuTien.subLevel - subLevel;
//                    if (subLevelDiff > 0) {
//                        damGoc += damGoc * (10 * subLevelDiff) / 100;
//                    }
//                    if (levelDiff > 0) {
//                        damGoc += damGoc * (30 * levelDiff) / 100;
//                    }
//                }
//            }
//        }
//
//        // --------- XỬ LÝ TU MA ----------
//        if ((plInjure.tuMa != null && plInjure.tuMa.isTuMa()) || plInjure.isBoss) {
//            damGoc = plAtt.nPoint.getDameAttack(false);
//            if (plInjure.isBoss) {
//                Boss boss = (Boss) plInjure;
//                int level = boss.level;
//                int subLevel = boss.subLevel;
//
//                int attLevel = (plAtt.tuMa != null && plAtt.tuMa.isTuMa()) ? (plAtt.tuMa.level / 10) : -1;
//                int attSubLevel = (plAtt.tuMa != null && plAtt.tuMa.isTuMa()) ? (plAtt.tuMa.level % 10) : -1;
//
//                if (attLevel < level) {
//                    int levelDiff = level - attLevel;
//                    damGoc -= damGoc * (10 + (20 * levelDiff)) / 100;
//                } else {
//                    int levelDiff = attLevel - level;
//                    int subLevelDiff = attSubLevel - subLevel;
//                    if (subLevelDiff > 0) {
//                        damGoc += damGoc * (5 * subLevelDiff) / 100;
//                    }
//                    if (levelDiff > 0) {
//                        damGoc += damGoc * (10 * levelDiff) / 100;
//                    }
//                }
//            } else if (plInjure.isPl()) {
//                int level = plInjure.tuMa.level / 10;
//                int subLevel = plInjure.tuMa.level % 10;
//
//                int attLevel = (plAtt.tuMa != null && plAtt.tuMa.isTuMa()) ? (plAtt.tuMa.level / 10) : -1;
//                int attSubLevel = (plAtt.tuMa != null && plAtt.tuMa.isTuMa()) ? (plAtt.tuMa.level % 10) : -1;
//
//                if (attLevel < level) {
//                    int levelDiff = level - attLevel;
//                    damGoc -= damGoc * (10 + (20 * levelDiff)) / 100;
//                } else {
//                    int levelDiff = attLevel - level;
//                    int subLevelDiff = attSubLevel - subLevel;
//                    if (subLevelDiff > 0) {
//                        damGoc += damGoc * (10 * subLevelDiff) / 100;
//                    }
//                    if (levelDiff > 0) {
//                        damGoc += damGoc * (10 * levelDiff) / 100;
//                    }
//                }
//            }
//        }
//
//        return damGoc;
//    }

    private double subDameWithCanhGioi(Player plAtt, Player plInjure) {
        double dame = plAtt.nPoint.getDameAttack(false);

        int attLevel = getLevel(plAtt);
        int attSub = getSubLevel(plAtt);

        int targetLevel = getLevel(plInjure);
        int targetSub = getSubLevel(plInjure);

        if (attLevel < 0 || targetLevel < 0) return dame;

        if (attLevel < targetLevel) {
            int levelDiff = targetLevel - attLevel;
            dame -= dame * (90 * levelDiff) / 100;
        } else {
            int levelDiff = attLevel - targetLevel;
            int subDiff = attSub - targetSub;

            if (subDiff > 0) {
                dame += dame * (10 * subDiff) / 100;
            }
            if (levelDiff > 0) {
                dame += dame * (30 * levelDiff) / 100;
            }
        }
        return dame;
    }

    private double subDameWithCanhGioi(Player plAtt, Player plInjure, double dame) {
        int attLevel = getLevel(plAtt);
        int attSub = getSubLevel(plAtt);

        int targetLevel = getLevel(plInjure);
        int targetSub = getSubLevel(plInjure);

        if (attLevel < 0 || targetLevel < 0) return dame;

        if (attLevel < targetLevel) {
            int levelDiff = targetLevel - attLevel;
            dame -= dame * (90 * levelDiff) / 100;
        } else {
            int levelDiff = attLevel - targetLevel;
            int subDiff = attSub - targetSub;

            if (subDiff > 0) {
                dame += dame * (10 * subDiff) / 100;
            }
            if (levelDiff > 0) {
                dame += dame * (30 * levelDiff) / 100;
            }
        }
        return dame;
    }

    private int getLevel(Player p) {
        if (p.isBoss) return ((Boss) p).level;
        if (p.tuTien != null && p.tuTien.isTuTien()) return p.tuTien.level;
        if (p.tuMa != null && p.tuMa.isTuMa()) return p.tuMa.level / 10;
        return -1;
    }

    private int getSubLevel(Player p) {
        if (p.isBoss) return ((Boss) p).subLevel;
        if (p.tuTien != null && p.tuTien.isTuTien()) return p.tuTien.subLevel;
        if (p.tuMa != null && p.tuMa.isTuMa()) return p.tuMa.level % 10;
        return -1;
    }

    private void playerAttackPlayer(Player plAtt, Player plInjure, boolean miss, boolean isLinhCan) {
        if (plInjure.effectSkill.anTroi) {
            plAtt.nPoint.isCrit100 = true;
        }
        // handle for dame bosss
        double damGoc = subDameWithCanhGioi(plAtt, plInjure);
        miss = neDon(plInjure, miss);
        double dameHit = plInjure.injured(plAtt, miss ? 0 : damGoc, false, false, false);
        phanSatThuong(plAtt, plInjure, Util.DoubleGioihan(dameHit));
        hutHPMP(plAtt, dameHit, false);
        hutLinhKhi(plAtt);
        sendMessagePlayerAttackPlayer(plAtt, plInjure, dameHit, (byte) 0);
        /// handle for linh can
        if (isLinhCan && plAtt.isPl() && plAtt.tuTien.isTuTien() && plAtt.tuTien.isAttackWithLinhCan) {
            long linhKhiPoint = TuTien.BASE_LINH_KHI[plAtt.tuTien.level] / (Util.nextInt(500, 1000));
            linhKhiPoint *= Util.nextInt(1, 3);
            if (!plAtt.tuTien.canHandleWithLinhKhiPoint(linhKhiPoint)) {
                return;
            }
            short paramOfLinhCan = plAtt.tuTien.linhCan.getThuocTinhLinhCan().getParam();
            if (plAtt.luyenDanSu.isLuyenDan() && plAtt.luyenDanSu.danDuocEffect.isBuffSTLinhCan()) {
                paramOfLinhCan += plAtt.luyenDanSu.danDuocEffect.stLinhCanBuff;
            }
            switch (plAtt.tuTien.linhCan.getLinhCanType()) {
                case 0:
                    // kim
                    // gay sat thuong xuyen giap bung no
                    dameHit = plAtt.nPoint.getDameAttack(false);
                    double damKim = dameHit * paramOfLinhCan / 100;
                    // + them sat thuong bung no
                    damKim += linhKhiPoint; // 12500
                    damKim += (dameHit * (plAtt.tuTien.congPhap.phamchat.id + 1 + plAtt.tuTien.xParam)) / 100;
                    double dameK = plInjure.injured(plAtt, damKim, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameK, (byte) 1);
                    break;
                case 1:
                    // ty le gay choang
                    double dameMoc = dameHit * (paramOfLinhCan / 20f) / 100;
                    dameMoc += linhKhiPoint;
                    dameMoc *= (plAtt.tuTien.congPhap.phamchat.id + 1 + plAtt.tuTien.xParam);
                    dameMoc = subDameWithCanhGioi(plAtt, plInjure, dameMoc);
                    double dameM = plInjure.injured(plAtt, dameMoc, false, false, true);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameM, (byte) 1);
                    if (Util.isTrue((paramOfLinhCan / 10), 100) && Util.canDoWithTime(plInjure.effectSkill.lastTimeStartStun, 5000)) {
                        // gay choang cho doi thu
                        if (!plInjure.effectSkill.isStun) {
                            int timeStun = 2000;
                            EffectSkillService.gI().startStun(plInjure, System.currentTimeMillis(), timeStun);
                        }
                    }
                    // gay them sat thuong chuan dua tren sat thuong hien co
                    break;
                case 2:
                    // gay sat thuong dua tren max mp
                    double dameThuy = (plAtt.nPoint.mpMax) * (paramOfLinhCan * 1.2) / 100;
                    dameThuy += linhKhiPoint;
                    dameThuy *= (plAtt.tuTien.congPhap.phamchat.id + 1 + plAtt.tuTien.xParam);
                    dameThuy = subDameWithCanhGioi(plAtt, plInjure, dameThuy);
                    double dameT = plInjure.injured(plAtt, dameThuy, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameT, (byte) 1);
                    break;
                case 3:
                    double dameHoa = dameHit * ((plAtt.nPoint.numAttackLinhCan) * Math.max(1, plAtt.tuTien.xParam) * Math.max(1, plAtt.tuTien.congPhap.phamchat.id + 1)) / 100;
                    dameHoa += linhKhiPoint;
                    if (plAtt.nPoint.numAttackLinhCan + 1 <= paramOfLinhCan) {
                        plAtt.nPoint.numAttackLinhCan += 1;
                        if (plAtt.nPoint.numAttackLinhCan == paramOfLinhCan) {
                            Service.gI().chat(plAtt, "Viêm Bạo");
                            dameHoa *= 2;
//                            EffectSkillService.gI().addThieuDot(plInjure, plAtt);
                        }
                    } else {
                        plAtt.nPoint.numAttackLinhCan = paramOfLinhCan;
                    }
                    plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                    double dm = plInjure.injured(plAtt, dameHoa, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dm, (byte) 1);
                    break;
                case 4:
                    double dameTho = (plAtt.nPoint.hpMax) * (paramOfLinhCan * 1.2) / 100;
                    dameTho += linhKhiPoint;
                    dameTho *= (plAtt.tuTien.congPhap.phamchat.id + 1 + plAtt.tuTien.xParam);
                    dameTho = subDameWithCanhGioi(plAtt, plInjure, dameTho);
                    double dameTh = plInjure.injured(plAtt, dameTho, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameTh, (byte) 1);
                    break;
                case 5:
                    double damePhong = dameHit * (paramOfLinhCan * Math.max(1, plAtt.tuTien.xParam) * Math.max(1, plAtt.tuTien.congPhap.phamchat.id + 1)) / 100;
                    damePhong += linhKhiPoint;
                    byte maxTyLeChiMang = (byte) (Math.min(paramOfLinhCan, 100));
                    if (plAtt.nPoint.numAttackLinhCan + 5 <= maxTyLeChiMang) {
                        plAtt.nPoint.numAttackLinhCan += 5;
                        plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                    } else {
                        plAtt.nPoint.numAttackLinhCan = maxTyLeChiMang;
                        plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                    }
                    if (Util.isTrue(plAtt.nPoint.numAttackLinhCan, 100)) {
                        damePhong *= (2 + (paramOfLinhCan / 50f));
                    }
                    double dp = plInjure.injured(plAtt, damePhong, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dp, (byte) 1);
                    break;
                case 6:
                    double dameLoi = dameHit * (paramOfLinhCan * Math.max(1, plAtt.tuTien.xParam) * Math.max(1, plAtt.tuTien.congPhap.phamchat.id + 1)) / 100;
                    dameLoi += linhKhiPoint;
                    byte maxTlChiMang = (byte) (Math.min(paramOfLinhCan, 100));
                    if (plAtt.nPoint.numAttackLinhCan + 1 <= maxTlChiMang) {
                        plAtt.nPoint.numAttackLinhCan += 1;
                        plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                    } else {
                        plAtt.nPoint.numAttackLinhCan = maxTlChiMang;
                        plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                    }
                    if (Util.isTrue(plAtt.nPoint.numAttackLinhCan, 100)) {
                        dameLoi *= (2.5 + (paramOfLinhCan / 50f));
                    }
                    double dl = plInjure.injured(plAtt, dameLoi, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dl, (byte) 1);
                    if (Util.isTrue((paramOfLinhCan / 10), 100) && Util.canDoWithTime(plInjure.effectSkill.lastTimeStartStun, 5000)) {
                        // gay choang cho doi thu
                        if (!plInjure.effectSkill.isStun) {
                            int timeStun = 2000;
                            EffectSkillService.gI().startStun(plInjure, System.currentTimeMillis(), timeStun);
                        }
                    }
                    break;
                case 7:
                    if (plInjure.nPoint.getCurrPercentHP() < 5 && Util.isTrue(paramOfLinhCan, 200)) {
                        // pham phan
                        Service.gI().chat(plAtt, "Thần thánh thẩm phán");
                        double dameQuang = plInjure.nPoint.hp;
                        double dq = plInjure.injured(plAtt, dameQuang, false, false, true);
                        sendMessagePlayerAttackPlayer(plAtt, plInjure, dq, (byte) 1);
                    } else {
                        double dameQuang = plAtt.nPoint.getDameAttack(false) * (((paramOfLinhCan) / 200f) * Math.max(1, plAtt.tuTien.xParam)) / 100;
                        dameQuang += linhKhiPoint;
                        double dq = plInjure.injured(plAtt, dameQuang, false, false, true);
                        sendMessagePlayerAttackPlayer(plAtt, plInjure, dq, (byte) 1);
                    }
                    break;
                case 8:
                    double dameAm = dameHit * paramOfLinhCan / 100;
                    double dameA = plInjure.injured(plAtt, dameAm, false, false, true);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameA, (byte) 1);
                    // gay am anh tru hp theo giay
                    if (Util.isTrue(paramOfLinhCan / 10, 100)) {
                        EffectSkillService.gI().addAmAnh(plInjure, plAtt);
                    }
                    break;
            }
            plAtt.tuTien.subLinhKhi(linhKhiPoint);
        }
        if (isLinhCan && plAtt.isPl() && plAtt.tuMa.isTuMa() && plAtt.tuMa.isAttackWithLinhCan) {
            if (!plAtt.tuMa.canHandleWithMaKhiPoint(3)) {
                return;
            }
            float paramOfLinhCan = plAtt.tuMa.linhCanTuMa.xParam;
            switch (plAtt.tuMa.linhCanTuMa.typeLinhCan) {
                case 0:
                    double hp = plInjure.injured(plAtt, plInjure.nPoint.hpMax * (paramOfLinhCan / 50), false, false, true);
                    plAtt.nPoint.hutMauTamThoi += hp;
                    plAtt.nPoint.hpMax += plAtt.nPoint.hutMauTamThoi;
                    plAtt.nPoint.lastTimeHutMau = System.currentTimeMillis();
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, hp, (byte) 0);
                    break;
                case 1:
                    double dameA = plInjure.injured(plAtt, dameHit * paramOfLinhCan, false, false, true);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameA, (byte) 0);
                    break;
                case 2:
                    double dameB = (plInjure.nPoint.hpMax * paramOfLinhCan / 50) * Util.nextInt(2, 4);
                    dameB = plInjure.injured(plAtt, dameB, false, false, false);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameB, (byte) 0);
                    break;
                case 3:
                    double dameC = plInjure.injured(plAtt, dameHit * paramOfLinhCan, false, false, true);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameC, (byte) 0);
                    if (Util.isTrue(paramOfLinhCan * Util.nextInt(2, 3), Util.nextInt(100, 120))) {
                        if (!plInjure.effectSkill.isStun) {
                            EffectSkillService.gI().startStun(plInjure, System.currentTimeMillis(), 2000);
                        }
                    }
                    break;
                case 4:
                    // - hp cua ban than
                    double dame = plAtt.nPoint.hpMax * (paramOfLinhCan);
                    plAtt.nPoint.subHP(dame);
                    double dameD = plInjure.injured(plAtt, dame, false, false, true);
                    sendMessagePlayerAttackPlayer(plAtt, plInjure, dameD, (byte) 0);
                    break;
            }
            plAtt.tuMa.subMaKhi(Util.nextInt(10, 30));
        }
        // handle for huyet mach
    }

    private boolean neDon(Player plInjure, boolean miss) {
        if (plInjure.tuTien.isTuTien()) {
            short xParam = plInjure.tuTien.linhCan.getThuocTinhLinhCan().getParam();
            if (plInjure.tuTien.linhCan.getLinhCanType() == 2 || plInjure.tuTien.linhCan.getLinhCanType() == 5 || plInjure.tuTien.linhCan.getLinhCanType() == 8) {
                if (xParam > 80) {
                    xParam = 80;
                }
                miss = Util.isTrue(xParam, 100);
            }
        }
        return miss;
    }

    private void hutLinhKhi(Player plAtt) {
        if (plAtt.tuTien.isTuTien()) {
            short xParam = (short) (plAtt.tuTien.linhCan.getThuocTinhLinhCan().getParam() / 10);
            long baseHutLinhKhi = TuTien.BASE_LINH_KHI_HOI_PHUC[plAtt.tuTien.level];
            if (plAtt.tuTien.linhCan.getLinhCanType() == 1 || plAtt.tuTien.linhCan.getLinhCanType() == 2 || plAtt.tuTien.linhCan.getLinhCanType() == 4) {
                baseHutLinhKhi *= xParam;
            }
            plAtt.tuTien.hoiPhucLinhKhi(baseHutLinhKhi);
        }
    }

    public void sendMessagePlayerAttackPlayer(Player plAtt, Player plInjure, double dameHit, byte type) {
        Message msg;
        try {
            msg = new Message(-60);
            msg.writer().writeInt((int) plAtt.id); //id pem
            msg.writer().writeByte(type); // type attack
            if (type == 1) {
                msg.writer().writeByte(plAtt.tuTien.linhCan.getLinhCanType());
            }
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId); //skill pem
            msg.writer().writeByte(1); //số người pem
            msg.writer().writeInt((int) plInjure.id); //id ăn pem
            byte typeSkill = SkillUtil.getTyleSkillAttack(plAtt.playerSkill.skillSelect);
            msg.writer().writeByte(typeSkill == 2 ? 0 : 1); //read continue
            msg.writer().writeByte(0); //type skill
            msg.writer().writeDouble(Util.DoubleGioihang(dameHit)); //dame ăn
            msg.writer().writeBoolean(plInjure.isDie()); //is die
            msg.writer().writeBoolean(plAtt.nPoint.isCrit); //crit
            if (typeSkill != 1) {
                Service.getInstance().sendMessAllPlayerInMap(plAtt, msg);
                msg.cleanup();
            } else {
                plInjure.sendMessage(msg);
                msg.cleanup();
                msg = new Message(-60);
                msg.writer().writeInt((int) plAtt.id); //id pem
                msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId); //skill pem
                msg.writer().writeByte(1); //số người pem
                msg.writer().writeInt((int) plInjure.id); //id ăn pem
                msg.writer().writeByte(typeSkill == 2 ? 0 : 1); //read continue
                msg.writer().writeByte(0); //type skill
                msg.writer().writeDouble(Util.DoubleGioihang(dameHit)); //dame ăn
                msg.writer().writeBoolean(plInjure.isDie()); //is die
                msg.writer().writeBoolean(plAtt.nPoint.isCrit); //crit
                Service.getInstance().sendMessAnotherNotMeInMap(plInjure, msg);
                msg.cleanup();
            }
            try {
                msg = Service.getInstance().messageSubCommand((byte) 14);
                msg.writer().writeInt((int) plInjure.id);
                msg.writer().writeDouble(Util.DoubleGioihang(plInjure.nPoint.hp));
                msg.writer().writeByte(0);
                msg.writer().writeDouble(Util.DoubleGioihang(plInjure.nPoint.hpMax));
                Service.getInstance().sendMessAnotherNotMeInMap(plInjure, msg);
                msg.cleanup();
            } catch (Exception e) {

            }
            Service.getInstance().addSMTN(plInjure, (byte) 1, 1, false);
            if (plInjure.isDie() && !plAtt.isBoss) {
                plAtt.fightMabu.changePoint((byte) 5);
            }

        } catch (Exception e) {
            Logger.logException(SkillService.class, e);
        }
    }

    private void playerAttackMob(Player plAtt, Mob mob, boolean miss, boolean dieWhenHpFull) {
        if (!mob.isDie()) {
            double dameHit = plAtt.nPoint.getDameAttack(true);
//            neDon(plAtt, null, miss);
            hutLinhKhi(plAtt);
            if (plAtt.charms.tdBatTu > System.currentTimeMillis() && plAtt.nPoint.hp == 1) {
                dameHit = 0;
            }
            if (plAtt.charms.tdManhMe > System.currentTimeMillis()) {
                dameHit += (dameHit * 150 / 100);
            }
            if (plAtt.isPet) {
                if (((Pet) plAtt).charms.tdDeTu > System.currentTimeMillis()) {
                    dameHit *= 2;
                }
            }
            if (miss) {
                dameHit = 0;
            }
            if (mob.isSieuQuai()) {
                if (dameHit > mob.point.maxHp / 10) {
                    dameHit = mob.point.maxHp / 10;
                }
            }
            hutHPMP(plAtt, dameHit, true);
            sendPlayerAttackMob(plAtt, mob);
            mob.injured(plAtt, dameHit, dieWhenHpFull, (byte) 0);
            if (plAtt.tuTien.isTuTien() && plAtt.tuTien.canHandleWithLinhKhiPoint(1) && plAtt.tuTien.isAttackWithLinhCan) {
                short paramOfLinhCan = plAtt.tuTien.linhCan.getThuocTinhLinhCan().getParam();
                switch (plAtt.tuTien.linhCan.getLinhCanType()) {
                    case 0:
                        // kim
                        // calc % mau da mat de cong them dame
                        double percentHpLost = 100 - plAtt.nPoint.getCurrPercentHP();
                        byte percentBuff = 0;
                        if (percentHpLost > 0) {
                            percentBuff = (byte) Math.max(1, percentHpLost);
                        }
                        double dame = dameHit * ((Math.max(1, paramOfLinhCan / 3f) + percentBuff) * Math.max(1, plAtt.tuTien.xParam)) / 100;
                        mob.injured(plAtt, dame, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;
                    case 1, 8:
                        // ty le gay choang
                        if (Util.isTrue((paramOfLinhCan / 10), 100) && Util.canDoWithTime(mob.effectSkill.lastTimeStun, 5000)) {
                            // gay choang cho doi thu
                            if (!mob.effectSkill.isStun) {
                                int timeStun = 2000;
                                mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                            }
                        }
                        double dameM = mob.point.hp * paramOfLinhCan / 100;
                        mob.injured(plAtt, dameM, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;
                    case 2:
                        double dameThuy = plAtt.nPoint.mpMax * (paramOfLinhCan / 2f) * Math.max(1, plAtt.tuTien.xParam) / 100f;
                        mob.injured(plAtt, dameThuy, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;
                    case 3:
                        double dameHoa = dameHit * (plAtt.nPoint.numAttackLinhCan * Math.max(1, plAtt.tuTien.xParam)) / 100;
                        if (plAtt.nPoint.numAttackLinhCan + 2 <= paramOfLinhCan) {
                            plAtt.nPoint.numAttackLinhCan += 2;
                            plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                        } else {
                            plAtt.nPoint.numAttackLinhCan = paramOfLinhCan;
                            plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                        }
                        mob.injured(plAtt, dameHoa, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;
                    case 4:
                        double dameTho = plAtt.nPoint.hpMax * (paramOfLinhCan / 2f) * Math.max(1, plAtt.tuTien.xParam) / 100f;
                        mob.injured(plAtt, dameTho, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;
                    case 5:
                        double damePhong = dameHit * (paramOfLinhCan * Math.max(1, plAtt.tuTien.xParam)) / 100;
                        byte maxTyLeChiMang = (byte) (Math.min(paramOfLinhCan / 3, 100));
                        if (plAtt.nPoint.numAttackLinhCan + 5 <= maxTyLeChiMang) {
                            plAtt.nPoint.numAttackLinhCan += 5;
                            plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                        } else {
                            plAtt.nPoint.numAttackLinhCan = maxTyLeChiMang;
                            plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                        }
                        if (Util.isTrue(plAtt.nPoint.numAttackLinhCan, 100)) {
                            damePhong *= (2 + (paramOfLinhCan / 100f));
                        }
                        mob.injured(plAtt, damePhong, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;
                    case 6:
                        double dameLoi = dameHit * (paramOfLinhCan * Math.max(1, plAtt.tuTien.xParam)) / 100;
                        byte maxTlChiMang = (byte) (Math.min(paramOfLinhCan / 50, 100));
                        if (plAtt.nPoint.numAttackLinhCan + 5 <= maxTlChiMang) {
                            plAtt.nPoint.numAttackLinhCan += 5;
                            plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                        } else {
                            plAtt.nPoint.numAttackLinhCan = maxTlChiMang;
                            plAtt.nPoint.lastTimeNumAttackLinhCan = System.currentTimeMillis();
                        }
                        if (Util.isTrue(plAtt.nPoint.numAttackLinhCan, 100)) {
                            dameLoi *= (2.5 + (paramOfLinhCan / 100f));
                        }
                        mob.injured(plAtt, dameLoi, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        if (Util.isTrue((paramOfLinhCan / 10), 100) && Util.canDoWithTime(mob.effectSkill.lastTimeStun, 5000)) {
                            // gay choang cho doi thu
                            if (!mob.effectSkill.isStun) {
                                int timeStun = 2000;
                                mob.effectSkill.startStun(System.currentTimeMillis(), timeStun);
                            }
                        }
                        break;
                    case 7:
                        double dameQuang = plAtt.nPoint.getDameAttack(false) * (paramOfLinhCan * Math.max(1, plAtt.tuTien.xParam)) / 100;
                        mob.injured(plAtt, dameQuang, false, (byte) 1);
                        sendPlayerAttackMob(plAtt, mob);
                        break;

                }
            }
        }
    }

    private void sendPlayerPrepareSkill(Player player, int affterMiliseconds) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(4);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(affterMiliseconds);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public static void sendPlayerPrepareBom(Player player, int affterMiliseconds) {
        Message msg;
        try {
            msg = new Message(-45);
            msg.writer().writeByte(7);
            msg.writer().writeInt((int) player.id);
//            msg.writer().writeShort(player.playerSkill.skillSelect.skillId);
            msg.writer().writeShort(104);
            msg.writer().writeShort(affterMiliseconds);
            Service.getInstance().sendMessAllPlayerInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public boolean canUseSkillWithMana(Player player) {
        if (player.isDaoLu) {
            return true;
        }
        if (player.playerSkill.skillSelect != null) {
            if (player.playerSkill.skillSelect.template.id == Skill.KAIOKEN) {
                double hpUse = player.nPoint.hpMax / 100 * 2;
                if (player.nPoint.hp <= hpUse) {
                    return false;
                }
            }
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0:
                    if (player.nPoint.mp >= player.playerSkill.skillSelect.manaUse) {
                        return true;
                    } else {
                        return false;
                    }
                case 1:
                    double mpUse = player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100;
                    if (player.nPoint.mp >= mpUse) {
                        return true;
                    } else {
                        return false;
                    }
                case 2:
                    if (player.nPoint.mp > 0) {
                        return true;
                    } else {
                        return false;
                    }
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public boolean canUseSkillWithCooldown(Player player) {
        if (player.isDaoLu) {
            return true;
        }
        return Util.canDoWithTime(player.playerSkill.skillSelect.lastTimeUseThisSkill, player.playerSkill.skillSelect.coolDown - 50);
    }

    private void affterUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        switch (skillId) {
            case Skill.KAMEJOKO:
            case Skill.MASENKO:
            case Skill.ANTOMIC:
                if (player.isPl()) {
                    player.achievement.plusCount(4);
                }
                break;
            case Skill.DICH_CHUYEN_TUC_THOI:
                if (intrinsic.id == 6) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            case Skill.THOI_MIEN:
                if (intrinsic.id == 7) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            case Skill.SOCOLA:
                if (intrinsic.id == 14) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
            case Skill.TROI:
                if (intrinsic.id == 22) {
                    player.nPoint.dameAfter = intrinsic.param1;
                }
                break;
        }
        setMpAffterUseSkill(player);
        setLastTimeUseSkill(player, skillId);
    }

    private void setMpAffterUseSkill(Player player) {
        if (player.isDaoLu) {
            return;
        }
        if (player.playerSkill.skillSelect != null) {
            switch (player.playerSkill.skillSelect.template.manaUseType) {
                case 0:
                    if (player.nPoint.mp >= player.playerSkill.skillSelect.manaUse) {
                        player.nPoint.setMp(player.nPoint.mp - player.playerSkill.skillSelect.manaUse);
                    }
                    break;
                case 1:
                    double mpUse = player.nPoint.mpMax * player.playerSkill.skillSelect.manaUse / 100;
                    if (player.nPoint.mp >= mpUse) {
                        player.nPoint.setMp(player.nPoint.mp - mpUse);
                    }
                    break;
                case 2:
                    player.nPoint.setMp(0);
                    break;
            }
            PlayerService.gI().sendInfoHpMpMoney(player);
        }
    }

    public void setLastTimeUseSkill(Player player, int skillId) {
        Intrinsic intrinsic = player.playerIntrinsic.intrinsic;
        int subTimeParam = 0;
        switch (skillId) {
            case Skill.TRI_THUONG:
                if (intrinsic.id == 10) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.THAI_DUONG_HA_SAN:
                if (intrinsic.id == 3) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.QUA_CAU_KENH_KHI:
                if (intrinsic.id == 4) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.KHIEN_NANG_LUONG:
                if (intrinsic.id == 5 || intrinsic.id == 15 || intrinsic.id == 20) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.MAKANKOSAPPO:
                if (intrinsic.id == 11) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.DE_TRUNG:
                if (intrinsic.id == 12) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.TU_SAT:
                if (intrinsic.id == 19) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.HUYT_SAO:
                if (intrinsic.id == 21) {
                    subTimeParam = intrinsic.param1;
                }
                break;
            case Skill.BIEN_HINH_TD:
            case Skill.BIEN_HINH_NM:
            case Skill.BIEN_HINH_XD:
                subTimeParam = 1;
                break;
        }
        int coolDown = player.playerSkill.skillSelect.coolDown;
        player.playerSkill.skillSelect.lastTimeUseThisSkill = System.currentTimeMillis() - (coolDown * subTimeParam / 100);
        if (subTimeParam != 0) {
            Service.getInstance().sendTimeSkill(player);
        }
    }

    private boolean canHsPlayer(Player player, Player plTarget) {
        if (plTarget == null) {
            return false;
        }
        if (plTarget.isBoss) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_ALL) {
            return false;
        }
        if (plTarget.typePk == ConstPlayer.PK_PVP) {
            return false;
        }
        if (player.cFlag != 0) {
            if (plTarget.cFlag != 0 && plTarget.cFlag != player.cFlag) {
                return false;
            }
        } else if (plTarget.cFlag != 0) {
            return false;
        }
        return true;
    }

    private boolean canAttackPlayer(Player p1, Player p2) {
        if (p1.isDie() || p2.isDie()) {
            return false;
        }
        if (p1.zone.map.mapId == 129 && p1.typePk > 0 && p2.typePk > 0) {
            return true;
        }

        if (p1.typePk == ConstPlayer.PK_ALL || p2.typePk == ConstPlayer.PK_ALL) {
            return true;
        }
        if (p1.typePk == ConstPlayer.PK_PVP || p2.typePk == ConstPlayer.PK_PVP) {
            return true;
        }
        if ((p1.cFlag != 0 && p2.cFlag != 0) && (p1.cFlag == 8 || p2.cFlag == 8 || p1.cFlag != p2.cFlag)) {
            return true;
        }
        if (p1.pvp == null || p2.pvp == null) {
            return false;
        }
        if (p1.pvp.isInPVP(p2) || p2.pvp.isInPVP(p1)) {
            return true;
        }
        return false;
    }

    private void sendPlayerAttackMob(Player plAtt, Mob mob) {
        Message msg;
        try {
            msg = new Message(54);
            msg.writer().writeInt((int) plAtt.id);
            msg.writer().writeByte(plAtt.playerSkill.skillSelect.skillId);
            msg.writer().writeByte(mob.id);
            Service.getInstance().sendMessAllPlayerInMap(plAtt, msg);
            msg.cleanup();

        } catch (Exception e) {

        }
    }

    public void selectSkill(Player player, int skillId) {
        Skill skillBefore = player.playerSkill.skillSelect;
        for (Skill skill : player.playerSkill.skills) {
            if (skill.skillId != -1 && skill.template.id == skillId) {
                player.playerSkill.skillSelect = skill;
                if (skillBefore != null) {
                    switch (skillBefore.template.id) {
                        case Skill.DRAGON:
                        case Skill.KAMEJOKO:
                        case Skill.DEMON:
                        case Skill.MASENKO:
                        case Skill.LIEN_HOAN:
                        case Skill.GALICK:
                        case Skill.ANTOMIC:
                            switch (skill.template.id) {
                                case Skill.DRAGON:
                                case Skill.KAMEJOKO:
                                case Skill.DEMON:
                                case Skill.MASENKO:
                                case Skill.LIEN_HOAN:
                                case Skill.GALICK:
                                case Skill.ANTOMIC:
//                                skill.lastTimeUseThisSkill = System.currentTimeMillis() + (skill.coolDown / 100);
                                    break;
                            }
                            break;
                    }
                    break;
                }
            }
        }
    }
}

/**
 * Code được viết bởi HOÀNG VIỆT Vui lòng không sao chép mã nguồn này dưới mọi
 * hình thức.
 */
