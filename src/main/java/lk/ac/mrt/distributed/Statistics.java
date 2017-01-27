package lk.ac.mrt.distributed;

/**
 * @author Chathura Widanage
 */
public class Statistics {
    public final static Statistics INSTANCE = new Statistics();

    public int routingMessagesIn = 0;

    public int routingMessagesOut = 0;

    public int queryMessagesIn = 0;

    public int queryMessagesOut = 0;


    private Statistics() {
    }

    public void resetRouting() {
        routingMessagesIn = 0;
        routingMessagesOut = 0;
    }

    public void resetQuery() {
        queryMessagesIn = 0;
        queryMessagesOut = 0;
    }

    public void print(){
        System.out.println("Routing In : "+routingMessagesIn);
        System.out.println("Routing Out : "+routingMessagesOut);
        System.out.println("Query In : "+queryMessagesIn);
        System.out.println("Query Out : "+queryMessagesOut);
    }

}
