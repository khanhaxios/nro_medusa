package com.girlkun.models.devices;

import com.girlkun.database.GirlkunDB;
import com.girlkun.result.GirlkunResultSet;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DeviceInfo {
    private static final int MAX_COUNT_DEVICE = 3;
    public static List<DeviceInfo> DEVICES_LIST = new ArrayList<>();
    private int id;
    private String deviceId;
    private String ipAddress;
    private String platform;
    private long createAt;
    private String note;

    public static boolean isHasDeviceInfo(String deviceId) {
        try {
            GirlkunResultSet girlkunResultSet = GirlkunDB.executeQuery("select 1 from devices where device_id=? limit 1", deviceId);
            return girlkunResultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isHasDeviceInfo(String deviceId, String ipAddress) {
        try {
            GirlkunResultSet girlkunResultSet = GirlkunDB.executeQuery("select 1 from devices where device_id=? and ip_address=? limit 1", deviceId, ipAddress);
            return girlkunResultSet.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static DeviceInfo loadDeviceFromDatabase(String deviceId) {
        DeviceInfo deviceInfo = null;
        try {
            if (!isHasDeviceInfo(deviceId)) {
                return null;
            }
            GirlkunResultSet girlkunResultSet = GirlkunDB.executeQuery("select * from devices where device_id=? limit 1", deviceId);
            while (girlkunResultSet.next()) {
                deviceInfo = new DeviceInfo();
                deviceInfo.setId(girlkunResultSet.getInt("id"));
                deviceInfo.setDeviceId(girlkunResultSet.getString("device_id"));
                deviceInfo.setIpAddress(girlkunResultSet.getString("ip_address"));
                deviceInfo.setPlatform(girlkunResultSet.getString("platform"));
                deviceInfo.setNote(girlkunResultSet.getString("note"));
                deviceInfo.setCreateAt(girlkunResultSet.getLong("created_at"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceInfo;
    }

    public static void insertDevice(String deviceId, String ipAddress, String platform) {
        try {
            if (isHasDeviceInfo(deviceId)) {
                return;
            }
            GirlkunDB.executeUpdate("insert into devices(device_id,ip_address,platform,created_at,note) values(?,?,?,?,?)", deviceId, ipAddress, platform, System.currentTimeMillis(), "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean insertDeviceGetBool(DeviceInfo deviceInfo) {
        try {
            if (deviceInfo == null) return false;
            return GirlkunDB.executeUpdate("insert into devices(device_id,ip_address,platform,created_at,note) values(?,?,?,?,?)", deviceInfo.deviceId, deviceInfo.ipAddress, deviceInfo.platform, deviceInfo.createAt, deviceInfo.note) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkCanCreateAccount(String deviceId) {
        try {
            GirlkunResultSet rs = GirlkunDB.executeQuery(
                    "SELECT COUNT(*) AS count FROM account WHERE device_id = ?",
                    deviceId
            );
            if (rs.next()) {
                long count = rs.getLong("count");
                return count < MAX_COUNT_DEVICE; // true nếu chưa vượt quá
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Trường hợp lỗi: không cho tạo
    }
}
