package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.CommandListener;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import lk.ac.mrt.distributed.api.messages.responses.UnRegisterResponse;

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
     *
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
     *
     */
    public abstract RegisterResponse register() throws CommunicationException,RegistrationException;

    /**
     * Unregisters node from the network
     *
     */
    public abstract UnRegisterResponse unregister() throws CommunicationException;

    public abstract void join(Set<Node> neighbours) throws CommunicationException;

    public abstract void leave(Set<Node> neighbours) throws CommunicationException;

    public abstract void search(String fileName, Set<Node> neighbours) throws CommunicationException;
}
