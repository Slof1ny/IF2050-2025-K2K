package Controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class CalendarRefreshController {
    private DoctorCalendarController calendarController;
    private List<CalendarUpdateListener> listeners;
    private int lastSelectedDoctorId = -1;
    private LocalDate lastSelectedDate = LocalDate.now();

    public CalendarRefreshController() {
        this.calendarController = new DoctorCalendarController();
        this.listeners = new ArrayList<>();
    }

    // Interface untuk listener yang akan dipanggil ketika calendar update
    public interface CalendarUpdateListener {
        void onCalendarUpdated(Map<String, Object> calendarData);
        void onDoctorChanged(int newDoctorId, String doctorName);
        void onDateChanged(LocalDate newDate);
    }

    // Method untuk register listener
    public void addCalendarUpdateListener(CalendarUpdateListener listener) {
        listeners.add(listener);
    }

    // Method untuk remove listener
    public void removeCalendarUpdateListener(CalendarUpdateListener listener) {
        listeners.remove(listener);
    }

    // Method utama untuk update calendar ketika doctor berubah
    public Map<String, Object> updateCalendarForDoctor(int doctorId, LocalDate date) {
        System.out.println("CalendarRefreshController: Updating calendar for Doctor ID " + doctorId + " on " + date);
        
        // Update internal state
        boolean doctorChanged = (lastSelectedDoctorId != doctorId);
        boolean dateChanged = !lastSelectedDate.equals(date);
        
        lastSelectedDoctorId = doctorId;
        lastSelectedDate = date;
        
        // Get fresh calendar data
        Map<String, Object> calendarData = calendarController.getPatientCalendarForDoctorWithLogging(doctorId, date);
        
        if (calendarData != null) {
            // Add metadata untuk UI
            calendarData.put("doctorChanged", doctorChanged);
            calendarData.put("dateChanged", dateChanged);
            calendarData.put("updateTimestamp", System.currentTimeMillis());
            
            // Notify all listeners
            notifyCalendarUpdated(calendarData);
            
            if (doctorChanged) {
                String doctorName = (String) calendarData.get("doctorName");
                notifyDoctorChanged(doctorId, doctorName);
            }
            
            if (dateChanged) {
                notifyDateChanged(date);
            }
        }
        
        return calendarData;
    }

    // Method untuk force refresh calendar
    public Map<String, Object> forceRefreshCurrentCalendar() {
        if (lastSelectedDoctorId > 0) {
            System.out.println("Force refreshing calendar...");
            return calendarController.forceRefreshDoctorCalendar(lastSelectedDoctorId, lastSelectedDate);
        }
        return null;
    }

    // Method untuk mendapatkan calendar data saat ini
    public Map<String, Object> getCurrentCalendarData() {
        if (lastSelectedDoctorId > 0) {
            return calendarController.getPatientCalendarForDoctorWithLogging(lastSelectedDoctorId, lastSelectedDate);
        }
        return null;
    }

    // Method untuk reset selection
    public void resetSelection() {
        lastSelectedDoctorId = -1;
        lastSelectedDate = LocalDate.now();
        
        // Notify listeners about reset
        Map<String, Object> resetData = new HashMap<>();
        resetData.put("reset", true);
        resetData.put("message", "Calendar selection reset");
        notifyCalendarUpdated(resetData);
    }

    // Notification methods
    private void notifyCalendarUpdated(Map<String, Object> calendarData) {
        for (CalendarUpdateListener listener : listeners) {
            try {
                listener.onCalendarUpdated(calendarData);
            } catch (Exception e) {
                System.err.println("Error notifying calendar update listener: " + e.getMessage());
            }
        }
    }

    private void notifyDoctorChanged(int doctorId, String doctorName) {
        for (CalendarUpdateListener listener : listeners) {
            try {
                listener.onDoctorChanged(doctorId, doctorName);
            } catch (Exception e) {
                System.err.println("Error notifying doctor change listener: " + e.getMessage());
            }
        }
    }

    private void notifyDateChanged(LocalDate date) {
        for (CalendarUpdateListener listener : listeners) {
            try {
                listener.onDateChanged(date);
            } catch (Exception e) {
                System.err.println("Error notifying date change listener: " + e.getMessage());
            }
        }
    }

    // Getters
    public int getLastSelectedDoctorId() {
        return lastSelectedDoctorId;
    }

    public LocalDate getLastSelectedDate() {
        return lastSelectedDate;
    }

    public DoctorCalendarController getCalendarController() {
        return calendarController;
    }

    public void closeConnection() {
        if (calendarController != null) {
            calendarController.closeConnection();
        }
    }
}
