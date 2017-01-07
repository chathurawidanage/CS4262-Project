package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class JoinRequest extends Message {
    private Node node;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public static JoinRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        String ip = stringTokenizer.nextToken();
        Integer port = Integer.parseInt(stringTokenizer.nextToken());

        Node node = new Node(ip, port);
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setNode(node);
        return joinRequest;
    }

    @Override
    public String getSendableString() {
        Node node = this.getNode();
        String msg = "JOIN " + node.getIp() + " " + node.getPort();
        return this.getLengthAppenedMessage(msg);
    }
}
