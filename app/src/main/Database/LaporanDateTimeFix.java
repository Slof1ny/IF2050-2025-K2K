package main.Database;

import java.sql.*;

public class LaporanDateTimeFix {
    private static final String URL = "jdbc:sqlite:user.db";
    
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(URL);
            
            System.out.println("=== Fixing Laporan DateTime Fields ===");
            
            // Check current laporan data
            System.out.println("\nBefore fix - Laporan data:");
            String selectBefore = "SELECT id_laporan, periode_mulai, periode_selesai, tanggal_dibuat FROM laporan";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectBefore)) {
                
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id_laporan") + 
                                     " | Periode Mulai: " + rs.getString("periode_mulai") +
                                     " | Periode Selesai: " + rs.getString("periode_selesai") +
                                     " | Tanggal Dibuat: " + rs.getString("tanggal_dibuat"));
                }
            }
            
            // Fix periode_mulai timestamps (convert milliseconds to datetime)
            System.out.println("\nConverting periode_mulai timestamps...");
            String updatePeriodeMulai = """
                UPDATE laporan 
                SET periode_mulai = datetime(CAST(periode_mulai AS INTEGER)/1000, 'unixepoch')
                WHERE periode_mulai NOT LIKE '%-%'
            """;
            try (Statement stmt = connection.createStatement()) {
                int rowsUpdated = stmt.executeUpdate(updatePeriodeMulai);
                System.out.println("Updated " + rowsUpdated + " rows for periode_mulai");
            }
            
            // Fix periode_selesai timestamps (convert milliseconds to datetime)
            System.out.println("Converting periode_selesai timestamps...");
            String updatePeriodeSelesai = """
                UPDATE laporan 
                SET periode_selesai = datetime(CAST(periode_selesai AS INTEGER)/1000, 'unixepoch')
                WHERE periode_selesai NOT LIKE '%-%'
            """;
            try (Statement stmt = connection.createStatement()) {
                int rowsUpdated = stmt.executeUpdate(updatePeriodeSelesai);
                System.out.println("Updated " + rowsUpdated + " rows for periode_selesai");
            }
            
            // Fix tanggal_dibuat timestamps (convert milliseconds to datetime)
            System.out.println("Converting tanggal_dibuat timestamps...");
            String updateTanggalDibuat = """
                UPDATE laporan 
                SET tanggal_dibuat = datetime(CAST(tanggal_dibuat AS INTEGER)/1000, 'unixepoch')
                WHERE tanggal_dibuat NOT LIKE '%-%'
            """;
            try (Statement stmt = connection.createStatement()) {
                int rowsUpdated = stmt.executeUpdate(updateTanggalDibuat);
                System.out.println("Updated " + rowsUpdated + " rows for tanggal_dibuat");
            }
            
            // Check after fix
            System.out.println("\nAfter fix - Laporan data:");
            String selectAfter = "SELECT id_laporan, periode_mulai, periode_selesai, tanggal_dibuat FROM laporan";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(selectAfter)) {
                
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id_laporan") + 
                                     " | Periode Mulai: " + rs.getString("periode_mulai") +
                                     " | Periode Selesai: " + rs.getString("periode_selesai") +
                                     " | Tanggal Dibuat: " + rs.getString("tanggal_dibuat"));
                }
            }
            
            connection.close();
            System.out.println("\n=== Laporan DateTime Fix Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
