package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedRaiTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.RaiTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.RaiTransmissionDaoImpl;

public class RaiScheduleService extends AbstractScheduleService {

    private RaiTransmissionDao dao = new CachedRaiTransmissionDao(new RaiTransmissionDaoImpl());

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(channel, currentDate);
        return returnedTransmissions;
    }
}
