package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import lk.ac.mrt.distributed.api.messages.responses.UnRegisterResponse;

import java.util.Set;

/**
 * @author Chathura Widanage
 */
public interface NodeOps {
    /**
     * Registers in the network by communicating with the bootstrap server
     *
     * @param node Node to be registered
     * @return {@link RegisterResponse} instance
     */
    RegisterResponse register(Node node);

    /**
     * Unregisters node from the network
     *
     * @param node
     * @return
     */
    UnRegisterResponse unregister(Node node);

    void join(Node node, Set<Node> neighbours);

    void leave(Node node, Set<Node> neighbours);

    void search(Node node, String fileName, Set<Node> neighbours);
}
