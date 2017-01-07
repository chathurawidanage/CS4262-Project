package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

/**
 * @author Chathura Widanage
 */
public class UnregisterRequest extends Message {

    private Node node;
    private String username;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static UnregisterRequest generate(String ipAddress, int port, String username) {
        UnregisterRequest unRegisterRequest = new UnregisterRequest();
        unRegisterRequest.setNode(new Node(ipAddress,port));
        unRegisterRequest.setUsername(username);
        return unRegisterRequest;
    }

    public String getSendableString() {
        // length UNREG IP_address port_no username
        Node node = this.getNode();
        String msg = "UNREG " + this.getNode().getIp() + " " + this.getNode().getPort() + " " + this.getUsername();
        return this.getLengthAppenedMessage(msg);
    }
}
