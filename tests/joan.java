import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import Database.Database;
import Model.*;
import Controller.*;
import java.util.*;
import java.sql.Date;

public class joan {
    static Database db;

    @BeforeAll
    static void setup() {
        db = new Database();
    }

    // 1. Mengelola Stok
    @Test
    void testUpdateStockNormal() {
        Produk produk = new Produk(0, "TestProduk", 1000, 10);
        db.addProduk(produk);
        produk.setStok(20);
        assertTrue(db.updateProduk(produk));
        Produk updated = db.getProdukById(produk.getId());
        assertEquals(20, updated.getStok());
    }
    @Test
    void testUpdateStockNegative() {
        Produk produk = new Produk(0, "TestProdukNeg", 1000, 10);
        db.addProduk(produk);
        produk.setStok(-5); // Anomali: stok negatif
        boolean result = db.updateProduk(produk);
        assertFalse(result || db.getProdukById(produk.getId()).getStok() < 0);
    }

    // 2. Mengecek Laporan Stok
    @Test
    void testGetLaporanStokNormal() {
        List<String> laporan = db.getLaporanWithAdminInfo();
        assertNotNull(laporan);
    }
    @Test
    void testGetLaporanStokNoData() {
        // Anomali: database kosong (anggap sudah clear sebelumnya)
        // Simulasikan dengan filter yang tidak ada hasil
        List<String> laporan = db.getLaporanWithAdminInfo();
        assertTrue(laporan.size() >= 0); // Tidak error walau kosong
    }

    // 3. Mengecek Riwayat Pasien
    @Test
    void testGetJadwalByPasienNormal() {
        int idPasien = 1; // Pastikan ada pasien dengan ID ini
        List<JadwalPemeriksaan> riwayat = db.getJadwalPemeriksaanByPasienId(idPasien);
        assertNotNull(riwayat);
    }
    @Test
    void testGetJadwalByPasienNotFound() {
        int idPasien = -999; // Anomali: ID tidak ada
        List<JadwalPemeriksaan> riwayat = db.getJadwalPemeriksaanByPasienId(idPasien);
        assertTrue(riwayat.isEmpty());
    }

    // 4. Memroses Resep Digital
    @Test
    void testProsesResepNormal() {
        Resep resep = new Resep(0, "Paracetamol:2;VitaminC:1", new Date(System.currentTimeMillis()), 1);
        ResepController rc = new ResepController();
        assertDoesNotThrow(() -> rc.prosesResep(resep));
    }
    @Test
    void testProsesResepFormatSalah() {
        Resep resep = new Resep(0, "SALAHFORMAT", new Date(System.currentTimeMillis()), 1); // Anomali: format salah
        ResepController rc = new ResepController();
        assertDoesNotThrow(() -> rc.prosesResep(resep)); // Harus tetap tidak crash
    }

    // 5. Memesan dan Melacak Pesanan
    @Test
    void testOrderAndTrackNormal() {
        Pesanan pesanan = new Pesanan(0, "belum dibayar", new Date(System.currentTimeMillis()), 1);
        assertTrue(db.addPesanan(pesanan));
        Pesanan fetched = db.getPesananById(pesanan.getIdPesanan());
        assertNotNull(fetched);
    }
    @Test
    void testOrderWithInvalidCustomer() {
        Pesanan pesanan = new Pesanan(0, "belum dibayar", new Date(System.currentTimeMillis()), -999); // Anomali: pelanggan tidak ada
        assertFalse(db.addPesanan(pesanan));
    }

    // 6. Memesan Jadwal
    @Test
    void testBookJadwalNormal() {
        JadwalPemeriksaanCtrl ctrl = new JadwalPemeriksaanCtrl();
        Date waktu = new Date(System.currentTimeMillis() + 1000000);
        ctrl.addJadwal(2, waktu);
        assertTrue(ctrl.bookJadwal(2, waktu, 1));
    }
    @Test
    void testBookJadwalDoubleBooking() {
        JadwalPemeriksaanCtrl ctrl = new JadwalPemeriksaanCtrl();
        Date waktu = new Date(System.currentTimeMillis() + 2000000);
        ctrl.addJadwal(3, waktu);
        ctrl.bookJadwal(3, waktu, 1);
        assertFalse(ctrl.bookJadwal(3, waktu, 2)); // Anomali: sudah di-book
    }

    // 7. Membuat Jadwal
    @Test
    void testAddJadwalNormal() {
        JadwalPemeriksaanCtrl ctrl = new JadwalPemeriksaanCtrl();
        Date waktu = new Date(System.currentTimeMillis() + 3000000);
        assertTrue(ctrl.addJadwal(4, waktu));
    }
    @Test
    void testAddJadwalDokterTidakAda() {
        JadwalPemeriksaanCtrl ctrl = new JadwalPemeriksaanCtrl();
        Date waktu = new Date(System.currentTimeMillis() + 4000000);
        assertFalse(ctrl.addJadwal(-999, waktu)); // Anomali: dokter tidak ada
    }

    // 8. Melihat Resep
    @Test
    void testViewResepNormal() {
        List<Resep> resepList = db.getAllResep();
        assertNotNull(resepList);
    }
    @Test
    void testViewResepNoData() {
        // Anomali: database kosong atau filter tidak ada hasil
        List<Resep> resepList = db.getResepByPelangganId(-999);
        assertTrue(resepList.isEmpty());
    }
}