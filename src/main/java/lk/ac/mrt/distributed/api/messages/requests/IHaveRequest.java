package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * IHAVE word ip port file_name1,file_name2... //by a node directly to master. let master know that I have a file name having the word
 * Created by Lasantha on 07-Jan-17.
 */
public class IHaveRequest extends Message {

    private String word;
    private Node node;
    private ArrayList<String> fileNames;

    public String getWord() {
        return word;
    }

    public Node getNode() {
        return node;
    }

    public ArrayList<String> getFileNames() {
        return fileNames;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setFileNames(ArrayList<String> fileNames) {
        this.fileNames = fileNames;
    }

    public static IHaveRequest parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        String word = stringTokenizer.nextToken();
        String ip = stringTokenizer.nextToken();
        Integer port = Integer.parseInt(stringTokenizer.nextToken());

        String[] files = stringTokenizer.nextToken().split(",");
        ArrayList<String> fileNames = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            fileNames.add(files[i]);
        }
        Node node = new Node(ip, port);
        IHaveRequest iHaveRequest = new IHaveRequest();
        iHaveRequest.setWord(word);
        iHaveRequest.setNode(node);
        iHaveRequest.setFileNames(fileNames);

        return iHaveRequest;
    }

    @Override
    public String getSendableString() {
        String msg = "IHAVE " + this.getWord() + " " + node.getIp() + " " + node.getPort() + " " + fileNames.get(0);

        for (int i = 1; i < fileNames.size(); i++) {
            msg += "," + fileNames.get(i);
        }
        return this.getLengthAppenedMessage(msg);
    }
}
