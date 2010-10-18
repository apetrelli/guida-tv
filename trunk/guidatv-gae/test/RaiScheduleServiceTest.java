import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.google.code.guidatv.client.model.Channel;
import com.google.code.guidatv.client.model.ChannelEntry;
import com.google.code.guidatv.client.model.IntervalEntry;
import com.google.code.guidatv.client.model.Schedule;
import com.google.code.guidatv.client.model.ScheduleResume;
import com.google.code.guidatv.client.model.Transmission;
import com.google.code.guidatv.server.service.ScheduleService;
import com.google.code.guidatv.server.service.impl.ScheduleServiceImpl;


public class RaiScheduleServiceTest {

    @Test
    public void testGetSchedule() {
        ScheduleService service = new ScheduleServiceImpl();

        Channel rai1 = new Channel("RaiUno", "Rai 1", "generalistic", "it_IT", "Rai");
        Channel rai2 = new Channel("RaiDue", "Rai 2", "generalistic", "it_IT", "Rai");
        Channel rai3 = new Channel("RaiTre", "Rai 3", "generalistic", "it_IT", "Rai");
        Date today = new Date();
        today = new Date(today.getYear(), today.getMonth(), today.getDate());
        Schedule rai1Schedule = service.getSchedule(rai1, today);
        Schedule rai2Schedule = service.getSchedule(rai2, today);
        Schedule rai3Schedule = service.getSchedule(rai3, today);
        Date start = new Date(today.getTime());
        Date end = new Date(today.getTime() + 24*60*60*1000);
        ScheduleResume resume = new ScheduleResume(start, end, 30);
        resume.add(rai1Schedule);
        resume.add(rai2Schedule);
        resume.add(rai3Schedule);
        DateFormat format = new SimpleDateFormat("HH:mm");
        for (IntervalEntry entry: resume.getIntervals()) {
            System.out.println(format.format(entry.getStart()));
            displayChannel(entry, rai1, format);
            displayChannel(entry, rai2, format);
            displayChannel(entry, rai3, format);
        }
    }

    private void displayChannel(IntervalEntry entry, Channel channel, DateFormat format) {
        ChannelEntry channelEntry = entry.getEntry(channel);
        if (channelEntry != null) {
            System.out.println("  " + channel.getName());
            for (Transmission transmission: channelEntry.getTransmissions()) {
                System.out.println("    " + format.format(transmission.getStart()) + ": " + transmission.getName());
            }
        }
    }

}
