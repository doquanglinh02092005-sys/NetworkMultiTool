
package networkmultitool;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class TestServer {

    private static final int PORT = 9000;

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("=== TCP TEST SERVER ===");
            System.out.println("Server dang lang nghe tai cong: " + PORT);

            while (true) {

                Socket client = server.accept();

                System.out.println(
                    "Client ket noi: " + client.getInetAddress()
                );

                Thread thread = new Thread(() -> xuLyClient(client));
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Loi Server: " + e.getMessage());
        }
    }

    private static void xuLyClient(Socket client) {

        try (
            Socket socket = client;

            BufferedReader input = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream(),
                    StandardCharsets.UTF_8
                )
            );

            PrintWriter output = new PrintWriter(
                new OutputStreamWriter(
                    socket.getOutputStream(),
                    StandardCharsets.UTF_8
                ),
                true
            )
        ) {

            socket.setSoTimeout(10000);

            String message;

            while ((message = input.readLine()) != null) {

                System.out.println("Nhan: " + message);

                if (message.trim().equalsIgnoreCase("PING")) {

                    output.println("PONG");
                    System.out.println("Gui: PONG");

                } else {

                    output.println("UNKNOWN_COMMAND");
                }
            }

        } catch (IOException e) {
            System.out.println(
                "Ket thuc ket noi: " + e.getMessage()
            );
        }
    }
}
