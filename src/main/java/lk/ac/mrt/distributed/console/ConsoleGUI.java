package lk.ac.mrt.distributed.console;


import lk.ac.mrt.distributed.api.Node;

import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.HashMap;

public class ConsoleGUI {
    private JButton btnSearch = new JButton("SEARCH");
    private JButton btnUnregister = new JButton("UNREGISTER");
    private JButton btnLeave = new JButton("LEAVE");
    private JTextField txtSearch = new JTextField("txtSearch");
    private JTable searchResultTable;
    private JScrollPane tableSearchScrollPane;
    private JTable myFilesTable;
    private JScrollPane tableMyFilesScrollPane;
    private Runnable leaveCallback;
    private Runnable unregisterCallBack;
    private Runnable registerCallback;
    private Runnable joinCallback;
    private TableModel searchTableModel;
    private TableModel myFilesTableModel;

    public ConsoleGUI(Runnable leaveCallback, Runnable unregisterCallBack, Runnable registerCallback, Runnable joinCallback) {
        this.leaveCallback = leaveCallback;
        this.unregisterCallBack = unregisterCallBack;
        this.registerCallback = registerCallback;
        this.joinCallback = joinCallback;
    }

    public void addComponentsToPane(Container pane) {
        String[] columnName = {"File", "Node"};
        Object[][] data = {
                {"FLV", "1"},
                {"FLV", "1"},
                {"FLV", "1"},
                {"FLV", "1"},
                {"FLV", "1"},
                {"FLV", "1"}
        };

        searchResultTable = new JTable(data, columnName);
        tableSearchScrollPane = new JScrollPane(searchResultTable);
        myFilesTable = new JTable(data, columnName);
        tableMyFilesScrollPane = new JScrollPane(myFilesTable);


        pane.setLayout(null);

        pane.add(txtSearch);
        pane.add(btnSearch);
        pane.add(btnLeave);
        pane.add(btnUnregister);
        pane.add(tableSearchScrollPane);
        pane.add(tableMyFilesScrollPane);


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
    }

    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    public void createAndShowGUI() {
        //Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        JFrame frame = new JFrame("AbsoluteLayoutDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Set up the content pane.
        addComponentsToPane(frame.getContentPane());

        //Size and display the window.
        Insets insets = frame.getInsets();
        frame.setSize(btnUnregister.getX() + btnUnregister.getWidth() + 10, 100);
        frame.setVisible(true);
    }

    public void loadSearchResults(HashMap<String, Node> searchResults) {

    }

    public void loadMyFiles(String[] files) {

    }

    private class MyTableModel implements TableModel {

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public String getColumnName(int columnIndex) {
            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return null;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

        }

        @Override
        public void addTableModelListener(TableModelListener l) {

        }

        @Override
        public void removeTableModelListener(TableModelListener l) {

        }
    }
}




