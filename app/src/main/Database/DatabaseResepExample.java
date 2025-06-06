package Database;

import Model.Pelanggan;
import Model.Resep;
import java.util.Date;
import java.util.List;

public class DatabaseResepExample {
    public static void main(String[] args) {
        // Membuat instance database SQLite
        Database db = new Database();
        
        System.out.println("=== SQLite Database Pelanggan dan Resep Example ===\n");
        
        // 1. Tambah pelanggan terlebih dahulu
        System.out.println("1. Menambah pelanggan:");
        Pelanggan pelanggan1 = new Pelanggan(0, "Dr. Ahmad", "dr.ahmad@hospital.com", "081234567890");
        Pelanggan pelanggan2 = new Pelanggan(0, "Dr. Sari", "dr.sari@hospital.com", "087654321098");
        
        if (db.addPelanggan(pelanggan1)) {
            System.out.println("✓ Pelanggan " + pelanggan1.getNama() + " berhasil ditambahkan dengan ID: " + pelanggan1.getId());
        }
        
        if (db.addPelanggan(pelanggan2)) {
            System.out.println("✓ Pelanggan " + pelanggan2.getNama() + " berhasil ditambahkan dengan ID: " + pelanggan2.getId());
        }
        
        // 2. Tambah resep untuk pelanggan
        System.out.println("\n2. Menambah resep:");
        Resep resep1 = new Resep(0, "Paracetamol 500mg, 3x sehari setelah makan", new Date(), pelanggan1.getId());
        Resep resep2 = new Resep(0, "Amoxicillin 250mg, 2x sehari sebelum makan", new Date(), pelanggan1.getId());
        Resep resep3 = new Resep(0, "Vitamin C 1000mg, 1x sehari", new Date(), pelanggan2.getId());
        
        if (db.addResep(resep1)) {
            System.out.println("✓ Resep berhasil ditambahkan dengan ID: " + resep1.getIDResep());
        }
        
        if (db.addResep(resep2)) {
            System.out.println("✓ Resep berhasil ditambahkan dengan ID: " + resep2.getIDResep());
        }
        
        if (db.addResep(resep3)) {
            System.out.println("✓ Resep berhasil ditambahkan dengan ID: " + resep3.getIDResep());
        }
        
        // 3. Ambil semua resep
        System.out.println("\n3. Daftar semua resep:");
        List<Resep> allResep = db.getAllResep();
        for (Resep r : allResep) {
            System.out.println("- ID Resep: " + r.getIDResep() + 
                             ", Isi: " + r.getIsiResep() + 
                             ", Tanggal: " + r.getTanggal() + 
                             ", ID Pelanggan: " + r.getIdPelanggan());
        }
        
        // 4. Ambil resep berdasarkan pelanggan
        System.out.println("\n4. Resep untuk pelanggan ID " + pelanggan1.getId() + ":");
        List<Resep> resepPelanggan1 = db.getResepByPelangganId(pelanggan1.getId());
        for (Resep r : resepPelanggan1) {
            System.out.println("- " + r.ambilRiwayatResep());
        }
        
        // 5. Ambil resep dengan info pelanggan (JOIN)
        System.out.println("\n5. Resep dengan informasi pelanggan:");
        List<String> resepWithInfo = db.getResepWithPelangganInfo();
        for (String info : resepWithInfo) {
            System.out.println("- " + info);
        }
        
        // 6. Cari resep berdasarkan isi
        System.out.println("\n6. Mencari resep dengan kata 'Paracetamol':");
        List<Resep> searchResult = db.searchResepByIsi("Paracetamol");
        for (Resep r : searchResult) {
            System.out.println("- Ditemukan: " + r.getIsiResep());
        }
        
        // 7. Update resep
        System.out.println("\n7. Update resep:");
        if (!allResep.isEmpty()) {
            Resep toUpdate = allResep.get(0);
            toUpdate.setIsiResep("Paracetamol 500mg UPDATED - 3x sehari setelah makan");
            
            if (db.updateResep(toUpdate)) {
                System.out.println("✓ Resep berhasil diupdate");
            }
        }
        
        // 8. Statistik
        System.out.println("\n8. Statistik database:");
        System.out.println("Total pelanggan: " + db.getTotalPelanggan());
        System.out.println("Total resep: " + db.getTotalResep());
        
        // 9. Test foreign key constraint (hapus pelanggan yang memiliki resep)
        System.out.println("\n9. Test Foreign Key Constraint:");
        System.out.println("Mencoba hapus pelanggan yang memiliki resep...");
        if (db.deletePelanggan(pelanggan1.getId())) {
            System.out.println("✓ Pelanggan dan semua resepnya berhasil dihapus (CASCADE)");
            
            // Cek resep yang tersisa
            List<Resep> remainingResep = db.getAllResep();
            System.out.println("Resep yang tersisa: " + remainingResep.size());
        }
        
        // Tutup koneksi
        System.out.println("\n=== Menutup koneksi database ===");
        db.closeConnection();
    }
}
