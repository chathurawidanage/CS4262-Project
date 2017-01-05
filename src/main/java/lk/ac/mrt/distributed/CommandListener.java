package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.Node;

/**
 * @author Chathura Widanage
 */
public interface CommandListener {
    void onSerachRequest(Node node, String keyword);
}
