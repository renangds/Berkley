import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

//Estrutura do cabeçalho: Operação, condição nó, pid, horário

public class Node extends Thread {
    private long pid;
    private MulticastPublisher publisher;
    private MulticastReceiver receiver;
    private Clock clockProcess;
    private boolean isDaemon;
    private MulticastSocket socket;

    //Types messages
    private static final String BULLY_CODE = "0";
    private static final String LEADER_SENDER = "1";
    private static final String SLAVE_TIMESTAMP_SENDER = "2";
    private static final String LEADER_TIMESTAMP_SENDER = "3";
    private static final String END_PROCESS = "99";

    public static void main(String...args){
        long pid = ProcessHandle.current().pid();

        System.out.println(pid);
    }

    public Node(String time, int delay) throws IOException{
        this.socket = new MulticastSocket(4446);
        this.pid = ProcessHandle.current().pid();
        this.publisher = new MulticastPublisher();
        this.receiver = new MulticastReceiver();
        this.isDaemon = true;
        this.clockProcess = new Clock(time, delay, this.pid);
    }

    public void run(){
        try{
            InetAddress group = InetAddress.getByName("230.0.0.0");
            this.socket.joinGroup(group);
        } catch(IOException e){
            System.out.println(e);
        }

        new Thread(this.clockProcess).start();

        System.out.println("Cliente ID: " + this.pid + " conectado com horário " + this.clockProcess);

        while(true){
            try{
                System.out.println("Aguardando receber comando");

                List<String> data = this.receiver.getData(this.socket);

                if(data.get(0).equals(Node.BULLY_CODE)){
                    System.out.println("O processo " + this.pid + " recebeu requisição para eleição");

                    try{
                        this.publisher.multicast("1 " + " 0 " + Long.toString(this.pid) + " " + this.clockProcess);

                        this.bully();
                    } catch(IOException e){
                        System.err.println(e);
                    }
                } else if(data.get(0).equals(Node.LEADER_SENDER)){
                    if(!this.isDaemon && data.get(1).equals("1")){
                        this.berkley_leader_receiver(data.get(3));
                    }
                } else if(data.get(0).equals(Node.SLAVE_TIMESTAMP_SENDER)){
                    if(this.isDaemon){
                        this.berkley_slave_sender(data.get(3));
                    }
                } else if(data.get(0).equals(Node.LEADER_TIMESTAMP_SENDER)){
                    if(!this.isDaemon){
                        this.berkley_leader_sender(data.get(3));
                    }
                } else if(data.get(0).equals(Node.END_PROCESS)){
                    this.leaveGroup();
                    break;
                }
            } catch(SocketTimeoutException e){
                break;
            } catch(IOException e){

            }
        }
    }

    public void bully() throws SocketException, IOException {
        while(true){
            this.socket.setSoTimeout(2000);

            try{
                List<String> data = this.receiver.getData(this.socket);

                if(data.size() < 3) continue;

                long idNodeProcess = Long.parseLong(data.get(3).trim());

                if(idNodeProcess > this.pid){
                    this.isDaemon = false;
                }
            } catch(SocketTimeoutException e){
                break;
            } catch(IOException e){

            }
        }

        if(this.isDaemon){
            System.out.println("Processo com PID " + this.pid + " é o líder!");
            this.publisher.multicast(Node.LEADER_SENDER + " 1 " + this.pid + " " + this.clockProcess.getMinutes());
        } else{
            System.out.println("Processo com PID" + this.pid + " não é o líder");
        }

        this.socket.setSoTimeout(0);
    }

    public void berkley_leader_receiver(String minutes) throws IOException, SocketTimeoutException{
        int difTime = this.clockProcess.timeStamp(Integer.parseInt(minutes));

        System.out.println("Enviando a diferença de " + difTime + " minutos ao líder!");

        this.publisher.multicast(Node.SLAVE_TIMESTAMP_SENDER + " 0 " + this.pid + " " + difTime);
    }

    public void berkley_slave_sender(String minutes) throws SocketException, SocketTimeoutException, IOException{
        List <Integer> times = new ArrayList<>();
        List <String> data = new ArrayList<>();

        times.add(0);
        times.add(Integer.parseInt(minutes));

        this.socket.setSoTimeout(2000);

        try{
            while(true){
                data = this.receiver.getData(this.socket);

                if(data.get(0).equals(Node.BULLY_CODE)){
                    this.bully();
                    break;
                }

                if(data.get(0).equals(Node.SLAVE_TIMESTAMP_SENDER)){
                    times.add(Integer.parseInt(data.get(3)));
                }
            }
        } catch(SocketTimeoutException e){
            int timeStamp = times.stream().mapToInt( p -> p/times.size()).sum();

            this.clockProcess.setCertainTime(timeStamp);

            this.publisher.multicast(Node.LEADER_TIMESTAMP_SENDER + " 1 " + this.pid + " " + this.clockProcess.getMinutes());

            System.out.println("O tempo do líder atualizado é: " + this.clockProcess.getNodeTime());
        }

        this.socket.setSoTimeout(0);
    }

    public void berkley_leader_sender(String time){
        int timeStamp = this.clockProcess.timeStamp(Integer.parseInt(time))*-1;

        System.out.println("Recebido do processo líder " + timeStamp + " para ajustar");

        this.clockProcess.setCertainTime(timeStamp);

        System.out.println("Tempo do processo " + this.pid + " ajustado para " + this.clockProcess.getNodeTime());
    }

    public void leaveGroup() throws SocketException, IOException {
        InetAddress group = InetAddress.getByName("230.0.0.0");
        this.socket.leaveGroup(group);
        this.socket.close();
    }
}
