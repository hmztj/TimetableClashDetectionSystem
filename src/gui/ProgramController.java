package gui;

import data_classes.Program;
import persistence.ActivityHandler;
import persistence.ProgramHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Locale;

public record ProgramController(GUI gui, ProgramHandler programHandler,
                                ActivityHandler activityHandler) {

    public ProgramController(GUI gui, ProgramHandler programHandler, ActivityHandler activityHandler) {
        this.gui = gui;
        this.programHandler = programHandler;
        this.activityHandler = activityHandler;

        setProgramsTable();
        loadPrograms();
        addProgram();
        editProgram();
        deleteProgram();
        deleteAllPrograms();
        searchPrograms();
        searchFieldFocusListener();
    }

    public void setProgramsTable() {

        gui.programsTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gui.programsTable.setModel(gui.programsTableModel);

        gui.programsTable.getRowSorter().addRowSorterListener(e -> gui.programSortingFlag = true);

        String[] programColumnNames = {"ID", "Type", "Name"};
        gui.programsTableModel.setColumnIdentifiers(programColumnNames);

        gui.programsTable.getColumnModel().getColumn(0).setMinWidth(50);
        gui.programsTable.getColumnModel().getColumn(1).setMinWidth(130);
        gui.programsTable.getColumnModel().getColumn(2).setPreferredWidth(270);

        ButtonGroup programTypeGroup = new ButtonGroup();
        programTypeGroup.add(gui.undergraduateRadioButton);
        programTypeGroup.add(gui.postgraduateRadioButton);
        gui.undergraduateRadioButton.setSelected(true);

        ((DefaultTableCellRenderer) gui.programsTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);

    }

    /* ===================================================================================
     * loads all the programs from the database and populate the table, and Combo box
     * to load the programs from database null argument is passed into the function */
    public void loadPrograms() {

        ArrayList<Object> programs = programHandler.loadAllFromDB(null);
        if (!programs.isEmpty()) {
            for (Object obj : programs) {
                Program program = (Program) obj; //Casting of program class to object.
                String[] rowData = {program.getPid(), program.getType(), program.getName()};
                gui.programsTableModel.addRow(rowData);
                gui.programsComboBox.addItem(program);
            }
        }

        gui.resizeColumnWidth(gui.programsTable);
        gui.programsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    public void addProgram() {

        gui.addProgramButton.addActionListener(e -> {

            if (gui.programNameInput.getText().isBlank() && gui.programIDInput.getText().isBlank()) {
                JOptionPane.showMessageDialog(gui, "Name and ID cannot be blank");
            } else if (gui.programNameInput.getText().isBlank() && !gui.programIDInput.getText().isBlank()) {
                JOptionPane.showMessageDialog(gui, "Name cannot be blank");
            } else if (gui.programIDInput.getText().isBlank() && !gui.programNameInput.getText().isBlank()) {
                JOptionPane.showMessageDialog(gui, "ID cannot be blank");
            } else {
                String name = gui.programNameInput.getText().toUpperCase();
                String programID = gui.programIDInput.getText().toUpperCase(Locale.ROOT);
                String type = "UNDERGRADUATE";

                if (gui.postgraduateRadioButton.isSelected()) {
                    type = "POSTGRADUATE";
                }

                Program program = new Program(programID, name, type);

                boolean saveSuccessful = programHandler.save(program);

                if (saveSuccessful) {

                    String[] rowData = {programID, type, name};
                    gui.programsTableModel.addRow(rowData);
                    gui.programsComboBox.addItem(program);
                    gui.resizeColumnWidth(gui.programsTable);

                }

                gui.programNameInput.setText("");
                gui.programIDInput.setText("");
            }
        });
    }

    public void editProgram() {

        gui.editProgramButton.addActionListener(e -> {

            if (!gui.programsTable.getSelectionModel().isSelectionEmpty()) {

                Program program = (Program) programHandler.get(gui.programID);

                assert program != null;
                UpdateProgram editProgramDialog = new UpdateProgram(program);

                if (editProgramDialog.editedSuccessfully()) {

                    program = editProgramDialog.getProgram();
                    boolean updateSuccessful = programHandler.update(program, gui.programID);

                    if (updateSuccessful) {
                        gui.programsTable.setValueAt(program.getPid(), gui.programsTable.getSelectedRow(), 0);
                        gui.programsTable.setValueAt(program.getType(), gui.programsTable.getSelectedRow(), 1);
                        gui.programsTable.setValueAt(program.getName(), gui.programsTable.getSelectedRow(), 2);
                        gui.resizeColumnWidth(gui.programsTable);
                    }
                }

            } else {
                JOptionPane.showMessageDialog(gui, "Please select a program from the list above");
            }
        });
    }

    public void deleteProgram() {

        gui.deleteProgramButton.addActionListener(e -> {

            if (!gui.programsTable.getSelectionModel().isSelectionEmpty()) {

                Program program = (Program) programHandler.get(gui.programID);
                gui.programsComboBox.removeItem(program);
                activityHandler.deleteAll(gui.programID);
                programHandler.delete(gui.programID);
                int row = gui.programsTable.getSelectedRow();
                if ((!gui.programSearchField.getText().contains("Search") && !gui.programSearchField.getText().isBlank()) || gui.programSortingFlag) {
                    row = gui.programsTable.getRowSorter().convertRowIndexToModel(row);
                }
                gui.programsTableModel.removeRow(row);
                gui.resizeColumnWidth(gui.programsTable);

            } else {
                JOptionPane.showMessageDialog(gui, "Please select a program from the list above");
            }

        });
    }

    public void deleteAllPrograms() {
        gui.deleteAllProgramButton.addActionListener(e -> {

            int opt = JOptionPane.showConfirmDialog(gui, "Are you sure you want to delete all Programs");

            if (opt == JOptionPane.YES_OPTION) {
                programHandler.deleteAll(null);
                gui.programsComboBox.removeAllItems();
                activityHandler.deleteAll("delete all");
                gui.programsTableModel.setRowCount(0);
                gui.resizeColumnWidth(gui.programsTable);
            }

        });
    }

    private void searchPrograms() {

        gui.programSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (!gui.programSearchField.getText().contains("Search")) {
                    String str = gui.programSearchField.getText();
                    gui.programsTableModel = (DefaultTableModel) gui.programsTable.getModel();
                    TableRowSorter<DefaultTableModel> programsSorter = new TableRowSorter<>(gui.programsTableModel);
                    gui.programsTable.setRowSorter(programsSorter);
                    programsSorter.setRowFilter(RowFilter.regexFilter(str.toUpperCase()));
                    gui.resizeColumnWidth(gui.programsTable);
                }
            }
        });
    }

    private void searchFieldFocusListener() {
        gui.programSearchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (gui.programSearchField.getText().contains("Search")) {
                    gui.programSearchField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (gui.programSearchField.getText().isBlank()) {
                    gui.programSearchField.setText("Search");
                }
            }
        });
    }

}
