package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.NodeOps;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.Message;
import lk.ac.mrt.distributed.api.messages.requests.RegisterRequest;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import lk.ac.mrt.distributed.api.messages.responses.UnRegisterResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.CountDownLatch;

/**
 * UDP implementation of node operations
 *
 * @author Chathura Widanage
 */
public class NodeOpsUDPImpl extends NodeOps implements Runnable {
    private final Logger logger = LogManager.getLogger(NodeOpsUDPImpl.class);

    private String bootstrapServerIp;
    private int bootstrapServerPort;

    private DatagramSocket socket;

    //RequestResponse Latches
    private RequestResponseHolder registerRequestResponseHolder;

    public NodeOpsUDPImpl(String bootstrapServerIp, int bootstrapServerPort) {
        this.bootstrapServerIp = bootstrapServerIp;
        this.bootstrapServerPort = bootstrapServerPort;
    }

    @Override
    protected void bootstrap() throws BootstrapException {
        try {
            socket = new DatagramSocket(this.selfNode.getPort());
        } catch (SocketException e) {
            throw new BootstrapException(e);
        }
        new Thread(this).start();
    }

    @Override
    public RegisterResponse register() throws CommunicationException, RegistrationException {
        RegisterRequest registerRequest = RegisterRequest.generate(selfNode.getIp(), selfNode.getPort(), selfNode.getUsername());
        try {
            registerRequestResponseHolder = new RequestResponseHolder();
            registerRequestResponseHolder.request = registerRequest;
            send(bootstrapServerIp, bootstrapServerPort, registerRequest.getSendableString().getBytes());
            registerRequestResponseHolder.countDownLatch.await();

            RegisterResponse registerResponse = RegisterResponse.parse(registerRequestResponseHolder.response);
            return registerResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommunicationException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new CommunicationException(e);
        }
    }

    @Override
    public UnRegisterResponse unregister() {
        return null;
    }

    @Override
    public void join(Set<Node> neighbours) {

    }

    @Override
    public void leave(Set<Node> neighbours) {

    }

    @Override
    public void search(String fileName, Set<Node> neighbours) {

    }

    @Override
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

                received(datagramPacket);

                //sending ACK //todo implement
                /*send(datagramPacket.getAddress(),
                        datagramPacket.getPort(), "GOT".getBytes());*/

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void received(DatagramPacket datagramPacket) {
        String msg = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String command = stringTokenizer.nextToken();
        switch (command) {
            case "REGOK":
                //handle register response
                logger.info("Received REGOK with message '{}'", msg);
                if (this.registerRequestResponseHolder != null) {
                    registerRequestResponseHolder.response = msg;
                    registerRequestResponseHolder.countDownLatch.countDown();
                }
                break;
        }
    }

    private void send(String ip, int port, byte[] msg) throws IOException {
        send(InetAddress.getByName(ip), port, msg);
    }

    private void send(InetAddress inetAddress, int port, byte[] msg) throws IOException {
        DatagramPacket datagramPacket = new DatagramPacket(msg, msg.length);
        datagramPacket.setAddress(inetAddress);
        datagramPacket.setPort(port);
        socket.send(datagramPacket);
    }

    private class RequestResponseHolder {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Message request;
        String response;
        //todo add timeout to retry if response doesn't arrive within X seconds
    }
}
