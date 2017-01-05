package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.NodeOps;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import lk.ac.mrt.distributed.api.messages.responses.UnRegisterResponse;

import java.net.*;
import java.util.*;

/**
 * @author Chathura Widanage
 */
public class SearchNode extends Node implements CommandListener {
    private Set<Node> neighbours;
    private Map<String, Node> masters;
    private Map<String, List<Node>> resourceProviders;

    private NodeOps nodeOps;


    public SearchNode(String myIp, int myPort, NodeOps nodeOps) throws SocketException, NullCommandListenerException, BootstrapException {
        super(myIp, myPort);
        this.neighbours = new HashSet<>();
        this.masters = new HashMap<>();
        this.resourceProviders = new HashMap<>();

        this.nodeOps = nodeOps;
        this.nodeOps.setCommandListener(this);
        this.nodeOps.start(this);
    }

    public void bootstrap() throws SocketException, UnknownHostException, CommunicationException {
        //register node
        RegisterResponse registerResponse = nodeOps.register();
        this.neighbours.addAll(registerResponse.getNodes());

        this.nodeOps.join(this.neighbours);
    }

    //listening forever
    //todo move to a different class
    public void run() {

    }

    private void receive(DatagramPacket datagramPacket) {

    }


    @Override
    public void onSearchRequest(Node node, String keyword) {

    }
}
