package lk.ac.mrt.distributed.api.messages.requests;

import lk.ac.mrt.distributed.api.messages.Message;

import java.util.StringTokenizer;

/**
 * @author Chathura Widanage
 */
public class RegisterRequest implements Message {
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

    public static RegisterRequest parse(String msg) {
        StringTokenizer stringTokenizer=new StringTokenizer(msg," ");
        String length=stringTokenizer.nextToken();
        String message=stringTokenizer.nextToken();
        String ip=stringTokenizer.nextToken();
        String port=stringTokenizer.nextToken();
        String username=stringTokenizer.nextToken();

        return null;
    }

    public static RegisterRequest generate(String ipAddress, int port, String username) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setIpAddress(ipAddress);
        registerRequest.setPort(port);
        registerRequest.setUsername(username);
        return registerRequest;
    }

    public String getSendableString() {
        String msg = "REG " + this.getIpAddress() + " " + this.getPort() + " " + this.getUsername();
        msg = msg.length() + 5 + " " + msg;
        return msg;
    }
}
