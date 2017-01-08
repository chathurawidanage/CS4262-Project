package lk.ac.mrt.distributed.console;


import javafx.util.Pair;
import lk.ac.mrt.distributed.SearchNode;
import lk.ac.mrt.distributed.api.Node;
import lk.ac.mrt.distributed.api.exceptions.CommunicationException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class NodeGUIConsole {
    private JButton btnSearch;
    private JButton btnUnregister;
    private JButton btnLeave;
    private JTextField txtSearch;
    private JTable searchResultTable;
    private JScrollPane tableSearchScrollPane;
    private JTable myFilesTable;
    private JScrollPane tableMyFilesScrollPane;
    private JFrame frame;
    private DefaultTableModel searchData;
    private DefaultTableModel myFileData;


    private SearchNode node;

    public NodeGUIConsole(SearchNode node) {
        this.node = node;
        final SearchNode nodeRef = node;
        btnSearch = new JButton("SEARCH");
        btnUnregister = new JButton("UNREGISTER");
        btnLeave = new JButton("LEAVE");
        txtSearch = new JTextField("");

        searchData = new DefaultTableModel(0,0);
        searchData.setColumnIdentifiers(new Object[]{"File", "Node"});

        myFileData =  new DefaultTableModel(0, 0);
        myFileData.setColumnIdentifiers(new Object[]{"File"});

        searchResultTable = new JTable(searchData);
        myFilesTable = new JTable(myFileData);

        List<String> myFiles = node.getFiles();
        for (String file :
                myFiles) {
            myFileData.addRow(new Object[]{file});
        }

        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        frame = new JFrame("Control Panel: " + node.getUsername() + "@" + node.getIp() + ":" + node.getPort());
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                try {
                    nodeRef.leave();
                    nodeRef.unregister();
                } catch (CommunicationException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    private void addComponentsToPane(final Container containerPane) {

        tableSearchScrollPane = new JScrollPane(searchResultTable);
        tableMyFilesScrollPane = new JScrollPane(myFilesTable);

        containerPane.setLayout(null);

        containerPane.add(txtSearch);
        containerPane.add(btnSearch);
        containerPane.add(btnLeave);
        containerPane.add(btnUnregister);
        containerPane.add(tableSearchScrollPane);
        containerPane.add(tableMyFilesScrollPane);

        txtSearch.setBounds(5, 5, 200, 20);
        btnSearch.setBounds(txtSearch.getX() + txtSearch.getWidth() + 5,
                txtSearch.getY(), btnSearch.getPreferredSize().width, txtSearch.getHeight());
        btnLeave.setBounds(btnSearch.getX() + btnSearch.getWidth() + 5,
                btnSearch.getY(), btnLeave.getPreferredSize().width, btnSearch.getHeight());
        btnUnregister.setBounds(btnLeave.getX() + btnLeave.getWidth() + 5,
                btnLeave.getY(), btnUnregister.getPreferredSize().width, btnLeave.getHeight());
        tableSearchScrollPane.setBounds(5, txtSearch.getY() + txtSearch.getHeight() + 5, btnUnregister.getWidth() + btnUnregister.getX(), 200);
        tableSearchScrollPane.setName("Search Results");
        searchResultTable.setPreferredScrollableViewportSize(new Dimension(btnUnregister.getWidth() + btnUnregister.getX(), 200));
        searchResultTable.setFillsViewportHeight(true);

        tableMyFilesScrollPane.setBounds(tableSearchScrollPane.getX(), tableSearchScrollPane.getY() + tableSearchScrollPane.getHeight() + 5,
                tableSearchScrollPane.getWidth(), tableSearchScrollPane.getHeight());

        myFilesTable.setPreferredScrollableViewportSize(new Dimension(searchResultTable.getWidth(), searchResultTable.getHeight()));
        myFilesTable.setFillsViewportHeight(true);

        btnSearch.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnSearch.setEnabled(false);
                populateSearchResult(node.search(txtSearch.getText().trim()));
                btnSearch.setEnabled(true);
            }
        });

        btnLeave.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    btnLeave.setEnabled(false);
                    node.leave();
                    JOptionPane.showMessageDialog(frame, "Leave Successful!");
                } catch (CommunicationException e1) {
                    JOptionPane.showMessageDialog(frame, "Leave failed: " + e1.getMessage());
                    btnLeave.setEnabled(true);
                    e1.printStackTrace();
                }
            }
        });

        btnUnregister.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnUnregister.setEnabled(false);
                try {
                    node.unregister();
                    JOptionPane.showMessageDialog(frame, "Unregistered!");
                } catch (CommunicationException e1) {
                    JOptionPane.showMessageDialog(frame, "Unregister failed: " + e1.getMessage());
                    e1.printStackTrace();
                    btnUnregister.setEnabled(true);
                }
            }
        });
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    public void display() {
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());
        //Size and display the window.
        frame.setSize(btnUnregister.getX() + btnUnregister.getWidth() + 10, 500);
        frame.setVisible(true);
    }

    private void populateSearchResult(List<Pair<String, Node>> resourceLocations) {
        for (int i = 0;i < searchData.getRowCount();i++)
            searchData.removeRow(i);
        for (Pair<String, Node> entry :
                resourceLocations) {
            searchData.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }
    }


}



