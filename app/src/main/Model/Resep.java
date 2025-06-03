package main.Model;

public class Resep {
    private int IDResep;
    private String IsiResepl;
    private Date Tanggal;
    private Pelanggan Pelanggan;

    public Recipe(int IDResep, String IsiResep, Date Tanggal, Pelanggan Pelanggan) {
        this.IDResep = IDResep;
        this.IsiResep = IsiResep;
        this.Tanggal = Tanggal;
        this.Pelanggan = Pelanggan;
    }

    // Getters dan Setters
    public int getIDResep() {return IDResep;}
    public String getIsiResep() {return IsiResep;}
    public Date getTanggal() {return Tanggal;}
    public Pelanggan getPelanggan() {return Pelanggan;}
    public void setIDResep(int IDResep) {this.IDResep = IDResep;}
    public void setIsiResep(String IsiResep) {this.IsiResep = IsiResep;}
    public void setTanggal(Date Tanggal) {this.Tanggal = Tanggal;}
    public void setPelanggan(Pelanggan Pelanggan) {this.Pelanggan = Pelanggan;}

    public String ambilRiwayatResep(){
        return "ID Resep: " + IDResep + ", Isi Resep: " + IsiResep + ", Tanggal: " + Tanggal.toString() + ", Pelanggan: " + Pelanggan.getNama();
    }
}
