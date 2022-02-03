package gui;

import clash_detection.KotlinClashDetection;
import clash_detection.ScalaClashDetection$;
import data_classes.Activity;
import data_classes.Module;
import data_classes.Program;
import persistence.ActivityHandler;
import persistence.ModuleHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;



public record ActivityController (GUI gui, ActivityHandler activityHandler, ModuleHandler moduleHandler){

    private static final KotlinClashDetection kotlinClashDetection = new KotlinClashDetection();

    public ActivityController(GUI gui, ActivityHandler activityHandler, ModuleHandler moduleHandler){
        this.gui = gui;

        this.activityHandler = activityHandler;
        this.moduleHandler = moduleHandler;

        setWeekDayTables();
        loadModulesComboBox();
        addActivity();
        deleteAllActivities();
        tableSelectionController();

    }

    private void setWeekDayTables() {

        String[] activityColumns = {"Activity", "Description", "Type", "Year", "Term", "Start", "End"};
        for (int i = 0; i < gui.weekDayTables.length; i++) {

            //renders cell uneditable
            gui.weekDayTableModels[i] = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }

            };

            //sets model to respective tables and add column identifiers
            gui.weekDayTables[i].setModel(gui.weekDayTableModels[i]);
            gui.weekDayTableModels[i].setColumnIdentifiers(activityColumns);
            gui.weekDayTables[i].setShowGrid(true);
            gui.weekDayPanels[i].add(gui.weekDayTables[i].getTableHeader(), BorderLayout.NORTH);
            gui.weekDayTables[i].getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));

            //sets fixed column lengths for smaller values i.e., time and activity type.
            gui.weekDayTables[i].getColumnModel().getColumn(0).setMinWidth(100);
            gui.weekDayTables[i].getColumnModel().getColumn(0).setMaxWidth(100);
            for(int col = 2; col <= 6; col++){
                if(col == 2) {
                    gui.weekDayTables[i].getColumnModel().getColumn(col).setMinWidth(100);
                    gui.weekDayTables[i].getColumnModel().getColumn(col).setMaxWidth(100);
                }else{
                    gui.weekDayTables[i].getColumnModel().getColumn(col).setMinWidth(60);
                    gui.weekDayTables[i].getColumnModel().getColumn(col).setMaxWidth(60);
                }
            }
        }
    }

    //resets all the tables of week days
    private void resetWeekDayTables() {
        for (DefaultTableModel model : gui.weekDayTableModels) {
            model.setRowCount(0);
        }
    }

    private void loadActivities() {

        Program program = (Program) gui.programsComboBox.getSelectedItem();
        assert program != null;
        String pid = program.getPid();
        ArrayList<Object> activities = activityHandler.loadAllFromDB(pid);

        if (!activities.isEmpty()) {
            //populate the respective tables using a switch case depending on activity's day of week.
            for (Object obj : activities) {
                Activity activity = (Activity) obj;
                String day = activity.getDayOfWeek();
                Module module = (Module) moduleHandler.get(activity.getMid());

                assert module != null;
                String desc = activity.getPid()+"/"+activity.getMid()+"/"+module.getName();

                Object[] rowData = {activity.getType(),
                        desc, module.getType(), module.getYear(), module.getTerm(),
                        activity.getStart(), activity.getEnd()};

                switch (day.toLowerCase()) {
                    case "monday" -> gui.weekDayTableModels[0].addRow(rowData);
                    case "tuesday" -> gui.weekDayTableModels[1].addRow(rowData);
                    case "wednesday" -> gui.weekDayTableModels[2].addRow(rowData);
                    case "thursday" -> gui.weekDayTableModels[3].addRow(rowData);
                    case "friday" -> gui.weekDayTableModels[4].addRow(rowData);
                }
            }
            for(JTable table: gui.weekDayTables){
                gui.resizeColumnWidth(table);
            }
        }
    }

    private void loadModulesComboBox(){
        gui.programsComboBox.addActionListener(e -> {

            resetWeekDayTables();
            /*
             * removes all the previously loaded modules whenever the program selection changes */
            if (gui.modulesComboBox.getItemCount() > 0) {
                gui.modulesComboBox.removeAllItems();
                gui.modulesComboBox.addItem("Please select a Module");
            }
            //checks if the user selection is not the first item in the combo box i.e., "please select a program"
            if (gui.programsComboBox.getSelectedIndex() != 0) {
                //loads all the activities associated with this program.
                loadActivities();
                /*
                 * load the selected object from the ComboBox and cast it to type Program then extract the value of programID
                 * from the program variable
                 * */
                Program program = (Program) gui.programsComboBox.getSelectedItem();
                assert program != null;
                String programID = program.getPid();

                ArrayList<Object> modules = moduleHandler.loadAllFromDB(programID);

                /* checks if list of the modules is empty or not. if it is empty then else block is executed */
                if (!modules.isEmpty()) {
                    for (Object obj : modules) {
                        Module module = (Module) obj;
                        gui.modulesComboBox.addItem(module);
                    }
                } else {
                    gui.modulesComboBox.addItem("</Selected Program has no Modules>");
                }
            }

        });

    }

    private Boolean verifyActivityInput() {

        int program, module, activity, day, start, end;
        program = gui.programsComboBox.getSelectedIndex();
        module = gui.modulesComboBox.getSelectedIndex();
        activity = gui.activityComboBox.getSelectedIndex();
        day = gui.dayComboBox.getSelectedIndex();
        start = gui.startTimeComboBox.getSelectedIndex();
        end = gui.endTimeComboBox.getSelectedIndex();

        if ((program * module * activity * day * start * end == 0)) {
            JOptionPane.showMessageDialog(null, "Please select all required fields");
        } else if (start >= end) {
            JOptionPane.showMessageDialog(null, "End-time must be greater than start-time");
            return false;
        } else {
            return true;
        }
        return null;
    }

    private void addActivity(){
        gui.addActivityButton.addActionListener(e -> {

            if (Boolean.TRUE.equals(verifyActivityInput()) || verifyActivityInput() != null) {

                Program program = (Program) gui.programsComboBox.getSelectedItem();
                Module module = (Module) gui.modulesComboBox.getSelectedItem();
                String type = (String) gui.activityComboBox.getSelectedItem();
                String day = (String) gui.dayComboBox.getSelectedItem();
                String start = (String) gui.startTimeComboBox.getSelectedItem();
                String end = (String) gui.endTimeComboBox.getSelectedItem();

                assert  program != null;
                assert  module != null;
                String desc = program.getPid()+"/"+module.getMid()+"/"+module.getName();

                int day_index = gui.dayComboBox.getSelectedIndex() - 1;

                assert type != null;
                assert day != null;
                assert start != null;
                assert end != null;
                Activity activity = new Activity(module.getMid(), program.getPid(), type, day, start, end,
                                                 module.getType(), module.getYear(), module.getTerm());

                Object[] rowData = {activity.getType(),
                        desc, module.getType(), module.getYear(), module.getTerm(),
                        activity.getStart(), activity.getEnd()};

                ArrayList<Object> data = activityHandler.loadAllFromDB(null);

                Activity clashingActivity = kotlinClashDetection.detectClash(data, activity);

                if (gui.scalaRadioButton.isSelected()) {
                    clashingActivity = ScalaClashDetection$.MODULE$.detectClash(data, activity);
                }

                if (clashingActivity == null) {

                    activityHandler.save(activity);
                    gui.weekDayTableModels[day_index].addRow(rowData);
                    gui.resizeColumnWidth(gui.weekDayTables[day_index]);

                } else {
                    JOptionPane.showMessageDialog(null, "Clash Detected at: " + clashingActivity);
                }

            }

        });

    }

    private void deleteAllActivities(){
        gui.deleteActivityButton.addActionListener(e -> {
            activityHandler.deleteAll("delete all");
            resetWeekDayTables();
        });
    }

    private void tableSelectionController() {
        gui.monTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                gui.monTable.getSelectionModel().clearSelection();
            }
        });
        gui.tueTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                gui.tueTable.getSelectionModel().clearSelection();
            }
        });
        gui.wedTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                gui.wedTable.getSelectionModel().clearSelection();
            }
        });
        gui.thuTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                gui.thuTable.getSelectionModel().clearSelection();
            }
        });
        gui.friTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                gui.friTable.getSelectionModel().clearSelection();
            }
        });
    }
}
