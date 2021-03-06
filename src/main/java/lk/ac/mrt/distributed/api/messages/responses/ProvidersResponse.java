package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * word ip1 port1 filename1 ip2 port2 filename2
 * Created by pubudu on 1/7/17.
 */
public class ProvidersResponse extends Message {
    private List<Node> providers;
    private String word;

    public static ProvidersResponse parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String messageType = stringTokenizer.nextToken();
        String word=stringTokenizer.nextToken();

        ProvidersResponse provForResponse = new ProvidersResponse();
        List<Node> providerNodes = new ArrayList<>();

        while (stringTokenizer.hasMoreTokens()) {
            String providerIp = stringTokenizer.nextToken();
            Integer providerPort = Integer.parseInt(stringTokenizer.nextToken()); // assuming the <ip port> structure is always followed
            Node node = new Node(providerIp, providerPort);
            if (providerNodes.contains(node)) {
                node = providerNodes.get(providerNodes.indexOf(node));
            } else {
                providerNodes.add(node);
            }
            if (node.getFiles() == null) {
                node.setFiles(new ArrayList<String>());
            }
            node.getFiles().add(stringTokenizer.nextToken());
        }

        provForResponse.setProviders(providerNodes);
        provForResponse.setWord(word);

        return provForResponse;
    }

    @Override
    public String getSendableString() {
        String response = "PROVS "+word;
        for (Node node : providers) {
            for (String file : node.getFiles()) {
                response += " " + node.getIp() + " " + node.getPort() + " " + file;
            }
        }
        return this.getLengthAppenedMessage(response);
    }

    public void setProviders(List<Node> providers) {
        this.providers = providers;
    }

    public List<Node> getProviders() {
        return providers;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
