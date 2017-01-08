package lk.ac.mrt.distributed;

import junit.framework.TestCase;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Chathura Widanage
 */
public class BootstrapTest extends TestCase {
    public void testTwoNodes() throws InterruptedException, UnknownHostException, RegistrationException, CommunicationException, SocketException, NullCommandListenerException, BootstrapException {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                BootstrapServer.start(55555);
            }
        })).start();

        Thread.sleep(200);
        System.out.println("Starting node 1");
        NodeOpsUDPImpl node1OpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        SearchNode searchNode1 = new SearchNode("NODE1", "127.0.0.1", 44446, node1OpsUDP);
        searchNode1.bootstrap();

        Thread.sleep(100);
        NodeOpsUDPImpl node2OpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        SearchNode searchNode2 = new SearchNode("NODE2", "127.0.0.1", 44447, node2OpsUDP);
        searchNode2.bootstrap();
    }
}