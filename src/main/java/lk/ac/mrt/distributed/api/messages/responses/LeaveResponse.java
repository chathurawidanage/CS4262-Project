package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class LeaveResponse extends Message {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static LeaveResponse parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        Integer value = Integer.parseInt(stringTokenizer.nextToken());

        LeaveResponse leaveResponse = new LeaveResponse();
        leaveResponse.setValue(value);
        return leaveResponse;
    }

    @Override
    public String getSendableString() {
        String msg = "LEAVEOK " + value;
        return this.getLengthAppenedMessage(msg);
    }
}
