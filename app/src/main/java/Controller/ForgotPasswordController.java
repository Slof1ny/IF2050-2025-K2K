package Controller;

import Database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ForgotPasswordController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private Button resetButton;
    @FXML
    private Label notificationLabel;

    @FXML
    public void handleResetPassword() {
        String username = usernameField.getText();
        String newPassword = newPasswordField.getText();
        if (username.isEmpty() || newPassword.isEmpty()) {
            showNotification("Semua field harus diisi.");
            return;
        }
        Database db = new Database();
        boolean updated = db.resetPasswordIfUserExists(username, newPassword);
        if (updated) {
            showNotification("Password berhasil direset. Silakan login.");
        } else {
            showNotification("Username tidak ditemukan di database.");
        }
    }

    @FXML
    public void handleBackToLogin() {
        try {
            Stage stage = (Stage) resetButton.getScene().getWindow();
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/LoginView.fxml"));
            stage.setScene(new Scene(loginRoot, 950, 600));
            stage.setTitle("Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showNotification(String message) {
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);
    }
}
