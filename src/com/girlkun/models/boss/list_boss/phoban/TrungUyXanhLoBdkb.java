/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.boss.list_boss.phoban;

import com.girlkun.consts.ConstPlayer;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.bdkb.BanDoKhoBauService;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 * @author BTH sieu cap vippr0
 */
public class TrungUyXanhLoBdkb extends Boss {

    private final int levell;
    protected Player playerAtt;

    public TrungUyXanhLoBdkb(Zone zone, int level, double dame, double hp, int id) throws Exception {
        super(id, new BossData(
                "Trung Úy Xanh Lơ", //name
                ConstPlayer.TRAI_DAT, //gender
                new short[]{135, 136, 137, -1, -1, -1}, //outfit {head, body, leg, bag, aura, eff}
                ((long) ((10000L + dame) * level)), //dame
                new double[]{(hp) * level}, //hp
                new int[]{137}, //map join
                new int[][]{
                        {Skill.LIEN_HOAN, 7, 300},
                        {Skill.KAMEJOKO, 7, 5000},
                        {Skill.TAI_TAO_NANG_LUONG, 6, 30000},
                        {Skill.DE_TRUNG, 6, 250000},
                },
                new String[]{"|-1|Kho báu ở đây là của ta"}, //text chat 1
                new String[]{"|-1|Nhóc con"}, //text chat 2
                new String[]{"|-1|Ta sẽ tiêu diệt tất cả bang hội ngươi"}, //text chat 3
                60,
                (byte) (level / 50),
                (byte) 10
        ));
        this.zone = zone;
        this.levell = level;
    }

    @Override
    public void reward(Player plKill) {
        if (levell == 110) {
            int a = 0;
            for (int i = 0; i < 40; i++) {
                ItemMap it = new ItemMap(this.zone, 861, 100, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
            }
        } else if (levell > 99 && levell < 110) {
            int a = 0;
            for (int i = 0; i < 40; i++) {
                ItemMap it = new ItemMap(this.zone, 861, 150, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
            }
        } else if (levell > 100) {
            int a = 0;
            for (int i = 0; i < 40; i++) {
                ItemMap it = new ItemMap(this.zone, 861, 250, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                        this.location.y - 24), -1);
                Service.getInstance().dropItemMap(this.zone, it);
                a += 10;
            }
        }
        if (levell <= 50) {
            // roi ruong bac
            ItemMap it = new ItemMap(this.zone, 573, Util.nextInt(1, 5), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        if (levell > 50 && levell <= 100) {
            ItemMap it = new ItemMap(this.zone, 574, Util.nextInt(1, 5), this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), -1);
            Service.getInstance().dropItemMap(this.zone, it);
        }
        if (levell > 100 && levell <= 250) {
            Item item = ItemService.gI().createNewItem(571, Util.nextInt(1, 5));
            InventoryServiceNew.gI().addItemBag(plKill, item);
            InventoryServiceNew.gI().sendItemBags(plKill);
        }
        if (levell > 250 && levell <= 500) {
            Item item = ItemService.gI().createNewItem(572, 1);
            InventoryServiceNew.gI().addItemBag(plKill, item);
            InventoryServiceNew.gI().sendItemBags(plKill);
        }
        if (plKill.nhiemVuDeTu != null) {
            plKill.nhiemVuDeTu.checkDoneTaskBanDoKhoBau();
        }
        BanDoKhoBauService.gI().setTimeOutMap(plKill, 20);
    }

    @Override
    public void active() {
        super.active();
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
