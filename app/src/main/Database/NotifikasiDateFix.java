// package Database;

// import java.sql.Connection;
// import java.sql.Statement;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.sql.DriverManager;
// import java.text.SimpleDateFormat;
// import java.util.Date;

// /**
//  * Utilitas untuk memperbaiki format tanggal pada tabel notifikasi
//  * yang masih berupa timestamp epoch milliseconds
//  */
// public class NotifikasiDateFix {
//     private static final String URL = "jdbc:sqlite:user.db";
    
//     public static void main(String[] args) {
//         System.out.println("=== PERBAIKAN FORMAT TANGGAL NOTIFIKASI ===\n");
        
//         Connection connection = null;
//         try {
//             Class.forName("org.sqlite.JDBC");
//             connection = DriverManager.getConnection(URL);
            
//             if (connection == null) {
//                 System.err.println("Gagal membuka koneksi database!");
//                 return;
//             }
            
//             // 1. Periksa data untuk melihat apakah tanggal masih berupa epoch milliseconds
//             System.out.println("1. Memeriksa format tanggal di tabel notifikasi...");
//             String checkSQL = "SELECT id_notif, tanggal FROM notifikasi LIMIT 1";
//             boolean needsFix = false;
            
//             try (PreparedStatement stmt = connection.prepareStatement(checkSQL);
//                  ResultSet rs = stmt.executeQuery()) {
                
//                 if (rs.next()) {
//                     String tanggalStr = rs.getString("tanggal");
//                     System.out.println("   Format tanggal saat ini: " + tanggalStr);
                    
//                     // Cek apakah tanggal berupa angka (epoch timestamp)
//                     try {
//                         long timestamp = Long.parseLong(tanggalStr);
//                         System.out.println("   Terdeteksi tanggal berupa timestamp: " + timestamp);
//                         needsFix = true;
//                     } catch (NumberFormatException e) {
//                         // Jika gagal dikonversi ke Long, berarti bukan angka
//                         System.out.println("   Format tanggal sudah benar (bukan timestamp)");
//                         needsFix = false;
//                     }
//                 }
//             }
            
//             if (!needsFix) {
//                 System.out.println("Format tanggal sudah benar, tidak perlu diperbaiki.");
//                 return;
//             }
            
//             // 2. Tampilkan contoh data sebelum perbaikan
//             System.out.println("\n2. Contoh data sebelum perbaikan:");
//             String beforeSQL = "SELECT id_notif, isi_pesan, tanggal, id_pelanggan FROM notifikasi LIMIT 3";
            
//             try (PreparedStatement stmt = connection.prepareStatement(beforeSQL);
//                  ResultSet rs = stmt.executeQuery()) {
                
//                 while (rs.next()) {
//                     System.out.println("   ID: " + rs.getInt("id_notif") +
//                                      " | Pesan: " + rs.getString("isi_pesan").substring(0, Math.min(30, rs.getString("isi_pesan").length())) + "..." + 
//                                      " | Tanggal: " + rs.getString("tanggal") +
//                                      " | Pelanggan ID: " + rs.getInt("id_pelanggan"));
//                 }
//             }
            
//             // 3. Perbaiki format tanggal dengan mengkonversi timestamp ke datetime
//             System.out.println("\n3. Memperbaiki format tanggal...");
//             String updateSQL = "UPDATE notifikasi SET tanggal = datetime(tanggal/1000, 'unixepoch', 'localtime') WHERE typeof(tanggal) = 'integer' OR tanggal LIKE '%[0-9]%'";
            
//             try (Statement stmt = connection.createStatement()) {
//                 int rowsUpdated = stmt.executeUpdate(updateSQL);
//                 System.out.println("   ✓ " + rowsUpdated + " baris tanggal berhasil diperbaiki");
//             }
            
//             // 4. Tampilkan contoh data setelah perbaikan
//             System.out.println("\n4. Contoh data setelah perbaikan:");
//             String afterSQL = "SELECT id_notif, isi_pesan, tanggal, id_pelanggan FROM notifikasi LIMIT 3";
            
//             try (PreparedStatement stmt = connection.prepareStatement(afterSQL);
//                  ResultSet rs = stmt.executeQuery()) {
                
//                 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                
//                 while (rs.next()) {
//                     // Konversi tanggal SQLite ke format yang lebih baik
//                     String formattedDate;
//                     try {
//                         Date date = rs.getTimestamp("tanggal");
//                         formattedDate = formatter.format(date);
//                     } catch (Exception e) {
//                         formattedDate = rs.getString("tanggal");
//                     }
                    
//                     System.out.println("   ID: " + rs.getInt("id_notif") +
//                                      " | Pesan: " + rs.getString("isi_pesan").substring(0, Math.min(30, rs.getString("isi_pesan").length())) + "..." + 
//                                      " | Tanggal: " + formattedDate +
//                                      " | Pelanggan ID: " + rs.getInt("id_pelanggan"));
//                 }
//             }
            
//             System.out.println("\n✓ Perbaikan format tanggal selesai!");
            
//         } catch (SQLException e) {
//             System.err.println("Error: " + e.getMessage());
//             e.printStackTrace();
//         } catch (ClassNotFoundException e) {
//             System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
//             e.printStackTrace();
//         } finally {
//             if (connection != null) {
//                 try {
//                     connection.close();
//                 } catch (SQLException e) {
//                     System.err.println("Error closing connection: " + e.getMessage());
//                 }
//             }
//             System.out.println("\n=== SELESAI ===");
//         }
//     }
// }
            
//             if (!needsFix) {
//                 System.out.println("Format tanggal sudah benar, tidak perlu diperbaiki.");
//                 return;
//             }
            
//             // 2. Tampilkan contoh data sebelum perbaikan
//             System.out.println("\n2. Contoh data sebelum perbaikan:");
//             String beforeSQL = "SELECT id_notif, isi_pesan, tanggal, id_pelanggan FROM notifikasi LIMIT 3";
            
//             try (PreparedStatement stmt = connection.prepareStatement(beforeSQL);
//                  ResultSet rs = stmt.executeQuery()) {
                
//                 while (rs.next()) {
//                     System.out.println("   ID: " + rs.getInt("id_notif") +
//                                      " | Pesan: " + rs.getString("isi_pesan").substring(0, Math.min(30, rs.getString("isi_pesan").length())) + "..." + 
//                                      " | Tanggal: " + rs.getString("tanggal") +
//                                      " | Pelanggan ID: " + rs.getInt("id_pelanggan"));
//                 }
//             }
            
//             // 3. Perbaiki format tanggal dengan mengkonversi timestamp ke datetime
//             System.out.println("\n3. Memperbaiki format tanggal...");
//             String updateSQL = "UPDATE notifikasi SET tanggal = datetime(tanggal/1000, 'unixepoch', 'localtime') WHERE typeof(tanggal) = 'integer' OR tanggal LIKE '%[0-9]%'";
            
//             try (Statement stmt = connection.createStatement()) {
//                 int rowsUpdated = stmt.executeUpdate(updateSQL);
//                 System.out.println("   ✓ " + rowsUpdated + " baris tanggal berhasil diperbaiki");
//             }
            
//             // 4. Tampilkan contoh data setelah perbaikan
//             System.out.println("\n4. Contoh data setelah perbaikan:");
//             String afterSQL = "SELECT id_notif, isi_pesan, tanggal, id_pelanggan FROM notifikasi LIMIT 3";
            
//             try (PreparedStatement stmt = connection.prepareStatement(afterSQL);
//                  ResultSet rs = stmt.executeQuery()) {
                
//                 SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                
//                 while (rs.next()) {
//                     // Konversi tanggal SQLite ke format yang lebih baik
//                     String formattedDate;
//                     try {
//                         Date date = rs.getTimestamp("tanggal");
//                         formattedDate = formatter.format(date);
//                     } catch (Exception e) {
//                         formattedDate = rs.getString("tanggal");
//                     }
                    
//                     System.out.println("   ID: " + rs.getInt("id_notif") +
//                                      " | Pesan: " + rs.getString("isi_pesan").substring(0, Math.min(30, rs.getString("isi_pesan").length())) + "..." + 
//                                      " | Tanggal: " + formattedDate +
//                                      " | Pelanggan ID: " + rs.getInt("id_pelanggan"));
//                 }
//             }
//               System.out.println("\n✓ Perbaikan format tanggal selesai!");
            
//         } catch (SQLException e) {
//             System.err.println("Error: " + e.getMessage());
//             e.printStackTrace();
//         } catch (ClassNotFoundException e) {
//             System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
//             e.printStackTrace();
//         } finally {
//             if (connection != null) {
//                 try {
//                     connection.close();
//                 } catch (SQLException e) {
//                     System.err.println("Error closing connection: " + e.getMessage());
//                 }
//             }
//             System.out.println("\n=== SELESAI ===");
//         }
//     }
// }
