package Controller;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class LoginController {

    // DIUBAH: Dari emailField menjadi nameField
    @FXML
    private TextField nameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheck;

    @FXML
    private ComboBox<String> roleDropdown;

    @FXML
    private Button signInButton;

    // DITAMBAHKAN: Variabel untuk pop-up notifikasi
    @FXML
    private Label notificationLabel;

    @FXML
    void handleSignIn() {
        // DIUBAH: Validasi menggunakan nama
        String name = nameField.getText();
        String password = passwordField.getText();
        String role = roleDropdown.getValue();
        boolean rememberMe = rememberMeCheck.isSelected();

        // DIUBAH: Kondisi validasi
        if (name.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Login Failed", "Nama, password, dan role tidak boleh kosong.");
            return;
        }

        // DIUBAH: Output pesan menggunakan nama
        String message = String.format(
            "Login attempt:\nNama: %s\nRole: %s\nRemember Me: %s",
            name, role, rememberMe
        );
        System.out.println(message);
        showAlert("Login Successful", message);
    }
    
    // DITAMBAHKAN: Method untuk menangani "Forgot Password"
    @FXML
    void handleForgotPassword() {
        // Set pesan notifikasi
        notificationLabel.setText("Silahkan hubungi admin untuk kendala ini");
        
        // Atur posisi awal (di atas layar)
        notificationLabel.setTranslateY(-50); // Tinggi label
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);

        // Animasi turun
        TranslateTransition slideDown = new TranslateTransition(Duration.millis(500), notificationLabel);
        slideDown.setToY(0);

        // Jeda selama 3 detik
        PauseTransition pause = new PauseTransition(Duration.seconds(3));

        // Animasi naik kembali
        TranslateTransition slideUp = new TranslateTransition(Duration.millis(500), notificationLabel);
        slideUp.setToY(-50);

        // Gabungkan semua animasi
        SequentialTransition sequence = new SequentialTransition(slideDown, pause, slideUp);
        
        // Setelah selesai, sembunyikan label lagi
        sequence.setOnFinished(e -> {
            notificationLabel.setVisible(false);
            notificationLabel.setManaged(false);
        });
        
        sequence.play();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
