package com.girlkun.models.player.Pet;

public class NhiemVuDeTuPhu {
    public String mota;
    public int currentCount;
    public int totalCount;
    // type 0 is tim kiem item

    public int targetId;

    public TaskType type;
    public boolean isDone;

    public NhiemVuDeTuPhu(String mota, int currentCount, int totalCount, TaskType type, int targetId) {
        this.mota = mota;
        this.targetId = targetId;
        this.currentCount = currentCount;
        this.totalCount = totalCount;
        this.type = type;
    }

    public void checkDoneTask() {
        if (currentCount == totalCount) {
            isDone = true;
        }
    }
}
