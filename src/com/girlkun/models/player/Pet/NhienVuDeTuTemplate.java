package com.girlkun.models.player.Pet;

import com.girlkun.models.boss.BossID;
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
            I = new NhienVuDeTuTemplate();
        }
        return I;
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

        Logger.log("Init nhiệm vụ đệ tử thành công\n");
    }
}
