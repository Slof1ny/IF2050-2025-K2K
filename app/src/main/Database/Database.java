package main.Database;

import main.Model.Pelanggan;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:user.db";
    
    private Connection connection;
    
    // Constructor untuk inisialisasi koneksi database
    public Database() {
        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create connection to SQLite database file
            connection = DriverManager.getConnection(URL);
            
            // Enable foreign key constraints
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
            }
            
            createTableIfNotExists();
            System.out.println("Database connected successfully: user.db");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }
      // Method untuk membuat tabel pelanggan jika belum ada
    private void createTableIfNotExists() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS pelanggan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                no_hp TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        String createTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_pelanggan_timestamp 
            AFTER UPDATE ON pelanggan
            BEGIN
                UPDATE pelanggan SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
            END
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.execute(createTriggerSQL);
            System.out.println("Table 'pelanggan' and trigger created or already exists.");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }
      // Method untuk menambah pelanggan baru
    public boolean addPelanggan(Pelanggan pelanggan) {
        String insertSQL = "INSERT INTO pelanggan (nama, email, no_hp) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, pelanggan.getNama());
            stmt.setString(2, pelanggan.getEmail());
            stmt.setString(3, pelanggan.getnoHp());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID using SQLite's last_insert_rowid()
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        pelanggan.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding pelanggan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua data pelanggan
    public List<Pelanggan> getAllPelanggan() {
        List<Pelanggan> pelangganList = new ArrayList<>();
        String selectSQL = "SELECT * FROM pelanggan ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pelanggan pelanggan = new Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp")
                );
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pelanggan: " + e.getMessage());
        }
        
        return pelangganList;
    }
    
    // Method untuk mengambil pelanggan berdasarkan ID
    public Pelanggan getPelangganById(int id) {
        String selectSQL = "SELECT * FROM pelanggan WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting pelanggan by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengambil pelanggan berdasarkan email
    public Pelanggan getPelangganByEmail(String email) {
        String selectSQL = "SELECT * FROM pelanggan WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting pelanggan by email: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengupdate data pelanggan
    public boolean updatePelanggan(Pelanggan pelanggan) {
        String updateSQL = "UPDATE pelanggan SET nama = ?, email = ?, no_hp = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, pelanggan.getNama());
            stmt.setString(2, pelanggan.getEmail());
            stmt.setString(3, pelanggan.getnoHp());
            stmt.setInt(4, pelanggan.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pelanggan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus pelanggan berdasarkan ID
    public boolean deletePelanggan(int id) {
        String deleteSQL = "DELETE FROM pelanggan WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pelanggan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mencari pelanggan berdasarkan nama (search)
    public List<Pelanggan> searchPelangganByNama(String nama) {
        List<Pelanggan> pelangganList = new ArrayList<>();
        String searchSQL = "SELECT * FROM pelanggan WHERE nama LIKE ? ORDER BY nama";
        
        try (PreparedStatement stmt = connection.prepareStatement(searchSQL)) {
            stmt.setString(1, "%" + nama + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Pelanggan pelanggan = new Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp")
                );
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            System.err.println("Error searching pelanggan by nama: " + e.getMessage());
        }
        
        return pelangganList;
    }
    
    // Method untuk menghitung total pelanggan
    public int getTotalPelanggan() {
        String countSQL = "SELECT COUNT(*) as total FROM pelanggan";
        
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting pelanggan: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Method untuk menutup koneksi database
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    
    // Method untuk mengecek apakah koneksi masih aktif
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
