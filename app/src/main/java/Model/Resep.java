package Model;

import java.util.Date;

public class Resep {
    private int IDResep;
    private String IsiResep;
    private Date Tanggal;
    private int idPelanggan;
    
    public Resep(int IDResep, String IsiResep, Date Tanggal, int idPelanggan) {
        this.IDResep = IDResep;
        this.IsiResep = IsiResep;
        this.Tanggal = Tanggal;
        this.idPelanggan = idPelanggan;
    }

    // Getters dan Setters
    public int getIDResep() {return IDResep;}
    public String getIsiResep() {return IsiResep;}
    public Date getTanggal() {return Tanggal;}
    public int getIdPelanggan() {return idPelanggan;}
    public void setIDResep(int IDResep) {this.IDResep = IDResep;}
    public void setIsiResep(String IsiResep) {this.IsiResep = IsiResep;}
    public void setTanggal(Date Tanggal) {this.Tanggal = Tanggal;}
    public void setIdPelanggan(int idPelanggan) {this.idPelanggan = idPelanggan;}

    public String ambilRiwayatResep(){
        return "ID Resep: " + IDResep + ", Isi Resep: " + IsiResep + ", Tanggal: " + Tanggal.toString() + ", ID_Pelanggan: " + idPelanggan;
    }
}
