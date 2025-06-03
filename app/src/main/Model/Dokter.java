package main.Model;

public class Dokter {
    private String nama;
    private String spesialisasi;
    private int id;

    public Dokter(String nama, String spesialisasi, int id) {
        this.nama = nama;
        this.spesialisasi = spesialisasi;
        this.id = id;
        // Constructor for Dokter class
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getSpesialisasi() {
        return spesialisasi;
    }

    public void setSpesialisasi(String spesialisasi) {
        this.spesialisasi = spesialisasi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Dokter{" +
                "nama='" + nama + '\'' +
                ", spesialisasi='" + spesialisasi + '\'' +
                ", id=" + id +
                '}';
    }
}