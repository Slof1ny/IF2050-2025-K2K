package Main;

import Database.Database;
import Model.Pelanggan;
import Model.Produk;
import Model.Pesanan;
import Model.DetailPesanan;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Program contoh untuk mendemonstrasikan sistem pesanan obat
 */
public class OrderSystemDemo {
    
    private static Database db;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        db = new Database();
        scanner = new Scanner(System.in);
        
        System.out.println("===== SISTEM PESANAN OBAT =====");
        
        boolean running = true;
        while(running) {
            System.out.println("\nMenu Utama:");
            System.out.println("1. Kelola Produk");
            System.out.println("2. Buat Pesanan Baru");
            System.out.println("3. Lihat Daftar Pesanan");
            System.out.println("4. Keluar");
            System.out.print("Pilihan Anda: ");
            
            int choice = getIntInput();
            
            switch(choice) {
                case 1:
                    manageProduk();
                    break;
                case 2:
                    createOrder();
                    break;
                case 3:
                    viewOrders();
                    break;
                case 4:
                    running = false;
                    System.out.println("Terima kasih telah menggunakan sistem kami.");
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
        
        scanner.close();
    }
    
    /**
     * Fungsi untuk mengelola produk
     */
    private static void manageProduk() {
        boolean back = false;
        
        while(!back) {
            System.out.println("\n----- KELOLA PRODUK -----");
            System.out.println("1. Lihat Semua Produk");
            System.out.println("2. Tambah Produk Baru");
            System.out.println("3. Perbarui Stok Produk");
            System.out.println("4. Cari Produk");
            System.out.println("5. Kembali ke Menu Utama");
            System.out.print("Pilihan Anda: ");
            
            int choice = getIntInput();
            
            switch(choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    addNewProduct();
                    break;
                case 3:
                    updateProductStock();
                    break;
                case 4:
                    searchProduct();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Pilihan tidak valid.");
            }
        }
    }
    
    /**
     * Menampilkan semua produk
     */
    private static void displayAllProducts() {
        List<Produk> produkList = db.getAllProduk();
        
        if (produkList.isEmpty()) {
            System.out.println("Tidak ada produk yang tersedia.");
            return;
        }
        
        System.out.println("\nDaftar Produk:");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("| %-3s | %-25s | %-10s | %-10s |\n", "ID", "Nama Produk", "Harga", "Stok");
        System.out.println("-----------------------------------------------------------------");
        
        for (Produk p : produkList) {
            System.out.printf("| %-3d | %-25s | Rp %-8.0f | %-10d |\n", 
                p.getId(), p.getNama(), p.getHarga(), p.getStok());
        }
        
        System.out.println("-----------------------------------------------------------------");
    }
    
    /**
     * Menambah produk baru
     */
    private static void addNewProduct() {
        System.out.println("\n----- TAMBAH PRODUK BARU -----");
        
        System.out.print("Nama Produk: ");
        String nama = scanner.nextLine();
        
        System.out.print("Harga (Rp): ");
        double harga = getDoubleInput();
        
        System.out.print("Stok: ");
        int stok = getIntInput();
        
        Produk newProduk = new Produk(0, nama, harga, stok);
        
        if (db.addProduk(newProduk)) {
            System.out.println("Produk berhasil ditambahkan dengan ID: " + newProduk.getId());
        } else {
            System.out.println("Gagal menambahkan produk.");
        }
    }
    
    /**
     * Memperbarui stok produk
     */
    private static void updateProductStock() {
        System.out.println("\n----- PERBARUI STOK PRODUK -----");
        
        System.out.print("Masukkan ID Produk: ");
        int id = getIntInput();
        
        Produk produk = db.getProdukById(id);
        
        if (produk == null) {
            System.out.println("Produk dengan ID " + id + " tidak ditemukan.");
            return;
        }
        
        System.out.println("Produk: " + produk.getNama());
        System.out.println("Stok saat ini: " + produk.getStok());
        
        System.out.print("Stok baru: ");
        int newStock = getIntInput();
        
        produk.setStok(newStock);
        
        if (db.updateProduk(produk)) {
            System.out.println("Stok produk berhasil diperbarui.");
        } else {
            System.out.println("Gagal memperbarui stok produk.");
        }
    }
    
    /**
     * Mencari produk berdasarkan nama
     */
    private static void searchProduct() {
        System.out.println("\n----- CARI PRODUK -----");
        
        System.out.print("Masukkan nama produk: ");
        String keyword = scanner.nextLine();
        
        List<Produk> results = db.searchProdukByNama(keyword);
        
        if (results.isEmpty()) {
            System.out.println("Tidak ada produk yang cocok dengan kata kunci '" + keyword + "'.");
            return;
        }
        
        System.out.println("\nHasil Pencarian:");
        System.out.println("-----------------------------------------------------------------");
        System.out.printf("| %-3s | %-25s | %-10s | %-10s |\n", "ID", "Nama Produk", "Harga", "Stok");
        System.out.println("-----------------------------------------------------------------");
        
        for (Produk p : results) {
            System.out.printf("| %-3d | %-25s | Rp %-8.0f | %-10d |\n", 
                p.getId(), p.getNama(), p.getHarga(), p.getStok());
        }
        
        System.out.println("-----------------------------------------------------------------");
    }
    
    /**
     * Membuat pesanan baru
     */
    private static void createOrder() {
        System.out.println("\n----- BUAT PESANAN BARU -----");
        
        // Pilih pelanggan
        Pelanggan pelanggan = selectCustomer();
        if (pelanggan == null) {
            System.out.println("Pembuatan pesanan dibatalkan.");
            return;
        }
        
        // Buat pesanan baru
        Pesanan pesanan = new Pesanan(0, "belum dibayar", new Date(), pelanggan.getId());
        
        if (!db.addPesanan(pesanan)) {
            System.out.println("Gagal membuat pesanan.");
            return;
        }
        
        System.out.println("Pesanan baru dibuat dengan ID: " + pesanan.getIdPesanan());
        
        // Tambahkan item ke pesanan
        boolean addingItems = true;
        
        while (addingItems) {
            displayAllProducts();
            
            System.out.print("\nMasukkan ID Produk (0 untuk selesai): ");
            int produkId = getIntInput();
            
            if (produkId == 0) {
                addingItems = false;
                continue;
            }
            
            Produk produk = db.getProdukById(produkId);
            
            if (produk == null) {
                System.out.println("Produk dengan ID " + produkId + " tidak ditemukan.");
                continue;
            }
            
            System.out.print("Jumlah: ");
            int quantity = getIntInput();
            
            if (quantity <= 0) {
                System.out.println("Jumlah harus lebih dari 0.");
                continue;
            }
            
            if (quantity > produk.getStok()) {
                System.out.println("Stok tidak mencukupi. Stok tersedia: " + produk.getStok());
                continue;
            }
            
            double totalHarga = quantity * produk.getHarga();
            
            DetailPesanan detail = new DetailPesanan(0, pesanan.getIdPesanan(), produk.getId(), quantity, totalHarga);
            
            if (db.addDetailPesanan(detail)) {
                System.out.println(quantity + " " + produk.getNama() + " ditambahkan ke pesanan.");
                
                // Update stok produk
                produk.setStok(produk.getStok() - quantity);
                db.updateProduk(produk);
            } else {
                System.out.println("Gagal menambahkan item ke pesanan.");
            }
            
            System.out.print("\nTambah item lain? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (!response.equals("y")) {
                addingItems = false;
            }
        }
        
        // Tampilkan ringkasan pesanan
        displayOrderSummary(pesanan.getIdPesanan());
        
        // Tanya apakah ingin mengubah status pesanan
        System.out.print("\nUbah status pesanan? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();
        
        if (response.equals("y")) {
            System.out.println("\nPilih status baru:");
            System.out.println("1. Proses");
            System.out.println("2. Selesai");
            System.out.print("Pilihan: ");
            
            int statusChoice = getIntInput();
            
            switch(statusChoice) {
                case 1:
                    pesanan.setStatus("proses");
                    break;
                case 2:
                    pesanan.setStatus("selesai");
                    break;
                default:
                    System.out.println("Status tidak valid. Status tetap: " + pesanan.getStatus());
                    return;
            }
            
            if (db.updatePesanan(pesanan)) {
                System.out.println("Status pesanan diubah menjadi: " + pesanan.getStatus());
            } else {
                System.out.println("Gagal mengubah status pesanan.");
            }
        }
    }
    
    /**
     * Memilih pelanggan untuk pesanan
     */
    private static Pelanggan selectCustomer() {
        List<Pelanggan> customers = db.getAllPelanggan();
        
        if (customers.isEmpty()) {
            System.out.println("Tidak ada pelanggan yang terdaftar. Silakan tambahkan pelanggan terlebih dahulu.");
            return null;
        }
        
        System.out.println("\nDaftar Pelanggan:");
        System.out.println("---------------------------------------------------");
        System.out.printf("| %-3s | %-20s | %-20s |\n", "ID", "Nama", "Email");
        System.out.println("---------------------------------------------------");
        
        for (Pelanggan p : customers) {
            System.out.printf("| %-3d | %-20s | %-20s |\n", 
                p.getId(), p.getNama(), p.getEmail());
        }
        
        System.out.println("---------------------------------------------------");
        
        System.out.print("Pilih ID Pelanggan: ");
        int customerId = getIntInput();
        
        for (Pelanggan p : customers) {
            if (p.getId() == customerId) {
                return p;
            }
        }
        
        System.out.println("Pelanggan dengan ID " + customerId + " tidak ditemukan.");
        return null;
    }
    
    /**
     * Melihat daftar pesanan
     */
    private static void viewOrders() {
        System.out.println("\n----- DAFTAR PESANAN -----");
        
        List<Pesanan> pesananList = db.getAllPesanan();
        
        if (pesananList.isEmpty()) {
            System.out.println("Tidak ada pesanan yang tersedia.");
            return;
        }
        
        System.out.println("\nDaftar Pesanan:");
        System.out.println("-------------------------------------------------------------");
        System.out.printf("| %-3s | %-12s | %-15s | %-12s |\n", "ID", "Tanggal", "Status", "ID Pelanggan");
        System.out.println("-------------------------------------------------------------");
        
        for (Pesanan p : pesananList) {
            System.out.printf("| %-3d | %-12s | %-15s | %-12d |\n", 
                p.getIdPesanan(), 
                String.format("%tF", p.getTanggal()), 
                p.getStatus(), 
                p.getIdPelanggan());
        }
        
        System.out.println("-------------------------------------------------------------");
        
        System.out.print("\nLihat detail pesanan? (Masukkan ID pesanan, 0 untuk kembali): ");
        int orderId = getIntInput();
        
        if (orderId > 0) {
            displayOrderSummary(orderId);
        }
    }
    
    /**
     * Menampilkan ringkasan pesanan
     */
    private static void displayOrderSummary(int orderId) {
        Pesanan pesanan = db.getPesananById(orderId);
        
        if (pesanan == null) {
            System.out.println("Pesanan dengan ID " + orderId + " tidak ditemukan.");
            return;
        }
        
        // Dapatkan data pelanggan
        Pelanggan pelanggan = db.getPelangganById(pesanan.getIdPelanggan());
        
        if (pelanggan == null) {
            System.out.println("Data pelanggan tidak ditemukan.");
            return;
        }
        
        // Dapatkan detail pesanan
        List<Map<String, Object>> details = db.getPesananLengkap(orderId);
        
        if (details.isEmpty()) {
            System.out.println("Pesanan tidak memiliki item.");
            return;
        }
        
        System.out.println("\n===== DETAIL PESANAN =====");
        System.out.println("ID Pesanan: " + pesanan.getIdPesanan());
        System.out.println("Tanggal: " + pesanan.getTanggal());
        System.out.println("Status: " + pesanan.getStatus());
        System.out.println("Pelanggan: " + pelanggan.getNama() + " (" + pelanggan.getEmail() + ")");
        
        System.out.println("\nItem Pesanan:");
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("| %-3s | %-25s | %-10s | %-6s | %-10s |\n", 
            "No", "Nama Produk", "Harga", "Jumlah", "Total");
        System.out.println("-----------------------------------------------------------------------");
        
        int itemNumber = 1;
        double grandTotal = 0;
        
        for (Map<String, Object> item : details) {
            String productName = (String) item.get("nama_produk");
            int quantity = (int) item.get("kuantitas");
            double price = (double) item.get("harga_produk");
            double total = (double) item.get("total_harga");
            
            System.out.printf("| %-3d | %-25s | Rp %-8.0f | %-6d | Rp %-8.0f |\n", 
                itemNumber++, productName, price, quantity, total);
            
            grandTotal += total;
        }
        
        System.out.println("-----------------------------------------------------------------------");
        System.out.printf("| %-49s | Rp %-8.0f |\n", "TOTAL", grandTotal);
        System.out.println("-----------------------------------------------------------------------");
    }
    
    /**
     * Utility method to get integer input
     */
    private static int getIntInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Masukkan angka yang valid: ");
            }
        }
    }
    
    /**
     * Utility method to get double input
     */
    private static double getDoubleInput() {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Masukkan angka yang valid: ");
            }
        }
    }
}
