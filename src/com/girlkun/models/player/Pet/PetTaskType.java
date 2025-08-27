/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.player.Pet;

public enum PetTaskType {
    MABU(0),
    BERRUS(1),
    ZENO(2),
    THAN_LONG_TY_TY(3),
    GOKU(4),
    BROLY(5),
    MASTER_BROLY(6);

    private final int taskKey;

    PetTaskType(int taskKey) {
        this.taskKey = taskKey;
    }

    public int getTaskKey() {
        return taskKey;
    }

    // Hàm tiện ích để map từ String (JSON/DB) sang enum
    public static PetTaskType fromTaskType(int taskType) {
        for (PetTaskType type : values()) {
            if (type.getTaskKey() == taskType) {
                return type;
            }
        }
        return null; // hoặc throw new IllegalArgumentException("Unknown taskType: " + taskType);
    }
}