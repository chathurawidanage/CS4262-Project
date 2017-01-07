package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.CommandListener;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.NodeOps;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.JoinRequest;
import lk.ac.mrt.distributed.api.messages.requests.LeaveRequest;
import lk.ac.mrt.distributed.api.messages.requests.YouNoMasterRequest;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;
import java.net.UnknownHostException;
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

    @Override
    public void onMasterBroadcast(MasterBroadcast masterBroadcast) {
        Node node = masterBroadcast.getNode();
        List<String> words = masterBroadcast.getWordsList();
        for (String word : words) {
            if (this.masters.containsKey(word)) {//possible master conflict
                Node existingMasterNode = this.masters.get(word);
                if (!existingMasterNode.equals(node)) {//let the old master know that he  is no longer the master
                    try {
                        nodeOps.letFalseMasterKnow(word, existingMasterNode, node);
                    } catch (CommunicationException e) {//todo ugly try catch
                        e.printStackTrace();
                    }//best way is to make older one YOUNOMASTER, since current broadcast message of current master is on air
                }
            } else {
                this.masters.put(word, node);
            }
        }
        //send the news to all neighbours
        try {
            this.nodeOps.broadcast(masterBroadcast, this.neighbours);
        } catch (CommunicationException e) {//todo ugly try catch here
            e.printStackTrace();
        }
    }

    @Override
    public void onMasterChangeBroadcast(MasterChangeBroadcast masterChangeBroadcast) {
        String word = masterChangeBroadcast.getWord();
        Node newMaster = masterChangeBroadcast.getNewMaster();
        Node oldMaster = masterChangeBroadcast.getOldMaster();
        Node currentMaster = masters.get(word);
        if (currentMaster != null && !currentMaster.equals(oldMaster)) {
            //todo handle later. There are possible more than 3 masters inside the network. Rare
        }
        masters.put(word, newMaster);//just replace the master
        try {
            this.nodeOps.broadcast(masterChangeBroadcast, this.neighbours);
        } catch (CommunicationException e) {//todo ugly try catch
            e.printStackTrace();
        }
    }

    @Override
    public void onYouNoMasterRequest(YouNoMasterRequest youNoMasterRequest) {
        //todo send my resources to new master
        resourceProviders.remove(youNoMasterRequest.getWord());
        masters.put(youNoMasterRequest.getWord(), youNoMasterRequest.getNewMaster());
        try {
            this.nodeOps.changeMasterBroadcast(
                    youNoMasterRequest.getWord(),
                    this,
                    youNoMasterRequest.getNewMaster(),
                    this.neighbours
            );
        } catch (CommunicationException e) {//todo ugly try catch here
            e.printStackTrace();
        }
    }
}
