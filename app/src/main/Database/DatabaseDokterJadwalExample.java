// package main.Database;

// import main.Model.Pelanggan;
// import main.Model.Dokter;
// import main.Model.JadwalPemeriksaan;
// import java.util.Date;
// import java.util.Calendar;
// import java.util.List;

// public class DatabaseDokterJadwalExample {
//     public static void main(String[] args) {
//         // Membuat instance database SQLite
//         Database db = new Database();
        
//         System.out.println("=== SQLite Database Dokter dan Jadwal Pemeriksaan Example ===\n");
        
//         // 1. Tambah dokter terlebih dahulu
//         System.out.println("1. Menambah dokter:");
//         Dokter dokter1 = new Dokter("Dr. Andi Wijaya", "Kardiologi", 0);
//         Dokter dokter2 = new Dokter("Dr. Sari Indah", "Dermatologi", 0);
//         Dokter dokter3 = new Dokter("Dr. Budi Santoso", "Orthopedi", 0);
        
//         if (db.addDokter(dokter1)) {
//             System.out.println("✓ Dokter " + dokter1.getNama() + " (" + dokter1.getSpesialisasi() + ") berhasil ditambahkan dengan ID: " + dokter1.getId());
//         }
        
//         if (db.addDokter(dokter2)) {
//             System.out.println("✓ Dokter " + dokter2.getNama() + " (" + dokter2.getSpesialisasi() + ") berhasil ditambahkan dengan ID: " + dokter2.getId());
//         }
        
//         if (db.addDokter(dokter3)) {
//             System.out.println("✓ Dokter " + dokter3.getNama() + " (" + dokter3.getSpesialisasi() + ") berhasil ditambahkan dengan ID: " + dokter3.getId());
//         }
        
//         // 2. Tambah pelanggan sebagai pasien (jika belum ada)
//         System.out.println("\n2. Menambah pasien:");
//         Pelanggan pasien1 = new Pelanggan(0, "Ahmad Rizki", "ahmad.rizki@email.com", "081234567890");
//         Pelanggan pasien2 = new Pelanggan(0, "Siti Nurhaliza", "siti.nur@email.com", "087654321098");
        
//         if (db.addPelanggan(pasien1)) {
//             System.out.println("✓ Pasien " + pasien1.getNama() + " berhasil ditambahkan dengan ID: " + pasien1.getId());
//         }
        
//         if (db.addPelanggan(pasien2)) {
//             System.out.println("✓ Pasien " + pasien2.getNama() + " berhasil ditambahkan dengan ID: " + pasien2.getId());
//         }
        
//         // 3. Tambah jadwal pemeriksaan dengan datetime yang berbeda
//         System.out.println("\n3. Menambah jadwal pemeriksaan:");
        
//         // Membuat tanggal dan waktu yang berbeda
//         Calendar cal = Calendar.getInstance();
        
//         // Jadwal 1: Hari ini jam 09:00
//         cal.set(Calendar.HOUR_OF_DAY, 9);
//         cal.set(Calendar.MINUTE, 0);
//         cal.set(Calendar.SECOND, 0);
//         cal.set(Calendar.MILLISECOND, 0);
//         Date tanggalWaktu1 = cal.getTime();
        
//         // Jadwal 2: Besok jam 10:30
//         cal.add(Calendar.DAY_OF_MONTH, 1);
//         cal.set(Calendar.HOUR_OF_DAY, 10);
//         cal.set(Calendar.MINUTE, 30);
//         Date tanggalWaktu2 = cal.getTime();
        
//         // Jadwal 3: Lusa jam 14:00
//         cal.add(Calendar.DAY_OF_MONTH, 1);
//         cal.set(Calendar.HOUR_OF_DAY, 14);
//         cal.set(Calendar.MINUTE, 0);
//         Date tanggalWaktu3 = cal.getTime();
        
//         JadwalPemeriksaan jadwal1 = new JadwalPemeriksaan(0, tanggalWaktu1, pasien1.getId(), dokter1.getId());
//         JadwalPemeriksaan jadwal2 = new JadwalPemeriksaan(0, tanggalWaktu2, pasien2.getId(), dokter2.getId());
//         JadwalPemeriksaan jadwal3 = new JadwalPemeriksaan(0, tanggalWaktu3, pasien1.getId(), dokter3.getId());
        
//         if (db.addJadwalPemeriksaan(jadwal1)) {
//             System.out.println("✓ Jadwal pemeriksaan berhasil ditambahkan dengan ID: " + jadwal1.getIdJadwal());
//         }
        
//         if (db.addJadwalPemeriksaan(jadwal2)) {
//             System.out.println("✓ Jadwal pemeriksaan berhasil ditambahkan dengan ID: " + jadwal2.getIdJadwal());
//         }
        
//         if (db.addJadwalPemeriksaan(jadwal3)) {
//             System.out.println("✓ Jadwal pemeriksaan berhasil ditambahkan dengan ID: " + jadwal3.getIdJadwal());
//         }
        
//         // 4. Ambil semua dokter
//         System.out.println("\n4. Daftar semua dokter:");
//         List<Dokter> allDokter = db.getAllDokter();
//         for (Dokter d : allDokter) {
//             System.out.println("- ID: " + d.getId() + 
//                              ", Nama: " + d.getNama() +
//                              ", Spesialisasi: " + d.getSpesialisasi());
//         }
//           // 5. Ambil semua jadwal pemeriksaan
//         System.out.println("\n5. Daftar semua jadwal pemeriksaan:");
//         List<JadwalPemeriksaan> allJadwal = db.getAllJadwalPemeriksaan();
//         for (JadwalPemeriksaan j : allJadwal) {
//             System.out.println("- ID Jadwal: " + j.getIdJadwal() + 
//                              ", Tanggal & Waktu: " + j.getTanggalWaktu() +
//                              ", ID Pasien: " + j.getIdPasien() +
//                              ", ID Dokter: " + j.getIdDokter());
//         }
        
//         // 6. Jadwal pemeriksaan dengan informasi lengkap (JOIN)
//         System.out.println("\n6. Jadwal pemeriksaan dengan informasi pasien dan dokter:");
//         List<String> jadwalWithInfo = db.getJadwalPemeriksaanWithInfo();
//         for (String info : jadwalWithInfo) {
//             System.out.println("- " + info);
//         }
        
//         // 7. Cari dokter berdasarkan spesialisasi
//         System.out.println("\n7. Mencari dokter dengan spesialisasi 'Kardiologi':");
//         List<Dokter> kardiologiDokter = db.searchDokterBySpesialisasi("Kardiologi");
//         for (Dokter d : kardiologiDokter) {
//             System.out.println("- Ditemukan: " + d.getNama() + " (" + d.getSpesialisasi() + ")");
//         }
//           // 8. Jadwal pemeriksaan untuk pasien tertentu
//         System.out.println("\n8. Jadwal pemeriksaan untuk pasien ID " + pasien1.getId() + ":");
//         List<JadwalPemeriksaan> jadwalPasien = db.getJadwalPemeriksaanByPasienId(pasien1.getId());
//         for (JadwalPemeriksaan j : jadwalPasien) {
//             System.out.println("- Jadwal ID: " + j.getIdJadwal() + 
//                              ", Tanggal & Waktu: " + j.getTanggalWaktu() +
//                              ", Dokter ID: " + j.getIdDokter());
//         }
        
//         // 9. Jadwal pemeriksaan untuk dokter tertentu
//         System.out.println("\n9. Jadwal pemeriksaan untuk dokter ID " + dokter1.getId() + ":");
//         List<JadwalPemeriksaan> jadwalDokter = db.getJadwalPemeriksaanByDokterId(dokter1.getId());
//         for (JadwalPemeriksaan j : jadwalDokter) {
//             System.out.println("- Jadwal ID: " + j.getIdJadwal() + 
//                              ", Tanggal & Waktu: " + j.getTanggalWaktu() +
//                              ", Pasien ID: " + j.getIdPasien());
//         }
        
//         // 10. Update dokter
//         System.out.println("\n10. Update dokter:");
//         Dokter toUpdateDokter = db.getDokterById(dokter2.getId());
//         if (toUpdateDokter != null) {
//             toUpdateDokter.setSpesialisasi("Dermatologi & Kosmetik");
//             if (db.updateDokter(toUpdateDokter)) {
//                 System.out.println("✓ Dokter berhasil diupdate: " + toUpdateDokter.getNama() + " - " + toUpdateDokter.getSpesialisasi());
//             }
//         }
//           // 11. Update jadwal pemeriksaan
//         System.out.println("\n11. Update jadwal pemeriksaan:");
//         JadwalPemeriksaan toUpdateJadwal = db.getJadwalPemeriksaanById(jadwal1.getIdJadwal());
//         if (toUpdateJadwal != null) {
//             // Update tanggal dan waktu ke jam 11:00 hari yang sama
//             Calendar updateCal = Calendar.getInstance();
//             updateCal.setTime(toUpdateJadwal.getTanggalWaktu());
//             updateCal.set(Calendar.HOUR_OF_DAY, 11);
//             updateCal.set(Calendar.MINUTE, 0);
//             toUpdateJadwal.setTanggalWaktu(updateCal.getTime());
            
//             if (db.updateJadwalPemeriksaan(toUpdateJadwal)) {
//                 System.out.println("✓ Jadwal pemeriksaan berhasil diupdate ke: " + toUpdateJadwal.getTanggalWaktu());
//             }
//         }
        
//         // 12. Statistik database
//         System.out.println("\n12. Statistik database:");
//         System.out.println("Total pelanggan: " + db.getTotalPelanggan());
//         System.out.println("Total dokter: " + db.getTotalDokter());
//         System.out.println("Total jadwal pemeriksaan: " + db.getTotalJadwalPemeriksaan());
        
//         // 13. Test Foreign Key Constraint
//         System.out.println("\n13. Test Foreign Key Constraint:");
//         System.out.println("Mencoba hapus dokter yang memiliki jadwal pemeriksaan...");
//         if (db.deleteDokter(dokter1.getId())) {
//             System.out.println("✓ Dokter dan semua jadwal pemeriksaannya berhasil dihapus (CASCADE)");
//             System.out.println("Jadwal pemeriksaan yang tersisa: " + db.getTotalJadwalPemeriksaan());
//         } else {
//             System.out.println("✗ Gagal menghapus dokter");
//         }
        
//         // Menutup koneksi database
//         System.out.println("\n=== Menutup koneksi database ===");
//         db.closeConnection();
//     }
// }
