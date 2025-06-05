package main.Database;

import java.sql.*;

public class DatabaseInspector {
    private static final String URL = "jdbc:sqlite:user.db";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(URL);
            
            System.out.println("=== Database Content Inspector ===\n");
            
            // Check pelanggan table
            System.out.println("1. Tabel Pelanggan:");
            String selectPelanggan = "SELECT * FROM pelanggan";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectPelanggan)) {
                
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + 
                                     ", Nama: " + rs.getString("nama") +
                                     ", Email: " + rs.getString("email") +
                                     ", Created: " + rs.getString("created_at") +
                                     ", Updated: " + rs.getString("updated_at"));
                }
            }
            
            // Check resep table - showing raw data
            System.out.println("\n2. Tabel Resep (Raw Data):");
            String selectResep = "SELECT id_resep, isi_resep, tanggal, id_pelanggan, created_at, updated_at FROM resep";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectResep)) {
                
                while (rs.next()) {
                    System.out.println("ID Resep: " + rs.getInt("id_resep"));
                    System.out.println("  Isi: " + rs.getString("isi_resep"));
                    System.out.println("  Tanggal (raw): " + rs.getString("tanggal"));
                    System.out.println("  Tanggal (as Timestamp): " + rs.getTimestamp("tanggal"));
                    System.out.println("  ID Pelanggan: " + rs.getInt("id_pelanggan"));
                    System.out.println("  Created: " + rs.getString("created_at"));
                    System.out.println("  Updated: " + rs.getString("updated_at"));
                    System.out.println("---");
                }
            }
              // Check dokter table
            System.out.println("\n3. Tabel Dokter:");
            String selectDokter = "SELECT * FROM dokter";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectDokter)) {
                
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + 
                                     ", Nama: " + rs.getString("nama") +
                                     ", Spesialisasi: " + rs.getString("spesialisasi") +
                                     ", Created: " + rs.getString("created_at") +
                                     ", Updated: " + rs.getString("updated_at"));
                }
            }
              // Check jadwal_pemeriksaan table
            System.out.println("\n4. Tabel Jadwal Pemeriksaan:");
            String selectJadwal = "SELECT * FROM jadwal_pemeriksaan";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectJadwal)) {
                
                while (rs.next()) {
                    System.out.println("ID Jadwal: " + rs.getInt("id_jadwal"));
                    System.out.println("  Tanggal & Waktu (raw): " + rs.getString("tanggal_waktu"));
                    System.out.println("  Tanggal & Waktu (as Timestamp): " + rs.getTimestamp("tanggal_waktu"));
                    System.out.println("  ID Pasien: " + rs.getInt("id_pasien"));
                    System.out.println("  ID Dokter: " + rs.getInt("id_dokter"));
                    System.out.println("  Created: " + rs.getString("created_at"));
                    System.out.println("  Updated: " + rs.getString("updated_at"));
                    System.out.println("---");
                }
            }
            
            // Check admin table
            System.out.println("\n5. Tabel Admin:");
            String selectAdmin = "SELECT * FROM admin";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectAdmin)) {
                
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("ID") + 
                                     ", Username: " + rs.getString("username") +
                                     ", Password: " + rs.getString("password") +
                                     ", Created: " + rs.getString("created_at") +
                                     ", Updated: " + rs.getString("updated_at"));
                }
            }
            
            // Check laporan table
            System.out.println("\n6. Tabel Laporan:");
            String selectLaporan = "SELECT * FROM laporan";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectLaporan)) {
                
                while (rs.next()) {
                    System.out.println("ID Laporan: " + rs.getInt("id_laporan"));
                    System.out.println("  Tipe: " + rs.getString("tipe_laporan"));
                    System.out.println("  Periode Mulai: " + rs.getString("periode_mulai"));
                    System.out.println("  Periode Selesai: " + rs.getString("periode_selesai"));
                    System.out.println("  Tanggal Dibuat: " + rs.getString("tanggal_dibuat"));
                    System.out.println("  Konten: " + rs.getString("konten_laporan"));
                    System.out.println("  Dibuat Oleh (Admin ID): " + rs.getInt("dibuat_oleh"));
                    System.out.println("  Created: " + rs.getString("created_at"));
                    System.out.println("  Updated: " + rs.getString("updated_at"));
                    System.out.println("---");
                }
            }
            
            // Check laporan with admin info using JOIN
            System.out.println("\n7. Laporan dengan Info Admin (JOIN):");
            String joinQuery = """
                SELECT l.id_laporan, l.tipe_laporan, l.periode_mulai, l.periode_selesai,
                       l.tanggal_dibuat, l.konten_laporan, a.username as admin_username
                FROM laporan l
                JOIN admin a ON l.dibuat_oleh = a.ID
                ORDER BY l.tanggal_dibuat DESC
            """;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(joinQuery)) {
                
                while (rs.next()) {
                    System.out.println("Laporan ID: " + rs.getInt("id_laporan") + 
                                     " | Tipe: " + rs.getString("tipe_laporan") +
                                     " | Dibuat oleh: " + rs.getString("admin_username") +
                                     " | Tanggal: " + rs.getString("tanggal_dibuat"));
                    System.out.println("  Konten: " + rs.getString("konten_laporan"));
                    System.out.println("---");
                }
            }            // Check SQLite date/time functions
            System.out.println("\n8. SQLite DateTime Functions Test:");
            String dateTimeTest = "SELECT datetime('now') as current_datetime, " +
                                 "strftime('%s', 'now') as unix_timestamp, " +
                                 "datetime(1749128732578/1000, 'unixepoch') as from_millis";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(dateTimeTest)) {
                
                if (rs.next()) {
                    System.out.println("Current DateTime: " + rs.getString("current_datetime"));
                    System.out.println("Unix Timestamp: " + rs.getString("unix_timestamp"));
                    System.out.println("From Milliseconds: " + rs.getString("from_millis"));
                }
            }
            
            // Check data types in resep table
            System.out.println("\n9. Schema Information:");
            String schemaInfo = "PRAGMA table_info(resep)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(schemaInfo)) {
                
                System.out.println("Resep table schema:");
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + 
                                     ", Type: " + rs.getString("type") +
                                     ", NotNull: " + rs.getInt("notnull") +
                                     ", Default: " + rs.getString("dflt_value"));
                }
            }
            
            // Check dokter table schema
            String schemaDokter = "PRAGMA table_info(dokter)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(schemaDokter)) {
                
                System.out.println("\nDokter table schema:");
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + 
                                     ", Type: " + rs.getString("type") +
                                     ", NotNull: " + rs.getInt("notnull") +
                                     ", Default: " + rs.getString("dflt_value"));
                }
            }
            
            // Check jadwal_pemeriksaan table schema
            String schemaJadwal = "PRAGMA table_info(jadwal_pemeriksaan)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(schemaJadwal)) {
                
                System.out.println("\nJadwal Pemeriksaan table schema:");
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + 
                                     ", Type: " + rs.getString("type") +
                                     ", NotNull: " + rs.getInt("notnull") +
                                     ", Default: " + rs.getString("dflt_value"));
                }
            }
            
            // Check admin table schema
            String schemaAdmin = "PRAGMA table_info(admin)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(schemaAdmin)) {
                
                System.out.println("\nAdmin table schema:");
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + 
                                     ", Type: " + rs.getString("type") +
                                     ", NotNull: " + rs.getInt("notnull") +
                                     ", Default: " + rs.getString("dflt_value"));
                }
            }
            
            // Check laporan table schema
            String schemaLaporan = "PRAGMA table_info(laporan)";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(schemaLaporan)) {
                
                System.out.println("\nLaporan table schema:");
                while (rs.next()) {
                    System.out.println("Column: " + rs.getString("name") + 
                                     ", Type: " + rs.getString("type") +
                                     ", NotNull: " + rs.getInt("notnull") +
                                     ", Default: " + rs.getString("dflt_value"));
                }
            }
            
            connection.close();
            System.out.println("\n=== Inspection Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
