package lk.ac.mrt.distributed;

/**
 * Created by wik2kassa on 1/8/2017.
 */
public class Neighbour{
    private String ip;
    private int port;
    private String username;

    public Neighbour(String ip, int port, String username){
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp(){
        return this.ip;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPort(){
        return this.port;
    }
}

