package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * Created by pubudu on 1/7/17.
 */
public class ProviderNotificationRequest extends Message {
    private Node node;
    private String word;

    public static ProviderNotificationRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String message = stringTokenizer.nextToken();
        String word = stringTokenizer.nextToken();
        String ip = stringTokenizer.nextToken();
        Integer port = Integer.parseInt(stringTokenizer.nextToken());

        Node node = new Node(ip, port);
        ProviderNotificationRequest provForRequest = new ProviderNotificationRequest();
        provForRequest.setNode(node);
        provForRequest.setWord(word);
        return provForRequest;
    }

    @Override
    public String getSendableString() {
        return this.getLengthAppenedMessage("PROVFOR " + word + " " + node.getIp() + " " + node.getPort());
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
