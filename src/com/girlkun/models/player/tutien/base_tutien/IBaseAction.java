/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.tutien.base_tutien;

import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;

public interface IBaseAction {
    long getExpCanGain(Mob targetMob);

    void levelUp();

    void levelDown();

    void resetLevel();

    float getLevelUpPercent();

    void openSystem();

    boolean canLevelUp();

    String getName();

    String getCurrentExpAsString();

    float getDameBuff();

    float getHPMPBuff();

    float getDefBuff();

    float getPSTBuff();

    float getHutHPBuff();

    float getHutMPBuff();

    float getNeBuff();

    float getChinhXacBuff();

}
