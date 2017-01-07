package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

/**
 * @author Chathura Widanage
 */
public class UnresgiterRequest extends Message {

    private Node node;
    private String username;

    public Node getNode() {
        return node;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSendableString() {
        // length UNREG IP_address port_no username
        Node node = this.getNode();
        String msg = "UNREG " + node.getIp() + " " + node.getPort() + " " + this.getUsername();
        return this.getLengthAppenedMessage(msg);
    }
}
