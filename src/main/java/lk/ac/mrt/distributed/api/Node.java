package lk.ac.mrt.distributed.api;

import java.util.List;
import java.util.Map;

/**
 * @author Chathura Widanage
 */
public class Node {
    private int port;
    private String ip;
    private List<Node> neighbours;
    private Map<String, Node> masters;
    private Map<String, List<Node>> resourceProviders;
}
