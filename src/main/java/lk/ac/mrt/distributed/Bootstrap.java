package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.console.NodeGUIConsole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author Chathura Widanage
 */
public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);
    private static NodeGUIConsole consoleGUI;
    public static void main(String[] args) throws SocketException {
        NodeOpsUDPImpl nodeOpsUDP = new NodeOpsUDPImpl("192.168.43.138", 55555);
        try {
            final SearchNode searchNode = new SearchNode("chathura2", "192.168.43.138", 44442, nodeOpsUDP);
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
