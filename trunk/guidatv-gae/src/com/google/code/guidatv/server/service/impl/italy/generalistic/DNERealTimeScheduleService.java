package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.DNERealTimeTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.DNERealTimeTransmissionDaoImpl;

public class DNERealTimeScheduleService extends AbstractScheduleService {

    private DNERealTimeTransmissionDao dao = new DNERealTimeTransmissionDaoImpl();

    public DNERealTimeScheduleService() {
        super(0, TimeZone.getTimeZone("Europe/Rome"));
    }

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(currentDate);
        return returnedTransmissions;
    }

}
