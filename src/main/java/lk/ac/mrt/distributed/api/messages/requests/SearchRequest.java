package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.regex.Pattern;

/**
 * @author Chathura Widanage
 */
public class SearchRequest extends Message {
    private Node node;
    private String fileName;
    int hops;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public static SearchRequest parse(String msg) {

        return null;
    }

    @Override
    public String getSendableString() {
        String msg = "SER " + node.getIp() + " " + node.getPort() + " '" + fileName + "' " + hops;
        return this.getLengthAppenedMessage(msg);
    }
}
