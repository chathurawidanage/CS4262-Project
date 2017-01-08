package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class UnregisterResponse extends Message {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static UnregisterResponse parse(String msg){
        //todo parse registser response
        StringTokenizer stringTokenizer=new StringTokenizer(msg," ");
        String length=stringTokenizer.nextToken();
        String message=stringTokenizer.nextToken();
        Integer value=Integer.parseInt(stringTokenizer.nextToken());
        UnregisterResponse unRegisterResponse=new UnregisterResponse();
        unRegisterResponse.setValue(value);
        return unRegisterResponse;
    }

    public String getSendableString() {
        //todo length UNROK value
        return null;
    }
}
