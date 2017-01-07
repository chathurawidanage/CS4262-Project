package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * @author Chathura Widanage
 */
public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) throws SocketException {
        NodeOpsUDPImpl nodeOpsUDP = new NodeOpsUDPImpl("127.0.0.1", 55555);
        try {
            SearchNode searchNode = new SearchNode("NODE1", "127.0.0.1", 44444, nodeOpsUDP);
            searchNode.bootstrap();
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
