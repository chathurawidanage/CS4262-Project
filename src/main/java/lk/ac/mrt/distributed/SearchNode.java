package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.CommandListener;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.NodeOps;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.requests.JoinRequest;
import lk.ac.mrt.distributed.api.messages.requests.LeaveRequest;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.*;
import java.util.*;

/**
 * @author Chathura Widanage
 */
public class SearchNode extends Node implements CommandListener {
    private final static Logger logger = LogManager.getLogger(SearchNode.class);

    private Set<Node> neighbours;
    private Map<String, Node> masters;//Some one else is master for these words
    private Map<String, List<Node>> resourceProviders;//I am master for these words

    private NodeOps nodeOps;

    public SearchNode(String username, String myIp, int myPort, NodeOps nodeOps) throws SocketException, NullCommandListenerException, BootstrapException {
        super(myIp, myPort);
        this.setUsername(username);
        this.neighbours = new HashSet<>();
        this.masters = new HashMap<>();
        this.resourceProviders = new HashMap<>();

        this.nodeOps = nodeOps;
        this.nodeOps.setCommandListener(this);
        this.nodeOps.start(this);
    }


    public void bootstrap() throws SocketException, UnknownHostException, CommunicationException, RegistrationException {
        //register node
        RegisterResponse registerResponse = nodeOps.register();
        this.neighbours.addAll(registerResponse.getNodes());
        if (!this.neighbours.isEmpty()) {//let others know I am here
            this.nodeOps.join(this.neighbours);
        }

        //our architecture specific stuff
    }


    @Override
    public void onSearchRequest(Node node, String keyword) {

    }

    @Override
    public int onLeaveRequest(LeaveRequest leaveRequest) {
        Node node = leaveRequest.getNode();
        if (node != null) {
            this.neighbours.remove(leaveRequest.getNode());
            return 0;
        } else {//rare case
            logger.error("Error occurred while leaving node. Node was null in the request");
            return 9999;
        }
    }

    @Override
    public int onJoinRequest(JoinRequest joinRequest) {
        Node node = joinRequest.getNode();
        if (node != null) {
            this.neighbours.add(node);
            return 0;
        }
        return 9999;
    }
}
