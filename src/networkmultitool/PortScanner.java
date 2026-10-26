
package networkmultitool;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Locale;

public class PortScanner {

    private static final int TIMEOUT = 600;

    public static String scan(String host, int startPort, int endPort) {

        StringBuilder result = new StringBuilder();

        if (host == null || host.trim().isEmpty()) {
            return "Vui long nhap IP hoac Host!";
        }

        if (startPort < 1 || endPort > 65535
                || startPort > endPort) {
            return "Khoang Port khong hop le!";
        }

        if (endPort - startPort + 1 > 1000) {
            return "Moi lan chi quet toi da 1000 cong TCP!";
        }

        String ip;

        try {
            ip = InetAddress.getByName(host).getHostAddress();
        } catch (Exception e) {
            return "Khong tim thay Host: " + host;
        }

        int open = 0;
        int closed = 0;
        int filtered = 0;
        int errors = 0;
        int scanned = 0;

        result.append("=== TCP PORT SCANNER ===\n");
        result.append("Host: ").append(host).append("\n");
        result.append("IP Address: ").append(ip).append("\n");
        result.append("Port Range: ")
              .append(startPort)
              .append(" - ")
              .append(endPort)
              .append("\n\n");

        result.append("PORT\tSTATE\tDETAILS\n");
        result.append("--------------------------------------\n");

        for (int port = startPort; port <= endPort; port++) {

            if (Thread.currentThread().isInterrupted()) {
                result.append("\nScan stopped by user.\n");
                break;
            }

            scanned++;

            try (Socket socket = new Socket()) {

                long start = System.nanoTime();

                socket.connect(
                    new InetSocketAddress(ip, port),
                    TIMEOUT
                );

                long end = System.nanoTime();

                double time = (end - start) / 1_000_000.0;

                open++;

                result.append(String.format(
                    Locale.US,
                    "%d/tcp\tOPEN\tConnected in %.2f ms\n",
                    port, time
                ));

            } catch (ConnectException e) {

                closed++;

                result.append(port)
                      .append("/tcp\tCLOSED\tConnection refused\n");

            } catch (SocketTimeoutException e) {

                filtered++;

                result.append(port)
                      .append("/tcp\tFILTERED\tRequest timed out\n");

            } catch (NoRouteToHostException e) {

                filtered++;

                result.append(port)
                      .append("/tcp\tUNREACHABLE\tNo route to host\n");

            } catch (IOException e) {

                errors++;

                result.append(port)
                      .append("/tcp\tERROR\t")
                      .append(e.getMessage())
                      .append("\n");
            }
        }

        result.append("\n=== SCAN SUMMARY ===\n");
        result.append("Ports scanned: ").append(scanned).append("\n");
        result.append("OPEN: ").append(open).append("\n");
        result.append("CLOSED: ").append(closed).append("\n");
        result.append("FILTERED/UNREACHABLE: ")
              .append(filtered).append("\n");
        result.append("ERROR: ").append(errors).append("\n");

        result.append("\nScan finished.\n");

        return result.toString();
    }

    public static void main(String[] args) {

        String result = scan(
            "127.0.0.1",
            8998,
            9002
        );

        System.out.println(result);
    }
}
