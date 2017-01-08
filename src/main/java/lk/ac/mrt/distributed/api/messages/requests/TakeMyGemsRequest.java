package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * TAKEMYGEMS oldmasterip oldmaster_port word ip11 port1 filename1
 *
 * @author Chathura Widanage
 */
public class TakeMyGemsRequest extends Message {
    private String word;
    private List<Node> providers;
    private Node oldMaster;

    public Node getOldMaster() {
        return oldMaster;
    }

    public void setOldMaster(Node oldMaster) {
        this.oldMaster = oldMaster;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<Node> getProviders() {
        return providers;
    }

    public void setProviders(List<Node> providers) {
        this.providers = providers;
    }

    public static TakeMyGemsRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg);
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        String oldMasterIp = stringTokenizer.nextToken();
        Integer oldMasterPort = Integer.parseInt(stringTokenizer.nextToken());
        Node oldMaster = new Node(oldMasterIp, oldMasterPort);
        String word = stringTokenizer.nextToken();
        List<Node> providers = new ArrayList<>();
        while (stringTokenizer.hasMoreTokens()) {
            String ip = stringTokenizer.nextToken();
            Integer port = Integer.parseInt(stringTokenizer.nextToken());
            Node node = new Node(ip, port);
            if (providers.contains(node)) {
                node = providers.get(providers.indexOf(node));
            } else {
                providers.add(node);
            }
            String fileName = stringTokenizer.nextToken();
            node.getFiles().add(fileName);
        }
        TakeMyGemsRequest takeMyGemsRequest = new TakeMyGemsRequest();
        takeMyGemsRequest.setProviders(providers);
        takeMyGemsRequest.setWord(word);
        takeMyGemsRequest.setOldMaster(oldMaster);
        return takeMyGemsRequest;
    }

    @Override
    public String getSendableString() {
        String msg = "TAKEMYGEMS " + oldMaster.getIp() + " " + oldMaster.getPort() + " " + word;
        for (Node node : providers) {
            for (String fileName : node.getFiles()) {
                msg += " " + node.getIp() + " " + node.getPort() + " " + fileName;
            }
        }
        return this.getLengthAppenedMessage(msg);
    }
}
