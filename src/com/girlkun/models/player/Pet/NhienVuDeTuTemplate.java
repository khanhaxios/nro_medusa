package com.girlkun.models.player.Pet;

import com.girlkun.models.boss.BossID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhienVuDeTuTemplate {
    public static Map<String, List<NhiemVuDeTuPhu>> nhiemVuDeTus = new HashMap<>();

    public static String MABU_TASK = "MABU";

    public void initTemplate() {
        // nhiem vu cho de mabu
        List<NhiemVuDeTuPhu> MABU_LIST = new ArrayList<>();
        MABU_LIST.add(new NhiemVuDeTuPhu("Tìm kiếm # hồn đệ ma bư", 10, TaskType.KILL_BOSS, BossID.MABU));
        nhiemVuDeTus.put(MABU_TASK, MABU_LIST);
    }
}
