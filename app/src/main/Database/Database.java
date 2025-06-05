package main.Database;

import main.Model.Pelanggan;
import main.Model.Resep;
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
    }    // Method untuk membuat tabel pelanggan dan resep jika belum ada
    private void createTableIfNotExists() {
        // Tabel pelanggan
        String createPelangganTableSQL = """
            CREATE TABLE IF NOT EXISTS pelanggan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                no_hp TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Tabel resep dengan foreign key ke pelanggan
        String createResepTableSQL = """
            CREATE TABLE IF NOT EXISTS resep (
                id_resep INTEGER PRIMARY KEY AUTOINCREMENT,
                isi_resep TEXT NOT NULL,
                tanggal DATETIME DEFAULT CURRENT_TIMESTAMP,
                id_pelanggan INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id) ON DELETE CASCADE
            )
        """;
        
        String createPelangganTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_pelanggan_timestamp 
            AFTER UPDATE ON pelanggan
            BEGIN
                UPDATE pelanggan SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
            END
        """;
        
        String createResepTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_resep_timestamp 
            AFTER UPDATE ON resep
            BEGIN
                UPDATE resep SET updated_at = CURRENT_TIMESTAMP WHERE id_resep = NEW.id_resep;
            END
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPelangganTableSQL);
            stmt.execute(createResepTableSQL);
            stmt.execute(createPelangganTriggerSQL);
            stmt.execute(createResepTriggerSQL);
            System.out.println("Tables 'pelanggan' and 'resep' with triggers created or already exist.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
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
    
    // ========== RESEP METHODS ==========
    
    // Method untuk menambah resep baru    public boolean addResep(Resep resep) {
        public boolean addResep(Resep resep) {
        String insertSQL = "INSERT INTO resep (isi_resep, tanggal, id_pelanggan) VALUES (?, datetime(?/1000, 'unixepoch', 'localtime'), ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, resep.getIsiResep());
            stmt.setLong(2, resep.getTanggal().getTime());
            stmt.setInt(3, resep.getIdPelanggan());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        resep.setIDResep(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding resep: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua resep
    public List<Resep> getAllResep() {
        List<Resep> resepList = new ArrayList<>();
        String selectSQL = "SELECT * FROM resep ORDER BY id_resep";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Resep resep = new Resep(
                    rs.getInt("id_resep"),
                    rs.getString("isi_resep"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
                resepList.add(resep);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all resep: " + e.getMessage());
        }
        
        return resepList;
    }
    
    // Method untuk mengambil resep berdasarkan ID
    public Resep getResepById(int idResep) {
        String selectSQL = "SELECT * FROM resep WHERE id_resep = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idResep);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Resep(
                    rs.getInt("id_resep"),
                    rs.getString("isi_resep"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting resep by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengambil resep berdasarkan ID pelanggan
    public List<Resep> getResepByPelangganId(int idPelanggan) {
        List<Resep> resepList = new ArrayList<>();
        String selectSQL = "SELECT * FROM resep WHERE id_pelanggan = ? ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPelanggan);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Resep resep = new Resep(
                    rs.getInt("id_resep"),
                    rs.getString("isi_resep"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
                resepList.add(resep);
            }
        } catch (SQLException e) {
            System.err.println("Error getting resep by pelanggan ID: " + e.getMessage());
        }
        
        return resepList;
    }
    
    // Method untuk mengupdate resep    public boolean updateResep(Resep resep) {
            public boolean updateResep(Resep resep) {
        String updateSQL = "UPDATE resep SET isi_resep = ?, tanggal = datetime(?/1000, 'unixepoch', 'localtime'), id_pelanggan = ? WHERE id_resep = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, resep.getIsiResep());
            stmt.setLong(2, resep.getTanggal().getTime());
            stmt.setInt(3, resep.getIdPelanggan());
            stmt.setInt(4, resep.getIDResep());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating resep: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus resep berdasarkan ID
    public boolean deleteResep(int idResep) {
        String deleteSQL = "DELETE FROM resep WHERE id_resep = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idResep);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting resep: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mencari resep berdasarkan isi resep
    public List<Resep> searchResepByIsi(String isiResep) {
        List<Resep> resepList = new ArrayList<>();
        String searchSQL = "SELECT * FROM resep WHERE isi_resep LIKE ? ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(searchSQL)) {
            stmt.setString(1, "%" + isiResep + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Resep resep = new Resep(
                    rs.getInt("id_resep"),
                    rs.getString("isi_resep"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
                resepList.add(resep);
            }
        } catch (SQLException e) {
            System.err.println("Error searching resep by isi: " + e.getMessage());
        }
        
        return resepList;
    }
    
    // Method untuk menghitung total resep
    public int getTotalResep() {
        String countSQL = "SELECT COUNT(*) as total FROM resep";
        
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting resep: " + e.getMessage());
        }
        
        return 0;
    }
    
    // Method untuk mengambil resep dengan info pelanggan (JOIN)
    public List<String> getResepWithPelangganInfo() {
        List<String> resepInfoList = new ArrayList<>();
        String joinSQL = """
            SELECT r.id_resep, r.isi_resep, r.tanggal, 
                   p.id, p.nama, p.email 
            FROM resep r 
            JOIN pelanggan p ON r.id_pelanggan = p.id 
            ORDER BY r.tanggal DESC
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(joinSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String info = String.format(
                    "Resep ID: %d | Isi: %s | Tanggal: %s | Pelanggan: %s (%s)",
                    rs.getInt("id_resep"),
                    rs.getString("isi_resep"),
                    rs.getTimestamp("tanggal").toString(),
                    rs.getString("nama"),
                    rs.getString("email")
                );
                resepInfoList.add(info);
            }
        } catch (SQLException e) {
            System.err.println("Error getting resep with pelanggan info: " + e.getMessage());
        }
        
        return resepInfoList;
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
