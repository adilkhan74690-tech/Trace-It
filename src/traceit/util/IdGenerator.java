package traceit.util;

import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {
    private static final AtomicInteger lostCounter = new AtomicInteger(1000);
    private static final AtomicInteger foundCounter = new AtomicInteger(1000);
    private static final AtomicInteger reportCounter = new AtomicInteger(1000);

    public static String generateLostItemId() {
        return "LST-" + lostCounter.incrementAndGet();
    }

    public static String generateFoundItemId() {
        return "FND-" + foundCounter.incrementAndGet();
    }

    public static String generateReportId() {
        return "REP-" + reportCounter.incrementAndGet();
    }

    public static void initializeCounters(int maxLost, int maxFound, int maxReport) {
        if (maxLost > 1000) lostCounter.set(maxLost);
        if (maxFound > 1000) foundCounter.set(maxFound);
        if (maxReport > 1000) reportCounter.set(maxReport);
    }
}
