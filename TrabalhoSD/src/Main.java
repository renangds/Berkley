import java.time.*;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) {
        Clock relogio = Clock.systemUTC();

        relogio.instant().plusSeconds(100000L);

        System.out.println(relogio.instant());
    }
}
