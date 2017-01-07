package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class JoinResponse implements Message {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static JoinResponse parse(String msg) {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        Integer value = Integer.parseInt(stringTokenizer.nextToken());

        JoinResponse joinResponse = new JoinResponse();
        joinResponse.setValue(value);
        return joinResponse;
    }

    @Override
    public String getSendableString() {
        return null;
    }
}
