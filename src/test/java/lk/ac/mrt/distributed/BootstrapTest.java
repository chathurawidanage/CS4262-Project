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

        Thread.sleep(5000);
        System.out.println("Starting node 1");
        NodeOpsUDPImpl node1OpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        final SearchNode searchNode1 = new SearchNode("NODE1", "127.0.0.1", 44446, node1OpsUDP);
        searchNode1.bootstrap();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NodeGUIConsole nc1 = new NodeGUIConsole(searchNode1);
                nc1.display();
            }
        });



        Thread.sleep(5000);
        NodeOpsUDPImpl node2OpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        final SearchNode searchNode2 = new SearchNode("NODE2", "127.0.0.1", 44447, node2OpsUDP);
        searchNode2.bootstrap();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NodeGUIConsole nc1 = new NodeGUIConsole(searchNode2);
                nc1.display();
            }
        });

     /*   Thread.sleep(10000);
        NodeOpsUDPImpl node3OpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        final SearchNode searchNode3 = new SearchNode("NODE3", "127.0.0.1", 44448, node3OpsUDP);
        searchNode3.bootstrap();

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                NodeGUIConsole nc1 = new NodeGUIConsole(searchNode3);
                nc1.display();
            }
        });
*/
        Scanner sc = new Scanner(System.in);
        System.out.println("Press enter to exit");
        sc.nextByte();
    }
}