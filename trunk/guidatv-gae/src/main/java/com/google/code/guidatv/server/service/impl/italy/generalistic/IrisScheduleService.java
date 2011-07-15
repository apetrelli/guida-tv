package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.model.Channel;
import com.google.code.guidatv.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedIrisTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.IrisTransmissionDao;

public class IrisScheduleService extends AbstractScheduleService {

    IrisTransmissionDao dao = new CachedIrisTransmissionDao();
    
    @Override
    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(currentDate);
        return returnedTransmissions;
    }
}
