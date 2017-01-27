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
public class SearchNode implements CommandListener {
    private final static Logger logger = LogManager.getLogger(SearchNode.class);

    private Set<Node> neighbours;
    private Map<String, Node> masters;//Some one else is master for these words
    private Map<String, Set<Node>> resourceProviders;//I am master for these words

    private NodeOps nodeOps;

    private Node selfNode;//todo can't extend this class with Node due to RMI restrictions

    public void printNodeStats() {
        System.out.println(String.format("Node has %d neighbours", neighbours.size()));
        System.out.println(String.format("Node has knowledge about %d masters", masters.size()));
        System.out.println(String.format("Node is master for %d words", resourceProviders.size()));
    }

    public SearchNode(String username, String myIp, int myPort, NodeOps nodeOps) throws SocketException, NullCommandListenerException, BootstrapException {
        this.selfNode = new Node(myIp, myPort);
        this.selfNode.setUsername(username);

        this.neighbours = new HashSet<>();
        this.masters = new HashMap<>();
        this.resourceProviders = new HashMap<>();

        this.nodeOps = nodeOps;
        this.nodeOps.setCommandListener(this);

        this.nodeOps.start(selfNode);
        //this.nodeOps.start(this);//todo send a copy to prevent RMI class cast

        //get random set of files
        ArrayList<String> randomFileNames = FileNamesGenerator.getRandomFileNames();
        this.selfNode.getFiles().addAll(randomFileNames);
    }

    public Node getSelfNode() {
        return selfNode;
    }

    public void bootstrap() throws SocketException, UnknownHostException, CommunicationException, RegistrationException {
        //register node
        Statistics.INSTANCE.routingMessagesOut++;
        RegisterResponse registerResponse = nodeOps.register();
        Statistics.INSTANCE.routingMessagesIn++;
        this.neighbours.addAll(registerResponse.getNodes());
        if (!this.neighbours.isEmpty()) {//let others know I am here
            this.nodeOps.join(this.neighbours);
        }

        //our architecture specific stuff
        for (Node neigh : neighbours) {
            Map<String, Node> newMasters = this.nodeOps.askForMasters(neigh);
            this.mergeNewMasters(newMasters);
        }


        Statistics.INSTANCE.routingMessagesOut += this.neighbours.size() * 2;//for join and master
        Statistics.INSTANCE.routingMessagesIn += this.neighbours.size() * 2;

        this.processMyFiles();
    }

    public void leave() throws CommunicationException {
        Set<Node> leaveNotifiers = new HashSet<>(neighbours);
        List<String> files = this.selfNode.getFiles();
        for (String file : files) {
            String[] split = file.split("_");
            for (String token : split) {
                leaveNotifiers.add(this.masters.get(token));
            }
        }
        Statistics.INSTANCE.routingMessagesOut += leaveNotifiers.size();

        this.nodeOps.leave(leaveNotifiers);//notify neighbours and masters

        //if leave OK transfer ownership if I am master
        if (neighbours.size() > 0) {
            Iterator<String> iterator = resourceProviders.keySet().iterator();
            while (iterator.hasNext()) {
                String word = iterator.next();
                Node newMaster = new ArrayList<>(neighbours).get(new Random().nextInt(neighbours.size()));
                logger.info("Transferring ownership of {}", word);
                try {
                    ArrayList<Node> nodes = new ArrayList<>(this.resourceProviders.get(word));
                    nodes.remove(this.getSelfNode());//remove me
                    Statistics.INSTANCE.routingMessagesOut++;
                    boolean transfered = this.nodeOps.transferResourceOwnership(
                            word,
                            newMaster,
                            nodes
                    );
                } catch (CommunicationException e) {
                    logger.error("Error in transferring resource ownership", e);
                }
                //resourceProviders.remove(youNoMasterRequest.getWord());
                //masters.put(youNoMasterRequest.getWord(), youNoMasterRequest.getNewMaster());
                try {
                    Statistics.INSTANCE.routingMessagesOut+=this.neighbours.size();
                    this.nodeOps.changeMasterBroadcast(
                            word,
                            this.selfNode,
                            newMaster,
                            this.neighbours
                    );
                } catch (CommunicationException e) {//todo ugly try catch here
                    logger.error("Failed to transfer ownership of {}", word, e);
                }
            }
        }

    }

    public void unregister() throws CommunicationException {
        Statistics.INSTANCE.routingMessagesOut++;
        this.nodeOps.unregister();
        Statistics.INSTANCE.routingMessagesIn++;//UNREGOK or return
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

        for (String file : this.selfNode.getFiles()) { //iterate through all files and build the inverted file index
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
                this.masters.put(fileToken, this.selfNode);//set me as master
                resoureceEndpoints = this.resourceProviders.get(fileToken);
                if (resoureceEndpoints == null) {
                    resoureceEndpoints = new HashSet<>();
                    this.resourceProviders.put(fileToken, resoureceEndpoints);
                }
                resoureceEndpoints.add(this.selfNode);
            }
        }

        try {
            if (iAmMasterFileTokens.size() > 0)
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
        Statistics.INSTANCE.routingMessagesIn++;
        Statistics.INSTANCE.routingMessagesOut++;
        logger.info("Leave request received for {}", leaveRequest.getNode());
        Node node = leaveRequest.getNode();
        if (node != null) {
            this.neighbours.remove(leaveRequest.getNode());

            //also leave if I have record as this node is a resource provider
            Iterator<String> iterator = this.resourceProviders.keySet().iterator();
            while (iterator.hasNext()) {
                String token = iterator.next();
                logger.debug("Checking token {} | {}", token, this.resourceProviders.get(token).toString());
                this.resourceProviders.get(token).remove(leaveRequest.getNode());
            }
            return 0;
        } else {//rare case
            logger.error("Error occurred while leaving node. Node was null in the request");
            return 9999;
        }
    }

    @Override
    public int onJoinRequest(JoinRequest joinRequest) {
        Statistics.INSTANCE.routingMessagesIn++;
        Statistics.INSTANCE.routingMessagesOut++;
        Node node = joinRequest.getNode();
        if (node != null) {
            this.neighbours.add(node);
            return 0;
        }
        return 9999;
    }

    @Override
    public void onMasterBroadcast(MasterBroadcast masterBroadcast) {
        Statistics.INSTANCE.routingMessagesIn++;
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
                            Statistics.INSTANCE.routingMessagesOut++;
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
            Statistics.INSTANCE.routingMessagesOut+=this.neighbours.size();
            this.nodeOps.broadcast(masterBroadcast, this.neighbours);
        } catch (CommunicationException e) {//todo ugly try catch here
            e.printStackTrace();
        }
    }

    @Override
    public void onMasterChangeBroadcast(MasterChangeBroadcast masterChangeBroadcast) {
        Statistics.INSTANCE.routingMessagesIn++;
        String word = masterChangeBroadcast.getWord();
        Node newMaster = masterChangeBroadcast.getNewMaster();
        Node oldMaster = masterChangeBroadcast.getOldMaster();
        Node currentMaster = masters.get(word);
        if (currentMaster != null && !currentMaster.equals(oldMaster)) {
            //todo handle later. There are possible more than 3 masters inside the network. Rare
        }
        masters.put(word, newMaster);//just replace the master
        try {
            Statistics.INSTANCE.routingMessagesOut+=this.neighbours.size();
            this.nodeOps.broadcast(masterChangeBroadcast, this.neighbours);
        } catch (CommunicationException e) {//todo ugly try catch
            e.printStackTrace();
        }
    }

    @Override
    public void onYouNoMasterRequest(YouNoMasterRequest youNoMasterRequest) {
        Statistics.INSTANCE.routingMessagesIn++;
        if (youNoMasterRequest.getNewMaster().equals(this)) {
            System.out.println("SHIT HAS HAPPENED");
            return;
        }
        //sending my resources to new master
        try {
            Statistics.INSTANCE.routingMessagesOut++;
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
            Statistics.INSTANCE.routingMessagesOut+=this.neighbours.size();
            this.nodeOps.changeMasterBroadcast(
                    youNoMasterRequest.getWord(),
                    this.selfNode,
                    youNoMasterRequest.getNewMaster(),
                    this.neighbours
            );
        } catch (CommunicationException e) {//todo ugly try catch here
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Node> onMasterWhoRequest(MasterWhoRequest masterWhoRequest) {
        Statistics.INSTANCE.routingMessagesIn++;
        Statistics.INSTANCE.routingMessagesOut++;
        return this.masters;/*
        Node askingNode = masterWhoRequest.getNode();
        try {
            this.nodeOps.sendMasters(askingNode, this.masters);
        } catch (CommunicationException e) {//todo ugly try catch
            e.printStackTrace();
        }*/
    }

    @Override
    public List<Node> onProvidersRequest(ProvidersRequest providersRequest) {
        Statistics.INSTANCE.queryMessagesIn++;
        Statistics.INSTANCE.queryMessagesOut++;
        String word = providersRequest.getWord();
        Set<Node> providers = this.resourceProviders.get(word);
        if (providers == null) {
            logger.warn("Null provider request. Possible error in protocol");
            providers = new HashSet<>();
        }
        return new ArrayList<>(providers);
    }

    @Override
    public void onTakeMyGemsRequest(TakeMyGemsRequest takeMyGemsRequest) {
        Statistics.INSTANCE.routingMessagesIn++;
        Statistics.INSTANCE.routingMessagesOut++;
        String word = takeMyGemsRequest.getWord();
        List<Node> providers = takeMyGemsRequest.getProviders();
        Set<Node> nodes = this.resourceProviders.get(word);
        if (nodes == null) {
            nodes = new HashSet<>();
            this.resourceProviders.put(word, nodes);
        }
        nodes.addAll(providers);
    }

    @Override
    public void onIHaveRequest(IHaveRequest iHaveRequest) {
        Statistics.INSTANCE.routingMessagesIn++;
        Statistics.INSTANCE.routingMessagesOut++;
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

    public SearchResult search(String query) {
        logger.info("SEARCH STARTED FOR - {}", query);
        long startTime = System.currentTimeMillis();
        int hopCounter = 0;

        HashMap<String, Set<Node>> queryTokensProviders = new HashMap<>();
        List<Pair<String, Node>> searchResults = new ArrayList<>();
        List<String> candidateFiles;
        Set<String> worldKeyset = masters.keySet();
        Set<String> myKeyset = resourceProviders.keySet();
        String[] temp = query.trim().split(" ");
        for (String t : temp) {
            if (worldKeyset.contains(t) || myKeyset.contains(t)) //this is an alien key, we have no files on this key
                queryTokensProviders.put(t, new HashSet<Node>());
        }

        for (String queryToken : queryTokensProviders.keySet()) {
            if (resourceProviders.containsKey(queryToken)) { //i am the master for this word
                logger.debug("This node is the master for {}", queryToken);
                queryTokensProviders.put(queryToken, resourceProviders.get(queryToken));
            } else if (masters.containsKey(queryToken)) {
                try {
                    logger.debug("Requesting providers for {} from {}", queryToken, masters.get(queryToken));
                    HashSet<Node> providers = new HashSet<>(nodeOps.getProvidersForWord(queryToken, masters.get(queryToken)));
                    queryTokensProviders.put(queryToken, providers);
                    hopCounter++;
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!queryTokensProviders.isEmpty()) {
            Iterator<String> iterator = queryTokensProviders.keySet().iterator();
            String queryToken = iterator.next();
            Set<Node> providers = queryTokensProviders.get(queryToken);

            while (iterator.hasNext()) {
                queryToken = iterator.next();
                providers.retainAll(queryTokensProviders.get(queryToken));
            }

            for (Node provider :
                    providers) {
                candidateFiles = provider.getFiles();
                for (String file :
                        candidateFiles) {
                    if (containsAll(file, queryTokensProviders.keySet()))
                        searchResults.add(new Pair(provider, file));
                }
            }
        }
        logger.info("SEARCH ENDED {} - {}ms & {} hops", query, System.currentTimeMillis() - startTime, hopCounter);
        SearchResult results=new SearchResult();
        results.results=searchResults;
        results.hops=hopCounter;
        results.time=System.currentTimeMillis() - startTime;
        return results;
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
            } else {
                this.masters.put(word, master);
            }
        }
    }
}
