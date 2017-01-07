package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * UNOMASTER word ip_real_master port_real_master
 *
 * @author Chathura Widanage
 */
public class YouNoMasterRequest extends Message {
    private String word;
    private Node newMaster;

    public YouNoMasterRequest(String word, Node newMaster) {
        this.word = word;
        this.newMaster = newMaster;
    }

    public Node getNewMaster() {
        return newMaster;
    }

    public String getWord() {
        return word;
    }

    public static YouNoMasterRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        String word = stringTokenizer.nextToken();
        String newMasterIp = stringTokenizer.nextToken();
        Integer newMasterPort = Integer.parseInt(stringTokenizer.nextToken());

        Node newMaster = new Node(newMasterIp, newMasterPort);
        return new YouNoMasterRequest(word, newMaster);
    }

    @Override
    public String getSendableString() {
        String msg = "UNOMASTER " + word + " " + newMaster.getIp() + " " + newMaster.getPort();
        return this.getLengthAppenedMessage(msg);
    }
}
