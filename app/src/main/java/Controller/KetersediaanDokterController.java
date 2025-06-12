package Controller;

import Database.Database;
import Model.Dokter;
import Model.KetersediaanDokter;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.Map;

public class KetersediaanDokterController {
    private Database db;

    public KetersediaanDokterController() {
        this.db = new Database();
    }

    // Method untuk dokter mengatur hari/jam tidak tersedia
    public boolean setTidakTersedia(int idDokter, Date tanggal, Time jamMulai, Time jamSelesai, String keterangan) {
        return db.addKetersediaanDokter(idDokter, tanggal, jamMulai, jamSelesai, "tidak_tersedia", keterangan);
    }

    // Method untuk dokter mengatur hari/jam tersedia kembali
    public boolean setTersedia(int idDokter, Date tanggal, Time jamMulai, Time jamSelesai, String keterangan) {
        return db.addKetersediaanDokter(idDokter, tanggal, jamMulai, jamSelesai, "tersedia", keterangan);
    }

    // Method untuk mengambil ketersediaan dokter pada tanggal tertentu
    public List<Map<String, Object>> getKetersediaanDokter(int idDokter, Date tanggal) {
        return db.getKetersediaanDokterByIdAndDate(idDokter, tanggal);
    }

    // Method untuk mengambil daftar dokter yang tersedia pada waktu tertentu
    public List<Dokter> getDokterTersedia(Date tanggal, Time jamMulai, Time jamSelesai) {
        return db.getDokterTersedia(tanggal, jamMulai, jamSelesai);
    }

    // Method untuk update status ketersediaan
    public boolean updateStatusKetersediaan(int idKetersediaan, String status, String keterangan) {
        return db.updateKetersediaanDokter(idKetersediaan, status, keterangan);
    }

    // Method untuk menghapus jadwal ketersediaan
    public boolean hapusKetersediaan(int idKetersediaan) {
        return db.deleteKetersediaanDokter(idKetersediaan);
    }

    // Method untuk mengecek apakah dokter tersedia pada waktu tertentu
    public boolean isDokterTersedia(int idDokter, Date tanggal, Time jamMulai, Time jamSelesai) {
        List<Map<String, Object>> ketersediaanList = db.getKetersediaanDokterByIdAndDate(idDokter, tanggal);
        
        for (Map<String, Object> ketersediaan : ketersediaanList) {
            String status = (String) ketersediaan.get("status");
            Time dbJamMulai = (Time) ketersediaan.get("jam_mulai");
            Time dbJamSelesai = (Time) ketersediaan.get("jam_selesai");
            
            // Cek apakah ada overlap waktu dan status tidak tersedia
            if ("tidak_tersedia".equals(status)) {
                if (isTimeOverlap(jamMulai, jamSelesai, dbJamMulai, dbJamSelesai)) {
                    return false; // Dokter tidak tersedia
                }
            }
        }
        
        return true; // Dokter tersedia
    }

    // Helper method untuk mengecek overlap waktu
    private boolean isTimeOverlap(Time start1, Time end1, Time start2, Time end2) {
        return start1.before(end2) && end1.after(start2);
    }

    public void closeConnection() {
        if (db != null) {
            db.closeConnection();
        }
    }
}
