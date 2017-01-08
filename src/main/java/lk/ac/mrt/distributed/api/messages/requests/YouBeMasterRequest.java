package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by pubudu on 1/7/17.
 */
public class YouBeMasterRequest extends Message {
    private String word;
    private List<Node> resourceProviders;

    public static YouBeMasterRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        StringTokenizer filenameTokenizer;
        String requestType = stringTokenizer.nextToken();
        String word = stringTokenizer.nextToken();
        List<Node> providers = new ArrayList<>();

        while (stringTokenizer.hasMoreTokens()) {
            String ip = stringTokenizer.nextToken();
            Integer port = Integer.parseInt(stringTokenizer.nextToken());
//            String filenames = stringTokenizer.nextToken();
//            filenameTokenizer = new StringTokenizer(filenames, ","); // TODO: append file name details as well
            providers.add(new Node(ip, port));
        }

        YouBeMasterRequest youbemasterRequest = new YouBeMasterRequest();
        youbemasterRequest.setResourceProviders(providers);
        youbemasterRequest.setWord(word);

        return youbemasterRequest;
    }

    public void setResourceProviders(List<Node> resourceProviders) {
        this.resourceProviders = resourceProviders;
    }

    @Override
    public String getSendableString() {
        String request = "YOUBEMASTER " + word;

        for (Node node : resourceProviders) {
            request += " " + node.getIp() + " " + node.getPort(); // TODO: add filenames as well
        }
        return request;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
