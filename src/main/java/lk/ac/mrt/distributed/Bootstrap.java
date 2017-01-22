package lk.ac.mrt.distributed;

import lk.ac.mrt.distributed.api.NodeOps;
import lk.ac.mrt.distributed.api.exceptions.BootstrapException;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;
import lk.ac.mrt.distributed.api.exceptions.NullCommandListenerException;
import lk.ac.mrt.distributed.api.exceptions.registration.RegistrationException;
import lk.ac.mrt.distributed.console.Console;
import lk.ac.mrt.distributed.console.NodeGUIConsole;
import lk.ac.mrt.distributed.console.TextConsole;
import org.apache.commons.cli.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * @author Chathura Widanage
 */
public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);
    private static NodeGUIConsole consoleGUI;

    public static void main(String[] args) throws SocketException {
        boolean useRMI = false;
        String bootServerIP = "127.0.0.1";
        int bootServerPort = 55555;
        boolean useGUI = true;
        String username = "kuiper";
        String myIP = "127.0.0.1";
        int myPort = 44443;

        NodeOps nodeOps;
        Console console;
        final SearchNode searchNode;
        Thread exitHook;
        Options commandlineOptions = new Options();
        commandlineOptions.addOption("r", "rmi", false, "use RMI instead of plain text UDP for comms");
        commandlineOptions.addOption("b", "bootstrap", true, "ip and port of bootstrap server in ip:port format");
        commandlineOptions.addOption("a", "addr", true, "ip and port of this node in ip:port format");
        commandlineOptions.addOption("c", "console", false, "use console instead of GUI");
        commandlineOptions.addOption("u", "username", true, "the username for the node");
        try {
            CommandLine cmd = (new DefaultParser()).parse(commandlineOptions, args);

            if (cmd.hasOption("b")) {
                String[] str = cmd.getOptionValue("b").split(":");
                bootServerIP = str[0];
                bootServerPort = Integer.parseInt(str[1]);
            }
            if (cmd.hasOption("a")) {
                String[] str = cmd.getOptionValue("a").split(":");
                myIP = str[0];
                myPort = Integer.parseInt(str[1]);
            }
            if (cmd.hasOption("u")) {
                username = cmd.getOptionValue("u");
            }

            logger.info("Bootstrap Server set to " + bootServerIP + ":" + bootServerPort);
            logger.info("Node address set to " + myIP + ":" + myPort);
            logger.info("Username set to " + username);

            if (cmd.hasOption("r")) {
                nodeOps = new NodeOpsRMIImpl(bootServerIP, bootServerPort);
                logger.info("Using RMI for networking");
            } else {
                nodeOps = new NodeOpsUDPImpl(bootServerIP, bootServerPort);
                logger.info("Using plain-text UDP for networking");
            }

            searchNode = new SearchNode(username, myIP, myPort, nodeOps);

            if (cmd.hasOption("c")) {
                console = new TextConsole(searchNode);
            } else {
                console = new NodeGUIConsole(searchNode);
            }

        } catch (Exception e) {
            Collection<Option> options = commandlineOptions.getOptions();
            for (Option opt :
                    options) {
                System.out.println(opt.getOpt() + ", " + opt.getLongOpt() + ": " + opt.getDescription());
            }
            e.printStackTrace();
            return;
        }
        exitHook = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("Server shutting down...");
                    searchNode.leave();
                    searchNode.unregister();
                } catch (CommunicationException e) {
                    e.printStackTrace();
                }
            }
        });

        try {

            searchNode.bootstrap();
            Runtime.getRuntime().addShutdownHook(exitHook);
            console.start();
        } catch (UnknownHostException | CommunicationException e) {
            e.printStackTrace();
        } catch (RegistrationException e) {
            logger.error("Bootstrapping failed", e);
        }
    }
}
