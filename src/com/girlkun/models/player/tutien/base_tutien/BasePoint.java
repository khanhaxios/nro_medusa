package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.player.Player;
import com.girlkun.services.PlayerService;

public abstract class BasePoint {
    public Player player;
    public boolean isOpenSystem;
    public byte level;
    public long linhKhiPoint;
    public long maxLinhKhiPoint;
    public byte subLevel;

    public long exp;
    public long maxExp;
    public int canCot = 0;
    public int ngoTinh = 0;
    public int thienPhu = 0;

    public BasePoint(Player player) {
        this.level = 0;
        this.subLevel = 0;
        this.exp = 0;
        this.maxExp = 0;
        this.player = player;
    }

    public BasePoint(byte level, byte subLevel, long exp, long maxExp) {
        this.level = level;
        this.subLevel = subLevel;
        this.exp = exp;
        this.maxExp = maxExp;
    }

    public void gainExp(long exp) {
        this.exp += exp;
        checkLevelUp();
    }

    public boolean isOpenSystem() {
        return isOpenSystem;
    }

    public void setOpenSystem(boolean openSystem) {
        isOpenSystem = openSystem;
    }

    public float getXDiemNgoTinh() {
        return ngoTinh / 100f;
    }

    public float getXDiemCanCot() {
        return canCot / 200f;
    }

    public float getXDiemThienPhu() {
        return Math.max(1, getXDiemCanCot() + getXDiemNgoTinh());
    }

    public void checkLevelUp() {
        if (this.exp - maxExp >= 0) {
            this.level += 1;
            restExp();
        }
    }

    public void restExp() {
        this.exp = 0;
        this.maxExp = getNextLevelExp();
    }

    protected long getNextLevelExp() {
        return (level + 1) * 100;
    }

    public byte getLevel() {
        return level;
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public boolean canHandleWithLinhKhiPoint(int percent) {
        return linhKhiPoint - (maxLinhKhiPoint * percent / 100) > 0;
    }

    public boolean canHandleWithLinhKhiPoint(long linhkhi) {
        return linhKhiPoint - linhkhi >= 0;
    }

    public void subLinhKhi(long linhKhiPoint) {
        this.linhKhiPoint -= linhKhiPoint;
        if (this.linhKhiPoint < 0) {
            this.linhKhiPoint = 0;
        }
    }

    public void addLinhKhi(long point) {
        this.linhKhiPoint += point;
        if (this.linhKhiPoint > maxLinhKhiPoint) this.linhKhiPoint = maxLinhKhiPoint;
    }

    public byte getSubLevel() {
        return subLevel;
    }

    public void setSubLevel(byte subLevel) {
        this.subLevel = subLevel;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public long getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(long maxExp) {
        this.maxExp = maxExp;
    }

    public long getLinhKhiPoint() {
        return linhKhiPoint;
    }

    public void setLinhKhiPoint(long linhKhiPoint) {
        this.linhKhiPoint = linhKhiPoint;
    }

    public long getMaxLinhKhiPoint() {
        return maxLinhKhiPoint;
    }

    public int getCanCot() {
        return canCot;
    }

    public void setCanCot(int canCot) {
        this.canCot = canCot;
    }

    public int getNgoTinh() {
        return ngoTinh;
    }

    public void setNgoTinh(int ngoTinh) {
        this.ngoTinh = ngoTinh;
    }

    public int getThienPhu() {
        return thienPhu;
    }

    public void setThienPhu(int thienPhu) {
        this.thienPhu = thienPhu;
    }

    public void setMaxLinhKhiPoint(long maxLinhKhiPoint) {
        this.maxLinhKhiPoint = maxLinhKhiPoint;
    }

    public void subLinhKhiPercent(int i) {
        linhKhiPoint -= maxLinhKhiPoint * i / 100;
        if (linhKhiPoint < 0) {
            linhKhiPoint = 0;
        }
        PlayerService.gI().sendLinhKhiPoint(player);
    }
}
