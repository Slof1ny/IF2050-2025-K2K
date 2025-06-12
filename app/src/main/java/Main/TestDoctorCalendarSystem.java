package Main;

import Controller.DoctorCalendarController;
import Controller.KetersediaanDokterController;
import Database.Database;
import Model.Dokter;
import Model.Pelanggan;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class TestDoctorCalendarSystem {
    public static void main(String[] args) {
        Database db = new Database();
        DoctorCalendarController calendarController = new DoctorCalendarController();
        KetersediaanDokterController ketersediaanController = new KetersediaanDokterController();
        
        System.out.println("=== DEMO SISTEM KALENDER DOKTER INDIVIDUAL ===\n");
        
        // 1. Setup data dokter dan pelanggan
        setupTestData(db);
        
        // 2. Demo: Tampilkan semua dokter
        System.out.println("1. DAFTAR DOKTER YANG TERSEDIA:");
        List<Dokter> allDoctors = calendarController.getAllDoctors();
        for (Dokter doctor : allDoctors) {
            System.out.println("   - ID: " + doctor.getId() + " | " + doctor.getNama() + 
                             " (" + doctor.getSpesialisasi() + ")");
        }
        System.out.println();
        
        // 3. Set ketersediaan dokter yang berbeda
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        Date sqlTomorrow = Date.valueOf(tomorrow);
        
        System.out.println("2. MENGATUR KETERSEDIAAN DOKTER:");
        
        // Dr. Ahmad tidak tersedia jam 10-12
        ketersediaanController.setTidakTersedia(1, sqlTomorrow, 
            Time.valueOf("10:00:00"), Time.valueOf("12:00:00"), "Cuti pribadi");
        System.out.println("   - Dr. Ahmad: Tidak tersedia 10:00-12:00 (Cuti pribadi)");
        
        // Dr. Sari tidak tersedia jam 14-16
        ketersediaanController.setTidakTersedia(2, sqlTomorrow, 
            Time.valueOf("14:00:00"), Time.valueOf("16:00:00"), "Seminar medis");
        System.out.println("   - Dr. Sari: Tidak tersedia 14:00-16:00 (Seminar medis)");
        System.out.println();
        
        // 4. Demo: Tampilkan kalender masing-masing dokter
        System.out.println("3. KALENDER DOKTER INDIVIDUAL untuk tanggal " + tomorrow + ":");
        
        for (Dokter doctor : allDoctors) {
            System.out.println("\n   📅 KALENDER " + doctor.getNama().toUpperCase() + ":");
            List<Map<String, Object>> slots = calendarController.getAvailableTimeSlots(doctor.getId(), tomorrow);
            
            for (Map<String, Object> slot : slots) {
                String time = (String) slot.get("time");
                boolean available = (Boolean) slot.get("available");
                String status = available ? "✅ TERSEDIA" : "❌ TIDAK TERSEDIA";
                
                if (!available) {
                    String reason = (String) slot.get("reason");
                    status += " (" + reason + ")";
                }
                
                System.out.println("      " + time + " - " + status);
            }
        }
        System.out.println();
        
        // 5. Demo: Booking appointment dengan dokter berbeda
        System.out.println("4. DEMO BOOKING APPOINTMENT:");
        
        // Book dengan Dr. Ahmad di jam 09:00 (tersedia)
        boolean booking1 = calendarController.bookAppointment(1, tomorrow, "09:00", 1);
        System.out.println("   - Booking Dr. Ahmad jam 09:00: " + (booking1 ? "BERHASIL" : "GAGAL"));
        
        // Coba book dengan Dr. Ahmad di jam 10:00 (tidak tersedia)
        boolean booking2 = calendarController.bookAppointment(1, tomorrow, "10:00", 1);
        System.out.println("   - Booking Dr. Ahmad jam 10:00: " + (booking2 ? "BERHASIL" : "GAGAL (tidak tersedia)"));
        
        // Book dengan Dr. Sari di jam 10:00 (tersedia)
        boolean booking3 = calendarController.bookAppointment(2, tomorrow, "10:00", 1);
        System.out.println("   - Booking Dr. Sari jam 10:00: " + (booking3 ? "BERHASIL" : "GAGAL"));
        
        // Coba book dengan Dr. Sari di jam 14:00 (tidak tersedia)
        boolean booking4 = calendarController.bookAppointment(2, tomorrow, "14:00", 1);
        System.out.println("   - Booking Dr. Sari jam 14:00: " + (booking4 ? "BERHASIL" : "GAGAL (tidak tersedia)"));
        System.out.println();
        
        // 6. Demo: Tampilkan kalender setelah booking
        System.out.println("5. KALENDER SETELAH BOOKING:");
        
        for (Dokter doctor : allDoctors) {
            System.out.println("\n   📅 KALENDER " + doctor.getNama().toUpperCase() + " (Updated):");
            List<Map<String, Object>> updatedSlots = calendarController.getAvailableTimeSlots(doctor.getId(), tomorrow);
            
            int availableCount = 0;
            for (Map<String, Object> slot : updatedSlots) {
                String time = (String) slot.get("time");
                boolean available = (Boolean) slot.get("available");
                
                if (available) {
                    availableCount++;
                }
                
                String status = available ? "✅ TERSEDIA" : "❌ TIDAK TERSEDIA";
                if (!available) {
                    String reason = (String) slot.get("reason");
                    status += " (" + reason + ")";
                }
                System.out.println("      " + time + " - " + status);
            }
            
            System.out.println("   👆 Total slot tersedia: " + availableCount + "/" + updatedSlots.size());
        }
        System.out.println();
        
        // 7. Demo: Tampilkan appointment pasien
        System.out.println("6. APPOINTMENT PASIEN:");
        List<Map<String, Object>> patientAppointments = calendarController.getPatientAppointments(1);
        
        if (patientAppointments.isEmpty()) {
            System.out.println("   Tidak ada appointment untuk pasien ID 1");
        } else {
            for (Map<String, Object> appointment : patientAppointments) {
                System.out.println("   - " + appointment.get("appointment_date") + 
                                 " " + appointment.get("appointment_time") + 
                                 " dengan " + appointment.get("doctor_name") + 
                                 " (" + appointment.get("doctor_specialization") + ")");
            }
        }
        
        System.out.println("\n=== DEMO SELESAI ===");
        System.out.println("💡 Setiap dokter sekarang memiliki kalender individu yang dapat diatur secara terpisah!");
        
        // Cleanup
        calendarController.closeConnection();
        ketersediaanController.closeConnection();
        db.closeConnection();
    }
    
    private static void setupTestData(Database db) {
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
    }
}
