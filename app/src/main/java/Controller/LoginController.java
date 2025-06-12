package Controller;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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

public class LoginController {

    @FXML
    private TextField nameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private CheckBox rememberMeCheck;
    @FXML
    private ComboBox<RoleItem> roleDropdown;
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
        String customerIcon = "M12 2.25c-5.206 0-9.45 4.244-9.75 9.428-.003.085-.003.17-.003.255 0 5.206 4.244 9.45 9.428 9.75.085.003.17.003.255.003 5.206 0 9.45-4.244 9.75-9.428.003-.085.003-.17.003-.255 0-5.206-4.244-9.45-9.428-9.75A9.686 9.686 0 0 0 12 2.25Zm0 17.25a7.5 7.5 0 0 1 0-15c4.136 0 7.5 3.364 7.5 7.5 0 4.136-3.364 7.5-7.5 7.5Zm-.75-7.5a.75.75 0 0 0 .75-.75V8.25a.75.75 0 0 0-1.5 0v4.5a.75.75 0 0 0 .75.75ZM12 7.5a.75.75 0 0 0 0-1.5.75.75 0 0 0 0 1.5Zm3.75 9a.75.75 0 0 0 0-1.5H10.5a.75.75 0 0 0 0 1.5h5.25Z";
        String doctorIcon = "M15.75 6a3.75 3.75 0 1 1-7.5 0 3.75 3.75 0 0 1 7.5 0ZM4.501 20.118a7.5 7.5 0 0 1 14.998 0A17.933 17.933 0 0 1 12 21.75c-2.676 0-5.216-.584-7.499-1.632Z M12 12.75a.75.75 0 0 0 .75-.75V8.25a.75.75 0 0 0-1.5 0V12a.75.75 0 0 0 .75.75Z M13.5 14.25a.75.75 0 0 0 0-1.5H10.5a.75.75 0 0 0 0 1.5h3Z";
        String adminIcon = "M17.982 18.725A7.488 7.488 0 0 0 12 15.75a7.488 7.488 0 0 0-5.982 2.975m11.963 0a9 9 0 1 0-11.963 0m11.963 0A8.966 8.966 0 0 1 12 21a8.966 8.966 0 0 1-5.982-2.275M15 9.75a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z";

        // Isi ComboBox dengan objek RoleItem
        roleDropdown.setItems(FXCollections.observableArrayList(
            new RoleItem("Customer", customerIcon),
            new RoleItem("Doctor", doctorIcon),
            new RoleItem("Admin", adminIcon)
        ));

        // Kustomisasi tampilan item di ComboBox
        Callback<ListView<RoleItem>, ListCell<RoleItem>> cellFactory = new Callback<ListView<RoleItem>, ListCell<RoleItem>>() {
            @Override
            public ListCell<RoleItem> call(ListView<RoleItem> param) {
                return new ListCell<RoleItem>() {
                    @Override
                    protected void updateItem(RoleItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            setGraphic(createRoleItemGraphic(item));
                        }
                    }
                };
            }
        };

        roleDropdown.setCellFactory(cellFactory);
        // Fix button cell creation
        roleDropdown.setButtonCell(new ListCell<RoleItem>() {
            @Override
            protected void updateItem(RoleItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setGraphic(createRoleItemGraphic(item));
                }
            }
        });
    }

    private HBox createRoleItemGraphic(RoleItem item) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        SVGPath icon = new SVGPath();
        icon.setContent(item.getSvgIconPath());
        icon.setFill(Color.web("#546e7a"));
        
        StackPane iconContainer = new StackPane(icon);
        iconContainer.setPrefSize(18, 18);

        Label label = new Label(item.getName());
        
        hbox.getChildren().addAll(iconContainer, label);
        return hbox;
    }

    @FXML
    void handleSignIn() {
        String name = nameField.getText().trim();
        String password = passwordField.getText();
        RoleItem selectedRole = roleDropdown.getValue();

        if (name.isEmpty() || password.isEmpty() || selectedRole == null) {
            showAlert("Validation Error", "Semua field harus diisi!");
            return;
        }

        String role = selectedRole.getName();
        boolean loginSuccessful = validateLogin(name, password, role);

        if (!loginSuccessful) {
            showAlert("Login Failed", "Nama atau password salah untuk role " + role + ".");
            return;
        }        // Set info login ke DashboardController
        Controller.DashboardController.setLoginInfo(role, name);

        // Navigate to appropriate view based on role
        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();
            String fxmlFile;
            String windowTitle;
            
            switch (role) {
                case "Admin":
                    fxmlFile = "/fxml/AdminView.fxml";
                    windowTitle = "TemuOptic Admin Dashboard";
                    break;
                case "Doctor":
                    fxmlFile = "/fxml/DoctorDashboardView.fxml";
                    windowTitle = "TemuOptic Doctor Dashboard";
                    break;
                case "Customer":
                default:
                    fxmlFile = "/fxml/DashboardView.fxml";
                    windowTitle = "TemuOptic Dashboard";
                    break;
            }
            
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource(fxmlFile));
            Scene dashboardScene = new Scene(dashboardRoot, 1000, 600);
            stage.setScene(dashboardScene);
            stage.setTitle(windowTitle);
            stage.centerOnScreen(); // Posisikan jendela di tengah layar
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Gagal memuat halaman dashboard: " + e.getMessage());
        }
    }

    private boolean validateLogin(String name, String password, String role) {
        Database.Database db = new Database.Database();
        
        switch (role) {
            case "Customer":
                return db.authenticatePelanggan(name, password);
            case "Doctor":
                return db.authenticateDokter(name, password);
            case "Admin":
                return db.authenticateAdmin(name, password);
            default:
                return false;
        }
    }

    @FXML
    void handleForgotPassword() {
        try {
            Stage stage = (Stage) signInButton.getScene().getWindow();
            Parent forgotPasswordRoot = FXMLLoader.load(getClass().getResource("/fxml/ForgotPasswordView.fxml"));
            stage.setScene(new Scene(forgotPasswordRoot, 950, 600));
            stage.setTitle("Forgot Password");
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
