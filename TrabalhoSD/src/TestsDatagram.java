import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class TestsDatagram extends Thread {

    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    protected int idProcess;
    protected boolean isDaemon = false;
    public static int id = 1;

    public static void main(String...args) throws IOException, InterruptedException{
        TestsDatagram t = new TestsDatagram();
        new Thread(t).start();
    }

    public static List<InetAddress> listAllAddresses() throws SocketException {
        List <InetAddress> list = new ArrayList<>();

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

        while(interfaces.hasMoreElements()){
            NetworkInterface networkInterface = interfaces.nextElement();

            if(networkInterface.isLoopback() || !networkInterface.isUp()){
                continue;
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(list::add);
        }

        System.out.println(list.size());

        return list;
    }

    public void run() {
        try{
            this.idProcess = id;
            id++;
            System.out.println("Cliente conectado com ID: " + this.idProcess);
            this.socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            socket.joinGroup(group);
            System.out.println("Cliente conectado ao grupo " + group.getHostAddress());
            int global = 0;

            //Trecho do código onde será feita nova eleição na criação do processo
            MulticastPublisher sender = new MulticastPublisher();
            sender.multicast(Messages.ELEICAO.getValue());

            System.out.println("Aguardando receber mensagem dos processos");

            DatagramPacket packetEleicao = new DatagramPacket(buf, buf.length);
            socket.receive(packetEleicao);
            int valueIdProcess = Integer.parseInt(new String(packetEleicao.getData(), 0, packetEleicao.getLength()));

            System.out.println(valueIdProcess);

            if(valueIdProcess < this.idProcess){
                this.isDaemon = true;
                System.out.println("Processo ID " + Integer.toString(this.idProcess) + " é o novo líder eleito");
                //sender.multicast("Processo com Id 3 lider");
            } else{

            }

            //Fim trecho do código da eleição

            /*
            if(listAllAddresses().size() == 1){
                System.out.println("Estou sozinho. Meu ID é: " + this.idProcess);
            } else{
                System.out.println("Sou novo no grupo, faremos novas eleições. Meu ID: " + this.idProcess);
            }
             */

            while (true) {
                System.out.println("Aguardando receber a mensagem");
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                //global += Integer.parseInt(received);
                //Thread.sleep(10000);
                //System.out.println(global);

                if ("0".equals(received)) {
                    break;
                } else if("2".equals(received)){
                    sender.multicast(Integer.toString(this.idProcess));
                } else if ("4".equals(received)){

                }
            }

            System.out.println("Terminando nó...");
            socket.leaveGroup(group);
            socket.close();
        } catch(IOException e){

        } /*catch(InterruptedException e){

        }*/
    }
}
