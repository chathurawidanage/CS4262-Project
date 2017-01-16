package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.*;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Chathura Widanage
 */
public interface CommandListener extends Remote {
    void onSearchRequest(Node node, String keyword);

    int onLeaveRequest(LeaveRequest leaveRequest) throws RemoteException;

    int onJoinRequest(JoinRequest joinRequest);

    void onMasterBroadcast(MasterBroadcast masterBroadcast);

    void onMasterChangeBroadcast(MasterChangeBroadcast masterChangeBroadcast);

    void onYouNoMasterRequest(YouNoMasterRequest youNoMasterRequest);

    void onMasterWhoRequest(MasterWhoRequest masterWhoRequest);

    void onProvidersRequest(ProvidersRequest providersRequest);

    void onTakeMyGemsRequest(TakeMyGemsRequest takeMyGemsRequest);

    void onIHaveRequest(IHaveRequest iHaveRequest);
}
