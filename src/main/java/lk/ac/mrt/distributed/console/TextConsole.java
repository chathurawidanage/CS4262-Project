package lk.ac.mrt.distributed.console;

import javafx.util.Pair;
import lk.ac.mrt.distributed.SearchNode;
import lk.ac.mrt.distributed.SearchResult;
import lk.ac.mrt.distributed.Statistics;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by wik2kassa on 1/22/2017.
 */
public class TextConsole extends Console {
    private String nodeHandle;

    public TextConsole(SearchNode mynode) {
        super(mynode);
        nodeHandle = mynode.getSelfNode().getUsername() + "@" + mynode.getSelfNode().getIp() + ":" +
                mynode.getSelfNode().getPort();
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
                "unreg - makes the node unregister the network\n" +
                "stats - show statistics\n" +
                "clrsr - clear statistics of routing\n" +
                "clrsq - clear statistics of query\n" +
                "auto - auto serach predefined set of queries\n"
        );
        while (true) {
            System.out.print(nodeHandle + " $ ");
            command = sc.nextLine().toLowerCase().trim();
            starttime = System.currentTimeMillis();
            if (command.trim().isEmpty())
                continue;
            if (command.startsWith("exit")) {
                return;
            } else if (command.startsWith("info")) {
                System.out.println("Node IP : " + mynode.getSelfNode().getIp());
                System.out.println("Node port: " + mynode.getSelfNode().getPort());
                System.out.println("Node username : " + mynode.getSelfNode().getUsername());
                System.out.println("File List:\n");
                for (String file :
                        myfiles) {
                    System.out.println(file);
                }
                System.out.println();
            } else if (command.startsWith("search ")) {
                SearchResult searchResult = search(command.substring("search ".length()));
                List<Pair<String, Node>> results = searchResult.results;
                System.out.println(results.size() + " matche(s) found in " + (System.currentTimeMillis() - starttime)
                        + " ms\n");
                for (Pair<String, Node> result :
                        results) {
                    Object filenam = result.getValue();
                    Object nodeaddrr = result.getKey();
                    System.out.println(nodeaddrr + "\t\t" + filenam);
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
            } else if (command.startsWith("stats")) {
                Statistics.INSTANCE.print();
                mynode.printNodeStats();
            } else if (command.startsWith("clrsr")) {
                Statistics.INSTANCE.resetRouting();
            } else if (command.startsWith("clrsq")) {
                Statistics.INSTANCE.resetQuery();
            } else if (command.startsWith("auto")) {
                String queries[] = {"Twilight",
                        "Jack",
                        "American Idol",
                        "Happy Feet",
                        "Twilight saga",
                        "Happy Feet",
                        "Happy Feet",
                        "Feet",
                        "Happy Feet",
                        "Twilight",
                        "Windows",
                        "Happy Feet",
                        "Mission Impossible",
                        "Twilight",
                        "Windows 8",
                        "The",
                        "Happy",
                        "Windows 8",
                        "Happy Feet",
                        "Super Mario",
                        "Jack and Jill",
                        "Happy Feet",
                        "Impossible",
                        "Happy Feet",
                        "Turn Up The Music",
                        "Adventures of Tintin",
                        "Twilight saga",
                        "Happy Feet",
                        "Super Mario",
                        "American Pickers",
                        "Microsoft Office 2010",
                        "Twilight",
                        "Modern Family",
                        "Jack and Jill",
                        "Jill",
                        "Glee",
                        "The Vampire Diarie",
                        "King Arthur",
                        "Jack and Jill",
                        "King Arthur",
                        "Windows XP",
                        "Harry Potter",
                        "Feet",
                        "Kung Fu Panda",
                        "Lady Gaga",
                        "Gaga",
                        "Happy Feet",
                        "Twilight",
                        "Hacking",
                        "King"};
                ArrayList<String> resultsList = new ArrayList<>();
                for (String query : queries) {
                    starttime = System.currentTimeMillis();
                    SearchResult searchResult = search(query.toLowerCase());
                    List<Pair<String, Node>> results = searchResult.results;
                    String row = query.replace(" ", "_") + ",";
                    row += results.size() + "," + searchResult.time + "," + searchResult.hops;
                    /*resultsList.add(results.size() + " matche(s) found in " + (System.currentTimeMillis() - starttime)
                            + " ms" + " " + searchResult.hops + " hops");
                    for (Pair<String, Node> result :
                            results) {
                        Object filenam = result.getValue();
                        Object nodeaddrr = result.getKey();
                        resultsList.add(nodeaddrr + "\t\t" + filenam);
                    }*/
                    resultsList.add(row);
                }
                for (String line : resultsList) {
                    System.out.println(line);
                }
            }
        }
    }

}
