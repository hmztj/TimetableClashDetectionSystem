package gui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class GUI extends JFrame {

    public JPanel mainPanel;

    public JTextField programIDInput;
    public JTextField programNameInput;
    public JRadioButton undergraduateRadioButton;
    public JRadioButton postgraduateRadioButton;
    public JButton addProgramButton;
    public JTable programsTable;
    public DefaultTableModel programsTableModel;
    public JTextField programSearchField;
    public Boolean programSortingFlag = false;
    public JButton editProgramButton;
    public JButton deleteProgramButton;
    public JButton deleteAllProgramButton;

    public JLabel pidLabel;
    public JTextField moduleIDInput;
    public JTextField moduleNameInput;
    public JRadioButton compulsoryRadioButton;
    public JRadioButton optionalRadioButton;
    public JComboBox<String> yearComboBox;
    public JComboBox<String> termComboBox;
    public JButton addModuleButton;
    public JTable modulesTable;
    public DefaultTableModel modulesTableModel;
    public JTextField moduleSearchField;
    public String programID;
    public Boolean moduleSortingFlag = false;
    public JButton editModuleButton;
    public JButton deleteModuleButton;
    public JButton deleteAllModuleButton;

    public JComboBox<Object> programsComboBox;
    public JComboBox<Object> modulesComboBox;
    public JComboBox<String> activityComboBox;
    public JComboBox<String> dayComboBox;
    public JComboBox<String> startTimeComboBox;
    public JComboBox<String> endTimeComboBox;
    public JRadioButton scalaRadioButton;
    public JRadioButton kotlinRadioButton;
    public JButton addActivityButton;
    public JTable monTable;
    public JTable tueTable;
    public JTable wedTable;
    public JTable thuTable;
    public JTable friTable;
    public final JTable[] weekDayTables = {monTable, tueTable, wedTable, thuTable, friTable};
    public JPanel monPanel;
    public JPanel tuePanel;
    public JPanel wedPanel;
    public JPanel thuPanel;
    public JPanel friPanel;
    public JButton deleteActivityButton;
    public final JPanel[] weekDayPanels = {monPanel, tuePanel, wedPanel, thuPanel, friPanel};
    public final DefaultTableModel[] weekDayTableModels = {null, null, null, null, null};

    public GUI() {
        super("Scheduling System");

        ButtonGroup clashTypeGroup = new ButtonGroup();
        clashTypeGroup.add(kotlinRadioButton);
        clashTypeGroup.add(scalaRadioButton);
        kotlinRadioButton.setSelected(true);

        setContentPane(mainPanel);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

    }

    /*
     * renders all the table cells based on the content size (sets column's width based on the widest content in it)
     * */
    public void resizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15; // Min width
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 5, width);
            }
            columnModel.getColumn(column).setMinWidth(width);
        }
    }

}
