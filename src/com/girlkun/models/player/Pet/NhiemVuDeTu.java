package com.girlkun.models.player.Pet;

import com.girlkun.models.player.Player;

import java.util.ArrayList;
import java.util.List;

public class NhiemVuDeTu {
    public int currentTaskIndex = -1;
    public Player player;
    public int MAX_TASK = 12;
    public List<NhiemVuDeTuPhu> subTask = new ArrayList<>();
    private static NhiemVuDeTu I;

    public static NhiemVuDeTu getI() {
        if (I == null) {
            I = new NhiemVuDeTu();
        }
        return I;
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
}
