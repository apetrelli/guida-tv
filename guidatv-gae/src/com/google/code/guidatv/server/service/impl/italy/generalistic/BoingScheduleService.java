package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Schedule;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.BoingTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.BoingTransmissionDaoImpl;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedBoingTransmissionDao;

public class BoingScheduleService extends AbstractScheduleService {

    private BoingTransmissionDao dao = new CachedBoingTransmissionDao(
            new BoingTransmissionDaoImpl());

    private ThreadLocal<Map<Date, List<Transmission>>> transmissionMapThreadLocal =
        new ThreadLocal<Map<Date, List<Transmission>>>();

    @Override
    public Schedule getSchedule(Channel channel, Date start, Date end) {
        transmissionMapThreadLocal.set(dao.getTransmissions());
        return super.getSchedule(channel, start, end);
    }

    @Override
    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        Map<Date, List<Transmission>> transmissionMap = transmissionMapThreadLocal
                .get();
        return transmissionMap.get(currentDate);
    }

}
