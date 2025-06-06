package main.Model;

import java.util.Date;

public class Notifikasi {
    private int idNotif;
    private String isiPesan;
    private Date tanggal;
    private int idPelanggan;


    public Notifikasi(int idNotif, String isiPesan, Date tanggal, int idPelanggan) {
        this.idNotif = idNotif;
        this.isiPesan = isiPesan;
        this.tanggal = tanggal;
        this.idPelanggan = idPelanggan; 
    }

    public int getIdNotif() {
        return idNotif;
    }

    public void setIdNotif(int idNotif) {
        this.idNotif = idNotif;
    }

    public String getIsiPesan() {
        return isiPesan;
    }

    public void setIsiPesan(String isiPesan) {
        this.isiPesan = isiPesan;
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
}