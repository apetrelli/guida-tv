package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Schedule;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedMediasetBaseTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.MediasetBaseTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.MediasetBaseTransmissionDaoImpl;

public class MediasetBaseScheduleService extends AbstractScheduleService {

    private MediasetBaseTransmissionDao dao = new CachedMediasetBaseTransmissionDao(
            new MediasetBaseTransmissionDaoImpl());

    private ThreadLocal<Map<Date, Map<String, List<Transmission>>>> transmissionMapThreadLocal =
        new ThreadLocal<Map<Date, Map<String, List<Transmission>>>>();

    @Override
    public Schedule getSchedule(Channel channel, Date start, Date end) {
        transmissionMapThreadLocal.set(dao.getTransmissions());
        return super.getSchedule(channel, start, end);
    }

    @Override
    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        Map<Date, Map<String, List<Transmission>>> transmissionMap = transmissionMapThreadLocal
                .get();
        Map<String, List<Transmission>> channel2transmissions = transmissionMap
                .get(currentDate);
        if (channel2transmissions != null) {
            return channel2transmissions.get(channel.getCode());
        }
        return null;
    }

}
