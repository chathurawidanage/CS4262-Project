package lk.ac.mrt.distributed;

import junit.framework.Test;
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
 * Created by wik2kassa on 1/9/2017.
 */
public class RealNetworkTest extends TestCase{
    public void realTest() throws InterruptedException, UnknownHostException, RegistrationException, CommunicationException, SocketException, NullCommandListenerException, BootstrapException {
        String bootstrapServerIp = "192.168.43.138";
        //start a Bootstrap Server

        int nodecount = 1;
        int node_start_port = 44446;
        for (int i = 0; i < nodecount; i++) {
            System.out.println("Starting node 1");
            NodeOpsUDPImpl node1OpsUDP = new NodeOpsUDPImpl(bootstrapServerIp, 55555);
            final SearchNode searchNode = new SearchNode("YASIRU" + (i + 1), "192.168.43.74", node_start_port + i, node1OpsUDP);
            searchNode.bootstrap();

            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    NodeGUIConsole nc1 = new NodeGUIConsole(searchNode);
                    nc1.start();
                }
            });
            Thread.sleep(200);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Press enter to exit");
        sc.nextByte();
    }
}
