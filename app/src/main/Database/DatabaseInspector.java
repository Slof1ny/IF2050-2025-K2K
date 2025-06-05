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
            
            // Check SQLite date/time functions
            System.out.println("\n3. SQLite DateTime Functions Test:");
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
            System.out.println("\n4. Schema Information:");
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
            
            connection.close();
            System.out.println("\n=== Inspection Complete ===");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
