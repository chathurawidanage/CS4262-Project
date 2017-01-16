package lk.ac.mrt.distributed.api;

import lk.ac.mrt.distributed.api.messages.broadcasts.MasterBroadcast;
import lk.ac.mrt.distributed.api.messages.broadcasts.MasterChangeBroadcast;
import lk.ac.mrt.distributed.api.messages.requests.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

/**
 * @author Chathura Widanage
 */
public interface CommandListener extends Remote {
    void onSearchRequest(Node node, String keyword) throws RemoteException;

    int onLeaveRequest(LeaveRequest leaveRequest) throws RemoteException;

    int onJoinRequest(JoinRequest joinRequest) throws RemoteException;

    void onMasterBroadcast(MasterBroadcast masterBroadcast) throws RemoteException;

    void onMasterChangeBroadcast(MasterChangeBroadcast masterChangeBroadcast) throws RemoteException;

    void onYouNoMasterRequest(YouNoMasterRequest youNoMasterRequest) throws RemoteException;

    Map<String,Node> onMasterWhoRequest(MasterWhoRequest masterWhoRequest) throws RemoteException;

    List<Node> onProvidersRequest(ProvidersRequest providersRequest) throws RemoteException;

    void onTakeMyGemsRequest(TakeMyGemsRequest takeMyGemsRequest) throws RemoteException;

    void onIHaveRequest(IHaveRequest iHaveRequest) throws RemoteException;
}
