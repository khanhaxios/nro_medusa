package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;

public interface IBaseAction {
    public long getExpCanGain(Mob targetMob);

    public void levelUp();

    public void levelDown();

    public void resetLevel();

    public float getLevelUpPercent();

    public void openSystem();

    public boolean canLevelUp();

    public String getName();

    public String getCurrentExpAsString();

    public float getDameBuff();

    public float getHPMPBuff();

    public float getDefBuff();

    public float getPSTBuff();

    public float getHutHPBuff();

    public float getHutMPBuff();

    public float getNeBuff();

    public float getChinhXacBuff();

}
