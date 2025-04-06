package com.girlkun.server;

import com.girlkun.data.DataGame;
import com.girlkun.database.GirlkunDB;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;

import com.girlkun.jdbc.daos.HistoryTransactionDAO;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.item.Item;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.map.challenge.MartialCongressManager;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.network.server.GirlkunSessionManager;
import com.girlkun.network.session.ISession;
import com.girlkun.network.example.MessageSendCollect;
import com.girlkun.network.server.GirlkunServer;
import com.girlkun.network.server.IServerClose;
import com.girlkun.network.server.ISessionAcceptHandler;
import com.girlkun.server.io.MyKeyHandler;
import com.girlkun.server.io.MySession;
import com.girlkun.models.kygui.ShopKyGuiManager;
import com.girlkun.panel.JFramePanel;
import com.girlkun.services.ClanService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NgocRongNamecService;
import com.girlkun.services.Service;
import com.girlkun.services.func.TaiXiu;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;

import java.net.Socket;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;

public class ServerManager {

    public static String timeStart;

    public ServerSocket panelSocket;
    public Socket panelClient;
    public static final Map CLIENTS = new HashMap();

    public static String NAME = "Girlkun75";
    public static int PORT = 14445;

    private static ServerManager instance;

    public static ServerSocket listenSocket;
    public static boolean isRunning;
    public static long delaylogin;

    public void init() {
        Manager.gI();
        try {
            if (Manager.LOCAL) {
                return;
            }
            GirlkunDB.executeUpdate("update account set last_time_login = '2000-01-01', "
                    + "last_time_logout = '2001-01-01'");
        } catch (Exception e) {
        }
        HistoryTransactionDAO.deleteHistory();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        ServerManager.gI().run();
//        JFramePanel.main(args);
    }

    public void activePanelControllerApi() {
        try {
            panelSocket = new ServerSocket(8888, 50, InetAddress.getByName("0.0.0.0"));
            Logger.log(Logger.CYAN, "Server API is waiting on port 8888\n");
            while (isRunning) {
                Socket client = panelSocket.accept();
                panelClient = client;
                Logger.log("New client connected: " + client.getInetAddress().getHostAddress());
                BufferedReader reader = new BufferedReader(new InputStreamReader(panelClient.getInputStream(), "UTF-8"));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(panelClient.getOutputStream(), "UTF-8"));
                new Thread(() -> {
                    try {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String command = line.split(":")[0];
                            if (command == null) {
                                continue;
                            }
                            switch (command) {
                                case PanelCommand.CMD_GET_INFO:
                                    // send data
                                    writer.write(String.format("DATA:%s:%s:%s:%s:%s:%s",
                                            GirlkunSessionManager.gI().getSessions().size(), Client.gI().getPlayers().size(), Thread.activeCount(), Manager.CONTENT_SUKIEN[Manager.SUKIEN], Manager.KHUYEN_MAI_NAP, Manager.RATE_EXP_SERVER));
                                    writer.newLine();
                                    writer.flush();
                                    break;
                                case PanelCommand.CMD_KICK_OUT:
                                    Client.gI().close();
                                    break;
                                case PanelCommand.CMD_NOTIFY:
                                    // get message first
                                    String[] messages = line.split(":");
                                    Message msg = new Message(93);
                                    try {
                                        msg.writer().writeUTF(messages[1]);
                                    } catch (IOException ex) {
                                        Logger.error(ex.getMessage());
                                    }
                                    Service.getInstance().sendMessAllPlayer(msg);
                                    msg.cleanup();
                                    break;
                                case PanelCommand.CMD_MAINTAIN:
                                    // start maintain progress
                                    Maintenance.gI().maintenance30Second();
                                    break;
                                case PanelCommand.CMD_SET_EVENT:
                                    String[] sukiens = line.split(":");
                                    byte sukien = Byte.parseByte(sukiens[1]);
                                    if (sukien < 0) {
                                        return;
                                    }
                                    Manager.SUKIEN = sukien;
                                    if (sukien > 0) {
                                        Service.getInstance().sendThongBaoAllPlayer(String.format("|7|Sự kiện %s đang được diễn ra"
                                                + "\n|5|Thông tin chi tiết Sự kiện vui lòng tự tìm kiếm", Manager.CONTENT_SUKIEN[sukien]));
                                    }
                                    break;
                                case PanelCommand.CMD_SET_EXP:
                                    String[] exps = line.split(":");
                                    byte exp = Byte.parseByte(exps[1]);
                                    if (exp <= 0) {
                                        return;
                                    }
                                    Manager.RATE_EXP_SERVER = exp;
                                    Service.gI().sendThongBaoAllPlayer("Tỷ lệ exp server đã thay đổi thành x" + Manager.RATE_EXP_SERVER);
                                    break;
                                case PanelCommand.CMD_SET_TRADE_RATE:
                                    String[] trades = line.split(":");
                                    short trade = Short.parseShort(trades[1]);
                                    if (trade <= 0) {
                                        return;
                                    }
                                    Manager.KHUYEN_MAI_NAP = trade;
                                    Service.gI().sendThongBaoAllPlayer("Tỷ lệ quy đổi đã thay đổi thành x" + Manager.RATE_EXP_SERVER);
                                    break;
                            }
                        }
                    } catch (Exception e) {

                    }
                }).start();
            }
        } catch (Exception e) {
            Logger.error("Error when init panel server socket " + e.getMessage());
        }
    }

    public void run() {
        long delay = 500;
        delaylogin = System.currentTimeMillis();
        isRunning = true;
        activeCommandLine();
        activeGame();
        activeServerSocket();
        activePanelControllerApi();
        Logger.log(Logger.PURPLE_BOLD_BRIGHT, "░░░░░░░░░░░░▄▄\n░░░░░░░░░░░█░░█\n░░░░░░░░░░░█░░█\n░░░░░░░░░░█░░░█\n░░░░░░░░░█░░░░█\n███████▄▄█░░░░░██████▄\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█████░░░░░░░░░█\n██████▀░░░░▀▀██████▀\n");
        new Thread(DaiHoiVoThuat.gI(), "Thread DHVT").start();
//        ChonAiDay.gI().lastTimeEnd = System.currentTimeMillis() + 300000;
//        new Thread(ChonAiDay.gI() , "Thread ChonAiDay").start();
        TaiXiu.gI().lastTimeEnd = System.currentTimeMillis() + 50000;
        new Thread(TaiXiu.gI(), "Thread TaiXiu").start();

        NgocRongNamecService.gI().initNgocRongNamec((byte) 0);

        new Thread(NgocRongNamecService.gI(), "Thread NRNM").start();

        new Thread(TopService.gI(), "Thread TOP").start();

        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    MartialCongressManager.gI().update();
                    ShopKyGuiManager.gI().save();
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    System.out.println("qwert");
                }
            }
        }, "Update dai hoi vo thuat").start();


        try {
            Thread.sleep(1000);
            BossManager.gI().loadBoss();
            Manager.MAPS.forEach(com.girlkun.models.map.Map::initBoss);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(BossManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            MySession session = new MySession(new Socket(DataGame.LINK_IP_PORT.split(":")[1], 14445));
            session.version = 231;
            session.login("adminnotify", "linh2k1");
            if (session.isConnected() && session.player != null) {
                Manager.medusa = session.player;
                Manager.medusa.isBot = true;
            }
        } catch (Exception e) {
            Logger.logException(ServerManager.class, e, "lỗi load bot");
        }
    }

    private void act() throws Exception {
        GirlkunServer.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
                    @Override
                    public void sessionInit(ISession is) {
//                antiddos girlkun
                        if (!canConnectWithIp(is.getIP())) {
                            is.disconnect();
                            return;
                        }
                        is = is.setMessageHandler(Controller.getInstance())
                                .setSendCollect(new MessageSendCollect())
                                .setKeyHandler(new MyKeyHandler())
                                .startCollect();
                    }

                    @Override
                    public void sessionDisconnect(ISession session) {
                        Client.gI().kickSession((MySession) session);
                    }
                }).setTypeSessioClone(MySession.class)
                .setDoSomeThingWhenClose(new IServerClose() {
                    @Override
                    public void serverClose() {
                        System.out.println("server close");
                        System.exit(0);
                    }
                })
                .start(PORT);

    }

    private void activeServerSocket() {
        if (true) {
            try {
                this.act();
            } catch (Exception e) {
            }
            return;
        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(MySession session) {
        Object o = CLIENTS.get(session.getIP());
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.getIP(), n);
        }
    }

    private void activeCommandLine() {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                String line = sc.nextLine();
                if (line.equals("baotri")) {
                    Maintenance.gI().start(60 * 2);
                } else if (line.equals("athread")) {
                    ServerNotify.gI().notify("MEDUSA debug server: " + Thread.activeCount());
                } else if (line.equals("nplayer")) {
                    Logger.error("Player in game: " + Client.gI().getPlayers().size() + "\n");
                } else if (line.equals("admin")) {
                    new Thread(() -> {
                        Client.gI().close();
                    }).start();
                } else if (line.startsWith("bang")) {
                    new Thread(() -> {
                        try {
                            ClanService.gI().close();
                            Logger.error("Save " + Manager.CLANS.size() + " bang");
                        } catch (Exception e) {
                            Logger.error("Lỗi save clan!...................................\n");
                        }
                    }).start();
                } else if (line.startsWith("a")) {
                    String a = line.replace("a ", "");
                    Service.getInstance().sendThongBaoAllPlayer(a);
                } else if (line.startsWith("qua")) {
//                    qua=1-1-1-1=1-1-1-1=
//                     qua=playerId - soluong - itemId - so_saophale = optioneId - param=
                    try {
                        List<Item.ItemOption> ios = new ArrayList<>();
                        String[] pagram1 = line.split("=")[1].split("-");
                        String[] pagram2 = line.split("=")[2].split("-");
                        if (pagram1.length == 4 && pagram2.length % 2 == 0) {
                            Player p = Client.gI().getPlayer(Integer.parseInt(pagram1[0]));
                            if (p != null) {
                                for (int i = 0; i < pagram2.length; i += 2) {
                                    ios.add(new Item.ItemOption(Integer.parseInt(pagram2[i]), Integer.parseInt(pagram2[i + 1])));
                                }
                                Item i = Util.sendDo(Integer.parseInt(pagram1[2]), Integer.parseInt(pagram1[3]), ios);
                                i.quantity = Integer.parseInt(pagram1[1]);
                                InventoryServiceNew.gI().addItemBag(p, i);
                                InventoryServiceNew.gI().sendItemBags(p);
                                Service.getInstance().sendThongBao(p, "Admin trả đồ. anh em thông cảm nhé...");
                            } else {
                                System.out.println("Người chơi không online");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Lỗi quà");
                    }
                } else if (line.equals("clean")) {
                    System.gc();
                    System.err.println("Clean.........");
                }
            }
        }, "Active line").start();
    }

    private void activeGame() {
    }

    public void close(long delay) {
        GirlkunServer.gI().stopConnect();

        isRunning = false;
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!...................................\n");
        }
        ShopKyGuiManager.gI().save();
        Client.gI().close();
        Logger.success("SUCCESSFULLY MAINTENANCE!...................................\n");
        System.exit(0);
    }
}
