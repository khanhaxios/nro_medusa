import java.io.*;
import java.net.Socket;

public class ClientSendMaintain {

    public static void main(String[] args) {
        String host = "127.0.0.1"; // hoặc IP của VPS nếu chạy từ máy khác
        int port = 8888;

        try (Socket socket = new Socket(host, port)) {
            System.out.println("✅ Connected to server panel API");

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Gửi command bảo trì
            String command = "M_MAINTAIN";
            writer.write(command);
            writer.newLine();
            writer.flush();
            System.out.println("📤 Command sent: " + command);
	    socket.close();

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }
}
