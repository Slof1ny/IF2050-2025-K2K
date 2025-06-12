package Model;

import java.sql.Date;
import java.sql.Time;

public class KetersediaanDokter {
    private int idKetersediaan;
    private int idDokter;
    private Date tanggal;
    private Time jamMulai;
    private Time jamSelesai;
    private String status; // "tersedia" atau "tidak_tersedia"
    private String keterangan;

    public KetersediaanDokter(int idKetersediaan, int idDokter, Date tanggal, Time jamMulai, Time jamSelesai, String status, String keterangan) {
        this.idKetersediaan = idKetersediaan;
        this.idDokter = idDokter;
        this.tanggal = tanggal;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.status = status;
        this.keterangan = keterangan;
    }

    // Getters and Setters
    public int getIdKetersediaan() {
        return idKetersediaan;
    }

    public void setIdKetersediaan(int idKetersediaan) {
        this.idKetersediaan = idKetersediaan;
    }

    public int getIdDokter() {
        return idDokter;
    }

    public void setIdDokter(int idDokter) {
        this.idDokter = idDokter;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public Time getJamMulai() {
        return jamMulai;
    }

    public void setJamMulai(Time jamMulai) {
        this.jamMulai = jamMulai;
    }

    public Time getJamSelesai() {
        return jamSelesai;
    }

    public void setJamSelesai(Time jamSelesai) {
        this.jamSelesai = jamSelesai;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public boolean isTersedia() {
        return "tersedia".equals(status);
    }

    public boolean isTidakTersedia() {
        return "tidak_tersedia".equals(status);
    }

    @Override
    public String toString() {
        return "KetersediaanDokter{" +
                "idKetersediaan=" + idKetersediaan +
                ", idDokter=" + idDokter +
                ", tanggal=" + tanggal +
                ", jamMulai=" + jamMulai +
                ", jamSelesai=" + jamSelesai +
                ", status='" + status + '\'' +
                ", keterangan='" + keterangan + '\'' +
                '}';
    }
}
