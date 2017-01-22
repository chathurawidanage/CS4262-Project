package lk.ac.mrt.distributed.console;

import javafx.util.Pair;
import lk.ac.mrt.distributed.SearchNode;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;

import java.util.List;
import java.util.Scanner;

/**
 * Created by wik2kassa on 1/22/2017.
 */
public class TextConsole extends Console {
    private String nodeHandle;

    public TextConsole(SearchNode mynode) {
        super(mynode);
        nodeHandle = mynode.getSelfNode().getUsername() + "@" + mynode.getSelfNode().getIp();
    }

    @Override
    public void start() {
        Scanner sc = new Scanner(System.in);
        String command;
        long starttime;
        System.out.println(nodeHandle + " initialized with the following files,");
        List<String> myfiles = mynode.getSelfNode().getFiles();
        for (String file :
                myfiles) {
            System.out.println(file);
        }
        System.out.println("search <query> - queries the system for a file\n" +
                "exit - shuts down the node and exits the console\n" +
                "leave - makes node leave the network\n" +
                "unreg - makes the node unregister the network\n"
        );
        while (true) {
            System.out.print(nodeHandle + " $ ");
            command = sc.next().toLowerCase().trim();
            starttime = System.currentTimeMillis();
            if (command.startsWith("EXIT")) {
                return;
            } else if (command.startsWith("search ")) {

                List<Pair<String, Node>> results = search(command.substring("search ".length()));
                System.out.println(results.size() + " matches found in " + (System.currentTimeMillis() - starttime)
                        + "\n");
                for (Pair<String, Node> result :
                        results) {
                    System.out.println(result.getValue().getIp() + " \t" + result.getValue());
                }
            } else if (command.startsWith("leave")) {
                try {
                    leave();
                    System.out.println(nodeHandle + " successfully left the system.");
                    nodeHandle += "[LEFT]";
                } catch (CommunicationException e) {
                    System.out.println("Failed to leave the system!");
                    e.printStackTrace();
                    System.out.println();
                }
            } else if (command.startsWith("unreg")) {
                try {
                    unregister();
                    System.out.println(nodeHandle + " successfully unregistered the system.");
                    nodeHandle += "[UNREGD]";
                } catch (CommunicationException e) {
                    System.out.println("Failed to unregister!");
                    e.printStackTrace();
                    System.out.println();
                }
            }
        }
    }

}
