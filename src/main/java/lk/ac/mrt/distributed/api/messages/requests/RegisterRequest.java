package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class RegisterRequest extends Message {
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

    public static RegisterRequest parse(String msg) {
        StringTokenizer stringTokenizer=new StringTokenizer(msg," ");
        String length=stringTokenizer.nextToken();
        String message=stringTokenizer.nextToken();
        String ip=stringTokenizer.nextToken();
        String port=stringTokenizer.nextToken();
        String username=stringTokenizer.nextToken();

        return null;
    }

    public static RegisterRequest generate(String ipAddress, int port, String username) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setNode(new Node(ipAddress,port));
        registerRequest.setUsername(username);
        return registerRequest;
    }

    public String getSendableString() {
        String msg = "REG " + this.getNode().getIp() + " " + this.getNode().getPort() + " " + this.getUsername();
        return this.getLengthAppenedMessage(msg);
    }
}
