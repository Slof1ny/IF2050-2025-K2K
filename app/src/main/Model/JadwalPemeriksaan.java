package main.Model;

import java.util.Date;

public class JadwalPemeriksaan {
    private int IdJadwal;
    private Date TanggalWaktu; // Menggabungkan tanggal dan waktu dalam satu field
    private int IdPasien;
    private int IdDokter;

    public JadwalPemeriksaan(int idJadwal, Date tanggalWaktu, int idPasien, int idDokter) {
        this.IdJadwal = idJadwal;
        this.TanggalWaktu = tanggalWaktu;
        this.IdPasien = idPasien;
        this.IdDokter = idDokter;
    }

    public int getIdJadwal() {
        return IdJadwal;
    }

    public void setIdJadwal(int idJadwal) {
        this.IdJadwal = idJadwal;
    }

    public Date getTanggalWaktu() {
        return TanggalWaktu;
    }

    public void setTanggalWaktu(Date tanggalWaktu) {
        this.TanggalWaktu = tanggalWaktu;
    }

    public int getIdPasien() {
        return IdPasien;
    }    public void setIdPasien(int idPasien) {
        this.IdPasien = idPasien;
    }

    public int getIdDokter() {
        return IdDokter;
    }

    public void setIdDokter(int idDokter) {
        this.IdDokter = idDokter;
    }

    public String ambilRiwayatPemeriksaan() {
        return "ID Jadwal: " + IdJadwal + ", Tanggal & Waktu: " + TanggalWaktu.toString() + ", ID Pasien: " + IdPasien + ", ID Dokter: " + IdDokter;
    }
}
