import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MulticastPublisher {
    private DatagramSocket socket;
    private InetAddress group;
    private byte[] buf;

    public void multicast(String message) throws IOException {
        this.socket = new DatagramSocket();
        group = InetAddress.getByName("230.0.0.0");
        this.buf = message.getBytes();

        DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, group, 4446);
        socket.send(packet);

        socket.close();
    }
}
