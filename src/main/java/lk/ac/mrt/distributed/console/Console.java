package lk.ac.mrt.distributed.console;

import javafx.util.Pair;
import lk.ac.mrt.distributed.SearchNode;
import lk.ac.mrt.distributed.SearchResult;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;

import java.util.List;

/**
 * Created by wik2kassa on 1/7/2017.
 */
public abstract class Console {
    SearchNode mynode;
    public Console(SearchNode mynode) {
        this.mynode = mynode;
    }
    SearchResult search(String query) {
        return mynode.search(query.trim());
    }
    void leave() throws CommunicationException {
        mynode.leave();
    }
    void unregister() throws CommunicationException {
        mynode.unregister();
    }
    abstract public void start();
}
