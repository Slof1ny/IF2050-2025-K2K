package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.ResourceBundle;
import java.text.NumberFormat;

public class DashboardController implements Initializable {
    public static String loggedRole = "";
    public static String loggedName = "";

    @FXML private Button userButton;
    @FXML private Button homeTab;
    @FXML private Button productsTab;
    @FXML private Button appointmentsTab;
    @FXML private Button dashboardTab;
    
    // Tab content containers
    @FXML private ScrollPane homeTabContent;
    @FXML private VBox productsTabContent;
    @FXML private VBox appointmentsTabContent;
    @FXML private VBox dashboardTabContent;
    @FXML private VBox profileTabContent;
    @FXML private VBox cartTabContent;

    private LocalDate currentWeekStart;
    private String currentActiveTab = "home";
    
    // Add field to track current appointment tab
    private String currentAppointmentTab = "book";    // Add fields to track selections
    private String selectedDoctor = null;
    private String selectedAppointmentType = null;
    private HBox selectedDoctorCard = null;
    private VBox selectedTypeCard = null;
    private boolean showingAppointmentTypes = false;
    private LocalDate selectedDate = null;
    private String selectedTime = null;

    // Add enum for availability status (customer view)
    private enum AvailabilityStatus {
        AVAILABLE, OCCUPIED, UNAVAILABLE
    }

    // Add field to track booked appointments
    private java.util.Set<String> bookedSlots = new java.util.HashSet<>();

    // Add static reference to doctor dashboard for updates
    private static Object doctorDashboardInstance = null;
    
    // Cart functionality
    private static java.util.List<CartItem> cartItems = new java.util.ArrayList<>();
      // Cart item class - make it public static so it can be accessed from Database class
    public static class CartItem {
        private Model.Produk product;
        private int quantity;
        
        public CartItem(Model.Produk product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
        
        public Model.Produk getProduct() { return product; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
        public double getTotalPrice() { return product.getHarga() * quantity; }
    }
    
    public static void setDoctorDashboardInstance(Object instance) {
        doctorDashboardInstance = instance;
    }

    public static void setLoginInfo(String role, String name) {
        loggedRole = role;
        loggedName = name;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set nama dan role pada button user jika sudah login
        if (userButton != null && !loggedRole.isEmpty() && !loggedName.isEmpty()) {
            userButton.setText(loggedRole + ": " + loggedName);
        }
        
        // Initialize current week start
        currentWeekStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);

        // Set active tab styling
        setActiveTab(homeTab);
        showHomeTab();
    }

    // Navigation handlers
    @FXML
    private void handleHome() {
        setActiveTab(homeTab);
        showHomeTab();
    }

    @FXML
    private void handleProducts() {
        setActiveTab(productsTab);
        showProductsTab();
    }

    @FXML
    private void handleAppointments() {
        setActiveTab(appointmentsTab);
        showAppointmentsTab();
    }

    @FXML
    private void handleDashboard() {
        setActiveTab(dashboardTab);
        showDashboardTab();
    }

    @FXML
    private void handleProfile() {
        // Clear active state from main navigation tabs first
        clearMainTabActiveStates();
        showProfileTab();
    }

    @FXML
    private void handleCart() {
        // Clear active state from main navigation tabs first  
        clearMainTabActiveStates();
        showCartTab();
    }

    @FXML
    private void handleAppointment() {
        showAppointmentCalendarDialog();
    }

    @FXML
    private void handlePrescription() {
        showAlert("Prescription", "Prescription management functionality will be implemented here.");
    }

    @FXML
    private void handleHistory() {
        showAlert("Medical History", "Medical history viewing functionality will be implemented here.");
    }

    @FXML
    private void handleLogout() {
        try {
            // Load the login screen
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.stage.Stage stage = (javafx.stage.Stage) userButton.getScene().getWindow();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 950, 600);
            stage.setScene(scene);
            stage.setTitle("Optik XYZ - Login");
            stage.centerOnScreen();
            
        } catch (java.io.IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleNotification() {
        showAlert("Notifications", "Notification center functionality will be implemented here.");
    }

    @FXML
    private void handleSettings() {
        showAlert("Settings", "Settings functionality will be implemented here.");
    }

    @FXML
    private void handleHelp() {
        showAlert("Help", "Help and support functionality will be implemented here.");
    }    @FXML
    private void handleEmergency() {
        showAlert("Emergency", "Emergency contact functionality will be implemented here.\n\nFor immediate medical assistance, please call:\n\n* Emergency: 119\n* Hospital: (021) 123-4567");
    }

    @FXML
    private void handleQuickBook() {
        // Same as appointment but with quick booking flow
        handleAppointment();
    }

    @FXML
    private void handleViewReports() {
        showAlert("Medical Reports", "Medical reports viewing functionality will be implemented here.");
    }

    @FXML
    private void handlePayment() {
        showAlert("Payment", "Payment management functionality will be implemented here.");
    }

    private void showAppointmentCalendarDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Book Appointment");
        dialog.setHeaderText("Select available time slot for your appointment");

        // Create the calendar content
        VBox content = new VBox(15);
        content.setPrefSize(800, 600);
        content.setPadding(new Insets(20));

        // Week navigation
        HBox weekNavigation = new HBox(15);
        weekNavigation.setAlignment(Pos.CENTER);        Button prevWeekBtn = new Button("< Previous Week");
        prevWeekBtn.getStyleClass().add("view-button");

        Label weekLabel = new Label("Week of " + currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        weekLabel.getStyleClass().add("section-header");

        Button nextWeekBtn = new Button("Next Week >");
        nextWeekBtn.getStyleClass().add("view-button");

        weekNavigation.getChildren().addAll(prevWeekBtn, weekLabel, nextWeekBtn);

        // Calendar grid
        GridPane calendar = new GridPane();
        calendar.setHgap(5);
        calendar.setVgap(5);
        calendar.getStyleClass().add("appointments-section");

        // Setup column constraints
        for (int i = 0; i < 8; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.SOMETIMES);
            col.setMinWidth(100);
            calendar.getColumnConstraints().add(col);
        }

        // Setup row constraints
        for (int i = 0; i < 11; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.SOMETIMES);
            row.setMinHeight(40);
            calendar.getRowConstraints().add(row);
        }

        // Create calendar content
        loadCustomerCalendar(calendar, weekLabel);

        // Week navigation handlers
        prevWeekBtn.setOnAction(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            weekLabel.setText("Week of " + currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            loadCustomerCalendar(calendar, weekLabel);
        });

        nextWeekBtn.setOnAction(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            weekLabel.setText("Week of " + currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            loadCustomerCalendar(calendar, weekLabel);
        });

        // Legend
        HBox legend = createAppointmentLegend();

        content.getChildren().addAll(weekNavigation, calendar, legend);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        // Style the dialog
        dialog.getDialogPane().getStylesheets().add("/css/doctor-dashboard.css");

        dialog.showAndWait();
    }

    private void loadCustomerCalendar(GridPane calendar, Label weekLabel) {
        calendar.getChildren().clear();

        // Create header row
        Label timeHeader = new Label("Time");
        timeHeader.getStyleClass().add("calendar-header");
        calendar.add(timeHeader, 0, 0);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            VBox dayHeaderContainer = new VBox(5);
            dayHeaderContainer.setAlignment(Pos.CENTER);

            Label dayHeader = new Label(days[i]);
            dayHeader.getStyleClass().add("calendar-header");

            LocalDate dayDate = currentWeekStart.plusDays(i);
            Label dateLabel = new Label(dayDate.getDayOfMonth() + "");
            dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #546e7a;");

            dayHeaderContainer.getChildren().addAll(dayHeader, dateLabel);
            calendar.add(dayHeaderContainer, i + 1, 0);
        }

        // Create time slots with updated availability
        String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

        for (int row = 1; row <= timeSlots.length; row++) {
            Label timeLabel = new Label(timeSlots[row - 1]);
            timeLabel.getStyleClass().add("calendar-time");
            calendar.add(timeLabel, 0, row);

            for (int col = 1; col <= 7; col++) {
                LocalDate dayDate = currentWeekStart.plusDays(col - 1);
                VBox timeSlot = createCustomerTimeSlot(dayDate, timeSlots[row - 1]);
                calendar.add(timeSlot, col, row);
            }
        }
    }

    private void handleTimeSlotSelection(LocalDate date, String time) {
        selectedDate = date;
        selectedTime = time;
        showingAppointmentTypes = true;
        
        // Refresh the content to show appointment type selection
        VBox container = getAppointmentContentContainer();
        if (container != null) {
            loadBookAppointmentContent(container);
        }
    }

    private HBox createAppointmentLegend() {
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getStyleClass().add("calendar-legend");

        // Available legend item
        HBox availableItem = new HBox(8);
        availableItem.setAlignment(Pos.CENTER_LEFT);

        Label availableColor = new Label();
        availableColor.getStyleClass().addAll("legend-color", "legend-available");
        Label availableText = new Label("Available - Click to book");
        availableText.getStyleClass().add("legend-text");
        availableItem.getChildren().addAll(availableColor, availableText);

        // Booked legend item
        HBox bookedItem = new HBox(8);
        bookedItem.setAlignment(Pos.CENTER_LEFT);

        Label bookedColor = new Label();
        bookedColor.getStyleClass().addAll("legend-color", "legend-occupied");
        Label bookedText = new Label("Already Booked");
        bookedText.getStyleClass().add("legend-text");
        bookedItem.getChildren().addAll(bookedColor, bookedText);

        // Unavailable legend item
        HBox unavailableItem = new HBox(8);
        unavailableItem.setAlignment(Pos.CENTER_LEFT);

        Label unavailableColor = new Label();
        unavailableColor.getStyleClass().addAll("legend-color", "legend-unavailable");
        Label unavailableText = new Label("Unavailable");
        unavailableText.getStyleClass().add("legend-text");
        unavailableItem.getChildren().addAll(unavailableColor, unavailableText);

        legend.getChildren().addAll(availableItem, bookedItem, unavailableItem);
        return legend;
    }

    // Add placeholder methods for missing functionality
    private void loadUpcomingAppointments(VBox container) {
        container.getChildren().clear();
        
        Label header = new Label("Upcoming Appointments");
        header.getStyleClass().add("card-title");
        
        try {
            Database.Database db = new Database.Database();
            int patientId = getCurrentPatientId();
            
            if (patientId == -1) {
                Label errorLabel = new Label("Please log in to view your appointments.");
                errorLabel.getStyleClass().add("card-description");
                container.getChildren().addAll(header, errorLabel);
                return;
            }
            
            // Get all appointments for this patient
            java.util.List<Model.JadwalPemeriksaan> allAppointments = db.getAllJadwalPemeriksaan();
            java.util.List<Model.JadwalPemeriksaan> patientAppointments = new java.util.ArrayList<>();
            
            LocalDate today = LocalDate.now();
            
            for (Model.JadwalPemeriksaan appointment : allAppointments) {
                if (appointment.getIdPasien() == patientId) {
                    LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    
                    // Only show future appointments
                    if (appointmentDate.isAfter(today) || appointmentDate.isEqual(today)) {
                        patientAppointments.add(appointment);
                    }
                }
            }
            
            if (patientAppointments.isEmpty()) {
                Label noAppointmentsLabel = new Label("No upcoming appointments found.");
                noAppointmentsLabel.getStyleClass().add("card-description");
                container.getChildren().addAll(header, noAppointmentsLabel);
                return;
            }
            
            // Sort appointments by date
            patientAppointments.sort((a1, a2) -> a1.getTanggalWaktu().compareTo(a2.getTanggalWaktu()));
            
            // Create appointment cards
            VBox appointmentsList = new VBox(10);
            appointmentsList.setPadding(new Insets(20, 0, 0, 0));
            
            for (Model.JadwalPemeriksaan appointment : patientAppointments) {
                VBox appointmentCard = createAppointmentCard(appointment, db);
                appointmentsList.getChildren().add(appointmentCard);
            }
            
            container.getChildren().addAll(header, appointmentsList);
            
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading appointments: " + e.getMessage());
            errorLabel.getStyleClass().add("card-description");
            container.getChildren().addAll(header, errorLabel);
        }
    }

    private void loadHistoryAppointments(VBox container) {
        container.getChildren().clear();
        
        Label header = new Label("Appointment History");
        header.getStyleClass().add("card-title");
        
        try {
            Database.Database db = new Database.Database();
            int patientId = getCurrentPatientId();
            
            if (patientId == -1) {
                Label errorLabel = new Label("Please log in to view your appointment history.");
                errorLabel.getStyleClass().add("card-description");
                container.getChildren().addAll(header, errorLabel);
                return;
            }
            
            // Get all appointments for this patient
            java.util.List<Model.JadwalPemeriksaan> allAppointments = db.getAllJadwalPemeriksaan();
            java.util.List<Model.JadwalPemeriksaan> pastAppointments = new java.util.ArrayList<>();
            
            LocalDate today = LocalDate.now();
            
            for (Model.JadwalPemeriksaan appointment : allAppointments) {
                if (appointment.getIdPasien() == patientId) {
                    LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                    
                    // Only show past appointments
                    if (appointmentDate.isBefore(today)) {
                        pastAppointments.add(appointment);
                    }
                }
            }
            
            if (pastAppointments.isEmpty()) {
                Label noHistoryLabel = new Label("No past appointments found.");
                noHistoryLabel.getStyleClass().add("card-description");
                container.getChildren().addAll(header, noHistoryLabel);
                return;
            }
            
            // Sort appointments by date (most recent first)
            pastAppointments.sort((a1, a2) -> a2.getTanggalWaktu().compareTo(a1.getTanggalWaktu()));
            
            // Create appointment cards
            VBox appointmentsList = new VBox(10);
            appointmentsList.setPadding(new Insets(20, 0, 0, 0));
            
            for (Model.JadwalPemeriksaan appointment : pastAppointments) {
                VBox appointmentCard = createAppointmentCard(appointment, db);
                appointmentCard.getStyleClass().add("appointment-card-past");
                appointmentsList.getChildren().add(appointmentCard);
            }
            
            container.getChildren().addAll(header, appointmentsList);
            
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading appointment history: " + e.getMessage());
            errorLabel.getStyleClass().add("card-description");
            container.getChildren().addAll(header, errorLabel);
        }
    }
    
    private VBox createAppointmentCard(Model.JadwalPemeriksaan appointment, Database.Database db) {
        VBox card = new VBox(10);
        card.getStyleClass().add("service-card");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        
        try {
            // Get doctor information
            Model.Dokter doctor = db.getDokterById(appointment.getIdDokter());
            String doctorName = doctor != null ? doctor.getNama() : "Unknown Doctor";
            String doctorSpecialty = doctor != null ? doctor.getSpesialisasi() : "General";
            
            // Format date and time
            LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            String appointmentTime = appointment.getTanggalWaktu().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
            
            // Create card header
            HBox cardHeader = new HBox(15);
            cardHeader.setAlignment(Pos.CENTER_LEFT);
            
            Label doctorLabel = new Label("Dr. " + doctorName);
            doctorLabel.getStyleClass().add("card-title");
            doctorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            
            Label specialtyLabel = new Label(doctorSpecialty);
            specialtyLabel.getStyleClass().add("doctor-specialty");
            specialtyLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            
            cardHeader.getChildren().addAll(doctorLabel, new Label("•"), specialtyLabel);
            
            // Create appointment details
            VBox details = new VBox(5);
              Label dateLabel = new Label("Date: " + appointmentDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            dateLabel.getStyleClass().add("field-label");
            
            Label timeLabel = new Label("Time: " + appointmentTime);
            timeLabel.getStyleClass().add("field-label");
            
            // Status indicator
            LocalDate today = LocalDate.now();
            Label statusLabel = new Label();
            if (appointmentDate.isAfter(today)) {
                statusLabel.setText("Upcoming");
                statusLabel.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            } else if (appointmentDate.equals(today)) {
                statusLabel.setText("Today");
                statusLabel.setStyle("-fx-text-fill: #007bff; -fx-font-weight: bold;");
            } else {
                statusLabel.setText("Completed");
                statusLabel.setStyle("-fx-text-fill: #6c757d; -fx-font-weight: bold;");
            }
            
            details.getChildren().addAll(dateLabel, timeLabel, statusLabel);
            
            // Add action buttons for upcoming appointments
            if (appointmentDate.isAfter(today) || appointmentDate.equals(today)) {
                HBox actionButtons = new HBox(10);
                actionButtons.setPadding(new Insets(10, 0, 0, 0));
                
                Button rescheduleBtn = new Button("Reschedule");
                rescheduleBtn.getStyleClass().add("hero-button-secondary");
                rescheduleBtn.setOnAction(e -> showAlert("Reschedule", "Reschedule functionality will be implemented soon."));
                
                Button cancelBtn = new Button("Cancel");
                cancelBtn.getStyleClass().add("hero-button-secondary");
                cancelBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                cancelBtn.setOnAction(e -> handleCancelAppointment(appointment));
                
                actionButtons.getChildren().addAll(rescheduleBtn, cancelBtn);
                details.getChildren().add(actionButtons);
            }
            
            card.getChildren().addAll(cardHeader, details);
            
        } catch (Exception e) {
            Label errorLabel = new Label("Error loading appointment details");
            errorLabel.getStyleClass().add("card-description");
            card.getChildren().add(errorLabel);
        }
        
        return card;
    }
    
    private void handleCancelAppointment(Model.JadwalPemeriksaan appointment) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Appointment");
        confirmDialog.setHeaderText("Are you sure you want to cancel this appointment?");
        confirmDialog.setContentText("This action cannot be undone.");
        
        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    Database.Database db = new Database.Database();
                    boolean cancelled = db.deleteJadwalPemeriksaan(appointment.getIdJadwal());
                    
                    if (cancelled) {
                        showAlert("Success", "Appointment cancelled successfully.");
                        
                        // Remove from booked slots if present
                        LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                        String appointmentTime = appointment.getTanggalWaktu().toInstant()
                            .atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                            .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                        String slotKey = appointmentDate.toString() + "_" + appointmentTime;
                        bookedSlots.remove(slotKey);
                        
                        // Refresh the current view based on which tab is active
                        VBox container = getAppointmentContentContainer();
                        if (container != null) {
                            if (currentAppointmentTab.equals("upcoming")) {
                                loadUpcomingAppointments(container);
                            } else if (currentAppointmentTab.equals("history")) {
                                loadHistoryAppointments(container);
                            } else {
                                loadBookAppointmentContent(container);
                            }
                        }
                        
                        // Refresh calendar display if on booking tab
                        refreshCalendarDisplay();
                        
                    } else {
                        showAlert("Error", "Failed to cancel appointment. Please try again.");
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Error", "An error occurred while cancelling the appointment: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadDashboardContent() {
        dashboardTabContent.getChildren().clear();
        Label header = new Label("Patient Dashboard");
        header.getStyleClass().add("section-title");
        dashboardTabContent.getChildren().add(header);
    }

    private void loadProfileContent() {
        profileTabContent.getChildren().clear();
        
        // Create scrollable content for profile
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("profile-scroll");
        
        VBox scrollableContent = new VBox(20);
        scrollableContent.setPadding(new Insets(20));
        
        Label header = new Label("My Profile");
        header.getStyleClass().add("section-title");
        
        VBox profileInfo = new VBox(15);
        profileInfo.setAlignment(Pos.CENTER_LEFT);
        
        // Create profile information cards
        VBox personalInfo = createProfileSection("Personal Information");
        personalInfo.getChildren().addAll(
            createProfileRow("Name", loggedName.isEmpty() ? "Customer User" : loggedName),
            createProfileRow("Role", loggedRole.isEmpty() ? "Customer" : loggedRole),
            createProfileRow("Email", "customer@example.com"),
            createProfileRow("Phone", "+62 123 456 7890")
        );
        
        VBox addressInfo = createProfileSection("Address Information");
        addressInfo.getChildren().addAll(
            createProfileRow("Street", "123 Main Street"),
            createProfileRow("City", "Jakarta"),
            createProfileRow("Postal Code", "12345"),
            createProfileRow("Country", "Indonesia")
        );
        
        VBox accountInfo = createProfileSection("Account Information");
        accountInfo.getChildren().addAll(
            createProfileRow("Member Since", "January 2024"),
            createProfileRow("Account Status", "Active"),
            createProfileRow("Last Login", "Today")
        );
        
        // Profile actions
        HBox profileActions = new HBox(15);
        profileActions.setAlignment(Pos.CENTER);
        profileActions.setPadding(new Insets(20, 0, 0, 0));
        
        Button editBtn = new Button("Edit Profile");
        editBtn.getStyleClass().add("hero-button-primary");
        editBtn.setOnAction(e -> showAlert("Edit Profile", "Edit profile functionality will be implemented here."));
        
        Button logoutBtn = new Button("Logout");
        logoutBtn.getStyleClass().add("hero-button-secondary");
        // Add red styling for logout button
        logoutBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; " +
                          "-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10px 20px; " +
                          "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(220, 53, 69, 0.3), 4, 0, 0, 2);");
        logoutBtn.setOnMouseEntered(e -> {
            logoutBtn.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; -fx-font-weight: bold; " +
                              "-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10px 20px; " +
                              "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(220, 53, 69, 0.5), 6, 0, 0, 3);");
        });
        logoutBtn.setOnMouseExited(e -> {
            logoutBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; " +
                              "-fx-background-radius: 8px; -fx-border-radius: 8px; -fx-padding: 10px 20px; " +
                              "-fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(220, 53, 69, 0.3), 4, 0, 0, 2);");
        });
        logoutBtn.setOnAction(e -> handleLogout());
        
        profileActions.getChildren().addAll(editBtn, logoutBtn);
        
        profileInfo.getChildren().addAll(personalInfo, addressInfo, accountInfo);
        scrollableContent.getChildren().addAll(header, profileInfo, profileActions);
        
        scrollPane.setContent(scrollableContent);
        profileTabContent.getChildren().add(scrollPane);
    }
    
    private VBox createProfileSection(String title) {
        VBox section = new VBox(10);
        section.getStyleClass().add("service-card");
        section.setPadding(new Insets(15));
        section.setAlignment(Pos.CENTER_LEFT);
        
        Label sectionTitle = new Label(title);
        sectionTitle.getStyleClass().add("card-title");
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        section.getChildren().add(sectionTitle);
        return section;
    }
    
    private HBox createProfileRow(String label, String value) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5, 0, 5, 0));
        
        Label labelText = new Label(label + ":");
        labelText.getStyleClass().add("field-label");
        labelText.setPrefWidth(120);
        labelText.setStyle("-fx-font-weight: bold;");
        
        Label valueText = new Label(value);
        valueText.getStyleClass().add("card-description");
        
        row.getChildren().addAll(labelText, valueText);
        return row;
    }    private void loadCartContent() {
        cartTabContent.getChildren().clear();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox scrollableContent = new VBox(20);
        scrollableContent.setPadding(new Insets(20));
        
        Label header = new Label("Shopping Cart");
        header.getStyleClass().add("section-title");
        
        if (cartItems.isEmpty()) {
            // Empty cart state
            VBox cartInfo = new VBox(20);
            cartInfo.setAlignment(Pos.CENTER);
            cartInfo.setPadding(new Insets(50));
            
            Label emptyMessage = new Label("Your cart is currently empty");
            emptyMessage.getStyleClass().add("section-subtitle");
            emptyMessage.setStyle("-fx-font-size: 18px; -fx-text-fill: #666;");
            
            Label subMessage = new Label("Browse our products to add items to your cart");
            subMessage.getStyleClass().add("card-description");
            
            Button shopBtn = new Button("Start Shopping");
            shopBtn.getStyleClass().add("hero-button-primary");
            shopBtn.setPrefWidth(200);
            shopBtn.setOnAction(e -> {
                setActiveTab(productsTab);
                showProductsTab();
            });
            
            cartInfo.getChildren().addAll(emptyMessage, subMessage, shopBtn);
            scrollableContent.getChildren().addAll(header, cartInfo);
        } else {
            // Cart with items
            VBox cartItemsContainer = new VBox(15);
            cartItemsContainer.setPadding(new Insets(20, 0, 20, 0));
            
            double totalPrice = 0;
            
            for (CartItem item : cartItems) {
                VBox itemCard = createCartItemCard(item);
                cartItemsContainer.getChildren().add(itemCard);
                totalPrice += item.getTotalPrice();
            }
            
            // Cart summary
            VBox summarySection = new VBox(15);
            summarySection.getStyleClass().add("service-card");
            summarySection.setPadding(new Insets(20));
            summarySection.setAlignment(Pos.CENTER);
            
            Label summaryHeader = new Label("Order Summary");
            summaryHeader.getStyleClass().add("card-title");
            summaryHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            
            Label itemCount = new Label("Items: " + cartItems.size());
            itemCount.getStyleClass().add("field-label");
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
            String formattedTotal = currencyFormat.format(totalPrice).replace("IDR", "Rp");
            
            Label totalLabel = new Label("Total: " + formattedTotal);
            totalLabel.getStyleClass().add("section-subtitle");
            totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
            
            HBox actionButtons = new HBox(15);
            actionButtons.setAlignment(Pos.CENTER);
            
            Button clearCartBtn = new Button("Clear Cart");
            clearCartBtn.getStyleClass().add("hero-button-secondary");
            clearCartBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            clearCartBtn.setOnAction(e -> handleClearCart());
            
            Button checkoutBtn = new Button("Checkout");
            checkoutBtn.getStyleClass().add("hero-button-primary");
            checkoutBtn.setPrefWidth(150);
            checkoutBtn.setOnAction(e -> handleCheckout());
            
            actionButtons.getChildren().addAll(clearCartBtn, checkoutBtn);
            summarySection.getChildren().addAll(summaryHeader, itemCount, totalLabel, actionButtons);
            
            scrollableContent.getChildren().addAll(header, cartItemsContainer, summarySection);
        }
        
        scrollPane.setContent(scrollableContent);
        cartTabContent.getChildren().add(scrollPane);
    }private VBox createProductCard(Model.Produk product) {
        VBox card = new VBox(12);
        card.getStyleClass().add("service-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(220);
        card.setPrefHeight(240); // Reduced height since we removed stock indicators
        card.setPadding(new Insets(15));
        
        // Product name
        Label productName = new Label(product.getNama());
        productName.getStyleClass().add("card-title");
        productName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        productName.setWrapText(true);
        
        // Price formatting
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        String formattedPrice = currencyFormat.format(product.getHarga()).replace("IDR", "Rp");
        
        Label productPrice = new Label(formattedPrice);
        productPrice.getStyleClass().add("section-subtitle");
        productPrice.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c5282;");
        
        // Product description or category (optional)
        Label productDescription = new Label("Quality optical product");
        productDescription.getStyleClass().add("card-description");
        productDescription.setStyle("-fx-font-size: 12px; -fx-text-fill: #666; -fx-wrap-text: true;");
        
        // Action buttons
        HBox buttonContainer = new HBox(8);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button addToCartBtn = new Button("Add to Cart");
        addToCartBtn.getStyleClass().add("hero-button-primary");
        addToCartBtn.setPrefWidth(100);
        
        Button viewDetailsBtn = new Button("Details");
        viewDetailsBtn.getStyleClass().add("hero-button-secondary");
        viewDetailsBtn.setPrefWidth(80);
        
        // Disable buttons if out of stock
        if (product.getStok() <= 0) {
            addToCartBtn.setDisable(true);
            addToCartBtn.setText("Out of Stock");
            addToCartBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        } else {
            addToCartBtn.setOnAction(e -> handleAddToCart(product));
        }
        
        viewDetailsBtn.setOnAction(e -> handleViewProductDetails(product));
        
        buttonContainer.getChildren().addAll(addToCartBtn, viewDetailsBtn);
        
        card.getChildren().addAll(productName, productPrice, productDescription, buttonContainer);
        return card;
    }
      private void handleAddToCart(Model.Produk product) {
        // Create quantity selection dialog
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Add to Cart");
        dialog.setHeaderText("Select quantity for " + product.getNama());
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        Label productInfo = new Label("Product: " + product.getNama() + "\nPrice: " + 
                                    NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
                                                .format(product.getHarga()).replace("IDR", "Rp"));
        productInfo.getStyleClass().add("field-label");
        
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.getStyleClass().add("field-label");
        
        Spinner<Integer> quantitySpinner = new Spinner<>(1, Math.min(product.getStok(), 10), 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);
        
        Label stockInfo = new Label("Available: " + product.getStok() + " units");
        stockInfo.getStyleClass().add("card-description");
        stockInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        content.getChildren().addAll(productInfo, quantityLabel, quantitySpinner, stockInfo);
        dialog.getDialogPane().setContent(content);
        
        ButtonType addButtonType = new ButtonType("Add to Cart", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return quantitySpinner.getValue();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(quantity -> {
            if (quantity > 0 && quantity <= product.getStok()) {
                // Check if product already exists in cart
                CartItem existingItem = cartItems.stream()
                    .filter(item -> item.getProduct().getId() == product.getId())
                    .findFirst()
                    .orElse(null);
                
                if (existingItem != null) {
                    // Update quantity if product already in cart
                    int newQuantity = existingItem.getQuantity() + quantity;
                    if (newQuantity <= product.getStok()) {
                        existingItem.setQuantity(newQuantity);
                        showAlert("Cart Updated", product.getNama() + " quantity updated to " + newQuantity + 
                                 "\nTotal: " + NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
                                                          .format(existingItem.getTotalPrice()).replace("IDR", "Rp"));
                    } else {
                        showAlert("Stock Limit", "Cannot add more items. Stock limit reached.");
                    }
                } else {
                    // Add new item to cart
                    cartItems.add(new CartItem(product, quantity));
                    showAlert("Added to Cart", quantity + "x " + product.getNama() + " added to cart!\nTotal: " + 
                             NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
                                        .format(product.getHarga() * quantity).replace("IDR", "Rp"));
                }
                
                // Update cart display if cart tab is currently active
                if (currentActiveTab.equals("cart")) {
                    loadCartContent();
                }
            } else {
                showAlert("Invalid Quantity", "Please select a valid quantity (1-" + product.getStok() + ")");
            }
        });
    }
    
    private void handleViewProductDetails(Model.Produk product) {
        String details = "Product Details:\n\n" +
                        "Name: " + product.getNama() + "\n" +
                        "Price: " + NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
                                                .format(product.getHarga()).replace("IDR", "Rp") + "\n" +
                        "Stock: " + product.getStok() + " units\n" +
                        "Product ID: " + product.getId();
        
        showAlert("Product Details", details);
    }

    private VBox createQuickBookCard(String title, String description) {
        VBox card = new VBox(8);
        card.getStyleClass().add("appointment-type-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(180);
        card.setPrefHeight(100);
        
        card.setOnMouseClicked(e -> {
            // Remove selection from previous card
            if (selectedTypeCard != null) {
                selectedTypeCard.getStyleClass().remove("appointment-type-card-selected");
            }
            
            // Select this card
            selectedAppointmentType = title;
            selectedTypeCard = card;
            card.getStyleClass().add("appointment-type-card-selected");
        });
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setAlignment(Pos.CENTER);
        
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("card-description");
        descLabel.setAlignment(Pos.CENTER);
        
        card.getChildren().addAll(titleLabel, descLabel);
        return card;
    }

    private void resetBookingSelections() {
        selectedDoctor = null;
        selectedAppointmentType = null;
        selectedDate = null;
        selectedTime = null;
        showingAppointmentTypes = false;
        
        if (selectedDoctorCard != null) {
            selectedDoctorCard.getStyleClass().remove("compact-doctor-card-selected");
            selectedDoctorCard = null;
        }
        if (selectedTypeCard != null) {
            selectedTypeCard.getStyleClass().remove("appointment-type-card-selected");
            selectedTypeCard = null;
        }
    }

    private VBox getAppointmentContentContainer() {
        try {
            ScrollPane scrollPane = (ScrollPane) appointmentsTabContent.getChildren().get(0);
            VBox scrollableContent = (VBox) scrollPane.getContent();
            return (VBox) scrollableContent.getChildren().get(2); // Content container
        } catch (Exception e) {
            return null;
        }
    }

    private void handleAppointmentBooking(LocalDate date, String time) {
        // Validate that a doctor is selected
        if (selectedDoctor == null) {
            showAlert("No Doctor Selected", "Please select a doctor before booking an appointment.");
            return;
        }
        
        Alert bookingDialog = new Alert(Alert.AlertType.CONFIRMATION);
        bookingDialog.setTitle("Book Appointment");
        bookingDialog.setHeaderText("Confirm Appointment Booking");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label dateTimeLabel = new Label("Date & Time: " +
            date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + " at " + time);
        dateTimeLabel.getStyleClass().add("patient-name");

        Label doctorLabel = new Label("Doctor: " + selectedDoctor);
        doctorLabel.getStyleClass().add("field-label");

        Label typeLabel = new Label("Appointment Type: " + (selectedAppointmentType != null ? selectedAppointmentType : "Regular Checkup"));
        typeLabel.getStyleClass().add("field-label");

        Label notesLabel = new Label("Additional Notes:");
        notesLabel.getStyleClass().add("field-label");

        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.setPromptText("Enter any additional information or symptoms...");

        content.getChildren().addAll(dateTimeLabel, doctorLabel, typeLabel, notesLabel, notesArea);

        bookingDialog.getDialogPane().setContent(content);

        bookingDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Try to save the appointment to database
                boolean appointmentSaved = saveAppointmentToDatabase(date, time, selectedDoctor, 
                    selectedAppointmentType != null ? selectedAppointmentType : "Regular Checkup", 
                    notesArea.getText());
                
                if (appointmentSaved) {
                    showAlert("Success", "Appointment booked successfully!\n\n" +
                        "Doctor: " + selectedDoctor + "\n" +
                        "Date: " + date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + "\n" +
                        "Time: " + time + "\n" +
                        "Type: " + (selectedAppointmentType != null ? selectedAppointmentType : "Regular Checkup"));
                    
                    // Reset selections and refresh calendar
                    resetBookingSelections();
                    
                    // Refresh the booking content to show updated calendar
                    VBox container = getAppointmentContentContainer();
                    if (container != null) {
                        loadBookAppointmentContent(container);
                    }
                } else {
                    showAlert("Error", "Failed to book appointment. Please try again.");
                }
            }
        });
    }

    private void loadAppointmentTypeSelection(VBox container) {
        container.getChildren().clear();
        
        Label header = new Label("Select Appointment Type");
        header.getStyleClass().add("card-title");
        
        // Show selected info
        HBox selectionInfo = new HBox(20);
        selectionInfo.setAlignment(Pos.CENTER);
        selectionInfo.setPadding(new Insets(15));
        selectionInfo.getStyleClass().add("selection-info");
        
        Label doctorInfo = new Label("Doctor: " + selectedDoctor);
        doctorInfo.getStyleClass().add("selection-text");
        
        Label dateInfo = new Label("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateInfo.getStyleClass().add("selection-text");
          Label timeInfo = new Label("Time: " + selectedTime);
        timeInfo.getStyleClass().add("selection-text");
        
        Button backBtn = new Button("< Back to Calendar");
        backBtn.getStyleClass().add("hero-button-secondary");
        backBtn.setOnAction(e -> {
            showingAppointmentTypes = false;
            selectedDate = null;
            selectedTime = null;
            loadBookAppointmentContent(container);
        });
        
        selectionInfo.getChildren().addAll(doctorInfo, dateInfo, timeInfo, backBtn);
        
        // Appointment type selection
        VBox typeSection = new VBox(20);
        typeSection.setAlignment(Pos.CENTER);
        
        Label typeHeader = new Label("Choose Appointment Type:");
        typeHeader.getStyleClass().add("section-subtitle");
        typeHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        GridPane typeOptions = new GridPane();
        typeOptions.setHgap(15);
        typeOptions.setVgap(15);
        typeOptions.setAlignment(Pos.CENTER);
        
        VBox eyeExamCard = createQuickBookCard("Eye Examination", "Comprehensive eye check");
        VBox checkupCard = createQuickBookCard("Regular Checkup", "Routine health assessment");
        VBox followupCard = createQuickBookCard("Follow-up Visit", "Post-treatment consultation");
        VBox lensCard = createQuickBookCard("Contact Lens Fitting", "Lens measurement & fitting");
        
        typeOptions.add(eyeExamCard, 0, 0);
        typeOptions.add(checkupCard, 1, 0);
        typeOptions.add(followupCard, 0, 1);
        typeOptions.add(lensCard, 1, 1);
        
        // Confirm button
        Button confirmBtn = new Button("Confirm Appointment");
        confirmBtn.getStyleClass().add("hero-button-primary");
        confirmBtn.setPrefWidth(200);
        confirmBtn.setOnAction(e -> {
            if (selectedAppointmentType == null) {
                showAlert("Select Type", "Please select an appointment type.");
                return;
            }
            confirmAppointmentBooking();
        });
        
        typeSection.getChildren().addAll(typeHeader, typeOptions, confirmBtn);
        container.getChildren().addAll(header, selectionInfo, typeSection);
    }
    
    private void confirmAppointmentBooking() {
        // Show confirmation dialog with notes
        Alert bookingDialog = new Alert(Alert.AlertType.CONFIRMATION);
        bookingDialog.setTitle("Confirm Appointment");
        bookingDialog.setHeaderText("Finalize Your Appointment");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label summaryLabel = new Label("Appointment Summary:");
        summaryLabel.getStyleClass().add("card-title");

        Label doctorLabel = new Label("Doctor: " + selectedDoctor);
        Label dateLabel = new Label("Date: " + selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        Label timeLabel = new Label("Time: " + selectedTime);
        Label typeLabel = new Label("Type: " + selectedAppointmentType);

        Label notesLabel = new Label("Additional Notes (Optional):");
        notesLabel.getStyleClass().add("field-label");

        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.setPromptText("Enter any additional information or symptoms...");

        content.getChildren().addAll(summaryLabel, doctorLabel, dateLabel, timeLabel, notesLabel, notesArea);
        bookingDialog.getDialogPane().setContent(content);

        bookingDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Try to save the appointment to database
                boolean appointmentSaved = saveAppointmentToDatabase(selectedDate, selectedTime, selectedDoctor, 
                    selectedAppointmentType, notesArea.getText());
                
                if (appointmentSaved) {
                    showAlert("Success", "Appointment booked successfully!\n\n" +
                        "Doctor: " + selectedDoctor + "\n" +
                        "Date: " + selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + "\n" +
                        "Time: " + selectedTime + "\n" +
                        "Type: " + selectedAppointmentType);
                } else {
                    showAlert("Error", "Failed to book appointment. Please try again.");
                }
                
                // Reset all selections
                resetBookingSelections();
                
                // Go back to calendar view
                VBox container = getAppointmentContentContainer();
                if (container != null) {
                    loadBookAppointmentContent(container);
                }
            }
        });
    }

    private void refreshCalendarDisplay() {
        // Refresh the appointment tab calendar if it's currently visible
        if (currentActiveTab.equals("appointments")) {
            // Refresh the booking calendar
            VBox container = getAppointmentContentContainer();
            if (container != null && !showingAppointmentTypes) {
                loadBookAppointmentContent(container);
            }
        }
    }
    
    private int getCurrentPatientId() {
        try {
            Database.Database db = new Database.Database();
            // Get patient by logged name (assuming loggedName contains patient name)
            java.util.List<Model.Pelanggan> patients = db.searchPelangganByNama(loggedName);
            
            if (!patients.isEmpty()) {
                return patients.get(0).getId();
            }
            
            // Alternative: get by exact name match
            for (Model.Pelanggan patient : db.getAllPelanggan()) {
                if (patient.getNama().equals(loggedName)) {
                    return patient.getId();
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if patient not found
    }

    private void clearMainTabActiveStates() {
        // Remove active class from main navigation tabs
        homeTab.getStyleClass().remove("nav-button-active");
        productsTab.getStyleClass().remove("nav-button-active");
        appointmentsTab.getStyleClass().remove("nav-button-active");
        dashboardTab.getStyleClass().remove("nav-button-active");
    }

    // Add all the remaining missing methods from the previous implementation
    private void loadDoctorCards(HBox doctorGrid) {
        try {
            Database.Database db = new Database.Database();
            java.util.List<Model.Dokter> doctors = db.getAllDokter();
            
            if (doctors.isEmpty()) {
                Label noDoctorsLabel = new Label("No doctors available at the moment");
                noDoctorsLabel.getStyleClass().add("card-description");
                doctorGrid.getChildren().add(noDoctorsLabel);
                return;
            }
            
            for (Model.Dokter doctor : doctors) {
                HBox doctorCard = createCompactDoctorCard(doctor.getNama(), doctor.getSpesialisasi());
                doctorGrid.getChildren().add(doctorCard);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading doctors. Please try again.");
            errorLabel.getStyleClass().add("card-description");
            doctorGrid.getChildren().add(errorLabel);
        }
    }

    private VBox createCustomerTimeSlot(LocalDate date, String time) {
        VBox slot = new VBox(2);
        slot.getStyleClass().add("calendar-slot");
        slot.setAlignment(Pos.CENTER);
        slot.setPrefHeight(40);

        AvailabilityStatus status = getSimulatedAvailability(date, time);
        updateCustomerSlotAppearance(slot, status);

        Label statusLabel = new Label(getCustomerStatusText(status));
        statusLabel.getStyleClass().addAll("calendar-status-label", getCustomerStatusStyleClass(status));

        if (status == AvailabilityStatus.OCCUPIED) {
            Label occupiedLabel = new Label("Booked");
            occupiedLabel.getStyleClass().add("calendar-appointment");
            slot.getChildren().add(occupiedLabel);
        }

        slot.getChildren().add(statusLabel);

        if (status == AvailabilityStatus.AVAILABLE) {
            slot.setOnMouseClicked(event -> handleAppointmentBooking(date, time));
            slot.setStyle(slot.getStyle() + "-fx-cursor: hand;");
        }

        return slot;
    }

    private AvailabilityStatus getSimulatedAvailability(LocalDate date, String time) {
        String slotKey = date.toString() + "_" + time;
        if (bookedSlots.contains(slotKey)) {
            return AvailabilityStatus.OCCUPIED;
        }
        
        if (isSlotBookedInDatabase(date, time)) {
            return AvailabilityStatus.OCCUPIED;
        }
        
        if (date.getDayOfWeek().getValue() == 7) { // Sunday
            return AvailabilityStatus.UNAVAILABLE;
        }

        if (time.equals("12:00") || time.equals("13:00")) { // Lunch break
            return AvailabilityStatus.UNAVAILABLE;
        }

        return AvailabilityStatus.AVAILABLE;
    }

    private boolean isSlotBookedInDatabase(LocalDate date, String time) {
        try {
            Database.Database db = new Database.Database();
            java.util.List<Model.JadwalPemeriksaan> allAppointments = db.getAllJadwalPemeriksaan();
            
            for (Model.JadwalPemeriksaan appointment : allAppointments) {
                LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String appointmentTime = appointment.getTanggalWaktu().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                
                if (appointmentDate.equals(date) && appointmentTime.equals(time)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateCustomerSlotAppearance(VBox slot, AvailabilityStatus status) {
        slot.getStyleClass().removeAll("calendar-slot-available", "calendar-slot-occupied", "calendar-slot-unavailable");
        
        switch (status) {
            case AVAILABLE:
                slot.getStyleClass().add("calendar-slot-available");
                break;
            case OCCUPIED:
                slot.getStyleClass().add("calendar-slot-occupied");
                break;
            case UNAVAILABLE:
                slot.getStyleClass().add("calendar-slot-unavailable");
                break;
        }
    }

    private String getCustomerStatusText(AvailabilityStatus status) {
        switch (status) {
            case AVAILABLE: return "Available";
            case OCCUPIED: return "Booked";
            case UNAVAILABLE: return "Unavailable";
            default: return "";
        }
    }

    private String getCustomerStatusStyleClass(AvailabilityStatus status) {
        switch (status) {
            case AVAILABLE: return "status-available";
            case OCCUPIED: return "status-occupied";
            case UNAVAILABLE: return "status-unavailable";
            default: return "";
        }
    }

    private HBox createCompactDoctorCard(String name, String specialty) {
        HBox card = new HBox(10);
        card.getStyleClass().add("compact-doctor-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPrefWidth(200);
        card.setPrefHeight(60);
        card.setPadding(new Insets(10));
        
        card.setOnMouseClicked(e -> {
            if (selectedDoctorCard != null) {
                selectedDoctorCard.getStyleClass().remove("compact-doctor-card-selected");
            }
            
            selectedDoctor = name;
            selectedDoctorCard = card;
            card.getStyleClass().add("compact-doctor-card-selected");
        });
        
        VBox doctorInfo = new VBox(2);
        doctorInfo.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("card-title");
        nameLabel.setStyle("-fx-font-size: 12px;");
        
        Label specialtyLabel = new Label(specialty);
        specialtyLabel.getStyleClass().add("doctor-specialty");
        specialtyLabel.setStyle("-fx-font-size: 10px;");
        
        doctorInfo.getChildren().addAll(nameLabel, specialtyLabel);
        card.getChildren().add(doctorInfo);
        
        return card;
    }

    // Add remaining tab management and content loading methods
    private void setActiveTab(Button activeTab) {
        homeTab.getStyleClass().remove("nav-button-active");
        productsTab.getStyleClass().remove("nav-button-active");
        appointmentsTab.getStyleClass().remove("nav-button-active");
        dashboardTab.getStyleClass().remove("nav-button-active");
        
        activeTab.getStyleClass().add("nav-button-active");
    }

    private void showHomeTab() {
        hideAllTabs();
        homeTabContent.setVisible(true);
        homeTabContent.setManaged(true);
        currentActiveTab = "home";
    }

    private void showProductsTab() {
        hideAllTabs();
        productsTabContent.setVisible(true);
        productsTabContent.setManaged(true);
        loadProductsContent();
        currentActiveTab = "products";
    }

    private void showAppointmentsTab() {
        hideAllTabs();
        appointmentsTabContent.setVisible(true);
        appointmentsTabContent.setManaged(true);
        loadAppointmentsContent();
        currentActiveTab = "appointments";
    }

    private void showDashboardTab() {
        hideAllTabs();
        dashboardTabContent.setVisible(true);
        dashboardTabContent.setManaged(true);
        loadDashboardContent();
        currentActiveTab = "dashboard";
    }

    private void showProfileTab() {
        hideAllTabs();
        profileTabContent.setVisible(true);
        profileTabContent.setManaged(true);
        loadProfileContent();
        currentActiveTab = "profile";
    }

    private void showCartTab() {
        hideAllTabs();
        cartTabContent.setVisible(true);
        cartTabContent.setManaged(true);
        loadCartContent();
        currentActiveTab = "cart";
    }

    private void hideAllTabs() {
        homeTabContent.setVisible(false);
        homeTabContent.setManaged(false);
        productsTabContent.setVisible(false);
        productsTabContent.setManaged(false);
        appointmentsTabContent.setVisible(false);
        appointmentsTabContent.setManaged(false);
        dashboardTabContent.setVisible(false);
        dashboardTabContent.setManaged(false);
        profileTabContent.setVisible(false);
        profileTabContent.setManaged(false);
        cartTabContent.setVisible(false);
        cartTabContent.setManaged(false);
    }    private void loadProductsContent() {
        productsTabContent.getChildren().clear();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox scrollableContent = new VBox(20);
        scrollableContent.setPadding(new Insets(20));
        
        Label header = new Label("Our Products");
        header.getStyleClass().add("section-title");
        
        try {
            Database.Database db = new Database.Database();
            java.util.List<Model.Produk> products = db.getAllProduk();
            
            if (products.isEmpty()) {
                Label noProductsLabel = new Label("No products available at the moment");
                noProductsLabel.getStyleClass().add("card-description");
                noProductsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
                
                VBox emptyState = new VBox(20);
                emptyState.setAlignment(Pos.CENTER);
                emptyState.setPadding(new Insets(50));
                emptyState.getChildren().add(noProductsLabel);
                
                scrollableContent.getChildren().addAll(header, emptyState);
            } else {
                // Create products grid
                GridPane productsGrid = new GridPane();
                productsGrid.setHgap(20);
                productsGrid.setVgap(20);
                productsGrid.setAlignment(Pos.CENTER);
                productsGrid.setPadding(new Insets(20, 0, 20, 0));
                
                int cols = 3; // Number of columns
                int row = 0;
                int col = 0;
                
                for (Model.Produk product : products) {
                    VBox productCard = createProductCard(product);
                    productsGrid.add(productCard, col, row);
                    
                    col++;
                    if (col >= cols) {
                        col = 0;
                        row++;
                    }
                }
                
                scrollableContent.getChildren().addAll(header, productsGrid);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading products: " + e.getMessage());
            errorLabel.getStyleClass().add("card-description");
            errorLabel.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 14px;");
            
            VBox errorState = new VBox(20);
            errorState.setAlignment(Pos.CENTER);
            errorState.setPadding(new Insets(50));
            errorState.getChildren().add(errorLabel);
            
            scrollableContent.getChildren().addAll(header, errorState);
        }
        
        scrollPane.setContent(scrollableContent);
        productsTabContent.getChildren().add(scrollPane);
    }

    private void loadAppointmentsContent() {
        appointmentsTabContent.getChildren().clear();
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.getStyleClass().add("appointments-scroll");
        
        VBox scrollableContent = new VBox(20);
        scrollableContent.setPadding(new Insets(20));
        
        Label header = new Label("Appointment Management");
        header.getStyleClass().add("section-title");
        
        HBox mainButtons = new HBox(0);
        mainButtons.setAlignment(Pos.CENTER);
        mainButtons.setPadding(new Insets(20, 0, 20, 0));
        
        Button bookBtn = new Button("Book New Appointment");
        bookBtn.getStyleClass().addAll("main-tab-button", "main-tab-left", "main-tab-active");
        bookBtn.setPrefWidth(200);
        
        Button upcomingBtn = new Button("Upcoming");
        upcomingBtn.getStyleClass().addAll("main-tab-button", "main-tab-middle");
        upcomingBtn.setPrefWidth(150);
        
        Button historyBtn = new Button("History");
        historyBtn.getStyleClass().addAll("main-tab-button", "main-tab-right");
        historyBtn.setPrefWidth(150);
        
        mainButtons.getChildren().addAll(bookBtn, upcomingBtn, historyBtn);
        
        VBox contentContainer = new VBox(15);
        
        loadBookAppointmentContent(contentContainer);
        
        bookBtn.setOnAction(e -> {
            setActiveMainTab(bookBtn, upcomingBtn, historyBtn);
            loadBookAppointmentContent(contentContainer);
        });
        
        upcomingBtn.setOnAction(e -> {
            setActiveMainTab(upcomingBtn, bookBtn, historyBtn);
            loadUpcomingAppointments(contentContainer);
        });
        
        historyBtn.setOnAction(e -> {
            setActiveMainTab(historyBtn, bookBtn, upcomingBtn);
            loadHistoryAppointments(contentContainer);
        });
        
        scrollableContent.getChildren().addAll(header, mainButtons, contentContainer);
        scrollPane.setContent(scrollableContent);
        
        appointmentsTabContent.getChildren().add(scrollPane);
    }

    private void setActiveMainTab(Button activeBtn, Button... otherBtns) {
        for (Button btn : otherBtns) {
            btn.getStyleClass().removeAll("main-tab-active");
        }
        activeBtn.getStyleClass().removeAll("main-tab-active");
        activeBtn.getStyleClass().add("main-tab-active");
        
        // Track which appointment tab is currently active
        if (activeBtn.getText().equals("Book New Appointment")) {
            currentAppointmentTab = "book";
        } else if (activeBtn.getText().equals("Upcoming")) {
            currentAppointmentTab = "upcoming";
        } else if (activeBtn.getText().equals("History")) {
            currentAppointmentTab = "history";
        }
    }

    private void loadBookAppointmentContent(VBox container) {
        container.getChildren().clear();
        
        if (showingAppointmentTypes) {
            loadAppointmentTypeSelection(container);
            return;
        }
        
        Label bookHeader = new Label("Book New Appointment");
        bookHeader.getStyleClass().add("card-title");
        
        VBox doctorSection = new VBox(15);
        doctorSection.setAlignment(Pos.CENTER);
        
        Label doctorSectionHeader = new Label("Select Your Doctor");
        doctorSectionHeader.getStyleClass().add("section-subtitle");
        doctorSectionHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        HBox doctorGrid = new HBox(10);
        doctorGrid.setAlignment(Pos.CENTER);
        
        loadDoctorCards(doctorGrid);
        
        doctorSection.getChildren().addAll(doctorSectionHeader, doctorGrid);
        
        VBox calendarSection = new VBox(15);
        calendarSection.setAlignment(Pos.CENTER);
        
        Label calendarHeader = new Label("Select Date & Time");
        calendarHeader.getStyleClass().add("section-subtitle");
        calendarHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        HBox weekNavigation = new HBox(15);
        weekNavigation.setAlignment(Pos.CENTER);
        weekNavigation.setPadding(new Insets(10, 0, 10, 0));        Button prevWeekBtn = new Button("< Previous Week");
        prevWeekBtn.getStyleClass().add("hero-button-secondary");

        Label weekLabel = new Label("Week of " + currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        weekLabel.getStyleClass().add("section-subtitle");
        weekLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button nextWeekBtn = new Button("Next Week >");
        nextWeekBtn.getStyleClass().add("hero-button-secondary");

        weekNavigation.getChildren().addAll(prevWeekBtn, weekLabel, nextWeekBtn);

        GridPane calendar = new GridPane();
        calendar.setHgap(5);
        calendar.setVgap(5);
        calendar.getStyleClass().add("service-card");
        calendar.setPadding(new Insets(15));

        for (int i = 0; i < 8; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.SOMETIMES);
            col.setMinWidth(100);
            calendar.getColumnConstraints().add(col);
        }

        for (int i = 0; i < 11; i++) {
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.SOMETIMES);
            row.setMinHeight(40);
            calendar.getRowConstraints().add(row);
        }

        loadBookingCalendar(calendar, weekLabel);

        prevWeekBtn.setOnAction(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            weekLabel.setText("Week of " + currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            loadBookingCalendar(calendar, weekLabel);
        });

        nextWeekBtn.setOnAction(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            weekLabel.setText("Week of " + currentWeekStart.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            loadBookingCalendar(calendar, weekLabel);
        });

        HBox legend = createAppointmentLegend();
        legend.setPadding(new Insets(15, 0, 0, 0));

        calendarSection.getChildren().addAll(calendarHeader, weekNavigation, calendar, legend);
        container.getChildren().addAll(bookHeader, doctorSection, calendarSection);
    }

    private void loadBookingCalendar(GridPane calendar, Label weekLabel) {
        calendar.getChildren().clear();

        Label timeHeader = new Label("Time");
        timeHeader.getStyleClass().add("calendar-header");
        calendar.add(timeHeader, 0, 0);

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            VBox dayHeaderContainer = new VBox(5);
            dayHeaderContainer.setAlignment(Pos.CENTER);

            Label dayHeader = new Label(days[i]);
            dayHeader.getStyleClass().add("calendar-header");

            LocalDate dayDate = currentWeekStart.plusDays(i);
            Label dateLabel = new Label(dayDate.getDayOfMonth() + "");
            dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #546e7a;");

            dayHeaderContainer.getChildren().addAll(dayHeader, dateLabel);
            calendar.add(dayHeaderContainer, i + 1, 0);
        }

        String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

        for (int row = 1; row <= timeSlots.length; row++) {
            Label timeLabel = new Label(timeSlots[row - 1]);
            timeLabel.getStyleClass().add("calendar-time");
            calendar.add(timeLabel, 0, row);

            for (int col = 1; col <= 7; col++) {
                LocalDate dayDate = currentWeekStart.plusDays(col - 1);
                VBox timeSlot = createBookingTimeSlot(dayDate, timeSlots[row - 1]);
                calendar.add(timeSlot, col, row);
            }
        }
    }

    private VBox createBookingTimeSlot(LocalDate date, String time) {
        VBox slot = new VBox(2);
        slot.getStyleClass().add("calendar-slot");
        slot.setAlignment(Pos.CENTER);
        slot.setPrefHeight(40);

        AvailabilityStatus status = getSimulatedAvailability(date, time);
        updateCustomerSlotAppearance(slot, status);

        Label statusLabel = new Label(getCustomerStatusText(status));
        statusLabel.getStyleClass().addAll("calendar-status-label", getCustomerStatusStyleClass(status));

        if (status == AvailabilityStatus.OCCUPIED) {
            Label occupiedLabel = new Label("Booked");
            occupiedLabel.getStyleClass().add("calendar-appointment");
            slot.getChildren().add(occupiedLabel);
        }

        slot.getChildren().add(statusLabel);

        if (status == AvailabilityStatus.AVAILABLE) {
            slot.setOnMouseClicked(event -> {
                if (selectedDoctor == null) {
                    showAlert("Select Doctor", "Please select a doctor first.");
                    return;
                }
                handleTimeSlotSelection(date, time);
            });
            slot.setStyle(slot.getStyle() + "-fx-cursor: hand;");
        }

        return slot;
    }

    public void refreshDashboard() {
        try {
            if (doctorDashboardInstance != null) {
                // Use reflection to call the method to avoid compile-time dependency
                java.lang.reflect.Method refreshMethod = doctorDashboardInstance.getClass()
                    .getMethod("refreshCalendarFromPatientBooking");
                refreshMethod.invoke(doctorDashboardInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean saveAppointmentToDatabase(LocalDate date, String time, String doctorName, String appointmentType, String notes) {
        try {
            Database.Database db = new Database.Database();
            
            Model.Dokter doctor = db.getDokterByNama(doctorName);
            if (doctor == null) {
                showAlert("Error", "Doctor not found: " + doctorName);
                return false;
            }
            
            int patientId = getCurrentPatientId();
            if (patientId == -1) {
                showAlert("Error", "Please log in as a patient to book appointments.");
                return false;
            }
            
            java.util.Date appointmentDateTime = java.sql.Timestamp.valueOf(
                date.toString() + " " + time + ":00"
            );
            
            Model.JadwalPemeriksaan jadwal = new Model.JadwalPemeriksaan(
                0, appointmentDateTime, patientId, doctor.getId()
            );
            
            boolean saved = db.addJadwalPemeriksaan(jadwal);
            
            if (saved) {
                String slotKey = date.toString() + "_" + time;
                bookedSlots.add(slotKey);
                refreshCalendarDisplay();
                addAppointmentNotification(patientId, doctorName, date, time, appointmentType);
                
                // Notify doctor dashboard if available
                refreshDashboard();
            }
            
            return saved;
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while saving the appointment: " + e.getMessage());
            return false;
        }
    }
    
    private void addAppointmentNotification(int patientId, String doctorName, LocalDate date, String time, String appointmentType) {
        try {
            Database.Database db = new Database.Database();
            
            String message = String.format(
                "Appointment confirmed with Dr. %s on %s at %s for %s",
                doctorName,
                date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                time,
                appointmentType
            );
            
            Model.Notifikasi notification = new Model.Notifikasi(
                0, message, new java.util.Date(), patientId
            );
            
            db.addNotifikasi(notification);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSaveAppointment() {
        if (selectedDoctor == null) {
            showAlert("No Doctor Selected", "Please select a doctor before booking an appointment.");
            return;
        }
        
        Alert bookingDialog = new Alert(Alert.AlertType.CONFIRMATION);
        bookingDialog.setTitle("Book Appointment");
        bookingDialog.setHeaderText("Confirm Appointment Booking");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        Label dateTimeLabel = new Label("Date & Time: " +
            selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + " at " + selectedTime);
        dateTimeLabel.getStyleClass().add("patient-name");

        Label doctorLabel = new Label("Doctor: " + selectedDoctor);
        doctorLabel.getStyleClass().add("field-label");

        Label typeLabel = new Label("Appointment Type: " + (selectedAppointmentType != null ? selectedAppointmentType : "Regular Checkup"));
        typeLabel.getStyleClass().add("field-label");

        Label notesLabel = new Label("Additional Notes:");
        notesLabel.getStyleClass().add("field-label");

        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        notesArea.setPromptText("Enter any additional information or symptoms...");

        content.getChildren().addAll(dateTimeLabel, doctorLabel, typeLabel, notesLabel, notesArea);

        bookingDialog.getDialogPane().setContent(content);

        bookingDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                boolean appointmentSaved = saveAppointmentToDatabase(selectedDate, selectedTime, selectedDoctor, 
                    selectedAppointmentType != null ? selectedAppointmentType : "Regular Checkup", 
                    notesArea.getText());
                
                if (appointmentSaved) {
                    showAlert("Success", "Appointment booked successfully!\n\n" +
                        "Doctor: " + selectedDoctor + "\n" +
                        "Date: " + selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + "\n" +
                        "Time: " + selectedTime + "\n" +
                        "Type: " + (selectedAppointmentType != null ? selectedAppointmentType : "Regular Checkup"));
                    
                    resetBookingSelections();
                    
                    VBox container = getAppointmentContentContainer();
                    if (container != null) {
                        loadBookAppointmentContent(container);
                    }
                } else {
                    showAlert("Error", "Failed to book appointment. Please try again.");
                }
            }
        });
    }
    
    private VBox createCartItemCard(CartItem item) {
        VBox card = new VBox(10);
        card.getStyleClass().add("service-card");
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Product info
        HBox productInfo = new HBox(15);
        productInfo.setAlignment(Pos.CENTER_LEFT);
        
        VBox productDetails = new VBox(5);
        productDetails.setAlignment(Pos.CENTER_LEFT);
        
        Label productName = new Label(item.getProduct().getNama());
        productName.getStyleClass().add("card-title");
        productName.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        String unitPrice = currencyFormat.format(item.getProduct().getHarga()).replace("IDR", "Rp");
        Label priceLabel = new Label("Unit Price: " + unitPrice);
        priceLabel.getStyleClass().add("card-description");
        
        productDetails.getChildren().addAll(productName, priceLabel);
        productInfo.getChildren().add(productDetails);
        
        // Quantity controls
        HBox quantityControls = new HBox(10);
        quantityControls.setAlignment(Pos.CENTER_LEFT);
        
        Label quantityLabel = new Label("Quantity:");
        quantityLabel.getStyleClass().add("field-label");
        
        Button decreaseBtn = new Button("-");
        decreaseBtn.getStyleClass().add("hero-button-secondary");
        decreaseBtn.setPrefWidth(30);
        decreaseBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                loadCartContent(); // Refresh cart display
            }
        });
        
        Label quantityDisplay = new Label(String.valueOf(item.getQuantity()));
        quantityDisplay.getStyleClass().add("field-label");
        quantityDisplay.setStyle("-fx-font-weight: bold; -fx-min-width: 30; -fx-alignment: center;");
        
        Button increaseBtn = new Button("+");
        increaseBtn.getStyleClass().add("hero-button-secondary");
        increaseBtn.setPrefWidth(30);
        increaseBtn.setOnAction(e -> {
            if (item.getQuantity() < item.getProduct().getStok()) {
                item.setQuantity(item.getQuantity() + 1);
                loadCartContent(); // Refresh cart display
            } else {
                showAlert("Stock Limit", "Cannot add more items. Stock limit reached.");
            }
        });
        
        quantityControls.getChildren().addAll(quantityLabel, decreaseBtn, quantityDisplay, increaseBtn);
        
        // Total price and remove button
        HBox bottomRow = new HBox(15);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        
        String totalPrice = currencyFormat.format(item.getTotalPrice()).replace("IDR", "Rp");
        Label totalLabel = new Label("Total: " + totalPrice);
        totalLabel.getStyleClass().add("section-subtitle");
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c5282;");
        
        Button removeBtn = new Button("Remove");
        removeBtn.getStyleClass().add("hero-button-secondary");
        removeBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> {
            cartItems.remove(item);
            loadCartContent(); // Refresh cart display
            showAlert("Item Removed", item.getProduct().getNama() + " removed from cart.");
        });
        
        bottomRow.getChildren().addAll(totalLabel, new javafx.scene.layout.Region(), removeBtn);
        javafx.scene.layout.HBox.setHgrow(bottomRow.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        
        card.getChildren().addAll(productInfo, quantityControls, bottomRow);
        return card;
    }
    
    private void handleClearCart() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Clear Cart");
        confirmDialog.setHeaderText("Are you sure you want to clear your cart?");
        confirmDialog.setContentText("This action will remove all items from your cart.");
        
        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                cartItems.clear();
                loadCartContent();
                showAlert("Cart Cleared", "All items have been removed from your cart.");
            }
        });
    }
    
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add some items before checkout.");
            return;
        }
        
        // Calculate total
        double totalAmount = cartItems.stream()
            .mapToDouble(CartItem::getTotalPrice)
            .sum();
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        String formattedTotal = currencyFormat.format(totalAmount).replace("IDR", "Rp");
        
        Alert checkoutDialog = new Alert(Alert.AlertType.CONFIRMATION);
        checkoutDialog.setTitle("Checkout Confirmation");
        checkoutDialog.setHeaderText("Confirm Your Order");
        
        // Create detailed checkout content
        VBox checkoutContent = new VBox(15);
        checkoutContent.setPadding(new Insets(20));
        
        Label summaryHeader = new Label("Order Summary:");
        summaryHeader.getStyleClass().add("card-title");
        summaryHeader.setStyle("-fx-font-weight: bold;");
        
        VBox itemsList = new VBox(5);
        for (CartItem item : cartItems) {
            String itemLine = item.getQuantity() + "x " + item.getProduct().getNama() + " - " + 
                            currencyFormat.format(item.getTotalPrice()).replace("IDR", "Rp");
            Label itemLabel = new Label(itemLine);
            itemLabel.getStyleClass().add("field-label");
            itemsList.getChildren().add(itemLabel);
        }
        
        Label totalLabel = new Label("Total: " + formattedTotal);
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c5282;");
        
        checkoutContent.getChildren().addAll(summaryHeader, itemsList, totalLabel);
        checkoutDialog.getDialogPane().setContent(checkoutContent);
        
        checkoutDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Process checkout
                boolean checkoutSuccess = processCheckout();
                if (checkoutSuccess) {
                    cartItems.clear();
                    loadCartContent();
                    showAlert("Order Placed", "Your order has been placed successfully!\nTotal: " + formattedTotal);
                } else {
                    showAlert("Checkout Failed", "There was an error processing your order. Please try again.");
                }
            }
        });
    }    private boolean processCheckout() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Your cart is empty. Add some products first.");
            return false;
        }
        
        try {
            // Calculate total
            double total = cartItems.stream().mapToDouble(CartItem::getTotalPrice).sum();
            
            // Get customer ID from logged name
            Database.Database db = new Database.Database();
            
            // DEBUG: Print logged name
            System.out.println("DEBUG - Logged user name: '" + loggedName + "'");
            
            int customerId = db.getPelangganIdByNama(loggedName);
            
            // DEBUG: Print customer ID lookup result
            System.out.println("DEBUG - Customer ID lookup result: " + customerId);
            
            if (customerId == -1) {
                // DEBUG: Try to see all customers in database
                System.out.println("DEBUG - All customers in database:");
                try {
                    java.util.List<Model.Pelanggan> allCustomers = db.getAllPelanggan();
                    for (Model.Pelanggan customer : allCustomers) {
                        System.out.println("  ID: " + customer.getId() + ", Name: '" + customer.getNama() + "'");
                    }
                } catch (Exception e) {
                    System.out.println("DEBUG - Error getting customers: " + e.getMessage());
                }
                
                showAlert("Error", "Could not find customer information. Please login again.\nLogged name: '" + loggedName + "'");
                return false;
            }
            
            // Validate stock availability before checkout
            for (CartItem item : cartItems) {
                Model.Produk currentProduct = db.getProdukById(item.getProduct().getId());
                if (currentProduct == null) {
                    showAlert("Error", "Product " + item.getProduct().getNama() + " no longer exists.");
                    return false;
                }
                if (currentProduct.getStok() < item.getQuantity()) {
                    showAlert("Insufficient Stock", "Not enough stock for " + item.getProduct().getNama() + 
                             ".\nAvailable: " + currentProduct.getStok() + ", Requested: " + item.getQuantity());
                    return false;
                }
            }
            
            // Save order to database with stock reduction
            boolean orderSaved = db.addPesananWithDetails(customerId, cartItems, total);
            
            if (!orderSaved) {
                showAlert("Error", "Failed to save order to database. Please try again.");
                return false;
            }
            
            // Show enhanced order confirmation
            showOrderConfirmationWithDatabase(total, customerId);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during checkout: " + e.getMessage());
            return false;
        }
    }
    
    private void showOrderConfirmationWithDatabase(double total, int customerId) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmed");
        alert.setHeaderText("Your order has been successfully placed!");
        
        StringBuilder content = new StringBuilder();
        content.append("Order Details:\n\n");
        
        // Show items from the cart that was just processed
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));
        for (CartItem item : cartItems) {
            content.append(String.format("• %s x%d - %s\n", 
                item.getProduct().getNama(), 
                item.getQuantity(),
                currencyFormat.format(item.getTotalPrice()).replace("IDR", "Rp")));
        }
        
        content.append("\n");
        content.append(String.format("Total: %s\n", currencyFormat.format(total).replace("IDR", "Rp")));
        content.append("Status: Order Completed\n");
        content.append("Customer ID: ").append(customerId).append("\n\n");
        content.append("Thank you for your purchase!\n");
        content.append("Your products have been ordered and stock has been updated.");
        
        alert.setContentText(content.toString());
        alert.showAndWait();
    }
}