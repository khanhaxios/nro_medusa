/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.congphap;

import com.girlkun.models.player.Player;
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

    public static CongPhapTuTien getById(Player player, int id) {
        CongPhapTuTien congPhapTuTien = new CongPhapTuTien(player);
        if (CongPhapTemplate.getI().CONG_PHAP_TU_TIEN.containsKey(id)) {
            CongPhapTuTien template = CongPhapTemplate.getI().CONG_PHAP_TU_TIEN.get(id);
            congPhapTuTien.id = template.id;
            congPhapTuTien.tenCongPhap = template.tenCongPhap;
            congPhapTuTien.mota = template.mota;
            congPhapTuTien.thuoctinh = template.thuoctinh;
            congPhapTuTien.maxLevel = template.maxLevel;
            congPhapTuTien.maxPham = template.maxPham;
        }
        return congPhapTuTien;
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

    public CongPhapTuTien getCongPhapTuTien(int i) {
        CongPhapTuTien congPhapTuTien = CONG_PHAP_TU_TIEN.get(i);
        return new CongPhapTuTien(congPhapTuTien.id, congPhapTuTien.tenCongPhap, congPhapTuTien.mota, congPhapTuTien.thuoctinh, congPhapTuTien.maxLevel, congPhapTuTien.maxPham);
    }

    public CongPhapTuMa getCongPhapTuMa(int i) {
        CongPhapTuMa congPhapTuMa = CONG_PHAP_TU_MA.get(i);
        return new CongPhapTuMa(congPhapTuMa.id, congPhapTuMa.tenCongPhap, congPhapTuMa.mota, congPhapTuMa.thuoctinh, congPhapTuMa.maxLevel, congPhapTuMa.maxPham);
    }
}
