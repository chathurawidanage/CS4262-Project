package lk.ac.mrt.distributed;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.PriorityQueue;

/**
 * @author Chathura Widanage
 */
public class SearchNode extends PriorityQueue implements Runnable {
    private DatagramSocket socket;

    public SearchNode() throws SocketException {
        socket = new DatagramSocket(4444);
    }

    public void run() {
        byte buffer[];
        DatagramPacket datagramPacket;
        while (true) {
            buffer = new byte[65536];
            datagramPacket = new DatagramPacket(buffer, buffer.length);
            try {
                System.out.println("Waiting...");
                socket.receive(datagramPacket);
                System.out.println(new String(buffer).trim());
                System.out.println(datagramPacket.getAddress().toString() + ":" + datagramPacket.getPort());
                send(datagramPacket.getAddress(),
                        datagramPacket.getPort(), "GOT".getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receive(DatagramPacket datagramPacket) {

    }

    private void send(InetAddress inetAddress, int port, byte[] msg) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length);
        datagramPacket.setAddress(inetAddress);
        datagramPacket.setPort(port);
        socket.send(datagramPacket);
    }
}
