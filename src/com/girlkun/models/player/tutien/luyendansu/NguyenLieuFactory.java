package com.girlkun.models.player.tutien.luyendansu;

import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class NguyenLieuFactory {
    public static Map<Integer, NguyenLieu> NGUYEN_LIEU_TEMPLATE = new HashMap<>();

    public static NguyenLieu radomizeNguyenLieu() {
        NguyenLieu nguyenLieu = new NguyenLieu();
        NguyenLieu old = NGUYEN_LIEU_TEMPLATE.get(Util.nextInt(0, NGUYEN_LIEU_TEMPLATE.size() - 1));
        nguyenLieu.copy(old);
        nguyenLieu.quality = 0;
        if (Util.isTrue(1, 100)) {
            nguyenLieu.quality = 4;
        } else if (Util.isTrue(10, 100)) {
            nguyenLieu.quality = 3;
        } else {
            nguyenLieu.quality = Util.nextInt(0, 2);
        }
        nguyenLieu.quantity = Util.nextInt(1, 2);
        return nguyenLieu;
    }

    public static NguyenLieu radomizeNguyenLieu(int min) {
        NguyenLieu nguyenLieu = new NguyenLieu();
        NguyenLieu old = NGUYEN_LIEU_TEMPLATE.get(Util.nextInt(0, NGUYEN_LIEU_TEMPLATE.size() - 1));
        while (old.id <= min) {
            old = NGUYEN_LIEU_TEMPLATE.get(Util.nextInt(0, NGUYEN_LIEU_TEMPLATE.size() - 1));
        }
        nguyenLieu.copy(old);
        nguyenLieu.quality = 0;
        if (Util.isTrue(1, 100)) {
            nguyenLieu.quality = 4;
        } else if (Util.isTrue(10, 100)) {
            nguyenLieu.quality = 3;
        } else {
            nguyenLieu.quality = Util.nextInt(0, 2);
        }
        nguyenLieu.quantity = Util.nextInt(1, 2);
        return nguyenLieu;
    }

    public static void loadNguyenLieu() {
        JSONParser parser = new JSONParser();
        String filePath = "data/girlkun/dan_phuong/nguyen_lieu.json";
        try (FileReader reader = new FileReader(filePath)) {
            Object obj = parser.parse(reader);
            JSONArray nguyenLieuArray = (JSONArray) obj;

            for (Object nlObj : nguyenLieuArray) {
                JSONObject nlJson = (JSONObject) nlObj;
                int id = ((Long) nlJson.get("id")).intValue();
                String ten = (String) nlJson.get("tenNguyenLieu");

                NguyenLieu nguyenLieu = new NguyenLieu(id, ten);
                NGUYEN_LIEU_TEMPLATE.put(id, nguyenLieu);
            }
            Logger.log("Đã nạp " + NGUYEN_LIEU_TEMPLATE.size() + " Nguyên liệu từ JSON" + "\n");
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }

    public static NguyenLieu getByIdAndQuantity(int nlId, int quantity) {
        NguyenLieu nguyenLieu = NGUYEN_LIEU_TEMPLATE.get(nlId);
        if (nguyenLieu != null) {
            nguyenLieu.quantity = quantity;
        }
        return nguyenLieu;
    }
}
