import java.util.TimeZone;

import org.junit.Test;

public class TimeZoneTest {

    @Test
    public void testTimeZone() {
        for (String zone: TimeZone.getAvailableIDs()) {
            System.out.println(zone);
        }
    }

}
