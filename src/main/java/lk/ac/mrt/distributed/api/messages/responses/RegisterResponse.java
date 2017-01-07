package lk.ac.mrt.distributed.api.messages.responses;

import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.registration.AlreadyRegisteredException;
import lk.ac.mrt.distributed.api.exceptions.registration.BootstrapServerFullException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegisteredToAnotherUserException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.api.messages.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class RegisterResponse extends Message {
    private List<Node> nodes;


    public int getNodesCount() {
        return nodes == null ? 0 : nodes.size();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public static RegisterResponse parse(String msg) throws RegistrationException {
        StringTokenizer stringTokenizer = new StringTokenizer(msg, " ");
        String length = stringTokenizer.nextToken();
        String message = stringTokenizer.nextToken();
        Integer noOfNodes = Integer.parseInt(stringTokenizer.nextToken());

        RegisterResponse registerResponse = new RegisterResponse();
        ArrayList<Node> neighbourNodes = new ArrayList<>();
        registerResponse.setNodes(neighbourNodes);
        if (noOfNodes > 3) {
            switch (noOfNodes) {
                case 9999:
                    throw new RegistrationException("Registration failed, there is some error in the command");
                case 9998:
                    throw new AlreadyRegisteredException("Registration failed, already registered to you, unregister first");
                case 9997:
                    throw new RegisteredToAnotherUserException("Registration failed, registered to another user, try a different IP and port");
                case 9996:
                    throw new BootstrapServerFullException("Registration failed, Bootstrap server full");
            }
        } else {
            for (int i = 0; i < noOfNodes; i++) {
                String neighbourIp = stringTokenizer.nextToken();
                Integer neighbourPort = Integer.parseInt(stringTokenizer.nextToken());
                Node neighbourNode = new Node(neighbourIp, neighbourPort);
                neighbourNodes.add(neighbourNode);
            }
        }
        return registerResponse;
    }

    public String getSendableString() {
        //no required
        return null;
    }
}
