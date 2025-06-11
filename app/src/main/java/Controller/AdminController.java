package Controller;

import Database.Database;
import Model.Dokter;
import Model.Produk;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AdminController {
    @FXML
    private TableView<Produk> stokTable;
    @FXML
    private TableColumn<Produk, String> namaCol;
    @FXML
    private TableColumn<Produk, Double> hargaCol;
    @FXML
    private TableColumn<Produk, Integer> stokCol;
    @FXML
    private Button addProductButton, editProductButton, deleteProductButton;
    @FXML
    private TextField doctorNameField, doctorSpesialisField;
    @FXML
    private PasswordField doctorPasswordField;
    @FXML
    private Button addDoctorButton;
    @FXML
    private Label notificationLabel;

    private Database db = new Database();

    @FXML
    public void initialize() {
        // Inisialisasi kolom tabel produk
        namaCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNama()));
        hargaCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getHarga()));
        stokCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStok()));
        loadProdukToTable();
        notificationLabel.setVisible(false);
        notificationLabel.setManaged(false);
        addDoctorButton.setOnAction(e -> handleAddDoctor());
    }

    private void loadProdukToTable() {
        ObservableList<Produk> produkList = FXCollections.observableArrayList(db.getAllProduk());
        stokTable.setItems(produkList);
    }

    private void handleAddDoctor() {
        String nama = doctorNameField.getText();
        String spesialis = doctorSpesialisField.getText();
        String password = doctorPasswordField.getText();
        if (nama.isEmpty() || spesialis.isEmpty() || password.isEmpty()) {
            showNotification("Semua field dokter harus diisi.");
            return;
        }
        Dokter dokter = new Dokter(nama, spesialis, password);
        boolean success = db.addDokter(dokter);
        if (success) {
            showNotification("Dokter berhasil ditambahkan!");
            doctorNameField.clear();
            doctorSpesialisField.clear();
            doctorPasswordField.clear();
        } else {
            showNotification("Gagal menambah dokter. Nama mungkin sudah ada.");
        }
    }

    private void showNotification(String message) {
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);
        notificationLabel.setManaged(true);
    }
}
