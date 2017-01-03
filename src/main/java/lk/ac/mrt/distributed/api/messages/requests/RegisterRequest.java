package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.messages.Sendable;

/**
 * @author Chathura Widanage
 */
public class RegisterRequest implements Sendable {
    private String ipAddress;
    private int port;
    private String username;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static RegisterRequest parse(String msg){
        //todo parse registser response
        return null;
    }

    public static RegisterRequest generate(String ipAddress,int port,String username){
        //todo implementation
        return null;
    }

    public String getSendableString() {
        //todo length REG IP_address port_no username
        return null;
    }
}
