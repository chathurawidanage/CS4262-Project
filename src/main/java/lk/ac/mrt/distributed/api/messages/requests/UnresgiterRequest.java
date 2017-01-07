package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.messages.Message;

/**
 * @author Chathura Widanage
 */
public class UnresgiterRequest extends Message{


    public String getSendableString() {
        // length UNREG IP_address port_no username

        //0028 UNREG 64.12.123.190 432
        //String unregMessage = "UNREG "
        return null;
    }
}
