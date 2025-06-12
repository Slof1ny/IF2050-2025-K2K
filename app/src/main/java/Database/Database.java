package Database;

import Model.Pelanggan;
import Model.Resep;
import Model.Dokter;
import Model.JadwalPemeriksaan;
import Model.Admin;
import Model.Laporan;
import Model.Notifikasi;
import Model.Produk;
import Model.Pesanan;
import Model.DetailPesanan;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class Database {
    private static final String URL = "jdbc:sqlite:user.db";
    
    private Connection connection;
      // Constructor untuk inisialisasi koneksi database
    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("DEBUG - Database working directory: " + System.getProperty("user.dir"));
            System.out.println("DEBUG - Database URL: " + URL);
            
            connection = DriverManager.getConnection(URL);
            System.out.println("DEBUG - Database connection established successfully");
            
            // Test if connection is working
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM produk")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("DEBUG - Current produk count in database: " + count);
                }
            } catch (SQLException e) {
                System.err.println("DEBUG - Error testing database connection: " + e.getMessage());
            }
            
            createTableIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTableIfNotExists() {
        // Method untuk membuat tabel pelanggan dan resep jika belum ada
        // Tabel pelanggan
        String createPelangganTableSQL = """
            CREATE TABLE IF NOT EXISTS pelanggan (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                no_hp TEXT NOT NULL,
                password TEXT NOT NULL,
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
                password TEXT NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;// Tabel jadwal_pemeriksaan dengan foreign key ke pelanggan dan dokter
        String createJadwalPemeriksaanTableSQL = """
            CREATE TABLE IF NOT EXISTS jadwal_pemeriksaan (
                id_jadwal INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal_waktu DATETIME NOT NULL,
                id_pasien INTEGER,
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
        
        // Tabel produk
        String createProdukTableSQL = """
            CREATE TABLE IF NOT EXISTS produk (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nama TEXT NOT NULL,
                harga REAL NOT NULL,
                stok INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        // Tabel pesanan dengan foreign key ke pelanggan
        String createPesananTableSQL = """
            CREATE TABLE IF NOT EXISTS pesanan (
                id_pesanan INTEGER PRIMARY KEY AUTOINCREMENT,
                tanggal DATETIME DEFAULT CURRENT_TIMESTAMP,
                status TEXT NOT NULL,
                id_pelanggan INTEGER NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_pelanggan) REFERENCES pelanggan(id) ON DELETE CASCADE
            )
        """;
        
        // Tabel detail_pesanan dengan foreign key ke pesanan dan produk
        String createDetailPesananTableSQL = """
            CREATE TABLE IF NOT EXISTS detail_pesanan (
                id_detail INTEGER PRIMARY KEY AUTOINCREMENT,
                id_pesanan INTEGER NOT NULL,
                id_produk INTEGER NOT NULL,
                kuantitas INTEGER NOT NULL,
                total_harga REAL NOT NULL,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_pesanan) REFERENCES pesanan(id_pesanan) ON DELETE CASCADE,
                FOREIGN KEY (id_produk) REFERENCES produk(id) ON DELETE CASCADE
            )
        """;
        
        // Tabel ketersediaan_dokter untuk mengelola jadwal tersedia/tidak tersedia dokter
        String createKetersediaanDokterTableSQL = """
            CREATE TABLE IF NOT EXISTS ketersediaan_dokter (
                id_ketersediaan INTEGER PRIMARY KEY AUTOINCREMENT,
                id_dokter INTEGER NOT NULL,
                tanggal DATE NOT NULL,
                jam_mulai TIME NOT NULL,
                jam_selesai TIME NOT NULL,
                status TEXT NOT NULL CHECK (status IN ('tersedia', 'tidak_tersedia')),
                keterangan TEXT,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (id_dokter) REFERENCES dokter(id) ON DELETE CASCADE,
                UNIQUE(id_dokter, tanggal, jam_mulai, jam_selesai)
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
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPelangganTableSQL);
            stmt.execute(createResepTableSQL);
            stmt.execute(createDokterTableSQL);
            stmt.execute(createJadwalPemeriksaanTableSQL);
            stmt.execute(createAdminTableSQL);
            stmt.execute(createLaporanTableSQL);
            stmt.execute(createNotifikasiTableSQL);
            stmt.execute(createProdukTableSQL);
            stmt.execute(createPesananTableSQL);
            stmt.execute(createDetailPesananTableSQL);
            stmt.execute(createKetersediaanDokterTableSQL);
            stmt.execute(createPelangganTriggerSQL);
            stmt.execute(createResepTriggerSQL);
            stmt.execute(createDokterTriggerSQL);
            stmt.execute(createJadwalPemeriksaanTriggerSQL);
            stmt.execute(createAdminTriggerSQL);
            stmt.execute(createLaporanTriggerSQL);
            stmt.execute(createNotifikasiTriggerSQL);
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public boolean addPelanggan(Pelanggan pelanggan) {
        String insertSQL = "INSERT INTO pelanggan (nama, email, no_hp, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, pelanggan.getNama());
            stmt.setString(2, pelanggan.getEmail());
            stmt.setString(3, pelanggan.getnoHp());
            stmt.setString(4, pelanggan.getPassword());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding pelanggan: " + e.getMessage());
        }
        return false;
    }

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
                    rs.getString("no_hp"),
                    rs.getString("password")
                );
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pelanggan: " + e.getMessage());
        }
        return pelangganList;
    }

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
                    rs.getString("no_hp"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting pelanggan by ID: " + e.getMessage());
        }
        return null;
    }

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
                    rs.getString("no_hp"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting pelanggan by email: " + e.getMessage());
        }
        return null;
    }

    public boolean updatePelanggan(Pelanggan pelanggan) {
        String updateSQL = "UPDATE pelanggan SET nama = ?, email = ?, no_hp = ?, password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, pelanggan.getNama());
            stmt.setString(2, pelanggan.getEmail());
            stmt.setString(3, pelanggan.getnoHp());
            stmt.setString(4, pelanggan.getPassword());
            stmt.setInt(5, pelanggan.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pelanggan: " + e.getMessage());
        }
        return false;
    }

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
                    rs.getString("no_hp"),
                    rs.getString("password")
                );
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            System.err.println("Error searching pelanggan by nama: " + e.getMessage());
        }
        return pelangganList;
    }

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
    public boolean addResep(Resep resep) {
        String insertSQL = "INSERT INTO resep (isi_resep, tanggal, id_pelanggan) VALUES (?, datetime(?/1000, 'unixepoch', 'localtime'), ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, resep.getIsiResep());
            stmt.setLong(2, resep.getTanggal().getTime());
            stmt.setInt(3, resep.getIdPelanggan());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
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

    public List<String> getResepWithPelangganInfo() {
        List<String> resepInfoList = new ArrayList<>();
        String joinSQL = "SELECT r.id_resep, r.isi_resep, r.tanggal, p.id, p.nama, p.email FROM resep r JOIN pelanggan p ON r.id_pelanggan = p.id ORDER BY r.tanggal DESC";
        try (PreparedStatement stmt = connection.prepareStatement(joinSQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String info = String.format("ResepID: %d, Isi: %s, Tanggal: %s, Pelanggan: %s (%s)",
                    rs.getInt("id_resep"), rs.getString("isi_resep"), rs.getString("tanggal"), rs.getString("nama"), rs.getString("email"));
                resepInfoList.add(info);
            }
        } catch (SQLException e) {
            System.err.println("Error joining resep and pelanggan: " + e.getMessage());
        }
        return resepInfoList;
    }    // ==================== DOKTER CRUD OPERATIONS ====================
    public boolean addDokter(Dokter dokter) {
        String insertSQL = "INSERT INTO dokter (nama, spesialisasi, password) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            System.out.println("Adding doctor: " + dokter.getNama() + " with specialization: " + dokter.getSpesialisasi());
            
            stmt.setString(1, dokter.getNama());
            stmt.setString(2, dokter.getSpesialisasi());
            stmt.setString(3, dokter.getPassword());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        dokter.setId(newId);
                        System.out.println("Doctor added successfully with ID: " + newId);
                    }
                }
                return true;
            } else {
                System.err.println("No rows were affected when adding doctor");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error adding dokter: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Dokter> getAllDokter() {
        List<Dokter> dokterList = new ArrayList<>();
        String selectSQL = "SELECT * FROM dokter ORDER BY id";
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Dokter dokter = new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id"),
                    rs.getString("password")
                );
                dokterList.add(dokter);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all dokter: " + e.getMessage());
        }
        return dokterList;
    }

    public Dokter getDokterById(int id) {
        String selectSQL = "SELECT * FROM dokter WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting dokter by ID: " + e.getMessage());
        }
        return null;
    }

    public Dokter getDokterByNama(String nama) {
        String selectSQL = "SELECT * FROM dokter WHERE nama = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, nama);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting dokter by nama: " + e.getMessage());
        }
        return null;
    }

    public boolean updateDokter(Dokter dokter) {
        String updateSQL = "UPDATE dokter SET nama = ?, spesialisasi = ?, password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, dokter.getNama());
            stmt.setString(2, dokter.getSpesialisasi());
            stmt.setString(3, dokter.getPassword());
            stmt.setInt(4, dokter.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating dokter: " + e.getMessage());
        }
        return false;
    }

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
                    rs.getInt("id"),
                    rs.getString("password")
                );
                dokterList.add(dokter);
            }
        } catch (SQLException e) {
            System.err.println("Error searching dokter by spesialisasi: " + e.getMessage());
        }
        return dokterList;
    }

    public int getTotalDokter() {
        String countSQL = "SELECT COUNT(*) as total FROM dokter";
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
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
            stmt.setInt(6, laporan.getDibuatOleh().getID());
            
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
                Admin admin = getAdminById(rs.getInt("dibuat_oleh"));
                Laporan laporan = new Laporan(
                    rs.getInt("id_laporan"),
                    rs.getString("tipe_laporan"),
                    new java.util.Date(rs.getTimestamp("periode_mulai").getTime()),
                    new java.util.Date(rs.getTimestamp("periode_selesai").getTime()),
                    new java.util.Date(rs.getTimestamp("tanggal_dibuat").getTime()),
                    rs.getString("konten_laporan"),
                    admin
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
                    Admin admin = getAdminById(rs.getInt("dibuat_oleh"));
                    return new Laporan(
                        rs.getInt("id_laporan"),
                        rs.getString("tipe_laporan"),
                        new java.util.Date(rs.getTimestamp("periode_mulai").getTime()),
                        new java.util.Date(rs.getTimestamp("periode_selesai").getTime()),
                        new java.util.Date(rs.getTimestamp("tanggal_dibuat").getTime()),
                        rs.getString("konten_laporan"),
                        admin
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
                    Admin admin = getAdminById(rs.getInt("dibuat_oleh"));
                    Laporan laporan = new Laporan(
                        rs.getInt("id_laporan"),
                        rs.getString("tipe_laporan"),
                        new java.util.Date(rs.getTimestamp("periode_mulai").getTime()),
                        new java.util.Date(rs.getTimestamp("periode_selesai").getTime()),
                        new java.util.Date(rs.getTimestamp("tanggal_dibuat").getTime()),
                        rs.getString("konten_laporan"),
                        admin
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
    
    // =================== PRODUK CRUD METHODS ===================
      // Method untuk menambah produk baru
    public boolean addProduk(Produk produk) {
        String insertSQL = "INSERT INTO produk (nama, harga, stok) VALUES (?, ?, ?)";
        
        System.out.println("DEBUG Database.addProduk - Input:");
        System.out.println("- Nama: '" + produk.getNama() + "'");
        System.out.println("- Harga: " + produk.getHarga());
        System.out.println("- Stok: " + produk.getStok());
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, produk.getNama());
            stmt.setDouble(2, produk.getHarga());
            stmt.setInt(3, produk.getStok());
            
            System.out.println("DEBUG - Executing SQL: " + insertSQL);
            System.out.println("DEBUG - Parameters: ['" + produk.getNama() + "', " + produk.getHarga() + ", " + produk.getStok() + "]");
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG - Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        int insertedId = rs.getInt(1);
                        System.out.println("DEBUG - Inserted ID: " + insertedId);
                        produk.setId(insertedId);
                    }
                }
                System.out.println("DEBUG - Product successfully added to database");
                return true;
            } else {
                System.err.println("DEBUG - No rows were affected by insert");
            }
        } catch (SQLException e) {
            System.err.println("Error adding produk: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Method untuk mengambil semua data produk
    public List<Produk> getAllProduk() {
        List<Produk> produkList = new ArrayList<>();
        String selectSQL = "SELECT * FROM produk ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Produk produk = new Produk(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getDouble("harga"),
                    rs.getInt("stok")
                );
                produkList.add(produk);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all produk: " + e.getMessage());
        }
        
        return produkList;
    }
    
    // Method untuk mengambil produk berdasarkan ID
    public Produk getProdukById(int id) {
        String selectSQL = "SELECT * FROM produk WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Produk(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getDouble("harga"),
                    rs.getInt("stok")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting produk by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengupdate data produk
    public boolean updateProduk(Produk produk) {
        String updateSQL = "UPDATE produk SET nama = ?, harga = ?, stok = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, produk.getNama());
            stmt.setDouble(2, produk.getHarga());
            stmt.setInt(3, produk.getStok());
            stmt.setInt(4, produk.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating produk: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus produk berdasarkan ID
    public boolean deleteProduk(int id) {
        String deleteSQL = "DELETE FROM produk WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting produk: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mencari produk berdasarkan nama
    public List<Produk> searchProdukByNama(String nama) {
        List<Produk> produkList = new ArrayList<>();
        String searchSQL = "SELECT * FROM produk WHERE nama LIKE ? ORDER BY nama";
        
        try (PreparedStatement stmt = connection.prepareStatement(searchSQL)) {
            stmt.setString(1, "%" + nama + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Produk produk = new Produk(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getDouble("harga"),
                    rs.getInt("stok")
                );
                produkList.add(produk);
            }
        } catch (SQLException e) {
            System.err.println("Error searching produk by nama: " + e.getMessage());
        }
        
        return produkList;
    }
    
    // =================== PESANAN CRUD METHODS ===================
      // Method untuk menambah pesanan baru
    public boolean addPesanan(Pesanan pesanan) {
        String insertSQL = "INSERT INTO pesanan (tanggal, status, id_pelanggan) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            // Use proper timestamp format instead of milliseconds
            stmt.setTimestamp(1, new Timestamp(pesanan.getTanggal().getTime()));
            stmt.setString(2, pesanan.getStatus());
            stmt.setInt(3, pesanan.getIdPelanggan());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        pesanan.setIdPesanan(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding pesanan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua data pesanan
    public List<Pesanan> getAllPesanan() {
        List<Pesanan> pesananList = new ArrayList<>();
        String selectSQL = "SELECT * FROM pesanan ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pesanan pesanan = new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("status"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
                pesananList.add(pesanan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pesanan: " + e.getMessage());
        }
        
        return pesananList;
    }
    
    // Method untuk mengambil pesanan berdasarkan ID
    public Pesanan getPesananById(int idPesanan) {
        String selectSQL = "SELECT * FROM pesanan WHERE id_pesanan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPesanan);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("status"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting pesanan by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengambil pesanan berdasarkan ID pelanggan
    public List<Pesanan> getPesananByPelangganId(int idPelanggan) {
        List<Pesanan> pesananList = new ArrayList<>();
        String selectSQL = "SELECT * FROM pesanan WHERE id_pelanggan = ? ORDER BY tanggal DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPelanggan);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Pesanan pesanan = new Pesanan(
                    rs.getInt("id_pesanan"),
                    rs.getString("status"),
                    rs.getTimestamp("tanggal"),
                    rs.getInt("id_pelanggan")
                );
                pesananList.add(pesanan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pesanan by pelanggan ID: " + e.getMessage());
        }
        
        return pesananList;
    }
      // Method untuk mengupdate pesanan
    public boolean updatePesanan(Pesanan pesanan) {
        String updateSQL = "UPDATE pesanan SET tanggal = ?, status = ?, id_pelanggan = ? WHERE id_pesanan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            // Use proper timestamp format instead of milliseconds
            stmt.setTimestamp(1, new Timestamp(pesanan.getTanggal().getTime()));
            stmt.setString(2, pesanan.getStatus());
            stmt.setInt(3, pesanan.getIdPelanggan());
            stmt.setInt(4, pesanan.getIdPesanan());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pesanan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus pesanan berdasarkan ID
    public boolean deletePesanan(int idPesanan) {
        String deleteSQL = "DELETE FROM pesanan WHERE id_pesanan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idPesanan);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pesanan: " + e.getMessage());
        }
        return false;
    }
    
    // =================== DETAIL PESANAN CRUD METHODS ===================
    
    // Method untuk menambah detail pesanan baru
    public boolean addDetailPesanan(DetailPesanan detailPesanan) {
        String insertSQL = "INSERT INTO detail_pesanan (id_pesanan, id_produk, kuantitas, total_harga) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, detailPesanan.getIdPesanan());
            stmt.setInt(2, detailPesanan.getIdProduk());
            stmt.setInt(3, detailPesanan.getKuantitas());
            stmt.setDouble(4, detailPesanan.getTotalHarga());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get the last inserted ID
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet rs = lastIdStmt.executeQuery()) {
                    if (rs.next()) {
                        detailPesanan.setIdDetail(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding detail pesanan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua detail pesanan berdasarkan ID pesanan
    public List<DetailPesanan> getDetailPesananByPesananId(int idPesanan) {
        List<DetailPesanan> detailList = new ArrayList<>();
        String selectSQL = "SELECT * FROM detail_pesanan WHERE id_pesanan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPesanan);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                DetailPesanan detail = new DetailPesanan(
                    rs.getInt("id_detail"),
                    rs.getInt("id_pesanan"),
                    rs.getInt("id_produk"),
                    rs.getInt("kuantitas"),
                    rs.getDouble("total_harga")
                );
                detailList.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting detail pesanan by pesanan ID: " + e.getMessage());
        }
        
        return detailList;
    }
    
    // Method untuk mengambil detail pesanan berdasarkan ID detail
    public DetailPesanan getDetailPesananById(int idDetail) {
        String selectSQL = "SELECT * FROM detail_pesanan WHERE id_detail = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idDetail);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new DetailPesanan(
                    rs.getInt("id_detail"),
                    rs.getInt("id_pesanan"),
                    rs.getInt("id_produk"),
                    rs.getInt("kuantitas"),
                    rs.getDouble("total_harga")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting detail pesanan by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    // Method untuk mengupdate detail pesanan
    public boolean updateDetailPesanan(DetailPesanan detailPesanan) {
        String updateSQL = "UPDATE detail_pesanan SET id_pesanan = ?, id_produk = ?, kuantitas = ?, total_harga = ? WHERE id_detail = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setInt(1, detailPesanan.getIdPesanan());
            stmt.setInt(2, detailPesanan.getIdProduk());
            stmt.setInt(3, detailPesanan.getKuantitas());
            stmt.setDouble(4, detailPesanan.getTotalHarga());
            stmt.setInt(5, detailPesanan.getIdDetail());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating detail pesanan: " + e.getMessage());
        }
        return false;
    }
      // Method untuk menghapus detail pesanan berdasarkan ID
    public boolean deleteDetailPesanan(int idDetail) {
        String deleteSQL = "DELETE FROM detail_pesanan WHERE id_detail = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idDetail);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting detail pesanan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus semua detail pesanan dari sebuah pesanan
    public boolean deleteAllDetailsByPesananId(int idPesanan) {
        String deleteSQL = "DELETE FROM detail_pesanan WHERE id_pesanan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idPesanan);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting all details by pesanan ID: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menambah ketersediaan dokter
    public boolean addKetersediaanDokter(int idDokter, java.sql.Date tanggal, java.sql.Time jamMulai, java.sql.Time jamSelesai, String status, String keterangan) {
        String insertSQL = "INSERT INTO ketersediaan_dokter (id_dokter, tanggal, jam_mulai, jam_selesai, status, keterangan) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setInt(1, idDokter);
            stmt.setDate(2, tanggal);
            stmt.setTime(3, jamMulai);
            stmt.setTime(4, jamSelesai);
            stmt.setString(5, status);
            stmt.setString(6, keterangan);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding ketersediaan dokter: " + e.getMessage());
        }
 return false;
    }
    
    // Method untuk mengambil ketersediaan dokter berdasarkan ID dokter dan tanggal
    public List<Map<String, Object>> getKetersediaanDokterByIdAndDate(int idDokter, java.sql.Date tanggal) {
        List<Map<String, Object>> ketersediaanList = new ArrayList<>();
        String selectSQL = "SELECT * FROM ketersediaan_dokter WHERE id_dokter = ? AND tanggal = ? ORDER BY jam_mulai";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idDokter);
            stmt.setDate(2, tanggal);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> ketersediaan = new HashMap<>();
                ketersediaan.put("id_ketersediaan", rs.getInt("id_ketersediaan"));
                ketersediaan.put("id_dokter", rs.getInt("id_dokter"));
                ketersediaan.put("tanggal", rs.getDate("tanggal"));
                ketersediaan.put("jam_mulai", rs.getTime("jam_mulai"));
                ketersediaan.put("jam_selesai", rs.getTime("jam_selesai"));
                ketersediaan.put("status", rs.getString("status"));
                ketersediaan.put("keterangan", rs.getString("keterangan"));
                ketersediaanList.add(ketersediaan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ketersediaan dokter: " + e.getMessage());
        }
        
        return ketersediaanList;
    }
    
    // Method untuk mengambil semua dokter yang tersedia pada tanggal dan jam tertentu
    public List<Dokter> getDokterTersedia(java.sql.Date tanggal, java.sql.Time jamMulai, java.sql.Time jamSelesai) {
        List<Dokter> dokterTersedia = new ArrayList<>();
        String selectSQL = """
            SELECT DISTINCT d.* FROM dokter d
            WHERE d.id NOT IN (
                SELECT kd.id_dokter FROM ketersediaan_dokter kd
                WHERE kd.tanggal = ? AND kd.status = 'tidak_tersedia'
                AND ((kd.jam_mulai <= ? AND kd.jam_selesai > ?) 
                     OR (kd.jam_mulai < ? AND kd.jam_selesai >= ?))
            )
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setDate(1, tanggal);
            stmt.setTime(2, jamMulai);
            stmt.setTime(3, jamMulai);
            stmt.setTime(4, jamSelesai);
            stmt.setTime(5, jamSelesai);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Dokter dokter = new Dokter(
                    rs.getString("nama"),
                    rs.getString("spesialisasi"),
                    rs.getInt("id"),
                    rs.getString("password")
                );
                dokterTersedia.add(dokter);
            }
        } catch (SQLException e) {
            System.err.println("Error getting dokter tersedia: " + e.getMessage());
        }
        
        return dokterTersedia;
    }
    
    // Method untuk update status ketersediaan dokter
    public boolean updateKetersediaanDokter(int idKetersediaan, String status, String keterangan) {
        String updateSQL = "UPDATE ketersediaan_dokter SET status = ?, keterangan = ? WHERE id_ketersediaan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, status);
            stmt.setString(2, keterangan);
            stmt.setInt(3, idKetersediaan);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating ketersediaan dokter: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus ketersediaan dokter
    public boolean deleteKetersediaanDokter(int idKetersediaan) {
        String deleteSQL = "DELETE FROM ketersediaan_dokter WHERE id_ketersediaan = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idKetersediaan);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting ketersediaan dokter: " + e.getMessage());
        }
        return false;
    }

    // Method untuk menambah pelanggan
    public boolean addPelanggan(String nama, String email, String noHp, String password) {
        String insertSQL = "INSERT INTO pelanggan (nama, email, no_hp, password) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, nama);
            stmt.setString(2, email);
            stmt.setString(3, noHp);
            stmt.setString(4, password);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding pelanggan: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua pelanggan
    public List<Pelanggan> getAllPelangganData() {
        List<Pelanggan> pelangganList = new ArrayList<>();
        String selectSQL = "SELECT * FROM pelanggan ORDER BY id";
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Pelanggan pelanggan = new Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp"),
                    rs.getString("password")
                );
                pelangganList.add(pelanggan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all pelanggan data: " + e.getMessage());
        }
        return pelangganList;
    }
    
    // Method untuk mengambil pelanggan berdasarkan ID
    public Pelanggan getPelangganDataById(int id) {
        String selectSQL = "SELECT * FROM pelanggan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Pelanggan(
                    rs.getInt("id"),
                    rs.getString("nama"),
                    rs.getString("email"),
                    rs.getString("no_hp"),
                    rs.getString("password")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting pelanggan data by ID: " + e.getMessage());
        }
        return null;
    }
    
    // Method untuk mengupdate data pelanggan
    public boolean updatePelangganData(Pelanggan pelanggan) {
        String updateSQL = "UPDATE pelanggan SET nama = ?, email = ?, no_hp = ?, password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, pelanggan.getNama());
            stmt.setString(2, pelanggan.getEmail());
            stmt.setString(3, pelanggan.getnoHp());
            stmt.setString(4, pelanggan.getPassword());
            stmt.setInt(5, pelanggan.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating pelanggan data: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus pelanggan
    public boolean deletePelangganData(int id) {
        String deleteSQL = "DELETE FROM pelanggan WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting pelanggan data: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menambah resep
    public boolean addResepData(String isiResep, int idPelanggan) {
        String insertSQL = "INSERT INTO resep (isi_resep, id_pelanggan) VALUES (?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, isiResep);
            stmt.setInt(2, idPelanggan);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error adding resep data: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk mengambil semua resep
    public List<Resep> getAllResepData() {
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
            System.err.println("Error getting all resep data: " + e.getMessage());
        }
        return resepList;
    }
    // Method untuk mengambil resep berdasarkan ID
    public Resep getResepDataById(int idResep) {
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
            System.err.println("Error getting resep data by ID: " + e.getMessage());
        }
        return null;
    }
    
    // Method untuk mengupdate resep
    public boolean updateResepData(Resep resep) {
        String updateSQL = "UPDATE resep SET isi_resep = ?, id_pelanggan = ? WHERE id_resep = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, resep.getIsiResep());
            stmt.setInt(2, resep.getIdPelanggan());
            stmt.setInt(3, resep.getIDResep());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating resep data: " + e.getMessage());
        }
        return false;
    }
    
    // Method untuk menghapus resep
    public boolean deleteResepData(int idResep) {
        String deleteSQL = "DELETE FROM resep WHERE id_resep = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
            stmt.setInt(1, idResep);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting resep data: " + e.getMessage());
        }
        return false;
    }
    
    // === AUTHENTICATION METHODS ===
    public boolean authenticatePelanggan(String nama, String password) {
        String sql = "SELECT * FROM pelanggan WHERE nama = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error authenticating pelanggan: " + e.getMessage());
        }
        return false;
    }

    public boolean authenticateDokter(String nama, String password) {
        String sql = "SELECT * FROM dokter WHERE nama = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error authenticating dokter: " + e.getMessage());
        }
        return false;
    }

    public boolean authenticateAdmin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error authenticating admin: " + e.getMessage());
        }
        return false;
    }
    
    // Reset password pelanggan jika nama ada di database
    public boolean resetPasswordIfUserExists(String nama, String newPassword) {
        String checkSQL = "SELECT * FROM pelanggan WHERE nama = ?";
        String updateSQL = "UPDATE pelanggan SET password = ? WHERE nama = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSQL)) {
            checkStmt.setString(1, nama);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                    updateStmt.setString(1, newPassword);
                    updateStmt.setString(2, nama);
                    int rows = updateStmt.executeUpdate();
                    return rows > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error resetting password: " + e.getMessage());
        }
        return false;
    }

    // =================== ADMIN STATISTICS METHODS ===================
    
    // Method untuk menghitung total pesanan yang selesai
    public int getTotalOrderSelesai() {
        String countSQL = "SELECT COUNT(*) as total FROM pesanan WHERE status = 'Selesai' OR status = 'selesai' OR status = 'SELESAI'";
        try (PreparedStatement stmt = connection.prepareStatement(countSQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error counting completed orders: " + e.getMessage());
        }
        return 0;
    }
    
    // Method untuk menghitung total revenue dari pesanan yang selesai
    public double getTotalRevenueSelesai() {
        String revenueSQL = """
            SELECT COALESCE(SUM(dp.total_harga), 0.0) as total_revenue 
            FROM detail_pesanan dp 
            JOIN pesanan p ON dp.id_pesanan = p.id_pesanan 
            WHERE p.status = 'Selesai' OR p.status = 'selesai' OR p.status = 'SELESAI'
        """;
        try (PreparedStatement stmt = connection.prepareStatement(revenueSQL);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating completed orders revenue: " + e.getMessage());
        }
        return 0.0;
    }

    // Method untuk mengambil informasi lengkap pesanan dengan detail dan produk
    public List<Map<String, Object>> getPesananLengkap(int idPesanan) {
        List<Map<String, Object>> detailList = new ArrayList<>();
        String selectSQL = """
            SELECT dp.id_detail, dp.id_pesanan, dp.id_produk, dp.kuantitas, dp.total_harga,
                   p.nama as nama_produk, p.harga as harga_produk, p.stok as stok_produk
            FROM detail_pesanan dp
            JOIN produk p ON dp.id_produk = p.id
            WHERE dp.id_pesanan = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPesanan);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> detailMap = new HashMap<>();
                detailMap.put("id_detail", rs.getInt("id_detail"));
                detailMap.put("id_pesanan", rs.getInt("id_pesanan"));
                detailMap.put("id_produk", rs.getInt("id_produk"));
                detailMap.put("kuantitas", rs.getInt("kuantitas"));
                detailMap.put("total_harga", rs.getDouble("total_harga"));
                detailMap.put("nama_produk", rs.getString("nama_produk"));
                detailMap.put("harga_produk", rs.getDouble("harga_produk"));
                detailMap.put("stok_produk", rs.getInt("stok_produk"));
                
                detailList.add(detailMap);
            }
        } catch (SQLException e) {
            System.err.println("Error getting pesanan lengkap: " + e.getMessage());
        }
        
        return detailList;
    }
    
    // =================== DOCTOR CALENDAR SPECIFIC METHODS ===================
    
    // Method untuk mengambil slot waktu yang tersedia untuk dokter tertentu pada tanggal tertentu
    public List<Map<String, Object>> getAvailableTimeSlotsForDoctor(int idDokter, java.sql.Date tanggal) {
        List<Map<String, Object>> availableSlots = new ArrayList<>();
        
        // Generate default time slots (9:00 AM to 5:00 PM, 1-hour slots)
        String[] defaultTimeSlots = {
            "09:00:00", "10:00:00", "11:00:00", "12:00:00", 
            "13:00:00", "14:00:00", "15:00:00", "16:00:00", "17:00:00"
        };
        
        // Get unavailable times for this doctor on this date
        String selectUnavailableSQL = """
            SELECT jam_mulai, jam_selesai FROM ketersediaan_dokter 
            WHERE id_dokter = ? AND tanggal = ? AND status = 'tidak_tersedia'
        """;
        
        // Get booked appointments for this doctor on this date
        String selectBookedSQL = """
            SELECT TIME(tanggal_waktu) as jam_booking FROM jadwal_pemeriksaan 
            WHERE id_dokter = ? AND DATE(tanggal_waktu) = ? AND id_pasien IS NOT NULL
        """;
        
        List<String> unavailableTimes = new ArrayList<>();
        List<String> bookedTimes = new ArrayList<>();
        
        try (PreparedStatement stmtUnavailable = connection.prepareStatement(selectUnavailableSQL)) {
            stmtUnavailable.setInt(1, idDokter);
            stmtUnavailable.setDate(2, tanggal);
            ResultSet rsUnavailable = stmtUnavailable.executeQuery();
            
            while (rsUnavailable.next()) {
                java.sql.Time jamMulai = rsUnavailable.getTime("jam_mulai");
                java.sql.Time jamSelesai = rsUnavailable.getTime("jam_selesai");
                
                // Add all hours between start and end time as unavailable
                for (String slot : defaultTimeSlots) {
                    java.sql.Time slotTime = java.sql.Time.valueOf(slot);
                    if (!slotTime.before(jamMulai) && slotTime.before(jamSelesai)) {
                        unavailableTimes.add(slot.substring(0, 5)); // HH:mm format
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting unavailable times: " + e.getMessage());
        }
        
        try (PreparedStatement stmtBooked = connection.prepareStatement(selectBookedSQL)) {
            stmtBooked.setInt(1, idDokter);
            stmtBooked.setDate(2, tanggal);
            ResultSet rsBooked = stmtBooked.executeQuery();
            
            while (rsBooked.next()) {
                String jamBooking = rsBooked.getString("jam_booking");
                if (jamBooking != null) {
                    bookedTimes.add(jamBooking.substring(0, 5)); // HH:mm format
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting booked times: " + e.getMessage());
        }
        
        // Create available slots list
        for (String timeSlot : defaultTimeSlots) {
            String timeSlotFormatted = timeSlot.substring(0, 5); // HH:mm format
            boolean isAvailable = !unavailableTimes.contains(timeSlotFormatted) && 
                                !bookedTimes.contains(timeSlotFormatted);
            
            Map<String, Object> slot = new HashMap<>();
            slot.put("time", timeSlotFormatted);
            slot.put("available", isAvailable);
            slot.put("doctor_id", idDokter);
            slot.put("date", tanggal);
            
            if (!isAvailable) {
                if (unavailableTimes.contains(timeSlotFormatted)) {
                    slot.put("reason", "Doctor not available");
                } else {
                    slot.put("reason", "Already booked");
                }
            }
            
            availableSlots.add(slot);
        }
        
        return availableSlots;
    }
    
    // Method untuk mengambil kalender dokter untuk rentang tanggal tertentu
    public Map<String, Object> getDoctorCalendar(int idDokter, java.sql.Date startDate, java.sql.Date endDate) {
        Map<String, Object> calendar = new HashMap<>();
        calendar.put("doctor_id", idDokter);
        calendar.put("start_date", startDate);
        calendar.put("end_date", endDate);
        
        // Get doctor information
        Dokter dokter = getDokterById(idDokter);
        calendar.put("doctor_name", dokter != null ? dokter.getNama() : "Unknown Doctor");
        calendar.put("doctor_specialization", dokter != null ? dokter.getSpesialisasi() : "Unknown");
        
        Map<String, List<Map<String, Object>>> dateSlots = new HashMap<>();
        
        // Iterate through each date in the range
        long currentTime = startDate.getTime();
        long endTime = endDate.getTime();
        long oneDay = 24 * 60 * 60 * 1000; // milliseconds in a day
        
        while (currentTime <= endTime) {
            java.sql.Date currentDate = new java.sql.Date(currentTime);
            String dateString = currentDate.toString();
            
            List<Map<String, Object>> slotsForDate = getAvailableTimeSlotsForDoctor(idDokter, currentDate);
            dateSlots.put(dateString, slotsForDate);
            
            currentTime += oneDay;
        }
        
        calendar.put("date_slots", dateSlots);
        return calendar;
    }
    
    // Method untuk mengambil ringkasan kalender semua dokter
    public List<Map<String, Object>> getAllDoctorsCalendarSummary(java.sql.Date date) {
        List<Map<String, Object>> doctorsSummary = new ArrayList<>();
        
        List<Dokter> allDokters = getAllDokter();
        
        for (Dokter dokter : allDokters) {
            Map<String, Object> summary = new HashMap<>();
            summary.put("doctor_id", dokter.getId());
            summary.put("doctor_name", dokter.getNama());
            summary.put("doctor_specialization", dokter.getSpesialisasi());
            
            List<Map<String, Object>> slots = getAvailableTimeSlotsForDoctor(dokter.getId(), date);
            long availableCount = slots.stream()
                .mapToLong(slot -> (Boolean) slot.get("available") ? 1 : 0)
                .sum();
            
            summary.put("total_slots", slots.size());
            summary.put("available_slots", availableCount);
            summary.put("booked_slots", slots.size() - availableCount);
            summary.put("availability_percentage", 
                slots.size() > 0 ? (availableCount * 100.0 / slots.size()) : 0);
            
            doctorsSummary.add(summary);
        }
        
        return doctorsSummary;
    }
    
    // Method untuk booking appointment dengan validasi dokter dan waktu
    public boolean bookAppointmentWithDoctor(int idDokter, java.sql.Date tanggal, String waktu, int idPasien) {
        // First check if the slot is available
        List<Map<String, Object>> availableSlots = getAvailableTimeSlotsForDoctor(idDokter, tanggal);
        
        boolean slotAvailable = false;
        for (Map<String, Object> slot : availableSlots) {
            if (slot.get("time").equals(waktu) && (Boolean) slot.get("available")) {
                slotAvailable = true;
                break;
            }
        }
        
        if (!slotAvailable) {
            System.err.println("Selected time slot is not available for this doctor");
            return false;
        }
        
        // Create the appointment datetime
        try {
            String dateTimeString = tanggal.toString() + " " + waktu + ":00";
            java.sql.Timestamp appointmentDateTime = java.sql.Timestamp.valueOf(dateTimeString);
            
            JadwalPemeriksaan jadwal = new JadwalPemeriksaan(0, appointmentDateTime, idPasien, idDokter);
            return addJadwalPemeriksaan(jadwal);
            
        } catch (Exception e) {
            System.err.println("Error creating appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Method untuk mengambil appointment pasien dengan informasi dokter
    public List<Map<String, Object>> getPatientAppointmentsWithDoctorInfo(int idPasien) {
        List<Map<String, Object>> appointments = new ArrayList<>();
        String selectSQL = """
            SELECT j.id_jadwal, j.tanggal_waktu, j.id_pasien, j.id_dokter,
                   d.nama as doctor_name, d.spesialisasi as doctor_specialization
            FROM jadwal_pemeriksaan j
            JOIN dokter d ON j.id_dokter = d.id
            WHERE j.id_pasien = ?
            ORDER BY j.tanggal_waktu DESC
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setInt(1, idPasien);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> appointment = new HashMap<>();
                appointment.put("id_jadwal", rs.getInt("id_jadwal"));
                appointment.put("tanggal_waktu", rs.getTimestamp("tanggal_waktu"));
                appointment.put("id_pasien", rs.getInt("id_pasien"));
                appointment.put("id_dokter", rs.getInt("id_dokter"));
                appointment.put("doctor_name", rs.getString("doctor_name"));
                appointment.put("doctor_specialization", rs.getString("doctor_specialization"));
                
                // Format date and time separately
                java.sql.Timestamp timestamp = rs.getTimestamp("tanggal_waktu");
                appointment.put("appointment_date", new java.sql.Date(timestamp.getTime()).toString());
                appointment.put("appointment_time", new java.sql.Time(timestamp.getTime()).toString().substring(0, 5));
                
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Error getting patient appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    // Method untuk cancel appointment
    public boolean cancelAppointment(int idJadwal, int idPasien) {
        // Verify the appointment belongs to the patient
        String verifySQL = "SELECT id_pasien FROM jadwal_pemeriksaan WHERE id_jadwal = ?";
        String deleteSQL = "DELETE FROM jadwal_pemeriksaan WHERE id_jadwal = ? AND id_pasien = ?";
        
        try (PreparedStatement verifyStmt = connection.prepareStatement(verifySQL)) {
            verifyStmt.setInt(1, idJadwal);
            ResultSet rs = verifyStmt.executeQuery();
            
            if (rs.next() && rs.getInt("id_pasien") == idPasien) {
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteSQL)) {
                    deleteStmt.setInt(1, idJadwal);
                    deleteStmt.setInt(2, idPasien);
                    
                    int rowsAffected = deleteStmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error canceling appointment: " + e.getMessage());
        }
        
        return false;
    }
    
    // =================== CHECKOUT AND ORDER METHODS ===================
      // Method untuk mendapatkan ID pelanggan berdasarkan nama (untuk logged user)
    public int getPelangganIdByNama(String nama) {
        String selectSQL = "SELECT id FROM pelanggan WHERE nama = ?";
        System.out.println("DEBUG - Searching for customer with name: '" + nama + "'");
        try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
            stmt.setString(1, nama);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("DEBUG - Found customer ID: " + id);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("Error getting pelanggan ID by nama: " + e.getMessage());
        }
        System.out.println("DEBUG - Customer not found");
        return -1; // Return -1 if not found
    }
      // Method untuk menambah pesanan dengan detail dalam satu transaksi (untuk checkout)
    public boolean addPesananWithDetails(int idPelanggan, java.util.List<Controller.DashboardController.CartItem> cartItems, double totalHarga) {
        String insertPesananSQL = "INSERT INTO pesanan (id_pelanggan, tanggal, status) VALUES (?, ?, ?)";
        String insertDetailSQL = "INSERT INTO detail_pesanan (id_pesanan, id_produk, kuantitas, total_harga) VALUES (?, ?, ?, ?)";
        String updateStokSQL = "UPDATE produk SET stok = stok - ? WHERE id = ?";
        
        try {
            connection.setAutoCommit(false); // Start transaction
              // Insert pesanan
            int idPesanan;
            try (PreparedStatement pesananStmt = connection.prepareStatement(insertPesananSQL)) {
                pesananStmt.setInt(1, idPelanggan);
                pesananStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                pesananStmt.setString(3, "selesai");
                
                int rowsAffected = pesananStmt.executeUpdate();
                if (rowsAffected == 0) {
                    connection.rollback();
                    return false;
                }
                
                // Get generated pesanan ID using SQLite's last_insert_rowid()
                String getLastIdSQL = "SELECT last_insert_rowid()";
                try (PreparedStatement lastIdStmt = connection.prepareStatement(getLastIdSQL);
                     ResultSet lastIdRs = lastIdStmt.executeQuery()) {
                    if (lastIdRs.next()) {
                        idPesanan = lastIdRs.getInt(1);
                    } else {
                        connection.rollback();
                        return false;
                    }
                }
            }
            
            // Insert detail pesanan and update stock for each cart item
            try (PreparedStatement detailStmt = connection.prepareStatement(insertDetailSQL);
                 PreparedStatement stokStmt = connection.prepareStatement(updateStokSQL)) {
                
                for (Controller.DashboardController.CartItem item : cartItems) {
                    // Insert detail pesanan
                    detailStmt.setInt(1, idPesanan);
                    detailStmt.setInt(2, item.getProduct().getId());
                    detailStmt.setInt(3, item.getQuantity());
                    detailStmt.setDouble(4, item.getTotalPrice());
                    detailStmt.executeUpdate();
                    
                    // Update product stock
                    stokStmt.setInt(1, item.getQuantity());
                    stokStmt.setInt(2, item.getProduct().getId());
                    int stockUpdateRows = stokStmt.executeUpdate();
                    
                    if (stockUpdateRows == 0) {
                        System.err.println("Warning: Stock update failed for product ID: " + item.getProduct().getId());
                        connection.rollback();
                        return false;
                    }
                }
            }
            
            connection.commit(); // Commit transaction
            System.out.println("Order successfully saved with ID: " + idPesanan + " for customer ID: " + idPelanggan);
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
            System.err.println("Error adding pesanan with details: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Reset auto-commit
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }
}
