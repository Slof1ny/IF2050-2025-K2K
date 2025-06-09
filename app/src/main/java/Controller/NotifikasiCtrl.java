package Controller;

import Database.Database;
import Model.Notifikasi;
import java.util.Date;

public class NotifikasiCtrl {
    private Database db = new Database();

    public boolean addNotifikasi(String isiPesan, Date tanggal, int idPelanggan) {
        if (isiPesan == null || tanggal == null || idPelanggan <= 0) {
            return false; 
        }
        Notifikasi notifikasi = new Notifikasi(0, isiPesan, tanggal, idPelanggan);
        return db.addNotifikasi(notifikasi);
    }
    public void sendNotifikasi(Notifikasi notifikasi) {
        if (notifikasi == null) {
            System.out.println("Notifikasi tidak valid");
            return;
        }
        System.out.println("Mengirim notifikasi: " + notifikasi.getIsiPesan() + " pada " + notifikasi.getFormattedTanggal()); //Ntar di tambahin buat di kirim ke pengguna (soon)
    }

}
