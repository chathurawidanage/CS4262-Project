package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.messages.Message;

/**
 * @author Chathura Widanage
 */
public class TakeMyGemsResponse extends Message {
    @Override
    public String getSendableString() {
        return this.getLengthAppenedMessage("TAKEMYGEMSOK");
    }
}
