package Model;

public class DetailPesanan {
    private int idDetail;
    private int idPesanan;
    private int idProduk;
    private int kuantitas;
    private double totalHarga;
    
    public DetailPesanan(int idDetail, int idPesanan, int idProduk, int kuantitas, double totalHarga) {
        this.idDetail = idDetail;
        this.idPesanan = idPesanan;
        this.idProduk = idProduk;
        this.kuantitas = kuantitas;
        this.totalHarga = totalHarga;
    }
    
    // Constructor tanpa ID untuk pembuatan detail pesanan baru
    public DetailPesanan(int idPesanan, int idProduk, int kuantitas, double totalHarga) {
        this.idPesanan = idPesanan;
        this.idProduk = idProduk;
        this.kuantitas = kuantitas;
        this.totalHarga = totalHarga;
    }
    
    public int getIdDetail() {
        return idDetail;
    }
    
    public void setIdDetail(int idDetail) {
        this.idDetail = idDetail;
    }
    
    public int getIdPesanan() {
        return idPesanan;
    }
    
    public void setIdPesanan(int idPesanan) {
        this.idPesanan = idPesanan;
    }
    
    public int getIdProduk() {
        return idProduk;
    }
    
    public void setIdProduk(int idProduk) {
        this.idProduk = idProduk;
    }
    
    public int getKuantitas() {
        return kuantitas;
    }
    
    public void setKuantitas(int kuantitas) {
        this.kuantitas = kuantitas;
    }
    
    public double getTotalHarga() {
        return totalHarga;
    }
    
    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }
    
    @Override
    public String toString() {
        return "Detail #" + idDetail + 
               " | Pesanan ID: " + idPesanan + 
               " | Produk ID: " + idProduk + 
               " | Kuantitas: " + kuantitas +
               " | Total Harga: " + totalHarga;
    }
}
