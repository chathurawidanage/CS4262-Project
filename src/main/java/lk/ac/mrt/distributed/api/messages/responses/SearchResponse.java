package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class SearchResponse extends Message {
    private Node node;
    private List<String> fileNames;
    private int hops;

    public SearchResponse() {
        this.fileNames = new ArrayList<>();
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public int getHops() {
        return hops;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }

    public static SearchResponse parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        Integer noOfFiles = Integer.parseInt(stringTokenizer.nextToken());
        String ip = stringTokenizer.nextToken();
        Integer port = Integer.parseInt(stringTokenizer.nextToken());

        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < noOfFiles; i++) {
            files.add(stringTokenizer.nextToken());
        }

        Node node = new Node(ip, port);
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setNode(node);
        searchResponse.setFileNames(files);
        return searchResponse;
    }


    @Override
    public String getSendableString() {
        String msg = "SEROK " + fileNames.size() + " " + node.getIp() + " " + node.getPort() + " 0 ";
        for (String fileName : fileNames) {
            msg += fileName + " ";
        }
        return this.getLengthAppenedMessage(msg.trim());
    }
}
