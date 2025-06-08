package com.girlkun.models.player.tutien.luyendansu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanPhuong {
    private int id;
    private byte capDoDan;

    private String tenDanPhuong;

    private short slDanCoTheAn;
    private short[][] vatLieu;
}
