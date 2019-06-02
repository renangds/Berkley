import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class MulticastPublisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public static void main(String...args) throws IOException{
        MulticastPublisher message = new MulticastPublisher();

        Scanner read = new Scanner(System.in);

        boolean quit = false;

        while(!quit){
            System.out.println("Digite o comando:\n 1. Adicionar nó \n 2. Berkley \n 3. Sair \n");
            int option = read.nextInt();

            if(option > 3 || option < 1){
                System.out.println("Comando inválido");
                continue;
            } else{
                switch(option){
                    case 1:

                        System.out.println("Criando um novo nó na rede distribuída");

                        System.out.println("Digite o tempo: ");

                        String time = read.next();

                        System.out.println("Digite o delay");

                        int delayTime = read.nextInt();

                        TestsDatagram novoNo = new TestsDatagram(time, delayTime);
                        new Thread(novoNo).start();
                        break;
                    case 2:
                        System.out.println("Algoritmo de Berkley...");
                        message.multicast("0");
                        break;
                    case 3:
                        System.out.println("Saindo...");
                        message.multicast("1");
                        quit = true;
                        break;
                }
            }
        }
    }

    public void multicast(String message) throws IOException {
        this.socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        this.buf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, group, 4446);
        socket.send(packet);

        socket.close();
    }
}
