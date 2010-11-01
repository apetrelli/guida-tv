package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedSkyTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.SkyTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.SkyTransmissionDaoImpl;

public class SkyScheduleService extends AbstractScheduleService {

    private SkyTransmissionDao dao = new CachedSkyTransmissionDao(
            new SkyTransmissionDaoImpl());

    public SkyScheduleService() {
        super(0, TimeZone.getTimeZone("Europe/Rome"));
    }

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(
                channel, currentDate);
        return returnedTransmissions;
    }

}
