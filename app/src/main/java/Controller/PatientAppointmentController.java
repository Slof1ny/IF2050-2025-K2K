package Controller;

import Model.Dokter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class PatientAppointmentController {
    private DoctorCalendarController calendarController;
    private int currentSelectedDoctorId = -1;
    private LocalDate currentSelectedDate = LocalDate.now();

    public PatientAppointmentController() {
        this.calendarController = new DoctorCalendarController();
    }

    // Method yang dipanggil ketika pasien memilih dokter
    public Map<String, Object> onDoctorSelected(int doctorId) {
        System.out.println("Patient selected doctor ID: " + doctorId);
        
        this.currentSelectedDoctorId = doctorId;
        
        // Return doctor-specific calendar data
        return calendarController.getPatientCalendarForDoctor(doctorId, currentSelectedDate);
    }

    // Method yang dipanggil ketika pasien memilih tanggal
    public Map<String, Object> onDateSelected(LocalDate selectedDate) {
        System.out.println("Patient selected date: " + selectedDate);
        
        this.currentSelectedDate = selectedDate;
        
        if (currentSelectedDoctorId > 0) {
            return calendarController.getPatientCalendarForDoctor(currentSelectedDoctorId, selectedDate);
        }
        
        return null;
    }

    // Method untuk mendapatkan daftar semua dokter
    public List<Dokter> getAvailableDoctors() {
        return calendarController.getAllDoctors();
    }

    // Method untuk booking appointment
    public Map<String, Object> bookAppointment(int doctorId, LocalDate date, String time, int patientId) {
        // Validate booking first
        Map<String, Object> validation = calendarController.validateBookingForDoctor(doctorId, date, time, patientId);
        
        if (!(Boolean) validation.get("valid")) {
            return validation;
        }

        // Proceed with booking
        boolean success = calendarController.bookAppointment(doctorId, date, time, patientId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        
        if (success) {
            result.put("message", "Appointment successfully booked with " + validation.get("doctorName"));
            // Return updated calendar data
            result.put("updatedCalendar", calendarController.getPatientCalendarForDoctor(doctorId, date));
        } else {
            result.put("message", "Failed to book appointment. Please try again.");
        }
        
        return result;
    }

    // Method untuk mendapatkan calendar data saat ini
    public Map<String, Object> getCurrentCalendarData() {
        if (currentSelectedDoctorId > 0) {
            return calendarController.getPatientCalendarForDoctor(currentSelectedDoctorId, currentSelectedDate);
        }
        return null;
    }

    // Method untuk reset selection
    public void resetSelection() {
        this.currentSelectedDoctorId = -1;
        this.currentSelectedDate = LocalDate.now();
    }

    // Method untuk mendapatkan doctor yang sedang dipilih
    public int getCurrentSelectedDoctorId() {
        return currentSelectedDoctorId;
    }

    // Method untuk mendapatkan tanggal yang sedang dipilih
    public LocalDate getCurrentSelectedDate() {
        return currentSelectedDate;
    }

    // Method untuk mendapatkan perbandingan dokter
    public List<Map<String, Object>> getDoctorComparison() {
        return calendarController.compareDoctorsAvailability(currentSelectedDate);
    }

    public void closeConnection() {
        if (calendarController != null) {
            calendarController.closeConnection();
        }
    }
}
