package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Broadcastable;
import lk.ac.mrt.distributed.api.CommandListener;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.*;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * @author Chathura Widanage
 */
public class NodeOpsRMIImpl extends NodeOpsUDPImpl {
    private Thread udpThread;

    public NodeOpsRMIImpl(String bootstrapServerIp, int bootstrapServerPort) {
        super(bootstrapServerIp, bootstrapServerPort);
    }

    @Override
    protected void bootstrap() throws BootstrapException {
        try {
            this.socket = new DatagramSocket(this.selfNode.getPort());
        } catch (SocketException e) {
            throw new BootstrapException(e);
        }
        udpThread = new Thread(this);
        udpThread.start();
    }

    @Override
    public RegisterResponse register() throws CommunicationException, RegistrationException {
        RegisterResponse registerResponse = super.register();
        //stop udp threads and listeners
        this.udpThread.stop();

        this.socket.close();

        Registry registry = null;
        try {
            registry = LocateRegistry.createRegistry(this.selfNode.getPort());
            CommandListener commandListener = (CommandListener) UnicastRemoteObject.exportObject(this.commandListener, new Random().nextInt(5000));
            registry.bind("ops", commandListener);
            return registerResponse;
        } catch (RemoteException | AlreadyBoundException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    public void join(Set<Node> neighbours) throws CommunicationException {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setNode(selfNode);
        boolean atLeastOneDid = false;
        Exception ex = null;
        for (Node nei : neighbours) {
            CommandListener remoteNodeCommandListener = getRemoteNodeCommandListener(nei);
            try {
                remoteNodeCommandListener.onJoinRequest(joinRequest);
                atLeastOneDid = true;
            } catch (RemoteException e) {
                logger.error("Error in joining", e);
                ex = e;
                //just skip so at least one will succeed
            }
        }
        if (!atLeastOneDid)
            throw new CommunicationException(ex);
    }

    @Override
    public void leave(Set<Node> neighbours) throws CommunicationException {
        super.leave(neighbours);
    }

    @Override
    public void broadcast(Broadcastable broadcastable, Set<Node> neighbours) throws CommunicationException {
        Broadcastable oldBroadcastable = broadcastableCache.get(broadcastable.getMessageId());
        if (oldBroadcastable == null || !oldBroadcastable.isBroadcasted()) {//prevent rebroadcasting same message
            //narrowcasting to all neighbours -> broadcasting to whole network
            broadcastableCache.put(broadcastable.getMessageId(), broadcastable);//put this first to avoid receiving message again to me from a neighbour
            boolean atLeastOneDid = false;
            Exception ex = null;
            for (Node n : neighbours) {//todo enclose in try catch to prevent breaking for loop
                CommandListener remoteNodeCommandListener = getRemoteNodeCommandListener(n);
                try {
                    if (broadcastable instanceof MasterBroadcast) {
                        remoteNodeCommandListener.onMasterBroadcast((MasterBroadcast) broadcastable);
                        atLeastOneDid = true;
                    } else if (broadcastable instanceof MasterChangeBroadcast) {
                        remoteNodeCommandListener.onMasterChangeBroadcast((MasterChangeBroadcast) broadcastable);
                        atLeastOneDid = true;
                    }
                } catch (RemoteException e) {
                    logger.error("Error in broadcasting", e);
                    ex = e;
                }
            }
            if (atLeastOneDid || neighbours.size() == 0) {
                broadcastable.setBroadcasted();
            } else {
                throw new CommunicationException(ex);
            }
        }
    }

    @Override
    public void changeMasterBroadcast(String word, Node oldMaster, Node newMaster, Set<Node> to) throws CommunicationException {
        MasterChangeBroadcast masterChangeBroadcast =
                new MasterChangeBroadcast(UUID.randomUUID().toString(), word, oldMaster, newMaster);
        this.broadcast(masterChangeBroadcast, to);
    }

    @Override
    public void broadcastIAmMaster(List<String> wordsList, Set<Node> neighbours) throws CommunicationException {
        MasterBroadcast masterBroadcast = new MasterBroadcast(UUID.randomUUID().toString(), selfNode);
        masterBroadcast.setWordsList(wordsList);
        this.broadcast(masterBroadcast, neighbours);
    }

    @Override
    public void letFalseMasterKnow(String word, Node falseMaster, Node newMaster) throws CommunicationException {
        YouNoMasterRequest youNoMasterRequest = new YouNoMasterRequest(word, newMaster);
        CommandListener remoteNodeCommandListener = this.getRemoteNodeCommandListener(falseMaster);
        try {
            remoteNodeCommandListener.onYouNoMasterRequest(youNoMasterRequest);
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    public Map<String, Node> askForMasters(Node neighbour) throws CommunicationException {
        MasterWhoRequest masterWhoRequest = new MasterWhoRequest();
        masterWhoRequest.setNode(selfNode);
        CommandListener remoteNodeCommandListener = this.getRemoteNodeCommandListener(neighbour);
        try {
            return remoteNodeCommandListener.onMasterWhoRequest(masterWhoRequest);
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    public void sendMasters(Node to, Map<String, Node> masters) throws CommunicationException {
        throw new NotImplementedException();//this method is usually not required
        //super.sendMasters(to, masters);
    }

    @Override
    public void iHaveFilesForWord(Node master, String word, List<String> fileNames) throws CommunicationException {
        IHaveRequest iHaveRequest = new IHaveRequest();
        iHaveRequest.setNode(selfNode);
        iHaveRequest.setFileNames(fileNames);
        iHaveRequest.setWord(word);
        CommandListener remoteNodeCommandListener = this.getRemoteNodeCommandListener(master);
        try {
            remoteNodeCommandListener.onIHaveRequest(iHaveRequest);
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    public List<Node> getProvidersForWord(String word, Node master) throws CommunicationException {
        ProvidersRequest providersRequest = new ProvidersRequest();
        providersRequest.setWord(word);
        providersRequest.setNode(selfNode);
        CommandListener remoteNodeCommandListener = this.getRemoteNodeCommandListener(master);
        try {
            return remoteNodeCommandListener.onProvidersRequest(providersRequest);
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    public void sendProviders(Node to, String word, List<Node> providers) throws CommunicationException {
        throw new NotImplementedException();//this method is not required in RMI version
    }

    @Override
    public boolean transferResourceOwnership(String word, Node newMaster, List<Node> providers) throws CommunicationException {
        TakeMyGemsRequest takeMyGemsRequest = new TakeMyGemsRequest();
        takeMyGemsRequest.setWord(word);
        takeMyGemsRequest.setProviders(providers);
        takeMyGemsRequest.setOldMaster(selfNode);

        CommandListener remoteNodeCommandListener = this.getRemoteNodeCommandListener(newMaster);
        try {
            remoteNodeCommandListener.onTakeMyGemsRequest(takeMyGemsRequest);
            return true;
        } catch (RemoteException e) {
            throw new CommunicationException(e);
        }
    }

    @Override
    public void sendOwnershipTaken(Node oldMaster) throws CommunicationException {
        throw new NotImplementedException();//not required in RMI version
    }

    private HashMap<Node, CommandListener> registryHashMap = new HashMap<>();

    private CommandListener getRemoteNodeCommandListener(Node node) throws CommunicationException {
        Registry registry = null;
        try {
            if (registryHashMap.containsKey(node)) {
                return registryHashMap.get(node);
            }
            registry = LocateRegistry.getRegistry(node.getIp(), node.getPort());
            CommandListener commandListener = (CommandListener) registry.lookup("ops");
            registryHashMap.put(node, commandListener);
            return commandListener;
        } catch (RemoteException | NotBoundException e) {
            logger.error("Error in obtaining remote command listener", e);
            throw new CommunicationException(e);
        }
    }
}
