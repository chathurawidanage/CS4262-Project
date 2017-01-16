package lk.ac.mrt.distributed.api.messages;

import java.io.Serializable;

/**
 * @author Chathura Widanage
 */
public abstract class Message implements Serializable{
    public abstract String getSendableString();

    protected String getLengthAppenedMessage(String message) {
        return String.format("%04d", message.length() + 5) + " " + message;
    }
}
