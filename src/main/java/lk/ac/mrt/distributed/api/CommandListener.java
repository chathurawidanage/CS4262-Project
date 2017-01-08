package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.*;

/**
 * @author Chathura Widanage
 */
public interface CommandListener {
    void onSearchRequest(Node node, String keyword);

    int onLeaveRequest(LeaveRequest leaveRequest);

    int onJoinRequest(JoinRequest joinRequest);

    void onMasterBroadcast(MasterBroadcast masterBroadcast);

    void onMasterChangeBroadcast(MasterChangeBroadcast masterChangeBroadcast);

    void onYouNoMasterRequest(YouNoMasterRequest youNoMasterRequest);

    void onMasterWhoRequest(MasterWhoRequest masterWhoRequest);

    void onProvidersRequest(ProvidersRequest providersRequest);
}
