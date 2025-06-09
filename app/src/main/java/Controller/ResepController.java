package Controller;

import Model.Resep;

public class ResepController {

    private StokController stokController;

    public ResepController() {
        this.stokController = new StokController();
    }

    public void prosesResep(Resep resep) {
        System.out.println("Memproses resep dengan ID: " + resep.getIDResep());

        String isiResep = resep.getIsiResep();
        String[] items = isiResep.split(";");

        for (String item : items) {
            String[] detailItem = item.split(":");
            if (detailItem.length == 2) {
                String namaProduk = detailItem[0].trim();
                int jumlah = Integer.parseInt(detailItem[1].trim());


                stokController.updateStok(namaProduk, jumlah);
            }
        }
        System.out.println("Proses resep selesai.");
    }

    public Resep getResepByPelanggan(int idPelanggan) {
        // nanti dibenerin
        System.out.println("Mengambil data resep untuk pelanggan dengan ID: " + idPelanggan);
        return null;
    }
}