package Database;

import Model.*;
import java.util.Date;
import java.util.List;

public class ComprehensiveTest {
    public static void main(String[] args) {
        System.out.println("=== COMPREHENSIVE DATABASE TEST ===");
        
        Database db = new Database();
        
        if (!db.isConnected()) {
            System.out.println("❌ Failed to connect to database!");
            return;
        }
        
        System.out.println("✅ Database connected successfully!\n");
        
        // Test 1: Admin Operations
        testAdminOperations(db);
        
        // Test 2: Laporan Operations  
        testLaporanOperations(db);
        
        // Test 3: CRUD Operations
        testCRUDOperations(db);
        
        // Test 4: Complex Queries
        testComplexQueries(db);
        
        db.closeConnection();
        System.out.println("\n=== TEST COMPLETED SUCCESSFULLY ===");
    }
    
    private static void testAdminOperations(Database db) {
        System.out.println("=== TESTING ADMIN OPERATIONS ===");
        
        // Test admin login
        Admin loginResult = db.loginAdmin("admin1", "password123");
        if (loginResult != null) {
            System.out.println("✅ Admin login successful: " + loginResult.getUsername());
        } else {
            System.out.println("❌ Admin login failed");
        }
        
        // Test admin creation
        Admin newAdmin = new Admin(0, "testadmin", "testpass");
        if (db.addAdmin(newAdmin)) {
            System.out.println("✅ New admin created successfully with ID: " + newAdmin.getID());
            
            // Test update
            newAdmin.setPassword("newpassword");
            if (db.updateAdmin(newAdmin)) {
                System.out.println("✅ Admin password updated successfully");
            }
            
            // Clean up - delete test admin
            if (db.deleteAdmin(newAdmin.getID())) {
                System.out.println("✅ Test admin deleted successfully");
            }
        }
        
        System.out.println("Total Admins: " + db.getAllAdmin().size() + "\n");
    }
    
    private static void testLaporanOperations(Database db) {
        System.out.println("=== TESTING LAPORAN OPERATIONS ===");
        
        // Get an admin to create laporan
        List<Admin> admins = db.getAllAdmin();
        if (admins.isEmpty()) {
            System.out.println("❌ No admin found for laporan test");
            return;
        }
        
        Admin admin = admins.get(0);
        
        // Create new laporan
        Date now = new Date();
        Date yesterday = new Date(now.getTime() - 24 * 60 * 60 * 1000);
        
        Laporan testLaporan = new Laporan(
            0,
            "Test Laporan CRUD",
            yesterday,
            now,
            now,
            "This is a comprehensive test laporan content",
            admin.getID()
        );
        
        if (db.addLaporan(testLaporan)) {
            System.out.println("✅ Test laporan created with ID: " + testLaporan.getIdLaporan());
            
            // Test get by ID
            Laporan retrieved = db.getLaporanById(testLaporan.getIdLaporan());
            if (retrieved != null) {
                System.out.println("✅ Laporan retrieved successfully: " + retrieved.getTipeLaporan());
            }
            
            // Test get by admin ID
            List<Laporan> adminLaporans = db.getLaporanByAdminId(admin.getID());
            System.out.println("✅ Found " + adminLaporans.size() + " laporans for admin: " + admin.getUsername());
            
            // Test update
            testLaporan.setKontenLaporan("Updated test content");
            if (db.updateLaporan(testLaporan)) {
                System.out.println("✅ Laporan updated successfully");
            }
            
            // Clean up
            if (db.deleteLaporan(testLaporan.getIdLaporan())) {
                System.out.println("✅ Test laporan deleted successfully");
            }
        }
        
        System.out.println("Total Laporans: " + db.getAllLaporan().size() + "\n");
    }
    
    private static void testCRUDOperations(Database db) {
        System.out.println("=== TESTING CRUD OPERATIONS ===");
        
        // Test Pelanggan CRUD
        Pelanggan testPelanggan = new Pelanggan(0, "Test Patient", "test@email.com", "1234567890");
        if (db.addPelanggan(testPelanggan)) {
            System.out.println("✅ Test pelanggan created with ID: " + testPelanggan.getId());
            
            // Test Resep CRUD
            Resep testResep = new Resep(0, "Test prescription content", new Date(), testPelanggan.getId());
            if (db.addResep(testResep)) {
                System.out.println("✅ Test resep created with ID: " + testResep.getIDResep());
                
                // Clean up resep
                db.deleteResep(testResep.getIDResep());
                System.out.println("✅ Test resep deleted");
            }
            
            // Clean up pelanggan
            db.deletePelanggan(testPelanggan.getId());
            System.out.println("✅ Test pelanggan deleted");
        }
        
        // Test Dokter CRUD
        Dokter testDokter = new Dokter("Dr. Test", "Test Specialty", 0);
        if (db.addDokter(testDokter)) {
            System.out.println("✅ Test dokter created with ID: " + testDokter.getId());
            
            // Clean up dokter
            db.deleteDokter(testDokter.getId());
            System.out.println("✅ Test dokter deleted");
        }
        
        System.out.println();
    }
    
    private static void testComplexQueries(Database db) {
        System.out.println("=== TESTING COMPLEX QUERIES ===");
        
        // Test search operations
        List<Pelanggan> searchResults = db.searchPelangganByNama("a");
        System.out.println("✅ Found " + searchResults.size() + " pelanggan with 'a' in name");
        
        // Test join operations
        List<String> laporanWithAdmin = db.getLaporanWithAdminInfo();
        System.out.println("✅ Retrieved " + laporanWithAdmin.size() + " laporan with admin info");
        
        List<String> jadwalWithInfo = db.getJadwalPemeriksaanWithInfo();
        System.out.println("✅ Retrieved " + jadwalWithInfo.size() + " jadwal with patient and doctor info");
        
        List<String> resepWithPelanggan = db.getResepWithPelangganInfo();
        System.out.println("✅ Retrieved " + resepWithPelanggan.size() + " resep with pelanggan info");
        
        System.out.println();
    }
}
