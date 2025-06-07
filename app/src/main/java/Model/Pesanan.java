package Model;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Pesanan {
    private int idPesanan;
    private String status; // selesai, proses, belum dibayar
    private Date tanggal;
    private int idPelanggan;
    

    public Pesanan(int idPesanan, String status, Date tanggal, int idPelanggan) {
        this.idPesanan = idPesanan;
        this.status = status;
        this.tanggal = tanggal;
        this.idPelanggan = idPelanggan;
    }
    
    // Constructor tanpa ID untuk pembuatan pesanan baru
    public Pesanan(String status, Date tanggal, int idPelanggan) {
        this.status = status;
        this.tanggal = tanggal;
        this.idPelanggan = idPelanggan;
    }    public int getIdPesanan() {
        return idPesanan;
    }
    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        // Validasi status
        if (status.equals("selesai") || status.equals("proses") || status.equals("belum dibayar")) {
            this.status = status;
        } else {
            throw new IllegalArgumentException("Status harus 'selesai', 'proses', atau 'belum dibayar'");
        }
    }
    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }
    
    public int getIdPelanggan() {
        return idPelanggan;
    }
    
    public void setIdPelanggan(int idPelanggan) {
        this.idPelanggan = idPelanggan;
    }
    
    // Method untuk mendapatkan format tanggal yang lebih rapi
    public String getFormattedTanggal() {
        if (tanggal == null) {
            return "Tanggal tidak tersedia";
        }
        
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatter.format(tanggal);
    }
    
    @Override
    public String toString() {
        return "Pesanan #" + idPesanan + 
               " | Tanggal: " + getFormattedTanggal() + 
               " | Status: " + status + 
               " | ID Pelanggan: " + idPelanggan;
    }
}