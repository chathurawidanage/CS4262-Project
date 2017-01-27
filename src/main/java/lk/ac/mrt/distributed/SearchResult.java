package lk.ac.mrt.distributed;

import javafx.util.Pair;
import lk.ac.mrt.distributed.api.Node;

import java.util.List;

/**
 * @author Chathura Widanage
 */
public class SearchResult {
    public List<Pair<String, Node>> results;
    public long time;
    public long hops;
}
