package main.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseMigration {
    private static final String URL = "jdbc:sqlite:user.db";
    private Connection connection;
    
    public DatabaseMigration() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(URL);
            // Enable foreign key constraints
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }
      public static void main(String[] args) {
        DatabaseMigration migration = new DatabaseMigration();
        System.out.println("=== Database Migration: Jadwal Pemeriksaan Table ===\n");
        
        try {
            migration.migrateJadwalPemeriksaanTable();
            migration.fixDateTimeFormat();
            System.out.println("✓ Migration and format fix completed successfully!");
        } catch (Exception e) {
            System.err.println("✗ Migration failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            migration.closeConnection();
        }
    }
    
    public void migrateJadwalPemeriksaanTable() throws SQLException {
        // Step 1: Check if migration is needed
        System.out.println("1. Checking current table structure...");
        if (hasNewStructure(connection)) {
            System.out.println("✓ Table already has the new structure. Migration not needed.");
            return;
        }
        
        // Step 2: Create new table with updated structure
        System.out.println("2. Creating new jadwal_pemeriksaan table structure...");
        String createNewTableSQL = """
            CREATE TABLE jadwal_pemeriksaan_new (
                id_jadwal INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal_waktu DATETIME NOT NULL,
                id_pasien INTEGER NOT NULL,
                id_dokter INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_pasien) REFERENCES pelanggan(id) ON DELETE CASCADE,
                FOREIGN KEY (id_dokter) REFERENCES dokter(id) ON DELETE CASCADE
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createNewTableSQL);
            System.out.println("✓ New table structure created.");
        }
          // Step 3: Migrate existing data
        System.out.println("3. Migrating existing data...");
        String selectOldDataSQL = "SELECT id_jadwal, tanggal, waktu, id_pasien, id_dokter, created_at FROM jadwal_pemeriksaan";
        String insertNewDataSQL = "INSERT INTO jadwal_pemeriksaan_new (id_jadwal, tanggal_waktu, id_pasien, id_dokter, created_at, updated_at) VALUES (?, datetime(?/1000, 'unixepoch', 'localtime'), ?, ?, datetime(?/1000, 'unixepoch', 'localtime'), datetime('now', 'localtime'))";
        
        try (PreparedStatement selectStmt = connection.prepareStatement(selectOldDataSQL);
             PreparedStatement insertStmt = connection.prepareStatement(insertNewDataSQL);
             ResultSet rs = selectStmt.executeQuery()) {
            
            int migratedRows = 0;
            while (rs.next()) {
                // Combine tanggal and waktu into single datetime
                // Note: The waktu column stores time as milliseconds since midnight
                long tanggalTime = rs.getTimestamp("tanggal").getTime();
                long waktuMillis = rs.getLong("waktu"); // Time in milliseconds since midnight
                
                // Create combined datetime
                long combinedTime = tanggalTime + waktuMillis;
                long createdAtTime = rs.getTimestamp("created_at").getTime();
                
                insertStmt.setInt(1, rs.getInt("id_jadwal"));
                insertStmt.setLong(2, combinedTime);
                insertStmt.setInt(3, rs.getInt("id_pasien"));
                insertStmt.setInt(4, rs.getInt("id_dokter"));
                insertStmt.setLong(5, createdAtTime);
                
                insertStmt.executeUpdate();
                migratedRows++;
            }
            
            System.out.println("✓ Migrated " + migratedRows + " rows of data.");
        }
        
        // Step 4: Drop old table and rename new table
        System.out.println("4. Replacing old table with new structure...");
        try (Statement stmt = connection.createStatement()) {
            // Drop the old table
            stmt.executeUpdate("DROP TABLE jadwal_pemeriksaan");
            System.out.println("✓ Old table dropped.");
            
            // Rename new table
            stmt.executeUpdate("ALTER TABLE jadwal_pemeriksaan_new RENAME TO jadwal_pemeriksaan");
            System.out.println("✓ New table renamed to jadwal_pemeriksaan.");
        }
        
        // Step 5: Recreate triggers for updated_at
        System.out.println("5. Creating updated_at trigger...");
        String createTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS jadwal_pemeriksaan_updated_at
            AFTER UPDATE ON jadwal_pemeriksaan
            FOR EACH ROW
            BEGIN
                UPDATE jadwal_pemeriksaan SET updated_at = CURRENT_TIMESTAMP WHERE id_jadwal = NEW.id_jadwal;
            END
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTriggerSQL);
            System.out.println("✓ Updated trigger created.");
        }
        
        System.out.println("6. Verifying migration...");
        verifyMigration(connection);
    }
    
    private boolean hasNewStructure(Connection connection) throws SQLException {
        String checkColumnSQL = "PRAGMA table_info(jadwal_pemeriksaan)";
        try (PreparedStatement stmt = connection.prepareStatement(checkColumnSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                if ("tanggal_waktu".equals(columnName)) {
                    return true; // New structure exists
                }
            }
        }
        return false; // Old structure
    }
    
    private void verifyMigration(Connection connection) throws SQLException {
        // Check table structure
        System.out.println("   Checking new table structure:");
        String checkStructureSQL = "PRAGMA table_info(jadwal_pemeriksaan)";
        try (PreparedStatement stmt = connection.prepareStatement(checkStructureSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String columnName = rs.getString("name");
                String columnType = rs.getString("type");
                System.out.println("   - Column: " + columnName + " (" + columnType + ")");
            }
        }
        
        // Check data count
        String countSQL = "SELECT COUNT(*) as total FROM jadwal_pemeriksaan";
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                int totalRows = rs.getInt("total");
                System.out.println("   - Total rows in migrated table: " + totalRows);
            }
        }
        
        // Show sample data
        if (hasSampleData(connection)) {
            System.out.println("   Sample migrated data:");
            String sampleSQL = "SELECT id_jadwal, tanggal_waktu, id_pasien, id_dokter FROM jadwal_pemeriksaan LIMIT 3";
            try (PreparedStatement stmt = connection.prepareStatement(sampleSQL);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    System.out.println("   - ID: " + rs.getInt("id_jadwal") + 
                                     ", DateTime: " + rs.getTimestamp("tanggal_waktu") +
                                     ", Patient: " + rs.getInt("id_pasien") +
                                     ", Doctor: " + rs.getInt("id_dokter"));
                }
            }
        }
    }
    
    private boolean hasSampleData(Connection connection) throws SQLException {
        String countSQL = "SELECT COUNT(*) as total FROM jadwal_pemeriksaan";
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            return rs.next() && rs.getInt("total") > 0;
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    public void fixDateTimeFormat() throws SQLException {
        System.out.println("=== Fixing DateTime Format in jadwal_pemeriksaan ===");
        
        // Step 1: Check if we have integer timestamps
        System.out.println("1. Checking current data format...");
        String checkSQL = "SELECT id_jadwal, tanggal_waktu, created_at FROM jadwal_pemeriksaan LIMIT 1";
        boolean needsFix = false;
        
        try (PreparedStatement stmt = connection.prepareStatement(checkSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                String tanggalWaktu = rs.getString("tanggal_waktu");
                String createdAt = rs.getString("created_at");
                
                // Check if tanggal_waktu is a pure number (integer timestamp)
                if (tanggalWaktu.matches("\\d+")) {
                    System.out.println("✓ Found integer timestamps that need conversion.");
                    needsFix = true;
                } else {
                    System.out.println("✓ DateTime format is already correct.");
                    return;
                }
            }
        }
        
        if (!needsFix) return;
        
        // Step 2: Update tanggal_waktu from integer to datetime string
        System.out.println("2. Converting tanggal_waktu from integer to datetime...");
        String updateTanggalWaktuSQL = "UPDATE jadwal_pemeriksaan SET tanggal_waktu = datetime(tanggal_waktu/1000, 'unixepoch', 'localtime') WHERE typeof(tanggal_waktu) = 'integer'";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateTanggalWaktuSQL)) {
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("✓ Updated " + rowsUpdated + " tanggal_waktu records.");
        }
        
        // Step 3: Update created_at from integer to datetime string  
        System.out.println("3. Converting created_at from integer to datetime...");
        String updateCreatedAtSQL = "UPDATE jadwal_pemeriksaan SET created_at = datetime(created_at/1000, 'unixepoch', 'localtime') WHERE typeof(created_at) = 'integer'";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateCreatedAtSQL)) {
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("✓ Updated " + rowsUpdated + " created_at records.");
        }
        
        // Step 4: Verify the fix
        System.out.println("4. Verifying datetime format fix...");
        String verifySQL = "SELECT id_jadwal, tanggal_waktu, created_at FROM jadwal_pemeriksaan";
        
        try (PreparedStatement stmt = connection.prepareStatement(verifySQL);
             ResultSet rs = stmt.executeQuery()) {
            
            System.out.println("   Fixed data:");
            while (rs.next()) {
                System.out.println("   - ID: " + rs.getInt("id_jadwal") + 
                                 ", DateTime: " + rs.getString("tanggal_waktu") +
                                 ", Created: " + rs.getString("created_at"));
            }
        }
        
        System.out.println("✓ DateTime format fix completed!");
    }
}
