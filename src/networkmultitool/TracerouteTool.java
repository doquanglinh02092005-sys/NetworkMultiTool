
package networkmultitool;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class TracerouteTool {

    private static final int ROUTER_PORT = 9101;
    private static final int MAX_HOPS = 3;

    public static String trace(String targetHost, String routerHost) {

        StringBuilder result = new StringBuilder();

        if (targetHost == null || targetHost.trim().isEmpty()
                || routerHost == null || routerHost.trim().isEmpty()) {
            return "Vui long nhap IP/Host va IP Router!";
        }

        InetAddress routerAddress;

        try {
            routerAddress = InetAddress.getByName(routerHost);
        } catch (Exception e) {
            return "Khong tim thay Router: " + routerHost;
        }

        String routerIP = routerAddress.getHostAddress();

        result.append("=== UDP TRACEROUTE SIMULATION ===\n");
        result.append("Target: ").append(targetHost).append("\n");
        result.append("Router Simulator: ").append(routerIP).append("\n");
        result.append("Maximum hops: ").append(MAX_HOPS).append("\n\n");

        int completedHops = 0;

        for (int ttl = 1; ttl <= MAX_HOPS; ttl++) {

            if (Thread.currentThread().isInterrupted()) {
                result.append("\nTraceroute stopped.\n");
                break;
            }

            try (DatagramSocket socket = new DatagramSocket()) {

                socket.setSoTimeout(2000);

                String message = "TRACE|" + ttl;

                byte[] sendData = message.getBytes(
                    StandardCharsets.UTF_8
                );

                DatagramPacket sendPacket = new DatagramPacket(
                    sendData,
                    sendData.length,
                    routerAddress,
                    ROUTER_PORT
                );

                long start = System.nanoTime();

                socket.send(sendPacket);

                byte[] receiveData = new byte[512];

                DatagramPacket receivePacket = new DatagramPacket(
                    receiveData,
                    receiveData.length
                );

                socket.receive(receivePacket);

                long end = System.nanoTime();

                double time = (end - start) / 1_000_000.0;

                String response = new String(
                    receivePacket.getData(),
                    receivePacket.getOffset(),
                    receivePacket.getLength(),
                    StandardCharsets.UTF_8
                );

                String[] parts = response.split("\\|");

                if (parts.length == 3
                        && (parts[0].equals("HOP")
                            || parts[0].equals("END"))) {

                    int hopNumber = Integer.parseInt(parts[1]);
                    int hopPort = Integer.parseInt(parts[2]);

                    if (hopNumber != ttl) {
                        result.append(ttl)
                              .append("    Invalid hop response\n");
                        continue;
                    }

                    String hopName = "Router " + hopNumber;

                    if (parts[0].equals("END")) {
                        hopName += " (simulation endpoint)";
                    }

                    result.append(String.format(
                        Locale.US,
                        "%d    %s    [%s:%d]    time=%.2f ms\n",
                        ttl,
                        hopName,
                        routerIP,
                        hopPort,
                        time
                    ));

                    completedHops++;

                } else {

                    result.append(ttl)
                          .append("    Invalid response\n");
                }

            } catch (SocketTimeoutException e) {

                result.append(ttl)
                      .append("    * * * Request timed out\n");

            } catch (Exception e) {

                result.append(ttl)
                      .append("    Error: ")
                      .append(e.getMessage())
                      .append("\n");
            }

            if (ttl < MAX_HOPS) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result.append("\nTraceroute stopped.\n");
                    break;
                }
            }
        }

        result.append("\n");

        if (completedHops == MAX_HOPS) {
            result.append("Simulated route completed.\n");
        } else {
            result.append("Simulated route incomplete.\n");
        }

        result.append(
            "Note: This is an application-layer UDP route simulation,\n"
        );
        result.append(
            "not the actual IP route to the target.\n"
        );

        return result.toString();
    }

    public static void main(String[] args) {

        String result = trace(
            "127.0.0.1",
            "127.0.0.1"
        );

        System.out.println(result);
    }
}
