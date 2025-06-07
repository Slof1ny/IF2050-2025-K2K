package Controller;

import Database.Database;
import Model.JadwalPemeriksaan;
import java.util.Date;

public class JadwalPemeriksaanCtrl {
    private Database db = new Database();
    public boolean addJadwal(int idDokter, Date tanggalWaktu) {
        JadwalPemeriksaan jadwal = new JadwalPemeriksaan(0, tanggalWaktu, 0, idDokter); // idPasien = 0 (NULL di DB)
        return db.addJadwalPemeriksaan(jadwal);
    }
    public boolean bookJadwal(int idDokter, Date tanggalWaktu, int idPasien) {
        for (JadwalPemeriksaan jadwal : db.getJadwalPemeriksaanByDokterId(idDokter)) {
            if (jadwal.getTanggalWaktu().equals(tanggalWaktu) && (jadwal.getIdPasien() == 0)) {
                jadwal.setIdPasien(idPasien);
                return db.updateJadwalPemeriksaan(jadwal);
            }
        }
        return false;
    }
}
