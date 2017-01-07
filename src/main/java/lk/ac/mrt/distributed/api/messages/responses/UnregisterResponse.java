package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.messages.Message;

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
        return null;
    }

    public static UnregisterResponse generate(int value){
        //todo implementation
        return null;
    }

    public String getSendableString() {
        //todo length UNROK value
        return null;
    }
}
