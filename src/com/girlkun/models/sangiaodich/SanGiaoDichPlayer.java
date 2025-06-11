package com.girlkun.models.sangiaodich;

import com.girlkun.models.player.Player;
import lombok.Data;

@Data
public class SanGiaoDichPlayer{
    private Player player;
    private int playerId;
    private int totalHold;
    private long lastTimeUpdate;
    // action for player
    // throw bua zeno to san
    // rut bua zeno tu san
}
