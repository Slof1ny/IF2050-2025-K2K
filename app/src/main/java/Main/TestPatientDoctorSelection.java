package Main;

import Controller.PatientAppointmentController;
import Controller.KetersediaanDokterController;
import Database.Database;
import Model.Dokter;
import Model.Pelanggan;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TestPatientDoctorSelection {
    public static void main(String[] args) {
        // Setup test data
        setupTestData();
        
        PatientAppointmentController patientController = new PatientAppointmentController();
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== DEMO: PATIENT DOCTOR SELECTION & CALENDAR ===\n");
        
        // Show available doctors
        System.out.println("Available Doctors:");
        List<Dokter> doctors = patientController.getAvailableDoctors();
        for (int i = 0; i < doctors.size(); i++) {
            Dokter doctor = doctors.get(i);
            System.out.println((i + 1) + ". " + doctor.getNama() + " (" + doctor.getSpesialisasi() + ")");
        }
        
        // Simulate patient selecting different doctors
        System.out.println("\n--- DEMO: Selecting Different Doctors ---");
        
        for (Dokter doctor : doctors) {
            System.out.println("\n🔄 Patient selects: " + doctor.getNama());
            
            Map<String, Object> calendarData = patientController.onDoctorSelected(doctor.getId());
            
            if (calendarData != null) {
                System.out.println("📅 Calendar for " + calendarData.get("doctorName") + ":");
                System.out.println("   Specialization: " + calendarData.get("doctorSpecialization"));
                System.out.println("   Date: " + calendarData.get("selectedDate"));
                System.out.println("   Available slots: " + calendarData.get("availableSlots") + "/" + calendarData.get("totalSlots"));
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> timeSlots = (List<Map<String, Object>>) calendarData.get("timeSlots");
                
                System.out.println("   Time slots:");
                for (Map<String, Object> slot : timeSlots) {
                    String status = (Boolean) slot.get("available") ? "✅ Available" : "❌ Not Available";
                    if (!(Boolean) slot.get("available")) {
                        status += " (" + slot.get("reason") + ")";
                    }
                    System.out.println("     " + slot.get("time") + " - " + status);
                }
            }
            
            // Small delay for demo effect
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Demo booking
        System.out.println("\n--- DEMO: Booking Appointment ---");
        
        // Select first doctor for booking demo
        Dokter firstDoctor = doctors.get(0);
        System.out.println("Selecting " + firstDoctor.getNama() + " for booking...");
        
        Map<String, Object> calendarForBooking = patientController.onDoctorSelected(firstDoctor.getId());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> timeSlots = (List<Map<String, Object>>) calendarForBooking.get("timeSlots");
        
        // Find first available slot
        String availableTime = null;
        for (Map<String, Object> slot : timeSlots) {
            if ((Boolean) slot.get("available")) {
                availableTime = (String) slot.get("time");
                break;
            }
        }
        
        if (availableTime != null) {
            System.out.println("Booking appointment at " + availableTime + "...");
            
            Map<String, Object> bookingResult = patientController.bookAppointment(
                firstDoctor.getId(), 
                LocalDate.now().plusDays(1), 
                availableTime, 
                1
            );
            
            System.out.println("Booking result: " + bookingResult.get("message"));
            
            if ((Boolean) bookingResult.get("success")) {
                System.out.println("✅ Appointment successfully booked!");
                
                // Show updated calendar
                @SuppressWarnings("unchecked")
                Map<String, Object> updatedCalendar = (Map<String, Object>) bookingResult.get("updatedCalendar");
                System.out.println("Updated available slots: " + updatedCalendar.get("availableSlots") + "/" + updatedCalendar.get("totalSlots"));
            }
        } else {
            System.out.println("❌ No available slots found for booking demo");
        }
        
        System.out.println("\n=== DEMO COMPLETED ===");
        System.out.println("💡 Each doctor now shows their own unique calendar!");
        System.out.println("💡 Calendar updates dynamically when patient selects different doctors!");
        
        scanner.close();
        patientController.closeConnection();
    }
    
    private static void setupTestData() {
        Database db = new Database();
        KetersediaanDokterController ketersediaanController = new KetersediaanDokterController();
        
        // Add test doctors with different availability
        Dokter dokter1 = new Dokter("Dr. Ahmad", "Mata", 1, "password123");
        Dokter dokter2 = new Dokter("Dr. Sari", "Mata", 2, "password456");
        Dokter dokter3 = new Dokter("Dr. Budi", "Mata", 3, "password789");
        
        db.addDokter(dokter1);
        db.addDokter(dokter2);
        db.addDokter(dokter3);
        
        // Add test patient
        Pelanggan pelanggan = new Pelanggan(1, "John Doe", "john@email.com", "081234567890", "password");
        db.addPelanggan(pelanggan);
        
        // Set different availability for each doctor
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date sqlTomorrow = Date.valueOf(tomorrow);
        
        // Dr. Ahmad not available 10-12
        ketersediaanController.setTidakTersedia(1, sqlTomorrow, 
            Time.valueOf("10:00:00"), Time.valueOf("12:00:00"), "Meeting");
        
        // Dr. Sari not available 14-16  
        ketersediaanController.setTidakTersedia(2, sqlTomorrow, 
            Time.valueOf("14:00:00"), Time.valueOf("16:00:00"), "Surgery");
        
        // Dr. Budi not available 09-11
        ketersediaanController.setTidakTersedia(3, sqlTomorrow, 
            Time.valueOf("09:00:00"), Time.valueOf("11:00:00"), "Training");
        
        ketersediaanController.closeConnection();
        db.closeConnection();
    }
}
