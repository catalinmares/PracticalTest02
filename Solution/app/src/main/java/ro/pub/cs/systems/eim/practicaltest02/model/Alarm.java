package ro.pub.cs.systems.eim.practicaltest02.model;

public class Alarm {
    private String hour;
    private String minute;
    private boolean isActive;

    public Alarm(String hour, String minute, boolean isActive) {
        this.hour = hour;
        this.minute = minute;
        this.isActive = isActive;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
