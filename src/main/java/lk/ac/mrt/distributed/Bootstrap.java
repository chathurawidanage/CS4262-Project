package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.console.NodeGUIConsole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Chathura Widanage
 */
public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);
    private static NodeGUIConsole consoleGUI;
    public static void main(String[] args) throws SocketException {
        //NodeOpsUDPImpl nodeOpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        if(args.length != 4)
            throw new IllegalArgumentException("Invalid number of arguments. " +
                    "There should be 4 arguments: bs-server-ip, bs-server-port, client-username, client-port");

        String bsServerIp = args[0];
        int bsServerPort = Integer.parseInt(args[1]);
        String username = args[2];
        int myPort = Integer.parseInt(args[3]);

        NodeOpsRMIImpl nodeOps = new NodeOpsRMIImpl(bsServerIp, bsServerPort);

        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            logger.info("Obtained host IP: " + ip);
            final SearchNode searchNode = new SearchNode(username, ip, myPort, nodeOps);
            searchNode.bootstrap();
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    consoleGUI = new NodeGUIConsole(searchNode);
                    consoleGUI.display();
                }
            });
        } catch (NullCommandListenerException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (BootstrapException e) {
            e.printStackTrace();
        } catch (CommunicationException e) {
            e.printStackTrace();
        } catch (RegistrationException e) {
            logger.error("Bootstrapping failed", e);
        }
    }
}
