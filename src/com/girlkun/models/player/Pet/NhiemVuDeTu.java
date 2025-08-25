package com.girlkun.models.player.Pet;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.models.skill.Skill;

import java.util.ArrayList;
import java.util.List;

public class NhiemVuDeTu {
    public int currentTaskIndex = -1;
    public Player player;
    public int MAX_TASK = 12;

    public PetTaskType type;
    public List<NhiemVuDeTuPhu> subTask = new ArrayList<>();

    public NhiemVuDeTu(Player player) {
        this.player = player;
    }

    public void processTask(int index) {

    }

    public boolean isDone() {
        return currentTaskIndex > MAX_TASK;
    }

    public void sendNextTask() {
        currentTaskIndex += 1;
    }

    public NhiemVuDeTuPhu getCurrentTask() {
        if (currentTaskIndex > subTask.size()) {
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
        if (task.type == TaskType.USE_SKILL && task.targetId == skill.template.id) {
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
            task.checkDoneTask();
        }
    }

    public void checkDoneTask(int i) {

    }
}
