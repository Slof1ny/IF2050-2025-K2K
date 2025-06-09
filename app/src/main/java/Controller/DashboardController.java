package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class DashboardController {
    public static String loggedRole = "";
    public static String loggedName = "";

    @FXML
    private Button userButton;

    public static void setLoginInfo(String role, String name) {
        loggedRole = role;
        loggedName = name;
    }

    @FXML
    public void initialize() {
        // Set nama dan role pada button user jika sudah login
        if (userButton != null && !loggedRole.isEmpty() && !loggedName.isEmpty()) {
            userButton.setText(loggedRole + ": " + loggedName);
        }
    }
}
