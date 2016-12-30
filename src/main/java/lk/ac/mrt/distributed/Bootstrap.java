package lk.ac.mrt.distributed;

import java.net.SocketException;

/**
 * @author Chathura Widanage
 */
public class Bootstrap {
    public static void main(String[] args) throws SocketException {
        new Thread(new SearchNode()).start();
    }
}
