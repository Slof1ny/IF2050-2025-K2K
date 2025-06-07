package Model;

public class Dokter {
    private String nama;
    private String spesialisasi;
    private int id;
    private String password;

    public Dokter(String nama, String spesialisasi, int id, String password) {
        this.nama = nama;
        this.spesialisasi = spesialisasi;
        this.id = id;
        this.password = password;
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
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean login(String id, String password) {
        return String.valueOf(this.id).equals(id) && this.password.equals(password);
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