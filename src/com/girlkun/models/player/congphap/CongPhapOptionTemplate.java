/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import java.util.ArrayList;
import java.util.List;

public class CongPhapOptionTemplate {
    public static List<CongPhapOption> congPhapTienOptions = new ArrayList<>();

    public static List<CongPhapOption> congPhapLuyenTheOptions = new ArrayList<>();

    public static void initTemplate() {
        congPhapTienOptions.add(new CongPhapOption(0, "Tăng #% tốc độ hồi linh khí"));
        congPhapTienOptions.add(new CongPhapOption(1, "Tăng #% căn cốt"));
        congPhapTienOptions.add(new CongPhapOption(2, "Tăng #% giảm hồi chiêu"));
        congPhapTienOptions.add(new CongPhapOption(3, "Tăng #% ngộ tính"));
        congPhapTienOptions.add(new CongPhapOption(4, "Tăng # căn cốt"));
        congPhapTienOptions.add(new CongPhapOption(5, "Tăng # ngộ tính"));
        congPhapTienOptions.add(new CongPhapOption(6, "Tăng # linh khí hồi mỗi lần"));
        congPhapTienOptions.add(new CongPhapOption(7, "Tăng #% Linh khí hồi mỗi lần"));
        congPhapTienOptions.add(new CongPhapOption(8, "Tăng # sát thương linh căn"));
        congPhapTienOptions.add(new CongPhapOption(9, "Tăng #% sát thương linh căn"));

        congPhapTienOptions.add(new CongPhapOption(10, "Tăng # sát thương kim"));
        congPhapTienOptions.add(new CongPhapOption(11, "Tăng # sát thương mộc"));
        congPhapTienOptions.add(new CongPhapOption(12, "Tăng # sát thương thủy"));
        congPhapTienOptions.add(new CongPhapOption(13, "Tăng # sát thương hỏa"));
        congPhapTienOptions.add(new CongPhapOption(14, "Tăng # sát thương thổ"));
        congPhapTienOptions.add(new CongPhapOption(15, "Tăng # sát thương phong"));
        congPhapTienOptions.add(new CongPhapOption(16, "Tăng # sát thương lôi"));
        congPhapTienOptions.add(new CongPhapOption(17, "Tăng # sát thương quang"));
        congPhapTienOptions.add(new CongPhapOption(18, "Tăng # sát thương ám"));
        congPhapTienOptions.add(new CongPhapOption(19, "Tăng # tu vi nhận được"));
        congPhapTienOptions.add(new CongPhapOption(20, "Tăng #% tu vi nhận được"));

        congPhapTienOptions.add(new CongPhapOption(21, "Tăng # thể chất"));
        congPhapTienOptions.add(new CongPhapOption(22, "Tăng # tinh thần"));
        congPhapTienOptions.add(new CongPhapOption(23, "Tăng # nhanh nhẹn"));
        congPhapTienOptions.add(new CongPhapOption(24, "Tăng # sức mạnh"));
        congPhapTienOptions.add(new CongPhapOption(25, "Tăng #% thể chất"));
        congPhapTienOptions.add(new CongPhapOption(26, "Tăng #% tinh thần"));
        congPhapTienOptions.add(new CongPhapOption(27, "Tăng #% nhanh nhẹn"));
        congPhapTienOptions.add(new CongPhapOption(28, "Tăng #% sức mạnh"));

        congPhapTienOptions.add(new CongPhapOption(29, "Tăng #% sát thương kim"));
        congPhapTienOptions.add(new CongPhapOption(30, "Tăng #% sát thương mộc"));
        congPhapTienOptions.add(new CongPhapOption(31, "Tăng #% sát thương thủy"));
        congPhapTienOptions.add(new CongPhapOption(32, "Tăng #% sát thương hỏa"));
        congPhapTienOptions.add(new CongPhapOption(33, "Tăng #% sát thương thổ"));
        congPhapTienOptions.add(new CongPhapOption(34, "Tăng #% sát thương phong"));
        congPhapTienOptions.add(new CongPhapOption(35, "Tăng #% sát thương lôi"));
        congPhapTienOptions.add(new CongPhapOption(36, "Tăng #% sát thương quang"));
        congPhapTienOptions.add(new CongPhapOption(37, "Tăng #% sát thương ám"));

        congPhapTienOptions.add(new CongPhapOption(38, "Tăng #% tỷ lệ đột phá"));
        congPhapTienOptions.add(new CongPhapOption(39, "Tăng #% tỷ lệ đột phá thiên đạo"));

        congPhapTienOptions.add(new CongPhapOption(40, "Tăng #% exp lục nghệ nhận được"));
        congPhapTienOptions.add(new CongPhapOption(41, "Giảm #% thời gian hồi tu vi"));


        ///options cong phap lt
        congPhapLuyenTheOptions.add(new CongPhapOption(0, "Tăng #% tốc độ tu luyện"));
        congPhapLuyenTheOptions.add(new CongPhapOption(1, "Tăng #% kinh nghiệm công pháp"));
        congPhapLuyenTheOptions.add(new CongPhapOption(2, "Tăng #% chỉ số công pháp"));
        congPhapLuyenTheOptions.add(new CongPhapOption(3, "Tăng #% chỉ số thể chất"));
        congPhapLuyenTheOptions.add(new CongPhapOption(4, "Tăng #% chỉ số tôi thể"));
        congPhapLuyenTheOptions.add(new CongPhapOption(5, "Tăng #% chỉ số võ kỹ"));
        congPhapLuyenTheOptions.add(new CongPhapOption(6, "Tăng #% chỉ số luyện thể"));
    }

    public static CongPhapOption getLuyenTheOption(int i) {
        if (i < 0 || i >= congPhapLuyenTheOptions.size()) {
            return null;
        }
        CongPhapOption congPhapOption = new CongPhapOption();
        congPhapOption.id = congPhapLuyenTheOptions.get(i).id;
        congPhapOption.name = congPhapLuyenTheOptions.get(i).name;
        return congPhapOption;
    }

    public static CongPhapOption getTienOption(int i) {
        if (i < 0 || i >= congPhapTienOptions.size()) {
            return null;
        }
        CongPhapOption congPhapOption = new CongPhapOption();
        congPhapOption.id = congPhapTienOptions.get(i).id;
        congPhapOption.name = congPhapTienOptions.get(i).name;
        return congPhapOption;
    }
}
