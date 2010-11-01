package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Schedule;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.ScheduleService;

public abstract class AbstractScheduleService implements ScheduleService {

    protected static final int ONE_DAY = 24 * 60 * 60 * 1000;
    
    private int dayStartOffsetMinutes;
    
    private TimeZone timeZone;
    
    public AbstractScheduleService() {
        this(6*60, TimeZone.getTimeZone("Europe/Rome"));
    }
    

    public AbstractScheduleService(int dayStartOffsetMinutes, TimeZone timeZone) {
        this.dayStartOffsetMinutes = dayStartOffsetMinutes;
        this.timeZone = timeZone;
    }
    
    @Override
    public Schedule getSchedule(Channel channel, Date day) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(day);
        cal.add(Calendar.DATE, 1);
        cal.add(Calendar.MILLISECOND, -1); // The last millisecond of the day after.
        return getSchedule(channel, day, cal.getTime());
    }
    
    public Schedule getSchedule(Channel channel, Date start, Date end) {
        Date daoStart = calculateDaoDateStart(start);
        Date daoEnd = calculateDaoDateEnd(end);

        List<Transmission> transmissions = new ArrayList<Transmission>();
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTime(daoStart);
        Calendar endCal = Calendar.getInstance(timeZone);
        endCal.setTime(daoEnd);
        while (cal.compareTo(endCal) <= 0) {
            fillTransmissions(channel, cal.getTime(), start, end, transmissions);
            cal.add(Calendar.DATE, 1);
        }

        return new Schedule(channel, transmissions);
    }

    protected void fillTransmissions(Channel channel, Date currentDate,
            Date start, Date end, List<Transmission> transmissions) {
        List<Transmission> returnedTransmissions = getTransmissionsByDate(
                channel, currentDate);
        if (returnedTransmissions != null) {
            for (Transmission transmission : returnedTransmissions) {
                if (transmission.getStart().compareTo(start) >= 0 && transmission.getStart().compareTo(end) <= 0) {
                    transmissions.add(transmission);
                }
            }
        }
    }
    
    protected abstract List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate);
    
    private Date calculateDaoDateStart(Date start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.setTimeZone(timeZone);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (dayStartOffsetMinutes > 0 && (hour*60 + minute) < dayStartOffsetMinutes) {
            cal.add(Calendar.DATE, -1);
        }
        return cal.getTime();
    }
    
    private Date calculateDaoDateEnd(Date start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.setTimeZone(timeZone);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}