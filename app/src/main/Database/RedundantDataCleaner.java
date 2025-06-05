// package main.Database;

// import java.sql.*;

// public class RedundantDataCleaner {
//     private static final String URL = "jdbc:sqlite:user.db";
    
//     public static void main(String[] args) {
//         try {
//             Class.forName("org.sqlite.JDBC");
//             Connection connection = DriverManager.getConnection(URL);
            
//             System.out.println("=== Redundant Data Cleaner ===\n");
            
//             // 1. Tampilkan data dokter sebelum pembersihan
//             System.out.println("1. Data dokter sebelum pembersihan:");
//             showDoctorData(connection);
            
//             // 2. Identifikasi duplikat di tabel dokter
//             System.out.println("\n2. Mengidentifikasi duplikat di tabel dokter...");
//             identifyDoctorDuplicates(connection);
            
//             // 3. Bersihkan duplikat dokter dengan aman
//             System.out.println("\n3. Membersihkan duplikat dokter...");
//             cleanDoctorDuplicatesSafe(connection);
            
//             // 4. Tampilkan data dokter setelah pembersihan
//             System.out.println("\n4. Data dokter setelah pembersihan:");
//             showDoctorData(connection);
            
//             // 5. Verifikasi integritas referensial
//             System.out.println("\n5. Verifikasi integritas referensial:");
//             verifyReferentialIntegrity(connection);
            
//             connection.close();
//             System.out.println("\n=== Redundant Data Cleanup Complete ===");
            
//         } catch (Exception e) {
//             System.err.println("Error: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
    
//     private static void showDoctorData(Connection connection) throws SQLException {
//         String query = "SELECT id, nama, spesialisasi, created_at FROM dokter ORDER BY nama, id";
//         try (Statement stmt = connection.createStatement();
//              ResultSet rs = stmt.executeQuery(query)) {
            
//             while (rs.next()) {
//                 System.out.println("ID: " + rs.getInt("id") + 
//                                  " | Nama: " + rs.getString("nama") +
//                                  " | Spesialisasi: " + rs.getString("spesialisasi") +
//                                  " | Created: " + rs.getString("created_at"));
//             }
//         }
//     }
    
//     private static void identifyDoctorDuplicates(Connection connection) throws SQLException {
//         String findDuplicates = """
//             SELECT nama, spesialisasi, COUNT(*) as jumlah, 
//                    GROUP_CONCAT(id) as id_list
//             FROM dokter 
//             GROUP BY nama, spesialisasi 
//             HAVING COUNT(*) > 1
//             ORDER BY nama
//         """;
        
//         try (Statement stmt = connection.createStatement();
//              ResultSet rs = stmt.executeQuery(findDuplicates)) {
            
//             boolean foundDuplicates = false;
//             while (rs.next()) {
//                 foundDuplicates = true;
//                 System.out.println("Duplikat ditemukan:");
//                 System.out.println("  Nama: " + rs.getString("nama"));
//                 System.out.println("  Spesialisasi: " + rs.getString("spesialisasi"));
//                 System.out.println("  Jumlah: " + rs.getInt("jumlah"));
//                 System.out.println("  ID List: " + rs.getString("id_list"));
//                 System.out.println("---");
//             }
            
//             if (!foundDuplicates) {
//                 System.out.println("Tidak ada duplikat ditemukan di tabel dokter.");
//             }
//         }
//     }
    
//     private static void cleanDoctorDuplicatesSafe(Connection connection) throws SQLException {
//         // Ambil daftar dokter yang direferensikan dalam jadwal_pemeriksaan
//         String getReferencedDoctors = "SELECT DISTINCT id_dokter FROM jadwal_pemeriksaan";
        
//         try (Statement stmt = connection.createStatement();
//              ResultSet rs = stmt.executeQuery(getReferencedDoctors)) {
            
//             System.out.println("Dokter yang direferensikan di jadwal_pemeriksaan:");
//             while (rs.next()) {
//                 System.out.println("  - ID: " + rs.getInt("id_dokter"));
//             }
//         }
        
//         // Hapus duplikat Dr. Sari Indah (pertahankan yang direferensikan atau ID terkecil)
//         cleanSpecificDoctorDuplicate(connection, "Dr. Sari Indah", "Dermatologi & Kosmetik");
        
//         // Hapus duplikat Dr. Budi Santoso (pertahankan yang direferensikan atau ID terkecil)
//         cleanSpecificDoctorDuplicate(connection, "Dr. Budi Santoso", "Orthopedi");
//     }
    
//     private static void cleanSpecificDoctorDuplicate(Connection connection, String nama, String spesialisasi) throws SQLException {
//         // Cari semua dokter dengan nama dan spesialisasi yang sama
//         String findSameDoctors = """
//             SELECT id, nama, spesialisasi, created_at 
//             FROM dokter 
//             WHERE nama = ? AND spesialisasi = ?
//             ORDER BY id ASC
//         """;
        
//         try (PreparedStatement pstmt = connection.prepareStatement(findSameDoctors)) {
//             pstmt.setString(1, nama);
//             pstmt.setString(2, spesialisasi);
            
//             try (ResultSet rs = pstmt.executeQuery()) {
//                 int count = 0;
//                 int firstId = -1;
//                 int secondId = -1;
                
//                 while (rs.next()) {
//                     count++;
//                     if (count == 1) {
//                         firstId = rs.getInt("id");
//                     } else if (count == 2) {
//                         secondId = rs.getInt("id");
//                         break;
//                     }
//                 }
                
//                 if (count > 1) {
//                     System.out.println("Menghapus duplikat untuk " + nama + ":");
//                     System.out.println("  Ditemukan " + count + " record");
//                     System.out.println("  ID pertama (akan dipertahankan): " + firstId);
//                     System.out.println("  ID kedua (akan dihapus): " + secondId);
                    
//                     // Cek apakah ID kedua direferensikan
//                     String checkReference = "SELECT COUNT(*) as count FROM jadwal_pemeriksaan WHERE id_dokter = ?";
//                     try (PreparedStatement checkStmt = connection.prepareStatement(checkReference)) {
//                         checkStmt.setInt(1, secondId);
//                         try (ResultSet checkRs = checkStmt.executeQuery()) {
//                             if (checkRs.next() && checkRs.getInt("count") > 0) {
//                                 System.out.println("  ⚠️  ID " + secondId + " direferensikan di jadwal_pemeriksaan");
//                                 System.out.println("  Memperbarui referensi ke ID " + firstId + "...");
                                
//                                 // Update referensi dari secondId ke firstId
//                                 String updateReferences = "UPDATE jadwal_pemeriksaan SET id_dokter = ? WHERE id_dokter = ?";
//                                 try (PreparedStatement updateStmt = connection.prepareStatement(updateReferences)) {
//                                     updateStmt.setInt(1, firstId);
//                                     updateStmt.setInt(2, secondId);
//                                     int updated = updateStmt.executeUpdate();
//                                     System.out.println("  Updated " + updated + " references");
//                                 }
//                             }
//                         }
//                     }
                    
//                     // Hapus duplikat
//                     String deleteDuplicate = "DELETE FROM dokter WHERE id = ?";
//                     try (PreparedStatement deleteStmt = connection.prepareStatement(deleteDuplicate)) {
//                         deleteStmt.setInt(1, secondId);
//                         int deleted = deleteStmt.executeUpdate();
//                         System.out.println("  Deleted " + deleted + " duplicate record (ID: " + secondId + ")");
//                     }
//                 } else {
//                     System.out.println("Tidak ada duplikat untuk " + nama);
//                 }
//             }
//         }
//     }
    
//     private static void verifyReferentialIntegrity(Connection connection) throws SQLException {
//         // Hitung total records per tabel
//         String[] tables = {"pelanggan", "dokter", "jadwal_pemeriksaan", "resep", "admin", "laporan"};
        
//         for (String table : tables) {
//             String countQuery = "SELECT COUNT(*) as total FROM " + table;
//             try (Statement stmt = connection.createStatement();
//                  ResultSet rs = stmt.executeQuery(countQuery)) {
                
//                 if (rs.next()) {
//                     System.out.println("Tabel " + table + ": " + rs.getInt("total") + " records");
//                 }
//             }
//         }
        
//         System.out.println();
        
//         // Cek broken references jadwal_pemeriksaan -> dokter
//         String checkDoctorRefs = """
//             SELECT COUNT(*) as broken_refs
//             FROM jadwal_pemeriksaan jp
//             LEFT JOIN dokter d ON jp.id_dokter = d.id
//             WHERE d.id IS NULL
//         """;
        
//         try (Statement stmt = connection.createStatement();
//              ResultSet rs = stmt.executeQuery(checkDoctorRefs)) {
            
//             if (rs.next()) {
//                 int brokenRefs = rs.getInt("broken_refs");
//                 if (brokenRefs > 0) {
//                     System.out.println("⚠️  " + brokenRefs + " broken references jadwal_pemeriksaan -> dokter");
//                 } else {
//                     System.out.println("✅ Referential integrity jadwal_pemeriksaan -> dokter: OK");
//                 }
//             }
//         }
        
//         // Cek broken references laporan -> admin
//         String checkAdminRefs = """
//             SELECT COUNT(*) as broken_refs
//             FROM laporan l
//             LEFT JOIN admin a ON l.dibuat_oleh = a.ID
//             WHERE a.ID IS NULL
//         """;
        
//         try (Statement stmt = connection.createStatement();
//              ResultSet rs = stmt.executeQuery(checkAdminRefs)) {
            
//             if (rs.next()) {
//                 int brokenRefs = rs.getInt("broken_refs");
//                 if (brokenRefs > 0) {
//                     System.out.println("⚠️  " + brokenRefs + " broken references laporan -> admin");
//                 } else {
//                     System.out.println("✅ Referential integrity laporan -> admin: OK");
//                 }
//             }
//         }
//     }
// }
