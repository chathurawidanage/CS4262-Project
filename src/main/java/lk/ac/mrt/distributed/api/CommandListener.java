package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.messages.requests.LeaveRequest;

/**
 * @author Chathura Widanage
 */
public interface CommandListener {
    void onSearchRequest(Node node, String keyword);

    int onLeaveRequest(LeaveRequest leaveRequest);
}
