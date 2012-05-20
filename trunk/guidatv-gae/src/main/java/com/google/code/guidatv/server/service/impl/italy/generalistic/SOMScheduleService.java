package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedSOMGialloTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.SOMGialloTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.SOMGialloTransmissionDaoImpl;

public class SOMScheduleService extends AbstractScheduleService {

    private SOMGialloTransmissionDao dao = new CachedSOMGialloTransmissionDao(new SOMGialloTransmissionDaoImpl());

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(channel, currentDate);
        return returnedTransmissions;
    }
}
