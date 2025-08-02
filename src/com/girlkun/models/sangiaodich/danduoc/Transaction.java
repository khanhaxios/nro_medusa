package com.girlkun.models.sangiaodich.danduoc;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.models.player.tutien.luyendansu.DanDuoc;
import com.girlkun.models.player.tutien.luyendansu.DanPhuong;
import com.girlkun.models.player.tutien.luyendansu.ITransaction;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    public String code;
    public List<ITransaction> iTransactions = new ArrayList<>();
    public List<ITransaction> iTransactionsAccept = new ArrayList<>();
    public long totalPrice;
    public Player playerRequest;
    public Player playerAccept;
    public boolean isPlayerRequestLock;
    public boolean isPlayerAcceptLock;
    public long timeWaitGiaoDich;
    public boolean takenItemPlayerRequest = false;
    public boolean takenItemPlayerAccept = false;
    public long lastTimeCreated;
    public int status; // 0 = pending , 1 = done , 2 = cancel

    public Transaction(Player player, Player playerAccept, long timeWaitGiaoDich) {
        this.playerRequest = player;
        this.playerAccept = playerAccept;
        this.timeWaitGiaoDich = timeWaitGiaoDich;
        isPlayerRequestLock = false;
        isPlayerAcceptLock = false;
        takenItemPlayerRequest = false;
        takenItemPlayerAccept = false;
        this.lastTimeCreated = System.currentTimeMillis();
    }

    public void lockTransaction(Player player) {
        if (player.id == playerRequest.id) {
            isPlayerRequestLock = true;
        } else if (player.id == playerAccept.id) {
            isPlayerAcceptLock = true;
        }
    }

    public int getStatus() {
        if (isPlayerRequestLock && isPlayerAcceptLock) {
            return 1; // Đã hoàn tất
        }
        if (System.currentTimeMillis() - lastTimeCreated > timeWaitGiaoDich) {
            return 2; // Hết thời gian, huỷ
        }
        return 0; // Đang chờ
    }

    public void dispose() {
        this.playerRequest = null;
        this.playerAccept = null;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusString() {
        StringBuilder stringBuilder = new StringBuilder();
        int st = getStatus();
        if (st == 0) {
            stringBuilder.append(playerRequest.name)
                    .append("[").append(isPlayerRequestLock ? "Đã khóa" : "Chưa khóa")
                    .append("]").append("=====")
                    .append(playerAccept.name)
                    .append("[").append(isPlayerAcceptLock ? "Đã khóa" : "Chưa khóa");
        } else if (st == 1) {
            stringBuilder.append("Giao dịch hoàn thành");
        } else {
            stringBuilder.append("Giao dịch bị hủy");
        }
        return stringBuilder.toString();
    }

    public void showBaseMenu(Player player) {
        if (player.id == playerAccept.id || player.id == playerRequest.id) {
            StringBuilder menuText = new StringBuilder();
            menuText.append("|7|======= Thông tin giao dịch =======").append("\n");
            menuText.append("|5|Người yêu cầu => ").append(playerRequest.name).append("\n");
            menuText.append("|5|Người xác nhận => ").append(playerAccept.name).append("\n");
            menuText.append("|7|======= Vật phẩm giao dịch =======").append("\n");
            menuText.append(getItemTrade());
            menuText.append("|7|======= Vật phẩm giao dịch =======").append("\n");
            menuText.append(getItemReceived());
            menuText.append("Tổng Giá => ").append(Util.powerToString(totalPrice)).append(" Điểm nạp").append("\n");
            menuText.append("Trạng Thái => ").append(getStatusString()).append("\n");
            String[] options = new String[]{};
            if (player.id == playerAccept.id) {
                player.iDMark.gdMenuType = 0;
                options = new String[]{"Đặt\nVật Phẩm", "Khóa\nGiao Dịch", "Hủy\nGiao Dịch", "Lấy\nVật Phẩm"};
            } else if (player.id == playerRequest.id) {
                player.iDMark.gdMenuType = 1;
                options = new String[]{"Khóa\nGiao Dịch", "Hủy\nGiao Dịch", "Lấy\nVật Phẩm"};
            }
            player.iDMark.currentGiaoDich = this;
            NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_BASE_GIAO_DICH, -1, menuText.toString(), options);
        } else {
            Service.gI().sendThongBao(player, "Bạn không có quyền xem giao dịch này");
        }
    }

    public void khoaGiaoDich(Player player) {
        if (getStatus() == 1 || getStatus() == 2) {
            Service.gI().sendThongBaoOK(player, "Không thể khóa giao dịch đã hoàn thành hoặc đã bị hủy");
            showBaseMenu(player);
            return;
        }
        lockTransaction(player);
        Service.gI().sendThongBaoOK(player, "Đã Khóa Giao Dịch");
    }

    public void huyGiaoDich(Player player) {
        if (getStatus() == 1 || getStatus() == 2) {
            Service.gI().sendThongBao(player, "Không thể hủy giao dịch đã hoàn thành hoặc đã bị hủy");
            showBaseMenu(player);
            return;
        }
        destroyTransaction();
        Service.gI().sendThongBaoOK(player, "Đã Hủy Giao Dịch");
    }

    public void takeItem(Player player) {
        if (player.id == playerRequest.id) {
            if (takenItemPlayerRequest) {
                Service.gI().sendThongBao(player, "Bạn đã lấy vật phẩm rồi");
                return;
            }
            // take item received
            for (ITransaction iTransaction : iTransactions) {
                if (iTransaction instanceof DanDuoc danDuoc) {
                    playerRequest.luyenDanSu.tuiDanDuoc.addDanDuoc(danDuoc);
                } else if (iTransaction instanceof DanPhuong danPhuong) {
                    playerRequest.luyenDanSu.tuiDanPhuong.addDanPhuong(danPhuong);
                }
            }
            takenItemPlayerRequest = true;
        } else if (player.id == playerAccept.id) {
            if (takenItemPlayerAccept) {
                Service.gI().sendThongBao(player, "Bạn đã lấy vật phẩm rồi");
                return;
            }
            for (ITransaction iTransaction : iTransactions) {
                if (iTransaction instanceof DanDuoc danDuoc) {
                    playerAccept.luyenDanSu.tuiDanDuoc.addDanDuoc(danDuoc);
                } else if (iTransaction instanceof DanPhuong danPhuong) {
                    playerAccept.luyenDanSu.tuiDanPhuong.addDanPhuong(danPhuong);
                }
            }
            takenItemPlayerAccept = true;
        }
        Service.gI().sendThongBao(player, "Vật phẩm đã được chuyển vào túi của bạn");
    }

    private void destroyTransaction() {
        this.lastTimeCreated = System.currentTimeMillis() + timeWaitGiaoDich;
        this.isPlayerAcceptLock = false;
        this.isPlayerRequestLock = false;
        this.dispose();
        // remove this self
    }

    public boolean canRemoveGiaoDich() {
        return getStatus() != 0 && takenItemPlayerRequest && takenItemPlayerAccept;
    }

    private String getItemTrade() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ITransaction iTransaction : iTransactions) {
            if (iTransaction instanceof DanDuoc danDuoc) {
                stringBuilder.append("|5|").append("[").append(danDuoc.getNameByCap()).append("]").append(danDuoc.tenDanDuoc).append("x").append(danDuoc.quantity).append("\n");
                // handle process for dan duoc
            } else if (iTransaction instanceof DanPhuong) {
                DanPhuong danPhuong = (DanPhuong) iTransactions;
                stringBuilder.append("|7|").append(danPhuong.tenDanPhuong).append("x1").append("\n");
                // handle process for dan phuong
            }
        }
        return stringBuilder.toString();
    }

    private String getItemReceived() {
        StringBuilder stringBuilder = new StringBuilder();
        for (ITransaction iTransaction : iTransactionsAccept) {
            if (iTransaction instanceof DanDuoc danDuoc) {
                stringBuilder.append("|5|").append("[").append(danDuoc.getNameByCap()).append("]").append(danDuoc.tenDanDuoc).append("x").append(danDuoc.quantity).append("\n");
                // handle process for dan duoc
            } else if (iTransaction instanceof DanPhuong) {
                DanPhuong danPhuong = (DanPhuong) iTransactions;
                stringBuilder.append("|7|").append(danPhuong.tenDanPhuong).append("x1").append("\n");
                // handle process for dan phuong
            }
        }
        return stringBuilder.toString();
    }

    public void addItems(ITransaction iTransaction) {
        this.iTransactions.add(iTransaction);
    }

    public void addItemsReceived(ITransaction iTransaction) {
        this.iTransactions.add(iTransaction);
    }
}
