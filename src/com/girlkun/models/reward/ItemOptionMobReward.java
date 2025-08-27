/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.reward;

import com.girlkun.models.Template;
import com.girlkun.server.Manager;
import com.girlkun.utils.Logger;
import lombok.Data;


@Data
public class ItemOptionMobReward {

    private Template.ItemOptionTemplate temp;
    private int[] param;
    private int[] ratio;

    public ItemOptionMobReward(int tempId, int[] param, int[] ratio) {

        this.temp = Manager.ITEM_OPTION_TEMPLATES.get(tempId);
        if (tempId == 232 && temp != null) {
            Logger.log(temp.toString());
        }
        this.param = param;
        if (this.param[0] < 0) {
            this.param[0] = -this.param[0];
        } else if (this.param[0] == 0) {
            this.param[0] = 1;
        }
        if (this.param[1] < 0) {
            this.param[1] = -this.param[1];
        } else if (this.param[1] == 0) {
            this.param[1] = 1;
        }
        if (this.param[0] > this.param[1]) {
            int tempSwap = this.param[0];
            this.param[0] = this.param[1];
            this.param[1] = tempSwap;
        }
        this.ratio = ratio;
    }

}





















