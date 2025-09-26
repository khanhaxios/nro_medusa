package com.girlkun.models.player.the_chat;

import java.util.ArrayList;
import java.util.List;

public class TheChatOption {
    public static List<TheChatOption> TC_OPTIONS = new ArrayList<>();
    public String name;
    public int id;
    public int param;

    public String getName() {
        return name.replace("#", String.valueOf(param));
    }

    public TheChatOption(int id, String name, int param) {
        this.id = id;
        this.name = name;
        this.param = param;
    }

    public TheChatOption() {

    }

    public TheChatOption(int id, String name) {
        this.name = name;
        this.id = id;
    }

    public static void initOptionTemplate() {
        TC_OPTIONS.clear();
        TC_OPTIONS.add(new TheChatOption(0, "Tăng # Tấn Công"));
        TC_OPTIONS.add(new TheChatOption(1, "Tăng # HP,MP"));
        TC_OPTIONS.add(new TheChatOption(2, "Tăng # Miễn Thương"));
        TC_OPTIONS.add(new TheChatOption(3, "Tăng # Miễn Thương Chuẩn"));
        TC_OPTIONS.add(new TheChatOption(4, "Tăng # Tốc độ tu luyện"));
        TC_OPTIONS.add(new TheChatOption(5, "Tăng #% SD,HP,KI"));
    }

    public static TheChatOption init(int id, int param) {
        TheChatOption theChatOption = TC_OPTIONS.get(id);
        TheChatOption theChatOption1 = new TheChatOption(theChatOption.id, theChatOption.name);
        theChatOption1.param = param;
        return theChatOption1;
    }
}
