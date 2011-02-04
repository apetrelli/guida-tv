package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedTelecomTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.TelecomTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.TelecomTransmissionDaoImpl;

public class TelecomScheduleService extends AbstractScheduleService {

    private TelecomTransmissionDao dao = new CachedTelecomTransmissionDao(new TelecomTransmissionDaoImpl());

    @Override
    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        return dao.getTransmissions(channel, currentDate);
    }

}
