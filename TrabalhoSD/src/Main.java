import java.io.IOException;
import java.time.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
        System.out.println("Informar o horário");

        MulticastPublisher multicast = new MulticastPublisher();

        Scanner s = new Scanner(System.in);

        String horario = s.next();

        System.out.println("Informar o delay");

        int delay = s.nextInt();

        Node node = new Node(horario, delay);

        new Thread(node).start();

        boolean loop = true;

        while(loop){
            System.out.println("Escolha o comando: \n 1. Eleição \n 2. Sair \n");

            int escolha = s.nextInt();

            switch(escolha){
                case 1:
                    multicast.multicast("0");
                    break;

                case 2:
                    System.out.println("Saindo...");
                    multicast.multicast("99");
                    loop = false;
                    break;

                    default:
                        System.out.println("Comando inválido");
                        break;
            }
        }
    }
}
