package Controller;

import Database.Database;
import Model.Dokter;
import Model.Produk;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
            notificationLabel.setVisible(false);
            notificationLabel.setManaged(false);
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
            
            // Calculate total revenue (example calculation)
            double totalRevenue = products.stream()
                .mapToDouble(p -> p.getHarga() * (100 - p.getStok())) // Assuming sold = 100 - current stock
                .sum();
              NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
            totalRevenueLabel.setText(currencyFormat.format(totalRevenue).replace("IDR", "Rp"));
            
            // Mock total orders for now
            totalOrdersLabel.setText("25");
            
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
        doctorEmailCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("N/A")); // Since Dokter doesn't have email field
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
            String imagePath = "/image/" + produk.getId() + ".png";
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
        addProductButton.setOnAction(e -> handleAddProduct());
        if (addNewProductButton != null) {
            addNewProductButton.setOnAction(e -> handleAddProduct());
        }
        editProductButton.setOnAction(e -> handleEditSelectedProduct());
        deleteProductButton.setOnAction(e -> handleDeleteSelectedProduct());
        addDoctorButton.setOnAction(e -> handleAddDoctor());
        editDoctorButton.setOnAction(e -> handleEditSelectedDoctor());
        removeDoctorButton.setOnAction(e -> handleRemoveSelectedDoctor());
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
        // TODO: Open add product dialog
        showNotification("Add Product feature - Coming Soon!");
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
        // TODO: Open edit product dialog
        showNotification("Edit Product: " + produk.getNama() + " - Coming Soon!");
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
        // Confirm deletion
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
        String nama = doctorNameField.getText();
        String spesialis = doctorSpesialisField.getText();
        String password = doctorPasswordField.getText();
        
        if (nama.isEmpty() || spesialis.isEmpty() || password.isEmpty()) {
            showNotification("Name, specialization, and password are required fields.");
            return;
        }
        
        Dokter dokter = new Dokter(nama, spesialis, password);
        
        boolean success = db.addDokter(dokter);
        if (success) {
            showNotification("Doctor added successfully!");
            doctorNameField.clear();
            doctorSpesialisField.clear();
            doctorPasswordField.clear();
            if (doctorEmailField != null) doctorEmailField.clear();
            if (doctorPhoneField != null) doctorPhoneField.clear();
            loadDoctorToTable();
            loadStatistics();
        } else {
            showNotification("Failed to add doctor. Name might already exist.");
        }
    }    private void showNotification(String message) {
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
    }
}
