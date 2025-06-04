// package main.Database;
// import main.Model.Pelanggan;
// import java.util.List;

// public class DatabaseExample {
//     public static void main(String[] args) {
//         // Membuat instance database SQLite
//         Database db = new Database();
        
//         // Contoh penggunaan database untuk Pelanggan dengan SQLite
//         System.out.println("=== SQLite Database Pelanggan Example ===\n");
        
//         // 1. Menambah pelanggan baru
//         System.out.println("1. Menambah pelanggan baru:");
//         Pelanggan pelanggan1 = new Pelanggan(0, "John Doe", "john.doe@email.com", "081234567890");
//         Pelanggan pelanggan2 = new Pelanggan(0, "Jane Smith", "jane.smith@email.com", "087654321098");
//         Pelanggan pelanggan3 = new Pelanggan(0, "Ahmad Hakim", "ahmad.hakim@email.com", "085678901234");
        
//         if (db.addPelanggan(pelanggan1)) {
//             System.out.println("✓ Pelanggan " + pelanggan1.getNama() + " berhasil ditambahkan dengan ID: " + pelanggan1.getId());
//         } else {
//             System.out.println("✗ Gagal menambahkan pelanggan " + pelanggan1.getNama());
//         }
        
//         if (db.addPelanggan(pelanggan2)) {
//             System.out.println("✓ Pelanggan " + pelanggan2.getNama() + " berhasil ditambahkan dengan ID: " + pelanggan2.getId());
//         } else {
//             System.out.println("✗ Gagal menambahkan pelanggan " + pelanggan2.getNama());
//         }
        
//         if (db.addPelanggan(pelanggan3)) {
//             System.out.println("✓ Pelanggan " + pelanggan3.getNama() + " berhasil ditambahkan dengan ID: " + pelanggan3.getId());
//         } else {
//             System.out.println("✗ Gagal menambahkan pelanggan " + pelanggan3.getNama());
//         }
        
//         // 2. Mengambil semua pelanggan
//         System.out.println("\n2. Daftar semua pelanggan:");
//         List<Pelanggan> allPelanggan = db.getAllPelanggan();
//         for (Pelanggan p : allPelanggan) {
//             System.out.println("- ID: " + p.getId() + ", Nama: " + p.getNama() + 
//                              ", Email: " + p.getEmail() + ", No HP: " + p.getnoHp());
//         }
        
//         // 3. Mengambil pelanggan berdasarkan ID
//         System.out.println("\n3. Mengambil pelanggan berdasarkan ID:");
//         if (!allPelanggan.isEmpty()) {
//             int firstId = allPelanggan.get(0).getId();
//             Pelanggan foundPelanggan = db.getPelangganById(firstId);
//             if (foundPelanggan != null) {
//                 System.out.println("Pelanggan ditemukan: " + foundPelanggan.getNama());
//             } else {
//                 System.out.println("Pelanggan tidak ditemukan");
//             }
//         }
        
//         // 4. Mencari pelanggan berdasarkan nama
//         System.out.println("\n4. Mencari pelanggan dengan nama mengandung 'John':");
//         List<Pelanggan> searchResult = db.searchPelangganByNama("John");
//         for (Pelanggan p : searchResult) {
//             System.out.println("- Ditemukan: " + p.getNama() + " (" + p.getEmail() + ")");
//         }
        
//         // 5. Update data pelanggan
//         System.out.println("\n5. Update data pelanggan:");
//         if (!allPelanggan.isEmpty()) {
//             Pelanggan toUpdate = allPelanggan.get(0);
//             toUpdate.setNama("John Doe Updated");
//             toUpdate.setnoHp("081111111111");
            
//             if (db.updatePelanggan(toUpdate)) {
//                 System.out.println("✓ Data pelanggan berhasil diupdate");
//             } else {
//                 System.out.println("✗ Gagal mengupdate data pelanggan");
//             }
//         }
        
//         // 6. Menghitung total pelanggan
//         System.out.println("\n6. Total pelanggan dalam database:");
//         int totalPelanggan = db.getTotalPelanggan();
//         System.out.println("Total: " + totalPelanggan + " pelanggan");
        
//         // 7. Mengambil pelanggan berdasarkan email
//         System.out.println("\n7. Mengambil pelanggan berdasarkan email:");
//         Pelanggan pelangganByEmail = db.getPelangganByEmail("jane.smith@email.com");
//         if (pelangganByEmail != null) {
//             System.out.println("Pelanggan ditemukan: " + pelangganByEmail.getNama());
//         } else {
//             System.out.println("Pelanggan dengan email tersebut tidak ditemukan");
//         }
        
//         // 8. Menghapus pelanggan (opsional - uncomment jika ingin mencoba)
//         /*
//         System.out.println("\n8. Menghapus pelanggan:");
//         if (!allPelanggan.isEmpty()) {
//             int idToDelete = allPelanggan.get(allPelanggan.size() - 1).getId();
//             if (db.deletePelanggan(idToDelete)) {
//                 System.out.println("✓ Pelanggan dengan ID " + idToDelete + " berhasil dihapus");
//             } else {
//                 System.out.println("✗ Gagal menghapus pelanggan dengan ID " + idToDelete);
//             }
//         }
//         */        
//         // Menutup koneksi database
//         System.out.println("\n=== Menutup koneksi SQLite database ===");
//         System.out.println("File database: user.db tersimpan di folder project");
//         db.closeConnection();
//     }
// }
