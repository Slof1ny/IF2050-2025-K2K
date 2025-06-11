package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.HashMap;

public class DoctorDashboardController implements Initializable {

    // Header components
    @FXML private Label welcomeBanner;
    @FXML private Button logoutButton;
    
    // Navigation tabs
    @FXML private Button appointmentsTab;
    @FXML private Button patientsTab;
    @FXML private Button scheduleTab;
    @FXML private Button profileTab;
    
    // Tab content containers
    @FXML private HBox appointmentsTabContent;
    @FXML private VBox patientsTabContent;
    @FXML private VBox scheduleTabContent;
    @FXML private VBox profileTabContent;
    
    // Appointments tab components
    @FXML private DatePicker appointmentDatePicker;
    @FXML private Button viewAppointmentsButton;
    @FXML private ScrollPane appointmentsScrollPane;
    @FXML private VBox appointmentsContainer;
    
    // Statistics labels
    @FXML private Label todayPatientsCount;
    @FXML private Label pendingCount;
    @FXML private Label completedCount;
    @FXML private Label totalPatientsCount;
    
    // Schedule tab components
    @FXML private Button prevWeekButton;
    @FXML private Button nextWeekButton;
    @FXML private Label currentWeekLabel;
    @FXML private GridPane weeklyCalendar;
    
    // Content containers
    @FXML private VBox patientsContainer;
    @FXML private VBox profileContainer;
    
    // Quick action buttons
    @FXML private Button viewReportsButton;
    @FXML private Button prescriptionsButton;
    @FXML private Button settingsButton;
    
    private static String doctorName;
    private LocalDate currentWeekStart;

    // Add enum for availability status
    private enum AvailabilityStatus {
        AVAILABLE, OCCUPIED, UNAVAILABLE
    }
    
    // Add data structure to store availability
    private Map<String, AvailabilityStatus> doctorAvailability = new HashMap<>();
    
    // Add field to track booked appointments for real-time updates
    private java.util.Set<String> bookedSlots = new java.util.HashSet<>();
    
    // Add data structure to store doctor's custom availability
    private Map<String, AvailabilityStatus> customAvailability = new HashMap<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default date to today
        appointmentDatePicker.setValue(LocalDate.now());
        currentWeekStart = LocalDate.now().with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        
        // Initialize with real data
        loadRealData();
        
        // Set active tab styling and show appointments tab
        setActiveTab(appointmentsTab);
        showAppointmentsTab();
        
        // Initialize appointment cards
        loadAppointmentCards();
        
        // Register this instance for patient dashboard notifications
        DashboardController.setDoctorDashboardInstance(this);
    }
    
    public static String getLoggedDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
        welcomeBanner.setText("Selamat datang, DR. " + doctorName);
        
        // Reload data for this specific doctor
        loadRealData();
    }
    
    private void loadRealData() {
        try {
            Database.Database db = new Database.Database();
            LocalDate today = LocalDate.now();
            
            // Calculate real statistics
            int todayPatients = calculateTodayPatients(db, today);
            int pendingAppointments = calculatePendingAppointments(db, today);
            int completedToday = calculateCompletedToday(db, today);
            int totalPatients = calculateTotalPatients(db);
            
            // Update statistics labels
            todayPatientsCount.setText(String.valueOf(todayPatients));
            pendingCount.setText(String.valueOf(pendingAppointments));
            completedCount.setText(String.valueOf(completedToday));
            totalPatientsCount.setText(String.valueOf(totalPatients));
            
            // Load today's appointments
            loadTodayAppointments(db, today);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private int calculateTodayPatients(Database.Database db, LocalDate date) {
        if (doctorName != null) {
            try {
                // Get doctor by name first
                Model.Dokter doctor = db.getDokterByNama(doctorName);
                if (doctor != null) {
                    // Get appointments for this doctor today
                    java.util.List<Model.JadwalPemeriksaan> todayAppointments = 
                        db.getJadwalPemeriksaanByDokterId(doctor.getId());
                    
                    // Filter for today's date
                    long todayCount = todayAppointments.stream()
                        .filter(appointment -> {
                            LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            return appointmentDate.equals(date);
                        })
                        .count();
                    
                    return (int) todayCount;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private int calculatePendingAppointments(Database.Database db, LocalDate date) {
        if (doctorName != null) {
            try {
                Model.Dokter doctor = db.getDokterByNama(doctorName);
                if (doctor != null) {
                    java.util.List<Model.JadwalPemeriksaan> appointments = 
                        db.getJadwalPemeriksaanByDokterId(doctor.getId());
                    
                    // Filter for today's date and future appointments (assuming pending means upcoming)
                    long pendingCount = appointments.stream()
                        .filter(appointment -> {
                            LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            return !appointmentDate.isBefore(date);
                        })
                        .count();
                    
                    return (int) pendingCount;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private int calculateCompletedToday(Database.Database db, LocalDate date) {
        if (doctorName != null) {
            try {
                Model.Dokter doctor = db.getDokterByNama(doctorName);
                if (doctor != null) {
                    java.util.List<Model.JadwalPemeriksaan> appointments = 
                        db.getJadwalPemeriksaanByDokterId(doctor.getId());
                    
                    // Filter for past appointments today (assuming completed means past appointments)
                    long completedCount = appointments.stream()
                        .filter(appointment -> {
                            LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            return appointmentDate.equals(date) && 
                                   appointment.getTanggalWaktu().before(new java.util.Date());
                        })
                        .count();
                    
                    return (int) completedCount;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private int calculateTotalPatients(Database.Database db) {
        if (doctorName != null) {
            try {
                Model.Dokter doctor = db.getDokterByNama(doctorName);
                if (doctor != null) {
                    java.util.List<Model.JadwalPemeriksaan> appointments = 
                        db.getJadwalPemeriksaanByDokterId(doctor.getId());
                    
                    // Get unique patient count
                    long uniquePatients = appointments.stream()
                        .mapToInt(Model.JadwalPemeriksaan::getIdPasien)
                        .distinct()
                        .count();
                    
                    return (int) uniquePatients;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
    
    private void loadTodayAppointments(Database.Database db, LocalDate date) {
        try {
            if (doctorName != null) {
                Model.Dokter doctor = db.getDokterByNama(doctorName);
                if (doctor != null) {
                    java.util.List<Model.JadwalPemeriksaan> appointments = 
                        db.getJadwalPemeriksaanByDokterId(doctor.getId());
                    
                    // Filter for today's appointments
                    java.util.List<Model.JadwalPemeriksaan> todayAppointments = appointments.stream()
                        .filter(appointment -> {
                            LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            return appointmentDate.equals(date);
                        })
                        .collect(java.util.stream.Collectors.toList());
                    
                    // Load appointment cards with real data
                    loadRealAppointmentCards(todayAppointments, db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadAppointmentCards(); // Fallback to dummy data
        }
    }
    
    private void loadRealAppointmentCards(java.util.List<Model.JadwalPemeriksaan> appointments, Database.Database db) {
        appointmentsContainer.getChildren().clear();
        
        if (appointments.isEmpty()) {
            Label noAppointmentsLabel = new Label("No appointments scheduled for today");
            noAppointmentsLabel.getStyleClass().add("card-description");
            appointmentsContainer.getChildren().add(noAppointmentsLabel);
            return;
        }
        
        for (Model.JadwalPemeriksaan appointment : appointments) {
            try {
                // Get patient information
                Model.Pelanggan patient = db.getPelangganById(appointment.getIdPasien());
                if (patient != null) {
                    VBox appointmentCard = createPatientCard(
                        patient.getNama(),
                        patient.getEmail(),
                        "Scheduled Appointment" // You can enhance this based on appointment type
                    );
                    appointmentsContainer.getChildren().add(appointmentCard);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void clearAppointmentData() {
        // Clear appointment container
        if (appointmentsContainer != null) {
            appointmentsContainer.getChildren().clear();
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Load the login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root, 950, 600);
            stage.setScene(scene);
            stage.setTitle("Optik XYZ - Login");
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login screen: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleAppointmentsTab(ActionEvent event) {
        setActiveTab(appointmentsTab);
        showAppointmentsTab();
    }
    
    @FXML
    private void handlePatientsTab(ActionEvent event) {
        setActiveTab(patientsTab);
        showPatientsTab();
    }
    
    @FXML
    private void handleScheduleTab(ActionEvent event) {
        setActiveTab(scheduleTab);
        showScheduleTab();
    }
    
    @FXML
    private void handleProfileTab(ActionEvent event) {
        setActiveTab(profileTab);
        showProfileTab();
    }

    private void setActiveTab(Button activeTab) {
        // Reset all tabs to default style (remove active class)
        appointmentsTab.getStyleClass().remove("nav-tab-active");
        patientsTab.getStyleClass().remove("nav-tab-active");
        scheduleTab.getStyleClass().remove("nav-tab-active");
        profileTab.getStyleClass().remove("nav-tab-active");
        
        // Add active class to the selected tab
        activeTab.getStyleClass().add("nav-tab-active");
    }
    
    private void showAppointmentsTab() {
        hideAllTabs();
        appointmentsTabContent.setVisible(true);
        appointmentsTabContent.setManaged(true);
    }

    private void showPatientsTab() {
        hideAllTabs();
        patientsTabContent.setVisible(true);
        patientsTabContent.setManaged(true);
        loadPatientsData();
    }

    private void showScheduleTab() {
        hideAllTabs();
        scheduleTabContent.setVisible(true);
        scheduleTabContent.setManaged(true);
        loadWeeklyCalendar();
    }

    private void showProfileTab() {
        hideAllTabs();
        profileTabContent.setVisible(true);
        profileTabContent.setManaged(true);
        loadProfileData();
    }

    private void hideAllTabs() {
        appointmentsTabContent.setVisible(false);
        appointmentsTabContent.setManaged(false);
        patientsTabContent.setVisible(false);
        patientsTabContent.setManaged(false);
        scheduleTabContent.setVisible(false);
        scheduleTabContent.setManaged(false);
        profileTabContent.setVisible(false);
        profileTabContent.setManaged(false);
    }

    private void loadAppointmentCards() {
        appointmentsContainer.getChildren().clear();
        
        // Create sample appointment cards
        appointmentsContainer.getChildren().addAll(
            createPatientCard("John Doe", "john.doe@email.com", "Regular Checkup"),
            createPatientCard("Jane Smith", "jane.smith@email.com", "Follow-up Visit"),
            createPatientCard("Alice Johnson", "alice.johnson@email.com", "Eye Examination"),
            createPatientCard("Bob Wilson", "bob.wilson@email.com", "Prescription Update")
        );
    }

    private void loadPatientsData() {
        patientsContainer.getChildren().clear();
        
        // Create patient list items
        patientsContainer.getChildren().addAll(
            createPatientListItem("John Doe", "john.doe@email.com", "Last visit: 2024-01-15", "Active"),
            createPatientListItem("Jane Smith", "jane.smith@email.com", "Last visit: 2024-01-10", "Active"),
            createPatientListItem("Alice Johnson", "alice.johnson@email.com", "Last visit: 2024-01-08", "Follow-up needed"),
            createPatientListItem("Bob Wilson", "bob.wilson@email.com", "Last visit: 2024-01-05", "Active"),
            createPatientListItem("Carol Davis", "carol.davis@email.com", "Last visit: 2024-01-03", "Active"),
            createPatientListItem("David Brown", "david.brown@email.com", "Last visit: 2024-01-01", "Inactive")
        );
    }

    private void loadWeeklyCalendar() {
        weeklyCalendar.getChildren().clear();
        weeklyCalendar.getColumnConstraints().clear();
        weeklyCalendar.getRowConstraints().clear();
        
        // Update week label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        currentWeekLabel.setText("Week of " + currentWeekStart.format(formatter));
        
        // Setup column constraints (8 columns: time + 7 days)
        for (int i = 0; i < 8; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setHgrow(Priority.SOMETIMES);
            col.setMinWidth(100);
            weeklyCalendar.getColumnConstraints().add(col);
        }
        
        // Setup row constraints (time slots + legend)
        for (int i = 0; i < 12; i++) { // Header + 10 time slots + legend
            RowConstraints row = new RowConstraints();
            row.setVgrow(Priority.SOMETIMES);
            row.setMinHeight(40);
            weeklyCalendar.getRowConstraints().add(row);
        }
        
        // Create header row
        Label timeHeader = new Label("Time");
        timeHeader.getStyleClass().add("calendar-header");
        weeklyCalendar.add(timeHeader, 0, 0);
        
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int i = 0; i < days.length; i++) {
            VBox dayHeaderContainer = new VBox(5);
            dayHeaderContainer.setAlignment(Pos.CENTER);
            
            Label dayHeader = new Label(days[i]);
            dayHeader.getStyleClass().add("calendar-header");
            
            // Add date under day name
            LocalDate dayDate = currentWeekStart.plusDays(i);
            Label dateLabel = new Label(dayDate.getDayOfMonth() + "");
            dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #546e7a;");
            
            dayHeaderContainer.getChildren().addAll(dayHeader, dateLabel);
            weeklyCalendar.add(dayHeaderContainer, i + 1, 0);
        }
        
        // Create time slots with real-time availability checking
        String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

        for (int row = 1; row <= timeSlots.length; row++) {
            Label timeLabel = new Label(timeSlots[row - 1]);
            timeLabel.getStyleClass().add("calendar-time");
            weeklyCalendar.add(timeLabel, 0, row);

            for (int col = 1; col <= 7; col++) {
                LocalDate dayDate = currentWeekStart.plusDays(col - 1);
                VBox timeSlot = createTimeSlot(dayDate, timeSlots[row - 1]);
                weeklyCalendar.add(timeSlot, col, row);
            }
        }
        
        // Add legend
        createCalendarLegend();
    }

    private VBox createTimeSlot(LocalDate date, String time) {
        VBox slot = new VBox(2);
        slot.getStyleClass().add("calendar-slot");
        slot.setAlignment(Pos.CENTER);
        slot.setPrefHeight(40);

        AvailabilityStatus status = getAvailability(date, time);
        updateSlotAppearance(slot, status);

        if (status == AvailabilityStatus.OCCUPIED) {
            // Get patient info for this appointment
            String patientInfo = getPatientInfoForSlot(date, time);
            if (!patientInfo.isEmpty()) {
                Label appointmentLabel = new Label(patientInfo);
                appointmentLabel.getStyleClass().add("calendar-appointment");
                appointmentLabel.setMaxWidth(80);
                appointmentLabel.setWrapText(true);
                slot.getChildren().add(appointmentLabel);
            } else {
                Label bookedLabel = new Label("Booked");
                bookedLabel.getStyleClass().add("calendar-appointment");
                slot.getChildren().add(bookedLabel);
            }
        }

        Label statusLabel = new Label(getStatusText(status));
        statusLabel.getStyleClass().addAll("calendar-status-label", getStatusStyleClass(status));
        slot.getChildren().add(statusLabel);

        // Add click handler for available and unavailable slots (not occupied ones)
        if (status != AvailabilityStatus.OCCUPIED) {
            slot.setOnMouseClicked(event -> handleSlotClick(date, time, slot));
            slot.setStyle(slot.getStyle() + "-fx-cursor: hand;");
            
            // Add hover effect
            slot.setOnMouseEntered(e -> slot.setStyle(slot.getStyle() + "-fx-opacity: 0.8;"));
            slot.setOnMouseExited(e -> slot.setStyle(slot.getStyle().replace("-fx-opacity: 0.8;", "")));
        }

        return slot;
    }
    
    private void handleSlotClick(LocalDate date, String time, VBox slot) {
        String slotKey = date.toString() + "_" + time;
        
        // Get current status
        AvailabilityStatus currentStatus = getAvailability(date, time);
        
        // Don't allow editing if there's already an appointment
        if (isSlotBookedForDoctor(date, time)) {
            showAlert("Cannot Edit", "This time slot already has an appointment and cannot be modified.");
            return;
        }
        
        // Toggle between available and unavailable
        AvailabilityStatus newStatus;
        if (currentStatus == AvailabilityStatus.AVAILABLE) {
            newStatus = AvailabilityStatus.UNAVAILABLE;
        } else {
            newStatus = AvailabilityStatus.AVAILABLE;
        }
        
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Change Availability");
        confirmDialog.setHeaderText("Update Time Slot Availability");
        
        String dateStr = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        String statusStr = newStatus == AvailabilityStatus.AVAILABLE ? "Available" : "Unavailable";
        
        confirmDialog.setContentText(String.format(
            "Do you want to set %s at %s as %s?\n\n" +
            "This will affect your schedule for patient bookings.",
            dateStr, time, statusStr
        ));
        
        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Update custom availability
                customAvailability.put(slotKey, newStatus);
                
                // Save to database/preferences if needed
                saveAvailabilityToDatabase(date, time, newStatus);
                
                // Update slot appearance
                updateSlotAppearance(slot, newStatus);
                
                // Update status label
                Label statusLabel = (Label) slot.getChildren().get(slot.getChildren().size() - 1);
                statusLabel.setText(getStatusText(newStatus));
                statusLabel.getStyleClass().removeAll("status-available", "status-occupied", "status-unavailable");
                statusLabel.getStyleClass().add(getStatusStyleClass(newStatus));
                
                // Show success message
                showAlert("Success", String.format(
                    "Time slot %s on %s has been set as %s",
                    time, dateStr, statusStr
                ));
            }
        });
    }
    
    private void saveAvailabilityToDatabase(LocalDate date, String time, AvailabilityStatus status) {
        try {
            Database.Database db = new Database.Database();
            
            // Get current doctor
            Model.Dokter doctor = db.getDokterByNama(doctorName);
            if (doctor == null) {
                return;
            }
            
            // For now, we'll store this in a simple way
            // You might want to create a separate table for doctor availability
            // This is a placeholder implementation
            
            System.out.println(String.format(
                "Saving availability for Dr. %s: %s %s = %s",
                doctorName, date, time, status
            ));
            
            // TODO: Implement actual database storage for custom availability
            // This could involve creating a DoctorAvailability table with columns:
            // - doctor_id, date, time, status
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private AvailabilityStatus getAvailability(LocalDate date, String time) {
        String slotKey = date.toString() + "_" + time;
        
        // Check if this slot has been booked during this session
        if (bookedSlots.contains(slotKey)) {
            return AvailabilityStatus.OCCUPIED;
        }
        
        // Check database for existing appointments for this doctor
        if (isSlotBookedForDoctor(date, time)) {
            return AvailabilityStatus.OCCUPIED;
        }
        
        // Check custom availability set by doctor
        if (customAvailability.containsKey(slotKey)) {
            return customAvailability.get(slotKey);
        }
        
        // Check if availability was saved to database
        AvailabilityStatus savedStatus = loadAvailabilityFromDatabase(date, time);
        if (savedStatus != null) {
            return savedStatus;
        }
        
        // Default availability rules
        if (date.getDayOfWeek().getValue() == 7) { // Sunday
            return AvailabilityStatus.UNAVAILABLE;
        }

        if (time.equals("12:00") || time.equals("13:00")) { // Lunch break
            return AvailabilityStatus.UNAVAILABLE;
        }

        return AvailabilityStatus.AVAILABLE;
    }
    
    private AvailabilityStatus loadAvailabilityFromDatabase(LocalDate date, String time) {
        try {
            Database.Database db = new Database.Database();
            
            // Get current doctor
            Model.Dokter doctor = db.getDokterByNama(doctorName);
            if (doctor == null) {
                return null;
            }
            
            // TODO: Implement actual database loading for custom availability
            // This would query the DoctorAvailability table
            
            return null; // Placeholder
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void createCalendarLegend() {
        // Create legend in the last row of the calendar
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER);
        legend.getStyleClass().add("calendar-legend");

        // Available legend item
        HBox availableItem = new HBox(8);
        availableItem.setAlignment(Pos.CENTER_LEFT);

        Label availableColor = new Label();
        availableColor.getStyleClass().addAll("legend-color", "legend-available");
        availableColor.setPrefWidth(15);
        availableColor.setPrefHeight(15);

        Label availableText = new Label("Available - Click to toggle");
        availableText.getStyleClass().add("legend-text");

        availableItem.getChildren().addAll(availableColor, availableText);

        // Occupied legend item
        HBox occupiedItem = new HBox(8);
        occupiedItem.setAlignment(Pos.CENTER_LEFT);

        Label occupiedColor = new Label();
        occupiedColor.getStyleClass().addAll("legend-color", "legend-occupied");
        occupiedColor.setPrefWidth(15);
        occupiedColor.setPrefHeight(15);

        Label occupiedText = new Label("Occupied - Cannot edit");
        occupiedText.getStyleClass().add("legend-text");

        occupiedItem.getChildren().addAll(occupiedColor, occupiedText);

        // Unavailable legend item
        HBox unavailableItem = new HBox(8);
        unavailableItem.setAlignment(Pos.CENTER_LEFT);

        Label unavailableColor = new Label();
        unavailableColor.getStyleClass().addAll("legend-color", "legend-unavailable");
        unavailableColor.setPrefWidth(15);
        unavailableColor.setPrefHeight(15);

        Label unavailableText = new Label("Unavailable - Click to toggle");
        unavailableText.getStyleClass().add("legend-text");

        unavailableItem.getChildren().addAll(unavailableColor, unavailableText);

        legend.getChildren().addAll(availableItem, occupiedItem, unavailableItem);

        // Add legend to the calendar at the bottom
        weeklyCalendar.add(legend, 0, 11, 8, 1); // Span across all columns
        GridPane.setHalignment(legend, HPos.CENTER);
    }

    // Add method to reset availability to defaults
    @FXML
    private void handleResetAvailability(ActionEvent event) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Reset Availability");
        confirmDialog.setHeaderText("Reset Calendar to Default");
        confirmDialog.setContentText(
            "This will reset your calendar to default availability settings:\n\n" +
            "• Available: Monday-Saturday 9:00-11:00, 14:00-18:00\n" +
            "• Unavailable: Sunday and lunch hours (12:00-13:00)\n\n" +
            "All custom availability settings will be lost. Continue?"
        );
        
        confirmDialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // Clear custom availability
                customAvailability.clear();
                
                // Clear from database
                clearAvailabilityFromDatabase();
                
                // Refresh calendar
                if (scheduleTabContent.isVisible()) {
                    loadWeeklyCalendar();
                }
                
                showAlert("Success", "Calendar availability has been reset to default settings.");
            }
        });
    }
    
    private void clearAvailabilityFromDatabase() {
        try {
            Database.Database db = new Database.Database();
            
            // Get current doctor
            Model.Dokter doctor = db.getDokterByNama(doctorName);
            if (doctor == null) {
                return;
            }
            
            // TODO: Implement database clearing for custom availability
            System.out.println("Clearing custom availability for Dr. " + doctorName);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshCalendarView() {
        // This method refreshes the calendar display
        try {
            Database.Database db = new Database.Database();
            LocalDate today = LocalDate.now();
            
            // Reload statistics
            loadRealData();
            
            // Reload today's appointments
            loadTodayAppointments(db, today);
            
            // If calendar is visible, refresh it
            if (scheduleTabContent.isVisible()) {
                loadWeeklyCalendar();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private boolean isSlotBookedForDoctor(LocalDate date, String time) {
        try {
            Database.Database db = new Database.Database();
            
            // Get current doctor's ID
            Model.Dokter currentDoctor = db.getDokterByNama(doctorName);
            if (currentDoctor == null) {
                return false;
            }
            
            java.util.List<Model.JadwalPemeriksaan> doctorAppointments = 
                db.getJadwalPemeriksaanByDokterId(currentDoctor.getId());
            
            for (Model.JadwalPemeriksaan appointment : doctorAppointments) {
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
    
    private String getPatientInfoForSlot(LocalDate date, String time) {
        try {
            Database.Database db = new Database.Database();
            
            // Get current doctor's ID
            Model.Dokter currentDoctor = db.getDokterByNama(doctorName);
            if (currentDoctor == null) {
                return "";
            }
            
            java.util.List<Model.JadwalPemeriksaan> doctorAppointments = 
                db.getJadwalPemeriksaanByDokterId(currentDoctor.getId());
            
            for (Model.JadwalPemeriksaan appointment : doctorAppointments) {
                LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                String appointmentTime = appointment.getTanggalWaktu().toInstant()
                    .atZone(java.time.ZoneId.systemDefault()).toLocalTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                
                if (appointmentDate.equals(date) && appointmentTime.equals(time)) {
                    // Get patient name
                    Model.Pelanggan patient = db.getPelangganById(appointment.getIdPasien());
                    if (patient != null) {
                        return patient.getNama();
                    }
                    return "Patient";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void updateSlotAppearance(VBox slot, AvailabilityStatus status) {
        // Remove existing status classes
        slot.getStyleClass().removeAll("calendar-slot-available", "calendar-slot-occupied", "calendar-slot-unavailable");
        
        // Add appropriate status class based on availability
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

    private String getStatusText(AvailabilityStatus status) {
        switch (status) {
            case AVAILABLE:
                return "Available";
            case OCCUPIED:
                return "Booked";
            case UNAVAILABLE:
                return "Unavailable";
            default:
                return "";
        }
    }

    private String getStatusStyleClass(AvailabilityStatus status) {
        switch (status) {
            case AVAILABLE:
                return "status-available";
            case OCCUPIED:
                return "status-occupied";
            case UNAVAILABLE:
                return "status-unavailable";
            default:
                return "";
        }
    }

    private void loadCalendar(GridPane calendar, Label weekLabel) {
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

        // Create time slots with real-time availability checking
        String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"};

        for (int row = 1; row <= timeSlots.length; row++) {
            Label timeLabel = new Label(timeSlots[row - 1]);
            timeLabel.getStyleClass().add("calendar-time");
            calendar.add(timeLabel, 0, row);

            for (int col = 1; col <= 7; col++) {
                LocalDate dayDate = currentWeekStart.plusDays(col - 1);
                VBox timeSlot = createTimeSlot(dayDate, timeSlots[row - 1]);
                calendar.add(timeSlot, col, row);
            }
        }
    }

    @FXML
    private void handlePrevWeek(ActionEvent event) {
        currentWeekStart = currentWeekStart.minusWeeks(1);
        loadWeeklyCalendar();
    }

    @FXML
    private void handleNextWeek(ActionEvent event) {
        currentWeekStart = currentWeekStart.plusWeeks(1);
        loadWeeklyCalendar();
    }

    @FXML
    private void handleDateChange(ActionEvent event) {
        LocalDate selectedDate = appointmentDatePicker.getValue();
        if (selectedDate != null) {
            updateAppointmentsForDate(selectedDate);
        }
    }
    
    @FXML
    private void handleViewAppointments(ActionEvent event) {
        LocalDate selectedDate = appointmentDatePicker.getValue();
        if (selectedDate != null) {
            updateAppointmentsForDate(selectedDate);
            showAlert("Info", "Updated appointments for: " + selectedDate);
        } else {
            showAlert("Error", "Please select a date first.");
        }
    }
    
    @FXML
    private void handlePatientDetails(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        String patientInfo = "Selected Patient";
        
        showAlert("Patient Details", "Viewing details for: " + patientInfo);
    }
    
    @FXML
    private void handleViewReports(ActionEvent event) {
        showAlert("View Reports", "View Reports functionality will be implemented here.");
    }
    
    @FXML
    private void handlePrescriptions(ActionEvent event) {
        showAlert("Prescriptions", "Prescriptions functionality will be implemented here.");
    }
    
    @FXML
    private void handleSettings(ActionEvent event) {
        showAlert("Settings", "Settings functionality will be implemented here.");
    }

    private void updateAppointmentsForDate(LocalDate date) {
        try {
            Database.Database db = new Database.Database();
            
            // Update statistics for selected date
            int dayPatients = calculateTodayPatients(db, date);
            int pending = calculatePendingAppointments(db, date);
            int completed = calculateCompletedToday(db, date);
            
            todayPatientsCount.setText(String.valueOf(dayPatients));
            pendingCount.setText(String.valueOf(pending));
            completedCount.setText(String.valueOf(completed));
            
            // Load appointments for selected date
            loadAppointmentsForDate(db, date);
            
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback for selected date
            if (date.equals(LocalDate.now())) {
                loadAppointmentCards();
            } else {
                clearAppointmentData();
            }
        }
    }
    
    private void loadAppointmentsForDate(Database.Database db, LocalDate date) {
        try {
            if (doctorName != null) {
                Model.Dokter doctor = db.getDokterByNama(doctorName);
                if (doctor != null) {
                    java.util.List<Model.JadwalPemeriksaan> appointments = 
                        db.getJadwalPemeriksaanByDokterId(doctor.getId());
                    
                    // Filter for selected date's appointments
                    java.util.List<Model.JadwalPemeriksaan> dateAppointments = appointments.stream()
                        .filter(appointment -> {
                            LocalDate appointmentDate = appointment.getTanggalWaktu().toInstant()
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                            return appointmentDate.equals(date);
                        })
                        .collect(java.util.stream.Collectors.toList());
                    
                    // Load appointment cards with real data
                    loadRealAppointmentCards(dateAppointments, db);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            clearAppointmentData();
        }
    }

    private void loadProfileData() {
        profileContainer.getChildren().clear();
        
        VBox profileInfo = new VBox(15.0);
        profileInfo.setPadding(new Insets(20));
        
        profileInfo.getChildren().addAll(
            createProfileField("Name", doctorName != null ? "DR. " + doctorName : "DR. Doctor User"),
            createProfileField("Specialization", "General Medicine"),
            createProfileField("License Number", "MD-12345"),
            createProfileField("Experience", "10 years"),
            createProfileField("Education", "MD from University Medical School"),
            createProfileField("Phone", "+62 123 456 7890"),
            createProfileField("Email", "doctor@optikxyz.com"),
            createProfileField("Working Hours", "Monday - Friday: 09:00 - 17:00"),
            createProfileField("Languages", "Indonesian, English")
        );
        
        Button editProfileButton = new Button("Edit Profile");
        editProfileButton.getStyleClass().add("action-button");
        editProfileButton.setOnAction(e -> showAlert("Edit Profile", "Edit profile functionality will be implemented here."));
        
        profileContainer.getChildren().addAll(profileInfo, editProfileButton);
    }

    private VBox createPatientCard(String name, String email, String appointmentType) {
        VBox card = new VBox(8.0);
        card.getStyleClass().add("patient-card");
        
        Label nameLabel = new Label("Nama Pasien:");
        nameLabel.getStyleClass().add("field-label");
        Label nameValue = new Label(name);
        nameValue.getStyleClass().add("patient-name");
        
        Label emailLabel = new Label("Email Pasien:");
        emailLabel.getStyleClass().add("field-label");
        Label emailValue = new Label(email);
        emailValue.getStyleClass().add("patient-email");
        
        Label typeLabel = new Label("Jenis Appointment:");
        typeLabel.getStyleClass().add("field-label");
        Label typeValue = new Label(appointmentType);
        typeValue.getStyleClass().add("appointment-type");
        
        Button detailsButton = new Button("View Details");
        detailsButton.getStyleClass().add("details-button");
        detailsButton.setOnAction(e -> showAlert("Patient Details", "Viewing details for: " + name));
        
        card.getChildren().addAll(nameLabel, nameValue, emailLabel, emailValue, typeLabel, typeValue, detailsButton);
        return card;
    }

    private VBox createPatientListItem(String name, String email, String lastVisit, String status) {
        VBox item = new VBox(8.0);
        item.getStyleClass().add("patient-card");
        
        HBox headerBox = new HBox(10.0);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("patient-name");
        
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().add("appointment-type");
        
        headerBox.getChildren().addAll(nameLabel, statusLabel);
        
        Label emailLabel = new Label(email);
        emailLabel.getStyleClass().add("patient-email");
        
        Label visitLabel = new Label(lastVisit);
        visitLabel.getStyleClass().add("field-label");
        
        HBox buttonBox = new HBox(10.0);
        Button viewButton = new Button("View History");
        viewButton.getStyleClass().add("details-button");
        viewButton.setOnAction(e -> showAlert("Patient History", "Viewing history for: " + name));
        
        Button editButton = new Button("Edit Info");
        editButton.getStyleClass().add("view-button");
        editButton.setOnAction(e -> showAlert("Edit Patient", "Editing info for: " + name));
        
        buttonBox.getChildren().addAll(viewButton, editButton);
        
        item.getChildren().addAll(headerBox, emailLabel, visitLabel, buttonBox);
        return item;
    }

    private VBox createProfileField(String label, String value) {
        VBox field = new VBox(3.0);
        
        Label fieldLabel = new Label(label + ":");
        fieldLabel.getStyleClass().add("field-label");
        
        Label fieldValue = new Label(value);
        fieldValue.getStyleClass().add("patient-name");
        
        field.getChildren().addAll(fieldLabel, fieldValue);
        return field;
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refreshCalendarFromPatientBooking() {
        // This method can be called when a patient books an appointment
        // to refresh the doctor's calendar view
        
        // Reload real-time data
        loadRealData();
        
        // If schedule tab is currently visible, refresh the calendar
        if (scheduleTabContent.isVisible()) {
            loadWeeklyCalendar();
        }
        
        // If appointments tab is visible, refresh appointment cards
        if (appointmentsTabContent.isVisible()) {
            try {
                Database.Database db = new Database.Database();
                LocalDate today = LocalDate.now();
                loadTodayAppointments(db, today);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}