
package networkmultitool;

import javax.swing.SwingUtilities;

public class NetworkMultiTool {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });

    }
}
