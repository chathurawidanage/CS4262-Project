package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Broadcastable;
import lk.ac.mrt.distributed.api.CommandListener;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.JoinRequest;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            CommandListener commandListener = (CommandListener) UnicastRemoteObject.exportObject(this.commandListener, 0);
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
        for (Node nei : neighbours) {
            CommandListener remoteNodeCommandListener = getRemoteNodeCommandListener(nei);
            remoteNodeCommandListener.onJoinRequest(joinRequest);
        }
        super.join(neighbours);
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
            for (Node n : neighbours) {//todo enclose in try catch to prevent breaking for loop
                CommandListener remoteNodeCommandListener = getRemoteNodeCommandListener(n);
                if (broadcastable instanceof MasterBroadcast) {
                    remoteNodeCommandListener.onMasterBroadcast((MasterBroadcast) broadcastable);
                } else if (broadcastable instanceof MasterChangeBroadcast) {
                    remoteNodeCommandListener.onMasterChangeBroadcast((MasterChangeBroadcast) broadcastable);
                }
            }
            broadcastable.setBroadcasted();
        }
    }

    @Override
    public void changeMasterBroadcast(String word, Node oldMaster, Node newMaster, Set<Node> to) throws CommunicationException {
        super.changeMasterBroadcast(word, oldMaster, newMaster, to);
    }

    @Override
    public void letFalseMasterKnow(String word, Node falseMaster, Node newMaster) throws CommunicationException {
        super.letFalseMasterKnow(word, falseMaster, newMaster);
    }

    @Override
    public Map<String, Node> askForMasters(Node neighbour) throws CommunicationException {
        return super.askForMasters(neighbour);
    }

    @Override
    public void sendMasters(Node to, Map<String, Node> masters) throws CommunicationException {
        super.sendMasters(to, masters);
    }

    @Override
    public void iHaveFilesForWord(Node master, String word, List<String> fileNames) throws CommunicationException {
        super.iHaveFilesForWord(master, word, fileNames);
    }

    @Override
    public List<Node> getProvidersForWord(String word, Node master) throws CommunicationException {
        return super.getProvidersForWord(word, master);
    }

    @Override
    public void sendProviders(Node to, String word, List<Node> providers) throws CommunicationException {
        super.sendProviders(to, word, providers);
    }

    @Override
    public boolean transferResourceOwnership(String word, Node newMaster, List<Node> providers) throws CommunicationException {
        return super.transferResourceOwnership(word, newMaster, providers);
    }

    @Override
    public void sendOwnershipTaken(Node oldMaster) throws CommunicationException {
        super.sendOwnershipTaken(oldMaster);
    }

    private CommandListener getRemoteNodeCommandListener(Node node) throws CommunicationException {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(node.getIp(), node.getPort());
            return (CommandListener) registry.lookup("ops");
        } catch (RemoteException | NotBoundException e) {
            logger.error("Error in obtaining remote command listener", e);
            throw new CommunicationException(e);
        }
    }
}
