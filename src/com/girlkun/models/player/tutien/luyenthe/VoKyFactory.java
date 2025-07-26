package com.girlkun.models.player.tutien.luyenthe;

import com.girlkun.models.player.Player;
import com.girlkun.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class VoKyFactory {
    public static List<VoKy> VO_KY_TEMPLATE = new ArrayList<>();

    public static VoKy randomizedVoKy(Player player) {
        VoKy voKy = VO_KY_TEMPLATE.get(Util.nextInt(0, VO_KY_TEMPLATE.size() - 1));
        voKy.init();
        voKy.player = player;
        return voKy;
    }


    public static void initTemplate() {
        // load from file vo_ky.json
        try {
            JSONParser parser = new JSONParser();
            JSONArray array = (JSONArray) parser.parse(new FileReader("data/girlkun/voky/vo_ky.json"));
            VO_KY_TEMPLATE.clear();
            for (Object obj : array) {
                JSONObject json = (JSONObject) obj;

                String ten = (String) json.get("tenVoKy");
                String moTa = (String) json.get("moTaVoKy");
                int type = Integer.parseInt(json.get("type").toString());
                int id = Integer.parseInt(json.get("id").toString());
                VoKy voKy = new VoKy(id, ten, moTa, type);
                VO_KY_TEMPLATE.add(voKy);
            }
            System.out.println("Đã load " + VO_KY_TEMPLATE.size() + " võ kỹ.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Không thể load file vo_ky.json");
        }
    }
}
