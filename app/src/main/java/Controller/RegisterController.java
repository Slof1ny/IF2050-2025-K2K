package Controller;

import Database.Database;
import Model.Pelanggan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private Label notificationLabel;

    @FXML
    public void handleRegister() {
        String nama = nameField.getText();
        String email = emailField.getText();
        String noHp = phoneField.getText();
        String password = passwordField.getText();

        if (nama.isEmpty() || email.isEmpty() || noHp.isEmpty() || password.isEmpty()) {
            showNotification("Semua field harus diisi.");
            return;
        }

        Database db = new Database();
        Pelanggan pelanggan = new Pelanggan(nama, email, noHp, password);
        boolean success = db.addPelanggan(pelanggan);
        if (success) {
            showNotification("Registrasi berhasil! Silakan login.");
        } else {
            showNotification("Registrasi gagal. Email mungkin sudah terdaftar.");
        }
    }

    @FXML
    public void handleBackToLogin() {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
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
