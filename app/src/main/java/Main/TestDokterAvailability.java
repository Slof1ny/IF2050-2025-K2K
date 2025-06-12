package Main;

import Controller.KetersediaanDokterController;
import Controller.JadwalPemeriksaanCtrl;
import Database.Database;
import Model.Dokter;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class TestDokterAvailability {
    public static void main(String[] args) {
        Database db = new Database();
        KetersediaanDokterController ketersediaanCtrl = new KetersediaanDokterController();
        JadwalPemeriksaanCtrl jadwalCtrl = new JadwalPemeriksaanCtrl();
        
        System.out.println("=== DEMO SISTEM KETERSEDIAAN DOKTER ===\n");
        
        // 1. Tambah dokter jika belum ada
        Dokter dokter1 = new Dokter("Dr. Ahmad", "Mata", 1, "password123");
        Dokter dokter2 = new Dokter("Dr. Sari", "Mata", 2, "password456");
        
        db.addDokter(dokter1);
        db.addDokter(dokter2);
        
        System.out.println("Dokter yang tersedia:");
        List<Dokter> allDokter = db.getAllDokter();
        for (Dokter d : allDokter) {
            System.out.println("- " + d.getNama() + " (" + d.getSpesialisasi() + ")");
        }
        System.out.println();
        
        // 2. Set dokter tidak tersedia pada tanggal dan jam tertentu
        Date tanggal = Date.valueOf("2025-01-15");
        Time jamMulai = Time.valueOf("09:00:00");
        Time jamSelesai = Time.valueOf("12:00:00");
        
        System.out.println("Setting Dr. Ahmad tidak tersedia pada 15 Januari 2025, 09:00-12:00");
        boolean success = ketersediaanCtrl.setTidakTersedia(1, tanggal, jamMulai, jamSelesai, "Cuti pribadi");
        System.out.println("Status: " + (success ? "Berhasil" : "Gagal"));
        System.out.println();
        
        // 3. Cek ketersediaan dokter
        System.out.println("Mengecek ketersediaan Dr. Ahmad pada 15 Januari 2025:");
        List<Map<String, Object>> ketersediaan = ketersediaanCtrl.getKetersediaanDokter(1, tanggal);
        for (Map<String, Object> k : ketersediaan) {
            System.out.println("- " + k.get("jam_mulai") + " - " + k.get("jam_selesai") + 
                             " : " + k.get("status") + " (" + k.get("keterangan") + ")");
        }
        System.out.println();
        
        // 4. Cek dokter yang tersedia pada waktu tertentu
        System.out.println("Dokter yang tersedia pada 15 Januari 2025, 10:00-11:00:");
        Time cekJamMulai = Time.valueOf("10:00:00");
        Time cekJamSelesai = Time.valueOf("11:00:00");
        
        List<Dokter> dokterTersedia = ketersediaanCtrl.getDokterTersedia(tanggal, cekJamMulai, cekJamSelesai);
        if (dokterTersedia.isEmpty()) {
            System.out.println("Tidak ada dokter yang tersedia pada waktu tersebut.");
        } else {
            for (Dokter d : dokterTersedia) {
                System.out.println("- " + d.getNama() + " (" + d.getSpesialisasi() + ")");
            }
        }
        System.out.println();
        
        // 5. Test booking jadwal dengan dokter yang tidak tersedia
        System.out.println("Mencoba booking jadwal dengan Dr. Ahmad pada waktu yang tidak tersedia:");
        java.util.Date tanggalWaktuBooking = new java.util.Date(tanggal.getTime() + cekJamMulai.getTime());
        boolean bookingSuccess = jadwalCtrl.bookJadwal(1, tanggalWaktuBooking, 1);
        System.out.println("Booking berhasil: " + bookingSuccess);
        System.out.println();
        
        // 6. Test booking dengan Dr. Sari yang tersedia
        System.out.println("Mencoba booking jadwal dengan Dr. Sari pada waktu yang sama:");
        boolean bookingSuccess2 = jadwalCtrl.bookJadwal(2, tanggalWaktuBooking, 1);
        System.out.println("Booking berhasil: " + bookingSuccess2);
        
        System.out.println("\n=== DEMO SELESAI ===");
        
        // Cleanup
        ketersediaanCtrl.closeConnection();
        db.closeConnection();
    }
}
