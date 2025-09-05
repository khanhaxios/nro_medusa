/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.utils.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CongPhapTemplate {
    private static CongPhapTemplate I;
    public Map<Integer, CongPhapTuTien> CONG_PHAP_TU_TIEN = new HashMap<>();
    public Map<Integer, CongPhapLuyenThe> CONG_PHAP_LUYEN_THE = new HashMap<>();
    public Map<Integer, CongPhapTuMa> CONG_PHAP_TU_MA = new HashMap<>();


    public static CongPhapTemplate getI() {
        if (I == null) {
            I = new CongPhapTemplate();
        }
        return I;
    }

    public void initTemplate() {
        CONG_PHAP_TU_TIEN = new HashMap<>();
        CONG_PHAP_TU_MA = new HashMap<>();
        CONG_PHAP_LUYEN_THE = new HashMap<>();
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("data/girlkun/cong_phap/cong_phap_tu_tien.json")) {
            JSONArray danPhuongArray = (JSONArray) parser.parse(reader);

            for (Object obj : danPhuongArray) {
                JSONObject danPhuongJson = (JSONObject) obj;

                int id = ((Long) danPhuongJson.get("id")).intValue();
                String ten = (String) danPhuongJson.get("tenCongPhap");
                String mota = String.valueOf(danPhuongJson.get("mota"));
                String thuoctinh = String.valueOf(danPhuongJson.get("thuoctinh"));
                byte maxLevel = Byte.parseByte(danPhuongJson.get("maxLevel").toString());
                byte maxPham = Byte.parseByte(danPhuongJson.get("maxPham").toString());
                CongPhapTuTien congPhapTuTien = new CongPhapTuTien(id, ten, mota, thuoctinh, maxLevel, maxPham);
                CONG_PHAP_TU_TIEN.put(id, congPhapTuTien);
            }
            System.out.println("Đã nạp " + CONG_PHAP_TU_TIEN.size() + " công pháp tu tiên từ JSON.");

        } catch (Exception e) {
            Logger.error(e.getMessage());
        }

        parser = new JSONParser();
        try (FileReader reader = new FileReader("data/girlkun/cong_phap/cong_phap_tuma.json")) {
            JSONArray danPhuongArray = (JSONArray) parser.parse(reader);

            for (Object obj : danPhuongArray) {
                JSONObject danPhuongJson = (JSONObject) obj;

                int id = ((Long) danPhuongJson.get("id")).intValue();
                String ten = (String) danPhuongJson.get("tenCongPhap");
                String mota = String.valueOf(danPhuongJson.get("mota"));
                String thuoctinh = String.valueOf(danPhuongJson.get("thuoctinh"));
                byte maxLevel = Byte.parseByte(danPhuongJson.get("maxLevel").toString());
                byte maxPham = Byte.parseByte(danPhuongJson.get("maxPham").toString());
                CongPhapTuMa congPhapTuTien = new CongPhapTuMa(id, ten, mota, thuoctinh, maxLevel, maxPham);
                CONG_PHAP_TU_MA.put(id, congPhapTuTien);
            }
            System.out.println("Đã nạp " + CONG_PHAP_TU_MA.size() + " công pháp tu ma từ JSON.");

        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
        parser = new JSONParser();
        try (FileReader reader = new FileReader("data/girlkun/cong_phap/cong_phap_luyen_the.json")) {
            JSONArray danPhuongArray = (JSONArray) parser.parse(reader);

            for (Object obj : danPhuongArray) {
                JSONObject danPhuongJson = (JSONObject) obj;

                int id = ((Long) danPhuongJson.get("id")).intValue();
                String ten = (String) danPhuongJson.get("tenCongPhap");
                String mota = String.valueOf(danPhuongJson.get("mota"));
                String thuoctinh = String.valueOf(danPhuongJson.get("thuoctinh"));
                byte maxLevel = Byte.parseByte(danPhuongJson.get("maxLevel").toString());
                byte maxPham = Byte.parseByte(danPhuongJson.get("maxPham").toString());
                CongPhapLuyenThe congPhapTuTien = new CongPhapLuyenThe(id, ten, mota, thuoctinh, maxLevel, maxPham);
                CONG_PHAP_LUYEN_THE.put(id, congPhapTuTien);
            }
            System.out.println("Đã nạp " + CONG_PHAP_LUYEN_THE.size() + " công pháp luyện thể từ JSON.");
        } catch (Exception e) {
            Logger.error(e.getMessage());
        }
    }
}
