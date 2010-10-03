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
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedRaiTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.RaiTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.RaiTransmissionDaoImpl;

public class RaiScheduleService implements ScheduleService {

    private RaiTransmissionDao dao = new CachedRaiTransmissionDao(new RaiTransmissionDaoImpl());
    private static final int ONE_DAY = 24 * 60 * 60 * 1000;

    @Override
    public Schedule getSchedule(Channel channel, Date day) {
        return getSchedule(channel, day, new Date(day.getTime() + ONE_DAY));
    }

    @Override
    public Schedule getSchedule(Channel channel, Date start, Date end) {
        Date daoStart = calculateDaoDate(start);
        Date daoEnd = calculateDaoDate(end);

        List<Transmission> transmissions = new ArrayList<Transmission>();
        Date currentDate = daoStart;
        while (currentDate.compareTo(daoEnd) <= 0) {
            List<Transmission> returnedTransmissions = dao.getTransmissions(channel, currentDate);
            if (returnedTransmissions != null) {
                for (Transmission transmission : returnedTransmissions) {
                    if (transmission.getStart().compareTo(start) >= 0 && transmission.getStart().compareTo(end) < 0) {
                        transmissions.add(transmission);
                    }
                }
            }
            currentDate = new Date(currentDate.getTime() + ONE_DAY);
        }

        return new Schedule(channel, transmissions);
    }

    private Date calculateDaoDate(Date start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Rome"));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        if (hour < 6) {
            cal.add(Calendar.DATE, -1);
        }
        return cal.getTime();
    }
}
