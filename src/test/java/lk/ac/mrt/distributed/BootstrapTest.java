package lk.ac.mrt.distributed;

import junit.framework.TestCase;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.console.NodeGUIConsole;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * @author Chathura Widanage
 */
public class BootstrapTest extends TestCase {
    public void testTwoNodes() throws InterruptedException, UnknownHostException, RegistrationException, CommunicationException, SocketException, NullCommandListenerException, BootstrapException {
        //start a Bootstrap Server
        (new Thread(new Runnable() {
            @Override
            public void run() {
                BootstrapServer.start(55555);
            }
        })).start();

        int nodecount = 5;
        int node_start_port = 44446;
        for (int i = 0; i < nodecount; i++) {
            System.out.println("Starting node 1");
            NodeOpsUDPImpl node1OpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
            final SearchNode searchNode = new SearchNode("NODE" + (i + 1), "127.0.0.1", node_start_port + i, node1OpsUDP);
            searchNode.bootstrap();

            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    NodeGUIConsole nc1 = new NodeGUIConsole(searchNode);
                    nc1.display();
                }
            });
            Thread.sleep(200);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Press enter to exit");
        sc.nextByte();
    }
}