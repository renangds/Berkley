import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.List;

public class MulticastReceiver {
    private DatagramPacket packet;
    private byte[] buffer;

    public MulticastReceiver(){
        this.buffer = new byte[512];
        this.packet = new DatagramPacket(this.buffer, this.buffer.length);
    }

    public List<String> getData(MulticastSocket socket) throws SocketTimeoutException, IOException {
        socket.receive(this.packet);

        String message = new String(this.packet.getData(), 0, this.packet.getLength());
        List <String> headerMessage = Arrays.asList(message.split(" "));

        headerMessage.stream().forEach(p -> p.trim());

        return headerMessage;
    }
}
