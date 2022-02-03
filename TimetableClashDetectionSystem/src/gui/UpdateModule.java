package gui;

import data_classes.Module;

import javax.swing.*;

public class UpdateModule extends JDialog {
    private JTextField moduleNameInput;
    private JTextField moduleIDInput;
    private JRadioButton COMPULSORYRadioButton;
    private JRadioButton OPTIONALRadioButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JPanel mainPanel;
    private JComboBox yearComboBox;
    private JComboBox termComboBox;
    private JLabel pidLabel;
    private String name, mid, type, pid, term, year;
    private Module module;
    private Boolean flag = false;

    /*
    * Custom dialog box to edit modules.
    * loads all the fields into the gui from the selected module, returns an updated/edited module upon save.
    * */
    public UpdateModule(Module module) {

        setTitle("Editing Module: "+module.getName());
        setModal(true);
        getRootPane().setDefaultButton(saveButton);

        this.pid = module.getPid();
        this.mid = module.getMid();
        this.name = module.getName();
        this.type = module.getType();
        this.year = module.getYear();
        this.term = module.getTerm();

        moduleNameInput.setText(name);
        moduleIDInput.setText(mid);
        pidLabel.setText(pid);

        if(type.equalsIgnoreCase("OPTIONAL")){
            OPTIONALRadioButton.setSelected(true);
        }else{
            COMPULSORYRadioButton.setSelected(true);
        }

        if(year.contains("1")){
            yearComboBox.setSelectedIndex(0);
        }else if (year.contains("2")){
            yearComboBox.setSelectedIndex(1);
        }else{
            yearComboBox.setSelectedIndex(2);
        }

        if(term.contains("1")){
            termComboBox.setSelectedIndex(0);
        }else{
            termComboBox.setSelectedIndex(1);
        }

        saveButton.addActionListener( e -> {

            if(moduleNameInput.getText().isBlank() || moduleIDInput.getText().isBlank()){
                JOptionPane.showMessageDialog(this, "Name and ID cannot be empty");
            }else {
                saveModule();
                flag = true;
            }

        });

        cancelButton.addActionListener(e -> {
            onCancel();
            flag = false;
        });

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    /*
    * called upon if the save button is clicked to save the edited changes.
    * */
    private void saveModule(){

        name = moduleNameInput.getText().toUpperCase();
        mid = moduleIDInput.getText().toUpperCase();
        if(OPTIONALRadioButton.isSelected()) {
            type = "OPTIONAL";
        }else{
            type = "COMPULSORY";
        }

        year = (String) yearComboBox.getSelectedItem();
        term = (String) termComboBox.getSelectedItem();

        module = new Module(pid, mid, name, type, year, term);
        dispose();

    }

    /*
    * disposes the dialog box if cancel button is press.
    * */
    private void  onCancel() {
        dispose();
    }

    /*
    * return true if there were any changes made to the original values
    * */
    public Boolean editedSuccessfully(){
        return flag;
    }

    /*
    * returns the edited module
    * */
    public Module getModule(){
        return module;
    }
    
}
