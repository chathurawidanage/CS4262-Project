package lk.ac.mrt.distributed.api.messages.broadcasts;

import lk.ac.mrt.distributed.api.Broadcastable;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * MENOMASTER uuid word ip port ip_real_master port_real_master
 *
 * @author Chathura Widanage
 */
public class MasterChangeBroadcast extends Message implements Broadcastable {
    private String uuid;
    private String word;
    private Node oldMaster;
    private Node newMaster;

    private boolean broadcasted;

    public MasterChangeBroadcast(String uuid, String word, Node oldMaster, Node newMaster) {
        this.uuid = uuid;
        this.oldMaster = oldMaster;
        this.word = word;
        this.newMaster = newMaster;
    }

    public String getWord() {
        return word;
    }

    public Node getOldMaster() {
        return oldMaster;
    }

    public Node getNewMaster() {
        return newMaster;
    }

    public static MasterChangeBroadcast parse(String msg){
        StringTokenizer stringTokenizer=new StringTokenizer(msg," ");
        String length=stringTokenizer.nextToken();
        String message=stringTokenizer.nextToken();
        String uuid=stringTokenizer.nextToken();
        String word=stringTokenizer.nextToken();

        String oldMasterIp=stringTokenizer.nextToken();
        Integer oldMasterPort=Integer.parseInt(stringTokenizer.nextToken());
        String newMasterIp=stringTokenizer.nextToken();
        Integer newMasterPort=Integer.parseInt(stringTokenizer.nextToken());

        Node oldMaster=new Node(oldMasterIp,oldMasterPort);
        Node newMaster=new Node(newMasterIp,newMasterPort);
        MasterChangeBroadcast masterChangeBroadcast
                =new MasterChangeBroadcast(uuid,word,oldMaster,newMaster);
        return masterChangeBroadcast;
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
        return this.uuid;
    }

    @Override
    public String getBroadcastMessage() {
        return this.getSendableString();
    }

    @Override
    public String getSendableString() {
        String msg = "MENOMASTER " + word + " " + oldMaster.getIp() + " " +
                oldMaster.getPort() + " " + newMaster.getIp() + " " + newMaster.getPort();
        return this.getLengthAppenedMessage(msg);
    }
}
