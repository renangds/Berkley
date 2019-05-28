import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class Teste1 {
    public static void main(String[] args) throws Exception {

        final InetAddress group = InetAddress.getByName("237.0.0.1");
        final int port = 9000;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MulticastSocket socket = new MulticastSocket(port);
                    socket.setInterface(InetAddress.getLocalHost());
                    socket.joinGroup(group);

                    DatagramPacket packet = new DatagramPacket(new byte[100], 100);
                    while(true) {
                        socket.receive(packet);
                        System.out.println("Got packet " +
                                Arrays.toString(packet.getData()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MulticastSocket socket = new MulticastSocket(port);
                    socket.setInterface(InetAddress.getLocalHost());
                    socket.joinGroup(group);

                    byte[] bt = new byte[100];
                    byte index = 0;
                    while(true) {
                        Arrays.fill(bt, (byte) index++);
                        socket.send(new DatagramPacket(bt, 100, group, port));
                        System.out.println("sent 100 bytes");
                        Thread.sleep(1*1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
