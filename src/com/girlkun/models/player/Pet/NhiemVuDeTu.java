/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.Pet;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.item.Item;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NhiemVuDeTu {
    public int currentTaskIndex = -1;
    public Player player;
    public int type;
    public List<NhiemVuDeTuPhu> subTask = new ArrayList<>();

    public NhiemVuDeTu(Player player) {
        this.player = player;
        subTask = new ArrayList<>();
    }

    public boolean isDone() {
        return (currentTaskIndex == subTask.size() - 1) && getCurrentTask().isDoneTask();
    }

    public void sendNextTask() {
        if (currentTaskIndex + 1 <= subTask.size() - 1) {
            currentTaskIndex += 1;
        }
    }

    public NhiemVuDeTuPhu getCurrentTask() {
        if (currentTaskIndex >= subTask.size()) {
            return subTask.get(subTask.size() - 1);
        }
        if (currentTaskIndex == -1) {
            return null;
        }
        return subTask.get(currentTaskIndex);
    }

    public void checkDoneTaskBoss(Boss boss) {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;

        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.KILL_BOSS && task.targetId == boss.id) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskPickItemAndUseItem(Item item) {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if ((task.type == TaskType.FIND_ITEM || task.type == TaskType.USE_ITEM) && task.targetId == item.template.id) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskUseSkill(Skill skill) {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.USE_SKILL && task.targetId == skill.template.type) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskFly() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.FLY) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskRun() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.RUN) {
            if (player.location.countRun > 5) {
                task.checkDoneTask();
                player.location.countRun = 0;
            } else {
                player.location.countRun++;
            }
        }
    }

    public void checkDoneTaskKillMob(Mob mob) {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.KILL_MOB && task.targetId == mob.tempId) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskBanDoKhoBau() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.BDKB) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskQuayMayMan() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.PLAY_LUCKY_ROUND) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskChuyenSinh() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.CHUYEN_SINH) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskMoGioiHan() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.MO_GIOI_HAN) {
            task.checkDoneTask();
        }
    }

    public void checkDoneTaskDatCanhGioi(int canhGioi) {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.DAT_CANH_GIOI) {
            task.currentCount = canhGioi;
        }
    }


    public void checkDoneTaskDotPha() {
        if (currentTaskIndex < 0 || currentTaskIndex >= subTask.size()) return;
        NhiemVuDeTuPhu task = subTask.get(currentTaskIndex);
        if (task.type == TaskType.DOT_PHA_CANH_GIOI) {
            task.checkDoneTask();
        }
    }

    public void dispose() {
        currentTaskIndex = -1;
        player.nhiemVuDeTu = new NhiemVuDeTu(player);
    }

    public void init(int taskType) {
        this.type = Objects.requireNonNull(PetTaskType.fromTaskType(taskType)).getTaskKey();
        this.subTask = NhienVuDeTuTemplate.getNhiemVu(taskType);
        this.currentTaskIndex = 0;
    }

    public void onLoad(int taskType, int currentTaskIndex, int currentCount) {
        // kiểm tra xem trong template có tồn tại type này không
        PetTaskType type = PetTaskType.fromTaskType(taskType);
        List<NhiemVuDeTuPhu> sub = NhienVuDeTuTemplate.nhiemVuDeTus.get(taskType);
        if (sub == null || sub.isEmpty()) {
            System.err.println("Không tìm thấy nhiệm vụ loại: " + taskType);
            return;
        }
        this.type = taskType; // map từ string sang enum (bạn có thể viết hàm này)
        this.subTask = sub;
        this.currentTaskIndex = Math.min(currentTaskIndex, sub.size() - 1);

        NhiemVuDeTuPhu nhiemVuDeTuPhu = sub.get(this.currentTaskIndex);
        if (nhiemVuDeTuPhu != null) {
            nhiemVuDeTuPhu.currentCount = Math.max(0, currentCount); // tránh giá trị âm
        }
    }
}
