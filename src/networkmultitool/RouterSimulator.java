
package networkmultitool;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class RouterSimulator {

    private static final int[] PORTS = {
        9101, 9102, 9103
    };

    public static void main(String[] args) {

        System.out.println("=== ROUTER SIMULATOR ===");
        System.out.println("Mo phong 3 Router bang UDP Socket");

        for (int i = 1; i <= 3; i++) {

            final int hop = i;

            Thread thread = new Thread(() -> {
                chayRouter(hop);
            });

            thread.start();
        }
    }

    private static void chayRouter(int hop) {

        int port = PORTS[hop - 1];

        try (DatagramSocket socket = new DatagramSocket(port)) {

            System.out.println(
                "Router " + hop +
                " dang lang nghe UDP port " + port
            );

            while (true) {

                byte[] buffer = new byte[512];

                DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length
                );

                socket.receive(packet);

                String message = new String(
                    packet.getData(),
                    packet.getOffset(),
                    packet.getLength(),
                    StandardCharsets.UTF_8
                );

                String[] parts = message.split("\\|");

                if (parts.length < 2
                        || !parts[0].equals("TRACE")) {
                    continue;
                }

                try {

                    int ttl = Integer.parseInt(parts[1]);

                    InetAddress clientIP;
                    int clientPort;

                    if (parts.length == 2 && hop == 1) {

                        clientIP = packet.getAddress();
                        clientPort = packet.getPort();

                    } else if (parts.length == 4) {

                        clientIP = InetAddress.getByName(
                            parts[2]
                        );

                        clientPort = Integer.parseInt(
                            parts[3]
                        );

                    } else {
                        continue;
                    }

                    System.out.println(
                        "Router " + hop +
                        " nhan goi tin - TTL = " + ttl
                    );

                    if (ttl <= 1 || hop == 3) {

                        String type =
                            (hop == 3) ? "END" : "HOP";

                        String response =
                            type + "|" + hop + "|" + port;

                        byte[] data = response.getBytes(
                            StandardCharsets.UTF_8
                        );

                        DatagramPacket reply =
                            new DatagramPacket(
                                data,
                                data.length,
                                clientIP,
                                clientPort
                            );

                        socket.send(reply);

                        System.out.println(
                            "Router " + hop +
                            " gui phan hoi ve Client"
                        );

                    } else {

                        int nextPort = PORTS[hop];

                        String forwardMessage =
                            "TRACE|" + (ttl - 1)
                            + "|" + clientIP.getHostAddress()
                            + "|" + clientPort;

                        byte[] data = forwardMessage.getBytes(
                            StandardCharsets.UTF_8
                        );

                        DatagramPacket forward =
                            new DatagramPacket(
                                data,
                                data.length,
                                InetAddress.getLoopbackAddress(),
                                nextPort
                            );

                        socket.send(forward);

                        System.out.println(
                            "Router " + hop +
                            " chuyen tiep den port " + nextPort
                        );
                    }

                } catch (Exception e) {

                    System.out.println(
                        "Goi tin khong hop le: "
                        + e.getMessage()
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                "Loi Router " + hop + ": "
                + e.getMessage()
            );
        }
    }
}
