package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private CheckBox rememberMeCheck;

    @FXML
    private ComboBox<String> roleDropdown;

    @FXML
    private Button signInButton;

    @FXML
    void handleSignIn() {
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleDropdown.getValue();
        boolean rememberMe = rememberMeCheck.isSelected();

        if (email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Login Failed", "Email, password, and role must be filled.");
            return;
        }

        // Demo output
        String message = String.format(
            "Login attempt:\nEmail: %s\nRole: %s\nRemember Me: %s",
            email, role, rememberMe
        );
        System.out.println(message);
        showAlert("Login Successful", message);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}