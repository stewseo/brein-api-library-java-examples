package org.example.business;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(value={ "_open_now" }, allowGetters=true)
public class Hours {
    public List<Open> open;
    public String hours_type;
    public Boolean is_open_now;

    public Hours(){}

    public List<Open> open() {
        return open;
    }

    public String hours_type() {
        return hours_type;
    }

    public Boolean is_open_now() {
        return is_open_now;
    }

    public void setOpen(List<Open> open) {
        this.open = open;
    }

    public void setHours_type(String hours_type) {
        this.hours_type = hours_type;
    }

    public void setIs_open_now(Boolean is_open_now) {
        this.is_open_now = is_open_now;
    }

    public static class Open{
        public Boolean is_overnight;
        public String start;
        public String end;
        public Integer day;

        public Open(){}

        public Boolean getIs_overnight() {
            return is_overnight;
        }

        public void setIs_overnight(Boolean is_overnight) {
            this.is_overnight = is_overnight;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }
    }
}
