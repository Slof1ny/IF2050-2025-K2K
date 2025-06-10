package Controller;

import Model.Laporan;
import Model.Admin;
import Database.Database;
import java.util.Date;

public class LaporanCtrl {
    private Database db = new Database();
    
    public Laporan generateLaporan(String tipeLaporan, Date periodeMulai, Date periodeSelesai, Admin admin) {
        Date tanggalDibuat = new Date();
        Laporan laporan = new Laporan(0, tipeLaporan, periodeMulai, periodeSelesai, tanggalDibuat, "", admin);

        String kontenLaporan = laporan.ambilData(periodeMulai, periodeSelesai);
        laporan.setKontenLaporan(kontenLaporan);

        return db.addLaporan(laporan) ? laporan : null;
    }
}