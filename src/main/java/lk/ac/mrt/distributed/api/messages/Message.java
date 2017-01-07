package lk.ac.mrt.distributed.api.messages;

/**
 * @author Chathura Widanage
 */
public abstract class Message {
    public abstract String getSendableString();

    protected String getLengthAppenedMessage(String message) {
        return String.format("%04d", message.length() + 5) + " " + message;
    }
}
