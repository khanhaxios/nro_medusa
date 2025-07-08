package com.girlkun.models.player.phapbao;

import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PhapBaoFactory {
    public static final String[] phapBaoNames = {"[Khôi]", // 0
            "[Thủ]",  // 1
            "[Bộ]",   // 2
            "[Y]"     // 3
    };
    public static final String[] vuKhiNames = {"[Thương]",      // 0
            "[Kiếm]",        // 1
            "[Đao]",         // 2
            "[Chùy]",        // 3
            "[Phủ]",         // 4
            "[Côn]",         // 5
            "[Bổng]",        // 6
            "[Tiên]",        // 7
            "[Tiêu]",    // 8
            "[Ám]",      // 9
            "[Trượng]", // 12
            "[Châu]",   // 13
            "[Phù]",    // 14
    };

    public static final String[] tenPhapBao = {
            // Nhóm bá đạo - chiến khí
            "Vô Song", "Chí Tôn", "Độc Tôn", "Thị Huyết", "Tuyệt Thế", "Bá Vương", "Huyết Ảnh", "Diệt Thần", "Sát Lục", "Trảm Ma", "Thánh Diệt", "Phá Thiên", "Hủy Diệt", "Diệt Cốt", "Bất Diệt", "Ám Ma", "Hắc Nhật", "Diệt Hồn", "Vong Linh", "Vô Ảnh",

            // Nhóm cổ phong - thần thoại
            "Vạn Cổ", "Trường Sinh", "Vô Thường", "U Minh", "Bất Chu", "Linh Tiêu", "Bích Hải", "Thiên Vận", "Huyền Vũ", "Thần Uy", "Tịch Diệt", "Vô Ngã", "Hư Vô", "Mê Tung", "Tàn Dương", "Tử Quang", "Nguyệt Ảnh", "Phong Tuyết", "Lưu Quang", "Huyễn Diệt",

            // Nhóm huyền bí - linh lực
            "Linh Hỏa", "Tâm Ảnh", "Mộng Huyễn", "Dị Sát", "Địa Luân", "Tinh Tâm", "Trảm Sát", "Đọa Hồn", "Dẫn Lộ", "Truy Ảnh"};

    public static PhapBao createRandomVuKhi(Player player) {
        int slOption = 2;
        PhapBao phapBao = new PhapBao(player);
        phapBao.initBase();
        phapBao.setType((byte) Util.nextInt(0, 4));
        phapBao.setSubType((byte) -1);
        if (phapBao.getType() == 4) {
            phapBao.setSubType((byte) Util.nextInt(0, vuKhiNames.length - 1));
        }
        phapBao.setName(tenPhapBao[Util.nextInt(0, tenPhapBao.length - 1)]);
        // random option
        if (Util.isTrue(10, 100)) {
            slOption = 3;
        }
        if (Util.isTrue(5, 200)) {
            slOption = 4;
        }

        if (Util.isTrue(1, 350)) {
            slOption = 5;
        }
        Set<Integer> usedOptionIds = new HashSet<>(); // để lưu các idOption đã được chọn
        List<Item.ItemOption> options = new ArrayList<>();
        while (options.size() < slOption) {
            int idOption = -1;
            if (Util.isTrue(1, 100)) {
                idOption = PhapBao.OPTION_SSS_VIP_CAN_ROLL[Util.nextInt(0, PhapBao.OPTION_SSS_VIP_CAN_ROLL.length - 1)];
            } else if (Util.isTrue(10, 100)) {
                idOption = PhapBao.OPTION_VIP_CAN_ROLL[Util.nextInt(0, PhapBao.OPTION_VIP_CAN_ROLL.length - 1)];
            } else {
                idOption = PhapBao.OPTION_CAN_ROLL[Util.nextInt(0, PhapBao.OPTION_CAN_ROLL.length - 1)];
            }

            if (usedOptionIds.contains(idOption)) {
                continue; // đã có option này rồi, random lại
            }

            usedOptionIds.add(idOption);
            int param = getParam(idOption, slOption);
            Item.ItemOption itemOption = new Item.ItemOption(idOption, param);
            options.add(itemOption);
        }
        // sort
        options.sort((o1, o2) -> {
            String name1 = o1.optionTemplate.name != null ? o1.optionTemplate.name : "";
            String name2 = o2.optionTemplate.name != null ? o2.optionTemplate.name : "";
            return Integer.compare(name1.length(), name2.length());
        });
        phapBao.options = options;
        return phapBao;
    }

    public static Item.ItemOption rollNewOption(int slOption) {
        int idOption;
        if (Util.isTrue(1, 100)) {
            idOption = PhapBao.OPTION_SSS_VIP_CAN_ROLL[Util.nextInt(0, PhapBao.OPTION_SSS_VIP_CAN_ROLL.length - 1)];
        } else if (Util.isTrue(10, 100)) {
            idOption = PhapBao.OPTION_VIP_CAN_ROLL[Util.nextInt(0, PhapBao.OPTION_VIP_CAN_ROLL.length - 1)];
        } else {
            idOption = PhapBao.OPTION_CAN_ROLL[Util.nextInt(0, PhapBao.OPTION_CAN_ROLL.length - 1)];
        }
        return new Item.ItemOption(idOption, getParam(idOption, slOption));
    }

    public static int getParam(int id, int slOption) {
        if (id == 0) {
            return Util.nextInt(10000, 50000) * slOption;
        }
        if (id == 2) {
            return Util.nextInt(30, 50) * slOption;
        }
        if (id == 6 || id == 7) {
            return Util.nextInt(30000, 50000) * slOption;
        }
        if (id == 14) {
            return Util.nextInt(1, 5);
        }
        return Util.nextInt(1, 3) * slOption;
    }
}
