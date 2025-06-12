package Controller;

import Database.Database;
import Model.JadwalPemeriksaan;
import Model.Dokter;
import java.util.Date;
import java.util.List;
import java.sql.Time;
import java.util.Map;

public class JadwalPemeriksaanCtrl {
    private Database db = new Database();
    private KetersediaanDokterController ketersediaanController = new KetersediaanDokterController();
    
    public boolean addJadwal(int idDokter, Date tanggalWaktu) {
        JadwalPemeriksaan jadwal = new JadwalPemeriksaan(0, tanggalWaktu, 0, idDokter); // idPasien = 0 (NULL di DB)
        return db.addJadwalPemeriksaan(jadwal);
    }
    
    public boolean bookJadwal(int idDokter, Date tanggalWaktu, int idPasien) {
        // Ekstrak tanggal dan waktu dari Date
        java.sql.Date tanggal = new java.sql.Date(tanggalWaktu.getTime());
        java.sql.Time jam = new java.sql.Time(tanggalWaktu.getTime());
        java.sql.Time jamSelesai = new java.sql.Time(tanggalWaktu.getTime() + 3600000); // +1 jam
        
        // Cek apakah dokter tersedia pada waktu tersebut
        if (!ketersediaanController.isDokterTersedia(idDokter, tanggal, jam, jamSelesai)) {
            System.out.println("Dokter tidak tersedia pada waktu yang diminta.");
            return false;
        }
        
        for (JadwalPemeriksaan jadwal : db.getJadwalPemeriksaanByDokterId(idDokter)) {
            if (jadwal.getTanggalWaktu().equals(tanggalWaktu) && (jadwal.getIdPasien() == 0)) {
                jadwal.setIdPasien(idPasien);
                return db.updateJadwalPemeriksaan(jadwal);
            }
        }
        return false;
    }
    
    // Method untuk mengambil dokter yang tersedia pada waktu tertentu
    public List<Dokter> getDokterTersedia(Date tanggalWaktu) {
        java.sql.Date tanggal = new java.sql.Date(tanggalWaktu.getTime());
        java.sql.Time jamMulai = new java.sql.Time(tanggalWaktu.getTime());
        java.sql.Time jamSelesai = new java.sql.Time(tanggalWaktu.getTime() + 3600000); // +1 jam
        
        return ketersediaanController.getDokterTersedia(tanggal, jamMulai, jamSelesai);
    }
    
    // Method untuk booking appointment dengan dokter dan kalender spesifik
    public boolean bookAppointmentWithDoctorCalendar(int idDokter, Date tanggal, String waktu, int idPasien) {
        java.sql.Date sqlDate = new java.sql.Date(tanggal.getTime());
        return db.bookAppointmentWithDoctor(idDokter, sqlDate, waktu, idPasien);
    }
    
    // Method untuk mengambil kalender dokter
    public List<Map<String, Object>> getDoctorAvailableSlots(int idDokter, Date tanggal) {
        java.sql.Date sqlDate = new java.sql.Date(tanggal.getTime());
        return db.getAvailableTimeSlotsForDoctor(idDokter, sqlDate);
    }
    
    // Method untuk mengambil semua dokter
    public List<Dokter> getAllDoctors() {
        return db.getAllDokter();
    }
    
    // Method untuk mengambil appointment pasien dengan info dokter
    public List<Map<String, Object>> getPatientAppointmentsWithDoctorInfo(int idPasien) {
        return db.getPatientAppointmentsWithDoctorInfo(idPasien);
    }
    
    // Method untuk cancel appointment
    public boolean cancelPatientAppointment(int idJadwal, int idPasien) {
        return db.cancelAppointment(idJadwal, idPasien);
    }
    
    // Method untuk menutup koneksi database
    public void closeConnection() {
        if (db != null) {
            db.closeConnection();
        }
        if (ketersediaanController != null) {
            ketersediaanController.closeConnection();
        }
    }
}
