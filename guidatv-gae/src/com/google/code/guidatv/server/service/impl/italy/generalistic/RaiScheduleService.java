package com.google.code.guidatv.server.service.impl.italy.generalistic;

import java.util.Date;
import java.util.List;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.CachedRaiTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.RaiTransmissionDao;
import com.google.code.guidatv.server.service.impl.italy.generalistic.dao.RaiTransmissionDaoImpl;

public class RaiScheduleService extends AbstractScheduleService {

    private RaiTransmissionDao dao = new CachedRaiTransmissionDao(new RaiTransmissionDaoImpl());

    protected void fillTransmissions(Channel channel, Date currentDate,
            Date start, Date end, List<Transmission> transmissions) {
        List<Transmission> returnedTransmissions = getTransmissionsByDate(
                channel, currentDate);
        if (returnedTransmissions != null) {
            for (Transmission transmission : returnedTransmissions) {
                if (transmission.getStart().compareTo(start) >= 0 && transmission.getStart().compareTo(end) < 0) {
                    transmissions.add(transmission);
                }
            }
        }
    }

    protected List<Transmission> getTransmissionsByDate(Channel channel,
            Date currentDate) {
        List<Transmission> returnedTransmissions = dao.getTransmissions(channel, currentDate);
        return returnedTransmissions;
    }
}
