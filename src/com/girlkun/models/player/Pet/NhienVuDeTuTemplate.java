/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */
package com.girlkun.models.player.Pet;

import com.girlkun.models.boss.BossID;
import com.girlkun.models.player.NPoint;
import com.girlkun.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhienVuDeTuTemplate {
    public static Map<Integer, List<NhiemVuDeTuPhu>> nhiemVuDeTus = new HashMap<>();

    private static NhienVuDeTuTemplate I;

    public static NhienVuDeTuTemplate getI() {
        if (I == null) {
            synchronized (NhienVuDeTuTemplate.class) {
                if (I == null) {
                    I = new NhienVuDeTuTemplate();
                }
            }
        }
        return I;
    }

    public static List<NhiemVuDeTuPhu> getNhiemVu(int key) {
        List<NhiemVuDeTuPhu> list = nhiemVuDeTus.get(key);
        List<NhiemVuDeTuPhu> cloneList = new ArrayList<>();
        if (list != null) {
            for (NhiemVuDeTuPhu nv : list) {
                cloneList.add(nv.clone());
            }
        }
        return cloneList;
    }

    public void initTemplate() {
        // nhiem vu cho de mabu
        List<NhiemVuDeTuPhu> MABU_LIST = new ArrayList<>();
        MABU_LIST.add(new NhiemVuDeTuPhu("Tiêu Diệt Ma nhân Bư", "Tìm kiếm và tiêu diệt # lần Boss Ma Nhân Bư", 10, TaskType.KILL_BOSS, BossID.MABU));
        MABU_LIST.add(new NhiemVuDeTuPhu("Thử Thách Thể Lực", "Hãy chạy # mét", 2000000, TaskType.RUN, -1));
        MABU_LIST.add(new NhiemVuDeTuPhu("Tìm Kiếm Nguyên Liệu", "Hãy tìm # huyết đan để nuôi trứng Ma Nhân Bư", 1000, TaskType.FIND_ITEM, 2077));
        MABU_LIST.add(new NhiemVuDeTuPhu("Luyện Tập Gian Khổ", "Hãy tìm và tiêu diệt # Mộc Nhân", 9999, TaskType.KILL_MOB, 0));
        MABU_LIST.add(new NhiemVuDeTuPhu("Tìm Kiếm Ma Nhân Bư Chuyển Thế", "Hãy đi tìm chuyển thế của Ma Nhân Bư là Boss HẮC ÁM HỦY DIỆT và tiêu diệt # lần", 10, TaskType.KILL_BOSS, BossID.BOSS_HAC));
        MABU_LIST.add(new NhiemVuDeTuPhu("Tìm kiếm nguyên liệu hiếm", "Hãy tham gia bản đồ kho báu # lần", 50, TaskType.BDKB, -1));
        MABU_LIST.add(new NhiemVuDeTuPhu("May mắn Nghịch Thiên", "Hãy tham gia vòng quay may mắn trên thần mèo # lần", 20, TaskType.PLAY_LUCKY_ROUND, -1));
        MABU_LIST.add(new NhiemVuDeTuPhu("Luyện Tập Khí", "Dùng # Lần Skill Gây Sát Thương", 2000000, TaskType.USE_SKILL, 1));
        MABU_LIST.add(new NhiemVuDeTuPhu("Đột Phá Giới Hạn", "Chuyển Sinh # Lần", 10, TaskType.CHUYEN_SINH, -1));
        MABU_LIST.add(new NhiemVuDeTuPhu("Nhiệm vụ cuối cùng", "Đột phá cảnh giới # lần", 6, TaskType.DOT_PHA_CANH_GIOI, -1));
        nhiemVuDeTus.put(PetTaskType.MABU.getTaskKey(), MABU_LIST);
        List<NhiemVuDeTuPhu> BERRUS_LIST = new ArrayList<>();
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Linh hồn Berrus", "Tìm kiếm # hồn Berrus", 20, TaskType.FIND_ITEM, 1108));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Học trò Thiên Sứ", "Tìm và đánh bại Thiên Sứ Wish # lần", 20, TaskType.KILL_BOSS, BossID.THIEN_SU_WHIS));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Huynh Đệ Tương Ái", "Tìm và đánh bại thần hủy diệt Champa # lần", 20, TaskType.KILL_BOSS, BossID.THAN_HUY_DIET_CHAMPA));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Kế thừa thần vị", "Đạt cảnh giới chức nghiệp cấp #", 50, TaskType.DAT_CANH_GIOI, -1));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Học sức mạnh Hủy Diệt", "Dùng Kỹ Năng Đặc Biệt # lần", 200, TaskType.USE_SKILL, 4));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Rèn Luyện Cơ Sở", "Chuyển Sinh # lần", 10, TaskType.CHUYEN_SINH, -1));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Rèn Luyện Thể Lực", "Chạy # mét", 300000, TaskType.RUN, -1));
        BERRUS_LIST.add(new NhiemVuDeTuPhu("Siêu Cấp Sư Phụ", "Đệ tử mở giới hạn sức mạnh # lần", NPoint.MAX_LIMIT, TaskType.MO_GIOI_HAN, -1));
        nhiemVuDeTus.put(PetTaskType.BERRUS.getTaskKey(), BERRUS_LIST);
        Logger.log("Init nhiệm vụ đệ tử thành công\n");
    }
}
