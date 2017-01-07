package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 *
 * MASTERWHO ip port//node to it's neighbours asking masters
 *
 * Created by Lasantha on 07-Jan-17.
 */
public class MasterWhoRequest extends Message{

    private Node node;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public static MasterWhoRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        String ip = stringTokenizer.nextToken();
        Integer port = Integer.parseInt(stringTokenizer.nextToken());

        Node node = new Node(ip, port);
        MasterWhoRequest masterWhoRequest = new MasterWhoRequest();
        masterWhoRequest.setNode(node);
        return masterWhoRequest;
    }

    @Override
    public String getSendableString() {
        String msg = "MASTERWHO " + node.getIp() + " " + node.getPort();
        return this.getLengthAppenedMessage(msg);
    }
}
