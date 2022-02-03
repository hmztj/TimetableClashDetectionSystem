package gui;

import data_classes.Module;
import persistence.ActivityHandler;
import persistence.ModuleHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Locale;

public record ModuleController(GUI gui, ModuleHandler moduleHandler,
                               ActivityHandler activityHandler) {

    public ModuleController(GUI gui, ModuleHandler moduleHandler, ActivityHandler activityHandler) {
        this.gui = gui;
        this.moduleHandler = moduleHandler;
        this.activityHandler = activityHandler;

        setModulesTable();
        loadModules();
        addModule();
        editModule();
        deleteModule();
        deleteAllModules();
        searchModules();
        searchFieldFocusListener();

    }

    public void setModulesTable() {

        gui.modulesTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        gui.modulesTable.setModel(gui.modulesTableModel);

        /*
         * checks if there has been any sorting applied on the table columns to let the table model know which index to use for the rows
         * i.e., view index or model index*/
        gui.modulesTable.getRowSorter().addRowSorterListener(e -> gui.moduleSortingFlag = true);

        String[] moduleColumnNames = {"ID", "Year", "term", "Type", "Name"};
        gui.modulesTableModel.setColumnIdentifiers(moduleColumnNames);

        gui.modulesTable.getColumnModel().getColumn(0).setMinWidth(50);
        gui.modulesTable.getColumnModel().getColumn(1).setMinWidth(50);
        gui.modulesTable.getColumnModel().getColumn(2).setMinWidth(50);
        gui.modulesTable.getColumnModel().getColumn(3).setMinWidth(100);
        gui.modulesTable.getColumnModel().getColumn(4).setPreferredWidth(200);

        ButtonGroup moduleTypeGroup = new ButtonGroup();
        moduleTypeGroup.add(gui.compulsoryRadioButton);
        moduleTypeGroup.add(gui.optionalRadioButton);
        gui.compulsoryRadioButton.setSelected(true);

        ((DefaultTableCellRenderer) gui.modulesTable.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.LEFT);

    }

    public void loadModules() {
        /*
         * loads all the modules associated with the selected program*/
        gui.programsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (!gui.programsTable.getSelectionModel().isSelectionEmpty()) {

                    gui.programID = (String) gui.programsTable.getValueAt(gui.programsTable.getSelectedRow(), 0);

                    gui.pidLabel.setText(gui.programID);

                    ArrayList<Object> modules = moduleHandler.loadAllFromDB(gui.programID);

                    if (!modules.isEmpty()) {

                        gui.modulesTableModel.setRowCount(0); //removes modules for the previously selected program (if any).
                        for (Object obj : modules) {
                            Module module = (Module) obj;
                            String name, pid, mid, type, term, year;
                            pid = module.getPid();
                            mid = module.getMid();
                            name = module.getName();
                            type = module.getType();
                            year = module.getYear();
                            term = module.getTerm();

                            String[] rowData = {pid + "-" + mid, year, term, type, name};
                            gui.modulesTableModel.addRow(rowData);
                            gui.modulesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                        }
                        gui.resizeColumnWidth(gui.modulesTable);
                        gui.modulesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                    } else {
                        gui.modulesTableModel.setRowCount(0);
                    }
                } else {

                    gui.modulesTableModel.setRowCount(0);
                    gui.pidLabel.setText(gui.programID);
                }
            }
        });

    }

    private void searchFieldFocusListener() {
        gui.moduleSearchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if (gui.moduleSearchField.getText().contains("Search")) {
                    gui.moduleSearchField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (gui.moduleSearchField.getText().isBlank()) {
                    gui.moduleSearchField.setText("Search");
                }
            }
        });
    }

    private Boolean verifyModuleInput() {
        if (gui.programsTable.getSelectionModel().isSelectionEmpty()) {
            JOptionPane.showMessageDialog(gui, "please select a program first to add modules into it");
            return false;
        } else if (gui.moduleNameInput.getText().isBlank() && gui.moduleIDInput.getText().isBlank()) {
            JOptionPane.showMessageDialog(gui, "Name and ID cannot be blank");
            return false;
        } else if (gui.moduleNameInput.getText().isBlank() && !gui.moduleIDInput.getText().isBlank()) {
            JOptionPane.showMessageDialog(gui, "Name cannot be blank");
            return false;
        } else if (gui.moduleIDInput.getText().isBlank() && !gui.moduleNameInput.getText().isBlank()) {
            JOptionPane.showMessageDialog(gui, "ID cannot be blank");
            return false;
        } else if (gui.yearComboBox.getSelectedIndex() == 0 && gui.termComboBox.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(gui, "Please select year");
            return false;
        } else if (gui.yearComboBox.getSelectedIndex() != 0 && gui.termComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(gui, "Please select term");
            return false;
        } else if (gui.yearComboBox.getSelectedIndex() == 0 && gui.termComboBox.getSelectedIndex() != 0) {
            JOptionPane.showMessageDialog(gui, "Please select year");
            return false;
        } else if (gui.yearComboBox.getSelectedIndex() == 0 && gui.termComboBox.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(gui, "Please select year and term");
            return false;
        } else {
            return true;
        }
    }

    private void addModule() {
        gui.addModuleButton.addActionListener(e -> {

            if (verifyModuleInput()) {

                String programID = gui.pidLabel.getText();
                String moduleID = gui.moduleIDInput.getText().toUpperCase(Locale.ROOT);
                String name = gui.moduleNameInput.getText().toUpperCase();
                String type = "COMPULSORY";
                String year = (String) gui.yearComboBox.getSelectedItem(); //Casting, helps to convert Object to String, To string helps but causes a warning
                String term = (String) gui.termComboBox.getSelectedItem();

                if (gui.optionalRadioButton.isSelected()) {
                    type = "OPTIONAL";
                }

                assert year != null;
                assert term != null;
                Module module = new Module(programID, moduleID, name, type, year, term);

                boolean saveSuccessful = moduleHandler.save(module);
                if (saveSuccessful) {
                    String[] rowData = {programID + "-" + moduleID, year, term, type, name};
                    gui.modulesTableModel.addRow(rowData);
                    gui.resizeColumnWidth(gui.modulesTable);
                }

                gui.moduleNameInput.setText("");
                gui.moduleIDInput.setText("");
                gui.yearComboBox.setSelectedIndex(0);
                gui.termComboBox.setSelectedIndex(0);
            }

        });

    }

    private void editModule() {
        gui.editModuleButton.addActionListener(e -> {

            if (!gui.modulesTable.getSelectionModel().isSelectionEmpty()) {

                int row = gui.modulesTable.getSelectedRow();
                int start, end;
                String mid;

                String str = (String) gui.modulesTable.getValueAt(row, 0);
                start = str.indexOf("-") + 1;
                end = str.length();
                mid = (String) str.subSequence(start, end);

                Module module = (Module) moduleHandler.get(mid); // loads module to be edited from the database

                assert module != null;
                UpdateModule editDialogBox = new UpdateModule(module);

                if (editDialogBox.editedSuccessfully()) {

                    module = editDialogBox.getModule(); //reassignment for edited module

                    boolean updateSuccessful = moduleHandler.update(module, mid);

                    if (updateSuccessful) {
                        gui.modulesTable.setValueAt(module.getPid() + "-" + module.getMid(), row, 0);
                        gui.modulesTable.setValueAt(module.getYear(), row, 1);
                        gui.modulesTable.setValueAt(module.getTerm(), row, 2);
                        gui.modulesTable.setValueAt(module.getType(), row, 3);
                        gui.modulesTable.setValueAt(module.getName(), row, 4);
                        gui.resizeColumnWidth(gui.modulesTable);
                    }
                }

            } else {
                JOptionPane.showMessageDialog(gui, "Please select a module from the list above");
            }
        });

    }

    private void deleteModule() {

        gui.deleteModuleButton.addActionListener(e -> {

            if (!gui.modulesTable.getSelectionModel().isSelectionEmpty()) {
                int start, end;
                String mid;
                String str = (String) gui.modulesTable.getValueAt(gui.modulesTable.getSelectedRow(), 0);

                start = str.indexOf("-") + 1;
                end = str.length();
                mid = (String) str.subSequence(start, end);

                Module module = (Module) moduleHandler.get(mid);
                moduleHandler.delete(mid);
                activityHandler.delete(mid);

                int row = gui.modulesTable.getSelectedRow();
                if ((!gui.moduleSearchField.getText().contains("Search") && !gui.moduleSearchField.getText().isBlank()) || gui.moduleSortingFlag) {
                    row = gui.modulesTable.getRowSorter().convertRowIndexToModel(row);
                }
                gui.modulesTableModel.removeRow(row);
                gui.modulesComboBox.removeItem(module);
                gui.resizeColumnWidth(gui.modulesTable);


            } else {
                JOptionPane.showMessageDialog(gui, "Please Select a module from the list above");
            }

        });

    }

    private void deleteAllModules() {

        gui.deleteAllModuleButton.addActionListener(e -> {


            if (!gui.programsTable.getSelectionModel().isSelectionEmpty()) {

                int opt = JOptionPane.showConfirmDialog(gui, "Are you sure you want to delete all Modules");
                if (opt == JOptionPane.YES_OPTION) {

                    moduleHandler.deleteAll(gui.programID);
                    activityHandler.deleteAll(gui.programID);
                    gui.modulesComboBox.removeAllItems();
                    gui.modulesTableModel.setRowCount(0);
                    gui.resizeColumnWidth(gui.modulesTable);
                }

            } else {
                JOptionPane.showMessageDialog(gui, "Please Select a program you wish to delete modules from");
            }

        });

    }

    private void searchModules() {

        gui.moduleSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (!gui.moduleSearchField.getText().contains("Search")) {
                    String str = gui.moduleSearchField.getText();
                    gui.modulesTableModel = (DefaultTableModel) gui.modulesTable.getModel();
                    TableRowSorter<DefaultTableModel> modulesSorter = new TableRowSorter<>(gui.modulesTableModel);
                    gui.modulesTable.setRowSorter(modulesSorter);
                    modulesSorter.setRowFilter(RowFilter.regexFilter(str.toUpperCase()));
                    gui.resizeColumnWidth(gui.modulesTable);
                }
            }
        });

    }


}
