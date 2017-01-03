package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.messages.responses.RegisterResponse;
import lk.ac.mrt.distributed.api.messages.responses.UnRegisterResponse;

/**
 * @author Chathura Widanage
 */
public interface NodeOps {
    RegisterResponse register(Node node);

    UnRegisterResponse unregister(Node node);

    void join(Node node);

    void leave(Node node);

    void search(Node node, String fileName);
}
