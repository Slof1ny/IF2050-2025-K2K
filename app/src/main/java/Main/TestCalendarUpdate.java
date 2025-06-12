package Main;

import Controller.CalendarRefreshController;
import Controller.KetersediaanDokterController;
import Database.Database;
import Model.Dokter;
import Model.Pelanggan;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Map;

public class TestCalendarUpdate {
    
    static class TestCalendarListener implements CalendarRefreshController.CalendarUpdateListener {
        @Override
        public void onCalendarUpdated(Map<String, Object> calendarData) {
            System.out.println("🔄 CALENDAR UPDATED!");
            if (calendarData.containsKey("reset")) {
                System.out.println("   " + calendarData.get("message"));
                return;
            }
            
            System.out.println("   Doctor: " + calendarData.get("doctorName"));
            System.out.println("   Available slots: " + calendarData.get("availableSlots") + "/" + calendarData.get("totalSlots"));
            System.out.println("   Doctor changed: " + calendarData.get("doctorChanged"));
            System.out.println("   Date changed: " + calendarData.get("dateChanged"));
        }

        @Override
        public void onDoctorChanged(int newDoctorId, String doctorName) {
            System.out.println("👨‍⚕️ DOCTOR CHANGED to: " + doctorName + " (ID: " + newDoctorId + ")");
        }

        @Override
        public void onDateChanged(LocalDate newDate) {
            System.out.println("📅 DATE CHANGED to: " + newDate);
        }
    }

    public static void main(String[] args) {
        // Setup test data
        setupTestData();
        
        System.out.println("=== TESTING CALENDAR UPDATE SYSTEM ===\n");
        
        CalendarRefreshController refreshController = new CalendarRefreshController();
        TestCalendarListener listener = new TestCalendarListener();
        
        // Register listener
        refreshController.addCalendarUpdateListener(listener);
        
        LocalDate testDate = LocalDate.now().plusDays(1);
        
        // Test 1: Select first doctor
        System.out.println("TEST 1: Selecting Dr. Ahmad (ID: 1)");
        Map<String, Object> calendar1 = refreshController.updateCalendarForDoctor(1, testDate);
        System.out.println();
        
        // Test 2: Switch to second doctor
        System.out.println("TEST 2: Switching to Dr. Sari (ID: 2)");
        Map<String, Object> calendar2 = refreshController.updateCalendarForDoctor(2, testDate);
        System.out.println();
        
        // Test 3: Switch to third doctor
        System.out.println("TEST 3: Switching to Dr. Budi (ID: 3)");
        Map<String, Object> calendar3 = refreshController.updateCalendarForDoctor(3, testDate);
        System.out.println();
        
        // Test 4: Switch back to first doctor (should show doctor changed)
        System.out.println("TEST 4: Switching back to Dr. Ahmad (ID: 1)");
        Map<String, Object> calendar4 = refreshController.updateCalendarForDoctor(1, testDate);
        System.out.println();
        
        // Test 5: Change date with same doctor
        System.out.println("TEST 5: Changing date with same doctor");
        LocalDate newDate = testDate.plusDays(1);
        Map<String, Object> calendar5 = refreshController.updateCalendarForDoctor(1, newDate);
        System.out.println();
        
        // Test 6: Force refresh
        System.out.println("TEST 6: Force refresh current calendar");
        Map<String, Object> refreshed = refreshController.forceRefreshCurrentCalendar();
        System.out.println("Force refresh result: " + (refreshed != null ? "SUCCESS" : "FAILED"));
        System.out.println();
        
        // Verify calendars are different
        System.out.println("=== VERIFICATION: Calendars are doctor-specific ===");
        verifyDoctorSpecificCalendars(refreshController, testDate);
        
        System.out.println("\n=== TEST COMPLETED ===");
        refreshController.closeConnection();
    }
    
    private static void verifyDoctorSpecificCalendars(CalendarRefreshController refreshController, LocalDate date) {
        System.out.println("Comparing calendars for all doctors on " + date + ":");
        
        for (int doctorId = 1; doctorId <= 3; doctorId++) {
            Map<String, Object> calendar = refreshController.updateCalendarForDoctor(doctorId, date);
            if (calendar != null) {
                System.out.println("Doctor " + calendar.get("doctorName") + ": " + 
                                 calendar.get("availableSlots") + "/" + calendar.get("totalSlots") + " available");
            }
        }
    }
    
    private static void setupTestData() {
        Database db = new Database();
        KetersediaanDokterController ketersediaanController = new KetersediaanDokterController();
        
        // Add test doctors
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
            Time.valueOf("10:00:00"), Time.valueOf("12:00:00"), "Dr. Ahmad Meeting");
        
        // Dr. Sari not available 14-16  
        ketersediaanController.setTidakTersedia(2, sqlTomorrow, 
            Time.valueOf("14:00:00"), Time.valueOf("16:00:00"), "Dr. Sari Surgery");
        
        // Dr. Budi not available 09-11
        ketersediaanController.setTidakTersedia(3, sqlTomorrow, 
            Time.valueOf("09:00:00"), Time.valueOf("11:00:00"), "Dr. Budi Training");
        
        ketersediaanController.closeConnection();
        db.closeConnection();
        
        System.out.println("Test data setup completed.");
        System.out.println("- Dr. Ahmad: Not available 10:00-12:00");
        System.out.println("- Dr. Sari: Not available 14:00-16:00");
        System.out.println("- Dr. Budi: Not available 09:00-11:00");
        System.out.println();
    }
}
