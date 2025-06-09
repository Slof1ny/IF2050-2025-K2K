package Controller;
import java.sql.Date;

import Database.Database;
import Model.Pesanan;

public class PesananCtrl {
    private Database db = new Database();

    public boolean addPesanan(int idPelanggan, String status, Date tanggal) {
        Pesanan pesanan = new Pesanan(0, status, tanggal, idPelanggan);
        return db.addPesanan(pesanan);
    }
    public String lacakPesanan(int idPesanan) {
        Pesanan pesanan = db.getPesananById(idPesanan);
        if (pesanan != null) {
            return "ID Pesanan: " + pesanan.getIdPesanan() + ", Status: " + pesanan.getStatus() + ", Tanggal: " + pesanan.getFormattedTanggal();
        } else {
            return "Pesanan tidak ditemukan.";
        }
    }
}
