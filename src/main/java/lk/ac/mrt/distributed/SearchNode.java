package lk.ac.mrt.distributed;

import javafx.util.Pair;
import lk.ac.mrt.distributed.api.CommandListener;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.NodeOps;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.*;
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
    private Map<String, Set<Node>> resourceProviders;//I am master for these words

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

    public void leave() throws CommunicationException {
        this.nodeOps.leave(neighbours);
    }

    public void unregister() throws CommunicationException {
        this.nodeOps.unregister();
    }

    public void join() throws CommunicationException {
        this.nodeOps.join(neighbours);
    }

    /**
     * This method split node's file names and let corresponding masters about them.
     * If there is no master, node self assign it self as the master and broadcast MEMASTER
     */
    private void processMyFiles() {
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
        Set<Node> resoureceEndpoints;

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
                this.masters.put(fileToken, this);//set me as master
                resoureceEndpoints = this.resourceProviders.get(fileToken);
                if (resoureceEndpoints == null) {
                    resoureceEndpoints = new HashSet<>();
                    this.resourceProviders.put(fileToken, resoureceEndpoints);
                }
                resoureceEndpoints.add(this);
            }
        }

        try {
            if(iAmMasterFileTokens.size() > 0)
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
                        if (existingMasterNode.equals(this)) {//if this node is the old master
                            YouNoMasterRequest youNoMasterRequest =
                                    new YouNoMasterRequest(word, node);
                            this.onYouNoMasterRequest(youNoMasterRequest);
                        } else {
                            nodeOps.letFalseMasterKnow(word, existingMasterNode, node);
                        }
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
        if(youNoMasterRequest.getNewMaster().equals(this)){
            System.out.println("SHIT HAS HAPPENED");
            return;
        }
        //sending my resources to new master
        try {
            boolean transfered = this.nodeOps.transferResourceOwnership(
                    youNoMasterRequest.getWord(),
                    youNoMasterRequest.getNewMaster(),
                    new ArrayList<Node>(this.resourceProviders.get(youNoMasterRequest.getWord()))
            );
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onProvidersRequest(ProvidersRequest providersRequest) {
        String word = providersRequest.getWord();
        Node requestingNode = providersRequest.getNode();
        Set<Node> providers = this.resourceProviders.get(word);
        try {
            if (providers == null) {
                logger.warn("Null provider request. Possible error in protocol");
                providers = new HashSet<>();
            }
            this.nodeOps.sendProviders(requestingNode, word, new ArrayList<>(providers));
        } catch (CommunicationException e) {//todo ugly try catch
            e.printStackTrace();
        }
    }

    @Override
    public void onTakeMyGemsRequest(TakeMyGemsRequest takeMyGemsRequest) {
        String word = takeMyGemsRequest.getWord();
        List<Node> providers = takeMyGemsRequest.getProviders();
        Set<Node> nodes = this.resourceProviders.get(word);
        if (nodes == null) {
            nodes = new HashSet<>();
            this.resourceProviders.put(word, nodes);
        }
        nodes.addAll(providers);
        try {
            this.nodeOps.sendOwnershipTaken(takeMyGemsRequest.getOldMaster());
        } catch (CommunicationException e) {//todo ugly try catch
            e.printStackTrace();
        }
    }

    @Override
    public void onIHaveRequest(IHaveRequest iHaveRequest) {
        String word = iHaveRequest.getWord();
        Node node = iHaveRequest.getNode();
        List<String> fileNames = iHaveRequest.getFileNames();
        node.setFiles(fileNames);
        Set<Node> nodes = this.resourceProviders.get(word);
        if (nodes == null) {
            nodes = new HashSet<>();
            this.resourceProviders.put(word, nodes);
        }
        nodes.add(node);
    }

    public List<Pair<String, Node>> search(String query) {
        HashSet<String> queryTokens = new HashSet<>();
        List<Node> providers = null;
        List<Pair<String, Node>> searchResults = new ArrayList<>();
        List<String> candidateFiles;
        String[] temp = query.trim().split("\\+s");
        for (String t : temp) queryTokens.add(t);
        for (String queryToken : queryTokens) {
            if (resourceProviders.containsKey(queryToken)) { //i am the master for this word
                providers = resourceProviders.get(queryToken);
            } else if (masters.containsKey(queryToken)) {
                try {
                    providers = nodeOps.getProvidersForWord(queryToken, masters.get(queryToken));
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
            } else { //this key is not in my current view of the network, hence return empty set
                searchResults.clear();
                return searchResults;
            }
            if (providers != null) {
                for (Node provider :
                        providers) {
                    candidateFiles = provider.getFiles();
                    for (String file :
                            candidateFiles) {
                        if (containsAll(file, queryTokens))
                            searchResults.add(new Pair(provider, file));
                    }
                }
            }
        }
        return searchResults;
    }

    private boolean containsAll(String haystack, Collection<String> needles) {
        List<String> haystackTokenized = Arrays.asList(haystack.split("[\\s_]+"));
        return haystackTokenized.containsAll(needles);
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
            }else{
                this.masters.put(word,master);
            }
        }
    }
}
