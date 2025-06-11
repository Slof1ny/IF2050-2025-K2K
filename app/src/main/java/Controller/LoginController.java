package Controller;

import java.io.IOException;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

public class LoginController {

    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberMeCheck;
    @FXML
    private ComboBox<RoleItem> roleDropdown; // DIUBAH: Tipe ComboBox
    @FXML
    private Button signInButton;
    @FXML
    private Label notificationLabel;

    /**
     * DITAMBAHKAN: Metode initialize() dijalankan setelah FXML dimuat.
     * Kita menggunakannya untuk mengatur ComboBox kustom.
     */
    @FXML
    public void initialize() {
        setupRoleComboBox();
    }

    /**
     * DITAMBAHKAN: Kelas internal sederhana untuk menyimpan data peran (teks dan path ikon).
     */
    private static class RoleItem {
        private final String name;
        private final String svgIconPath;

        public RoleItem(String name, String svgIconPath) {
            this.name = name;
            this.svgIconPath = svgIconPath;
        }

        public String getName() {
            return name;
        }

        public String getSvgIconPath() {
            return svgIconPath;
        }

        // Override toString() agar ComboBox tahu teks apa yang harus ditampilkan secara default
        @Override
        public String toString() {
            return name;
        }
    }

    private void setupRoleComboBox() {
        String customerIcon = "M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z";
        String doctorIcon = "M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z M12 12.75a.75.75 0 0 0 .75-.75V8.25a.75.75 0 0 0-1.5 0V12a.75.75 0 0 0 .75.75Z M13.5 14.25a.75.75 0 0 0 0-1.5H10.5a.75.75 0 0 0 0 1.5h3Z";
        String adminIcon = "M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z";

        // Isi ComboBox dengan objek RoleItem
        roleDropdown.setItems(FXCollections.observableArrayList(
            new RoleItem("Customer", customerIcon),
            new RoleItem("Doctor", doctorIcon),
            new RoleItem("Admin", adminIcon)
        ));

        Callback<ListView<RoleItem>, ListCell<RoleItem>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(RoleItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Buat HBox untuk menampung ikon dan teks
                    HBox hbox = new HBox(10); 
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    SVGPath icon = new SVGPath();
                    icon.setContent(item.getSvgIconPath());
                    icon.setFill(Color.web("#546e7a")); // Warna ikon
                    
                    StackPane iconContainer = new StackPane(icon);
                    iconContainer.setPrefSize(18, 18);

                    Label label = new Label(item.getName());
                    
                    hbox.getChildren().addAll(iconContainer, label);
                    setGraphic(hbox);
                }
            }
        };

        roleDropdown.setCellFactory(cellFactory);
        roleDropdown.setButtonCell(cellFactory.call(null)); // Terapkan juga ke tombol yang terlihat
    }
    @FXML
    void handleSignIn() {
        String name = nameField.getText();
        String password = passwordField.getText();
        RoleItem selectedRole = roleDropdown.getValue();

        if (name.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert("Login Failed", "Nama, password, dan role tidak boleh kosong.");
            return;
        }

        // Autentikasi ke database sesuai role
        Database.Database db = new Database.Database();
        boolean isAuthenticated = false;
        String role = selectedRole.getName();
        if (role.equalsIgnoreCase("Customer")) {
            isAuthenticated = db.authenticatePelanggan(name, password);
        } else if (role.equalsIgnoreCase("Doctor")) {
            isAuthenticated = db.authenticateDokter(name, password);
        } else if (role.equalsIgnoreCase("Admin")) {
            isAuthenticated = db.authenticateAdmin(name, password);
        }

        if (!isAuthenticated) {
            showAlert("Login Failed", "Nama atau password salah untuk role " + role + ".");
            return;
        }

        // Set info login ke DashboardController
        Controller.DashboardController.setLoginInfo(role, name);

        // Jika berhasil, ke dashboard sesuai role
        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();
            Parent root;
            if (role.equalsIgnoreCase("Admin")) {
                root = FXMLLoader.load(getClass().getResource("/fxml/AdminView.fxml"));
            } else {
                root = FXMLLoader.load(getClass().getResource("/fxml/DashboardView.fxml"));
            }
            Scene scene = new Scene(root, 1000, 600);
            stage.setScene(scene);
            stage.setTitle(role.equalsIgnoreCase("Admin") ? "Admin Dashboard" : "TemuOptic Dashboard");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal memuat halaman dashboard.");
        }
    }
    @FXML
    void handleForgotPassword() {
        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();
            Parent forgotRoot = FXMLLoader.load(getClass().getResource("/fxml/ForgotPasswordView.fxml"));
            stage.setScene(new Scene(forgotRoot, 950, 600));
            stage.setTitle("Reset Password");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleRegisterLink() {
        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/fxml/RegisterView.fxml"));
            stage.setScene(new Scene(registerRoot, 950, 600));
            stage.setTitle("Register");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
