// package Database;

// import Model.*;
// import java.util.Date;
// import java.util.List;

// public class DatabaseTest {
//     public static void main(String[] args) {
//         System.out.println("=== Database Connection Test ===");
        
//         // Create database instance
//         Database db = new Database();
        
//         if (db.isConnected()) {
//             System.out.println("✓ Database connection successful!");
            
//             // Test getting counts
//             System.out.println("\n=== Current Database Statistics ===");
//             System.out.println("Total Pelanggan: " + db.getTotalPelanggan());
//             System.out.println("Total Resep: " + db.getTotalResep());
//             System.out.println("Total Dokter: " + db.getTotalDokter());
//             System.out.println("Total Jadwal Pemeriksaan: " + db.getTotalJadwalPemeriksaan());
            
//             // Test Admin functionality
//             System.out.println("\n=== Testing Admin Operations ===");
//             List<Admin> adminList = db.getAllAdmin();
//             System.out.println("Total Admin in database: " + adminList.size());
            
//             for (Admin admin : adminList) {
//                 System.out.println("Admin: " + admin.getUsername() + " (ID: " + admin.getID() + ")");
//             }
            
//             // Test Laporan functionality
//             System.out.println("\n=== Testing Laporan Operations ===");
//             List<Laporan> laporanList = db.getAllLaporan();
//             System.out.println("Total Laporan in database: " + laporanList.size());
            
//             for (Laporan laporan : laporanList) {
//                 System.out.println("Laporan: " + laporan.getTipeLaporan() + " (ID: " + laporan.getIdLaporan() + ")");
//             }
//               // Test getting Laporan with Admin info
//             System.out.println("\n=== Laporan with Admin Info ===");
//             List<String> laporanInfo = db.getLaporanWithAdminInfo();
//             for (String info : laporanInfo) {
//                 System.out.println(info);
//             }
            
//             // Test Notifikasi functionality
//             System.out.println("\n=== Testing Notifikasi Operations ===");
//             System.out.println("Total Notifikasi in database: " + db.getTotalNotifikasi());
            
//             List<Notifikasi> notifikasiList = db.getAllNotifikasi();
//             if (!notifikasiList.isEmpty()) {
//                 System.out.println("Recent notifications:");                for (int i = 0; i < Math.min(3, notifikasiList.size()); i++) {
//                     Notifikasi notif = notifikasiList.get(i);
//                     System.out.println("  - ID: " + notif.getIdNotif() + 
//                                      " | Message: " + notif.getIsiPesan().substring(0, Math.min(50, notif.getIsiPesan().length())) + "..." +
//                                      " | Date: " + notif.getFormattedTanggal() +
//                                      " | Pelanggan ID: " + notif.getIdPelanggan());
//                 }
//             } else {
//                 System.out.println("No notifications in database");
//             }
            
//             System.out.println("\n✓ All database operations completed successfully!");
            
//             // Close connection
//             db.closeConnection();
//             System.out.println("✓ Database connection closed.");
            
//         } else {
//             System.out.println("✗ Failed to connect to database!");
//         }
//     }
// }
