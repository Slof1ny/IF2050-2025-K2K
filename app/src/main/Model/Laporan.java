package main.Model;

import java.util.Date;

public class Laporan {
    private int idLaporan;
    private String tipeLaporan;
    private Date periodeMulai;
    private Date periodeSelesai;
    private Date tanggalDibuat;
    private String kontenLaporan;
    private int dibuatOleh;

    public Laporan(int idLaporan, String tipeLaporan, Date periodeMulai, Date periodeSelesai, Date tanggalDibuat, String kontenLaporan, int dibuatOleh) {
        this.idLaporan = idLaporan;
        this.tipeLaporan = tipeLaporan;
        this.periodeMulai = periodeMulai;
        this.periodeSelesai = periodeSelesai;
        this.tanggalDibuat = tanggalDibuat;
        this.kontenLaporan = kontenLaporan;
        this.dibuatOleh = dibuatOleh;
    }

    public int getIdLaporan() {
        return idLaporan;
    }

    public void setIdLaporan(int idLaporan) {
        this.idLaporan = idLaporan;
    }

    public String getTipeLaporan() {
        return tipeLaporan;
    }

    public void setTipeLaporan(String tipeLaporan) {
        this.tipeLaporan = tipeLaporan;
    }

    public Date getPeriodeMulai() {
        return periodeMulai;
    }

    public void setPeriodeMulai(Date periodeMulai) {
        this.periodeMulai = periodeMulai;
    }

    public Date getPeriodeSelesai() {
        return periodeSelesai;
    }

    public void setPeriodeSelesai(Date periodeSelesai) {
        this.periodeSelesai = periodeSelesai;
    }

    public Date getTanggalDibuat() {
        return tanggalDibuat;
    }

    public void setTanggalDibuat(Date tanggalDibuat) {
        this.tanggalDibuat = tanggalDibuat;
    }

    public String getKontenLaporan() {
        return kontenLaporan;
    }

    public void setKontenLaporan(String kontenLaporan) {
        this.kontenLaporan = kontenLaporan;
    }

    public int getDibuatOleh() {
        return dibuatOleh;
    }

    public void setDibuatOleh(int dibuatOleh) {
        this.dibuatOleh = dibuatOleh;
    }
    
    public String ambilData(Date periodeMulai, Date periodeSelesai) {
        return "Data laporan dari " + periodeMulai + " sampai " + periodeSelesai;
    }
}