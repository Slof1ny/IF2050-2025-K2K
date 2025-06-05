// package main.Database;

// import main.Model.Admin;
// import main.Model.Laporan;
// import java.util.List;
// import java.util.Date;

// public class DatabaseAdminLaporanExample {
    
//     public static void main(String[] args) {
//         System.out.println("=== Testing Admin and Laporan Tables ===\n");
        
//         Database db = new Database();
        
//         // Test 1: Add Admin
//         System.out.println("1. Adding Admin users...");
//         Admin admin1 = new Admin(0, "admin1", "password123");
//         Admin admin2 = new Admin(0, "superadmin", "securepass456");
        
//         boolean admin1Added = db.addAdmin(admin1);
//         boolean admin2Added = db.addAdmin(admin2);
        
//         System.out.println("   Admin1 added: " + admin1Added + " with ID: " + admin1.getID());
//         System.out.println("   Admin2 added: " + admin2Added + " with ID: " + admin2.getID());        
//         // Test 2: Login Admin
//         System.out.println("\n2. Testing Admin Login...");
//         Admin loginTest = db.loginAdmin("admin1", "password123");
//         if (loginTest != null) {
//             System.out.println("   ✓ Login successful for: " + loginTest.getUsername());
//         } else {
//             System.out.println("   ✗ Login failed");
//         }
        
//         Admin failLogin = db.loginAdmin("admin1", "wrongpassword");
//         if (failLogin == null) {
//             System.out.println("   ✓ Correctly rejected wrong password");
//         }
        
//         // Test 3: Get All Admins
//         System.out.println("\n3. Retrieving all admins...");
//         List<Admin> allAdmins = db.getAllAdmin();
//         for (Admin admin : allAdmins) {
//             System.out.println("   Admin ID: " + admin.getID() + ", Username: " + admin.getUsername());
//         }
        
//         // Test 4: Add Laporan (using correct constructor)
//         System.out.println("\n4. Adding Laporan entries...");
//         Date now = new Date();
//         Date periodStart = new Date(now.getTime() - (7 * 24 * 60 * 60 * 1000)); // 7 days ago
//         Date periodEnd = new Date();
        
//         Laporan laporan1 = new Laporan(0, "Laporan Harian Apotek", periodStart, periodEnd, now, 
//                                       "Penjualan hari ini mencapai target dengan baik.", admin1.getID());
//         Laporan laporan2 = new Laporan(0, "Laporan Stok Obat", periodStart, periodEnd, now,
//                                       "Beberapa obat perlu direstok minggu depan.", admin1.getID());
//         Laporan laporan3 = new Laporan(0, "Laporan Sistem", periodStart, periodEnd, now,
//                                       "Sistem berjalan normal tanpa gangguan.", admin2.getID());
        
//         boolean laporan1Added = db.addLaporan(laporan1);
//         boolean laporan2Added = db.addLaporan(laporan2);
//         boolean laporan3Added = db.addLaporan(laporan3);
        
//         System.out.println("   Laporan 1 added: " + laporan1Added + " with ID: " + laporan1.getIdLaporan());
//         System.out.println("   Laporan 2 added: " + laporan2Added + " with ID: " + laporan2.getIdLaporan());
//         System.out.println("   Laporan 3 added: " + laporan3Added + " with ID: " + laporan3.getIdLaporan());        
//         // Test 5: Get All Laporan
//         System.out.println("\n5. Retrieving all laporan...");
//         List<Laporan> allLaporan = db.getAllLaporan();
//         for (Laporan laporan : allLaporan) {
//             System.out.println("   Laporan ID: " + laporan.getIdLaporan() + ", Tipe: " + laporan.getTipeLaporan() + 
//                              ", Created by Admin ID: " + laporan.getDibuatOleh());
//         }
        
//         // Test 6: Get Laporan by Admin ID
//         System.out.println("\n6. Getting laporan by admin1 (ID: " + admin1.getID() + ")...");
//         List<Laporan> admin1Laporan = db.getLaporanByAdminId(admin1.getID());
//         for (Laporan laporan : admin1Laporan) {
//             System.out.println("   - " + laporan.getTipeLaporan());
//         }
        
//         // Test 7: Get Laporan with Admin Info
//         System.out.println("\n7. Getting laporan with admin info...");
//         try {
//             List<String> laporanWithInfo = db.getLaporanWithAdminInfo();
//             for (String laporanInfo : laporanWithInfo) {
//                 System.out.println("   " + laporanInfo);
//             }
//         } catch (Exception e) {
//             System.out.println("   Method not available or error: " + e.getMessage());
//         }        
//         // Test 8: Update Admin
//         System.out.println("\n8. Updating admin password...");
//         Admin adminToUpdate = db.getAdminById(admin1.getID());
//         if (adminToUpdate != null) {
//             adminToUpdate.setPassword("newpassword789");
//             boolean updated = db.updateAdmin(adminToUpdate);
//             System.out.println("   Admin update " + (updated ? "successful" : "failed"));
            
//             // Verify update
//             Admin loginWithNewPass = db.loginAdmin("admin1", "newpassword789");
//             if (loginWithNewPass != null) {
//                 System.out.println("   ✓ New password works");
//             }
//         }
        
//         // Test 9: Update Laporan
//         System.out.println("\n9. Updating laporan content...");
//         Laporan laporanToUpdate = db.getLaporanById(laporan1.getIdLaporan());
//         if (laporanToUpdate != null) {
//             laporanToUpdate.setKontenLaporan("Penjualan hari ini mencapai target dengan sangat baik. Update: semua target tercapai 110%.");
//             boolean updated = db.updateLaporan(laporanToUpdate);
//             System.out.println("   Laporan update " + (updated ? "successful" : "failed"));
//         }
        
//         // Test 10: Show Final State
//         System.out.println("\n10. Final database state:");
//         System.out.println("   Total Admins: " + db.getAllAdmin().size());
//         System.out.println("   Total Laporan: " + db.getAllLaporan().size());
        
//         // Test 11: Test Foreign Key Constraint
//         System.out.println("\n11. Testing foreign key constraint...");
//         try {
//             Date testDate = new Date();
//             Laporan invalidLaporan = new Laporan(0, "Test Laporan", testDate, testDate, testDate, "Test content", 999); // Non-existent admin ID
//             boolean result = db.addLaporan(invalidLaporan);
//             if (!result) {
//                 System.out.println("   ✓ Foreign key constraint working - invalid admin ID rejected");
//             } else {
//                 System.out.println("   ✗ Foreign key constraint not working - invalid admin ID accepted");
//             }
//         } catch (Exception e) {
//             System.out.println("   ✓ Foreign key constraint working - exception thrown: " + e.getMessage());
//         }
        
//         System.out.println("\n=== Test Complete ===");
//     }
// }
