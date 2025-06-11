package Controller;

import Database.Database;
import Model.Dokter;
import Model.Produk;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminController {
    // Statistics Labels
    @FXML
    private Label totalProductsLabel, totalDoctorsLabel, totalOrdersLabel, totalRevenueLabel;
    
    // Product Grid Container
    @FXML
    private HBox productGridContainer;
    
    // Table Components
    @FXML
    private TableView<Produk> stokTable;
    @FXML
    private TableColumn<Produk, String> namaCol;
    @FXML
    private TableColumn<Produk, Double> hargaCol;
    @FXML
    private TableColumn<Produk, Integer> stokCol;
    @FXML
    private TableColumn<Produk, String> categoryCol;
    
    // Doctor Table Components
    @FXML
    private TableView<Dokter> doctorTable;
    @FXML
    private TableColumn<Dokter, String> doctorNameCol, doctorSpecCol, doctorEmailCol;
      // Buttons
    @FXML
    private Button addProductButton, editProductButton, deleteProductButton;
    @FXML
    private Button addNewProductButton;
    @FXML
    private Button addDoctorButton, editDoctorButton, removeDoctorButton;
    
    // Form Fields
    @FXML
    private TextField doctorNameField, doctorSpesialisField, doctorEmailField, doctorPhoneField;
    @FXML
    private PasswordField doctorPasswordField;
    
    // Notification
    @FXML
    private Label notificationLabel;    private Database db = new Database();    @FXML
    public void initialize() {
        try {
            System.out.println("AdminController initializing...");
            
            // Check if essential FXML components are loaded
            System.out.println("Checking FXML components:");
            System.out.println("- addDoctorButton: " + (addDoctorButton != null ? "OK" : "NULL"));
            System.out.println("- doctorNameField: " + (doctorNameField != null ? "OK" : "NULL"));
            System.out.println("- doctorSpesialisField: " + (doctorSpesialisField != null ? "OK" : "NULL"));
            System.out.println("- doctorPasswordField: " + (doctorPasswordField != null ? "OK" : "NULL"));
            System.out.println("- doctorTable: " + (doctorTable != null ? "OK" : "NULL"));
            
            // Initialize statistics
            loadStatistics();
            
            // Initialize table columns
            initializeTableColumns();
            
            // Load data to tables
            loadProdukToTable();
            loadDoctorToTable();
            
            // Load product cards
            loadProductCards();
            
            // Setup button handlers
            setupButtonHandlers();
            
            // Initialize notification
            if (notificationLabel != null) {
                notificationLabel.setVisible(false);
                notificationLabel.setManaged(false);
            }
            
            System.out.println("AdminController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadStatistics() {
        try {
            // Get statistics from database
            List<Produk> products = db.getAllProduk();
            List<Dokter> doctors = db.getAllDokter();

            // Update statistics labels
            totalProductsLabel.setText(String.valueOf(products.size()));
            totalDoctorsLabel.setText(String.valueOf(doctors.size()));

            // Total order selesai
            int totalOrderSelesai = db.getTotalOrderSelesai();
            totalOrdersLabel.setText(String.valueOf(totalOrderSelesai));

            // Total revenue dari pesanan selesai
            double totalRevenue = db.getTotalRevenueSelesai();
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            totalRevenueLabel.setText(nf.format(totalRevenue));
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }
    
    private void initializeTableColumns() {
        // Product table columns
        namaCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNama()));
        hargaCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getHarga()));
        stokCol.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStok()));
        categoryCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("General")); // Default category since no category field
        // Doctor table columns
        doctorNameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getNama()));
        doctorSpecCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSpesialisasi()));
        // Jangan set cellValueFactory untuk doctorEmailCol, biarkan kosong agar tidak tampil
    }
      private void loadProductCards() {
        try {
            List<Produk> products = db.getAllProduk();
            
            // Clear existing cards
            if (productGridContainer != null) {
                productGridContainer.getChildren().clear();
                productGridContainer.setPadding(new Insets(10));
                productGridContainer.setSpacing(20);
                
                // Create cards for each product
                for (Produk produk : products) {
                    try {
                        VBox productCard = createProductCard(produk);
                        productGridContainer.getChildren().add(productCard);
                    } catch (Exception e) {
                        System.err.println("Error creating product card for: " + produk.getNama() + " - " + e.getMessage());
                    }
                }
            } else {
                System.err.println("productGridContainer is null!");
            }
        } catch (Exception e) {
            System.err.println("Error loading product cards: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private VBox createProductCard(Produk produk) {
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setSpacing(12);
        card.setPrefWidth(220);
        card.setPrefHeight(280);
          // Image container
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("product-image-container");
        imageContainer.setPrefHeight(140);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(180);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        // Load product image based on product ID
        try {
            String imagePath = "/image/produk/" + produk.getId() + ".png";
            Image productImage = new Image(getClass().getResourceAsStream(imagePath));
            
            // Check if image was loaded successfully
            if (productImage.isError()) {
                // If specific image not found, use placeholder
                setPlaceholderImage(imageView);
            } else {
                imageView.setImage(productImage);
            }
        } catch (Exception e) {
            // If error loading image, use placeholder
            setPlaceholderImage(imageView);
            System.err.println("Error loading image for product " + produk.getId() + ": " + e.getMessage());
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Product info container
        VBox infoContainer = new VBox();
        infoContainer.getStyleClass().add("product-info");
        infoContainer.setSpacing(8);
        
        // Product name
        Label nameLabel = new Label(produk.getNama());
        nameLabel.getStyleClass().add("product-name");
          // Product category
        Label categoryLabel = new Label("General"); // Default category since Produk doesn't have category field
        categoryLabel.getStyleClass().add("product-category");
        
        // Price and stock container
        HBox priceStockContainer = new HBox();
        priceStockContainer.setSpacing(10);
          NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        Label priceLabel = new Label(currencyFormat.format(produk.getHarga()).replace("IDR", "Rp"));
        priceLabel.getStyleClass().add("product-price");
        
        Label stockLabel = new Label("Stock: " + produk.getStok());
        stockLabel.getStyleClass().add("product-stock");
          priceStockContainer.getChildren().addAll(priceLabel, new javafx.scene.layout.Region(), stockLabel);
        javafx.scene.layout.HBox.setHgrow(priceStockContainer.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        
        // Action buttons
        HBox buttonContainer = new HBox();
        buttonContainer.setSpacing(8);
        buttonContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("product-action-button-secondary");
        editButton.setOnAction(e -> handleEditProduct(produk));
        
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("product-action-button-danger");
        deleteButton.setOnAction(e -> handleDeleteProduct(produk));
        
        buttonContainer.getChildren().addAll(editButton, deleteButton);
        
        // Add all components to info container
        infoContainer.getChildren().addAll(nameLabel, categoryLabel, priceStockContainer, buttonContainer);
        
        // Add all components to card
        card.getChildren().addAll(imageContainer, infoContainer);
          return card;
    }
    
    private void setPlaceholderImage(ImageView imageView) {
        // Create a simple placeholder when image is not found
        imageView.setImage(null);
        imageView.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 2px; -fx-border-style: dashed; -fx-border-radius: 8px;");
    }    private void setupButtonHandlers() {
        // Product management buttons
        if (addProductButton != null) {
            addProductButton.setOnAction(e -> handleAddProduct());
        }
        if (addNewProductButton != null) {
            addNewProductButton.setOnAction(e -> handleAddProduct());
        }
        if (editProductButton != null) {
            editProductButton.setOnAction(e -> handleEditSelectedProduct());
        }
        if (deleteProductButton != null) {
            deleteProductButton.setOnAction(e -> handleDeleteSelectedProduct());
        }
        
        // Doctor management buttons
        if (addDoctorButton != null) {
            addDoctorButton.setOnAction(e -> handleAddDoctor());
            System.out.println("Add Doctor button handler set successfully");
        } else {
            System.err.println("WARNING: addDoctorButton is null!");
        }
        
        if (editDoctorButton != null) {
            editDoctorButton.setOnAction(e -> handleEditSelectedDoctor());
        }
        if (removeDoctorButton != null) {
            removeDoctorButton.setOnAction(e -> handleRemoveSelectedDoctor());
        }
    }
    
    private void loadProdukToTable() {
        ObservableList<Produk> produkList = FXCollections.observableArrayList(db.getAllProduk());
        stokTable.setItems(produkList);
    }
    
    private void loadDoctorToTable() {
        try {
            ObservableList<Dokter> doctorList = FXCollections.observableArrayList(db.getAllDokter());
            doctorTable.setItems(doctorList);
        } catch (Exception e) {
            System.err.println("Error loading doctors to table: " + e.getMessage());
        }
    }
    
    // Product Management Methods
    private void handleAddProduct() {
        Dialog<Produk> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Tambah Produk Baru");

        // Form
        Label nameLabel = new Label("Nama:");
        TextField nameField = new TextField();
        Label priceLabel = new Label("Harga:");
        TextField priceField = new TextField();
        Label stokLabel = new Label("Stok:");
        TextField stokField = new TextField();
        Label imageLabel = new Label("Gambar (PNG):");
        TextField imagePathField = new TextField();
        imagePathField.setEditable(false);
        Button browseButton = new Button("Browse");
        HBox imageBox = new HBox(8, imagePathField, browseButton);

        final java.io.File[] selectedImage = {null};
        browseButton.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Pilih Gambar Produk (PNG)");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PNG Images", "*.png"));
            java.io.File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null && file.getName().toLowerCase().endsWith(".png")) {
                imagePathField.setText(file.getAbsolutePath());
                selectedImage[0] = file;
            } else if (file != null) {
                showNotification("File harus PNG!");
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(nameLabel, 0, 0); grid.add(nameField, 1, 0);
        grid.add(priceLabel, 0, 1); grid.add(priceField, 1, 1);
        grid.add(stokLabel, 0, 2); grid.add(stokField, 1, 2);
        grid.add(imageLabel, 0, 3); grid.add(imageBox, 1, 3);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nama = nameField.getText();
                    double harga = Double.parseDouble(priceField.getText());
                    int stok = Integer.parseInt(stokField.getText());
                    if (selectedImage[0] == null) {
                        showNotification("Pilih gambar produk (PNG)!");
                        return null;
                    }
                    Produk produkBaru = new Produk(null, nama, harga, stok);
                    boolean success = db.addProduk(produkBaru);
                    if (success) {
                        // Ambil ID produk yang baru saja ditambahkan
                        int idProduk = db.getLastProdukId();
                        // Simpan gambar ke /image/produk/{id}.png
                        java.nio.file.Path dest = java.nio.file.Paths.get(System.getProperty("user.dir"), "app", "src", "main", "resources", "image", "produk", idProduk + ".png");
                        java.nio.file.Files.copy(selectedImage[0].toPath(), dest, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        produkBaru.setId(idProduk);
                        return produkBaru;
                    } else {
                        showNotification("Gagal menambah produk!");
                    }
                } catch (Exception e) {
                    showNotification("Input tidak valid atau gagal simpan gambar!");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(produk -> {
            loadProdukToTable();
            loadProductCards();
            loadStatistics();
            showNotification("Produk berhasil ditambahkan!");
        });
    }
    
    private void handleEditSelectedProduct() {
        Produk selectedProduct = stokTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            handleEditProduct(selectedProduct);
        } else {
            showNotification("Please select a product to edit.");
        }
    }
    
    private void handleEditProduct(Produk produk) {
        Dialog<Produk> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.setHeaderText("Edit data produk: " + produk.getNama());

        // Form
        Label nameLabel = new Label("Nama:");
        TextField nameField = new TextField(produk.getNama());
        Label priceLabel = new Label("Harga:");
        TextField priceField = new TextField(String.valueOf(produk.getHarga()));
        Label stokLabel = new Label("Stok:");
        TextField stokField = new TextField(String.valueOf(produk.getStok()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(nameLabel, 0, 0); grid.add(nameField, 1, 0);
        grid.add(priceLabel, 0, 1); grid.add(priceField, 1, 1);
        grid.add(stokLabel, 0, 2); grid.add(stokField, 1, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String nama = nameField.getText();
                    double harga = Double.parseDouble(priceField.getText());
                    int stok = Integer.parseInt(stokField.getText());
                    produk.setNama(nama);
                    produk.setHarga(harga);
                    produk.setStok(stok);
                    return produk;
                } catch (Exception e) {
                    showNotification("Input tidak valid!");
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedProduk -> {
            boolean success = db.updateProduk(updatedProduk);
            if (success) {
                showNotification("Produk berhasil diupdate!");
                loadProdukToTable();
                loadProductCards();
                loadStatistics();
            } else {
                showNotification("Gagal update produk!");
            }
        });
    }
    
    private void handleDeleteSelectedProduct() {
        Produk selectedProduct = stokTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            handleDeleteProduct(selectedProduct);
        } else {
            showNotification("Please select a product to delete.");
        }
    }
    
    private void handleDeleteProduct(Produk produk) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Product");
        alert.setHeaderText("Delete " + produk.getNama());
        alert.setContentText("Are you sure you want to delete this product?");
        if (alert.showAndWait().get() == ButtonType.OK) {
            boolean success = db.deleteProduk(produk.getId());
            if (success) {
                showNotification("Product deleted successfully!");
                loadProdukToTable();
                loadProductCards();
                loadStatistics();
            } else {
                showNotification("Failed to delete product.");
            }
        }
    }
    // Doctor Management Methods
    private void handleEditSelectedDoctor() {
        Dokter selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            // TODO: Open edit doctor dialog
            showNotification("Edit Doctor: " + selectedDoctor.getNama() + " - Coming Soon!");
        } else {
            showNotification("Please select a doctor to edit.");
        }
    }
    
    private void handleRemoveSelectedDoctor() {
        Dokter selectedDoctor = doctorTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor != null) {
            // Confirm deletion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Remove Doctor");
            alert.setHeaderText("Remove " + selectedDoctor.getNama());
            alert.setContentText("Are you sure you want to remove this doctor?");
            
            if (alert.showAndWait().get() == ButtonType.OK) {
                boolean success = db.deleteDokter(selectedDoctor.getId());
                if (success) {
                    showNotification("Doctor removed successfully!");
                    loadDoctorToTable();
                    loadStatistics();
                } else {
                    showNotification("Failed to remove doctor.");
                }
            }
        } else {
            showNotification("Please select a doctor to remove.");
        }
    }    private void handleAddDoctor() {
        try {
            // Get input values
            String nama = doctorNameField.getText().trim();
            String spesialis = doctorSpesialisField.getText().trim();
            String password = doctorPasswordField.getText().trim();
            
            // Validate input
            if (nama.isEmpty() || spesialis.isEmpty() || password.isEmpty()) {
                showNotification("Name, specialization, and password are required fields.");
                return;
            }
            
            // Create doctor object
            Dokter dokter = new Dokter(nama, spesialis, password);
            System.out.println("Attempting to add doctor: " + dokter.getNama() + " - " + dokter.getSpesialisasi());
            
            // Add to database
            boolean success = db.addDokter(dokter);
            
            if (success) {
                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Doctor " + nama + " added successfully!");
                alert.showAndWait();
                
                // Clear form fields
                doctorNameField.clear();
                doctorSpesialisField.clear();
                doctorPasswordField.clear();
                
                // Refresh data
                loadDoctorToTable();
                loadStatistics();
                
                showNotification("Doctor added successfully!");
                System.out.println("Doctor added successfully with ID: " + dokter.getId());
            } else {
                showNotification("Failed to add doctor. Please check the console for errors.");
                System.err.println("Failed to add doctor to database");
            }
        } catch (Exception e) {
            String errorMessage = "Error adding doctor: " + e.getMessage();
            showNotification(errorMessage);
            System.err.println(errorMessage);
            e.printStackTrace();
        }
    }    private void showNotification(String message) {
        if (notificationLabel != null) {
            notificationLabel.setText(message);
            notificationLabel.setVisible(true);
            notificationLabel.setManaged(true);
            
            // Hide notification after 3 seconds
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
                    notificationLabel.setVisible(false);
                    notificationLabel.setManaged(false);
                })
            );
            timeline.play();
        } else {
            System.out.println("Notification: " + message);        }
    }
}
