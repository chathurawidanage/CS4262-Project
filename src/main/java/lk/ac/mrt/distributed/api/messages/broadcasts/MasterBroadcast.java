package lk.ac.mrt.distributed.api.messages.broadcasts;

import lk.ac.mrt.distributed.api.Broadcastable;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * MEMASTER uuid ip port word_count word1 word2 word3
 *
 * @author Chathura Widanage
 */
public class MasterBroadcast extends Message implements Broadcastable {
    private String uuid;
    private Node node;
    private List<String> wordsList;

    private boolean broadcasted;//just a flag

    public MasterBroadcast(String uuid, Node node) {
        this.uuid = uuid;
        this.node = node;
    }

    public List<String> getWordsList() {
        return wordsList;
    }

    public Node getNode() {
        return node;
    }

    public String getUuid() {
        return uuid;
    }

    public static MasterBroadcast parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        String uuid = stringTokenizer.nextToken();
        String ip = stringTokenizer.nextToken();
        Integer port = Integer.parseInt(stringTokenizer.nextToken());
        Integer wordCount = Integer.parseInt(stringTokenizer.nextToken());

        Node node = new Node(ip, port);
        MasterBroadcast masterBroadcast = new MasterBroadcast(uuid, node);
        masterBroadcast.wordsList = new ArrayList<>();
        for (int i = 0; i < wordCount; i++) {
            masterBroadcast.wordsList.add(stringTokenizer.nextToken());
        }
        return masterBroadcast;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MasterBroadcast) {
            return ((MasterBroadcast) obj).uuid.equals(uuid);
        }
        return false;
    }

    @Override
    public String getSendableString() {
        String msg = "MEMASTER " + node.getIp() + " " + node.getPort() + " ";
        for (String word : wordsList) {
            msg += word + " ";
        }
        return this.getLengthAppenedMessage(msg.trim());
    }

    @Override
    public boolean isBroadcasted() {
        return broadcasted;
    }

    @Override
    public void setBroadcasted() {
        this.broadcasted = true;
    }

    @Override
    public String getMessageId() {
        return this.getUuid();
    }

    @Override
    public String getBroadcastMessage() {
        return this.getSendableString();
    }
}
