package Controller;

import Database.Database;
import Model.Dokter;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoctorCalendarController {
    private Database db;

    public DoctorCalendarController() {
        this.db = new Database();
    }

    // Method untuk mengambil semua dokter
    public List<Dokter> getAllDoctors() {
        return db.getAllDokter();
    }

    // Method untuk mengambil kalender dokter tertentu
    public Map<String, Object> getDoctorCalendar(int doctorId, LocalDate startDate, LocalDate endDate) {
        Date sqlStartDate = Date.valueOf(startDate);
        Date sqlEndDate = Date.valueOf(endDate);
        return db.getDoctorCalendar(doctorId, sqlStartDate, sqlEndDate);
    }

    // Method untuk mengambil slot waktu yang tersedia untuk dokter pada tanggal tertentu
    public List<Map<String, Object>> getAvailableTimeSlots(int doctorId, LocalDate date) {
        Date sqlDate = Date.valueOf(date);
        return db.getAvailableTimeSlotsForDoctor(doctorId, sqlDate);
    }

    // Method untuk booking appointment
    public boolean bookAppointment(int doctorId, LocalDate date, String time, int patientId) {
        Date sqlDate = Date.valueOf(date);
        return db.bookAppointmentWithDoctor(doctorId, sqlDate, time, patientId);
    }
    
    // Method untuk force refresh calendar data (untuk debugging)
    public Map<String, Object> forceRefreshDoctorCalendar(int doctorId, LocalDate date) {
        System.out.println("Force refreshing calendar for Doctor ID: " + doctorId + " on date: " + date);
        
        // Clear any potential cache by creating new database connection
        Database freshDb = new Database();
        Date sqlDate = Date.valueOf(date);
        
        // Get fresh data
        List<Map<String, Object>> freshTimeSlots = freshDb.getAvailableTimeSlotsForDoctor(doctorId, sqlDate);
        Dokter doctor = freshDb.getDokterById(doctorId);
        
        freshDb.closeConnection();
        
        if (doctor == null) {
            System.err.println("Doctor not found with ID: " + doctorId);
            return null;
        }
        
        Map<String, Object> calendarData = new HashMap<>();
        calendarData.put("doctorId", doctorId);
        calendarData.put("doctorName", doctor.getNama());
        calendarData.put("doctorSpecialization", doctor.getSpesialisasi());
        calendarData.put("selectedDate", date.toString());
        calendarData.put("timeSlots", freshTimeSlots);
        calendarData.put("refreshTimestamp", System.currentTimeMillis());
        
        long availableSlots = freshTimeSlots.stream().mapToLong(slot -> (Boolean) slot.get("available") ? 1 : 0).sum();
        calendarData.put("totalSlots", freshTimeSlots.size());
        calendarData.put("availableSlots", availableSlots);
        calendarData.put("bookedSlots", freshTimeSlots.size() - availableSlots);
        
        System.out.println("Refreshed calendar - Doctor: " + doctor.getNama() + ", Available: " + availableSlots + "/" + freshTimeSlots.size());
        
        return calendarData;
    }
    
    // Method untuk membandingkan calendar data dua dokter
    public Map<String, Object> compareTwoDoctorCalendars(int doctorId1, int doctorId2, LocalDate date) {
        Map<String, Object> comparison = new HashMap<>();
        
        Map<String, Object> doctor1Calendar = forceRefreshDoctorCalendar(doctorId1, date);
        Map<String, Object> doctor2Calendar = forceRefreshDoctorCalendar(doctorId2, date);
        
        comparison.put("doctor1", doctor1Calendar);
        comparison.put("doctor2", doctor2Calendar);
        comparison.put("comparisonDate", date.toString());
        
        return comparison;
    }
    
    // Method untuk mendapatkan calendar dengan logging untuk debugging
    public Map<String, Object> getPatientCalendarForDoctorWithLogging(int doctorId, LocalDate selectedDate) {
        System.out.println("=== GETTING CALENDAR FOR DOCTOR ===");
        System.out.println("Doctor ID: " + doctorId);
        System.out.println("Selected Date: " + selectedDate);
        
        Date sqlDate = Date.valueOf(selectedDate);
        
        // Get doctor info
        Dokter doctor = getDoctorById(doctorId);
        if (doctor == null) {
            System.err.println("ERROR: Doctor not found with ID: " + doctorId);
            return null;
        }
        
        System.out.println("Doctor Found: " + doctor.getNama() + " (" + doctor.getSpesialisasi() + ")");
        
        // Get available slots for this doctor on the selected date
        List<Map<String, Object>> timeSlots = db.getAvailableTimeSlotsForDoctor(doctorId, sqlDate);
        System.out.println("Time slots retrieved: " + timeSlots.size());
        
        // Log each time slot
        for (Map<String, Object> slot : timeSlots) {
            System.out.println("  - " + slot.get("time") + ": " + (Boolean) slot.get("available"));
        }
        
        // Create calendar response specifically for patient interface
        Map<String, Object> calendarData = new HashMap<>();
        calendarData.put("doctorId", doctorId);
        calendarData.put("doctorName", doctor.getNama());
        calendarData.put("doctorSpecialization", doctor.getSpesialisasi());
        calendarData.put("selectedDate", selectedDate.toString());
        calendarData.put("timeSlots", timeSlots);
        
        // Calculate availability statistics
        long availableSlots = timeSlots.stream().mapToLong(slot -> (Boolean) slot.get("available") ? 1 : 0).sum();
        calendarData.put("totalSlots", timeSlots.size());
        calendarData.put("availableSlots", availableSlots);
        calendarData.put("bookedSlots", timeSlots.size() - availableSlots);
        
        System.out.println("Calendar data created - Available: " + availableSlots + "/" + timeSlots.size());
        System.out.println("=== END CALENDAR RETRIEVAL ===\n");
        
        return calendarData;
    }
    
    // Method untuk mendapatkan calendar data khusus untuk interface pasien
    public Map<String, Object> getPatientCalendarForDoctor(int doctorId, LocalDate selectedDate) {
        Date sqlDate = Date.valueOf(selectedDate);
        
        // Get doctor info
        Dokter doctor = getDoctorById(doctorId);
        if (doctor == null) {
            return null;
        }
        
        // Get available slots for this doctor on the selected date
        List<Map<String, Object>> timeSlots = db.getAvailableTimeSlotsForDoctor(doctorId, sqlDate);
        
        // Create calendar response specifically for patient interface
        Map<String, Object> calendarData = new HashMap<>();
        calendarData.put("doctorId", doctorId);
        calendarData.put("doctorName", doctor.getNama());
        calendarData.put("doctorSpecialization", doctor.getSpesialisasi());
        calendarData.put("selectedDate", selectedDate.toString());
        calendarData.put("timeSlots", timeSlots);
        
        // Calculate availability statistics
        long availableSlots = timeSlots.stream().mapToLong(slot -> (Boolean) slot.get("available") ? 1 : 0).sum();
        calendarData.put("totalSlots", timeSlots.size());
        calendarData.put("availableSlots", availableSlots);
        calendarData.put("bookedSlots", timeSlots.size() - availableSlots);
        
        return calendarData;
    }
    
    // Method untuk mendapatkan available dates untuk doctor dalam range tertentu
    public List<Map<String, Object>> getAvailableDatesForDoctor(int doctorId, LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> availableDates = new ArrayList<>();
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            Date sqlDate = Date.valueOf(currentDate);
            List<Map<String, Object>> slots = db.getAvailableTimeSlotsForDoctor(doctorId, sqlDate);
            
            long availableCount = slots.stream().mapToLong(slot -> (Boolean) slot.get("available") ? 1 : 0).sum();
            
            Map<String, Object> dateInfo = new HashMap<>();
            dateInfo.put("date", currentDate.toString());
            dateInfo.put("dayOfWeek", currentDate.getDayOfWeek().toString());
            dateInfo.put("totalSlots", slots.size());
            dateInfo.put("availableSlots", availableCount);
            dateInfo.put("hasAvailability", availableCount > 0);
            dateInfo.put("doctorId", doctorId);
            
            availableDates.add(dateInfo);
            currentDate = currentDate.plusDays(1);
        }
        
        return availableDates;
    }
    
    // Method untuk switch doctor dan mendapatkan calendar baru
    public Map<String, Object> switchDoctorCalendar(int fromDoctorId, int toDoctorId, LocalDate selectedDate) {
        // Log the switch for debugging
        System.out.println("Switching calendar from Doctor ID " + fromDoctorId + " to Doctor ID " + toDoctorId);
        
        // Return new calendar data for the selected doctor
        return getPatientCalendarForDoctor(toDoctorId, selectedDate);
    }
    
    // Method untuk mendapatkan doctor comparison untuk tanggal tertentu
    public List<Map<String, Object>> compareDoctorsAvailability(LocalDate date) {
        List<Dokter> allDoctors = getAllDoctors();
        List<Map<String, Object>> comparison = new ArrayList<>();
        
        for (Dokter doctor : allDoctors) {
            Map<String, Object> doctorData = getPatientCalendarForDoctor(doctor.getId(), date);
            if (doctorData != null) {
                comparison.add(doctorData);
            }
        }
        
        return comparison;
    }
    
    // Method untuk validasi booking dengan doctor-specific rules
    public Map<String, Object> validateBookingForDoctor(int doctorId, LocalDate date, String time, int patientId) {
        Map<String, Object> validation = new HashMap<>();
        
        // Check if doctor exists
        Dokter doctor = getDoctorById(doctorId);
        if (doctor == null) {
            validation.put("valid", false);
            validation.put("message", "Doctor not found");
            return validation;
        }
        
        // Check if slot is available
        boolean isAvailable = isDoctorAvailable(doctorId, date, time);
        if (!isAvailable) {
            validation.put("valid", false);
            validation.put("message", "Selected time slot is not available for " + doctor.getNama());
            return validation;
        }
        
        // Check if patient already has appointment with this doctor on the same day
        List<Map<String, Object>> patientAppointments = db.getPatientAppointmentsWithDoctorInfo(patientId);
        for (Map<String, Object> appointment : patientAppointments) {
            String appointmentDate = (String) appointment.get("appointment_date");
            Integer appointmentDoctorId = (Integer) appointment.get("id_dokter");
            
            if (date.toString().equals(appointmentDate) && doctorId == appointmentDoctorId) {
                validation.put("valid", false);
                validation.put("message", "You already have an appointment with " + doctor.getNama() + " on this date");
                return validation;
            }
        }
        
        validation.put("valid", true);
        validation.put("message", "Booking validation successful");
        validation.put("doctorName", doctor.getNama());
        validation.put("doctorSpecialization", doctor.getSpesialisasi());
        
        return validation;
    }
    
    // Method untuk mendapatkan next available slot untuk doctor
    public Map<String, Object> getNextAvailableSlotForDoctor(int doctorId, LocalDate fromDate) {
        LocalDate checkDate = fromDate;
        LocalDate maxDate = fromDate.plusDays(30); // Check up to 30 days ahead
        
        while (!checkDate.isAfter(maxDate)) {
            List<Map<String, Object>> slots = getAvailableTimeSlots(doctorId, checkDate);
            
            for (Map<String, Object> slot : slots) {
                if ((Boolean) slot.get("available")) {
                    Map<String, Object> nextSlot = new HashMap<>();
                    nextSlot.put("doctorId", doctorId);
                    nextSlot.put("date", checkDate.toString());
                    nextSlot.put("time", slot.get("time"));
                    nextSlot.put("found", true);
                    return nextSlot;
                }
            }
            
            checkDate = checkDate.plusDays(1);
        }
        
        Map<String, Object> noSlot = new HashMap<>();
        noSlot.put("found", false);
        noSlot.put("message", "No available slots found in the next 30 days");
        return noSlot;
    }

    // Method untuk mengambil kalender dokter untuk minggu ini
    public Map<String, Object> getDoctorWeeklyCalendar(int doctorId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        
        return getDoctorCalendar(doctorId, startOfWeek, endOfWeek);
    }

    // Method untuk mengambil kalender dokter untuk bulan ini
    public Map<String, Object> getDoctorMonthlyCalendar(int doctorId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        
        return getDoctorCalendar(doctorId, startOfMonth, endOfMonth);
    }

    // Method untuk mengecek apakah dokter tersedia pada waktu tertentu
    public boolean isDoctorAvailable(int doctorId, LocalDate date, String time) {
        List<Map<String, Object>> slots = getAvailableTimeSlots(doctorId, date);
        
        for (Map<String, Object> slot : slots) {
            if (slot.get("time").equals(time) && (Boolean) slot.get("available")) {
                return true;
            }
        }
        
        return false;
    }

    // Method untuk mengambil slot waktu yang tersedia untuk hari ini
    public List<Map<String, Object>> getTodayAvailableSlots(int doctorId) {
        return getAvailableTimeSlots(doctorId, LocalDate.now());
    }

    // Method untuk mengambil ringkasan kalender semua dokter
    public List<Map<String, Object>> getAllDoctorsCalendarSummary(LocalDate date) {
        Date sqlDate = Date.valueOf(date);
        return db.getAllDoctorsCalendarSummary(sqlDate);
    }

    // Method untuk mengambil appointment pasien dengan info dokter
    public List<Map<String, Object>> getPatientAppointments(int patientId) {
        return db.getPatientAppointmentsWithDoctorInfo(patientId);
    }

    // Method untuk cancel appointment
    public boolean cancelAppointment(int appointmentId, int patientId) {
        return db.cancelAppointment(appointmentId, patientId);
    }

    // Method untuk mengambil informasi dokter berdasarkan ID
    public Dokter getDoctorById(int doctorId) {
        return db.getDokterById(doctorId);
    }

    public void closeConnection() {
        if (db != null) {
            db.closeConnection();
        }
    }
}
