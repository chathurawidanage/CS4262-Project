package lk.ac.mrt.distributed.api;

/**
 * @author Chathura Widanage
 */
public interface Broadcastable {
    boolean isBroadcasted();

    void setBroadcasted();

    String getMessageId();

    String getBroadcastMessage();
}
