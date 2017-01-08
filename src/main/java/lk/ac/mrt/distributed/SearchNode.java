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
import lk.ac.mrt.distributed.api.messages.requests.MasterWhoRequest;
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

        //get random set of files
        ArrayList<String> randomFileNames = FileNamesGenerator.getRandomFileNames();
        this.files.addAll(randomFileNames);
    }

    public void bootstrap() throws SocketException, UnknownHostException, CommunicationException, RegistrationException {
        //register node
        RegisterResponse registerResponse = nodeOps.register();
        this.neighbours.addAll(registerResponse.getNodes());
        if (!this.neighbours.isEmpty()) {//let others know I am here
            this.nodeOps.join(this.neighbours);
        }

        //our architecture specific stuff
        for (Node neigh : neighbours) {
            Map<String, Node> newMasters = this.nodeOps.askForMasters(neigh);
            this.mergeNewMasters(newMasters);
        }

        this.processMyFiles();
    }

    /**
     * This method split node's file names and let corresponding masters about them.
     * If there is no master, node self assign it self as the master and broadcast MEMASTER
     */
    public void processMyFiles() {
        HashMap<String, HashSet<String>> invertedFileIndex = new HashMap<>();
        HashSet<String> files;
        String[] tokens;

        for (String file : this.files) { //iterate through all files and build the inverted file index
            if (file != null) {
                tokens = file.split("_");
                for (String token : tokens) {
                    files = invertedFileIndex.get(token);
                    if (files == null) {
                        files = new HashSet<>();
                        files.add(file);
                        invertedFileIndex.put(token, files);
                    } else {
                        files.add(file);
                    }
                }
            }
        }

        Set<String> fileTokens = invertedFileIndex.keySet(); //the file tokens of all the files that i hold
        ArrayList<String> filesList = new ArrayList<>();
        ArrayList<String> iAmMasterFileTokens = new ArrayList<>();
        List<Node> resoureceEndpoints;

        for (String fileToken : fileTokens) {
            if (masters.containsKey(fileToken)) {
                filesList.clear();
                filesList.addAll(invertedFileIndex.get(fileToken));
                try {
                    nodeOps.iHaveFilesForWord(masters.get(fileToken), fileToken, filesList);
                } catch (CommunicationException e) {
                    e.printStackTrace(); //todo handle this
                }
            } else {
                iAmMasterFileTokens.add(fileToken);
                resoureceEndpoints = this.resourceProviders.get(fileToken);
                if (resoureceEndpoints == null) resoureceEndpoints = new ArrayList();
                resoureceEndpoints.add(this);
            }
        }

        try {
            nodeOps.broadcastIAmMaster(iAmMasterFileTokens, this.neighbours);
        } catch (CommunicationException e) {
            e.printStackTrace(); //todo handle this
        }
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

    @Override
    public void onMasterWhoRequest(MasterWhoRequest masterWhoRequest) {
        Node askingNode = masterWhoRequest.getNode();
        try {
            this.nodeOps.sendMasters(askingNode, this.masters);
        } catch (CommunicationException e) {//todo ugly try catch
            e.printStackTrace();
        }
    }

    /**
     * Will be used by node it self to carefully merge existing masters and
     * handle conflicts and let false master know about the conflicts
     *
     * @param newMasters
     */
    private void mergeNewMasters(Map<String, Node> newMasters) {
        Iterator<String> wordsIterator = newMasters.keySet().iterator();
        while (wordsIterator.hasNext()) {
            String word = wordsIterator.next();
            Node master = newMasters.get(word);
            if (this.masters.containsKey(word) && !this.masters.get(word).equals(master)) {//conflict
                Node oldMaster = this.masters.get(word);
                this.masters.put(word, master);
                //let false master know he is no longer the master
                try {
                    this.nodeOps.letFalseMasterKnow(word, oldMaster, master);
                } catch (CommunicationException e) {//todo ugly try catch
                    e.printStackTrace();
                }
            }
        }
    }
}
