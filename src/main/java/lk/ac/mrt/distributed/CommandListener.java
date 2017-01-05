package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Node;

/**
 * @author Chathura Widanage
 */
public interface CommandListener {
    void onSearchRequest(Node node, String keyword);
}
