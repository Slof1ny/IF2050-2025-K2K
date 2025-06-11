package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class DoctorDashboardController implements Initializable {

    // Header components
    @FXML private Label welcomeBanner;
    @FXML private Button logoutButton;
    
    // Navigation tabs
    @FXML private Button appointmentsTab;
    @FXML private Button patientsTab;
    @FXML private Button scheduleTab;
    @FXML private Button profileTab;
    
    // Date picker and view button
    @FXML private DatePicker appointmentDatePicker;
    @FXML private Button viewAppointmentsButton;
    
    // Statistics labels
    @FXML private Label todayPatientsCount;
    @FXML private Label pendingCount;
    @FXML private Label completedCount;
    @FXML private Label totalPatientsCount;
    
    // Patient information labels
    @FXML private Label patientName1;
    @FXML private Label patientEmail1;
    @FXML private Label appointmentType1;
    @FXML private Button detailsButton1;
    
    @FXML private Label patientName2;
    @FXML private Label patientEmail2;
    @FXML private Label appointmentType2;
    @FXML private Button detailsButton2;
    
    // Quick action buttons
    @FXML private Button addPatientButton;
    @FXML private Button viewReportsButton;
    @FXML private Button prescriptionsButton;
    @FXML private Button settingsButton;
    
    private String doctorName;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set default date to today
        appointmentDatePicker.setValue(LocalDate.now());
        
        // Initialize with real data
        loadRealData();
        
        // Set active tab styling
        setActiveTab(appointmentsTab);
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
            return db.getTodayPatientsCount(doctorName, date);
        }
        return 0;
    }
    
    private int calculatePendingAppointments(Database.Database db, LocalDate date) {
        if (doctorName != null) {
            return db.getPendingAppointmentsCount(doctorName, date);
        }
        return 0;
    }
    
    private int calculateCompletedToday(Database.Database db, LocalDate date) {
        if (doctorName != null) {
            return db.getCompletedAppointmentsCount(doctorName, date);
        }
        return 0;
    }
    
    private int calculateTotalPatients(Database.Database db) {
        if (doctorName != null) {
            return db.getTotalPatientsCount(doctorName);
        }
        return 0;
    }
    
    private void loadTodayAppointments(Database.Database db, LocalDate date) {
        try {
            if (doctorName != null) {
                List<Map<String, Object>> appointments = db.getTodayAppointments(doctorName, date);
                
                // Clear existing data
                clearAppointmentData();
                
                // Load first appointment
                if (appointments.size() > 0) {
                    Map<String, Object> apt1 = appointments.get(0);
                    patientName1.setText((String) apt1.get("patient_name"));
                    patientEmail1.setText((String) apt1.get("patient_email"));
                    appointmentType1.setText((String) apt1.get("appointment_type"));
                }
                
                // Load second appointment
                if (appointments.size() > 1) {
                    Map<String, Object> apt2 = appointments.get(1);
                    patientName2.setText((String) apt2.get("patient_name"));
                    patientEmail2.setText((String) apt2.get("patient_email"));
                    appointmentType2.setText((String) apt2.get("appointment_type"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadSamplePatientData();
        }
    }
    
    private void loadSamplePatientData() {
        // This method should be replaced with actual database queries
        patientName1.setText("John Doe");
        patientEmail1.setText("john.doe@email.com");
        appointmentType1.setText("Regular Checkup");
        
        patientName2.setText("Jane Smith");
        patientEmail2.setText("jane.smith@email.com");
        appointmentType2.setText("Follow-up Visit");
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
        // Additional logic for appointments view
    }
    
    @FXML
    private void handlePatientsTab(ActionEvent event) {
        setActiveTab(patientsTab);
        // Additional logic for patients view
    }
    
    @FXML
    private void handleScheduleTab(ActionEvent event) {
        setActiveTab(scheduleTab);
        // Additional logic for schedule view
    }
    
    @FXML
    private void handleProfileTab(ActionEvent event) {
        setActiveTab(profileTab);
        // Additional logic for profile view
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
                loadSamplePatientData();
            } else {
                clearAppointmentData();
            }
        }
    }
    
    private void loadAppointmentsForDate(Database.Database db, LocalDate date) {
        try {
            // Query database for appointments on specific date
            // Example: SELECT * FROM appointments WHERE doctor_name = ? AND DATE(appointment_date) = ?
            
            if (date.equals(LocalDate.now())) {
                loadSamplePatientData();
            } else {
                clearAppointmentData();
            }
            
        } catch (Exception e) {
            clearAppointmentData();
        }
    }
    
    private void clearAppointmentData() {
        patientName1.setText("No appointments");
        patientEmail1.setText("for selected date");
        appointmentType1.setText("");
        
        patientName2.setText("");
        patientEmail2.setText("");
        appointmentType2.setText("");
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
        String patientInfo = "";
        
        if (clickedButton == detailsButton1) {
            patientInfo = patientName1.getText();
        } else if (clickedButton == detailsButton2) {
            patientInfo = patientName2.getText();
        }
        
        showAlert("Patient Details", "Viewing details for: " + patientInfo);
    }
    
    @FXML
    private void handleAddPatient(ActionEvent event) {
        showAlert("Add Patient", "Add Patient functionality will be implemented here.");
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}