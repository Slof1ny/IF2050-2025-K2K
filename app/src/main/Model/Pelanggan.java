package Model;
public class Pelanggan {
    private int id;
    private String nama;
    private String email;
    private String noHp;
    private String password;

    public Pelanggan(int id, String nama, String email, String noHp, String password) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.noHp = noHp;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getnoHp() {
        return noHp;
    }

    public void setnoHp(String noHp) {
        this.noHp = noHp;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
}
