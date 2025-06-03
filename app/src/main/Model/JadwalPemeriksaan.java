package main.Model;

import java.util.Date;
import java.util.Time;


public class JadwalPemeriksaan {
    private int IdJadwal;
    private Time Waktu;
    private Date Tanggal;
    private Pelanggan Pasien;

    public JadwalPemeriksaan(int idJadwal, Time waktu, Date tanggal, Pelanggan pasien) {
        this.IdJadwal = idJadwal;
        this.Waktu = waktu;
        this.Tanggal = tanggal;

    public int getIdJadwal() {
        return IdJadwal;
    }

    public void setIdJadwal(int idJadwal) {
        this.IdJadwal = idJadwal;
    }

    public Date getTanggal() {
        return Tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.Tanggal = tanggal;
    }

    public Time getWaktu() {
        return Waktu;
    }

    public void setWaktu(Time waktu) {
        this.Waktu = waktu;
    }

    public Pelanggan getPasien() {
        return Pasien;
    }

    public void setPasien(Pelanggan pasien) {
        this.Pasien = pasien;
    }

    public String ambilRiwayatPemeriksaan() {
        return "ID Jadwal: " + IdJadwal + ", Tanggal: " + Tanggal.toString() + ", Waktu: " + Waktu.toString() + ", Pasien: " + Pasien.getNama();
    }
}
