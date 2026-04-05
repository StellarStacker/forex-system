package forex_trading;

import javax.swing.SwingUtilities;

public class Forex_trading {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApp app = new MainApp();
            app.setVisible(true);
        });
    }
}
