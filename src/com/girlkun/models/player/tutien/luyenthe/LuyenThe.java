package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.base_tutien.BasePoint;
import com.girlkun.models.player.tutien.base_tutien.IBaseAction;

public class LuyenThe extends BasePoint implements IBaseAction {

    public LuyenThe(Player player) {
        super(player);
    }

    @Override
    public long getExpCanGain(Mob targetMob) {
        return 0;
    }

    @Override
    public void levelUp() {

    }

    @Override
    public void levelDown() {

    }

    @Override
    public void resetLevel() {

    }

    @Override
    public float getLevelUpPercent() {
        return 0;
    }

    @Override
    public void openSystem() {

    }

    @Override
    public boolean canLevelUp() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getCurrentExpAsString() {
        return null;
    }

    @Override
    public float getDameBuff() {
        return 0;
    }

    @Override
    public float getHPMPBuff() {
        return 0;
    }

    @Override
    public float getDefBuff() {
        return 0;
    }

    @Override
    public float getPSTBuff() {
        return 0;
    }

    @Override
    public float getHutHPBuff() {
        return 0;
    }

    @Override
    public float getHutMPBuff() {
        return 0;
    }

    @Override
    public float getNeBuff() {
        return 0;
    }

    @Override
    public float getChinhXacBuff() {
        return 0;
    }
}
