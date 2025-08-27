/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss.gas;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.ItemMapService;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0
 */
public class DrLyChee extends Boss {
    private static final int[][] FULL_DEMON = new int[][]{{Skill.DEMON, 1}, {Skill.DEMON, 2}, {Skill.DEMON, 3}, {Skill.DEMON, 4}, {Skill.DEMON, 5}, {Skill.DEMON, 6}, {Skill.DEMON, 7}};
    private long lastTimeHapThu;
    private int timeHapThu;
    private final long lastUpdate = System.currentTimeMillis();
    private long timeJoinMap;
    private final int levell;
    private final int initSuper = 0;
    protected Player playerAtt;
    private final int timeLive = 200000000;

    public DrLyChee(Zone zone, short level, int dame, int hp, int id) throws Exception {
        super(id, new BossData(
                "DrLyChee", //name
                ConstPlayer.TRAI_DAT, //gender
                new short[]{1309, 1310, 1311, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((1000000 * level)), //dame
                new double[]{((1_000_000_000 * level))}, //hp
                new int[]{148}, //map join
                new int[][]{
                        {Skill.LIEN_HOAN, 7, 300},
                        {Skill.THOI_MIEN, 7, 30000},
                        {Skill.BIEN_KHI, 7, 150000},
                        {Skill.TROI, 7, 30000},
                        {Skill.KAMEJOKO, 7, 5000},},
                new String[]{}, //text chat 1
                new String[]{"|-1|Nhóc con"}, //text chat 2
                new String[]{}, //text chat 3
                60,
                (byte) 6, (byte) 10
        ));
        this.nPoint.setBasePoint();
        this.nPoint.setFullHpMp();
        this.zone = zone;
        this.levell = level;
    }

    @Override
    public void reward(Player plKill) {
        if (levell > 0 && levell < 100) {
            // roi long thạch
            Service.gI().dropItemMap(zone, ItemMapService.gI().createItemMapFromItem(zone, 1, plKill.location.x, zone.map.yPhysicInTop(plKill.location.x, plKill.location.y - 24), plKill.id, ItemService.gI().createNewItem((short) 2098)));
        }
        if (levell > 100 && levell < 250) {
            Service.gI().dropItemMap(zone, ItemMapService.gI().createItemMapFromItem(zone, 10, plKill.location.x, zone.map.yPhysicInTop(plKill.location.x, plKill.location.y - 24), plKill.id, ItemService.gI().createNewItem((short) 2098)));
        }
        if (levell > 250 && levell <= 500) {
            Service.gI().dropItemMap(zone, ItemMapService.gI().createItemMapFromItem(zone, 20, plKill.location.x, zone.map.yPhysicInTop(plKill.location.x, plKill.location.y - 24), plKill.id, ItemService.gI().createNewItem((short) 2098)));
        }
        if (Util.isTrue(10, 100)) {
            Service.gI().dropItemMap(zone, ItemMapService.gI().createItemMapFromItem(zone, Util.nextInt(1, 3), plKill.location.x, zone.map.yPhysicInTop(plKill.location.x, plKill.location.y - 24), plKill.id, ItemService.gI().createNewItem((short) Util.nextInt(2084, 2097))));
        }
    }

    @Override
    public void active() {
        super.active();
    }

    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}







