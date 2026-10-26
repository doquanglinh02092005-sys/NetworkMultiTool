
package networkmultitool;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class PingTool {

    public static String ping(String host, int port) {

        if (host == null || host.trim().isEmpty()
                || port < 1 || port > 65535) {
            return "Invalid IP/Host or Port!";
        }

        String ip;

        try {
            ip = InetAddress.getByName(host).getHostAddress();
        } catch (Exception e) {
            return "Cannot resolve host: " + host;
        }

        StringBuilder result = new StringBuilder();

        int sent = 0;
        int received = 0;

        double min = Double.MAX_VALUE;
        double max = 0;
        double total = 0;

        result.append("Pinging ")
              .append(host)
              .append(" [")
              .append(ip)
              .append("] using TCP port ")
              .append(port)
              .append(":\n\n");

        for (int i = 1; i <= 4; i++) {

            if (Thread.currentThread().isInterrupted()) {
                result.append("Ping stopped.\n");
                break;
            }

            sent++;

            try (Socket socket = new Socket()) {

                socket.connect(
                    new InetSocketAddress(ip, port), 2000
                );

                socket.setSoTimeout(2000);

                PrintWriter output = new PrintWriter(
                    socket.getOutputStream(),
                    true,
                    StandardCharsets.UTF_8
                );

                BufferedReader input = new BufferedReader(
                    new InputStreamReader(
                        socket.getInputStream(),
                        StandardCharsets.UTF_8
                    )
                );

                long start = System.nanoTime();

                output.println("PING");

                String reply = input.readLine();

                long end = System.nanoTime();

                if ("PONG".equalsIgnoreCase(reply)) {

                    double time = (end - start) / 1_000_000.0;

                    received++;
                    total += time;

                    min = Math.min(min, time);
                    max = Math.max(max, time);

                    result.append(String.format(
                        Locale.US,
                        "Reply from %s: time=%.2f ms\n",
                        ip, time
                    ));

                } else {
                    result.append(
                        "Invalid TCP response from " + ip + "\n"
                    );
                }

            } catch (SocketTimeoutException e) {

                result.append("Request timed out.\n");

            } catch (Exception e) {

                result.append("TCP connection failed: ")
                      .append(e.getMessage())
                      .append("\n");
            }

            if (i < 4) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    result.append("Ping stopped.\n");
                    break;
                }
            }
        }

        int lost = sent - received;
        int lossPercent = sent == 0 ? 0 : lost * 100 / sent;

        result.append("\nTCP Ping statistics for ")
              .append(ip)
              .append(":\n");

        result.append("    Packets: Sent = ")
              .append(sent)
              .append(", Received = ")
              .append(received)
              .append(", Lost = ")
              .append(lost)
              .append(" (")
              .append(lossPercent)
              .append("% loss)\n");

        if (received > 0) {

            result.append("\nApproximate round trip times:\n");

            result.append(String.format(
                Locale.US,
                "    Minimum = %.2f ms\n", min
            ));

            result.append(String.format(
                Locale.US,
                "    Maximum = %.2f ms\n", max
            ));

            result.append(String.format(
                Locale.US,
                "    Average = %.2f ms\n",
                total / received
            ));
        }

        return result.toString();
    }

    
    
    
    
public static String connectPing(String host, int port) {

    if (host == null || host.trim().isEmpty()) {
        return "Vui long nhap IP hoac Host!";
    }

    if (port < 1 || port > 65535) {
        return "Port khong hop le!";
    }

    String ip;

    try {
        ip = java.net.InetAddress.getByName(host)
                .getHostAddress();
    } catch (Exception e) {
        return "Khong tim thay Host: " + host;
    }

    StringBuilder result = new StringBuilder();

    int sent = 0;
    int received = 0;

    double min = Double.MAX_VALUE;
    double max = 0;
    double total = 0;

    result.append("TCP Connect Ping ")
          .append(host)
          .append(" [")
          .append(ip)
          .append("] port ")
          .append(port)
          .append(":\n\n");

    for (int i = 1; i <= 4; i++) {

        if (Thread.currentThread().isInterrupted()) {
            result.append("\nPing stopped.\n");
            break;
        }

        sent++;

        try (java.net.Socket socket = new java.net.Socket()) {

            long start = System.nanoTime();

            socket.connect(
                new java.net.InetSocketAddress(ip, port),
                2000
            );

            long end = System.nanoTime();

            double time = (end - start) / 1_000_000.0;

            received++;
            total += time;

            min = Math.min(min, time);
            max = Math.max(max, time);

            result.append(String.format(
                java.util.Locale.US,
                "Reply from %s: TCP connected, time=%.2f ms\n",
                ip, time
            ));

        } catch (java.net.SocketTimeoutException e) {

            result.append("Request timed out.\n");

        } catch (java.io.IOException e) {

            result.append("Connection failed: ")
                  .append(e.getMessage())
                  .append("\n");
        }

        if (i < 4) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                result.append("\nPing stopped.\n");
                break;
            }
        }
    }

    int lost = sent - received;
    int lossPercent = sent == 0 ? 0 : lost * 100 / sent;

    result.append("\nTCP Connect Ping statistics:\n");

    result.append("Packets: Sent = ")
          .append(sent)
          .append(", Received = ")
          .append(received)
          .append(", Lost = ")
          .append(lost)
          .append(" (")
          .append(lossPercent)
          .append("% loss)\n");

    if (received > 0) {

        result.append("\nConnection times:\n");

        result.append(String.format(
            java.util.Locale.US,
            "Minimum = %.2f ms\n", min
        ));

        result.append(String.format(
            java.util.Locale.US,
            "Maximum = %.2f ms\n", max
        ));

        result.append(String.format(
            java.util.Locale.US,
            "Average = %.2f ms\n",
            total / received
        ));
    }

    return result.toString();
}

    public static void main(String[] args) {
        System.out.println(
        connectPing("google.com", 443)
    );
    }
}
