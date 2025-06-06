package Database;

import Model.Pelanggan;
import Model.Resep;
import Model.Dokter;
import Model.JadwalPemeriksaan;
import Model.Admin;
import Model.Laporan;
import Model.Notifikasi;
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
        
        // Tabel dokter
        String createDokterTableSQL = """
            CREATE TABLE IF NOT EXISTS dokter (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                spesialisasi TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;        // Tabel jadwal_pemeriksaan dengan foreign key ke pelanggan dan dokter
        String createJadwalPemeriksaanTableSQL = """
            CREATE TABLE IF NOT EXISTS jadwal_pemeriksaan (
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
        
        // Tabel admin
        String createAdminTableSQL = """
            CREATE TABLE IF NOT EXISTS admin (
                ID INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;
          // Tabel laporan dengan foreign key ke admin
        String createLaporanTableSQL = """
            CREATE TABLE IF NOT EXISTS laporan (
                id_laporan INTEGER PRIMARY KEY AUTOINCREMENT,
                tipe_laporan TEXT NOT NULL,
                periode_mulai DATETIME NOT NULL,
                periode_selesai DATETIME NOT NULL,
                tanggal_dibuat DATETIME DEFAULT CURRENT_TIMESTAMP,
                konten_laporan TEXT,
                dibuat_oleh INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (dibuat_oleh) REFERENCES admin(ID) ON DELETE CASCADE
            )
        """;
        
        // Tabel notifikasi dengan foreign key ke pelanggan
        String createNotifikasiTableSQL = """
            CREATE TABLE IF NOT EXISTS notifikasi (
                id_notif INTEGER PRIMARY KEY AUTOINCREMENT,
                isi_pesan TEXT NOT NULL,
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
        
        String createDokterTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_dokter_timestamp 
            AFTER UPDATE ON dokter
            BEGIN
                UPDATE dokter SET updated_at = CURRENT_TIMESTAMP WHERE id = NEW.id;
            END
        """;
          String createJadwalPemeriksaanTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_jadwal_pemeriksaan_timestamp 
            AFTER UPDATE ON jadwal_pemeriksaan
            BEGIN
                UPDATE jadwal_pemeriksaan SET updated_at = CURRENT_TIMESTAMP WHERE id_jadwal = NEW.id_jadwal;
            END
        """;
        
        String createAdminTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_admin_timestamp 
            AFTER UPDATE ON admin
            BEGIN
                UPDATE admin SET updated_at = CURRENT_TIMESTAMP WHERE ID = NEW.ID;
            END
        """;
          String createLaporanTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_laporan_timestamp 
            AFTER UPDATE ON laporan
            BEGIN
                UPDATE laporan SET updated_at = CURRENT_TIMESTAMP WHERE id_laporan = NEW.id_laporan;
            END
        """;
        
        String createNotifikasiTriggerSQL = """
            CREATE TRIGGER IF NOT EXISTS update_notifikasi_timestamp 
            AFTER UPDATE ON notifikasi
            BEGIN
                UPDATE notifikasi SET updated_at = CURRENT_TIMESTAMP WHERE id_notif = NEW.id_notif;
            END
        """;        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPelangganTableSQL);
            stmt.execute(createResepTableSQL);
            stmt.execute(createDokterTableSQL);
            stmt.execute(createJadwalPemeriksaanTableSQL);
            stmt.execute(createAdminTableSQL);
            stmt.execute(createLaporanTableSQL);
            stmt.execute(createNotifikasiTableSQL);
            stmt.execute(createPelangganTriggerSQL);
            stmt.execute(createResepTriggerSQL);
            stmt.execute(createDokterTriggerSQL);
            stmt.execute(createJadwalPemeriksaanTriggerSQL);
            stmt.execute(createAdminTriggerSQL);
            stmt.execute(createLaporanTriggerSQL);
            stmt.execute(createNotifikasiTriggerSQL);
            System.out.println("Tables 'pelanggan', 'resep', 'dokter', 'jadwal_pemeriksaan', 'admin', 'laporan', 'notifikasi' with triggers created or already exist.");
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
        
        return resepInfoList;    }
    
    // ==================== DOKTER CRUD OPERATIONS ====================
    
    // Method untuk menambah dokter baru
    public boolean addDokter(Dokter dokter) {
        String insertSQL = "INSERT INTO dokter (nama, spesialisasi) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, dokter.getNama());
            stmt.setString(2, dokter.getSpesialisasi());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        dokter.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding dokter: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua dokter
    public List<Dokter> getAllDokter() {
        List<Dokter> dokterList = new ArrayList<>();
        String selectSQL = "SELECT * FROM dokter ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Dokter dokter = new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id")
                );
                dokterList.add(dokter);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all dokter: " + e.getMessage());
        }
        
        return dokterList;
    }
    
    // Method untuk mengambil dokter berdasarkan ID
    public Dokter getDokterById(int id) {
        String selectSQL = "SELECT * FROM dokter WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting dokter by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengupdate data dokter
    public boolean updateDokter(Dokter dokter) {
        String updateSQL = "UPDATE dokter SET nama = ?, spesialisasi = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, dokter.getNama());
            stmt.setString(2, dokter.getSpesialisasi());
            stmt.setInt(3, dokter.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating dokter: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus dokter berdasarkan ID
    public boolean deleteDokter(int id) {
        String deleteSQL = "DELETE FROM dokter WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting dokter: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mencari dokter berdasarkan spesialisasi
    public List<Dokter> searchDokterBySpesialisasi(String spesialisasi) {
        List<Dokter> dokterList = new ArrayList<>();
        String searchSQL = "SELECT * FROM dokter WHERE spesialisasi LIKE ? ORDER BY nama";
        
        try (PreparedStatement stmt = connection.prepareStatement(searchSQL)) {
            stmt.setString(1, "%" + spesialisasi + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Dokter dokter = new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id")
                );
                dokterList.add(dokter);
            }
        } catch (SQLException e) {
            System.err.println("Error searching dokter by spesialisasi: " + e.getMessage());
        }
        
        return dokterList;
    }
    
    // Method untuk menghitung total dokter
    public int getTotalDokter() {
        String countSQL = "SELECT COUNT(*) FROM dokter";
        
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting dokter: " + e.getMessage());
        }
        
        return 0;
    }
      // ==================== JADWAL PEMERIKSAAN CRUD OPERATIONS ====================
    
    // Method untuk menambah jadwal pemeriksaan baru
    public boolean addJadwalPemeriksaan(JadwalPemeriksaan jadwal) {
        String insertSQL = "INSERT INTO jadwal_pemeriksaan (tanggal_waktu, id_pasien, id_dokter) VALUES (datetime(?/1000, 'unixepoch', 'localtime'), ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setLong(1, jadwal.getTanggalWaktu().getTime());
            stmt.setInt(2, jadwal.getIdPasien());
            stmt.setInt(3, jadwal.getIdDokter());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        jadwal.setIdJadwal(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding jadwal pemeriksaan: " + e.getMessage());
        }
        return false;
    }
      // Method untuk mengambil semua jadwal pemeriksaan
    public List<JadwalPemeriksaan> getAllJadwalPemeriksaan() {
        List<JadwalPemeriksaan> jadwalList = new ArrayList<>();
        String selectSQL = "SELECT * FROM jadwal_pemeriksaan ORDER BY tanggal_waktu";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                JadwalPemeriksaan jadwal = new JadwalPemeriksaan(
                    rs.getInt("id_jadwal"),
                    rs.getTimestamp("tanggal_waktu"),
                    rs.getInt("id_pasien"),
                    rs.getInt("id_dokter")
                );
                jadwalList.add(jadwal);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all jadwal pemeriksaan: " + e.getMessage());
        }
        
        return jadwalList;
    }
      // Method untuk mengambil jadwal pemeriksaan berdasarkan ID
    public JadwalPemeriksaan getJadwalPemeriksaanById(int idJadwal) {
        String selectSQL = "SELECT * FROM jadwal_pemeriksaan WHERE id_jadwal = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idJadwal);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new JadwalPemeriksaan(
                    rs.getInt("id_jadwal"),
                    rs.getTimestamp("tanggal_waktu"),
                    rs.getInt("id_pasien"),
                    rs.getInt("id_dokter")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting jadwal pemeriksaan by ID: " + e.getMessage());
        }
        
        return null;
    }
      // Method untuk mengambil jadwal pemeriksaan berdasarkan ID pasien
    public List<JadwalPemeriksaan> getJadwalPemeriksaanByPasienId(int idPasien) {
        List<JadwalPemeriksaan> jadwalList = new ArrayList<>();
        String selectSQL = "SELECT * FROM jadwal_pemeriksaan WHERE id_pasien = ? ORDER BY tanggal_waktu";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPasien);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                JadwalPemeriksaan jadwal = new JadwalPemeriksaan(
                    rs.getInt("id_jadwal"),
                    rs.getTimestamp("tanggal_waktu"),
                    rs.getInt("id_pasien"),
                    rs.getInt("id_dokter")
                );
                jadwalList.add(jadwal);
            }
        } catch (SQLException e) {
            System.err.println("Error getting jadwal pemeriksaan by pasien ID: " + e.getMessage());
        }
        
        return jadwalList;
    }
      // Method untuk mengambil jadwal pemeriksaan berdasarkan ID dokter
    public List<JadwalPemeriksaan> getJadwalPemeriksaanByDokterId(int idDokter) {
        List<JadwalPemeriksaan> jadwalList = new ArrayList<>();
        String selectSQL = "SELECT * FROM jadwal_pemeriksaan WHERE id_dokter = ? ORDER BY tanggal_waktu";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idDokter);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                JadwalPemeriksaan jadwal = new JadwalPemeriksaan(
                    rs.getInt("id_jadwal"),
                    rs.getTimestamp("tanggal_waktu"),
                    rs.getInt("id_pasien"),
                    rs.getInt("id_dokter")
                );
                jadwalList.add(jadwal);
            }
        } catch (SQLException e) {
            System.err.println("Error getting jadwal pemeriksaan by dokter ID: " + e.getMessage());
        }
        
        return jadwalList;
    }
      // Method untuk mengupdate jadwal pemeriksaan
    public boolean updateJadwalPemeriksaan(JadwalPemeriksaan jadwal) {
        String updateSQL = "UPDATE jadwal_pemeriksaan SET tanggal_waktu = datetime(?/1000, 'unixepoch', 'localtime'), id_pasien = ?, id_dokter = ? WHERE id_jadwal = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setLong(1, jadwal.getTanggalWaktu().getTime());
            stmt.setInt(2, jadwal.getIdPasien());
            stmt.setInt(3, jadwal.getIdDokter());
            stmt.setInt(4, jadwal.getIdJadwal());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating jadwal pemeriksaan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus jadwal pemeriksaan berdasarkan ID
    public boolean deleteJadwalPemeriksaan(int idJadwal) {
        String deleteSQL = "DELETE FROM jadwal_pemeriksaan WHERE id_jadwal = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idJadwal);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting jadwal pemeriksaan: " + e.getMessage());
        }
        return false;
    }
      // Method untuk mengambil jadwal pemeriksaan dengan informasi pasien dan dokter (JOIN)
    public List<String> getJadwalPemeriksaanWithInfo() {
        List<String> jadwalInfoList = new ArrayList<>();
        String selectSQL = """
            SELECT j.id_jadwal, j.tanggal_waktu,
                   p.nama as nama_pasien, p.email as email_pasien,
                   d.nama as nama_dokter, d.spesialisasi
            FROM jadwal_pemeriksaan j
            JOIN pelanggan p ON j.id_pasien = p.id
            JOIN dokter d ON j.id_dokter = d.id
            ORDER BY j.tanggal_waktu
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
              while (rs.next()) {
                String jadwalInfo = String.format(
                    "Jadwal ID: %d | Tanggal & Waktu: %s | Pasien: %s (%s) | Dokter: %s (%s)",
                    rs.getInt("id_jadwal"),
                    rs.getTimestamp("tanggal_waktu"),
                    rs.getString("nama_pasien"),
                    rs.getString("email_pasien"),
                    rs.getString("nama_dokter"),
                    rs.getString("spesialisasi")
                );
                jadwalInfoList.add(jadwalInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting jadwal pemeriksaan with info: " + e.getMessage());
        }
        
        return jadwalInfoList;
    }
    
    // Method untuk menghitung total jadwal pemeriksaan
    public int getTotalJadwalPemeriksaan() {
        String countSQL = "SELECT COUNT(*) FROM jadwal_pemeriksaan";
        
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting jadwal pemeriksaan: " + e.getMessage());
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
    
    // =================== ADMIN CRUD METHODS ===================
    
    // Method untuk menambah admin baru
    public boolean addAdmin(Admin admin) {
        String insertSQL = "INSERT INTO admin (username, password) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPassword());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        admin.setID(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding admin: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua data admin
    public List<Admin> getAllAdmin() {
        List<Admin> adminList = new ArrayList<>();
        String selectSQL = "SELECT * FROM admin ORDER BY ID";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Admin admin = new Admin(
                    rs.getInt("ID"),
                    rs.getString("username"),
                    rs.getString("password")
                );
                adminList.add(admin);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all admin: " + e.getMessage());
        }
        
        return adminList;
    }
    
    // Method untuk mengambil admin berdasarkan ID
    public Admin getAdminById(int id) {
        String selectSQL = "SELECT * FROM admin WHERE ID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("ID"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting admin by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk login admin
    public Admin loginAdmin(String username, String password) {
        String selectSQL = "SELECT * FROM admin WHERE username = ? AND password = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                        rs.getInt("ID"),
                        rs.getString("username"),
                        rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during admin login: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk update admin
    public boolean updateAdmin(Admin admin) {
        String updateSQL = "UPDATE admin SET username = ?, password = ? WHERE ID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPassword());
            stmt.setInt(3, admin.getID());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating admin: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus admin
    public boolean deleteAdmin(int id) {
        String deleteSQL = "DELETE FROM admin WHERE ID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting admin: " + e.getMessage());
        }
        return false;
    }
    
    // =================== LAPORAN CRUD METHODS ===================
    
    // Method untuk menambah laporan baru
    public boolean addLaporan(Laporan laporan) {
        String insertSQL = """
            INSERT INTO laporan (tipe_laporan, periode_mulai, periode_selesai, 
                               tanggal_dibuat, konten_laporan, dibuat_oleh) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, laporan.getTipeLaporan());
            stmt.setTimestamp(2, new Timestamp(laporan.getPeriodeMulai().getTime()));
            stmt.setTimestamp(3, new Timestamp(laporan.getPeriodeSelesai().getTime()));
            stmt.setTimestamp(4, new Timestamp(laporan.getTanggalDibuat().getTime()));
            stmt.setString(5, laporan.getKontenLaporan());
            stmt.setInt(6, laporan.getDibuatOleh());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        laporan.setIdLaporan(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding laporan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua data laporan
    public List<Laporan> getAllLaporan() {
        List<Laporan> laporanList = new ArrayList<>();
        String selectSQL = "SELECT * FROM laporan ORDER BY tanggal_dibuat DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Laporan laporan = new Laporan(
                    rs.getInt("id_laporan"),
                    rs.getString("tipe_laporan"),
                    new java.util.Date(rs.getTimestamp("periode_mulai").getTime()),
                    new java.util.Date(rs.getTimestamp("periode_selesai").getTime()),
                    new java.util.Date(rs.getTimestamp("tanggal_dibuat").getTime()),
                    rs.getString("konten_laporan"),
                    rs.getInt("dibuat_oleh")
                );
                laporanList.add(laporan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all laporan: " + e.getMessage());
        }
        
        return laporanList;
    }
    
    // Method untuk mengambil laporan berdasarkan ID
    public Laporan getLaporanById(int id) {
        String selectSQL = "SELECT * FROM laporan WHERE id_laporan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Laporan(
                        rs.getInt("id_laporan"),
                        rs.getString("tipe_laporan"),
                        new java.util.Date(rs.getTimestamp("periode_mulai").getTime()),
                        new java.util.Date(rs.getTimestamp("periode_selesai").getTime()),
                        new java.util.Date(rs.getTimestamp("tanggal_dibuat").getTime()),
                        rs.getString("konten_laporan"),
                        rs.getInt("dibuat_oleh")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting laporan by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengambil laporan berdasarkan admin yang membuat
    public List<Laporan> getLaporanByAdminId(int adminId) {
        List<Laporan> laporanList = new ArrayList<>();
        String selectSQL = "SELECT * FROM laporan WHERE dibuat_oleh = ? ORDER BY tanggal_dibuat DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, adminId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Laporan laporan = new Laporan(
                        rs.getInt("id_laporan"),
                        rs.getString("tipe_laporan"),
                        new java.util.Date(rs.getTimestamp("periode_mulai").getTime()),
                        new java.util.Date(rs.getTimestamp("periode_selesai").getTime()),
                        new java.util.Date(rs.getTimestamp("tanggal_dibuat").getTime()),
                        rs.getString("konten_laporan"),
                        rs.getInt("dibuat_oleh")
                    );
                    laporanList.add(laporan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting laporan by admin ID: " + e.getMessage());
        }
        
        return laporanList;
    }
    
    // Method untuk mengambil laporan dengan informasi admin yang membuat
    public List<String> getLaporanWithAdminInfo() {
        List<String> laporanInfoList = new ArrayList<>();
        String selectSQL = """
            SELECT l.id_laporan, l.tipe_laporan, l.periode_mulai, l.periode_selesai,
                   l.tanggal_dibuat, l.konten_laporan, a.username as admin_username
            FROM laporan l
            JOIN admin a ON l.dibuat_oleh = a.ID
            ORDER BY l.tanggal_dibuat DESC
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String laporanInfo = String.format(
                    "Laporan ID: %d | Tipe: %s | Periode: %s s/d %s | Dibuat: %s | Admin: %s",
                    rs.getInt("id_laporan"),
                    rs.getString("tipe_laporan"),
                    rs.getTimestamp("periode_mulai"),
                    rs.getTimestamp("periode_selesai"),
                    rs.getTimestamp("tanggal_dibuat"),
                    rs.getString("admin_username")
                );
                laporanInfoList.add(laporanInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting laporan with admin info: " + e.getMessage());
        }
        
        return laporanInfoList;
    }
    
    // Method untuk update laporan
    public boolean updateLaporan(Laporan laporan) {
        String updateSQL = """
            UPDATE laporan SET tipe_laporan = ?, periode_mulai = ?, periode_selesai = ?,
                             konten_laporan = ? WHERE id_laporan = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, laporan.getTipeLaporan());
            stmt.setTimestamp(2, new Timestamp(laporan.getPeriodeMulai().getTime()));
            stmt.setTimestamp(3, new Timestamp(laporan.getPeriodeSelesai().getTime()));
            stmt.setString(4, laporan.getKontenLaporan());
            stmt.setInt(5, laporan.getIdLaporan());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating laporan: " + e.getMessage());
        }
        return false;
    }
      // Method untuk menghapus laporan
    public boolean deleteLaporan(int id) {
        String deleteSQL = "DELETE FROM laporan WHERE id_laporan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting laporan: " + e.getMessage());
        }
        return false;
    }
    
    // =================== NOTIFIKASI CRUD METHODS ===================
    
        // Method untuk menambah notifikasi baru
    public boolean addNotifikasi(Notifikasi notifikasi) {
        String insertSQL = "INSERT INTO notifikasi (isi_pesan, tanggal, id_pelanggan) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, notifikasi.getIsiPesan());
            // Store date as ISO string instead of raw timestamp
            stmt.setString(2, new java.sql.Timestamp(notifikasi.getTanggal().getTime()).toString());
            stmt.setInt(3, notifikasi.getIdPelanggan());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        notifikasi.setIdNotif(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding notifikasi: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua notifikasi
    public List<Notifikasi> getAllNotifikasi() {
        List<Notifikasi> notifikasiList = new ArrayList<>();
        String selectSQL = "SELECT * FROM notifikasi ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Notifikasi notifikasi = new Notifikasi(
                    rs.getInt("id_notif"),
                    rs.getString("isi_pesan"),
                    new java.util.Date(rs.getTimestamp("tanggal").getTime()),
                    rs.getInt("id_pelanggan")
                );
                notifikasiList.add(notifikasi);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all notifikasi: " + e.getMessage());
        }
        
        return notifikasiList;
    }
    
    // Method untuk mengambil notifikasi berdasarkan ID
    public Notifikasi getNotifikasiById(int idNotif) {
        String selectSQL = "SELECT * FROM notifikasi WHERE id_notif = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idNotif);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Notifikasi(
                        rs.getInt("id_notif"),
                        rs.getString("isi_pesan"),
                        new java.util.Date(rs.getTimestamp("tanggal").getTime()),
                        rs.getInt("id_pelanggan")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting notifikasi by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengambil notifikasi berdasarkan ID pelanggan
    public List<Notifikasi> getNotifikasiByPelangganId(int idPelanggan) {
        List<Notifikasi> notifikasiList = new ArrayList<>();
        String selectSQL = "SELECT * FROM notifikasi WHERE id_pelanggan = ? ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPelanggan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notifikasi notifikasi = new Notifikasi(
                        rs.getInt("id_notif"),
                        rs.getString("isi_pesan"),
                        new java.util.Date(rs.getTimestamp("tanggal").getTime()),
                        rs.getInt("id_pelanggan")
                    );
                    notifikasiList.add(notifikasi);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting notifikasi by pelanggan ID: " + e.getMessage());
        }
        
        return notifikasiList;
    }
      // Method untuk mengupdate notifikasi
    public boolean updateNotifikasi(Notifikasi notifikasi) {
        String updateSQL = "UPDATE notifikasi SET isi_pesan = ?, tanggal = ? WHERE id_notif = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, notifikasi.getIsiPesan());
            stmt.setString(2, new java.sql.Timestamp(notifikasi.getTanggal().getTime()).toString());
            stmt.setInt(3, notifikasi.getIdNotif());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating notifikasi: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus notifikasi berdasarkan ID
    public boolean deleteNotifikasi(int idNotif) {
        String deleteSQL = "DELETE FROM notifikasi WHERE id_notif = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idNotif);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting notifikasi: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mencari notifikasi berdasarkan isi pesan
    public List<Notifikasi> searchNotifikasiByIsiPesan(String keyword) {
        List<Notifikasi> notifikasiList = new ArrayList<>();
        String selectSQL = "SELECT * FROM notifikasi WHERE isi_pesan LIKE ? ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, "%" + keyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Notifikasi notifikasi = new Notifikasi(
                        rs.getInt("id_notif"),
                        rs.getString("isi_pesan"),
                        new java.util.Date(rs.getTimestamp("tanggal").getTime()),
                        rs.getInt("id_pelanggan")
                    );
                    notifikasiList.add(notifikasi);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching notifikasi by isi pesan: " + e.getMessage());
        }
        
        return notifikasiList;
    }
      // Method untuk mengambil notifikasi dengan informasi pelanggan (JOIN)
    public List<String> getNotifikasiWithPelangganInfo() {
        List<String> notifikasiInfoList = new ArrayList<>();
        String selectSQL = """
            SELECT n.id_notif, n.isi_pesan, n.tanggal, p.nama as pelanggan_nama, p.email as pelanggan_email
            FROM notifikasi n
            JOIN pelanggan p ON n.id_pelanggan = p.id
            ORDER BY n.tanggal DESC
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            // Import SimpleDateFormat untuk format tanggal yang lebih baik
            java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            
            while (rs.next()) {
                // Format tanggal dengan SimpleDateFormat
                String formattedDate = formatter.format(rs.getTimestamp("tanggal"));
                
                String notifikasiInfo = String.format(
                    "Notifikasi ID: %d | Pesan: %s | Tanggal: %s | Pelanggan: %s (%s)",
                    rs.getInt("id_notif"),
                    rs.getString("isi_pesan"),
                    formattedDate,
                    rs.getString("pelanggan_nama"),
                    rs.getString("pelanggan_email")
                );
                notifikasiInfoList.add(notifikasiInfo);
            }
        } catch (SQLException e) {
            System.err.println("Error getting notifikasi with pelanggan info: " + e.getMessage());
        }
        
        return notifikasiInfoList;
    }
    
    // Method untuk menghitung total notifikasi
    public int getTotalNotifikasi() {
        String countSQL = "SELECT COUNT(*) as total FROM notifikasi";
        
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting notifikasi: " + e.getMessage());
        }
        return 0;
    }
    
    // Method untuk menghitung total notifikasi berdasarkan pelanggan
    public int getTotalNotifikasiByPelanggan(int idPelanggan) {
        String countSQL = "SELECT COUNT(*) as total FROM notifikasi WHERE id_pelanggan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(countSQL)) {
            stmt.setInt(1, idPelanggan);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error counting notifikasi by pelanggan: " + e.getMessage());
        }
        return 0;
    }
}
