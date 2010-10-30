package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedSkyTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.SkyTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.SkyTransmissionDaoImpl;

public class SkyScheduleService extends AbstractScheduleService {

//    private SkyTransmissionDao dao = new CachedSkyTransmissionDao(new SkyTransmissionDaoImpl());
    private SkyTransmissionDao dao = new SkyTransmissionDaoImpl();

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(channel, currentDate);
        return returnedTransmissions;
    }

}
