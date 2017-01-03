package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Sendable;

import java.util.List;

/**
 * @author Chathura Widanage
 */
public class RegisterResponse implements Sendable {
    private int nodesCount;
    private List<Node> nodes;


    public int getNodesCount() {
        return nodesCount;
    }

    public void setNodesCount(int nodesCount) {
        this.nodesCount = nodesCount;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public static RegisterResponse parse(String msg){
        //todo parse registser response
        return null;
    }

    public static RegisterResponse generate(List<Node> nodes){
        //todo implementation
        return null;
    }

    public String getSendableString() {
        //todo implementation : length REGOK no_nodes IP_1 port_1 IP_2 port_2
        return null;
    }
}
