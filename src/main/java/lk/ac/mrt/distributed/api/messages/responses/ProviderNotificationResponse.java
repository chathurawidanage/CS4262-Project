package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by pubudu on 1/7/17.
 */
public class ProviderNotificationResponse extends Message {
    private List<Node> providers;
    private String word;

    public static ProviderNotificationResponse parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String messageType = stringTokenizer.nextToken();

        ProviderNotificationResponse provForResponse = new ProviderNotificationResponse();
        List<Node> providerNodes = new ArrayList<>();

        while (stringTokenizer.hasMoreTokens()) {
            String providerIp = stringTokenizer.nextToken();
            Integer providerPort = Integer.parseInt(stringTokenizer.nextToken()); // assuming the <ip port> structure is always followed
            Node neighbourNode = new Node(providerIp, providerPort);
            providerNodes.add(neighbourNode);
        }

        provForResponse.setProviders(providerNodes);

        return provForResponse;
    }

    @Override
    public String getSendableString() {
        String response = "PROVS";

        for (Node node : providers) {
            response += " " + word + " " + node.getIp() + " " + node.getPort();
            // TODO: append the file names as well
        }
        return null;
    }

    public void setProviders(List<Node> providers) {
        this.providers = providers;
    }

    public List<Node> getProviders() {
        return providers;
    }
}
