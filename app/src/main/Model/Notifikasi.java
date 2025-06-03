package main.Model;

public class Notifikasi {
    private int idNotif;
    private String isiPesan;
    private Date tanggal;


    public Notifikasi(int idNotif, String isiPesan, Date tanggal) {
        this.idNotif = idNotif;
        this.isiPesan = isiPesan;
        this.tanggal = tanggal;
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
}