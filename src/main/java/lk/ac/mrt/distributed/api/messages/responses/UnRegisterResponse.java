package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class UnRegisterResponse extends Message {

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static UnRegisterResponse parse(String msg){
        //todo parse registser response
        StringTokenizer stringTokenizer=new StringTokenizer(msg," ");
        String length=stringTokenizer.nextToken();
        String message=stringTokenizer.nextToken();
        Integer value=Integer.parseInt(stringTokenizer.nextToken());
        UnRegisterResponse unRegisterResponse=new UnRegisterResponse();
        unRegisterResponse.setValue(value);
        return unRegisterResponse;
    }

    public String getSendableString() {
        //todo length UNROK value
        //not necessary
        return null;
    }
}
