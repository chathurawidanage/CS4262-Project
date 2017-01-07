package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * MASTERS word1 ip1 port1 word2 ip2 port2//neighbours send this as a repose for MasterWhoRequest
 *
 * Created by Lasantha on 07-Jan-17.
 */
public class MasterWhoResponse extends Message{
    private Map<String, Node> masters;

    public Map<String, Node> getMasters() {
        return masters;
    }

    public void setMasters(Map<String, Node> masters) {
        this.masters = masters;
    }

    public static MasterWhoResponse parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();

        Map<String, Node> masters = new HashMap<>();

        while (stringTokenizer.hasMoreTokens()){
            String word = stringTokenizer.nextToken();
            String ip = stringTokenizer.nextToken();
            Integer port = Integer.parseInt(stringTokenizer.nextToken());

            Node node = new Node(ip,port);
            masters.put(word,node);
        }

        MasterWhoResponse masterWhoResponse = new MasterWhoResponse();
        masterWhoResponse.setMasters(masters);
        return masterWhoResponse;
    }

    @Override
    public String getSendableString() {
        String msg = "MASTERS";
        for (Map.Entry<String, Node> master : masters.entrySet())
        {
            msg+=" "+master.getKey()+" "+master.getValue().getIp()+" "+master.getValue().getPort();
        }
        return this.getLengthAppenedMessage(msg);
    }
}
