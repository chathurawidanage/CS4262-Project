package lk.ac.mrt.distributed.api;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chathura Widanage
 */
public class Node {
    protected int port;
    protected String ip;
    protected String username;
    protected List<String> files;

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.files = new ArrayList<>();
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return username + "@" + ip + ":" + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Node) {
            Node comp = (Node) obj;
            return this.ip.equals(comp) && this.port == comp.port;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (ip + ":" + port).hashCode();
    }
}
