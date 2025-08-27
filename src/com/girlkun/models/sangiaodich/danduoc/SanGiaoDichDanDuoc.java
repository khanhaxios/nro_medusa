/*
 * Copyright (c) 2025. Code By KanDev if u want share this source pla don't remove this copy right
 */

package com.girlkun.models.sangiaodich.danduoc;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.player.Player;
import com.girlkun.services.NpcService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SanGiaoDichDanDuoc implements Runnable {
    public static boolean IS_RUNNING = true;
    private long lastTimeUpdate;
    public List<Transaction> transactions = new ArrayList<>();
    private static SanGiaoDichDanDuoc I;

    public static SanGiaoDichDanDuoc getI() {
        if (I == null) {
            I = new SanGiaoDichDanDuoc();
        }
        return I;
    }

    public void showBaseMenu(Player player) {
        List<Transaction> playerTransaction = getListTransactionByPlayer(player);
        String menuText = "|7|Sàn Giao Dịch Đan Dược" + "\n" +
                getListTransactionInfo(playerTransaction);
        String[] options = new String[]{"Mở Giao\nDịch", "Hủy Giao\nDịch", "Chi Tiết\nGiao Dịch"};
        NpcService.gI().createMenuConMeo(player, ConstNpc.BASE_MENU_GD, -1, menuText, options);
    }

    private String getListTransactionInfo(List<Transaction> playerTransaction) {
        if (playerTransaction == null || playerTransaction.isEmpty()) {
            return "|1|Không có giao dịch nào!";
        }

        StringBuilder sb = new StringBuilder();
        for (Transaction transaction : playerTransaction) {
            sb.append("|5|[")
                    .append(transaction.code)
                    .append("]==>")
                    .append(transaction.getStatusString())
                    .append("\n");
        }
        return sb.toString().trim();
    }

    public List<Transaction> getListTransactionByPlayer(Player player) {
        List<Transaction> transactions1 = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.playerRequest != null && transaction.playerAccept != null) {
                if (transaction.playerRequest.id == player.id) {
                    transactions1.add(transaction);
                }
            }
        }
        return transactions1;
    }

    @Override
    public void run() {
        while (IS_RUNNING) {
            try {
                Iterator<Transaction> iterator = transactions.iterator();
                while (iterator.hasNext()) {
                    Transaction giaoDich = iterator.next();
                    if (giaoDich.canRemoveGiaoDich()) {
                        iterator.remove();
                    }
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean canTakeCode(String maGiaoDich) {
        boolean canUse = true;
        for (Transaction transaction : transactions) {
            if (Objects.equals(transaction.code, maGiaoDich)) {
                canUse = false;
                break;
            }
        }
        return canUse;
    }

    public Transaction getById(String maGiaoDich) {
        for (Transaction transaction : transactions) {
            if (Objects.equals(transaction.code, maGiaoDich)) {
                return transaction;
            }
        }
        return null;
    }

    public void huyGiaoDich(Transaction transaction, Player player) {
        transaction.huyGiaoDich(player);
    }

    public void xemInfo(Transaction transaction, Player player) {
        transaction.showBaseMenu(player);
    }
}
