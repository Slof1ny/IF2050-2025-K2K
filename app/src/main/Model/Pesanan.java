package main.Model;
import java.util.Date;
import Model.Produk;

public class Pesanan {
    private int idPesanan;
    private String status;
    private Date tanggal;
    private Produk[] produklist;
    

    public Pesanan(int idPesanan, String status, Date tanggal, Produk[] produklist) {
        this.idPesanan = idPesanan;
        this.status = status;
        this.tanggal = tanggal;
        this.produklist = produklist;
    }

    public int getIdPesanan() {
        return idPesanan;
    }
    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }
    public Produk[] getProduklist() {
        return produklist;
    }

    public void setProduklist(Produk[] produklist) {
        this.produklist = produklist;
    }
}