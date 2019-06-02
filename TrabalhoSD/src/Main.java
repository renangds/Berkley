import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Main {

    public static void main(String[] args) {
        String cabecalho1 = "1 45640";
        String cabecalho2 = "2 4545";
        String cabecalho3 = "3 222";

        List <String> requisicao = Arrays.asList(cabecalho1.split(" "));

        requisicao.forEach(System.out::println);
    }
}
