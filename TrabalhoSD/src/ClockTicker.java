import java.io.IOException;
import java.time.*;
import java.util.Objects;

public class ClockTicker extends Clock  {
    public static void main(String... args) throws InterruptedException {
        ClockTicker ticker = new ClockTicker();
        ticker.runTime();
    }

    public void runTime() throws InterruptedException {
        while(true){
            System.out.println("Fora da thread!");
            Thread.sleep(1000);
            log(this.instant());
        }
    }

    private static void log(Object msg){
        System.out.println(Objects.toString(msg).split("^[0-9]{2}[:][0-9]{2}[:][0-9]{2}")[0]);
    }

    @Override
    public ZoneId getZone(){
        return DEFAULT_TZONE;
    }

    @Override
    public Clock withZone(ZoneId zone){
        return Clock.fixed(WHEN_STARTED, zone);
    }

    @Override
    public Instant instant(){
        return nextInstant();
    }

    private final Instant WHEN_STARTED = Instant.now();
    private final ZoneId DEFAULT_TZONE = ZoneId.systemDefault();
    private long count = 0;

    private Instant nextInstant(){
        count++;
        return WHEN_STARTED.plusSeconds(count);
    }

}
