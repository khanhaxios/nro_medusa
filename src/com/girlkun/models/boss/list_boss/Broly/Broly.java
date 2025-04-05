package com.girlkun.models.boss.list_boss.Broly;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossStatus;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.boss.list_boss.cell.SieuBoHung;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.utils.SkillUtil;
import com.girlkun.utils.Util;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Broly extends Boss {

    private long lastTimeHP;
    private int timeHP;
    public Broly() throws Exception {
        super(BossID.BROLY , BossesData.BROLY);
    }
    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        try {
            this.HoiPhuc();
        } catch (Exception ex) {
                System.out.println("        loi broly");
            Logger.getLogger(SieuBoHung.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void HoiPhuc() throws Exception {
        if (!Util.canDoWithTime(this.lastTimeHP, this.timeHP)) {
            return;
        }
        Player pl = this.zone.getRandomPlayerInMap();
        if (pl == null || pl.isDie()) {
            return;
        }
        if(this.nPoint.hp < this.nPoint.hpg * 50/100){
            this.nPoint.dameg = Util.DoubleGioihan(this.nPoint.hpg /1000);
            this.nPoint.hpg += (this.nPoint.hpg * 300 / 100);
            this.nPoint.critg++;
            this.nPoint.calPoint();
            PlayerService.gI().hoiPhuc(this, this.nPoint.hp, 0);
            Service.gI().sendThongBao(pl, "Tên broly hắn lại tăng sức mạnh rồi!");
            this.chat(2, "Mọi người cẩn thận sức mạnh hắn ta tăng đột biến..");
            this.chat("Graaaaaa...");
            this.lastTimeHP = System.currentTimeMillis();
            this.timeHP = Util.nextInt(15000, 20000);
        }
    }
    

    private long st;
    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st= System.currentTimeMillis();
    }
    @Override
    public void moveToPlayer(Player player) {
        this.moveTo(player.location.x, player.location.y);
    }
    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon - plAtt.nPoint.tlchinhxac, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = 1;
            }
            if (damage > 1) {
                    damage = nPoint.hpMax/50;
                }
            this.nPoint.subHP(damage);
            if (isDie()) {
                try {
                    if(this.nPoint.hpMax >= 2000000){
                    new SuperBroly(this.zone ,(int) 1,(int) 1,BossID.SP_BROLY);
                    }
                } catch (Exception ex) {
                    System.out.println("     Loi ra Super Broly");
                }
                    this.setDie(plAtt);
                    die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }
}





















