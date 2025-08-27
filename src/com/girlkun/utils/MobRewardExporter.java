/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.utils;

import com.girlkun.models.reward.ItemMobReward;
import com.girlkun.models.reward.ItemOptionMobReward;
import com.girlkun.models.reward.MobReward;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static com.girlkun.server.Manager.MOB_REWARDS;

public class MobRewardExporter {
    public static void exportMobRewardsToTxt(String outputPath) {
        File outputFile = new File(outputPath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (MobReward mobReward : MOB_REWARDS.values()) {
                writer.write("Mob ID: " + mobReward.getMobId());
                writer.newLine();

                // Gold Rewards
                writer.write("  Gold Rewards:");
                writer.newLine();
                for (ItemMobReward item : mobReward.getGoldReward()) {
                    writeItemReward(writer, item, "    ");
                }

                // Item Rewards
                writer.write("  Item Rewards:");
                writer.newLine();
                for (ItemMobReward item : mobReward.getItemReward()) {
                    writeItemReward(writer, item, "    ");
                }

                writer.write("--------------------------------------------------");
                writer.newLine();
            }

            System.out.println("Xuất dữ liệu MOB_REWARDS thành công ra: " + outputPath);
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file: " + e.getMessage());
        }
    }

    private static void writeItemReward(BufferedWriter writer, ItemMobReward item, String indent) throws IOException {
        writer.write(indent + "- Item ID: " + item.getTemp().id);
        writer.newLine();
        writer.write(indent + "  + Quantity: " + item.getQuantity()[0] + " - " + item.getQuantity()[1]);
        writer.newLine();
        writer.write(indent + "  + Ratio: " + item.getRatio()[0] + " - " + item.getRatio()[1]);
        writer.newLine();
        writer.write(indent + "  + Gender: " + item.getGender());
        writer.newLine();
        writer.write(indent + "  + MapDrop: " + arrayToString(item.getMapDrop()));
        writer.newLine();

        // Item options
        if (!item.getOption().isEmpty()) {
            writer.write(indent + "  + Options:");
            writer.newLine();
            for (ItemOptionMobReward opt : item.getOption()) {
                writer.write(indent + "    * Option ID: " + opt.getTemp().id);
                writer.newLine();
                writer.write(indent + "      - Param: " + opt.getParam()[0] + " - " + opt.getParam()[1]);
                writer.newLine();
                writer.write(indent + "      - Ratio: " + opt.getRatio()[0] + " - " + opt.getRatio()[1]);
                writer.newLine();
            }
        }
    }

    private static String arrayToString(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(", ");
        }
        return sb.toString();
    }
}
