package com.girlkun.models.sangiaodich.danduoc;

import com.girlkun.jdbc.daos.GodGK;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.luyendansu.ITransaction;
import com.girlkun.services.Service;
import com.girlkun.services.func.Input;

public class SanGiaoDichService {
    public static int MAX_GD = 8;
    private static SanGiaoDichService I;

    public static SanGiaoDichService getI() {
        if (I == null) {
            I = new SanGiaoDichService();
        }
        return I;
    }

    public void createGiaoDichForm(Player player) {
        Input.SubInput[] subInputs = new Input.SubInput[]{new Input.SubInput("Nhập vào mã giao dịch", Input.ANY), new Input.SubInput("Nhập vào id  người cần giao dịch", Input.NUMERIC), new Input.SubInput("Nhập vào id vật phẩm cần giao dịch", Input.NUMERIC), new Input.SubInput("Nhập vào số lượng cần giao dịch", Input.NUMERIC)};
        Input.gI().createForm(player, Input.CREATE_GIAO_DICH, "Tạo giao dịch mới", subInputs);
    }

    public void createGiaoDich(String maGiaoDich, int idNhanVat, int idItem, int slItem, int diemNap, Player player) {
        // check id nhan vat
        if (SanGiaoDichDanDuoc.getI().getListTransactionByPlayer(player).size() + 1 > MAX_GD) {
            Service.gI().sendThongBao(player, "Bạn tối đa chỉ được tạo 8 giao dịch cùng lúc");
            return;
        }
        boolean isExit = PlayerDAO.checkHasExit(idNhanVat);
        if (!isExit) {
            Service.gI().sendThongBao(player, "Người chơi không tồn tại id [" + idNhanVat + "]");
            return;
        }
        Player playerAccept = GodGK.loadById(idNhanVat);
        // check ma
        if (maGiaoDich.length() > 8) {
            Service.gI().sendThongBao(player, "Mã giao dịch tối đa 8 ký tự");
            return;
        }
        if (!SanGiaoDichDanDuoc.getI().canTakeCode(maGiaoDich)) {
            Service.gI().sendThongBao(player, "Mã Giao Dịch Đã Được Sử Dụng");
            return;
        }
        if (!playerAccept.luyenDanSu.isLuyenDan()) {
            Service.gI().sendThongBao(player, "Đối phương chưa mở luyện đan");
            return;
        }
        // take item by id
        if (!player.luyenDanSu.isLuyenDan()) {
            Service.gI().sendThongBao(player, "Bạn chưa mở luyện đan");
            return;
        }
        if (slItem <= 0) {
            Service.gI().sendThongBao(player, "Số lượng vật phẩm cần lớn hơn 0");
            return;
        }
        // find Dan Duoc
        ITransaction iTransaction = null;
        iTransaction = player.luyenDanSu.tuiDanDuoc.takeDanDuocSplit(idItem, slItem);
        if (iTransaction == null) {
            //find dan phuong
            iTransaction = player.luyenDanSu.tuiDanPhuong.takeDanPhuong(idItem);
        }
        if (iTransaction == null) {
            Service.gI().sendThongBaoOK(player, "Không tìm thấy đan phương hoặc đan dược có id [" + idItem + "]");
            return;
        }
        Transaction transaction = new Transaction(player, playerAccept, 86_400_000, maGiaoDich);
        transaction.addItems(iTransaction);
        diemNap = 1000;
        transaction.totalPrice = diemNap;
        SanGiaoDichDanDuoc.getI().addTransaction(transaction);
        Service.gI().sendThongBao(player, "Tạo giao dịch thành công , bạn có thể xem ở danh sách giao dịch");
        SanGiaoDichDanDuoc.getI().showBaseMenu(player);
    }

    public void createHuyGiaoDichForm(Player player) {
        Input.SubInput[] subInputs = new Input.SubInput[]{new Input.SubInput("Nhập vào mã giao dịch", Input.ANY),};
        Input.gI().createForm(player, Input.HUY_GD, "Hủy Giao Dịch", subInputs);
    }

    public void createInfoGiaoDichForm(Player player) {
        Input.SubInput[] subInputs = new Input.SubInput[]{new Input.SubInput("Nhập vào mã giao dịch", Input.ANY),};
        Input.gI().createForm(player, Input.XEM_INFO, "Xem thông tin giao dịch", subInputs);
    }

    public void huyGD(String maGiaoDich, Player player) {
        Transaction transaction = SanGiaoDichDanDuoc.getI().getById(maGiaoDich);
        if (transaction == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy giao dịch id [" + maGiaoDich + "]");
            return;
        }
        if (transaction.playerRequest.id != player.id && transaction.playerAccept.id != player.id) {
            Service.gI().sendThongBao(player, "Bạn không có quyền xem giao dịch này");
            return;
        }
        SanGiaoDichDanDuoc.getI().huyGiaoDich(transaction, player);
    }

    public void xemInfo(String maGiaoDich, Player player) {
        Transaction transaction = SanGiaoDichDanDuoc.getI().getById(maGiaoDich);
        if (transaction == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy giao dịch id [" + maGiaoDich + "]");
            return;
        }
        if (transaction.playerRequest.id != player.id && transaction.playerAccept.id != player.id) {
            Service.gI().sendThongBao(player, "Bạn không có quyền xem giao dịch này");
            return;
        }
        SanGiaoDichDanDuoc.getI().xemInfo(transaction, player);
    }

    public void createDatVatPhamGiaoDichForm(Player player) {
        Input.SubInput[] subInputs = new Input.SubInput[]{new Input.SubInput("Nhập vào mã giao dịch", Input.ANY), new Input.SubInput("Nhập vào id vật phẩm cần giao dịch", Input.NUMERIC), new Input.SubInput("Nhập vào số lượng cần giao dịch", Input.NUMERIC),};
        Input.gI().createForm(player, Input.DAT_VAT_PHAM_VAO_GD, "Đặt vật phẩm vào giao dịch", subInputs);
    }

    public void datVatPhamVaoGiaoDich(String gdd, int idItem, int slItem, Player player) {
        Transaction transaction = SanGiaoDichDanDuoc.getI().getById(gdd);
        if (transaction == null) {
            Service.gI().sendThongBao(player, "Không tìm thấy giao dịch id [" + gdd + "]");
            return;
        }
        if (transaction.playerRequest.id != player.id && transaction.playerAccept.id != player.id) {
            Service.gI().sendThongBao(player, "Bạn không có quyền xem giao dịch này");
            return;
        }
        // take item by id
        if (!player.luyenDanSu.isLuyenDan()) {
            Service.gI().sendThongBao(player, "Bạn chưa mở luyện đan");
            return;
        }
        if (slItem <= 0) {
            Service.gI().sendThongBao(player, "Số lượng vật phẩm cần lớn hơn 0");
            return;
        }
        // find Dan Duoc
        ITransaction iTransaction = null;
        iTransaction = player.luyenDanSu.tuiDanDuoc.takeDanDuoc(idItem, slItem);
        if (iTransaction == null) {
            //find dan phuong
            iTransaction = player.luyenDanSu.tuiDanPhuong.takeDanPhuong(idItem);
        }
        if (iTransaction == null) {
            Service.gI().sendThongBaoOK(player, "Không tìm thấy đan phương hoặc đan dược có id [" + idItem + "]");
            return;
        }
        int diemNap = slItem * 100;
        if (player.id == transaction.playerAccept.id) {
            transaction.addItemsReceived(iTransaction);
        } else if (player.id == transaction.playerRequest.id) {
            transaction.addItems(iTransaction);
        } else {
            Service.gI().sendThongBao(player, "Bạn không có quyền đặt vật phẩm");
            return;
        }
        transaction.totalPrice += diemNap;
        Service.gI().sendThongBao(player, "Đặt vật phẩm vào thành công");
        transaction.showBaseMenu(player);
    }

    public void khoaGd(Player player) {
        if (player.iDMark.currentGiaoDich != null) {
            player.iDMark.currentGiaoDich.khoaGiaoDich(player);
            player.iDMark.currentGiaoDich.showBaseMenu(player);
            Service.gI().sendThongBao(player, "Đã khóa giao dịch");
        }
    }
}
