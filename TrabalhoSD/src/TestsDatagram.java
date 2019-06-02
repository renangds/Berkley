import java.io.IOException;
import java.net.*;
import java.util.*;

public class TestsDatagram extends Thread {

    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    protected int idProcess;
    protected boolean isDaemon = true;
    protected Clock processTime;
    public static int id = 1;

    public TestsDatagram(String timeProcess, int delayTime){
        this.processTime = new Clock(timeProcess, delayTime, this.idProcess);
    }

    public void run(){
        try{
            this.idProcess = id;
            id++;

            new Thread(this.processTime).start();

            System.out.println("Cliente conectado com ID: " + this.idProcess);

            this.socket = new MulticastSocket(4446);
            DatagramPacket receiver = new DatagramPacket(this.buf, this.buf.length);
            InetAddress group = InetAddress.getByName("230.0.0.0");
            this.socket.joinGroup(group);

            while(true){
                System.out.println("Aguardando receber mensagem");
                this.socket.receive(receiver);

                System.out.println("Current time ID " + this.idProcess + ": " + this.processTime.getNodeTime().toString());

                String rect = new String(receiver.getData(), 0, receiver.getLength());
                System.out.println("Processo ID " + this.idProcess + " Recebeu requisição " + rect);

                if("0".equals(rect)){
                    MulticastPublisher publisher = new MulticastPublisher();
                    publisher.multicast(Integer.toString(this.idProcess));

                    while(true){
                        //Algoritmo de Bully
                        this.socket.setSoTimeout(2000);

                        try{
                            this.socket.receive(receiver);
                            String newMessage = new String(receiver.getData(), 0, receiver.getLength());
                            int idNetworkNode = Integer.parseInt(newMessage);

                            if(idNetworkNode == this.idProcess) continue;

                            if(idNetworkNode > this.idProcess){
                                this.isDaemon = false;
                            }

                        } catch(SocketTimeoutException e){

                            //Algoritmo de Berkley
                            if(this.isDaemon){
                                System.out.println("Process with ID " + this.idProcess + " is a leader!");

                                System.out.println("Leader current time ID "+ this.idProcess + ": " + this.processTime.getNodeTime().toString());

                                System.out.println("O valor a ser enviado é: " + this.processTime.getMinutes());

                                publisher.multicast("1 " + Integer.toString(this.processTime.getMinutes()));

                                List <Integer> times = new ArrayList<>();

                                List <String> messages = new ArrayList<>();

                                times.add(0);

                                while(true){
                                    this.socket.setSoTimeout(2000);

                                    try{
                                        socket.receive(receiver);
                                        String timeString = new String(receiver.getData(), 0, receiver.getLength());

                                        messages = Arrays.asList(timeString.split(" "));

                                        if(messages.get(0).trim().equals("1")) continue;

                                        int temp = Integer.parseInt(messages.get(1).toString().trim());

                                        times.add(temp);
                                    } catch(SocketTimeoutException r){
                                        int timeStamp = times.stream().mapToInt(p -> p/times.size()).sum();

                                        this.processTime.setCertainTime(timeStamp);

                                        publisher.multicast("1 " + Integer.toString(this.processTime.getMinutes()));

                                        System.out.println("Tempo do líder atualizado é: " + this.processTime.getNodeTime());

                                        socket.receive(receiver); //Artifício para receber a última mensagem

                                        this.socket.setSoTimeout(0);

                                        break;
                                    }
                                }

                            } else{
                                System.out.println("Slave current time id: " + this.idProcess + ": " + this.processTime.getNodeTime().toString());

                                List <String> messages = new ArrayList<>();

                                //Irá receber a mensagem do processo líder

                                System.out.println("Aguardando receber mensagem com os minutos do processo líder");

                                this.socket.receive(receiver);

                                String firstTimeDaemon = new String(receiver.getData(), 0, receiver.getLength());

                                messages = Arrays.asList(firstTimeDaemon.split(" "));

                                int diffTime = this.processTime.timeStamp(Integer.parseInt(messages.get(1)));

                                System.out.println("Enviado a diferença de tempo de " + diffTime + " ao líder");

                                publisher.multicast("0 " + Integer.toString(diffTime));

                                while(true){
                                    this.socket.setSoTimeout(4000);

                                    try{
                                        System.out.println("Aguardando receber mensagem do líder para acertar o tempo");
                                        this.socket.receive(receiver);

                                        String secondMessageDaemon = new String(receiver.getData(), 0, receiver.getLength());

                                        messages = Arrays.asList(secondMessageDaemon.split(" "));

                                        if(!messages.get(0).trim().equals("1")) continue;

                                        int timeStamp = this.processTime.timeStamp(Integer.parseInt(messages.get(1)))*-1;

                                        this.processTime.setCertainTime(timeStamp);

                                        System.out.println("Recebido " + timeStamp + " do processo lider para ajustar " +
                                                "relógio ID " + this.idProcess);

                                        System.out.println("Tempo do processo " + this.idProcess + " acertado para " +
                                                this.processTime.getNodeTime().toString());
                                    } catch(SocketTimeoutException r){

                                        break;
                                    }
                                }

                                this.socket.setSoTimeout(0);

                            }

                            break;
                        }
                    }

                    this.socket.setSoTimeout(0);
                } else{
                    break;
                }
            }

            System.out.println("Finalizado");
            this.socket.leaveGroup(group);
            this.socket.close();
        } catch(IOException e){

        }
    }
}
