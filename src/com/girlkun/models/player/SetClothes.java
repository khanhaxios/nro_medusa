package com.girlkun.models.player;

import com.girlkun.models.item.Item;

public class SetClothes {

    private Player player;

    public SetClothes(Player player) {
        this.player = player;
    }

    public byte NhanHoang;
    public byte MaThan;
    public byte ThienTu;
    public byte nguyenthuytd;
    public byte nguyenthuyxd;
    public byte nguyenthuynm;
    public byte thongkhotd;
    public byte thongkhoxd;
    public byte thongkhonm;

    public byte songoku;
    public byte thienXinHang;
    public byte kirin;

    public byte ocTieu;
    public byte pikkoroDaimao;
    public byte picolo;

    public byte kakarot;
    public byte cadic;
    public byte nappa;

    public byte solomon;
    public byte setDHD;
    public byte setDTS;
    public byte setDTL;
    public byte tinhan;
    public byte nguyetan;
    public byte nhatan;
    public byte mahoa;
    public byte thanhoa;
    public byte nguyenthuy;
    public byte thongkho;
    public byte huvo;

    public byte setJirenTD;
    public byte setJirenNM;
    public byte setJirenXD;

    public byte setGokuUITD;
    public byte setGokuUINM;
    public byte setGokuUIXD;

    public byte skhMedusa;

    public boolean godClothes;
    public int ctHaiTac = -1;
    public int ctDeKaiDo = -1;

    public boolean isDaoYeuLinhPhucMa;
    public boolean isThuongLinhDietMa;

    // SET KiCH HOAT PHU KIEN
    public byte pkkhMedusa;

    public void setup() {
//        if (!player.isPet){
//        setupGOGETA();
//        }
        setDefault();
        setupSKT();
        setupAN();

        //setmahoa();
        // setthanhoa();
        setupDTS();
        setupDHD();
        setupDTL();
        setupDeoLung();
        this.godClothes = true;
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                if (item.template.id > 567 || item.template.id < 555) {
                    this.godClothes = false;
                    break;
                }
            } else {
                this.godClothes = false;
                break;
            }
        }
        Item ct = this.player.inventory.itemsBody.get(5);
        if (ct.isNotNullItem()) {
            switch (ct.template.id) {
                case 618:
                case 619:
                case 620:
                case 621:
                case 622:
                case 623:
                case 624:
                case 626:
                case 627:
                    this.ctHaiTac = ct.template.id;
                    break;
                case 843:
                case 844:
                case 845:
                case 853:
                case 854:
                case 855:
                case 856:
                case 857:
                case 858:
                    this.ctDeKaiDo = ct.template.id;
                    break;

            }
        }
    }

    private void setupSKT() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 129:
                        case 141:
                            isActSet = true;
                            songoku++;
                            break;
                        case 127:
                        case 139:
                            isActSet = true;
                            thienXinHang++;
                            break;
                        case 128:
                        case 140:
                            isActSet = true;
                            kirin++;
                            break;
                        case 131:
                        case 143:
                            isActSet = true;
                            ocTieu++;
                            break;
                        case 132:
                        case 144:
                            isActSet = true;
                            pikkoroDaimao++;
                            break;
                        case 130:
                        case 142:
                            isActSet = true;
                            picolo++;
                            break;
                        case 135:
                        case 138:
                            isActSet = true;
                            nappa++;
                            break;
                        case 133:
                        case 136:
                            isActSet = true;
                            kakarot++;
                            break;
                        case 134:
                        case 137:
                            isActSet = true;
                            cadic++;
                            break;
                        case 189:
                        case 199:
                            isActSet = true;
                            NhanHoang++;
                            break;
                        case 190:
                        case 200:
                            isActSet = true;
                            MaThan++;
                            break;
                        case 191:
                        case 201:
                            isActSet = true;
                            ThienTu++;
                            break;
                        case 213:
                        case 216:
                            isActSet = true;
                            nguyenthuytd++;
                            break;
                        case 214:
                        case 217:
                            isActSet = true;
                            nguyenthuynm++;
                            break;
                        case 215:
                        case 218:
                            isActSet = true;
                            nguyenthuyxd++;
                            break;
                        //thongkho
                        case 224:
                        case 227:
                            isActSet = true;
                            thongkhotd++;
                            break;
                        case 225:
                        case 228:
                            isActSet = true;
                            thongkhonm++;
                            break;
                        case 226:
                        case 229:
                            isActSet = true;
                            thongkhoxd++;
                            break;
                        case 235:
                        case 238:
                            isActSet = true;
                            setJirenTD++;
                            break;
                        case 236:
                        case 239:
                            isActSet = true;
                            setJirenNM++;
                            break;
                        case 237:
                        case 240:
                            isActSet = true;
                            setJirenXD++;
                            break;
                        case 241:
                        case 244:
                            isActSet = true;
                            setGokuUITD++;
                            break;
                        case 242:
                        case 245:
                            isActSet = true;
                            setGokuUINM++;
                            break;
                        case 243:
                        case 246:
                            isActSet = true;
                            setGokuUIXD++;
                            break;
                    }
                    if (isActSet) {
                        break;
                    }
                }
            } else {
                break;
            }
        }
    }

    public boolean isSetThongKho() {
        return thongkhotd == 5 || thongkhonm == 5 || thongkhoxd == 5;
    }

    private void setupAN() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSett = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 34:
                            isActSett = true;
                            tinhan++;
                            break;
                        case 35:
                            isActSett = true;
                            nguyetan++;
                            break;
                        case 36:
                            isActSett = true;
                            nhatan++;
                            break;
                        case 205:
                            isActSett = true;
                            mahoa++;
                            break;
                        case 206:
                            isActSett = true;
                            thanhoa++;
                            break;
                        case 204:
                            isActSett = true;
                            nguyenthuy++;
                            break;
                        case 212:
                            isActSett = true;
                            huvo++;
                            break;
                    }
                    if (isActSett) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }
//    private void setmahoa() {
//        for (int i = 0; i < 5; i++) {
//            Item item = this.player.inventory.itemsBody.get(i);
//            if (item.isNotNullItem()) {
//                boolean isActSett = false;
//                for (Item.ItemOption io : item.itemOptions) {
//                    switch (io.optionTemplate.id) {
//                        case 205:
//                            isActSett = true;
//                            mahoa++;
//                            break;
//                       // case 206:
//                         //   isActSett = true;
//                         //   thanhoa++;
//                        //    break;
//                        //case 36:
//                      //      isActSett = true;
//                        //    nhatan++;
//                        //    break;
//                    }
//                    if (isActSett) {
//                        break;
//                    }
//
//                }
//            } else {
//                break;
//            }
//        }
//    }
//    private void setthanhoa() {
//        for (int i = 0; i < 5; i++) {
//            Item item = this.player.inventory.itemsBody.get(i);
//            if (item.isNotNullItem()) {
//                boolean isActSett = false;
//                for (Item.ItemOption io : item.itemOptions) {
//                    switch (io.optionTemplate.id) {
//                        case 206:
//                            isActSett = true;
//                            thanhoa++;
//                            break;
//                       // case 206:
//                         //   isActSett = true;
//                         //   thanhoa++;
//                        //    break;
//                        //case 36:
//                      //      isActSett = true;
//                        //    nhatan++;
//                        //    break;
//                    }
//                    if (isActSett) {
//                        break;
//                    }
//
//                }
//            } else {
//                break;
//            }
//        }
//    }
//    private void setupGOGETA() {
//        for (int i = 5; i < 11; i++) {
//            Item item = this.player.inventory.itemsBody.get(i);
//            if (item.isNotNullItem()) {
//                boolean isActSet = false;
//                for (Item.ItemOption io : item.itemOptions) {
//                    switch (io.optionTemplate.id) {
//                        case 192:
//                        case 193:
//                            isActSet = true;
//                            solomon++;
//                            break;
//                    }
//                    if (isActSet) {
//                        break;
//                    }
//
//                }
//            } else {
//                break;
//            }
//        }
//    }

    private void setupDTS() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 120) {
                                setDTS++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDHD() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 80) {
                                setDHD++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDTL() {
        for (int i = 0; i < 5; i++) {
            Item item = this.player.inventory.itemsBody.get(i);
            if (item.isNotNullItem()) {
                boolean isActSet = false;
                for (Item.ItemOption io : item.itemOptions) {
                    switch (io.optionTemplate.id) {
                        case 21:
                            if (io.param == 15) {
                                setDTL++;
                            }
                            break;
                    }
                    if (isActSet) {
                        break;
                    }

                }
            } else {
                break;
            }
        }
    }

    private void setupDeoLung() {
        if (this.player.inventory.itemsBody.size() > 8) {
            Item deoLung = this.player.inventory.itemsBody.get(8);
            if (deoLung != null && deoLung.isNotNullItem()) {
                switch (deoLung.template.id) {
                    case 1406 ->
                        this.isThuongLinhDietMa = true;
                    case 1405 ->
                        this.isDaoYeuLinhPhucMa = true;
                }
            }
        }
    }

    private void setDefaultPKKH() {
        this.pkkhMedusa = 0;
    }

    public void setupPhuKien() {
        setDefaultPKKH();
        if (this.player.inventory.itemsBody.size() > 11) {
            for (int i = 7; i < 12; i++) {
                Item phuKien = this.player.inventory.itemsBody.get(i);
                if (phuKien.isNotNullItem()) {
                    boolean isActSet = false;
                    for (Item.ItemOption io : phuKien.itemOptions) {
                        switch (io.optionTemplate.id) {
                            case 247:
                                pkkhMedusa++;
                                isActSet = true;
                                break;
                        }
                        if (isActSet) {
                            break;
                        }
                    }
                }

            }
        }
    }

    public short getOptionPhuKienKichHoat() {
        switch (this.pkkhMedusa) {
            case 2:
                return 248;
            case 3:
                return 249;
            case 5:
                return 250;
            default:
                break;
        }
        return 247;
    }

    public boolean isSetJiren() {
        return setJirenTD == 5 || setJirenNM == 5 || setJirenXD == 5;
    }

    public boolean isSetGokuUI() {
        return setGokuUITD == 5 || setGokuUINM == 5 || setGokuUIXD == 5;
    }

    private void setDefault() {
        this.songoku = 0;
        this.thienXinHang = 0;
        this.kirin = 0;
        this.ocTieu = 0;
        this.pikkoroDaimao = 0;
        this.picolo = 0;
        this.kakarot = 0;
        this.cadic = 0;
        this.nappa = 0;
        this.setDHD = 0;
        this.setDTS = 0;
        this.setDTL = 0;
        this.solomon = 0;
        this.tinhan = 0;
        this.nhatan = 0;
        this.nguyetan = 0;
        this.godClothes = false;
        this.ctHaiTac = -1;
        this.ctDeKaiDo = -1;

        this.NhanHoang = 0;
        this.MaThan = 0;
        this.ThienTu = 0;
        this.nguyenthuytd = 0;
        this.nguyenthuynm = 0;
        this.nguyenthuyxd = 0;
        this.thongkhotd = 0;
        this.thongkhonm = 0;
        this.thongkhoxd = 0;
        this.mahoa = 0;
        this.thanhoa = 0;
        this.nguyenthuy = 0;
        this.huvo = 0;
        this.setJirenTD = 0;
        this.setJirenNM = 0;
        this.setJirenXD = 0;
        this.setGokuUITD = 0;
        this.setGokuUINM = 0;
        this.setGokuUIXD = 0;
        this.skhMedusa = 0;

        this.isDaoYeuLinhPhucMa = false;
        this.isThuongLinhDietMa = false;
    }

    public void dispose() {
        this.player = null;
    }
}
