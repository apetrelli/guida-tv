package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedDNERealTimeTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.DNERealTimeTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.DNERealTimeTransmissionDaoImpl;

public class DNERealTimeScheduleService extends AbstractScheduleService {

    private DNERealTimeTransmissionDao dao = new CachedDNERealTimeTransmissionDao(
            new DNERealTimeTransmissionDaoImpl());

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(channel, currentDate);
        return returnedTransmissions;
    }

}
