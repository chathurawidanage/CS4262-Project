package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import lk.ac.mrt.distributed.api.messages.responses.UnregisterResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Chathura Widanage
 */
public abstract class NodeOps {

    protected CommandListener commandListener;
    protected Node selfNode;

    public void setCommandListener(CommandListener commandListener) {
        this.commandListener = commandListener;
    }

    /**
     * Start server or do anything which initiate communication
     */
    protected abstract void bootstrap() throws BootstrapException;

    /**
     * Called by the Node to initiate the Node Operations
     *
     * @param node {@link Node} instance which acts as the self node
     */
    public void start(Node node) throws NullCommandListenerException, BootstrapException {
        if (this.commandListener == null) {
            throw new NullCommandListenerException();
        }
        this.selfNode = node;
        bootstrap();
    }

    /**
     * Registers in the network by communicating with the bootstrap server
     */
    public abstract RegisterResponse register() throws CommunicationException, RegistrationException;

    /**
     * Unregisters node from the network
     */
    public abstract UnregisterResponse unregister() throws CommunicationException;

    public abstract void join(Set<Node> neighbours) throws CommunicationException;

    public abstract void leave(Set<Node> neighbours) throws CommunicationException;

    public abstract void search(String fileName, Set<Node> neighbours) throws CommunicationException;

    public abstract void broadcast(Broadcastable broadcastable, Set<Node> neighbours) throws CommunicationException;

    public abstract void changeMasterBroadcast(String word, Node oldMaster, Node newMaster, Set<Node> toNeighbours) throws CommunicationException;

    /**
     * Let and older master know that, he is no longer the master. It is his responsibility to
     * call changeMasterBroadcast when he receive this message
     *
     * @param word
     * @param falseMaster
     * @param newMaster
     */
    public abstract void letFalseMasterKnow(String word, Node falseMaster, Node newMaster) throws CommunicationException;

    /**
     * Deliberately asking only from one to simplify things
     *
     * @param neighbours
     * @throws CommunicationException
     */
    public abstract Map<String, Node> askForMasters(Node neighbours) throws CommunicationException;

    public abstract void sendMasters(Node to, Map<String, Node> masters) throws CommunicationException;

    /**
     * This will be sent by a node to the master of the word
     *
     * @param master
     */
    public abstract void iHaveFilesForWord(Node master, String word, List<String> fileNames) throws CommunicationException;
}
